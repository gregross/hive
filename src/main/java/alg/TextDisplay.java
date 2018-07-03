/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is HIVE .
 *
 * The Initial Developer of the Original Code is
 * Greg Ross.
 * Portions created by the Initial Developer are Copyright (C) 2000-2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Greg Ross <gr@dcs.gla.ac.uk>
 		   Matthew Chalmers <matthew@dcs.gla.ac.uk>
 *                 Alistair Morrison <morrisaj@dcs.gla.ac.uk>
 *		   Andrew Didsbury
 *           		
 *	
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
/**
 * TextDisplay
 *
 * This class allows the user to view the text of files that have been processed
 *
 *  @author Greg Ross
 */
package alg;

import data.*;
import parent_gui.*;
import alg.lucene.*;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.analysis.standard.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.Box.Filler;
import java.util.StringTokenizer;

public class TextDisplay extends DefaultVisualModule implements 
						MouseListener, ActionListener,
						Runnable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 160;
	private int width = 140;
	
	// The DataItemCollection that this viewer holds
	
	private DataItemCollection dataItems = null;
	
	// A reference to the original data item collection that dataItems of which dataItems are a subset
	
	private DataItemCollection originalData = null;
	
	// The text component
	
	JTextPane textPane;
	JScrollPane paneScrollPane;
	
	// Popup Menu
	
	JPopupMenu popup;
	JMenuItem excludeMenuItem;
	JMenuItem reweightMenuItem;
	JMenuItem includeMenuItem;
	
	// ArrayList of the indexed terms that the user has selected
	
	ArrayList selectedWord = null;
	
	// ArrayList of selected terms that might be content-bearing words but are not included as
	// dimensions
	
	ArrayList nonDimensionTerms = null;
	
	String newline = "\n";
	
	// Sometimes it can take a while for the text to load. Put this
	// procedure into a thread so that the user can abort
	
	Thread thread = null;
	
	// A label to indicate the number of documents being displayed
	
	private JLabel jlSelected;
	
	// Set the styles of text
	
	private StyledDocument doc;
	
	// If the thread for populating the text view is still running when the user deletes the input
	// or sets new input, then this variable tells the thread to exit
	
	private boolean bStopThread = false;
	
	// Determine the top most frequent terms in the text
	
	private static final int HIGH_WEIGHT_COUNT = 5;
	
	// Label to indicate the HIGH_WEIGHT_COUNT most frequent weighted terms in the current text
	
	private JLabel jlHighCaption;
	private JLabel jlHighTerms;
	
	private ArrayList words;
	private ArrayList freq;
	private HashMap wrds;
	
	
	public TextDisplay(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Text viewer");
		setToolTipText("Text viewer");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.VISUALISATION_MODE);
		setBackground(Color.lightGray);
		setDimension(width, height);
		addControls();
	}
	
	private void addControls()
	{
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		
		// Add the label to indicate how many docs are being viewed
		
		jlSelected = new JLabel("0 documents");
		jlSelected.setForeground(Color.gray);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(jlSelected);
		cPanel.add(p);
		
		// Add the label to show the most frequent terms in the text
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		jlHighTerms = new JLabel(" ");
		jlHighTerms.setForeground(Color.red);
		jlHighCaption = new JLabel(" ");
		jlHighCaption.setForeground(Color.black);
		p.add(jlHighCaption);
		Filler filler = new Filler(new Dimension(10, 10), new Dimension(10, 10), new Dimension(10, 10));
		p.add(filler);
		p.add(jlHighTerms);
		cPanel.add(p);
		
		// Add the text component
		
		textPane = new JTextPane();
		textPane.setBackground(Color.lightGray);
		textPane.setText("");
		textPane.setEnabled(false);
		textPane.setEditable(false);
		textPane.addMouseListener(this);
		paneScrollPane = new JScrollPane(textPane);
		paneScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		cPanel.add(paneScrollPane);
		
		doc = textPane.getStyledDocument();
		addStylesToDocument(doc);
		
		add(cPanel, "Center");
		
		// Fill out the borders of the plot
		
		filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
		
		addPopupMenu();
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				// Data on the input port
				
				if (arg.get(0) instanceof DataItemCollection)
				{
					dataItems = (DataItemCollection)arg.get(0);
					
					if (dataItems.getSize() == 1)
						jlSelected.setText(dataItems.getSize() + " document (loaded " + 0 + ")");
					else
						jlSelected.setText(dataItems.getSize() + " documents (loaded " + 0 + ")");
					
					textPane.setEnabled(true);
					textPane.setText("");
					
					if (dataItems.getSize() > 0)
					{
						textPane.setBackground(Color.white);
						jlSelected.setForeground(Color.black);
					}
					else
					{
						textPane.setBackground(Color.lightGray);
						jlSelected.setForeground(Color.gray);
					}
					
					bStopThread = true;
					if (thread != null)
					{
						try
						{
							thread.join();
						}
						catch (InterruptedException ie){}
					}
					thread = null;
					textPane.setText("");
					bStopThread = false;
					
					jlHighCaption.setText(" ");
					jlHighTerms.setText(" ");
					
					thread = new Thread(this);
					thread.start();
				}
				
				if (arg.size() > 1)
					if (arg.get(1) instanceof DataItemCollection)
					{
						originalData = (DataItemCollection)arg.get(1);
					}
			}
		}
		else
		{
			// Input module was deleted
			
			dataItems = null;
			originalData = null;
			textPane.setBackground(Color.lightGray);
			
			// Exit from the thread
			
			bStopThread = true;
			if (thread != null)
			{
				try
				{
					thread.join();
				}
				catch (InterruptedException ie){}
			}
			bStopThread = false;
			thread = null;
			
			// Clear all text
			
			textPane.setText("");
			textPane.setEnabled(false);
			
			// Reset info labels
			
			jlSelected.setText("0 documents");
			jlSelected.setForeground(Color.gray);
			
			jlHighTerms.setText(" ");
			jlHighCaption.setText(" ");
		}
	}
	
	/**
	* Loading the text into the view can be quite slow. Put the process
	* into its own thread to allow the user to cancel etc
	*/
	
	public void run()
	{
		try
		{
			readFiles();
			wrds = null;
			showHighTerms();
		}
		catch (Exception e){}
	}
	
	/**
	* Get the file path data from the DataItemCollection and load the text
	* into the view. Make stopwords anc content-bearing word distinguishable
	*/
	
	private void readFiles()
	{
		if (dataItems != null)
		{
			BufferedReader fin;
			DataItem dItem;
			
			// Store the file text in one array and the file paths in another
			
			String[] sText = new String[dataItems.getSize()];
			String[] sTitle = new String[dataItems.getSize()];
			
			words = new ArrayList();
			freq = new ArrayList();
			wrds = new HashMap(dataItems.getDataItem(0).getTextValues().length);
			
			int count = 0;
			String sTemp = "";
			
			try
			{
				for (int i = 0; i < dataItems.getSize(); i++)
				{
					dItem = (DataItem)dataItems.getDataItem(i);
					fin = new BufferedReader(new FileReader(dItem.getPath()));
					
					sTitle[count] = "   " + dItem.getPath() + newline + newline;
					sTemp = "";
					
					while(fin.ready())
					{
						if (bStopThread == true)
						{
							textPane.setText("");
							fin.close();
							return;
						}
						
						sTemp = sTemp + " " + fin.readLine();
					}
					
					sText[count] = sTemp;
					
					fin.close();
					count++;
				}
				
				// Make the file path bold and the body text regular
				
				try
				{
					for (int i = 0; i < sText.length; i++)
					{
						if (bStopThread == false)
						{
							doc.insertString(doc.getLength(), "qqq", doc.getStyle("icon"));
							doc.insertString(doc.getLength(), sTitle[i], doc.getStyle("bold"));
							addBodyText(doc, sText[i]);
						}
						else
						{
							textPane.setText("");
							return;
						}
						
						// Let the user see progress
						
						if (dataItems.getSize() == 1)
							jlSelected.setText(dataItems.getSize() + " document (loaded " + (i + 1) + ")");
						else
							jlSelected.setText(dataItems.getSize() + " documents (loaded " + (i + 1) + ")");
					}
					
					jlSelected.setText(dataItems.getSize() + " documents");
					
					// Scroll to the top of the page
					
					textPane.setCaret(new DefaultCaret());
					paneScrollPane.getVerticalScrollBar().setValue(0);
					
					// If the thread has been set to null (the user has cancelled)
					// clear the display
					
					if (dataItems == null)
						textPane.setText("");
				}
				catch (BadLocationException ble)
				{
					
				}
					}
					catch (IOException ex)
					{
						System.out.println("Text viewer can't find text files.");
					}
				}
	}
	
	/**
	* Given a piece of text, return the tokens that are eligible for indexing,
	* i.e. words that pass the stopword filter and stemming
	*/
	
	private TokenStream getTokenStream(String sText)
	{
		StemAnalyzer sa = new StemAnalyzer();
		TokenStream result = sa.tokenStream(sText);
		return result;
	}
	
	/**
	* When adding the body text of a file to the text pane, highlight the
	* content-bearing words.
	*/
	
	private void addBodyText(StyledDocument doc, String sText)
	{
		try
		{
			// Split the text into individual non-normalised tokens
			
			TokenStream stream = new StandardTokenizer(new StringReader(sText));
			
			// For each token, if it is a stopword or term not included as a dimension in the
			// DataItemCollection then display its text as normal. Otherwise distinguish the
			// content-bearing words
			
			org.apache.lucene.analysis.Token tAll = stream.next();
			TokenStream result;
			
			while (tAll != null)
			{
				// Apply stopping and stemming etc to the text
				
				result = getTokenStream(tAll.termText());
				org.apache.lucene.analysis.Token t = result.next();
				
				if (t != null)
				{
					// If the term that gets through stopping and stemming
					// is an actual dimension used in the DataItemCollection, then
					// this should be made distinctive in the text viewer
					
					if (dataItems.getFields().contains(t.termText()))
					{
						doc.insertString(doc.getLength(), " " + tAll.termText() + " ", doc.getStyle("foregroundContentWord"));
						storeTermCounts(t);
					}
					else
					{
						// This content word is potentially content bearing but is not a dimension in the DataItemCollection.
						
						doc.insertString(doc.getLength(), " " + tAll.termText() + " ", doc.getStyle("foregroundContentWord_removed"));
					}
				}
				else
				{
					doc.insertString(doc.getLength(), " " + tAll.termText() + " ", doc.getStyle("foregroundContentWord_stopWord"));
				}
				tAll = stream.next();
			}
			
			doc.insertString(doc.getLength(), newline, doc.getStyle("regular"));
			doc.insertString(doc.getLength(), newline, doc.getStyle("regular"));
			stream.close();
		}
		catch (IOException ioe){}
		catch (BadLocationException be){}
	}
	
	/**
	* Create the ports and append them to the module
	*/
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 0;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Data in");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* These styles are used to distinguish the different types of words
	* (stopped, content-bearing, file name etc
	*/
	
    	protected void addStylesToDocument(StyledDocument doc)
	{
		//Initialize some styles.
		
		Style def = StyleContext.getDefaultStyleContext().
			getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
		
		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);
		
		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);
		
		s = doc.addStyle("small", regular);
		StyleConstants.setFontSize(s, 10);
		
		s = doc.addStyle("large", regular);
		StyleConstants.setFontSize(s, 16);
		
		s = doc.addStyle("icon", regular);
		StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
		ImageIcon pigIcon = createImageIcon("images/kit.gif", "a cute pig");
		if (pigIcon != null)
		{
			StyleConstants.setIcon(s, pigIcon);
		}
		else
			System.out.println("null");
		
		s = doc.addStyle("foregroundContentWord", regular);
		StyleConstants.setForeground(s, Color.red);
		
		s = doc.addStyle("foregroundContentWord_removed", regular);
		StyleConstants.setForeground(s, Color.black);
		
		s = doc.addStyle("foregroundContentWord_stopWord", regular);
		StyleConstants.setForeground(s, Color.gray);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	
	protected static ImageIcon createImageIcon(String path, String description)
	{
		java.net.URL imgURL = TextDisplay.class.getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL, description);
		}
		else
		{
			return null;
		}
	}
	
	/**
	* Add a popup menu to allow the user select terms that should be excluded
	* from the word space
	*/
	
	private void addPopupMenu()
	{
		popup = new JPopupMenu();
		excludeMenuItem = new JMenuItem("Exclude selection from analysis");
		excludeMenuItem.addActionListener(this);
		excludeMenuItem.setActionCommand("excludeTerm");
		popup.add(excludeMenuItem);
		
		reweightMenuItem = new JMenuItem("Modify term importance");
		reweightMenuItem.addActionListener(this);
		reweightMenuItem.setActionCommand("weighTerm");
		popup.add(reweightMenuItem);
		
		includeMenuItem = new JMenuItem("Include selection in analysis");
		includeMenuItem.addActionListener(this);
		includeMenuItem.setActionCommand("includeTerm");
		popup.add(includeMenuItem);
		
		popup.addMouseListener(this);
	}
	
	/**
	* Given a piece of selected text, determine whether it includes any
	* content-bearing words. If it does, store these in an array and return it
	*/
	
	private ArrayList getContentTokens(String selectedText)
	{
		ArrayList output = new ArrayList();
		
		try
		{
			// Split the text into individual non-normalised tokens
			
			TokenStream stream = new StandardTokenizer(new StringReader(selectedText));
			
			// For each token, if it is a stopword or term not included as a dimension in the
			// DataItemCollection then drop it
			
			org.apache.lucene.analysis.Token tAll = stream.next();
			
			while (tAll != null)
			{
				// Apply stopping and stemming etc to the text
				
				TokenStream result = getTokenStream(tAll.termText());
				org.apache.lucene.analysis.Token t = result.next();
				
				if (t != null)
				{
					// If the term that gets through stopping and stemming
					// is an actual dimension used in the DataItemCollection, then
					// this should be stored
					
					if (dataItems.getFields().contains(t.termText()) && 
						!output.contains(t.termText()))
						output.add(t.termText());
					else if (!dataItems.getFields().contains(t.termText()) && 
						!nonDimensionTerms.contains(t.termText()))
						nonDimensionTerms.add(t.termText());
				}
				tAll = stream.next();
			}
			stream.close();
		}
		catch (IOException ioe){System.out.println("IOException");}
		return output;
	}
	
	/**
	* Implementation of the MouseListener interface
	*/
	
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == 3)
		{
			// Only show the popup menu for excluding terms from the word space
			// if..
			if (dataItems != null) // ...there's data
				if (dataItems.getSize() > 0) // ...there's data
					if (textPane.getSelectedText() != null) // ...there's selected text
					{
						nonDimensionTerms = new ArrayList();
						selectedWord = getContentTokens(textPane.getSelectedText());
						
						// ...the selected text must contain at least one content-bearing word
						
						if ((selectedWord.size() > 0) && (nonDimensionTerms.size() == 0))
						{
							reweightMenuItem.setVisible(true);
							excludeMenuItem.setVisible(true);
							includeMenuItem.setVisible(false);
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
						else if ((selectedWord.size() == 0) && (nonDimensionTerms.size() > 0))
						{
							reweightMenuItem.setVisible(false);
							excludeMenuItem.setVisible(false);
							includeMenuItem.setVisible(true);
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
						else if ((selectedWord.size() > 0) && (nonDimensionTerms.size() > 0))
						{
							reweightMenuItem.setVisible(true);
							excludeMenuItem.setVisible(true);
							includeMenuItem.setVisible(true);
							popup.show(e.getComponent(), e.getX(), e.getY());
						}
					}
		}
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	/**
	* Interface for ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof JMenuItem)
		{
			if (e.getActionCommand().equals("excludeTerm"))
			{
				// The user has selected one or more content bearing words
				// and opted to remove them as dimensions from the DataItemCollection.
				// Highlight these terms
				
				highlightSelection(false, selectedWord);
				removeDimensions();
				showHighTerms();
			}
			else if (e.getActionCommand().equals("weighTerm"))
			{
				reWeighTerm();
			}
			else if (e.getActionCommand().equals("includeTerm"))
			{
				addDimensions();
				highlightSelection(true, nonDimensionTerms);
				showHighTerms();
			}
		}
	}
	
	/** 
	* Show a dialog prompting the user to enter a weight value for the
	* selected term(s)
	*/
	
	private void reWeighTerm()
	{
		// If only one term is selected, show its current weight in the input dialogue
		
		Object val = null;
		int index = 0;
		if (selectedWord.size() == 1)
		{
			index = originalData.getFields().indexOf((String)selectedWord.get(0));
			double weight = 0;
			boolean b = false;
			int count = 0;
			while (!b && count < originalData.getSize())
			{
				weight = originalData.getDataItem(count).getTextValues()[index];
				if (weight > 0)
					b = true;
				count++;
			}
			
			val = JOptionPane.showInputDialog(this, "Enter a weight for the selected term", new Double(weight));
		}
		else if (selectedWord.size() > 1)
		{
			val = JOptionPane.showInputDialog("Enter a weight for the selected term");
		}
		
		double newVal = -1d;
		try
		{
			newVal = Double.parseDouble(val.toString());
		}
		catch (Exception ne){}
		
		// If the number is valid, update the data set
		
		if (newVal > -1d)
		{
			DataItem item = null;
			double[] values = null;
			
			for (int j = 0; j < selectedWord.size(); j++)
			{
				index = originalData.getFields().indexOf((String)selectedWord.get(j));
				originalData.getMaximums().set(index, new Double(newVal));
				originalData.getMinimums().set(index, new Double(0));
				
				for (int i = 0; i < originalData.getSize(); i++)
				{
					item = originalData.getDataItem(i);
					values = item.getTextValues();
					
					if (values[index] > 0)
						values[index] = newVal;
				}
			}
		}
	}
	
	/**
	* When the user decides to remove a term completely from the analysis,
	* i.e. from the vector space, that term(s) is made distinctive in the view
	*/
	
	private void highlightSelection(boolean bAdded, ArrayList terms)
	{
		try
		{
			// Apply stopping and stemming etc to the all displayed text
			
			Document d = textPane.getDocument();
			String text = d.getText(0, d.getLength());
			
			TokenStream result = getTokenStream(text);
			org.apache.lucene.analysis.Token tAll = result.next();
			StyledDocument doc = textPane.getStyledDocument();
			int len = 0;
			
			String styleProp = "";
			
			if (bAdded)
				styleProp = "foregroundContentWord";
			else
				styleProp = "foregroundContentWord_removed";
			
			while (tAll != null)
			{
				// If the stemmed word is one of the words selected by the user, then
				// highlight it as being removed from analysis
				
				if (terms.contains(tAll.termText()))
				{
					len = tAll.endOffset() - tAll.startOffset();
					doc.setCharacterAttributes(tAll.startOffset(), len,
						doc.getStyle(styleProp), true);
				}
				
				tAll = result.next();
			}
			result.close();
		}
		catch (IOException ioe){}
		catch (BadLocationException be){}
	}
	
	/**
	* This method is called after the user has decided to remove selected terms from the DataItemCollection dimensions.
	*/
	
	private void removeDimensions()
	{
		DataItem item = null;
		double[] values = null;
		int delIndex = -1;
		
		for (int j = 0; j < selectedWord.size(); j++)
		{
			// Get the index of each removed dimension
			
			delIndex = originalData.getFields().indexOf((String)selectedWord.get(j));
			
			// Remove the type and field from the DataItemCollection
			
			originalData.getTypes().remove(delIndex);
			originalData.getFields().remove(delIndex);
			originalData.getMaximums().remove(delIndex);
			originalData.getMinimums().remove(delIndex);
			
			// Remove this dimension from the data item's value array
			
			for (int i = 0; i < originalData.getSize(); i++)
			{
				item = originalData.getDataItem(i);
				values = item.getTextValues();
				
				double[] newValues = new double[values.length - 1];
				
				int k;
				for (k = 0; k < delIndex; k++)
				{
					newValues[k] = values[k];
				}
				
				for (k = delIndex + 2; k < values.length; k++)
				{
					newValues[k - 1] = values[k];
				}
				
				item.setTextValues(newValues);
			}
		}
	}
	
	/**
	* For completeness, this method allows dimensions to be added to the DataItemCollection
	* When a term is added, is is given a weight of 1. This could (should?) be modified
	* so a tf-idf value is used instead and that the resulting vector be re-normalised
	*/
	
	private void addDimensions()
	{
		DataItem item = null;
		double[] values = null;
		
		for (int j = 0; j < nonDimensionTerms.size(); j++)
		{
			// Update the DataItemCollection
			
			int[] matchingDocs = getMathingDocs((String)nonDimensionTerms.get(j));
			
			if (matchingDocs != null)
			{
				originalData.getTypes().add(new Integer(DataItemCollection.DOUBLE));
				originalData.getFields().add((String)nonDimensionTerms.get(j));
				originalData.getMaximums().add(new Double(1));
				originalData.getMinimums().add(new Double(0));
				
				int k;
				
				for (int i = 0; i < originalData.getSize(); i++)
				{
					// Get a data item
					
					item = originalData.getDataItem(i);
					values = item.getTextValues();
					
					// Recreate the original feature vector but with one new
					// element at the end
					
					double[] newValues = new double[values.length + 1];
					
					for (k = 0; k < values.length; k++)
						newValues[k] = values[k];
					
					// Reset the high dimensional feature vector
					
					item.setTextValues(newValues);
				}
				
				// Set the new element of each document that contains the corresponding
				// term, to 1
				
				for (k = 0; k < matchingDocs.length; k++)
				{
					item = originalData.getDataItemByID(matchingDocs[k]);
					
					if (item != null)
						item.getTextValues()[item.getTextValues().length - 1] = 1;
				}
			}
		}
	}
	
	/**
	* Given that the user has opted to include a new term in the analysis, this
	* method returns an array of integers representing the indices of documents that
	* contain that term
	*/
	
	private int[] getMathingDocs(String term)
	{
		// Query the Lucene search engine to retrieve all docs that contain the given
		// term
		
		int[] result = null;
		
		try
		{
			Searcher searcher = new IndexSearcher(originalData.getIndexPath());
			StemAnalyzer analyzer = new StemAnalyzer();
			
			Query query = QueryParser.parse(term, "contents", analyzer);
			Hits hits = searcher.search(query);
			
			result = new int[hits.length()];
			
			for (int i = 0; i < hits.length(); i++)
			{
				result[i] = hits.id(i);
			}
		}
		catch (Exception e){}
		
		return result;
	}
	
	/**
	* When text is loaded into the view, this method is called to detect and display
	* the most frequent terms
	*/
	
	private void showHighTerms()
	{
		if (dataItems.getSize() == 0)
		{
			jlHighTerms.setText(" ");
			jlHighCaption.setText(" ");
			return;
		}
		
		String sTerms = getHighestFrequencyTerms();
		
		// Display the top counts
		
		jlHighCaption.setText("Most frequent words: ");
		jlHighTerms.setText(sTerms);
	}
	
	/**
	* For the currently selected items cache terms along with their frequencies
	* in the subset
	*/
	
	private void storeTermCounts(org.apache.lucene.analysis.Token t)
	{
		Object ind = wrds.get(t.termText());
		int index = -1;
		if (ind != null)
			index = ((Integer)ind).intValue();
		
		if (ind == null)
		{
			words.add(t.termText());
			freq.add(new Integer(1));
			wrds.put(t.termText(), new Integer(freq.size() - 1));
		}
		else
		{
			int val = ((Integer)freq.get(index)).intValue() + 1;
			freq.set(index, new Integer(val));
		}
	}
	
	/**
	* Return a string representing the n most frequent terms and their frequqncies
	*/
	
	private String getHighestFrequencyTerms()
	{
		int numHighTerms = Math.min(HIGH_WEIGHT_COUNT, dataItems.getDataItem(0).getTextValues().length);
		String sTerm = "";
		int index;
		
		for (int i = 0; i < numHighTerms; i++)
		{
			index = getHighestF(words, freq);
			if (index > -1)
			{
				sTerm = sTerm + ", " + (String)words.get(index) + "("
					+ freq.get(index) + ")";
				
				words.remove(index);
				freq.remove(index);
			}
			else
				return "";
		}
		
		return sTerm.substring(2);
	}
	
	private int getHighestF(ArrayList w, ArrayList f)
	{
		double highest = Double.MIN_VALUE;
		int val;
		int index = -1;
		
		for (int i = 0; i < w.size(); i++)
		{
			val = ((Integer)f.get(i)).intValue();
			if (val >= highest)
			{
				highest = val;
				index = i;
			}
		}
		
		return index;
	}
}

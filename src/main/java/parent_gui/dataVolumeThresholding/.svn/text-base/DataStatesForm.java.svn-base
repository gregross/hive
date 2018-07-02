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
 * Algorithmic testbed
 *
 * DataStatesForm
 *
 * This dialog allows the user to enter the values for
 * data cardinality and dimensionality that determine the
 * categorical L (low), M (moderate) and H (high) states
 *
 *
 *  @author Greg Ross
 */
package parent_gui.dataVolumeThresholding;

import parent_gui.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Properties;
import java.lang.Exception;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.awt.Font;

public class DataStatesForm extends JDialog implements DocumentListener
{
	static DataStatesForm instance;
	private static Mdi mdiForm;
	
	// Panel to hold the two areas for setting cardinalty and dimensionality
	// category thresholds
	
	private JPanel jpThreshold;
	
	// Text field for High Cardinality
	
	private JTextField txtHighCard;
	
	// Text field for moderate Cardinality
	
	private JTextField txtModCard;
	
	// Text field for low Cardinality
	
	private JTextField txtLowCard;
	
	// Dimensionality test fields
	
	private JTextField txtHighDim;
	private JTextField txtModDim;
	private JTextField txtLowDim;
	
	// Labels
	
	private JLabel lblCardHigh;
	private JLabel lblCardLow;
	private JLabel lblCardMod;
	private JLabel lblDimHigh;
	private JLabel lblDimLow;
	private JLabel lblDimMod;
	
	// The LMH values for cardinality and dimensionality are stored
	// in a properties file
	
	private Properties properties;
	
	// LMH values
	
	private int dim_Low;
	private int dim_High;
	private int card_Low;
	private int card_High;
	
	public DataStatesForm(String title, Mdi mdi)
	{
	      
	      mdiForm = mdi;
	      
	      // Load LMH values from DataStates.prop
	      
	      properties = new Properties();
	      initValues();
	      
	      setSize(300,300);
	      
	      //  Centre the window in the screen
	      
	      double left, top;
	      left = (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2.0d) - (getWidth() / 2.0d);
	      top = (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2.0d) - (getHeight() / 2.0d);
	      setLocation(new Long(Math.round(left)).intValue(), new Long(Math.round(top)).intValue());
	      
	      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	      setBackground(Color.lightGray);
	      setVisible(false);
	      
	      // Add a cancel button
	      
	      JButton bCancel = new JButton("Cancel");
	      bCancel.addActionListener(new ActionListener() 
	      {
		      public void actionPerformed(ActionEvent e) 
		      {
			      hide();
			      dispose();
		      }
	      });
	      
	      // Add a close button
	      
	      JButton bClose = new JButton("Close");
	      bClose.addActionListener(new ActionListener() 
	      {
		      public void actionPerformed(ActionEvent e) 
		      {
			      saveProperties();
			      hide();
			      dispose();
		      }
	      });
	      
	      JPanel buttonBox = new JPanel();
	      buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
	      buttonBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	      buttonBox.add(Box.createVerticalGlue());
	      buttonBox.add(Box.createVerticalStrut(10));
	      buttonBox.add(bCancel);
	      buttonBox.add(Box.createVerticalStrut(10));
	      buttonBox.add(bClose);
	      buttonBox.add(Box.createVerticalStrut(10));
	      
	      // Make both buttons the same size
	      
	      Dimension bSize = new Dimension(50, 25);
	      
	      bClose.setMinimumSize(bCancel.getMinimumSize());
	      bClose.setPreferredSize(bCancel.getPreferredSize());
	      bClose.setMaximumSize(bCancel.getMaximumSize());
	      
	      getContentPane().add(buttonBox, BorderLayout.EAST);
	      
	      // Add the panel that will hold the controls for
	      // determining cardinality and dimensionality categories
	      
	      jpThreshold = new JPanel();
	      jpThreshold.setLayout(new BoxLayout(jpThreshold, BoxLayout.Y_AXIS));
	      getContentPane().add(jpThreshold, BorderLayout.CENTER);
	      
	      // Add the cardinality controls
	      
	      addCardinalityBox();
	      
	      // Add the dimensionality controls
	      
	      addDimensionalityBox();
	      
	      setFonts();
	      
	      pack();
	      
	      registerTextDocuments();
	      
	      // Show the values as stored in the propertoes file
	      
	      showStoredValues();
	      
	      setTitle("Data volume categories");
	      setModal(true);
	      setResizable(false);
	      setVisible(true);
	}
	
	private void addCardinalityBox()
	{
		// Create a panel to hold the labels
		
		JPanel jpCardLabels = new JPanel();
		jpCardLabels.setLayout(new GridLayout(0, 1));
		
		// Create the labels
		
		lblCardHigh = new JLabel("High (>):");
		lblCardHigh.setForeground(Color.red);
		lblCardMod = new JLabel("Moderate:");
		lblCardMod.setForeground(new Color(210, 68, 0));
		lblCardLow = new JLabel("Low (<):");
		lblCardLow.setForeground(new Color(64, 0, 255));
		
		// Add labels to label panel
		
		jpCardLabels.add(lblCardHigh);
		jpCardLabels.add(lblCardMod);
		jpCardLabels.add(lblCardLow);
		
		// Create a panel to hold the text fields
		
		JPanel jpCardText = new JPanel();
		jpCardText.setLayout(new GridLayout(0, 1));
		
		// Create the text fields
		
		txtHighCard = new JTextField();
		txtHighCard.setMinimumSize(new Dimension(50, 18)); 
		txtHighCard.setPreferredSize(new Dimension(200, 18)); 
		txtHighCard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); 
		
		txtModCard = new JTextField();
		txtModCard.setEditable(false);
		txtModCard.setMinimumSize(new Dimension(50, 18)); 
		txtModCard.setPreferredSize(new Dimension(200, 18)); 
		txtModCard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); 
		
		txtLowCard = new JTextField();
		txtLowCard.setMinimumSize(new Dimension(50, 18)); 
		txtLowCard.setPreferredSize(new Dimension(200, 18)); 
		txtLowCard.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); 
		
		// Add the text fields to the text panel
		
		jpCardText.add(txtHighCard);
		jpCardText.add(txtModCard);
		jpCardText.add(txtLowCard);
		
		// Tell accessibility tools about label/textfield pairs
		
		lblCardHigh.setLabelFor(txtHighCard);
		lblCardMod.setLabelFor(txtModCard);
		lblCardLow.setLabelFor(txtLowCard);
		
		// Add the label and text panels to another panel
		
		JPanel jpHolder = new JPanel();
		jpHolder.setBorder(BorderFactory.createTitledBorder("Cardinality categories"));
		jpHolder.setLayout(new BorderLayout());
		jpHolder.add(jpCardLabels, BorderLayout.CENTER);
		jpHolder.add(jpCardText, BorderLayout.EAST);
		
		jpThreshold.add(jpHolder);
	}
	
	private void addDimensionalityBox()
	{
		// Create a panel to hold the labels
		
		JPanel jpDimLabels = new JPanel();
		jpDimLabels.setLayout(new GridLayout(0, 1));
		
		// Create the labels
		
		lblDimHigh = new JLabel("High (>):");
		lblDimHigh.setForeground(Color.red);
		lblDimMod = new JLabel("Moderate:");
		lblDimMod.setForeground(new Color(210, 68, 0));
		lblDimLow = new JLabel("Low (<):");
		lblDimLow.setForeground(new Color(64, 0, 255));
		
		// Add labels to label panel
		
		jpDimLabels.add(lblDimHigh);
		jpDimLabels.add(lblDimMod);
		jpDimLabels.add(lblDimLow);
		
		// Create a panel to hold the text fields
		
		JPanel jpDimText = new JPanel();
		jpDimText.setLayout(new GridLayout(0, 1));
		
		// Create the text fields
		
		txtHighDim = new JTextField();
		txtHighDim.setMinimumSize(new Dimension(50, 18)); 
		txtHighDim.setPreferredSize(new Dimension(200, 18)); 
		txtHighDim.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); 
		
		txtModDim = new JTextField();
		txtModDim.setEditable(false);
		txtModDim.setMinimumSize(new Dimension(50, 18)); 
		txtModDim.setPreferredSize(new Dimension(200, 18)); 
		txtModDim.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); 
		
		txtLowDim = new JTextField();
		txtLowDim.setMinimumSize(new Dimension(50, 18)); 
		txtLowDim.setPreferredSize(new Dimension(200, 18)); 
		txtLowDim.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE)); 
		
		// Add the text fields to the text panel
		
		jpDimText.add(txtHighDim);
		jpDimText.add(txtModDim);
		jpDimText.add(txtLowDim);
		
		// Tell accessibility tools about label/textfield pairs
		
		lblDimHigh.setLabelFor(txtHighDim);
		lblDimMod.setLabelFor(txtModDim);
		lblDimLow.setLabelFor(txtLowDim);
		
		// Add the label and text panels to another panel
		
		JPanel jpHolder = new JPanel();
		jpHolder.setBorder(BorderFactory.createTitledBorder("Dimensionality categories"));
		jpHolder.setLayout(new BorderLayout());
		jpHolder.add(jpDimLabels, BorderLayout.CENTER);
		jpHolder.add(jpDimText, BorderLayout.EAST);
		
		jpThreshold.add(jpHolder);
	}
	
	/**
	* Use ValidTextFieldDocument as the PlainDocument for
	* the text controls to ensure that only numerical info can
	* be entered. Also register DocumentListeners
	*/
	
	private void registerTextDocuments()
	{
		txtHighCard.setDocument(new ValidTextFieldDocument(txtHighCard));
		txtLowCard.setDocument(new ValidTextFieldDocument(txtLowCard));
		txtHighDim.setDocument(new ValidTextFieldDocument(txtHighDim));
		txtLowDim.setDocument(new ValidTextFieldDocument(txtLowDim));
		
		txtHighCard.getDocument().addDocumentListener(this);
		txtLowCard.getDocument().addDocumentListener(this);
		txtHighDim.getDocument().addDocumentListener(this);
		txtLowDim.getDocument().addDocumentListener(this);
	}
	
	private void setFonts()
	{
		Font f = new Font("Arial", Font.PLAIN,  15);
		
		txtHighCard.setFont(f);
		txtLowCard.setFont(f);
		txtModCard.setFont(f);
		txtHighDim.setFont(f);
		txtLowDim.setFont(f);
		txtModDim.setFont(f);
		
		f = new Font("Arial", Font.BOLD,  12);
		
		lblCardHigh.setFont(f);
		lblCardLow.setFont(f);
		lblCardMod.setFont(f);
		lblDimHigh.setFont(f);
		lblDimLow.setFont(f);
		lblDimMod.setFont(f);
	}
	
    	public static DataStatesForm getInstance(String title, Mdi mdi)
	{
		instance = new DataStatesForm(title, mdi);
		return instance;
	}
	
	public static boolean formIsOpen()
	{
		if (instance == null)
			return false;
		else
			return true;
	}
	
	/**
	* Save LMH value changes to the properties file
	*/
	
	protected void saveProperties()
	{
		// Fits make sure that the values are correct
		
		verifyValues();
		
		try
		{
			// Now reflect the values in the properties object
			
			properties.setProperty("dim_Low", txtLowDim.getText());
			properties.setProperty("dim_High", txtHighDim.getText());
			properties.setProperty("card_Low", txtLowCard.getText());
			properties.setProperty("card_High", txtHighCard.getText());
			
			// Create an output stream to the properties file
			
			OutputStream output = new FileOutputStream(DataVolumeThreshold.PROP_FILE + PropertiesHandler.DEFAULT_EXT);
			
			// Save the properties in the file
			
			if (output != null)
				properties.store(output, "");
			else
				throw new NoPropertiesException();
		}
		catch (Exception npe)
		{
			System.err.println("couldn't load the properties for Data States");
		}
		
		// Now save the values to the DataVolumeThreshold class
		
		DataVolumeThreshold.loadProperties();
	}
	
	/** 
	* Before saving LMH values we must make sure that we have no null values
	* If we have a null value in a text field, replace it with the default
	* value
	*/
	
	private void verifyValues()
	{
		// Dimensionality thresholds
		
		if ((txtHighDim.getText().length() == 0) && (txtLowDim.getText().length() == 0))
		{	
			txtHighDim.setText((new Integer(dim_High)).toString());
			txtLowDim.setText((new Integer(dim_Low)).toString());
		}
		else if ((txtHighDim.getText().length() == 0) && (txtLowDim.getText().length() > 0))
			txtHighDim.setText((new Integer(Integer.parseInt(txtLowDim.getText()) + 1)).toString());
		else if ((txtHighDim.getText().length() > 0) && (txtLowDim.getText().length() == 0))
			if (Integer.parseInt(txtHighDim.getText()) > 1)
				txtLowDim.setText((new Integer(Integer.parseInt(txtHighDim.getText()) - 1)).toString());
			else
			{
				txtHighDim.setText("2");
				txtLowDim.setText((new Integer(Integer.parseInt(txtHighDim.getText()) - 1)).toString());
			}
		
		// Cardinality thresholds
		
		if ((txtHighCard.getText().length() == 0) && (txtLowCard.getText().length() == 0))
		{	
			txtHighCard.setText((new Integer(card_High)).toString());
			txtLowCard.setText((new Integer(card_Low)).toString());
		}
		else if ((txtHighCard.getText().length() == 0) && (txtLowCard.getText().length() > 0))
			txtHighCard.setText((new Integer(Integer.parseInt(txtLowCard.getText()) + 1)).toString());
		else if ((txtHighCard.getText().length() > 0) && (txtLowCard.getText().length() == 0))
			if (Integer.parseInt(txtHighCard.getText()) > 1)
				txtLowCard.setText((new Integer(Integer.parseInt(txtHighCard.getText()) - 1)).toString());
			else
			{
				txtHighCard.setText("2");
				txtLowCard.setText((new Integer(Integer.parseInt(txtHighCard.getText()) - 1)).toString());
			}
	}
	
	/**
	* intitialises the values held in the properties object
	*/
	public void initValues()
	{
		dim_Low        	= DataVolumeThreshold.getLowDimensionality();
		dim_High        = DataVolumeThreshold.getHighDimensionality();
		card_Low    	= DataVolumeThreshold.getLowCardinality();
		card_High   	= DataVolumeThreshold.getHighCardinality();
	}
	
	/**
	* Load the LMH values from the DataStates.prop file into
	* the appropriate text boxes
	*/
	
	private void showStoredValues()
	{
		txtHighDim.setText(new Integer(dim_High).toString());
		txtLowDim.setText(new Integer(dim_Low).toString());
		txtModDim.setText(new Integer(dim_Low).toString() + " to " + new Integer(dim_High).toString());
		
		txtHighCard.setText(new Integer(card_High).toString());
		txtLowCard.setText(new Integer(card_Low).toString());
		txtModCard.setText(new Integer(card_Low).toString() + " to " + new Integer(card_High).toString());
	}
	
	/**
	* Implementation of the DocumentListener interface
	*/
	
	public void insertUpdate(DocumentEvent e)
	{
		JTextField source = ((ValidTextFieldDocument)e.getDocument()).getSource();
		modifyValues(source);
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		JTextField source = ((ValidTextFieldDocument)e.getDocument()).getSource();
		
		modifyValues(source);
	}
	
	public void changedUpdate(DocumentEvent e)
	{
		JTextField source = ((ValidTextFieldDocument)e.getDocument()).getSource();
		modifyValues(source);
	}
	
	/**
	* When the value of one text box changes make sure that the other
	* text boxes have values that accommodate the new one. i.e. the
	* value for low cardinality should alway be lower than the value
	* for high cardinality
	*/
	
	private void modifyValues(JTextField txt)
	{
		int lowValue = 0;
		int highValue = 0;
		
		if ((txt == txtLowCard) || (txt == txtHighCard))
		{
			if (!txtLowCard.getText().equals(""))
				lowValue = Integer.parseInt(txtLowCard.getText());
			if (!txtHighCard.getText().equals(""))
				highValue = Integer.parseInt(txtHighCard.getText());
			
			// If the low value is greater than or equal to the
			// high value, change the value in the text box that was
			// not being edited to maintain the LMH relationship
			
			try
			{
				if ((lowValue >= highValue) && (highValue !=0))
				{
					if (txt == txtLowCard)
						txtHighCard.setText((new Integer(lowValue + 1)).toString());
					else
						txtLowCard.setText((new Integer(highValue - 1)).toString());
				}
			}
			catch (Exception e){}
		}
		else if ((txt == txtLowDim) || (txt == txtHighDim))
		{
			if (!txtLowDim.getText().equals(""))
				lowValue = Integer.parseInt(txtLowDim.getText());
			
			if (!txtHighDim.getText().equals(""))
				highValue = Integer.parseInt(txtHighDim.getText());
			
			// If the low value is greater than or equal to the
			// high value, change the value in the text box that was
			// not being edited to maintain the LMH relationship
			
			try
			{
				if ((lowValue >= highValue) && (highValue !=0))
				{
					if (txt == txtLowDim)
						txtHighDim.setText((new Integer(lowValue + 1)).toString());
					else
						txtLowDim.setText((new Integer(highValue - 1)).toString());
				}
			}
			catch (Exception e){}
		}
		
		setModerateText();
	}
	
	/**
	* given the low and high threshold values for cardinality and dimensionality,
	* determine the text to be entered into the moderate text fields
	*/
	
	private void setModerateText()
	{
		int lowValue = 0;
		int highValue = 0;
		
		// Cardinality
		
		if ((txtHighCard.getText().length() > 0) && (txtLowCard.getText().length() > 0))
		{	
			highValue = (new Integer(Integer.parseInt(txtHighCard.getText()))).intValue();
			lowValue = (new Integer(Integer.parseInt(txtLowCard.getText()))).intValue();
		}
		else if ((txtHighCard.getText().length() == 0) && (txtLowCard.getText().length() > 0))
		{
			lowValue = (new Integer(Integer.parseInt(txtLowCard.getText()))).intValue();
			highValue = (new Integer(lowValue + 1)).intValue();
		}
		else if ((txtHighCard.getText().length() > 0) && (txtLowCard.getText().length() == 0))
			if (Integer.parseInt(txtHighCard.getText()) > 1)
			{
				highValue = (new Integer(Integer.parseInt(txtHighCard.getText()))).intValue();
				lowValue = (new Integer(highValue - 1)).intValue();
			}
			else
			{
				highValue = 2;
				lowValue = 1;
			}
		else if ((txtHighCard.getText().length() == 0) && (txtLowCard.getText().length() == 0))
		{
			highValue = card_High;
			lowValue = card_Low;
		}
			
		txtModCard.setText((new Integer(lowValue)).toString() + " to " + (new Integer(highValue)).toString());
		
		// Dimensionality
		
		if ((txtHighDim.getText().length() > 0) && (txtLowDim.getText().length() > 0))
		{	
			highValue = (new Integer(Integer.parseInt(txtHighDim.getText()))).intValue();
			lowValue = (new Integer(Integer.parseInt(txtLowDim.getText()))).intValue();
		}
		else if ((txtHighDim.getText().length() == 0) && (txtLowDim.getText().length() > 0))
		{
			lowValue = (new Integer(Integer.parseInt(txtLowDim.getText()))).intValue();
			highValue = (new Integer(lowValue + 1)).intValue();
		}
		else if ((txtHighDim.getText().length() > 0) && (txtLowDim.getText().length() == 0))
			if (Integer.parseInt(txtHighDim.getText()) > 1)
			{
				highValue = (new Integer(Integer.parseInt(txtHighDim.getText()))).intValue();
				lowValue = (new Integer(highValue - 1)).intValue();
			}
			else
			{
				highValue = 2;
				lowValue = 1;
			}
		else if ((txtHighDim.getText().length() == 0) && (txtLowDim.getText().length() == 0))
		{
			highValue = dim_High;
			lowValue = dim_Low;
		}
			
		txtModDim.setText((new Integer(lowValue)).toString() + " to " + (new Integer(highValue)).toString());
	}
}

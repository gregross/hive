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
 * TextSearch
 *
 * This visual module allows a user to query text data. It connects to a scatterplot
 * layout of the data set and highlights points that match the query
 *
 *  @author Greg Ross
 */
package alg; 
 
import parent_gui.*;
import data.*;
import alg.lucene.*;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.analysis.standard.*;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.*;

public class TextSearch extends DefaultVisualModule implements ActionListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 70;
	private int width = 300;
	
	// The data to be searched
	
	DataItemCollection dataItems;
	
	// The documents returned for a query
	
	DataItemCollection results = null;
	
	// The keys of the retrieved items
	
	HashSet selection = null;
	
	// Controls
	
	JTextField txtQuery;
	JButton cmdSearch;
	
	public TextSearch(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Search");
		setToolTipText("Query the data");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.VISUALISATION_MODE);
		setBackground(Color.lightGray);
		setDimension(width, height);
		addControls();
		
		// when the user presses the enter key in the search field, apply the query
		
		txtQuery.addActionListener(new ActionListener()
		{
			public void  actionPerformed(ActionEvent e)
			{ 
				query();
			}
		});
	}
	
	/**
	* Add query controls to the module
	*/
	
	private void addControls()
	{
		// Fill out the East and West and south regions of the module's BorderLayout
		// so the JPanel has a border
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
		
		// Add a new JPanel to contain the query controls
		
		JPanel queryPanel =  new JPanel();
		queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.X_AXIS));
		add(queryPanel, "Center");
		
		// Add a text field so that the user can enter a query
		
		txtQuery = new JTextField();
		txtQuery.setPreferredSize(new Dimension(200, 50));
		queryPanel.add(txtQuery);
		
		// Add a button so that the user can submit the query
		
		cmdSearch = new JButton("Search");
		cmdSearch.addActionListener(this);
		queryPanel.add(cmdSearch);
		
		setControleEnabled(false);
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	private void setControleEnabled(boolean b)
	{
		txtQuery.setEnabled(b);
		cmdSearch.setEnabled(b);
		
		if (b)
		{
			txtQuery.setBackground(Color.white);
		}
		else
		{
			txtQuery.setBackground(Color.lightGray);
		}
	}
	
	/**
	* Implement the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == cmdSearch)
		{
			query();
		}
	}
	
	/**
	* Query the Lucene search engine and return the indices of matchin items
	*/
	
	private void query()
	{
		// Query the Lucene search engine to retrieve all docs that contain the given
		// terms
		
		String searchString = txtQuery.getText().trim();
		
		if (!searchString.equals(""))
		{
			// Array to store IDs of matching items
			
			int[] r = null;
			
			try
			{
				Searcher searcher = new IndexSearcher(dataItems.getIndexPath());
				StemAnalyzer analyzer = new StemAnalyzer();
				
				Query q = QueryParser.parse(searchString, "contents", analyzer);
				Hits hits = searcher.search(q);
				
				r = new int[hits.length()];
				
				selection = new HashSet(hits.length());
				
				for (int i = 0; i < hits.length(); i++)
				{
					r[i] = hits.id(i);
					selection.add(new Integer(r[i]));
				}
				
				// Create a data item collection from the results
				
				results = dataItems.createNewCollection(r);
				
				// Send these to the output
				
				sendData();
			}
			catch (Exception e){}
		}
	}
	
	/**
	*  This is called when a connected module wants to notify this
	*  module of a change
	*/
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				// Data on the input port
				
				if (arg.get(0) instanceof DataItemCollection)
				{
					// We can only use this module for searching text data
					
					if (((DataItemCollection)arg.get(0)).isTextCorpus())
					{
						setControleEnabled(true);
						dataItems = (DataItemCollection)arg.get(0);
					}
				}
				else
					setControleEnabled(false);
			}
		}
		else
		{
			// Input module was deleted
			
			dataItems = null;
			setControleEnabled(false);
			getOutPort(0).sendData(null);
			getOutPort(1).sendData(null);
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 2;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' port
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data source");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' ports
		
		// Add an output port for found data items. That is, 
		// we might want to send these to another layout algorithm to
		// obtain a sub-layout or to a text viewer
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Results");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		port = new ModulePort(this, ScriptModel.SELECTION_PORT, 1);
		port.setPortLabel("Selection");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* When the user selects a sample size, this method is used to
	* send the sample and remainder to the output ports
	*/
	
	public void sendData()
	{	
		// Get references to the selction handling objects
		
		// Send the results data
		
		ArrayList transferData = new ArrayList();
		transferData.add(results);
		getOutPort(0).sendData(transferData);
		
		// Send the selection data
		
		transferData = new ArrayList(2);
		transferData.add(null);
		transferData.add(selection);
		transferData.add("queryResults");
		getOutPort(1).sendData(transferData);
	}
	
	/**
	* Overriding the method in DefaultVisualModule, this allows the module to tighten the
	* constraints upon inter-port (module) connections
	*/
	
	public boolean allowPortConnection(ModulePort port)
	{
		// In this case don't allow a data source to linked to this module
		// if it represents a triangular matrix.
		
		if ((port.getVisualModule() instanceof DataSource_Triangle) ||
			(port.getVisualModule() instanceof DataSource))
			return false;
		else
			return true;
	}
}

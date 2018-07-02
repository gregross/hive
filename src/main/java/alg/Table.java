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
 * Table
 *
 * Class represents an instance of a visual module for a Table visualisation
 *
 *  @author Greg Ross
 */
 package alg; 
 
import alg.fileloader.*;
import alg.fisheyeTable.*;
import data.*;
import parent_gui.*;

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box.Filler;

public class Table extends DefaultVisualModule
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// This holds the fisheye table and its controls
	
	protected FisheyeTableHolder tableholder;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 128;
	private int width = 153;
	
	// The data to be displayed
	
	DataItemCollection dataItems;
	
	// Store the selection objects that are defined in DataViewerTable
	
	private Collection selection;
	private SelectionHandler selectionHandler;
	
	// Reference to the DataViewerTable JTable instance
	
	private DataViewerTable dataTable;
	
	public Table(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Table");
		setToolTipText("Fisheye table");
		setLabelCaption(getName());
		
		dataItems = new DataItemCollection();
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.VISUALISATION_MODE);
		setDimension(width, height);
		setBackground(Color.lightGray);
		addControls();
	}
	
	private void addControls()
	{
		// Add a JPanel to the CENTER region of the module
		// Add a table
		
		tableholder = new FisheyeTableHolder(dataItems, mdiForm, this);
		
		// Get a reference to the actual table object that is embedded in
		// tableholder
		
		dataTable = tableholder.getDataViewerTable();
		
		add(tableholder, "Center");
		tableholder.setControlsEnabled(false);
		
		// Fill out the East and West and south regions of the module's BorderLayout
		// so the table has a border
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	/**
	* Called when the selection has changed and a component wants to view 
	* this data
	*
	*/
	
	public void updateThis()
	{	
		remove(tableholder);
		tableholder = new FisheyeTableHolder(dataItems, mdiForm, this);
		dataTable = tableholder.getDataViewerTable();
		
		// Get the selection handler from the underlying table
		
		selectionHandler = dataTable.getSelectionHandler();
		
		add(tableholder, "Center");
		validate();
		
		if (getParentForm().getLinkMode())
			tableholder.setVisible(false);
		else
			tableholder.setVisible(true);
		
		// Initially sort the table ascending on the first column
		
		dataTable.setSortIcon(0);
		
		DataViewerTableModel m = (DataViewerTableModel)dataTable.getModel();
		m.sort(0, DataViewerTableModel.SORT_ASCENDING);
		dataTable.repaint();
		dataTable.validate();
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
				
				tableholder.setControlsEnabled(true);
				dataItems = (DataItemCollection)(arg.get(0));
				
				// Display data in the table
				
				updateThis();
			}
			else if (toPort.getKey().equals("o0"))
			{
				// Selection data arrived
				
				if (dataItems != null)
				{
					selection = (Collection)arg.get(1);
					dataTable.setSelection(selection);
					try
					{
						selectionHandler.updateSelection();
					}
					catch (Exception e){}
				}
			}
		}
		else
		{
			// Input module was deleted
			
			dataItems = null;
			dataItems = new DataItemCollection();
			updateThis();
			revalidate();
			tableholder.setControlsEnabled(false);
			
			// Remove sort icon from table
			
			dataTable.getColumnModel().getColumn(0).setHeaderRenderer(null);
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 1;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' port
		
		port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
		port.setPortLabel("Selection");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* When the selection is changed, this is called to send the
	* selection to the selection port
	*/
	
	public void sendSelection()
	{	
		// Get references to the selction handling objects
		
		selection = dataTable.getSelection();
		
		ArrayList transferData = new ArrayList(2);
		transferData.add(dataItems);
		transferData.add(selection);
		getOutPort(0).sendData(transferData);
	}
	
	public void beforeSerialise()
	{
		tableholder.setFishEye();
	}
	
	/**
	* Overriding the method in DefaultVisualModule, this allows the module to tighten the
	* constraints upon inter-port (module) connections
	*/
	
	public boolean allowPortConnection(ModulePort port)
	{
		// In this case don't allow a data source to linked to this module
		// if it represents a triangular matrix.
		
		if (port.getVisualModule() instanceof DataSource_Triangle)
			return false;
		else
			return true;
	}
}

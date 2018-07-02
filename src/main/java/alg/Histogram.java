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
 * Histogram
 *
 * Class represents an instance of a visual module for a histogram visualisation
 *
 *  @author Greg Ross
 */
 package alg; 

import alg.histogram.DoubleSliderPanel;
import data.*;
import parent_gui.*;

import java.util.ArrayList;
import java.util.Collection;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.Box.Filler;

public class Histogram extends DefaultVisualModule
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The data
	
	private DataItemCollection dataItems;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 123;
	private int width = 159;
	
	private DoubleSliderPanel slider = null;
	private MouseHandler mouseHandler;
	
	private Collection selection;
	private SelectionHandler selectionHandler;
	
	public Histogram(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Histogram");
		setToolTipText("Histogram");
		setLabelCaption(getName());
		
		mouseHandler = new MouseHandler(this);
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.VISUALISATION_MODE);
		setDimension(width, height);
		setBackground(Color.lightGray);
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 2;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Data in");
		ports.add(port);
		
		// Add 'out' port
		
		port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
		port.setPortLabel("Selection");
		ports.add(port);
		
		// Add an output port for the selected data items. That is, 
		// we might want to send these to another layout algorithm to
		// obtain a sub-layout
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
		port.setPortLabel("Data out");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		addPorts(ports);
		
		addControls();
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
				
				if (slider != null)
				{
					remove(slider);
					slider = null;
				}
				
				dataItems = (DataItemCollection)(arg.get(0));
				
				// By default show the first numeric dimension
				// in the histogram
				
				ArrayList types  = dataItems.getTypes();
				
				for ( int i = 0 ; i < types.size() ; i++ ) 
				{
					// A Non String (Integer, Date, Double)
					// create a SLIDER
					
					if ( ((Integer)types.get(i)).intValue() != 
		                         	DataItemCollection.STRING ) 
					{
						getOutPort(1).sendData(null);
						slider =  new DoubleSliderPanel(dataItems, i, this);
						slider.setPreferredSize(getPreferredSize());
						slider.addMouseListener(mouseHandler);
						slider.setAlignmentX(LEFT_ALIGNMENT);
						
						add(slider);
						
						// Get the selection handler
						
						selectionHandler = slider.getSelectionHandler();
						slider.selectAll();
						selectionHandler.updateSelection();
						
						// If we're in link mode then the controls should not
						// be visible
						
						if (getParentForm().getLinkMode())
							slider.setVisible(false);
						else
							slider.setVisible(true);
						
						break;
					}
				}
			}
			else if (toPort.getKey().equals("o0"))
			{
				// Selection data arrived
				
				if (dataItems != null)
				{
					selection = (Collection)arg.get(1);
					
					if ((selection != null) && (slider != null))
					{
						slider.setSelection(selection);
						selectionHandler.updateSelection();
					}
				}
			}
		}
		else
		{
			// Input module was deleted
			
			dataItems = null;
			selection = null;
			if (slider != null)
				remove(slider);
			
			getOutPort(1).sendData(null);
			
			slider = null;
			dataItems = new DataItemCollection();
		}
	}
	
	private void addControls()
	{	
		// Fill out the East and West regions of the module's BorderLayout
		// so the graph has a border
		
		Filler filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
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
	* When the selection is changed, this is called to send the
	* selection to the selction port
	*/
	
	public void sendSelection()
	{	
		// Get references to the selction handling objects
		
		selection = slider.getSelection();
		
		ArrayList transferData = new ArrayList(2);
		transferData.add(dataItems);
		transferData.add(selection);
		getOutPort(0).sendData(transferData);
	}
	
	/**
	* When the user has made a selection and then presses the button to send the
	* represented data to the output port, this method is called
	*/
	
	public void sendDataOut()
	{
		ArrayList transferData = new ArrayList(2);
		
		// Create a new DataItemCollection from the selected points and
		// send it to the output
		
		DataItemCollection selectedData;
		int[] itemIndices = new int[slider.getSelectionData().size()];
		
		for (int j = 0; j < slider.getSelectionData().size(); j++)
		{
			itemIndices[j] = ((Integer)slider.getSelectionData().get(j)).intValue();
		}
		
		selectedData = dataItems.createNewCollection(itemIndices);
		
		transferData.add(selectedData);
		
		// Also add a reference to the original DataItemCollection
		
		transferData.add(dataItems);
		
		// If the rows are labelled, add the labels to the output
		
		if (dataItems.getRowLabels() != null)
		{
			ArrayList rowLabels = new ArrayList();
			
			for (int i = 0; i < itemIndices.length; i++)
			{
				rowLabels.add((String)dataItems.getRowLabels().get(itemIndices[i]));
			}
			
			transferData.add(rowLabels);
		}
		
		getOutPort(1).sendData(transferData);
	}
	
	private class MouseHandler extends MouseAdapter implements java.io.Serializable
	{
		// Versioning for serialisation
		
		static final long serialVersionUID = 50L;
		
		private JPanel selected;
		
		/**
		* cosntructor:
		*
		* @param parent The parent of this mouse handler
		*/
		
		public MouseHandler(Histogram histogram)
		{
			super();
		}
		
		/**
		* sets the currently selected JPanel, which is highlighted and its ref
		* is stored
		* @param selected The selected JPanel
		*
		*/
		
		public void setSelected( JPanel selected ){}
		
		/**
		* Called whenever a mouse click occurs
		*
		* @param me The mouse event which caused this call
		*/
		
		public void mouseClicked(MouseEvent me)
		{
			setFocus();
			bringToFront();
		}
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

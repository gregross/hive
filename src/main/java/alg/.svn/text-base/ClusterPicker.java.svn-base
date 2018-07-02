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
 * ClusterPicker
 *
 * Given several collections of data (clusters), this class ranks them and
 * allows the user to allocate them to various output ports
 *
 *  @author Greg Ross
 */
package alg;
 
import parent_gui.*;
import data.*;
import alg.clusterPicker.*;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.Box.Filler;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;

public class ClusterPicker extends DefaultVisualModule implements ActionListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 134;
	private int width = 200;
	
	// An array of DataItemCollections
	
	private ArrayList dataCollection = null;
	
	// The full data collection
	
	private DataItemCollection fullDataSet = null;
	
	// The numbmer of output ports that can each be allocated a cluster
	
	public static final int NUM_CLUSTERS_OUT = 8;
	
	// The JPanel that sits in the middle of the visual module and holds
	// all other controls
	
	private JPanel controlPane = null;
	
	// A refernce to the table that the user can ue to assign clusters to
	// output ports
	
	private ClusterPickerTable clusterTable = null;
	
	// If the set of input clusters have come from Voronoi clustering, then
	// store the correspdong cluster colours used in the Voronoi view
	
	private ArrayList clusterColours = null;
	
	public ClusterPicker(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Cluster picker");
		setToolTipText("Cluster picker");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
	}
	
	/**
	* Implement the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		
	}
	
	/**
	*  This is called when a connected module wants to notify this
	*  module of a change
	*/
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		int i;
		
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				// Data on the input port
				
				dataCollection = new ArrayList();
				
				if (arg.get(0) instanceof DataItemCollection)
				{
					fullDataSet = (DataItemCollection)arg.get(0);
					
					if (arg.size() > 1)
					{
						if (arg.get(1) instanceof ArrayList)
						{
							ArrayList dataSets = (ArrayList)arg.get(1);
							
							for (i = 0; i < dataSets.size(); i++)
								if (dataSets.get(i) instanceof DataItemCollection)
									dataCollection.add(dataSets.get(i));
								else if (dataSets.get(i) instanceof ArrayList)
								{
									clusterColours = (ArrayList)dataSets.get(i);
								}
							
							if (dataCollection.size() > 0)
							{
								clusterTable.init(dataCollection.size(), clusterColours);
								ouputClusters();
							}
						}
					}
				}
			}
		}
		else
		{
			// Input module was deleted
			
			dataCollection = null;
			
			for (i = 0; i < NUM_CLUSTERS_OUT; i++)
				getOutPort(i).sendData(null);
			
			// Remove all data from the table
			
			((ClusterPickerTableModel)clusterTable.getModel()).setData(new Object[0][0]);
			((ClusterPickerTableModel)clusterTable.getModel()).fireTableDataChanged();
		}
	}
	
	private void ouputClusters()
	{
		int count = 0;
		int numOutputPorts = NUM_CLUSTERS_OUT;
		
		for (int i = 0; i < dataCollection.size(); i++)
		{
			ArrayList t = new ArrayList();
			
			t.add(dataCollection.get(i));
			
			if (count < numOutputPorts)
				getOutPort(count).sendData(t);
			else
				return;
			
			count++;
		}
	}
	
	/**
	* Called from ClusterPickerTableModel when the user changes a port
	* allocation
	*/
	
	public void senOutput(int dataCollectionIndex, int portNumber)
	{
		if (dataCollectionIndex != -1)
		{
			ArrayList t = new ArrayList();
			t.add(dataCollection.get(dataCollectionIndex));
			getOutPort(portNumber).sendData(t);
		}
		else
			getOutPort(portNumber).sendData(null);
	}
	
	private void addControls()
	{
		addControlPane();
		addClusterTable();
	}
	
	private void addClusterTable()
	{
		clusterTable = new ClusterPickerTable(this);
		
		//Create the scroll pane and add the table to it
		
		JScrollPane scrollPane = new JScrollPane(clusterTable);
		controlPane.add(scrollPane);
	}
	
	private void addControlPane()
	{
		// Add a JPanel to the module. All other controls will rest on this JPanel
		
		controlPane = new JPanel();
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.X_AXIS));
		add(controlPane, "Center");
		
		// Make sure that the sides of this panel do not extend to the edges
		// of the module
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = NUM_CLUSTERS_OUT;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' ports
		
		for (int i = 0; i < NUM_CLUSTERS_OUT; i++)
		{
			port = new ModulePort(this, ScriptModel.OUTPUT_PORT, i);
			port.setPortLabel("Cluster " + (i + 1));
			port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
			ports.add(port);
		}
		
		addPorts(ports);
	}
}

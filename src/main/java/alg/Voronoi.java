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
 * Voronoi
 *
 * Class represents an instance of a visual module for a planar
 * ordinary Voronoi diagram
 *
 *  @author Greg Ross
 */

package alg;

import math.Coordinate;
import alg.voronoi.*;
import data.*;
import parent_gui.*;
import parent_gui.dataVolumeThresholding.HybridGenerator;
import parent_gui.dataVolumeThresholding.HybridAdaptor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Color;
import java.lang.Runnable;
import javax.swing.Box.Filler;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

public class Voronoi extends HybridAdaptor implements ItemListener, 
						ActionListener, ChangeListener, Runnable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 128;
	private int width = 153;
	
	// The respective low D positions
	
	private ArrayList positions;
	
	// The above positions but normalised to lie in the interval
	// [0, 1]
	
	private ArrayList normPositions;
	
	// The CSV data
	
	private DataItemCollection dataItems;
	
	// Store the selection objects that are defined in ScatterPanel
	
	private Collection selection;
	private SelectionHandler selectionHandler;
	
	// The JPanel subclass that renders the Voronoi diagram
	
	private VoronoiPane voronoiPane;
	
	// The thread through which this module runs
	
	private volatile Thread thread;
	
	// Control panel that also holds the main graph
	
	private JPanel cPanel;
	
	// Check boxes
	
	private JCheckBox chkShowAllEdges;
	private JCheckBox chkShowGenerators;
	private JCheckBox chkCluster;
	
	// Clustering button
	
	private JButton cmdCluster;
	
	// Radio buttons
	
	private JRadioButton rbPerimeter;
	private JRadioButton rbArea;
	
	// Slider for polygon area/perimeter threshold
	
	private JSlider clusterSlider;
	
	// JPanel for cluster/area shading
	
	private JPanel clusterPanel;
	private JPanel attPanel;
	
	// If a hybrid algorithm is not in place, set the following to false
	// This variable is required because the module inherits from HybridAdapter
	// and it runs in its own thread, effectively overriding HybridAdapter's
	// run method
	
	private boolean bHybridInPlace = false;
	
	// Value of the position on the cluster slider that represents the average
	// polygon perimeter/area
	
	private int avgPos = 0;
	
	public Voronoi(Mdi mdiForm, DrawingCanvas drawPane)
	{
		// Do HIVE-specific stuff:
		
		super(mdiForm, drawPane);
		
		setName("Voronoi");
		setToolTipText("Voronoi diagram");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.VISUALISATION_MODE);
		setDimension(width, height);
		setBackground(Color.lightGray);
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
			if (hybridInPlace(fromPort, toPort, arg)) // Only continue if a hybrid algorithm is in place
			{
				bHybridInPlace = true;
				
				if (toPort.getKey().equals("i0"))
				{
					// Data has arrived on the input
					
					DataItemCollection tempCSV = dataItems;
					
					if (arg.get(0) instanceof DataItemCollection)
						dataItems = (DataItemCollection)arg.get(0);
					
					if (arg.size() > 1)
						if (arg.get(1) instanceof ArrayList)
							positions = (ArrayList)arg.get(1);
					
					// If the CSV data being passed in is the same as the existing
					// data set, then we need only take notice of the positions data
					// being passed in.
					
					if (tempCSV != (DataItemCollection)arg.get(0))
					{
						// Only initialise the scatter panel with the data when
						// all of the data (the original set and the low D positions)
						// have been supplied
						
						if ((dataItems != null) && (positions != null))
						{
							startAlg();
						}
					}
					else
					{
						// Update the existing plotted points
						
						startAlg();
						
						positions = (ArrayList)arg.get(1);
						voronoiPane.setPositions(positions);
					}
				}
				else if (toPort.getKey().equals("o0"))
				{
					// Data has arrived on the selection port
					
					if ((dataItems != null) && (selectionHandler != null))
					{
						selection = (Collection)arg.get(1);
						
						voronoiPane.setSelection(selection);
						selectionHandler.updateSelection();
					}
				}
			}
			else
				bHybridInPlace = false;
		}
		else
		{
			// Input module was deleted
			
			clusterSlider.setEnabled(false);
			((TitledBorder)clusterSlider.getBorder()).setTitleColor(Color.gray);
			((TitledBorder)clusterPanel.getBorder()).setTitleColor(Color.gray);
			chkShowAllEdges.setEnabled(false);
			chkShowGenerators.setEnabled(false);
			chkCluster.setEnabled(false);
			cmdCluster.setEnabled(false);
			rbPerimeter.setEnabled(false);
			rbArea.setEnabled(false);
			
			bHybridInPlace = false;
			voronoiPane.clear();
			voronoiPane.init();
			dataItems = null;
			positions = null;
		}
	}
	
	public void startAlg()
	{
		// Wait for the current thread to die before
		// starting another
		
		try
		{
			thread.join();
		}
		catch (Exception e){}
		
		thread = new Thread(Voronoi.this);
		thread.start();
	}
	
	private void addControls()
	{
		voronoiPane = new VoronoiPane(this);
		
		cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
		cPanel.add(voronoiPane);
		add(cPanel, "Center");
		
		addCheckBoxes();
		addRadioButtons();
		
		((TitledBorder)clusterSlider.getBorder()).setTitleColor(Color.gray);
		((TitledBorder)clusterPanel.getBorder()).setTitleColor(Color.gray);
		
		// Fill out the East and West and south regions of the module's BorderLayout
		// so the graph has a border
		
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
	
	private void addCheckBoxes()
	{
		JPanel chkPane = new JPanel();
		chkPane.setLayout(new BoxLayout(chkPane, BoxLayout.X_AXIS));
		
		chkShowAllEdges = new JCheckBox("Edges");
		chkShowAllEdges.setEnabled(false);
		chkShowAllEdges.addItemListener(this);
		chkPane.add(chkShowAllEdges);
		
		chkShowGenerators = new JCheckBox("Generators", true);
		chkShowGenerators.setEnabled(false);
		chkShowGenerators.addItemListener(this);
		chkPane.add(chkShowGenerators);
		
		cPanel.add(chkPane);
	}
	
	/**
	* Add radio buttons to the interface to determine whether to shade polygons
	* according to an area/perimeter threshold or by clustering/partitioning
	*/
	
	private void addRadioButtons()
	{
		// JPanel for holding the clusterSlider and Checkbox
		
		clusterPanel = new JPanel();
		clusterPanel.setLayout(new BoxLayout(clusterPanel, BoxLayout.X_AXIS));
		clusterPanel.setBorder(BorderFactory.createTitledBorder("Polygon grouping:"));
		
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		
		// Add a check box that allow the user to enable or disable the
		// the highlighting/clustering of Voronoi polygons
		
		chkCluster = new JCheckBox("Enable", true);
		chkCluster.setEnabled(false);
		chkCluster.addItemListener(this);
		radioPanel.add(chkCluster);
		
		// Add a command button to let the user run the segmentation algorithm
		
		cmdCluster = new JButton("Cluster");
		cmdCluster.setEnabled(false);
		cmdCluster.addActionListener(this);
		radioPanel.add(cmdCluster);
		
		clusterPanel.add(radioPanel);
		
		// Create a new JPanel that will hold the slider and two
		// radio buttons. The radio buttons will dertermine whether
		// polygon perimeter or area is used in calculations
		
		attPanel = new JPanel();
		attPanel.setLayout(new BoxLayout(attPanel, BoxLayout.Y_AXIS));
		addPolygonattributePanel();
		addClusterSlider();
		
		clusterPanel.add(attPanel);
		
		// Add all to the main panel
		
		cPanel.add(clusterPanel);
	}
	
	/**
	* Add the controls that will allow the user to determine whether
	* the polygon perimeter or area is used in clustering
	*/
	
	private void addPolygonattributePanel()
	{
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		rbPerimeter = new JRadioButton("Perimeter");
		rbPerimeter.setSelected(true);
		rbPerimeter.setEnabled(false);
		rbPerimeter.addActionListener(this);
		rbArea = new JRadioButton("Area");
		rbArea.addActionListener(this);
		rbArea.setEnabled(false);
		ButtonGroup group = new ButtonGroup();
		group.add(rbPerimeter);
		group.add(rbArea);
		jp.add(rbPerimeter);
		jp.add(rbArea);
		attPanel.add(jp);
	}
	
	private void addClusterSlider()
	{
		clusterSlider = new JSlider(0, 1000);
		clusterSlider.setValue(0);
		clusterSlider.setEnabled(false);
		clusterSlider.addChangeListener(this);
		clusterSlider.setBorder(BorderFactory.createTitledBorder("Shade to area threshold:"));
		attPanel.add(clusterSlider);
	}
	
	// ItemListener interface implementation
	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() == chkShowAllEdges)
		{
			voronoiPane.setShowAllEdges(chkShowAllEdges.isSelected());
			voronoiPane.update();
		}
		else if (e.getSource() == chkShowGenerators)
		{
			voronoiPane.setShowGenerators(chkShowGenerators.isSelected());
			voronoiPane.update();
		}
		else if (e.getSource() == chkCluster)
		{
			if (chkCluster.isSelected())
			{
				rbPerimeter.setEnabled(true);
				rbArea.setEnabled(true);
				((TitledBorder)clusterPanel.getBorder()).setTitleColor(Color.black);
				((TitledBorder)clusterSlider.getBorder()).setTitleColor(Color.black);
				voronoiPane.setCluster(true);
				clusterSlider.setEnabled(true);
				cmdCluster.setEnabled(true);
				voronoiPane.update();
			}
			else
			{
				cmdCluster.setEnabled(false);
				rbPerimeter.setEnabled(false);
				rbArea.setEnabled(false);
				((TitledBorder)clusterPanel.getBorder()).setTitleColor(Color.gray);
				((TitledBorder)clusterSlider.getBorder()).setTitleColor(Color.gray);
				
				voronoiPane.setCluster(false);
				clusterSlider.setEnabled(false);
			}
			
			voronoiPane.update();
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 3;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' port
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortDataStructure(ScriptModel.VECTOR);
		port.setPortLabel("Data in");
		ports.add(port);
		
		// Add 'out' selection port
		
		port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
		port.setPortLabel("Selection");
		ports.add(port);
		
		// Add 'out' port for the selected cluster data
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Cluster out");
		ports.add(port);
		
		// Add 'out' port for all clusters
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 2);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("All clusters out");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* The planar ordinary Voronoi diagram is based upon the incremental algorithm
	* This means that the input data must be normalised to be within the interval
	* [0, 1]. The following method carries out this normalisation
	*/
	
	private void normalisePositions()
	{
		double highestX = Double.MIN_VALUE;
		double highestY = Double.MIN_VALUE;
		double lowestX = Double.MAX_VALUE;
		double lowestY = Double.MAX_VALUE;
		
		Coordinate c, newC;
		int i;
		
		normPositions = new ArrayList();
		
		for (i = 0; i < positions.size(); i++)
		{
			c = (Coordinate)positions.get(i);
			if (c.getX() > highestX)
				highestX = c.getX();
			
			if (c.getY() > highestY)
				highestY = c.getY();
			
			if (c.getX() < lowestX)
				lowestX = c.getX();
			
			if (c.getY() < lowestY)
				lowestY = c.getY();
		}
		
		double newX, newY;
		
		// Scale and transpose the positions
		
		/*if (lowestX > 0) lowestX = 0;
		if (lowestY > 0) lowestY = 0;
		if ((highestX < 1) || (highestX == 0)) highestX = 1;
		if ((highestY < 1) || (highestY == 0)) highestY = 1;
		
		lowestX = Math.abs(lowestX);
		lowestY = Math.abs(lowestY);
		
		highestX += lowestX;
		highestY += lowestY;
		
		for (i = 0; i < positions.size(); i++)
		{
			c = (Coordinate)positions.get(i);
			
			newX = (lowestX + c.getX()) / highestX;
			newY = (lowestY + c.getY()) / highestY;
			
			newC = new Coord(newX, newY);
			
			normPositions.add(newC);
		}*/
		
		double diffValX = 0 - lowestX;
		double diffValY = 0 - lowestY;
		for (i = 0; i < positions.size(); i++)
		{
			c = (Coordinate)positions.get(i);
			
			if (c.getX() == lowestX)
				newX = 0;
			else
				newX = (diffValX + c.getX()) / (highestX + diffValX);
			
			if (c.getY() == lowestY)
				newY = 0;
			else
				newY = (diffValY + c.getY()) / (highestY + diffValY);
			
			newC = new Coord(newX, newY);
			
			normPositions.add(newC);
		}
	}
	
	public void run()
	{
		// If there's no algorithm in place that will allow the Voronoi module to
		// render the hD data in 2D, then create one via the HybridAdapter's
		// run method
		
		if (bHybridInPlace)
		{
			Thread thisThread = Thread.currentThread();
			while (thread == thisThread)
			{
				clusterSlider.setEnabled(false);
				chkCluster.setEnabled(false);
				cmdCluster.setEnabled(false);
				rbArea.setEnabled(false);
				rbPerimeter.setEnabled(false);
				
				voronoiPane.clear();
				normalisePositions();
				voronoiPane.renderVoronoi(normPositions, dataItems);
				selectionHandler = voronoiPane.getSelectionHandler();
				thread = null;
				
				chkCluster.setEnabled(true);
				
				if (chkCluster.isSelected())
				{
					cmdCluster.setEnabled(true);
					rbPerimeter.setEnabled(true);
					rbArea.setEnabled(true);
					((TitledBorder)clusterPanel.getBorder()).setTitleColor(Color.black);
					((TitledBorder)clusterSlider.getBorder()).setTitleColor(Color.black);
					clusterSlider.setEnabled(true);
					
					voronoiPane.setCluster(true);
				}
				
				chkShowAllEdges.setEnabled(true);
				chkShowGenerators.setEnabled(true);
			}
		}
		else
		{
			try
			{
				super.run();
			}
			catch (Exception e){};
		}
	}
	
	/**
	* After the Voronoi has been run, update the slider for filtering
	* polygons by perimeter/size
	*/
	
	private void updateSlider()
	{
		// Mark the average Voronoi polygon perimeter/area on the slider
		
		Hashtable labelTable = new Hashtable();
		double incSize;
		String dev;
		
		if (voronoiPane.getPerimeterCalc())
		{
			incSize = voronoiPane.getAveragePerimeter() /
				(voronoiPane.getStdDevPerimeter() / (double)clusterSlider.getMaximum());
			dev = "1 stdDev";
		}
		else
		{
			incSize = voronoiPane.getAverageArea() /
				((voronoiPane.getStdDevArea()/5d) / (double)clusterSlider.getMaximum());
			dev = "1/5 stdDev";
		}
		
		avgPos = (new Double(incSize)).intValue();
		
		labelTable.put(new Integer(avgPos), new JLabel("avg"));
		labelTable.put(new Integer(clusterSlider.getMaximum()), new JLabel(dev));
		
		clusterSlider.setMajorTickSpacing(clusterSlider.getMaximum() / 4);
		clusterSlider.setPaintTicks(true);
		
		clusterSlider.setLabelTable(labelTable);
		clusterSlider.setPaintLabels(true);
	}
	
	/**
	* When the selection is changed, this is called to send the
	* selection to the selction port
	*/
	
	public void sendSelection()
	{
		// Get references to the selction handling objects
		
		selection = voronoiPane.getSelection();
		
		ArrayList transferData = new ArrayList(2);
		transferData.add(dataItems);
		transferData.add(selection);
		getOutPort(0).sendData(transferData);
	}
	
	/**
	* When a cluster is selected, this method is called to create a new DataItemCollection
	* representing the cluster, and send it to the output
	*/
	
	public void sendCluster(ArrayList cluster)
	{
		if (cluster == null) return;
		
		int[] itemIndices = new int[cluster.size()];
		
		for (int i = 0; i < cluster.size(); i++)
			itemIndices[i] = ((Integer)cluster.get(i)).intValue();
		
		DataItemCollection clusterOut = dataItems.createNewCollection(itemIndices);
		
		ArrayList transferData = new ArrayList();
		transferData.add(clusterOut);
		// Also add a reference to the original DataItemCollection
		
		transferData.add(dataItems);
		getOutPort(1).sendData(transferData);
	}
	
	/**
	* After clustering has been performed, this methpd is called to
	* convert each cluster to a DataItemCollection and send them to the
	* output for all clusters
	*/
	
	public void sendAllClusters(ArrayList clusters, ArrayList clusterColours)
	{
		if (clusters != null)
		{
			ArrayList output = new ArrayList(2);
			output.add(dataItems);
			
			ArrayList clusterSet = new ArrayList(clusters.size() + 1);
			
			ArrayList aTemp;
			alg.voronoi.Polygon p;
			int itemKey;
			DataItemCollection dTemp;
			int[] itemIndices;
			
			for (int i = 0; i < clusters.size(); i++)
			{
				aTemp = (ArrayList)clusters.get(i);
				
				itemIndices = new int[aTemp.size()];
				
				for (int j = 0; j < aTemp.size(); j++)
				{
					p = (alg.voronoi.Polygon)aTemp.get(j);
					itemKey = p.getGenerator().getIndex();
					itemIndices[j] = itemKey;
				}
				
				dTemp = dataItems.createNewCollection(itemIndices);
				
				clusterSet.add(dTemp);
			}
			
			if (clusterSet.size() > 0)
			{
				// Also send the colours corresponding to those indicating the clusters in the view
				
				clusterSet.add(clusterColours);
				
				output.add(clusterSet);
				getOutPort(2).sendData(output);
			}
			else
				getOutPort(2).sendData(null);
		}
	}
	
	/**
	* Interface implementation for the threshold slider's
	* ChangeListener
	*/
	
	public void stateChanged(ChangeEvent e)
	{
		sliderStateChanged();
	}
	
	private void sliderStateChanged()
	{
		double incSize;
		
		if (voronoiPane.getPerimeterCalc())
			incSize =  voronoiPane.getStdDevPerimeter() / (double)clusterSlider.getMaximum();
		else
			incSize =  (voronoiPane.getStdDevArea()/5d) / (double)clusterSlider.getMaximum();
		
		voronoiPane.setThresholdAttribute(clusterSlider.getValue() * incSize);
		voronoiPane.update();
	}
	
	/**
	* Interface implementation for ActionListener
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == rbPerimeter)
		{
			voronoiPane.setPerimeterCalc(true);
			voronoiPane.setAreaCalc(false);
			sliderStateChanged();
			updateSlider();
			voronoiPane.update();
		}
		else if (e.getSource() == rbArea)
		{
			voronoiPane.setAreaCalc(true);
			voronoiPane.setPerimeterCalc(false);
			sliderStateChanged();
			updateSlider();
			voronoiPane.update();
		}
		else if (e.getSource() == cmdCluster)
		{
			voronoiPane.cluster();
		}
	}
	
	public JCheckBox getClusterCheck()
	{
		return chkCluster;
	}
	
	public void beforeSerialise()
	{
		// Don't serialise until the main thread has stopped
		
		try
		{
			if (thread != null)
				while (thread.isAlive()){}
		}
		catch (Exception e){}
		
		voronoiPane.beforeSerialise();
	}
	
	/**
	* A bug in Java 1.4 serialisation means that the winged-edge data structure causes
	* deep recursion and therefore a StackOverflowError. To get round this, all edge and
	* polygon references etc are transient, i.e. we dont't serialise the structure.
	* Thus, we have to rebuild it upon deserialisation.
	*/
	
	private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		voronoiPane.clear();
		if ((dataItems != null) && (positions != null))
		{
			startAlg();
		}
	}
}

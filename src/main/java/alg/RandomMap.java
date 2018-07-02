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
 * RandomMap
 *
 * Class represents an instance of a visual module for random mapping
 *
 *  @author Alistair Morrison, Greg Ross
 */
package alg; 
 
import parent_gui.*;
import data.*;
import math.*;
import javax.swing.JPanel;

import java.lang.Math;
import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.event.*;

public class RandomMap extends DefaultVisualModule 
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 64;
	private int width = 114;
	
	// The input data
	
	private DataItemCollection dataItems;
	
	// The random matrix
	
	private ArrayList randomMatrix;
	
	// The lowD values
	
	private ArrayList mappedData;
	
	// Controls
	
	private JButton select= new JButton("Project");
	
	// number of integer/real dimensions in dataItems
	
	private int numNumericDims;
	
	// The high dimensional matrix of data derived from dataItems
	
	private double[][] rData;
	
	public RandomMap(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("rnd Mapping");
		setToolTipText("Random Mapping");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
		setControlsEnabled(false);
	}
	
	private void addControls()
	{
		JPanel centrePane = new JPanel();
		select.setEnabled(false);
		addButtonActionListeners();
		centrePane.add(select);
		centrePane.setOpaque(false);
		add(centrePane, "Center");
		setInterfaceVisibility();
	}
	
	private void setControlsEnabled(boolean bEnabled)
	{
		select.setEnabled(bEnabled);
	}
	
	private void init() 
	{
		mappedData = new ArrayList();
		mappedData.clear();
		for (int i = 0 ; i < dataItems.getSize() ; i++) 
		{
		    mappedData.add(new Coordinate());
		}
	}
	
	/**
	* Fill the D*2 matrix with random values
	*/
	
	private void selectRandomMatrix() 
	{
		int width = 2;
		int length = numNumericDims;
		randomMatrix = new ArrayList();
		
		for (int i=0; i<length; i++)
		{
		    ArrayList thisRow = new ArrayList();
		    for (int j=0;j<width;j++)
			thisRow.add(new Double(Math.random()));
		    randomMatrix.add(thisRow);
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
			
			dataItems = (DataItemCollection)(arg.get(0));
			if (dataItems.getSize() > 0){
			    setControlsEnabled(true);
			    init();
			    getOutPort(0).sendData(null);
			}
		    }
		}
		else 
		{
		    // Input module was deleted
		    
		    dataItems = null;
		    setControlsEnabled(false);
		    getOutPort(0).sendData(null);
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
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Output");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* For each item in dataOriginal, create a numerically continuous
	* vector representation.
	*/
	private void createInputs()
	{
		int numFieldsOriginal = dataItems.getNumFields();
		
		// Use only the numerical fields to create the array of 
		// inputs
		
		// Determine the number of numerical elements in each input
		// vector to be used in the calculations
		
		ArrayList numPositions = new ArrayList();
		numNumericDims = 0;
		
		for (int i = 0; i < numFieldsOriginal; i++)
		{
			if (((DataItem)dataItems.getDataItem(0)).getValues()[i] instanceof Date)
			{
				numNumericDims++;
				numPositions.add(new Integer(i));
			}
			else if (((DataItem)dataItems.getDataItem(0)).getValues()[i] instanceof Integer)
			{
				numNumericDims++;
				numPositions.add(new Integer(i));
			}
			else if (((DataItem)dataItems.getDataItem(0)).getValues()[i] instanceof Double)
			{
				numNumericDims++;
				numPositions.add(new Integer(i));
			}
		}
		
		// Initialise the numerically continuous input array so that its rows represent the
		// numerical values and its columns represent the data items
		
		rData = new double[numNumericDims][dataItems.getSize()];
		
		// For each original data item create a vector for K-means input
		
		double dTemp = 0.0d;
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			for (int j = 0; j < numNumericDims; j++)
			{	
				int iTemp = ((Integer)numPositions.get(j)).intValue();
				Object dat = ((DataItem)dataItems.getDataItem(i)).getValues()[iTemp];
				
				if (dat instanceof Date)
					dTemp = (double)((Date)dat).getTime();
				else if (dat instanceof Integer)
					dTemp = (double)((Integer)dat).intValue();
				else if (dat instanceof Double)
					dTemp = ((Double)dat).doubleValue();
				else // NaN
					dTemp = Double.MAX_VALUE;
				
				if (dTemp < Double.MAX_VALUE)
				{
					// Normalise the data
					
					double average
						= dataItems.average()[iTemp];
						
					double sigma = dataItems.sigma()[iTemp];
					
					dTemp = (dTemp - average) / (2.0d * sigma);
					
					rData[j][i] =  dTemp;
				}
			}	
		}
	}
	
	/**
	* Called when the data is text based (tf-idf)
	*/
	
	private void createInputsText()
	{
		int numFieldsOriginal = dataItems.getNumFields();
		
		// Use only the numerical fields to create the array of 
		// inputs
		
		ArrayList numPositions = new ArrayList();
		numNumericDims = dataItems.getFields().size();
		
		rData = new double[numNumericDims][dataItems.getSize()];
		
		// For each original data item create a vector for input
		
		double dTemp = 0.0d;
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			for (int j = 0; j < numNumericDims; j++)
			{
				dTemp = ((DataItem)dataItems.getDataItem(i)).getTextValues()[j];
				
				if (dTemp < Double.MAX_VALUE)
				{
					// Normalise the data
					
					double average = dataItems.average()[j];
					
					double sigma = dataItems.sigma()[j];
					
					dTemp = (dTemp - average) / (2.0d * sigma);
					
					rData[j][i] =  dTemp;
				}
			}	
		}
	}
	
	/**
	*  Multiply the input data by the random matrix - project to 2D
	*/
	
	private void go() 
	{
		double total;
		double xBiggest=0.0, yBiggest=0.0;
		for (int i = 0; i < dataItems.getSize(); i++) 
		{
		     ArrayList thisRow = new ArrayList();
		     for (int j = 0; j < 2; j++) 
		     {
			 total=0;
			 for (int k = 0; k < randomMatrix.size(); k++) 
			 {
			 	total += rData[k][i] * ((Double)((ArrayList)randomMatrix.get(k)).get(j)).doubleValue();
			 }
			 if (j==0)
			 {
			     ((Coordinate)mappedData.get(i)).setX(total);
			 }
			 else
			 {
			     ((Coordinate)mappedData.get(i)).setY(total);
			 }
		     }
		 }
	}
	
	private void addButtonActionListeners()
	{
		select.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				setFocus();
				
				if (((DataItem)dataItems.getDataItem(0)).getTextValues() == null)
					createInputs();
				else
					createInputsText();
				
				selectRandomMatrix();
				go();
				
				// Send 2D positions to the output
				
				ArrayList transferData;
				transferData = new ArrayList();
				transferData.add(dataItems);
				transferData.add(mappedData);
				getOutPort(0).sendData(transferData);
			}
		});
	}
	
	/**
	* Method to restore action listener for the JButtons
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, 
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		mdiForm = parent_gui.Mdi.getInstance();
		addButtonActionListeners();
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

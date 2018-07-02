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
 * DataTranspose
 *
 * This visual module is used to transpose a CSV data set, i.e. each column becomes a row
 *
 *  @author Greg Ross
 */
package alg;

import parent_gui.*;
import data.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.Box.Filler;

public class DataTranspose extends DefaultVisualModule implements ItemListener
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
	
	// The transposed data
	
	private DataItemCollection transposedData = null;
	
	private ArrayList fieldNames = null;
	
	// Checkbox for determining whether the data are normalised to the [0, 1] interval
	
	private JCheckBox chkNormalise = null;
	
	// Stop the item listener firing when we set the normalisation checkbox
	
	private boolean bToggleNorm = false;
	
	public DataTranspose(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Transpose");
		setToolTipText("Transpose input data");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
	}
	
	private void addControls()
	{
		chkNormalise = new JCheckBox("normalise [0, 1]", false);
		chkNormalise.addItemListener(this);
		chkNormalise.setSelected(false);
		chkNormalise.setEnabled(false);
		add(chkNormalise);
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
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
				
				// Create an entirely new instance of the data set to buffer
				// changes
				
				int[] keys = new int[dataItems.getSize()];
				for (int i = 0; i < dataItems.getSize(); i++)
				{
					keys[i] = i;
				}
				dataItems = dataItems.createNewCollection(keys);
				
				if (dataItems != null)
				{
					if (dataItems.getSize() > 0)
					{
						if (dataItems.allDoublesOrIntegers())
						{
							// If the data has come from a selection mede in a scatterplot, and the collection
							// had row labels then these labels will be in the third element of the data
							// that is transferred into this module
							
							if (arg.size() == 3)
							{
								// There are some row labels which will become
								// field labels
								
								fieldNames = (ArrayList)arg.get(2);
							}
							else
								fieldNames = null;
							
							chkNormalise.setEnabled(true);
							
							if (chkNormalise.isSelected())
								bToggleNorm = true;
							
							chkNormalise.setSelected(false);
							
							setBackground(Color.orange);
							createTransposedDataSet();
							sendData();
							return;
						}
					}
				}
			}
			setBackground(Color.red);
			getOutPort(0).sendData(null);
		}
		else
		{
			// Input module was deleted
			
			chkNormalise.setEnabled(false);
			setBackground(Color.orange);
			dataItems = null;
			transposedData = null;
			fieldNames = null;
			
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
		port.setPortLabel("Data out");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* Send the transposed data to the output
	*/
	
	public void sendData()
	{	
		// Get references to the selction handling objects
		
		// Send the sample
		
		ArrayList transferData = new ArrayList();
		transferData.add(transposedData);
		
		getOutPort(0).sendData(transferData);
	}
	
	private void createTransposedDataSet()
	{
		// Create the data collection and set the fields and types
		
		transposedData = new DataItemCollection();
		
		if (!dataItems.getTransposed())
		{
			transposedData.setTransposed(true);
			transposedData.setRowLabels(dataItems.getFields());
		}
		else
			transposedData.setTransposed(false);
		
		ArrayList fields = new ArrayList(dataItems.getSize());
		ArrayList types = new ArrayList(dataItems.getSize());
		int i;
		
		for (i = 0; i < dataItems.getSize(); i++)
		{
			fields.add((new Integer(i)).toString());
			types.add(new Integer(DataItemCollection.DOUBLE));
		}
		
		// If the original collection had row labels, make these the new column labels
		
		if (fieldNames == null)
			transposedData.setFields(fields);
		else
			transposedData.setFields(fieldNames);
		
		transposedData.setTypes(types);
		
		// If the original data are binary then the transposed set are also
		// binary
		
		transposedData.setBinary(dataItems.getBinary());
		
		// Set normalisation stuff
		
		double[] sumOfVals;
		double[] sumOfSquares;
		sumOfVals    = new double[fields.size()];
		sumOfSquares = new double[fields.size()];
		transposedData.intiNormalArrays(sumOfVals, sumOfSquares);
		DataItem newItem;
		ArrayList values;
		double val;
		
		// Create each new data item from each column of dataItems
		
		for (int j = 0; j < dataItems.getTypes().size(); j++)
		{
			values = new ArrayList();
			
			for (i = 0; i < dataItems.getSize(); i++)
			{
				if (((DataItem)dataItems.getDataItem(i)).getValues()[j] instanceof Double)
					val = ((Double)((DataItem)dataItems.getDataItem(i)).getValues()[j]).doubleValue();
				else
					val = ((Integer)((DataItem)dataItems.getDataItem(i)).getValues()[j]).doubleValue();
				
				values.add(new Double(val));
				sumOfVals[i] += val;
				sumOfSquares[i] += (val * val);
			}
			
			newItem = new DataItem(values.toArray(), j);
			
			transposedData.addItem(newItem);
		}
		
		transposedData.getDataItems().trimToSize();
        	transposedData.setNormalizeData(sumOfVals, sumOfSquares);
		
		// Set the variable frequencies
		
		transposedData.setBinaryFreq(dataItems.getBinaryFreq());
		transposedData.setMaxFreq(dataItems.getMaxFreq());
		transposedData.setMinFreq(dataItems.getMinFreq());
		
		transposedData.setBinaryNumVars(dataItems.getBinaryNumVars());
		transposedData.setMaxNumVars(dataItems.getMaxNumVars());
		transposedData.setMinNumVars(dataItems.getMinNumVars());
		transposedData.setIndexPath(dataItems.getIndexPath());
		transposedData.setDataPath(dataItems.getDataPath());
	}
	
	/**
	* Implementation of the ItemListener interface
	*/
	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() == chkNormalise)
		{
			if (!bToggleNorm)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					if ((transposedData != null) && (dataItems != null))
					{
						if (dataItems.getTransposed())
							transposedData.setNormalised(true);
						else
						{
							dataItems.setNormalised(true);
							createTransposedDataSet();
						}
					}
				}
				else
				{
					if ((transposedData != null) && (dataItems != null))
					{
						if (dataItems.getTransposed())
						{
							transposedData.setNormalised(false);
						}
						else
						{
							dataItems.setNormalised(false);
							createTransposedDataSet();
						}
					}
				}
				
				sendData();
			}
			else
				bToggleNorm = false;
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

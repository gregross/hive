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
 * SSA 
 *
 * Class implementing code for Smallest Space Analysis (or Similar Structure Analysis)
 *
 *  @author Greg Ross
 */
package alg; 
 
import parent_gui.*;
import data.*;
import math.*;
import alg.ssa.SSA_ResultsView;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.Box.Filler;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.event.*;
import java.io.*;

public class SSA extends DefaultVisualModule
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
	
	// The input data
	
	private DataItemCollection dataItems;
	
	// The path to the SSA input data file
	
	private String inputFilePath = null;
	
	private ArrayList fieldNames = null;
	
	// Store the output coordinates of SSA routine
	
	private double[] SSA_XOutput;
	private double[] SSA_YOutput;
	
	// The native method for performing SSA automatically runs on the variables/columns of
	// the dataset instead of the rows, we therefore must transpose the dataset if we wish it
	// to work on rows by default
	
	private DataItemCollection transposedSet = null;
	
	private final JButton start = new JButton("Start");
	private final JButton showResults = new JButton("Details");
	
	public SSA(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("SSA");
		setToolTipText("Smallest Space Analysis");
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
		// Add a new JPanel to the centre of the module
		
		JPanel jPane = new JPanel();
		jPane.setOpaque(false);
		start.setEnabled(false);
		showResults.setEnabled(false);
		
		addButtonActionListeners();
		
		jPane.add(start);
		jPane.add(showResults);
		
		add(jPane, "Center");
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	private void addButtonActionListeners()
	{
		// Add a button to start SSA, to this
		// new JPanel
		
		start.addActionListener(new ActionListener()
		{
            		public void actionPerformed(ActionEvent e)
			{
				setFocus();
				
				// Send a trigger stating that the model has started
				
				ArrayList transferD = new ArrayList();
				transferD.add("start");
				getOutPort(2).sendData(transferD);
				
				// Start the model
				
				runSSA();
				showResults.setEnabled(true);
				
				// Now send a signal to say that SSA has finished
				
				transferD = new ArrayList();
				transferD.add("startNextMod");
				getOutPort(1).sendData(transferD);
            		}
        	});
		
		// Add a button to show SSA details such as co-efficient of alienation etc
		
		showResults.addActionListener(new ActionListener()
		{
            		public void actionPerformed(ActionEvent e)
			{
				// Open a singleton view to show the details of the
				// last SSA run. These details are parsed from the
				// COEFFS.TMP file that is generated by the SSA DLL
				
				SSA_ResultsView.getInstance();
            		}
        	});
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
				getOutPort(0).sendData(null);
				
				if (dataItems.getSize() > 0)
				{
					setBackground(Color.orange);
					
					// Only allow SSA on non-textual, numeric data
					
					if (dataItems.allDoublesOrIntegers() && dataItems.getSize() <= 100)
					{
						// If the data has come from a selection made in a scatterplot, and the collection
						// had row labels then these labels will be in the third element of the data
						// that is transferred into this module
						
						if ((arg.size() == 3) && !dataItems.isTextCorpus())
						{
							// There are some row labels which will become
							// field labels
							
							fieldNames = (ArrayList)arg.get(2);
						}
						else
							fieldNames = null;
						
						start.setEnabled(true);
					}
					else
					{
						// Show that the input is invalid
						
						start.setEnabled(false);
						showResults.setEnabled(false);
						setBackground(Color.red);
						getOutPort(0).sendData(null);
					}
				}
				else
				{
					start.setEnabled(false);
					showResults.setEnabled(false);
				}
			}
		}
		else
		{
			// Input module was deleted
			
			start.setEnabled(false);
			showResults.setEnabled(false);
			dataItems = null;
			transposedSet = null;
			getOutPort(0).sendData(null);
			setBackground(Color.orange);
		}
	}
	
	private void runSSA()
	{
		// Create a transposed collection from the data
		
		transposedSet = transposeDataSet();
		
		// Get the current working directory
		
		String userdir = System.getProperty("user.dir");
		
		// Load the C++ DLL containing the SSA functions
		
		System.load(userdir + "/atb/alg/ssa/ssa_DLL.dll");
		
		// Create a LIFA-formatted file from the DataItemCollection
		
		if (dataItems.getLowerTriangular())
		{
			inputFilePath = userdir + "\\atb\\alg\\ssa\\msv.dat";
			createSSA_TriangleFile();
		}
		else
		{
			inputFilePath = userdir + "\\atb\\alg\\ssa\\msv.dat";
			createSSA_InputFile();
		}
		
		// Invoke the DLL (native) doSSA function via JNI
		
		doSSA(inputFilePath);
		
		// The SSA routine output is in the form of a text file in the current user
		// directory. Parse this file to get the output coordinates
		
		ArrayList positions = createPositions(userdir);
		
		// Send to the output
		
		ArrayList transferData = new ArrayList();
		transferData.add(dataItems);
		transferData.add(positions);
		
		getOutPort(0).sendData(transferData);
	}
	
	/** 
	* The native SSA routine works by defult on the columns. We must therefore
	* transpose the dataset to have it work on the rows by default
	*/
	
	private DataItemCollection transposeDataSet()
	{
		// Create the data collection and set the fields and types
		
		DataItemCollection transposedData = new DataItemCollection();
		
		transposedData.setDataPath(dataItems.getDataPath());
		
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
		
		return transposedData;
	}
	
	/**
	* The native SSA routine places its output coordinates in a text file.
	* This method parses the text file and creates positions from the coordinates for
	* each item
	*/
	
	private ArrayList createPositions(String userDir)
	{
		int setSize = dataItems.getSize();
		ArrayList positions = new ArrayList(setSize);
		BufferedReader outputFile = null;
		
		try
		{
			outputFile = new BufferedReader(new FileReader(userDir + "/COORDS.TMP"));
			String line = outputFile.readLine();
			String[] splitLine;
			int i, j = 0;
			int nonZero;
			double[] coords = new double[2];
			SSA_XOutput = new double[setSize];
			SSA_YOutput = new double[setSize];
			
			while ((line != null) && (j < setSize))
			{
				nonZero = 0;
				splitLine = line.split(" ");
				
				for (i = 0; i < splitLine.length; i++)
				{
					if (!splitLine[i].equals(""))
					{
						if (nonZero >= 2)
							coords[nonZero - 2] = Double.parseDouble(splitLine[i]);
							
						nonZero++;
					}
				}
				
				line = outputFile.readLine();
				
				SSA_XOutput[j] = coords[0];
				SSA_YOutput[j] = coords[1];
				
				j++;
			}
			outputFile.close();
			NormalisePoints();
			
			for (i = 0; i < SSA_XOutput.length; i++)
				positions.add(new Coordinate(SSA_XOutput[i], SSA_YOutput[i]));
		}
		catch (Exception e)
		{
			if (outputFile != null)
				try{outputFile.close();} catch (IOException e2){}
			
			System.out.println("Error reading output file.");
		}
		
		return positions;
	}
	
	// Normalise the SSA coordinates to [0, 1]..
	
	private void NormalisePoints()
	{
		int i;
		double X;
		double Y;
		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = Double.MIN_VALUE;
		
		// Loop through the values to find the min and max values.
		
		for (i = 0; i < SSA_XOutput.length; i++)
		{
			if (SSA_XOutput[i] > xMax)
				xMax = SSA_XOutput[i];
			
			if (SSA_XOutput[i] < xMin)
				xMin = SSA_XOutput[i];
			
			if (SSA_YOutput[i] > yMax)
				yMax = SSA_YOutput[i];
			
			if (SSA_YOutput[i] < yMin)
				yMin = SSA_YOutput[i];
		}
		
		double xCur, yCur;
		
		for (i = 0; i < SSA_XOutput.length; i++)
		{
			xCur = SSA_XOutput[i];
			yCur = SSA_YOutput[i];
			
			X = 1 * (xCur - xMin) / (xMax - xMin);
			Y = 1 * (yCur - yMin) / (yMax - yMin);
			
			// Reverse the axes direction and convert from [0, 1] interval to
			// [-0.5, 0.5]
			
			SSA_XOutput[i] = 1 - X - 0.5;
			SSA_YOutput[i] = 1- Y - 0.5;
		}
	}
	
	/**
	* If the input is in the form of a trianglular matrix, remove the first line.
	* This line contains labels.
	*/
	
	private void createSSA_TriangleFile()
	{
		FileOutputStream out = null;
		BufferedReader outputFile = null;
		try
		{
			outputFile = new BufferedReader(new FileReader(dataItems.getDataPath()));
			String line = outputFile.readLine(); // Miss first line.
			line = outputFile.readLine();
			PrintWriter pw;
			out = new FileOutputStream(inputFilePath);
			pw = new PrintWriter(out, true);
			DataItem item;
			
			while (line != null)
			{
				pw.print(line);
				pw.println("");
				line = outputFile.readLine();
			}
			
			out.close();
			outputFile.close();
		} catch (IOException e)
		{
			System.out.println("Error creating SSA input");
			
			if (out != null)
				try{out.close(); outputFile.close();} catch (IOException e2){}
		}
	}
	
	/**
	* The input to SSA is in the form of a file formatted for the LIFA software
	* "Liverpool Interactive Facet Analysis"
	*/
	
	private void createSSA_InputFile()
	{
		FileOutputStream out = null;
		try
		{
			PrintWriter pw;
			out = new FileOutputStream(inputFilePath);
			pw = new PrintWriter(out, true);
			DataItem item;
			
			for (int i = 0; i < transposedSet.getSize(); i++)
			{
				for (int j = 0; j < transposedSet.getFields().size(); j++)
				{
					item = (DataItem)transposedSet.getDataItem(i);
					
					if (j == (transposedSet.getFields().size() - 1))
						pw.print(item.getValues()[j]);
					else
						pw.print(item.getValues()[j] + " ");
				}
				pw.println("");
			}
			
			out.close();
		} catch (IOException e)
		{
			System.out.println("Error creating SSA input");
			
			if (out != null)
				try{out.close();} catch (IOException e2){}
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 3;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' port for low-D representation vectors
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Output");
		ports.add(port);
		
		// Add 'out' port for convergence trigger
		
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
		port.setPortLabel("Convergence trigger");
		ports.add(port);
		
		// Add 'out' port for start trigger
		
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 2);
		port.setPortLabel("Started");
		ports.add(port);
		
		addPorts(ports);
	}
	
	// Declare the doSSA function as a native method because it is stored
	// in the C++ DLL SSA_DLL.dll
	// JNI is used to let the VM call the function at runtime
	
	private static native void doSSA(String fileName);
	
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
}

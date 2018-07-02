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
 * PCA
 *
 * Class represents an instance of a visual module for the Neural Principal Components Analysis algorithm
 *
 *  @author Greg Ross
 */
package alg;

import data.*;
import math.*;
import alg.pca.ValidateTriggerField;
import parent_gui.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.swing.event.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;
import java.lang.Runnable;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class PCA extends DefaultVisualModule implements DocumentListener, Runnable
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
	
	protected static int pcaOutputs	= 2;
	
    	// Coolection representing the original data.
	
    	protected DataItemCollection dataOriginal;
	
	// Array representing the continous numerical data in dataOriginal
	
	protected double[][] pcaInput;
	
	// Array for an individual input vector
	
	protected double[] input;
	
	// Array for the output of the pca
	
	protected double[] pcaOutput;
	
    	// Array for the weights between the inputs and outputs of the network
	
	protected double[][] pcaWeight;
	
	// The dimensionality of the PCA input vectors
	
	protected int pcaInputElementCount = 0;
	
	protected double learningRate = 0.001d;
	protected double initialLearningRate = learningRate;
	
	private int numEpochs;
	
	// Format object to format and parse numbers in the text field
	// for determining the initial network training rate
	
	private NumberFormat doubleFormat;
	
	protected ArrayList position;
	
	protected int numObjects = 0;
	
	// Controls
	
	private JButton run = new JButton("Run");
	private JButton train = new JButton("Train");
	private JTextField txtTrain = new JTextField();
	private JTextField txtEpoch = new JTextField();
	
	// Used to determine whether a valid training rate has been given,
	// i.e., not zero or of zero length
	
	private boolean bValidTrainRate = true;
	
	// Same for number of epochs
	
	private boolean bValidEpoch = true;
	
	// Documents used for special text-formatting and constraints
	
	private ValidateTriggerField doubleDoc;
	private ValidateTriggerField integerDoc;
	
	// The thread that controls the running of the PCA model
	
	private volatile Thread thread;
	private boolean bDeleted = false;
	private boolean bTrain = true;
	
	public PCA(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("PCA");
		setToolTipText("Neural Principal Component Analysis");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
		
		initTextBoxes();
	}
	
	private void initTextBoxes()
	{
		// Instantiate the formatting object for decimal (double)
		// numbers
		
		doubleFormat = DecimalFormat.getNumberInstance();
		((DecimalFormat)doubleFormat).applyPattern("#.#");
		doubleFormat.setMinimumFractionDigits(1);
		
		// Create the PlainDocument subclass
		
		doubleDoc = new ValidateTriggerField(doubleFormat);
		txtTrain.setDocument(doubleDoc);
		
		// Document listener for double value changes
		
		txtTrain.getDocument().addDocumentListener(this);
		
		txtTrain.setText(new Double(learningRate).toString());
		
		// Apply formatting etc for integer-only text entry for
		// specifying the number of Epochs used in training
		
		integerDoc = new ValidateTriggerField(null);
		txtEpoch.setDocument(integerDoc);
		txtEpoch.getDocument().addDocumentListener(this);
	}
	
	/**
	* Implementation of the DocumentListener interface
	*/
	
	public void insertUpdate(DocumentEvent e)
	{
		if (e.getDocument() == doubleDoc)
			setTrainingRate();
		else
			setEpochs();
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		if (e.getDocument() == doubleDoc)
			setTrainingRate();
		else
			setEpochs();
	}
	
	public void changedUpdate(DocumentEvent e){}
	
	/**
	* END OF implementation of the DocumentListener interface
	*/
	
	private void setTrainingRate()
	{
		if (txtTrain.getText().length() > 0)
		{
			double rate = Double.parseDouble(txtTrain.getText());
			if (rate  > 0d)
			{
				learningRate = rate;
				initialLearningRate = learningRate;
				bValidTrainRate = true;
			}
			else
			{
				learningRate = 0.001d;
				initialLearningRate = learningRate;
				bValidTrainRate = false;
			}
		}
		else
		{
			learningRate = 0.001d;
			initialLearningRate = learningRate;
			bValidTrainRate = false;
		}
	}
	
	private void setEpochs()
	{
		// Use the text field to determine over how many epochs
		// the PCA network should be trained
		
		if (txtEpoch.getText().length() > 0)
		{
			int epochs = Integer.parseInt(txtEpoch.getText());
			if (epochs  > 0)
			{
				numEpochs = epochs;
				bValidEpoch = true;
			}
			else
			{
				numEpochs = 100 * numObjects;
				bValidEpoch = false;
			}
		}
		else
		{
			numEpochs = 100 * numObjects;
			bValidEpoch = false;
		}
	}
	
	private void addControls()
	{	
		run.setEnabled(false);
		train.setEnabled(false);
		
		addButtonActionListeners();
		
		// Add a new JPanel to the centre of the module
		// This lays out components vertically
		
		JPanel jPane = new JPanel();
		jPane.setOpaque(false);
		jPane.setLayout(new BoxLayout(jPane, BoxLayout.Y_AXIS));
		add(jPane, "Center");
		jPane.setBorder(new EmptyBorder(5,5,5,5));
		
		// Add another panel to this
		// This is for the two buttons
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		jPane.add(buttonPanel);
		buttonPanel.add(run);
		buttonPanel.add(train);
		
		// Add the text field for entering the learning rate
		// First add a JPanel that will hold the text field and
		// always appear under the two buttons
		
		JPanel textPanel = new JPanel();
		
		// Use grid layout so that labels and text fields are laid out vertically
		
		textPanel.setLayout(new GridLayout(0, 1));
		textPanel.setOpaque(false);
		
		JLabel lblRate = new JLabel("Learning rate:");
		textPanel.add(lblRate);
		
		txtTrain.setEnabled(false);
		txtTrain.setBackground(Color.lightGray);
		textPanel.add(txtTrain);
		
		JLabel lblEpoch = new JLabel("Number of epochs:");
		textPanel.add(lblEpoch);
		
		txtEpoch.setEnabled(false);
		txtEpoch.setBackground(Color.lightGray);
		textPanel.add(txtEpoch);
		
		jPane.add(textPanel);
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	private void addButtonActionListeners()
	{
		// Add a button to start/stop the input to the network
		
		run.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				bTrain = false;
				thread = new Thread(PCA.this);
				thread.start();
            		}
        	});
		
		// Add a button to train the network
		
		train.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				bTrain = true;
				thread = new Thread(PCA.this);
				thread.start();
            		}
        	});
	}
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				// Data on the input port
				
				train.setEnabled(true);
				run.setEnabled(true);
				txtTrain.setEnabled(true);
				txtTrain.setBackground(Color.white);
				
				txtEpoch.setEnabled(true);
				txtEpoch.setBackground(Color.white);
				
				pcaInputElementCount = 0;
				
				setTrainingRate();
				
				dataOriginal = (DataItemCollection)(arg.get(0));
				numObjects = dataOriginal.getSize();
				
				// Set the default number of epochs
				
				numEpochs = 100 * numObjects;
				txtEpoch.setText((new Integer(numEpochs)).toString());
				
				Coordinate.setActiveDimensions(2);
				
				if (((DataItem)dataOriginal.getDataItem(0)).getTextValues() == null)
					createPCAInputs();
				else
					createPCAInputsText();
				
				initPCAOutputs();
				
				//trainNetwork();
				
				// Initialise the array that holds the 2D representation of the data
				
				position = new ArrayList();
				position.clear();
				for (int i = 0 ; i < dataOriginal.getSize() ; i++ ) 
				{
					position.add(new Coordinate());
				}
				
				//visualise();
				
				// Send 2D positions to the output
				
				ArrayList transferData;
				transferData = new ArrayList();
				transferData.add(dataOriginal);
				transferData.add(position);
				getOutPort(0).sendData(transferData);
			}
		}
		else
		{
			// Input module was deleted
			
			getOutPort(0).sendData(null);
			thread = null;
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 2;
		int numOutPorts = 1;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Data in");
		ports.add(port);
		
		// Add 'out' port
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Output");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* For each item in dataOriginal, create a numerically continuous
	* vector representation (CSV version_.
	*/
	private void createPCAInputs()
	{
		int numFieldsOriginal = dataOriginal.getNumFields();
		
		// Use only the numerical fields to create the array of PCA
		// inputs
		
		
		// Determine the number of numerical elements in each input
		// vector to be used in the PCA
		
		ArrayList numPositions = new ArrayList();
		
		for (int i = 0; i < numFieldsOriginal; i++)
		{
			if (((DataItem)dataOriginal.getDataItem(0)).getValues()[i] instanceof Date)
			{
				pcaInputElementCount++;
				numPositions.add(new Integer(i));
			}
			else if (((DataItem)dataOriginal.getDataItem(0)).getValues()[i] instanceof Integer)
			{
				pcaInputElementCount++;
				numPositions.add(new Integer(i));
			}
			else if (((DataItem)dataOriginal.getDataItem(0)).getValues()[i] instanceof Double)
			{
				pcaInputElementCount++;
				numPositions.add(new Integer(i));
			}
		}
		
		// Initialise the PCA input array so that its rows represent the
		// numerical values and its columns represent the data items
		
		pcaInput = new double[pcaInputElementCount][numObjects];
		
		// For each original data item create a vector for PCA input.
		
		double dTemp = 0.0d;
		
		for (int i = 0; i < numObjects; i++)
		{
			for (int j = 0; j < pcaInputElementCount; j++)
			{	
				int iTemp = ((Integer)numPositions.get(j)).intValue();
				Object dat = ((DataItem)dataOriginal.getDataItem(i)).getValues()[iTemp];
				
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
					double average
						= dataOriginal.average()[iTemp];
						
					double sigma =  dataOriginal.sigma()[iTemp];
					
					dTemp = (dTemp - average) / (2.0d * sigma);
					
					pcaInput[j][i] =  dTemp;
				}
			}	
		}
		
		// Initialise the weights
		
		initPCAWeights();
	}
	
	/**
	* Called when the data is text based (tf-idf)
	*/
	
	private void createPCAInputsText()
	{
		int numFieldsOriginal = dataOriginal.getNumFields();
		
		// Use only the numerical fields to create the array of 
		// inputs
		
		ArrayList numPositions = new ArrayList();
		pcaInputElementCount = dataOriginal.getFields().size();
		
		pcaInput = new double[pcaInputElementCount][numObjects];
		
		// For each original data item create a vector for input
		
		double dTemp = 0.0d;
		
		for (int i = 0; i < dataOriginal.getSize(); i++)
		{
			for (int j = 0; j < pcaInputElementCount; j++)
			{
				dTemp = ((DataItem)dataOriginal.getDataItem(i)).getTextValues()[j];
				
				if (dTemp < Double.MAX_VALUE)
				{
					// Normalise the data
					
					double average = dataOriginal.average()[j];
					
					double sigma = dataOriginal.sigma()[j];
					
					if (!Double.isNaN(sigma))
						dTemp = (dTemp - average) / (2.0d * sigma);
					
					pcaInput[j][i] =  dTemp;
				}
			}	
		}
		
		// Initialise the weights
		
		initPCAWeights();
	}
	
	private void initPCAOutputs()
	{
		pcaOutput = new double[pcaOutputs]; 
	}
	
	private void initPCAWeights()
	{
		pcaWeight = new double[pcaOutputs][pcaInputElementCount];
	
		// Make each initial weight a random number
	
		for (int i = 0; i < pcaInputElementCount; i++)
		{
			for (int j = 0; j < pcaOutputs; j++)
			{
				pcaWeight[j][i] = ((Math.random() - 0.5d)/1000.0d);
			}
		}
	}
	
	private void trainNetwork()
	{
		for (int i = 0; i < numEpochs; i++)
		{
			setInputs();
			feedForward();
			changeWeights();
			learningRate -= initialLearningRate/((double)numEpochs);
		}
	}
	
	/**
	* Introduce a data point to the network.
	*/
	private void setInputs()
	{
		int dataPt = (int)(numObjects * Math.random());
		input = new double[pcaInputElementCount];
		
	  	for (int i = 0; i < pcaInputElementCount; i++)
		{
			input[i] = pcaInput[i][dataPt];
		}
	}
	
	private void setInputs(int dataPt)
	{
		input = new double[pcaInputElementCount];
		
		for (int i = 0; i < pcaInputElementCount; i++)
		{
			input[i] = pcaInput[i][dataPt];
		}
	}
	
	/**
	*	Determine the network output according to the data point introduced
	* 	above.
	*/
	private void feedForward()
	{
		for (int out = 0; out < pcaOutputs; out++)
		{
			pcaOutput[out] = 0;
			for (int inDim = 0; inDim < pcaInputElementCount; inDim++)
			{
				if (input[inDim] < Double.MAX_VALUE)
					pcaOutput[out] += pcaWeight[out][inDim] * input[inDim];	
			}
		}    
	}
	
	/**
	*	After introducing a new training data point and determining the output
	* as above, modify the network weights using a version of Hebbian learning.
	*/
	private void changeWeights()
	{
		for (int out = 0; out <  pcaOutputs; out++)
		{
			for (int i = 0; i < pcaInputElementCount; i++)
			{
				if (input[i] < Double.MAX_VALUE)
					input[i] -= pcaWeight[out][i] * pcaOutput[out];
			}
			for (int j = 0; j < pcaInputElementCount; j++)
				if (input[j] < Double.MAX_VALUE)
				{
					pcaWeight[out][j] += learningRate * input[j]
						* pcaOutput[out];
				}
				
				//pcaWeight[out][j] += learningRate * pcaOutput[out]
				//* (input[j] - pcaOutput[out] * pcaWeight[out][j]);
		}
	}
	
	private void visualise()
	{
		for (int i = 0; i < numObjects; i++)
		{
			setInputs(i);
			feedForward();
		
        		((Coordinate)position.get(i)).setX(pcaOutput[0]);
			((Coordinate)position.get(i)).setY(pcaOutput[1]);
		}
	}
	
	/**
	* Method to restore action listener for the JButtons
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, 
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		
		addButtonActionListeners();
		
		txtTrain.getDocument().addDocumentListener(this);
		txtEpoch.getDocument().addDocumentListener(this);
	}
	
	private void RunOrTrain(boolean bTrain)
	{
		if (bTrain)
		{
			setFocus();
			setTrainingRate();
			initPCAWeights();
                	trainNetwork();
			
			// If an illegal value was set for the training rate,
			// reset it to the default.
			
			if (bValidTrainRate == false)
				txtTrain.setText("0.001");
			
			if (bValidEpoch == false)
				txtEpoch.setText((new Integer(100 * numObjects)).toString());
		}
		else
		{
			setFocus();
			
                	visualise();
			
			// Send 2D positions to the output
			
			ArrayList transferData;
			transferData = new ArrayList();
			transferData.add(dataOriginal);
			transferData.add(position);
			getOutPort(0).sendData(transferData);
		}
	}
	
	public void run()
	{
		Thread thisThread = Thread.currentThread();
		
		run.setEnabled(false);
		train.setEnabled(false);
		
		while (thread == thisThread)
		{ 
			RunOrTrain(bTrain);
			thread = null;
		}
		
		run.setEnabled(true);
		train.setEnabled(true);
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

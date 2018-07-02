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
 * Portions created by the Initial Developer are Copyright (C) 2000-2005
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
 * Algorithmic test bed
 * 
 * ShepardNMDS
 * 
 * Implementation of a Shepard's non-metric MDS algorithm. This module does not carry out the
 * final projection onto a lower number of dimensions. This is left to an external module
 * such as PCA.
 *
 * @author Greg Ross
 */
 
package alg; 
 
import parent_gui.*;
import data.*;
import math.*;
import alg.Shepard_NMDS.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Collections;
import java.util.Random;
import java.awt.Color;
import java.awt.event.*;
import java.lang.Runnable;
import java.lang.Thread;
import javax.swing.*;

public class ShepardNMDS extends DefaultVisualModule implements Runnable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 110;
	private int width = 234;
	
	// The array of data to be transferred to other linked modules
	
	private ArrayList transferData;
	
	// The thread that controls the running of the routine
	
	private volatile Thread thread;
	
	// Button to start MDS.
	
	private final JButton start = new JButton("Start");
	
	// Button to progress by 1 iteration.
	
	private final JButton cmdIter = new JButton("do 1");
	
	// Label for displaying the current iteration number
	
	private JLabel lblIterNumber;
	private JLabel lblMono;
	
	// The data set (N items).
	
	private DataItemCollection dataItems = null;
	
	// The N(N - 1)/2 proximities - upper right triangular matrix
	// ranked in ascending order.
	
	private ArrayList proximities = null;
	
	// Arraylist of keys so we can retrieve the ranks of proximities
	// after they've been sorted.
	
	private ArrayList keys = null;
	
	// The positions of the points in (N-1)-d space
	
	private double[][] simPos;
	
	// The distances in (N-1)-d space ranked in descending order
	
	private ArrayList distances = null;
	
	// The keys of the distances - so we can retrieve the correct ranks.
	
	private ArrayList distKeys = null;
	
	// The average (N-1)-d distance.
	
	private double averageDist = 0;
	
	// The average proximity.
	
	private double averageProx = 0;
	
	// Keep track of the number of iterations.
	
	private int numIterations = 0;
	
	// Parameter for convergence to monotonicity. The larger, the value, the faster the
	// convergence. However, if it's too large then the algorithm will degenerate into
	// non-conovergent oscillation.
	
	private double alpha = 0.035d;
	
	// Parameter govering the rate in which the (N-1)-d space is collapsed into a space
	// of smaller dimensionality. This value must be less the alpha otherwise monotonic
	// convergence will be at risk.
	
	private double beta = 0.002;
	
	// Control panel for interactively setting the above parmaters.
	
	private NMDSControlPanel controlPanel = null;
	private final JButton showControls = new JButton("Show controls");
	
	// Determine the type of convergence triggering
	
	public static final int ITERATIVE_CONVERGENCE = 0;
	public static final int MONOTONICITY_CONVERGENCE = 1;
	
	// Has a convergence trigger been set?
	
	private boolean bConvergenceTrig = false;
	
	// What type of trigger?
	
	private int convergenceTrigger = ITERATIVE_CONVERGENCE;
	
	// Should the trigger controls be enabled?
	
	private boolean triggerEnabled = false;
	
	// Scalar thresholds for convergence triggering
	
	private int iterationTrigValue;
	private double monoTriggerValue;
	
	private double sumProxDev = 0;
	
	public ShepardNMDS(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("NMDS");
		setToolTipText("Shepard's non-metric MDS");
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
		cmdIter.setEnabled(false);
		addButtonActionListeners();
		jPane.add(start);
		jPane.add(cmdIter);
		jPane.add(showControls);
		
		// Add a label showing the current iteration number
		
		lblIterNumber = new JLabel("Iterations: 0");
		lblMono = new JLabel("Mono:");
		jPane.add(lblIterNumber);
		jPane.add(lblMono);
		
		add(jPane, "Center");
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	private void addButtonActionListeners()
	{
		// Add a button to start/stop the routine to this
		// new JPanel
		
		start.addActionListener(new ActionListener()
		{
            		public void actionPerformed(ActionEvent e)
			{
				setFocus();
                		if (start.getText().equals("Start"))
				{
					ArrayList transferD = new ArrayList();
					transferD.add("start");
					getOutPort(2).sendData(transferD);
							
					// Start the model
					
					startRoutine();
				}
				else
					stopRoutine();
            		}
        	});
		
		// Add a button to hide/show spring model controls
		
		showControls.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				setFocus();
                		if (showControls.getText().equals("Show controls"))
					showEngineControls();
				else
					hideEngineControls();
					
				showControls.setPreferredSize(new java.awt.Dimension(113, 27));
            		}
        	});
		
		// Action listener for the button that progresses the routine by one iteration.
		
		cmdIter.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				setFocus();
                		doOne();
            		}
        	});
	}
	
	private void doOne()
	{
		try
		{
			sumProxDev = 0;
			doIteration();
			numIterations++;
			
			lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
			
			transferData = new ArrayList();
			transferData.add(createOutput());
			
			// Display the level of departure from monotonicity.
			
			lblMono.setText("Mono: " + (new Double(monoDep())).toString());
			
			getOutPort(0).sendData(transferData);
		}
		catch(java.lang.Exception e2)
		{
			// An exception may have arisen because the DataSource data has
			// been changed whilst in the last iteration of the current thread
			
			e2.toString();
		}
	}
	
	/** 
	* When the data set is null, make sure that the user can't
	* start the routine
	*/
	
	private void startControls()
	{
		start.setText("Start");
		
		if (dataItems == null)
		{
			start.setEnabled(false);
			cmdIter.setEnabled(false);
		}
		else
		{
			start.setEnabled(true);
			cmdIter.setEnabled(true);
		}
	}
	
	private void stopRoutine()
	{
		start.setText("Start");
		
		if (dataItems != null)
		{
			transferData = new ArrayList();
			transferData.add(createOutput());
			getOutPort(0).sendData(transferData);
		}
		
		thread = null;
		
		if (controlPanel != null)
			controlPanel.enableDisableTriggerControls(true);
	}
	
	private void startRoutine()
	{	
		start.setText("Stop");
		thread = new Thread(this);
		thread.start();
		
		if (controlPanel != null)
			controlPanel.enableDisableTriggerControls(false);
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
		
		// Add 'out' port for high-D representation vectors
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Output");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
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
				// High D data has been sent to the first in-port
				
				// Only accept it if the set is large enough
				
				if (((DataItemCollection)arg.get(0)).getSize() > 2)
				{
					// First, if the model is already
					// running, stop it
					
					stopRoutine();
					getOutPort(0).sendData(null);
					dataItems = (DataItemCollection)arg.get(0);
					startControls();
					
					if (dataItems != null)
					{
						initRoutine();
						numIterations = 0;
					}
					
					// If the control panel is showing then enable the controls
					
					if (controlPanel != null)
						setControlValues();
					
					// Allow the convergence trigger controls to be enabled
					
					triggerEnabled = true;
					
					// If the convergence trigger has not already been set
					// make the default value = number of data items
					
					if (iterationTrigValue == 0)
						iterationTrigValue = dataItems.getSize();
				}
				else
				{
					// Data set is too small
					
					nullifyReferences();
					stopRoutine();
					getOutPort(0).sendData(null);
					startControls();
					
					if (controlPanel != null)
						controlPanel.enableDisableControls(false);
					
					triggerEnabled = false;
				}
				
				numIterations = 0;
				lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
				lblMono.setText("Mono: 0");
			}
		}
		else
		{
			if (toPort.getKey().equals("i0"))
			{
				// Input module was deleted
				
				nullifyReferences();
				stopRoutine();
				getOutPort(0).sendData(null);
				numIterations = 0;
				lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
				lblMono.setText("Mono: 0");
				
				if (controlPanel != null)
					controlPanel.enableDisableControls(false);
				
				triggerEnabled = false;
			}
			startControls();
		}
	}
	
	/**
	* Implementation of the runnable interface
	*/
	
	public void run()
	{
		double monoValue = 0;
		ArrayList transferData;
		Thread thisThread = Thread.currentThread();
		while (thread == thisThread)
		{
			try
			{
				sumProxDev = 0;
				doIteration();
				numIterations++;
				
				lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
				
				// Display the level of departure from monotonicity.
				
				monoValue = monoDep();
				lblMono.setText("Mono: " + (new Double(monoValue)).toString());
				
				if ((numIterations % 10) == 0)
				{
					transferData = new ArrayList();
					transferData.add(createOutput());
					
					getOutPort(0).sendData(transferData);
				}
				
				if (bConvergenceTrig)
				{
					if (convergenceTrigger == ITERATIVE_CONVERGENCE)
					{
						if (numIterations >= iterationTrigValue)
						{
							stopRoutine();
							transferData = new ArrayList();
							transferData.add("startNextMod");
							getOutPort(1).sendData(transferData);
						}
					}
					else if (convergenceTrigger == MONOTONICITY_CONVERGENCE)
					{
						if (monoValue < monoTriggerValue)
						{
							stopRoutine();
							transferData = new ArrayList();
							transferData.add("startNextMod");
							getOutPort(1).sendData(transferData);
						}
					}
				}
				
				thread.yield();
			}
			catch(java.lang.Exception e2)
			{
				// An exception may have arisen because the DataSource data has
				// been changed whilst in the last iteration of the current thread
				
				e2.printStackTrace();
			}
		}
	}
	
	/**
	* When the high D input to the routine has been removed
	* nullify all of the references previously stored
	*/
	
	private void nullifyReferences()
	{
		dataItems = null;
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
	
	public void beforeSerialise()
	{
		// Don't try to serialise threads
		
		if (showControls.getText().equals("Hide controls"))
			hideEngineControls();
		
		if (start.getText().equals("Stop"))
			stopRoutine();
	}
	
	/**
	* Initialise the routine.
	*/
	
	private void initRoutine()
	{
		lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
		initSimplexPositions();
	}
	
	/**
	* Given the input data, find the N(N - 1)/2 proximities and place N points
	* on the N vertices of a (N - 1)-dimensional simplex.
	*/
	
	private void initSimplexPositions()
	{
		// First condition the proximities.
		
		calcProximities();
		
		// Now position points on simplex.
		
		int numDim = dataItems.getSize() - 1;
		simPos = new double[dataItems.getSize()][numDim];
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			for (int j = 1; j <= (numDim / 2); j++)
			{
				simPos[i][(2 * j) - 2] = Math.cos(2 * j * i * Math.PI / (double)dataItems.getSize()) / Math.sqrt(dataItems.getSize());
				simPos[i][(2 * j) - 1] = Math.sin(2 * j * i * Math.PI / (double)dataItems.getSize()) / Math.sqrt(dataItems.getSize());
			}
			
			// If N is even...
			
			if ((dataItems.getSize() % 2) == 0)
				simPos[i][numDim - 1] = Math.pow(-1, i) / Math.sqrt(2 * dataItems.getSize());
		}
	}
	
	/**
	* If the input data are a set of N scalars or vectors, calculate
	* the N(N - 1)/2 proximities.
	*/
	
	private void calcProximities()
	{
		int size = dataItems.getSize();
		proximities = new ArrayList(((size * size) - size) / 2);
		keys = new ArrayList(((size * size) - size) / 2);
		int key = 0;
		
		for (int i = 0; i < (size - 1); i++)
		{
			for (int j = i + 1; j < size; j++)
			{
				proximities.add(new Double(dataItems.getDesiredDist(i, j)));
				keys.add(new Integer(key));
				key++;
			}
		}
		
		// Standardise...
		
		standardiseProximities();
		
		// ...and sort in sort in ascending order.
		
		DistanceComparator distComp = new DistanceComparator(proximities, true);
		Collections.sort(keys, distComp);
	}
	
	/**
	* Standardise proximities so that the largest measure is unity and
	* the smallest measure is zero.
	*/
	
	private void standardiseProximities()
	{
		double low = Integer.MAX_VALUE;
		double high = Integer.MIN_VALUE;
		double val;
		int i;
		
		for (i = 0; i < proximities.size(); i++)
		{
			val = ((Double)proximities.get(i)).doubleValue();
			
			if (val > high)
				high = val;
			
			if (val < low)
				low = val;
		}
		
		high -= low;
		averageProx = 0;
		
		for (i = 0; i < proximities.size(); i++)
		{
			val = ((Double)proximities.get(i)).doubleValue();
			val -= low;
			val /= high;
			averageProx += val;
			proximities.set(i, new Double(val));
		}
		
		int size = dataItems.getSize();
		averageProx /= (double)(((size * size) - size) / 2);
	}
	
	/**
	* Calculate vectors for achieving monotonicity and collapsing the (N-1)-d Euclidean space.
	*/
	
	private void doIteration()
	{
		// Rank the distances in descending order.
		
		if (numIterations == 0)
			calcDistances();
		
		DistanceComparator distComp = new DistanceComparator(distances, true);
		Collections.sort(distKeys, distComp);
		
		// For each point get the sums of the displacement vectors.
		
		double distance, proximity;
		int size = dataItems.getSize();
		int index, a, i, j;
		double[] mono, dim;
		double[][] delta;
		delta = new double[size][size - 1];
		
		for (i = 0; i < size; i++)
		{
			for (j = 0; j < size; j++)
			{
				if (i != j)
				{
					index = getIndex(i, j, size);
					distance = ((Double)distances.get(index)).doubleValue();
					proximity = ((Double)proximities.get(index)).doubleValue();
					mono = monoVector(proximity, distance, distKeys.indexOf(new Integer(index)), i, j);
					dim = dimVector(proximity, distance, i, j);
					
					// Sum the vectors to form the displacement vector.
					
					for (a = 0; a < (size - 1); a++)
						delta[i][a] += (mono[a] + dim[a]);
				}
			}
		}
		
		// Add the displacement vector to the position vector for each object;
		
		for (i = 0; i < size; i++)
		{
			for (a = 0; a < (size - 1); a++)
			{
				simPos[i][a] += delta[i][a];
			}
		}
		
		// Apply the similarity transform to the (n-1)-d points so that the average inter-point
		// distance is unity and points are centred around the origin.
		
		int numDim = dataItems.getSize() - 1;
		
		double sum[] = new double[numDim];
		
		for (i = 0; i < size; i++)
		{
			for (j = 0; j < (size - 1); j++)
			{
				sum[j] += simPos[i][j];
			}
		}
		
		for (j = 0; j < (numDim); j++)
			sum[j] /= size; 			// Calculate centroid.
		
		for (i = 0; i < size; i++)
		{
			for (j = 0; j < (numDim); j++)
				simPos[i][j] -= sum[j]; 	// Centroid to origin.
		}
		
		calcDistances();
		distComp = new DistanceComparator(distances, true);
		Collections.sort(distKeys, distComp);
		
		for (i = 0; i < size; i++)
		{
			for (j = 0; j < (numDim); j++)
			{
				simPos[i][j] /= averageDist; 	// Average inter-point distance to unity.
			}
		}
	}
	
	private void calcDistances()
	{
		// First calc the N(N - 1)/2 distances.
		
		int size = dataItems.getSize();
		distances = new ArrayList(((size * size) - size) / 2);
		distKeys = new ArrayList(((size * size) - size) / 2);
		int key = 0;
		int numDim = dataItems.getSize() - 1;
		double sumDist = 0, dist;
		
		for (int i = 0; i < (size - 1); i++)
		{
			for (int j = (i + 1); j < size; j++)
			{
				dist = dist(i, j);
				sumDist += dist;
				distances.add(new Double(dist));
				distKeys.add(new Integer(key));
				key++;
			}
		}
		
		// Get the averge distance so that we can apply the "similarity"
		// transform to the distances at the end of each iteration.
		
		averageDist = sumDist / (double)(((size * size) - size) / 2);
	}
	
	/**
	* Return the Euclidean distance between the two (N-1)-d items given.
	*/
	
	private double dist(int a, int b)
	{
		double sum = 0;
		int numDim = dataItems.getSize() - 1;
		double diff;
		
		for (int i = 0; i < numDim; i++)
		{
			diff = simPos[a][i] - simPos[b][i];
			sum += (diff * diff);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	* Given an (N-1)-d point, calculate the vector for achieving
	* monotonicity.
	*/
	
	private double[] monoVector(double prox, double dist, int distRank, int i, int j)
	{
		int n = dataItems.getSize();
		double xi, xj;
		double sd;
		double[] result = new double[n - 1];
		sd = ((Double)(proximities.get(((Integer)keys.get(distRank)).intValue()))).doubleValue();
		
		for (int a = 0; a < (n - 1); a++)
		{
			xi = simPos[i][a];
			xj = simPos[j][a];
			result[a] = (alpha * (sd - prox) * (xj - xi)) / dist;
		}
		
		sumProxDev += ((sd - prox) * (sd - prox));
		
		return result;
	}
	
	/**
	* Given an (N-1)-d point, calculate the vector for achieving
	* a reduction in dimensionality.
	*/
	
	private double[] dimVector(double prox, double dist, int i, int j)
	{
		int n = dataItems.getSize();
		double xi, xj;
		double[] result = new double[n - 1];
		
		for (int a = 0; a < (n - 1); a++)
		{
			xi = simPos[i][a];
			xj = simPos[j][a];
			result[a] = (beta * (averageProx - prox) * (xj - xi)) / dist;
		}
		
		return result;
	}
	
	/**
	* Given row and col values for an upper-triangle matrix, return the
	* ordinal index of the corresponding 1-d array.
	*/
	
	private int getIndex(int row, int col, int numCols)
	{
		if (col > row)
		{
			return (int)(col + (((2 * numCols * row) - ((row * row) + row)) / 2d) - row - 1);
		}
		else
		{
			return (int)(row + (((2 * numCols * col) - ((col * col) + col)) / 2d) - col - 1);
		}
	}
	
	/**
	* Create the output DataItemCollection. This will be subject to dimension reduction from d = (n - 1).
	*/
	
	private DataItemCollection createOutput()
	{
		DataItemCollection outputData = new DataItemCollection();
		ArrayList fields = new ArrayList(dataItems.getSize() - 1);
		ArrayList types = new ArrayList(dataItems.getSize() - 1);
		
		for (int a = 0; a < (dataItems.getSize() - 1); a++)
		{
			types.add(new Integer(DataItemCollection.DOUBLE));
			fields.add((new Integer(a)).toString());
		}
		
		outputData.setFields(fields);
		outputData.setTypes(types);
		
		DataItem newItem;
		ArrayList values;
		double[] sumOfVals = new double[fields.size()];
		double[] sumOfSquares = new double[fields.size()];
		outputData.intiNormalArrays(sumOfVals, sumOfSquares);
		double val;
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			values = new ArrayList();
			
			for (int j = 0; j < (dataItems.getSize() - 1); j++)
			{
				val = simPos[i][j];
				values.add(new Double(val));
				sumOfVals[j] += val;
				sumOfSquares[j] += val * val; 
			}
			newItem = new DataItem(values.toArray(), i);
			outputData.addItem(newItem);
		}
		
		outputData.getDataItems().trimToSize();
		
		//normalize the data
		
		outputData.setNormalizeData(sumOfVals, sumOfSquares);
		
		// Determine whether the data are binary
		
		outputData.determineBinary();
		
		// Determine whether the data are genetic sequences
		
		outputData.determineSequenceData();
		
		// If the data are binary, determine the frequency of each variables
		
		outputData.setVariableFrequencies();
		
		return outputData;
	}
	
	/**
	* Calculate the over-all departure from monotonicity.
	*/
	
	private double monoDep()
	{
		int n = dataItems.getSize();
		/*double diff = 0, sd, prox;
		int index;
		int distRank, simKey;
		
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (i != j)
				{
					index = getIndex(i, j, n);
					distRank = distKeys.indexOf(new Integer(index));
					simKey = ((Integer)keys.get(distRank)).intValue();
					sd = ((Double)proximities.get(simKey)).doubleValue();
					prox = ((Double)proximities.get(index)).doubleValue();
					diff += ((sd - prox) * (sd - prox));
				}
			}
		}
		
		return (2 * diff) / (double)(n * (n-1));*/
		return (2 * sumProxDev) / (double)(n * (n-1));
	}
	
	/**
	* Accessor methods for setting the alpha and beta parameters.
	*/
	
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}
	
	public void setBeta(double beta)
	{
		this.beta = beta;
	}
	
	private void showEngineControls()
	{
		showControls.setText("Hide controls");
		
		// Stop drag handles from being drawn
		
		mdiForm.clearSelectedModule(getKey());
		
		// Show the controls
		
		controlPanel = new NMDSControlPanel(this);
		controlPanel.setOpaque(false);
		setControlValues();
		add(controlPanel, "South");
	}
	
	private void hideEngineControls()
	{
		showControls.setText("Show controls");
		
		// Stop drag handles from being drawn
		
		mdiForm.clearSelectedModule(getKey());
		
		// Hide controls
		
		remove(controlPanel);
		controlPanel.restore();
		controlPanel = null;
	}
	
	private void setControlValues()
	{
		if (dataItems != null)
		{
			controlPanel.setAlpha(alpha);
			controlPanel.setBeta(beta);
			controlPanel.setOpaque(false);
			
			// Make sure that the convergence trigger controls are enabled
			
			controlPanel.enableDisableTriggerControls(true);
		}
	}
	
	/**
	* Set the number of iterations required to be performed before
	* the spring model is considered to have converged
	*/
	
	public void setIterationTrigValue(int value)
	{
		iterationTrigValue = value;
	}
	
	public int getIterationTrigValue()
	{
		return 	iterationTrigValue;
	}
	
	/**
	* Set the lower threshold for differential
	* required to be performed before
	* the model is considered to have converged
	*/
	
	public void setMonoTrigValue(double value)
	{
		monoTriggerValue = value;
	}
	
	public double getMonoTrigValue()
	{
		return monoTriggerValue;
	}
	
	public void setConvergenceTrigger(int convergence)
	{
		convergenceTrigger = convergence;
	}
	
	public int getConvergenceTrigger()
	{
		return convergenceTrigger;
	}
	
	public boolean isTriggerEnabled()
	{
		return triggerEnabled;
	}
	
	public void setConvergenceTriggerEnabled(boolean bConvergenceTrig)
	{
		this.bConvergenceTrig = bConvergenceTrig;
	}
	
	public boolean isConvergenceTrigger()
	{
		return bConvergenceTrig;
	}
}

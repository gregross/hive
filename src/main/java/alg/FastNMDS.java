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
 * FastNMDS
 * 
 * Implementation of a Shepard's non-metric MDS algorithm based upon Chalmers' '96 spring model.
 * This module does not carry out the
 * final projection onto a lower number of dimensions. This is left to an external module
 * such as PCA or spring model.
 *
 * @author Greg Ross
 */
 
package alg; 
 
import parent_gui.*;
import data.*;
import math.*;
import alg.fast_NMDS.*;

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

public class FastNMDS extends DefaultVisualModule implements Runnable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 110;
	private int width = 267;
	
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
	private ArrayList distances = null;
	private ArrayList distKeys = null;
	private ArrayList keys = null;
	
	// The positions of the points in (N-1)-d space
	
	private double[][] simPos;
	
	// The average (N-1)-d distance.
	
	private double averageDist = 0;
	
	// The average proximity.
	
	private double averageProx = 0;
	
	// Keep track of the number of iterations.
	
	private int numIterations = 0;
	
	// Parameter for convergence to monotonicity. The larger, the value, the faster the
	// convergence. However, if it's too large then the algorithm will degenerate into
	// non-conovergent oscillation.
	
	private double alpha = 0.2d;
	
	// Parameter govering the rate in which the (N-1)-d space is collapsed into a space
	// of smaller dimensionality. This value must be less the alpha otherwise monotonic
	// convergence will be at risk.
	
	private double beta = 0.0;
	
	// Control panel for interactively setting the above parmaters.
	
	private NMDSControlPanel controlPanel = null;
	private final JButton showControls = new JButton("Show controls");
	
	// Sample, neighbour lists etc.
	// Each element is also an arrayList of indices
	
    	private ArrayList   neighbours, samples, cachedDistances, thisSampleDists, hts;
	private int          neighbourSize = 6;
	private int          sampleSize    = 3;
	
	// The number of output dimensions.
	
	private int numDim = 0;
	
	// The output data.
	
	private ArrayList positions;
	
	// Acumulate the sum of proximity deviations for an iteration.
	
	private double sumProxDev;
	
	// Keep track of local monotonicity.
	
	private double localMonotonicity = Double.MAX_VALUE;
	
	// Highest approximate layout distance from the previous iteration
	
	private double highestDist;
	
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
	
	public FastNMDS(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Fast NMDS");
		setToolTipText("Fast non-metric MDS");
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
		double monoValue = 0;
		
		try
		{
			// If the number of input data it larger than the size of the neighbour
			// and sample sets, then we can utilise the speed-up as derived from Chalmers'
			// spring model.
			
			if ((localMonotonicity > 0.001) && (numIterations < dataItems.getSize()))
				doIteration();
			else
			{
				sumProxDev = 0;
				doShepardIteration();
				
				// Display the leel of departure from monotonicity.
				
				monoValue = monoDep();
				lblMono.setText("Mono: " + (new Double(monoValue)).toString());
			}
				
			numIterations++;
			
			lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
			
			transferData = new ArrayList();
			transferData.add(dataItems);
			create2DOutput();
			transferData.add(positions);
			
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
		
		// Add 'in' ports.
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' port for.
		
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
					
					nullifyReferences();
					
					if (arg.get(0) != null)
						if (arg.get(0) instanceof DataItemCollection)
						{
							localMonotonicity = Double.MAX_VALUE;
							stopRoutine();
							getOutPort(0).sendData(null);
							dataItems = (DataItemCollection)arg.get(0);
							startControls();
							
							// Determine the number of output dimensions.
							
							numDim = 2;
							initRoutine();
							numIterations = 0;
							
							ArrayList transferData = new ArrayList();
							transferData.add(dataItems);
							create2DOutput();
							transferData.add(positions);
							getOutPort(0).sendData(transferData);
							
							// If the convergence trigger has not already been set
							// make the default value = number of data items
							
							if (iterationTrigValue == 0)
								iterationTrigValue = dataItems.getSize();
						}
					
					// If the control panel is showing then enable the controls
					
					if (controlPanel != null)
						setControlValues();
					
					// Allow the convergence trigger controls to be enabled
					
					triggerEnabled = true;
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
		ArrayList transferData;
		Thread thisThread = Thread.currentThread();
		double monoValue = 0;
		boolean bSecondStage = false;
		while (thread == thisThread)
		{
			try
			{
				// Use the pure neighbour and sample approach for attaining local monotonicity.
				// Once this drops nelow a threshold value, switch to stage two to converge to
				// global monotonicity.
				
				if ((localMonotonicity > 0.001) && (numIterations < dataItems.getSize()))
					doIteration();
				else
				{
					bSecondStage = true;
					sumProxDev = 0;
					doShepardIteration();
					
					// Display the leel of departure from monotonicity.
					
					monoValue = monoDep();
					lblMono.setText("Mono: " + (new Double(monoValue)).toString());
				}
				
				// Send output.
				
				if ((dataItems != null) && ((numIterations % 10) == 0))
				{
					transferData = new ArrayList();
					transferData.add(dataItems);
					create2DOutput();
					transferData.add(positions);
					getOutPort(0).sendData(transferData);
				}
				
				numIterations++;
				lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
				
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
						if ((monoValue < monoTriggerValue) && bSecondStage)
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
				
				//System.out.println("error");
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
		neighbours = null;
		samples = null;
		cachedDistances = null;
		thisSampleDists = null;
		hts = null;
		proximities = null;
		keys = null;
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
		int i;
		positions = new ArrayList(dataItems.getSize());
		
		for (i = 0 ; i < dataItems.getSize() ; i++)
		{
			positions.add(new Coordinate());
		}
		
		lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
		initSimplexPositions();
		
		// Init the neighbours array list to be a random sample
		
		neighbours = new ArrayList(neighbourSize);
		samples = new ArrayList(sampleSize);
		
		cachedDistances = new ArrayList(neighbourSize);
		thisSampleDists = new ArrayList(sampleSize);
		
		if (dataItems.getSize() <= (neighbourSize + sampleSize))
			return;
		
		// Init every element of neighbours and samples with a random list
		
		for (i = 0 ; i < dataItems.getSize() ; i++)
		{
			cachedDistances.add(new ArrayList(neighbourSize));
			HashSet exclude = new HashSet();
			exclude.add(new Integer(i));
			
			// Init each neighbours set to a random list
			
			ArrayList neighbs = Utils.createRandomSample(null,
							 exclude,
							 dataItems.getSize(),
							 neighbourSize);
							 
			// Initialise distances - save computation later
			// also set up hashtable
			
			int index, y;
			
			Hashtable ht = new Hashtable(neighbourSize);
			for (y = 0; y < neighbourSize; y++)
			{
				index = getIndex(i,  ((Integer)neighbs.get(y)).intValue(), dataItems.getSize());
				Double di = (Double)proximities.get(index);
				((ArrayList)cachedDistances.get(i)).add(di);
				ht.put((Integer)neighbs.get(y), new Integer(y));
			}
			
			// sort the arraylist into ascending order
			
			NeighbourComparator comp = new NeighbourComparator(dataItems, i, (ArrayList)cachedDistances.get(i), ht);
			Collections.sort(neighbs, comp);
			Collections.sort((ArrayList)cachedDistances.get(i));
			Collections.reverse(((ArrayList)cachedDistances.get(i)));
			
			neighbours.add(neighbs);
			exclude = new HashSet(neighbs);
			exclude.add(new Integer(i));
			
			// Insert an ArrayList of samples into each samples element
			
			samples.add(Utils.createRandomSample(null,
						 exclude,
						 dataItems.getSize(),
						 sampleSize));
		}
		
		for (int p = 0; p < sampleSize; p++) 
			thisSampleDists.add(new Double(0.0));
	}
	
	/**
	* Given the input data, find the N(N - 1)/2 proximities and place N points
	* on the N vertices of a (N - 1)-dimensional simplex.
	*/
	
	private void initSimplexPositions()
	{
		// First condition the proximities.
		
		calcProximities();
		
		// Now position points on simplex if the desired number of output dimensions in the first instance is
		// equal to (n-1), otherwise randomly place points in the d-D output space.
		
		simPos = new double[dataItems.getSize()][numDim];
		int i, j;
		
		if (numDim == (dataItems.getSize() - 1))
		{
			for (i = 0; i < dataItems.getSize(); i++)
			{
				for (j = 1; j <= (numDim / 2); j++)
				{
					simPos[i][(2 * j) - 2] = Math.cos(2 * j * i * Math.PI / (double)dataItems.getSize()) / Math.sqrt(dataItems.getSize());
					simPos[i][(2 * j) - 1] = Math.sin(2 * j * i * Math.PI / (double)dataItems.getSize()) / Math.sqrt(dataItems.getSize());
				}
				
				// If N is even...
				
				if ((dataItems.getSize() % 2) == 0)
					simPos[i][numDim - 1] = Math.pow(-1, i) / Math.sqrt(2 * dataItems.getSize());
			}
		}
		else
		{
			Random rand = new Random(System.currentTimeMillis());
			Coordinate c;
			
			for (i = 0; i < dataItems.getSize(); i++)
			{
				for (j = 0; j < numDim; j++)
					simPos[i][j] = rand.nextDouble();
				
				c = (Coordinate)positions.get(i);
				c.setX(simPos[i][0]);
				
				if (numDim > 1)
					c.setY(simPos[i][1]);
			}
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
		double prox;
		
		for (int i = 0; i < (size - 1); i++)
		{
			for (int j = i + 1; j < size; j++)
			{
				prox = dataItems.getDesiredDist(i, j);
				proximities.add(new Double(prox));
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
	* Calculate vectors for achieving local monotonicity.
	*/
	
	private void doIteration()
	{
		// For each point get the sums of the displacement vectors and use these to update
		// the positions in (n-1)-d Euclidean space.
		
		sumProxDev = 0d;
		double distance, proximity;
		int size = dataItems.getSize();
		int a, i, j, ji;
		double[] mono, dim;
		double[][] delta;
		delta = new double[size][numDim];
		ArrayList s = null;
		
		// Setup the union of neighbour and sample proximities and distances,
		// calculating the (n-1)-d distances on the fly.
		
		ArrayList distanceSubset;
		ArrayList distSubsetKeys;
		ArrayList proxSubset;
		ArrayList proxSubsetKeys;
		
		for (i = 0; i < size; i++)
		{
			randomizeSample(i);
			
			s = new ArrayList(neighbourSize + sampleSize);
			s.addAll((ArrayList)neighbours.get(i));
			s.addAll((ArrayList)samples.get(i));
			
			// Update item i according to just the neighbour and sample sets.
			// Get the distancecs and proximities and rank their collection indices.
			
			distanceSubset = new ArrayList(s.size());
			proxSubset = new ArrayList(s.size());
			distSubsetKeys = getDistSubset(s, distanceSubset, i);
			proxSubsetKeys = getProxSubset(s, proxSubset, i);
			
			for (j = 0; j < s.size(); j++)
			{
				ji = ((Integer)s.get(j)).intValue();
				distance = ((Double)distanceSubset.get(j)).doubleValue();
				proximity = ((Double)proxSubset.get(j)).doubleValue();
				mono = monoVector(proximity, distance, j, i, ji, proxSubset, proxSubsetKeys, distSubsetKeys);
				dim = dimVector(proximity, distance, i, ji);
				
				// Sum the vectors to form the displacement vector.
				
				for (a = 0; a < (numDim); a++)
					delta[i][a] += (mono[a] + dim[a]);
			}
			
			findNewNeighbours(i);
			
			// Update positions.
			
			for (a = 0; a < (numDim); a++)
				simPos[i][a] += delta[i][a];
		}
		
		// Display the level of departure from local monotonicity.
		
		localMonotonicity = ((2 * sumProxDev) / ((double)neighbourSize * size));
		lblMono.setText("Mono: " + (new Double(localMonotonicity)).toString());
	}
	
	/**
	* Calculate vectors for achieving global monotonicity.
	*/
	
	private void doShepardIteration()
	{
		// Calulate and rank the distances.
		
		calcDistances(); // O(N^2)
		DistanceComparator distComp = new DistanceComparator(distances, true);
		Collections.sort(distKeys, distComp); // O(N^2 log N^2).
		
		// For each point get the sums of the displacement vectors.
		
		double distance, proximity;
		int size = dataItems.getSize();
		int index, a, i, j;
		double[] mono, dim;
		double[][] delta;
		delta = new double[size][numDim];
		int ij;
		ArrayList s;
		int distRank;
		
		for (i = 0; i < size; i++)
		{
			randomizeSample(i);
			
			// Get the neighbour and sample sets for point i and update its
			// position according to these only.
			
			s = new ArrayList(neighbourSize + sampleSize);
			s.addAll((ArrayList)neighbours.get(i));
			s.addAll((ArrayList)samples.get(i));
			
			for (j = 0; j < s.size(); j++)
			{
				ij = ((Integer)s.get(j)).intValue();
				index = getIndex(i, ij, size);
				distance = ((Double)distances.get(index)).doubleValue();
				distRank = distKeys.indexOf(new Integer(index));
				proximity = ((Double)proximities.get(index)).doubleValue();
				mono = monoVector(proximity, distance, distRank, i, ij); // O(N).
				dim = dimVector(proximity, distance, i, ij);
				
				// Sum the vectors to form the displacement vector.
				
				for (a = 0; a < (numDim); a++)
					delta[i][a] += (mono[a] + dim[a]);
			}
			
			findNewNeighbours(i);
			
			// Update positions.
			
			for (a = 0; a < (numDim); a++)
				simPos[i][a] += delta[i][a];
		}
		
		// Apply the similarity transform to the (n-1)-d points so that the average inter-point
		// distance is unity and points are centred around the origin.
		
		double sum[] = new double[numDim];
		
		for (i = 0; i < size; i++)
		{
			for (j = 0; j < (numDim); j++)
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
	
	/*
	* Given the neighbour and sample sets of proximities, calculate their (n-1)-d distances.
	*/
	
	private ArrayList getDistSubset(ArrayList s, ArrayList distanceSubset, int item)
	{
		int dj, j;
		ArrayList distSubsetKeys = new ArrayList(distanceSubset.size());
		
		for (j = 0; j < s.size(); j++)
		{
			dj = ((Integer)s.get(j)).intValue();
			distanceSubset.add(new Double(dist(item, dj)));
			distSubsetKeys.add(new Integer(j));
		}
		
		// Rank the subset of distances.
		
		DistanceComparator distComp = new DistanceComparator(distanceSubset, true);
		Collections.sort(distSubsetKeys, distComp);
		return distSubsetKeys;
	}
	
	/*
	* Given the neighbour and sample sets of proximities, return their proximities.
	*/
	
	private ArrayList getProxSubset(ArrayList s, ArrayList proxSubset, int item)
	{
		int id, dj, j;
		ArrayList proxSubsetKeys = new ArrayList(proxSubset.size());
		
		for (j = 0; j < s.size(); j++)
		{
			dj = ((Integer)s.get(j)).intValue();
			id = getIndex(item, dj, dataItems.getSize());
			proxSubset.add(proximities.get(id));
			proxSubsetKeys.add(new Integer(j));
		}
		
		// Rank the subset of proximities.
		
		DistanceComparator distComp = new DistanceComparator(proxSubset, true);
		Collections.sort(proxSubsetKeys, distComp);
		return proxSubsetKeys;
	}
	
	/**
	* Return the Euclidean distance between the two (N-1)-d items given.
	*/
	
	private double dist(int a, int b)
	{
		double sum = 0;
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
	
	private double[] monoVector(double prox, double dist, int r, int i, int j, ArrayList proxSubset, ArrayList proxSubsetKeys, ArrayList distSubsetKeys)
	{
		int n = dataItems.getSize();
		double xi, xj;
		double sd;
		double[] result = new double[numDim];
		double d;
		int distRank = distSubsetKeys.indexOf(new Integer(r));
		int proxRank = proxSubsetKeys.indexOf(new Integer(r));
		sd = ((Double)(proxSubset.get(((Integer)proxSubsetKeys.get(distRank)).intValue()))).doubleValue();
		
		// The following summation is used for calculating local montonicity.
		
		if (r < neighbourSize) // Only calculate local monotincity over the neighbour sets (i.e. LOCAL).
			sumProxDev += ((sd - prox) * (sd - prox));
		
		// ...and this is the calculation for the displacement vector.
		
		for (int a = 0; a < numDim; a++)
		{
			xi = simPos[i][a];
			xj = simPos[j][a];
			result[a] = (alpha * (sd - prox) * (xj - xi)) / dist;
		}
		return result;
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
		double[] result = new double[numDim];
		sd = ((Double)(proximities.get(((Integer)keys.get(distRank)).intValue()))).doubleValue();
		
		for (int a = 0; a < (numDim); a++)
		{
			xi = simPos[i][a];
			xj = simPos[j][a];
			
			// The similarity (sd) at the corresponding rank of the distance is taken as the
			// real current similarity and prox is the desired similarity.
			
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
		double[] result = new double[numDim];
		
		for (int a = 0; a < numDim; a++)
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
		ArrayList fields = new ArrayList(numDim);
		ArrayList types = new ArrayList(numDim);
		
		for (int a = 0; a < (numDim); a++)
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
			
			for (int j = 0; j < (numDim); j++)
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
	* Create a 2D vector output.
	*/
	
	private void create2DOutput()
	{
		Coordinate c;
		int i;
		
		/*// Standardise the output to [0, 1].
		
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		
		for (i = 0; i < dataItems.getSize(); i++)
		{
			if (simPos[i][0] > maxX)
				maxX = simPos[i][0];
			
			if (simPos[i][0] < minX)
				minX = simPos[i][0];
			
			if (simPos[i][1] > maxY)
				maxY = simPos[i][1];
			
			if (simPos[i][1] < minY)
				minY = simPos[i][1];
		}
		
		maxX -= minX;
		maxY -= minY;
		
		for (i = 0; i < dataItems.getSize(); i++)
		{
			c = (Coordinate)positions.get(i);
			c.setX(((simPos[i][0] - minX) / maxX) - 0.5);
			c.setY(((simPos[i][1] - minY) / maxY) - 0.5);
		}*/
		
		for (i = 0; i < dataItems.getSize(); i++)
		{
			c = (Coordinate)positions.get(i);
			c.setX(simPos[i][0] - 0.5);
			c.setY(simPos[i][1] - 0.5);
		}
	}
	
	/**
	* Calculate the over-all departure from monotonicity.
	*/
	
	private double monoDep()
	{
		/*int n = dataItems.getSize();
		double diff = 0, sd, prox;
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
		
		return (2 * sumProxDev) / (double)(dataItems.getSize() * (sampleSize + neighbourSize));
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
		add(controlPanel, "South");
		setControlValues();
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
	* Creates a new arrayList of random numbers to be used by the samples 
	* ArrayList.  This list  will contain a sampleSize random numbers, 
	* corresponding to dataItem indices, such that none of the values are the 
	* same as ones already in the sample or already in the neighbours list 
	* and are between 0 and dataItems.getSize().  THe resulting list will be 
	* stored in samples[index].
	* 
	* @param index The index of the samples arrayList to store the result
	*/
	
	private void randomizeSample(int index)
	{
		// The neighbours list, which is not wanted in this sample
		
		HashSet exclude = new HashSet((ArrayList)neighbours.get(index));
		exclude.add(new Integer(index));
		
		ArrayList newSample = Utils.createRandomSample(null,
			exclude,
			dataItems.getSize(),
			sampleSize);
		
		int id;
		
		for (int y = 0; y < sampleSize; y++)
		{
			id = getIndex(index, ((Integer)newSample.get(y)).intValue(), dataItems.getSize());
			thisSampleDists.set(y, (Double)proximities.get(id));
		}
		
		samples.set(index, newSample);
	}
	
	/**
	* For the object at index point, check through its samples list to check if
	* any of those objects would make better neighbours than the ones
	* currently in the neighbours list.
	*
	* @param index The index of the element whose samples list should be
	* examined for better neighbours
	*/
	
	private void findNewNeighbours(int index)
	{
		ArrayList sample = (ArrayList)samples.get(index);
		ArrayList neighbs = (ArrayList)neighbours.get(index);
		Hashtable ht = new Hashtable();
		
		for (int i = 0 ; i < sampleSize ; i++)
		{
			// Get the sample Object index
			
			int sampObj = ((Integer)sample.get(i)).intValue();
			
			// Check to see if this value would be suitable as a new neighbour
			
			if (((Double)thisSampleDists.get(i)).doubleValue() < 
				((Double)((ArrayList)cachedDistances.get(index)).get(0)).doubleValue()) 
			{
				neighbs.set(0, new Integer(sampObj));
				((ArrayList)cachedDistances.get(index)).set(0, (Double)thisSampleDists.get(i));
				for (int p = 0; p < neighbourSize; p++)
				{
					ht.put((Integer)neighbs.get(p), new Integer(p));
				}
				
				// Sort the arraylist into ascending order
				
				NeighbourComparator comp = new NeighbourComparator(dataItems, index, (ArrayList)cachedDistances.get(index), ht);
				
				// Then sort the neighbour set and distance set
				
				Collections.sort(neighbs, comp);
				Collections.sort(((ArrayList)cachedDistances.get(index)));
				Collections.reverse(((ArrayList)cachedDistances.get(index)));
			}
		}
	}
	
	/**
	* Use FastMap heuristic to determine the approximate greatest distance between two
	* items in the layout.
	*/
	
	private double getHighestDist()
	{
		int i, j, a, b = 0;
		double high = 1;
		double dist;
		
		Random rand = new Random(System.currentTimeMillis());
		a = rand.nextInt(dataItems.getSize());
		
		for (int iter = 0; iter < 5; iter++)
		{
			high = Double.MIN_VALUE;
			
			for (i = 0; i < dataItems.getSize(); i++)
			{
				dist = dist(i, a);
				
				if (dist > high)
				{
					high = dist;
					b = i;
				}
			}
			
			a = b;
		}
		
		return high;
	}
	
	/**
	* Return the approximate rank of the distance between the two items given.
	*/
	
	private int getApproximateDistRank(int i, int j)
	{
		double dist = dist(i, j);
		int num = (dataItems.getSize() * (dataItems.getSize() - 1)) / 2;
		
		// Take the lower bound of layout distances as zero and the
		// highest as getHighestDist(). Place these on a linear scale
		// with N(N - 1)/2 gradiations and return the approximate rank
		// of dist(i, j) as its ordinal point on this scale.
		
		if (dist > highestDist)
			highestDist = dist;
		
		int result = (int)Math.floor(((dist / highestDist) * (num - 1)) + 0.5d);
		
		return result;
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

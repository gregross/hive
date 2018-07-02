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
 * KMeans
 *
 * Class represents an instance of a visual module for the k-means algorithm
 *
 *  @author Greg Ross
 */
 package alg; 
 
import data.*;
import math.*;
import alg.kmeans.ValidateTriggerField;

import parent_gui.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import javax.swing.Box.Filler;
import javax.swing.event.*;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.*;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.lang.Runnable;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class KMeans extends DefaultVisualModule implements Runnable, ActionListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 180;
	private int width = 210;
	
	// The input data that will be clustered
	
	private DataItemCollection dataItems;
	
	// The number of cluster centroids. This must be a number less than
	// or equal to dataItems.getSize()
	
	private int numCentroids;
	
	// Array to contain the current centroids
	
	private double centroids[][];
	
	// The nearest neigbours/cluster members for the centroids
	// The cluster members are stored in ArrayLists
	
	private ArrayList members[];
	
	// Determine the number of numerical dimensions in dataItems
	
	private int numNumericDims = 0;
	
	// The array containing the numerical data items upon which
	// K-means will be run
	
	private double[][] kData;
	
	// Determine which centroids have converged
	
	private boolean convergedCentroids[];
	
	// Items in dataItems that are closest to the calculated
	// centroids. This are surrogate centroids but will be
	// treated as the true centroids for output of the module
	
	private DataItemCollection dataCentroids;
	
	// Button to allow the user to run the K-means algorithm
	
	private JButton run = new JButton("Run");
	
	// Text field for determining the number of centroids
	
	private JTextField txtCentroids = new JTextField();
	
	// Text field for determining the number of iteration until convergence
	
	private JTextField txtIterations = new JTextField();
	
	// Radio button controls for determining the type of convergence
	
	private JRadioButton rbIterationTrigger;
	private JRadioButton rbCentroidTrigger;
	private ButtonGroup radioGroup;
	
	// Document used to constrain text field input to integers
	
	private ValidateTriggerField integerDoc;
	
	// Thread of execution
	
	private Thread thread;
	
	// Has the spring model converged one it's reached a certain
	// number of iterations?
	
	private boolean bIterConverge = false;
	
	// Determine which iteration we're at
	
	private int currentIteration = 0;
	
	// JPanel for convergence controls
	
	JPanel triggerPanel;
	
	// JLabel for displaying the current iteration number
	
	private JLabel iterCount;
	
	public KMeans(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("k-means");
		setToolTipText("K-means clustering");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				currentIteration = 0;
				
				dataItems = (DataItemCollection)(arg.get(0));
				
				run.setEnabled(true);
				
				txtCentroids.setEnabled(true);
				txtCentroids.setBackground(Color.white);
				txtCentroids.setDocument(new ValidTextFieldDoc(dataItems.getSize(), txtCentroids, run));
				
				// Determine whether to enable the iterations convergence text field
				
				if (rbCentroidTrigger.isSelected())
				{
					txtIterations.setEnabled(false);
					txtIterations.setBackground(Color.lightGray);
				}
				else
				{
					txtIterations.setEnabled(true);
					txtIterations.setBackground(Color.white);
				}
				
				setDefaultCentroids();
				setDefaultIterations();
			}
		}
		else
		{
			currentIteration = 0;
			
			// Make sure that the thread ends
			
			thread = null;
			
			// Disable the controls
			
			run.setEnabled(false);
			
			txtCentroids.setEnabled(false);
			txtCentroids.setBackground(Color.lightGray);
			
			txtIterations.setEnabled(false);
			txtIterations.setBackground(Color.lightGray);
			
			// Dereference data arrays
			
			dataItems = null;
			centroids = null;
			members = null;
			kData = null;
			
			// Let connected modules know that the data-flow has been broken
			
			getOutPort(0).sendData(null);
		}
	}
	
	/**
	* When data is initially sent to the module, set the default
	* number of centroids to calculate, equal to the sqr root of N
	*/
	
	private void setDefaultCentroids()
	{
		numCentroids = (new Double(Math.sqrt(dataItems.getSize()))).intValue();
		txtCentroids.setText((new Integer(numCentroids)).toString());
	}
	
	/**
	* When the number of K-means iterations determines when to stop
	* the algorithm (converge), this method is used to set the
	* default number of iterations. Typically, this is equal to N
	*/
	
	private void setDefaultIterations()
	{
		txtIterations.setText((new Integer(dataItems.getSize())).toString());
	}
	
	/**
	* Add swing components for controlling the k-means algorithm
	*/
	
	private void addControls()
	{
		// Add a new JPanel to the centre of the module
		// This lays out components vertically
		
		JPanel jPane = new JPanel();
		jPane.setOpaque(false);
		jPane.setLayout(new BoxLayout(jPane, BoxLayout.Y_AXIS));
		
		add(jPane, "Center");
		
		// Add the run button
		
		addRunButton(jPane);
		
		addIterationLabel(jPane);
		
		// Add the text entry field for determining the number
		// of K-means centroids
		
		addCentroidsField(jPane);
		
		// Add controls for determining when to stop the algorithm
		
		addConvergenceControls(jPane);
		
		Filler filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(1, 1));
		add(filler, "South");
	}
	
	private void addIterationLabel(JPanel panel)
	{
		JLabel lbl = new JLabel("Iteration:");
		iterCount = new JLabel("0");
		
		JPanel pane = new JPanel();
		pane.setOpaque(false);
		pane.add(lbl);
		pane.add(iterCount);
		panel.add(pane);
	}
	
	/**
	* Add a button to the interface to allow the user
	* to start the K-means algorithm
	*/
	
	private void addRunButton(JPanel panel)
	{
		run.setEnabled(false);
		
		JPanel pane = new JPanel();
		pane.setOpaque(false);
		pane.add(run);
		
		// Now add the button below the label
		
		panel.add(pane);
		
		addButtonActionListeners();
	}
	
	private void addButtonActionListeners()
	{
		// Add the actionlistener to the button
		
		run.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{ 	
				currentIteration = 0;
				
				thread = new Thread(KMeans.this);
				thread.start();
            		}
        	});
	}
	
	/**
	* Add the text field for determining the number of centroids to
	* to compute
	*/
	
	private void addCentroidsField(JPanel panel)
	{
		txtCentroids.setEnabled(false);
		txtCentroids.setBackground(Color.lightGray);
		txtCentroids.setPreferredSize(new Dimension(60, 18));
		txtCentroids.setMaximumSize(new Dimension(60, 18));
		txtCentroids.setMinimumSize(new Dimension(60, 18));
		
		// Add a label
		
		JLabel cenLabel = new JLabel("No. of centroids:");
		
		// New JPanel to hold the controls
		
		JPanel pane = new JPanel();
		pane.setOpaque(false);
		
		pane.add(cenLabel);
		
		// Add the text box
		
		pane.add(txtCentroids);
		
		// Add new JPanel to module
		
		panel.add(pane);
	}
	
	/** 
	* Internal class to deal with valid text-entry for
	* determining the number of centroids
	*/
	
	private class ValidTextFieldDoc extends PlainDocument
	{
		private int maxValue;
		private JTextField txtBox;
		private JButton apply;
		
		public ValidTextFieldDoc(int maxValue, JTextField txtBox, JButton apply)
		{
			// The maximum allowable value that the text box can hold
			
			this.maxValue = maxValue;
			
			// Reference to the text box per se
			
			this.txtBox = txtBox;
			
			// Reference to the apply button that is enabled only when
			// the text is valid
			
			this.apply = apply;
		}
		
		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
			{
				// Allow only numerals to be entered
				
				char[] source = str.toCharArray();
				char[] result = new char[source.length];
				int j = 0;
				
				for (int i = 0; i < result.length; i++) 
				{
					if (Character.isDigit(source[i]))
						result[j++] = source[i];
				}
				
				// don't allow the first number to be 0
				
				if ((offs == 0) && str.equals("0"))
					return;
				
				// Exit here if a non-numeric entry was attempted.
				// Even though the text was blocked, a parse exception would
				// still arise
				
				if ((txtBox.getText() + new String(result, 0, j)).equals(""))
				{
					apply.setEnabled(false);
					return;
				}
					
				// Don't allow the number to be larger than maxValue
					
				if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) <= maxValue)
				{
					super.insertString(offs, new String(result, 0, j), a);
					apply.setEnabled(true);
				}
			}
			
			protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng)
			{
				super.removeUpdate(chng);
				
				if ((txtBox.getText().length() < 2) || (chng.getLength() == txtBox.getText().length()))
					apply.setEnabled(false);
			}
	}
	
	/**
	* Implements the controls for determining when to stop the
	* K-means algorithm
	*/
	
	private void addConvergenceControls(JPanel panel)
	{	
		// Create a panel, with a titled border, that will hold
		// the convergence controls
		
		triggerPanel = new JPanel();
		triggerPanel.setLayout(new BoxLayout(triggerPanel, BoxLayout.X_AXIS));
		triggerPanel.setBorder(new TitledBorder("Convergence trigger:"));
		triggerPanel.setOpaque(false);
		
		// Create radio button for determining convergence by the number
		// of iterations
		
		rbIterationTrigger = new JRadioButton("Iteration");
		rbIterationTrigger.setActionCommand("rbIterationTrigger");
		rbIterationTrigger.addActionListener(this);
		rbIterationTrigger.setOpaque(false);
		
		// Radio button for centroid value based convergence
		
		rbCentroidTrigger = new JRadioButton("Centroid");
		rbCentroidTrigger.setActionCommand("rbCentroidTrigger");
		rbCentroidTrigger.addActionListener(this);
		rbCentroidTrigger.setOpaque(false);
		
		// The centroid convergence method is default
		
		rbCentroidTrigger.setSelected(true);
		txtIterations.setEnabled(false);
		txtIterations.setBackground(Color.lightGray);
		txtIterations.setMaximumSize(new Dimension(60, 18));
		txtIterations.setPreferredSize(new Dimension(60, 18));
		
		// create button group and add the radio buttons
		
		radioGroup = new ButtonGroup();
		radioGroup.add(rbIterationTrigger);
		radioGroup.add(rbCentroidTrigger);
		
		// Create the PlainDocument subclass. The null parameter means
		// that the validation here will be based upon integer values only,
		// i.e. for entering the number of centroids
		
		integerDoc = new ValidateTriggerField(null);
		txtIterations.setDocument(integerDoc);
		
		// Add the controls to the triggerPanel
		
		triggerPanel.add(rbCentroidTrigger);
		triggerPanel.add(rbIterationTrigger);
		triggerPanel.add(txtIterations);
		
		// Add the panel to the super panel
		
		panel.add(triggerPanel);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == rbIterationTrigger)
		{
			txtIterations.setEnabled(true);
			txtIterations.setBackground(Color.white);
			txtIterations.requestFocus();
			bIterConverge = true;
		}
		else
		{
			txtIterations.setEnabled(false);
			txtIterations.setBackground(Color.lightGray);
			bIterConverge = false;
		}
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
		
		kData = new double[numNumericDims][dataItems.getSize()];
		
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
						
					double sigma =  dataItems.sigma()[iTemp];
					
					dTemp = (dTemp - average) / (2.0d * sigma);
					
					kData[j][i] =  dTemp;
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
		
		// Determine the number of numerical elements in each input
		// vector to be used in the calculations
		
		ArrayList numPositions = new ArrayList();
		numNumericDims = dataItems.getFields().size();
		
		// Initialise the numerically continuous input array so that its rows represent the
		// numerical values and its columns represent the data items
		
		kData = new double[numNumericDims][dataItems.getSize()];
		
		// For each original data item create a vector for K-means input
		
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
					double sigma =  dataItems.sigma()[j];
					dTemp = (dTemp - average) / (2.0d * sigma);
					kData[j][i] =  dTemp;
				}
			}	
		}
	}
	
	/**
	* Calculate the Euclidean distance between two a data item and
	* a centroid
	*/
	
	private double getDist(int item, int centroid)
	{
		double diff = 0;
		double sumDiff = 0;
		
		for (int i = 0; i < numNumericDims; i++)
		{
			diff = (kData[i][item] - centroids[i][centroid]);
			diff = diff * diff;
			
			sumDiff += diff;
		}
		
		return Math.sqrt(sumDiff);
	}
	
	/**
	* Create the intial array of centroids by randomly selecting
	* numCentroids vectors from kData
	*/
	
	private void initCentroids()
	{
		centroids = new double[numNumericDims][numCentroids];
		
		// Might as well also initiate the cluster members array here
		// Also initiate the array for determining how many and which
		// centroids have converged
		
		members = new ArrayList[numCentroids];
		convergedCentroids = new boolean[numCentroids];
		
		for (int cent = 0; cent < numCentroids; cent++)
		{
			members[cent] = null;
			members[cent] = new ArrayList();
			convergedCentroids[cent] = false;
		}
		
		
		Random rnd = new Random(System.currentTimeMillis());
		Integer rand;
		
		// Temporary ArrayList so that we can keep track of what is and isn't
		// already been added to the centroids array
		
		ArrayList indices = new ArrayList(numCentroids);
		
		for (int i = 0; i < numCentroids; i++)
		{
			rand = new Integer((int)(rnd.nextDouble() * (double)dataItems.getSize()));
			
			while (indices.contains(rand) == true)
				rand = new Integer((int)(rnd.nextDouble() * (double)dataItems.getSize()));
			
			indices.add(rand);
			
			// Create the centroid
			
			for (int j = 0; j < numNumericDims; j++)
				centroids[j][i] = kData[j][rand.intValue()];
		}
		
		indices = null;
	}
	
	/**
	* For each centroid, determine its nearest neighbour which isn't
	* already associated with another centroid
	*/
	
	private void gatherClusters()
	{
		// The minimum distance found so far from a centroid to an item
		
		double minDist;
		
		// The centroid that is closest (minDist) to the item
		
		int closestCentroid = 0;
		
		for (int item = 0; item < dataItems.getSize(); item++)
		{
			minDist = Double.MAX_VALUE;
			double dist = 0;
			
			for (int cent = 0; cent < numCentroids; cent++)
			{
				dist = getDist(item, cent);
				if (dist < minDist)
				{
					minDist = dist;
					closestCentroid = cent;
				}
			}
			
			// Add the data items indice (w.r.t to kData) to the
			// centroid's entry in the members array
			
			members[closestCentroid].add(new Integer(item));
		}
	}
	
	/**
	* After gathering the clusters, calculate the mean of each cluster
	* and make this the centroid
	*/
	
	private void updateCentroids()
	{
		// Array for the sums of each dimension in one
		// cluster
		
		double sum[] = new double[numNumericDims];
		
		// ArraList for cluster members
		
		ArrayList clusterMembers;
		
		for (int cent = 0; cent < numCentroids; cent++)
		{
			clusterMembers = members[cent];
			
			for (int member = 0; member < clusterMembers.size(); member++)
			{
				for (int dim = 0; dim < numNumericDims; dim++)
				{
					sum[dim] += kData[dim][((Integer)clusterMembers.get(member)).intValue()];
				}
			}
			
			// Now average over each dimension
			
			for (int dim = 0; dim < numNumericDims; dim++)
			{
				sum[dim] = sum[dim] / (double)clusterMembers.size();
				
				// Has the centroid converged/
				
				if (!bIterConverge)
					convergedCentroids[cent] = centroidConverged(cent, sum);
				
				// Replace the centroid with the new average centroid
				
				centroids[dim][cent] = sum[dim];
			}
		}
	}
	
	/**
	* Determine whether the centroid has converged by checking to
	* see if its values over all dimensions are unchanged
	*/
	
	private boolean centroidConverged(int centroid, double newCent[])
	{
		boolean converged = true;
		double round1 = 0d;
		double round2 = 0d;
		
		for (int dim = 0; dim < numNumericDims; dim++)
		{
			// Uncomment the following two lines if we want to
			// round the numbers to 4 decimal places
			
			//round1 = Math.round(centroids[dim][centroid]*10000)/10000f;
			//round2 = Math.round(newCent[dim]*10000)/10000f;
			
			//...and comment these out
			
			round1 = centroids[dim][centroid];
			round2 = newCent[dim];
			
			if (round1 != round2)
			{
				converged = false;
				break;
			}
		}
		
		return converged;
	}
	
	/**
	* Given the convergedCentroids array and numEpochs, determine
	* whether the calculations should cease
	*/
	
	private boolean kMeansConverged()
	{
		boolean converged = true;
		
		if (bIterConverge)
		{
			// When the algorithm has run upto txtIterations.getText()
			// iterations, stop
			
			if (txtIterations.getText().equals(""))
				converged = true;
			else
			{
				int iter = Integer.parseInt(txtIterations.getText());
				
				if (currentIteration >= iter)
					converged = true;
				else
					converged = false;
			}
		}
		else
		{
			for (int i = 0; i < numCentroids; i++)
			{
				if (convergedCentroids[i] != true)
				{
					converged = false;
					break;
				}
			}
		}
		
		return converged;
	}
	
	/**
	* After K-means has converged, use this method to 
	* find the closest data element in dataItems to each centroid
	* and create a new DataItemCollection from these
	* This means that if there were qualitative variables in the data
	* then they will be reintroduced in the true centroids
	*/
	
	private void createTrueCentroids()
	{
		ArrayList cluster;
		double minDist;
		double dist;
		int closestMemeber = 0;
		
		int[] itemIndices = new int[numCentroids];
		
		for (int cent = 0; cent < numCentroids; cent++)
		{
			cluster = members[cent];
			
			minDist = Double.MAX_VALUE;
			
			for (int member = 0; member <  cluster.size(); member++)
			{
				dist = getDist(((Integer)(cluster.get(member))).intValue(), cent);
				
				if (dist < minDist)
				{
					minDist = dist;
					closestMemeber = ((Integer)(cluster.get(member))).intValue();
				}
			}
			
			// Store the true centroid
			
			if (cluster.size() > 0)
				itemIndices[cent] = closestMemeber;
			else
				itemIndices[cent] = -1;
		}
		
		dataCentroids = dataItems.createNewCollection(itemIndices);
	}
	
	/**
	* Run the K-means algorithm until it converges
	*/
	
	private void runKmeans()
	{
		while ((kMeansConverged() == false) && (thread != null))
		{
			// First reset the members (clusters) array
			
			for (int i = 0; i < numCentroids; i++)
			{
				members[i] = null;
				members[i] = new ArrayList();
			}
			
			// Form clusters
			
			gatherClusters();
			
			// Recreate the centroids based upon the newly formed clusters
			
			updateCentroids();
			
			currentIteration++;
			
			iterCount.setText((new Integer(currentIteration)).toString());
		}
	}
	
	/**
	* Thread in which the K-means algorithm is run
	*/
	
	public void run()
	{
		Thread thisThread = Thread.currentThread();
		while (thread == thisThread)
		{ 
			try
			{
				if (txtCentroids.getText() != null)
					if (!txtCentroids.getText().equals(""))
						numCentroids = Integer.parseInt(txtCentroids.getText());
				
				if (((DataItem)dataItems.getDataItem(0)).getTextValues() == null)
					createInputs();
				else
					createInputsText();
				
				initCentroids();
				
				runKmeans();
				
				createTrueCentroids();
				
				// Ensure that this is only run once
				
				thread = null;
				
				// Send the data to the output
				
				ArrayList transferData;
				transferData = new ArrayList();
				transferData.add(dataCentroids);
				
				// Send the centroids to output 0
				
				getOutPort(0).sendData(transferData);
				
				// Send all of the clusters to the output
				
				sendAllClusters();
				
				// Send notification to modules connected to out port 1
				// that the algorithm has finished
				
				// Send notification to output 2 that K-means has finished
				
				transferData = new ArrayList();
				transferData.add(new String("go"));
				getOutPort(1).sendData(transferData);
			}
			catch (NullPointerException e){}
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 2;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' port
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Data in");
		ports.add(port);
		
		// Add 'out' ports
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Centroids");
		ports.add(port);
		
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
		port.setPortLabel("Finished");
		ports.add(port);
		
		// Add 'out' port for all clusters
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 2);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("All clusters out");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* Need to override paintComponent because serialisation, for
	* some reason, resets the opacity of the radio buttons. Must
	* be another Java bug
	*/
	
	public void paintComponent(java.awt.Graphics g)
	{
		super.paintComponent(g);
		
		if (triggerPanel != null)
		{
			triggerPanel.setOpaque(false);
			rbCentroidTrigger.setOpaque(false);
			rbIterationTrigger.setOpaque(false);
		}
	}
	
	/**
	* After clustering has been performed, this methpd is called to
	* convert each cluster to a DataItemCollection and send them to the
	* output for all clusters
	*/
	
	public void sendAllClusters()
	{
		if (members.length != 0)
		{
			ArrayList output = new ArrayList(2);
			output.add(dataItems);
			
			ArrayList clusterSet = new ArrayList(members.length);
			
			ArrayList aTemp;
			int itemKey;
			DataItemCollection dTemp;
			int[] itemIndices;
			
			for (int i = 0; i < members.length; i++)
			{
				aTemp = (ArrayList)members[i];
				
				itemIndices = new int[aTemp.size()];
				
				for (int j = 0; j < aTemp.size(); j++)
				{
					itemKey = ((Integer)aTemp.get(j)).intValue();
					itemIndices[j] = itemKey;
				}
				
				dTemp = dataItems.createNewCollection(itemIndices);
				clusterSet.add(dTemp);
			}
			
			if (clusterSet.size() > 0)
			{
				output.add(clusterSet);
				getOutPort(2).sendData(output);
			}
			else
				getOutPort(2).sendData(null);
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

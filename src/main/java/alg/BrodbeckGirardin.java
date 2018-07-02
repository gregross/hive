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
 * BrodbeckGirardin
 *
 * Class represents an instance of a visual module for using Brodbeck
 * and Girardin's original interpolation routine.
 *
 *  @author Greg Ross
 */
package alg; 
 
import parent_gui.*;
import data.*;
import math.*;

import java.util.*;
import java.awt.Color;
import java.lang.Runnable;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BrodbeckGirardin extends DefaultVisualModule implements Runnable
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
	
	DataItemCollection dataItems;
	
	// The sample
	
	ArrayList origSubset;
	ArrayList subset;
	DataItemCollection sampleData;
	
	// 2D coordinates of the sample data
	
	ArrayList position;
	
	// The remainder
	
	ArrayList remainder;
	DataItemCollection remainderData;
	
	// Other variables for interpolation and binary search
	
	private ArrayList interpSubset;
	private int interpSubsetSize;
	private ArrayList interpSubsetNums;
	private ArrayList subsetToInterp;
	
	// Variables for force placement refinement of interpolation
	
	private int vectorIterations  = 5;
	private double springForce = 0.7;
	
	// The thread that controls the running of the interpoaltion model
	
	private volatile Thread thread;
	private boolean bDeleted = false;
	
	// The seize of the full data set (interpolation items + items to be interpolated)
	
	private int dataSetSize = 0;
	
	// Temporary store for the 2D positions of the sample data items
	
	private ArrayList samplePositions;
	
	// Store the IDs of data items that are in the sample but not in dataItems
	
	private ArrayList excludedSamples;
	
    private boolean parentsProvided;
    private ArrayList parentsList;
    private JLabel percentDone;

	public BrodbeckGirardin(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);

		setName("Old Interp.");
		setToolTipText("Brodbeck and Girardin's original interpolation");
		setLabelCaption(getName());

		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControl();
		parentsProvided=false;
	}
	

    public void addControl(){
	JPanel mainPanel = new JPanel();
	percentDone = new JLabel(" ");
	mainPanel.add(percentDone);
	mainPanel.setOpaque(false);
	add(mainPanel, "Center");

    }


	/**
	*  This is called when a connected module wants to notify this
	*  module of a change
	*/
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	    if (arg != null){	
		if (toPort.getKey().equals("i0"))
		{
		    nullifyReferences();
		    
		    percentDone.setText("0%");
		    
		    // Received trigger to start interpolation
		    
		    if (((String)arg.get(0)).equals("first"))
		    {
			ArrayList starting= new ArrayList();
			starting.add(new String("first"));
			getOutPort(1).sendData(starting);
		    }
		    
		    if (((String)arg.get(0)).equals("startNextMod"))
		    {
			queryInPorts();
			processInput();
			
			if ((dataItems != null) && (sampleData != null))
			{
			    ArrayList starting= new ArrayList();
			    starting.add(new String("start"));
			    
			    getOutPort(1).sendData(starting);
			    thread = new Thread(this);
			    thread.start();
			    bDeleted = false;
			}
		    }
		}
		else if (toPort.getKey().equals("i1")){
		    // Received the parent data; the points around which
		    // we will interpolate the new data
		    //		    System.out.println("Interp: in port 1: samplePos");
		    thread = null;
		}
		else if (toPort.getKey().equals("i2")){
		    // We have received the data that will be interpolated
		    // around the parent points
		    //		    System.out.println("Interp: in port 2: data-parents");
		    thread = null;
		}
		else if (toPort.getKey().equals("i4")){
		    int p = 5;
		}
	    }
	    else{
		// Input module or link was deleted or reset
			
		thread = null;
		
		sampleData =null;
		dataItems = null;
		
		getOutPort(0).sendData(null);
		bDeleted = true;
			
		parentsProvided = false;
		System.gc();
	    }
	}
	
	/**
	* When data arrives on one port check the other to see if there are
	* data there as well
	*/
	
	private void queryInPorts()
	{
		ArrayList connectedPorts;
		ModulePort port;
		
		// Get sample positions data
		
		connectedPorts = getInPort(1).getObservedPorts();
		if ((connectedPorts != null) && (connectedPorts.size() > 0)){
		    if (connectedPorts.get(0) != null)	{
			port =(ModulePort)connectedPorts.get(0);
			if (port.getData() != null){
			    sampleData = (DataItemCollection)port.getData().get(0);
			    samplePositions = (ArrayList)port.getData().get(1);
			}
		    }
		}
		
		// Get data to be interpolated onto the layout
		
		connectedPorts = getInPort(2).getObservedPorts();
		if (connectedPorts != null){
		    for (int i = 0; i < connectedPorts.size(); i++)
			if ((connectedPorts.get(0) != null) && (connectedPorts.size() > 0)){
			    port =(ModulePort)connectedPorts.get(0);
			    if (port.getData() != null)	{
				
				// Isolate the data set that is to be interpolated onto the layout
				// because this set is often concatenated
				
			    	int[] itemIndices = new int[((DataItemCollection)(port.getData().get(0))).getSize()];
				
				for (int j = 0; j < ((DataItemCollection)(port.getData().get(0))).getSize(); j++)
					itemIndices[j] = j;
				
				dataItems = ((DataItemCollection)(port.getData().get(0))).createNewCollection(itemIndices);
			    }
			}
		}
		connectedPorts = getInPort(3).getObservedPorts();
		if ((connectedPorts != null) && (connectedPorts.size() > 0)){
		    if (connectedPorts.get(0) != null){
			port =(ModulePort)connectedPorts.get(0);
			if (port.getData() != null){
			    parentsList = (ArrayList)port.getData().get(0);
			    parentsProvided=true;
			}
		    }
		}
	}
	
	/**
	* The sample (parent set) and input data might arrive in different
	* orders. This method resolves this problem
	*/
	
	private void processInput()
	{	
		// Get the sample data set
				
		if ((sampleData != null) && (dataItems != null))
		{
			dataSetSize = dataItems.getSize();
			
			origSubset = new ArrayList(sampleData.getIDs());
			
			subset = new ArrayList(sampleData.getIDs());
			interpSubsetSize = (int)Math.sqrt(origSubset.size());
			interpSubset =  new ArrayList(interpSubsetSize);
			
			// Create the set containing indices of dataItems that are to be
			// interpolated onto the the 2D space
			
			subsetToInterp = createSubsetToInterp();
			
			// Create the data set that contains the items to be interpolated and
			// the parent (interpolation) items
			
			dataItems = createFullFDataSet(dataItems, sampleData);
			
			// Make sure that data set size variable now reflects the concatenated
			// data set
			
			dataSetSize = dataItems.getSize();
			
			// Make sure that the order of the stored 2D position matches the order
			// of the dataItems set
			
			createPositionsArray(samplePositions);
		}
	}
	
	/**
	* Given the input data (dataItems) and the sample (SampleData) return an
	* array list that IDs of dataItems that are not also in SampleData
	*/
	
	private ArrayList createSubsetToInterp()
	{
		subsetToInterp = new ArrayList();
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			if (origSubset.contains(new Integer(dataItems.getDataItem(i).getID())) != true)
				subsetToInterp.add(new Integer(i));
		}
		
		return subsetToInterp;
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 5;
		int numOutPorts = 2;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		// 1) Port for triggering the start of interpolation
		
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 0);
		port.setPortLabel("Start");
		ports.add(port);
		
		// 2) Port for data points around which data set is interpolated
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
		port.setPortLabel("SamplePos");
		ports.add(port);
		
		// 3) The set of data items that are to be interpolated
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 2);
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		port.setPortLabel("Data in");
		ports.add(port);
	       

		// 4) Parent-finding method
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 3);
		port.setPortLabel("Parents");
		ports.add(port);

	// 5) Parameters for the module
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 4);
		port.setPortLabel("Set parameters");
		ports.add(port);
		// Add 'out' ports
		
		// The 2D representation of the data and interpolation so far
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Output");
		ports.add(port);
		
		// A trigger output to signal that interpolation has finished
		
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
		port.setPortLabel("Finished");
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* Method to interpolate all of the points not in the subset, does
	* this by finding the closest object in the subset and placing
	* it somewhere near it.  Does in a loop
	*
	* Optimised for speed
	*
	*/
	protected void loopInterpolateDataItems()
	{
		Coordinate.setActiveDimensions(2);
		int fullSize = dataSetSize;
		int numTimes = subsetToInterp.size();

		//percentDone=new JLabel();
		percentDone.setText("0%");
		if (parentsProvided)
		    System.out.println("parents supplied: "+parentsList.size());
		for (int q=0; q<numTimes; q++)
		{
		    int i = ((Integer)subsetToInterp.get(q)).intValue();
			
		    // Init the minimum distance to be dist to the first object
		    int minIndex;
			
		    if (!parentsProvided){ // use brute-force as default
			minIndex = ((Integer)origSubset.get(0)).intValue();
			
			double minDist = dataItems.getDesiredDist(i, minIndex);
			// Find a suitable position (parent-finding)
			
			for( int j = 0 ; j < origSubset.size(); j++ ){
			    int samp = ((Integer)origSubset.get(j)).intValue();
			    if (samp != i)
				if( dataItems.getDesiredDist(i, samp) < minDist ){
				    minDist = dataItems.getDesiredDist(i, samp);
				    minIndex = samp;
				}
			}
		    } else{
			minIndex = ((Integer)parentsList.get(q)).intValue();// q as an index, since list ordered 1..(n-rootN)
		    }
		    // Cache dists to sample for i
		    
		    double[] sampCache = new double[interpSubset.size()];
		    
		    for (int p=0; p<interpSubset.size(); p++)
			{
			    int ind = ((Integer)interpSubset.get(p)).intValue();
			    sampCache[p]= dataItems.getDesiredDist(i,ind);
			}
		    
	    		placeNearToNearestNeighbour(i, minIndex, interpSubset, sampCache); // not orig subset
			
	    		// Add this point to the subset
			
	    		subset.add(new Integer(i));
			//if (numTimes>20000&&q%5000==0)//provide some feedback for a long operation
			//  System.out.print(q+",");
			if (((q%(numTimes/10))==0) && q>0)
			    percentDone.setText(""+((q*100)/numTimes)+"%");
	     }
		Coordinate.setActiveDimensions(4);
		percentDone.setText("100%");
		// Stop the thread
		
		thread = null;
	}
	
    	/**
	* Returns the sum of the difference between the desired distance and 
	* the actual distance from object index, to all of the points held in 
	* the arrayList sample
	*
	* @param index  The index of the object which all dists are measured from
	* @param p      The new coordinate that is being evaluated for index
	* @param sample The arrayList of indices to measure dists to
	* @return The sum of the desired distances
	*/
	protected double sumDistToSample(int index, Coordinate p, ArrayList sample, double[] sampCache)
	{
		double total = 0.0;
		
		for( int i = 0 ; i < sample.size() ;  i++ ) 
		{
		    int samp = ((Integer)sample.get(i)).intValue();
		    if (samp !=index) 
			{
				Vect v = new Vect(p, (Coordinate)position.get(samp));
				double realD = v.getLength();
				double desD;
				
				if (sampCache==null)
					desD = dataItems.getDesiredDist(index, samp);
				else // cache;
					desD = sampCache[i];
					
				double dif = (realD - desD);
				
				if (dif < 0.0d )
					dif *= -1.0d;
				total += dif;	
			}
		}
		return total;
	}
	
    	/**
	* Calculates the sum of the forces from the object index to all of the 
	* points in the sample
	*
	* @param index
	* @param sample
	* @return The resultant force from the object to the sample
	*/
	protected Vect sumForcesToSample(int index, ArrayList sample, double[] sampCache) 
	{
	    Vect f = new Vect();
	    
	    for( int i = 0 ; i < sample.size() ; i++ ) 
		{
		    int samp = ((Integer)sample.get(i)).intValue();
		    
		    if (samp!=index){
			// Get the unit vector from index pt to sample pt
			
			Coordinate p1 = (Coordinate)position.get(index);
			Coordinate p2 = (Coordinate)position.get(samp);
			Vect v = new Vect(p1, p2);
			Vect unitVect = v.normalizeVector();
			
			// Scale it by spring force
			
			double realD = v.getLength();
			double desD;
			if (sampCache==null)
			    desD= dataItems.getDesiredDist(index, samp);
			else//cache
			    desD=sampCache[i];
			
			double dist = (realD - desD);
			
			double force = (springForce * dist);// * deltaTime;
			unitVect.scale(force);
			f.add(unitVect);
		    }
		}
		
	    // Scale this force, relative to how many samples it was summed to
	    
	    f.scale( 1.0 / ((double)sample.size()) );
		
	    return f;
	}
	
	/**
	* Same as the above but explicitly takes a coordinate for the revised point.
	*/
	
	protected Vect sumForcesToSample(int index, Coordinate p1, ArrayList sample, double[] sampCache) 
	{
	    Vect f = new Vect();
	    
	    for( int i = 0 ; i < sample.size() ; i++ ) 
		{
		    int samp = ((Integer)sample.get(i)).intValue();
		    
			// Get the unit vector from p1 to sample p2
			
			Coordinate p2 = (Coordinate)position.get(samp);
			Vect v = new Vect(p1, p2);
			Vect unitVect = v.normalizeVector();
			
			// Scale it by spring force
			
			double realD = v.getLength();
			double desD;
			if (sampCache==null)
			    desD= dataItems.getDesiredDist(index, samp);
			else//cache
			    desD=sampCache[i];
			
			double dist = (realD - desD);
			
			double force = (springForce * dist);// * deltaTime;
			unitVect.scale(force);
			f.add(unitVect);
		}
		
	    // Scale this force, relative to how many samples it was summed to
	    
	    f.scale( 1.0 / ((double)sample.size()) );
		
	    return f;
	}
	
	/**
	* Places the point close to its nearest neighbour.
	*
	* @param index Index of the item being placed.
	* @param parent Index of the (probable) nearest neighbour.
	* @param sample Sample on which to perform calculations 
	* @param sampCache cache of neighbour distances
	*
	*/
	private void placeNearToNearestNeighbour(int index, int parent, ArrayList sample, double[] sampCache)
	{
		Coordinate pos = (Coordinate)position.get(parent);
		
		double radius = dataItems.getDesiredDist(index, parent);
		
		double sumDist = Double.MAX_VALUE;
		
		double dist0 = 0.0;
		
		// Variables for use in binary search of circle quadrant
		
		int lowBound = 0;
		int highBound = 0;
		int i;
		
		// Sample a series of angles on the circumference of the
		// circle and choose the point that has the smallest
		// discrepancy between the low and high-d distances.
		
		Coordinate newCirclePt, bestPoint = null;
		double minDist = Double.MAX_VALUE;
		Random rnd = new Random(System.currentTimeMillis());
		
		for (i = 0; i < 10; i++)
		{
			double angle = rnd.nextDouble() * 360d;
			newCirclePt = cPoint(angle, radius, pos);
			dist0 = sumDistToSample(index, newCirclePt, sample, sampCache);
			
			if (dist0 < minDist)
				bestPoint = new Coordinate(newCirclePt);
		}
		
		((Coordinate)position.get(index)).set(bestPoint);
		
		// Use force calculations to add a vector to the so-far derived
		// position, to hopefully move it closer to the ideal position.
		
		for (i = 0 ; i < vectorIterations ; i++ )
		{
	    		// Get the current position of item index.
			
			Coordinate newVectPt = new Coordinate(bestPoint);
			
			// Get force vector from circle pt to sample of points
			
			Vect v = sumForcesToSample(index, newVectPt, sample, sampCache);
			
			minDist = Double.MAX_VALUE;
			
			for (int j = 0; j < 5; j++)
			{
				// Sample along direction of force vector.
				
				Vect newVect = new Vect(v);
				newVect.scale(rnd.nextDouble());
				
				// Add the new force vector to current position.
				
				newVectPt.add(newVect);
				
				// Keep track of the best position along the force vector
				
				dist0 = sumDistToSample(index, newVectPt, sample, sampCache);
				
				if (dist0 < minDist)
					bestPoint = newVectPt;
			}
		}
		
		// Make this new position the position of item index.
		
		((Coordinate)position.get(index)).set(bestPoint);
	}
	
	/**
	*
	* Returns a coordinate of a point on the circumference of a circle.
	*
	* @param angleIn The angle to the circle's centre.
	* @param radi The radius of the circle.
	* @param posIn the position of the centre of the circle.
	* 
	*/
	protected Coordinate cPoint(double angleIn, double radi, Coordinate posIn)
	{
		Coordinate pt = new Coordinate(
	   		posIn.getX() + Math.cos(Math.toRadians(angleIn))*radi, 
			posIn.getY() + Math.sin(Math.toRadians(angleIn))*radi);
			
		return pt;
	}

	protected int binSearch(double lB, double hB, Coordinate pos, double radi, int index, ArrayList sample, double[] sampCache){
		double lBound = lB;
		double hBound = hB;
		while ((int)lBound <= (int)hBound)
		{
			int mid = (int)Math.round((lBound + hBound)/2);

			if ((mid == (int)lBound) || (mid == (int)hBound))
			{
				// If the mid point in the circle quadrant is equal to 
				// either the upper or lower bound then there is no more
				// search space and either the upper or lower bound is the 
				// optimal position on the circle.

				if ((sumDistToSample(index, cPoint(lBound, radi, pos), sample, sampCache)) >= 
					(sumDistToSample(index, cPoint(hBound, radi, pos), sample, sampCache)))
					return (int)hBound;
				else
					return (int)lBound; 
			}
			else
			{	
				double distMidLeft = sumDistToSample(index, cPoint((double)(mid + 1), radi, pos), sample, sampCache);
				double distMidRight = sumDistToSample(index, cPoint((double)(mid - 1), radi, pos), sample, sampCache);
				double distMid = sumDistToSample(index, cPoint((double)mid, radi, pos), sample, sampCache);
				
	    			// Determine which half of the space contains the closest point to
				// the samples.
				
	    		if (distMid > distMidLeft)
				lBound = (double)(mid + 1);
			else if (distMid > distMidRight)
				hBound = (double)(mid - 1);
			else
				return mid;
			}
		}
		return -1;
	}
	
	public void run()
	{
		loopInterpolateDataItems();
		
		// Send the data and its 2D representation to the output
		
		ArrayList transferData = new ArrayList();
		transferData.add(dataItems);
		transferData.add(position);
		
		getOutPort(0).sendData(transferData);
		
		// Send notification to output 2 that interpolation has finished
		ArrayList converged= new ArrayList();
		converged.add(new String("startNextMod"));
		//			System.out.println("Interp: out port 1: startNextMod");
		getOutPort(1).sendData(converged);
		
		if (bDeleted)
		{
			getOutPort(0).sendData(null);;
		}
	}
	
	/** 
	* Given the data set and the sample, it is possible that the data-in is also a
	* subset, in which case we will assume that the concatenation of the two sets
	* will yield the full set
	*/
	
	private DataItemCollection createFullFDataSet(DataItemCollection dataIn, DataItemCollection sampleIn)
	{
		// We will assume that the input data are a subset if they do not contain
		// at least one of the sample elements
		
		// Store the IDs of sample items that are not in dataIn
		
		excludedSamples = new ArrayList();
		ArrayList dataIDs = dataIn.getIDs();
		
		// Recreate the origSubset so that it contains the indices relating the
		// interpolation parents to the concatenated data set
		
		origSubset = new ArrayList();
		int indice = dataSetSize;
		
		for (int i = 0; i < sampleIn.getSize(); i++)
		{
			if (dataIDs.contains(new Integer(sampleIn.getDataItem(i).getID())) != true)
			{
				excludedSamples.add(new Integer(i));
				
				origSubset.add(new Integer(indice));
				indice++;
			}
			else
			{
				origSubset.add(new Integer(dataIDs.indexOf(new Integer(sampleIn.getDataItem(i).getID()))));
			}
		}
		
		// Now that we've re-created the origSubset, we can now derive the
		// random indices for the interpSubset
		
		interpSubsetNums = Utils.createRandomSample(null, null,
				origSubset.size(), interpSubsetSize);
		
		for (int i=0; i<interpSubsetSize; i++) 
			interpSubset.add(origSubset.get(((Integer)interpSubsetNums.get(i)).intValue()));
		
		// If the data are a subset then concatenate with the sample set
		
		for (int i = 0; i < excludedSamples.size(); i++)
			dataIn.addItem(sampleIn.getDataItem(((Integer)excludedSamples.get(i)).intValue()));
		
		return dataIn;
	}
	

	
    /**
     * When the high D input to the spring model has been removed
     * nullify all of the references previously stored
     */
	
    private void nullifyReferences(){
	dataItems =null;
	origSubset =null;
	subset =null;
	sampleData =null;
	position =null;
	remainder =null;
	remainderData =null;
	interpSubset =null;
	interpSubsetNums =null;
	subsetToInterp =null;
	samplePositions =null;
	excludedSamples =null;
	parentsList =null;
    }

	private void createPositionsArray(ArrayList samplePos)
	{
		// Create the positions array for the whole data set, 
		// putting the correct positions for the samples in place
		
		// Get the 2D coordinates of the sample data set
		
		ArrayList samples = samplePos;
		
		position = new ArrayList(dataSetSize);
		
		// Add positions for the original inpout data (items to be
		// interpolated onto the 2D space)
		
		for (int i=0; i<dataSetSize; i++)
			position.add(new Coordinate());
		
		// Now append the items in the sample that were not already in the
		// input data
		
		for (int i = 0; i < origSubset.size(); i++)
		{	
			position.set(((Integer)(origSubset.get(i))).intValue(), samplePositions.get(i));
		}
	}
}

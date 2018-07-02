/**
 * Algorithmic test bed
 * 
 * SpringModel
 * 
 * Implementation of a Chalmers' 1996 spring model. This model should work in 
 * linear time per iteration.  It does this by using caching of neighbour sets 
 * with stochastic sampling
 *
 * @author Andrew Didsbury, Greg Ross, Alistair Morrison
 */
 
package alg; 

import parent_gui.*;
import data.*;
import math.*;
import alg.springModel.*;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Random;
import java.awt.Color;
import java.awt.event.*;
import java.lang.Runnable;
import java.lang.NullPointerException;
import java.lang.Thread;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import java.io.*;


public class SpringModel extends DefaultVisualModule implements Runnable{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
	
    // The parent MDI form
	
    private static Mdi mdiForm;
	
    // The parent drawing surface
	
    private DrawingCanvas drawPane;
	
    // The dimensions of the module
	
    private int height = 150;
    private int width = 284;
    
    // Locally held values (testing if it is faster)
    
    private final static int scatterUpdate=5;

    private ArrayList            position;
    private ArrayList            velocity;
    private ArrayList            force;
    protected DataItemCollection   dataItems;
    private double               unrelatedDist;
	
    // Set these to default values
    
    private double        layoutBounds        = 1.0;
    private double        rangeLo             = -0.5;
    private double        rangeHi             = 0.5;
    private double        dampingFactor       = 0.3;
    private double        springForce         = 0.7;
    private double        gravityForce        = 0.7;
    private double        gravityDampingForce = 0.2;
    private double        timeForce           = 0.7;
    private double        timeDampingForce    = 0.2;
    private double        freeness            = 0.85;
    private double        deltaTime           = 0.3;

    // Compensates the size of the data size, the accumulated forces for 1000
    // items will be much larger than for 100 items
	 
    private double               dataSizeFactor;
    
    //    private long                 startTime;
    // private long                 runningTime;
    
    // Each element is also an arrayList of indices
    
    private ArrayList   neighbours, samples, thisSampleDists, hts;
    private ArrayList distances; // distances to each obj's neighbour set elements.  ie distances[i] = arraylist of dists to objects in i's neighbour set
    public int          neighbourSize = 6;
    public int          sampleSize    = 3;
	
    // The array of data to be transferred to other linked modules
	
    private ArrayList transferData;
	
    // The thread that controls the running of the spring model
	
    private volatile Thread thread;
	
    // Determine whether the high-D input has been removed
	
    private boolean bHigh_D_InputRemoved = false;
	
    private final JButton start = new JButton("Start");
    private final JButton showControls = new JButton("Show controls");
    private SpringModelControlPanel controlPanel;
	
    // Label for displaying the current iteration number
	
    protected JLabel lblIterNumber;
	
    // Determine the type of convergence triggering
	
    public static final int ITERATIVE_CONVERGENCE = 0;
    public static final int VELOCITY_CONVERGENCE = 1;
    
    // Has a convergence trigger been set?
	
    private boolean bConvergenceTrig = false;
	
    // What type of trigger?
	
    private int convergenceTrigger = ITERATIVE_CONVERGENCE;
	
    // Should the trigger controls be enabled?
    
    private boolean triggerEnabled = false;
    
    // Scalar thresholds for convergence triggering
	
    private int iterationTrigValue;
    private double velocityTriggerValue;
    
    // Number of spring model iterations performed so far
	
    protected int numIterations;
    private ArrayList neighbSample;
    private boolean hasRun=false;  // has the model run before?
    
    private boolean running = false;  // to prevent sending converged trigger, then further down run() method sending data


    // divide and re-align
    protected int                 numAnchors=3;
    protected int                 numSets=0;
    private JCheckBox splitUpBox;
    private JCheckBox initPos;
    private JCheckBox preCalc;
    private boolean splitUp;
    private ArrayList anchors;
    private HashSet anchorContents;
    private ArrayList sets;
    private ArrayList setContents;
    private ArrayList whichSet; // in same order as dataItems, an int to say which set this obj is in.  i.e if pos i of whichSet==2, means that object dataItems.get(i) is in set 2
    //    private ArrayList eachSetPositions;
    private ArrayList anchorPositions;
    private ArrayList currentSet;
    protected int currentLayout=0;
    private JTextField numSetsBox;
    private boolean useParams=false;

    private int posCounter=0;

    protected int dataSize;
    protected ArrayList fullDists;

    protected boolean bombing=false;
    protected Coordinate bombSite;
    private ArrayList bombEnergy;
    protected double blastEnergy;
//     protected double previousMaxDist=0.0;
//     protected double currentMaxDist=0.0;

    private boolean useDistInput;

    
    public SpringModel(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);
	setName("Spring Model");
	setLabelCaption(getName());
	setToolTipText("Spring Model");

	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.ALGORITHM_MODE);
	setBackground(Color.orange);
	setDimension(width, height);
	addControls();
	
    }
	
    /**
     * Set the number of iterations required to be performed before
     * the spring model is considered to have converged
     */
    
    public void setIterationTrigValue(int value){
	iterationTrigValue = value;
    }
	
    public int getIterationTrigValue(){
	return 	iterationTrigValue;
    }
	
    /**
     * Set the lower threshold for veolocity differential
     * required to be performed before
     * the spring model is considered to have converged
     */
	
    public void setVelocityTrigValue(double value){
	velocityTriggerValue = value;
    }
	
    public double getVelocityTrigValue(){
	return velocityTriggerValue;
    }
	
    public void setConvergenceTrigger(int convergence){
	convergenceTrigger = convergence;
    }
	
    public int getConvergenceTrigger(){
	return convergenceTrigger;
    }
	
    public boolean isTriggerEnabled(){
	return triggerEnabled;	
    }
	
    public void setConvergenceTriggerEnabled(boolean bConvergenceTrig){
	this.bConvergenceTrig = bConvergenceTrig;	
    }
	
    public boolean isConvergenceTrigger(){
	return bConvergenceTrig;	
    }
	
    private void addControls(){
	// Add a new JPanel to the centre of the module
		
	JPanel jPane = new JPanel();
	JPanel jPane2 = new JPanel();
	JPanel jPane3 = new JPanel();
	JPanel jPane4 = new JPanel();
	jPane.setOpaque(false);
	jPane2.setOpaque(false);
	jPane3.setOpaque(false);
	jPane4.setOpaque(false);
	start.setEnabled(false);
	
	splitUpBox = new JCheckBox();
	splitUpBox.setOpaque(false);
	initPos = new JCheckBox();
	initPos.setOpaque(false);
	initPos.setEnabled(false);
	numSetsBox = new JTextField("2");
	numSetsBox.setColumns(3);
	numSetsBox.setOpaque(false);
	numSetsBox.setEnabled(false);
	preCalc = new JCheckBox();
	preCalc.setOpaque(false);
	preCalc.setEnabled(true);


	//numSets = Integer.parseInt(numSetsBox.getText());

	addButtonActionListeners();
		
	// Add a label showing the current iteration number
		
	lblIterNumber = new JLabel("Iterations: 0");
			
	jPane.add(start);
	jPane.add(showControls);
	jPane.add(lblIterNumber);
	jPane4.add(new JLabel("Split up?"));
	jPane4.add(splitUpBox);
	jPane4.add(new JLabel("Number of sets: "));
	jPane4.add(numSetsBox);
	jPane4.add(new JLabel("Init pos?"));
	jPane4.add(initPos);
	jPane2.add(new JLabel("Pre-calculate distances"));
	jPane2.add(preCalc);
	jPane3.add(jPane); jPane3.add(jPane4); jPane3.add(jPane2);
	add(jPane3, "Center");
		
	// Make the controls visible depending upon the context
	// of the VisualModule
	
	setInterfaceVisibility();
    }
	
    private void addButtonActionListeners(){
	// Add a button to start/stop the spring model to this
	// new JPanel
	
	start.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setFocus();
		    if (start.getText().equals("Start")){
			ArrayList starting= new ArrayList();
			starting.add(new String("start"));
			getOutPort(1).sendData(starting);
			startSpringModel();
			

			//			System.out.println("Spring: OutPort 1: starting");
		    }
		    else
			stopSpringModel();
		}
	    });
		
	// Add a button to hide/show spring model controls
		
	showControls.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setFocus();
		    if (showControls.getText().equals("Show controls"))
			showEngineControls();
		    else
			hideEngineControls();
		    
		    showControls.setPreferredSize(new java.awt.Dimension(113, 27));
		}
	    });
	splitUpBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (splitUpBox.isSelected()){
			numSetsBox.setEnabled(true);
			initPos.setEnabled(true);
		    }
		    else{
			numSetsBox.setEnabled(false);
			initPos.setEnabled(false);
		    }
		}
	    });
    }
    
    private void showEngineControls(){
	showControls.setText("Hide controls");
		
	// Stop drag handles from being drawn
		
	mdiForm.clearSelectedModule(getKey());
		
	// Show the controls
		
	controlPanel = new SpringModelControlPanel(this);
	controlPanel.setOpaque(false);
	setControlValues();
	add(controlPanel, "South");
    }
	
    private void setControlValues(){
	if (dataItems != null){
	    controlPanel.setFreeness(freeness);
	    controlPanel.setSpring(springForce);
	    controlPanel.setDamping(dampingFactor);
	    controlPanel.setOpaque(false);
			
	    // Make sure that the convergence trigger controls are enabled
	    
	    controlPanel.enableDisableTriggerControls(true);
	}
    }
	
    public void setFreeness(double freeness){
	this.freeness = freeness;	
    }
	
    public void setSpringForce(double springForce){
	this.springForce = springForce;	
    }
	
    public void setDampingFactor(double dampingFactor){
	this.dampingFactor = dampingFactor;	
    }
	
    private void hideEngineControls(){
	showControls.setText("Show controls");
		
	// Stop drag handles from being drawn
	
	mdiForm.clearSelectedModule(getKey());
	
	// Hide controls
	
	remove(controlPanel);
	controlPanel.restore();
	controlPanel = null;
    }
	
    /** 
     * When the data set is null, make sure that the user can't
     * start the spring model
     */
    private void startControls(){
	start.setText("Start");	
	
	if (dataItems == null)
	    start.setEnabled(false);
	else
	    start.setEnabled(true);
    }
	

    private void commonInit() {
	// This method is called after the module has received data on its data-in port
	// Set the number of dimensions that are being used
	







	//check here for params input and set useParams as appropriate








	Coordinate.setActiveDimensions(4);
	
	numIterations = 0;
	lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
		
	init();
	randomisePositions();
	    
	unrelatedDist = dataItems.getUnrelatedDist();
	dataSizeFactor = 1.0 / (double)(dataItems.getSize() - 1);
	
	thisSampleDists = new ArrayList();
	for (int p=0; p<sampleSize; p++) 
	    thisSampleDists.add(new Double(0.0));
	
	dataSizeFactor = 1.0 / (double)(neighbourSize + sampleSize);

    }

    private void initSpringModel(){  
	    
	
	commonInit();
	// init the neighbours array list to be a random sample
       
	neighbours = new ArrayList();
	samples = new ArrayList();
	distances = new ArrayList();
	hts = new ArrayList();
	
	// init every element of neighbours and samples with a random list
	
	for ( int i = 0 ; i < dataItems.getSize() ; i++ ) {
	    distances.add(new ArrayList(neighbourSize));
	    HashSet exclude = new HashSet();
	    exclude.add(new Integer(i));
		
	    // Init each neighbours set to a random list
		
	    ArrayList neighbs = Utils.createRandomSample(null, exclude,dataItems.getSize(), neighbourSize);
	    
	    // Initialise distances - save computation later
	    // also set up hashtable 
			
	    Hashtable ht = new Hashtable(neighbourSize);
	    for (int y=0; y< neighbourSize; y++){
		Double di = new Double(dataItems.getDesiredDist(i, ((Integer)neighbs.get(y)).intValue()));
		((ArrayList)distances.get(i)).add(di);
		ht.put((Integer)neighbs.get(y), new Integer(y));
	    }
		
	    hts.add(ht);
		
	    // sort the arraylist into ascending order
			
	    NeighbourComparator comp = new NeighbourComparator(dataItems, i, (ArrayList)distances.get(i), ht);
	    Collections.sort(neighbs, comp);
	    Collections.sort((ArrayList)distances.get(i));
	    Collections.reverse(((ArrayList)distances.get(i)));
	    
	    neighbours.add(neighbs);
	    exclude = new HashSet(neighbs);
	    exclude.add(new Integer(i));
	    
	    // Insert an ArrayList of samples into each samples element
	    
	    samples.add(Utils.createRandomSample(null,
						 exclude,
						 dataItems.getSize(),
						 sampleSize));
	}

//  	// morrisaj : 26.6.03
//  	// take sample of data and look at neighb comparisons for this 
//  	// sample only
//  	neighbSample = Utils.createRandomSample(null, null, dataItems.getSize(), 3*(int)Math.sqrt(dataItems.getSize()));
//  	Collections.sort(neighbSample);


	ArrayList anchSend = new ArrayList();
	anchSend.add(new String("NoAnchors"));
	//	System.out.println("Spring: OutPort 0: anchs");
	getOutPort(0).sendData(anchSend);
	
	// Send positions to the low-D out-port
	transferData = new ArrayList();
	transferData.add(dataItems);
	transferData.add(position);

	//	System.out.println("Spring: OutPort 0: posns");
	getOutPort(0).sendData(transferData);
    }
	

    private void stopSpringModel(){
	start.setText("Start");
	splitUpBox.setEnabled(true);
	initPos.setEnabled(splitUpBox.isSelected());
	preCalc.setEnabled(true);
	thread = null;
	running=false;
	// Only allow the user to modify the convergence triggering values while
	// the spring model is not iterating
	
	if (controlPanel != null)
	    controlPanel.enableDisableTriggerControls(true);
    }
	
    private void startSpringModel(){	
	start.setText("Stop");
	splitUpBox.setEnabled(false);
	initPos.setEnabled(false);
	preCalc.setEnabled(false);

	//	firstTwenty();
	if (preCalc.isSelected()){// pre-calculate all distances
	    dataSize = dataItems.getSize();
	    fullDists = new ArrayList(); 
	    ArrayList thisD;
	    for (int i = 0; i< dataSize; i++){
		thisD = new ArrayList();
		for (int j=0; j<=i; j++){
		    if (i==j)
			thisD.add(new Double(0.0));
		    else
			thisD.add(new Double(dataItems.getDesiredDist(i, j)));
		}
		fullDists.add(thisD);
	    }
	}
	hasRun=true;
	running = true;
	    
	thread = new Thread(this);
	thread.start();
	
	// Only allow the user to modify the convergence triggering values while
	// the spring model is not iterating
	
	if (controlPanel != null)
	    controlPanel.enableDisableTriggerControls(false);
    }
    
    // Create the ports and append them to the module
    private void setPorts(){
	int numInPorts = 5;
	int numOutPorts = 1;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
	
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortLabel("Data in");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);
		
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
	port.setPortLabel("Dists in");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 2);
	port.setPortLabel("Set parameters");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 3);
	port.setPortLabel("Initial positions");
	ports.add(port);
		
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 4);
	port.setPortLabel("Start");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 5);
	port.setPortLabel("Blast");
	ports.add(port);
		
	// Add 'out' port for low-D representation vectors
	
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
	port.setPortLabel("Output");
	ports.add(port);
		
	// Add 'out' port for convergence trigger
	
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
	port.setPortLabel("Convergence trigger");
	ports.add(port);
		
	addPorts(ports);
    }

    private void setInitialAnchors(){
	/**
	 * Use the linear heuristic algorithm described in the FastMap 
	 *(Faloutsos)  paper to determine the two points in the data 
	 * set that are furthest apart.
	 *
	 */
		
	anchors = new ArrayList();

	// Choose an object at random.
	int secondAnchorIndex = (int)(Math.random() * (dataItems.getSize()));
	int firstAnchorIndex=0;

	// Find the object that is furthest away from this.
	double maxDist = 0.0d;
	double dist;

	for (int i = 0; i < dataItems.getSize(); i++) {
	    if (secondAnchorIndex != i) {
		dist = dataItems.getDesiredDist(i, secondAnchorIndex);
		if (dist > maxDist) {
		    maxDist = dist;

		    // Set the first distant object:
		    firstAnchorIndex = i;
		}
	    }
	}

	// Find the object that is furthest from the first distant object.
	
	maxDist = 0.0d;
	for (int i = 0; i < dataItems.getSize(); i++){
	    if (firstAnchorIndex != i){
		dist = dataItems.getDesiredDist(i, firstAnchorIndex);
		if (dist > maxDist){
		    maxDist = dist;

		    // Set the second distant object:
		    secondAnchorIndex = i;
		}
	    }
	}

	// These two objects are the 1st two anchors
	anchors.add(new Integer(firstAnchorIndex));
	anchors.add(new Integer(secondAnchorIndex));

	//	System.out.println("Anchors:  "+firstAnchorIndex+"  "+secondAnchorIndex);

	//to find 3rd anchor (max distance from 1st 2 and midpt) need to lay out 1st 2	
    }

    private void findThirdAnchor() {
	int firstAnchorIndex = ((Integer)anchors.get(0)).intValue();
	int secondAnchorIndex = ((Integer)anchors.get(1)).intValue();
	int thirdAnchorIndex=0;
	
	Coordinate midPt = new Coordinate(((((Coordinate)position.get(firstAnchorIndex)).getX() + ((Coordinate)position.get(secondAnchorIndex)).getX()) / 2), ((((Coordinate)position.get(firstAnchorIndex)).getY() + ((Coordinate)position.get(secondAnchorIndex)).getY()) / 2));
	Coordinate p1;
	double maxDist = 0.0d, dist=0.0;
	for (int i = 0; i < dataItems.getSize(); i++){
	    if (firstAnchorIndex != i && secondAnchorIndex != i){
		dist = dataItems.getDesiredDist(i, firstAnchorIndex);
		dist += dataItems.getDesiredDist(i, secondAnchorIndex);
		p1 = (Coordinate)position.get(i);
		Vect v = new Vect(p1, midPt);		
		dist += v.getLength(); //mid-pt
		if (dist > maxDist){
		    maxDist = dist;
		    // Set the 3rd distant object:
		    thirdAnchorIndex = i;
		}
	    }
	}
	anchors.add(new Integer(thirdAnchorIndex));
	for (int u=1; u<numSets; u++)
	    ((ArrayList)sets.get(u)).add(2, new Integer(thirdAnchorIndex));
    }

    
    private void initSplit(){
	anchorContents = new HashSet(anchors);
	sets = new ArrayList();
	setContents = new ArrayList();
	whichSet = new ArrayList();
	anchorPositions=new ArrayList();

	currentLayout=0;
	commonInit();

	//Create separate sets
	//add anchors to every set
	for (int i =0; i<numSets; i++) {
	    ArrayList AL = new ArrayList();
	    for (int j =0; j<numAnchors-1; j++) { //only got 1st 2 at moment
		AL.add((Integer)anchors.get(j));
	    }
	    sets.add(AL);
	    anchorPositions.add(new ArrayList());
	}

	double random;
	int addTo;
	for (int i=0; i< dataItems.getSize(); i++) {
	    if (!anchorContents.contains(new Integer(i))){ // if this not a anchor
		addTo=numSets-1;
		random = Math.random();
		for (int j=0; j<numSets; j++){ //add obj randomly to a set
		    if (random < ((j+1)*(1.0/(double)numSets))){
			addTo=j;
			break;
		    }
		}
		((ArrayList)sets.get(addTo)).add(new Integer(i)); 
		whichSet.add(new Integer(addTo));
	    }  else {
		whichSet.add(new Integer(0)); //anchor
	    }
	}

	System.out.print(numSets+" sets, of sizes ");
	for (int i=0; i<numSets; i++)
	    System.out.print(((ArrayList)sets.get(i)).size()+" ");
	System.out.println();

	ArrayList thisSet;
	for (int i =0; i<numSets; i++){
	    thisSet = (ArrayList)sets.get(i);
	    setContents.add( new HashSet( thisSet ));
	    //debugging
	    //	    for (int k=0; k<thisSet.size(); k++)
	    //System.out.print(((Integer)thisSet.get(k)).intValue()+" ");
	    //System.out.println();
	}

	currentSet = (ArrayList)sets.get(0);

	// initialise neighbour sets.  Needs to be done differently since have Include as well as Exclude hashSets for createRandSample
	
	// init the neighbours array list to be a random sample
	neighbours = new ArrayList();
	samples = new ArrayList();
	distances = new ArrayList();
	hts = new ArrayList();
	    
	// init every element of neighbours and samples with a random list
	for ( int i = 0 ; i < dataItems.getSize() ; i++ ) {
		
	    distances.add(new ArrayList(neighbourSize));
		
	    HashSet exclude = new HashSet();
	    exclude.add(new Integer(i));		
	    HashSet include = (HashSet)setContents.get(((Integer)whichSet.get(i)).intValue()); // only elements from same set
	    // add anchors to include
	    Collection c = anchors;
	    include.add(anchors);

	    //init each neighbours set to a random list
	    ArrayList neighbs = Utils.createRandomSample(include, exclude, dataItems.getSize(),neighbourSize);
	    
	    //initialise distances - save computation later
	    // also set up hashtable 
	    Hashtable ht = new Hashtable(neighbourSize);
	    
	    //	    System.out.print("initialise neighbs for "+i+": ");
	    for (int y=0; y< neighbourSize; y++){
		Double di = new Double(dataItems.getDesiredDist(i, ((Integer)neighbs.get(y)).intValue()));
		((ArrayList)distances.get(i)).add(di);
		ht.put((Integer)neighbs.get(y), new Integer(y));
		//System.out.print(" "+((Integer)neighbs.get(y)).intValue());
	    }//System.out.println();

	    hts.add(ht);
	    // sort the arraylist into ascending order
	    NeighbourComparator comp = new NeighbourComparator(dataItems, i, (ArrayList)distances.get(i), ht);
	    Collections.sort(neighbs, comp);
	    Collections.sort((ArrayList)distances.get(i));
	    Collections.reverse((ArrayList)distances.get(i));
		
	    neighbours.add(neighbs);
		
	    exclude = new HashSet(neighbs);
	    exclude.add(new Integer(i));		
	    //insert an ArrayList of samples into each samples element
	    samples.add(Utils.createRandomSample(include, exclude, dataItems.getSize(), sampleSize));
	}
	
	//	System.out.println("obj  "+((Integer)((ArrayList)sets.get(1)).get(3)).intValue()+" (in 2nd set) before firstTwenty:");
	//for (int ir=0; ir<neighbourSize; ir++)
	// System.out.print(" "+((Integer)((ArrayList)neighbours.get(((Integer)((ArrayList)sets.get(1)).get(3)).intValue())).get(ir)).intValue());
	//System.out.println();


	thisSampleDists = new ArrayList();
	for (int p=0; p<sampleSize; p++) 
	    thisSampleDists.add(new Double(0.0));
	
	firstTwenty();

	//	System.out.println("obj  "+((Integer)((ArrayList)sets.get(1)).get(3)).intValue()+" (in 2nd set) after firstTwenty:");
	//for (int ir=0; ir<neighbourSize; ir++)
	//  System.out.print(" "+((Integer)((ArrayList)neighbours.get(((Integer)((ArrayList)sets.get(1)).get(3)).intValue())).get(ir)).intValue());
	//System.out.println();

	ArrayList anchSend = new ArrayList();
	ArrayList anchContents = new ArrayList();
	anchSend.add(new String("Anchors"));
	anchContents.add(new Integer(numAnchors));
	anchContents.add(new Integer(numSets));
	anchContents.add(anchorContents);
	anchContents.add(anchors);
	anchContents.add(whichSet);
	anchSend.add(anchContents);
	//	System.out.println("Spring: OutPort 0: anchs");
	getOutPort(0).sendData(anchSend);

	// Send positions to the low-D out-port
	transferData = new ArrayList();
	transferData.add(dataItems);
	transferData.add(position);

	//	System.out.println("Spring: OutPort 0: posns");
	getOutPort(0).sendData(transferData);
    }




    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (arg != null){
	    if (toPort.getKey().equals("i0")){
		nullifyReferences();
		// High D data has been sent to the first in-port
		// Only accept it if the set is large enough
			
		//		System.out.println("Spring: InPort 0: data In");	
		if (((DataItemCollection)arg.get(0)).getSize() > (neighbourSize + sampleSize)){
		    // First, if the spring model is already
		    // running, stop it
		    
		    useDistInput = false;
		    bHigh_D_InputRemoved = true;
		    stopSpringModel();
					
		    getOutPort(0).sendData(null);
		    		
		    bHigh_D_InputRemoved = false;
					
		    dataItems = (DataItemCollection)arg.get(0);
		    dataSize = dataItems.getSize();
			
		    System.out.println("Spring: outPort 0: "+dataItems.getSize());
		
		    if (dataItems != null)
			if (!splitUpBox.isSelected()){
			    initSpringModel();
			}
			else{
			    if (!useParams){
				numSets = Integer.parseInt(numSetsBox.getText());
			    }
			    setInitialAnchors();
			    initSplit();
			}
		    startControls();
				       
		    // If the convergence trigger has not already been set
		    // make the default value = number of data items
					
		    if (iterationTrigValue == 0)
			iterationTrigValue = dataItems.getSize();
		    
		    // If the control panel is showing then enable the controls
					
		    if (controlPanel != null){
			controlPanel.setTriggerText();
			controlPanel.enableDisableControls(true);
			setControlValues();
		    }
					
		    // Allow the convergence trigger controls to be enabled
		    
		    triggerEnabled = true;
		}
		else{
		    // Data set is too small
					
		    bHigh_D_InputRemoved = true;
		    nullifyReferences();
		    stopSpringModel();
		    //		    System.out.println("Spring: outPort 0: null");
		    getOutPort(0).sendData(null);
					
		    // If the control panel is showing then disable the controls
					
		    triggerEnabled = false;
		    
		    if (controlPanel != null){
			controlPanel.enableDisableControls(false);
			bConvergenceTrig = false;
		    }
		    startControls();
		}
	    }
	    else if (toPort.getKey().equals("i1")){

		fullDists = (ArrayList)arg.get(0);
		if (fullDists == null) System.out.println("null fullDists");
		else{
		    useDistInput = true;
		    System.out.println("fullDist in"+fullDists.size());
		}

	    }
	    else if (toPort.getKey().equals("i2")){ //parameters
		// *** All Parameters Are Strings ***

		// param 0: name of module for which this set of params meant
		// param 1: ITERATIVE_CONVERGENCE = 0; VELOCITY_CONVERGENCE = 1;
		//2nd param: numIts (ignored if param1==1)
		//3rd: velocity (ignored if p1==0)	
		//4th : 0:no sets, 1: useSets
		//5th: numSets
		
		// *** All Parameters Are Strings ***
		//		System.out.println("Spring Model: parameters received");
		if (!useDistInput){
		    useParams=true;
		
		    if (showControls.getText().equals("Hide controls"))
			hideEngineControls();
		    showControls.setEnabled(false);
		    
		    boolean found =false;
		    int counter=0;
		    ArrayList paramList= new ArrayList();
		    
		    //search for Springs parameter package
		    while(!found && counter<arg.size()){
			paramList = (ArrayList)arg.get(counter);
			if (((String)paramList.get(0)).equals("SpringModel"))//package meant for this module - NEED SOME WAY OF DISCRIMINATING BETWEEN DIFFERENT SPRING MODULES
			    found=true;
			else
			    counter++;
		    }
		    
		    //System.out.println(Integer.parseInt((String)paramList.get(1))+" -  "+Integer.parseInt((String)paramList.get(2)));
		    if (found){
			convergenceTrigger = Integer.parseInt((String)paramList.get(1));
			
			if(convergenceTrigger==0)
			    setIterationTrigValue(Integer.parseInt((String)paramList.get(2)));
			else
			    setVelocityTrigValue(Double.parseDouble((String)paramList.get(3)));
			setConvergenceTriggerEnabled(true);
		    }
		    if (Integer.parseInt((String)paramList.get(4))==1){
			splitUpBox.setSelected(true);
			numSets = Integer.parseInt((String)paramList.get(5));
			if (Integer.parseInt((String)paramList.get(6))==1)
			    initPos.setSelected(true);
			else
			    initPos.setSelected(false);
		    }else{
			splitUpBox.setSelected(false);
		    }
		}
	    } else if (toPort.getKey().equals("i3")){
		// Low D data has been sent to intialise the
		// starting positions of items
				
		System.out.println("Spring: InPort 2: positions");	
		if (!(arg.get(0) instanceof String && ((String)arg.get(0)).equals("NoAnchors"))){ //anchor chat. Ignore
		    if ((ArrayList)arg.get(1) != null){
			if (dataItems != null){
			    setInitialPositions((ArrayList)arg.get(1), (DataItemCollection)arg.get(0));
			    System.out.println("Spring: OutPort 0: posns");	
			    getOutPort(0).sendData(transferData);
			}
		    }
		}
	    }
	    else if (toPort.getKey().equals("i4")){
		// A trigger has been sent to start the spring model
		//		System.out.println("Spring: InPort 3: start *"+(String)arg.get(0)+"* ");
		if (((String)arg.get(0)).equals("first")){
		    ArrayList starting= new ArrayList();
		    starting.add(new String("first"));
		    getOutPort(1).sendData(starting);
		}
		if (dataItems != null && ((String)arg.get(0)).equals("startNextMod")){ 
		    // if (hasRun) // if this is first time, no need to init
		    //initSpringModel();
		    //resetIterations();
		    startSpringModel();
		    ArrayList starting= new ArrayList();
		    starting.add(new String("start"));
		    //		    System.out.println("Spring: OutPort 1: starting");
		    getOutPort(1).sendData(starting);
		}
	    }else if (toPort.getKey().equals("i5")){
		//blast info
		// args: 0 - bombSite coords  
		//       1 - blastEnergy double
		if (!bombing){
		    bombing = true;
		    bombSite = (Coordinate)arg.get(0);
		    blastEnergy = ((Double)arg.get(1)).doubleValue();
		    initBombEnergy();
		}
	    }
	}
	else{
	    if (toPort.getKey().equals("i0")){
		// Input module was deleted
			
		bHigh_D_InputRemoved = true;
		nullifyReferences();
		stopSpringModel();
		//		System.out.println("Spring: outPort 0: null");
		getOutPort(0).sendData(null);
		
		// If the control panel is showing then disable the controls
		
		triggerEnabled = false;
				
		if (controlPanel != null){
		    controlPanel.enableDisableControls(false);
		    bConvergenceTrig = false;
		}
		startControls();
	    }
	    if (toPort.getKey().equals("ii")){
		useDistInput = false;
		fullDists = null;
	    }
	    if (toPort.getKey().equals("i2")){
		//parameters deleted
		showControls.setEnabled(false);
	    }
	}
    }
    
    
    private void initBombEnergy(){

	Coordinate c;
	Vect v;
	double distFromBlast;
	bombEnergy = null;
	bombEnergy = new ArrayList();
	double thisEnergy;
	double distEnergy;
	int more=0, less=0;
	for (int i=0; i< dataSize; i++){
	    c = ((Coordinate)position.get(i));
	    v = new Vect(bombSite,c);
	    distFromBlast = v.getLength(); 

	    if (distFromBlast>1.0)more++;
	    else less++;
	    if (distFromBlast<1.0){
		distEnergy = 1.0-distFromBlast;
		thisEnergy = distEnergy * blastEnergy;
		bombEnergy.add(new Double(thisEnergy));
	    }else
		bombEnergy.add(new Double(1.0));
	}
    }


    /**
     * When the module recieves position data, make sure that the correct
     * positions for this module's dataItems are set
     */
    private void setInitialPositions(ArrayList pos, DataItemCollection dataIn){
	Coordinate thisCoord;
	Coordinate newCoord;
	
	int dataItemID = 0;
	int dataInID = 0;
	

	//stop clock here
	ArrayList converged= new ArrayList();
	converged.add(new String("toSubtract"));
	getOutPort(1).sendData(converged);	    
	
	System.out.println("start line-up"+System.currentTimeMillis());
	for (int i = 0 ; i < dataItems.getSize() ; i++){ //O(Nsqd) process
	    // Find each dataItems corresponding position in the input data
			
	    dataItemID = dataItems.getDataItem(i).getID();
			
	    for (int j = 0; j < dataIn.getSize(); j++){
		dataInID = dataIn.getDataItem(j).getID();
		
		if (dataItemID == dataInID){
		    thisCoord = (Coordinate)position.get(i);
		    newCoord = (Coordinate)pos.get(j);
		    
		    // Set the input positions as the current positions for
		    // the output
		    
		    thisCoord.set(newCoord.getX(), newCoord.getY(), 
				  newCoord.getW(), newCoord.getZ());
		    
		    break;
		}
	    }
	}
	converged= new ArrayList();
	converged.add(new String("endSubtract"));
	getOutPort(1).sendData(converged);	 
	System.out.println("end line-up"+System.currentTimeMillis());

    }
	
    /**
     * Initalises the data structures needed for the spring model
     *
     */
    private void init(){
	position = new ArrayList();
	position.clear();
	velocity = new ArrayList();
	velocity.clear();
	force    = new ArrayList();
	force.clear();
		
	// Alias all of the position, vel & force vals from the dataItems
	// so that they can be accessed locally - makes it a wee bitty faster
		
	for (int i = 0 ; i < dataItems.getSize() ; i++){
	    position.add(new Coordinate());
	    velocity.add(new Vect());
	    force.add(new Vect());
	}
    }
	
    /** 
     * Randomises the starting locations of the data set. To be called once
     * at startup preferably
     */    
    private void randomisePositions(){
	Random rand = new Random(System.currentTimeMillis());
	for (int i = 0 ; i < dataItems.getSize() ; i++ ) {	
	    Coordinate p = (Coordinate)position.get(i);   
	    p.set(rand.nextDouble() * layoutBounds + rangeLo, 
		  rand.nextDouble() * layoutBounds + rangeLo, 
		  rand.nextDouble() * layoutBounds + rangeLo,
		  rand.nextDouble() * layoutBounds + rangeLo);
	}
    }
    
    /**
     * Implementation of the runnable interface
     */
	
    public void run(){
	Thread thisThread = Thread.currentThread();
	while (thread == thisThread) {
	    try{
		//System.out.println(getAvgStress()+" ");
		doIteration();
		
		// Has convergence triggering been set?
		    
		if (bConvergenceTrig){
		    // See what type convergence triggering has been set
		    
		    //		    System.out.println("  "+getAvgVelocity());
		    if ((convergenceTrigger == ITERATIVE_CONVERGENCE  && numIterations > iterationTrigValue) ||(splitUpBox.isSelected()&&convergenceTrigger == VELOCITY_CONVERGENCE&& numIterations>10&&currentLayout>0&& getAvgVelocity() < (1.5*velocityTriggerValue)) || (convergenceTrigger == VELOCITY_CONVERGENCE && numIterations>10&& getAvgVelocity() < velocityTriggerValue) || numIterations>1500){//|| (initPos.isSelected() && currentLayout>0 && posCounter++ > 25)
			// Stop the spring model if the number
			// of iterations performed has reached the
			// upper threshold
				
			posCounter=0;
			//lblIterNumber.setText("Iterations: " + (new Integer(iterationTrigValue)).toString());
			System.out.println(numIterations+" its");
			

			//abandon
			if (numIterations>1500){
			    try{
				FileOutputStream out;
				PrintWriter pw;
				out = new FileOutputStream("abandoned.txt", true);
				pw = new PrintWriter(out,true);
				pw.println(numSets);
				out.close();
			    } catch (IOException e){e.printStackTrace();};
			}

			
			if (!splitUpBox.isSelected()){
			    stopSpringModel();
			    ArrayList converged= new ArrayList();
			    converged.add(new String("startNextMod"));
			    //			    System.out.println("Spring: OutPort 1: StartNextMod");
			    getOutPort(1).sendData(converged);
			    showControls.setEnabled(true);
			}else { // splitting up
//  			    if (currentLayout==0){ // 1st time through - find 3rd anchor
//  				findThirdAnchor();
//  			    }
//  			    for (int y=0; y<numAnchors; y++){
//  				((ArrayList)anchorPositions.get(currentLayout)).add(new Coordinate(((Coordinate)position.get(((Integer)anchors.get(y)).intValue())))); //add current position of anchors to anchorPositions array for this layout
//  			    }
			    if (currentLayout==numSets-1){
				stopSpringModel();
				ArrayList anchSend = new ArrayList();
				anchSend.add(new String("NextSet"));
				//				System.out.println("Spring: OutPort 0: anchs");
				getOutPort(0).sendData(anchSend);
				getOutPort(0).sendData(transferData);
				ArrayList converged= new ArrayList();
				converged.add(new String("startNextMod"));
				//				System.out.println("Spring: OutPort 1: StartNextMod");
				getOutPort(1).sendData(converged);
				showControls.setEnabled(true);
			    } else { //finished a layout, but more sub-layouts to do
				numIterations =0;
				currentLayout++;
				ArrayList anchSend = new ArrayList();
				anchSend.add(new String("NextSet"));
				//				System.out.println("Spring: OutPort 0: anchs");
				getOutPort(0).sendData(anchSend);

				System.out.println("layout "+currentLayout);
				currentSet= (ArrayList)sets.get(currentLayout);
				
				firstTwenty();
				if (initPos.isSelected())
				    initPos();
			    }
			}
		    }			
		}
	    }
	    catch(java.lang.Exception e2){
		// An exception may have arisen because the DataSource data has
		// been changed whilst in the last iteration of the current thread
		System.out.println("Exception");
		e2.printStackTrace();
	    }
	    
	    // Only pass the modified positions to the output
	    // Do this after every 10 iterations
	    
	    if (((numIterations % scatterUpdate) == 0) && running){
		//		System.out.println("Spring: OutPort 0: posns");	
		getOutPort(0).sendData(transferData);
	    }
	    thread.yield();
	}
	if (!bHigh_D_InputRemoved){
	    if (running){
		//		System.out.println("Spring: OutPort 0: positions");	
		getOutPort(0).sendData(transferData);
	    }
	}
	else
	    nullifyReferences();
	
	
    }
	    
    
    // initialise positions of objects in currentSet to be on top of their
    // best neighbour
    private void initPos(){
	
	for (int i=0; i<currentSet.size(); i++){
	    position.set(((Integer)currentSet.get(i)).intValue(), new Coordinate(((Coordinate)position.get(((Integer)((ArrayList)neighbours.get(((Integer)currentSet.get(i)).intValue())).get(neighbourSize-1)).intValue())).getX(), ((Coordinate)position.get(((Integer)((ArrayList)neighbours.get(((Integer)currentSet.get(i)).intValue())).get(neighbourSize-1)).intValue())).getY(),((Coordinate)position.get(((Integer)((ArrayList)neighbours.get(((Integer)currentSet.get(i)).intValue())).get(neighbourSize-1)).intValue())).getZ(),((Coordinate)position.get(((Integer)((ArrayList)neighbours.get(((Integer)currentSet.get(i)).intValue())).get(neighbourSize-1)).intValue())).getW())); //not set to be actual object, just value
	}
    }


    private void firstTwenty() {
	// morrisaj : 30.6.03
	// 1st 20 'iterations' just update neighb sets - don't move objs
	System.out.println("firstTwenty");
	if (!splitUpBox.isSelected()){
	    for (int i=0; i<20; i++){
		for (int index = 0; index < dataItems.getSize(); index++) {
		    randomiseSample(index, false);
		    findNewNeighbours(index);   
		}
	    }
	}else {
	    for (int i=0; i<20; i++){
		for (int index = 0; index < currentSet.size(); index++) { //currentSet.size()
		    //These are how we should do it
		    //randomiseSample(((Integer)currentSet.get(index)).intValue());
		    //findNewNeighbours(((Integer)currentSet.get(index)).intValue());
		    
		    // "index", not items in currentSet. Sets first setSize objects (randomly distributed through data sets) to best neighbs from currentSet.  Works well for 2 sets, mix all neighbs between sets - good for making 2nd layout "fit into" 1st.
		    //randomiseSample(index, false);
		    //findNewNeighbours(index);   


		    // 1st set, give best within-set neighbs. Thereafter, only neighbs from static (completed) sublayout.  At moment, just 1st sublayout, although could do from all (eg if on layout 4 of 5, pick neighbs from layouts 1-3, not just 1)
		    if (currentLayout==0)
			randomiseSample(((Integer)currentSet.get(index)).intValue(), false);
		    else
			randomiseSample(((Integer)currentSet.get(index)).intValue(), true);
		    findNewNeighbours(((Integer)currentSet.get(index)).intValue());   
		   
		}
	    }
	}
    }
    
//      private void realign(){
//  	stopSpringModel();
//  	System.out.println("realign");
//  	ArrayList all = (ArrayList)sets.get(0);
//  	for (int y=0; y<numAnchors; y++){
//  	    position.set(((Integer)anchors.get(y)).intValue(), ((Coordinate)((ArrayList)anchorPositions.get(0)).get(y))); //set positions of anchors to be posns in layout 0
//  	}
	
//  	for (int i=1; i<numSets; i++){
//  	    ArrayList newAL = new ArrayList();
//  	    for (int q=0; q<((ArrayList)sets.get(i)).size(); q++) {
//  		if (q!=0 && q!=1) // remove anchors
//  		    newAL.add((Integer)((ArrayList)sets.get(i)).get(q));
//  	    }
//  	    all.addAll(newAL);
//  	    for (int r=0; r<numAnchors; r++){
//  		((ArrayList)sets.get(i)).remove(0);
//  	    }
//  	}
//      }

	
    /**
     * When the high D input to the spring model has been removed
     * nullify all of the references previously stored
     */
	
    private void nullifyReferences(){
	dataItems = null; 
	position = null;
	velocity = null;
	force = null;
	neighbours = null;
	samples = null;
	thisSampleDists = null;
	distances = null;
	hts = null;
	transferData = null;
	anchors=null;
	anchorContents=null;
	sets = null;
	setContents=null;
	whichSet=null;
	anchorPositions=null;
	currentSet=null;
      
    }
    
    /**
     * Method to perform one iteration of the layout algorithm for this
     * layout model
     */
	
    private void doIteration() throws NullPointerException
    {
	//	System.out.println("obj  "+((Integer)((ArrayList)sets.get(1)).get(3)).intValue()+" (in 2nd set):");
	//for (int i=0; i<neighbourSize; i++)
	    //	    System.out.print(" "+((Integer)((ArrayList)neighbours.get(((Integer)((ArrayList)sets.get(1)).get(3)).intValue())).get(i)).intValue());
	    //System.out.println();

	//if (startTime == 0)
	//	    startTime = System.currentTimeMillis();

	//	previousMaxDist = currentMaxDist;
	//      currentMaxDist=0.0;


	// decay bomb energy here?

	if (!splitUpBox.isSelected()){
	    // Iterate over whole data set
	    for (int i = 0 ; i < dataItems.getSize() ; i++) {
		// Calculate the forces that will be exerted on this object

		calcForces(i);
		calcNeighbForces(i);
	    }
	    for (int i = 0 ; i < dataItems.getSize() ; i++) {  
		if (Coordinate.getActiveDimensions() == 4)
		    calcTimeForce(i);
	    
		if (Coordinate.getActiveDimensions() >=  3)
		    calcGravityForce(i);
		
		// Integrate the changes that have just been calculated to calc 
		// this objects new velocity and force
		
		integrateChanges(i);
	    }
	} else{
	    //  System.out.println("--->");
	    //iterate over whole section of data set
	    for ( int i = 0 ; i < currentSet.size() ; i++ ) {
		//calculate the forces that will be exerted on this object
		calcForces(((Integer)currentSet.get(i)).intValue());
		calcNeighbForces(((Integer)currentSet.get(i)).intValue());
	    }
	    for (int i = 0 ; i < currentSet.size() ; i++) {  
		if (Coordinate.getActiveDimensions() == 4)
		    calcTimeForce(((Integer)currentSet.get(i)).intValue());
	    
		if (Coordinate.getActiveDimensions() >=  3)
		    calcGravityForce(((Integer)currentSet.get(i)).intValue());
		
		// Integrate the changes that have just been calculated to calc 
		// this objects new velocity and force
		
		integrateChanges(((Integer)currentSet.get(i)).intValue());
	   
		//System.out.print(((Integer)currentSet.get(i)).intValue()+" ");
	    }	
	}
	//	System.out.println("<---");
	
	numIterations++;
	lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
	if (bombing)
	    bombing = false;
    }
    

    /**  calculate forces based on neighb sample
     * 
     */
    private void calcNeighbForces(int index) {	
		
	ArrayList neighbs = (ArrayList)neighbours.get(index);
		
	// Iterate thro' neighbour set, calcing force based on sim & 
	// euclidean dist
	
	//	System.out.print("calcNeighbForces for"+index+": ");
	for ( int i = 0 ; i < neighbourSize ; i++ ) {
	    addForces(index, ((Integer)neighbs.get(i)).intValue(), ((Double)((ArrayList)distances.get(index)).get(i)).doubleValue());
	    // debugging
	    //	    System.out.print(((Integer)neighbs.get(i)).intValue()+" ");
	}
	//	System.out.println();
	findNewNeighbours(index);   
    }


    /**
     * Calculates the forces that will be exerted on dataItem with index index
     * Calcs forces only by looking at neighbours and samples lists.  
     * Overrides the method in SpringModel
     *
     * @param index The index of the dataItem that forces are to be calculated 
     * on
     */
    private void calcForces(int index)
    {	
	// First randomise the sample
		
	randomiseSample(index, false);
	ArrayList sample = (ArrayList)samples.get(index);
		
	// Iterate thro' sample , calcing force based on sim & eucldiean dist
		
	//	System.out.print("calcforces for "+index+": ");
	for ( int i = 0 ; i < sampleSize ; i++ ) {
	    addForces(index, ((Integer)sample.get(i)).intValue(), ((Double)thisSampleDists.get(i)).doubleValue());
	    // debugging
	    //	    System.out.print(((Integer)sample.get(i)).intValue()+" ");
	}
	//	System.out.println();
    }
	
     /**
     * Calculates the force that will be acting between obj1 and obj2
     * This is based on the difference between their actual distance and
     * their high dimensional distance.
     *
     *  WITH CACHING OF DISTANCES
     * 
     * @param obj1
     * @param obj2
     * @param desiredDist
     */
     
    protected void addForces(int obj1, int obj2, double desiredDist)
    {	
	Coordinate p1 = (Coordinate)position.get(obj1);
	Coordinate p2 = (Coordinate)position.get(obj2);
	Vect v = new Vect(p1, p2);	
	double realDist = v.getLength();

// 	if (realDist>currentMaxDist)
// 	    currentMaxDist=realDist;
		
	if (desiredDist == Double.MAX_VALUE) // if couldn't do comparison
		desiredDist=realDist;
			
	//if (obj1==30) System.out.println(desiredDist+" "+realDist);

	// spring force to attain ideal seperation
			
	double spring = springForce * (realDist - desiredDist);
			
	// get the velocity vector between these two points 
	// this is used to calc a damping factor, to stop everything 
	// getting too fast 
			
	Vect relativeVel = new Vect((Vect)velocity.get(obj1),
		(Vect)velocity.get(obj2));


	Vect unitVect = v.normalizeVector();
			
	// rate of change of separation
			
	double separationSpeed = relativeVel.dotProduct(unitVect); 
	// force due to damping of separation
			
	double damping = dampingFactor * separationSpeed;
	// add on the force component to each dimension
			
	unitVect.scale(spring + damping);
	
	// add this vector onto the force of obj1

	((Vect)force.get(obj1)).add(unitVect);
			
	// and subtract from the force of obj2
			
	((Vect)force.get(obj2)).sub(unitVect);
    } 
	
    /**
     * For the object at index point, check thro' its samples list to check if 
     * any of those objects would make better neighbours than the ones 
     * currently in the neighbours list. 
     *
     * @param index The index of the element whose samples list should be
     * examined for better neighbours
     */
    
    private void findNewNeighbours(int index){
	ArrayList sample = (ArrayList)samples.get(index);
	ArrayList neighbs = (ArrayList)neighbours.get(index);
	ArrayList dists = (ArrayList)distances.get(index);
	Hashtable ht = new Hashtable();
	
	for (int i = 0 ; i < sampleSize ; i++) {
	    // Get the sample Object index
	    
	    int sampObj = ((Integer)sample.get(i)).intValue();
	    
	    
	    // Check to see if this value would be suitable as a new neighbour
  
	    if (((Double)thisSampleDists.get(i)).doubleValue() < ((Double)((ArrayList)distances.get(index)).get(0)).doubleValue()) {
		
		neighbs.set(0, new Integer(sampObj));
		((ArrayList)distances.get(index)).set(0, (Double)thisSampleDists.get(i));
		for (int p=0; p<neighbourSize; p++){
		    ht.put((Integer)neighbs.get(p), new Integer(p));
		}
	    
	
		// sort the arraylist into ascending order
		
		NeighbourComparator comp = new NeighbourComparator(dataItems, index, (ArrayList)distances.get(index), ht);
				
		//then sort the neighbour set and distance set
		
		Collections.sort(neighbs, comp);
		Collections.sort(((ArrayList)distances.get(index)));
		Collections.reverse(((ArrayList)distances.get(index)));	    
	    }
	}
    }
	
	/**
	* swaps 2 values in the distance arraylist
	*
	* @param index object ID index 
	* @param ind1 index of 1 distance
	* @param ind2 index of other distance
	*/
	private void swapDistances(int index, int ind1, int ind2) 
	{
		ArrayList thisAL = (ArrayList)distances.get(index);
		Double temp = (Double)thisAL.remove(ind1);
		thisAL.set(ind1, (Double)thisAL.remove(ind2));
		thisAL.set(ind2, temp);
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
	* @param pickFrom (morrisaj) constrain sample to be picked from 1st (completed) subset (else from current subset)
	*/
	
    private void randomiseSample(int index, boolean pickFrom)
	{
	    // The neighbours list, which is not wanted in this sample
	    HashSet exclude = new HashSet((ArrayList)neighbours.get(index));
	    exclude.add(new Integer(index));
	    ArrayList newSample;
	    if (!splitUpBox.isSelected()){
		newSample = Utils.createRandomSample(null,exclude,dataItems.getSize(), sampleSize);
	    } else if (pickFrom){
		HashSet include = (HashSet)setContents.get(0);
		newSample = Utils.createRandomSample(include, exclude, dataItems.getSize(), sampleSize);
	    } else{
		HashSet include = (HashSet)setContents.get(currentLayout); //only sample from within current (sub)set
		newSample = Utils.createRandomSample(include, exclude, dataItems.getSize(), sampleSize);
	    }
	    for (int y=0; y< sampleSize; y++){
		if(preCalc.isSelected() || useDistInput){
			
		    if (index> ((Integer)newSample.get(y)).intValue())
			try{
			    thisSampleDists.set(y, (Double)((ArrayList)fullDists.get(index)).get(((Integer)newSample.get(y)).intValue()));
			}catch(Exception e){if (thisSampleDists==null)System.out.print("A");if(fullDists==null)System.out.print("B");if(newSample==null)System.out.print("C");//System.out.println(y+","+thisSampleDists.size()+" "+index+","+fullDists.size()+" "+y+","+newSample.size());
			}
		    else 
			thisSampleDists.set(y, (Double)((ArrayList)fullDists.get(((Integer)newSample.get(y)).intValue())).get(index));
		}
		else
		    thisSampleDists.set(y, new Double(dataItems.getDesiredDist(index, ((Integer)newSample.get(y)).intValue())));
		
		samples.set(index, newSample);
	    }
	}
  
	
	/**
	* Method to simulate gravity acting on the system, does this by 
	* dividing the z component of the force
	*
	* @param index The index of the object to calc Gravity force for
	*/
	
	private void calcGravityForce(int index)
	{
		double height = ((Coordinate)position.get(index)).getZ();
		
		Vect f = (Vect)force.get(index);
		Vect v = (Vect)velocity.get(index);
		
		f.setZ( -(height * gravityForce) - (v.getZ() * gravityDampingForce));
	}
	
	/**
	* Method to apply a similar effect on the fourth dimension, which I have 
	* called time, to flatten everything out to 2D
	*
	* @param index  The index of the object to calc Time force for
	*/
	
	private void calcTimeForce(int index)
	{
		double time = ((Coordinate)position.get(index)).getW();
		
		Vect f = (Vect)force.get(index);
		Vect v = (Vect)velocity.get(index);
		
		f.setW( -(time * timeForce) - (v.getW() * timeDampingForce));
	}
	
	/**
	* Integrates the changes that have already been calculated.  Uses the 
	* force and velocity calculations, to move the position based on the
	* current velocity and then to alter the current velocity based on the 
	* forces acting on this object.
	*
	* @param index The index of the object to integrate changes for
	*/
	
    protected void integrateChanges(int index)
	{
		// Adjust the force calculation to be the average force, this
		// involves scaling by the number of calcs done
		
		Vect f = (Vect)force.get(index);
	
		if (bombing && (bombEnergy!=null)){
		    //if (index%100==0) System.out.println(index+" -- "+f+" -- "+((Double)bombEnergy.get(index)).doubleValue());
		    f.scale(((Double)bombEnergy.get(index)).doubleValue());
		    
		}
		f.scale( dataSizeFactor );
		// Scale velocity by force and freeness
		
		Vect vel = (Vect)velocity.get(index);
		Vect scaleForce = new Vect(f);
		scaleForce.scale(deltaTime);
		vel.add(scaleForce);
		vel.scale( freeness );
		// Add velocity onto position
		
		Vect scaleVel = new Vect(vel);
		scaleVel.scale( deltaTime );
		((Coordinate)position.get(index)).add( scaleVel );
	}
	
	/**
	* Returns the coordinate position of the object corresponding to the 
	* index index
	* 
	* @param index The index of the object
	* @return The coordinate of the object
	*/
	
	public Coordinate getPosition(int index)
	{
		return (Coordinate)position.get(index);
	}
	
	/**
	* Returns the data item at index index.
	*
	* @param index The index of the data item wanted
	* @return The data item that was at this index
	*/
	
	public DataItem getDataItem(int index)
	{
		return dataItems.getDataItem(index);
	}
	
	/**
	* Returns the dataItemCollection object that this layoutmodel is 
	* representing.
	* 
	* @return The DataItemCollection that this model is laying out
	*/
	
	public DataItemCollection getDataItemCollection()
	{
		return dataItems;
	}
	
	/**
	* Accessor method for the dataItems object
	*
	* @param dataItems THe dataItemCollection to be used with this layout 
	* model
	*/
	
	public void setDataItemCollection( DataItemCollection dataItems)
	{
		this.dataItems = dataItems;
	}
	
	/**
	* Returns the number of iterations that have been carried out by this 
	* layout model 
	*
	* @return The number of iterations that this layout model has done
	*/
	
	public int getNumIterations()
	{
		return numIterations;
	}
	
	/**
	* Calculates the approximate error in this layout, does this by calcing 
	* the value for a subset of the data set to get an approximation of  
	* the error without slowing down the layout too much.
	*
	* @return The approximation of the avg error
	*/
	
	public double getApproxError()
	{
		ArrayList sample = Utils.createRandomSample(null, null, 
		dataItems.getSize(), 
		Math.min(50, dataItems.getSize()));
		
		double error = 0.0;
		int numComps = 0;
		double lowDist = 0.0;
		double highDist = 0.0;
		
		for (int i = 1 ; i < sample.size() ; i++)
		{
			int obj1 = ((Integer)sample.get(i)).intValue();
			
			for (int j = 0 ; j < i ; j++)
			{	
				int obj2 = ((Integer)sample.get(j)).intValue();
				
				Vect v = new Vect((Coordinate)position.get(obj1),
				(Coordinate)position.get(obj2));
				
				lowDist = v.getLength();
				highDist = dataItems.getDesiredDist(obj1, obj2);
				error += (lowDist - highDist);
				numComps++;
			}
		}    
		return error/(double)numComps;
	}
	
	/**
	* Returns the average error in the data set
	*
	* @return the average error
	*/
	
	public double getAvgError()
	{
		double error = 0.0;
		int numComps = 0;
		
		for (int i = 1 ; i < dataItems.getSize() ; i++ ) 
		{    
			for (int j = 0 ; j < i ; j++)
			{		
				Vect v = new Vect((Coordinate)position.get(i),
				(Coordinate)position.get(j));
				
				double lowDist = v.getLength();
				double highDist = dataItems.getDesiredDist(i, j);
				error += (lowDist - highDist);
				numComps++;
			}
		}
		return error/(double)numComps;
	}
	
	/**
	* Returns an approximation of the average error in the data set
	*
	* @return An approx of the avg velocity
	*/
	
	public double getApproxVelocity()
	{
		
		ArrayList sample = Utils.createRandomSample(null, null, 
		dataItems.getSize(), 
		Math.min(50, dataItems.getSize()));
		
		double totalVel = 0.0;
		
		for (int i = 0 ; i < sample.size() ; i++) 
		{
			int index = ((Integer)sample.get(i)).intValue();
			totalVel += ((Vect)velocity.get(index)).getLength();
		}
		
		return totalVel / sample.size();
	}
	
    /**
     * Returns the average velocity in the data set
     *
     * @return the average velocity
     */
    
    public double getAvgVelocity(){
	double totalVel = 0.0;
	if (!splitUpBox.isSelected()){
	    for (int i = 0 ; i < dataItems.getSize() ; i++)
		totalVel += ((Vect)velocity.get(i)).getLength();
	    return totalVel / dataItems.getSize();
	} else{
	    for (int i = 0 ; i < currentSet.size() ; i++){
		totalVel += ((Vect)velocity.get(((Integer)currentSet.get(i)).intValue())).getLength();
	    }
	    return totalVel / currentSet.size();
	}
    }
    
	/**
	* Returns the average stress in the data set
	*
	* @return the average stress
	*/
	
	public double getAvgStress()
	{
		double lowDist = 0.0;
		double highDist = 0.0;
		double totalLowDist = 0.0;
		int numComps = 0;
		double stress = 0.0;
		
		
		for (int i = 1 ; i < dataItems.getSize() ; i++) 
		{
			for (int j = 0 ; j < i ; j++ )
			{		
				Vect v = new Vect((Coordinate)position.get(i),
				(Coordinate)position.get(j));
				
				lowDist = v.getLength();
				highDist = dataItems.getDesiredDist(i, j);
				stress += (lowDist - highDist) * (lowDist - highDist);
				totalLowDist += (lowDist * lowDist);
				numComps++;
			}
		}    
		stress = stress / totalLowDist;
		return stress;
	}
	
	/**
	* @return the approximate stress in the layout
	*/
	

    private void resetIterations() {
	numIterations = 0;
	lblIterNumber.setText("Iterations: " + numIterations);//(new Integer(iterationTrigValue)).toString());
    }

	public double getApproxStress()
	{
		double totalStress = 0.0;
		ArrayList sample = Utils.createRandomSample(null, null, 
		dataItems.getSize(), 
		Math.min(50, dataItems.getSize()));
		double stress = 0.0;
		int numComps = 0;
		double lowDist = 0.0;
		double highDist = 0.0;
		double totalLowDist = 0.0;
		
		for (int i = 1 ; i < sample.size() ; i++) 
		{
			int obj1 = ((Integer)sample.get(i)).intValue();
			
			for (int j = 0 ; j < i ; j++)
			{
				int obj2 = ((Integer)sample.get(j)).intValue();
				
				Vect v = new Vect((Coordinate)position.get(obj1),
				(Coordinate)position.get(obj2));
				
				lowDist = v.getLength();
				highDist = dataItems.getDesiredDist(obj1, obj2);
				stress += (lowDist - highDist) * (lowDist - highDist);
				totalLowDist += (lowDist * lowDist);
				numComps++;
			}
		}    
		stress = stress / totalLowDist;
		return stress/(double)numComps;
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
			stopSpringModel();
	}
}

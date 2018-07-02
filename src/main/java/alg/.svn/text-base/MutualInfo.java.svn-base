/**
 * Algorithmic testbed
 *
 * Mutual Information
 *
 * Operations using the Mutual Information metric
 *
 *  @author Alistair Morrison
 */
package alg; 
 
import parent_gui.*;
import data.*;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.Box.Filler;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.util.Date;
import javax.swing.event.*;
import javax.swing.text.*;

public class MutualInfo extends DefaultVisualModule  {

    private boolean outputMI=false;
    private boolean normalise = true;
    private int numStDevs = 2;


    // Versioning for serialisation
        
    static final long serialVersionUID = 50L;
    
    // The parent MDI form
    
    private static Mdi mdiForm;
    
    // The parent drawing surface
    
    private DrawingCanvas drawPane;

    private JTextField stepSizeBox;
    private JTextField numBucketsBox;
    private JTextField overlapSizeBox;
    private JTextField fileNameStoreBox;
    private JTextField fileNameLoadBox;
    private int stepSize;
    private int overlapSize;
    private JButton calcMatrixButton;
    private JButton storeMatrixButton;
    private JButton loadMatrixButton;
    private JButton processDataButton;
    private JLabel numObjs;
    private JLabel matrixLabels;
    private JCheckBox disjointObjs;
    
    private ArrayList fullDists;
    private ArrayList bucketWidths;

    private String defaultFilePrefix = "c:\\MImatrix";

    // The dimensions of the module
    
    private int height = 275;
    private int width = 300;
    
    private static double smallConstant = 0.0000001;

    // The input data
        
    DataItemCollection processedData;
    DataItemCollection originalData;


        
    public MutualInfo(Mdi mdiForm, DrawingCanvas drawPane)   {
        super(mdiForm, drawPane);

        setName("Mutual Info");
        setToolTipText("Mutual Information");
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
        
    
    private void addControls()  {
                
        JPanel thisPLeft = new JPanel();
	JPanel thisPRight = new JPanel();
	JPanel thisPBoth = new JPanel();
	JPanel thisPResizeMatrix = new JPanel();
	JPanel thisPCalcDists = new JPanel();
	JPanel holder = new JPanel();
	
        thisPLeft.setOpaque(false);
	thisPResizeMatrix.setOpaque(false);
	thisPCalcDists.setOpaque(false);
	thisPRight.setOpaque(false);
	thisPBoth.setOpaque(false);
	holder.setOpaque(false);

	thisPLeft.setMaximumSize(new Dimension(150,400));
	thisPLeft.setPreferredSize(new Dimension(140,390));
	thisPResizeMatrix.setLayout(new FlowLayout());

	thisPLeft.setLayout(new BoxLayout(thisPLeft, BoxLayout.Y_AXIS));

	holder.setLayout(new BorderLayout());
	thisPBoth.setLayout(new BoxLayout(thisPBoth, BoxLayout.X_AXIS));

        JLabel numObjsLabel = new JLabel("num objects: ");
        numObjs = new JLabel("    ");

        JLabel setStep = new JLabel("set step size: ");
        stepSizeBox = new JTextField("5");
	stepSize = Integer.parseInt(stepSizeBox.getText());
        stepSizeBox.setPreferredSize(new Dimension(25, 18));
	numBucketsBox = new JTextField("15");
	numBucketsBox.setPreferredSize(new Dimension(25, 18));
	overlapSizeBox = new JTextField("1");
	overlapSizeBox.setPreferredSize(new Dimension(25,18));
	overlapSize = Integer.parseInt(overlapSizeBox.getText());
        calcMatrixButton = new JButton("calc matrix");
	calcMatrixButton.setEnabled(false);
	JLabel disjointLabel = new JLabel("Disjoint objs?");
	disjointObjs = new JCheckBox();
	disjointObjs.setSelected(true);
	overlapSizeBox.setEnabled(false);
	disjointObjs.setOpaque(false);

	processDataButton = new JButton("slice data");
	processDataButton.setEnabled(false);

	JPanel storeMatrixPanel = new JPanel();
	storeMatrixPanel.setLayout(new BoxLayout(storeMatrixPanel, BoxLayout.Y_AXIS));
	storeMatrixPanel.setBorder(new TitledBorder("Store MI distances"));
	storeMatrixPanel.setOpaque(false);
	JLabel fileNameLabel = new JLabel("file name:");
	fileNameStoreBox = new JTextField();
        fileNameStoreBox.setPreferredSize(new Dimension(50,18));
	//        fileNameStoreBox.setMaximumSize(new Dimension(250, 18));
	storeMatrixButton = new JButton("store matrix");
	storeMatrixPanel.add(fileNameLabel);
	storeMatrixPanel.add(fileNameStoreBox);
	storeMatrixPanel.add(storeMatrixButton);
	storeMatrixButton.setEnabled(false);

	JPanel loadMatrixPanel = new JPanel();
	loadMatrixPanel.setLayout(new BoxLayout(loadMatrixPanel, BoxLayout.Y_AXIS));
	loadMatrixPanel.setBorder(new TitledBorder("Load MI distances"));
	loadMatrixPanel.setOpaque(false);
	JLabel fileLdNameLabel = new JLabel("file name:");
	fileNameLoadBox = new JTextField("");
        fileNameLoadBox.setPreferredSize(new Dimension(50,18));
	//fileNameLoadBox.setMaximumSize(new Dimension(60, 18));
	loadMatrixButton = new JButton("load matrix");
	loadMatrixPanel.add(fileLdNameLabel);
	loadMatrixPanel.add(fileNameLoadBox);
	loadMatrixPanel.add(loadMatrixButton);

	thisPResizeMatrix.setBorder(new TitledBorder("Slice data")); 
	
	thisPResizeMatrix.add(numObjsLabel);
        thisPResizeMatrix.add(numObjs);
        thisPResizeMatrix.add(setStep);
        thisPResizeMatrix.add(stepSizeBox);
	thisPResizeMatrix.add(new JLabel("overlap size: "));
	thisPResizeMatrix.add(overlapSizeBox);
	thisPResizeMatrix.add(new JLabel("num buckets"));
	thisPResizeMatrix.add(numBucketsBox);
	thisPResizeMatrix.add(disjointLabel,BorderLayout.SOUTH);
	thisPResizeMatrix.add(disjointObjs);
	thisPResizeMatrix.add(processDataButton);

	thisPCalcDists.setBorder(new TitledBorder("Calc MI distances"));
        thisPCalcDists.add(calcMatrixButton);
	
	Filler filler = new Filler(new Dimension(5, 50), new Dimension(5, 50), new Dimension(5, 50));

	thisPRight.setLayout(new BoxLayout(thisPRight, BoxLayout.Y_AXIS));
	thisPRight.add(storeMatrixPanel);
	thisPRight.add(loadMatrixPanel);
	thisPRight.add(filler);
	
	matrixLabels = new JLabel("");
	thisPRight.add(matrixLabels);

	holder.add(thisPResizeMatrix);
	thisPLeft.add(holder);
	thisPLeft.add(thisPCalcDists);
	thisPBoth.add(thisPLeft);
	thisPBoth.add(thisPRight);
        add(thisPBoth);

	filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));

        add(filler, "South");
        filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
        add(filler, "West");
        filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
        add(filler, "East");
        
        // Make the controls visible depending upon the context
        // of the VisualModule
        
        setInterfaceVisibility();
        addButtonActionListeners();
    }
    
    private void setControlsEnabled(boolean bEnabled)  {
    }
        
        
    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
        if (arg != null){
            if (toPort.getKey().equals("i0")){
                // Data on the input port
                    
                nullifyReferences();
                originalData = (DataItemCollection)(arg.get(0));
		//                processedData = originalData;
		numObjs.setText((new Integer(originalData.getSize())).toString());
		fileNameStoreBox.setText(defaultFilePrefix+"_"+numObjs.getText()+".dat");
		fileNameLoadBox.setText(defaultFilePrefix+"_"+numObjs.getText()+".dat");
		processDataButton.setEnabled(true);

		if (originalData.getSize() > 0)
		    setControlsEnabled(true);
		//	processData();
	
	    }
	   //  } else if (toPort.getKey().equals("o6")){
// 		// Selection data arrived
	    
// 		if (dataItems != null)   {
// 		    selection = (Collection)arg.get(1);
// 		    selectionHandler.updateSelection();
// 		    ArrayList sender = new ArrayList();
		    
// 		    getOutPort(2).sendData("select");
// 		}
	    
	    getOutPort(4).sendData(null); // reset matrix view
	    
	} else   {
	    // Input module was deleted
	    
	    nullifyReferences();
	    getOutPort(0).sendData(null);
	    getOutPort(1).sendData(null);
	    getOutPort(2).sendData(null);
	    getOutPort(3).sendData(null);
	    getOutPort(4).sendData(null);
	    getOutPort(5).sendData(null);
	}
	storeMatrixButton.setEnabled(false);
    }
    
    // Create the ports and append them to the module
    
    private void setPorts()   {
	int numInPorts = 1;
	int numOutPorts = 6;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
	
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortLabel("Data in");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);

	// Add 'out' port
		
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
	port.setPortLabel("Concat Data Out");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
	port.setPortLabel("Dists");
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 2);
	port.setPortLabel("Step size");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 3);
	port.setPortDataStructure(ScriptModel.VECTOR);
	port.setPortLabel("MI on full dims");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 4);
	port.setPortDataStructure(ScriptModel.VECTOR);
	port.setPortLabel("Prob Matrix");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 5);
	port.setPortLabel("Trigger");
	ports.add(port);

// 	port = new ModulePort(this, ScriptModel.SELECTION_PORT, 6);
// 	port.setPortLabel("Selection");
// 	ports.add(port);

	addPorts(ports);
    }
	



    /**
     * When the high D input to the spring model has been removed
     * nullify all of the references previously stored
     */
	
    private void nullifyReferences(){
	processedData = null; 
	originalData = null;
	fileNameStoreBox.setText("");
	fileNameLoadBox.setText("");
	processDataButton.setEnabled(false);
	calcMatrixButton.setEnabled(false);
	storeMatrixButton.setEnabled(false);
    }

    private void enableOverlap(boolean b){
	overlapSizeBox.setEnabled(b);
    }


    private void processData(){

	//slice data into new form

	try{
	    stepSize = Integer.parseInt(stepSizeBox.getText());
	    overlapSize = Integer.parseInt(overlapSizeBox.getText());
	    if (overlapSize>stepSize){
		overlapSize = stepSize-1;
		overlapSizeBox.setText(""+overlapSize);
	    }
	}catch (NumberFormatException ex){
	    System.out.println("Only enter integers");
	}
	
	processedData = new DataItemCollection();
	DataItem thisItem;
	int count =0;
	    
	ArrayList fields = originalData.getFields();
	ArrayList types = originalData.getTypes();
	ArrayList newFields = new ArrayList(); 
	ArrayList newTypes = new ArrayList();
	String f;

	int origDim= fields.size();
	    
	//aggregate fields
	for (int j=0; j<origDim; j++){
	    for (int i=0; i<stepSize; i++){
		f = ((String)fields.get(j)).concat("_"+i);
		newFields.add(f);
		newTypes.add((Integer)types.get(j));
	    }
	}
	
	double[] sumOfVals    =new double[newFields.size()];//= originalData.getSumOfVals();
	double[] sumOfSquares =new double[newFields.size()];//= originalData.getSumOfSquares();
	
	processedData.setFields(newFields);
	processedData.setTypes(newTypes);
	
	processedData.intiNormalArrays(sumOfVals, sumOfSquares);
	    
	int type;
	int item;

	if (disjointObjs.isSelected()){

// 	    for (item=0; item< originalData.getSize(); item++){
// 		if (item%stepSize==0){
// 		    if (item>0){
// 			processedData.addItem(thisItem);
// 		    }
// 		    thisItem = new DataItem((Object[])((DataItem)originalData.getDataItem(item)).getValues(),processedData,count++);
		    
// 		}else
// 		    thisItem.appendDims((Object[])((DataItem)originalData.getDataItem(item)).getValues());
// 	    }
// 	    if ((item)%stepSize==0)
// 		processedData.addItem(thisItem);


	    DataItem addItem;
 	    for (int a=0; a< originalData.getSize()/stepSize; a++){
		Object[] mDims = new Object[stepSize*origDim];
		for (int j=0; j<stepSize;j++){
		    thisItem = originalData.getDataItem((a*stepSize)+j);
		    for (int i=0; i<origDim;i++){
			mDims[(i*stepSize)+j]=thisItem.getValue(i);
		    }
		}
		addItem = new DataItem(mDims,a);
		processedData.addItem(addItem);
	    }
	    

	    
	} else {

	    DataItem addItem;
 	    for (int a=0; a< ((originalData.getSize()-stepSize)/overlapSize)+1; a++){
		Object[] mDims = new Object[stepSize*origDim];
		for (int j=0; j<stepSize;j++){
		    thisItem = originalData.getDataItem((a*overlapSize)+j);
		    for (int i=0; i<origDim;i++){
			mDims[(i*stepSize)+j]=thisItem.getValue(i);
		    }
		}
		addItem = new DataItem(mDims,a);
		processedData.addItem(addItem);
	    }
	   //  for (item=0; item< originalData.getSize()-stepSize; item++){
// 		thisItem=  new DataItem((Object[])((DataItem)originalData.getDataItem(item)).getValues(),processedData,count++);
// 		for (int j=1; j<stepSize; j++)
// 		    thisItem.appendDims((Object[])((DataItem)originalData.getDataItem(item+j)).getValues());
// 		processedData.addItem(thisItem);
		
// 	    }
	}
	//	System.out.println("dims  "+processedData.getSize()+" "+newFields.size());	


	for (int i=0; i< processedData.getSize(); i++){	
	    //normalisation stuff
	    thisItem = processedData.getDataItem(i);
	    for (int colNum=0; colNum<newFields.size(); colNum++){
		
		type = ((Integer)newTypes.get(colNum)).intValue();
		
		switch (type){
		case DataItemCollection.DATE: // Date
		    Date d = (Date)thisItem.getValue(colNum);
		    sumOfVals[colNum]    += (double)d.getTime();
		    sumOfSquares[colNum] += (double)d.getTime() * (double)d.getTime(); 
		    break;                       
		case DataItemCollection.INTEGER: // Integer
		    int intVal = ((Integer)thisItem.getValue(colNum)).intValue();
		    sumOfVals[colNum]    += (double)intVal;
		    sumOfSquares[colNum] += (double)(intVal * intVal);     
		    break;                        
		case DataItemCollection.DOUBLE: // Double
		    double dblVal = ((Double)thisItem.getValue(colNum)).doubleValue();
		    sumOfVals[colNum]    += dblVal;
		    sumOfSquares[colNum] += dblVal * dblVal; 
		    break;                       
		default:               
		    System.exit(0);
		}                             
		
	    }
	}

	processedData.setNormalizeData(sumOfVals, sumOfSquares);
// 	ArrayList thisT2 = new ArrayList();
	// 	ArrayList thisT;
	
	// 	for (int i=0; i< processedData.getSize(); i++){
	// 	    thisT= new ArrayList();
	// 	    for (int j=0; j<processedData.getSize();j++)
	// 		if (i==j)
	// 		    thisT.add(new Double(0.0));
	// 		else
	// 		    thisT.add(new Double(processedData.getDesiredDist(i,j)));
	// 	    thisT2.add(thisT);
	// 	}



	sendData("timeSeriesInfo");
	sendData("");

	calcMatrixButton.setEnabled(true);
	doFullMI(Integer.parseInt(numBucketsBox.getText()));
    }


    /* 
     * Use MI calcs to make a distance matrix between every 
     * pair of concat'd objs
     * 
     * Matrix is triangular
     *
     */
       private ArrayList makeMImatrix(){
	
	normalise = false;// or would mess up bucketing

	ArrayList miMatrix = new ArrayList();

	//init
	int numBuckets = Integer.parseInt(numBucketsBox.getText());
	
	// To do: 
	// * change number of MI concat objs to include variable size of overlap (at momement, overlap = 1).  numNewObjs will then be = ((N-miStepSize)/overlap) - 1

	//variables
	double thisMI;
	ArrayList thisRangeDist;
	ArrayList theseShannons;
	ArrayList thisCondEnt;
	double condEnt;
	double thisCondProb; 
	double tempCE;	  
	ArrayList allValueRanges;
	ArrayList thisVR;
	ArrayList yBucket, allTheseyBs;
	ArrayList temp, minVals, objYDist;
	ArrayList thisMIDistance=new ArrayList();


	for (int x=0; x< processedData.getSize(); x++){//comparing all concat objs x with each other y
	    //  System.out.println("-------------------- x-obj"+x);
	    theseShannons = new ArrayList();//shannon entropies for each slice of concat obj x
	    allValueRanges=new ArrayList();//objs in each slice of concat obj x
	    
	    bucketWidths = new ArrayList();//bucket width of each slice of concat obj x
	    minVals = new ArrayList();// minVals of each slice of concat obj x

	    //get Shannon entropies for each slice of obj x
	    for (int i=0; i<originalData.getNumFields(); i++){
		// perform for each band of dims corresponding to 
		// an orig dim, then average
		
		thisVR = (processedData.getDataItem(x)).getValueRange((i*stepSize),((i+1)*stepSize)-1);
		allValueRanges.add(thisVR);


		// calc Shannon Entropies for each concat'd obj 	
		temp =  ((ArrayList)getRangeProbDist(thisVR,numBuckets));
		thisRangeDist  = (ArrayList)temp.get(0);
		bucketWidths.add((Double)temp.get(2));
		minVals.add((Double)temp.get(3));

		double shannonEntropy = 0.0;
		double thisDistVal;
		for (int j=0; j<numBuckets; j++){
		    thisDistVal = ((Double)thisRangeDist.get(j)).doubleValue();
		    shannonEntropy += (thisDistVal* Math.log(thisDistVal));
		}
		shannonEntropy*=-1.0;
		theseShannons.add(new Double(shannonEntropy));
	    }
	    
	    thisMIDistance = new ArrayList(); // the MI between objs x and y
	    for (int y=0; y<= x; y++){ //comparing all concat objs x with each other y
		//		System.out.println("=========== y-obj"+y);

 		if (x==y)
 		    thisMIDistance.add(new Double(0.0));
// 		else if (y<x)
// 		    thisMIDistance.add((Double)((ArrayList)miMatrix.get(y)).get(x));
 		else{
		    thisMI=0.0;
		
		    for (int i=0; i<originalData.getNumFields(); i++){ //do each slice (orig Dim) separately
			condEnt=0.0;
			thisVR = processedData.getDataItem(y).getValueRange((i*stepSize),((i+1)*stepSize)-1);
			//preproc store these to make it quicker? Or take up too much memory?
		    
			temp = (ArrayList)((ArrayList)getRangeProbDist(thisVR,numBuckets));
			allTheseyBs = (ArrayList)temp.get(1); // actual indices in each bucket of distribution for slice i of concat obj y
			objYDist = (ArrayList)temp.get(0); //distribution
		    
			//conditional entropy  H(X|Y)		    
			for (int a=0; a<numBuckets; a++){
			
			    yBucket = (ArrayList)allTheseyBs.get(a); // indices in bucket a
			
			    thisCondEnt=getConditionalProbDists((ArrayList)allValueRanges.get(i), yBucket, numBuckets,((Double)bucketWidths.get(i)).doubleValue(),((Double)minVals.get(i)).doubleValue(),Double.MAX_VALUE);
			
			    tempCE=0.0;
			    for (int b=0; b<numBuckets; b++){
				thisCondProb = ((Double)thisCondEnt.get(b)).doubleValue();
				tempCE+=(thisCondProb * Math.log(thisCondProb));
			    }
			    tempCE*=-1.0;
			
			    condEnt+= (((Double)objYDist.get(a)).doubleValue()*tempCE);
			}
		    
			//			System.out.print("   "+(((Double)theseShannons.get(i)).doubleValue())+","+condEnt);
			thisMI += ((((Double)theseShannons.get(i)).doubleValue() - condEnt)/Math.log(2));
		    }
		    if (thisMI/originalData.getNumFields() < 0.0)
			System.out.println("negative MI: "+(thisMI/originalData.getNumFields()));
		    thisMIDistance.add(new Double(1.0/(thisMI/originalData.getNumFields())));  // (1 / MI) because want high MI to be small dist on map
		}
	    }
	    miMatrix.add(thisMIDistance);
	}
	return miMatrix;
    }

    /*
     * Calculate MI between every pair of input dims
     *
     */
    private void doFullMI(int numBuckets){
	////////////////////////////////////////////////////////////
	// the mutual info stuff
	////////////////////////////////////////////////////////////
	
	//MI between input dims
	ArrayList inputDimsMI = new ArrayList();
	ArrayList shannonEntropies = new ArrayList();
	ArrayList mutualInfos = new ArrayList();

	//init
	//	int numBuckets = 5;
	bucketWidths = new ArrayList();
	for (int i=0; i<originalData.getNumFields(); i++)
	    bucketWidths.add(new Double(1.0));
	
	//get prob distributions 
	ArrayList probDists= new ArrayList();//probDist[i] has ArrayList A for ith dim: A[0] is ArrayList of proportion of objs in each bucket and A[1] is ArrayList of indices in each bucket and A[2] is list of which bucket each obj added to
	for (int i=0; i<originalData.getNumFields(); i++){
	    probDists.add(getProbDist(originalData.getColumn(i),i,numBuckets));
	}

	ArrayList thisDist;
	for (int x=0; x<originalData.getNumFields(); x++){
	    
	    thisDist = (ArrayList)((ArrayList)probDists.get(x)).get(0);
	    double shannonEntropy = 0.0;
	    double thisDistVal;
	    for (int i=0; i<numBuckets; i++){
		thisDistVal = ((Double)thisDist.get(i)).doubleValue();
		//if (thisDistVal==0.0)thisDistVal=0.0000000000000001;//hack
		shannonEntropy += (thisDistVal* Math.log(thisDistVal));
	    }
	    shannonEntropy*=-1.0;
	    shannonEntropies.add(new Double(shannonEntropy));
	}

	ArrayList conditionalEntropies = new ArrayList();
	ArrayList thisCondEnt;	ArrayList adderCE;
	double condEnt;
	double thisCondProb; double tempCE;
	ArrayList theseMIs;
	
	double minVal,maxVal;

	//conditional entropy  H(X|Y)
	for (int i=0; i<originalData.getNumFields(); i++){
	    if (normalise){
		double dimAvg = ((double[])originalData.getAverage())[i];
		double dimStdDev =((double[])originalData.getSigma())[i];
		minVal = dimAvg-(dimStdDev*numStDevs);
		maxVal = dimAvg+(dimStdDev*numStDevs);
	    }else{
		minVal = ((Double)originalData.getMinimums().get(i)).doubleValue();
		maxVal = ((Double)originalData.getMaximums().get(i)).doubleValue()+smallConstant;
	    }
	    theseMIs = new ArrayList();	    
	    adderCE = new ArrayList();
	    for (int j=0; j<originalData.getNumFields(); j++){
		condEnt=0.0;
		if (outputMI)		System.out.println("\n-----------------------\nDims"+i+" and "+j);

		for (int y=0; y<numBuckets; y++){
		    thisCondEnt=getConditionalProbDists(originalData.getColumn(i),(ArrayList)((ArrayList)((ArrayList)probDists.get(j)).get(1)).get(y),numBuckets,((Double)bucketWidths.get(i)).doubleValue(),minVal,maxVal); // minVal worked out above, in accordance with normalisation strategy
		    tempCE=0.0;
		    for (int x=0; x<numBuckets; x++){
			thisCondProb = ((Double)thisCondEnt.get(x)).doubleValue();
			//			if (thisCondProb==0.0)thisCondProb=0.0000000000000000000000001;//hack
			tempCE+=(thisCondProb * Math.log(thisCondProb));
			//System.out.print(thisCondProb+","+Math.log(thisCondProb)+"  ");
		    }
		    tempCE*=-1.0;
		    condEnt+= (((Double)((ArrayList)((ArrayList)probDists.get(j)).get(0)).get(y)).doubleValue()*tempCE); //System.out.print("--"+((Double)((ArrayList)((ArrayList)probDists.get(secondInputDim)).get(0)).get(y)).doubleValue()+"//");
		}
		adderCE.add(new Double(condEnt));

		
		double mutualInfo = ((Double)shannonEntropies.get(i)).doubleValue() - condEnt;
		mutualInfo /= Math.log(2);
	
		if (outputMI){
		    System.out.println("Shannon"+(float)((Double)shannonEntropies.get(i)).doubleValue()+"  condit"+(float)condEnt);
		    System.out.print("Mutual Info ="+(float)mutualInfo);
		    double mi2 = mutualInfo/((Double)shannonEntropies.get(i)).doubleValue(); // normalise by Shannon entropy.  So that, eg stable dims (or sections of dims if using between periods of time) with low Shannon ent don't mean that MI automatically low
		    System.out.print("  (norm'd be SE ="+(float)mi2+")");
		}
		
		//		System.out.println(i+","+j+","+mutualInfo);
	    
		theseMIs.add(new Double(mutualInfo));
		//System.out.println(i+","+j+","+(float)mutualInfo);
	    }
	    mutualInfos.add(theseMIs);
	    conditionalEntropies.add(adderCE);
	}


	//data to send
	ArrayList outputMIs = new ArrayList();
	outputMIs.add(new DataItemCollection());
	outputMIs.add(mutualInfos);
	outputMIs.add(new String("twoDblock")); //instruction to chart to over-ride
	getOutPort(3).sendData(outputMIs);
	
	ArrayList calcJEpack = calcJointEntropies(numBuckets, probDists);
	// pass out joint entropies
	ArrayList probOut = (ArrayList)calcJEpack.get(0);
	ArrayList jointEntObjRefs = (ArrayList)calcJEpack.get(1);

	outputMIs = new ArrayList();
	outputMIs.add(originalData);
	outputMIs.add(probOut);
	outputMIs.add(jointEntObjRefs);
	
	getOutPort(4).sendData(outputMIs);
    }

    private ArrayList initJEs(int numBuckets, boolean refs){
	ArrayList initJE = new ArrayList();
	ArrayList thisIJE;

	//init >0.0
	for (int i=0; i<numBuckets; i++){
	    thisIJE=new ArrayList();
	    for (int j=0; j<numBuckets; j++)
		if (!refs)
		    thisIJE.add(new Double(smallConstant));
		else
		    thisIJE.add(new ArrayList());
	    initJE.add(thisIJE);
	}
	
	return initJE;
    }

//     private double[][] initJEs(int numBuckets){
// 	double[][] initJE = new double[numBuckets][numBuckets];
	

// 	//init >0.0
// 	for (int i=0; i<numBuckets; i++){
// 	    for (int j=0; j<numBuckets; j++)
// 		initJE[i][j] = smallConstant;
// 	}
// 	return initJE;
//     }



    private ArrayList calcJointEntropies(int numBuckets, ArrayList probDists){
	// create triangular arraylist (origDim * origDim) / 2, 
	// with each item an AL of joint entropies for that dim pair

	ArrayList allJointEnts = new ArrayList(); //for all dim pairs
	ArrayList allJointEntRefs = new ArrayList();

	ArrayList rowOfJEs, rowOfJErefs;
	//	double[][] thisJE;
	ArrayList thisJE, thisJErefs;
	ArrayList whichBsX, whichBsY;
	int thisXBuck, thisYBuck;
	int numFields = originalData.getNumFields();
	
	for (int x=0; x<numFields; x++){ //for all dims
	    whichBsX = (ArrayList)((ArrayList)probDists.get(x)).get(2);
	    rowOfJEs = new ArrayList();
	    rowOfJErefs = new ArrayList();
	    for (int y=0; y<=x; y++){ // against all dims

		whichBsY = (ArrayList)((ArrayList)probDists.get(y)).get(2);

		thisJE= initJEs(numBuckets,false); //init to 0.00001s
		thisJErefs = initJEs(numBuckets, true);
			
		for (int i=0; i<whichBsX.size() ; i++){
		    thisXBuck = ((Integer)whichBsX.get(i)).intValue();
		    thisYBuck = ((Integer)whichBsY.get(i)).intValue();
		    //thisJE[thisXBuck][thisYBuck]+=1.0;
		    ((ArrayList)thisJE.get(thisXBuck)).set(thisYBuck, new Double(((Double)((ArrayList)thisJE.get(thisXBuck)).get(thisYBuck)).doubleValue()+1.0));  //increment count
		    ((ArrayList)((ArrayList)thisJErefs.get(thisXBuck)).get(thisYBuck)).add(new Integer(i)); // store obj ref in 2D matrix

		    //thisJE.set(thisXBuck,);
		}
		rowOfJEs.add(thisJE); 
		rowOfJErefs.add(thisJErefs);
	    }
	    allJointEnts.add(rowOfJEs);
	    allJointEntRefs.add(rowOfJErefs);
	}


	//normalise each matrix
	double checkTot;
	double matrixSum=(double)(originalData.getSize()+((numBuckets*numBuckets)*smallConstant));
	for (int x=0; x<numFields; x++){
	    rowOfJEs = (ArrayList)allJointEnts.get(x);
	    for (int y=0; y<=x; y++){
		thisJE = (ArrayList)rowOfJEs.get(y);
		//checkTot=0.0;
		for (int i=0; i<thisJE.size(); i++){
		    for (int j=0; j<thisJE.size(); j++){
			((ArrayList)thisJE.get(i)).set(j, new Double(((Double)((ArrayList)thisJE.get(i)).get(j)).doubleValue() / matrixSum));
			
			//checkTot+=((Double)((ArrayList)thisJE.get(i)).get(j)).doubleValue();
		    }
		}
		//		System.out.println("check="+checkTot);
	    }
	}
	ArrayList returner = new ArrayList();
	returner.add(allJointEnts);
	returner.add(allJointEntRefs);
	return returner;
    }


    private ArrayList getRangeProbDist(ArrayList range, int numBuckets){
	if (range.size()>0 && range.get(0) instanceof Double){
	    double max = -1.0*Double.MAX_VALUE;
	    double min = Double.MAX_VALUE;
	    for (int i=0; i<range.size();i++){
		if (((Double)range.get(i)).doubleValue()<min)
		    min = ((Double)range.get(i)).doubleValue();
		if (((Double)range.get(i)).doubleValue()>max)
		    max = ((Double)range.get(i)).doubleValue();
	    }
	    max+=smallConstant;
	    double bucketWidth = (max-min)/numBuckets;
	    
	    ArrayList values = new ArrayList();
	    for (int i=0; i<numBuckets; i++) // init buckets
		values.add(new ArrayList());
	    
	    double thisVal;
	    int whichBucket;
	    for (int i=0; i<range.size(); i++){// assign objs to buckets on this dim
		try{
		    thisVal= ((Double)range.get(i)).doubleValue();
		    whichBucket = (int)((thisVal-min)/bucketWidth);
		    //		if (whichBucket<0) whichBucket=0; if (whichBucket>=numBuckets) whichBucket = numBuckets-1;
		    ((ArrayList)values.get(whichBucket)).add(new Integer(i));
		}catch (Exception ex){System.out.println(min+" "+max+" "+bucketWidth+" "+numBuckets+" "+(((Double)range.get(i)).doubleValue()));ex.printStackTrace();}

	    }

	    ArrayList distrib = new ArrayList();
	    double dVal;
	    for (int i=0; i<numBuckets; i++){ // proportion of objs in each bucket
		distrib.add(new Double(((double)(((ArrayList)values.get(i)).size()) +smallConstant)/ ((double)range.size() + (double)numBuckets))); // add small num to each bucket - like initialising all hists with small val to avoid probs later of having 0 probability (then divide by dimSize+numBuckets)
	    }
	    
	    ArrayList returnAL = new ArrayList();
	    returnAL.add(distrib);
	    returnAL.add(values);
	    returnAL.add(new Double(bucketWidth));
	    returnAL.add(new Double(min));
	    return returnAL;
	} else {
	    System.out.print("null range dist ");System.out.print(range.size()+"  "); if (range.size()>0)System.out.println(range.get(0));
	    return null;
	}
    }


    private ArrayList getProbDist(ArrayList dim, int dimNum, int numBuckets){
	// get probability distribution of variable dim
	//returns arraylist--  
	// 0:ArrayList of distribution (proportion of pts in each bucket)
	// 1:ArrayList of actual values in each bucket- ArrayLists of indices 
	// 2:ArrayList of which buckets each obj went into (ordered)

	double dimAvg = ((double[])originalData.getAverage())[dimNum];
	double dimStdDev =((double[])originalData.getSigma())[dimNum];
	double sdMin = dimAvg-(dimStdDev*numStDevs);
	double sdMax = dimAvg+(dimStdDev*numStDevs);
	

	ArrayList distrib=new ArrayList();
	ArrayList whichBs = new ArrayList();


	if (dim.get(0) instanceof Double){
	    double max, min;
	    if(!normalise){
		max = ((Double)originalData.getMaximums().get(dimNum)).doubleValue()+smallConstant;
		min = ((Double)originalData.getMinimums().get(dimNum)).doubleValue(); 
	    }else{
		max = sdMax;
		min = sdMin;
	    }
	    
	    double thisVal;

	    double bucketWidth = (max-min)/numBuckets; //set bucket width
	    bucketWidths.set(dimNum,new Double(bucketWidth));

	    ArrayList values = new ArrayList();
	    for (int i=0; i<numBuckets; i++) // init buckets
		values.add(new ArrayList());

	    int whichBucket;
	    for (int i=0; i<dim.size(); i++){// assign objs to buckets on this dim
		thisVal= ((Double)dim.get(i)).doubleValue();
		if (normalise){
		    if (thisVal>(sdMax-smallConstant))
			thisVal=(sdMax-smallConstant);
		    if (thisVal<(sdMin+smallConstant))
			thisVal=(sdMin+smallConstant);
		}
		
		whichBucket = (int)((thisVal-min)/bucketWidth);
		//if (whichBucket<0) whichBucket=0; if (whichBucket>=numBuckets) whichBucket = numBuckets-1;
		((ArrayList)values.get(whichBucket)).add(new Integer(i));
		whichBs.add(new Integer(whichBucket));
	    }

	    double dVal;
	    for (int i=0; i<numBuckets; i++){ // proportion of objs in each bucket
		distrib.add(new Double(((double)(((ArrayList)values.get(i)).size()) +smallConstant)/ ((double)dim.size() + ((double)numBuckets * smallConstant)))); // add small num to each bucket - like initialising all hists with small val to avoid probs later of having 0 probability (then divide by dimSize+numBuckets)
		//if (outputMI)		System.out.println(i+" -- "+(Double)distrib.get(i));
	    }
	    
	    ArrayList returnAL = new ArrayList();
	    returnAL.add(distrib);
	    returnAL.add(values);
	    returnAL.add(whichBs);
	    return returnAL;
	} else return null;
    }


    private ArrayList getConditionalProbDists(ArrayList xDim, ArrayList yBucket, int numBuckets, double bucketWidth, double thisMin, double thisMax){
	//returns dist of xVars for yBucket - when y values are in yBucket

	ArrayList values=new ArrayList();  ArrayList distrib = new ArrayList();
	for (int i=0; i<numBuckets; i++) // init buckets
	    values.add(new ArrayList());

	int whichXBucket; double thisVal; int index;
	for (int i=0; i<yBucket.size(); i++){
	    // for every point in this yBucket, get value on x dim and see which x-bucket it falls into : build up distribution
	    index = ((Integer)yBucket.get(i)).intValue();
	    thisVal = ((Double)xDim.get(index)).doubleValue();
	    if (normalise){
		if (thisVal>(thisMax-smallConstant))
		    thisVal=(thisMax-smallConstant);
		if (thisVal<(thisMin+smallConstant))
		    thisVal=(thisMin+smallConstant);
	    }

	    
	    whichXBucket = (int)((thisVal-thisMin)/bucketWidth);
	    //if (whichXBucket<0) whichXBucket=0; if (whichXBucket>=numBuckets) whichXBucket = numBuckets-1;
	    ((ArrayList)values.get(whichXBucket)).add(new Integer(index));
	}
	
	double dVal;
	for (int i=0; i<numBuckets; i++){
	    distrib.add(new Double(((double)(((ArrayList)values.get(i)).size()) +smallConstant)/ ((double)yBucket.size() + (double)numBuckets))); // adding small val to each bucket, as in getProbDist
	    
	    //System.out.print(" "+i+"="+(new Double((double)(((ArrayList)values.get(i)).size()) / ((double)yBucket.size()))));
	}
	//	System.out.println();
	return distrib;

    }

    private void matrixInfo(int N){
	if (N>0)
	    matrixLabels.setText("Matrix size: "+N);
	else
	    matrixLabels.setText("");
    }

    /**
     */
    public void sendData(String param)   {	
	ArrayList transferData = new ArrayList();
	
	if (param.equals("timeSeriesInfo") || param.equals("select")){
	    transferData.add(new Integer(stepSize));
	    transferData.add(new Integer(overlapSize));
	    transferData.add(new Boolean(disjointObjs.isSelected()));
	    transferData.add(processedData);
	 //    if (param.equals("select")){
// 		ArrayList select = new ArrayList();
// 		transferData.add(select);
// 	    }
	    getOutPort(2).sendData(transferData);
	    transferData = new ArrayList();
	    transferData.add(processedData);
	    getOutPort(0).sendData(transferData);
	}


	if (param.equals("Dists")){
	    transferData.add(processedData);
	    getOutPort(0).sendData(transferData);
	    transferData = new ArrayList();
	    transferData.add(fullDists);
	    getOutPort(1).sendData(transferData);
	}

    }

    private void addButtonActionListeners(){
	
	stepSizeBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    drawPane.repaint();
		}
	    });
	disjointObjs.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
		    enableOverlap(!disjointObjs.isSelected());
		}
	    });

	calcMatrixButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (originalData != null){
			processData();
			
			
			//start the clock
			ArrayList starting= new ArrayList();
			starting.add(new String("start"));
			getOutPort(5).sendData(starting);
			
			fullDists = makeMImatrix();
			
			//stop
			starting = new ArrayList();
			starting.add(new String("startNextMod"));
			getOutPort(5).sendData(starting);
			
			
			//for (int i=3; i<11; i++)
			//    doFullMI(i);
			sendData("Dists");
			storeMatrixButton.setEnabled(true);
			
		    }
		}
	    });
	
	
	processDataButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    try{
			processData();
		    }catch (NumberFormatException ex){
			System.out.println("Only enter integers");
		    }
		}
	    });

	storeMatrixButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    try{
			java.io.PrintWriter out  = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(fileNameStoreBox.getText())));
			ArrayList theseDists;
			String s;
			//write data size first
			out.write(""+originalData.getSize()+","+((ArrayList)originalData.getTypes()).size()+",");//orig dims
			out.write(""+processedData.getSize()+","+((ArrayList)processedData.getTypes()).size()+",");//concat dims
			out.write("\n");
			for (int i=0; i< fullDists.size(); i++){
			    theseDists = (ArrayList)fullDists.get(i);
			    for (int j=0; j< theseDists.size()-1; j++){
				s= ((Double)theseDists.get(j)).toString();
				out.write(s+",", 0, s.length()+1);
			    }
			    s= ((Double)theseDists.get(theseDists.size()-1)).toString();
			    out.write(s+",", 0, s.length()+1);
			    out.write("\n");
			    out.flush();
			}
			out.close();
		    }catch( Exception ex){ex.printStackTrace();}
		}
	    });
	
	loadMatrixButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    char c;
		    String current = ""; int origN, origD, newN, newD;
		    try{
			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.FileReader(fileNameLoadBox.getText()));
			current="";
			c = 'a';
			while (c!=','){
			    c=(char)in.read();
			    current = current.concat(String.valueOf(c));
			}
			origN = Integer.parseInt(current.substring(0,current.length()-1));//remove comma
			current="";
			c = 'a';
			while (c!=','){
			    c=(char)in.read();
			    current = current.concat(String.valueOf(c));
			}
			origD = Integer.parseInt(current.substring(0,current.length()-1));//remove comma
			current="";
			c = 'a';
			while (c!=','){
			    c=(char)in.read();
			    current = current.concat(String.valueOf(c));
			}
			newN = Integer.parseInt(current.substring(0,current.length()-1));//remove comma
current="";
			c = 'a';
			while (c!=','){
			    c=(char)in.read();
			    current = current.concat(String.valueOf(c));
			}
			newD = Integer.parseInt(current.substring(0,current.length()-1));//remove comma
			

			in.readLine();//skip_line

			if (origN==originalData.getSize() && origD == ((ArrayList)originalData.getTypes()).size() && newN == processedData.getSize() && newD == ((ArrayList)processedData.getTypes()).size()){
			    ArrayList theseDists; fullDists = new ArrayList();
			    //read matrix
			    for (int i=0; i< newN; i++){
				theseDists = new ArrayList();
				for (int j=0; j< i+1; j++){
				    current = new String();
				    c = 'a';
				    while (c!=','){
					c = (char)in.read();
					current = current.concat(String.valueOf(c));
				    }
				    theseDists.add(new Double(Double.parseDouble(current.substring(0,current.length()-1))));
				    //  System.out.print("<"+current.substring(0,current.length()-1)+">,");
				}
				in.readLine();
				fullDists.add(theseDists);
				//System.out.println("");
			    }

			    matrixInfo(newN);
			}else{
			    matrixInfo(0);
			    System.out.println("Distance matrix doesn't match data sizes");
			    System.out.println(origN+","+originalData.getSize()+"  "+origD+","+((ArrayList)originalData.getTypes()).size()+"  "+newN+","+processedData.getSize()+"  "+newD+","+((ArrayList)processedData.getTypes()).size());
			}
			    
			in.close();
			System.out.println("Data read");
			sendData("Dists");
			storeMatrixButton.setEnabled(true);
			
		    }catch (Exception ex1){System.out.println("File read error");}
		}
	    });
    }
					   
}
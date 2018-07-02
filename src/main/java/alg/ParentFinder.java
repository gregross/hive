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
 * ParentFinder
 *
 * Find parents pre-interpolation.  Includes implementation of pivot-based parent-finding for O(N^5/4) MDS
 *
 *  @author Alistair Morrison, Greg Ross
 */

package alg; 
 
import parent_gui.*;
import data.*;

import java.util.HashSet;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.Box.Filler;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;

public class ParentFinder extends DefaultVisualModule implements ActionListener
{

    // Versioning for serialisation	
    static final long serialVersionUID = 50L;
	
    // The parent MDI form	
    private static Mdi mdiForm;
	
    // The parent drawing surface	
    private DrawingCanvas drawPane;
	
    // The dimensions of the module	
    private int height = 144;
    private int width = 390;
    
    // The input data	
    DataItemCollection dataItems;
    
    //The parents
    ArrayList parents;

    // Controls
    JRadioButton brute, sampSearch, pivots, rbSqrN_Items, rb4RootN_Items, rbX_Items;
    JTextField txtPivotsSize;
    JButton cmdApply;
    JPanel radioPane;
    JPanel mainPane;
    int subsetSize;

    // Store the sizes of the various samples
    private int sqrNSize, root4Size, xSize;

    private ArrayList sample, remainder;
    private int numPivots;
    protected double bucketWidth;
    protected ArrayList[][] pivotBuckets; //array of array of arraylists - contents of buckets for each pivot.  e.g. pivotBuckets[3][6] is an arraylist of 3rd pivot, 6th bucket
    protected int numBuckets=35;
    protected ArrayList pivotIDs; //original IDs of points selected as pivots
    protected boolean choosePivots = false;

    protected ArrayList maxDists;  // the maximum distance to an object for each pivot
    protected ArrayList bucketWidths; // width of each pivots' buckets (each pivot has a seperate bucketWidth, but every bucket of pivot i is of equal width)



    //stuff for assessing parents
    FileReader fr,fr2;
    BufferedReader br,br2;
    String buffer = "";
    int p = 1; // buffer[p..] contains next input


    public ParentFinder(Mdi mdiForm, DrawingCanvas drawPane)
    {
	super(mdiForm, drawPane);

	setName("ParentFinder");
	setToolTipText("ParentFinder");
	setLabelCaption(getName());

	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.ALGORITHM_MODE);
	setBackground(Color.orange);
	setDimension(width, height);
	addControls();
	setControlsEnabled(false);
	setPivotsEnabled(false);
	parents = new ArrayList();
    }

    private void calculateSampleSizes(){
	if (dataItems != null){
	    sqrNSize = (new Double(Math.sqrt((new Double(dataItems.getSize())).doubleValue()))).intValue();
	    root4Size = (new Double(Math.sqrt(sqrNSize))).intValue();
	    
	    // Set the labels of the radio buttons
	    
	    rbSqrN_Items.setText("n^1/2 (" + sqrNSize + ")");
	    rb4RootN_Items.setText("n^1/4 (" + root4Size + ")");
	}
	else{
	    sqrNSize = 0;
	    root4Size = 0;
	    rbSqrN_Items.setText("n^1/2");
	    rb4RootN_Items.setText("n^1/4");
	}
    }
    
  /**
     * When the high D input to the spring model has been removed
     * nullify all of the references previously stored
     */
	
    private void nullifyReferences(){

	dataItems = null; 
	parents = null;
	sample = null;
	remainder = null;
	pivotBuckets = null;
	pivotIDs = null;

      
    }

    private void setNumPivots (int i) {
	System.out.println("numPivots:"+i);
	numPivots =i;
    }

    private void getParents(){
	System.out.print("getParents");
	int minIndex=0, c1, c2;
	double minDist, maxDist, dist;

	//pivots
	if (pivots.isSelected()){
	    System.out.println("Pivots");	
	    numBuckets= (int)Math.sqrt(subsetSize);
	    // 1) initialise pivot stuff
	    //if (subsetSize < 3*numPivots)
	    //numPivots = (int)Math.sqrt(subsetSize);
	    maxDists = new ArrayList();
	    bucketWidths = new ArrayList();

	    pivotBuckets = new ArrayList[numPivots][numBuckets]; //array of arrays of arraylists
	    if (!choosePivots){
		//choose randomly - could also allow user to select
		pivotIDs = Utils.createRandomSample(null, null, subsetSize, numPivots);
	    }else{
		int h = 4; //whatever
	    }
	    System.out.print(numPivots+" pivots -");
	    for (int i=0; i<numPivots;i++){
		for (int j=0; j<numBuckets; j++)
		    pivotBuckets[i][j] = new ArrayList();
		System.out.print(" "+((Integer)sample.get(((Integer)pivotIDs.get(i)).intValue())).intValue());
	    }
	    System.out.println();	


	    // 2) pre-process
	    //Preprocessing required by pivot-based method of finding each
	    // sample point's 'parent'
	    // Takes random sample of points to be pivots, then 
	    // transforms objects into pivot-space, where each object 
	    // is represented by its distance from each pivot{
	    
	    double[][] fullDists = new double[sample.size()][numPivots];		
	    // get distances, keep track of biggest
	    for (int j =0; j< numPivots; j++) {
		maxDist=Double.MIN_VALUE;
		c1 = ((Integer)sample.get(((Integer)pivotIDs.get(j)).intValue())).intValue();
		for (int i =0; i< sample.size(); i++) {
		    c2 = ((Integer)sample.get(i)).intValue();
		    if (c1!=c2) {
			dist = dataItems.getDesiredDist(c1,c2);
			if (dist>maxDist) 
			    maxDist = dist;
			fullDists[i][j]=dist;
			
		    } 
		    else //distance to self=0.
			fullDists[i][j]=0.0;
		}
		maxDists.add(new Double(maxDist));
		bucketWidth = (maxDist/(double)numBuckets);// *1.4;
		bucketWidths.add(new Double(bucketWidth));
	    }
	    // put into buckets.  (Simple version : maxDist/numBuckets)
	    // Make new p-dimensional space (p = num pivots)
	    
	    for (int j =0; j< numPivots; j++) {
		bucketWidth = ((Double)bucketWidths.get(j)).doubleValue();
		for (int i =0; i< sample.size(); i++) {
		    if (fullDists[i][j]<0) System.out.print("<<<<<<<<<<<0"+i+" "+j);
		    if (bucketWidth<0) System.out.print("<<<<<<<<<<<0"+bucketWidth);
		    pivotBuckets[j][(int)((fullDists[i][j]-0.0001)/bucketWidth)].add((Integer)sample.get(i));
		}
	    }
	    
	    
	    // 3) parent-find
	    for (int r=0; r< remainder.size(); r++){
		int i = ((Integer)remainder.get(r)).intValue();     
		int bNum, comp;
		double clDist=Double.MAX_VALUE;
		ArrayList bucketContents;
		int tot=0;
		for (int p=0; p< numPivots; p++){
		    comp = ((Integer)sample.get(((Integer)pivotIDs.get(p)).intValue())).intValue();
		    bucketWidth = ((Double)bucketWidths.get(p)).doubleValue();
		    if (comp != i) {
			dist = dataItems.getDesiredDist(i, comp);  //dist calc to get bucket
			bNum = (int)((dist-0.0001)/bucketWidth);
			if (bNum >= numBuckets)
			    bNum = numBuckets-1;
			bucketContents = pivotBuckets[p][bNum];//bucketContents = pivotSet[p].getBucket(bNum); //get rest of that bucket
			tot+=bucketContents.size();
			for (int w=0; w <  bucketContents.size(); w++) { //for all pts in bucket
			    c1=((Integer)bucketContents.get(w)).intValue();
			    if (c1!=i) {
				dist = dataItems.getDesiredDist(c1,i); // main dist calcs - number of which limited by pre-proc
				if (dist < clDist) {
				    clDist = dist;
				    minIndex = ((Integer)bucketContents.get(w)).intValue();
				} 
			    }
			}
		    } else System.out.println("pivotsQuery - getDist to same obj");
		}
		parents.add(new Integer(minIndex));
	    }
	}
	//brute force
	else if (brute.isSelected()){
	    System.out.println("brute force");
	    for (int r=0; r< remainder.size(); r++){
		int i = ((Integer)remainder.get(r)).intValue();
		minIndex = ((Integer)sample.get(0)).intValue();
		
		minDist = dataItems.getDesiredDist(i, minIndex);
		
		for( int j = 0 ; j < sample.size(); j++ ){
		    int samp = ((Integer)sample.get(j)).intValue();
		    if (samp != i)
			if( dataItems.getDesiredDist(i, samp) < minDist ){
			    minDist = dataItems.getDesiredDist(i, samp);
			    minIndex = samp;
			}
		}
		parents.add(new Integer(minIndex));
	    }
	}
	
	// sample
	else if (sampSearch.isSelected()){		
	    System.out.println("sample search");
	    int interpSubsetSize = (int)Math.sqrt(subsetSize) ;
	    if (interpSubsetSize<10 && subsetSize>10) //have minimum interpSubset size = 10 (unless this is bigger than sample size)
		interpSubsetSize=10;
	    ArrayList interpSubset =  new ArrayList(interpSubsetSize);
	    ArrayList interpSubsetNums= Utils.createRandomSample(null, new HashSet(sample), sample.size(), interpSubsetSize);
	    for (int w=0; w<interpSubsetSize; w++) {
		interpSubset.add(sample.get(((Integer)interpSubsetNums.get(w)).intValue()));
	    }
	    
	    for (int r=0; r< remainder.size(); r++){    
		int i = ((Integer)remainder.get(r)).intValue();
		minIndex = ((Integer)interpSubset.get(0)).intValue();
		minDist = dataItems.getDesiredDist(i, minIndex);
		
		for( int j = 0 ; j < interpSubset.size(); j++ ) {
		    int samp = ((Integer)interpSubset.get(j)).intValue();
		    if (samp !=i)
			if( dataItems.getDesiredDist(i, samp) < minDist ) {
			    
			    minDist = dataItems.getDesiredDist(i, samp);
			    minIndex = samp;
			}    
		}
		parents.add(new Integer(minIndex));
		if ((r%5000)==0) System.out.print(" "+r);
	    }
	    System.out.println("sampsearch done");
	}
	
	//	printNeighbOrder();
	//	printParents2();// ver 2: see comments in procedures
	sendData();
    }



    private void addControls()
    {
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	GridBagLayout gridbag2 = new GridBagLayout();
	mainPane = new JPanel(gridbag2);
	mainPane.setBorder(BorderFactory.createTitledBorder("Parent-finding method"));
	mainPane.setOpaque(false);
		
	brute = new JRadioButton("Brute force");
	brute.setActionCommand("brute");
	brute.addActionListener(this);
	brute.setOpaque(false);

	sampSearch = new JRadioButton("Sample");
	sampSearch.setActionCommand("sampSearch");
	sampSearch.addActionListener(this);
	sampSearch.setOpaque(false);

	pivots = new JRadioButton("Pivots");
	pivots.setActionCommand("pivots");
	pivots.addActionListener(this);
	pivots.setOpaque(false);

	ButtonGroup group = new ButtonGroup();
	group.add(brute);
	group.add(sampSearch);
	group.add(pivots);

	mainPane.add(brute);
	mainPane.add(sampSearch);
	mainPane.add(pivots);

	radioPane = new JPanel(gridbag);
	radioPane.setBorder(BorderFactory.createTitledBorder("Number of pivots"));
	radioPane.setOpaque(false);

	// Add the radio buttons	
	// Use square root of data items
	rbSqrN_Items = new JRadioButton("n^1/2");
	rbSqrN_Items.setActionCommand("rbSqrN_Items");
	rbSqrN_Items.addActionListener(this);
	rbSqrN_Items.setOpaque(false);
		
	// Use n^1/4 data items
	rb4RootN_Items = new JRadioButton("n^1/4");
	rb4RootN_Items.setActionCommand("rb4RootN_Items");
	rb4RootN_Items.addActionListener(this);
	rb4RootN_Items.setOpaque(false);
		
	// Allow the user to specifiy the sample size
	rbX_Items = new JRadioButton("X <= n:");
	rbX_Items.setActionCommand("rbX_Items");
	rbX_Items.addActionListener(this);
	rbX_Items.setOpaque(false);
		
	// create button group and add the radio buttons
	ButtonGroup group2 = new ButtonGroup();
	group2.add(rbSqrN_Items);
	group2.add(rb4RootN_Items);
	group2.add(rbX_Items);
		
	// Add to the JPanel
	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.weighty = 1.0;
		
	c.gridwidth = GridBagConstraints.REMAINDER; // End row
	gridbag.setConstraints(rbSqrN_Items, c);
	radioPane.add(rbSqrN_Items);
		
	c.gridwidth = GridBagConstraints.RELATIVE; // Start row
	gridbag.setConstraints(rb4RootN_Items, c);
	radioPane.add(rb4RootN_Items);
		
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(rbX_Items, c);
	radioPane.add(rbX_Items);
		
	// Add a JTextField instance for allowing the user to specify the sample size
		
	txtPivotsSize = new JTextField();
		
	// Ensure that the user can only enter a number <= to the total size
	// of the data set
		
	txtPivotsSize.setDocument(new ValidTextFieldDoc(0, txtPivotsSize, null));
	txtPivotsSize.setPreferredSize(new Dimension(60, 18));
	txtPivotsSize.setMaximumSize(new Dimension(60, 18));
	txtPivotsSize.setMinimumSize(new Dimension(60, 18));
	txtPivotsSize.setActionCommand("txtPivotsSize");
	txtPivotsSize.addActionListener(this);
		
	c.gridwidth = GridBagConstraints.RELATIVE;
	c.fill = GridBagConstraints.NONE;
	gridbag.setConstraints(txtPivotsSize, c);
	radioPane.add(txtPivotsSize);
		
	// Add a button to apply changes made in the text entry box
		
	cmdApply = new JButton("Apply");
	cmdApply.setActionCommand("cmdApply");
	cmdApply.addActionListener(this);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(cmdApply, c);
	radioPane.add(cmdApply);
		
	// Add the JPanel to the module
		
	mainPane.add(radioPane);
	add(mainPane, "Center");
		
	// Fill out the East and West and south regions of the module's BorderLayout
	// so the JPanel has a border
		
	Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
	add(filler, "South");
	filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	add(filler, "West");
	filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	add(filler, "East");
		
	// Make the controls visible depending upon the context
	// of the VisualModule
		
	setInterfaceVisibility();
    }


    private void setControlsEnabled(boolean bEnabled){
	brute.setEnabled(bEnabled);
	sampSearch.setEnabled(bEnabled);
	pivots.setEnabled(bEnabled);
    }

    private void setPivotsEnabled(boolean bEnabled){
	rbSqrN_Items.setEnabled(bEnabled);
	rb4RootN_Items.setEnabled(bEnabled);
	rbX_Items.setEnabled(bEnabled);
	setTextEnabled();
    }


    private void setTextEnabled(){
	if ((rbX_Items.isEnabled()) && (rbX_Items.isSelected())){
	    txtPivotsSize.setEnabled(true);
	    txtPivotsSize.requestFocus();
	    txtPivotsSize.setBackground(Color.white);
	    
	    // Only enable the text box if it contains valid text
	    
	    if (txtPivotsSize.getText().length() > 0)
		cmdApply.setEnabled(true);
	    else
		cmdApply.setEnabled(false);
	}
	else{
	    txtPivotsSize.setEnabled(false);
	    txtPivotsSize.setBackground(Color.lightGray);
	    cmdApply.setEnabled(false);
	}
    }

    /** 
     * Internal class to deal with valid text-entry for
     * determining the sample size
     */
	
    private class ValidTextFieldDoc extends PlainDocument	{
	private int maxValue;
	private JTextField txtBox;
	private JButton apply;

	public ValidTextFieldDoc(int maxValue, JTextField txtBox, JButton apply)	{
	    // The maximum allowable value that the text box can hold
			
	    this.maxValue = maxValue;
			
	    // Reference to the text box per se
			
	    this.txtBox = txtBox;
			
	    // Reference to the apply button that is enabled only when
	    // the text is valid
			
	    this.apply = apply;
	}
	
	public void insertString(int offs, String str, AttributeSet a)
	    throws BadLocationException{
	    // Allow only numerals to be entered
				
	    char[] source = str.toCharArray();
	    char[] result = new char[source.length];
	    int j = 0;

	    for (int i = 0; i < result.length; i++) {
		if (Character.isDigit(source[i]))
		    result[j++] = source[i];
	    }
				
	    // don't allow the first number to be 0
				
	    if ((offs == 0) && str.equals("0"))
		return;
				
	    // Exit here if a non-numeric entry was attempted.
	    // Even though the text was blocked, a parse exception would
	    // still arise
				
	    if ((txtBox.getText() + new String(result, 0, j)).equals("")) {
		apply.setEnabled(false);
		return;
	    }
					
	    // Don't allow the number to be larger than maxValue
					
	    if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) <= maxValue)	{
		super.insertString(offs, new String(result, 0, j), a);
		apply.setEnabled(true);
	    }
	}
			
	protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng)	{
	    super.removeUpdate(chng);
				
	    if ((txtBox.getText().length() < 2) || (chng.getLength() == txtBox.getText().length()))
		apply.setEnabled(false);
	}
    }



    public void actionPerformed(ActionEvent e){
	// Determine whether to set the
	// text entry controls
		
	setTextEnabled();
		
	if (e.getSource() == cmdApply){
	    xSize = Integer.parseInt(txtPivotsSize.getText());
	    rbX_Items.setText("X <= n: (" + xSize + ")");
	    setNumPivots(xSize);
	}
	else if (e.getSource() == rbSqrN_Items)
	    setNumPivots(sqrNSize);
	else if (e.getSource() == rb4RootN_Items)
	   setNumPivots(root4Size);
	else if (e.getSource() == rb4RootN_Items)
	    setNumPivots(root4Size);
	else if (e.getSource() == brute || e.getSource() == sampSearch)
	    setPivotsEnabled(false);
 	else if ( e.getSource() == pivots)
	    setPivotsEnabled(true);
    }

	
    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
	
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
    {
	if (arg != null){
	    if (toPort.getKey().equals("i0")){
		nullifyReferences();
		// Received trigger to start 
		if (((String)arg.get(0)).equals("startNextMod")){
		    queryInPorts();
		    ArrayList starting= new ArrayList();
		    starting.add(new String("start"));
		    getOutPort(1).sendData(starting);
		    getParents();
		}
	    }
	    else if (toPort.getKey().equals("i3")){ // data from data set
		queryInPorts();
		txtPivotsSize.setDocument(new ValidTextFieldDoc(dataItems.getSize(), txtPivotsSize, cmdApply));
	    }
	    else if (toPort.getKey().equals("i2")){ // remainder port
		queryInPorts();
		calculateSampleSizes();
	    }
	    else if (toPort.getKey().equals("i4")){
		// *** All Parameters Are Strings ***
		// param 1: model (0:brute, 1: sample, 2: pivots)
		// param 2: [if Pivots] number : (0:rootN,1:rootrootN,2:constant)
		// param 3: [if previous params = (2,2)] the constant number of pivots to use


		boolean found =false;
		int counter=0;
		ArrayList paramList= new ArrayList();
		    
		while(!found && counter<arg.size()){
		    paramList = (ArrayList)arg.get(counter);
		    if (((String)paramList.get(0)).equals("ParentFinder"))//package meant for this module
			found=true;
		    else
			counter++;
		}
		if (found){
		    int param0=Integer.parseInt((String)paramList.get(1));
		    if(param0==0)
			brute.setSelected(true);
		    else if (param0==1){
			sampSearch.setSelected(true);
			sampSearch.doClick();
		    }
		    else{
			pivots.setSelected(true);
			int param1 = Integer.parseInt((String)paramList.get(2));// param 1
			if (param1==0)
			    rbSqrN_Items.setSelected(true);
			else if (param1==1)
			    rb4RootN_Items.setSelected(true);
			else{
			    setPivotsEnabled(true);
			    txtPivotsSize.setDocument(new ValidTextFieldDoc(200, txtPivotsSize, cmdApply));
			    rbX_Items.doClick();
			    setTextEnabled();
			    txtPivotsSize.setText(((String)paramList.get(3)));  System.out.println((String)paramList.get(3));
			    xSize = Integer.parseInt(txtPivotsSize.getText());
			    rbX_Items.setText("X <= n: (" + xSize + ")");
			    setNumPivots(xSize);
			}
		    }
		}
		    
	    }
	}
	else
	    {
		// Input module was deleted
		
		dataItems = null;
		parents=null;
		setControlsEnabled(false);
		setPivotsEnabled(false);
		calculateSampleSizes();
		dataItems = null;
			
		getOutPort(0).sendData(null);
			
		getOutPort(1).sendData(null);
	    }
    }
	
    private void queryInPorts(){
	ArrayList connectedPorts;
	ModulePort port;

	dataItems=null;
	parents=new ArrayList();
	sample=null; 
	remainder=null;

	// Get parent data
	boolean s=false,r=false,f=false;
	//	System.out.println("first");
	connectedPorts = getInPort(1).getObservedPorts(); // sample
	if ((connectedPorts != null) && (connectedPorts.size() > 0)){
	    if (connectedPorts.get(0) != null)	{
		port =(ModulePort)connectedPorts.get(0);
		if (port.getData() != null){
		    sample = (ArrayList)port.getData().get(1);
		    subsetSize = sample.size();
		    s=true;	//System.out.println("s");
		}
	    }
	}
	connectedPorts = getInPort(2).getObservedPorts(); //remainder
	if ((connectedPorts != null) && (connectedPorts.size() > 0)){
	    if (connectedPorts.get(0) != null)	{
		port =(ModulePort)connectedPorts.get(0);
		if (port.getData() != null){
		    remainder = (ArrayList)port.getData().get(1); //arraylist of indices
		    r=true;//	System.out.println("r -"+remainder.size());
		}
	    }
	}
	connectedPorts = getInPort(3).getObservedPorts(); //full data
	if ((connectedPorts != null) && (connectedPorts.size() > 0)){
	    if (connectedPorts.get(0) != null)	{
		port =(ModulePort)connectedPorts.get(0);
		if (port.getData() != null){
		    dataItems = (DataItemCollection)port.getData().get(0);
		    f=true;	//System.out.println("f");
		}
	    }
	}
	if (s&&r&&f)
	    setControlsEnabled(true);
    }

    // Create the ports and append them to the module
    private void setPorts()
    {
	int numInPorts = 5;
	int numOutPorts = 1;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
		
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 0);
	port.setPortLabel("Start");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
	port.setPortLabel("Sample");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 2);
	port.setPortLabel("Remainder");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 3);
	port.setPortLabel("Data");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 4);
	port.setPortLabel("Parameters");
	ports.add(port);

	// Add 'out' port
		
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
	port.setPortLabel("Parents");
	ports.add(port);
		
	
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
	port.setPortLabel("Convergence trigger");
	ports.add(port);

	addPorts(ports);
    }
	
    /**
     * When the user selects a sample size, this method is used to
     * send the sample and remainder to the output ports
     */
	
    public void sendData()
    {	
	// Get references to the selction handling objects
		
	// Send the sample
	System.out.println("sending");
	ArrayList transferData = new ArrayList();
	transferData.add(parents);
	System.out.println("sent");
	getOutPort(0).sendData(transferData);
	ArrayList converged= new ArrayList();
	converged.add(new String("startNextMod"));
	getOutPort(1).sendData(converged);
		
    }
	
    private void printNeighbOrder(){
	//  FOR LIST OF BEST NEIGHBOURS:

 	int sz = dataItems.getSize();
 	double[][] distAr = new double[sz][sz];
 	int[][] numAr = new int[sz][sz];//row i, left to right, will be other objs is order of proximity to i
 	for (int i=0;i<sz;i++) {
 	    for (int j=0;j<sz;j++) {
 		if (i!=j){
 		    distAr[i][j]=dataItems.getDesiredDist(i, j);
 		    numAr[i][j]=j;
 		}
 		else {
 		    numAr[i][j]=0;
 		    distAr[i][j]=0.0;}
 	    }
 	}
	// 	System.out.println("sort calcs"); //sort numArray
 	for (int z=0;z<sz;z++) {//arrange row z of numArray into order of proximity
 	    for (int i=0;i<sz;i++) {//this and one below are the 2 loops required for sort
 		double smallest = Double.MAX_VALUE;
 		int sm=0;
 		for (int j=i;j<sz;j++) {// find pos of lowest value in (i..end) of row z of distArray (index of obj closest to obj z)
 		    int ghj = numAr[z][j];
 		    if (ghj!=z){
 			double thisD = distAr[z][ghj];
 			if (thisD<smallest) {
 			    smallest = thisD;
 			    sm=j;
 			}
 		    }
 		}
 		int temp = numAr[z][i];
 		numAr[z][i]=numAr[z][sm];
 		numAr[z][sm]=temp;
 	    }
 	}
	
	try{
	    FileOutputStream out = new FileOutputStream("NeighbsOrder.dat", true);
	    PrintWriter pw = new PrintWriter(out,true);

	    for (int z=0;z<sz;z++) {
		pw.println("--------------");
		pw.print("pt "+z+"  ");
		for (int i=0;i<sz-1;i++) {
		    pw.print(numAr[z][i]+" ");
		}
		pw.println();
	    }
	    out.close();
	}catch (IOException e){e.printStackTrace();}
    }


    private String readString(BufferedReader br) {
    // Consume and return the remainder of current line (end-of-line discarded).
        try {
            if (buffer!= null && p>buffer.length()) {
                buffer = br.readLine();
                p = 0;
            }   
            if (buffer == null) throw new IOException("Unexpected end of file."); 
            int t = p;  p = buffer.length() + 1;
            return buffer.substring(t);
       } catch (IOException ioe) {
            System.err.println("IO Exception in readString()");
            return "";
       } 
    }

    private boolean endOfFile(BufferedReader br) { // More characters? 
        // This method is intended for use when keyboard is redirected to file
        if (available()>0) return false;
        try { buffer = br.readLine(); 
        } catch (IOException ioe) {
            System.err.println("IO Exception in Keyboard.readChar()");
        }
        p = 0;
        return (buffer == null);
    }

    private int available() {
    // Number of characters available on this line (including end-of-line, 
    // which counts as one character, i.e. '\n')
        if (buffer == null) return 0;
        else return (buffer.length()+1-p);
    }

   /**
     * Need to override paintComponent because serialisation, for
     * some reason, resets the opacity of the radio buttons. Must
     * be another Java bug
     */
	
    public void paintComponent(java.awt.Graphics g)
    {
	super.paintComponent(g);
		
	if (radioPane != null)
	    {
		radioPane.setOpaque(false);
		rbSqrN_Items.setOpaque(false);
		rb4RootN_Items.setOpaque(false);
		rbX_Items.setOpaque(false);
	    }
    }   
}
/** 
 * Algorithmic test bed
 *  
 * Time Series Drawer
 *  
 *  @author Alistair Morrison
 */ 

package alg.timeSeries;

import data.DataItemCollection;
import data.DataItem;
import parent_gui.*;
import alg.TimeSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import java.awt.event.*;
import java.awt.*;

public class TimeSeriesDrawer extends JPanel implements Selectable, SelectionChangedListener, MouseListener, java.io.Serializable, MouseMotionListener{


    private transient Mdi mdiForm;
	
    private DataItemCollection dataItems;
    
    private TimeSeriesHolder timeSeriesHolder;
    private TimeSeries visMod;
 	
    protected Set theSelection;
    private SelectionHandler selected; 
    private ArrayList selectedAL;
    

    //    private ArrayList origSel;
    private ArrayList avgSelection;

    private final int permittedAccuracy = 5;

    private int numDims;
    private int numObjs;
    private ArrayList dimNames;
    private ArrayList dimSpacings;

    private int borderSpace=7;
    private int draggingArrow = -1;
    private int dragStartX;
    private ArrayList oldSpacings;
    private int squashDim;
    private int squashMovement = 0;

    private ArrowXs arrowPosns;

    private HashSet fullSet;
    //    private ArrayList altered;

    private ArrayList yAxis;
    private int selectBarHeight = 25;
    private int axisHeight=30;
    private int keyWidth = 50;
    private int eachKeyWidth = 15;
    private int eachSep = 25;
    private int charHeight = 9;
    private int charWidth = 9;

    private int setWidth, setHeight;

    private double overallMin, overallMax;

    private ArrayList processedValues;
    private ArrayList scaledDims;
    private ArrayList extremeHighs; // extremes from scaledDims
    private ArrayList extremeLows;
    private ArrayList extremeHighPos;
    private ArrayList extremeLowPos; 
    private boolean showExtremes;
    private int stepSize=1;
    private int spacing;

    private ArrayList drawDims;
    private ArrayList nullValues;
    private ArrayList colours;
    private ArrayList labels;
    private int numLabels;
    private int labelEvery;

    private final int triangleSize = 7;

    private HashSet deselect = new HashSet();
    private boolean miConnected=false;
    private int miStepSize=0;
    private int miOverlap=1;
    private boolean miDisjoint;
    private int miObjs;
    private ArrayList miSelected;
    private DataItemCollection miDataItems;
    private ArrayList miColours;
    private int colourOneObj;

    public TimeSeriesDrawer(DataItemCollection dataItems, Mdi mdiForm, TimeSeriesHolder timeSeriesHolder, TimeSeries visMod) {
	this.mdiForm = mdiForm;
	this.dataItems = dataItems;
	this.timeSeriesHolder = timeSeriesHolder;
	this.visMod = visMod;

	//addMouseListener(this);
	
	//setAutoscrolls(true);

	if (dataItems.getSize() > 0){
	    selected = new SelectionHandler(dataItems.getSize(), dataItems);
	    selected.addSelectableObject(this);
	    selected.addSelectionChangedListener(this);
	} else {
	    if (selected != null){
		selected.removeSelectionChangedListener(this);
		selected.removeSelectableObject(this);
		selected = null;
	    }
	}

	numObjs = dataItems.getSize();
	numDims = dataItems.getNumNumericDims();
	dimNames = dataItems.getFields();
	
	processedValues = new ArrayList();

	drawDims = new ArrayList();

	//	colours
	colours = new ArrayList();
	for (int b=0; b<numDims; b++){
	    int variation = (3+b)%3;
	    Color col;
	    float extent = (float)b/numDims;
	    float base = (float)(b-3.0)/numDims;
	    if (b<3) base=1.0f;
	    if (variation==0)  col = new Color(extent,base,base);
	    else if (variation==1)  col = new Color(base,extent,base);
	    else col = new Color(base,base,extent);
	    colours.add(col);
	    drawDims.add(new Boolean(true));
	}

	avgSelection = new ArrayList();

	for (int i =0; i<numObjs; i++){
	    avgSelection.add(new Boolean(selected.getState(i)));
	}

	miConnected  =false;
	if (numObjs>0){
	    setDims();
	    setWidth = this.getWidth();
	}
	addMouseListener(this);
	addMouseMotionListener(this);
	//	if(numObjs>0)
	//  visMod.sendFocus(drawDims);

	if (miConnected)
	    setMI();
    }

    private int getDrawWidth(){
	return (this.getWidth()-keyWidth-axisHeight-(2*borderSpace));
    }

    
    private int getDrawHeight(){
	return (this.getHeight() - axisHeight - (2*borderSpace)-selectBarHeight);
    }

    //    private void setWidth(){
    //borderSpace = this.getWidth()/50;
    //}

    public void initAxes() {
	//	setWidth();
	setWidth = this.getWidth();
	setHeight = this.getHeight();

	//	theSelection = new HashSet();


	// default spacing
	spacing=getDrawWidth()/numObjs;

	if (getDrawWidth()>0)
	    stepSize = numObjs/getDrawWidth()+1;
	if (stepSize<1) stepSize = 1;
	
	scaledDims = new ArrayList();
	extremeHighs = new ArrayList();
	extremeHighPos = new ArrayList();
	extremeLows = new ArrayList();
	extremeLowPos = new ArrayList();


	if (spacing<1){
	    //if can't draw 1 obj per pixel
	    doAveraging();
	    visMod.setExtremes(stepSize);
	}
	else{
	    scaledDims = processedValues;
	    visMod.setExtremes(1); // no need for separate drawing of extremes
	    showExtremes = false;
	}

	arrowPosns = new ArrowXs(axisHeight+borderSpace, (((numObjs/stepSize)-1)*spacing)+borderSpace+axisHeight,triangleSize);
	setLabels();


	//	handleSelection(arrowPosns.getLeft(), arrowPosns.getRight());

	//	theSelection = selected.getAll();
	selected.updateSelection();
	//visMod.sendSelection();
	//init(0);

    }

    private void doAveraging(){
	int typeID;
	for (int j=0; j<numDims; j++){
	    int i = dataItems.getNumericDimNumber(j);
	    typeID = ((Integer)dataItems.getTypes().get(i)).intValue();
	    scaledDims.add(averagePoints(j, typeID,(ArrayList)processedValues.get(j),stepSize)); //NB processedVals has skipped nulls
	    
	}
	if (((ArrayList)scaledDims.get(0)).size() >0)
	    spacing = getDrawWidth()/((ArrayList)scaledDims.get(0)).size(); 
	setAvgSelect();

    }

    private void setAvgSelect(){
	// handle selections when averaging points per pixel
	int size = dataItems.getSize();
	avgSelection = new ArrayList(); 
	boolean sel;
	for (int i=0; i<(size/stepSize); i++){
	    sel = false;
	    for (int j=0; j<stepSize; j++){
		if (selected.getState((stepSize*i)+j))
		    sel=true;
	    }
	    avgSelection.add(new Boolean(sel));
	}
    }

    private void drawKey(Graphics g) {
	
	for (int i=0; i<numDims; i++){
	//     g.drawLine(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2), borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep),this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2)+eachKeyWidth, borderSpace + ((i/2)*eachKeyWidth+ (i/2)*eachSep));
// 	    g.drawLine(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2), borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep),this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2), borderSpace + (((i/2)+1)*eachKeyWidth+ (i/2)*eachSep));
// 	    g.drawLine(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2)+eachKeyWidth, borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep),this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2)+eachKeyWidth, borderSpace + (((i/2)+1)*eachKeyWidth+(i/2) * eachSep));
// 	    g.drawLine(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2), borderSpace + (((i/2)+1)*eachKeyWidth + (i/2)*eachSep),this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2)+eachKeyWidth, borderSpace + (((i/2)+1)*eachKeyWidth+(i/2) * eachSep));
	    int charsToShow = (keyWidth/2)/charWidth;
	    g.setColor(Color.black);
	    g.drawRect(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2), borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep),eachKeyWidth,eachKeyWidth); 
	    if (((Boolean)drawDims.get(i)).booleanValue()){
		g.setColor((Color)colours.get(i));
		g.fillRect(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2), borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep),eachKeyWidth,eachKeyWidth);
	    }
	    if (charsToShow<((String)dimNames.get(i)).length())
		g.drawString(((String)dimNames.get(i)).substring(0,charsToShow),this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2),borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep)+eachKeyWidth+charHeight+2); 
	    else
		g.drawString((String)dimNames.get(i),this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2),borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep)+eachKeyWidth+charHeight+2); 
	}
    }
    
    private void setMI(){
	if (miStepSize>0){
	    miSelected = new ArrayList();
	    miObjs = miDisjoint?(numObjs/miStepSize):(((numObjs-miStepSize)/miOverlap)+1);
	    //if (!miDisjoint&&(numObjs-miStepSize)%miOverlap==0)
	    //miObjs++;
	    for (int j=0; j<miObjs; j++)
		miSelected.add(new Boolean(true));
	    
	    mutualInfoSelectInit();
	    initAxes();
	    handleSelection(arrowPosns.getLeft(), arrowPosns.getRight());
	}else
	    miConnected=false;
	
	repaint();
    }

    public void setMI(int i, int o, boolean disjoint, DataItemCollection miD){
	miConnected=true;
	miDisjoint = disjoint;	
	miDataItems = miD;
	miStepSize=i;
	miOverlap=o;
	if (numObjs>0)
	    setMI();
    }


    public void setAvgSels(){
	
	int numToDo = numObjs;
	if (spacing>=1){
	    avgSelection = new ArrayList();
	    for (int i =0; i<numToDo; i++){
		avgSelection.add(new Boolean(selected.getState(i)));
	    }
	}else{
	    doAveraging();
	}
    }

    public void showExtremes(boolean b){
	showExtremes=b;
	initAxes();
	repaint();
    }

    private ArrayList averagePoints(int dimNum, int type,ArrayList input, int stepSize){
	//average data over stepSize points

	double chunk; int ichunk;

	int extremeEvery=0;
	ArrayList theseHighExtremes, thisHighExtremePos, theseLowExtremes, thisLowExtremePos;

	//	if (showExtremes){
	    extremeEvery = (numObjs/stepSize)/10;
	    theseHighExtremes = new ArrayList();
	    thisHighExtremePos = new ArrayList();
	    theseLowExtremes = new ArrayList();
	    thisLowExtremePos = new ArrayList();
	    //	}

	    int thisExEv, thisExHighPos, thisExLowPos;
	ArrayList avgData = new ArrayList(); 

	double thisVal, thisHighExtreme=Double.MIN_VALUE, thisLowExtreme = Double.MAX_VALUE;
	thisExHighPos = 0;  thisExLowPos = 0;
	int ignoreCount=0, thisIC;
	int nextNull=0;
	int thisNullSize=((ArrayList)nullValues.get(dimNum)).size(); 
	boolean nullsHere=false;

	if (thisNullSize>ignoreCount){ // more nullVals for this dim
	    nextNull = ((Integer)((ArrayList)nullValues.get(dimNum)).get(ignoreCount)).intValue();
	    nullsHere = true;
	}


	switch (type) {
	case DataItemCollection.INTEGER:
	case DataItemCollection.DOUBLE:

	    thisExEv=0;
	    for (int i=0; i<(numObjs/stepSize); i++){
		chunk=0.0; thisIC=0;
		for (int j=0; j<stepSize; j++){
		    if (nullsHere && ((i*stepSize) + j) == nextNull){
			if (ignoreCount+1<((ArrayList)nullValues.get(dimNum)).size())
			    nextNull = ((Integer)((ArrayList)nullValues.get(dimNum)).get(++ignoreCount)).intValue();
			if(ignoreCount==thisNullSize)
			    nullsHere=false;
			thisIC++;
		    }else{
			thisVal = ((Double)input.get((stepSize*i)+j)).doubleValue();
			chunk+=thisVal;
			if (showExtremes && thisVal > thisHighExtreme){
			    thisHighExtreme = thisVal;
			    thisExHighPos = i;
			}
			if (showExtremes && thisVal < thisLowExtreme){
			    thisLowExtreme = thisVal;
			    thisExLowPos = i;
			}
		    }
		}
		if (showExtremes&&(thisExEv++%extremeEvery == 0)&&thisExEv>1){
		    theseHighExtremes.add(new Double(thisHighExtreme));
		    thisHighExtremePos.add(new Integer(thisExHighPos));
		    theseLowExtremes.add(new Double(thisLowExtreme));
		    thisLowExtremePos.add(new Integer(thisExLowPos));
		    thisHighExtreme = Double.MIN_VALUE;    thisExHighPos=0;
		    thisLowExtreme = Double.MAX_VALUE;    thisExLowPos=0;
		}
		if ((stepSize-thisIC)>0)
		    avgData.add(new Double(chunk/(double)(stepSize-thisIC)));
		else // all nulls here
		    avgData.add(new Double(Double.MIN_VALUE));
		
	    }
	    if (showExtremes) {
		if (thisHighExtreme > Double.MIN_VALUE){
		    theseHighExtremes.add(new Double(thisHighExtreme));
		    thisHighExtremePos.add(new Integer(thisExHighPos));
		}
		if (thisLowExtreme < Double.MAX_VALUE){
		    theseLowExtremes.add(new Double(thisLowExtreme));
		    thisLowExtremePos.add(new Integer(thisExLowPos));
		}
		extremeHighs.add(theseHighExtremes); extremeHighPos.add(thisHighExtremePos); 
		extremeLows.add(theseLowExtremes); extremeLowPos.add(thisLowExtremePos);
	    }
	    break;
	    
	    // draw all as doubles
	    
	    // 	case DataItemCollection.INTEGER:
	    // 	    for (int i=0; i<(size/stepSize); i++){
	    // 		ichunk=0;
	    // 		for (int j=0; j<stepSize; j++)
	    // 		    ichunk+=((Integer)input.get((stepSize*i)+j)).intValue();
	    // 		avgData.add(new Integer(ichunk/stepSize));
	    // 	    }
	    // 	    break;
	default:
	    break;
	}
	return avgData;
    }

    private int scaleToFit(double d){
	//receive values in range 0..1 and scale for drawing on panel

	return (int)(((1.0-d) * (double)(getDrawHeight()))) +borderSpace+selectBarHeight;
    }
    
    private double inverseScaleToFit(int i){
	return (double)(i-borderSpace)/(double)getDrawHeight();
    }

    private void setLabels(){

	int labWidth = (new Integer(numObjs)).toString().length() * charWidth;//width in pixels of longest number on x axis
	//int labelSpacing = labWidth+10;
	numLabels= getDrawWidth() / labWidth;//(numObjs*spacing)/labWidth;
	if (numLabels>0){
	    if (numObjs>numLabels)
		labelEvery = (int)(1.2 * (double)((numObjs/stepSize)/numLabels))+1;
	    else 
		labelEvery = 1;
	    labels = new ArrayList();   
	    //numLabels = ((this.getWidth()-axisHeight-keyWidth- (2*borderSpace))/labelSpacing);
	    Integer label;
	    numLabels = (numObjs/stepSize)/labelEvery;
	    for (int i=0; i<numLabels+1; i++){
		label = new Integer(i*labelEvery*stepSize);
		labels.add(label.toString());
	    }
	}
	yAxis = new ArrayList();
	int numYs = getDrawHeight() / ((int)(((double)charHeight)*2.5));
	for (int i=0; i< numYs; i++){
	    yAxis.add(new Double(overallMin+ (i*((overallMax-overallMin)/numYs))));
	}

    }


    private void drawAxes(Graphics g)  {

	//	setWidth();

	g.setColor(Color.black);

	int x,y; 
	x = y = borderSpace;
       
	g.setColor(Color.black);

	// x axis

	if (this.getWidth()>(borderSpace+keyWidth+axisHeight))
	    g.drawLine(borderSpace+axisHeight, getDrawHeight()+borderSpace+selectBarHeight, ((((numObjs/stepSize)-1)+(numObjs%2))*spacing)+borderSpace+axisHeight, getDrawHeight()+borderSpace+selectBarHeight);//x   
	if (numLabels>0)
	    for (int i=1; i<numLabels+1; i++){
		g.drawLine(axisHeight+borderSpace+(i*spacing*labelEvery), getDrawHeight()+borderSpace+selectBarHeight, axisHeight+borderSpace+(i*spacing*labelEvery), getDrawHeight()+borderSpace+3+selectBarHeight);
		if (labels.size()>0)
		    g.drawString((String)labels.get(i),axisHeight+borderSpace+(i*spacing*labelEvery), getDrawHeight()+borderSpace+15+selectBarHeight);
	    }
	if (getDrawWidth()/numObjs>1){
	    for (int i=1; i<numObjs; i++){
		g.drawLine(axisHeight+borderSpace+(i*spacing), getDrawHeight()+borderSpace+selectBarHeight, axisHeight+borderSpace+(i*spacing), getDrawHeight()+borderSpace+1+selectBarHeight);
	    }
	}
	
	if (overallMax * overallMin < 0){// if different signs, draw an axis at 0
	    int zero = scaleToFit((0.0-overallMin)/(overallMax-overallMin));
	    if (this.getWidth()>(borderSpace+keyWidth+axisHeight))
		g.drawLine(borderSpace+axisHeight,  zero, (((numObjs/stepSize)-1)*spacing)+borderSpace+axisHeight,zero);
	    g.drawString("0", axisHeight+borderSpace-charWidth-2, zero+5);
	}


	// y axis
	g.drawLine(borderSpace+axisHeight, borderSpace+selectBarHeight, borderSpace+axisHeight, getDrawHeight()+borderSpace+selectBarHeight);//y
	String thisS = (new Double(overallMax)).toString();
	int end = thisS.length();
	if (end>5) end = 5;
	g.drawString(""+thisS.substring(0,end), borderSpace, borderSpace+selectBarHeight+5);
	thisS = (new Double(overallMin)).toString();
	end = thisS.length();
	if (end>5) end = 5;
	g.drawString(""+thisS.substring(0,end), borderSpace, getDrawHeight()+selectBarHeight+borderSpace+5);

	//	int ySpacing = getDrawHeight()/(yAxis.size()-1);
	int yPos;
	for (int i=1; i<yAxis.size(); i++){
	    yPos = scaleToFit((((Double)yAxis.get(i)).doubleValue()-overallMin)/(overallMax-overallMin));
	    g.drawLine(borderSpace+axisHeight-2, yPos, borderSpace+axisHeight,yPos);
	    g.drawString((((Double)yAxis.get(i)).toString()).substring(0,4),borderSpace, yPos+5);
	}

	// for (int i=0; i<3; i++){
// 	    g.drawLine(axisHeight+borderSpace+(i*((this.getWidth()-axisHeight)/numLabels)), getDrawHeight()+borderSpace, axisHeight+borderSpace+(i*((this.getWidth()-axisHeight)/numLabels)), getDrawHeight()+borderSpace+3);
// 	    g.drawString((String)labels.get(i),axisHeight+borderSpace+(i*((this.getWidth()-axisHeight)/numLabels)), getDrawHeight()+borderSpace+15);
// 	}
	
	int numDs = numDims/2; if (numDims%numDs==1)numDs++;
	if((this.getWidth()-2*borderSpace) > 3*keyWidth && (this.getHeight()-2*borderSpace) > ((numDs/2)*eachKeyWidth + numDs*eachSep))
	    drawKey(g);
	
    }

    
    public void drawLines(Graphics g){

	HashSet sel = selected.getSelectedIndices();
		
	int x, y, oldx, oldy; x = oldx = y = oldy = borderSpace;
	int numToDraw=0;

	ArrayList thisDim;
	if (spacing>0)
	    for (int j=0; j<numDims; j++){
		if (((Boolean)drawDims.get(j)).booleanValue()){
		    g.setColor((Color)colours.get(j));
		    thisDim = (ArrayList)scaledDims.get(j);
		    //numToDraw = miConnected?(((numObjs/stepSize))-(((numObjs/stepSize))%(miStepSize/stepSize)))-1:(numObjs/stepSize)-1;

		    numToDraw = miConnected?(miDisjoint?(((miStepSize*(miObjs))/stepSize)-1):((numObjs/stepSize)-1)):(numObjs/stepSize)-1;
		    
		    double stP, endP;
		    for (int i=0; i<numToDraw; i++){
			stP = ((Double)thisDim.get(i)).doubleValue();
			endP = ((Double)thisDim.get(i+1)).doubleValue();
			if (stP!=Double.MIN_VALUE && endP!=Double.MIN_VALUE)
			    if (((Boolean)avgSelection.get(i)).booleanValue() || miConnected){

				g.drawLine(axisHeight+borderSpace+(i*spacing), scaleToFit(stP), axisHeight+borderSpace+((i+1)*spacing), scaleToFit(endP));
			    }else{
				g.setColor(Color.gray);
				g.drawLine(axisHeight+borderSpace+(i*spacing), scaleToFit(stP), axisHeight+borderSpace+((i+1)*spacing), scaleToFit(endP));
				g.setColor((Color)colours.get(j));
			    }
		    }
// 		    for (int i=0; i<miObjs;i++){
// 			Color f = g.getColor();
// 			g.setColor(Color.blue);
// 			g.drawLine(axisHeight+borderSpace+(((i*miStepSize)/stepSize)*spacing), borderSpace,axisHeight+borderSpace+(((i*miStepSize)/stepSize)*spacing),getDrawHeight()+selectBarHeight);
// 			g.setColor(f);
// 		    }
		    
		}
	    }
    }
    
    private void drawExtremes (Graphics g){
	
	if (spacing>0)
	    for (int j=0; j<numDims; j++){
		if (((Boolean)drawDims.get(j)).booleanValue()){
		    g.setColor((Color)colours.get(j));

		    ArrayList theseHighExtremes=(ArrayList)extremeHighs.get(j);
		    ArrayList theseHighExtremePos=(ArrayList)extremeHighPos.get(j);
		    ArrayList theseLowExtremes=(ArrayList)extremeLows.get(j);
		    ArrayList theseLowExtremePos=(ArrayList)extremeLowPos.get(j);
		    int drawX; double extremeVal;
		    for (int i=0; i<theseHighExtremes.size(); i++){
			drawX = ((Integer)theseHighExtremePos.get(i)).intValue();
			extremeVal = ((Double)theseHighExtremes.get(i)).doubleValue();
			g.fillRect(axisHeight+borderSpace+(drawX*spacing)-1,scaleToFit(extremeVal),3,2);
			drawX = ((Integer)theseLowExtremePos.get(i)).intValue();
			extremeVal = ((Double)theseLowExtremes.get(i)).doubleValue();
			g.fillRect(axisHeight+borderSpace+(drawX*spacing)-1,scaleToFit(extremeVal),3,2);
		    }
		}
	    }
    }
    
    private void mutualInfoSelectInit(){
	//num poss overlaps: ie pixelWidth-1
	int numOver=0;
	try{
	    numOver = ((miStepSize/stepSize))/miOverlap; 
	}catch (Exception e){System.out.println("Exception "+miStepSize+" "+stepSize+" "+miOverlap);}
	
	numOver++;
	if (numOver<1)
	    numOver=1;

	//set opacity/translucence so darker if lots overlap
	colourOneObj = 255/numOver;
	
	miColours = new ArrayList();
	for (int i=0; i<numObjs/stepSize;i++)//every obj to draw has a colour
	    miColours.add(new Color(255,0,0,0));
	
	
	/*
	  loop through all MI objs (N-MIstepSize if total overlap)
	    if selected
	      darken (more opaque) rectangle of full width of MI obj
	      --i.e. loop width of obj in pixels
                        getOpaque += colourOneObj
	  drawLines (some system to ensure not same colour as colourOneObj bg?)
	*/   
    }
	

    private void filter(){
	theSelection.removeAll(deselect);
    }



    private void setDims(){
	// in Par Coords, scale each dim val to 0..1 an axis
	// Here, set 0..1 on all dims


	ArrayList maxVals = dataItems.getMaxima();
	ArrayList minVals = dataItems.getMinima();
	ArrayList types = dataItems.getTypes();

	ArrayList numericDims = dataItems.getNumericDims();

	overallMax=Double.MIN_VALUE;
	overallMin= Double.MAX_VALUE;

	Object thisM;
	for (int i=0; i<maxVals.size(); i++){
	    if (((Boolean)drawDims.get(i)).booleanValue()){
		thisM = maxVals.get(i);
		if (thisM instanceof Double && ((Double)thisM).doubleValue()>overallMax)
		    overallMax = ((Double)thisM).doubleValue();
		else if (thisM instanceof Integer && (double)((Integer)thisM).intValue() > overallMax)
		    overallMax = (double)((Integer)thisM).intValue();
		
		thisM = minVals.get(i);
		if (thisM instanceof Double && ((Double)thisM).doubleValue()<overallMin)
		    overallMin = ((Double)thisM).doubleValue();
		else if (thisM instanceof Integer && (double)((Integer)thisM).intValue() < overallMin)
		    overallMin = (double)((Integer)thisM).intValue();
	    }
	}

	overallMin = overallMin<0.0?(overallMin*1.03):(overallMin*0.97); // get extreme vals away from axes
	overallMax *= 1.03;

	processedValues = new ArrayList();
	nullValues = new ArrayList();	    
	for (int i =0; i<types.size(); i++){
	    nullValues.add(new ArrayList());
	    if (((Boolean)numericDims.get(i)).booleanValue()){ 
		processedValues.add(processDimension(i,(ArrayList)dataItems.getColumn(i), ((Integer)types.get(i)).intValue(), new Double(overallMax), new Double(overallMin))); }
	}
	    
    }
    
    private ArrayList processDimension(int dimNum, ArrayList values, int type, Object max, Object min){
	// get values 0..1 on each dimension for drawing lines
	// keep this process separate from drawing, so won't have to recompute this at repaint

	ArrayList thisDim = new ArrayList();
	double thisMin, dimWidth;
	switch (type) {
	case DataItemCollection.DOUBLE:
	    thisMin = ((Double)min).doubleValue();
	    dimWidth = ((Double)max).doubleValue() - thisMin;

	    for (int i=0; i<values.size(); i++){
		if (values.get(i) != null)
		    thisDim.add(new Double(((((Double)values.get(i)).doubleValue() - thisMin) / dimWidth))); 
		else
		    ((ArrayList)nullValues.get(dimNum)).add(new Integer(i));
	    }
	    
	    
	    break;
	case DataItemCollection.INTEGER:
	    thisMin = (double)((Integer)min).intValue();
	    dimWidth = (double)(((Integer)max).intValue()) - thisMin;
	    for (int i=0; i<values.size(); i++)
		thisDim.add(new Double((((double)(((Integer)values.get(i)).intValue()) - thisMin) / dimWidth)));
	    break;
	default:
	    break;
	}	    
	
	return thisDim;
    }


    public void paint(Graphics g){
	if (numObjs>0){
	    if ((setWidth != this.getWidth())  ||  setHeight != this.getHeight())
		initAxes();
	    g.setColor(Color.gray);
	    g.fillRect(0, 0, axisHeight+borderSpace, this.getHeight());
	    g.fillRect(0, 0, this.getWidth(),borderSpace+selectBarHeight);
	    g.fillRect(0, getDrawHeight(), this.getWidth(), this.getHeight()-getDrawHeight());

	    //handle selection
	    if (!miConnected){
		setAvgSelect();
		for (int i=0; i<(numObjs/stepSize)-1; i++)
		    if (((Boolean)avgSelection.get(i)).booleanValue())
			g.fillRect(axisHeight+borderSpace+(i*spacing), borderSpace+selectBarHeight,spacing,getDrawHeight());
		    else{
			g.setColor(Color.darkGray);
			g.fillRect(axisHeight+borderSpace+(i*spacing), borderSpace,spacing,getDrawHeight()+selectBarHeight);
			g.setColor(Color.gray);
		    }
	    }else{
		if (miDisjoint){
		    //		    for (int i=0; i< (miObjs); i++){
		    //g.fillRect(axisHeight+borderSpace+(i*((spacing*miStepSize)/stepSize)), borderSpace,spacing*miStepSize/stepSize,getDrawHeight()+selectBarHeight);
		    int numInMIobj = miStepSize/stepSize;
		    int numToDraw = (miStepSize*miObjs)/stepSize;
		    int origRectSize = spacing*(miStepSize/stepSize); 
		    int rectSize;

		    for (int i=0; i<miObjs;i++){ //numToDraw; i++){
			
			rectSize = origRectSize;
			if (selected.getState(i))//(((Boolean)miSelected.get(i)).booleanValue()){
			    g.setColor(Color.red);		  
			else{
			    g.setColor(Color.darkGray);
			}
			if (((((i+1)*miStepSize)/stepSize)*spacing)-((((i*miStepSize)/stepSize)*spacing)+rectSize)>0)
			    rectSize=origRectSize+1;
			
			g.fillRect(axisHeight+borderSpace+(((i*miStepSize)/stepSize)*spacing), borderSpace,rectSize,getDrawHeight()+selectBarHeight);
			// for (int j=0; j< numInMIobj; j++)
// 			    g.fillRect(axisHeight+borderSpace+(i*spacing*numInMIobj)+(j*spacing), borderSpace,spacing,getDrawHeight()+selectBarHeight);
			
		    }
		    
		    g.setColor(Color.darkGray);
		    int remain = getDrawWidth()-(((numObjs/stepSize))*spacing);
		    g.fillRect((axisHeight+borderSpace+(((miObjs*miStepSize)/stepSize)-1)*spacing)+1, borderSpace,((this.getWidth()-borderSpace-keyWidth-remain)-axisHeight+borderSpace+((miObjs*miStepSize)/stepSize)*spacing),getDrawHeight()+selectBarHeight);
		    
		}else{
		    //background, because some translucent
		    g.setColor(Color.gray);
		    g.fillRect(axisHeight+borderSpace, borderSpace,getDrawWidth(),getDrawHeight()+selectBarHeight);
		    
		    setMIcolours();
		    for (int i=0; i<(numObjs/stepSize); i++){
			g.setColor((Color)miColours.get(i));
			g.fillRect(axisHeight+borderSpace+(i*spacing), borderSpace,spacing,getDrawHeight()+selectBarHeight);
		    }
			
		}
	    }
	    g.setColor(Color.gray);
	    int remain = getDrawWidth()-(((numObjs/stepSize)-1)*spacing);
	    g.fillRect(this.getWidth()-borderSpace-keyWidth-remain, 0, borderSpace+keyWidth+remain, this.getHeight());
	    drawLines(g);
	    drawAxes(g);
	    if (showExtremes)
		drawExtremes(g);
	    drawSelectBar(g);
	    if (miConnected){
		int width = (miStepSize/stepSize) * spacing;//
		if (width>0)
		    drawMutualInfoSize(g,width);
	    }
	}
    }

    private void setMIcolours(){
	//miColours reps each orig obj. Set to appropriate colour by overlapping miObjs

	mutualInfoSelectInit();
	int thisObj;

	for (int i=0; i<miObjs; i++){//for all MI objs
	    thisObj=i*miOverlap;
	    if (selected.getState(i))
		for (int j=0; j<miStepSize/stepSize; j++){ //for all constituent orig objs in this concat MI obj
		    int alpha = ((Color)miColours.get(thisObj)).getAlpha()+colourOneObj;
		    if (alpha>255){
			alpha = 255;
			System.out.println("high alpha"+alpha);
		    }
		    miColours.set(thisObj,new Color(((Color)miColours.get(thisObj)).getRed(),((Color)miColours.get(thisObj)).getGreen(),((Color)miColours.get(thisObj)).getBlue(),alpha));
		    thisObj++;	
		}
	}
    }

    private void drawMutualInfoSize(Graphics g,int width){
	g.setColor(Color.red);
	int leftP = axisHeight+borderSpace;
	int yP = this.getHeight()-20;
	g.drawLine(leftP,yP, leftP+width,yP);
	g.drawLine(leftP,yP, leftP,yP-3);
	g.drawLine(leftP+width,yP, leftP+width,yP-3);

	if (miDisjoint)
	    leftP+=width;
	else
	    leftP+=((width/miStepSize)*miOverlap);
	yP+=4;

	g.drawLine(leftP,yP, leftP+width,yP);
	g.drawLine(leftP,yP, leftP,yP-3);
	g.drawLine(leftP+width,yP, leftP+width,yP-3);

	String MImessage = "Mutual Info steps";
	if (MImessage.length() * charWidth <= getDrawWidth())
	    g.drawString(MImessage,leftP-(width/2),yP+charHeight+3);
    }

    private void drawSelectBar(Graphics g){
	int l = arrowPosns.getLeft();
	int r = arrowPosns.getRight();
	int y = selectBarHeight/2;

	g.setColor(Color.black);
	if (this.getWidth()>(borderSpace+keyWidth+axisHeight)){
	    g.drawLine(axisHeight+borderSpace, y, (((numObjs/stepSize)-1)*spacing)+borderSpace+axisHeight,y);
	    g.setColor(Color.green); //selected bit
	    g.drawLine(l, y, r, y);	
	    
	}

	l-=triangleSize;
	r+= triangleSize;

	g.setColor(Color.black);
	if (draggingArrow==0)
	    g.setColor(Color.green);
	g.drawLine(l,y-triangleSize,l,y+triangleSize);
	g.drawLine(l,y-triangleSize,l+triangleSize,y);
	g.drawLine(l,y+triangleSize,l+triangleSize,y);


	g.setColor(Color.black);
	if (draggingArrow==1)
	    g.setColor(Color.green);
	g.drawLine(r,y-triangleSize,r,y+triangleSize);
	g.drawLine(r,y-triangleSize,r-triangleSize,y);
	g.drawLine(r,y+triangleSize,r-triangleSize,y);
    }

    
    public void update(){
	repaint();
    }

    /**
     *  Set the selection 
     *  Boolean value indicates whether normal selection of objs, or of mutual info concat'd objs
     */
    
    public void setSelection(Collection selection,boolean mutualInfoSelection){
	theSelection = 	(HashSet)selection;
	if (!mutualInfoSelection)
	    filter();
    }
    

    /**
     * Return the selection handler
     */
    
    public SelectionHandler getSelectionHandler()   {
	return selected;
    }
	

    /**
     * Returns the indices of the selected items in this object
     * 
     *
     * @return The selection
     */
     
    public Collection getSelection(){
	return theSelection;
    }

    public ArrayList getSelectedAL(){
	return selectedAL;
    }


    private Object pixelsToValue(int type, int dim, int pixels){
	double thisMin, dimWidth = 0;
	Object max = ((ArrayList)dataItems.getMaxima()).get(dim);
	Object min = ((ArrayList)dataItems.getMinima()).get(dim);

	switch(type){
	case DataItemCollection.DOUBLE:

	    thisMin = ((Double)min).doubleValue();
	    dimWidth = ((Double)max).doubleValue() - thisMin;
	    
	    return new Double((inverseScaleToFit(pixels) * dimWidth) + thisMin);
	    
	    //	    break;
	case DataItemCollection.INTEGER:
	    thisMin = (double)((Integer)min).intValue();
	    dimWidth = (double)(((Integer)max).intValue()) - thisMin;
	    return new Integer((int)((inverseScaleToFit(pixels) * dimWidth) + thisMin));
	    //break;
	default:
	    break;
	}
	return new Object();
    }

    // private Collection getDeselectedObjs(int type, int dim, Object top,Object bottom){
// 	Collection c = new ArrayList();
// 	fullSet = new HashSet();
// 	HashSet deselected = new HashSet();

// 	switch(type){
// 	case DataItemCollection.DOUBLE:
// 	    double dtop = ((Double)top).doubleValue();
// 	    double dbottom = ((Double)bottom).doubleValue();
// 	    double dthisVal; 
// 	    for (int i=0; i< numObjs; i++){
// 		dthisVal = ((Double)((DataItem)dataItems.getDataItem(i)).getValue(dim)).doubleValue(); 
// 		if (dthisVal>=dbottom && dthisVal<=dtop)
// 		    c.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
// 		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
// 		deselected.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
// 	    }
// 	    break;
// 	case DataItemCollection.INTEGER:
// 	    int itop = ((Integer)top).intValue();
// 	    int ibottom = ((Integer)bottom).intValue();
// 	    int ithisVal;
// 	    for (int i=0; i< numObjs; i++){
// 		ithisVal = ((Integer)((DataItem)dataItems.getDataItem(i)).getValue(dim)).intValue();
// 		if (ithisVal>=ibottom && ithisVal<=itop)
// 		    c.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
// 		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
// 		deselected.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
// 	    }
// 	    break;
// 	default:
// 	    break;
// 	} 
// 	deselected.removeAll(c);
// 	return deselected;
//     }
    


    public void handleSelection(int left, int right){
	
	//receive pixels, work out the number of axis units from left and right
	int leftVal = (left-axisHeight-borderSpace)/spacing;
	int rightVal = (right-axisHeight-borderSpace)/spacing;

	theSelection = new HashSet();
	ArrayList desels = new ArrayList();
	selectedAL = new ArrayList();
	
	fullSet = new HashSet();
		
	int leftMarker, rightMarker;
	if (miConnected){
	    if (miDisjoint){
		
		// for adding all objs
		//leftMarker = (((leftVal*stepSize)/miStepSize)*miStepSize)+miStepSize;
		//rightMarker = (((rightVal*stepSize)/miStepSize)*miStepSize);

		leftMarker = ((leftVal*stepSize)/miStepSize);
		if ((leftVal*stepSize)%miStepSize >0 || (((leftVal*stepSize)%miStepSize ==0)&&((left-axisHeight-borderSpace)%spacing) > 0)) leftMarker++;

		rightMarker = ((rightVal*stepSize)/miStepSize);
		if ((rightVal*stepSize) >= numObjs-1)
		    rightMarker = miObjs;

	    } else {
		leftMarker = (leftVal*stepSize)/miOverlap;
		rightMarker = (((rightVal+1)*stepSize-miStepSize)/miOverlap)+1; 
		if (rightMarker >miObjs)rightMarker = miObjs;
		if (rightMarker<leftMarker) rightMarker = leftMarker;
	    }
	    miSelected=new ArrayList();
	    for (int i=0; i< leftMarker; i++){
		//miSelected.add(new Boolean(false));
		desels.add(new Integer(((DataItem)miDataItems.getDataItem(i)).getID()));
		fullSet.add(new Integer(((DataItem)miDataItems.getDataItem(i)).getID()));
	    }
	    
	    for (int i=leftMarker; i<rightMarker; i++){
		//		    miSelected.add(new Boolean(true));
		fullSet.add(new Integer(((DataItem)miDataItems.getDataItem(i)).getID()));
		selectedAL.add(new Integer(((DataItem)miDataItems.getDataItem(i)).getID()));
	    }
	    for (int i=rightMarker; i< miObjs; i++){
		//		    miSelected.add(new Boolean(false));		
		desels.add(new Integer(((DataItem)miDataItems.getDataItem(i)).getID()));
		fullSet.add(new Integer(((DataItem)miDataItems.getDataItem(i)).getID()));
	    }
	}else{
	    leftMarker = leftVal*stepSize;
	    rightMarker = rightVal*stepSize;
	
	    for (int i=0; i< leftMarker; i++){
		desels.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
	    }
	    for (int i=leftMarker; i<rightMarker; i++){
		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		selectedAL.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
	    }
	    for (int i=rightMarker; i< numObjs; i++){
		desels.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
	    }
	}   
	deselect = new HashSet(desels);
	
	theSelection.addAll(fullSet);//add everything
	theSelection.removeAll(deselect);
	
	selected.updateSelection();

	
	if(!miConnected){	    
	    setAvgSels();
	}

	visMod.sendSelection();
    }


   
    public void mousePressed(MouseEvent e){
 	int x = e.getX();
 	int y = e.getY();
 	draggingArrow = -1; 
	if (Math.abs(y-(selectBarHeight/2)) < permittedAccuracy)
	    draggingArrow = arrowPosns.hitArrow(x);
	else
	    return;
	repaint();
    }

    public void mouseClicked(MouseEvent e){	
	int x = e.getX();
 	int y = e.getY();
	for (int i=0; i<numDims; i++){
	    if ((x > (this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2))) && (x<(this.getWidth()-borderSpace-((i+1)%2+1)*(keyWidth/2))+eachKeyWidth) && (y>(borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep))) && (y<(borderSpace + ((i/2)*eachKeyWidth + (i/2)*eachSep))+eachKeyWidth)){
		if (((Boolean)drawDims.get(i)).booleanValue())
		    drawDims.set(i, new Boolean(false));
		else
		    drawDims.set(i, new Boolean(true));
		setDims();
		initAxes();
		repaint();
	    }
	}
    }

    public void mouseReleased(MouseEvent e){
// 	if (draggingArrow>=0)
// 	    handleSelection(dataItems.getNumericDimNumber(draggingAxis), ((ArrowXs)arrowPosns.get(draggingAxis)).getBottom()-triangleSize, ((ArrowXs)arrowPosns.get(draggingAxis)).getTop()+triangleSize);
 	if (draggingArrow>-1)
	    visMod.sendFocus(drawDims);
	draggingArrow = -1;
	repaint();
    }
    
    public void mouseDragged(MouseEvent e){

	int x = e.getX();
	if (draggingArrow==0 && x>=axisHeight+borderSpace && x< arrowPosns.getRight())
	    arrowPosns.setLeft(x);
	else if(draggingArrow==1 && x<=((((numObjs/stepSize)-1)*spacing)+borderSpace+axisHeight) && x>= arrowPosns.getLeft()){
	    arrowPosns.setRight(x);
	}

	handleSelection(arrowPosns.getLeft(), arrowPosns.getRight());
	//	initAxes();
	repaint();
    }    

    public void mouseMoved(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void selectionChanged(SelectionHandler select){}

    public Collection getDeselection(){
	return null;
    }	

    /**
     * Sets this selectable object to select all its items
     *
     */	
    public void selectAll()	{

    }
    
    /**
     * Sets this selectable item to select none of it items
     *
     */
    
    public void selectNone()    {
	theSelection.clear();
    }
    
}

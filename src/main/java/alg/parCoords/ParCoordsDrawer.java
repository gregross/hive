/** 
 * Algorithmic test bed
 *  
 * Parallel Coordinates Drawer
 *  
 *  @author Alistair Morrison
 */ 

package alg.parCoords;

import data.DataItemCollection;
import data.DataItem;
import parent_gui.*;
import alg.ParallelCoords;

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

public class ParCoordsDrawer extends JPanel implements Selectable, SelectionChangedListener, MouseListener, java.io.Serializable, MouseMotionListener{


    private transient Mdi mdiForm;
	
    private DataItemCollection dataItems;
    
    private ParCoordsHolder parCoordsHolder;
    private ParallelCoords visMod;
 	
    protected Set theSelection;
    private SelectionHandler selected; 
    
    private final int permittedAccuracy = 5;

    private int numDims;
    private int numObjs;
    private ArrayList dimNames;
    private ArrayList dimSpacings;

    private int borderSpace;
    private int draggingAxis = -1;
    private int draggingArrow = -1;
    private int dragStartX;
    private ArrayList oldSpacings;
    private int squashDim;
    private int squashMovement = 0;
    
    private ArrayList arrowPosns;

    private HashSet fullSet;
    private ArrayList deselecteds;
    private ArrayList altered;
    
    private int markHeight=20;

    private int setWidth;

    private ArrayList drawValues;

    private int charHeight = 9;
    private int charWidth = 9;
    private final int triangleSize = 7;

    public ParCoordsDrawer(DataItemCollection dataItems, Mdi mdiForm, ParCoordsHolder parCoordsHolder, ParallelCoords visMod) {

	this.mdiForm = mdiForm;
	this.dataItems = dataItems;
	this.parCoordsHolder = parCoordsHolder;
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
	
	drawValues = new ArrayList();

	if (numObjs>0){
	    setDims();
	    setWidth = this.getWidth();
	}
	addMouseListener(this);
	addMouseMotionListener(this);
		
    }

    private void setWidth(){
	borderSpace = this.getWidth()/50;
    }

    private void initAxes() {
	int g = scaleToFit(0.2);
	setWidth();
	setWidth = this.getWidth();
	arrowPosns = new ArrayList();
	theSelection = new HashSet();

	// default spacing
	int spacing=0;
	if (numDims > 0)
	    spacing =  getSpacing();
	dimSpacings = new ArrayList();
	altered = new ArrayList();
	deselecteds = new ArrayList();
	for (int i=0; i<numDims; i++){
	    dimSpacings.add(new Integer(spacing));
	    altered.add(new Boolean(false));
	    int arrowPlace = borderSpace+markHeight-triangleSize;
	    if (arrowPlace<0) arrowPlace = 0;
	    arrowPosns.add(new ArrowYs(arrowPlace, this.getHeight()-arrowPlace,triangleSize));
	    altered.add(new Boolean(false));
	    deselecteds.add(new HashSet());
	}
	
	theSelection = selected.getAll();
	selected.updateSelection();
	visMod.sendSelection();
	//init(0);

    }

    private int getSpacing(){
	return (this.getWidth()-(2*borderSpace)-10)/(numDims-1);
    }

    
    private int scaleToFit(double d){
	//receive values in range 0..1 and scale for drawing on panel

	return ((int)((1.0-d) * (double)(getDrawHeight())))+borderSpace+markHeight;
    }

    private int getDrawHeight(){
	return (this.getHeight() - (2*borderSpace) - (2*markHeight));
    }

    private double inverseScaleToFit(int i){
	return (1.0-((double)(i-borderSpace-markHeight)/(double)(getDrawHeight())));
    }


    private void drawAxes(Graphics g)  {
	setWidth();

	g.setColor(Color.black);
	
	int x,y; 
	x = y = borderSpace;
	System.out.println(numDims+"*");
	for (int i=0; i<numDims; i++){
	    g.setColor(Color.black);
	    if (draggingAxis==i) 
		g.setColor(Color.green);
	    drawTriangles(g,x,((ArrowYs)arrowPosns.get(i)).getTop(),((ArrowYs)arrowPosns.get(i)).getBottom());

	    if (draggingArrow>-1)
		g.setColor(Color.black);
	    g.drawLine(x, y+markHeight, x, y+getDrawHeight()+ markHeight);
	    x += ((Integer)dimSpacings.get(i)).intValue(); 
	}

	ArrayList names = dataItems.getFields();

	//marks
	x = y = borderSpace; String thisTop="",thisBottom="",thisLabel="";
	int numToShow = ((this.getWidth()-(2*borderSpace)-10)/numDims)/charWidth+1, thisNTS;
	int typeID; boolean drawLabels;
	for (int j=0; j<numDims; j++){
	    drawLabels=true;
	    if (j==draggingAxis)
		g.setColor(Color.green);
	    else
		g.setColor(Color.black);
	    thisNTS = numToShow; 
	    thisLabel = (String)names.get(dataItems.getNumericDimNumber(j));
	    if (thisNTS>thisLabel.length()) thisNTS = thisLabel.length();
	    g.drawString(""+thisLabel.substring(0,thisNTS),x,borderSpace+2);
	    g.setColor(Color.blue);
	    typeID  = ((Integer)dataItems.getTypes().get(dataItems.getNumericDimNumber(j))).intValue(); System.out.println("a"+typeID+","+DataItemCollection.INTEGER);
	    if (typeID==DataItemCollection.DOUBLE){
		thisTop = ((Double)(pixelsToValue(typeID,dataItems.getNumericDimNumber(j),((ArrowYs)arrowPosns.get(j)).getTop()+triangleSize))).toString();
		thisBottom = ((Double)(pixelsToValue(typeID,dataItems.getNumericDimNumber(j),((ArrowYs)arrowPosns.get(j)).getBottom()-triangleSize))).toString(); System.out.print("Double");
	    } else if (typeID==DataItemCollection.INTEGER){
		thisTop = ((Integer)(pixelsToValue(typeID,dataItems.getNumericDimNumber(j),((ArrowYs)arrowPosns.get(j)).getTop()+triangleSize))).toString();
		thisBottom = ((Integer)(pixelsToValue(typeID,dataItems.getNumericDimNumber(j),((ArrowYs)arrowPosns.get(j)).getBottom()-triangleSize))).toString(); System.out.print("Int");

	    } else
		drawLabels = false;
	    if (drawLabels){
		thisNTS = numToShow;
		if (thisNTS>thisTop.length()) thisNTS = thisTop.length();
		g.drawString(thisTop.substring(0,thisNTS),x,borderSpace+12);
		thisNTS=numToShow;
		if (thisNTS>thisBottom.length()) thisNTS = thisBottom.length();
		g.drawString(thisBottom.substring(0,thisNTS),x,getDrawHeight()+borderSpace+(2*markHeight));
		x += ((Integer)dimSpacings.get(j)).intValue();
	    }
	}
	
    }

    private void drawTriangles(Graphics g,int x, int y, int yDown){


	g.drawLine(x-triangleSize,y,x+triangleSize,y);
	g.drawLine(x-triangleSize,y,x,y+triangleSize);
	g.drawLine(x+triangleSize,y,x,y+triangleSize);

	g.drawLine(x-triangleSize,yDown,x+triangleSize,yDown);
	g.drawLine(x-triangleSize,yDown,x,yDown-triangleSize);
	g.drawLine(x+triangleSize,yDown,x,yDown-triangleSize);
    }



    public void drawLines(Graphics g){

	HashSet sel = selected.getSelectedIndices();

	int spacing=0;
	int x, y, oldx, oldy; x = oldx = y = oldy = borderSpace;

	g.setColor(Color.blue);
	//for (int i=0; i<numObjs; i++){
	    //  theseValues = ((DataItem)dataItems.getDataItem(i)).getValues();
	    
	    //x = borderSpace;
	    //y = ((((Double)theseValues.get(0)).doubleValue()-thisMin )/ dimWidth) * (this.getHeight()- borderSpace) -borderSpace; 
	//  for (int j=1; j<numDims; j++){
		
	//if (((Integer)types.get(j)).intValue() == DataItemCollection.DOUBLE ) {// || ((Integer)types.get(j)).intValue()==DataItemCollection.Integer)
	//	    oldx = x;
	//    oldy = y;
	//    x += spacing;
	//    y = ((((Double)theseValues.get(j)).doubleValue()-thisMin) / dimWidth) * (this.getHeight()- borderSpace) -borderSpace;
		    
		
	ArrayList thisDim;
	ArrayList nextDim;
	
	//deselecteds
	g.setColor(Color.gray);
	for (int j=0; j<numDims-1; j++){
	    thisDim = (ArrayList)drawValues.get(j);
	    nextDim = (ArrayList)drawValues.get(j+1);
	    spacing = ((Integer)dimSpacings.get(j)).intValue();
	    for (int i=0; i<numObjs; i++)
		if (!selected.getState(i))
		    g.drawLine(x, scaleToFit(((Double)thisDim.get(i)).doubleValue()), x+spacing, scaleToFit(((Double)nextDim.get(i)).doubleValue()));
	    x += spacing;
	}

	//selecteds
	x = oldx = y = oldy = borderSpace;
	g.setColor(Color.blue);
	for (int j=0; j<numDims-1; j++){
	    thisDim = (ArrayList)drawValues.get(j);
	    nextDim = (ArrayList)drawValues.get(j+1);
	    spacing = ((Integer)dimSpacings.get(j)).intValue();
	    for (int i=0; i<numObjs; i++)
		if (selected.getState(i))
		    g.drawLine(x, scaleToFit(((Double)thisDim.get(i)).doubleValue()), x+spacing, scaleToFit(((Double)nextDim.get(i)).doubleValue()));
	    x += spacing;
	}


    }

    private void filter(){
	for (int i=0; i<numDims; i++)//filter on all dims
	    if (((Boolean)altered.get(i)).booleanValue())
		theSelection.removeAll(((HashSet)deselecteds.get(i)));
    }



    private void setDims(){
	ArrayList maxVals = dataItems.getMaxima();
	ArrayList minVals = dataItems.getMinima();
	ArrayList types = dataItems.getTypes();
	
	ArrayList numericDims = dataItems.getNumericDims();

	for (int i =0; i<types.size(); i++)
	    if (((Boolean)numericDims.get(i)).booleanValue())
		drawValues.add(processDimension((ArrayList)dataItems.getColumn(i), ((Integer)types.get(i)).intValue(), maxVals.get(i), minVals.get(i)));
	
    }
    
    private ArrayList processDimension(ArrayList values, int type, Object max, Object min){
	// get values 0..1 on each dimension for drawing lines
	// keep this process separate from drawing, so won't have to recompute this at repaint

	ArrayList thisDim = new ArrayList();
	double thisMin, dimWidth;
	switch (type) {
	case DataItemCollection.DOUBLE:
	    thisMin = ((Double)min).doubleValue();
	    dimWidth = ((Double)max).doubleValue() - thisMin;
	    for (int i=0; i<values.size(); i++){
		thisDim.add(new Double(((((Double)values.get(i)).doubleValue() - thisMin) / dimWidth)));
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
	    if (setWidth != this.getWidth())
		initAxes();
	    g.clearRect(0, 0, this.getWidth(), this.getHeight());
	    drawLines(g);
	    drawAxes(g);
	}
    }

    public void update(){
	repaint();
    }

    /**
     *  Set the selection 
     */
    
    public void setSelection(Collection selection)   {
	theSelection = 	(HashSet)selection;
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

    private Collection getDeselectedObjs(int type, int dim, Object top,Object bottom){
	Collection c = new ArrayList();
	fullSet = new HashSet();
	HashSet deselected = new HashSet();

	switch(type){
	case DataItemCollection.DOUBLE:
	    double dtop = ((Double)top).doubleValue();
	    double dbottom = ((Double)bottom).doubleValue();
	    double dthisVal; 
	    for (int i=0; i< numObjs; i++){
		dthisVal = ((Double)((DataItem)dataItems.getDataItem(i)).getValue(dim)).doubleValue(); 
		if (dthisVal>=dbottom && dthisVal<=dtop)
		    c.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		deselected.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
	    }
	    break;
	case DataItemCollection.INTEGER:
	    int itop = ((Integer)top).intValue();
	    int ibottom = ((Integer)bottom).intValue();
	    int ithisVal;
	    for (int i=0; i< numObjs; i++){
		ithisVal = ((Integer)((DataItem)dataItems.getDataItem(i)).getValue(dim)).intValue();
		if (ithisVal>=ibottom && ithisVal<=itop)
		    c.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		fullSet.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
		deselected.add(new Integer(((DataItem)dataItems.getDataItem(i)).getID()));
	    }
	    break;
	default:
	    break;
	} 
	deselected.removeAll(c);
	return deselected;
    }
    


    public void handleSelection(int dim, int bottom, int top)	{
	altered.set(draggingAxis,new Boolean(true)); //draggingAxis rather than dim
	int typeID  = ((Integer)dataItems.getTypes().get(dim)).intValue();
	Object topVal = pixelsToValue(typeID, dim, top);
	Object bottomVal = pixelsToValue(typeID, dim, bottom);
	HashSet thisDimDeselect = new HashSet(getDeselectedObjs(typeID,dim,topVal,bottomVal));
	deselecteds.set(draggingAxis,thisDimDeselect);

	theSelection.addAll(fullSet);//add everything

	filter();
	
	selected.updateSelection();
	visMod.sendSelection();
    }


    private int clickDim(int x){

	int dimX = borderSpace;
       
	for (int i=0; i<numDims; i++){
	    if (Math.abs(dimX-x) <= permittedAccuracy){
		//clicked triangle?
		return i;
	    }
	    dimX += ((Integer)dimSpacings.get(i)).intValue();
	}	
	return -1;
    }

    public void mousePressed(MouseEvent e){
	int x = e.getX();
	int y = e.getY();
	draggingArrow = -1;
	int clickDim = clickDim(x);
	if (clickDim>=0){ //hit an axis
	    draggingAxis = clickDim;
	    draggingArrow = ((ArrowYs)arrowPosns.get(clickDim)).hitArrow(y);
	}
	else
	    return;
	
	if (draggingArrow==-1){// hit axis, but not arrow
	    
	    if (draggingAxis>0){ //can't drag 1st bar
		oldSpacings = new ArrayList();
		for (int i=0; i<dimSpacings.size(); i++){
		    oldSpacings.add(new Integer(((Integer)dimSpacings.get(i)).intValue())); 
		}
		dragStartX=x; 
		squashDim=draggingAxis;
	    }
	}
	repaint();
    }

    public void mouseClicked(MouseEvent e){
    }

    public void mouseReleased(MouseEvent e){
	if (draggingArrow>=0)
	    handleSelection(dataItems.getNumericDimNumber(draggingAxis), ((ArrowYs)arrowPosns.get(draggingAxis)).getBottom()-triangleSize, ((ArrowYs)arrowPosns.get(draggingAxis)).getTop()+triangleSize);
	//System.out.println("send selection");
	draggingArrow = -1;
	draggingAxis=-1;
	squashMovement = 0;
	repaint();
    }
    
    public void mouseDragged(MouseEvent e){
	if (draggingArrow>=0){ // dragging an arrow
	    int newYPos = e.getY();
	    int arrowPlace = borderSpace+markHeight-triangleSize;
	    if (arrowPlace<0) arrowPlace = 0;
	    if (newYPos<arrowPlace)
		newYPos = arrowPlace;
	    if (newYPos>(this.getHeight()-arrowPlace))
		newYPos = (this.getHeight()-arrowPlace);
	    ((ArrowYs)arrowPosns.get(draggingAxis)).setPosn(draggingArrow,newYPos);

	    handleSelection(dataItems.getNumericDimNumber(draggingAxis), ((ArrowYs)arrowPosns.get(draggingAxis)).getBottom()-triangleSize, ((ArrowYs)arrowPosns.get(draggingAxis)).getTop()+triangleSize);
	    

	}else{

	    int minDimWidth = 5;
	    
	    if (draggingAxis>0 && e.getX()-5 > (borderSpace+(Math.abs(squashDim-draggingAxis)*minDimWidth)) && (e.getX() <(this.getWidth() - (Math.abs(squashDim-draggingAxis)*minDimWidth)-borderSpace))){
		int dragStored = ((Integer)oldSpacings.get(draggingAxis-1)).intValue();
		int movement = e.getX()-dragStartX;

		if(movement>0){//dragging right
		    if (draggingAxis>0)
			dimSpacings.set(draggingAxis-1,new Integer(dragStored+movement)); 
		    else
			System.out.println("draggingAxis problem");
		    dimSpacings.set(squashDim,new Integer(((Integer)oldSpacings.get(squashDim)).intValue()-(movement-squashMovement))); 
		}else{//dragging left
		    dimSpacings.set(draggingAxis,new Integer(((Integer)oldSpacings.get(draggingAxis)).intValue()+Math.abs(movement)));  // dist from one to right
		    if(squashDim>0)
			dimSpacings.set((squashDim-1),new Integer(((Integer)oldSpacings.get(squashDim-1)).intValue()+(movement-squashMovement))); 
		    else
			System.out.println("squashDim problem");
		    //System.out.println(" "+draggingAxis+","+new Integer(((Integer)oldSpacings.get(draggingAxis)).intValue()+Math.abs(movement))+"    "+(squashDim-1)+","+new Integer(((Integer)oldSpacings.get(squashDim-1)).intValue()+(movement-squashMovement))+"("+((Integer)oldSpacings.get(squashDim-1)).intValue()+" "+movement+" "+squashMovement+")");
		}
		
		//drag right
		if (draggingAxis>0 && (movement-squashMovement) > ((Integer)oldSpacings.get(squashDim)).intValue()-minDimWidth){
		    dimSpacings.set(squashDim,new Integer(minDimWidth));
		    squashMovement+=(((Integer)oldSpacings.get(squashDim)).intValue()-minDimWidth);
		    dimSpacings.set(draggingAxis-1,new Integer(squashMovement+((Integer)oldSpacings.get(draggingAxis-1)).intValue())); 
		    
		    
		    squashDim++;
		}
		
		
		if (draggingAxis>0 && (squashMovement > 0) && (movement-squashMovement < 0)){ 
		    // returning from drag right and realign squashed dims
		    dimSpacings.set(squashDim,new Integer(((Integer)oldSpacings.get(squashDim)).intValue()));
		    
		    dimSpacings.set(draggingAxis-1,new Integer(squashMovement+((Integer)oldSpacings.get(draggingAxis-1)).intValue())); 
		    
		    squashDim--;
		    squashMovement-=(((Integer)oldSpacings.get(squashDim)).intValue()-minDimWidth);
		}
		
		//drag left
	    int arrowPlace = borderSpace-triangleSize;
	    if (arrowPlace<0) arrowPlace = 0;
		if (squashDim>0 && (movement-squashMovement) < (-1*((Integer)oldSpacings.get(squashDim-1)).intValue()+minDimWidth)){ 
		    dimSpacings.set(squashDim-1,new Integer(minDimWidth));
		    squashMovement-=(((Integer)oldSpacings.get(squashDim-1)).intValue()-minDimWidth);
		    dimSpacings.set(draggingAxis,new Integer(-1*(squashMovement-((Integer)oldSpacings.get(draggingAxis)).intValue()))); 
		    squashDim--;
		}
		
		
		if (squashDim>0 && (squashMovement < 0) && (movement-squashMovement > 0)){ 
		    // realign squashed dims
		    dimSpacings.set(squashDim-1,new Integer(((Integer)oldSpacings.get(squashDim-1)).intValue()));
		    
		    dimSpacings.set(draggingAxis,new Integer(-1*(squashMovement-((Integer)oldSpacings.get(draggingAxis)).intValue()))); 
		
		squashMovement+=(((Integer)oldSpacings.get(squashDim)).intValue()-minDimWidth);
		squashDim++;
		}
	    }
	}
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

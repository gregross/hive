/**
 * Algorithmic testbed
 *
 * ScatterPanel to display the graph on a ScatterPlot module
 *
 *  @author Andrew Didsbury, Greg Ross, Alistair Morrison
 */
 
package alg.scatterplot2;

import math.*;
import data.*;
import parent_gui.*;
import ColorScales.ColorScales;
import alg.*;

import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.lang.Thread;

public class ScatterPanel extends JPanel implements Selectable,
					     SelectionChangedListener,
						    java.io.Serializable, Runnable{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private DataItemCollection dataItems;
	
	protected          int               dataSize;
	protected volatile boolean           drawTrails  = true;
	protected          int               width;
	protected          int               height;
	
	protected          double            scaleX      = 1.0;
	protected          double            scaleY      = 1.0;
	
	protected          double            offsetX     = 0.0;
	protected          double            offsetY     = 0.0;
	
	protected          Point             origin;
	
	protected          ArrayList         trails;
	protected          SelectionHandler  selected; 
	protected          Set               theSelection;
	
	protected          MouseHandler      mouse;
	
	protected          Color[]           colorScheme;
	protected          int               colorField;
	protected          int               selectedCol;
	
	public static      double            DATA_DOT_SIZE  = 3.0;
	public static      double            TRAIL_DOT_SIZE = 4.0;
	public static      int               TRAIL_LENGTH   = 10;
	public static      Color             TRAIL_COLOR    = Color.cyan;
	public static      Color             DES_TRAIL_COL  = Color.lightGray;
	public static      Color             DESELECTED_DOT = Color.gray;
	public static      Color             BACKGROUND     =
	new Color(100,100,100);
	
	public static      long              MAX_DELAY      = 800;
	
	// The list of position of data items
	
	private ArrayList positions;
	
	// The reference to the parent visual object
	
        private Scatterplot2 visMod;
    
    protected HashSet anchorSet = new HashSet();
    protected ArrayList anchors;
    protected ArrayList whichSet;
    protected ArrayList anchorPos, anchorPos2;
    protected int numSubLayouts;
    protected int numAnchors;
    protected boolean doAnchors=false;
    protected int currentLayout;
    int globalCounter=0;
    protected HashSet pivotContents;
    protected ArrayList pivotIDs;
    protected boolean pivotSpring = false;
    protected boolean drawBuckets;
    protected ArrayList bucketWidths;
    protected int numBuckets;
    protected ArrayList bucket0=new ArrayList();//of hashsets
    protected boolean colourBuckets=false;
    protected boolean bombing = false, bombDropped=false;
    protected Point dropCoords;
    protected double bombbwidthX, bombbwidthY, bombthisWidthX, bombthisWidthY;
    protected Color bombColour;
    protected int bombRadius;
    final static int blastSize=300;
    private volatile Thread thread;

    public ScatterPanel(Scatterplot2 visMod) {
		this.visMod = visMod;
		setPreferredSize(new Dimension(400,400));
		
		// Set of selected indices
		
		theSelection = new HashSet();
		
		// Init trails and selected items
		
		trails = new ArrayList();
		
		mouse = new MouseHandler(this);
		origin = new Point();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		
		// Set the default colour scheme to 'Heated Object'
		
		setColorScheme("HeatedObject");
	}
	
	/**
	* Return the parent of this class, i.e. the ScatterPlot VisualModule
	*
	*/
	
	public VisualModule getParentModule()
	{
		return visMod;	
	}
	
	public void init(DataItemCollection dataItems, ArrayList positions)
	{
		this.dataItems = dataItems;
		this.positions = positions;
		
		if (dataItems != null)
		{
			dataSize = dataItems.getSize();
		
			selected = new SelectionHandler(dataSize, dataItems);
			selected.addSelectableObject(this);
			selected.addSelectionChangedListener(this);
		
			for (int i = 0 ; i < dataSize ; i++) 
			{
				trails.add(new ArrayList());
				theSelection.add(new Integer(i));
			}
		}
		else
		{
			dataSize = 0;
			if (selected != null)
			{
				selected.removeSelectionChangedListener(this);
				selected.removeSelectableObject(this);
				selected = null;
			}
		}
		
		//reset the zoom and pan
		
		reset();
	}
	
	/**
	* Method to accept a set of positions to plot points at
	*/
	
	public void setPositions(ArrayList positions)
	{
		this.positions = positions;	
	}
	
	/**
	* Method that should be called whenever an screen update is required, 
	*/
	
	public void update()
	{
		repaint();
	}
	
	/**
	* Overrides the standard paint method, displays all the dots onto the 
	* screen
	*
	*/
	
    public void paint(Graphics g){
	try{
	    width  = this.getWidth();
	    height = this.getHeight();
	    g.setColor(BACKGROUND);
	    g.fillRect(0, 0, width, height);
	    
	    // Get the origin.
			
	    origin.x = width / 2;
	    origin.y = height / 2;
				
	    int numItems = dataSize;
	    
	    // Draw the trails first
	    
// 			for (int i = 0 ; i < numItems ; i++ )
// 			{
// 				int k = i;
					
// 				if (!selected.getState(k))
// 					drawTrail(g, k, DES_TRAIL_COL);
// 			}
				
// 			for (int i = 0 ; i < numItems ; i++) 
// 			{
// 				int k = i;
						
// 				if (selected.getState(k))
// 					drawTrail(g, k, getDotColor(k));
// 			}
				
	    // Then draw data points		
	    if (bombing){
		bombColour = new Color(1.0f,0.2f,0.2f);//(((float)(blastSize-bombRadius)/(float)blastSize),0.0f,0.0f);
		g.setColor(bombColour);
		g.drawOval((int)(dropCoords.getX()-bombthisWidthX), (int)(dropCoords.getY()-bombthisWidthY), (int)(bombthisWidthX*2.0), (int)(bombthisWidthY*2.0));
	    }  


	    for (int k = 0 ; k < numItems ; k++) 
		{
		    
		    if (!selected.getState(k))
			drawDot(g, k, DESELECTED_DOT);
		}
	    
	    Coordinate c;
	    Vect v;
	    double distFromBlast;
	    for (int k = 0 ; k < numItems ; k++) {
 		// if(bombing){
//  		    c = ((Coordinate)positions.get(k));
// 		    v = new Vect(dropCoords,calcPosX(c.getX()));
// 		    distFromBlast = v.getLength();
// 		}
		    if (((selected.getState(k)&&!(doAnchors||pivotSpring))))
			drawDot(g, k, getDotColor(k));
		    else if (colourBuckets){
			for (int b=0; b<numBuckets; b++){
			    if (((HashSet)bucket0.get(b)).contains(new Integer(k))){
				int variation = (3+b)%3;
				Color col;
				float extent = (float)b/numBuckets;
				float base = (float)(b-3.0)/numBuckets;
				if (b<4) base=1.0f;
				if (variation==0)  col = new Color(extent,base,base);
				else if (variation==1)  col = new Color(base,extent,base);
				else col = new Color(base,base,extent);
				drawDot(g,k,col);
			    }
			}
		    }
		
		    else if (doAnchors && anchorSet.contains(new Integer(k)))
			drawDot(g, k, Color.green);
		    else if (doAnchors&&((Integer)whichSet.get(k)).intValue()!=currentLayout )		
			drawDot(g, k, Color.lightGray);	
		    else if ((selected.getState(k)))
			drawDot(g, k, getDotColor(k));
	    
		
	    }
	    if (pivotSpring&&drawBuckets)
		drawBuckets(g);
	    if (pivotSpring){//draw on top
		for (int i=0; i<pivotIDs.size();i++){
		    int k = ((Integer)pivotIDs.get(i)).intValue();
		    drawDot(g,k,Color.green);
		}
	    }   
	}
	catch (Exception e){e.printStackTrace();}
    }
	
	/**
	* Draws a dot located at index index in the data item collection, with 
	* the color col.  Draw all of this onto the graphics g.
	*
	*/
	
	protected void drawDot(Graphics g, int index, Color col)
	{
		Coordinate c = ((Coordinate)positions.get(index));
		//if(index==1) System.out.println(c.getX()+" "+origin.x+" "+offsetX+" "+scaleX+" "+width);

		double x = calcPosX( c.getX() );
		double y = calcPosY( c.getY() );
		//draw data point
		
		//if (col==Color.green) 	System.out.println(origin.x + " "+c.getX()+" "+ offsetX+" "+scaleX+" "+(width/2));

		g.setColor(col);
		g.fillRect((int)(x - (DATA_DOT_SIZE / 2)), 
		(int)(y - (DATA_DOT_SIZE / 2)),
		(int)DATA_DOT_SIZE, 
		(int)DATA_DOT_SIZE);
	}
	
	/**
	* Draws a trail on the data item with index index. and colour col
	* 
	* @param g
	* @param index
	* @param col
	*/
	
	protected void drawTrail(Graphics g, int index, Color color)
	{
		Color col = new Color(color.getRed(), 
		color.getGreen(), 
		color.getBlue());
		Coordinate lastPt = new Coordinate();
		
		// If there are new points in the data set, create new trails
		
		while (index > trails.size() - 1)
			trails.add(new ArrayList());
			
		int limit = Math.min(((ArrayList)trails.get(index)).size(), 
			TRAIL_LENGTH);
			
		for (int k = 0 ; k < limit ; k++) 
		{
			Coordinate trailPt = (Coordinate)((ArrayList)trails.
			get(index)).get(k);
				
			double x = calcPosX(trailPt.getX());
			double y = calcPosY(trailPt.getY());
				
			if ((int)x != (int)calcPosX(lastPt.getX()) &&
			(int)y != (int)calcPosY(lastPt.getY()))
			{
				g.setColor(col);

				if (drawTrails) 
				{
					g.fillRect( (int)( x - (TRAIL_DOT_SIZE / 2)), 
						(int)( y - (TRAIL_DOT_SIZE / 2)), 
						(int)TRAIL_DOT_SIZE, 
						(int)TRAIL_DOT_SIZE);
				}
			}
				
			// Make color darker every 3 iterations
				
			col = fadeToBackground(col);
			lastPt = trailPt;
		}
	}
	
	/**
	* Method to convert the color passed in to a version of itself more 
	* similar to the background color
	*
	* @param col The colour to fade
	* @return The resultant color
	*/
	
	private Color fadeToBackground(Color col)
	{
		int red = (int)( (((double)col.getRed() * 3.0) + 
			(double)BACKGROUND.getRed() ) / 4.0);
		int green = (int)( (((double)col.getGreen() * 3.0) + 
			(double)BACKGROUND.getGreen() ) / 4.0);
		int blue = (int)( (((double)col.getBlue() * 3.0) + 
			(double)BACKGROUND.getBlue() ) / 4.0); 
		
		return new Color(red, green, blue);
	}
	
	/**
	* calculates the x position that this object should be at within
	* the current JPanel and layoutbounds of the layoutModel
	*
	*/
	
	public double calcPosX(double x)
	{
		return origin.x + ((x + offsetX) * scaleX * (width/2)); // GR: Reduce width
	}
	
	/**
	* calculates the y position that this object should be at within
	* the current JPanel and layoutbounds of the layoutModel
	*
	*/
	
	public double calcPosY(double y)
	{
		return origin.y + ((y + offsetY) * scaleY * (height/2)); // GR: Reduce height
	}
	
	/**
	* Returns the dot with index current drawing color, gets this from the 
	* ColorScales class by looking up this dots colour relative to the current
	* colorScheme and colorField.
	*
	* @param index The index of the dot to retrieve color for
	* @return The color that this dot should be
	*/
	
	public Color getDotColor(int index)
	{
		double max = convToDouble(dataItems.getMaximum(colorField));
			
		double min = convToDouble(dataItems.getMinimum(colorField));
			
		double val = convToDouble(dataItems.getDataItem(index).getValues()[colorField]);
			
		int i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
		if (doAnchors && anchorSet.contains(new Integer(index))){
		    return Color.green;
		}
		return colorScheme[i];
	}
	
	/**
	* private method to convert an object which is known to be of type Integer
	* Double or Date then into a double primitive
	*
	* @param o The object
	* @return The corresponding double value
	*/
	
	private double convToDouble(Object o) 
	{
		if (o instanceof Integer)
			return (double)((Integer)o).intValue();
		else if (o instanceof Double)
			return ((Double)o).doubleValue();
		else if (o instanceof Date)
			return (double)((Date)o).getTime();

		// keep compiler happy
		
		return 0.0;			
	}
	
	/**
	* updates the contents of the trails information.
	*
	*/
	
	public void updateTrails()
	{
		int numItems = dataSize;
		
		// Draw the trails first
		
		for (int i = 0 ; i < numItems ; i++ )
		{
			int k = i;
					
			Coordinate c = ((Coordinate)positions.get(k));
			((ArrayList)trails.get(k)).add(0, new Coordinate(c.getX(), 
				c.getY()));
					
			if (((ArrayList)trails.get(k)).size() > TRAIL_LENGTH)
				((ArrayList)trails.get(k)).remove(
					((ArrayList)trails.get(k)).size()-1);
		}
	}
	
	//--------------------------
	// Methods to handle rectangle selection from the mouse handler
	//-----------------------
	
	/**
	* Sets the currently selected indices of the scatter panel to be the 
	* indices of the data items held within rect
	*
	* @param rect The rectangle to look in
	*/
	
	public void handleSelection(Rectangle rect)
	{
		if (dataItems != null)
		{
			theSelection = new HashSet(getRectContents(rect));

			selected.updateSelection();
			visMod.sendSelection();
		}
	}
	
	/**
	* Adds all the data items which are within the on screen rectangle rect
	* to the currently selected indices that this object holds
	*
	* @param rect The rectange of data items
	*/
	
	public void addToSelection(Rectangle rect)
	{
		if (dataItems != null)
		{
			theSelection.addAll(getRectContents(rect));
			selected.updateSelection();
			visMod.sendSelection();
		}
	}
	
	/**
	* Removes all the data items which are within the on screen rectangle rect
	* from the currently selected indices that this object holds
	*
	* @param rect The rectange of data items
	*/
	
	public void removeFromSelection(Rectangle rect)
	{
		if (dataItems != null)
		{
			theSelection.removeAll(getRectContents(rect));
			selected.updateSelection();
			visMod.sendSelection();
		}
	}
	
	/**
	* Returns the indices of the data items that are contained inside the 
	* on screen rectangle rect.
	*
	* @param rect The rectangle
	* @return The collection of indices inside this rectangle
	*/
	
	protected Collection getRectContents(Rectangle rect)
	{
		// Adjust the selection rectangle so that it is relative to the on 
		// screen dots
		
		rect.x -= (int)(DATA_DOT_SIZE / 2.0); 
		rect.y -= (int)(DATA_DOT_SIZE / 2.0); 
		
		if (rect.height == 0 && rect.width == 0){
		    rect.height = (int)DATA_DOT_SIZE; 
		    rect.width  = (int)DATA_DOT_SIZE;
		}else{
		    rect.height += 2 * (int)DATA_DOT_SIZE; 
		    rect.width  += 2 * (int)DATA_DOT_SIZE;
		}
		
		ArrayList contents = new ArrayList();
		int numItems = dataSize;
			
		// Find the on screen positions that are within this rect
		
		for (int i = 0; i < numItems ; i++ ){
		    int k = i;
		    
		    Coordinate c = ((Coordinate)positions.get(k));
		    
		    if (rect.contains( calcPosX( c.getX()),calcPosY(c.getY()))){
			//contents.add(new Integer(k));
			
			contents.add(new Integer(dataItems.getDataItem(k).getID()));
		    }	
		}
		
		return contents;
	}
	
	//--------------------
	// Standard accessors for variables that can be modified
	//----------------
	
	public void setColorField(String field)
	{
		this.colorField = dataItems.getFields().indexOf(field);
		update();
	}
	
	/**
	* Sets the color scheme for the scatter panel to use
	*
	*/
	
	public void setColorScheme(String color)
	{	
		this.colorScheme = ColorScales.getScale(color);
		
		update();
	}
	
	/**
	* Sets the selected column value, this is used for the tooltips to give a 
	* value
	* 
	* @param colName The name of the column which is selected
	*/
	
	public void setSelectedColumn( String colName )
	{
		selectedCol = dataItems.getFields().indexOf( colName );
	}
	
	/**
	* resets the scale and offset values to their original values.  This 
	* should be called whenever a new layout is being started or someone 
	* has scrolled or zoomed away from all the data
	*/
	
	public void reset()
	{
		scaleX  = 1.0;  scaleY  = 1.0;
		offsetX = 0.0;  offsetY = 0.0;
		
		update();
	}
	
	/**
	* Sets the scale to be the new value scale
	*
	* @param scale The new scale value
	*/
	
	public void setScale( double scaleX, double scaleY )
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	/**
	* Sets the x scale to be the new value scale
	*
	* @param scaleX The new x scale value
	*/
	
	public void setScaleX( double scaleX )
	{
		this.scaleX = scaleX;
	}
	
	/**
	* Sets the y scale to be the new value scale
	*
	* @param scaleY The new y scale value
	*/
	
	public void setScaleY( double scaleY )
	{
		this.scaleY = scaleY;
	}
	
	/**
	* Returns the current value of scale in the x direction
	* 
	* @return The scale value
	*/ 
	
	public double getScaleX()
	{
		return scaleX;
	}
	
	/**
	* Returns the current value of scale in the y direction
	* 
	* @return The scale value
	*/ 
	
	public double getScaleY()
	{
		return scaleY;
	}
	
	/**
	* Returns the x offset value
	*
	* @return The x offset
	*/
	
	public double getOffsetX()
	{
		return offsetX;
	}
	
	/**
	* Returns the y offset value
	*
	* @return The y offset
	*/
	
	public double getOffsetY()
	{
		return offsetY;
	}
	
	/**
	* sets the x offset value
	*
	* @return The x offset
	*/
	
	public void setOffsetX( double offsetX )
	{
		this.offsetX = offsetX;
	}
	
	/**
	* sets the y offset value
	*
	* @return The y offset
	*/
	
	public void setOffsetY( double offsetY )
	{
		this.offsetY = offsetY;
	}
	
	/**
	* Returns the Point at the centre of the display, the origin
	*
	* @return The origin
	*/
	
	public Point getOrigin()
	{
		return origin;
	}
	
	// -----------------------
	// Methods required by selectable interface
	// -----------------
	
	/**
	* Method that will be called the selection is changed in the selection 
	* handler
	*
	* @param select The selection handler that created the selection changed
	*/
	
	public void selectionChanged(SelectionHandler select)
	{
		update();
	}
	
	/**
	* Returns the selection of this object
	*
	* @return The selection
	*/
	
	public Collection getSelection()
	{
		return theSelection;
	}
	
	public void setSelection(Collection selection)
	{
		theSelection = 	(HashSet)selection;
		selected.updateSelection();
	}
	
	public SelectionHandler getSelectionHandler()
	{
		return selected;
	}
	
	/**
	* Returns the indices of the deselected items in this object
	* This method may return null if the getSelection method is being used
	* instead.  
	*
	* @return The deselection
	*/
	
	public Collection getDeselection()
	{
		return null;
	}
	
	/**
	* Sets this selectable object to select all its items
	*
	*/
	
	public void selectAll()
	{
	    if (dataItems != null){
		theSelection = new HashSet();
		ArrayList sel = new ArrayList();
		
		for (int i = 0 ; i < dataItems.getSize() ; i++){
		    //    theSelection.add(new Integer(i));
		    sel.add(new Integer(dataItems.getDataItem(i).getID()));
		    
		}
		theSelection = new HashSet(sel);

		selected.updateSelection();
		visMod.sendSelection();
	    }
	}

	/**
	* Sets this selectable item to select none of it items
	*
	*/
	
	public void selectNone()
	{
		theSelection.clear();
	}
	
	// over ride methods in JComponent to display tool tips
	
	/**
	* Sets the tool tip text to be displayed to be relative to the data element
	* under the cursor
	*
	* @param event The mouse event that caused this tool tip to be displayed
	* @return The tool tip text to display
	*/
	
	public String getToolTipText(MouseEvent event)
	{
		// Find index of item under mouse cursor
		
		ArrayList indices = new ArrayList(getRectContents(
			new Rectangle(event.getX(), 
				event.getY(), 0, 0 )));
				
		if ( indices.size() == 0 ) 
			return null;
		
		// Get the value in selectedCol of the data collection
		
		Object val = dataItems.getDataItem(((Integer)indices.get(0)).intValue() ).getValues()[selectedCol];
		
		if ( val == null )
			return "no data";
		else
			return val.toString();		
	}
	
	/**
	* Sets the location that the tool tip should be displayed at
	*
	* @param event The mouse event that caused this to be called
	* @return The coords of the tool tip
	*/
	
	public Point getToolTipLocation(MouseEvent event)
	{
		return new Point(event.getX()+10, event.getY());
	}


    public void setAnchors(int i, int j, HashSet h, ArrayList a, ArrayList w){
	System.out.println("do anchors");
	doAnchors = true;
	anchorSet=h;
	anchors = a;
	
	
	numSubLayouts=j;
	numAnchors = i;
	whichSet = w;
	currentLayout=0;

	//  System.out.print(">>>");
//  	for (int kl=0; kl<whichSet.size(); kl++){
//  	    System.out.print(((Integer)whichSet.get(kl)).intValue()+"  ");
//  	}
//  	System.out.print("<<<"); 
    }
    
    public void setCurrentSet(int c){
	currentLayout=c;
    }

    public void anchorsAway(){
	doAnchors = false;
    }
 
    public void nextSet(){
	currentLayout++;
	if (currentLayout == numSubLayouts)
	    doAnchors=false;
	update();
    }

    public void setPivotIDs(HashSet pidConts, ArrayList pids){
	//System.out.println("scatterplot received pivots");
	pivotIDs = pids;
	pivotContents = pidConts;
	pivotSpring = true;
	repaint();
    }

    public void showBuckets(boolean show, ArrayList bw, int nb){
	drawBuckets = show;
	bucketWidths=bw;
	numBuckets = nb;
	double bigDist=0.0;
	for (int i=0; i<pivotIDs.size(); i++){
	    bigDist=0.0;
	    for (int j=0; j<dataItems.getSize(); j++){
		int obj1 = ((Integer)pivotIDs.get(i)).intValue();
		int obj2 =j;
		Coordinate p1 = (Coordinate)positions.get(obj1);
		Coordinate p2 = (Coordinate)positions.get(obj2);
		Vect v = new Vect(p1, p2);	
		double realDist = v.getLength();
		if (realDist>bigDist)
		    bigDist = realDist;
			
	    }
	    //System.out.println("pivot"+i+" "+bigDist);
	}
	repaint();
    }

    private void drawBuckets(Graphics g){
	g.setColor(Color.green);
	for (int i=0; i< pivotIDs.size(); i++){
	    if (i>0)g.setColor(Color.gray);
		Coordinate c = ((Coordinate)positions.get(((Integer)pivotIDs.get(i)).intValue()));
		
		double x = calcPosX( c.getX() );
		double y = calcPosY( c.getY() );
		//draw data point
		
		
		//System.out.println(i+" "+scaleX+"*"+((Double)bucketWidths.get(i)).doubleValue()+" "+origin.x);


		double bwidthX = (scaleX*((Double)bucketWidths.get(i)).doubleValue()*width/2);
		double bwidthY = (scaleY*((Double)bucketWidths.get(i)).doubleValue()*height/2);
	    double thiswidthX, thiswidthY;
	    for (int j=0; j<numBuckets;j++){
		thiswidthX=bwidthX*((double)j+1.0); 	thiswidthY=bwidthY*((double)j+1.0);
		g.drawOval((int)(x - thiswidthX), (int)(y - thiswidthY), (int)(thiswidthX*2.0), (int)(thiswidthY*2.0)); //*2  because of how it draws circles. provide dimesnions of rectnagle that could encompass oval
	    }
	}
    }

    public void setBucket0(int numBucks,ArrayList ar){
	colourBuckets=true;
	bucket0=null;
	bucket0 = ar;
	numBuckets=numBucks;
	repaint();
    }
    public void resetBucket0(){
	colourBuckets=false;
	bucket0=null; 
	repaint();
    }

    public void run(){
	double energy = 50;
	bombRadius=0;
	bombColour = new Color((blastSize-bombRadius)/blastSize,0.0f,0.0f);
	//for (bombRadius=0; bombRadius< blastSize; bombRadius++){
	while(!bombDropped){
	    bombRadius++;
	    bombbwidthX = (0.025*width/2); // * scaleX
	    bombbwidthY = (0.025*height/2); // * scaleY
	    bombthisWidthX=bombbwidthX*((double)bombRadius+1.0); 
	    bombthisWidthY=bombbwidthY*((double)bombRadius+1.0);
	    for (int i=0; i<8000000; i++);
	    repaint();
	}
	bombing=false;
	selectAll();
	repaint();
    }
    
    public void dropBomb(Point thisDrop) {
	if (!bombing){
	    bombDropped=false;
	    dropCoords = thisDrop;
	    bombing=true;
	    (getGraphics()).fillRect((int)(dropCoords.getX() - (DATA_DOT_SIZE / 2)), (int)(dropCoords.getY() - (DATA_DOT_SIZE / 2)),(int)DATA_DOT_SIZE, (int)DATA_DOT_SIZE);
	    double xCoord = (((thisDrop.getX() -origin.x)/((double)(width/2)))/ scaleX ) - offsetX;
	    double yCoord = (((thisDrop.getY() -origin.y)/((double)(height/2)))/ scaleY ) - offsetY;
	    visMod.dropBomb(new Coordinate(xCoord, yCoord));
	    thread = new Thread(this);
	    thread.start();
// 	    double energy = 50;
// 	    System.out.println("Go");
// 	    bombRadius=0;
// 	    bombColour = new Color((blastSize-bombRadius)/blastSize,0.0f,0.0f);
// 	    //for (bombRadius=0; bombRadius< blastSize; bombRadius++){
// 	    while(bombRadius<100){
// 		bombRadius++; System.out.print(bombRadius);
// 		bombbwidthX = (scaleX*0.025*width/2);
// 		bombbwidthY = (scaleY*0.025*height/2);
		
// 		bombthisWidthX=bombbwidthX*((double)bombRadius+1.0); 
// 		bombthisWidthY=bombbwidthY*((double)bombRadius+1.0);
// 		for (int i=0; i<1000000; i++)
// 		    ;
// 		repaint();
// 	    }
// 	    bombing=false;
// 	    selectAll();
// 	    repaint();
	}
    }

    public void bombDropped(){
	// stop bombing
	bombDropped=true;
	thread = null;
    }

}

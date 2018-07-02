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
 *                 Matthew Chalmers <matthew@dcs.gla.ac.uk>
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
 * MatrixView
 *
 * Visualisation of matrix cells, coloured by value
 *
 *  @author Alistair Morrison
 */
package alg; 
 
import ColorScales.ColorScales;

import data.*;
import parent_gui.*;

import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.*;
import java.awt.event.*;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Box.Filler;
import javax.swing.border.MatteBorder;
import javax.swing.border.Border;


public class MatrixView extends DefaultVisualModule implements ActionListener, Selectable, SelectionChangedListener {
    // Versioning for serialisation
	
    static final long serialVersionUID = 50L;
	
    // The parent MDI form
	
    private static Mdi mdiForm;
	
    // The parent drawing surface
	
    private DrawingCanvas drawPane;
	
    // the matrix
    private MatrixGrid matrixPanel;

    private JPanel southPanel;
    private JPanel selector;
    private JPanel colSelect;

    private ArrayList colours;

    // The dimensions of the module
	
    private int height = 250;
    private int width = 250;
	
    // The data to be displayed
    private ArrayList matrix;
    private ArrayList input;
    private ArrayList jointEntObjRefs;
    private ArrayList inputRefs;
    private double maxVal;
    private double minVal;
    protected Color[] colourScheme;	
    //    private ArrayList listenerMatrix;
    private ArrayList cellMatrix;
        
    private Border selectedOldBorder;
    private ArrayList borderColours;
    private ArrayList origBorderColours;

    private JPanel negPanel;
    private boolean negCols;
    private JCheckBox negative;
    private JComboBox dim1, dim2, colCombo;
    private int xDim, yDim, selectedColIndex;
    private int selectedID=-1;
    private ArrayList fields;
    private boolean bLoading = false;

    private boolean transpose;
    private int matrixLength;


    private DataItemCollection dataItems;

    // Store the selection objects 
    private Collection selection;
    private SelectionHandler selectionHandler;
    
    public MatrixView(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);
	
	setName("MatrixView");
	setToolTipText("Matrix View");
	setLabelCaption(getName());
	
	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.VISUALISATION_MODE);
	setDimension(width, height);
	setBackground(Color.lightGray);
	colourScheme = ColorScales.getScale("BTC");//("HeatedObject");
	addControls();
	xDim=yDim=0;
    }
    
    private void addControls(){

	matrixPanel = new MatrixGrid(this);
	add(matrixPanel, "Center");

	selector = new JPanel();
	selector.setOpaque(false);

	Vector colourNames = ColorScales.getNames();

	Object[] colNames = colourNames.toArray();
	colCombo = new JComboBox(colNames);
	selectedColIndex = 1;
	colCombo.addActionListener(this);
	//colCombo.setSelectedIndex(selectedColIndex);

	colSelect = new JPanel();
	colSelect.setOpaque(false);
	colSelect.add(colCombo);

	negative = new JCheckBox();
	negative.addActionListener(this);
	negPanel = new JPanel();
	negative.setOpaque(false);
	negPanel.setOpaque(false);
	negPanel.add(new JLabel("inverse colours"));
	negPanel.add(negative);

	southPanel = new JPanel();
	southPanel.setOpaque(false);
	southPanel.add(selector);
	southPanel.add(colSelect);
	southPanel.add(negPanel);
	add(southPanel, "South");
	Filler filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	add(filler, "West");
	filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	add(filler, "East");
	
	// Make the controls visible depending upon the context
	// of the VisualModule

	setInterfaceVisibility();
    }
	
    /**
     * Called when the selection has changed and a component wants to view 
     * this data
     *
     */
	
    public void updateThis(){	
	remove(matrixPanel);
	JPanel thisJP;
	Graphics thisG;

	origBorderColours = new ArrayList();
	matrixPanel = new MatrixGrid(this);
	//	listenerMatrix = new ArrayList();
	cellMatrix = new ArrayList();
	matrixPanel.setLayout(new GridLayout(matrixLength, matrixLength));
	MatrixPanelListener thisMPL;

	// 	((GridLayout)matrixPanel.getLayout()).setHgap(1);
 	//((GridLayout)matrixPanel.getLayout()).setVgap(1);

	Color col;
	for (int i=0; i<matrixLength; i++)
	    for (int j=0; j<matrixLength; j++){
		thisJP = new JPanel();
		if (transpose){
		    col = getCellColour(((Double)((ArrayList)matrix.get(j)).get(i)).doubleValue());
		    thisMPL = new MatrixPanelListener (thisJP, this, (i*matrixLength)+j, (ArrayList)((ArrayList)jointEntObjRefs.get(j)).get(i));
		}   else{
		    col = getCellColour(((Double)((ArrayList)matrix.get(i)).get(j)).doubleValue());
		    thisMPL = new MatrixPanelListener (thisJP, this, (i*matrixLength)+j, (ArrayList)((ArrayList)jointEntObjRefs.get(i)).get(j));
		}
		thisJP.setBackground(col);
		origBorderColours.add(col);
		thisJP.addMouseListener(thisMPL); 
		//		listenerMatrix.add(thisMPL);
		cellMatrix.add(thisJP);
		matrixPanel.add(thisJP);
	    }
	
	borderColours = origBorderColours;
	add(matrixPanel, "Center");
	
	remove(southPanel);
	ArrayList entries = new ArrayList();
	
	for (int i = 0; i < input.size(); i++)  {
	    entries.add((String)fields.get(i));
	}
	
	// Use the Array list to initialise the combo box
	Object[] dimEntries = entries.toArray();
	dim1 = new JComboBox(dimEntries);
	dim2 = new JComboBox(dimEntries);
	dim1.addActionListener(this);
	dim2.addActionListener(this);
	
	if (xDim>0)dim1.setSelectedIndex(xDim);
	if (yDim>0)dim2.setSelectedIndex(yDim);

	selector = new JPanel();
	selector.setOpaque(false);
	selector.add(dim1);
	selector.add(dim2);

	colSelect = new JPanel();
	colSelect.setOpaque(false);
	colSelect.add(colCombo);

	southPanel = new JPanel();
	southPanel.setOpaque(false);
	southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
	southPanel.add(selector);
	southPanel.add(colSelect);
	southPanel.add(negPanel);

	colCombo.setSelectedIndex(selectedColIndex);

	add(southPanel, "South");

	//	matrixPanel.repaint();
	repaint();
    }

    public void selectCell(int ID, ArrayList sels){
	
	resetBorders();
	
	if (selectedID>-1)//after 1st time
	    ((JPanel)cellMatrix.get(selectedID)).setBorder((MatteBorder)selectedOldBorder);
	
	selection = new HashSet();
	selection.addAll(sels);

	selectionHandler.updateSelection();
	selectedID = ID;

	JPanel thisJP = (JPanel)cellMatrix.get(ID);
	selectedOldBorder = thisJP.getBorder();

	drawBorder();

	ArrayList transferData = new ArrayList();
	transferData.add(null);
	transferData.add(selection);
	getOutPort(0).sendData(transferData);
    }

    private void drawBorder(){
	if (selectedID>-1){
	    JPanel thisJP = (JPanel)cellMatrix.get(selectedID);
	    int borderH = thisJP.getHeight()/8, borderW = thisJP.getWidth()/8;
	    if (borderH==0 && thisJP.getHeight()>=1) borderH=1; 
	    if(borderW==0 && thisJP.getWidth()>=1) borderW=1;
	    MatteBorder border = new MatteBorder(borderW, borderH, borderW, borderH,Color.red);
	    thisJP.setBorder(border);
	}
    }

    private void resetBorders(){
	borderColours = origBorderColours;
// 	JPanel thisJP;
// 	MatteBorder border;
// 	for (int i=0; i< matrixLength*matrixLength; i++){
// 	    thisJP = (JPanel)cellMatrix.get(i);
// 	    border = new MatteBorder(0,0,0,0,Color.red);
// 	    thisJP.setBorder(border);
// 	}
    }

    private void drawAllBorders(){
	if (matrix !=null){
	    MatteBorder border;
	    JPanel thisJP = (JPanel)cellMatrix.get(0);
	    int borderH = thisJP.getHeight()/8, borderW = thisJP.getWidth()/8;
	    if (borderH==0 && thisJP.getHeight()>=1) borderH=1; 
	    if(borderW==0 && thisJP.getWidth()>=1) borderW=1;
	    for (int i=0; i< matrixLength*matrixLength; i++){
		thisJP = (JPanel)cellMatrix.get(i);
		border = new MatteBorder(borderW, borderH, borderW, borderH,(Color)borderColours.get(i));	    
		thisJP.setBorder(border);
	    }
	}
    }

	
    public Color getCellColour(double val){
	
	if (val==minVal) // colour 0 vals outwith colour scale
	    return Color.darkGray;
	//int i = (int)(((val-minVal)/(maxVal-minVal))*(colourScheme.length)); 
	int i = (int)(((val-minVal)/(maxVal-minVal))*((double)(colourScheme.length-10))) + 5; 
	if (negCols)
	    return colourScheme[colourScheme.length-i];
	return colourScheme[i];
    }


    private void setMatrix(){
	if (input!=null){
	    if (yDim > xDim){
		matrix = (ArrayList)((ArrayList)input.get(yDim)).get(xDim);
		jointEntObjRefs = (ArrayList)((ArrayList)inputRefs.get(yDim)).get(xDim);
		transpose = true;
	    }else{
		transpose = false;
		matrix = (ArrayList)((ArrayList)input.get(xDim)).get(yDim);
		jointEntObjRefs = (ArrayList)((ArrayList)inputRefs.get(xDim)).get(yDim);
	    }
	}
    }

//     private void setColours(int numDims){
// 	colours = new ArrayList();
// 	for (int b=0; b<numDims; b++){
// 	    int variation = (3+b)%3;
// 	    Color col;
// 	    float extent = (float)b/numDims;
// 	    float base = (float)(b-3.0)/numDims;
// 	    if (b<3) base=1.0f;
// 	    if (variation==0)  col = new Color(extent,base,base);
// 	    else if (variation==1)  col = new Color(base,extent,base);
// 	    else col = new Color(base,base,extent);
// 	    colours.add(col);
// 	}
//     }


    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
	
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg) {
	if (arg != null){
	    if (toPort.getKey().equals("i0")){
		// Data on the input port

		selectedID = -1;
		//gets in triangular arraylist (origDim * origDim) / 2, 
		// with each item an AL of ALs of joint entropies for that dim pair
		dataItems = (DataItemCollection)(arg.get(0));
		fields = dataItems.getFields();
		input = (ArrayList)(arg.get(1));
		inputRefs = (ArrayList)(arg.get(2));

		setMatrix();

		matrixLength = matrix.size();

		selectionHandler = new SelectionHandler(dataItems.getSize(), dataItems);
		selectionHandler.addSelectableObject(this);
		selectionHandler.addSelectionChangedListener(this);

		//		setColours(fields.size());

		setMaxAndMin();
		updateThis();
		repaint();
	    }
	    else if (toPort.getKey().equals("o0")){
		// Selection data arrived
				
		if (matrix != null){
		    selection = (Collection)arg.get(1);
		    setSelection(selection);
		    try{
			selectionHandler.updateSelection();
		    }
		    catch (Exception e){}
		}
	    }
	}else{
	    // Input module was deleted
		
	    xDim=yDim=0;
	    matrix = null;
	    remove(matrixPanel);
	    remove(southPanel);

	    if (selectionHandler != null){
		selectionHandler.removeSelectionChangedListener(this);
		selectionHandler.removeSelectableObject(this);
		selectionHandler = null;
	    }
	}
    }

    private void setMaxAndMin(){
	double thisVal;
	minVal = Double.MAX_VALUE;
	maxVal = Double.MIN_VALUE;
	for (int i=0; i< matrixLength; i++)
	    for (int j=0; j<matrixLength; j++){
		thisVal=((Double)((ArrayList)matrix.get(i)).get(j)).doubleValue();
		if (thisVal<minVal)
		    minVal = thisVal;		
		if (thisVal>maxVal)
		    maxVal=thisVal;
	    }
    }
    
    
    public void setSelection(Collection sel){
	selection = (HashSet)sel;
	
	ArrayList propSelected = new ArrayList();//proportion of objs in each cell that are selected
	ArrayList theseObjs;
	int thisID;
	int thisCount;
	int thisSize;


	// at moment, looping through all objs every time get selection data
	// would be more efficient to get selectIDs, and loop through, 
	// adding one to the proportion of each cell to which they belong

	for (int i=0; i< matrixLength; i++){
	    for (int j=0; j< matrixLength; j++){//for all cells	    
		thisCount = 0;
		if (transpose)
		    theseObjs =  (ArrayList)((ArrayList)jointEntObjRefs.get(j)).get(i);
		else
		    theseObjs = (ArrayList)((ArrayList)jointEntObjRefs.get(i)).get(j);
	    
		thisSize = theseObjs.size();
		for (int k=0; k<thisSize; k++){//all objs in cell
		    thisID = ((Integer)theseObjs.get(k)).intValue();
		    if (selectionHandler.getState(thisID))
			//if selected, add to count for this cell
			thisCount++;
		}
		propSelected.add(new Double(((double)thisCount)/((double)thisSize)));
	    }
	}
	
	int alpha;
	double proportion;
	borderColours = new ArrayList();
	for (int i=0; i< matrixLength*matrixLength; i++){//for all cells
	    proportion = ((Double)propSelected.get(i)).doubleValue();
	    alpha = (int)((double)255 * proportion);
	    
	    if (alpha>255){ //error
		alpha = 255;
		System.out.println("high alpha"+alpha);
	    }
	    
	    borderColours.add(new Color(255,0,0,alpha));
	}
	drawAllBorders();
    }



    // Create the ports and append them to the module
	
    private void setPorts(){
	int numInPorts = 1;
	int numOutPorts = 1;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
		

	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortDataStructure(ScriptModel.VECTOR);
	port.setPortLabel("Matrix in");
	ports.add(port);

	// Add 'out' port
		
	port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
	port.setPortLabel("Selection");
	ports.add(port);
		
	addPorts(ports);
    }
	
    /**
     * Returns the indices of the selected items in this object
     * 
     *
     * @return The selection
     */
     
    public Collection getSelection(){
	return selection;
    }

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
	selection.clear();
    }
    	

    /**
     * When the selection is changed, this is called to send the
     * selection to the selection port
     */
	
    public void sendSelection()
    {	
	// Get references to the selction handling objects

// 	selection  =;
	// ArrayList transferData = new ArrayList(2);
// 	transferData.add(matrix);
// 	transferData.add(selection);
// 	getOutPort(0).sendData(transferData);
    }

    public void actionPerformed(ActionEvent e){
	if (!bLoading){
	    if (e.getSource() ==dim1) {
		xDim = dim1.getSelectedIndex();
	    }else if(e.getSource() ==dim2) {
		yDim = dim2.getSelectedIndex();
	    }
	    if (e.getSource()==colCombo){
		colourScheme = ColorScales.getScale((String)colCombo.getSelectedItem());
		selectedColIndex = colCombo.getSelectedIndex();
	    }
	    if (e.getSource() == negative)
		negCols = negative.isSelected();
	    bLoading=true;
	    setMatrix();
	    setMaxAndMin();	    
	    updateThis();
	    bLoading=false;
	}
	repaint();
    }


    public class MatrixPanelListener implements MouseListener{

	private JPanel thisJP;
	private MatrixView parent;
	private int ID;
	private ArrayList objs;// the objs represented in this cell

	public MatrixPanelListener(JPanel jp, MatrixView mv, int i, ArrayList objs){
	    thisJP = jp;
	    parent = mv;
	    ID = i;
	    this.objs = objs;
	}


	public ArrayList getObjs(){
	    return objs;
	}

	public void mousePressed(MouseEvent e){	
	    parent.selectCell(ID,objs);
	    if (objs==null)
		System.out.println("null objs selected");
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}



    }

    private class MatrixGrid extends JPanel{
	MatrixView parent;
	int thisH=0, thisW=0;

	public MatrixGrid(MatrixView mv){
	    parent = mv;
	}

	public void paint(Graphics g){
	    if (getHeight() !=thisH || getWidth() !=thisW){//has been resized
		parent.drawAllBorders();
		parent.drawBorder();       
	    }
	    super.paint(g);
	}

    }
}

/**
 * Algorithmic testbed
 *
 * A chart component for plotting time and stress etc. Utilises JOpenChart
 *
 *  @author Greg Ross, Alistair Morrison
 */
 
package alg;

import data.*;
import parent_gui.*;


import java.awt.*;
import java.io.*;

import de.progra.charting.swing.*;
import de.progra.charting.event.*;
import de.progra.charting.model.*;
import de.progra.charting.render.*;
import de.progra.charting.*;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.Timer;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import javax.swing.Box.Filler;
import javax.swing.*;

public class Chart extends DefaultVisualModule implements ChartDataModelListener, ActionListener

{
    // Versioning for serialisation
	
    static final long serialVersionUID = 50L;
	
    private DataItemCollection dataItems;
	
    // The parent drawing surface
	
    private DrawingCanvas drawPane;
	
    private static Mdi mdiForm;
	
    private int height = 158;
    private int width = 303;
	
    private ChartPanel panel;
    private EditableChartDataModel data;
	
    private boolean bGraphAdded = false;
	
    private double[][] model;
    private double[] columns;
    private String[] rows;
	
    // Has all data arrived?
	
    private boolean bPort1 = false;
    private boolean bPort2 = false;
	
    private ArrayList xReceived;// list of all unique x axis values received
    private ArrayList numAtX; // number of values received for each specific x value.  So can plot point at average position
    private ArrayList sumAtX; // sum of values
	
    private String title = null;
    private String xAxis = null;
    private String yAxis = null;
	
    private JLabel numSeriesLabel;
    private int numSeries=0;
    private JTextField  typeSeriesNames;
    private ArrayList seriesNames;
    private JButton addSeries;
    private JButton clearSeries;
    
    private EditableDataSet[] ds;
    private boolean addedThisSeries;
    private String currentName;

    int countIts=0;

    public Chart(Mdi mdiForm, DrawingCanvas drawPane)
    {
	super(mdiForm, drawPane);
		
		
	setName("Chart");
	setToolTipText("Chart");
	setLabelCaption(getName());
		
		
	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.VISUALISATION_MODE);
	setDimension(width, height);
	setBackground(Color.lightGray);
		
	addControls();
    }
	
    private void addControls()   {
	// Fill out the East and West and south regions of the module's BorderLayout
	// so the graph has a border
	JPanel series = new JPanel();
		
	numSeriesLabel = new JLabel("   0");
	typeSeriesNames = new JTextField("series1");
	typeSeriesNames.setColumns(15);
		
	xReceived = new ArrayList();
	numAtX = new ArrayList();
	sumAtX = new ArrayList();
		
	seriesNames= new ArrayList();
	series.add(typeSeriesNames);
	addSeries = new JButton("Add");
	clearSeries = new JButton("Clear");
	addSeries.setActionCommand("addSeries");
	addSeries.addActionListener(this);
	clearSeries.setActionCommand("clearSeries");
	clearSeries.addActionListener(this);
	series.add(addSeries);
	series.add(clearSeries);
	series.add(numSeriesLabel);
	series.setOpaque(false);
	add(series, "North");
	setInterfaceVisibility();
    }
	
    /**
     * Implementation of the ChartDataModelListener interface
     */
	
    public void chartDataChanged(ChartDataModelEvent evt)
    {
	panel.revalidate();
	repaint();
    }
	
    // Create the ports and append them to the module
	
    private void setPorts()
    {
	int numInPorts = 3;
	int numOutPorts = 0;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
		
	// 1) Dependent variable
		
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortLabel("Dependent variable");
	port.setPortDataStructure(ScriptModel.VECTOR);
	ports.add(port);
		
	// 2) Independent variable (VECTOR)
		
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
	port.setPortLabel("Independent variable");
	port.setPortDataStructure(ScriptModel.VECTOR);
	ports.add(port);
		
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN,2);
	port.setPortLabel("New series");
	ports.add(port);
		
	addPorts(ports);
    }
	
    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (arg != null){
	    if (toPort.getKey().equals("i0")) {// Dependent

		// get whole block in 1 go: [0]=DIC; [1]=ArrayList; [2]=String;
		// if String = "twoDblock" then Arraylist has 2D block
		// not worrying about avg'ing etc.
		if (arg.size()>2 && arg.get(1) instanceof ArrayList && arg.get(2) instanceof String && ((String)arg.get(2)).equals("twoDblock")){
		    System.out.println("Chart - 2D block");

		    // each row a new series  
		    // last row is labels. If empty, use default.
		    ArrayList block = (ArrayList)arg.get(1);
		    ArrayList labels=new ArrayList();
		    ArrayList thisSeries;
		    if (block.size()>0){

			numSeries = block.size();
			ds = new EditableDataSet[numSeries];
			if (block.get(block.size()-1) instanceof ArrayList && ((ArrayList)block.get(block.size()-1)).get(0) instanceof String){
			    //labels
			    labels = ((ArrayList)block.get(0));
			    numSeries--;
			}
			else
			    for (int i=0; i<((ArrayList)block.get(1)).size(); i++)
				labels.add((new Integer(i)).toString());
			
			for (int i =0; i< numSeries; i++){//series
			    thisSeries = (ArrayList)block.get(i);

			    Integer[] dsIn1 = new Integer[((ArrayList)thisSeries).size()];
			    double[] dsIn2 = new double[((ArrayList)thisSeries).size()];
			    
			    for (int j = 0; j< thisSeries.size(); j++){
				dsIn1[j]=new Integer(j); 
				dsIn2[j]=((Double)thisSeries.get(j)).doubleValue(); 
			    }
			    ds[i]=new EditableDataSet(ChartUtilities.transformArray(dsIn2),dsIn1,CoordSystem.FIRST_YAXIS,(String)labels.get(i));
			    addedThisSeries = true;

			}
		    }
		    
		    makeFifthChart();
		    
		}else{
		    if (arg.get(0) != null){
			if (numSeries>0)   {
			    if (arg.size() > 1){
				if (arg.get(1) instanceof ArrayList){
				    initDependentVariable(((ArrayList)arg.get(1)).get(0));
				    makeFifthChart();
				}
			    }
			}
		    }
		}
	    } else if (toPort.getKey().equals("i1")) {// Independent variable
		
	    } else if (toPort.getKey().equals("i2")) {//New series
		newSeries();
		typeSeriesNames.setText("series"+(numSeries+1));
		numSeriesLabel.setText(new Integer(numSeries).toString());
	    }
	    else{
		// Input module or link was deleted or reset
		
		bGraphAdded = false;
		bPort1 = false;
		bPort2 = false;
	    }
	}
    }
    
    private void initDependentVariable(Object dIn)
    {
	Object ob = getDataIndependentVariable();
	countIts--;
	if (ob instanceof Integer)   {
		// integers for x axis values.  Probably data size.  Value could re-occur, so average over
	    
		int thisX = ((Integer)getDataIndependentVariable()).intValue();
		int thisPos = ((ArrayList)xReceived.get(numSeries-1)).indexOf(new Integer(thisX));
	    
		if (thisPos>-1) {
		    // add received value to arrays
		    
		    ((ArrayList)numAtX.get(numSeries-1)).set(thisPos,new Integer((((Integer)((ArrayList)numAtX.get(numSeries-1)).get(thisPos)).intValue())+1));
		}else  {
		    // not seen this independent variable before
		    
		    thisPos=((ArrayList)sumAtX.get(numSeries-1)).size();
		    ((ArrayList)numAtX.get(numSeries-1)).add(new Integer(1));
		    
		    if (dIn instanceof Integer)
			((ArrayList)sumAtX.get(numSeries-1)).add(new Integer(0));
		    else if (dIn instanceof Double)
			((ArrayList)sumAtX.get(numSeries-1)).add(new Double(0.0));
		    
		    ((ArrayList)xReceived.get(numSeries-1)).add(new Integer(thisX));
		}
		
		if (dIn instanceof Integer)  {
		    // update totals in SumAtX
		    
		    int iTemp = (((Integer)((ArrayList)sumAtX.get(numSeries-1)).get(thisPos)).intValue())+((Integer)dIn).intValue();
		    ((ArrayList)sumAtX.get(numSeries-1)).set(thisPos, new Integer(iTemp));
		} else if (dIn instanceof Double)   {
		    double dTemp = (((Double)((ArrayList)sumAtX.get(numSeries-1)).get(thisPos)).doubleValue())+((Double)dIn).doubleValue();
		    ((ArrayList)sumAtX.get(numSeries-1)).set(thisPos, new Double(dTemp));
		}
	    
		if (!addedThisSeries)  {
		    if (((ArrayList)numAtX.get(numSeries-1)).size()==2)  {
			// the current series not  yet drawn on graph, but this is the 
			// second distinct value for the independent variable and so we want to add it now
			
			addedThisSeries=true;
			addSeries.setEnabled(true);
			double[] dsIn1 = new double[((ArrayList)xReceived.get(numSeries-1)).size()];
			double[] dsIn2 = new double[((ArrayList)numAtX.get(numSeries-1)).size()];
			for (int i=0; i< ((ArrayList)xReceived.get(numSeries-1)).size(); i++)  {
			    dsIn1[i]=(double)((Integer)((ArrayList)xReceived.get(numSeries-1)).get(i)).intValue();
			}
			
			for (int i=0; i< ((ArrayList)numAtX.get(numSeries-1)).size(); i++)  {
			    double h1 = ((Double)((ArrayList)sumAtX.get(numSeries-1)).get(i)).doubleValue();
			    double h2 = (double)((Integer)((ArrayList)numAtX.get(numSeries-1)).get(i)).intValue();
			    dsIn2[i]= h1/h2;
			}
		    
			ds[numSeries-1]=new EditableDataSet(ChartUtilities.transformArray(dsIn2),ChartUtilities.transformArray(dsIn1), CoordSystem.FIRST_YAXIS, currentName);
		    }  else  {
			// the current series not  yet drawn on graph, so store values until that time   i.e. numAtX.get(ns-1).size() < 2
			
		    }
		}
		
		// this bit not just an "else" so that can do this stuff when addedThisSeries set to true in above loop
		
		if (addedThisSeries)  {
		    // series already drawn on graph, insert new point
		    
		    if (numSeries > 0) {
			// insert the averaged value to chart
			if (dIn instanceof Integer) {
			    int iTemp = ((Integer)((ArrayList)sumAtX.get(numSeries-1)).get(thisPos)).intValue()/ ((Integer)((ArrayList)numAtX.get(numSeries-1)).get(thisPos)).intValue();
			    ds[numSeries-1].insertValue(new Double(iTemp), new Double(thisX));
			}
			else if (dIn instanceof Double) {
			    if (data != null)  {
				double dTemp = ((Double)((ArrayList)sumAtX.get(numSeries-1)).get(thisPos)).doubleValue() / ((double)((Integer)((ArrayList)numAtX.get(numSeries-1)).get(thisPos)).intValue());
				ds[numSeries-1].insertValue(new Double(dTemp), new Double(thisX));
			    }
			}
		    }
		}



	}else{
	    // double for x axis values.  No re-occurence expected, should be constantly increasing
	    
	    int thisPos=((ArrayList)sumAtX.get(numSeries-1)).size();
	    ((ArrayList)xReceived.get(numSeries-1)).add((Double)ob);
	    ((ArrayList)sumAtX.get(numSeries-1)).add((Double)(dIn));
	    
	    if (!addedThisSeries)  {
		if (((ArrayList)sumAtX.get(numSeries-1)).size()==2)  {
		    // the current series not  yet drawn on graph, but this is the second distinct value for the independent variable and so we want to add it now
		    
		    addedThisSeries=true;
		    double[] dsIn1 = new double[((ArrayList)xReceived.get(numSeries-1)).size()];
		    double[] dsIn2 = new double[((ArrayList)sumAtX.get(numSeries-1)).size()];
		    for (int i=0; i< ((ArrayList)xReceived.get(numSeries-1)).size(); i++){
			dsIn1[i]=((Double)((ArrayList)xReceived.get(numSeries-1)).get(i)).doubleValue(); System.out.print("a "+dsIn1[i]+",");
		    }
		    for (int i=0; i< ((ArrayList)sumAtX.get(numSeries-1)).size(); i++){
			dsIn2[i]= ((Double)((ArrayList)sumAtX.get(numSeries-1)).get(i)).doubleValue();  System.out.println(dsIn2[i]+"   ");
		    }
		    ds[numSeries-1]=new EditableDataSet(ChartUtilities.transformArray(dsIn2),ChartUtilities.transformArray(dsIn1),CoordSystem.FIRST_YAXIS,currentName);
		}
	    }
	    
	    // this bit not just an "else" so that can do this stuff when addedThisSeries set to true in above loop
	    
	    if (addedThisSeries){
		// series already drawn on graph, insert new point
		    
		if (numSeries > 0) // insert the averaged value to chart
		    if (data != null){
			ds[numSeries-1].insertValue(((Double)((ArrayList)sumAtX.get(numSeries-1)).get(thisPos)), (Double)ob);
		    }
	    }
	}
    }
    
    public void actionPerformed(ActionEvent e)   {
	// Determine whether to set the
	// text entry controls
	
	if (e.getSource() == addSeries)   {
	    newSeries();
	    
	    // don't add new series yet.
	    
	}else if (e.getSource()==clearSeries)  {
	    numSeries=0;
	    seriesNames=null;
	    seriesNames = new ArrayList();
	    sumAtX = new ArrayList();
	    numAtX = new ArrayList();
	    xReceived = new ArrayList();
	    addSeries.setEnabled(true);
	}
	typeSeriesNames.setText("series"+(numSeries+1));
	numSeriesLabel.setText(new Integer(numSeries).toString());
	makeFifthChart();
    }
   
    private Object getDataIndependentVariable()  {
	ArrayList connectedPorts;
	ModulePort port;
	
	// Get sample positions data
	
	connectedPorts = getInPort(1).getObservedPorts();
	if ((connectedPorts != null) && (connectedPorts.size() > 0))  {
	    if (connectedPorts.get(0) != null)  {
		port =(ModulePort)connectedPorts.get(0);
		if (port.getData() != null)   {
		    // The independent variable is data set size()
		    
		    if (port.getData().size() > 1){
			if (port.getData().get(1) instanceof ArrayList)  {
			    Object val = ((ArrayList)port.getData().get(1)).get(0);
			    
			    if (val instanceof DataItemCollection) {
				xAxis = "Data set size(N)";
				return new Integer(((DataItemCollection)val).getSize());
			    } else if (val instanceof ArrayList) {
				
			    } else if (val instanceof Double) {
				xAxis = "Time (sec)";
				return (Double)val;
			    } else if (port.getData().get(0) instanceof DataItemCollection) {
				xAxis = "Data set size(N)";
				return new Integer(((DataItemCollection)port.getData().get(0)).getSize());
			    }
			}
		    } else if (port.getData().get(0) instanceof DataItemCollection) {
			xAxis = "Data set size(N)";
			return new Integer(((DataItemCollection)port.getData().get(0)).getSize());
		    }
		}
	    }
	} else  {
	    countIts++;
	    return new Integer(countIts);
	}
	return new Integer(0);
    }


    private void newSeries()  {
	numSeries++;
	System.out.println("adding series: "+numSeries);

	countIts = 0;
	
	addedThisSeries=false;
	addSeries.setEnabled(false);
	currentName =  typeSeriesNames.getText();
	seriesNames.add(typeSeriesNames.getText());
	sumAtX.add(new ArrayList());
	numAtX.add(new ArrayList());
	xReceived.add(new ArrayList());
	
	int i;
	
	if (numSeries > 1)   {
	    EditableDataSet dsTemp[] = new EditableDataSet[numSeries - 1];
	    
	    for (i = 0; i < (numSeries - 1); i++)
		dsTemp[i] = ds[i];
	    
	    ds = new EditableDataSet[numSeries];
	    
	    for (i = 0; i< (numSeries - 1); i++)
		ds[i] = dsTemp[i];
	}
	else
	    ds = new EditableDataSet[numSeries];
    }
	
    /**
     * Given the input module attached to port 0, set the title for the
     * y_axis and the chart title
     */
	
    private void determineY_AxisLabel()
    {
		
    }

    public void makeFifthChart()   {
	try   {
	    Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
	    add(filler, "South");
	    filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	    add(filler, "West");
	    filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	    add(filler, "East");
	    
	    data = null;
	    
	    if (panel != null)
		remove(panel);
	    
	    panel = null;
	    
	    // Filling all DataSets
	    
	    if (numSeries > 0)   {
		String title= "  "; //don't delete
		
		int width = 640;
		int height = 480;
		boolean noneAdded=false;
		
		if (addedThisSeries) // if current series has > 2 independent axis values
		    data = new EditableChartDataModel(ds);
		else {
		    EditableDataSet[] d2 = new  EditableDataSet[numSeries-1];
		    noneAdded=true;
		    for (int i=0; i< numSeries-1; i++){
			d2[i]=ds[i];
			noneAdded=false;
		    }
		    data = new EditableChartDataModel(d2);
		    
		}
	
		if (!noneAdded){
		    data.setAutoScale(true);
		    
		    
		    panel = new ChartPanel(data, title, DefaultChart.LINEAR_X_LINEAR_Y);
		    panel.addChartRenderer(new LineChartRenderer(panel.getCoordSystem(), data), 1);
		    data.addChartDataModelListener(this);
		    
		    add(panel, "Center");
		}
	    }	
	}
	catch (Exception ex){System.out.println("Chart draw exception");}
	
	if (panel != null)
	    panel.revalidate();
		
	repaint();
    }
}
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
 * Stress calculations
 *
 *  @author Alistair Morrison, Greg Ross
 */
 
package alg;

import math.*;
import data.*;
import parent_gui.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;

import java.io.*;

public class Stress extends DefaultVisualModule implements ActionListener{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
	
    private DataItemCollection dataItems;
    private ArrayList            position;

    // The parent drawing surface
    private DrawingCanvas drawPane;
	
    private static Mdi mdiForm;
	
    private JButton start= new JButton("Get Now");
    private JRadioButton full,sample;
    private JRadioButton divideHigh, divideLow;

    private int height = 170;
    private int width = 200;  

    private ArrayList transferData;

    private JLabel stressDisplay;
    private JTextField outFile;

    private boolean sampleStress=false;
    private boolean divideByHigh=false;

    private ArrayList stresses = new ArrayList(); // all stress values from multiple runs
    private ArrayList positionsBatch;
    private boolean receivedBatch;


    public Stress(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);

	setName("Stress");
	setToolTipText("Stress");
	setLabelCaption(getName());

	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.ALGORITHM_MODE);
	setDimension(width, height);
	setBackground(Color.lightGray);
	addControls();
	transferData = new ArrayList();
    }
	
    private void addControls(){
	JPanel centrePane = new JPanel();
	JPanel southPane = new JPanel();
	JPanel radioPane= new JPanel();	
	JPanel radioPane2= new JPanel();	
	ButtonGroup group = new ButtonGroup();
	ButtonGroup group2 = new ButtonGroup();

	sample = new JRadioButton("root(N) sample");
	sample.setActionCommand("sample");
	sample.setOpaque(false);
	sample.addActionListener(this);
	full = new JRadioButton("full stress");
	full.setActionCommand("full");
	full.addActionListener(this);
	full.setOpaque(false);
	full.setSelected(true);

	group.add(full);
	group.add(sample);
	radioPane.add(full);
	radioPane.add(sample);
	
	divideHigh = new JRadioButton("divide by highD");
	divideHigh.setActionCommand("high");
	divideHigh.addActionListener(this);
	divideHigh.setOpaque(false);
	divideHigh.setSelected(false);
	divideLow = new JRadioButton("divide by lowD");
	divideLow.setActionCommand("low");
	divideLow.addActionListener(this);
	divideLow.setOpaque(false);
	divideLow.setSelected(true);// default is Kruskal Stress-1

	group2.add(divideHigh);
	group2.add(divideLow);
	radioPane2.add(divideHigh);
	radioPane2.add(divideLow);

	stressDisplay = new JLabel("");
	outFile = new JTextField("Stress.dat");
	outFile.setColumns(10);
	start.setEnabled(false);
	addButtonActionListeners();
	
	centrePane.add(outFile);
	southPane.add(stressDisplay);
	centrePane.add(start);
	centrePane.add(radioPane);
	centrePane.add(radioPane2);
	centrePane.setOpaque(false);
	southPane.setOpaque(false);
	radioPane.setOpaque(false);
	radioPane2.setOpaque(false);
	add(centrePane, "Center");
	add(southPane, "South");
	setInterfaceVisibility();
    }
	
    // Create the ports and append them to the module
    
    private void setPorts(){
	int numInPorts = 3;
	int numOutPorts = 3;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
	
	// Add 'in' ports
	
	// 1) Port for triggering the start of the clock
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortLabel("Data");
	ports.add(port);
	
	// 2) Port for triggering save current positions
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 1);
	port.setPortLabel("Get Positions");
	ports.add(port);
	
	// 3) Port for taking separate DataItemCollection for which we wish to compare the
	// positions provided via input port 0;
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 2);
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	port.setPortLabel("Get High-D data");
	ports.add(port);

	// Add 'out' port
		
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 0);
	port.setPortLabel("Done");
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
	port.setPortLabel("Pause");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 2);
	port.setPortLabel("Stress");
	ports.add(port);

	addPorts(ports);
    }
	
    private void addButtonActionListeners(){
	start.addActionListener(this);	
    }   
 
    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
    {
	if (arg != null)
	{
	    if (toPort.getKey().equals("i0"))
	    {
		start.setEnabled(true);
	    }
	    else if  (toPort.getKey().equals("i2"))
	    {
		queryInPorts();
	    }
	    else if (toPort.getKey().equals("i1"))
	    {
		//store current positions
		
		if (((String)arg.get(0)).equals("startNextMod"))
		{
		    queryInPorts();
		    
		    if (!receivedBatch)
		    {
			ArrayList pauser = new ArrayList();
			pauser.add(new String("pause"));
			getOutPort(1).sendData(pauser);// Stop!
			fileDump(false);
			getOutPort(1).sendData(pauser);// ... carry on
			ArrayList restart = new ArrayList();
			restart.add(new String("restart"));
			//getOutPort(0).sendData(restart);
		    }
		    else
		    {
			// batch of positions received from Afters
			
			ArrayList pauser = new ArrayList();
			pauser.add(new String("pause"));
			getOutPort(1).sendData(pauser);// Stop!
			fileDump(true);
			getOutPort(1).sendData(pauser);// ... carry on
		    }
		} 
		else if (((String)arg.get(0)).equals("clockGet"))
		{
		    queryInPorts();
		    double stress = getStress(position,dataItems);
		    send(stress);
		}
	    }
	}
	else {
	    // Input module or link was deleted or reset
	    position = null;
	    dataItems=null;
	    start.setEnabled(false);
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
	ArrayList connectedPorts2;
	ModulePort port;
	
	// Get parent data
	
	receivedBatch=false;
	connectedPorts = getInPort(0).getObservedPorts();
	connectedPorts2 = getInPort(0).getObservedPorts();
	
	if (connectedPorts2 == null)
	{
		if ((connectedPorts != null) && (connectedPorts.size() > 0))
		{
			if (connectedPorts.get(0) != null)
			{
				port =(ModulePort)connectedPorts.get(0);
				if (port.getData() != null && port.getData().get(0) instanceof DataItemCollection)
				{
					start.setEnabled(true);
					dataItems =  (DataItemCollection)port.getData().get(0);
					position = (ArrayList)port.getData().get(1);
				}
				else if (port.getData() != null && port.getData().get(0) instanceof ArrayList)
				{
					// big load of positions - positions for each Afters iteration.
					
					start.setEnabled(true);
					dataItems =  (DataItemCollection)port.getData().get(1);
					positionsBatch = (ArrayList)port.getData().get(0);
					receivedBatch=true;
				}
			}
		}
	}
	else
	{
		boolean bHighD = false;
		boolean bLowD = false;
		
		if (connectedPorts2.size() > 0)
		{
			port =(ModulePort)connectedPorts2.get(0);
			if (port.getData() != null)
			{
				dataItems =  (DataItemCollection)port.getData().get(0);
				bHighD = true;
			}
		}
		
		if (connectedPorts.size() > 0)
		{
			port =(ModulePort)connectedPorts.get(0);
			if (port.getData() != null)
			{
				position =  (ArrayList)port.getData().get(1);
				bLowD = true;
			}
		}
		
		if (bHighD && bLowD)
			start.setEnabled(true);
	}
    }

    double getStress(ArrayList pos, DataItemCollection dataItems) {
       stressDisplay.setText("calculating...");
       double lowDist = 0.0;
       double highDist = 0.0;
       double totalLowDist = 0.0;
       double totalHighDist = 0.0;
       double stress = 0.0;
       double lowDistTot=0.0;// for profiling
       if (dataItems==null)
	   queryInPorts();
       if (dataItems!=null){
	   if (sampleStress){
	       ArrayList sampleSet = Utils.createRandomSample(null, null, dataItems.getSize(), (int)Math.sqrt(dataItems.getSize()));
	       
	       lowDistTot=0.0;
	       for (int z = 1 ; z < sampleSet.size() ; z++) {    
		   int i = ((Integer)sampleSet.get(z)).intValue();
		   
		   for (int x=0; ((x<z)&&( x!=z)); x++) {
		       int j = ((Integer)sampleSet.get(x)).intValue();
		       Vect v = new Vect((Coordinate)pos.get(i),
					 (Coordinate)pos.get(j));
		       
		       lowDist = v.getLength(2); //System.out.print(lowDist+"  ");
		       highDist = dataItems.getDesiredDist(i, j);lowDistTot+=lowDist;// for profiling// System.out.println(lowDist);
		       stress += (lowDist - highDist) * (lowDist - highDist);
		       totalLowDist += (lowDist * lowDist);
		       totalHighDist += (highDist * highDist);
		   }
	       }    
	   } else
	   {
	       // First standrdise both the high- and low-d distances to the
	       // interval [0, 1].
	       
	       int i, j, count = 0;
	       double[] highD = new double[((dataItems.getSize() * dataItems.getSize()) - dataItems.getSize()) / 2];
	       double[] lowD = new double[highD.length];
	       
	       for (i = 0; i < (dataItems.getSize() - 1); i++)
	       {
		   for (j = (i + 1) ; j < dataItems.getSize() ; j++ )
		   {
			Vect v = new Vect((Coordinate)pos.get(i), (Coordinate)pos.get(j));
			lowDist = v.getLength(2);
			highDist = dataItems.getDesiredDist(i, j);
			highD[count] = highDist;
			lowD[count] = lowDist;
			count++;
		   }
	       }
	       
	       count = 0;
	       highD = standardizeValues(highD);
	       lowD = standardizeValues(lowD);
	       
	       for (i = 0; i < (dataItems.getSize() - 1); i++)
	       {
		   for (j = (i + 1) ; j < dataItems.getSize() ; j++ )
		   {
			//stress += (lowDist - highDist) * (lowDist - highDist);
			//totalLowDist += (lowDist * lowDist);
			//totalHighDist += (highDist * highDist);
			stress += (lowD[count] - highD[count]) * (lowD[count] - highD[count]);
			totalLowDist += (lowD[count] * lowD[count]);
			totalHighDist += (highD[count] * highD[count]);
			count++;
		   }
	       }
	   }
	   if (!divideByHigh){
	       stressDisplay.setText((new Double(stress/totalLowDist)).toString());
	       return (stress / totalLowDist);
	   }else{
	       stressDisplay.setText((new Double(stress/totalHighDist)).toString());
	       return (stress / totalHighDist);
	   }
       }
       return 0.0; // if dataItems==null
   }
   
   private double[] standardizeValues(double[] values)
   {
	double low = Integer.MAX_VALUE;
	double high = Integer.MIN_VALUE;
	double val;
	double[] result = new double[values.length];
	int i;
	
	for (i = 0; i < values.length; i++)
	{
		val = values[i];
		
		if (val > high)
			high = val;
		
		if (val < low)
			low = val;
	}
	
	high -= low;
	
	for (i = 0; i < values.length; i++)
	{
		val =  values[i];
		val -= low;
		val /= high;
		result[i] = val;
	}
	
	return result;
   }
   
   /**
	* Given row and col values for an upper-triangle matrix, return the
	* ordinal index of the corresponding 1-d array.
	*/
	
	private int getIndex(int row, int col, int numCols)
	{
		if (col > row)
		{
			return (int)(col + (((2 * numCols * row) - ((row * row) + row)) / 2d) - row - 1);
		}
		else
		{
			return (int)(row + (((2 * numCols * col) - ((col * col) + col)) / 2d) - col - 1);
		}
	}
    
    synchronized void fileDump(boolean batch){
	if (!batch){
	    try {
		FileOutputStream out;
		PrintWriter pw;
		out = new FileOutputStream(outFile.getText(), true);
		pw = new PrintWriter(out,true);
		double thisStress;
		thisStress = getStress(position,dataItems);
		
		send(thisStress);
		
		pw.println(thisStress+" ");
		out.close();
	} catch (IOException e){e.printStackTrace();};
	
	
	ArrayList restart = new ArrayList();
	restart.add(new String("restart"));
	getOutPort(0).sendData(restart);
	} else{ 
	    try {
		FileOutputStream out;
		PrintWriter pw;
		out = new FileOutputStream("AftersFile.txt", true);
		pw = new PrintWriter(out,true);
		double thisStress;
		for (int i=0; i<positionsBatch.size(); i++){
		    thisStress = getStress((ArrayList)positionsBatch.get(i),dataItems);
		    pw.print(thisStress+",");
		}
		pw.println();
		out.close();
	    } catch (IOException e){e.printStackTrace();};
	    
	    //can reset arrays
	    positionsBatch=null;
	    
	    ArrayList restart = new ArrayList();
	    restart.add(new String("restart"));
	    getOutPort(0).sendData(restart);
	}
    }


//     public void run() {
// 	fileDump();
//     }

    
    /**
     * Implement the ActionListener interface
     */
	
    public void actionPerformed(ActionEvent e){
		
	if (e.getSource() == full){
	    sampleStress=false;
	}
	else if (e.getSource() == sample){
	    sampleStress=true;
	} else if (e.getSource()==divideHigh){
	    divideByHigh=true;
	}
	else if (e.getSource() == start){
	    setFocus();
	    //sendPause();
	    queryInPorts();
	    
	    double stress = getStress(position, dataItems);
	    //sendPause();
	    send(stress);
	}else
	    divideByHigh=false;
    }
    
//      /**
//      * Send data on pause port
//      */
    
//     public void sendPause(){	
// 	ArrayList pause = new ArrayList();
// 	pause.add(null);
// 	getOutPort(1).sendData(pause);
//     }


   /**
     * Send stress
     */
    
    public void send(double stress){
	stressDisplay.setText((new Double(stress)).toString());
	ArrayList ar = new ArrayList();
	ArrayList ar2 = new ArrayList();
	ar.add(new DataItemCollection());
	ar2.add((new Double(stress)));
	ar.add(ar2);
	getOutPort(2).sendData(ar);
    }
}

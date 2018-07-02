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
 * Clock panel to measure module run times
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
import javax.swing.JTextField;
import java.io.*;
import java.lang.Thread;

public class Clock extends DefaultVisualModule implements Runnable, ActionListener{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
	
    private DataItemCollection dataItems;
    
    // The parent drawing surface
    private DrawingCanvas drawPane;
	
    private static Mdi mdiForm;
	
    private JButton start= new JButton("Start");
    private JButton start2= new JButton("Start Ticker");
    

    private int height = 128;
    private int width = 153;  
		
    private long startTime, stopTime,pauseTime, startPause,endPause;
    private ArrayList transferData;

    private boolean paused, running=false;  // N.B. can be both running and paused

    private JLabel timeDisplay;
    private JTextField outFile;

    private boolean subtract=false;
    private boolean threadGoing=false;
    private Thread t;
    private JTextField ticker; 

    public Clock(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);

	setName("Clock");
	setToolTipText("Clock");
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
	timeDisplay = new JLabel("0:00");
	outFile = new JTextField("RunTimes.dat");
	outFile.setColumns(10);
	start.setEnabled(true);
	start2.setEnabled(true);

	ticker = new JTextField("3");
	ticker.setColumns(3);
	ticker.setOpaque(false);

	addButtonActionListeners();
	southPane.add(timeDisplay);
	centrePane.add(outFile);
	centrePane.add(start);
	centrePane.add(ticker);
	centrePane.add(start2);
	centrePane.setOpaque(false);
	southPane.setOpaque(false);
	add(centrePane, "Center");
	add(southPane, "South");
	setInterfaceVisibility();
    }
	
    // Create the ports and append them to the module
    
    private void setPorts(){
	int numInPorts = 3;
	int numOutPorts = 2;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
	
	// 0) Port for triggering the start of the clock
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 0);
	port.setPortLabel("Start trigger");
	ports.add(port);
		
	// 1) Port for triggering stop
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 1);
	port.setPortLabel("Stop trigger");
	ports.add(port);
	
	// 2) Port for triggering pause
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 2);
	port.setPortLabel("Pause trigger");
	ports.add(port);	// Add 'out' port
		
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 0);//to stress
	port.setPortLabel("Trigger");
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
	port.setPortDataStructure(ScriptModel.VECTOR);
	port.setPortLabel("Time");
	ports.add(port);
	
	addPorts(ports);
    }
	
    private void addButtonActionListeners(){
	start.addActionListener(this);	   
	start2.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e)
    {
	    if (e.getSource() == start)
	    {
		    setFocus();
		    if (start.getText().equals("Stop")){
			running=false;
			stopTime = System.currentTimeMillis();
			start.setText("Start");
			send();
		    }
		    else{
			running=true;
			startTime = System.currentTimeMillis();
			pauseTime=0;
			start.setText("Stop");
			timeDisplay.setText("running");
		    }
	    }
	    else if (e.getSource() == start2)
	    {
		    if (!threadGoing)
			startIt();
		    else
			stopIt();
	    }
    }
    
    private void startIt(){
	start2.setText("Stop Ticker");
	timeDisplay.setText("0 seconds");
	t = new Thread(this);
	t.start();
	threadGoing=true;
    }

    private void stopIt(){
	start2.setText("Start Ticker");
	t=null;
	threadGoing=false;
    }


    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (arg != null){
	    if (toPort.getKey().equals("i0")){
		// Start the clock
		if (!subtract&&((String)arg.get(0)).equals("start")
		    || ((String)arg.get(0)).equals("first")){
		    running=true;
		    pauseTime=0;
		    startTime = System.currentTimeMillis();
		    start.setEnabled(false);//timing from module
		    timeDisplay.setText("running");
		}
		if (((String)arg.get(0)).equals("toSubtract")){//higher priority.  Wee hack to stop clock when spring is lining up object IDs from interpolation.  Hook this up to spring model then subtract later.
		    running=true;
		    subtract = true;
		    pauseTime=0;
		    startTime = System.currentTimeMillis();
		    start.setEnabled(false);//timing from module
		    timeDisplay.setText("running");
		}
	    }
	    else if (toPort.getKey().equals("i1")){
		// Stop
		
		if (subtract&&((String)arg.get(0)).equals("endSubtract")){
		    running=false;
		    stopTime = System.currentTimeMillis();
		    start.setEnabled(true); //stop timing from module
		    //System.out.print("stop");
		    send();
		}
		if (!subtract&&((String)arg.get(0)).equals("startNextMod")){
		    running=false;
		    stopTime = System.currentTimeMillis();
		    start.setEnabled(true); //stop timing from module
		    //System.out.print("stop");
		    send();
		}
	    }
	    else if (toPort.getKey().equals("i2")){
		//pause
		if (running){
		    if (paused){
			paused=false;
			timeDisplay.setText("running");
			endPause=System.currentTimeMillis();
			pauseTime=endPause-startPause;
		    }else{
			paused=true;
			startPause=System.currentTimeMillis();
			timeDisplay.setText("paused");
		    }
		}
	    }else{
		// Input module or link was deleted or reset
		t = null;
		System.gc();
	    }
	}
    }
    

    public void run(){
	int everySec=Integer.parseInt((ticker.getText()));
	running=true;
	startTime = System.currentTimeMillis();
	while (running&&threadGoing){
	    long waiter = System.currentTimeMillis();
	    while (System.currentTimeMillis()<waiter+(1000*everySec));
	    send(System.currentTimeMillis());
	    
	    ArrayList ar = new ArrayList();
	    ar.add(new String("clockGet"));
	    getOutPort(0).sendData(ar);
	    System.out.print("get");
	}

    }


    private void fileDump(){
	try {
	    FileOutputStream out;
	    PrintWriter pw;
	    out = new FileOutputStream(outFile.getText(), true);
	    pw = new PrintWriter(out,true);
	    pw.println((timeDisplay.getText()).substring(0,((timeDisplay.getText()).length()-8))+" ");
	    out.close();
	} catch (IOException e){e.printStackTrace();};
    }
    

    /**
	* Send time
	*/
	
	public void send(){	
	    timeDisplay.setText(""+(new Double(((double)(stopTime-startTime-pauseTime))/1000.0)).toString()+" seconds");
	    
	    if ((outFile.getText()).length()>0)
		fileDump();
	    ArrayList ar = new ArrayList();
	    ar.add(new String("startNextMod"));
	    getOutPort(0).sendData(ar);
	    ar=null;
	    ar = new ArrayList();
	    ArrayList ar2 = new ArrayList();
	    ar.add(new DataItemCollection());
	    ar2.add(new Double(Double.parseDouble((timeDisplay.getText()).substring(0,((timeDisplay.getText()).length()-8)))));
	    ar.add(ar2);
	    getOutPort(1).sendData(ar);
	}

	public void send(long l){	
	    timeDisplay.setText(""+(new Double((l-startTime)/1000.0)).toString()+" seconds");
	   
	    ArrayList ar = new ArrayList();
	    ArrayList ar2 = new ArrayList();
	    ar.add(new DataItemCollection());
	    ar2.add(new Double((l-startTime)/1000.0));
	    ar.add(ar2);
	    getOutPort(1).sendData(ar);
	}

}

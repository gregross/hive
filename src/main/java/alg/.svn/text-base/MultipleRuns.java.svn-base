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
 * Controls to execute multiple runs of a particular algorithm
 *
 *  @author Alistair Morrison, Greg Ross
 */
 
 
package alg;


import alg.fileloader.*;
import data.*;
import math.*;
import data.*;
import parent_gui.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.io.*;
import java.text.ParseException;
import java.lang.Thread;

public class MultipleRuns extends DefaultVisualModule implements ActionListener{//, Runnable{
    private Thread waitThread;

    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    // The parent drawing surface
    private DrawingCanvas drawPane;
	
    private static Mdi mdiForm;
	
    private JButton start= new JButton("Start");
    private JRadioButton allTogetherRadio, separateRadio;
    JTextField numRuns;
    JButton numRunsApply;
    JLabel numRunsLabel;
    JLabel numRunsLabel2;

    JLabel specifyFile;
    private JTextField dataFile;
    private JTextField allTogether;

    private DataLoader loader;
    private DataItemCollection csvData;

    private int height = 290;
    private int width = 360;  

    private int currentRun = 0, thisRun =0;
    private int numTimes;

    private boolean separatesReady= false;
    private boolean commandLine = false;
    
    private String currentData="";
    private int numTheseRuns = 0; // number of runs under current conditions
    private int numConditions= 0;
    private ArrayList theseRuns;
    private int totalNumRuns=0;
    private JRadioButton stressConnected;

    public MultipleRuns(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);

	setName("Multiple Runs");
	setToolTipText("Multiple Runs");
	setLabelCaption(getName());

	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.ALGORITHM_MODE);
	setDimension(width, height);
	setBackground(Color.lightGray);
	addControls();
    }
	
    private void addControls(){ 
	JPanel centrePane = new JPanel();
	JPanel northPane = new JPanel();
	JPanel numRunsPane = new JPanel();
	JPanel allTogetherPane = new JPanel();
	JPanel allTogetherPane2 = new JPanel();
	JPanel choicePane = new JPanel();
	JPanel dataRow = new JPanel();
	JPanel separatePane= new JPanel();
	JPanel bottomPane = new JPanel();

	choicePane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Select Mode"));
	choicePane.setLayout(new BoxLayout(choicePane, BoxLayout.Y_AXIS));
	allTogetherRadio = new JRadioButton("Command line input");
	allTogetherRadio.setOpaque(false);
	allTogetherRadio.setActionCommand("allTogether");
	allTogetherRadio.addActionListener(this);
	separateRadio = new JRadioButton("Specify in modules");
	separateRadio.setOpaque(false);
	separateRadio.setActionCommand("separate");
	separateRadio.addActionListener(this);
	separateRadio.setSelected(true);

	choicePane.add(allTogetherRadio);
	choicePane.add(separateRadio);
	choicePane.setOpaque(false);

	northPane.setLayout(new BorderLayout());
	centrePane.setLayout(new BorderLayout());
	northPane.add(choicePane, BorderLayout.NORTH);

	allTogetherPane.setBorder( new TitledBorder( LineBorder.createGrayLineBorder(), "Command Line"));
	allTogetherPane2.setLayout(new BoxLayout(allTogetherPane2, BoxLayout.Y_AXIS));
	separatePane.setBorder( new TitledBorder( LineBorder.createGrayLineBorder(), "Separates"));
	separatePane.setLayout(new BoxLayout(separatePane, BoxLayout.Y_AXIS));
	
	dataFile = new JTextField("dataIn/macrofocus/macrofocus-2000.csv");
	dataFile.setColumns(25);
	dataFile.setPreferredSize(new Dimension(200, 18));
	dataFile.setMaximumSize(new Dimension(200, 18));
	dataFile.setMinimumSize(new Dimension(200, 18));
	
	dataRow.setLayout(new BoxLayout(dataRow, BoxLayout.X_AXIS));
	specifyFile = new JLabel("Specify data file");
	dataRow.add(specifyFile);
	dataRow.add(dataFile);
	dataRow.setOpaque(false);
	
	JLabel cl = new JLabel("(data file,[numRuns,<modulename,(parameters)>])"); 
	allTogether = new JTextField("(datain/Audio/only13LP70000.csv,[5,<SpringModel,(0,250,0.003,0,5)>])");
	allTogether.setColumns(10);
	allTogether.setEnabled(false);
	allTogetherPane2.add(cl);
	allTogetherPane2.add(allTogether);

	ButtonGroup group = new ButtonGroup();
	group.add(separateRadio);	
	group.add(allTogetherRadio);	

	numRuns = new JTextField();
	numRuns.setColumns(3);
	numRuns.setPreferredSize(new Dimension(60, 18));
	numRuns.setMaximumSize(new Dimension(60, 18));
	numRuns.setMinimumSize(new Dimension(60, 18));
	numRunsApply = new JButton("Apply");
	numRunsApply.setActionCommand("numRunsApply");
	numRunsApply.addActionListener(this);
	numRunsLabel = new JLabel("   0");
	numRunsPane.setLayout(new BoxLayout(numRunsPane, BoxLayout.X_AXIS));
	numRunsLabel2 = new JLabel("Number of Runs");
	numRunsPane.add(numRunsLabel2);
	numRunsPane.add(numRuns);
	numRunsPane.add(numRunsApply);
	numRunsPane.add(numRunsLabel);
	numRunsPane.setOpaque(false);

	stressConnected = new JRadioButton("Stress connected?",true);
	stressConnected.setOpaque(false);

	separatePane.add(dataRow);
	separatePane.add(numRunsPane);
	bottomPane.add(stressConnected);
	bottomPane.add(start);
	bottomPane.setOpaque(false);
	centrePane.add(bottomPane,BorderLayout.SOUTH);
	separatePane.setOpaque(false);
	allTogetherPane2.setOpaque(false);
	allTogetherPane.add(allTogetherPane2);
	allTogetherPane.setOpaque(false);
	separatePane.setOpaque(false);
	northPane.add(allTogetherPane, BorderLayout.CENTER);
	northPane.setOpaque(false);
	centrePane.add(northPane,BorderLayout.NORTH);
	centrePane.add(separatePane, BorderLayout.CENTER);
	centrePane.setOpaque(false);
	add(centrePane, "Center");
	start.setEnabled(false);
	addButtonActionListeners();
	setInterfaceVisibility();
    }
	
    // Create the ports and append them to the module
    
    private void setAllTogether(boolean b){
	// b - use cmdLine
	allTogether.setEditable(b);
	allTogether.setEnabled(b);
	dataFile.setEditable(!b);
	dataFile.setEnabled(!b);
	numRuns.setEditable(!b);
	specifyFile.setEnabled(!b);
	numRunsLabel2.setEnabled(!b);
	numRunsApply.setEnabled(!b);
	numRunsLabel.setEnabled(!b);
	start.setEnabled(separatesReady||b);
	//start.setEnabled(!b);    
    }

    private void setPorts(){
	int numInPorts = 1;
	int numOutPorts = 4;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	//in port
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 0);
	port.setPortLabel("Run complete");
	ports.add(port);

		// Add 'out' port
		
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
	port.setPortLabel("Out");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);

	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
	port.setPortLabel("Parameters");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 2);
	port.setPortLabel("Start trigger");
	ports.add(port);

	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 3);
	port.setPortLabel("End trigger");
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 4);
	port.setPortLabel("New series");
	ports.add(port);

	addPorts(ports);
    }
	


 //    synchronized void waiter(){
// 	long waiter = System.currentTimeMillis(); int p=0;
// 	System.out.print("Stop...  ");
// 	while (System.currentTimeMillis()<waiter+7000)
// 	    p++;
// 	System.out.println("Carry on");
// 	notify();
//     }

 /**
	*  This is called when a connected module wants to notify this
	*  module of a change
	*/
	
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (arg != null){
	    if (toPort.getKey().equals("i0")){
		if (((String)arg.get(0)).equals("startNextMod")){
		// 1 run complete
		    //System.out.println("MR: InPort "+toPort.getKey()+": currentRun="+currentRun);		    
		    //waitThread = new Thread(this);
		    //waitThread.start();
		    

		    //the stuff in run()
		    ///////////////////////////
			
			
			    // long waiter = System.currentTimeMillis(); int p=0;
			    // 		    System.out.print("Stop...  ");
			    // 		    while (System.currentTimeMillis()<waiter+3000)
			    // 			p++;
			    // 		    System.out.println("Carry on");
			if(!stressConnected.isSelected()){//no stress module connected, this can control timing
			    //System.out.println("done  "+currentRun+"  altogether: "+numTimes);
			    
			    if (commandLine)
			    {
				goCmdLine(false);
			    }
			    else{
				if (currentRun++ < numTimes){
				    //System.out.println("start run number"+currentRun);
				    go(false);
				}else{ //complete
				    ArrayList stop= new ArrayList();
				    stop.add(new String("finished"));
				    start.setText("Start");
				    //System.out.println("MR: OutPort 2: stop");
				    getOutPort(3).sendData(stop);
				}
			    }
			}
		    //////////////////////////////

		} else if (((String)arg.get(0)).equals("restart")){  //stress module connected, let it control timing
		    //System.out.println("done  "+currentRun+"  altogether: "+numTimes);
		    if (commandLine)
			goCmdLine(false);
		    else{
			if (currentRun++ < numTimes){
			    //System.out.println("start run number"+currentRun);
			    go(false);
			}else{ //complete
			    ArrayList stop= new ArrayList();
			    stop.add(new String("finished"));
			    start.setText("Start");
			    //System.out.println("MR: OutPort 2: stop");
			    getOutPort(3).sendData(stop);
			}
		    }
		}
	    }
	}
    }



    private void addButtonActionListeners(){
	start.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setFocus();
		    if (start.getText().equals("Stop")){
			start.setText("Start");
			//System.out.println("halt");
			//sendStop();
		    }
		    else{
			start.setText("Stop");
			if (!commandLine){
			    currentRun=0;
			    numTimes = Integer.parseInt((numRunsLabel.getText()).substring(3));
			    if (currentRun==0)
				go(true);
			    else if (currentRun < numTimes){
				System.gc();
				go(false);
			    }
			    currentRun++;
			} else {
			    //parse
			    String fullCmdLine = allTogether.getText();
			    char[] source = fullCmdLine.toCharArray();
			    String fileName="", numRunsS="", moduleName="", param="";
			    ArrayList params = new ArrayList(), modParams = new ArrayList(), modules = new ArrayList(), oneRun = new ArrayList();
			    theseRuns=new ArrayList();
			    int numRuns=0;
			    boolean thisStageDone=false, exitModulesLoop=false, exitParamsLoop=false, exitDataFileLoop=false, exitMainLoop=false;
			    char separator = ',';
			    
			    //loop here for (), reset all datafile etc strings
			    
			    if (source.length>0){
				int curPos=0;
				while (!exitMainLoop){// end of input?
				    int startPos=curPos;
				    thisStageDone=false;
				    fileName ="";
				    while (!thisStageDone && curPos<source.length){ //file name
					fileName = fileName.concat(String.valueOf(source[curPos]));
					if (source[curPos]==separator){
					    fileName = fileName.substring(1,curPos-startPos);
					    thisStageDone=true;
					    
					}
					else 
					    curPos++;
				    }
				    
				    exitDataFileLoop=false;
				    while(!exitDataFileLoop){
					curPos+=2; // "["
					startPos=curPos;
					thisStageDone=false; 
					numRunsS="";
					while (!thisStageDone && curPos<source.length){ //num runs
					    numRunsS = numRunsS.concat(String.valueOf(source[curPos]));
					    if (source[curPos]==separator){
						numRunsS = numRunsS.substring(0,curPos-startPos);
						numRuns = Integer.parseInt(numRunsS);
						thisStageDone=true;
					    }
					    else 
						curPos++;
					}
					modParams=new ArrayList();
					exitModulesLoop=false;
					while(!exitModulesLoop){
					    curPos+=2; // "<"
					    startPos=curPos;
					    thisStageDone=false;
					    moduleName="";
					    params=new ArrayList();
					    while (!thisStageDone && curPos<source.length){ //module name
						moduleName = moduleName.concat(String.valueOf(source[curPos]));
						if (source[curPos]==separator){
						    moduleName = moduleName.substring(0,curPos-startPos);
						    modules.add(moduleName);
						    params.add(moduleName);
						    thisStageDone=true;
						}
						else 
						    curPos++;
					    }
					    curPos+=2; // "("
					    // all parameters sent as strings
					    exitParamsLoop=false;
					    while(!exitParamsLoop){
						startPos=curPos;
						thisStageDone=false;
						param="";
						while (!thisStageDone && curPos<source.length){ //parameters
						    param = param.concat(String.valueOf(source[curPos]));
						    if (source[curPos]==separator || source[curPos]==')'){
							param = param.substring(0,curPos-startPos);
							params.add(param);//System.out.println("param:"+param);
							thisStageDone=true;
						    }
						    else 
							curPos++;
						}
						if (source[curPos]==')'){
						    exitParamsLoop=true;
						}
						else //","
						    curPos++;
					    }
					    modParams.add(params);
					    curPos+=2; // ">", ending module, params pair
					    // now either "," - another module,parms pair, or "]" - end of modules list for these runs
					    if (source[curPos]==']')
						exitModulesLoop = true;
					}
					oneRun = new ArrayList();
					oneRun.add(fileName);
					oneRun.add(new Integer(numRuns));
					oneRun.add(modules);
					oneRun.add(modParams);
					theseRuns.add(oneRun);
					curPos++; // "]"
					// now either "," - another numRuns,moduleSet pair, or ")" - end of runs for this data file
					if (source[curPos]==')')
					    exitDataFileLoop = true;    
				    }
				    curPos++; // ")"
				    // now either "," - another data set + associated numRuns, modules etc or EOF
				    if (curPos>=source.length-1)
					exitMainLoop=true;
				    else
					curPos++; // ","
				}
			    }
			    // run
			    currentRun=0;
			    thisRun=0;
			    numConditions= theseRuns.size();
			    numTheseRuns = ((Integer)((ArrayList)theseRuns.get(currentRun)).get(1)).intValue(); //number of times for this set-up;
			    goCmdLine(true);
			}
		    }
		}
	    }); 
	numRunsApply.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setFocus();
		    numRunsLabel.setText("   "+numRuns.getText());
		    getData(dataFile.getText());
		    char[] source = numRuns.getText().toCharArray();
		    boolean allDigits=false;
		    if (source.length>0)
			allDigits=true;
		    for (int i = 0; i < source.length; i++) 
			if (!Character.isDigit(source[i]))
			    allDigits=false;
		    if (allDigits){
			start.setEnabled(true);
			separatesReady=true;
		    }
		}
	    });
   }
    

    private void setAllTogether() {

	
	
	//start.setEnabled(true);
    }

    private void getData(String fileName) {
	csvData=null;
	try {
	    //data.setFileName(fileName);
	    loader = new CSVLoader(fileName);
	    loader.readData();	    
	    csvData = loader.getDataItemCollection();
	    loader=null;

	}catch ( FileNotFoundException e){
	    e.printStackTrace();
	}
	catch ( IOException e) {
	    e.printStackTrace();
	}
	catch (ParseException e) {
	    e.printStackTrace();
	}

	ArrayList transferData = new ArrayList();
	transferData.add(csvData);

	//System.out.println("MR: OutPort 0: dataOut");
	getOutPort(0).sendData(transferData);

    }

 
    synchronized void goCmdLine(boolean first){
	System.gc();
	//System.out.println("gocmdline "+currentRun+" "+numConditions+" "+thisRun+" "+numTheseRuns);
	ArrayList go = new ArrayList();
	
	//every 10 runs, if stress module connected, send a getStress() and wait for notification
// 	if (totalNumRuns>0&&((totalNumRuns%4)==0)){
// 	    try {
// 		ArrayList stop= new ArrayList();
// 		// misleading names, change sometime
// 		stop.add(new String("finished"));
// 		System.out.println("MR: OutPort 2: stop");
// 		getOutPort(3).sendData(stop);
// 		wait();
// 	    } catch (InterruptedException e) {
// 		System.out.println ("InterruptedException");
// 	    }
// 	}
	
	if (first || thisRun == numTheseRuns){
	    if (!first)
		currentRun++;
	    if (currentRun<numConditions){ // new set of parameters
		thisRun=0;
		currentData = (String)((ArrayList)theseRuns.get(currentRun)).get(0);
		numTheseRuns = ((Integer)((ArrayList)theseRuns.get(currentRun)).get(1)).intValue(); //number of times for this set-up
		getOutPort(1).sendData((ArrayList)((ArrayList)theseRuns.get(currentRun)).get(3));//send parameters
		
		getData(currentData);
		

		ArrayList aTemp = new ArrayList();
		aTemp.add("new series");
		getOutPort(4).sendData(aTemp);

		// write to files, help to read output data files
	    try {
		FileOutputStream out;
		PrintWriter pw;
		out = new FileOutputStream("FirstSpringTimes.dat", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
out = new FileOutputStream("ParentTimes.dat", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
out = new FileOutputStream("InterpStress.dat", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
out = new FileOutputStream("PlaceTimes.dat", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
out = new FileOutputStream("TotalTimes.dat", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
out = new FileOutputStream("RunSubtract.dat", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
out = new FileOutputStream("AftersFile.txt", true);
		pw = new PrintWriter(out,true);
		pw.println("--------"+currentData);
		out.close();
	    } catch (IOException e){e.printStackTrace();};
	    


		if (first){
		    go.add(new String("first"));//reset Stresses
		    getOutPort(2).sendData(go);
		}
		go = new ArrayList();
		go.add(new String("startNextMod"));
		getOutPort(2).sendData(go);
		thisRun++;
	    }else{ //complete
		ArrayList stop= new ArrayList();
		stop.add(new String("finished"));
		start.setText("Start");
		//System.out.println("MR: OutPort 2: stop");
		//getOutPort(3).sendData(stop);
	    }
	}else{
	    getData(currentData);
	    getOutPort(1).sendData((ArrayList)((ArrayList)theseRuns.get(currentRun)).get(3));//send parameters
	    thisRun++;
	    go.add(new String("startNextMod"));
	    getOutPort(2).sendData(go);
	}
	totalNumRuns++;
    }

   private void go(boolean firstTime) {
	// firstTime - from button click or feedback trigger?
	ArrayList go= new ArrayList();
	if (firstTime){
	    go.add(new String("first"));
	    getOutPort(2).sendData(go);
	    go = new ArrayList();
	}else{
	    getData(dataFile.getText());
	}
	go.add(new String("startNextMod"));
	//System.out.println("MR: OutPort 1: go");
	getOutPort(2).sendData(go);
    }

    /**
     * Implement the ActionListener interface
     */
    public void actionPerformed(ActionEvent e){
	// 	
	if (e.getSource() == separateRadio)
	    commandLine = false;
	else if (e.getSource() == allTogetherRadio)
	    commandLine=true;
	setAllTogether(commandLine);
    }

//     public void run(){
// 	long waiter = System.currentTimeMillis(); int p=0;
// 	System.out.print("Stop...  ");
// 	while (System.currentTimeMillis()<waiter+3000)
// 	    p++;
// 	System.out.println("Carry on");
// 	System.out.println("done  "+currentRun+"  altogether: "+numTimes);
	
// 	if (commandLine)
// 	    goCmdLine(false);
// 	else{
// 	    if (currentRun++ < numTimes){
// 		System.out.println("start run number"+currentRun);
// 		go(false);
// 	    }else{ //complete
// 		ArrayList stop= new ArrayList();
// 		stop.add(new String("finished"));
// 		System.out.println("MR: OutPort 2: stop");
// 		getOutPort(3).sendData(stop);
// 	    }
// 	}
// 	waitThread = null;
//     }
    

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


	  
//      /**
//       * Send stop command
//       */
	
//      public void sendStop(){	
//  	System.out.println("send");
//  	// getOutPort(0).sendData(new ArrayList());//.add(new Long(stopTime-startTime)));
//      }
}

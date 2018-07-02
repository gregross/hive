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
 * Concatenates properties of algorithmic runs into a DataItemCollection.
 * Output is conceptually the same as a data module
 *
 *  @author Alistair Morrison
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
import javax.swing.JButton;

public class Concat extends DefaultVisualModule{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
	
    private DataItemCollection dataItems;
    private final JButton send = new JButton("send");
    
    private final JButton clear = new JButton("clear");
    // The parent drawing surface
    private DrawingCanvas drawPane;
	
    private static Mdi mdiForm;
    
    private int height = 128;
    private int width = 153;  
		
    private int numObjs=0;
    
    //for normalisation
    private double[] sumOfVals;
    private double [] sumOfSquares ;


    
    public Concat(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);

	setName("Concat");
	setToolTipText("Concat");
	setLabelCaption(getName());

	this.mdiForm = mdiForm;
	this.drawPane = drawPane;
	setPorts();
	setMode(DefaultVisualModule.ALGORITHM_MODE);
	setDimension(width, height);
	setBackground(Color.orange);
	addControls();
	dataItems = new DataItemCollection();
    }
	
    private void addControls(){
	JPanel sendP = new JPanel();
	send.setEnabled(false);
	clear.setEnabled(false);
	sendP.add(send);
	sendP.add(clear);
	sendP.setOpaque(false);
	add(sendP, "Center");
	addButtonActionListeners();
	setInterfaceVisibility();
    }
	
    // Create the ports and append them to the module
    
    private void setPorts(){
	int numInPorts = 2;
	int numOutPorts = 1;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
		
	// Add 'in' ports
	
	port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 0);
	port.setPortLabel("Trigger");
	ports.add(port);
		
	port = new ModulePort(this, ScriptModel.MULTI_PORT_IN, 1);
	port.setPortLabel("All values");
	ports.add(port);
	

	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
	port.setPortLabel("Out");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	
	ports.add(port);	
	addPorts(ports);
    }
	
   
    

    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (arg != null){	
	    if (toPort.getKey().equals("i0")){
		// trigger
		
		getNewObject();
		send.setEnabled(true);
		clear.setEnabled(true);
	    }
	    else if (toPort.getKey().equals("i1")){
	    }else{
		// Input module or link was deleted or reset
		System.gc();
	    }
	}
    }


    private void getNewObject(){
	ArrayList fields;
	ArrayList types;

	fields = new ArrayList();
	types = new ArrayList();
	ModulePort port;
	DataItem dI;
	boolean lookAtDIC = false;

	if (numObjs==0)
	    dataItems = new DataItemCollection();

	ArrayList connectedPorts = getInPort(1).getObservedPorts();	

	if ((connectedPorts != null) && (connectedPorts.size() > 0)){
	    ArrayList vals = new ArrayList();
	    for (int portCounter = 0; portCounter< connectedPorts.size(); portCounter++){
		
		if (connectedPorts.get(portCounter) != null){
		    port =(ModulePort)connectedPorts.get(portCounter);
		    if (port.getData() != null){
			
			if (((DataItemCollection)port.getData().get(0)).getSize()>0)
			    lookAtDIC = true;
			else
			    lookAtDIC = false;


			if (lookAtDIC){// this dimension is size of data set
			    if (numObjs==0)
			    {
				// one field for cardinality, one for dimensionality.
				
				types.add(new Integer(2));
				fields.add("cardinality");
				types.add(new Integer(2));
				fields.add("dimensionality");
			    }
			    vals.add(new Integer(((DataItemCollection)port.getData().get(0)).getSize()));
			    vals.add(new Integer(((DataItemCollection)port.getData().get(0)).getFields().size()));
			}else{

			    // numobjs == 0, so creating dataitemcollection 
			    if (numObjs==0) {// set types
				
				if (((ArrayList)port.getData().get(1)).get(0) instanceof Integer)
				    types.add(new Integer(2)); // ???
				else if (((ArrayList)port.getData().get(1)).get(0) instanceof Double) 
				    types.add(new Integer(3));
				else // instanceof String)
				    types.add(new Integer(0));
			    	
			    	// Derive appropriate field name from source module.
				
			        if (port.getVisualModule() instanceof Clock)
					fields.add("runtime");
				else if (port.getVisualModule() instanceof Stress)
					fields.add("stress");
				else
					fields.add("v"+portCounter);
			    }
			    
			    if (((ArrayList)port.getData().get(1)).get(0) instanceof Integer){
				vals.add((Integer)(((ArrayList)port.getData().get(1)).get(0)));}
			    else if (((ArrayList)port.getData().get(1)).get(0) instanceof Double){
				vals.add((Double)(((ArrayList)port.getData().get(1)).get(0)));}
			    else {// instanceof String
				vals.add((String)(((ArrayList)port.getData().get(1)).get(0)));}
			    
			}
		    }
		}
	    }
	    if (numObjs==0){
		dataItems.setFields(fields);
		dataItems.setTypes(types);
		sumOfVals    = new double[fields.size()];
		sumOfSquares = new double[fields.size()];
		dataItems.intiNormalArrays(sumOfVals, sumOfSquares);
	    }
	   
	    dI= new DataItem(vals.toArray(), numObjs); 
	    dataItems.addItem(dI);
	    numObjs++;
	}
    }

    private void addButtonActionListeners(){
	
      send.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		    setFocus();
		    
		    ArrayList sending= new ArrayList();
		    	
		    
		    //normalise the data
		    dataItems.setNormalizeData(sumOfVals, sumOfSquares);

		    sending.add(dataItems);
		    getOutPort(0).sendData(sending);
	      }});

      clear.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		    setFocus();
		    send.setEnabled(false);
		    dataItems=null;
		    numObjs=0;
		    ArrayList sending = null;
		    getOutPort(0).sendData(sending);
	      }});


  }
	
	/**
	* After deserialisation we need to add the button actionalisteners again.
	*/
	
	private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		addButtonActionListeners();
	}
}
/**
 * Algorithmic testbed
 *
 * Parallel Coordinates module
 *
 *
 *  @author Alistair Morrison
 */

package alg; 
 
import data.*;
import parent_gui.*;
import alg.parCoords.*;

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box.Filler;

public class ParallelCoords extends DefaultVisualModule {

    // Versioning for serialisation
    static final long serialVersionUID = 50L;
	
    // The parent MDI form
    private static Mdi mdiForm;
	
    // The parent drawing surface
    private DrawingCanvas drawPane;

    private DataItemCollection dataItems;

    private int height = 228;
    private int width = 453;

    private ParCoordsHolder parCoordsHolder;
    private ParCoordsDrawer parCoords;

    private Collection selection;
    private SelectionHandler selectionHandler;

    public ParallelCoords(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);
	
	setName("Par Coords");
	setToolTipText("Parallel Coordinates");
	setLabelCaption(getName());
	
	dataItems = new DataItemCollection();

	this.mdiForm = mdiForm;
	this.drawPane = drawPane;

	setPorts();
	setMode(DefaultVisualModule.VISUALISATION_MODE);
	setDimension(width, height);
	setBackground(Color.lightGray);
	addControls();
    }

    
    private void addControls(){
	// Add a JPanel to the CENTER region of the module
	
	parCoordsHolder = new ParCoordsHolder(dataItems, mdiForm, this);
	parCoords = parCoordsHolder.getDrawer();

	add(parCoordsHolder, "Center");
	parCoordsHolder.setControlsEnabled(false);
	
	// Fill out the East and West and south regions of the module's BorderLayout
	// so the table has a border
	
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


    public void updateThis(){
	remove(parCoordsHolder);
	parCoordsHolder = new ParCoordsHolder(dataItems, mdiForm, this);
	parCoords = parCoordsHolder.getDrawer();
	
	// Get the selection handler from the underlying parCoords
	
	selectionHandler = parCoords.getSelectionHandler();
	
	add(parCoordsHolder, "Center");
	validate();
	
	if (getParentForm().getLinkMode())
	    parCoordsHolder.setVisible(false);
	else
	    parCoordsHolder.setVisible(true);
	
	parCoords.repaint();
	parCoords.validate();
    }

    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (arg != null)   {
	    if (toPort.getKey().equals("i0"))  {
		// Data on the input port
		
		parCoordsHolder.setControlsEnabled(true);
		dataItems = (DataItemCollection)(arg.get(0));
		
		updateThis();
	    }
	    else if (toPort.getKey().equals("o0")){
		// Selection data arrived
		
		if (dataItems != null)   {
		    selection = (Collection)arg.get(1);
		    parCoords.setSelection(selection);
		    try	{
			selectionHandler.updateSelection();
			parCoords.repaint();
		    }
		    catch (Exception e){}
		}
	    }
	}else   {
	    // Input module was deleted
	    
	    dataItems = null;
	    dataItems = new DataItemCollection();
	    updateThis();
	    revalidate();
	    parCoordsHolder.setControlsEnabled(false);
	    
	}
    }
    
    // Create the ports and append them to the module
    
    private void setPorts()   {
	int numInPorts = 1;
	int numOutPorts = 1;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
	
	// Add 'in' ports
	
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortLabel("Data in");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);
	
	// Add 'out' port
	
	port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
	port.setPortLabel("Selection");
	ports.add(port);
	
	addPorts(ports);
    }
	
    /**
     * When the selection is changed, this is called to send the
     * selection to the selection port
     */
	
    public void sendSelection(){
	// Get references to the selction handling objects
		
	selection = parCoords.getSelection();
		
	ArrayList transferData = new ArrayList(2);
	transferData.add(null);
	transferData.add(selection);
	getOutPort(0).sendData(transferData);
    }
	
}
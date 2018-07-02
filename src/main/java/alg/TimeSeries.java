/**
 * Algorithmic testbed
 *
 * Time Series module
 *
 *
 *  @author Alistair Morrison
 */

package alg; 
 
import data.*;
import parent_gui.*;
import alg.timeSeries.*;


import java.util.Iterator;
import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.Box.Filler;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.event.*;
import javax.swing.event.*;


public class TimeSeries extends DefaultVisualModule {

    // Versioning for serialisation
    static final long serialVersionUID = 50L;
	
    // The parent MDI form
    private static Mdi mdiForm;
	
    // The parent drawing surface
    private DrawingCanvas drawPane;

    private DataItemCollection dataItems;

    private int height = 228;
    private int width = 453;

    private TimeSeriesHolder timeSeriesHolder;
    private TimeSeriesDrawer timeSeries;

    private Collection selection;
    private SelectionHandler selectionHandler;

    private JCheckBox showExtremes;
    private JLabel pixLabel;

    private boolean mutualInfoConnected=false;
    private int mutualInfoStepSize=0;
    private int mutualInfoOverlapSize=0;
    private boolean mutualInfoDisjoint=false;
    private DataItemCollection miDataItems;

    public TimeSeries(Mdi mdiForm, DrawingCanvas drawPane){
	super(mdiForm, drawPane);
	
	setName("Time Series");
	setToolTipText("Time Series");
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
	
	timeSeriesHolder = new TimeSeriesHolder(dataItems, mdiForm, this);
	timeSeries = timeSeriesHolder.getDrawer();

	add(timeSeriesHolder, "Center");
	timeSeriesHolder.setControlsEnabled(false);
	
	// Fill out the East and West and south regions of the module's BorderLayout
	// so the table has a border

	JPanel southPanel = new JPanel();
	southPanel.setOpaque(false);
	showExtremes = new JCheckBox();
	showExtremes.setOpaque(false);
	southPanel.add(new JLabel("Points per pixel: "));
	pixLabel = new JLabel();
	southPanel.add(pixLabel);
	southPanel.add(new JLabel("Show extreme values?"));
	southPanel.add(showExtremes);
	//	timeSeries.showExtremes(false);
	add(southPanel, "South");
	showExtremes.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    timeSeries.showExtremes(showExtremes.isSelected());
		}
	    });
	
	
	Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
	filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	add(filler, "West");
	filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
	add(filler, "East");
	
	// Make the controls visible depending upon the context
	// of the VisualModule
	
	setInterfaceVisibility();
    }


    public void updateThis(){
	remove(timeSeriesHolder);
	timeSeriesHolder = new TimeSeriesHolder(dataItems, mdiForm, this);
	timeSeries = timeSeriesHolder.getDrawer();
	
	// Get the selection handler from the underlying timeSeries
	
	selectionHandler = timeSeries.getSelectionHandler();
	
	add(timeSeriesHolder, "Center");
	validate();
	
	if (getParentForm().getLinkMode())
	    timeSeriesHolder.setVisible(false);
	else
	    timeSeriesHolder.setVisible(true);
	
	//	if(mutualInfoConnected)
	//   timeSeries.setMI(mutualInfoStepSize,mutualInfoDisjoint,miDataItems);
	timeSeries.repaint();
	timeSeries.validate();
    }

    public void setExtremes(int i){
	if (i==1)
	    showExtremes.setSelected(false);
	showExtremes.setEnabled(i!=1);
	setPointPerPixel(i);
    }


    /**
     *  This is called when a connected module wants to notify this
     *  module of a change
     */
    
    public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){
	if (toPort.getKey().equals("i0"))  {
	    // Data on the input port
	    if (arg != null)   {
		timeSeriesHolder.setControlsEnabled(true);
		dataItems = (DataItemCollection)(arg.get(0));
		
		updateThis();
	    }else {
	    // Input module was deleted
	    
		dataItems = null;
		dataItems = new DataItemCollection();
		updateThis();
		revalidate();
		timeSeriesHolder.setControlsEnabled(false);
		
	    }
	}else if (toPort.getKey().equals("i1")){
	    if (arg!=null){
		mutualInfoConnected = true;
		mutualInfoStepSize=((Integer)arg.get(0)).intValue();
		if (arg.size() > 1){
		    mutualInfoOverlapSize = ((Integer)arg.get(1)).intValue();
		    mutualInfoDisjoint = ((Boolean)arg.get(2)).booleanValue();
		    miDataItems = (DataItemCollection)arg.get(3);
		}
		
// 		if (arg.size()>4){// arg has selection info
// 		    ArrayList selection = (ArrayList)arg.get(4);
// 		    System.out.println("received selection");
// 		}

	    }
	    else {
		mutualInfoConnected=false;
		mutualInfoStepSize=0;
		miDataItems = null;
	    }
	    timeSeries.setMI(mutualInfoStepSize,mutualInfoOverlapSize,mutualInfoDisjoint,miDataItems);
	} else if (toPort.getKey().equals("o0")){
	    // Selection data arrived
	    
	    //if (arg.size()> 2 && arg.get(2) instanceof Boolean && ((Boolean)arg.get(2)).booleanValue())		// only proceed if these are MI objs
	    if (dataItems != null)   {
		selection = (Collection)arg.get(1);
		if (mutualInfoConnected)
		    timeSeries.setSelection(selection,true);
		else
		    timeSeries.setSelection(selection,false);
		try {
		    selectionHandler.updateSelection();
		    if (!mutualInfoConnected)
			timeSeries.setAvgSels();
		    timeSeries.repaint();
		}
		catch (Exception e){System.out.println("Selection exception");}
	    }
	    
	}
    }
    
    private void setPointPerPixel(int i){
	pixLabel.setText((new Integer(i)).toString());
	repaint();
    }

    // Create the ports and append them to the module
    
    private void setPorts()   {
	int numInPorts = 2;
	int numOutPorts = 2;
	ArrayList ports = new ArrayList(numInPorts + numOutPorts);
	ModulePort port;
	
	// Add 'in' ports
	
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
	port.setPortLabel("Data in");
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
	port.setPortLabel("MI in");
	ports.add(port);

	// Add 'out' port
	
	port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
	port.setPortLabel("Selection");
	ports.add(port);
	
	port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
	port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
	port.setPortLabel("Subset out");
	ports.add(port);

	addPorts(ports);
    }
	
    /**
     * When the selection is changed, this is called to send the
     * selection to the selection port
     */
	
    public void sendSelection(){
	// Get references to the selction handling objects
		
	selection = timeSeries.getSelection();
		
	ArrayList transferData = new ArrayList();
	transferData.add(null);
	transferData.add(selection);
	getOutPort(0).sendData(transferData);
    }
	
    public void sendFocus(ArrayList dims){
	ArrayList focus = timeSeries.getSelectedAL();
	if (focus!=null){
	    Iterator itr = focus.iterator();
	    int count=0;
	    int[] indices = new int[focus.size()];
	    System.out.println("new set"+focus.size());

	    while (itr.hasNext())
		indices[count++]=((Integer)itr.next()).intValue();
	
	    DataItemCollection focusOut = dataItems.createNewCollection(indices,dims);
	    
	    ArrayList transferData = new ArrayList();
	    transferData.add(focusOut);
	    transferData.add(dataItems);
	
	    getOutPort(1).sendData(transferData);
	}else
	    getOutPort(1).sendData(null);
    }

}
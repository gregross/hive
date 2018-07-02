/** 
 * Algorithmic test bed
 *  
 * TimeSeriesHolder
 * 
 * 
 *  
 *  @author Alistair Morrison
 */ 

package alg.timeSeries;

import data.DataItemCollection;
import parent_gui.*;
import alg.TimeSeries;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;



public class TimeSeriesHolder extends JPanel implements MouseMotionListener,
							java.io.Serializable
{

    // Versioning for serialisation
    static final long serialVersionUID = 50L;
    
    // The parent MDI form
    private static Mdi mdiForm;
    
    // The VisualModule parent
    private TimeSeries parent;

    private TimeSeriesDrawer timeSeries;
    
    /**Scrollpane to contain the table*/
    protected JScrollPane scrollpane;

    public TimeSeriesHolder(DataItemCollection dataItems, Mdi mdiForm, TimeSeries parent){
	super(new BorderLayout());
		
	this.mdiForm = mdiForm;
	this.parent = parent;

	timeSeries= new TimeSeriesDrawer(dataItems, mdiForm, this, parent);

	//scrollpane=new JScrollPane(timeSeries);
	this.add(timeSeries,BorderLayout.CENTER);
    }


    public void setControlsEnabled(boolean bEnabled)  {
	// Determine whether the controls should be enabled
	
    }


    public TimeSeriesDrawer getDrawer(){
	return timeSeries;
    }


    public void mouseDragged(MouseEvent me)   {

    } 
    public void mouseMoved(MouseEvent e)    {
    }
    
    private void mouseClick(MouseEvent e){}

}
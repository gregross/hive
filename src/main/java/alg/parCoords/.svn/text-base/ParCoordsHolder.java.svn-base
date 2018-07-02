/** 
 * Algorithmic test bed
 *  
 * ParCoordsHolder
 * 
 * 
 *  
 *  @author Alistair Morrison
 */ 

package alg.parCoords;

import data.DataItemCollection;
import parent_gui.*;
import alg.ParallelCoords;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;



public class ParCoordsHolder extends JPanel implements MouseMotionListener,
							java.io.Serializable
{

    // Versioning for serialisation
    static final long serialVersionUID = 50L;
    
    // The parent MDI form
    private static Mdi mdiForm;
    
    // The VisualModule parent
    private ParallelCoords parent;

    private ParCoordsDrawer parCoords;
    
    /**Scrollpane to contain the table*/
    protected JScrollPane scrollpane;

    public ParCoordsHolder(DataItemCollection dataItems, Mdi mdiForm, ParallelCoords parent){
	super(new BorderLayout());
		
	this.mdiForm = mdiForm;
	this.parent = parent;

	parCoords= new ParCoordsDrawer(dataItems, mdiForm, this, parent);

	//scrollpane=new JScrollPane(parCoords);
	this.add(parCoords,BorderLayout.CENTER);
    }


    public void setControlsEnabled(boolean bEnabled)  {
	// Determine whether the controls should be enabled
	
    }


    public ParCoordsDrawer getDrawer(){
	return parCoords;
    }


    public void mouseDragged(MouseEvent me)   {

    } 
    public void mouseMoved(MouseEvent e)    {
    }
    
    private void mouseClick(MouseEvent e){}

}
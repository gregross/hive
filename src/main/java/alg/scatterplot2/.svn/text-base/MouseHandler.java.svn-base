/**
 * Algorithmic testbed
 *
 * MouseHandler
 *
 * Handles mouse interactions for the ScatterPanel component, all the
 * results of interactions are passed onto the scatterpanel.  The class
 * also remembers state information.
 *
 *  @author Andrew Didsbury, Greg Ross, Alistair Morrison
 */

package alg.scatterplot2;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;

public class MouseHandler extends MouseInputAdapter implements java.io.Serializable
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    protected ScatterPanel parent;
    protected Point        down;
    protected Rectangle    rect;

    protected boolean bombing=false;
    
    /**
     * constructor:
     *
     * @param parent
     */
     
    public MouseHandler(ScatterPanel parent)
    {
	this.parent = parent;
	down = new Point();
	rect = new Rectangle();
    }
    
    /**
     * Method that is called whenever the mouse is dragged, that is, has a
     * button held down and is moved
     *
     * @param me
     */
     
    public void mouseDragged(MouseEvent me)
    {
	// First button pressed, SELECTING
	
	if ((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) 
	{
	    parent.setCursor(Cursor.getPredefinedCursor(1));
	    Graphics g = parent.getGraphics();
	    g.setXORMode(Color.white);
	    	    
	    // Remove the previous rect, if its not empty
	    
	    if  (!( rect.width == 0 && rect.height == 0)) 
		g.drawRect(rect.x, rect.y, rect.width, rect.height);
	    
	    // Check mouse draggin is within range of viewer
	    
	    int realX = me.getX();
	    int realY = me.getY();
	    if (me.getX() < 0)
		realX = 0;				
	    if (me.getX() > parent.getSize().width)
		realX = parent.getSize().width;
	    if (me.getY() < 0)
		realY = 0;
	    if (me.getY() > parent.getSize().height)
		realY = parent.getSize().height;
	    
	    rect.width = Math.abs(realX - down.x);
	    rect.height = Math.abs(realY - down.y);
	    rect.x = down.x;
	    rect.y = down.y;
	    
	    if (realX < rect.x && realY <= rect.y)
	    {
		rect.x = realX;
		rect.y = realY;
	    } 
	    else if (realX >= rect.x && realY < rect.y)
		rect.y = realY;
	    else if (realX < rect.x && realY > rect.y)
		rect.x = realX;
	    	    
	    g.drawRect(rect.x, rect.y, rect.width, rect.height);
	}
	
	// Middle button held down, ZOOMING
    
	else if ( (me.getModifiers() & InputEvent.BUTTON2_MASK)
	     == InputEvent.BUTTON2_MASK ) 
	{
	    // If the zoom level is greater than 1.0 then applying scaling 
	    // to the factor used for panning
	    
	    double factor = 500;
	    
	    double delta = ((double)(down.y - me.getY())) / factor;
		
	    double newScaleX = parent.getScaleX() + delta;
	    double newScaleY = parent.getScaleY() + delta;
	    if (newScaleX < 0.01) newScaleX = 0.01;
	    if (newScaleY < 0.01) newScaleY = 0.01;
	    
	    parent.setScale( newScaleX, newScaleY );
	    parent.update();
	}

	// Right mouse button held down, PANNING

	else if ((me.getModifiers() & InputEvent.BUTTON3_MASK)
		  == InputEvent.BUTTON3_MASK) 
	{
	    
	    parent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	    
	    //choose the change in scale value, make it relative to 
	    // the current scale level, so pans slower at greate zoom
	    
	    double xFactor = 400.0;
	    double yFactor = 400.0;
	    if (parent.getScaleX() > 1.0)
		xFactor *= parent.getScaleX();
	    if (parent.getScaleY() > 1.0)
		yFactor *= parent.getScaleY();
	    
	    double deltaX = ((double)down.x - me.getX()) / xFactor;

	    double deltaY = ((double)down.y - me.getY()) / yFactor;
	    
	    parent.setOffsetX( parent.getOffsetX() + deltaX );
	    parent.setOffsetY( parent.getOffsetY() + deltaY );
	    parent.update();
	}
    }
    
    public void mousePressed( MouseEvent me )
    {
	// Record coords mouse pressed at
	
	down.x = me.getX();
	down.y = me.getY();
	
	if (me.getClickCount() == 2&& (me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK){  // double click
	    bombing = true;
	    parent.dropBomb(down);
	}

	// SELECTING, init the rectangle
	
	if ((me.getModifiers() & InputEvent.BUTTON1_MASK)
	     == InputEvent.BUTTON1_MASK) 
	{
	    rect.x      = down.x;
	    rect.y      = down.y;
	    rect.width  = 0;
	    rect.height = 0;
	}
	
	// Middle button pressed down, ZOOMING
	
	else if ((me.getModifiers() & InputEvent.BUTTON2_MASK)
	     == InputEvent.BUTTON2_MASK)
	{
	    
	}
    }
    
    public void mouseReleased(MouseEvent me) 
    {
	parent.setCursor(Cursor.getPredefinedCursor(0));
	
	if (bombing){
	    
	    parent.bombDropped();
	}else{


	    // Button 1 held down

	    if ((me.getModifiers() & InputEvent.BUTTON1_MASK)
		== InputEvent.BUTTON1_MASK) 
		{
		    Graphics g = parent.getGraphics();
		    g.setXORMode(Color.white);
	    
		    // Remove the previous rect, unless it never moved
	    
		    if (!( rect.width == 0 && rect.height == 0))
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
	    
		    // SHIFT modifier	   
	    
		    if ((me.getModifiers() & InputEvent.SHIFT_MASK)
			== InputEvent.SHIFT_MASK )
			parent.addToSelection(rect);
	    
		    // CONTROL modifier
	    
		    else if ((me.getModifiers() & InputEvent.CTRL_MASK)
			     == InputEvent.CTRL_MASK)
			parent.removeFromSelection(rect);
	    
		    // No modifier
	    
		    else 
			parent.handleSelection(rect);
	    	    
		    rect = new Rectangle();
		}
	}
    }

    public void mouseClicked(MouseEvent me)
    {
	parent.getParentModule().setFocus();
	parent.getParentModule().bringToFront();
    }
    
}

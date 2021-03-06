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
 * VoronoiMouseHandler
 *
 * Handles mouse interactions for the Voronoi's JPanel component (VoronoiPane), all the
 * results of interactions are passed onto VoronoiPane.  The class
 * also remembers state information
 *
 *  @author Greg Ross
 */

package alg.voronoi;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;

public class VoronoiMouseHandler extends MouseInputAdapter implements java.io.Serializable
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    protected VoronoiPane  parent;
    protected Point        down;
    protected Rectangle    rect;
    
    /**
     * constructor:
     *
     * @param parent
     */
     
    public VoronoiMouseHandler(VoronoiPane parent)
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
	
	if ((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK  && !me.isShiftDown() && !me.isControlDown()) 
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
	     == InputEvent.BUTTON2_MASK  || me.isControlDown()) 
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
	    parent.repaint();
	}
	
	// Right mouse button held down, PANNING
	
	else if ((me.getModifiers() & InputEvent.BUTTON3_MASK)
		  == InputEvent.BUTTON3_MASK  || me.isShiftDown()) 
	{
	    parent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	    
	    //choose the change in scale value, make it relative to 
	    // the current scale level, so pans slower at great zoom
	    
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
	    parent.repaint();
	}
    }
    
    public void mousePressed( MouseEvent me )
    {
	// Record coords mouse pressed at
	
	down.x = me.getX();
	down.y = me.getY();
	
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
	
	// Button 1 held down
	
	if (((me.getModifiers() & InputEvent.BUTTON1_MASK)
	     == InputEvent.BUTTON1_MASK) && !me.isShiftDown() && !me.isControlDown())
	{
	    Graphics g = parent.getGraphics();
	    g.setXORMode(Color.white);
	    
	    // Remove the previous rect, unless it never moved
	    
	    if (!( rect.width == 0 && rect.height == 0))
		g.drawRect(rect.x, rect.y, rect.width, rect.height);
	    
	    /**
	    * Use the following code to determine whether to add or remove
	    * from the current selection..
	    
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
	    
	    */
	    
	    // See if the user has selected a cluster
	    
	    int x = me.getX();
	    int y = me.getY();
	    
	    if (!checkForCluster(x, y))
	    	parent.handleSelection(rect);
	    
	    rect = new Rectangle();
	}
    }
    
    public void mouseClicked(MouseEvent me)
    {
	parent.getParentModule().setFocus();
	parent.getParentModule().bringToFront();
    }
    
    public void mouseMoved(MouseEvent e)
    {
	    
    }
    
    private boolean checkForCluster(int x, int y)
    {
	    if ((parent.getParentModule().getClusterCheck().isSelected())
	    && (parent.getParentModule().getClusterCheck().isEnabled()))
	    {
		    // Highlight cluster
		    
		    parent.highlightCluster(new Coord((double)x, (double)y));
		    return true;
	    }
	    else
		    return false;
    }
}

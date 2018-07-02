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
 * MouseHandler
 *
 * Handles mouse interactions for the ScatterPanel component, all the
 * results of interactions are passed onto the scatterpanel.  The class
 * also remembers state information.
 *
 *  @author Andrew Didsbury, Greg Ross
 */

package alg.scatterplot;

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
    
    // Starting drag-position for zooming
    
    protected Point        scaleMouseDown;
    
    // Offset that is applied before the current zooming
    
    protected double 	   originalOffsetX = 0d;
    protected double 	   originalOffsetY = 0d;
    
    // Scalefactor that was applied immediately prior to the current zoom
    
    protected double	   oldScaleFactor = 0d;
    
    // Maximum and minimum zoom factors
    
    protected int maxZoomFactor = 10;
    protected float minZoomFactor = 0.08f;
    
    // Selection rectangle
    
    protected Rectangle    rect;
    
    /**
     * constructor:
     *
     * @param parent
     */
     
    public MouseHandler(ScatterPanel parent)
    {
	this.parent = parent;
	down = new Point();
	scaleMouseDown = new Point();
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
	
	//********** use me.isShiftDown() me.isControlDown() etc. for key presses *************
	
	if (((me.getModifiers() & InputEvent.BUTTON1_MASK) == 
		InputEvent.BUTTON1_MASK))
	{
	    parent.setDraggingBox(true);
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
	
	else if (((me.getModifiers() & InputEvent.BUTTON2_MASK)
	     == InputEvent.BUTTON2_MASK))
	{
	    // Allow the user to zoom into the spot initially clicked and dragged with the mouse.
	    
	    double diffYPerPixel = ((double)down.y - me.getY()) * maxZoomFactor/(double)parent.height;
	    
	    if (((parent.getScaleX() + diffYPerPixel) <= maxZoomFactor) && ((parent.getScaleX() + diffYPerPixel) >= minZoomFactor))
	    {
		    // Set the zoom
		    
		    parent.setScale(parent.getScaleX() + diffYPerPixel, parent.getScaleY() + diffYPerPixel);
		    
		    // Now fix the initially selected position so that it remains visible
		    
		    double displacementX = (((parent.getOrigin().getX() - scaleMouseDown.x) + originalOffsetX)
		    	/ (oldScaleFactor+1)) * ((parent.getScaleX() - 1) - oldScaleFactor);
		    double displacementY = (((parent.getOrigin().getY() - scaleMouseDown.y) + originalOffsetY)
		    	/ (oldScaleFactor+1)) * ((parent.getScaleY() - 1) - oldScaleFactor);
		    parent.setOffsetX(originalOffsetX + displacementX);
		    parent.setOffsetY(originalOffsetY + displacementY);
	    }
	    
	    down.y = me.getY();
	    parent.update();
	}
	
	// Right mouse button held down, PANNING
	
	else if (((me.getModifiers() & InputEvent.BUTTON3_MASK)
		  == InputEvent.BUTTON3_MASK))
	{
	    parent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	    
	    // Allow the user to pan the view.
	    
	    parent.setOffsetX( parent.getOffsetX() + ((double)me.getX() - down.x));
	    parent.setOffsetY( parent.getOffsetY() + ((double)me.getY() - down.y));
	    down.y = me.getY();
	    down.x = me.getX();
	    parent.update();
	}
    }
    
    public void mousePressed( MouseEvent me )
    {
	// Record coords mouse pressed at
	
	down.x = me.getX();
	down.y = me.getY();
	
	// SELECTING, init the rectangle
	
	if (((me.getModifiers() & InputEvent.BUTTON1_MASK )
	     == InputEvent.BUTTON1_MASK))
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
	    oldScaleFactor = parent.getScaleY() - 1;
	    scaleMouseDown.x = me.getX();
	    scaleMouseDown.y = me.getY();
	    originalOffsetX = parent.getOffsetX();
	    originalOffsetY = parent.getOffsetY();
	}
    }
    
    public void mouseReleased(MouseEvent me)
    {
	parent.setCursor(Cursor.getPredefinedCursor(0));
	
	// Button 1 held down
	
	if (((me.getModifiers() & InputEvent.BUTTON1_MASK)
	     == InputEvent.BUTTON1_MASK))
	{
	    Graphics g = parent.getGraphics();
	    g.setXORMode(Color.white);
	    
	    // Remove the previous rect, unless it never moved
	    
	    if (!( rect.width == 0 && rect.height == 0))
		g.drawRect(rect.x, rect.y, rect.width, rect.height);
	    
	    /**
	    * Use the following code to determine whether to add or remove
	    * from the current selection..
	    */
	    
	    // SHIFT modifier
	    
	    if ((me.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK )
		    parent.addToSelection(rect);
	    
	    // CONTROL modifier
	    
	    else if ((me.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK)
		parent.removeFromSelection(rect);
	    
	    else
		    parent.handleSelection(rect);
	    
	    parent.setDraggingBox(false);
	    
	    rect = new Rectangle();
	}
    }
    
    public void mouseClicked(MouseEvent me)
    {
	parent.getParentModule().setFocus();
	parent.getParentModule().bringToFront();
    }
}

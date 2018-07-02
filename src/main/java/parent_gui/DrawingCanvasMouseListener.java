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
 * DrawingCanvasMouseListener
 *
 * Handles all mouse events that are applicable to the JLayeredPane which is an
 * instance of the DrawingCanvas.
 *
 *  @author Greg Ross
 */
package parent_gui;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JInternalFrame;
import java.awt.Rectangle;
import java.util.*;
import java.awt.geom.*;
import java.awt.Point;

public class DrawingCanvasMouseListener extends MouseInputAdapter implements java.io.Serializable
{
	transient private Mdi mdiForm;
	private DrawingCanvas drawPane;
	
	// Instance of the JInternalFrame descendant that holds this component
	
	private DrawingForm parentForm;
	
	// Has a link been selected for dragging?
	
	private boolean bLinkSelected = false;
	
	// If a composite link has been selected, store the segment that was clicked
	
	private int linkSegment = -1;
	
	// If a composite link was selected within a smal distance of a join,
	// store the join number (0...jn)
	
	private int joinSelected = -1;
	
	public DrawingCanvasMouseListener(Mdi mdiForm, DrawingCanvas drawPane)
	{
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		parentForm = drawPane.getParentForm();
	}
	
	public void mousePressed(MouseEvent e)
	{
		// If the drawing surface is double-clicked then we want to show
		// the input and out ports on the visual components.
		
		if (e.getClickCount() == 2)
		{
			if (parentForm.getLinkMode() == true)
			{
				parentForm.setLinkMode(false);
				mdiForm.getViewMenu_LinkMode().setState(false);
			}
			else
			{
				parentForm.setLinkMode(true);
				mdiForm.getViewMenu_LinkMode().setState(true);
			}
		}
		
		// Determine whether the user has clicked a link
		
		if (linkSelected(e))
			bLinkSelected = true;
		else
		{
			bLinkSelected = false;
			
			// Remove the highlight from the connected ports
			
			mdiForm.setPortsWithLinkHighlight(null, null, drawPane.getParentForm());
		}
		
		// Must request focus so that the panel can handle keyboard
		// events, such as for deleting links
			
		drawPane.requestFocus();
		
		// Remove the focus from modules
		
		mdiForm.clearAllModules();
	}
	
	private boolean linkSelected(MouseEvent e)
	{
		// Create an invisible rectangle at the tip of the mouse cursor	and
		// use the Line2D's intersect method to determine whether the line
		// intersects this rectangle
		
		Rectangle rect = new Rectangle(e.getX() - 3, e.getY() - 3, 6, 6);
		HashMap links = drawPane.getLinks();
		if (links.size() > 0)
		{
			Set set = links.keySet();
			Iterator iter = set.iterator();
			Link link = null;
			int x1, y1, x2, y2;
			Line2D.Double line = null;
			ModulePort from_Port, to_Port;
			while (iter.hasNext())
			{
				link = ((Link)links.get((String)iter.next()));
				
				// If the composite link had been manipulated, make sure that
				// the correct join is selected (if any)
					
				link.resetCurrentJoin();
				
				if (link.getPath() == null)
				{
					from_Port = link.getFromPort();
					to_Port = link.getToPort();
					x1 = from_Port.xPos();
					y1 = from_Port.yPos();
					x2 = to_Port.xPos();
					y2 = to_Port.yPos();
					line = new Line2D.Double(x1, y1, x2, y2);
					if (line.intersects(rect))
					{
						linkSegment = -1;
						joinSelected = -1;
						selectLink(link);
						drawPane.paintLinks();
						return true;
					}
				}
				else
				{
					if (compositeLinkSelected(rect, link.getPath(), e))
					{
						selectLink(link);
						drawPane.paintLinks();
						return true;
					}	
				}
			}
			drawPane.setSelectedLink(null);
			drawPane.paintLinks();
			return false;
		}
		else
			return false;
	}
	
	private void selectLink(Link link)
	{
		// given a link make it selected
		
		drawPane.setSelectedLink(link);
		
		// Highlight the two ports that terminate the link
		
		drawPane.setSelectedLinkPorts(link);
		
		drawPane.paintLinks();
		link.setSelected(true);
		
		// Remove the focus from modules
		
		mdiForm.clearAllModules();
	}
	
	private boolean compositeLinkSelected(Rectangle rect, GeneralPath path, MouseEvent e)
	{
		// Given the path of a composite link, break it into its constituent
		// line segments and determine whether any intersect the rectangle rect
		
		PathIterator pi = path.getPathIterator(null);
		double[] coords = new double[2];
		Point start = null;
		Point end = null;
		Line2D.Double line = null;
		int iteration = -1;
		while (!pi.isDone())
		{
			if (end != null)
				start = end;
			end = null;
			int segtype = pi.currentSegment(coords);
			switch (segtype) 
			{
				case PathIterator.SEG_MOVETO:
					start = new Point((int)coords[0], (int)coords[1]);
					iteration++;
					break;
				case PathIterator.SEG_LINETO:
					end = new Point((int)coords[0], (int)coords[1]);
					iteration++;
					break;
			}
			
			// Create a line out of the current segment and test this
			// to see if it intersects rect (the rectangle at the 
			// mouse pointer
			
			if ((start != null) && (end != null))
			{
				line = new Line2D.Double(start, end);
				if (line.intersects(rect))
				{
					// The user has clicked the path
					linkSegment = iteration;
					joinSelected = getSelectedJoin(start, end, e);
					return true;
				}
			}
			
			pi.next();
		}
		return false;
	}
	
	private int getSelectedJoin(Point start, Point end, MouseEvent e)
	{
		// If a composite link was selected and the mouse was clicked
		// within a certain distance of either the start or end point of
		// the clicked segment, return the path indice of the start or end point
		// This represents a join in the link
		
		Point mousePoint = new Point(e.getX(), e.getY());
		
		double distanceToStart = GuiUtil.pointDistance(start, mousePoint);
		double distanceToEnd = GuiUtil.pointDistance(end, mousePoint);
		
		if (distanceToStart < 10)
			return (linkSegment - 1);
		else if (distanceToEnd < 10)
			return linkSegment;
		else
			return -1;
	}
	
	public void mouseMoved(MouseEvent e)
	{
		// When the mouse moves off of a port and onto the drawing
		// canvas, remove the highlight from the port on the module that currently has it.
		
		GuiUtil.removeCompatHighlight();
		
		DefaultVisualModule modWithPortHighlight = mdiForm.getModuleWithPortHighlight();
		if (modWithPortHighlight != null)
		{
			modWithPortHighlight.MouseOverInPort(-1);
			modWithPortHighlight.MouseOverOutPort(-1);
			
			if (modWithPortHighlight.getParentForm().getLinkMode())
				modWithPortHighlight.repaint();
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		bLinkSelected = false;
		
		Link link = drawPane.getSelectedLink();
		if (link != null)
		{
			if (link.getPath() != null)
			{
				link.validateLastJoin(link.getLastJoinMoved());
				//
				Point lJoin = link.getLeftLastJoinMoved();
				Point rJoin = link.getRightLastJoinMoved();
				//
				link.validateLastJoin(lJoin);
				link.validateLastJoin(rJoin);
			}
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		// If the user has selected a link and is now dragging it,
		// configure the link so that the drag point becomes a join
		// which can be arbitrarily moved w.r.t to the link's fixed points
		
		if (bLinkSelected)
		{
			Link link = drawPane.getSelectedLink();
			if (link != null)
			{
				link.dragLink(e.getX(), e.getY(), linkSegment, joinSelected);
				linkSegment = -1;
				drawPane.paintLinks();
			}
		}
	}
	
	/**
	* Method to restore transient/static object references after
	* deserialisation
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, 
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		mdiForm = parent_gui.Mdi.getInstance();
	}
}

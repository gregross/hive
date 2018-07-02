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
 * DrawingCanvas
 *
 * Class represents the surface upon which the visual composition takes place.
 *
 *  @author Greg Ross
 */
package parent_gui;

import javax.swing.JLayeredPane;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.*;
import java.awt.*;
import java.awt.RenderingHints;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.awt.Graphics2D;

public class DrawingCanvas extends JLayeredPane
{
	
	private static Mdi mdiForm;
	private DrawingCanvasMouseListener canvasMouseListener;
	
	// Instance of the JInternalFrame descendant that holds this component
	
	private DrawingForm parent;

	// ***************************** Transient link stuff ****************************
	
	// Coordinates of a transient link
	
	private float x1;
	private float y1;
	private float x2;
	private float y2;
	transient private Object oldRenderValue;
	
	// Variables to restore the Graphics for painting objects such as the visual modules
	// further down the containment hierarchy.
	
	transient private BasicStroke oldStroke;
	
	// Instance of a line
	
	transient private Line2D.Double line = null;
	
	// Make sure that a transient link is only drawn when the user dragging
	// from the port of a visual module
	
	private boolean bDrawTransient = false;
	
	private HashMap links;
	
	// The hidden buffer we paint to during double-buffering
	
	transient private BufferedImage bi;
	
	// Make sure that the BufferedImage instance is created only once
	// This is to enhance performance
	
	private boolean bBufferCreated = false;
	
	// The graphics object returned by BufferedImage
	
	transient private Graphics2D big;
	transient private GradientPaint redtowhite;
	
	transient private BasicStroke stroke;
	
	// Variable to store the width and height of the drawing area. These
	// are used to tell determine whether it has been resized and therefore
	// a new buffered image must be created
	
	private int oldWidth = 0;
	private int oldHeight = 0;
	private boolean bScrollInvalidated = false;
	
	// ****************************************************************************
	
	public DrawingCanvas(Mdi mdiForm, DrawingForm parent)
	{
		super();
		this.mdiForm = mdiForm;
		this.parent = parent;
		
		// Set up the mouse listener.
		
		canvasMouseListener = new DrawingCanvasMouseListener(this.mdiForm, this);
		addMouseListener(canvasMouseListener);
		addMouseMotionListener(canvasMouseListener);
		DrawingCanvasKeyListener keyListener = new DrawingCanvasKeyListener(this.mdiForm, this);
		addKeyListener(keyListener);
		
		// Get the a reference to the links collection associated with this view
		
		links = mdiForm.getModvp_Links();
	}
	
	public void addLink(VisualModule from_Mod, VisualModule to_Mod, ModulePort from_Port, ModulePort to_Port)
	{
		String linkKey = from_Mod.getKey() + "_" + to_Mod.getKey() + "_" + from_Port.getKey()
		+ "_" + to_Port.getKey();
		
		Link link = new Link(from_Mod, to_Mod, from_Port, to_Port, linkKey);
		if (!links.containsKey(linkKey))
		{
			links.put(linkKey, link);
			
			// Register the ports with each other as being Observable and an
			// Observer
			
			from_Port.addObservablePort(to_Port);
			to_Port.addObservablePort(from_Port);
			
			// Select the new link
			
			setSelectedLink(link);
			mdiForm.clearAllModules();
			link.setSelected(true);
			
			// Give the draw pane the focus so that keyboard events are detected
			
			requestFocus();
		}
		
	}
	
	public HashMap getLinks()
	{
		return links;	
	}
	
	public void paintComponent(Graphics g)
	{	
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		if (bDrawTransient)
			drawTransientLink(g2, Link.SELECTED_FROM_COLOR, Link.SELECTED_TO_COLOR, true);
		
		if (links.size() > 0)
			drawLinks(g2);
	}
	
	public void setScrollInvalidation(boolean bValid)
	{
		// This is rtue if the scoll bars i the parent form have
		// been adjusted
		
		bScrollInvalidated = bValid;
	}
	
	public void paintLinks()
	{
		// Create the off-screen buffer for graphics rendering
		
		int w, h;
		
		w = (new Double(getSize().getWidth())).intValue();
		h = (new Double(getSize().getHeight())).intValue();
		
		if (!bBufferCreated || (oldWidth != w) || (oldHeight != h) || bScrollInvalidated)
		{
			bScrollInvalidated = false;
			oldWidth = w;
			oldHeight = h;
			bi = (BufferedImage)createImage(w, h);
			big = bi.createGraphics();
			bBufferCreated = true;
		}
		
		// Create the graphics objects
		
		Graphics g = getGraphics();
		Graphics2D g2d = (Graphics2D)g;
		
		Rectangle oldClip = big.getClipBounds();
		
		g2d.setClip(GuiUtil.getClipShape(this));
		
		big.setColor(Color.black);
		big.fill(GuiUtil.getClipShape(this));
		
		if (bDrawTransient)
			drawTransientLink(big, Link.SELECTED_FROM_COLOR, Link.SELECTED_TO_COLOR, true);
		
		if (links.size() > 0)
			drawLinks(big);
			
		g2d.drawImage(bi, 0, 0, this);
		g2d.setClip(oldClip);
	}
	
	public void drawLinks(Graphics2D g2)
	{
		// Given the stored references to links within the links collection
		// draw them between the visual modules
		
		Set set = links.keySet();
		Iterator iter = set.iterator();
		Link link = null;
		ModulePort from_Port = null;
		ModulePort to_Port = null;
		int xFrom, yFrom, xTo, yTo;
		Color paintColour = null;
		Color paintColour2 = null;
		while (iter.hasNext())
		{
			link = ((Link)links.get((String)iter.next()));
			from_Port = link.getFromPort();
			to_Port = link.getToPort();
			this.x1 = from_Port.xPos();
			this.y1 = from_Port.yPos();
			this.x2 = to_Port.xPos();
			this.y2 = to_Port.yPos();
			
			// Set the correct colour (depending upon the select state of the link)
			
			if (link.getSelected())
			{
				paintColour = Link.SELECTED_FROM_COLOR;
				paintColour2 = Link.SELECTED_TO_COLOR;
			}
			else
			{
				paintColour = Link.UNSELECTED_FROM_COLOR;
				paintColour2 = Link.UNSELECTED_TO_COLOR;
			}
				
			// Update the link in case one of the connecting modules has moved
			
			link.updatePath();
			
			// If it is a selection link then draw it thinner than a data link
			
			boolean bDataLink = true;
			if ((from_Port.getPortMode() == ScriptModel.SELECTION_PORT) && 
			(to_Port.getPortMode() == ScriptModel.SELECTION_PORT))
				bDataLink = false; // If the link is between two selection ports, draw it thinner
			
			// Determine whether it's a composite link
			
			if (link.getPath() == null)
				drawTransientLink(g2, paintColour, paintColour2, bDataLink);
			else
				drawCompositeLink(g2, link, paintColour, paintColour2, bDataLink);
		}
	}
	
	private void drawCompositeLink(Graphics2D g2, Link link, Color lineColour, Color paintColour2, boolean bDataLink)
	{
		
		// Determine the current graphics context so that it can be restored
		
		oldRenderValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		oldStroke = (BasicStroke)g2.getStroke();
		redtowhite = new GradientPaint(x1, y1, lineColour, x2, y2, paintColour2);
		g2.setPaint(redtowhite);
		
		// Draw the path (composite link)
		
		if (bDataLink)
			stroke = new BasicStroke(Link.LINE_THICKNESS_DATA, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		else
			stroke = new BasicStroke(Link.LINE_THICKNESS_SELECTION, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		
		g2.setStroke(stroke);
		
		g2.draw(link.getPath());
		
		// Restore the graphics context
		
		g2.setStroke(oldStroke);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldRenderValue);
	}
	
	private void drawTransientLink(Graphics2D g2, Color lineColour, Color paintColour2, boolean bDataLink)
	{	
		oldRenderValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
		// Set the line style.
		// Thickness
		
		oldStroke = (BasicStroke)g2.getStroke(); 
		
		if (bDataLink)
			stroke = new BasicStroke(Link.LINE_THICKNESS_DATA, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		else
			stroke = new BasicStroke(Link.LINE_THICKNESS_SELECTION, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);
		
		// Set a gradient paint
		
		GradientPaint redtowhite = new GradientPaint(x1, y1, lineColour, x2, y2, paintColour2);
		g2.setPaint(redtowhite);
		
		// Shape (line)
		
		if (line == null)
			line = new Line2D.Double(this.x1, this.y1, this.x2, this.y2);
		else
			line.setLine(this.x1, this.y1, this.x2, this.y2);
		
		// Draw the line to the buffer
		
		g2.draw(line);
		
		// Reset the graphic context.
		
		g2.setStroke(oldStroke);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldRenderValue);
		
		bDrawTransient = false;
		line = null;
	}
	
	public void drawTransientLink(int x1, int y1, int x2, int y2)
	{
		bDrawTransient = true;
		this.x1 = (new Integer(x1)).floatValue();
		this.y1 = (new Integer(y1)).floatValue();
		this.x2 = (new Integer(x2)).floatValue();
		this.y2 = (new Integer(y2)).floatValue();
		
		paintLinks();
	}
	
	public DrawingForm getParentForm()
	{
		return parent;
	}
	
	public Link getSelectedLink()
	{
		return mdiForm.getSelected_ModVP_Link();	
	}
	
	public void setSelectedLink(Link link)
	{
		mdiForm.setSelected_ModVP_Link(link);
		
		// Deal with linked port highlighting
		
		setSelectedLinkPorts(link);
	}
	
	public void setSelectedLinkPorts(Link link)
	{
		// Given that a link is selected, highlight the ports
		// that are connected to that link
		
		ModulePort from_Port = null;
		ModulePort to_Port = null;
		
		if (link != null)
		{
			from_Port = link.getFromPort();
			to_Port = link.getToPort();
		}
			
		// Remove the link highlight from the previous ports (if any)
			
		mdiForm.setPortsWithLinkHighlight(from_Port, to_Port, getParentForm());
			
		// Highlight the newly selected link-ports
		
		boolean bHighlightFrom = false;
		boolean bHighlightTo = false;
		
		if (link != null)
		{
			if (from_Port != null)
				from_Port.setLinkHighlight(true);
			
			if (to_Port != null)
				to_Port.setLinkHighlight(true); 
		}
	}
	
	public void setDrawTransient(boolean bDrawTransient)
	{
		this.bDrawTransient = bDrawTransient;	
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

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
 * Contributor(s): Matthew Chalmers <matthew@dcs.gla.ac.uk>
 *                 Alistair Morrison <morrisaj@dcs.gla.ac.uk>
 *                 Greg Ross <gr@dcs.gla.ac.uk>
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
 * VisualModuleMouseListener
 *
 * Class represents the mouse listener adapter for a visual module.
 * This determines whether the mouse is selecting, moving or resizing 
 * module representations and draggin links from port to port
 *
 *  @author Greg Ross
 */
package parent_gui;
 
import javax.swing.event.*;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.*;
import java.awt.Color;
import java.util.*;

public class VisualModuleMouseListener extends MouseInputAdapter implements java.io.Serializable
{
	
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The visual module that represents an algorithm or visualisation
	
	private VisualModule visMod;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	private DrawingCanvas drawPane;
	
	// Determine which grab handle is selected
	
	static final int NW_HANDLE = 0;
	static final int N_HANDLE = 1;
	static final int NE_HANDLE = 2;
	static final int E_HANDLE = 3;
	static final int SE_HANDLE = 4;
	static final int S_HANDLE = 5;
	static final int SW_HANDLE = 6;
	static final int W_HANDLE = 7;
	
	// Variables and constants for determining which grab handle is selected.
	
	boolean yTop =  false;
	boolean yMiddle = false;
	boolean yBottom = false;
	boolean xLeft = false;
	boolean xMiddle = false;
	boolean xRight = false;
	
	// Co-ordinates and dimensions for grab handles.
	
	int grabHandleWidth = 6;
	int grabHandleHeight = 6;
	
	// Determine when the mouse is over a port
	
	boolean bMouseOverPort = false;
	
	// Determine when the user is dragging a link from a port
	
	boolean bDragFromPort = false;
	
	// Has the user just tried to create a link?
	
	boolean bCreateLink = false;
	
	// If the user is attempting to create a link, hold references to the
	// ports and modules associated by the link
	
	ModulePort from_Port, to_Port; // from_Port is always an output and to_Port is always an input port
	VisualModule from_Module, to_Module; // Likewise for the visual modules
	
	// Determine where the user initially clicked the mouse
	
	int clickX, clickY;
	
	VisualModule otherModule = null;
	
	public VisualModuleMouseListener(VisualModule visMod, Mdi mdiForm)
	{
		this.visMod = visMod;
		this.mdiForm = mdiForm;
		drawPane = this.visMod.getParentForm().getDrawPane();
		
		// Get the linking cursor
			
		//custCursor = new CustCursor(visMod.getToolkit());
	}
	
	public void mouseClicked(MouseEvent e)
    	{
		// Show the drag points on the border of the module so that
		// the user can use them for re-sizing it.
		
		// First clear all previous module selections.
		
		if (visMod.getParentForm().getLinkMode())
		{
			mdiForm.clearAllModules();
			visMod.setSelected(true);
		}
		
		// If one of the links was selected, unselect it
		
		visMod.getParentForm().getDrawPane().setSelectedLink(null);
		visMod.getParentForm().getDrawPane().paintLinks();
		
		// Must request focus so that the label can handle keyboard
		// events.
		
		visMod.requestFocus();
		
		// Does the user want to drag a link from a port
		
		if (bMouseOverPort)
			bDragFromPort = true;
		else
			bDragFromPort = false;
	}
	
	public void mousePressed(MouseEvent e)
	{
		if (e.getComponent().getName() != null)
		{ 
			mouseClicked(e);
			
			// Store module dimension for later use when resizing.
			
			visMod.setLastRenderHeight(visMod.getHeight());
			visMod.setLastRenderWidth(visMod.getWidth());
			
			visMod.bringToFront();
			
			// Get co-ordinates in case the user is about to drag
			// the module.
			
			Double x = new Double(e.getX());
			Double y = new Double(e.getY());
			visMod.setMouseDiffX(x.intValue());
			visMod.setMouseDiffY(y.intValue());
			visMod.setOldCursorX(visMod.getMouseDiffX());
			visMod.setOldCursorY(visMod.getMouseDiffY());
			
			clickX = x.intValue();
			clickY = y.intValue();
			
			if (visMod.getParentForm().getLinkMode())
			{
				checkMovement(x, y);
				highlightCompatiblePorts();
			}
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		// Store module dimension for later use when resizing.
		
		visMod.setLastRenderHeight(visMod.getHeight());
		visMod.setLastRenderWidth(visMod.getWidth());
		
		// Move the child components to the correct positions.
		
		visMod.revalidate();
		
		// The user is no longer dragging a link from a port
		
		bDragFromPort = false;
		
		// If the user had just dragged the end of a transient link over another
		// module's port and released it, create a link if it is valid
		
		if (bCreateLink)
		{
			visMod.getParentForm().getDrawPane().addLink(from_Module, to_Module, from_Port, to_Port);
		}
		
		bCreateLink = false;
		from_Port = null;
		to_Port = null;
		from_Module = null;
		to_Module = null;
		
		visMod.getParentForm().getDrawPane().setDrawTransient(false);
		visMod.getParentForm().getDrawPane().paintLinks();
		
		// Restore the port highlight
		
		visMod.getParentForm().setDragHighlightPort(null);
		
		GuiUtil.removeCompatHighlight();
		
		if (visMod.getParentForm().getLinkMode())
			visMod.repaint();
	}
	
	public void mouseDragged(MouseEvent e)
	{
		bCreateLink = false;
		from_Port = null;
		to_Port = null;
		from_Module = null;
		to_Module = null;
		
		// Allow the user to drag the modules about the screen and
		// resize.
		
		// Make sure that it's a module that is being dragged.
		
		if (e.getComponent().getName() != null)
		{	
			// Move the child components to the correct positions.
			
			visMod.revalidate();
			
			Double x = new Double(e.getX());
			Double y = new Double(e.getY());
			
			if ((visMod.getResizing() == true) && (visMod.getParentForm().getLinkMode() == false))
			{
				// The user wants to resize the module.
				
				visMod.resizeModule(x.intValue(), y.intValue());
				
				// Redraw any links
				
				visMod.getParentForm().getDrawPane().paintLinks();
			}
			else if (bDragFromPort)
			{
				// The user wants to drag a link from a port
				
				Point origin = visMod.getLocation();
				int originX = (new Double(origin.getX())).intValue();
				int originY = (new Double(origin.getY())).intValue();
				int endX = originX + x.intValue();
				int endY = originY + y.intValue();
				int startX = originX + clickX;
				int startY = originY + clickY;
				
				// The user is dragging a link from the port of the visual module
				// Draw the link as it's dragged (a transient link)
				
				visMod.getParentForm().getDrawPane().drawTransientLink(startX, startY, endX, endY);
				linkDraggedOverPort(endX, endY);
			}
			else
			{
				// The user wants to reposition the module.
				
				int oldX, oldY, newX, newY = 0;
				JPanel modPanel;
				
				modPanel = visMod;
				
				// Get the module's position on the JPanel.
				
				x = new Double(modPanel.getLocation().getX());
				y = new Double(modPanel.getLocation().getY());
				oldX = x.intValue();
				oldY = y.intValue();
				
				// Get the cursor posistion.
				
				x = new Double(e.getX());
				y = new Double(e.getY());
				
				int clickPosX = visMod.getMouseDiffX();
				int clickPosY = visMod.getMouseDiffY();
				
				visMod.setMouseDiffX(visMod.getMouseDiffX() - x.intValue());
				visMod.setMouseDiffY(visMod.getMouseDiffY() - y.intValue());
				
				newX = oldX - visMod.getMouseDiffX();
				newY = oldY - visMod.getMouseDiffY();
				
				// Make sure that the cursor stays in a position
				// relative to where it was first placed.
				
				visMod.setMouseDiffX(clickPosX);
				visMod.setMouseDiffY(clickPosY);
				
				// Move the module to so that its centre follows the
				// mouse cursor.
				
				// Make sure that the X and Y position is within the 
				// bounds of the form.
				
				if (newX < 0)
					newX = 0;
				else if (newX > (modPanel.getParent().getWidth()	
					- modPanel.getWidth()))
					newX = (modPanel.getParent().getWidth()
						- modPanel.getWidth());
						
				if (newY < 0)
					newY = 0;
				else if (newY > (modPanel.getParent().getHeight() 
					- modPanel.getHeight()))
					newY = (modPanel.getParent().getHeight()
						- modPanel.getHeight());
						
				modPanel.setBounds(newX, newY, visMod.getWidth(), 
					visMod.getHeight());
					
				// Redraw any links
				
				visMod.getParentForm().getDrawPane().paintLinks();
			}
		}
		
		// Make way for keyboard reception.
		
		visMod.requestFocus();
	}
	
	public void checkMovement(Double x, Double y)
	{
		// When the mouse moves off of a port and onto the drawing
		// canvas, remove the highlight from the port on the module that currently has it.
		
		VisualModule modWithPortHighlight = mdiForm.getModuleWithPortHighlight();
		if ((modWithPortHighlight != null) && (modWithPortHighlight.getKey() != visMod.getKey()))
		{
			// Remove highlight from the port of the host module
			
			modWithPortHighlight.MouseOverInPort(-1);
			modWithPortHighlight.MouseOverOutPort(-1);
		}
		
		// If the mouse moves over a drag handle, change the cursor to
		// reflect it.
		
		if ((isGrabbingHandle(x.intValue(), y.intValue()) == true) && (visMod.getParentForm().getLinkMode() == false))
		{
			bMouseOverPort = false;
			visMod.MouseOverInPort(-1);
			visMod.MouseOverOutPort(-1);
			visMod.setResizing(true);
			visMod.setResizeDirection(getGrabHandle());
			switch(visMod.getResizeDirection())
			{
				case NW_HANDLE:
					visMod.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
					break;
				case N_HANDLE:
					visMod.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
					break;
				case NE_HANDLE:
					visMod.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
					break;
				case E_HANDLE:
					visMod.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					break;
				case SE_HANDLE:
					visMod.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
					break;
				case S_HANDLE:
					visMod.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
					break;
				case SW_HANDLE:
					visMod.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
					break;
				case W_HANDLE:
					visMod.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
					break;
				default:
				{
					visMod.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					visMod.setResizing(false);
				}
			}
		}
		else if (visMod.getParentForm().getLinkMode() == true)
		{
			// The form is in link mode. Check to see if the mouse is moving over a port
			
			bMouseOverPort = false;
			
			visMod.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			visMod.MouseOverInPort(mouseOverInPort(x, y));
			
			visMod.MouseOverOutPort(mouseOverOutPort(x, y));
			
			// If not over a port, set the mouse cursor to the defaul pointer
			
			if (!bMouseOverPort)
			{
				visMod.MouseOverInPort(-1);
				visMod.MouseOverOutPort(-1);
			}
		}
		else
		{
			bMouseOverPort = false;
			visMod.MouseOverInPort(-1);
			visMod.MouseOverOutPort(-1);
			visMod.setResizing(false);
			visMod.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void mouseMoved(MouseEvent e)
	{
		Double x = new Double(e.getX());
		Double y = new Double(e.getY());
		checkMovement(x, y);
	}
	
	/**
	* When the user presses the mouse over a port, highlight
	* all other compatible ports
	*/
	
	private void highlightCompatiblePorts()
	{
		// Make the mouse is over a port
		
		if (bMouseOverPort)
		{
			// Get the selected port
			
			// First determine which port we've selected
			
			String portKey = null;
			
			if (visMod.MouseOverOutPort() > -1 )
				portKey = "o" + visMod.MouseOverOutPort();
			else if (visMod.MouseOverInPort() > -1 )
				portKey = "i" + visMod.MouseOverInPort();
			
			ModulePort clickedPort = (ModulePort)visMod.getPorts().get(portKey);
			
			// Get iterator for the collection of existing modules
			
			HashMap modules = mdiForm.getModules();
			Set set = modules.keySet();
			Iterator iter = set.iterator();
			
			ModulePort otherPort;
			DefaultVisualModule otherModule;
			
			// Go through all modules in the view
			
			while (iter.hasNext())
			{
				// Go through all ports of each module
				
				otherModule = ((DefaultVisualModule)modules.get((String)iter.next()));
				
				// Make sure that we're not looking at ports on the same current module
				// and that we're only looking at other modules within the same view
				
				if ((otherModule != visMod) && 
					(visMod.getParentForm().getTitle().equals(otherModule.getParentForm().getTitle())))
				{
					// Get port iterator
					
					Set portSet = otherModule.getPorts().keySet();
					Iterator portIter = portSet.iterator();
					
					while (portIter.hasNext())
					{
						otherPort = (ModulePort)otherModule.getPorts().get((String)portIter.next());
						
						if (ScriptModel.portsCompatible(clickedPort , otherPort) && 
							ScriptModel.portsCompatible(otherPort , clickedPort))
						{
							otherPort.setCompatHighlight(true);
						}
						else
							otherPort.setCompatHighlight(false);
					}
				}
			}
		}
	}
	
	public int mouseOverInPort(Double x, Double y)
	{
		// See if the cursor is above the inport
		
		int result = -1;
		
		if (visMod.getInPorts() != null)
		{
			ArrayList inPorts = visMod.getInPorts();
			for (int i = 0; i < inPorts.size(); i++)
			{
				if (inPorts.get(i) != null)
				{
					if ((((Ellipse2D.Double)inPorts.get(i)).contains(x.doubleValue(), y.doubleValue())))
					{
						bMouseOverPort = true;
						result = i;
					}
				}
			}
		}
		return result;
	}
	
	public int mouseOverOutPort(Double x, Double y)
	{
		// See if the cursor is above the outPort
		
		int result = -1;
			
		if (visMod.getOutPorts() != null)
		{
			ArrayList outPorts = visMod.getOutPorts();
			for (int i = 0; i < outPorts.size(); i++)
			{
				if (outPorts.get(i) != null)
				{
					if ((((Ellipse2D.Double)outPorts.get(i)).contains(x.doubleValue(), y.doubleValue())))
					{ 
						bMouseOverPort = true;
						result = i;
					}
				}
			}
		}
		return result;
	}
	
	private boolean isDraggingGrabHandleX(int x, int y)
	{
		// Determine whether the mouse pointer is clicked onto
		// a drag point in the X-axis.
		
		xLeft = false;
		xMiddle = false;
		xRight = false;
		
		if ((x >= 2) && (x <= (2 + grabHandleWidth)))
		{
			xLeft = true;
			return true;
		}
		else if ((x >= (visMod.getWidth() / 2) - (grabHandleWidth / 2)) && 
			(x <= (visMod.getWidth() / 2) + (grabHandleWidth / 2)))
		{
			xMiddle = true;
			return true;
		}
		else if ((x >= (visMod.getWidth() - (2 + grabHandleWidth))) &&
				(x <= (visMod.getWidth() - (2 + grabHandleWidth) + grabHandleWidth)))
		{
			xRight = true;
			return true;
		}
		else
			return false;
	}
	
	private boolean isDraggingGrabHandleY(int x, int y)
	{
		// Determine whether the mouse pointer is clicked onto
		// a drag point in the Y-axis
		
		yTop = false;
		yMiddle = false;
		yBottom = false;
		
		if ((y >= 2) && (y <= (2 + grabHandleHeight)))
		{
			yTop = true;
			return true;
		}
		else if ((y >= (visMod.getHeight() / 2) - (grabHandleHeight / 2)) && 
			(y <= (visMod.getHeight() / 2) + (grabHandleHeight / 2)))
		{
			yMiddle = true;
			return true;
		}
		else if ((y >= (visMod.getHeight() - (grabHandleHeight + 2))) && 
			(y <= (visMod.getHeight() - (grabHandleHeight + 2) + grabHandleHeight)))
		{
			yBottom = true;
			return true;
		}
		else
			return false;
	}
	
	private boolean isGrabbingHandle(int x, int y)
	{
		if ((isDraggingGrabHandleX(x, y) == true) && (isDraggingGrabHandleY(x, y) == true) 
			&& (visMod.getSelected() == true))
			return true;
		else
			return false;
	}
	
	private int getGrabHandle()
	{
		// Given the position of the mouse pointer determine
		// which drag handle is selected.
		
		if (xLeft == true)
		{
			if (yTop == true)
				return NW_HANDLE;
			else if (yMiddle == true)
				return W_HANDLE;
			else if (yBottom == true)
				return SW_HANDLE;
			else
				return -1;
		}
		else if (xMiddle == true)
		{
			if (yTop == true)
				return N_HANDLE;
			else if (yBottom == true)
				return S_HANDLE;
			else
				return -1;
		}
		else if (xRight == true)
		{
			if (yTop == true)
				return NE_HANDLE;
			else if (yMiddle == true)
				return E_HANDLE;
			else if (yBottom == true)
				return SE_HANDLE;
			else
				return -1;
		}
		else
			return -1;
	}
	
	public void linkDraggedOverPort(int endX, int endY)
	{
		// Given the location of the end of the link, determine
		// whether it is being dragged over a port in another
		// visual module
		
		// Get the rest of the modules
		
		HashMap modules = mdiForm.getModules();
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		
		Point origin;
		int originX, localX;
		int originY, localY;
		
		Double lx, ly;
		
		int portNumber;
		
		while (iter.hasNext())
		{
			// Make sure that we don't detect the link being dragged over the
			// source module (this one)
			
			otherModule =  (VisualModule)modules.get((String)iter.next());
			
			otherModule.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			if (!visMod.getKey().equals(otherModule.getKey()) &&  
				(visMod.getParentForm().getTitle().equals(otherModule.getParentForm().getTitle())))
			{
				// Translate the link's end-point coordinates from
				// those of the JLayeredPane to the local coordinates of
				// the module being tested.
				
				origin = otherModule.getLocation();
				originX = (new Double(origin.getX())).intValue();
				originY = (new Double(origin.getY())).intValue();
				localX = endX - originX;
				localY = endY - originY;
				lx = new Double((double)localX);
				ly = new Double((double)localY);
				
				portNumber = otherModule.getMotionListener().mouseOverInPort(lx,ly);
				if (portNumber > -1) 
				{
					// The user has dragged the end of the link over one of the input
					// ports of the other module
					
					// Determine whether a new link should be made
					
					String portKey = visMod.getPortWithHighlight();
					
					if (visMod.getPorts().containsKey(portKey))
					{
						from_Port = (ModulePort)visMod.getPorts().get(portKey);
						portKey = "i" + (new Integer(portNumber)).toString();
						if (otherModule.getPorts().containsKey(portKey))
						{	
							// Determine whether this is a valid link according to
							// the script model
							
							if (ScriptModel.portsCompatible(from_Port , ((ModulePort)otherModule.getPorts().get(portKey))))
							{
								to_Port = (ModulePort)otherModule.getPorts().get(portKey);
								
								// Flag the fact that we're creating a link so that
								// upon mouse-release it can be created
								
								bCreateLink = true;
								from_Module = visMod;
								to_Module = otherModule;
								
								// Highlight the port to show that this is OK
								
								visMod.getParentForm().setDragHighlightPort(to_Port);
								to_Port.setDragHighlight(ScriptModel.VALID_LINK_HIGHLIGHT);
							}
							else
							{
								// Highlight the port to show that this link is invalid
								
								visMod.getParentForm().setDragHighlightPort(((ModulePort)otherModule.getPorts().get(portKey)));
								((ModulePort)otherModule.getPorts().get(portKey)).setDragHighlight(ScriptModel.INVALID_LINK_HIGHLIGHT);
							}
							otherModule.repaint();
							break;
						}
					}
				}
				else
				{
					portNumber = otherModule.getMotionListener().mouseOverOutPort(lx,ly);
					if (portNumber > -1)
					{
						// The user has dragged the end of the link over one of the
						// output ports of the other module
						
						// Determine whether a new link should be made
						
						String portKey = visMod.getPortWithHighlight();
						if (visMod.getPorts().containsKey(portKey))
						{
							to_Port = (ModulePort)visMod.getPorts().get(portKey);
							portKey = "o" + (new Integer(portNumber)).toString();
							if (otherModule.getPorts().containsKey(portKey))
							{	
								// Determine whether this is a valid link according to
								// the script model
							
								if (ScriptModel.portsCompatible(((ModulePort)otherModule.getPorts().get(portKey)), to_Port))
								{
									from_Port = (ModulePort)otherModule.getPorts().get(portKey);
									
									// Flag the fact that we're creating a link so that
									// upon mouse-release it can be created
									
									bCreateLink = true;
									to_Module = visMod;
									from_Module = otherModule;
									
									// Highlight the port to show that this is OK
									
									visMod.getParentForm().setDragHighlightPort(from_Port);
									from_Port.setDragHighlight(ScriptModel.VALID_LINK_HIGHLIGHT);
								}
								else
								{
									// Highlight the port to show that this link is invalid
									
									visMod.getParentForm().setDragHighlightPort(((ModulePort)otherModule.getPorts().get(portKey)));
									((ModulePort)otherModule.getPorts().get(portKey)).setDragHighlight(ScriptModel.INVALID_LINK_HIGHLIGHT);
								}
								otherModule.repaint();
								break;
							}
						}
					}
					else
					{
						visMod.getParentForm().setDragHighlightPort(null);
						otherModule.repaint();
					}
				}
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

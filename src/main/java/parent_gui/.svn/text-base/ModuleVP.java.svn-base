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
 * ModuleVP (Module Visual Programming)
 *
 * Class represents an MDI child form.  This child form represents the canvas
 * upon which the algorithmic components and visualisations are represented
 * in a block diagram for visual programming.
 *
 *  @author Greg Ross
 */
package parent_gui;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.HashMap;

public class ModuleVP extends DrawingForm
{
	static ModuleVP instance;
	private static Mdi mdiForm;
	private static DrawingCanvas drawPane;
	
	// Co-ordinates of mouse drop.
	
	int mouseDropX = 0;
	int mouseDropY = 0;
	
	// Size of last dropped module.
	
	int moduleRenderWidth;
	int moduleRenderHeight;
	
	private String key = null;
	
	private String lastModName = null;
	
	// Stop the drag event from continuously drawing the module outline
	// when dragging from the toolbar
	
	private double oldX, oldY;
	
	public ModuleVP(String title, Mdi mdi) 
	{
		super(title, mdi);
		mdiForm = mdi;
		
		this.drawPane = super.getDrawPane();
		reloadGraph();
	}
	
	public DrawingCanvas getDrawingCanvas()
	{
		return drawPane;
	}
	
	public void setAppearance()
	{
		setVisible(true);
		setLocationAndSize();	
	}
	
	public static ModuleVP getInstance(String title, Mdi mdi)
	{
		if (instance == null)
		{
			instance = new ModuleVP(title, mdi);
		}
		else
			try 
			{
				instance.setSelected(true);
			} 	
			catch (java.beans.PropertyVetoException e) {}
			
		// Make the menu item in mdiForm for opening this view checked
		
		mdiForm.getOpenCanvasMenu().setState(true);
		mdiForm.getViewMenu_TileVisHoriz().setEnabled(true);
		mdiForm.getViewMenu_TileVisVerti().setEnabled(true);
		mdiForm.getViewMenu_LinkMode().setEnabled(true);
		
		return instance;
	}
	
	public static boolean formIsOpen()
	{
		if (instance == null)
			return false;	
		else
			return true;	
	}
	
	public void setLocationAndSize()
	{
		maximise();
	}
	
	// Implement the DropTargetListener interface methods.
	
	public void dragEnter(DropTargetDragEvent e)
	{
		mouseDropX = 0;
		mouseDropY = 0;
	}
	
	public void dragExit(DropTargetEvent e)
	{
		update();
	}
	
	public void dragOver(DropTargetDragEvent e)
	{
		// If the user is currently dragging a module over the form then
		// draw an outline of where the module will be placed once dropped.
		
		String modName = mdiForm.getDraggedModuleName();
		
		// Make sure that we only execute the following code once during a drag operation
		
		if (modName == null)
		{	
			e.rejectDrag();
			return;
		}
		
		if (!modName.equals(lastModName))
		{
			Dimension d = getModuleDimension(modName, getDrawPane());
			this.moduleRenderWidth = (new Double(d.getWidth())).intValue();
			this.moduleRenderHeight = (new Double(d.getHeight())).intValue();
			lastModName = modName;
		}
		
		if ((oldX != e.getLocation().getX()) || (oldY != e.getLocation().getY()))
		{
			Double x = new Double(e.getLocation().getX());
			Double y = new Double(e.getLocation().getY());
			
			Graphics g = drawPane.getGraphics();
			
			g.setXORMode(Color.lightGray);
			
			// Overdraw the old shadow.
			
			if ((!((mouseDropX == 0) && (mouseDropY == 0))))	
				g.drawRect(mouseDropX, mouseDropY, moduleRenderWidth, moduleRenderHeight);
			
			// Make this co-ordinate the top left corner of the rendered
			// module.
			
			mouseDropX = x.intValue() - (moduleRenderWidth / 2);
			mouseDropY = y.intValue() - (moduleRenderHeight / 2);
			g.drawRect(mouseDropX, mouseDropY, moduleRenderWidth, moduleRenderHeight);
			
			// Store the new position so that we know not continuously paint over it
			
			oldX = e.getLocation().getX();
			oldY = e.getLocation().getY();
		}
	}
	
	public void drop(DropTargetDropEvent e)
	{
		try
		{
			key = null;
			
			// Draw a module on the canvas.
			
			// First get the module name (the DrgLabel's text).
			
			String modName
				= (String)e.getTransferable().getTransferData(DataFlavor.stringFlavor);
			
			mdiForm.IncrementModuleInstances(mdiForm.getDraggedModuleName());
			
			// Get the drop location and store the co-ordinate.
			
			Double x = new Double(e.getLocation().getX());
			Double y = new Double(e.getLocation().getY());
			
			// Make this co-ordinate the top left corner of the rendered
			// module.
			
			mouseDropX = x.intValue() - (moduleRenderWidth / 2);
			mouseDropY = y.intValue() - (moduleRenderHeight / 2);
			
			// Render in this view.
			
			setDropCoords(mouseDropX, mouseDropY);
			DefaultVisualModule visMod = renderModule(modName, false);
			
			key = null;
			
			// Select the newly added module
			
			setModWithFocus(visMod);
			visMod.bringToFront();
			visMod.setFocus();
			
			// Give this form the focus.
			
			try 
			{
				setSelected(true);
			} 	
			catch (java.beans.PropertyVetoException e3) {}
			
			// Select the newly added module (again because
			// there's some weird focus thing going on)
			
			setModWithFocus(visMod);
			visMod.bringToFront();
			visMod.setFocus();
		}
		catch(IOException e2){}
		catch(UnsupportedFlavorException e2){}
		catch(InvalidDnDOperationException e3){}
		
		mdiForm.setDraggedModuleName(null);
	}
	
	public DefaultVisualModule renderModule(String modName, int mouseDropX, int mouseDropY)
	{
		// Whenever the user drags a visual module onto the interaction-flow
		// frame, this method is called to automatically add the same module
		// to the algorithm/data-flow view.
		
		setDropCoords(mouseDropX, mouseDropY);
		this.mouseDropX = mouseDropX;
		this.mouseDropY = mouseDropY;
		return renderModule(modName, false);
	}
	
	public void internalFrameClosed(InternalFrameEvent e)
	{
		instance = null;
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

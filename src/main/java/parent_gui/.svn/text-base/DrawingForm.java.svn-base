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
 * DrawingForm
 *
 * This class is the superclass of the drawing pane window for visual programming (ModuleVP).
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.awt.event.*;
import java.awt.*;
import java.awt.dnd.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;

// Import algorithmic classes

import alg.*;

public class DrawingForm extends JInternalFrame implements InternalFrameListener, DropTargetListener, AdjustmentListener
{
	private static Mdi mdiForm;
	
	private DrawingCanvas drawPane;
	
    	// Co-ordinates of mouse drop
	
    	int mouseDropX = 0;
	int mouseDropY = 0;
	
    	int mouseDiffX, mouseDiffY = 0;
	
	private String key = null;
	
	// Determine whether the visual modules are in linking mode
	
	private boolean bLinkMode = false;
	
	// Hold a reference to the module that currently has the focus
	
	private DefaultVisualModule modWithFocus;
	
	// Reference to the module port that is highlighted because a link is being dragged over it
	
	private ModulePort dragHighlightPort = null;
	
	public DrawingForm(String title, Mdi mdi)
	{
		super(title, 
			true, // resizable
			true, // closable
			true, // maximizable
			true);// iconifiable
			
		mdiForm = mdi;
		
		drawPane = new DrawingCanvas(mdiForm, this);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addInternalFrameListener(this);
		mdi.getDesktop().add(this);
		moveToFront();
		try 
		{
			setSelected(true);
		} 	
		catch (java.beans.PropertyVetoException e) {}
		
		// Set this form as a drag 'n' drop target.
		// Do this by adding a JLayeredPane as the drawing surface.
		// Don't use a layout manager as we will want absolute control
		// over the positioning of any Swing components that appear. 
		// See http://java.sun.com/docs/books/tutorial/uiswing/layout/none.html
		
		this.drawPane = drawPane;
		drawPane.setLayout(null);
		drawPane.setOpaque(true);
		drawPane.setBackground(Color.black);
		
		// Add a scroll pane.
		
		drawPane.setPreferredSize(new Dimension(mdiForm.getDesktop().getWidth() - 50, 
		mdiForm.getDesktop().getHeight() - 50));
		JScrollPane scrollPane = new JScrollPane(drawPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setContentPane(scrollPane);
		
		// Set this form's drawing surface as the drop target.
		
		new DropTarget(drawPane, DnDConstants.ACTION_MOVE, this);
		
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
		scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
		
		ImageIcon icon = new ImageIcon("images/HIVE2.gif");
		setFrameIcon(icon);
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		// If the view has been scrolled, then the buffered image
		// for drawing the visualisation schema will have to be
		// recreated
		
		drawPane.setScrollInvalidation(true);
	}
	
	public void setHalfMDISize(boolean bTop)
	{
		// Make the form half the height of the MDI and the same width.
		
		try
		{
			setMaximum(false);
		}
		catch (PropertyVetoException e){}
		setSize(mdiForm.getDesktop().getWidth(), (mdiForm.getDesktop().getHeight() / 2));
		if (bTop == true)
			setLocation(0, 0);
		else
			setLocation(0, (mdiForm.getDesktop().getHeight() / 2));	
	}
	
	public void maximise()
	{
		// Maximise this form.
	
		setSize(mdiForm.getDesktop().getWidth(), mdiForm.getDesktop().getHeight());
		setLocation(0, 0);
	}
    
    	public void update()
	{
		repaint();
	}
	
	// Implement the InternalFrameListener methods.
    
    	public void internalFrameClosing(InternalFrameEvent e)
	{
		// Make the menu item in mdiForm for opening this view unchecked
		
		mdiForm.getOpenCanvasMenu().setState(false);
		mdiForm.getViewMenu_TileVisHoriz().setEnabled(false);
		mdiForm.getViewMenu_TileVisVerti().setEnabled(false);
		mdiForm.getViewMenu_LinkMode().setEnabled(false);
		
		try
		{
			setMaximum(false);
		}
		catch (PropertyVetoException e2){}
	}

	public void internalFrameClosed(InternalFrameEvent e){}
	public void internalFrameOpened(InternalFrameEvent e){}
	public void internalFrameIconified(InternalFrameEvent e){}
	public void internalFrameDeiconified(InternalFrameEvent e){}
	public void internalFrameActivated(InternalFrameEvent e){}
	public void internalFrameDeactivated(InternalFrameEvent e){}
	
	// Accessor methods for determining the link mode of the modules on this drawing surface.
	
	public boolean getLinkMode()
	{
		return bLinkMode;
	}
	
	public void setLinkMode(boolean linkMode)
	{
		bLinkMode = linkMode;
		
		// If the link mode has been set to true then make all the modules
		// present their I/O ports
		
		if (bLinkMode == true)
		{
			// The user wishes to make links
			// First remove the grab handles from the currently selected module
			
			if (modWithFocus != null)
			{
				modWithFocus.setSelected(false);
				modWithFocus.repaint();	
			}
			
			// Now paint all of the modules with their I/O ports showing.
			
			mdiForm.clearAllModules();
		}
		else
		{	
			// The user wants to exit link mode
			// First remove all of the I/O ports from the display
			
			mdiForm.clearAllModules();
			
			// Now show the currently selected module with its grab handles showing.
			
			if (modWithFocus != null)
			{
				modWithFocus.setSelected(true);
				modWithFocus.repaint();
			}
		}
	}
	 
	 public void dragEnter(DropTargetDragEvent dtde){}
	 public void dragOver(DropTargetDragEvent dtde){}
	 public void dropActionChanged(DropTargetDragEvent dtde){}
	 public void dragExit(DropTargetEvent dte){}
	 public void drop(DropTargetDropEvent dtde){}
	 
	 public Dimension getModuleDimension(String modName, DrawingCanvas dPane)
	 {
		// Get the height and width of an object
		
		DefaultVisualModule tempVisualModule = getModuleInstance(modName, dPane);
		if (tempVisualModule != null)
		{
			Dimension d = new Dimension(tempVisualModule.getWidth(),  tempVisualModule.getHeight());
			return d;
		}
		else
			return new Dimension(100,  100);
	 }
	 
	 private DefaultVisualModule getModuleInstance(String modName, DrawingCanvas dPane)
	 {
		 // Given a module name return the specific specialised instance of that module
		
		HashMap m = mdiForm.getToolBarClass().getReflectionClass().getClasses();
		
		if (m.size() > 0)
		{
			Set set = m.keySet();
			Iterator iter = set.iterator();
			DefaultVisualModule mod;
			
			while (iter.hasNext())
			{
				String classKey = (String)iter.next();
				try
				{
					Class c = ((Class)m.get(classKey));
					Class[] intArgsClass = new Class[] {mdiForm.getClass(), drawPane.getClass()};
					Constructor cons = c.getConstructor(intArgsClass);
					Object[] intArgs = new Object[] {mdiForm, drawPane};
					mod = (DefaultVisualModule)cons.newInstance(intArgs);
					
					if (mod.getName().equals(modName))
						return mod;
				}
				catch (Exception e){return null;}
			}
			
			return null;
		}
		else
			return null;
	 }
	 
	 public DefaultVisualModule renderModule(String modName, boolean bVisual)
	 {
		 // Given the co-ordinates where a dragged module was dropped,
		 // draw a representation of the module on the form.
		 
		 // Make sure that other modules are no longer selected (drag-handles visible).
		 
		 mdiForm.clearAllModules();
		 
		 // Now create a visual module
		 
		 DefaultVisualModule newModule = getModuleInstance(modName, drawPane);
		 
		 // Add the JPanel to the JLayeredPane
		 
		 drawPane.add(newModule);
		 
		 drawPane.repaint();
		 
		 newModule.setBounds(mouseDropX, mouseDropY, newModule.getWidth(),  newModule.getHeight());
		 
		 // Derive a unique, random ID for the new module.
		 
		 /*Integer iKey = new Integer(GuiUtil.randInteger(0, 1000));
		 key = iKey.toString();
		 HashMap mods = mdiForm.getModules();
		 while (mods.containsKey(key))
		 {
			 iKey = new Integer(GuiUtil.randInteger(0, 1000));
			 key = iKey.toString();
		 }*/
		 
		 Long lKey = new Long(System.currentTimeMillis());
		 key = lKey.toString();
		 
		 // Store the ID with the  module instance.
		 
		 newModule.setKey(key);
		 
		 newModule.bringToFront();
		 
		 // Add the module to the collection of rendered modules.
		 
		 mdiForm.addModule(key, newModule);
		 
		 // Remove the highlight from DragLabels on the toolbar
		 
		 mdiForm.getToolBarClass().removeLabelHighlights();
		 
		 return newModule;
	 }
	 
	 public void setDropCoords(int mouseDropX, int mouseDropY)
	 {
		  // Whenever the user drags a visual module onto the interaction-flow
		  // frame, this method is called to automatically add the same module
		  // to the algorithm/data-flow view.
		  
		  this.mouseDropX = mouseDropX;
		  this.mouseDropY = mouseDropY;
		  modWithFocus = null;
	  }
	  
	  public DrawingCanvas getDrawPane()
	  {
		  return  drawPane;   
	  }
	   
	   public DefaultVisualModule getModWithFocus()
	   {
		return modWithFocus;   
	   }
	   
	   public void setModWithFocus(DefaultVisualModule modWithFocus)
	   {
		this.modWithFocus = modWithFocus;   
	   }
	   
	   public void reloadGraph()
	   {
		// If the form has been closed after creating a graph 
		// (from visual modules and links), reload that graph
		
		// First re-render any visual modules
		
		HashMap modules = mdiForm.getModules();
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		VisualModule visMod;
		while (iter.hasNext())
		{
			visMod = (VisualModule)modules.get((String)iter.next());
			if (getTitle().equals(visMod.getParentForm().getTitle()))
			{
				drawPane.add(visMod);
				visMod.setParentForm(this);
				double dx = visMod.getLocation().getX();
				double dy = visMod.getLocation().getY();
				int ix = (new Double(dx)).intValue();
				int iy = (new Double(dy)).intValue();
				visMod.setBounds(ix, iy, visMod.getWidth(),  visMod.getHeight());
			}
		}
	   }
	   
	   public void setDragHighlightPort(ModulePort port)
	   {
		if (dragHighlightPort != null)
			dragHighlightPort.setDragHighlight(-1);
		dragHighlightPort = port;
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

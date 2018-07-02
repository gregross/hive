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
 * DragLabel
 *
 * Extends JLabel to provide a draggable label that represents a module to be 
 * manipulated in visual programming.
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.*;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;

public class DragLabel extends JLabel implements DragGestureListener,
						DragSourceListener
{
	private DragSource ds = DragSource.getDefaultDragSource();
	private boolean bDrawBorder = false;
	
	public DragLabel(String s)
	{
		super(s);
		
		int action = DnDConstants.ACTION_MOVE;
		ds.createDefaultDragGestureRecognizer(this, action, this);
	}
	
	public DragLabel(String s, int alignment)
	{
		super(s, alignment);
		
		int action = DnDConstants.ACTION_MOVE;
		ds.createDefaultDragGestureRecognizer(this, action, this);
		
		setBackground(Color.lightGray);
		setOpaque(true);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	
	public DragLabel(String s, Icon icon, int alignment)
	{
		super(s, icon, alignment);
		
		int action = DnDConstants.ACTION_MOVE;
		ds.createDefaultDragGestureRecognizer(this, action, this);
		
		setBackground(Color.lightGray);
		setOpaque(true);
		setBorder(BorderFactory.createLineBorder(Color.gray));
	}
	
	public void dragGestureRecognized(DragGestureEvent e)
	{
		try
		{
			Transferable t = new StringSelection(getText());
			e.startDrag(DragSource.DefaultCopyNoDrop, t, this);
		}
		catch(InvalidDnDOperationException e2)
		{
			System.out.println(e2);
		}
	}
	
	public void dragDropEnd(DragSourceDropEvent e){}
	
	public void dragEnter(DragSourceDragEvent e)
	{
		// Make sure that the correct cursor is shown while dragging
		// a module
		
		DragSourceContext context = e.getDragSourceContext();
		
		//intersection of the users selected action, and the source and target actions
		
		int myaction = e.getDropAction();
		if( (myaction & DnDConstants.ACTION_MOVE) != 0)
		{ 
			context.setCursor(DragSource.DefaultCopyDrop);
		} 
		else
		{
			context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}
	
	public void dragExit(DragSourceEvent e)
	{
		DragSourceContext context = e.getDragSourceContext();
		context.setCursor(DragSource.DefaultCopyNoDrop);
	}
	
	public void dragOver(DragSourceDragEvent e){}
	public void dropActionChanged(DragSourceDragEvent e){}
	
	public void highlight(boolean bDrawBorder)
	{
		this.bDrawBorder = bDrawBorder;	
		if (bDrawBorder)
			setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		else
			setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
}

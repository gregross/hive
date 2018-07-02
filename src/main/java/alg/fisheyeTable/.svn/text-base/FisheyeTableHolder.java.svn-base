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
 * Algorithmic test bed
 *  
 * FisheyeTableHolder
 * 
 * FisheyeTableHolder is a JPanel which contains a DataViewerTable and various
 * buttons for controlling the table.
 * It turns the table into a fisheye view when the fisheye view is selected,
 * replacing the ordinary scrollbar on the table with a slider to move the
 * fisheye view up or down.  It also contains a dialog which allows the 
 * user to choose which column to sort the table by
 * Note that the actual sort routine is coded into the table.
 *  
 *  @author 
 */ 

package alg.fisheyeTable;

import data.DataItemCollection;
import parent_gui.*;
import alg.Table;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.InputEvent;
import java.awt.Cursor;
import java.util.*;

public class FisheyeTableHolder extends JPanel implements ChangeListener,
							MouseMotionListener,
							java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The VisualModule parent
	
	private Table visModTable;
	
	/** The table*/
	
	protected DataViewerTable table;
	
	/**Scrollpane to contain the table*/
	
	protected JScrollPane scrollpane;
	
	/** panel to hold the buttons*/
	
	protected JPanel buttonPanel;
	
	/**This checkbox selects whether or not the table is in fisheye mode*/
	
	protected JCheckBox fishEyeCheckBox;
	
	/**This controls the position of the "bubble" created by the fisheye*/
	
	protected JSlider fishEyeSlider;
	
	/**Keeps track of the row heights before the fisheye was called*/
	
	private int currentRowHeights;
	
	/**used by the fisheye view to keep track of which row it last expanded
	*so that it can be shrunk again to 2 pixels*/
	
	private int previousRowModified=0;
	
	/**holds the slider bar for controlling the fisheye position*/
	
	protected JPanel fishEyeSliderPanel;
	
	/**Previous Y coordinate of mouse during right-click dragging - for fisheye*/
	
	private int prevY = 0;
	
	/**Current row we are sitting at in fisheye table*/
	
	private int currentTableRow=0;
	
	/**
	*Constructor - initialises the contents of the panel and loads the table
	*
	*@param dataItems the actual data to put in the table
	*/
	
	public FisheyeTableHolder(DataItemCollection dataItems, Mdi mdiForm, Table visModTable)
	{
		super(new BorderLayout());
		
		this.mdiForm = mdiForm;
		this.visModTable = visModTable;
		
		// Add the table
		
		table= new DataViewerTable(dataItems, mdiForm, this);
		
		// Construct and position top level components
		
		buttonPanel=new JPanel(new BorderLayout());
		scrollpane=new JScrollPane(table);
		fishEyeCheckBox=new JCheckBox("fisheye");
		
		// The increments on the slider are created to be the same as the
		// number of rows in the table
		
		fishEyeSlider=new JSlider(JSlider.VERTICAL,0,table.getRowCount(), 0);
		fishEyeSliderPanel=new JPanel(new GridLayout());
		fishEyeSliderPanel.add(fishEyeSlider);
		
		// Makes sure slider is the right way up
		
		fishEyeSlider.setInverted(true);
		fishEyeSlider.setVisible(false);
		
		// Start with fisheye mode off
		
		fishEyeCheckBox.setSelected(false);
		this.add(buttonPanel,BorderLayout.NORTH);
		this.add(scrollpane,BorderLayout.CENTER);
		buttonPanel.add(fishEyeCheckBox,BorderLayout.WEST);
		this.add(fishEyeSliderPanel,BorderLayout.EAST);
		table.addMouseMotionListener(this);
		
		// Add event listeners to the widgets
		
		fishEyeCheckBox.addChangeListener(this);
		fishEyeSlider.addChangeListener(this);
		
		// Store the hight of the rows so that it can be returned to normal
		// after fisheye mode is exited
		
		currentRowHeights=table.getRowHeight();
	}
	
	/**
	*Toggles the table between fisheye mode and normal mode.
	*Triggered by a change in the fishEyeCheckBox value.
	*/
	
	public void setFishEye()
	{
		if (!fishEyeCheckBox.isSelected()) 
		{
			table.setRowHeight(currentRowHeights);
			scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			fishEyeSlider.setVisible(false);	    
			revalidate();
			repaint();
		}
		else 
		{
			table.setRowHeight(2);
			fishEyeSlider.setVisible(true);
			revalidate();
			repaint();
		}
	}
	
	/**
	*Called by a change in the fishEyeSlider.  Moves the "bubble" view of the table to the correct position in the table.
	*/
	
	private void updateFishEye()
	{
		showTableRow(fishEyeSlider.getValue());
	}
	
	/**
	*This moves the focus and resizes the rows of the table corresponding to
	*the position on the slider.
	*/ 
	
	public void showTableRow(int row) 
	{
		//This makes sure that the previous set of rows covered by the bubble
		//are reset to height 2.  The exception handle will catch as soon as 
		//you overrun or underrun the tables and harmlessly exit
		
		for (int i=previousRowModified-7;i<previousRowModified+7;i++)
		{
			try
			{
				table.setRowHeight(i,2);
			}
			catch (Exception e){}
		}
		
		//This is to make sure that the table scrolls down in such a way that
		//the focused cell is somewhere in the middle of the table 
		//rather than at the edge.  It harmlessly exits if we are near
		//the end of the table
		
		 Rectangle rect;
		
		// Scrolls to the exact cell in focus.
		
		rect=table.getCellRect(row, 0, true);
		table.scrollRectToVisible(rect);
		
		// These resize the rows of the table around the focus to various sizes
		// again it will just die quietly if the table is near the
		// top or the bottom
		
		try
		{
			if (row > 0)
				table.setRowHeight(row-1,16);
			if (row > 1)
				table.setRowHeight(row-2,16);
			if (row > 2)
				table.setRowHeight(row-3,10);
			if (row > 3)
				table.setRowHeight(row-4,10);
			if (row > 4)
				table.setRowHeight(row-5,8);
			if (row > 5)
				table.setRowHeight(row-6,5);
			if (row > 6)
				table.setRowHeight(row-7,3);
		}
		catch (Exception e){}
		
		// This is the resize of the focused row
		
		table.setRowHeight(row,16);
		try
		{
			if (row < (table.getRowCount() - 6))
				table.setRowHeight(row+6,3);
			if (row < (table.getRowCount() - 5))
				table.setRowHeight(row+6,5);
			if (row < (table.getRowCount() - 4))
				table.setRowHeight(row+5,8);
			if (row < (table.getRowCount() - 3))
				table.setRowHeight(row+4,10);
			if (row < (table.getRowCount() - 2))
				table.setRowHeight(row+3,10);
			if (row < (table.getRowCount() - 1))
				table.setRowHeight(row+2,16);
			if (row < (table.getRowCount()))
				table.setRowHeight(row+1,16);
		}
		catch (Exception e){}
		
		// Update our tracker of the previous row.
		
		previousRowModified=row;
		
		// If the focus row is selected by clicking on the table,
		// set the value of the slider to represent the position
		// relative to the whole table
		
		fishEyeSlider.setValue(row);
	}
	
	private void mouseClick(MouseEvent e){}
	
	/**
	*implements @see ChangeListener interface
	*/
	
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource()==fishEyeCheckBox)
		{
			setFishEye();
		}
		else if (e.getSource()==fishEyeSlider)
		{
			if (!fishEyeSlider.getValueIsAdjusting())
			{
				updateFishEye();
			}
		}
	}
	
	/** 
	* Allow the table to determine whether we're in fisheye mode
	*/
	
	public boolean fisheyeMode()
	{
		return fishEyeCheckBox.isSelected();	
	}
	
	/**
	*Right mouse drag will cause the fisheye table to scroll up or down
	*adjusting the fisheyeslider at the same time
	*/
	public void mouseDragged(MouseEvent me)
	{
		if ( ((me.getModifiers() & InputEvent.BUTTON3_MASK)
			== InputEvent.BUTTON3_MASK)&fishEyeCheckBox.isSelected()) 
		{
			int newY=me.getY();
			
			if ((newY>prevY)&&(previousRowModified<table.getRowCount()-4))
				fishEyeSlider.setValue(previousRowModified+3);
			else if (previousRowModified>3)
				fishEyeSlider.setValue(previousRowModified-3);

			prevY=newY;
		}
	} 
	public void mouseMoved(MouseEvent e)
	{
		visModTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	* Return a reference to the Table VisualModule instance
	*/
	
	public Table getVisualModule()
	{
		return visModTable;
	}
	
	/**
	* Return a reference to the DataViewerTable JTable instance
	*/
	
	public DataViewerTable getDataViewerTable()
	{
		return table;	
	}
	
	public void setControlsEnabled(boolean bEnabled)
	{
		// Determine whether the fisheye check box should be enabled
		
		fishEyeCheckBox.setEnabled(bEnabled);
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

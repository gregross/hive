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
 * DataViewerTable
 * 
 * Allows the user to choose a column to sort by
 * Selecting a row or rows in the table causes the objects to be highlighted
 * in the scatterplot and sliders
 * Implements a fisheye lens to allow viewing of the whole table at once
 * Implements a system which permits barcharts to replace non ordinal columns
 *  
 *  @author Greg Ross
 */ 

package alg.fisheyeTable;

import data.DataItemCollection;
import parent_gui.*;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.*;
import java.awt.*;

public class DataViewerTable extends javax.swing.JTable implements Selectable, 
  							SelectionChangedListener,
							ListSelectionListener,
							MouseListener,
							java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	protected ArrayList          columnNames;
	protected ArrayList          columnTypes;
	
	// Stores the current user selection in the table
	
     	protected Set theSelection;
	private SelectionHandler selected; 
	
     	// Renders the numerical columns
	
     	protected HistogramTableCellRenderer histogramcellrenderer; 
	
     	// Renders ordinal columns
	
     	protected FisheyeTableTextRenderer textrenderer;
	
	// Render the icon in the table header columns to indicate sorting
	
	private SortColumnRenderer sortColRenderer;
	
	// The parent MDI form of the application
	
	private transient Mdi mdiForm;
	
	private DataItemCollection dataItems;
	
	// Store a reference to the table holder so that when the user
	// selects a table row whilst in fisheye mode, the focus can be
	// changed
	
	private FisheyeTableHolder fisheyeTableHolder;
	
	private boolean bIgnoreSelectionChange = false;
	
	// The current sort column
	
	int sortCol = 0;
	
	// The current sort direction
	
	int sortDirection = DataViewerTableModel.SORT_ASCENDING;
	
        /**
	* Constructor method
	*/
	
     	public DataViewerTable(DataItemCollection dataItems, Mdi mdiForm, FisheyeTableHolder fisheyeTableHolder)
	{	
		this.mdiForm = mdiForm;
		
		this.dataItems = dataItems;
		
		this.fisheyeTableHolder = fisheyeTableHolder;
		
		addMouseListener(this);
		
		setAutoscrolls(true);
		
		if (dataItems.getSize() > 0)
		{
			selected = new SelectionHandler(dataItems.getSize(), dataItems);
			selected.addSelectableObject(this);
			selected.addSelectionChangedListener(this);
		}
		else
		{
			if (selected != null)
			{
				selected.removeSelectionChangedListener(this);
				selected.removeSelectableObject(this);
				selected = null;
			}
		}
		
		resetTable();
		
		// Add a mouse listener to the header columns of the table
		// so that we can click them to apply sorting
		
		setColumnSelectionAllowed(false);
		
		addSortMouseListener();
	}
	
	private void addSortMouseListener()
	{
		MouseAdapter listMouseListener = new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				TableColumnModel columnModel = getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX()); 
				int column = convertColumnIndexToModel(viewColumn); 
				if (e.getClickCount() == 1 && column != -1) 
				{
					if (sortCol == viewColumn)
						if (sortDirection == DataViewerTableModel.SORT_ASCENDING)
							sortDirection = DataViewerTableModel.SORT_DESCENDING;
						else
							sortDirection = DataViewerTableModel.SORT_ASCENDING;
					else
						sortCol = viewColumn;
					
					setSortIcon(viewColumn);
					DataViewerTableModel model = (DataViewerTableModel)getModel();
					model.sort(column, sortDirection);
					
					// Update the selection (if any) so that the highlighted
					// rows move with the sort
					
					selectionChanged(selected);
				}
			}
		};
		
		JTableHeader th = getTableHeader(); 
		th.addMouseListener(listMouseListener); 
	}
	
	/**
	* When the table sort is reset, this is called to restore the
	* table to its original view
	*/
	
	public void resetTable()
	{
		// Data model underlying table based on dataItems
		
		this.setModel(new DataViewerTableModel(dataItems));
		
		// Listen for user interactions
		
		this.getSelectionModel().addListSelectionListener(this);
		
		// Start off with all values selected
		
		selectAll();
		
		if (getRowCount() > 0)
		{
			addRowSelectionInterval(0,(getRowCount()-1));
		}
		
		// Create and add the table header cell renderer for providing
		// visual clues to the current sort
		
		sortColRenderer = new SortColumnRenderer(this);
		
		histogramcellrenderer = new HistogramTableCellRenderer(this);
		textrenderer = new FisheyeTableTextRenderer(this);
		
		// Assign each column its appropriate cell renderer
		
		for (int i = 0; i < getColumnCount(); i++)
		{
			int columnType = ((DataViewerTableModel)getModel()).getColumnType(i);
			
			if ((columnType==DataItemCollection.INTEGER)|(columnType==DataItemCollection.DOUBLE))
				getColumnModel().getColumn(i).setCellRenderer(histogramcellrenderer);
			
			if ((columnType==DataItemCollection.STRING)|(columnType==DataItemCollection.DATE))
				getColumnModel().getColumn(i).setCellRenderer(textrenderer);
			
			// Store the column ID relative to the data with each column in case the user
			// re-orders the table columns
			
			getColumnModel().getColumn(i).setIdentifier(new Integer(i));
		}
		
		// Make the last column invisible. This colummn contains the row indices
		
		TableColumn idClmn= getColumn(new Integer(getColumnCount()-1));
		idClmn.setMaxWidth(-1);
		idClmn.setMinWidth(-1);
		idClmn.setPreferredWidth(-1);
		
		clearSelection();
	}
	
	/**
	* When a sort is applied the following method is called to
	* display the appropriate icon in the sort column header
	*/
	
	public void setSortIcon(int columnID)
	{
		// First remove any icon from any other header
		
		for (int i = 0; i < getColumnCount(); i++)
		{
			getColumnModel().getColumn(i).setHeaderRenderer(null);
		}
		
		if (sortDirection == DataViewerTableModel.SORT_DESCENDING)
			sortColRenderer.setIcon(new ImageIcon ("images/downArrow.gif"));
		else if (sortDirection == DataViewerTableModel.SORT_ASCENDING)
			sortColRenderer.setIcon(new ImageIcon ("images/upArrow.gif"));
		
		getColumnModel().getColumn(columnID).setHeaderRenderer(sortColRenderer);
	}
	
    	/**
     	* Returns the indices of the selected items in this object
	* This method may return null if the getDeselection method is being used
	* instead.
	*
	* @return The selection
	*/
     
     	public Collection getSelection()
	{
		return theSelection;
	}
	
	/**
	* Return the selection in the table
	*/
	
	public void setSelection(Collection selection)
	{
		theSelection = 	(HashSet)selection;
	}
    
    	/**
	* Returns the indices of the deselected items in this object
	* This method may return null if the getSelection method is being used
	* instead.  
	*
	* @return The deselection
	*/
	
	public Collection getDeselection()
	{
		// Not used
		
		return null;
	}
	
	/**
	* Sets this selectable object to select all its items
	*
	*/
	
	public void selectAll()
	{
		theSelection = new HashSet();
		for ( int i = 0 ; i < getModel().getRowCount(); i++ )
		{
			theSelection.add(new Integer(i));
		}
	}
	
	/**
	* Sets this selectable item to select none of it items
	*
	*/
	
	public void selectNone()
	{
		theSelection.clear();
	}
	
	/**
	* called when the state of an item has been changed by the user
	* required by ItemListener
	* Updates the SelectionHandler if changes are made.
	*/
	
	public void valueChanged(ListSelectionEvent e) 
	{	
		super.valueChanged(e);
		
		// In the middle of changing, don't do anything
		
		if (e.getValueIsAdjusting()) return;
		if (!bIgnoreSelectionChange)
		{
			// If the table contains no data don't do anything
		
			if (theSelection==null) return;
			if (e.getSource()==this.getSelectionModel())
			{
				// Clear current item selection
				
				selectNone();
					
				int rowcount=getRowCount();
				int i;
				for (i=0; i<rowcount; i++)
				{
					if (isRowSelected(i))
					{
						// Update current selection
						
						// Use the indice in the last (hidden) column to correctly
						// identify the selected items
						
						int indexCol;
						
						try
						{
							indexCol = getColumnModel().getColumnIndex(new Integer(getColumnCount()-1));
						}
						catch(java.lang.IllegalArgumentException e2)
						{
							indexCol = (getColumnCount()-1);
						}
						
						theSelection.add(getValueAt(i, indexCol));
					}
				}
			}
		
			// Notify connected VisualModule instances that the selection has changed
			
			fisheyeTableHolder.getVisualModule().sendSelection();
		}
	}
	
	/**
	* Method that will be called if the selection is changed in the selection 
	* handler
	*
	* @param select The selection handler that created the selection changed
	*/
	
	public void selectionChanged(SelectionHandler select)
	{	
		// The selection of a connected VisualModule has changed
		// Make sure that this is reflected within this view
		
		bIgnoreSelectionChange = true;
			
		Iterator iter = theSelection.iterator();
		int index, theRow = -1;
		
		clearSelection();
			
		while (iter.hasNext())
		{
			index = ((Integer)iter.next()).intValue();
			
			// Use the index column to find the correct row
			
			int indexCol = getColumnModel().getColumnIndex(new Integer(getColumnCount()-1));
			
			for (int i = 0; i < getRowCount(); i++)
			{
				if (((Integer)getValueAt(i, indexCol)).intValue() == index)
				{
					theRow = i;
					break;
				}
			}
			
			if (theRow != -1)
				addRowSelectionInterval(theRow, theRow);	
		}
		
		// Scroll the table to the selection
		
		if (theRow > -1)
		{
			Rectangle rect = getCellRect(theRow, 0, true);
			scrollRectToVisible(rect);
		}
		
		repaint();
		validate();
		bIgnoreSelectionChange = false;
	}
			
	/** 
	* Returns the name of the column that this check box panel represents
	* 
	* @return The column name 
	*/
			
	public String toString()
	{
		return ("This is the data table");
	}
	
	/**
	* Methods to implement the table's mouse listener
	*/
	
	public void mouseClicked(MouseEvent e)
	{	
		if ((getSelectedRow() != -1) && fisheyeTableHolder.fisheyeMode())
			fisheyeTableHolder.showTableRow(getSelectedRow());
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	
	public void mouseEntered(MouseEvent e)
	{
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void mouseExited(MouseEvent e){}
	
	/**
	* Return the selection handler from the table
	*/
	
	public SelectionHandler getSelectionHandler()
	{
		return selected;
	}
	
	/**
	* Return a reference to the parent FisheyeTableHolder instance
	*/
	
	public FisheyeTableHolder getTableHolder()
	{
		return fisheyeTableHolder;	
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
		addSortMouseListener();
		setSortIcon(sortCol);
	}
}

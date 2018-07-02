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
 * DataViewerTableModel
 * 
 * This is a JTable tablemodel which implements the functions detailed in the
 * IS3 assignment.
 *  
 *  @author Greg Ross
 */ 

package alg.fisheyeTable;

import data.*;

import java.util.ArrayList;
import java.util.Date;

public class DataViewerTableModel extends javax.swing.table.AbstractTableModel implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	protected int rows;
	protected int columns;
	protected ArrayList fields;
	
	// The individual cell values. This array is modified during sorting
	
	protected Object[][] values;
	
	// The cell values. This array is kept the same after loading it
	// with values so that the values array (above) can revert back
	// to its initial state before sorting. This is so that the sort order is
	// maintained
	
	protected Object[][] origValues;
	
	protected ArrayList types;
	private DataItemCollection dataItems;
	
	// Determine the direction in which the table is sorted
	
	public static final int SORT_ASCENDING = 0;
	public static final int SORT_DESCENDING = 1;
	
	/**
	*Constructor - makes the data sets underlying the table
	*/
	
	public DataViewerTableModel(DataItemCollection dataItems)
	{
		this.dataItems = dataItems;
		this.rows=dataItems.getSize();
		
		// Add an extra (hidden) column to contain row indices
		
		this.columns=dataItems.getNumFields() + 1;
		this.fields=dataItems.getFields();
		this.types=dataItems.getTypes();
		
		// Copy the data into values from dataItems
		
		// Add one extra column to contain the row index; initially 1..n
		
		values = new Object[rows][columns + 1];
		origValues = new Object[rows][columns + 1];
		
		ArrayList column_data=dataItems.getDataItems();
		Object[] column_array=column_data.toArray();
		
		DataItem dTemp;
		
		for (int i=0;i<columns;i++)
		{
			for (int j=0;j<rows;j++)
			{
				if (i < (columns-1))
				{
					dTemp = (DataItem)column_array[j];
					
					if (dTemp.getTextValues() == null)
					{
						values[j][i] = dTemp.getValues()[i];
						origValues[j][i] = dTemp.getValues()[i];
					}
					else
					{
						values[j][i] = new Double(dTemp.getTextValues()[i]);
						origValues[j][i] = new Double(dTemp.getTextValues()[i]);
					}
				}
				else // Add the index column
				{
					values[j][i] = (new Integer(dataItems.getDataItem(j).getID()));
					origValues[j][i] = (new Integer(dataItems.getDataItem(j).getID()));
				}
			}
		}
	}
	
	/**
	* Between sorts, the values array must be reinitialised to its
	* original order so that each sort option always produces
	* the same order as before
	*/
	
	private void resetValuesArray()
	{
		for (int i=0;i<columns;i++)
		{
			for (int j=0;j<rows;j++)
			{
				if (i < (columns-1))
					values[j][i] = origValues[j][i];
				else // Add the index column
					values[j][i] = origValues[j][i];
			}
		}
	}
	
	/**
	*@param column the column number
	*@return the maximum value in the given column
	*/    
	
	public Object getMaximum(int column)
	{
		return dataItems.getMaximum(column);
	}
	
	/**
	*@param column the column number
	*@return the minimum value in the given column
	*/ 
	
	public Object getMinimum(int column)
	{
		return dataItems.getMinimum(column);
	}
		
	/**
	*Overwrites getRowCount in AbstractTableModel
	*@return the number of rows in the table
	*/
	
	public int getRowCount()
	{
		return rows;
	}
	
	/**
	*Overwrites getColumnCount in AbstractTableModel
	*@return the number of columns in the table
	*/
	
	public int getColumnCount()
	{
		return columns;
	}
	
	/**
	*Overwrites getValueAt in AbstractTableModel
	*@param row an integer specifying which row the value is at
	*@param column an integer specifying which column the value is at
	*@return the value at the row and column as an Object
	*/
	
	public Object getValueAt(int row, int column)
	{
		return values[row][column];
	}
	
	/**
	*Get the type of the data in the column
	*@param columnIndex the column number
	*@return an integer value representing the type as specified by @see DataItemCollection
	*/
	
	public int getColumnType(int columnIndex)
	{
		if (columnIndex < (columns-1))
			return ((Integer)types.get(columnIndex)).intValue();
		else // Make the index column a string type for rendering purposes
			return DataItemCollection.STRING;
	}
	
	/**
	*Gets the name of a column
	*@param columnIndex the column number
	*@return a String containing the name
	*/
	
	public String getColumnName(int columnIndex)
	{
		if (columnIndex < (columns-1))
			return (String)fields.get(columnIndex);
		else if (rows > 0) // Title the index column as 'hidden'
			return "hidden";
		else
			return null;
	}
	
	/**
	*Sorts the table by a column
	*@param column the integer value of the the column
	*/
	
	public void sort(int column, int sortDirection)
	{
		int dataType=getColumnType(column);
		try
		{
			resetValuesArray();
			QuickSort(values, 0, rows-1, column, dataType, sortDirection);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	/**
	*Quicksort routine to sort the table.
	*@param a the values in the table
	*@param lo0 the lowest row number to sort (usually 0)
	*@param hi0 the highest row number to sort (usually the last row in the table)
	*@param columnID the numerical value of the column to sort
	*@param dataType the data type of the column as specified by @see DataItemCollection
	*/
	
	private void QuickSort(Object[][] a, int lo0, int hi0, int columnID, int dataType, int sortDirection) throws Exception
	{
		int lo = lo0;
		int hi = hi0;
		Object[] mid;
		
		if (hi0 > lo0)
		{
			
			/* Arbitrarily establishing partition element as the midpoint of
			* the array.
			*/
			
			mid = a[ ( lo0 + hi0 ) / 2 ];
			
			// Loop through the array until indices cross
			
			if (sortDirection == SORT_ASCENDING)
			{
				while( lo <= hi )
				{
					/* Find the first element that is greater than or equal to 
					* the partition element starting from the left Index.
					*/
					
					while( ( lo < hi0 ) && ((compareColumns(a[lo], mid, columnID, dataType))<0))
						lo++;
					
					/* Find an element that is smaller than or equal to 
					* the partition element starting from the right Index.
					*/
					
					while( ( hi > lo0 ) && ((compareColumns(a[hi], mid, columnID, dataType))>0))
						hi--;
						
						// If the indexes have not crossed, swap
						
						if(lo <= hi) 
						{
							swap(a, lo, hi);
							++lo;
							--hi;
						}
				}
				
				/* If the right index has not reached the left side of array
				* must now sort the left partition.
				*/
				
				if(lo0 < hi)
					QuickSort(a, lo0, hi, columnID, dataType, sortDirection);
				
				/* If the left index has not reached the right side of array
				* must now sort the right partition.
				*/
				
				if(lo < hi0)
					QuickSort(a, lo, hi0, columnID, dataType, sortDirection);
			}
			else if (sortDirection == SORT_DESCENDING)
			{
				while( lo <= hi )
				{
					/* Find the first element that is less than or equal to 
					* the partition element starting from the left Index.
					*/
					
					while((lo < hi0) && ((compareColumns(a[lo], mid, columnID, dataType)) > 0))
						lo++;
					
					/* Find an element that is greater than or equal to 
					* the partition element starting from the right Index.
					*/
					
					while((hi > lo0) && ((compareColumns(a[hi], mid, columnID, dataType)) < 0))
						hi--;
						
						// If the indexes have not crossed, swap
						
						if(lo <= hi) 
						{
							swap(a, lo, hi);
							++lo;
							--hi;
						}
				}
				
				/* If the right index has not reached the left side of array
				* must now sort the left partition.
				*/
				
				if(lo0 < hi)
					QuickSort(a, lo0, hi, columnID, dataType, sortDirection);
				
				/* If the left index has not reached the right side of array
				* must now sort the right partition.
				*/
				
				if(lo < hi0)
					QuickSort(a, lo, hi0, columnID, dataType, sortDirection);
			}
		}
	}
	
	/**
	*swap two values in the table
	*@param a the table data
	*@param l1 the row containing the first value
	*@param l1 the row containing the second value
	*/
	
	private void swap(Object[][] a, int l1, int l2)
	{
		Object[] tmp=a[l1];
		a[l1]=a[l2];
		a[l2]=tmp;
	}  
	
	/**
	*Comparison routine to compare two rows of the table based on the the contents of one particular column
	*@param o1 one row of the table
	*@param 02 the other row
	*@param the column to compare on
	*@param dataType the type of the column to compare on as defined in @see DataItemCollection
	*@return 1 if o1>o2, 0 if they are equal by value, -1 if o1 < 02
	*/
	
	private int compareColumns(Object[] o1, Object[] o2, int columnID, int dataType)
	{
		// Values are numerical
		
		if (dataType==DataItemCollection.INTEGER | dataType==DataItemCollection.DOUBLE)
		{
			if (((Number)o1[columnID]).doubleValue()<((Number)o2[columnID]).doubleValue()) 
				return -1;
			else if (((Number)o1[columnID]).doubleValue()>((Number)o2[columnID]).doubleValue())
				return 1;
			else
				return 0;
		}
		
		//Values are Dates
		
		else if (dataType==DataItemCollection.DATE)
		{
			long n1 = ((Date)o1[columnID]).getTime();
			long n2 = ((Date)o2[columnID]).getTime();
			
			if (n1 < n2) 
				return -1;
			else if (n1 > n2) 
				return 1;
			else 
				return 0;
		}
		
		// Values are Strings
		
		else if (dataType==DataItemCollection.STRING)
		{
			int result = ((String)o1[columnID]).compareTo((String)o2[columnID]);
			
			if (result < 0) 
				return -1;
			else if (result > 0) 
				return 1;
			else 
				return 0;
		}
		
		//Else convert the objects to their string representations and
		//compare these
		
		else 
		{
			String s1 = o1[columnID].toString();
			String s2 = o2[columnID].toString();
			int result = s1.compareTo(s2);
			
			if (result < 0) 
				return -1;
			else if (result > 0) 
				return 1;
			else 
				return 0;
		}
	}
}

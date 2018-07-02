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
 * ClusterPickerTableModel
 * 
 * JTable table model for the cluster picker visual module
 *  
 *  @author Greg Ross
 */ 

package alg.clusterPicker;

import alg.ClusterPicker;

import javax.swing.table.AbstractTableModel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class ClusterPickerTableModel extends AbstractTableModel
{
	private String[] columnNames = {"Cluster", "Output port"};
	
	// The info that will appear in the table
	
	private Object[][] data;
	
	// Reference to the parent visual module
	
	ClusterPicker clusterPicker;
	
	// If the user tries to set assign a port that has already been allocated,
	// then this variable is used to store the old value, if the user decides to cancel
	// the operation
	
	private String oldPort = null;
	
	private boolean bSkipFireChangeNotification = false;
	
	public ClusterPickerTableModel(Object[][] data, ClusterPicker clusterPicker)
	{
		this.data = data;
		this.clusterPicker = clusterPicker;
	}
	
	public void setData(Object[][] data)
	{
		this.data = data;
	}
	
	public int getColumnCount()
	{
		return columnNames.length;
	}
	
	public int getRowCount()
	{
		return data.length;
	}
	
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
	public Object getValueAt(int row, int col)
	{
		return data[row][col];
	}
	
	/*
	* JTable uses this method to determine the default renderer/
	* editor for each cell.  If we didn't implement this method,
	* then the last column would not conatain the combo box
	*/
	public Class getColumnClass(int c)
	{
		return getValueAt(0, c).getClass();
	}
	
	/*
	* Don't need to implement this method unless your table's
	* editable
	*/
	public boolean isCellEditable(int row, int col) 
	{
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		
		if (col < (columnNames.length - 1))
		{
			return false;
		} 
		else
		{
			return true;
		}
	}
	
	/*
	* Don't need to implement this method unless your table's
	* data can change
	*/
	public void setValueAt(Object value, int row, int col)
	{
		oldPort = (String)data[row][col];
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	/**
	* Override fireTableCellUpdated so that we can check to see
	* whether the user's port allocation is unique. If not, prompt
	* the user to ascertain whether to retain or revert
	*/
	
	public void fireTableCellUpdated(int row, int column)
	{
		// Get the current new value
		
		String newValue = (String)getValueAt(row, column);
		
		// If this value has already been allocated..
		
		boolean bChanged = false;
		int portNumber;
		
		try
		{
			for (int i = 0; i < getRowCount(); i++)
			{
				if (((String)data[i][1]).equals(newValue) && (i != row) && !((String)data[row][1]).equals("Unallocated")
					& (!((String)data[row][1]).equals(oldPort)))
				{
					// Another cluster has already allocated to the selected port
					// Prompt the user
					
					if (userSaysContinue(newValue, (String)getValueAt(i, 0)))
					{
						data[i][column] = "Unallocated";
						bSkipFireChangeNotification = true;
						fireTableCellUpdated(i, column);
						bSkipFireChangeNotification = false;
						
						// send the data to the appropriate output ports
						
						if (!oldPort.equals("Unallocated"))
						{
							portNumber = Integer.parseInt(oldPort.split("Port ")[1]);
							clusterPicker.senOutput(-1, portNumber - 1);
						}
						
						portNumber = Integer.parseInt(newValue.split("Port ")[1]);
						clusterPicker.senOutput(row, portNumber - 1);
					}
					else
					{
						data[row][column] = oldPort;
						bSkipFireChangeNotification = true;
						fireTableCellUpdated(row, column);
						bSkipFireChangeNotification = false;
					}
					
					return;
				}
			}
			
			// Change from 'Unallocated' to a port number
			
			if (!oldPort.equals(newValue) && !bSkipFireChangeNotification)
			{
				if (oldPort.equals("Unallocated") && !newValue.equals("Unallocated"))
				{
					portNumber = Integer.parseInt(newValue.split("Port ")[1]);
					clusterPicker.senOutput(row, portNumber - 1);
				}
				else if (!oldPort.equals("Unallocated") && newValue.equals("Unallocated"))
				{
					// Change to 'Unallocated'
					
					portNumber = Integer.parseInt(oldPort.split("Port ")[1]);
					clusterPicker.senOutput(-1, portNumber - 1);
				}
				else if (!oldPort.equals("Unallocated") && !newValue.equals("Unallocated"))
				{
					portNumber = Integer.parseInt(oldPort.split("Port ")[1]);
					clusterPicker.senOutput(-1, portNumber - 1);
					
					portNumber = Integer.parseInt(newValue.split("Port ")[1]);
					clusterPicker.senOutput(row, portNumber - 1);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(oldPort);
		}
	}
	
	protected boolean userSaysContinue(String port, String clusterName)
	{
		Object[] options = {"Continue", "Cancel"};
		int answer = JOptionPane.showOptionDialog(
			SwingUtilities.getWindowAncestor(clusterPicker),
			port + " has already been allocated to " + clusterName + ". "
			+ "You can either continue and reallocate the port "
			+ "or cancel and revert to the last value.",
			"Port already allocated",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.ERROR_MESSAGE,
			null,
			options,
			options[1]);
		
		if (answer == 0)
		{
			return true;
		}
		return false;
	}
}

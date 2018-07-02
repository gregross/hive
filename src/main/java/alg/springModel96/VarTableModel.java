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
 * VarTableModel
 *
 * Table model for the attribute table in the form for allowing the user
 * to decide which variables to run a (Chalmers' '96) spring model on
 *
 *  @author Greg Ross
 */
package alg.springModel96;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class VarTableModel extends AbstractTableModel implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private String[] columnNames = {"Attribute", "Include?"};
	
	// The info that will appear in the table
	
	private Object[][] data;
	
	// Referencce to the table's parent form
	
	private VarSelectTable parentForm;
	
	public VarTableModel(Object[][] data, VarSelectTable parentForm)
	{
		this.data = data;
		this.parentForm = parentForm;
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
         * editor for each cell
         */
	 
        public Class getColumnClass(int c)
	{
            return getValueAt(0, c).getClass();
        }
	
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
	 
        public boolean isCellEditable(int row, int col)
	{
            if (col == 1)
	    {
                return true;
            } 
	    else
	    {
                return false;
            }
        }
	
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
	 
        public void setValueAt(Object value, int row, int col)
	{
            data[row][col] = value;
            fireTableCellUpdated(row, col);
	    
	    // If there are attributes selected in the Table then the
	    // export button on the parent form should be enabled
	    
	    parentForm.enableDisableExportButton();
        }
}

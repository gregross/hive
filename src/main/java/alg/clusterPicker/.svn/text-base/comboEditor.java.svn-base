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
 * comboEditor
 *
 * Implements a cell editor using a combo box for allocating clusters to output ports
 *
 *  @author Greg Ross
 */
package alg.clusterPicker;

import alg.ClusterPicker;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Toolkit;

public class comboEditor extends DefaultCellEditor
{
	JComboBox comboBox;
	
	public comboEditor()
	{
		super(new JComboBox());
		comboBox = (JComboBox)getComponent();
		
		for (int i = 0; i < ClusterPicker.NUM_CLUSTERS_OUT; i++)
			comboBox.addItem("Port " + (i + 1));
		
		comboBox.addItem("Unallocated");
	}
	
	// Override to invoke setSelectedIndex on the formatted text field
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int column)
	{
		JComboBox comboBox = (JComboBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
		comboBox.setSelectedItem(value);
		return comboBox;
	}
	
	// Override to ensure that the value remains valid. i.e. the cell cannot
	// be the value of a port that's already been allocated
	
	public Object getCellEditorValue()
	{
		JComboBox comboBox = (JComboBox)getComponent();
		
		return comboBox.getSelectedItem();
	}
	
	//Override to check whether the edit is valid,
	//setting the value if it is and complaining if
	//it isn't.  If it's OK for the editor to go
	//away, we need to invoke the superclass's version 
	//of this method so that everything gets cleaned up
	
	public boolean stopCellEditing()
	{
		JComboBox comboBox = (JComboBox)getComponent();
		
		/*if (comboBox.isEditValid())
		{
			try
			{
				comboBox.commitEdit();
			}
			catch (java.text.ParseException exc){}
		}
		else
		{
			//text is invalid
			
			if (!userSaysRevert())
			{
				//user wants to edit
				
				return false; //don't let the editor go away
			}
		}*/
		
		return super.stopCellEditing();
	}
	
	/** 
	* Lets the user know that the text they entered is 
	* bad. Returns true if the user elects to revert to
	* the last good value.  Otherwise, returns false, 
	* indicating that the user wants to continue editing.
	*/
	protected boolean userSaysRevert()
	{
		Toolkit.getDefaultToolkit().beep();
		comboBox.setPopupVisible(true);
		Object[] options = {"Edit", "Revert"};
		int answer = JOptionPane.showOptionDialog(
			SwingUtilities.getWindowAncestor(comboBox),
			"This port has already been allocated. "
			+ "You can either continue editing "
			+ "or revert to the last value.",
			"Port already allocated",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.ERROR_MESSAGE,
			null,
			options,
			options[1]);
		
		if (answer == 1)
		{
			// Revert!
			
			//comboBox.setSelectedItem(value);
			return true;
		}
		return false;
	}
}

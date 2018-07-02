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
 * ClusterPickerTable
 * 
 * A table that shows the number of clusters and to which ports they are assigned
 *  
 *  @author Greg Ross
 */ 

package alg.clusterPicker;
import alg.ClusterPicker;

import javax.swing.table.*;
import javax.swing.DefaultCellEditor;
import javax.swing.event.*;
import java.awt.Color;
import java.util.ArrayList;

public class ClusterPickerTable extends javax.swing.JTable implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	public ClusterPickerTable(ClusterPicker clusterPicker)
	{
		this.setModel(new ClusterPickerTableModel(new Object[0][0], clusterPicker));
		setPortColumn();
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(false);
		setAutoscrolls(true);
	}
	
	/**
	* Set the customised combo-based cell editor as the editor for the
	* port allocation column
	*/
	
	private void setPortColumn()
	{
		getColumnModel().getColumn(1).setCellEditor(new comboEditor());
	}
	
	/**
	* When the ClusterPicker visual module receives a set of clusters, the number of clusters
	* is sent to the init method so that the table can update its table model
	*/
	
	public void init(int numClusters, ArrayList clusterColours)
	{
		int cols = 2;
		
		Object[][] data = new Object[numClusters][cols];
		
		for (int i = 0; i < numClusters; i++)
			for (int j = 0; j < cols; j++)
				if (j == 0)
					data[i][j] = "cluster " + (i + 1);
				else if (j < 2)
					if (i < ClusterPicker.NUM_CLUSTERS_OUT)
						data[i][j] = "Port " + (i + 1);
					else
						data[i][j] = "Unallocated";
					
		// If clusterColours is not null create a special renderer to colour the name column
		
		boolean bCustomRenderer = false;
		
		if (clusterColours != null)
		{
			if (clusterColours.size() == numClusters)
			{
				if (clusterColours.get(0) instanceof Color)
				{
					getColumnModel().getColumn(0).setCellRenderer(new ClusterPickerNameCellRenderer(this, clusterColours));
					bCustomRenderer = true;
				}
			}
		}
		
		if (!bCustomRenderer)
			getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer());
				
		((ClusterPickerTableModel)getModel()).setData(data);
		((ClusterPickerTableModel)getModel()).fireTableDataChanged();
	}
}

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
 * ClusterPickerNameCellRenderer
 * 
 * Overrides DefaultTableCellRenderer to paint the cluster name column of the ClusterPicker table
 * in colours corresponding to Voronoi Clustering (if applicable)
 *  
 *  @author 
 */ 

package alg.clusterPicker;

import alg.ClusterPicker;

import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.ArrayList;

public class ClusterPickerNameCellRenderer extends DefaultTableCellRenderer implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Colours to use when rendering
	
	Color foreground;
	Color textfg;
	
	// The font and contents of the text in the cell.
	
	Font  tableFont;
	String cellText;
	
	// Store a reference to the parent JTable instance
	
	private ClusterPickerTable jtable;
	
	ArrayList clusterColours = null;
	
	public ClusterPickerNameCellRenderer(){}
	
	public ClusterPickerNameCellRenderer(ClusterPickerTable jtable, ArrayList clusterColours) 
	{
		this.jtable = jtable;
		this.clusterColours = clusterColours;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,  boolean isSelected,
											boolean hasFocus, 
											int row, 
											int column)
	{
		if (clusterColours != null)
		{
			foreground = (Color)clusterColours.get(row);
			textfg = table.getForeground();
			
			cellText = value.toString();
			tableFont = table.getFont();
		}
			
		return this;
	}
	
	/**
	* Overrides the paint method of JComponent 
	*
	*/
	
	public void paintComponent(Graphics g)
	{		
		if (clusterColours != null)
		{
			int width = this.getWidth();
			int height = this.getHeight();
			float fontSize = (height - 1);
			
			// Where to place the text
			
			int placeat = (height - 2);
			
			if (fontSize < 1.5) fontSize= (float)(1.5);
			
			g.setColor(foreground);
			g.fillRect(0, 0 , width, height);
				
			// Draw text on top, textfg colour
				
			g.setColor(textfg);
				
			if (placeat > 1) 
			{
				g.setFont(tableFont.deriveFont(fontSize));
				g.drawString(cellText, 2, placeat);
			}
		}
		else
			super.paintComponent(g);
	}
}

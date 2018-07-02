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
 * FisheyeTableTextRenderer
 * 
 * Renders the contents of textual columns in the table
 *  
 *  @author 
 */ 

package alg.fisheyeTable;

import data.DataItemCollection;

import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *Renders the contents of textual columns in the table
 */
 
public class FisheyeTableTextRenderer extends DefaultTableCellRenderer implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	Color background;
	Color foreground;
	String cellText;
	Font  tableFont;
	
	// Determine whether the represented cell has been selected
	
	private boolean isSelected;
	
	// Store a reference to the parent JTable instance
	
	private DataViewerTable jtable;
	
	public FisheyeTableTextRenderer(){}
	
	public FisheyeTableTextRenderer(DataViewerTable jtable) 
	{
		this.jtable = jtable; 
	}	
	
	public Component getTableCellRendererComponent(JTable table, 
						Object value, 
						boolean isSelected, 
						boolean hasFocus, 
						int row, 
						int column)
	{
		
		this.isSelected = isSelected;
		
		if (isSelected) 
		{
			background=Color.pink;
			foreground=table.getForeground();
		}
		else 
		{
			background=Color.white;
			foreground=table.getForeground();
		}
		
		cellText=value.toString();
		tableFont=table.getFont();
		return this;
	}
	
	/**
	* Overrides the paint method of JComponent 
	* Places the textual contents of the cell onscreen
	* Font is proportional to the height of the row. 
	*/
	
	public void paintComponent(Graphics g)
	{		
		int width=this.getWidth();
		int height=this.getHeight();
		float fontSize=(height-1);
		int placeat=(height-2);
		
		if (fontSize<1.5) fontSize=(float)(1.5);
		if (placeat<1)  placeat=1;
		
		g.setColor(background);
		g.fillRect(0,0,width,height);
		
		// Draw remaining background
		
		if (isSelected)
		{
			g.setColor(Color.yellow);
			g.fillRect(0, 0, width, height);
		}
		
		g.setColor(foreground);
		g.setFont(tableFont.deriveFont(fontSize));
		g.drawString(cellText,2,placeat);
		
		if (isSelected && !jtable.getTableHolder().fisheyeMode())
		{
			g.setColor(Color.black);
			g.drawRect(0, 0, width-1, height-1);
		}
	}
}

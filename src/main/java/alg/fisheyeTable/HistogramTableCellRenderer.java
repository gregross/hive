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
 * HistogramTableCellRenderer
 * 
 * This renders the numerical columns in the table as histograms with
 * text on top to show value.
 *  
 *  @author 
 */ 

package alg.fisheyeTable;

import data.DataItemCollection;

import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.table.DefaultTableCellRenderer;

public class HistogramTableCellRenderer extends DefaultTableCellRenderer implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Colours to use when rendering
	
	Color foreground;
	Color textfg;
	
	// Percentage of cell to cover with histogram
	
	private double histogramPercent;
	
	// Used in calculating histogramPercent from the values in the column
	
	double currentColumnValue;
	double minColumnValue;
	double maxColumnValue;
	
	// The font and contents of the text in the cell.
	
	Font  tableFont;
	String cellText;
	
	// Determine whether the cell represented is currently selected
	
	private boolean isSelected;
	
	// Store a reference to the parent JTable instance
	
	private DataViewerTable jtable;
	
	public HistogramTableCellRenderer(){}
	
	public HistogramTableCellRenderer(DataViewerTable jtable) 
	{
		this.jtable = jtable;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,  boolean isSelected,
											boolean hasFocus, 
											int row, 
											int column)
	{
		histogramPercent = 0;
		
		// Choose which colour scheme to use based on
		
		this.isSelected = isSelected;
		
		if (isSelected) 
		{
			foreground=Color.pink;
			textfg=table.getForeground();
		}
		else 
		{
			foreground=Color.lightGray;
			textfg=table.getForeground();
		}
		
		// Calculate percentage of table to fill by equation:
		// histogramPercent=(value-min)/(min-max)
		// calculate percentages for an Integer column
		
		// Use the column identifier to identify the type info and related values.
		// This is in case the user has re-ordered the columns
		
		Object colID = table.getColumnModel().getColumn(column).getIdentifier();
		int col = ((Integer)colID).intValue();
		
		if (((DataViewerTableModel)table.getModel()).getColumnType(col)==DataItemCollection.INTEGER)
		{
			minColumnValue=((Integer)((DataViewerTableModel)table.getModel()).getMinimum(col)).intValue();
			maxColumnValue=((Integer)((DataViewerTableModel)table.getModel()).getMaximum(col)).intValue();
			currentColumnValue=((Integer)value).intValue();
		}
		
		// Calculate percentages for a Double column
		
		else 
		{
			minColumnValue=((Double)((DataViewerTableModel)table.getModel()).getMinimum(col)).doubleValue();
			maxColumnValue=((Double)((DataViewerTableModel)table.getModel()).getMaximum(col)).doubleValue();
			currentColumnValue=((Double)value).doubleValue();
		}

		histogramPercent=(currentColumnValue-minColumnValue)/(maxColumnValue-minColumnValue);
		
		cellText=value.toString();
		tableFont=table.getFont();
			
		return this;
	}
	
	/**
	* Overrides the paint method of JComponent 
	*
	*/
	
	public void paintComponent(Graphics g)
	{		
		int width=this.getWidth();
		int height=this.getHeight();
		int boxFill=(int)(width*histogramPercent);
		float fontSize=(height-1);
		
		// Where to place the text
		
		int placeat=(height-2);
		
		if (fontSize<1.5) fontSize=(float)(1.5);
		
		// Draw histogram foreground colour
		
		g.setColor(foreground);
		g.fillRect(0,0,boxFill,height);
		
		// Draw remaining background
		
		if (isSelected)
		{
			g.setColor(Color.yellow);
			int rw = boxFill - width;
			if (rw < 0)
				rw *= -1;
			g.fillRect(boxFill, 0, rw, height);
		}
			
		// Draw text on top, textfg colour
			
		g.setColor(textfg);
			
		if (placeat>1) 
		{
			g.setFont(tableFont.deriveFont(fontSize));
			g.drawString(cellText,2,placeat);
		}
		
		if (isSelected && !jtable.getTableHolder().fisheyeMode())
		{
			g.setColor(Color.black);
			g.drawRect(0, 0, width-1, height-1);
		}
	}
}

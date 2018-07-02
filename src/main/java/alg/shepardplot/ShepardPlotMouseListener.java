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
 * A mouse listener for the Shepard Plot
 *
 *  @author Greg Ross
 */

package alg.shepardplot;
import alg.ShepardPlot;

import org.jfree.data.XYSeriesCollection;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYSeries;
import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.event.*;
import java.util.ArrayList;

public class ShepardPlotMouseListener extends MouseInputAdapter implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent Visual Module
	
	ShepardPlot shepardPlot;
	
	// The chart's ChartPanel object
	
	ChartPanel chtPanel;
	
	//The custom XYPlot that draws the bounding rectangle
	
	CustomPlot cPlot;
	
	// Store the coordinate where the user presses and releases the mouse button
	
	private int mousePressX = 0;
	private int mousePressY = 0;
	private int mouseReleaseX = 0;
	private int mouseReleaseY = 0;
	
	// The current mouse position
	
	private int currentMouseX = 0;
	private int currentMouseY = 0;
	
	// The last bounding rectangle to be drawn
	
	private Rectangle oldRect = null;
	
	public ShepardPlotMouseListener(ShepardPlot shepardPlot)
	{
		this.shepardPlot = shepardPlot;
		cPlot = (CustomPlot)shepardPlot.getChart().getPlot();
		chtPanel = shepardPlot.getChartPanel();
	}
	
	public void mouseClicked(MouseEvent e)
	{
		clearSelection();
	}
	
	private void clearSelection()
	{
		// Clear the highlight, if there is one
		
		XYSeriesCollection xySeries = shepardPlot.getSeriesCollection();
		
		if (xySeries.getSeriesCount() > 1)
			xySeries.removeSeries(1);
	}
	
	public void mousePressed(MouseEvent e)
	{
		mousePressX = e.getX();
		mousePressY = e.getY();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		mouseReleaseX = e.getX();
		mouseReleaseY = e.getY();
		
		// If a bounding rectangle had been drawn, remove it
		
		Graphics2D g2 = (Graphics2D)chtPanel.getGraphics();
		
		// Set the clipping region
		
		Rectangle2D area = shepardPlot.getChartPanel().getScaledDataArea();
		g2.clip(area);
		
		// Set the paint to XOrMode
		
		g2.setPaint(Color.gray);
		g2.setXORMode(Color.red);
		
		// Overwrie the last rectangle and return and highlight any selected points
		
		ArrayList selection = new ArrayList();
		
		if (oldRect != null)
		{
			if (shepardPlot.getSeriesCollection().getSeriesCount() > 0)
				if (shepardPlot.getSeriesCollection().getSeries(0).getItemCount() > 0)
				{
					selection = getSelectedPoints();
					highlightSelection(selection);
					shepardPlot.handleSelection(selection);
				}
			
			g2.draw(oldRect);
			oldRect = null;
		}
	}
	
	/**
	* Add a new series to represent the selected points
	*/
	
	private void highlightSelection(ArrayList selection)
	{
		if (selection.size() > 0)
		{
			// Get the collection of existing series
			
			XYSeriesCollection xySeries = shepardPlot.getSeriesCollection();
			
			// If the highlight series already exists, remove it
			
			if (xySeries.getSeriesCount() > 1)
				xySeries.removeSeries(1);
			
			// Add the new highlight series
			
			XYSeries highlightSeries = new CustomXYSeries("Selected");
			
			// Add the selected points to the highlight series
			
			double seriesX, seriesY;
			int index;
			
			for (int i = 0; i < selection.size(); i++)
			{
				index = ((Integer)selection.get(i)).intValue();
				seriesX = ((Double)xySeries.getXValue(0, index)).doubleValue();
				seriesY = ((Double)xySeries.getYValue(0, index)).doubleValue();
				
				highlightSeries.add(seriesX, seriesY);
			}
			
			xySeries.addSeries(highlightSeries);
			
			// Get the series renderer
			
			XYPlot p = shepardPlot.getChart().getXYPlot();
			StandardXYItemRenderer r = (StandardXYItemRenderer)p.getRenderer();
			
			// Set the renering for the highlight series
			
			r.setSeriesPaint(1, Color.yellow);
			Rectangle rect = new Rectangle(1, 0, 2, 2);
			r.setSeriesShape(1, rect);
			r.setSeriesShapesFilled(1, new Boolean("true"));
		}
		else
			clearSelection();
	}
	
	/**
	* Determine which points are inside the rectangle drawn by the user
	*/
	
	private ArrayList getSelectedPoints()
	{
		// Get the points in the series
		
		ArrayList result = new ArrayList();
		
		XYSeriesCollection xySeries = (XYSeriesCollection)shepardPlot.getChart().getXYPlot().getDataset();
		
		// Scale each point to Java2D space co-ordinates
		
		double seriesX = 0d, seriesY = 0d;
		double newX = 0d, newY = 0d;
		XYPlot p = shepardPlot.getChart().getXYPlot();
		ValueAxis xAxis =  p.getDomainAxis();
		ValueAxis yAxis =  p.getRangeAxis();
		Rectangle2D rect = shepardPlot.getChartPanel().getScaledDataArea();
		
		for (int i = 0; i < xySeries.getItemCount(0); i++)
		{
			seriesX = ((Double)xySeries.getXValue(0, i)).doubleValue();
			seriesY = ((Double)xySeries.getYValue(0, i)).doubleValue();
			
			// Convert to Java2D co-ordinates
			
			newX = xAxis.translateValueToJava2D(seriesX, rect, p.getDomainAxisEdge());
			newY = yAxis.translateValueToJava2D(seriesY, rect, p.getRangeAxisEdge());
			
			// Check to see if the point intersects the selection rectangle
			
			if (oldRect.contains(newX, newY))
				result.add(new Integer(i));
		}
		
		return result;
	}
	
	public void mouseDragged(MouseEvent e)
	{
		currentMouseX = e.getX();
		currentMouseY = e.getY();
		
		// Determine the rectangle that represents the bounding region
		
		Rectangle rect = new Rectangle();
		rect.width = Math.abs(currentMouseX - mousePressX);
		rect.height = Math.abs(currentMouseY - mousePressY);
	    	rect.x = mousePressX;
	    	rect.y = mousePressY;
		
		 if (currentMouseX < rect.x && currentMouseY <= rect.y)
		{
			rect.x = currentMouseX;
			rect.y = currentMouseY;
		} 
		else if (currentMouseX >= rect.x && currentMouseY < rect.y)
			rect.y = currentMouseY;
		else if (currentMouseX < rect.x && currentMouseY > rect.y)
			rect.x = currentMouseX;
		
		Graphics2D g2 = (Graphics2D)chtPanel.getGraphics();
		
		// Set the clipping region
		
		Rectangle2D area = shepardPlot.getChartPanel().getScaledDataArea();
		g2.clip(area);
		
		// Set the paint to XOrMode
		
		g2.setPaint(Color.gray);
		g2.setXORMode(Color.red);
		
		// Overwrie the last rectangle
		
		if (oldRect != null)
			g2.draw(oldRect);
		
		// Draw the rectangle
		
		g2.draw(rect);
		oldRect = rect;
		rect = null;
	}
}

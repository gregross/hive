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
 * ScatterPanel to display the graph on a ScatterPlot module
 *
 *  @author Andrew Didsbury, Greg Ross
 */
 
package alg.scatterplot;

import math.*;
import data.*;
import parent_gui.*;
import ColorScales.ColorScales;
import alg.*;
import alg.fileloader.ExampleFileFilter;
import excel.ExcelExport;

import edu.uci.ics.screencap.*;
import org.jibble.epsgraphics.*;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Collections;
import java.io.File;

public class ScatterPanel extends JPanel implements Selectable,
					     SelectionChangedListener,
					     java.io.Serializable, ActionListener,
					     MouseListener, FocusListener, MouseMotionListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private DataItemCollection dataItems;
	
	protected          int               dataSize;
	protected volatile boolean           drawTrails  = true;
	protected          int               width;
	protected          int               height;
	
	protected          double            scaleX      = 1.0;
	protected          double            scaleY      = 1.0;
	
	protected          double            offsetX     = 0.0;
	protected          double            offsetY     = 0.0;
	
	protected          Point             origin;
	
	protected          ArrayList         trails;
	protected          SelectionHandler  selected;
	protected          Set               theSelection;
	
	// The collection of selected ordinal indices matching theSelection
	
	private		   ArrayList	     selectionData;
	
	protected          MouseHandler      mouse;
	
	protected          Color[]           colorScheme;
	protected          int               colorField;
	protected          int               selectedCol;
	
	public static      double            DATA_DOT_SIZE  = 5.0;
	public static      double            TRAIL_DOT_SIZE = 4.0;
	public static      int               TRAIL_LENGTH   = 10;
	public static      Color             TRAIL_COLOR    = Color.cyan;
	public static      Color             DES_TRAIL_COL  = Color.lightGray;
	public static      Color             DESELECTED_DOT = Color.gray;
	public             Color             BACKGROUND     = new Color(100,100,100);
	
	public static      long              MAX_DELAY      = 800;
	
	// The list of position of data items
	
	private ArrayList positions;
	
	// The reference to the parent visual object
	
	private Scatterplot visMod;
	
	// Determine the number of selected points
	
	private int numSelected = 0;
	
	// Popup menu for allowing the user to export the selected points to MS Excel for charting
	
	JPopupMenu popup;
	JMenuItem exportSelectionToExcel;
	
	// Menu for allowing the user to colour over attributes
	
	JMenuItem jmColourOver;
	
	// Menu for allowing the user to select a colour scheme
	
	JMenuItem jmColourScheme;
	
	// Menu to set background brightness
	
	JMenuItem jmBackgroundColour;
	
	// Menu for adding labels to the layout
	
	JMenuItem jmAddLabel;
	JMenuItem jmEditLabel;
	
	// Menu for image dump
	
	JMenuItem jmImageDump;
	
	// Menu item for rendering a surface based upon binary variable frequency.
	
	JCheckBoxMenuItem jmVariableFreq;
	JCheckBoxMenuItem jmDensitySurface;
	
	// Menu for showing row labels (esp. for binary, transposed, text and triangular data)
	
	JCheckBoxMenuItem jmLabels;
	private boolean bShowAllLabels = false;
	
	// file choose to let the user specify a location and file name
	// for the image dump
	
	private JFileChooser chooser;
	
	// A modal dialogue to allow users to exort data to Excel
	
	ExcelExport excelExportDialog;
	
	// A modal dialog allowing the user to colour according to attrbutes
	
	ColorAttributeForm colorForm;
	
	// A modal dialog allowing the user to change the colour scheme
	
	ColorSchemeForm colorSchemeForm;
	private String colourSchemeName;
	
	// A modal dialog for changing the background brightness
	
	BackgroundBrightForm backgroundBrightForm;
	
	// A modal dialog for changing the power value for IDW interpolation.
	
	IDWPowerForm IDWForm;
	
	// If the user is panning don't show the context menu upon releasing the mouse
	
	private boolean bPanning = false;
	
	//Is the user dragging the bounding rectangle?
	
	private boolean draggingRectangle = false;
	
	// Store the location where the user clicks the mouse to bring up the
	// popup menu for annotating the layout
	
	private int xLabel, yLabel;
	
	// the text box used for annotating the layout
	
	private JTextField txtLabel = null;
	
	// Determine whether we're editing an annotation
	
	private boolean bEditingAnnotation = false; 
	
	// If the user clicks somewhere on the layout to commit changes to an annotation
	// set the following flag to true so that we do not scrub any layout selections
	
	private boolean bLabelLostFocus = false;
	
	// A list of annotations
	
	private ArrayList annotations = null;
	private ArrayList xLabelPos = null;
	private ArrayList yLabelPos = null;
	private int highlightedLabel = -1;
	
	private transient DefaultExcentricLabels exLabels;
	
	// If the data are binary and transposed, then the following is set to true
	// if the user wishes to colour according to variables frequency
	
	private boolean bColourToFreq = false;
	
	// ...visible if the above is true
	
	private JCheckBoxMenuItem jmFreq = null;
	
	// If the data are binary and NOT transposed then allow the user
	// the option of colouring according to the number of variables present
	
	private boolean bColourToNumVars = false;
	private JCheckBoxMenuItem jmNumVars = null;
	
	// Min and Max coordinate values.
	
	private double minX, maxX, minY, maxY;
	
	// The number of neighbours over which to carry out IDW.
	
	private int numNeighbours;
	
	// Store the value we'll use to guide interpolation of the surface.
	
	private float [] counts;
	
	// Store the coordinates of points in the display space.
	
	private double[][] coords;
	
	// Determine whether the actual point positions have changed, as opposed
	// to just being scaled or translated.
	
	private boolean bPointsChanged = false;
	private double [][][] cellCoords = null;
	private double minCellValue = Double.MAX_VALUE;
	private double maxCellValue = Double.MIN_VALUE;
	private double [][] cellValues = null;
	
	// Store the sorted X- and Y-axis values of all input points.
	
	private ArrayList sortedX;
	private ArrayList keysX;
	private ArrayList keysXCopy;
	
	// Determine whether the frequency surface should be visible.
	
	private boolean bFreqSurface = false;
	private boolean bDensSurface = false;
	
	// Property for setting the IDW power value.
	
	private int IDWPower = 4;
	
	public ScatterPanel(Scatterplot visMod)
	{
		this.visMod = visMod;
		
		// Set of selected indices
		
		theSelection = new HashSet();
		selectionData = new ArrayList();
		
		// Init trails and selected items
		
		trails = new ArrayList();
		
		mouse = new MouseHandler(this);
		origin = new Point();
		
		// Add a mouse listener for view transformations (zooming, panning, selection etc.)
		
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseMotionListener(this);
		
		// Set the default colour scheme to 'Heated Object'
		
		setColorScheme("HeatedObject");
		colourSchemeName = "HeatedObject";
		
		// Add a second mouse listener for popup menus.
		
		addMouseListener(this);
		addPopupMenu();
		
		setLayout(null);
	}
	
	/**
	* Return the parent of this class, i.e. the ScatterPlot VisualModule
	*
	*/
	
	public VisualModule getParentModule()
	{
		return visMod;
	}
	
	public void init(DataItemCollection dataItems, ArrayList positions)
	{
		this.dataItems = dataItems;
		this.positions = positions;
		
		annotations = new ArrayList();
		xLabelPos = new ArrayList();
		yLabelPos = new ArrayList();
		
		jmFreq.setState(false);
		jmFreq.setVisible(false);
		
		jmNumVars.setState(false);
		jmNumVars.setVisible(false);
		
		bFreqSurface = false;
		jmVariableFreq.setState(false);
		jmVariableFreq.setVisible(false);
		
		bDensSurface = false;
		jmDensitySurface.setState(false);
		jmDensitySurface.setVisible(false);
		
		jmColourOver.setEnabled(true);
		bColourToFreq = false;
		bColourToNumVars = false;
		
		jmLabels.setState(false);
		jmLabels.setVisible(false);
		bShowAllLabels = false;
		
		if (dataItems != null)
		{
			// Setup selection stuff.
			
			dataSize = dataItems.getSize();
			selected = new SelectionHandler(dataSize, dataItems);
			selected.addSelectableObject(this);
			selected.addSelectionChangedListener(this);
			
			for (int i = 0 ; i < dataSize ; i++) 
			{
				trails.add(new ArrayList());
				theSelection.add(new Integer(i));
				selectionData.add(new Integer(i));
			}
			
			// If the data are transposed or are a triangular matrix or text then allow the user to show labels.
			
			if (dataItems.getTransposed() || dataItems.getLowerTriangular() || dataItems.isTextCorpus())
			{
				exLabels = new DefaultExcentricLabels(dataItems, selected);
				exLabels.setVisualization(this);
				exLabels.setMaxLabels(10);
				
				// Labeling options.
				
				jmLabels.setVisible(true);
				
				// The darker the background, the lighter the text. Conversely,
				// the lighter the backround, the darker the text
				
				if (BACKGROUND.getRed() < 160)
					exLabels.setBackgroundColor(Color.white);
				else
				{
					int cl = 255 - BACKGROUND.getRed();
					exLabels.setBackgroundColor(new Color(cl, cl, cl));
				}
			}
			else
			{
				exLabels = null;
			}
			
			// If the data are binary and depending upon whether they're transposed,
			// provide the option to colour according to the variable frequency and
			// number of variables
			
			if (dataItems.getBinary() && dataItems.getTransposed())
			{
				// When the data are transposed and binary, colour the points according to
				// variables frequency by default
				
				jmColourOver.setEnabled(false);
				bColourToFreq = true;
				jmFreq.setState(true);
				jmFreq.setVisible(true);
				jmVariableFreq.setState(false);
				jmVariableFreq.setVisible(true);
			}
			else if (dataItems.getBinary() && !dataItems.getTransposed())
			{
				jmNumVars.setVisible(true);
				jmVariableFreq.setVisible(false);
			}
			
			// Temporary. Need to add an extra menu item for this when plotting a density surface.
			
			jmDensitySurface.setState(false);
			jmDensitySurface.setVisible(true);
			
			// Set the appropriate caption for the labels menu item.
			
			if (dataItems.isTextCorpus() && dataItems.getTransposed())
				jmLabels.setText("Show variable labels");
			else if (dataItems.isTextCorpus())
				jmLabels.setText("Show document labels");
		}
		else
		{
			dataSize = 0;
			if (selected != null)
			{
				selected.removeSelectionChangedListener(this);
				selected.removeSelectableObject(this);
				selected = null;
			}
		}
		
		//reset the zoom and pan
		
		reset();
	}
	
	/**
	* Method to accept a set of positions to plot points at
	*/
	
	public void setPositions(ArrayList positions)
	{
		this.positions = positions;
	}
	
	/**
	* Method that should be called whenever an screen update is required.
	*/
	
	public void update(boolean bPointsMoved)
	{
		bPointsChanged = bPointsMoved;
		repaint();
	}
	
	public void update()
	{
		bPointsChanged = false;
		repaint();
	}
	
	/**
	* Overrides the standard paint method, displays all the dots onto the 
	* screen
	*
	*/
	
	public void paint(Graphics g)
	{
		try
		{
			width  = this.getWidth();
			height = this.getHeight();
			
			g.setColor(BACKGROUND);
			g.fillRect(0, 0, width, height);
			
			//paintContours(g);
			
			// Get the origin.
			
			origin.x = width / 2;
			origin.y = height / 2;
			
			if (dataItems != null)
				storePointCoords();
			
			// Should we plot a variable frequency/density surface.
			
			if ((dataItems != null) && (bFreqSurface || bDensSurface))
			{
				paintSurface(g);
			}
			
			int numItems = dataSize;
			
			// Draw the trails first
			
			/*for (int i = 0 ; i < numItems ; i++ )
			{
				int k = i;
				
				if (!selected.getState(k))
					drawTrail(g, k, DES_TRAIL_COL);
			}
			
			for (int i = 0 ; i < numItems ; i++)
			{
				int k = i;
				
				if (selected.getState(k))
					drawTrail(g, k, getDotColor(k));
			}*/
			
			// Then draw data points
			
			for (int i = 0 ; i < numItems ; i++)
			{
				if (!selected.getState(i))
					drawDot(g, i, DESELECTED_DOT);
			}
			
			numSelected = 0;
			
			for (int i = 0 ; i < numItems ; i++)
			{
				if (selected.getState(i))
				{
					drawDot(g, i, getDotColor(i));
					numSelected++;
				}
			}
			
			// If the data set is a text collection and the user has issued a query,
			// highlight the points returned
			
			if (visMod.getQueryPoints() != null)
			{
				for (int i = 0; i < numItems; i++)
				{
					Integer ID = new Integer(dataItems.getDataItem(i).getID());
					
					if (visMod.getQueryPoints().contains(ID) && !selected.getState(i))
					{
						drawDot(g, i, Color.red);
					}
					else if (visMod.getQueryPoints().contains(ID) && selected.getState(i))
					{
						drawDot(g, i, Color.blue);
					}
				}
			}
			
			if (numItems > 0)
				visMod.setSelectionNumberCaption(numSelected + " of " + dataItems.getSize() + " items selected");
			
			// If the user is annotating the layout then make sure that the text box remains visible
			
			if (txtLabel != null)
				if (txtLabel.isVisible())
					txtLabel.repaint();
			
			// If there are any annotations, render them
			
			if (annotations != null)
			{
				g.setFont(new Font("sansserif", Font.BOLD, 12));
				int x, y;
				
				for (int i = 0; i < annotations.size(); i++)
				{
					if (highlightedLabel == i)
					{
						// The darker the background, the lighter the text. Conversely,
						// the lighter the backround, the darker the text, But in this
						// case yellow
						
						g.setColor(Color.red);
					}
					else
					{
						// The darker the background, the lighter the text. Conversely,
						// the lighter the backround, the darker the text
						
						if (BACKGROUND.getRed() < 160)
							g.setColor(Color.white);
						else
						{
							int cl = 255 - BACKGROUND.getRed();
							g.setColor(new Color(cl, cl, cl));
						}
					}
					
					x = (int)calcPosX(((Double)xLabelPos.get(i)).doubleValue());
					y = (int)calcPosY(((Double)yLabelPos.get(i)).doubleValue()) + (txtLabel.getHeight() / 2);
					
					g.drawString(annotations.get(i).toString(), x, y);
				}
			}
		}
		catch (Exception e){}
		
		if ((exLabels != null) && (!draggingRectangle))
			exLabels.paint((Graphics2D)g, (Rectangle2D)getBounds());
		
		if (bShowAllLabels)
		{
			paintAllLabels(g);
		}
	}
	
	/**
	* Render all row labels on the scatterplot
	*/
	
	private void paintAllLabels(Graphics g)
	{
		Coordinate c;
		int x, y;
		String sFrequency;
		if (dataItems.getRowLabels() == null)
			return;
		
		// The darker the background, the lighter the text. Conversely,
		// the lighter the backround, the darker the text
		
		int re, gr, bl;
		if (BACKGROUND.getRed() < 160)
			re = gr = bl = 255;
		else
			re = gr = bl = 255 - BACKGROUND.getRed();
		
		g.setColor(new Color(re, gr, bl));
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			// If the label represents a point that is selected, change the font
			// to bold.
			
			if (selected.getState(i))
				g.setFont(new Font("sansserif", Font.BOLD, 12));
			else
				g.setFont(new Font("sansserif", Font.PLAIN, 10));
			
			c = (Coordinate)positions.get(i);
			x = (int)calcPosX(c.getX());
			y = (int)calcPosY(c.getY());
			
			// If the data are binary then include the variable frequency
			
			if (i < dataItems.getRowLabels().size())
				if (dataItems.getBinary())
				{
					sFrequency = new Integer(dataItems.getBinaryFreq()[i]).toString();
					g.drawString(dataItems.getRowLabels().get(i).toString() + " (" + sFrequency + ")", x + 5, y - 2);
				}
				else
					g.drawString(dataItems.getRowLabels().get(i).toString(), x + 5, y - 2);
		}
	}
	
	private void paintContours(Graphics g)
	{
		if (positions == null)
			return;
		
		double width = 20;
		double dist, dx, dy, oldVal= 0, expDist;
		Coordinate c = null;
		double x;
		double y;
		
		for (int i = 0; i < getWidth(); i += 1)
		{
			for (int j = 0; j < getHeight(); j += 1)
			{
				double val = 0;
				
				for (int k = 0; k < positions.size(); k++)
				{
					c = (Coordinate)positions.get(k);
					x = calcPosX( c.getX() );
					y = calcPosY( c.getY() );
					dx = x - (double)i;
					dy = y - (double)j;
					dist = dx * dx + dy * dy;
					expDist = (Math.exp(-dist / (2 * width * width)));
					val += expDist;
				}
				oldVal = val;
				val *= 128;
				
				if ((val >= 0) && (val <= 255))
				{
					//g.setColor(new Color((int)val, (int)val, (int)val));
					//g.fillRect(i, j, 1, 1);
					
					if ((val > 125) && (val < 128))
					{
						g.setColor(new Color(0, 255, 0));
						g.fillRect(i, j, 2, 2);
					}
					
					if ((val > 97) && (val < 100))
					{
						g.setColor(new Color(0, 255, 0));
						g.fillRect(i, j, 2, 2);
					}
				}
			}
		}
	}
	
	/**
	* Draws a dot located at index index in the data item collection, with 
	* the color col.  Draw all of this onto the graphics g.
	*
	*/
	
	protected void drawDot(Graphics g, int index, Color col)
	{
		double x = coords[index][0];
		double y = coords[index][1];
		
		//draw data point
		
		g.setColor(col);
		g.fillRect((int)(x - (DATA_DOT_SIZE / 2)), 
		(int)(y - (DATA_DOT_SIZE / 2)),
		(int)DATA_DOT_SIZE, 
		(int)DATA_DOT_SIZE);
	}
	
	/**
	* Return the rectangle representing the shape of a given point
	*/
	
	protected Shape getPointShape(int item)
	{
		Coordinate c = ((Coordinate)positions.get(item));
		double x = calcPosX( c.getX() );
		double y = calcPosY( c.getY() );
		
		return new Rectangle((int)(x - (DATA_DOT_SIZE / 2)), 
		(int)(y - (DATA_DOT_SIZE / 2)),
		(int)DATA_DOT_SIZE, 
		(int)DATA_DOT_SIZE);
	}
	
	/**
	* Draws a trail on the data item with index index. and colour col
	* 
	* @param g
	* @param index
	* @param col
	*/
	
	protected void drawTrail(Graphics g, int index, Color color)
	{
		Color col = new Color(color.getRed(), 
		color.getGreen(), 
		color.getBlue());
		Coordinate lastPt = new Coordinate();
		
		// If there are new points in the data set, create new trails
		
		while (index > trails.size() - 1)
			trails.add(new ArrayList());
			
		int limit = Math.min(((ArrayList)trails.get(index)).size(), 
			TRAIL_LENGTH);
			
		for (int k = 0 ; k < limit ; k++) 
		{
			Coordinate trailPt = (Coordinate)((ArrayList)trails.
			get(index)).get(k);
				
			double x = calcPosX(trailPt.getX());
			double y = calcPosY(trailPt.getY());
				
			if ((int)x != (int)calcPosX(lastPt.getX()) &&
			(int)y != (int)calcPosY(lastPt.getY()))
			{
				g.setColor(col);

				if (drawTrails) 
				{
					g.fillRect( (int)( x - (TRAIL_DOT_SIZE / 2)), 
						(int)( y - (TRAIL_DOT_SIZE / 2)), 
						(int)TRAIL_DOT_SIZE, 
						(int)TRAIL_DOT_SIZE);
				}
			}
				
			// Make color darker every 3 iterations
				
			col = fadeToBackground(col);
			lastPt = trailPt;
		}
	}
	
	/**
	* Method to convert the color passed in to a version of itself more 
	* similar to the background color
	*
	* @param col The colour to fade
	* @return The resultant color
	*/
	
	private Color fadeToBackground(Color col)
	{
		int red = (int)( (((double)col.getRed() * 3.0) + 
			(double)BACKGROUND.getRed() ) / 4.0);
		int green = (int)( (((double)col.getGreen() * 3.0) + 
			(double)BACKGROUND.getGreen() ) / 4.0);
		int blue = (int)( (((double)col.getBlue() * 3.0) + 
			(double)BACKGROUND.getBlue() ) / 4.0); 
		
		return new Color(red, green, blue);
	}
	
	/**
	* calculates the x position that this object should be at within
	* the current JPanel and layoutbounds of the layoutModel
	*
	*/
	
	public double calcPosX(double x)
	{
		return origin.x + (x * (width / 2) * scaleX) + offsetX;
	}
	
	/**
	* calculates the y position that this object should be at within
	* the current JPanel and layoutbounds of the layoutModel
	*
	*/
	
	public double calcPosY(double y)
	{
		return origin.y + (y * (height / 2) * scaleY) + offsetY;
	}
	
	/**
	* Given a point in the coordinate space of the scatterplot, return its
	* x coordinate in the data space
	*/
	
	private double transposeX(double x)
	{
		return (x - origin.x - offsetX) / ((width / 2) * scaleX);
	}
	
	private double transposeY(double y)
	{
		return (y - origin.y - offsetY) / ((height / 2) * scaleY);
	}
	
	/**
	* Returns the dot with index current drawing color, gets this from the 
	* ColorScales class by looking up this dots colour relative to the current
	* colorScheme and colorField.
	*
	* @param index The index of the dot to retrieve color for
	* @return The color that this dot should be
	*/
	
	public Color getDotColor(int index)
	{
		// If the data set is CSV derived then return the appropriate colour
		// otherwise if the set is tf-idf (really high D), or triangular just return a default
		
		double max;
		double min;
		double val;
		int i;
		
		// We don't have any variables for a triangular data matrix, thus return
		// one colour.
		
		if (dataItems.getLowerTriangular())
			return Color.yellow;
		
		// If an interpolation surface is currently visible, invert the colours used for selected points.
		
		if (bFreqSurface || bDensSurface)
		{
			if ((((DataItem)dataItems.getDataItem(0)).getTextValues() == null) && (!bColourToFreq) && (!bColourToNumVars))
			{
				max = convToDouble(dataItems.getMaximum(colorField));
				min = convToDouble(dataItems.getMinimum(colorField));
				val = convToDouble(dataItems.getDataItem(index).getValues()[colorField]);
				i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
				return colorScheme[255 - i];
			}
			else if (bColourToFreq)
			{
				max = dataItems.getMaxFreq();
				min = dataItems.getMinFreq();
				val = dataItems.getBinaryFreq()[index];
				i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
				return colorScheme[255 - i];
			}
			else
				return Color.yellow;
		}
		else
		{
			if ((((DataItem)dataItems.getDataItem(0)).getTextValues() == null) && (!bColourToFreq) && (!bColourToNumVars))
			{
				max = convToDouble(dataItems.getMaximum(colorField));
				min = convToDouble(dataItems.getMinimum(colorField));
				val = convToDouble(dataItems.getDataItem(index).getValues()[colorField]);
				i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
				return colorScheme[i];
			}
			else if (bColourToFreq)
			{
				max = dataItems.getMaxFreq();
				min = dataItems.getMinFreq();
				val = dataItems.getBinaryFreq()[index];
				i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
				return colorScheme[i];
			}
			else if (bColourToNumVars)
			{
				max = dataItems.getMaxNumVars();
				min = dataItems.getMinNumVars();
				val = dataItems.getBinaryNumVars()[index];
				i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
				return colorScheme[i];
			}
			else
				return Color.yellow;
		}
	}
	
	/**
	* private method to convert an object which is known to be of type Integer
	* Double or Date then into a double primitive
	*
	* @param o The object
	* @return The corresponding double value
	*/
	
	private double convToDouble(Object o) 
	{
		if (o instanceof Integer)
			return (double)((Integer)o).intValue();
		else if (o instanceof Double)
			return ((Double)o).doubleValue();
		else if (o instanceof Date)
			return (double)((Date)o).getTime();

		// keep compiler happy
		
		return 0.0;			
	}
	
	/**
	* updates the contents of the trails information.
	*
	*/
	
	public void updateTrails()
	{
		int numItems = dataSize;
		
		// Draw the trails first
		
		for (int i = 0 ; i < numItems ; i++ )
		{
			int k = i;
					
			Coordinate c = ((Coordinate)positions.get(k));
			((ArrayList)trails.get(k)).add(0, new Coordinate(c.getX(), 
				c.getY()));
					
			if (((ArrayList)trails.get(k)).size() > TRAIL_LENGTH)
				((ArrayList)trails.get(k)).remove(
					((ArrayList)trails.get(k)).size()-1);
		}
	}
	
	//--------------------------
	// Methods to handle rectangle selection from the mouse handler
	//-----------------------
	
	/**
	* Sets the currently selected indices of the scatter panel to be the 
	* indices of the data items held within rect
	*
	* @param rect The rectangle to look in
	*/
	
	public void handleSelection(Rectangle rect)
	{
		if (dataItems != null)
		{
			selectionData.clear();
			theSelection = new HashSet(getRectContents(rect, selectionData, false));
			selected.updateSelection();
			
			enableDisableExportButon();
			
			visMod.sendSelectPort();
		}
	}
	
	/**
	* Adds all the data items which are within the on screen rectangle rect
	* to the currently selected indices that this object holds
	*
	* @param rect The rectange of data items
	*/
	
	public void addToSelection(Rectangle rect)
	{
		if (dataItems != null)
		{
			ArrayList pos = new ArrayList();
			theSelection.addAll(getRectContents(rect, pos, true));
			selectionData.addAll(pos);
			selected.updateSelection();
			
			enableDisableExportButon();
			
			visMod.sendSelectPort();
		}
	}
	
	/**
	* Removes all the data items which are within the on screen rectangle rect
	* from the currently selected indices that this object holds
	*
	* @param rect The rectange of data items
	*/
	
	public void removeFromSelection(Rectangle rect)
	{
		if (dataItems != null)
		{
			ArrayList pos = new ArrayList();
			theSelection.removeAll(getRectContents(rect, pos, false));
			selectionData.removeAll(pos);
			selected.updateSelection();
			
			enableDisableExportButon();
			
			visMod.sendSelectPort();
		}
	}
	
	private void enableDisableExportButon()
	{
		if (theSelection.size() > 0)
		{
			visMod.getSendButton().setEnabled(true);
			visMod.getSendButton().setForeground(Color.black);
		}
		else
		{
			visMod.getSendButton().setEnabled(false);
			visMod.getSendButton().setForeground(Color.gray);
		}
	}
	
	/**
	* Returns the indices of the data items that are contained inside the 
	* on screen rectangle rect.
	*
	* @param rect The rectangle
	* @return The collection of indices inside this rectangle
	*/
	
	protected Collection getRectContents(Rectangle rect, ArrayList pos, boolean bAdding)
	{
		// Adjust the selection rectangle so that it is relative to the on 
		// screen dots
		
		rect.x -= (int)(DATA_DOT_SIZE / 2.0); 
		rect.y -= (int)(DATA_DOT_SIZE / 2.0); 
		
		if (rect.height == 0 && rect.width == 0) 
		{
			rect.height = (int)DATA_DOT_SIZE; 
			rect.width  = (int)DATA_DOT_SIZE;
		}
		else 
		{
			rect.height += 2 * (int)DATA_DOT_SIZE; 
			rect.width  += 2 * (int)DATA_DOT_SIZE;
		}
		
		ArrayList contents = new ArrayList();
		int numItems = dataSize;
			
		// Find the on screen positions that are within this rect
		
		for (int k = 0; k < numItems ; k++ ) 
		{
			if (rect.contains(coords[k][0], coords[k][1]))
			{
				if (bAdding)
				{
					if (!theSelection.contains(new Integer(dataItems.getDataItem(k).getID())))
					{
						contents.add(new Integer(dataItems.getDataItem(k).getID()));
						pos.add(new Integer(k));
					}
				}
				else
				{
					contents.add(new Integer(dataItems.getDataItem(k).getID()));
					pos.add(new Integer(k));
				}
			}
		}
			
		return contents;
	}
	
	protected ArrayList getRContents(Rectangle rect)
	{
		// Adjust the selection rectangle so that it is relative to the on 
		// screen dots
		
		rect.x -= (int)(DATA_DOT_SIZE / 2.0); 
		rect.y -= (int)(DATA_DOT_SIZE / 2.0); 
		
		if (rect.height == 0 && rect.width == 0) 
		{
			rect.height = (int)DATA_DOT_SIZE; 
			rect.width  = (int)DATA_DOT_SIZE;
		}
		else 
		{
			rect.height += 2 * (int)DATA_DOT_SIZE; 
			rect.width  += 2 * (int)DATA_DOT_SIZE;
		}
		
		ArrayList contents = new ArrayList();
		int numItems = dataSize;
		
		for (int k = 0; k < numItems ; k++ ) 
		{
			if (rect.contains(coords[k][0], coords[k][1]))
			{
				contents.add(new Integer(k));
			}
		}
		
		return contents;
	}
	
	//--------------------
	// Standard accessors for variables that can be modified
	//----------------
	
	public void setColorField(String field)
	{
		this.colorField = dataItems.getFields().indexOf(field);
		update(true);
	}
	
	/**
	* Sets the color scheme for the scatter panel to use
	*
	*/
	
	public void setColorScheme(String color)
	{
		this.colorScheme = ColorScales.getScale(color);
		colourSchemeName = color;
		update(true);
	}
	
	/**
	* Sets the selected column value, this is used for the tooltips to give a 
	* value
	* 
	* @param colName The name of the column which is selected
	*/
	
	public void setSelectedColumn( String colName )
	{
		selectedCol = dataItems.getFields().indexOf( colName );
	}
	
	/**
	* resets the scale and offset values to their original values.  This 
	* should be called whenever a new layout is being started or someone 
	* has scrolled or zoomed away from all the data
	*/
	
	public void reset()
	{
		if (txtLabel != null)
			txtLabel.setVisible(false);
		
		scaleX  = 1.0;  scaleY  = 1.0;
		offsetX = 0.0;  offsetY = 0.0;
		
		update();
	}
	
	/**
	* Sets the scale to be the new value scale
	*
	* @param scale The new scale value
	*/
	
	public void setScale( double scaleX, double scaleY )
	{
		if (txtLabel != null)
			txtLabel.setVisible(false);
		
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	/**
	* Sets the x scale to be the new value scale
	*
	* @param scaleX The new x scale value
	*/
	
	public void setScaleX( double scaleX )
	{
		if (txtLabel != null)
			txtLabel.setVisible(false);
		
		this.scaleX = scaleX;
	}
	
	/**
	* Sets the y scale to be the new value scale
	*
	* @param scaleY The new y scale value
	*/
	
	public void setScaleY( double scaleY )
	{
		if (txtLabel != null)
			txtLabel.setVisible(false);
		
		this.scaleY = scaleY;
	}
	
	/**
	* Returns the current value of scale in the x direction
	* 
	* @return The scale value
	*/ 
	
	public double getScaleX()
	{
		return scaleX;
	}
	
	/**
	* Returns the current value of scale in the y direction
	* 
	* @return The scale value
	*/ 
	
	public double getScaleY()
	{
		return scaleY;
	}
	
	/**
	* Returns the x offset value
	*
	* @return The x offset
	*/
	
	public double getOffsetX()
	{
		return offsetX;
	}
	
	/**
	* Returns the y offset value
	*
	* @return The y offset
	*/
	
	public double getOffsetY()
	{
		return offsetY;
	}
	
	/**
	* sets the x offset value
	*
	* @return The x offset
	*/
	
	public void setOffsetX( double offsetX )
	{
		bPanning = true;
		
		if (txtLabel != null)
			txtLabel.setVisible(false);
		
		this.offsetX = offsetX;
	}
	
	/**
	* sets the y offset value
	*
	* @return The y offset
	*/
	
	public void setOffsetY( double offsetY )
	{
		if (txtLabel != null)
			txtLabel.setVisible(false);
		
		this.offsetY = offsetY;
	}
	
	/**
	* Returns the Point at the centre of the display, the origin
	*
	* @return The origin
	*/
	
	public Point getOrigin()
	{
		return origin;
	}
	
	// -----------------------
	// Methods required by selectable interface
	// -----------------
	
	/**
	* Method that will be called the selection is changed in the selection 
	* handler
	*
	* @param select The selection handler that created the selection changed
	*/
	
	public void selectionChanged(SelectionHandler select)
	{
		update();
	}
	
	/**
	* Returns the selection of this object
	*
	* @return The selection
	*/
	
	public Collection getSelection()
	{
		return theSelection;
	}
	
	public ArrayList getSelectionData()
	{
		return selectionData;
	}
	
	public void setSelection(Collection selection)
	{
		theSelection = 	(HashSet)selection;
		selectionData = new ArrayList(selection);
		selected.updateSelection();
		enableDisableExportButon();
	}
	
	public SelectionHandler getSelectionHandler()
	{
		return selected;
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
		return null;
	}
	
	/**
	* Sets this selectable object to select all its items
	*
	*/
	
	public void selectAll()
	{
		theSelection = new HashSet();
		for (int i = 0 ; i < dataItems.getSize() ; i++)
		{
			theSelection.add(new Integer(i));
			selectionData.add(new Integer(i));
		}
	}

	/**
	* Sets this selectable item to select none of it items
	*
	*/
	
	public void selectNone()
	{
		theSelection.clear();
		selectionData.clear();
	}
	
	// over ride methods in JComponent to display tool tips
	
	/**
	* Sets the tool tip text to be displayed to be relative to the data element
	* under the cursor
	*
	* @param event The mouse event that caused this tool tip to be displayed
	* @return The tool tip text to display
	*/
	
	public String getToolTipText(MouseEvent event)
	{
		// Find index of item under mouse cursor
		
		/*ArrayList indices = new ArrayList(getRectContents(
			new Rectangle(event.getX(), 
				event.getY(), 0, 0 )));
		
		if ( indices.size() == 0 ) 
			return null;
		
		// Get the value in selectedCol of the data collection
		
		Object val = dataItems.getDataItem(((Integer)indices.get(0)).intValue() ).getValues()[selectedCol];
		
		if ( val == null )
			return "no data";
		else
			return val.toString();*/
		
		return null;
	}
	
	/**
	* Sets the location that the tool tip should be displayed at
	*
	* @param event The mouse event that caused this to be called
	* @return The coords of the tool tip
	*/
	
	public Point getToolTipLocation(MouseEvent event)
	{
		return new Point(event.getX()+10, event.getY());
	}
	
	/**
	* Add a popup menu to allow the user to export selected points to
	* MS Excel for plotting
	*/
	
	private void addPopupMenu()
	{
		popup = new JPopupMenu();
		
		// Excel
		
		exportSelectionToExcel = new JMenuItem("Export selection to Excel");
		exportSelectionToExcel.addActionListener(this);
		exportSelectionToExcel.setActionCommand("exportToExcelForPlot");
		popup.add(exportSelectionToExcel);
		
		// Colour over
		
		jmColourOver = new JMenuItem("Colour to attribute");
		jmColourOver.addActionListener(this);
		jmColourOver.setActionCommand("colourOver");
		popup.add(jmColourOver);
		
		// Colour scheme
		
		jmColourScheme = new JMenuItem("Select colour scheme");
		jmColourScheme.addActionListener(this);
		jmColourScheme.setActionCommand("colourScheme");
		popup.add(jmColourScheme);
		
		// Background brightness
		
		jmBackgroundColour = new JMenuItem("Set background brightness");
		jmBackgroundColour.addActionListener(this);
		jmBackgroundColour.setActionCommand("background_brightness");
		popup.add(jmBackgroundColour);
		
		// Labelling
		
		jmAddLabel = new JMenuItem("Annotate the layout");
		jmAddLabel.addActionListener(this);
		jmAddLabel.setActionCommand("addLabel");
		popup.add(jmAddLabel);
		
		jmEditLabel = new JMenuItem("Edit annotation");
		jmEditLabel.addActionListener(this);
		jmEditLabel.setActionCommand("editLabel");
		popup.add(jmEditLabel);
		
		// Image dump
		
		jmImageDump = new JMenuItem("Dump view to file");
		jmImageDump.addActionListener(this);
		jmImageDump.setActionCommand("imageDump");
		popup.add(jmImageDump);
		
		// Colour according to binary variables frequency
		
		jmFreq = new JCheckBoxMenuItem("Colour according to frequency", false);
		jmFreq.addActionListener(this);
		jmFreq.setActionCommand("freq");
		jmFreq.setVisible(false);
		popup.add(jmFreq);
		
		// Colour according to the number of variables
		
		jmNumVars = new JCheckBoxMenuItem("Colour to number of vars", false);
		jmNumVars.addActionListener(this);
		jmNumVars.setActionCommand("numVars");
		jmNumVars.setVisible(false);
		popup.add(jmNumVars);
		
		// Apply an Inverse Distance Weighted surface to the plot of variable
		// frequencies.
		
		jmVariableFreq = new JCheckBoxMenuItem("Show frequency surface");
		jmVariableFreq.addActionListener(this);
		jmVariableFreq.setActionCommand("freqSurface");
		jmVariableFreq.setVisible(false);
		popup.add(jmVariableFreq);
		
		jmDensitySurface = new JCheckBoxMenuItem("Show density surface");
		jmDensitySurface.addActionListener(this);
		jmDensitySurface.setActionCommand("densSurface");
		jmDensitySurface.setVisible(false);
		popup.add(jmDensitySurface);
		
		// When the data are binary let the user show all labels simultaneously
		
		jmLabels = new JCheckBoxMenuItem("Show variable labels", false);
		jmLabels.addActionListener(this);
		jmLabels.setActionCommand("allLabels");
		jmLabels.setVisible(false);
		popup.add(jmLabels);
		
		popup.addMouseListener(this);
	}
	
	/**
	* Implementation of the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() instanceof JMenuItem)
		{
			if (e.getActionCommand().equals("exportToExcelForPlot"))
			{
				// Show a dialogue to ask the user which two variables he/she/it
				// would like to plot
				
				// Create an array of the ordinal positions for selected items
				
				int index = 0;
				ArrayList sel = new ArrayList();
				
				for (int i = 0; i < dataItems.getSize(); i++)
				{
					if (selected.getState(i))
						sel.add(new Integer(i));
				}
				
				excelExportDialog = ExcelExport.getInstance("Export selection to Excel", sel, dataItems);
				excelExportDialog = null;
			}
			else if (e.getActionCommand().equals("colourOver"))
			{
				// Show the dialog to allow the user to colour over attributes
				
				colorForm = ColorAttributeForm.getInstance("Colour over attributes", dataItems, this, colorField);
				colorForm = null;
			}
			else if (e.getActionCommand().equals("colourScheme"))
			{
				// Show the dialog to allow the user to change the current colour scheme
				
				colorSchemeForm = ColorSchemeForm.getInstance("Select a colour scheme", this, colourSchemeName);
				colorSchemeForm = null;
			}
			else if (e.getActionCommand().equals("addLabel"))
			{
				// Allow the user to place a text box at a location on the layout for annotation
				
				if (txtLabel == null)
				{
					// Init the array lists for storing annotation info
					
					annotations = new ArrayList();
					xLabelPos = new ArrayList();
					yLabelPos = new ArrayList();
					
					txtLabel = new JTextField();
					txtLabel.addFocusListener(this);
					txtLabel.addActionListener(this);
					add(txtLabel);
				}
				
				txtLabel.setVisible(false);
				txtLabel.setBounds(xLabel, yLabel, 100,  20);
				txtLabel.setVisible(true);
				if (!txtLabel.getText().equals(""))
					txtLabel.select(0, txtLabel.getText().length());
				
				highlightedLabel = -1;
				txtLabel.grabFocus();
			}
			else if (e.getActionCommand().equals("editLabel"))
			{
				// Allow the user to edit an annotation
				
				bEditingAnnotation = true;
				
				Graphics g = getGraphics();
				g.setFont(new Font("sansserif", Font.BOLD, 12));
				FontMetrics fm = g.getFontMetrics();
				Rectangle2D.Float bounds;
				bounds = (Rectangle2D.Float)fm.getStringBounds(annotations.get(highlightedLabel).toString(), g);
				bounds.setRect(calcPosX(((Double)xLabelPos.get(highlightedLabel)).doubleValue()), 
					calcPosY(((Double)yLabelPos.get(highlightedLabel)).doubleValue()) - 8, bounds.getWidth(), 20);
				
				txtLabel.setVisible(false);
				txtLabel.setText(annotations.get(highlightedLabel).toString());
				txtLabel.setBounds(bounds.getBounds());
				txtLabel.setVisible(true);
				
				if (!txtLabel.getText().equals(""))
					txtLabel.select(0, txtLabel.getText().length());
				
				txtLabel.grabFocus();
			}
			else if (e.getActionCommand().equals("imageDump"))
			{
				// Dump the view into a png image file
				
				Dump dumper = new PNGDump();
				chooser = new JFileChooser(".");
				
				try
				{
					// Let the user decide what the image file is called and
					// where it is created
					
					int returnVal;
					ExampleFileFilter filter = new ExampleFileFilter("png", "PNG files");
					chooser.setFileFilter(filter);
					returnVal = chooser.showSaveDialog(this);
					
					if(returnVal == JFileChooser.APPROVE_OPTION)
					{
						File file;
						
						if (chooser.getSelectedFile().getAbsolutePath().indexOf(".png") == -1)
						{
							file = new File(chooser.getSelectedFile().getAbsolutePath() + ".png");
						}
						else
							file = chooser.getSelectedFile();
						
						// dump the image
						
						dumper.dumpComponent(file, this);
					}
				}
				catch (java.io.IOException eio){System.out.println("Error with screen dump");}
			}
			else if (e.getActionCommand().equals("freqSurface"))
			{
				// Remove density plot.
				if (bDensSurface)
				{
					bDensSurface = false;
					jmDensitySurface.setState(false);
				}
				
				bFreqSurface = jmVariableFreq.getState();
				bPointsChanged = true;
				repaint();
				
				// If the frequency surface is about to be shown,
				// display controls for altering the power value to use.
				
				if (bFreqSurface)
					IDWForm = IDWPowerForm.getInstance("Set intperpolation power", this, IDWPower);
			}
			else if (e.getActionCommand().equals("densSurface"))
			{
				// Remove frequency plot.
				if (bFreqSurface)
				{
					bFreqSurface = false;
					jmVariableFreq.setState(false);
				}
				
				bDensSurface = jmDensitySurface.getState();
				bPointsChanged = true;
				repaint();
				
				// If the density surface is about to be shown,
				// display controls for altering the power value to use.
				
				if (bDensSurface)
					IDWForm = IDWPowerForm.getInstance("Set intperpolation power", this, IDWPower);
			}
			else if (e.getActionCommand().equals("background_brightness"))
			{
				// Show the dialog to allow the user to change the background brightness
				
				backgroundBrightForm = BackgroundBrightForm.getInstance("Set background brightness", this, BACKGROUND);
				backgroundBrightForm = null;
			}
			else if (e.getActionCommand().equals("freq"))
			{
				bColourToFreq = jmFreq.getState();
				jmColourOver.setEnabled(!jmFreq.getState());
				repaint();
			}
			else if (e.getActionCommand().equals("numVars"))
			{
				bColourToNumVars = jmNumVars.getState();
				jmColourOver.setEnabled(!jmNumVars.getState());
				repaint();
			}
			else if (e.getActionCommand().equals("allLabels"))
			{
				bShowAllLabels = jmLabels.getState();
				
				if (bShowAllLabels)
				{
					exLabels = null;
				}
				else
				{
					exLabels = new DefaultExcentricLabels(dataItems, selected);
					exLabels.setVisualization(this);
					exLabels.setMaxLabels(10);
					
					// The darker the background, the lighter the text. Conversely,
					// the lighter the backround, the darker the text
					
					if (BACKGROUND.getRed() < 160)
						exLabels.setBackgroundColor(Color.white);
					else
					{
						int cl = 255 - BACKGROUND.getRed();
						exLabels.setBackgroundColor(new Color(cl, cl, cl));
					}
				}
			}
		}
		else if (e.getSource() == txtLabel)
		{
			// When the user pressses enter in the annotation text box, hide it and
			// save the text
			
			textLostFocus();
		}
	}
	
	/**
	* Accessor method to set the backgroun colour
	*/
	
	public void setBackgroundColour(Color backColour)
	{
		BACKGROUND = backColour;
		
		if (exLabels != null)
		{
			if (BACKGROUND.getRed() < 160)
				exLabels.setBackgroundColor(Color.white);
			else
			{
				int cl = 255 - BACKGROUND.getRed();
				exLabels.setBackgroundColor(new Color(cl, cl, cl));
			}
		}
		
		repaint();
	}
	
	/**
	* Implementation of the MouseListener interface
	*/
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			xLabel = e.getX();
			yLabel = e.getY();
			
			if ((highlightedLabel > -1) && !bPanning && !bEditingAnnotation)
			{
				bEditingAnnotation = true;
				
				exportSelectionToExcel.setVisible(false);
				exportSelectionToExcel.setEnabled(false);
				jmColourOver.setVisible(!dataItems.getLowerTriangular());
				jmColourScheme.setVisible(false);
				jmAddLabel.setVisible(false);
				jmEditLabel.setVisible(true);
				
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if ((numSelected > 0) && !bPanning)
			{
				exportSelectionToExcel.setVisible(!dataItems.getLowerTriangular());
				exportSelectionToExcel.setEnabled(!dataItems.getLowerTriangular());
				jmColourOver.setVisible(!dataItems.getLowerTriangular());
				jmColourScheme.setVisible(!dataItems.getLowerTriangular());
				jmAddLabel.setVisible(true);
				jmEditLabel.setVisible(false);
				jmBackgroundColour.setVisible(true);
				
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
			else if ((dataItems != null) && !bPanning)
			{
				exportSelectionToExcel.setVisible(!dataItems.getLowerTriangular());
				exportSelectionToExcel.setEnabled(false);
				jmColourOver.setVisible(!dataItems.getLowerTriangular());
				jmColourScheme.setVisible(!dataItems.getLowerTriangular());
				jmAddLabel.setVisible(true);
				jmEditLabel.setVisible(false);
				jmBackgroundColour.setVisible(true);
				
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		bPanning = false;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		bEditingAnnotation = false;
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	/**
	* Implementation of the FocusListener interface for the txtLabel component used
	* in annotating the layout
	*/
	
	public void focusLost(FocusEvent e)
	{
		textLostFocus();
	}
	
	public void focusGained(FocusEvent e){}
	
	private void textLostFocus()
	{
		// When txtLabel loses the focus, render the text on the layout
		
		if (txtLabel != null)
		{
			bEditingAnnotation = false;
			if (highlightedLabel < 0)
			{
				// A new annotation
				
				if (!txtLabel.getText().equals(""))
				{
					Double x, y;
					x = new Double(transposeX((double)xLabel));
					y = new Double(transposeY((double)yLabel));
					
					if (!xLabelPos.contains(x) || !yLabelPos.contains(y))
					{
						annotations.add(txtLabel.getText());
						xLabelPos.add(x);
						yLabelPos.add(y);
						repaint();
					}
				}
			}
			else
			{
				// Editing an existing annotation
				
				if (txtLabel.getText().equals(""))
				{
					annotations.remove(highlightedLabel);
					xLabelPos.remove(highlightedLabel);
					yLabelPos.remove(highlightedLabel);
					highlightedLabel = -1;
				}
				else
					annotations.set(highlightedLabel, txtLabel.getText());
				
				bEditingAnnotation = false;
				
				repaint();
			}
			
			txtLabel.setVisible(false);
		}
		
		bLabelLostFocus = true;
	}
	
	/**
	* Implementation of MouseMotionListener
	*/
	
	public void mouseMoved(MouseEvent e)
	{
		if (!bEditingAnnotation)
			if (mouseOverAnnotation(e.getX(), e.getY()) != null)
				if (exLabels != null)
					exLabels.setVisible(false);
	}
	
	public void mouseDragged(MouseEvent e){}
	
	/**
	* If the user moves the mouse over an annotation, the following method returns the
	* bounds of the text.
	*/
	
	private Rectangle2D.Float mouseOverAnnotation(int x, int y)
	{
		// First, determine if there are any annotations
		
		if (xLabelPos != null)
		{
			if (xLabelPos.size() > 0)
			{
				// There are some annotations. Determine whether the mouse is
				// within the region of one of them
				
				Graphics g = getGraphics();
				g.setFont(new Font("sansserif", Font.BOLD, 12));
				FontMetrics fm = g.getFontMetrics();
				Rectangle2D.Float bounds;
				int tempi;
				
				for (int i = 0; i < annotations.size(); i++)
				{
					bounds = (Rectangle2D.Float)fm.getStringBounds(annotations.get(i).toString(), g);
					bounds.setRect(calcPosX(((Double)xLabelPos.get(i)).doubleValue()),
						calcPosY(((Double)yLabelPos.get(i)).doubleValue()), bounds.getWidth(), bounds.getHeight());
					
					if (bounds.contains((double)x, (double)y))
					{
						// Remove other annotation highlights
						
						if ((highlightedLabel > -1) && (highlightedLabel != i))
						{
							tempi = highlightedLabel;
							highlightedLabel = -1;
							highlightAnnotation(tempi);
						}
						
						if (highlightedLabel != i)
						{
							highlightedLabel = i;
							
							highlightAnnotation(i);
						}
						
						return bounds;
					}
				}
				
				// Remove other annotation highlights
				
				if (highlightedLabel > -1)
				{
					tempi = highlightedLabel;
					highlightedLabel = -1;
					highlightAnnotation(tempi);
				}
			}
		}
		highlightedLabel = -1;
		return null;
	}
	
	/**
	* Highlight a given annotation
	*/
	
	private void highlightAnnotation(int i)
	{
		Graphics g = getGraphics();
		g.setFont(new Font("sansserif", Font.BOLD, 12));
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D.Float bounds;
		
		bounds = (Rectangle2D.Float)fm.getStringBounds(annotations.get(i).toString(), g);
		bounds.setRect(calcPosX(((Double)xLabelPos.get(i)).doubleValue()), 
			calcPosY(((Double)yLabelPos.get(i)).doubleValue()), bounds.getWidth(), bounds.getHeight());
			
		Shape origClip;
		
		if (g.getClip() != null)
			origClip = g.getClip();
		else
			origClip = getBounds();
		
		int xpos = (new Double(calcPosX(((Double)xLabelPos.get(i)).doubleValue()))).intValue();
		int ypos = (new Double(calcPosY(((Double)yLabelPos.get(i)).doubleValue()))).intValue();
		int w = (new Double(bounds.getWidth())).intValue();
		int h = (new Double(bounds.getHeight())).intValue();
		
		g.setClip(xpos, ypos, w, h);
		paint(g);
		g.setClip(origClip);
	}
	
	/**
	* Accessor method for determining whether the user is dragging the boudning box
	*/
	
	public void setDraggingBox(boolean b)
	{
		draggingRectangle = b;
	}
	
	public boolean getDraggingBox()
	{
		return draggingRectangle;
	}
	
	/**
	* Cannot serialise the class for excentric labeling and therefore need to set it here
	*/
	
	private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		if (dataItems != null)
		{
			if ((dataItems.getSize() > 0) && dataItems.getTransposed() && !bShowAllLabels)
			{	
				exLabels = new DefaultExcentricLabels(dataItems, selected);
				exLabels.setVisualization(this);
				exLabels.setMaxLabels(10);
				
				// The darker the background, the lighter the text. Conversely,
				// the lighter the backround, the darker the text
				
				if (BACKGROUND.getRed() < 160)
					exLabels.setBackgroundColor(Color.white);
				else
				{
					int cl = 255 - BACKGROUND.getRed();
					exLabels.setBackgroundColor(new Color(cl, cl, cl));
				}
			}
		}
	}
	
	/**
	* Divide the interval between minX, maxX and minY, maxY into cells
	* and colour a surface according to Inverse Distance Weighting (IDW)
	* interpolation.
	*/
	
	private void paintSurface(Graphics g)
	{
		int numXCells = 75;
		int numYCells = 75;
		int i, j;
		double xWidth = (maxX - minX) / (double)numXCells;
		double yHeight = (maxY - minY) / (double)numYCells;
		double xStart = minX;
		double YStart = minY;
		double x, y;
		
		// Determine the number of neighbours over which to carry out
		// IDW.
		
		if (dataItems.getSize() >= 20)
			numNeighbours = 20;
		else
			numNeighbours = dataItems.getSize();
		
		// Set up the counts array - the value we'll use to guide
		// interpolation.
		
		if (bPointsChanged)
		{
			// Also, setup the sorted x-axis array for finding k nearest neighbours.
			
			storeSortedArrays();
			
			counts = new float[dataItems.getSize()];
			
			for (i = 0; i < dataItems.getSize(); i++)
			{
				if (bFreqSurface)
					counts[i] = dataItems.getBinaryFreq()[i];
				else if (bDensSurface)
					counts[i] = densityAtPoint((int)coords[i][0], (int)coords[i][1]);
			}
		}
		
		// Store the cell coords.
		
		if (bPointsChanged || (cellCoords == null))
		{
			minCellValue = Double.MAX_VALUE;
			maxCellValue = Double.MIN_VALUE;
			cellValues = new double[numXCells][numYCells];
			bPointsChanged = true;
		}
		
		cellCoords = new double[numXCells][numYCells][2];
		
		for (i = 0; i < numXCells; i++)
		{
			x = ((i * xWidth) + xStart);
			
			for (j = 0; j < numYCells; j++)
			{
				// Draw a rectangle at the cell and colour it
				// according to a chosen value.
				
				y = ((j * yHeight) + YStart);
				
				// Store the x and y coordinates of the cells.
				
				cellCoords[i][j][0] = x;
				cellCoords[i][j][1] = y;
				
				// Keep track of the min and max cell values.
				
				if (bPointsChanged)
				{
					cellValues[i][j] = getCellValue((int)calcPosX(x), (int)calcPosY(y));
					
					if (cellValues[i][j] < minCellValue)
						minCellValue = cellValues[i][j];
					if (cellValues[i][j] > maxCellValue)
						maxCellValue = cellValues[i][j];
				}
			}
		}
		
		bPointsChanged = false;
		
		// Apply the colouring.
		
		int count = 0;
		int colVal;
		
		// Set the correct width and height of each cell. Add 1 pixel to make sure there are no gaps
		// rendered between the cells.
		
		int screenCellWidth = ((int)calcPosX(cellCoords[1][0][0])) - ((int)calcPosX(cellCoords[0][0][0])) + 1;
		int screenCellHeight = ((int)calcPosY(cellCoords[0][1][1])) - ((int)calcPosY(cellCoords[0][0][1])) + 1;
		
		for (i = 0; i < numXCells; i++)
		{
			for (j = 0; j < numYCells; j++)
			{
				colVal = (int)(((cellValues[i][j] - minCellValue)/(maxCellValue - minCellValue))*(colorScheme.length - 40)) + 20;
				g.setColor(colorScheme[colVal]);
				g.fillRect((int)calcPosX(cellCoords[i][j][0]) , (int)calcPosY(cellCoords[i][j][1]), screenCellWidth, screenCellHeight);
				count++;
			}
		}
	}
	
	/**
	* Calculate and store the density at the given point.
	**/
	
	private float densityAtPoint(int x, int y)
	{
		double [] distances = new double[numNeighbours];
		int [] neighbours  = new int[numNeighbours];
		getNeighbours(x, y, distances, neighbours);
		
		float sum = 0;
		for (int i = 0; i < numNeighbours; i++)
		{
			sum += distances[i];
		}
		return numNeighbours/(float)sum;
	}
	
	/**
	* Get the layout distance between two points.
	*/
	
	private double getLayoutDist(double x1, double y1, double x2, double y2)
	{
		return ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2));
	}
	
	private void storePointCoords()
	{
		int i;
		double x, y;
		Coordinate c;
		int numItems = dataItems.getSize();
		coords = new double[numItems][2];
		
		minX = Double.MAX_VALUE;
		maxX = Double.MIN_VALUE;
		minY = minX;
		maxY = maxX;
		
		for (i = 0; i < numItems; i++)
		{
			c = ((Coordinate)positions.get(i));
			x = calcPosX(c.getX());
			y = calcPosY(c.getY());
			coords[i][0] = x;
			coords[i][1] = y;
			
			// Get the min and max coordinate values.
			// Use these to determine the extent of the rendering region.
			
			if (c.getX() < minX)
				minX = c.getX();
			if (c.getX() > maxX)
				maxX = c.getX();
			if (c.getY() < minY)
				minY = c.getY();
			if (c.getY() > maxY)
				maxY = c.getY();
		}
	}
	
	/**
	* Given a cell, return its IDW value.
	*/
	
	private double getCellValue(int x, int y)
	{
		double [] distances = new double[numNeighbours];
		int [] neighbours  = new int[numNeighbours];
		getNeighbours(x, y, distances, neighbours);
		return calcWeights(neighbours, distances);
	}
	
	/**
	* Return the indices of the nearest numNeighbours points.
	* IDW will be calculated according to these.
	*/
	
	private void getNeighbours(int x, int y, double [] distances, int [] neighbours)
	{	
		/*try
		{
			keysXCopy = new ArrayList(keysX);
			getNearestNeighbours(x, y, distances, neighbours, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
		
		int i;
		ArrayList distance = new ArrayList(dataItems.getSize());
		ArrayList keys = new ArrayList(dataItems.getSize());
		
		for (i = 0; i < dataItems.getSize(); i++)
		{
			distance.add(new Double(getLayoutDist((double)x, (double)y, coords[i][0], coords[i][1])));
			keys.add(new Integer(i));
		}
		
		// Sort the distance keys in ascending order of the corresponding distances.
		
		DistanceComparator distComp = new DistanceComparator(distance, true);
		Collections.sort(keys, distComp);
		
		for (i = 0; i < numNeighbours; i++)
		{
			distances[i] = ((Double)distance.get(((Integer)keys.get(i)).intValue())).doubleValue();
			neighbours[i] = ((Integer)keys.get(i)).intValue();
		}
	}
	
	/**
	* Store and order the x values of the input data. This will
	* be used in finding the k-nearest neighbours for any point.
	**/
	
	private void storeSortedArrays()
	{
		sortedX = new ArrayList(dataItems.getSize());
		keysX = new ArrayList(dataItems.getSize());
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			sortedX.add(new Double((int)coords[i][0]));
			keysX.add(new Integer(i));
		}
		
		DistanceComparator distComp = new DistanceComparator(sortedX, true);
		Collections.sort(keysX, distComp);
	}
	
	/**
	* Given the array of sorted x-axis values, return the index
	* of the closest item according to its position on the x-axis.
	**/
	
	private int getNearestXPoint(int x, int y)
	{
		// Use a binary search to find the two closest point in the x-axis.
		
		int min = 0;
		int max = keysXCopy.size() - 1;
		int middle;
		int closestMatchIndex = -1;
		double dist, distL, distH;
		int realIndex, realIndexL, realIndexH;
		int xMaxIndex = ((Integer)keysXCopy.get(keysXCopy.size() - 1)).intValue();
		int xMinIndex = ((Integer)keysXCopy.get(0)).intValue();
		
		if (x >= ((Double)sortedX.get(xMaxIndex)).intValue())
		{
			// Value is at the top end of the scale.
			
			closestMatchIndex = keysXCopy.size() - 1;
			realIndex = ((Integer)keysXCopy.get(closestMatchIndex)).intValue();
			dist = getLayoutDist(x, y, (int)coords[realIndex][0], (int)coords[realIndex][1]);
			realIndexL = ((Integer)keysXCopy.get(closestMatchIndex - 1)).intValue();
			distL = getLayoutDist(x, y, (int)coords[realIndexL][0], (int)coords[realIndexL][1]);
			
			if (dist > distL)
				return closestMatchIndex - 1;
			else
				return closestMatchIndex;
		}
		else if (x <= ((Double)sortedX.get(xMinIndex)).intValue())
		{
			// Value is at the bottom end of the scale.
			
			closestMatchIndex = 0;
			realIndex = ((Integer)keysXCopy.get(closestMatchIndex)).intValue();
			dist = getLayoutDist(x, y, (int)coords[realIndex][0], (int)coords[realIndex][1]);
			realIndexH = ((Integer)keysXCopy.get(closestMatchIndex + 1)).intValue();
			distH = getLayoutDist(x, y, (int)coords[realIndexH][0], (int)coords[realIndexH][1]);
			
			if (dist > distH)
				return closestMatchIndex + 1;
			else
				return closestMatchIndex;
		}
		else
		{
			// Apply binary search.
			
			while(min <= max)
			{
				middle = (max + min) / 2;
				int xMiddleIndex = ((Integer)keysXCopy.get(middle)).intValue();
				if (x == ((Double)sortedX.get(xMiddleIndex)).intValue())
				{
					// Found the closest matching items on the x-axis.
					
					closestMatchIndex = middle;
					break;
				}
				else if (x < ((Double)sortedX.get(xMiddleIndex)).intValue())
				{
					// Search the lhs of the x-axis.
					
					max = middle - 1;
				}
				else
				{
					// Search the rhs of the x-axis.
					
					min = middle + 1;
				}
			}
			
			if (closestMatchIndex == -1)
				closestMatchIndex = max;
			
			// Search the neighouring scale items to find the actual closest.
			
			try
			{
				realIndex = ((Integer)keysXCopy.get(closestMatchIndex)).intValue();
				dist = getLayoutDist(x, y, (int)coords[realIndex][0], (int)coords[realIndex][1]);
				
				distL = Double.MAX_VALUE;
				if ((closestMatchIndex - 1) >= 0)
				{
					realIndexL = ((Integer)keysXCopy.get(closestMatchIndex - 1)).intValue();
					distL = getLayoutDist(x, y, (int)coords[realIndexL][0], (int)coords[realIndexL][1]);
				}
				
				distH = Double.MAX_VALUE;
				if ((closestMatchIndex + 1) < keysXCopy.size())
				{
					realIndexH = ((Integer)keysXCopy.get(closestMatchIndex + 1)).intValue();
					distH = getLayoutDist(x, y, (int)coords[realIndexH][0], (int)coords[realIndexH][1]);
				}
				
				if ((dist <= distL) && (dist <= distH))
					return closestMatchIndex;
				else if ((distL <= dist) && (distL <= distH))
				{
					return closestMatchIndex - 1;
				}
				else if ((distH <= dist) && (distH <= distL))
					return closestMatchIndex + 1;
				else
					return closestMatchIndex;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return closestMatchIndex;
			}
		}
	}
	
	private void getNearestNeighbours(int x, int y, double [] distances, int [] neighbours, int neighbourIndex)
	{
		// Get the distance to the closest item on the x-axis.
		
		int closestXIndex = getNearestXPoint(x, y);
		
		// Get the distance to the closest item.
		
		int realIndex;
		realIndex = ((Integer)keysXCopy.get(closestXIndex)).intValue();
		double xDist = getLayoutDist(x, y, (int)coords[realIndex][0], (int)coords[realIndex][1]);
		
		// Find all the points on the x-axis that are within this distance.
		
		double dx;
		ArrayList newDistances = new ArrayList();
		newDistances.add(new Double(xDist));
		ArrayList newIndices = new ArrayList();
		newIndices.add(new Integer(closestXIndex));
		ArrayList newIndicesMaker = new ArrayList();
		newIndicesMaker.add(new Integer(0));
		int currentIndex = closestXIndex - 1;
		
		// First search the lhs.
		
		while (currentIndex >= 0)
		{
			realIndex = ((Integer)keysXCopy.get(currentIndex)).intValue();
			dx = getLayoutDist(x, y, (int)coords[realIndex][0], (int)coords[realIndex][1]);
			
			if (dx <= xDist)
			{
				newDistances.add(new Double(dx));
				newIndices.add(new Integer(currentIndex));
				newIndicesMaker.add(new Integer(newIndicesMaker.size()));
			}
			currentIndex--;
		}
		
		// Now search the rhs.
		
		currentIndex = closestXIndex + 1;
		while (currentIndex <= (keysXCopy.size() - 1))
		{
			realIndex = ((Integer)keysXCopy.get(currentIndex)).intValue();
			dx = getLayoutDist(x, y, (int)coords[realIndex][0], (int)coords[realIndex][1]);
			
			if (dx <= xDist)
			{
				newDistances.add(new Double(dx));
				newIndices.add(new Integer(currentIndex));
				newIndicesMaker.add(new Integer(newIndicesMaker.size()));
			}
			currentIndex++;
		}
		
		// Find the smallest distance from the set of distances gathered.
		
		DistanceComparator distComp = new DistanceComparator(newDistances, true);
		Collections.sort(newIndicesMaker, distComp);
		
		closestXIndex = ((Integer)newIndicesMaker.get(0)).intValue();
		distances[neighbourIndex] = ((Double)newDistances.get(closestXIndex)).doubleValue();
		closestXIndex = ((Integer)newIndices.get(closestXIndex)).intValue();
		neighbours[neighbourIndex] = ((Integer)keysXCopy.get(closestXIndex)).intValue();
		
		// Remove the nearest neighbour from the sorted x-arrays.
		
		keysXCopy.remove(closestXIndex);
		
		// Recursively retrieve the k nearest neigbours.
		
		if ((neighbourIndex + 1) < (numNeighbours - 1))
			getNearestNeighbours(x, y, distances, neighbours, neighbourIndex + 1);
	}
	
	/**
	* Given a cell and the nearest points to it, determine the Inverse Distance Weighted (IDW)
	* interpolation value.
	*/
	
	private double calcWeights(int [] neighbours, double [] distances)
	{
		int power = IDWPower;
		
		double sumOfWeights = 0;
		int i;
		
		// Convert the distances to weights.
		
		for (i = 0; i < numNeighbours; i++)
		{
			if (distances[i] == 0)
				distances[i] = 0.000001;
			
			distances[i] = Math.pow(distances[i], (-power / 2d));
			sumOfWeights += distances[i];
		}
		
		// Divide each weight by the sum of weights.
		
		for (i = 0; i < numNeighbours; i++)
			distances[i] /= sumOfWeights;
		
		// Use the 'count' field values of the points to get the interp value.
		
		float countValue;
		sumOfWeights = 0;
		
		for (i = 0; i < numNeighbours; i++)
		{
			countValue = counts[neighbours[i]];
			sumOfWeights += (countValue * distances[i]);
		}
		
		return sumOfWeights;
	}
	
	/**
	* Accessor method for determining the power value used for IDW
	* interpolation.
	*/
	
	public void setIDWPower(int power)
	{
		IDWPower = power;
		bPointsChanged = true;
		repaint();
	}
}

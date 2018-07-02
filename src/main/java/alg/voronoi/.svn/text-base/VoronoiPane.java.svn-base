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
 * VoronoiPane: displays the planar ordinary Voronoi diagram
 * in the Voronoi module. The algorithm used is the topological approach
 * to the incremental method (see "Spatial Tesselations", Okabe et al.)
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import data.*;
import alg.Voronoi;
import parent_gui.*;
import math.*;
import ColorScales.ColorScales;
import excel.*;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.util.*;
import java.awt.event.*;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.Random;
import java.io.IOException;

public class VoronoiPane extends JPanel implements Selectable,
					     SelectionChangedListener, ActionListener,
					     MouseListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The reference to the parent visual object
	
	private Voronoi visMod;
	
	// The three intitial generators for the Voronoi diagram
	
	private Coord p1;
	private Coord p2;
	private Coord p3;
	
	// All generators
	
	private ArrayList generators = new ArrayList();
	
	// Store vertices
	
	private ArrayList vertices = new ArrayList();
	
	// Store polygons
	
	private ArrayList polygons = new ArrayList();
	
	// Store edges
	
	private ArrayList edges = new ArrayList();
	
	// Edges to be deleted
	
	private ArrayList delEdges = new ArrayList();
	
	// Quaternary tree
	
	private QuaternaryTree qTree;
	
	// Declarations for handling the scale of the diagram
	
	private           double            scaleX      = 1.0;
	private           double            scaleY      = 1.0;
	
	private           double            offsetX     = -0.5;
	private           double            offsetY     = -0.5;
	
	private           int               width;
	private           int               height;
	
	private           Point             origin;
	
	private           VoronoiMouseHandler      mouse;
	
	private  static      double            DATA_DOT_SIZE  = 3.5;
	
	// Variables for double buffering
	
	private Graphics paintGraphics;
	private Image offScreen = null;
	private int oldW, oldH;
	
	// Determine whether polygon areas or perimeters are to be used in clustering
	
	private boolean bPerimeterCluster = true;
	private boolean bAreaCluster = false;
	
	// The value that the user might set as a perimeter threshold under which
	// Voronoi polygons should be shaded
	
	private double thresholdAttribute = 0;
	
	// Variables for determining which parts of the Voronoi diagram
	// to render
	
	private boolean bShowAllEdges = false;
	private boolean bShowGenerators = true;
	private boolean bClusters = true;
	
	// Variables required by selection handling
	
	private SelectionHandler  selected = null;
	private Set theSelection;
	private DataItemCollection dataItems = null;
	private ArrayList points = null;
	
	// The color scheme used to highlight points
	
	private Color[] colorScheme;
	private int colorField;
	
	// Determine whether the Voronoi polygon perimeters/areas have been calculated
	
	private boolean bPolygonCalcPerimeter = false;
	private boolean bPolygonCalcArea = false;
	
	// Class to calculate Voronoi statistics
	
	private VoronoiStat vStat;
	
	// List of regions that have been seperated as a result of
	// the polygon/area threshold
	
	private ArrayList regions = null;
	
	// Store the selected cluster
	
	private ArrayList selectedCluster = null;
	
	// Store the highest density of a set of clusters
	
	private double maxDensity;
	
	// Determine whether the automatic clustering algorithm is responsible
	// for the current segementation, or the manual slider
	
	private boolean bSliderCluster = true;
	
	// An array of colours corresponding to the list of clusters (if any) in the view
	
	private ArrayList clusterColours = null;
	
	// Popup menu for allowing the user to export the selected points to MS Excel for charting
	
	JPopupMenu popup;
	JMenuItem exportSelectionToExcel;
	
	// A modal dialogue to allow users to exort data to Excel
	
	ExcelExport excelExportDialog;
	
	// If the user is panning don't show the context menu upon releasing the mouse
	
	private boolean bPanning = false;
	
	public VoronoiPane(Voronoi visMod)
	{
		this.visMod = visMod;
		
		// Set of selected indices
		
		theSelection = new HashSet();
		
		setPreferredSize(new Dimension(600, 600));
		setOpaque(true);
		setBackground(new Color(100, 100, 100));
		
		mouse = new VoronoiMouseHandler(this);
		origin = new Point();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		
		// Set the default colour scheme to 'Heated Object'
		
		setColorScheme("HeatedObject");
		
		// Add a second mouse listener for popup menus.
		
		addMouseListener(this);
		addPopupMenu();
	}
	
	public void init()
	{
		if (dataItems != null)
		{
			selected = new SelectionHandler(points.size(), dataItems);
			selected.addSelectableObject(this);
			selected.addSelectionChangedListener(this);
			
			for (int i = 0 ; i < (points.size()) ; i++) 
			{
				theSelection.add(new Integer(i));
			}
		}
		else
		{
			if (selected != null)
			{
				selected.removeSelectionChangedListener(this);
				selected.removeSelectableObject(this);
				selected = null;
			}
		}
	}
	
	/**
	* Draw the diagram.
	*/
	public void renderVoronoi(ArrayList points, DataItemCollection dataItems)
	{
		boolean bInit = false;
		if (this.dataItems == null)
			bInit = true;
		else if (this.dataItems != dataItems)
			bInit = true;
		
		this.points = points;
		this.dataItems = dataItems;
		
		if (bInit == true)
			init();
		
		createSeedGraph();
		
		// Get the vertex in the centre of the circle that
		// circumbscribes the three points
		
		createFirstVertex();
		
		// Create the vertices that represent the points on the
		// closed curve that surround the augmented geometric graph
		
		createInfiniteVertices();
		
		// Create the winged-edge data structure
		
		createWingedEdge();
		
		// Remove the temporary store of vertices
		
		vertices.clear();
		
		Coord c;
		
		// Build the quaternary tree for fast indexing
		
		qTree = new QuaternaryTree(points, p1, this);
		
		// Use quaternary reordering to add points to the diagram in an
		// efficient order
		
		populateDiagram();
		
		// Remove all deleted edges
		
		for (int i = 0; i < edges.size(); i++)
		{
			if (((Edge)edges.get(i)).deleted())
				edges.set(i, null);
		}
		
		// Calculate the perimeters/areas of the Voronoi polygons so
		// that we can highlight them according to area
		
		bPolygonCalcPerimeter = false;
		bPolygonCalcArea = false;
		
		// Instantiate the class that is responsible for calculating
		// statistics and clustering
		
		vStat = new VoronoiStat(polygons, this);
		
		if (bClusters)
		{
			if (bPerimeterCluster)
			{
				vStat.calculatePerimeters();
				bPolygonCalcPerimeter = true;
			}
			else if (bAreaCluster)
			{
				vStat.calculateAreas();
				bPolygonCalcArea = true;
			}
		}
		
		repaint();
	}
	
	/**
	* Add the points to the Voronoi diagram. Go through the
	* quaternary tree adding the nodes from each level from the
	* top downwards
	*/
	
	private void populateDiagram()
	{
		int k = qTree.levelCount();
		ArrayList nodes;
		QuadTreeNode node, parentNode;
		
		// Get each level of the tree
		
		for (int i = 0; i < k; i++)
		{
			nodes = qTree.getLevel(i);
			
			// If the node is not a leaf node, then only add the
			// generator to the diagram if it is not
			// the same generator that its parent references
			
			for (int j = 0; j < nodes.size(); j++)
			{
				node = (QuadTreeNode)nodes.get(j);
				parentNode = node.getParentNode();
				if ((node.getNodeType()) != QuadTreeNode.LEAF_NODE)
				{
					if ((node.getGenerator(0) != null) &&
						node.getGenerator(0) != parentNode.getGenerator(0))
					{
						addGenerator((Coord)node.getGenerator(0), node);
					}
				}
				else
				{
					for (int m = 0; m < node.getGeneratorCount(); m++)
					{
						if (node.getGenerator(m) != parentNode.getGenerator(0))
							addGenerator((Coord)node.getGenerator(m), node);
					}
				}
			}
		}
	}
	
	/**
	* Create the augmented geometric graph corresponding to the
	* Voronoi diagram of the intial three generator points
	* These generator points contain the unit square containing the
	* rest of the Voronoi diagram
	*/
	
	private void createSeedGraph()
	{
		// Three points whose convex hull forms a triangle
		// which contains the unit square
		
		// Point 1
		
		p1 = new Coord(0.5d, 3*Math.sqrt(2d)/2d+0.5);
		
		// Point 2
		
		p2 = new Coord(-3*Math.sqrt(6d)/4d+0.5, -3*Math.sqrt(2d)/4d+0.5);
		
		// Point 3
		
		p3 = new Coord(3*Math.sqrt(6d)/4d+0.5, -3*Math.sqrt(2d)/4d+0.5);
		
		// Store the intial generators
		
		generators.add(p1);
		generators.add(p3);
		generators.add(p2);
	}
	
	/**
	* Given the three intial generators of the seed diagram,
	* determine the circle that circumbscribes them.
	* The centre of this circle is the coordainate of the first
	* non-infinite vertex
	*/
	
	private boolean getCenter(Coord centre)
	{
		// Given three points, return the coordinates of the center of the circle passing
		// through them, return false if no such circle exists.
		
		boolean result;
		result = true;
		
		double x, y;
		double ma, mb;
		
		ma = 0d;
		mb = 0d;
		
	    	double nx1, nx2, nx3, ny1, ny2, ny3, ny4;
		
		nx1 = p1.getX(); 
		nx2 = p2.getX(); 
		nx3 = p3.getX();
		ny1 = p1.getY();
		ny2 = p2.getY();
		ny3 = p3.getY();
		
		if (nx1 != nx2)
			ma = (ny2 - ny1) / (nx2 - nx1);
		else
			result = false;
		
		if (nx2 != nx3)
			mb = (ny3 - ny3) / (nx3 - nx2);
		else
			result = false;
		
		if ((ma == 0) && (mb == 0))
			result = false;
		
		if (ma == mb)
			result = false;
		
		if (result == true)
		{
			x = (ma * mb * (ny1 - ny3) + mb * (nx1 + nx2) - ma * (nx2 + nx3)) / (2 * (mb - ma));
			
			if (ma != 0)
				y = -(x - (nx1 + nx2) / 2) / ma + (ny1 + ny2) / 2;
			else
				y = -(x - (nx2 + nx3) / 2) / mb + (ny2 + ny3) / 2;
			
			centre.setX(x);
			centre.setY(y);
		}
		
	    return result;
	}
	
	/**
	* Determine the radius of the circle that circumscribes the
	* first three generators
	*/
	
	private double getCircle(Coord centre)
	{
	    // Find the radius of the circle that circumscribes 3 points
	    // We do this by finding the distance between one of the
	    // points on the perimeter and the center of the circle
	    
	    if (getCenter(centre) == true)
		return Math.sqrt(((p1.getX() - centre.getX()) * (p1.getX() - centre.getX())) +
			((p1.getY() - centre.getY()) * (p1.getY() - centre.getY())));
	    else
		return 0d;
	}
	
	/**
	* From the centre point of the above cirlce, create
	* the first vertex of the Voronoi diagram
	*/
	
	private void createFirstVertex()
	{
		Coord centre = new Coord();
		if (getCenter(centre) == true)
		{
			Vertex v = new Vertex();
			v.setX(centre.getX());
			v.setY(centre.getY());
			v.setWi(1);
			
			// Add the vertex to the vetrices HashMap
			
			vertices.add(v);
		}
	}
	
	/**
	* Determine the infinite vertices on the closed curve containing the augmented
	* geometric graph. Do this by finding the line from the first non-infinite
	* vertex to the centre point of each line between the seed generators
	*/
	
	private void createInfiniteVertices()
	{
		addSeedVertex(p1, p3);
		addSeedVertex(p2, p3);
		addSeedVertex(p1, p2);
	}
	
	private void addSeedVertex(Coord c1, Coord c2)
	{
		double midX, midY;
		double centreX = ((Vertex)vertices.get(0)).getX();
		double centreY = ((Vertex)vertices.get(0)).getY();
		double length;
		
		// Mid-point of line connecting c1 and c2
		
		midX = (c1.getX() + c2.getX()) / 2d;
		midY = (c1.getY() + c2.getY()) / 2d;
		
		// Vector representing direction from centre vertex to this point
		
		length = Math.sqrt(((midX - centreX) * (midX - centreX)) + ((midY - centreY) * (midY - centreY)));
		
		Vertex v = new Vertex();
		v.setX((midX - centreX) / length);
		v.setY((midY - centreY) / length);
		v.setWi(0);
		
		vertices.add(v);
	}
	
	/**
	* Given the vertices of the seed Voronoi diagram, derive the 
	* winged edge data structure
	*/
	
	private void createWingedEdge()
	{
		createEdges();
		createPolygons();
		createVertices();
		
		// Finish setting the polygon and vertex variables for the edges
		
		((Edge)edges.get(0)).set_cwPredecessor((Edge)edges.get(2));
		((Edge)edges.get(0)).set_ccwPredecessor((Edge)edges.get(1));
		((Edge)edges.get(0)).set_cwSuccessor((Edge)edges.get(4));
		((Edge)edges.get(0)).set_ccwSuccessor((Edge)edges.get(3));
		((Edge)edges.get(0)).setLeftPolygon((Polygon)polygons.get(1));
		((Edge)edges.get(0)).setRightPolygon((Polygon)polygons.get(0));
		
		((Edge)edges.get(1)).set_cwPredecessor((Edge)edges.get(0));
		((Edge)edges.get(1)).set_ccwPredecessor((Edge)edges.get(2));
		((Edge)edges.get(1)).set_cwSuccessor((Edge)edges.get(5));
		((Edge)edges.get(1)).set_ccwSuccessor((Edge)edges.get(4));
		((Edge)edges.get(1)).setLeftPolygon((Polygon)polygons.get(2));
		((Edge)edges.get(1)).setRightPolygon((Polygon)polygons.get(1));
		
		((Edge)edges.get(2)).set_cwPredecessor((Edge)edges.get(1));
		((Edge)edges.get(2)).set_ccwPredecessor((Edge)edges.get(0));
		((Edge)edges.get(2)).set_cwSuccessor((Edge)edges.get(3));
		((Edge)edges.get(2)).set_ccwSuccessor((Edge)edges.get(5));
		((Edge)edges.get(2)).setLeftPolygon((Polygon)polygons.get(0));
		((Edge)edges.get(2)).setRightPolygon((Polygon)polygons.get(2));
		
		((Edge)edges.get(3)).set_cwPredecessor((Edge)edges.get(5));
		((Edge)edges.get(3)).set_ccwPredecessor((Edge)edges.get(2));
		((Edge)edges.get(3)).set_cwSuccessor((Edge)edges.get(0));
		((Edge)edges.get(3)).set_ccwSuccessor((Edge)edges.get(4));
		((Edge)edges.get(3)).setLeftPolygon((Polygon)polygons.get(0));
		((Edge)edges.get(3)).setRightPolygon((Polygon)polygons.get(3));
		
		((Edge)edges.get(4)).set_cwPredecessor((Edge)edges.get(3));
		((Edge)edges.get(4)).set_ccwPredecessor((Edge)edges.get(0));
		((Edge)edges.get(4)).set_cwSuccessor((Edge)edges.get(1));
		((Edge)edges.get(4)).set_ccwSuccessor((Edge)edges.get(5));
		((Edge)edges.get(4)).setLeftPolygon((Polygon)polygons.get(1));
		((Edge)edges.get(4)).setRightPolygon((Polygon)polygons.get(3));
		
		((Edge)edges.get(5)).set_cwPredecessor((Edge)edges.get(4));
		((Edge)edges.get(5)).set_ccwPredecessor((Edge)edges.get(1));
		((Edge)edges.get(5)).set_cwSuccessor((Edge)edges.get(2));
		((Edge)edges.get(5)).set_ccwSuccessor((Edge)edges.get(3));
		((Edge)edges.get(5)).setLeftPolygon((Polygon)polygons.get(2));
		((Edge)edges.get(5)).setRightPolygon((Polygon)polygons.get(3));
	}
	
	private void createVertices()
	{
		Vertex v;
		
		v = (Vertex)vertices.get(0);
		v.setEdgeAroundVertex((Edge)edges.get(0));
		
		v = (Vertex)vertices.get(1);
		v.setEdgeAroundVertex((Edge)edges.get(3));
		
		v = (Vertex)vertices.get(2);
		v.setEdgeAroundVertex((Edge)edges.get(4));
		
		v = (Vertex)vertices.get(3);
		v.setEdgeAroundVertex((Edge)edges.get(5));
	}
	
	private void createPolygons()
	{
		Polygon p;
		
		// Initially there are four polygons, including
		// the theoretical outermost infinite region
		
		for (int i = 0; i < 4; i++)
		{
			p = new Polygon();
			p.setEdgeAroundPolygon((Edge)edges.get(i));
			polygons.add(p);
		}
		
		((Polygon)polygons.get(0)).setGenerator(p1);
		((Polygon)polygons.get(1)).setGenerator(p3);
		((Polygon)polygons.get(2)).setGenerator(p2);
		
		p1.setPolygon((Polygon)polygons.get(0));
		p3.setPolygon((Polygon)polygons.get(1));
		p2.setPolygon((Polygon)polygons.get(2));
	}
	
	private void createEdges()
	{
		Edge e;
		
		// Create edge 0
		
		e = new Edge();
		e.setStartVertex((Vertex)vertices.get(0));
		e.setEndVertex((Vertex)vertices.get(1));
		edges.add(e);
		
		// Create edge 1
		
		e = new Edge();
		e.setStartVertex((Vertex)vertices.get(0));
		e.setEndVertex((Vertex)vertices.get(2));
		edges.add(e);
		
		// Create edge 2
		
		e = new Edge();
		e.setStartVertex((Vertex)vertices.get(0));
		e.setEndVertex((Vertex)vertices.get(3));
		edges.add(e);
		
		// Create edge 3
		
		e = new Edge();
		e.setStartVertex((Vertex)vertices.get(3));
		e.setEndVertex((Vertex)vertices.get(1));
		edges.add(e);
		
		// Create edge 4
		
		e = new Edge();
		e.setStartVertex((Vertex)vertices.get(1));
		e.setEndVertex((Vertex)vertices.get(2));
		edges.add(e);
		
		// Create edge 5
		
		e = new Edge();
		e.setStartVertex((Vertex)vertices.get(2));
		e.setEndVertex((Vertex)vertices.get(3));
		edges.add(e);
	}
	
	/**
	* Given a vertex j, determine the edges and polygons
	* that are incident to it. e is the list of edges incident to the vertex
	* and p is the list of polygons incident to the vertex
	*/
	
	public void getEdgesAndPolygons(Vertex j, ArrayList e, ArrayList p)
	{
		Edge k, kStart;
		
		k = j.getEdgeAroundVertex();
		kStart = j.getEdgeAroundVertex();
		
		if (e != null)
			e.add(k);
		
		boolean bFinished = false;
		
		while (bFinished == false)
		{
			if (j == k.getStartVertex())
			{
				if (p != null)
					p.add(k.getLeftPolygon());
				
				k = k.get_ccwPredecessor();
			}
			else
			{
				if (p != null) 
					p.add(k.getRightPolygon());
				
				k = k.get_ccwSuccessor();
			}
			
			if (k == kStart)
			{
				bFinished = true;
			}
			else
				if (e != null)
					e.add(k);
		}
	}
	
	/**
	* Given a polygon, determine the edges and vertices that
	* suround it. e is the list of edges around the polygon
	* and v is the list of vertices around the polygon
	*/
	
	public void getEdgesAndVertices(Polygon p, ArrayList e, ArrayList v)
	{
		Edge k, kStart;
		
		k = p.getEdgeAroundPolygon();
		kStart = p.getEdgeAroundPolygon();
		
		if (e != null)
			e.add(k);
		
		boolean bFinished = false;
				
		while (!bFinished)
		{
			if (p == k.getLeftPolygon())
			{
				if (v != null)
					v.add(k.getEndVertex());
				
				k = k.get_cwSuccessor();
			}
			else
			{
				if (v != null)
					v.add(k.getStartVertex());
				
				k = k.get_cwPredecessor();
			}
			
			if (k == kStart)
			{
				bFinished = true;
			}
			else
				if (e != null) 
					e.add(k);
		}
	}
	
	/**
	* H(Pi, Pj, Pk, Pi) = 0 represents the circle passing through the points
	* Pi, Pj, Pk counterclockwise in this order. If H(Pi, Pj, Pk, P) < 0 then
	* the circle contains point P. If H(Pi, Pj, Pk, P) >= 0 where P is not in
	* {Pi, Pj, Pk}, then the circloe is empty
	*/
	
	private double H(Coord Pi, Coord Pj, Coord Pk, Coord Pl)
	{
		double J2ijk, J3ijk, J4ijk;
		double Xi, Xj, Xk, Xl, Yi, Yk, Yj, Yl;
		
		Xi = Pk.getX();  Yk = Pi.getY();
		Xj = Pj.getX();  Yj = Pj.getY();
		Xk = Pi.getX();  Yi = Pk.getY();
		Xl = Pl.getX();  Yl = Pl.getY();
		
		J2ijk = ((Yi - Yk) * (((Xj - Xk) * (Xj - Xk)) + ((Yj - Yk) * (Yj - Yk)))) - 
			((Yj - Yk) * (((Xi - Xk) * (Xi - Xk)) + ((Yi - Yk) * (Yi - Yk))));
			
		J3ijk = ((Xi - Xk) * (((Xj - Xk) * (Xj - Xk)) + ((Yj - Yk) * (Yj - Yk)))) - 
			((Xj - Xk) * (((Xi - Xk) * (Xi - Xk)) + ((Yi - Yk) * (Yi - Yk))));
			
		J4ijk = ((Xi - Xk) * (Yj - Yk)) - ((Xj - Xk) * (Yi - Yk));
		
		double result = ((J2ijk * (Xl - Xk)) - (J3ijk * (Yl - Yk)) + (J4ijk * (((Xl - Xk) * (Xl - Xk)) + ((Yl - Yk) * (Yl - Yk))))); 
		
		return result;
	}
	
	/**
	* Method to add a new generator to the Voronoi diagram
	*/
	
	public void addGenerator(Coord p, QuadTreeNode node)
	{
		// Get the polygon that surrounds the closest generator to p
		
		Polygon closestG = findClosestGenerator(p, node);
		
		if (closestG != null)
		{
			Polygon closestPolygon = closestG;
			
			// List of vertices surrounding the polygon
			
			ArrayList v = new ArrayList();
			
			getEdgesAndVertices(closestPolygon, null, v);
			
			// Array to hold vertices that will be deleted
			
			ArrayList T = new ArrayList();
			
			// For each vertex (Qijk) that is on the boundary of the polygon
			// find the one that gives the smallest value of H(Pi, Pj, Pk, P)
			// Add this vertex to T
			
			double minH = Double.MAX_VALUE;
			double hValue;
			Vertex smallestV = null;
			ArrayList pg = new ArrayList();
			Coord Pi, Pj, Pk;
			
			for (int i = 0; i < v.size(); i++)
			{
				pg.clear();
				
				getEdgesAndPolygons((Vertex)v.get(i), null, pg);
				
				Pi = ((Polygon)pg.get(0)).getGenerator();
				Pj = ((Polygon)pg.get(1)).getGenerator();
				Pk = ((Polygon)pg.get(2)).getGenerator();
				
				// Make sure that we don't try to use the virtual generator
				// that's in the outermost, infinite region
				
				if ((Pi != null) && (Pj != null) && (Pk != null))
				{
					hValue = H(Pi, Pj, Pk, p);
					
					if (hValue < minH)
					{
						minH = hValue;
						smallestV = (Vertex)v.get(i);
					}
				}
			}
			
			T.add(smallestV);
			
			// Expand the set of vertices T that will form the tree
			// that will be deleted to make way for the new Voronoi
			// polygon
			
			expandT(T, p, 0);
			deleteEdges();
			createNewVertices(T, closestPolygon, p);
			
			generators.add(p);
			
			T.clear();
		}
	}
	
	/**
	* For every edge connecting a vertex in T with a vertex
	* not in T, create a new vertex on the edge and thus divide
	* the edge into two edges
	*/
	
	private void createNewVertices(ArrayList T, Polygon closestPolygon, Coord p)
	{
		// The array list of edges that are to be split in two
		
		ArrayList modifiedEdges = new ArrayList();
		Edge e;
		
		int i;
		
		Vertex endV, startV;
		ArrayList ed = new ArrayList();
		
		for (i = 0; i < T.size(); i++)
		{
			ed.clear();
			
			getEdgesAndPolygons(((Vertex)T.get(i)), ed, null);
			
			for (int j = 0; j < ed.size(); j++)
			{
				startV = ((Edge)ed.get(j)).getStartVertex();
				endV = ((Edge)ed.get(j)).getEndVertex();
				if (!(T.contains(startV) && T.contains(endV)))
					modifiedEdges.add((Edge)ed.get(j));
			}
		}
		
		try
		{
			traceEdges(modifiedEdges, closestPolygon, p, T);
		}
		catch (Exception ex){}
	}
	
	private void traceEdges(ArrayList modifiedEdges, Polygon closestPolygon, Coord p, ArrayList T)
	{
		Edge e, newEdge, first;
		
		// Create a placeholder for the new polygon
		
		Polygon newPolygon = new Polygon();
		newPolygon.setGenerator(p);
		p.setPolygon(newPolygon);
		
		// Start with the edges that have the closest polygon to the new
		// generator, on their getLeftPolygon and/or getRighPolygon properties
		
		Edge firstEdge = null, secondEdge = null;
		
		int i, firstIndex = 0, secondIndex = 0;
		for (i = 0; i < modifiedEdges.size(); i++)
		{
			e = (Edge)modifiedEdges.get(i);
			if (e.getLeftPolygon() == closestPolygon)
			{
				if (firstEdge == null)
				{
					firstEdge = e;
					firstIndex = i;
				}
				else if (firstEdge != null)
				{
					secondEdge = e;
					secondIndex = i;
				}
				
				if ((firstEdge != null) && (secondEdge != null))
					break;
			}
			else if (e.getRightPolygon() == closestPolygon)
			{
				if (secondEdge == null)
				{
					secondEdge = e;
					secondIndex = i;
				}
				else if (secondEdge != null)
				{
					firstEdge = e;
					firstIndex = i;
				}
				
				if ((firstEdge != null) && (secondEdge != null))
					break;
			}
		}
		
		ArrayList newEdges = new ArrayList(); // List of newly created edges
		
		// Create the first new edge
		
		newEdge = createFirstNewEdge(firstEdge, secondEdge, closestPolygon, p, newPolygon);
		
		ArrayList aTemp = sortInitialEdges(newEdge, p, firstEdge, secondEdge);
		
		if (aTemp != null)
		{
			int temp = secondIndex;
			secondIndex = firstIndex;
			firstIndex = temp;
			
			firstEdge = (Edge)aTemp.get(0);
			secondEdge = (Edge)aTemp.get(1);
		}
		
		modifiedEdges.remove(secondIndex);
		
		first = firstEdge;
		
		newEdges.add(newEdge); // Store the new edge
		newPolygon.setEdgeAroundPolygon(newEdge); // Set the edge attribute for the new polygon
		
		// Create the other new edges
		
		ArrayList modEdges = new ArrayList();
		modEdges.add(firstEdge);
		modEdges.add(secondEdge);
		
		boolean bFinished = false;
		Polygon lastBounded = closestPolygon;
		Polygon nextPolygon;
		
		while (!bFinished)
		{
			Vertex prevStart = ((Edge)newEdges.get(newEdges.size() - 1)).getEndVertex();
			
			if (secondEdge.getLeftPolygon() == lastBounded)
				nextPolygon = secondEdge.getRightPolygon();
			else
				nextPolygon = secondEdge.getLeftPolygon();
				
			lastBounded = nextPolygon;
			
			for (i = 0; i < modifiedEdges.size(); i++)
			{
				// Search for the edge that shares a polygon other than the
				// the latest one bounded, with secondEdge
				
				e = (Edge)modifiedEdges.get(i);
				
				if ((e.getLeftPolygon() == nextPolygon) || 
					(e.getRightPolygon() == nextPolygon))
				{
					secondIndex = i;
					firstEdge = secondEdge;
					secondEdge = e;
					break;
				}
			}
			
			modifiedEdges.remove(secondIndex);
			
			if (secondEdge == first)
			{
				// We've completed the loop bounding the subtree contained in T
				
				newEdge = new Edge();
				newEdge.setStartVertex(prevStart);
				newEdge.setEndVertex(((Edge)newEdges.get(0)).getStartVertex());
				newEdge.setLeftPolygon(newPolygon);
				newEdge.setRightPolygon(nextPolygon);
				newEdges.add(newEdge); // Store the new edge
				bFinished = true;
			}
			else
			{
				newEdge = createNewEdge(secondEdge,  nextPolygon, p, newPolygon);
				newEdge.setStartVertex(prevStart);
				newEdges.add(newEdge); // Store the new edge
				modEdges.add(secondEdge);
			}
		}
		
		// Add the new edges to the set of existing edges
		
		for (i = 0; i < newEdges.size(); i++)
		{
			e = (Edge)newEdges.get(i);
			edges.add(e);
		}
		
		modifyEdges(newEdges, modEdges, T, newPolygon);
		
		// Add the new polygon to the last entry of the polygons array
		
		polygons.add(newPolygon);
	}
	
	/**
	* When creating the first new Voronoi edge, we must determine
	* upon which of the two existing edges the new edge's startVertex will be.
	*/
	
	private ArrayList sortInitialEdges(Edge newEdge, Coord p, Edge firstEdge,
		Edge secondEdge)
	{
		// Make sure that p is to the left of the new edge
		
		Vertex v1, v2;
		v1 = newEdge.getStartVertex();
		v2 = newEdge.getEndVertex();
		
		ArrayList result = null;
		
		double iTest = (p.getY() - v1.getY()) * (v2.getX() - v1.getX()) - 
			(p.getX() - v1.getX()) * (v2.getY() - v1.getY());
		
		if (iTest > 0)
		{
			result = new ArrayList(2);
			newEdge.setStartVertex(v2);
			newEdge.setEndVertex(v1);
			result.add(secondEdge);
			result.add(firstEdge);
		}
		
		return result;
	}
	
	/**
	* Given the set of edges that are to be split in half and
	* the newly created edges, split the modified edges and
	* delete the sub-tree contained in T
	*/
	
	private void modifyEdges(ArrayList newEdges, ArrayList modEdges, ArrayList T, Polygon newPolygon)
	{
		Edge eMod, eNew, e;
		
		// Modify existing edges
		
		int i;
		for (i = 0; i < modEdges.size(); i ++)
		{
			eNew = (Edge)newEdges.get(i);
			eMod = (Edge)modEdges.get(i);
			
			if (T.contains(eMod.getStartVertex()))
			{
				eMod.setStartVertex(eNew.getStartVertex());
				
				eMod.set_ccwPredecessor(eNew);
				if (i > 0)
					eMod.set_cwPredecessor((Edge)newEdges.get(i - 1));
				else
					eMod.set_cwPredecessor((Edge)newEdges.get(newEdges.size() - 1));
			}
			else if (T.contains(eMod.getEndVertex()))
			{
				eMod.setEndVertex(eNew.getStartVertex());
				
				eMod.set_ccwSuccessor(eNew);
				if (i > 0)
					eMod.set_cwSuccessor((Edge)newEdges.get(i - 1));
				else
					eMod.set_cwSuccessor((Edge)newEdges.get(newEdges.size() - 1));
			}
		}
		
		// Update the predecessor and successor properties of the newly
		// created edges
		
		for (i = 0; i < newEdges.size(); i++)
		{
			e = (Edge)newEdges.get(i);
			e.set_cwPredecessor((Edge)modEdges.get(i));
			
			if (i > 0)
				e.set_ccwPredecessor((Edge)newEdges.get(i - 1));
			else
				e.set_ccwPredecessor((Edge)newEdges.get(newEdges.size() - 1));
			
			if (i == (newEdges.size() - 1))
			{
				e.set_ccwSuccessor((Edge)modEdges.get(0));
				e.set_cwSuccessor((Edge)newEdges.get(0));
			}
			else
			{
				e.set_ccwSuccessor((Edge)modEdges.get(i + 1));
				e.set_cwSuccessor((Edge)newEdges.get(i + 1));
			}
		}
	}
	
	/**
	* Project a line between the generator of poli and p and then find
	* the coordinates of where the perpendicular bisector of this line
	* intersects firstEdge and secondEdge. These will be the coordinates of
	* the new vertices. Draw an edge between these
	*/
	
	private Edge createFirstNewEdge(Edge firstEdge, Edge secondEdge, Polygon poli,  Coord p, 
		Polygon newPolygon)
	{
		// Get the generator for polygon pi
		
		Coord pi = poli.getGenerator();
		
		// Get the line between pi and p
		
		double x1, x2, y1, y2;
		x1 = p.getX(); x2 = pi.getX();
		y1 = p.getY();  y2 = pi.getY();
		
		// Create a line representing the perpendicular bisector of this line
		
		Coord mid = new Coord((x1 + x2) / 2d, (y1 + y2) / 2d); // Mid point of line
		Coord bi = new Coord((y1 - y2), -(x1 - x2)); // Direction of perpendicular line
		double px1, px2, py1, py2; // Segment of the bisector
		px1 = mid.getX();
		py1 = mid.getY();
		px2 = mid.getX() + bi.getX();
		py2 = mid.getY() + bi.getY();
		
		// Find the point where this line intersects firstEdge and secondEdge
		// then create the edge with these coordinates as the start and end
		// vertices
		
		Edge newEdge = new Edge();
		Vertex v;
		
		v = new Vertex(getIntersection(firstEdge, px1, py1, px2, py2));
		v.setWi(1);
		
		newEdge.setStartVertex(v);
		newEdge.getStartVertex().setEdgeAroundVertex(newEdge);
		
		v = new Vertex(getIntersection(secondEdge, px1, py1, px2, py2));
		v.setWi(1);
		
		newEdge.setEndVertex(v);
		newEdge.getEndVertex().setEdgeAroundVertex(newEdge);
		
		// Set the polygon attributes
		
		newEdge.setRightPolygon(poli);
		newEdge.setLeftPolygon(newPolygon);
		
		return newEdge;
	}
	
	/**
	* Project a line between the generator of poli and p and then find
	* the coordinates of where the perpendicular bisector of this line
	* intersects firstEdge. These will be the coordinates of
	* the new vertice. Draw an edge between these
	*/
	
	private Edge createNewEdge(Edge secondEdge, Polygon poli,  Coord p, 
		Polygon newPolygon)
	{
		// Get the generator for polygon pi
		
		Coord pi = poli.getGenerator();
		
		// Get the line between pi and p
		
		double x1, x2, y1, y2;
		x1 = p.getX(); x2 = pi.getX();
		y1 = p.getY();  y2 = pi.getY();
		
		// Create a line representing the perpendicular bisector of this line
		
		Coord mid = new Coord((x1 + x2) / 2d, (y1 + y2) / 2d); // Mid point of line
		Coord bi = new Coord((y1 - y2), -(x1 - x2)); // Direction of perpendicular line
		double px1, px2, py1, py2; // Segment of the bisector
		px1 = mid.getX();
		py1 = mid.getY();
		px2 = mid.getX() + bi.getX();
		py2 = mid.getY() + bi.getY();
		
		// Find the point where this line intersects firstEdge and secondEdge
		// then create the edge with these coordinates as the start and end
		// vertices
		
		Edge newEdge = new Edge();
		Vertex v;
		
		v = new Vertex(getIntersection(secondEdge, px1, py1, px2, py2));
		v.setWi(1);
		
		newEdge.setEndVertex(v);
		newEdge.getEndVertex().setEdgeAroundVertex(newEdge);
		
		// Set the polygon attributes
		
		newEdge.setRightPolygon(poli);
		newEdge.setLeftPolygon(newPolygon);
		
		return newEdge;
	}
	
	/**
	* Given an edge and a line, return a coordinate representing the point
	* at which the line intersects the edge
	*/
	
	private Coord getIntersection(Edge e, double xa, double ya, double xb, double yb)
	{
		double x1 = xa; double y1 = ya;
		double x2 = xb; double y2 = yb;
		double x3, x4, y3, y4;
		
		if (e.getStartVertex().getWi() == 0)
		{
			x3 = e.getEndVertex().getX() + e.getStartVertex().getX();
			x4 = e.getEndVertex().getX();
			y3 = e.getEndVertex().getY() + e.getStartVertex().getY();
			y4 = e.getEndVertex().getY();
		}
		else if (e.getEndVertex().getWi() == 0)
		{
			x3 = e.getStartVertex().getX();
			x4 = e.getStartVertex().getX() + e.getEndVertex().getX();
			y3 = e.getStartVertex().getY();
			y4 = e.getStartVertex().getY() + e.getEndVertex().getY();
		}
		else
		{
			x3 = e.getStartVertex().getX();
			x4 = e.getEndVertex().getX();
			y3 = e.getStartVertex().getY();
			y4 = e.getEndVertex().getY();
		}
		
		double nma, nmb;  	// The two numerators
		double dm;      	// The common denominator
		double ua, ub;    	// The coefficients.
		
		nma = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3)));
		nmb = (((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3)));
		dm = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
		
		if ((dm == 0) && (nma == 0)){}
			//System.out.println("Some Voronoi edges are co-incident.");
		else if (dm == 0){}
			//System.out.println("Some Voronoi edges are parallel.");
		else
		{
			ua = nma / dm;
			ub = nmb / dm;
			
			double x, y;
			
			x = x1 + (ua * (x2 - x1));
			y = y1 + (ua * (y2 - y1));
			
			return new Coord(x, y);
		}
		return null;
	}
	
	/** for each vertex that is connected by an edge to the vertices in T,
	* add that vertex if H(Pi, Pj, Pk, P) < 0 and the vertex is not on the
	* outermost circuit of the geometric graph (v.getWi() = 0)
	*/
	
	private void expandT(ArrayList T, Coord p, int startNum)
	{
		Edge e;
		Vertex startVertex, endVertex, vertexToAdd, vInT;
		
		vInT = (Vertex)T.get(startNum);
		
		ArrayList eg = new ArrayList();
		
		getEdgesAndPolygons(vInT, eg, null);
		
		for (int i = 0; i < eg.size(); i++)
		{
			e = (Edge)eg.get(i);
			
			if (e != null)
			{
				startVertex = e.getStartVertex();
				endVertex = e.getEndVertex();
				
				// Make sure that the edge is not wholly on the outermost circuit
				
				if ((startVertex.getWi() == 1) || (endVertex.getWi() == 1))
				{
					// Make sure that the start and end vertices are
					// not already in T
					
					if (!(T.contains(startVertex) && T.contains(endVertex)))
					{
						// The vertex should not be on the outermost circuit
						// of G
						
						if (vInT == startVertex)
							vertexToAdd = endVertex;
						else
							vertexToAdd = startVertex;
						
						if (vertexToAdd.getWi() == 1)
						{
							// If H(Pi, Pj, Pk, P) < 0 then add
							// the vertex to T
							
							ArrayList pg = new ArrayList();
							
							getEdgesAndPolygons(vertexToAdd, null, pg);
							
							Coord Pi, Pj, Pk;
							
							Pi = ((Polygon)pg.get(0)).getGenerator();
							Pj = ((Polygon)pg.get(1)).getGenerator();
							Pk = ((Polygon)pg.get(2)).getGenerator();
							
							// Make sure that we don't try to use the virtual generator
							// that's in the outermost, infinite region
							
							if ((Pi != null) && (Pj != null) && (Pk != null))
							{
								if (H(Pi, Pj, Pk, p) < 0d)
								{
									T.add(vertexToAdd);
									expandT(T, p, (T.size() - 1));
								}
							}
						}
						else
							vertexToAdd = null;
					}
					else
					{
						// Delete old edges that were completely contained by T
						
						delEdges.add(e);
					}
				}
			}
		}
	}
	
	private void deleteEdges()
	{
		int index;
		Edge e;
		for (int i = 0; i < delEdges.size(); i++)
		{
			e = (Edge)delEdges.get(i);
			
			updatePolygonForEdgeDeletion(e, e.getLeftPolygon(), delEdges);
			updatePolygonForEdgeDeletion(e, e.getRightPolygon(), delEdges);
			
			e.deleted(true);
		}
		
		delEdges.clear();
		edges.trimToSize();
	}
	
	/** 
	* If the edge to be deleted is either of its
	* neighbouring polygons' edgeAroundPolygon attribute
	* then get the edges around that polygon and set
	* reset this attribute for another edge
	*/
	
	private void updatePolygonForEdgeDeletion(Edge delEdge, Polygon p, ArrayList delEdges)
	{
		ArrayList ed = new ArrayList();
		Edge e;
		int i;
		
		if (p.getEdgeAroundPolygon() == delEdge)
		{
			getEdgesAndVertices(p, ed, null);
			
			for (i = 0; i < ed.size(); i++)
			{
				e = (Edge)ed.get(i);
				if ((e != delEdge) && (!delEdges.contains(e)))
				{
					p.setEdgeAroundPolygon(e);
					break;
				}
			}
		}
	}
	
	/**
	* Given a coordinate, determine the closest generator to it
	*/
	
	private Polygon findClosestGenerator(Coord p, QuadTreeNode node)
	{
		// Use quaternary initial guessing to find a close candidate
		// generator for starting the search for the actual closest one
		
		// If 'node' is not a leaf node then use its parent as the
		// initial guess
		
		Coord pi = null;
		
		if (node.getNodeType() != QuadTreeNode.LEAF_NODE)
			pi = (Coord)node.getParentNode().getGenerator(0);
		else
			if (node.getGeneratorCount() > 1)
			{
				pi = node.getGenerator(0);
				
				if (pi.getPolygon() == null)
					pi = node.getParentNode().getGenerator(0);
			}
			else
				pi = node.getParentNode().getGenerator(0);
			
		return getClosestPolygon(pi, p, false);
	}
	
	private Polygon getClosestPolygon(Coord intitialGuess, Coord p,  boolean scalePoints)
	{
		try
		{
			Coord pk = null;
			int i;
			Coord pi, pj = null;
			
			Coord temp1;
			
			pi = intitialGuess;
			
			// Find the neighbouring generator pi that is closest
			// to p
			
			ArrayList e = new ArrayList();
			boolean bFinished = false;
			while (!bFinished)
			{
				// Get edges around pi
				
				e.clear();
				Polygon pgi = pi.getPolygon();
				
				getEdgesAndVertices(pgi, e, null);
				
				// From each of these edges get the polygon that is
				// not pgi. Find the polygon that gives the smallest
				// distance to p
				
				double minDist = Double.MAX_VALUE;
				double dist;
				Polygon otherP;
				Edge ed;
				Coord otherG;
				
				for (i = 0; i < e.size(); i++)
				{
					// Get the polygon
					
					ed = (Edge)e.get(i);
					if (ed.getLeftPolygon() == pgi)
						otherP = ed.getRightPolygon();
					else
						otherP = ed.getLeftPolygon();
					
					otherG = otherP.getGenerator();
					
					if (otherG != null)
					{
						pk = otherG;
						
						// Rescale the points so that they're in the coordinate
						// space of the view
						
						if (scalePoints)
						{
							temp1 = new Coord(pk);
							temp1.setX(calcPosX(temp1.getX()));
							temp1.setY(calcPosY(temp1.getY()));
						}
						else
							temp1 = pk;
						
						dist = getDistance(temp1, p);
						
						if (dist <= minDist)
						{
							minDist = dist;
							pj = otherG;
						}
					}
				}
				
				// Rescale the points so that they're in the coordinate
				// space of the view
				
				if (scalePoints)
				{
					temp1 = new Coord(pi);
					temp1.setX(calcPosX(temp1.getX()));
					temp1.setY(calcPosY(temp1.getY()));
				}
				else
					temp1 = pi;
				
				if (getDistance(temp1, p) <= minDist)
					bFinished = true;
				else
					pi = pj;
			}
				return  pi.getPolygon();
		}
		catch (Exception exc)
		{
			return null;
		}
	}
	
	/**
	* Given two coordinates, return the Euclidean distance
	* between them
	*/
	
	public double getDistance(Coord p1, Coord p2)
	{
		return Math.sqrt(((p1.getX() - p2.getX()) * (p1.getX() - 
			p2.getX())) + ((p1.getY() - p2.getY()) * (p1.getY() - p2.getY())));
	}
	
	public void paintComponent(Graphics g)
	{
		try
		{
			if (generators.size() > 0)
			{
				int w, h;
				w = (new Double(getSize().getWidth())).intValue();
				h = (new Double(getSize().getHeight())).intValue();
				
				if (offScreen == null || w != oldW || h != oldH)
				{
					oldW = w;
					oldH = h;
					offScreen = createImage(w, h);
					paintGraphics = offScreen.getGraphics();
				}
				
				paintGraphics.setColor(getBackground());
				paintGraphics.fillRect(0, 0, w, h);
				
				render(paintGraphics);
				if (offScreen != null)
					g.drawImage(offScreen, 0, 0, this);
			}
			else if (offScreen != null)
				g.drawImage(offScreen, 0, 0, this);
			else
				super.paintComponent(g);
		}
		catch (NullPointerException e)
		{
			super.paintComponent(g);
		}
	}
	
	/**
	* Test stub to see if the seed diagram is being correctly
	* calculated
	*/
	
	private void render(Graphics g)
	{
		if (generators.size() > 0)
		{
			// Get the origin.
			
			width  = this.getWidth();
			height = this.getHeight();
			origin.x = width / 2;
			origin.y = height / 2;
			
			// Plot filled polygons
			
			if (bClusters)
			{
				try
				{
					shadeRegions(g);
				}
				catch (Exception e){}
			}
			
			// See if we should render all edges
			
			if (bShowAllEdges == true)
				renderEdges(g);
			
			// If a cluster is selected..
			
			if (selectedCluster != null)
				paintCluster(selectedCluster, g);
			
			// Plot the generators
			
			if (bShowGenerators == true)
				plotGenerators(g, false);
		}
	}
	
	private void plotGenerators(Graphics g, boolean allGray)
	{
		int generatorWidth = (int)DATA_DOT_SIZE;
		int generatorHeight = (int)DATA_DOT_SIZE;
		Coord p;
		
		for (int j = 0; j < points.size(); j++)
		{
			if ((!selected.getState(j)) || allGray)
				g.setColor(Color.gray);
			else
				g.setColor(getDotColor(j));
			
			// If the mouse is over the polygon, highlight it
			
			if (points.get(j) instanceof Coord)
			{
				p = (Coord)points.get(j);
				g.fillOval((int)(calcPosX(p.getX()) - (generatorWidth / 2d)),
					(int)(calcPosY(p.getY()) - (generatorHeight / 2d)),
					generatorWidth, generatorHeight);
			}
		}
	}
	
	/**
	* Highlight each region with a different colour
	*/
	
	private void shadeRegions(Graphics g)
	{
		ArrayList po = new ArrayList(polygons);
		
		if (po.size() == 0) return;
		
		if (regions == null) return;
		
		ArrayList aReg;
		Polygon p;
		ArrayList v;
		Coord pnt;
		
		// First plot all points so that points that aren't part
		// of a cluster are still shown
		
		plotGenerators(g, true);
		
		for (int i = 0; i < regions.size(); i++)
		{
			aReg = (ArrayList)regions.get(i);
			if (aReg != null)
			{
				// If the user pressed the cluster button then plot points
				// and colour by cluster membership, otherwise shade polygons
				
				g.setColor(getStandardColor(i + 1));
				
				for (int j = 0; j < aReg.size(); j++)
				{
					v = new ArrayList();
					p = (Polygon)aReg.get(j);
					
					if (!bSliderCluster)
					{
						pnt = p.getGenerator();
						g.fillOval((int)(calcPosX(pnt.getX()) - (DATA_DOT_SIZE / 2d)),
							(int)(calcPosY(pnt.getY()) - (DATA_DOT_SIZE / 2d)),
							(int)DATA_DOT_SIZE, (int)DATA_DOT_SIZE);
					}
					else
					{
						getEdgesAndVertices(p, null, v);
						
						int[] x = new int[v.size()];
						int[] y = new int[v.size()];
						
						Vertex ve;
						
						for (int k = 0; k < v.size(); k++)
						{
							ve = (Vertex)v.get(k);
							if (ve.getWi() == 1)
							{
								x[k] = (int)(calcPosX(ve.getX()));
								y[k] = (int)(calcPosY(ve.getY()));
							}
						}
						g.fillPolygon(x, y, x.length);
					}
				}
			}
		}
	}
	
	/**
	* When provided with the index of a cluster, provide a color for
	* shading the polygons of the cluster based upon its density
	*/
	
	private Color getCol(int index)
	{
		double currentDensity = vStat.getDensity((ArrayList)regions.get(index - 1));
		int v = (int)((255 / maxDensity) * currentDensity);
		float rate = 0.75f;
		return new Color((int)(v * rate), (int)(v * rate), (int)(v * rate));
	}
	
	/**
	* When provided with the index of a cluster, provide a standard
	* color for shading
	*/
	
	private Color getStandardColor(int index)
	{
		// Use standard colour scheme
		
		if (index > 15)
			index = index % 15;
		
		Color col = null;
		
		switch (index)
		{
			case 1:
				col = Color.white;
				break;
			case 2:
				col = Color.cyan;
				break;
			case 3:
				col = Color.magenta;
				break;
			case 4:
				col = Color.blue;
				break;
			case 5:
				col = Color.green;
				break;
			case 6:
				col = Color.yellow;
				break;
			case 7:
				col = Color.orange;
				break;
			case 8:
				col = Color.red;
				break;
			case 9:
				col = Color.pink;
				break;
			case 10:
				col = (Color.cyan).darker();
				break;
			case 11:
				col = (Color.magenta).darker();
				break;
			case 12:
				col = (Color.blue).darker();
				break;
			case 13:
				col = (Color.green).darker();
				break;
			case 14:
				col = (Color.yellow).darker();
				break;
			case 15:
				col = (Color.orange).darker();
				break;
			default:
				return null;
		}
		
		float rate = 0.75f;
		
		return new Color((int)(col.getRed() * rate), (int)(col.getGreen() * rate), (int)(col.getBlue() * rate));
	}
	
	/**
	* Render all edges in the Voronoi diagram
	*/
	
	private void renderEdges(Graphics g)
	{
		g.setColor(Color.blue);
		Vertex startV, endV;
		int x1, x2, y1, y2;
		
		for (int i = 0; i < edges.size(); i++)
		{
			if (edges.get(i) != null)
				if (!((Edge)edges.get(i)).deleted())
				{
					startV = ((Edge)edges.get(i)).getStartVertex();
					endV = ((Edge)edges.get(i)).getEndVertex();
					
					if ((startV.getWi() == 1) && (endV.getWi() == 1))
					{
						x1 = (int)calcPosX(startV.getX());
						y1 = (int)calcPosY(startV.getY());
						x2 = (int)calcPosX(endV.getX());
						y2 = (int)calcPosY(endV.getY());
						g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
					}
				}
		}
	}
	
	/**
	* Given a point, if it is contained within a cluster, highlight the cluster
	*/
	
	public void highlightCluster(Coord p)
	{
		selectedCluster = null;
		
		if (qTree != null)
		{
			if (points.get(qTree.getClosestPoint(p, qTree.getRootNode())) instanceof Coord)
			{
				// Get the closest generator to the given point
				
				Coord closestPoint = null;
				
				// First use the quad tree to get an approximate nearest point
				
				Coord intitialGuess = (Coord)points.get(qTree.getClosestPoint(p, qTree.getRootNode()));
				
				// Now use getClosestPolygon to find the actual closest point using the
				// intial approximation
				
				if (intitialGuess.getPolygon() != null)
					closestPoint = getClosestPolygon(intitialGuess, p, true).getGenerator();
				
				// Check to see if the closest generator is in a region
				
				if (closestPoint != null)
				{
					ArrayList cluster = closestPoint.getPolygon().getRegion();
					
					if (cluster != null)
					{
						selectedCluster = cluster;
						
						// Send the selected items to the selection port
						
						handleClusterSelection(cluster);
						
						repaint();
						return;
					}
				}
			}
		}
		
		repaint();
	}
	
	private void paintCluster(ArrayList cluster, Graphics g)
	{
		Color col = getStandardColor(regions.indexOf(cluster) + 1);
		Color newColor;
		
		float rate = 0.75f;
		
		if (col != null)
		{
			newColor = new Color((int)(col.getRed() / rate), (int)(col.getGreen() / rate), (int)(col.getBlue() / rate));
			g.setColor(newColor);
		}
		
		Polygon p;
		ArrayList v;
		
		for (int j = 0; j < cluster.size(); j++)
		{
			v = new ArrayList();
			p = (Polygon)cluster.get(j);
			getEdgesAndVertices(p, null, v);
			
			int[] x = new int[v.size()];
			int[] y = new int[v.size()];
			
			Vertex ve;
			
			for (int k = 0; k < v.size(); k++)
			{
				ve = (Vertex)v.get(k);
				if (ve.getWi() == 1)
				{
					x[k] = (int)(calcPosX(ve.getX()));
					y[k] = (int)(calcPosY(ve.getY()));
				}
			}
			
			g.drawPolygon(x, y, x.length);
		}
	}
	
	/**
	* calculates the x position that this object should be at within
	* the current JPanel and layoutbounds of the layoutModel
	*
	*/
	
	public double calcPosX(double x)
	{
		return origin.x + ((x + offsetX) * scaleX * (width / 2));
	}
	
	/**
	* calculates the y position that this object should be at within
	* the current JPanel and layoutbounds of the layoutModel
	*
	*/
	
	public double calcPosY(double y)
	{
		return origin.y + ((y + offsetY) * scaleY * (height/2)); // GR: Reduce height
	}
	
	/**
	* Remove all generators, polygons and vertices from the diagram
	*/
	
	public void clear()
	{
		offScreen = null;
		generators.clear();
		vertices.clear();
		polygons.clear();
		edges.clear();
		delEdges.clear();
		qTree = null;
		regions = null;
		selectedCluster = null;
		vStat = null;
		bSliderCluster = true;
	}
	
	/**
	* Return the parent of this class, i.e. the Voronoi VisualModule
	*
	*/
	
	public Voronoi getParentModule()
	{
		return visMod;
	}
	
	/**
	* resets the scale and offset values to their original values.  This 
	* should be called whenever a new layout is being started or someone 
	* has scrolled or zoomed away from all the data
	*/
	
	public void reset()
	{
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
		this.scaleX = scaleX;
	}
	
	/**
	* Sets the y scale to be the new value scale
	*
	* @param scaleY The new y scale value
	*/
	
	public void setScaleY( double scaleY )
	{
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
		this.offsetX = offsetX;
		bPanning = true;
	}
	
	/**
	* sets the y offset value
	*
	* @return The y offset
	*/
	
	public void setOffsetY( double offsetY )
	{
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
	
	/**
	* Method that should be called whenever an screen update is required, 
	*/
	
	public void update()
	{
		repaint();
	}
	
	/**
	* Method to accept a set of positions to plot points at
	*/
	
	public void setPositions(ArrayList positions)
	{
		points = positions;
	}
	
	/**
	* Accessor method for deciding whether to render all edges
	* not just the ones for shaded polygons
	*/
	public void setShowAllEdges(boolean bShow)
	{
		bShowAllEdges = bShow;
	}
	
	/**
	* Accessor method for deciding whether to render generators
	*/
	public void setShowGenerators(boolean bShow)
	{
		bShowGenerators = bShow;
	}
	
	/**
	* Determine whether we should partition the set of polyons according
	* to perimeter/area, i.e. cluster
	*/
	
	public void setCluster(boolean bClusters)
	{
		selectedCluster = null;
		
		if (bClusters)
		{
			// If the user wishes to seperate clusters manually...
			
			ArrayList po = new ArrayList(polygons);
			
			try
			{
				po.remove(0);
				po.remove(0);
				po.remove(0);
				po.remove(0);
				regions = vStat.getRegions(po, 10, thresholdAttribute);
			}
			catch (NullPointerException ex){}
		}
		
		this.bClusters = bClusters;
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
		if ((dataItems != null) && (selected != null))
		{
			theSelection = new HashSet(getRectContents(rect));
			selected.updateSelection();
			visMod.sendSelection();
		}
	}
	
	private void handleClusterSelection(ArrayList cluster)
	{
		ArrayList selectedItems = new ArrayList();
		ArrayList indices = new ArrayList();
		Coord c;
		Polygon p;
		
		for (int i = 0; i < cluster.size(); i++)
		{
			p = (Polygon)cluster.get(i);
			c = p.getGenerator();
			selectedItems.add(new Integer(dataItems.getDataItem(c.getIndex()).getID()));
			indices.add(new Integer(c.getIndex()));
		}
		
		theSelection = new HashSet(selectedItems);
		selected.updateSelection();
		visMod.sendCluster(indices);
		visMod.sendSelection();
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
			theSelection.addAll(getRectContents(rect));
			selected.updateSelection();
			visMod.sendSelection();
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
			theSelection.removeAll(getRectContents(rect));
			selected.updateSelection();
			visMod.sendSelection();
		}
	}
	
	/**
	* Returns the indices of the data items that are contained inside the 
	* on screen rectangle rect.
	*
	* @param rect The rectangle
	* @return The collection of indices inside this rectangle
	*/
	
	protected Collection getRectContents(Rectangle rect)
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
		int numItems = points.size();
		
		// Find the on screen positions that are within this rect
		
		for (int i = 0; i < numItems ; i++ )
		{
			int k = i;
			
			Coordinate c = ((Coordinate)points.get(k));
			
			if (rect.contains( calcPosX( c.getX()),
			calcPosY(c.getY())))
			{
				contents.add(new Integer(dataItems.getDataItem(k).getID()));
			}
		}
		
		return contents;
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
	
	public void setSelection(Collection selection)
	{
		theSelection = 	(HashSet)selection;
		selected.updateSelection();
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
			theSelection.add(new Integer(i));
	}
	
	/**
	* Sets this selectable item to select none of it items
	*
	*/
	
	public void selectNone()
	{
		theSelection.clear();
	}
	
	/**
	* Accessor methods for the current max and min Voronoi
	* polygon perimeters
	*/
	
	public double getMinPerimeter()
	{
		return vStat.getMinPerimeter();
	}
	
	public double getMaxPerimeter()
	{
		return vStat.getMaxPerimeter();
	}
	
	public double getAveragePerimeter()
	{
		return vStat.getAveragePerimeter();
	}
	
	public double getStdDevPerimeter()
	{
		return vStat.getStdDevPerimeter();
	}
	
	/**
	* Accessor methods for the current max and min Voronoi
	* polygon areas
	*/
	
	public double getMinArea()
	{
		return vStat.getMinArea();
	}
	
	public double getMaxArea()
	{
		return vStat.getMaxArea();
	}
	
	public double getAverageArea()
	{
		return vStat.getAverageArea();
	}
	
	public double getStdDevArea()
	{
		return vStat.getStdDevArea();
	}
	
	public void setThresholdAttribute(double thresholdAttribute)
	{
		// If the user is changing the threshold for seperating clusters..
		
		selectedCluster = null;
		
		if (bClusters)
		{
			ArrayList po = new ArrayList(polygons);
			po.remove(0);
			po.remove(0);
			po.remove(0);
			po.remove(0);
			regions = vStat.getRegions(po, 10, thresholdAttribute);
		}
		
		this.thresholdAttribute = thresholdAttribute;
		bSliderCluster = true;
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
		// otherwise if the set is tf-idf (really high D) just return a default
		
		// We don't have any variables for a triangular data matrix, thus return
		// one colour.
		
		if (dataItems.getLowerTriangular())
			return Color.yellow;
		
		if (((DataItem)dataItems.getDataItem(0)).getTextValues() == null)
		{
			double max = convToDouble(dataItems.getMaximum(colorField));
			double min = convToDouble(dataItems.getMinimum(colorField));
			double val = convToDouble(dataItems.getDataItem(index).getValues()[colorField]);
			int i = (int)(((val-min)/(max-min))*(colorScheme.length-40)) + 20;
			return colorScheme[i];
		}
		else
			return Color.yellow;
	}
	
	/**
	* Sets the color scheme for the scatter panel to use
	*
	*/
	
	public void setColorScheme(String color)
	{	
		this.colorScheme = ColorScales.getScale(color);
		
		update();
	}
	
	public void setColorField(String field)
	{
		this.colorField = dataItems.getFields().indexOf(field);
		update();
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
	* Accessor methods for determining whether polygon perimeters or areas
	* are to be used in clustering
	*/
	
	public void setPerimeterCalc(boolean b)
	{
		bPerimeterCluster = b;
		bSliderCluster = b;
		
		if (!bPolygonCalcPerimeter)
		{
			vStat.calculatePerimeters();
			bPolygonCalcPerimeter = true;
		}
	}
	
	public boolean getPerimeterCalc()
	{
		return bPerimeterCluster;
	}
	
	public void setAreaCalc(boolean b)
	{
		bAreaCluster = b;
		bSliderCluster = b;
		
		if (!bPolygonCalcArea)
		{
			vStat.calculateAreas();
			bPolygonCalcArea = true;
		}
	}
	
	public boolean getAreaCalc()
	{
		return bAreaCluster;
	}
	
	/**
	* Accessor method allowing Voronoi to to tell vStat to run the
	* clustering/segmentation algorithm
	*/
	
	public void cluster()
	{
		selectedCluster = null;
		
		//regions = vStat.clusterBySize();
		
		regions = vStat.hybridCluster();
		bSliderCluster = false;
		
		// Set the highest density so that we can shade polygons according to this
		
		//maxDensity = vStat.gethighestDensity(regions);
		repaint();
		
		// Send the clusters to the output of the Voronoi visual module
		
		clusterColours = new ArrayList();
		
		for (int i = 0; i < regions.size(); i++)
			clusterColours.add(getStandardColor(i + 1));
		
		visMod.sendAllClusters(regions, clusterColours);
	}
	
	/**
	* This method is called before serialisation occurs
	*/
	
	public void beforeSerialise()
	{
		// thes objects cannt be serialised and therefore must be set to null
		// beforehand
		
		offScreen = null;
		paintGraphics = null;
	}
	
	/**
	* Add a popup menu to allow the user to export selected points to
	* MS Excel for plotting
	*/
	
	private void addPopupMenu()
	{
		popup = new JPopupMenu();
		exportSelectionToExcel = new JMenuItem("Export selection to Excel");
		exportSelectionToExcel.addActionListener(this);
		exportSelectionToExcel.setActionCommand("exportToExcelForPlot");
		popup.add(exportSelectionToExcel);
		
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
				
				ArrayList selectionData = new ArrayList(theSelection);
				
				excelExportDialog = ExcelExport.getInstance("Export selection to Excel", selectionData, dataItems);
			}
		}
	}
	
	/**
	* Implementation of the MouseListener interface
	*/
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			if ((theSelection.size() > 0) && !bPanning)
			{
				exportSelectionToExcel.setVisible(true);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		bPanning = false;
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
}

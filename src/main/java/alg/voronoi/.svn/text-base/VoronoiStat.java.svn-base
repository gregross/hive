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
 * VoronoiStat: responsible for calculating statistics such as average
 * Voronoi polygon perimeter and area
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import data.*;
import math.*;

import java.util.*;

public class VoronoiStat implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private VoronoiPane voronoiPane;
	private ArrayList polygons;
	
	// The Voronoi polygon perimeter attributes
	
	private double minPerimeter;
	private double maxPerimeter;
	private double averagePerimeter;
	private double stdDevPerimeter;
	
	// The Voronoi polygon area attributes
	
	private double minArea;
	private double maxArea;
	private double averageArea;
	private double stdDevArea;
	
	// When carrying out density based partitioning, the following
	// variable determines the minimum cluster size
	
	private int minClusterSize = 10;
	
	// Array of cluster tree nodes
	
	private ArrayList clusterTree;
	
	// A HashMap to determine the cluster leaf node that a particular generator
	// belongs to
	
	private HashMap generatorLeaf;
	
	public VoronoiStat(ArrayList polygons, VoronoiPane voronoiPane)
	{
		this.voronoiPane = voronoiPane;
		this.polygons = polygons;
	}
	
	/**
	* Calculate the each Voronoi's total edge length to
	* approximate its perimeter. Store this value with
	* the polygon object
	*/
	
	public void calculatePerimeters()
	{
		minPerimeter = Double.MAX_VALUE;
		maxPerimeter = Double.MIN_VALUE;
		averagePerimeter = 0d;
		double totalLength;
		double runningTotal = 0d;
		double sumOfSquares = 0d;
		Polygon p;
		ArrayList e;
		Edge ed;
		
		// For each polygon..
		
		for (int i = 4; i < polygons.size(); i++)
		{
			totalLength = 0d;
			p = (Polygon)polygons.get(i);
			e = new ArrayList();
			
			voronoiPane.getEdgesAndVertices(p, e, null);
			
			// For each edge around the polygon..
			
			for (int j = 0; j < e.size(); j++)
			{
				ed = (Edge)e.get(j);
				
				if ((ed.getStartVertex().getWi() != 0) && (ed.getEndVertex().getWi() != 0))
					totalLength += voronoiPane.getDistance(ed.getStartVertex(), ed.getEndVertex());
			}
			
			// Derive the miniumum and maximum perimeters
			
			if (totalLength > maxPerimeter)
				maxPerimeter = totalLength;
			else if (totalLength < minPerimeter)
				minPerimeter = totalLength;
			
			sumOfSquares += (totalLength * totalLength);
			
			// Store the perimeter with the polygon
			
			p.setPerimeter(totalLength);
			
			// Keep track of the runnin total of perimeters
			// so that we can calculate the average
			
			runningTotal += totalLength;
		}
		
		// Calculate the average perimeter
		
		if (runningTotal > 0)
			averagePerimeter = runningTotal / ((double)(polygons.size() - 4));
		
		// Calculate the standard deviation of the perimeters
		
		stdDevPerimeter = Math.sqrt((sumOfSquares - ((double)(polygons.size() - 4)* averagePerimeter
			* averagePerimeter))/(double)(polygons.size() - 4));
	}
	
	/**
	* Calculate the area statistics of Voronoi polygons
	*/
	
	public void calculateAreas()
	{
		minArea = Double.MAX_VALUE;
		maxArea = Double.MIN_VALUE;
		averageArea = 0d;
		Polygon p;
		double sum = 0;
		double sumOfSquares = 0d;
		double area;
		
		// For each polygon..
		
		for (int i = 4; i < polygons.size(); i++)
		{
			p = (Polygon)polygons.get(i);
			area = getArea(p);
			p.setArea(area);
			sum += area;
			
			if (area < minArea)
				minArea = area;
			
			if (area > maxArea)
				maxArea = area;
			
			sumOfSquares += (area * area);
		}
		
		averageArea = sum / ((double)(polygons.size() - 4));
		
		// Calculate the standard deviation of the areas
		
		stdDevArea = Math.sqrt((sumOfSquares - ((double)(polygons.size() - 4)* averageArea
			* averageArea))/(double)(polygons.size() - 4));
	}
	
	/**
	* Given a polygon calculate its area
	*/
	
	private double getArea(Polygon p)
	{
		ArrayList v = new ArrayList();
		voronoiPane.getEdgesAndVertices(p, null, v);
		
		double xi = 0, xi_1 = 0, yi = 0, yi_1 = 0;
		double sum = 0;
		Vertex ve;
		
		for (int i = 0; i < v.size(); i++)
		{
			ve = ((Vertex)v.get(i));
			xi = ve.getX();
			yi = ve.getY();
			
			if (i == (v.size() - 1))
			{
				ve = ((Vertex)v.get(0));
				xi_1 = ve.getX();
				yi_1 = ve.getY();
			}
			else
			{
				ve = ((Vertex)v.get(i + 1));
				xi_1 = ve.getX();
				yi_1 = ve.getY();
			}
			
			sum += ((xi * yi_1) - (xi_1 * yi));
		}
		
		return (Math.abs(sum) / 2d);
	}
	
	/**
	* Accessor methods for the current max and min Voronoi
	* polygon perimeters
	*/
	
	public double getMinPerimeter()
	{
		return minPerimeter;
	}
	
	public double getMaxPerimeter()
	{
		return maxPerimeter;
	}
	
	public double getAveragePerimeter()
	{
		return averagePerimeter;
	}
	
	public double getStdDevPerimeter()
	{
		return stdDevPerimeter;
	}
	
	/**
	* Accessor methods for the current max and min Voronoi
	* polygon areas
	*/
	
	public double getMinArea()
	{
		return minArea;
	}
	
	public double getMaxArea()
	{
		return maxArea;
	}
	
	public double getAverageArea()
	{
		return averageArea;
	}
	
	public double getStdDevArea()
	{
		return stdDevArea;
	}
	
	/**
	* Set all polygon regions to null. This region property is used
	* to determine whether a polygon is part of a cluster and if so
	* which cluster and the identity of its other members
	*/
	
	private void initPolygonRegions(ArrayList pSet)
	{
		Polygon p;
		
		for (int i = 0; i < pSet.size(); i++)
		{
			p = (Polygon)pSet.get(i);
			
			if (p != null)
				p.setRegion(null);
		}
	}
	
	/**
	* Given that a threshold t for area or perimeter is set so that Voronoi polygons below
	* this threshold are selected, find contiguous regions consisting of b or more
	* highlighted polygons within the set pSubset
	*/
	
	public ArrayList getRegions(ArrayList pSubset, int b, double t)
	{
		Polygon p;
		int i;
		double att;
		ArrayList regions = new ArrayList();
		ArrayList newRegion;
		
		// Initialise the polygons so that they are flagged as not being
		// part of any region of contiguous cells
		
		initPolygonRegions(pSubset);
		
		ArrayList stillToCheck = new ArrayList();
		
		for (i = 0; i < pSubset.size(); i++)
		{
			p = (Polygon)pSubset.get(i);
			
			if (p != null)
			{
				if (voronoiPane.getPerimeterCalc())
					att = p.getPerimeter();
				else
					att = p.getArea();
				
				if ((att < t) && (att > 0) && (p.getRegion() == null))
				{
					newRegion = new ArrayList();
					newRegion.add(p);
					regions.add(newRegion);
					p.setRegion(newRegion);
					
					formRegions(p, t, stillToCheck, p.getRegion());
					
					ArrayList newr = new ArrayList();
					
					while (stillToCheck.size() > 0)
					{
						for (int j = 0; j < stillToCheck.size(); j++)
						{
							formRegions((Polygon)stillToCheck.get(j), t, newr, ((Polygon)stillToCheck.get(j)).getRegion());
						}
						stillToCheck = new ArrayList(newr);
						newr = new ArrayList();
					}
				}
			}
		}
		
		// Make sure that we only return contiguous regions that consist of b or more
		// polygons (generators)
		
		return filterForMinClusterSize(regions, b);
	}
	
	/**
	* A recursive method that propagates from cell to cell creating a cluster
	* depending upon the threshold value for Voronoi polygon perimeter or area
	*/
	
	private void formRegions(Polygon p, double t, ArrayList stillToCheck, ArrayList region)
	{
		ArrayList adjP = null;
		Polygon adjPolygon;
		double att;
		
		try
		{
			adjP = getAdjacentPolygons(p);
		}
		catch(Exception e){}
		
		if (adjP == null) return;
		
		for (int i = 0; i < adjP.size(); i++)
		{
			adjPolygon = (Polygon)adjP.get(i);
			
			if (voronoiPane.getPerimeterCalc())
				att = adjPolygon.getPerimeter();
			else
				att = adjPolygon.getArea();
			
			if ((att < t) && (att > 0) && (adjPolygon.getRegion() == null))
			{
				region.add(adjPolygon);
				adjPolygon.setRegion(region);
				stillToCheck.add(adjPolygon);
			}
		}
	}
	
	/**
	* Get the polygons that are adjacent to the given polygon
	*/
	
	public ArrayList getAdjacentPolygons(Polygon p)
	{
		ArrayList edges = new ArrayList();
		voronoiPane.getEdgesAndVertices(p, edges, null);
		Edge e;
		ArrayList result = new ArrayList();
		
		if (edges.size() > 0)
		{
			for (int j = 0; j < edges.size(); j++)
			{
				e = (Edge)edges.get(j);
				
				if (e.getLeftPolygon() == p)
					result.add(e.getRightPolygon());
				else
					result.add(e.getLeftPolygon());
			}
		}
		
		return result;
	}
	
	/**
	* Use a descending Voronoi polygon area or perimter threshold to determine region
	* density and segment the plane according to likely clusters
	*/
	
	public ArrayList clusterByThreshold()
	{
		// Set the max and min threshold values
		
		double tMax;
		double tMin;
		double a;
		
		ArrayList clusters = new ArrayList();
		
		if (voronoiPane.getPerimeterCalc())
		{
			tMax = voronoiPane.getStdDevPerimeter();
			tMin = voronoiPane.getMinPerimeter();
			a = 0.01 * (tMax - tMin); // This means that the number of increment steps is constant
		}
		else
		{
			tMax = voronoiPane.getStdDevArea();
			tMin = voronoiPane.getMinArea();
			a = 0.0001;
		}
		
		ArrayList po = new ArrayList(polygons);
		po.remove(0);
		po.remove(0);
		po.remove(0);
		po.remove(0);
		
		// Init the cluster tree and root node
		
		clusterTree = new ArrayList();
		ClusterNode rootNode = new ClusterNode(null, 0, ClusterNode.ROOT_NODE);
		clusterTree.add(rootNode);
		
		generatorLeaf = new HashMap(po.size());
		
		segmentByThreshold(po, tMin, tMax, clusters, a, rootNode);
		
		return clusters;
	}
	
	/**
	* Since this procedure is recursive over the whole data set, the overall
	* time complexity is O(nLogn)
	*/
	
	private void segmentByThreshold(ArrayList region, double tMin, double tStart, 
		ArrayList clusters, double a, ClusterNode parentNode)
	{
		double threshold = tStart;
		ArrayList regions = new ArrayList();
		ArrayList r;
		boolean newRegionsFormed = false;
		Polygon p;
		
		ClusterNode newNode;
		
		while ((threshold > tMin) && (regions.size() < 2)) // This 'while' ensures that all n polygons are considered
		{
			threshold -= a;
			regions = getRegions(region, minClusterSize, threshold);
			
			if (regions.size() > 1)
			{
				for (int i = 0; i < regions.size(); i++)
				{
					newNode = new ClusterNode(parentNode, 
						parentNode.getLevel() + 1, ClusterNode.INTERMEDIATE_NODE);
						
					parentNode.addChild(newNode);
					
					r = (ArrayList)regions.get(i);
					
					// Only try and split the cluster if the
					// average number of members for two sub-clusters is large enough
					
					if (r.size() >= (2 * minClusterSize))
					{
						associateGeneratorToCluster(r, newNode);
						segmentByThreshold(r, tMin, threshold, clusters, a, newNode); // Recursion makes O(n log n)
						newRegionsFormed = true;
					}
					else
					{
						// Make the cluster a leaf node
						
						newNode.setNodeType(ClusterNode.LEAF_NODE);
						newNode.setCluster(r);
						clusterTree.add(newNode);
						associateGeneratorToCluster(r, newNode);
						
						clusters.add(r);
						
						for (int j = 0; j < r.size(); j++)
						{
							p = (Polygon)r.get(j);
							p.setRegion(r);
						}
						newRegionsFormed = true;
					}
				}
			}
		}
		
		if (!newRegionsFormed)
		{
			// Make the cluster a leaf node
			
			if (parentNode.getNodeType() == ClusterNode.ROOT_NODE)
			{
				parentNode.setNodeType(ClusterNode.LEAF_NODE);
				parentNode.setCluster(region);
			}
			else
			{
				parentNode.setNodeType(ClusterNode.LEAF_NODE);
				parentNode.setCluster(region);
				clusterTree.add(parentNode);
				associateGeneratorToCluster(region, parentNode);
			}
			
			clusters.add(region);
			
			for (int j = 0; j < region.size(); j++)
			{
				p = (Polygon)region.get(j);
				p.setRegion(region);
			}
		}
	}
	
	/**
	* Given a set of Voronoi polygons (a region),
	* calculate the density
	*/
	
	public double getDensity(ArrayList region)
	{
		Polygon p;
		double totalArea = 0;
		
		for (int i = 0; i < region.size(); i++)
		{
			p = (Polygon)region.get(i);
			
			if (voronoiPane.getPerimeterCalc())
				totalArea += p.getPerimeter();
			else
				totalArea += p.getArea();
		}
		
		return (double)region.size() / totalArea;
	}
	
	/**
	* Out of a set of clusters, return the density of the one with the highest density
	*/
	
	public double gethighestDensity(ArrayList c)
	{
		double maxDensity = Double.MIN_VALUE;
		double d;
		
		for (int i = 0; i < c.size(); i++)
		{
			d = getDensity((ArrayList)c.get(i));
			
			if (maxDensity < d)
				maxDensity = d;
		}
		
		return maxDensity;
	}
	
	/**
	* Segment the Voronoi diagram by selecting the polygon with the
	* smallest area or perimeter and then expanding it into a cluster
	* by adding adjacent similar polygons. Once the cluster can't be
	* expanded any more, remove the cluster and start again with the
	* next smallest polygon
	*/
	
	public ArrayList clusterBySize()
	{
		// Init the array list of clusters that this method will (hopefully) return
		
		ArrayList clusters = new ArrayList();
		
		// Define the array list of all polygons except the outermost infinite
		// and the ones that border this
		
		ArrayList po = new ArrayList(polygons);
		po.remove(0);
		po.remove(0);
		po.remove(0);
		po.remove(0);
		
		// Initialise the polygons so that they are flagged as not being
		// part of any region of contiguous cells
		
		Polygon p;
		int i;
		
		initPolygonRegions(po);
		
		// Sort the list of polygons in ascending order
		
		ClusterComparator comp = new ClusterComparator(voronoiPane.getPerimeterCalc());
		Collections.sort(po, comp);
		
		// Starting with the polygons with the smallest area or perimeter, segment
		
		double att;
		
		for (i = 0; i < po.size(); i++)
		{
			p = (Polygon)po.get(i);
			
			if (p != null)
			{
				// Only start a new cluster if the polygon is not already part of an
				// existing cluster
				
				if (p.getRegion() == null)
				{
					if (voronoiPane.getPerimeterCalc())
						att = p.getPerimeter();
					else
						att = p.getArea();
					
					if (att > 0)
						agglomerativeCluster(p, att, clusters);
				}
			}
		}
		
		// Make sure that we only return contiguous regions that consist of minClusterSize or more
		// polygons (generators)
		
		return filterForMinClusterSize(clusters, minClusterSize);
	}
	
	/**
	* Given an ArrayList of ArrayLists - one for each cluster
	* return a set in which the size of each cluster is greater than
	* or equal to the gven min size
	*/
	
	private ArrayList filterForMinClusterSize(ArrayList c, int minSize)
	{
		ArrayList result = new ArrayList();
		Polygon delP;
		ArrayList cluster;
		
		for (int i = 0; i < c.size(); i++)
		{
			cluster = (ArrayList)c.get(i);
			
			if (cluster.size() >= minSize)
			{
				result.add(c.get(i));
			}
			else
			{
				for (int j = 0; j < cluster.size(); j++)
				{
					delP = (Polygon)cluster.get(j);
					delP.setRegion(null);
				}
			}
		}
		return result;
	}
	
	/**
	* Calls the agglomerative clustering routine for using an area/perimeter
	* difference to decide whether to merge polygons into onr region
	*/
	
	private void agglomerativeCluster(Polygon p, double att, ArrayList clusters)
	{
		ArrayList newCluster = new ArrayList();
		newCluster.add(p);
		clusters.add(newCluster);
		p.setRegion(newCluster);
		formRegions(p, att, newCluster);
	}
	
	/**
	* A recursive method that propagates from cell to cell creating a cluster
	* depending upon the size of the area or perimeter. NO global threshold is used
	* as in formRegions above
	*/
	
	private void formRegions(Polygon p, double polygonAttribute, ArrayList region)
	{
		ArrayList adjP = null;
		Polygon adjPolygon;
		double att;
		double stdDev;
		
		try
		{
			adjP = getAdjacentPolygons(p);
		}
		catch(Exception e){}
		
		if (adjP == null) return;
		
		for (int i = 0; i < adjP.size(); i++)
		{
			adjPolygon = (Polygon)adjP.get(i);
			
			// Get the Voronoi polygon area or perimeter
			
			if (voronoiPane.getPerimeterCalc())
			{
				att = adjPolygon.getPerimeter();
				stdDev = voronoiPane.getStdDevPerimeter();
			}
			else
			{
				att = adjPolygon.getArea();
				stdDev = voronoiPane.getStdDevArea();
			}
			
			// Get the difference in area/perimeter between the start polygon's and the
			// current polygon's
			
			double diff = Math.abs(polygonAttribute - att);
			
			if ((adjPolygon.getRegion() == null) && (diff <= (0.1d)) &&
				(att > 0))
			{
				region.add(adjPolygon);
				adjPolygon.setRegion(region);
				formRegions(adjPolygon, polygonAttribute, region);
			}
		}
		
	}
	
	/**
	* First use the reducing threshold divisive clustering and then
	* apply the agglomerative clustering
	*/
	
	public ArrayList hybridCluster()
	{
		ArrayList clusters = new ArrayList();
		
		// Get clusters from the divisive approach
		
		ArrayList dClusters = clusterByThreshold();
		
		// Initialise all polygons so that they declare no particular region membership
		
		initPolygonRegions(polygons);
		
		// For each cluster, get the average inter-object distance, and..
		// for each cluster get the smallest polygon. This will be the seed for
		// agglomerative clustering
		
		double[] avgDists = new double[dClusters.size()];
		Polygon[] smallestP = new Polygon[dClusters.size()];
		int i;
		
		for (i = 0; i < dClusters.size(); i++) // This is of O(N)
		{
			// Get average inter-object distance of cluster
			
			avgDists[i] = avgInterDistance((ArrayList)dClusters.get(i));
			
			// Get the smallest polygon in the cluster
			
			smallestP[i] = getSmallestPolygon((ArrayList)dClusters.get(i));
		}
		
		// Sort the above arrays in increasing average inter-object distance
		
		FastQSortAlgorithm fastSort = new FastQSortAlgorithm();
		fastSort.sort(avgDists, smallestP);
		
		// Apply agglomerative clustering starting at each cluster identified
		// by the divisive clustering above
		
		for (i = 0; i < avgDists.length; i++)
			agglomerativeCluster(smallestP[i], clusters, avgDists[i] * 2d);
		
		return clusters;
	}
	
	/**
	* Calls the agglomerative clustering routine 2. However, in this instance
	* cluster object inter-distance values are used to determine when a polygon
	* should beecom part of a region
	*/
	
	private void agglomerativeCluster(Polygon p, ArrayList clusters, double avgDist)
	{
		if (p != null)
		{
			if (p.getRegion() == null)
			{
				ArrayList newCluster = new ArrayList();
				newCluster.add(p);
				clusters.add(newCluster);
				p.setRegion(newCluster);
				formRegions(p, newCluster, avgDist);
			}
		}
	}
	
	/**
	* A recursive method that propagates from cell to cell creating a cluster
	* depending upon the DISTANCE (see two other formRegions instances above). NO global threshold is used
	* as in formRegions above
	*
	* Since we're checking adjPolygon.getRegion() == null, we cannot process more than n polygons
	* and therefore this procedure is of O(n)
	*/
	
	private void formRegions(Polygon p, ArrayList region, double avgDist)
	{
		ArrayList adjP = null;
		Polygon adjPolygon;
		double stdDev;
		
		try
		{
			adjP = getAdjacentPolygons(p);
		}
		catch(Exception e){}
		
		if (adjP == null) return;
		
		double dist;
		Coord g1, g2;
		
		g1 = p.getGenerator();
		
		for (int i = 0; i < adjP.size(); i++)
		{
			adjPolygon = (Polygon)adjP.get(i);
			
			if ((adjPolygon.getArea() > 0) || (adjPolygon.getPerimeter() > 0))
			{
				// Get the distance between the input polygon's generator
				// and the current adjacent polygon
				
				g2 = adjPolygon.getGenerator();
				
				if ((g1 != null) && (g2 != null))
				{
					dist = voronoiPane.getDistance(g1, g2);
					
					double prob = getProbability(g1, g2);
										
					if ((adjPolygon.getRegion() == null) && (dist <= (avgDist * prob)))
					{
						region.add(adjPolygon);
						adjPolygon.setRegion(region);
						formRegions(adjPolygon, region, avgDist);
					}
				}
			}
		}
		
	}
	
	/**
	* Return the average inter-object distance for a given set of Voronoi polygons
	*/
	
	private double avgInterDistance(ArrayList pSet)
	{
		double totalDist = 0;
		Polygon p1, p2;
		Coord g1, g2;
		
		for (int i = 0; i < (pSet.size() - 1); i++)
		{
			for (int j = (i + 1); j < pSet.size(); j++)
			{
				p1 = (Polygon)pSet.get(i);
				p2 = (Polygon)pSet.get(j);
				g1 = p1.getGenerator();
				g2 = p2.getGenerator();
				
				totalDist += voronoiPane.getDistance(g1, g2);
			}
		}
		
		double n = ((double)pSet.size());
		
		return totalDist / (((n * n) - n) / 2d);
	}
	
	/**
	* Find the smallest polygon within the given set
	*/
	
	private Polygon getSmallestPolygon(ArrayList pSet)
	{
		double smallest = Double.MAX_VALUE;
		Polygon p;
		Polygon smallestP = null;
		double att;
		
		for (int i = 0; i < pSet.size(); i++)
		{
			p = (Polygon)pSet.get(i);
			
			if (voronoiPane.getPerimeterCalc())
				att = p.getPerimeter();
			else
				att = p.getArea();
			
			if (att < smallest)
			{
				smallest = att;
				smallestP = p;
			}
		}
		return smallestP;
	}
	
	/**
	* Find the largest polygon within the given set
	*/
	
	private Polygon getLargestPolygon(ArrayList pSet)
	{
		double largest = Double.MIN_VALUE;
		Polygon p;
		Polygon largestP = null;
		double att;
		
		for (int i = 0; i < pSet.size(); i++)
		{
			p = (Polygon)pSet.get(i);
			
			if (voronoiPane.getPerimeterCalc())
				att = p.getPerimeter();
			else
				att = p.getArea();
			
			if (att > largest)
			{
				largest = att;
				largestP = p;
			}
		}
		return largestP;
	}
	
	/**
	* Associate a generator to a cluster leaf node when carrying out ClusterByThreshold
	*/
	
	private void associateGeneratorToCluster(ArrayList region, ClusterNode node)
	{
		Polygon p;
		Coordinate g;
		
		for (int i = 0; i < region.size(); i++)
		{
			p = (Polygon)region.get(i);
			g = p.getGenerator();
			
			generatorLeaf.put(g, node);
		}
	}
	
	/*
	* Given two items return the probability that they should be in the same
	* cluster according to their co-occurrence in the cluster tree created in 
	* ClusterByThreshold
	*/
	
	private double getProbability(Coordinate c1, Coordinate c2)
	{
		// First see if the two coordinates are in the same lowest
		// level cluster (leaf node). If they are then assign a probability
		// of one
		
		ClusterNode leaf1, leaf2;
		
		leaf1 = (ClusterNode)generatorLeaf.get(c1);
		leaf2 = (ClusterNode)generatorLeaf.get(c2);
		
		if ((leaf1 == null) || (leaf2 == null))
		{//System.out.println("leave(s) null");
			return 0.25d;
		}
		else if (leaf1 == leaf2)
		{//System.out.println("Both leaves same");
			return 1d;
		}
		else
		{
			// The two points are not in the same cluster. Find
			// out where in the tree that they diverged
			
			int level;
			int divergeLevel;
			
			if (leaf1.getLevel() == leaf2.getLevel())
			{
				// If the two nodes are at the same level of the tree
				// move up from that level until they have the same parent
				
				level = leaf1.getLevel();
				ClusterNode p1 = leaf1.getParent(), p2 = leaf2.getParent();
				
				while (p1 != p2)
				{
					p1 = p1.getParent();
					p2 = p2.getParent();
				}
				
				divergeLevel = p1.getLevel();
				
				System.out.println(divergeLevel / (double)leaf1.getLevel());
				
				return divergeLevel / (double)leaf1.getLevel();
			}
			else
			{
				double result;
				
				// Starting at the deepest node go back up the tree until
				// both nodes have the same paprent
				
				ClusterNode p1, p2, deepest;
				
				if (leaf1.getLevel() > leaf2.getLevel())
				{
					deepest = leaf1;
					p1 = leaf1.getParent();
					p2 = leaf2.getParent();
				}
				else
				{
					deepest = leaf2;
					p2 = leaf1.getParent();
					p1 = leaf2.getParent();
				}
				
				while (p1.getLevel() != p2.getLevel())
					p1 = p1.getParent();
				
				while (p1 != p2)
				{
					p1 = p1.getParent();
					p2 = p2.getParent();
				}
				
				divergeLevel = p1.getLevel();
				
				result = Math.sqrt(((double)divergeLevel) / ((double)deepest.getLevel()));
				
				return result;
			}
		}
	}
	
	/**
	* The purpose of the following inner class is to sort an array of cluster
	* inter-object distances to maintain the match in indices with a corresponding
	* array of cluster polygons
	*/
	
	/*
	 * @(#)QSortAlgorithm.java      1.3   29 Feb 1996 James Gosling
	 *
	 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
	 *
	 * Permission to use, copy, modify, and distribute this software
	 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
	 * without fee is hereby granted. 
	 * Please refer to the file http://www.javasoft.com/copy_trademarks.html
	 * for further important copyright and trademark information and to
	 * http://www.javasoft.com/licensing.html for further important
	 * licensing information for the Java (tm) Technology.
	 * 
	 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
	 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
	 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
	 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
	 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
	 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
	 * 
	 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
	 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
	 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
	 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
	 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
	 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
	 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
	 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
	 * HIGH RISK ACTIVITIES.
	 */
	
	/**
	 * A quick sort demonstration algorithm
	 * SortAlgorithm.java
	 *
	 * @author James Gosling
	 * @author Kevin A. Smith
	 * @version     @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
	 * extended with TriMedian and InsertionSort by Denis Ahrens
	 * with all the tips from Robert Sedgewick (Algorithms in C++).
	 * It uses TriMedian and InsertionSort for lists shorts than 4.
	 * <fuhrmann@cs.tu-berlin.de>
	 */
	 public class FastQSortAlgorithm implements java.io.Serializable
	 {
		 // Versioning for serialisation
		 
		 static final long serialVersionUID = 50L;
		 
		 /** This is a generic version of C.A.R Hoare's Quick Sort 
		 * algorithm.  This will handle arrays that are already
		 * sorted, and arrays with duplicate keys.<BR>
		 *
		 * If you think of a one dimensional array as going from
		 * the lowest index on the left to the highest index on the right
		 * then the parameters to this function are lowest index or
		 * left and highest index or right.  The first time you call
		 * this function it will be with the parameters 0, a.length - 1.
		 *
		 * @param a         an integer array
		 * @param lo0     left boundary of array partition
		 * @param hi0     right boundary of array partition
		 */
		 private void QuickSort(double a[], Polygon b[], int l, int r)
		 {
			 int M = 4;
			 int i;
			 int j;
			 double v;
			 
			 if ((r-l)>M)
			 {
				 i = (r+l)/2;
				 if (a[l]>a[i]) swap(a, b, l,i);     // Tri-Median Methode!
				 if (a[l]>a[r]) swap(a, b, l,r);
				 if (a[i]>a[r]) swap(a, b, i,r);
				 
				 j = r-1;
				 swap(a, b, i, j);
				 i = l;
				 v = a[j];
				for(;;)
				{
					while(a[++i]<v);
						while(a[--j]>v);
							if (j<i) break;
								swap (a, b, i,j);
				}
				swap(a, b, i,r-1);
				QuickSort(a, b, l,j);
				QuickSort(a, b, i+1,r);
			 }
		 }
		 
		private void swap(double a[], Polygon b[], int i, int j)
		{
			double T1;
			T1 = a[i]; 
			a[i] = a[j];
			a[j] = T1;
			
			Polygon T2;
			T2 = b[i]; 
			b[i] = b[j];
			b[j] = T2;
		}
		
		private void InsertionSort(double a[], Polygon b[], int lo0, int hi0)
		{
			int i;
			int j;
			double v;
			Polygon v2;
			
			for (i=lo0+1;i<=hi0;i++)
			{
				v = a[i];
				v2 = b[i];
				
				j=i;
				while ((j>lo0) && (a[j-1]>v))
				{
					a[j] = a[j-1];
					b[j] = b[j-1];
					
					j--;
				}
				a[j] = v;
				b[j] = v2;
			}
		}
		
		public void sort(double a[], Polygon b[])
		{
			QuickSort(a, b, 0, a.length - 1);
			InsertionSort(a, b, 0, a.length-1);
		}
	}
}

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
 * QuaternaryTree: provides a bucketing approach for fast nearest
 * neighbour searches
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import data.*;

import java.util.ArrayList;

public class QuaternaryTree implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Array list of coordinates for generators that are
	// to be placed in the Voronoi diagram
	
	private ArrayList generators;
	
	// The number of levels in the tree
	
	private int k = 0;
	
	// The number of generators
	
	private int numPoints = 0;
	
	// Number of buckets (leaf nodes) in the tree
	
	private int numBuckets = 0;
	
	// Array of buckets
	
	private ArrayList buckets[];
	
	// Array of tree levels. Each element is an array list of
	// tree nodes on the corresponding level
	
	private ArrayList treeLevels[];
	
	private QuadTreeNode rootNode;
	
	// The number of rows and columns in the grid representing
	// the entire set of leaf node
	
	private int numRows;
	
	// The coordinate that will be assigned to the rootnode
	
	private Coord p1;
	
	// The VoronoiPane parent component
	
	private VoronoiPane parent;
	
	public QuaternaryTree(ArrayList generators, Coord p1, VoronoiPane parent)
	{
		this.parent = parent;
		this.generators = generators;
		numPoints = generators.size();
		this.p1 = p1;
		createTree();
	}
	
	/**
	* Build the quaternary tree
	*/
	private void createTree()
	{
		defineK();
		
		try
		{
			addPointsToBuckets();
		}
		catch (Exception ex){}
		
		// Create the root node
		
		rootNode = new QuadTreeNode();
		rootNode.setNodeType(QuadTreeNode.ROOT_NODE);
		rootNode.addGenerator(p1);
		
		// Build the quaternary tree
		
		buildTree(k, rootNode, 0, numRows - 1, 0, numRows - 1);
	}
	
	/**
	* Given the number of genereator points, determine the
	* number of levels and leaf nodes in the tree
	*/
	private void defineK()
	{
		k = (int)Math.round(Math.log(numPoints) / Math.log(4));
		numBuckets = (int)Math.pow(4, k);
		numRows = (int)Math.sqrt((double)numBuckets);
		
		// Init the array of tree levels
		
		treeLevels = new ArrayList[k];
		
		for (int i = 0; i < k; i++)
			treeLevels[i] = new ArrayList();
	}
	
	/**
	* Allocate each generator to a bucket in the tree
	*/
	private void addPointsToBuckets()
	{	
		buckets = new ArrayList[numBuckets];
		int r, c, index;
		Coord g;
		int k2 = (int)Math.pow(2, k);
		
		for (int i = 0; i < generators.size(); i++)
		{
			// Determine the row and column by multiplying the
			// coordinate by 2^k and then truncating off the fractional
			// parts
			
			g = (Coord)generators.get(i);
			
			// Store a reference to the DataItem in the Coord object
			// This will facilitate view coordination
			
			g.setIndex(i);
			
			double x = 0, y = 0;
			if (g.getX() == 1) 
				x = 0.999999;
			else
				x = g.getX();
			
			if (g.getY() == 1)
				y = 0.999999;
			else
				y = g.getY();
			
			r = (int)(k2 * y);
			c = (int)(k2 * x);
			
			// The generators are added to a 1D array of buckets. Each
			// index position is derived from the r and c values
			
			index = c + (r * numRows);
			
			if (index < buckets.length)
				if (buckets[index] == null)
					buckets[index] = new ArrayList();
			
			buckets[index].add(generators.get(i));
		}
	}
	
	/**
	* Build the structure of the tree in a depth-first manner
	*/
	private void buildTree(int kIn, QuadTreeNode n, int rStart, int rEnd, int cStart, int cEnd)
	{
		if (kIn > 0)
		{
			QuadTreeNode newNode;
			int divider1 = rStart + (int)((rEnd - rStart) / 2);
			int divider2 = cStart + (int)((cEnd - cStart) / 2);
			
			for (int i = 0; i < 4; i++)
			{
				newNode = new QuadTreeNode();
				
				switch(i)
				{
					case 0:
						n.setTopLeft(newNode);
						newNode.setParentNode(n);
						buildTree(kIn - 1, newNode, rStart, divider1, cStart, divider2);
						break;
					case 1:
						n.setTopRight(newNode);
						newNode.setParentNode(n);
						buildTree(kIn - 1, newNode, rStart, divider1, divider2 + 1, cEnd);
						break;
					case 2:
						n.setBottomLeft(newNode);
						newNode.setParentNode(n);
						buildTree(kIn - 1, newNode, divider1 + 1, rEnd, cStart, divider2);
						break;
					case 3:
						n.setBottomRight(newNode);
						newNode.setParentNode(n);
						buildTree(kIn - 1, newNode, divider1 + 1, rEnd, divider2 + 1, cEnd);
						break;
				}
				
				// Store the nodes for this particular level
				
				treeLevels[Math.abs(kIn - k)].add(newNode);
			}
		}
		else
		{
			// Add the corresponding buckets to each of the leaf nodes
			
			n.setNodeType(QuadTreeNode.LEAF_NODE);
			
			int index = cStart + (rStart * numRows);
			
			n.setGenerators(buckets[index]);
			
			// Populate the parent nodes up to but not including the root node
			
			if (buckets[index] != null)
				populateParentNodes(n, (Coord)(buckets[index].get(0)));
		}
	}
	
	/**
	* After creating and populating the leaf nodes, we must select a
	* generator from each non-empty leaf node bucket and allocate it
	* to each consecutive parent defined on the path from the leaf node
	* to the root node
	*/
	
	private void populateParentNodes(QuadTreeNode child, Coord g)
	{
		QuadTreeNode parent = child.getParentNode();
		
		if ((parent.getNodeType() != QuadTreeNode.ROOT_NODE) && (parent.getGeneratorCount() == 0))
		{
			parent.addGenerator(g);
			populateParentNodes(parent, g);
		}
	}
	
	/**
	* Acccessor method to get the number of levels in the tree
	*/
	public int levelCount()
	{
		return k;
	}
	
	/**
	* Accessor method to get nodes in a particular level of the tree
	*/
	public ArrayList getLevel(int i)
	{
		if ((i < 0) || (i > (k - 1)))
			return null;
		else
			return treeLevels[i];
	}
	
	/**
	* Given a 2D point, determine which point in the tree is closest to it
	*/
	public int getClosestPoint(Coord p, QuadTreeNode node)
	{
		int result = -1;
		double dist, closestDist = Double.MAX_VALUE;
		QuadTreeNode testNode = null;
		QuadTreeNode closestNode = null;
		Coord scaledPoint = null;
		
		// Find the populated node which has a generator point closest to p
		
		if (node.getNodeType() != QuadTreeNode.LEAF_NODE)
		{
			for (int j = 0; j < 4; j++)
			{
				// Determine which qudrant node we're examining
				
				switch(j)
				{
					case 0:
						testNode = node.getTopLeft();
						break;
					case 1:
						testNode = node.getTopRight();
						break;
					case 2:
						testNode = node.getBottomLeft();
						break;
					case 3:
						testNode = node.getBottomRight();
						break;
				}
				
				if (testNode.getGeneratorCount() > 0)
				{
					// Determine the closest node
					
					scaledPoint = new Coord((Coord)testNode.getGenerator(0));
					scaledPoint.setX(parent.calcPosX(scaledPoint.getX()));
					scaledPoint.setY(parent.calcPosY(scaledPoint.getY()));
					
					dist = parent.getDistance(p, scaledPoint);
					if (dist < closestDist)
					{
						closestDist = dist;
						closestNode = testNode;
					}
				}
			}
			
			if (closestNode != null)
				result = getClosestPoint(p, closestNode);
			else
				result = getClosestPoint(p, testNode);
		}
		else
		{
			// This is a leaf node, find the closest point in its bucket
			
			for (int i = 0; i < node.getGeneratorCount(); i++)
			{
				scaledPoint = new Coord((Coord)node.getGenerator(i));
				scaledPoint.setX(parent.calcPosX(scaledPoint.getX()));
				scaledPoint.setY(parent.calcPosY(scaledPoint.getY()));
				
				dist = parent.getDistance(p, scaledPoint);
				if (dist < closestDist)
				{
					closestDist = dist;
					result = ((Coord)node.getGenerator(i)).getIndex();
				}
			}
		}
		
		return result;
	}
	
	public QuadTreeNode getRootNode()
	{
		return rootNode;
	}
}

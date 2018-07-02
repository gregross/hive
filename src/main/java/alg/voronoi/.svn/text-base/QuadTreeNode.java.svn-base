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
 * QuadTreeNode: A node for the QuaternaryTree class
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import math.Coordinate;

import java.util.ArrayList;

public class QuadTreeNode implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Determine the type of node
	
	public static final int ROOT_NODE = 0;
	public static final int INTERMEDDIATE_NODE = 1;
	public static final int LEAF_NODE = 2;
	
	private int nodeType =  INTERMEDDIATE_NODE;
	
	// Pointer to the child nodes defined by quadrant. These are null for
	// leaf nodes
	
	private QuadTreeNode topLeft = null;
	private QuadTreeNode topRight = null;
	private QuadTreeNode bottomLeft = null;
	private QuadTreeNode bottomRight = null;
	
	// Pointer to the parent node. This is null for
	// the root node
	
	private QuadTreeNode parentNode = null;
	
	// A generator associated with this node
	
	private ArrayList generators = null;
	
	// Accessor methods for node type
	
	public int getNodeType()
	{
		return nodeType;
	}
	
	public void setNodeType(int nodeType)
	{
		this.nodeType = nodeType;
	}
	
	// Accessor methods for node children
	
	public QuadTreeNode getTopLeft()
	{
		return topLeft;
	}
	
	public void setTopLeft(QuadTreeNode topLeft)
	{
		this.topLeft = topLeft;
	}
	
	public QuadTreeNode getTopRight()
	{
		return topRight;
	}
	
	public void setTopRight(QuadTreeNode topRight)
	{
		this.topRight = topRight;
	}
	
	public QuadTreeNode getBottomLeft()
	{
		return bottomLeft;
	}
	
	public void setBottomLeft(QuadTreeNode bottomLeft)
	{
		this.bottomLeft = bottomLeft;
	}
	
	public QuadTreeNode getBottomRight()
	{
		return bottomRight;
	}
	
	public void setBottomRight(QuadTreeNode bottomRight)
	{
		this.bottomRight = bottomRight;
	}
	
	// Accessor methods for the parent node
	
	public QuadTreeNode getParentNode()
	{
		return parentNode;
	}
	
	public void setParentNode(QuadTreeNode parentNode)
	{
		this.parentNode = parentNode;
	}
	
	// Accessor methods for the generator(s) associated with this node
	
	public Coord getGenerator(int index)
	{
		if (generators == null)
			return null;
		else
			return (Coord)generators.get(index);
	}
	
	public void addGenerator(Coord generator)
	{
		if (generators == null)
			generators = new ArrayList();
		
		generators.add(generator);
	}
	
	public void setGenerators(ArrayList generators)
	{
		this.generators = generators;
	}
	
	public int getGeneratorCount()
	{
		if (generators == null)
			return 0;
		else
			return generators.size();
	}
}

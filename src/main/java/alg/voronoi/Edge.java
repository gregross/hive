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
 * Edge: This class represents a edge in a planar
 * ordinary Voronoi diagram
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

public class Edge implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// rightPolygon: The polygon that is to
	// the right of the edge
	
	transient Polygon rightPolygon;
	
	// leftPolygon: The polygon that is to
	// the left of the edge
	
	transient Polygon leftPolygon;
	
	// startVertex: The start vertex of the
	// edge
	
	transient Vertex startVertex;
	
	// endVertex: The end vertex of the
	// edge
	
	transient Vertex endVertex;
	
	// cwPredecessor: The edge next to this edge
	// clockwise around the start vertex
	
	transient Edge cwPredecessor;
	
	// ccwPredecessor: The edge next to this edge
	// counterclockwise around the start vertex
	
	transient Edge ccwPredecessor;
	
	// cwSuccessor: The next to this edge
	// clockwise around the end vertex
	
	transient Edge cwSuccessor;
	
	// ccwSuccessor: The edge next to this edge
	// counterclockwise around the end vertex
	
	transient Edge ccwSuccessor;
	
	// Determine whether this edge should be deleted
	
	private boolean bDeleted = false;
	
	/**
	* Accessor methods for the above variables
	*/
	
	public Polygon getRightPolygon()
	{
		return rightPolygon;
	}
	
	public void setRightPolygon(Polygon rightPolygon)
	{
		this.rightPolygon = rightPolygon;
	}
	
	public Polygon getLeftPolygon()
	{
		return leftPolygon;
	}
	
	public void setLeftPolygon(Polygon leftPolygon)
	{
		this.leftPolygon = leftPolygon;
	}
	
	public Vertex getStartVertex()
	{
		return startVertex;
	}
	
	public void setStartVertex(Vertex startVertex)
	{
		this.startVertex = startVertex;
	}
	
	public Vertex getEndVertex()
	{
		return endVertex;
	}
	
	public void setEndVertex(Vertex endVertex)
	{
		this.endVertex = endVertex;
	}
	
	public Edge get_cwPredecessor()
	{
		return cwPredecessor;
	}
	
	public void set_cwPredecessor(Edge cwPredecessor)
	{
		this.cwPredecessor = cwPredecessor;
	}
	
	public Edge get_ccwPredecessor()
	{
		return ccwPredecessor;
	}
	
	public void set_ccwPredecessor(Edge ccwPredecessor)
	{
		this.ccwPredecessor = ccwPredecessor;
	}
	
	public Edge get_cwSuccessor()
	{
		return cwSuccessor;
	}
	
	public void set_cwSuccessor(Edge cwSuccessor)
	{
		this.cwSuccessor = cwSuccessor;
	}
	
	public Edge get_ccwSuccessor()
	{
		return ccwSuccessor;
	}
	
	public void set_ccwSuccessor(Edge ccwSuccessor)
	{
		this.ccwSuccessor = ccwSuccessor;
	}
	
	public boolean deleted()
	{
		return bDeleted;
	}
	
	public void deleted(boolean bDeleted)
	{
		this.bDeleted = bDeleted;
	}
}

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
 * Vertex: represents a vertex in a Voronoi diagram
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

public class Vertex extends Coord implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// edgeAroundVertex: An edge incident to the
	// vertex
	
	transient Edge edgeAroundVertex;
	
	// w: Contains 1 if vertex i is an ordinary point, otherwise
	// contains 0 if point i is a point at infinity
	
	// x and y: If Vertex i is an ordinary point,
	// that is w = 1 then store the x and y co-ordinates
	// respectively. On the other hand if w = 0 then store the
	// x and y components of the unit vector designating the direction
	// in which the associated (infinite) Voronoi edge runs
	
	int wi;
	
	public Vertex(Coord c)
	{
		super(c);
	}
	
	public Vertex()
	{
		super();
	}
	
	public void setWi(int wi)
	{
		this.wi = wi;
	}
	
	public int getWi()
	{
		return wi;
	}
	
	public Edge getEdgeAroundVertex()
	{
		return edgeAroundVertex;
	}
	
	public void setEdgeAroundVertex(Edge edgeAroundVertex)
	{
		this.edgeAroundVertex = edgeAroundVertex;
	}
}

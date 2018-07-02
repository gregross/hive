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
 * Polygon: This class represents a polygon in a planar
 * ordinary Voronoi diagram
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import java.util.ArrayList;

public class Polygon  implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// edgeAroundPolygon: An edge on the boundary of
	// the polygon
	
	private transient Edge edgeAroundPolygon;
	
	// The generator that is associated with the polygon
	
	private transient Coord generator = null;
	
	// The perimeter of the Voronoi polygon
	
	private double perimeter = 0;
	
	// The area of the Voronoi polygon
	
	private double area = 0;
	
	// Determine whether the polygon is part of a region of similar contiguous polygons
	
	private transient ArrayList region = null;
	
	public Edge getEdgeAroundPolygon()
	{
		return edgeAroundPolygon;
	}
	
	public void setEdgeAroundPolygon(Edge edgeAroundPolygon)
	{
		this.edgeAroundPolygon = edgeAroundPolygon;
	}
	
	public Coord getGenerator()
	{
		return generator;
	}
	
	public void setGenerator(Coord generator)
	{
		this.generator = generator;
	}
	
	public double getPerimeter()
	{
		return perimeter;
	}
	
	public void setPerimeter(double perimeter)
	{
		this.perimeter = perimeter;
	}
	
	public double getArea()
	{
		return area;
	}
	
	public void setArea(double area)
	{
		this.area = area;
	}
	
	public void setRegion(ArrayList region)
	{
		this.region = region;
	}
	
	public ArrayList getRegion()
	{
		return region;
	}
}

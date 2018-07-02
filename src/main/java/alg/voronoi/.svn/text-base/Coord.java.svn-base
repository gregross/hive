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
 * Contributor(s): Matthew Chalmers <matthew@dcs.gla.ac.uk>
 *                 Alistair Morrison <morrisaj@dcs.gla.ac.uk>
 *                 Greg Ross <gr@dcs.gla.ac.uk>
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
 * Coord: This class represents a site/generator in a planar
 * ordinary Voronoi diagram
 *
 *  @author Greg Ross
 */
 
package alg.voronoi;

import math.Coordinate;

import java.util.ArrayList;
import java.io.Serializable;

public class Coord extends Coordinate implements java.io.Serializable
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    // The poylgon that surrounds the generators
    
    transient Polygon p = null;
    
    // Store the ordinal number of the data point in the set of
    // points from which the tree has been built
    
    int index;
    
    /**
     * constructor: Takes no params and sets the initial values to be 0.0
     */
    public Coord()
    {
	super();
    }
    
    /**
     * constructor: takes two double values to represent the coords
     *
     * @param x The x value of coord
     * @param y The y value
     */
    
    public Coord(double x, double y)
    {
	super.setX(x) ;
	super.setY(y);
    }
    
    /**
     * constructor: instantiate the coordinate with a new copy of an existing
     * coordinate
     * 
     * @param c Coordinate values to be used for instantiating
     */
    public Coord(Coord c)
    {
	this(c.getX(), c.getY());
    }
    
    public Coord(Coordinate c)
    {
	this(c.getX(), c.getY());
    }
    
    /**
     * Sets the values of the coordinates
     *
     * @param x The x value
     * @param y The y value
     */
    public void set(double x, double y)
    {
	setX(x);
	setY(y);
    }
    
    /**
     *
     *
     * @param c
     */
    public void set(Coord c)
    {
	setX(c.getX()); 
	setY(c.getY());
    }
    
    public double length()
    {
	return Math.sqrt((super.getX() * super.getX()) + (super.getY() * super.getY()));
    }
    
    public double dotProduct(Coord c)
    {
	return (c.getX() * x) + (c.getY() * y) ;
    }
    
    public Polygon getPolygon()
    {
	    return p;
    }
    
    public void setPolygon(Polygon p)
    {
	    this.p = p;
    }
    
    public void setIndex(int index)
    {
	    this.index = index;
    }
    
    public int getIndex()
    {
	    return index;
    }
}

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
 * Link
 *
 * Class represents a link or link section in the data flow model for visual composition.
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.awt.geom.*;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Color;

public class Link implements java.io.Serializable
{
	public static final int LINE_THICKNESS_DATA = 10;	// Data link thickness
	public static final int LINE_THICKNESS_SELECTION = 5;	// Selection link thickness
	
	public static final Color SELECTED_FROM_COLOR = Color.red;
	public static final Color SELECTED_TO_COLOR = Color.red;
	public static final Color UNSELECTED_FROM_COLOR = Color.gray;
	public static final Color UNSELECTED_TO_COLOR = Color.gray;
	
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Store references to the VisualModule instances that are connected by
	// a link
	
	private VisualModule from_VisMod = null;
	private VisualModule to_VisMod = null;
	
	// Store references to the ports that are connected by this link
	
	private ModulePort from_Port = null;
	private ModulePort to_Port = null;
	
	// Determine whether the link has been selected
	
	private boolean bSelected = false;
	
	// Store the key of the link that is used in retrieving it from the HashMap of links
	// in the MDI form
	
	private String key = null;
	
	// If the link is made up of more than one segment store its path
	
	transient private GeneralPath path = null;
	transient private GeneralPath newPath = null;
	
	// Determine which link join is selected
	
	private int joinSelected = -1;
	private boolean joinNotSelected = false;
	private int linkSegment = -1;
	
	// Store the last join moved to determine whether it is aligned
	// with the one before and the one after it. If it is then it
	// should be removed
	
	private Point lastJoinMoved = null;
	private Point beforeLastJoinMoved = null;
	private Point afterLastJoinMoved = null;
	
	// Array of double numbers to hold a representation of the path
	// This is used to restore the path after deserialisations because
	// GeneralPath objects cannot be serialised
	
	private double[][] serialisedPath;
	
	// Determine whether the link is one created between two modules
	// where it is expected that HIVE will generate an algorithm to be
	// inserted between them
	
	private boolean placeHolder = false;
	
	public Link(VisualModule from_VisMod, VisualModule to_VisMod, 
	ModulePort from_Port, ModulePort to_Port, String key)
	{
		this.from_VisMod = from_VisMod;
		this.to_VisMod = to_VisMod;
		this.from_Port = from_Port;
		this.to_Port = to_Port;
		this.key = key;
		
		// Determine whether this link is a place holder for a
		// hybrid algorithm
		
		if (ScriptModel.triggerHybrid(from_Port, to_Port))
			placeHolder = true;
		else
			placeHolder = false;
	}
	
	/**
	* Accessor method to determine whether this link is a place holder
	* for a hybrid algorithmic conjunction
	*/
	
	public boolean isPlaceHolder()
	{
		return placeHolder;
	}
	
	public GeneralPath getPath()
	{
		return path;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public void setKey(String sKey)
	{
		key = sKey;
	}
	
	public boolean getSelected()
	{
		return bSelected;
	}
	
	public void setSelected(boolean bSelected)
	{
		this.bSelected = bSelected;
	}
	
	public ModulePort getFromPort()
	{
		return from_Port;
	}
	
	public ModulePort getToPort()
	{
		return 	to_Port;
	}
	
	public VisualModule getFromModule()
	{
		return from_VisMod;
	}
	
	public VisualModule getToModule()
	{
		return to_VisMod;
	}
	
	public void dragLink(int x, int y, int linkSegment, int joinSelected)
	{
		// the user has selected a link and is dragging it.
		// Determine whether to create a new join or move
		// an existing one
		
		// If the user had created a new join, this is automatically selected
		// internal to this class whereas the joinSelected passed in remains = -1
		// Hence we need to only set the internal join to the passed in value once
		
		if (!joinNotSelected)
		{
			this.joinSelected = joinSelected;
			this.linkSegment = linkSegment;
		}
		
		if (path == null)
		{
			// The first time the user manipulates the link
			
			path = new GeneralPath();
			path.moveTo(from_Port.xPos(), from_Port.yPos());
			path.lineTo(to_Port.xPos(), to_Port.yPos());
		}
		else if (this.joinSelected == -1)
		{	
			// Create a new join/segment
			
			restorePath(x, y, this.linkSegment, false);
			joinNotSelected = true;
			this.joinSelected = this.linkSegment;
		}
		else if (this.joinSelected != -1)
		{
			// Move a join
			
			restorePath(x, y, this.joinSelected, true);
		}
	}
	
	public Point getLastJoinMoved()
	{
		return lastJoinMoved;	
	}
	
	public Point getRightLastJoinMoved()
	{
		return afterLastJoinMoved;	
	}
	
	public Point getLeftLastJoinMoved()
	{
		return beforeLastJoinMoved;	
	}
	
	public void validateLastJoin(Point theJoin)
	{
		// If the last link manipulation moved a join to be
		// in line with joins immediately before and after it
		// then that join should be removed
		
		if (theJoin != null)
		{
			PathIterator pi = path.getPathIterator(null);
			double[] coords = new double[2];
			beforeLastJoinMoved = null;
			afterLastJoinMoved = null;
			boolean beforeFound = false;
			boolean afterFound = false;
			while (!pi.isDone())
			{
				int segtype = pi.currentSegment(coords);
				switch (segtype) 
				{
					case PathIterator.SEG_MOVETO:
						if (!beforeFound)
							beforeLastJoinMoved = new Point((int)coords[0], (int)coords[1]);
						break;
					case PathIterator.SEG_LINETO:
						if (!afterFound)
						{
							afterLastJoinMoved = new Point((int)coords[0], (int)coords[1]);
							if (beforeFound)
								afterFound = true;
						}
						
						// Check to see if this is the last join moved
						if (theJoin.equals(new Point((int)coords[0], (int)coords[1])))
							beforeFound = true;
						
						if (!beforeFound)
						{
							beforeLastJoinMoved = new Point((int)coords[0], (int)coords[1]);
						}
						break;
				}
				pi.next();
			}
			if ((beforeLastJoinMoved != null) && (afterLastJoinMoved != null)
				&& (!theJoin.equals(beforeLastJoinMoved)) && (!theJoin.equals(afterLastJoinMoved)))
				if (isInLine(beforeLastJoinMoved, theJoin, afterLastJoinMoved))
					removeJoin(theJoin);
		}
	}
	
	private void removeJoin(Point join)
	{
		PathIterator pi = path.getPathIterator(null);
		double[] coords = new double[2];
		newPath = new GeneralPath();
		Point before = null;
		Point after = null;
		boolean beforeFound = false;
		boolean afterFound = false;
		while (!pi.isDone())
		{
			int segtype = pi.currentSegment(coords);
			switch (segtype) 
			{
				case PathIterator.SEG_MOVETO:
					newPath.moveTo((int)coords[0], (int)coords[1]);
					break;
				case PathIterator.SEG_LINETO:
					if (!join.equals(new Point((int)coords[0], (int)coords[1])))
						newPath.lineTo((int)coords[0], (int)coords[1]);
					break;
			}
			pi.next();
		}
		path.reset();
		path = newPath;
		from_VisMod.getParentForm().getDrawPane().paintLinks();
	}
	
	private boolean isInLine(Point before, Point current, Point after)
	{
		// When the user is moving a link join, this function
		// returns true if the join is moved to be aligned with
		// the line connecting the point before it and the one
		// after it
		
		double distBeforeToAfter = GuiUtil.pointDistance(before, after);
		double distCurrentToBefore = GuiUtil.pointDistance(current, before);
		double distCurrentToAfter = GuiUtil.pointDistance(current, after);
		
		// If the sum of lengths of the two join segments is close to the
		// direct distance between the before and after joins then return true
		
		double diff = ((distCurrentToBefore + distCurrentToAfter) - distBeforeToAfter);
		if (diff < 0)
			diff *= -1;
			
		if (diff < 0.5)
			return true;
		else
			return false;
	}
	
	private void restorePath(int x, int y, int index, boolean atJoin)
	{
		// Breakdown the old path creating a new one at the same time
		// the boolean value atJoin determines whether a join location is 
		// being manipulated
		
		PathIterator pi = path.getPathIterator(null);
		double[] coords = new double[2];
		int iteration = -1;
		newPath = new GeneralPath();
		while (!pi.isDone())
		{
			int segtype = pi.currentSegment(coords);
			switch (segtype) 
			{
				case PathIterator.SEG_MOVETO:
					iteration++;
					newPath.moveTo((int)coords[0], (int)coords[1]);
					
					// If index = -1 then this is the first time the link is modified
					// Create a new line and set the segment to 1 so that the new join will
					// be selected
					
					if (index == -1)
					{
						newPath.lineTo(x, y);
						linkSegment = 1;
					}
					break;
				case PathIterator.SEG_LINETO:
					iteration++;
					
					// Check to see if the current iteration matches the index
					// of the segment or join (whichever is selected)
					
					if (iteration != index)
						newPath.lineTo((int)coords[0], (int)coords[1]);
					else
					{
						if (!atJoin)
						{
							newPath.lineTo(x, y);
							newPath.lineTo((int)coords[0], (int)coords[1]);
						}
						else
						{
							newPath.lineTo(x, y);
							
							// Store the last join moved so that we can test
							// its alignment with the link
							
							lastJoinMoved = new Point(x, y);
						}
					}
					break;
			}
			pi.next();
		}
		path.reset();
		path = newPath;
	}
	
	public void updatePath()
	{
		// When the composite link is drawn, this method is called
		// to determine whether the connecting modules have moved,
		// if so, the path is set to null, i.e. a straight line is drawn
		
		if (path != null)
		{
			// Get the start and end points of the path
			
			PathIterator pi = path.getPathIterator(null);
			double[] coords = new double[2];
			Point start = null;
			Point end = null;
			Line2D.Double line = null;
			while (!pi.isDone())
			{
				end = null;
				int segtype = pi.currentSegment(coords);
				switch (segtype) 
				{
					case PathIterator.SEG_MOVETO:
						start = new Point((int)coords[0], (int)coords[1]);
						break;
					case PathIterator.SEG_LINETO:
						end = new Point((int)coords[0], (int)coords[1]);
						break;
				}
				pi.next();
			}
			
			// When the start and end points of the path are found
			// compare these to the port positions of the connecting modules
			// to see if the modules have changed position
			
			if ((start != null) && (end  != null))
			{
				Point from_Port_Point = new Point(from_Port.xPos(), from_Port.yPos());
				Point to_Port_Point = new Point(to_Port.xPos(), to_Port.yPos());
				
				if ((!start.equals(from_Port_Point)) || (!end.equals(to_Port_Point)))
				{	
					path.reset();
					path = null;
				}
			}
		}
	}
	
	public void resetCurrentJoin()
	{
		// Resets the currently selected join so that the same
		// one isn't always selected
		
		joinNotSelected = false;
		linkSegment = -1;
		lastJoinMoved = null;
	}
	
	/**
	* If a link is deleted, either because a module or the link its self
	* is removed, then this method is called to remove the registered 
	* Observer VisualModules from the ModulePorts concerned
	*/
	
	public void removeObservers()
	{
		from_Port.removeObserver(to_Port);
		to_Port.removeObserver(from_Port);
	}
	
	/**
	* Transform the path into an array which can be serialised
	*/
	
	private void transformPathForSerialisation()
	{
		if (path != null)
		{
			PathIterator pi = path.getPathIterator(null);
			double[] coords = new double[2];
			int numEntries = 0;
			int currentEntry = 0;
			
			// Traverse the path to determine the size of the array
			
			while (!pi.isDone())
			{
				numEntries++;
				pi.next();
			}
			
			// Now initialise the array
			
			serialisedPath = new double[numEntries][2];
			
			// Traverse the path again, this time copying each point's
			// co-ordinates into the array
			
			pi = path.getPathIterator(null);
			
			while (!pi.isDone())
			{
				int segtype = pi.currentSegment(coords);
				serialisedPath[currentEntry][0] = coords[0];
				serialisedPath[currentEntry][1] = coords[1];
				
				currentEntry++;
				pi.next();
			}
		}
		else
			serialisedPath = null;
	}
	
	/**
	* When the serialisedPath array is deserialised, we must
	* reconstruct the GeneralPath object 'path'
	*/
	
	private void restoreSerialisedPath()
	{
		if (serialisedPath != null)
		{	
			int count = serialisedPath.length;
			path = new GeneralPath();
			
			for (int i = 0; i < count; i++)
			{
				if (i == 0)
					path.moveTo((int)serialisedPath[i][0], (int)serialisedPath[i][1]);
				else
					path.lineTo((int)serialisedPath[i][0], (int)serialisedPath[i][1]);
			}
		}
	}
	
	/**
	* Method to restore transient/static object references after
	* deserialisation
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException,
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		restoreSerialisedPath();
	}
	
	/**
	* When serialising, this method is called to transform the GeneralPath instance, path,
	* into a serialisable array.
	*/
	
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
	{
		out.defaultWriteObject();
		transformPathForSerialisation();
	}
}

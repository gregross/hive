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
 * GuiUtil
 *
 * Contains utility functions for the application GUI.
 *
 *  @author Greg Ross
 */
package parent_gui;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import java.util.*;
import java.awt.*;
import java.awt.geom.Area;

public class GuiUtil 
{
	
	private static Mdi mdiForm;
	private static JDesktopPane desktop;
	
	public GuiUtil(Mdi frmIn)
	{
		mdiForm = frmIn;
	}
	
	public static boolean isFormOpen(String name)
	{
		// Determine whether the form with the given title
		// is open.
		
		int count;
		
		desktop = mdiForm.getDesktop();
		
		JInternalFrame child;
		
		boolean result = false;
		
		for (count = 0; count < desktop.getAllFrames().length; count++)
		{
			child = desktop.getAllFrames()[count];
			if (child.getTitle().equals(name))
			{
				result =  true;
				break;
			}
		}
		
		return result;
	}
	
	public static int randInteger(int lowerBound, int upperBound)
	{
		// Return a random number within the specified range 
		// (inclusive of the lower and upper bounds).
		
		if ((upperBound - lowerBound) > 0)
		{
			Random rand = new Random();
			return (lowerBound + rand.nextInt((upperBound - lowerBound)));
		}
		else
			return -1;
	}
	
	public static boolean oddNumber(int num)
	{
		// Determine whether the number, num, is od.
		// If so, return true, otherwise false
		
		if ((num % 2) == 0)
			return false;
		else
			return true;
	}
	
	public static double pointDistance(Point point1, Point point2)
	{
		// Return the Euclidean distance between the two give points
		
		double x1, y1, x2, y2;
		x1 = point1.getX();
		y1 = point1.getY();
		x2 = point2.getX();
		y2 = point2.getY();
		
		double sumDiff = 0;
		sumDiff += ((x1 - x2) * (x1 - x2));
		sumDiff += ((y1 - y2) * (y1 - y2));
		return Math.sqrt(sumDiff);
	}
	
	public static Shape getClipShape(DrawingCanvas drawPane)
	{
		// Given a drawing surface, derive the shape of the area not
		// taken up by visual modules. This is used used in the DrawingCanvas
		// PaintComponent method to set the clipping region
		
		// We must take into account the offsets of the scroll bars
		
		int derivedHeight = (new Double(drawPane.getSize().getHeight())).intValue();
		int y = (new Double(drawPane.getLocation().getY())).intValue();
		
		if (y < 0)
			derivedHeight += (y * -1);
		
		int derivedWidth = (new Double(drawPane.getSize().getWidth())).intValue();
		int x = (new Double(drawPane.getLocation().getX())).intValue();
		
		if (x < 0)
			derivedWidth += (x * -1);
		
		Rectangle newBounds = new Rectangle(drawPane.getBounds(null));
		newBounds.setSize(derivedWidth, derivedHeight);
		
		//Area parentArea = new Area(drawPane.getBounds(null));
		Area parentArea = new Area(newBounds);
		
		Rectangle componentBounds;
		
		for (int i = 0; i < drawPane.getComponentCount(); i++)
		{
			if (drawPane.getComponent(i) instanceof VisualModule)
			{
				componentBounds = drawPane.getComponent(i).getBounds(null);
				componentBounds.setLocation((int)(componentBounds.getLocation().getX()), 
					((int)componentBounds.getLocation().getY()));
				
				parentArea.subtract(new Area(componentBounds));
				
				//parentArea.subtract(new Area(drawPane.getComponent(i).getBounds(null)));
			}
		}
		return parentArea;
	}
	
	/**
	* When the mouse is released make sure that no ports are highligted
	* purely because they were compatible with the previously selected port
	*/
	
	public static void removeCompatHighlight()
	{	
		// Get iterator for the collection of existing modules
		
		HashMap modules = mdiForm.getModules();
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		
		ModulePort otherPort;
		DefaultVisualModule otherModule;
		
		// Go through all modules in the view
		
		while (iter.hasNext())
		{
			// Go through all ports of each module
			
			otherModule = ((DefaultVisualModule)modules.get((String)iter.next()));
			
			// Get port iterator
			
			Set portSet = otherModule.getPorts().keySet();
			Iterator portIter = portSet.iterator();
			
			while (portIter.hasNext())
			{
				otherPort = (ModulePort)otherModule.getPorts().get((String)portIter.next());
				otherPort.setCompatHighlight(false);
				
				if (otherModule.getParentForm().getLinkMode())
					otherModule.repaint();
			}
		}
	}
}

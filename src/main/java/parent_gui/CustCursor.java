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
 * CustCursor
 *
 * Class to represent available custom cursors
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.awt.*;

public class CustCursor
{
	Toolkit tk;
	Cursor LinkCursor = null;
	Cursor LinkOKCursor = null;
	Cursor LinkInvalidCursor = null;
	
	public CustCursor(Toolkit tk)
	{
		this.tk = tk;
		createLinkCursor("images/linkCursor.gif");
		createLinkOKCursor("images/motif_LinkDrop32x32.gif");
		createLinkInvalidCursor("images/motif_LinkNoDrop32x32.gif");
	}
	
	private boolean CustomCursorSupported()
	{
		Dimension d = tk.getBestCursorSize(32, 32);
		int colors = tk.getMaximumCursorColors();
		if (!d.equals(new Dimension(0, 0)) && (colors != 0)) 
			return true;
		else
			return false;
	}
	
	private void createLinkCursor(String sPath)
	{
		if (CustomCursorSupported())
		{
			Image image = tk.getImage(sPath);

			if (image != null) 
			{ 
				try 
				{ 
					// Create a custom cursor from the image
					// Specify hot spot coordinates and a string
					// containing a description of the cursor
					// for use with Java Accessibility
					LinkCursor = tk.createCustomCursor(image, new Point(0, 0), "linkCursor");
				} 
				catch(Exception exc) 
				{ 
					// Catch exceptions so that we
					// don't try to set a null cursor
					System.err.println("Unable to create custom cursor.");
				} 
			}
		}
	}
	
	private void createLinkOKCursor(String sPath)
	{
		if (CustomCursorSupported())
		{
			Image image = tk.getImage(sPath);

			if (image != null) 
			{ 
				try 
				{ 
					// Create a custom cursor from the image
					// Specify hot spot coordinates and a string
					// containing a description of the cursor
					// for use with Java Accessibility
					LinkOKCursor = tk.createCustomCursor(image, new Point(0, 0), "linkOKcursor");
				} 
				catch(Exception exc) 
				{ 
					// Catch exceptions so that we
					// don't try to set a null cursor
					System.err.println("Unable to create custom cursor.");
				} 
			}
		}
	}
	
	private void createLinkInvalidCursor(String sPath)
	{
		if (CustomCursorSupported())
		{
			Image image = tk.getImage(sPath);

			if (image != null) 
			{ 
				try 
				{ 
					// Create a custom cursor from the image
					// Specify hot spot coordinates and a string
					// containing a description of the cursor
					// for use with Java Accessibility
					LinkInvalidCursor = tk.createCustomCursor(image, new Point(0, 0), "linkNotOKcursor");
				} 
				catch(Exception exc) 
				{ 
					// Catch exceptions so that we
					// don't try to set a null cursor
					System.err.println("Unable to create custom cursor.");
				} 
			}
		}
	}
	
	public Cursor getLinkCursor()
	{
		return LinkCursor;
	}
	
	public Cursor getLinkOKCursor()
	{
		return LinkOKCursor;	
	}
	
	public Cursor getLinkInvalidCursor()
	{
		return LinkInvalidCursor;	
	}
}

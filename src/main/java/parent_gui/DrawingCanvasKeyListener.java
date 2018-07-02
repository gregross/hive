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
 * DrawingCanvasKeyListener
 *
 * Handles all keyboard events that are applicable to the JLayeredPane which is an
 * instance of the DrawingCanvas.
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class DrawingCanvasKeyListener extends KeyAdapter
{
	transient private Mdi mdiForm;
	private DrawingCanvas drawPane;
	private HashMap links;
		
	public DrawingCanvasKeyListener(Mdi mdiForm, DrawingCanvas drawPane)
	{
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
	}
	
	public void keyPressed(KeyEvent e)
	{
		// If a link is selected and the user presses the delete key,
		// remove the link
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE)
		{
			if (drawPane.getSelectedLink() != null)
			{
				Link link = drawPane.getSelectedLink();
				String linkKey = link.getKey();
				links = drawPane.getLinks();
				
				// Unregister Observer ports
				
				((Link)links.get(linkKey)).removeObservers();
				
				links.remove(linkKey);
				mdiForm.setPortsWithLinkHighlight(null, null, drawPane.getParentForm());
				drawPane.repaint();
				drawPane.setSelectedLink(null);
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
		mdiForm = parent_gui.Mdi.getInstance();
	}
}

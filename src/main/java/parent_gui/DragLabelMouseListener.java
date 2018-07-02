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
 * DragLabelMouseListener
 *
 * Handles all mouse events that are applicable to the DragLabel class
 *
 *  @author Greg Ross
 */
package parent_gui;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.Cursor;

public class DragLabelMouseListener extends MouseInputAdapter
{
	transient private Mdi mdiForm;
	private DragLabel label;
	
	public DragLabelMouseListener(Mdi mdiForm)
	{
		this.mdiForm = mdiForm;
	}
	
	public void mousePressed(MouseEvent e)
	{
		// Store the name of the dragged label
		
		if (e.getComponent() instanceof JLabel)
		{
			JLabel theLabel = (JLabel)e.getComponent();
			mdiForm.setDraggedModuleName(theLabel.getText());
		}
	}
	
	public void mouseEntered(MouseEvent e)
	{
		if (e.getComponent() instanceof DragLabel)
		{
			if (label != null)	
				if (label != (DragLabel)e.getComponent())
					label.highlight(false);
			
			label = (DragLabel)e.getComponent();
			mdiForm.setCursor(new Cursor(Cursor.HAND_CURSOR));
			label.highlight(true);
		}
	}
	
	public void mouseExited(MouseEvent e)
	{
		if (e.getComponent() instanceof DragLabel)
		{
			label = (DragLabel)e.getComponent();
			mdiForm.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			label.highlight(false);
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

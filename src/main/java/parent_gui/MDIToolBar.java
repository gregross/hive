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
 * MDIToolBar
 *
 * Class defines the toolbar found on the MDI form.
 *
 *  @author Greg Ross
 */
package parent_gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.reflect.Constructor;

public class MDIToolBar implements Runnable
{
	private static Mdi mdiForm;
	private JToolBar toolbar;
	
	// Store the number of components so that they can be referenced for
	// enabling / disabling.
	
	private int numButtons = 4;
	
	private DragLabelMouseListener dragLabelMouseListener;
	
	private JPanel dragPaneAlg;
	private JPanel dragPaneVis;
	
	// List of VisualModules loaded at runtime
	
	private ArrayList mods;
	
	// Class that uses the reflection API to load in VisualModules at runtime
	
	private ModuleReflection m;
	private ModuleReflection mr;
	
	// Thread that will periodically check to see if any new modules 
	// have been added/removed from the atb/alg directory.
	
	private Thread dynaLoadThread;
	
	// The period after which we will see if we have added/removed modules
	
	private static final int SLEEP_PERIOD = 15000;
	
	public 	MDIToolBar(Mdi frmIn)
	{
		mdiForm = frmIn;
		
		// Do the reflection stuff to load the library of visual
		// components at runtime
		
		m = new ModuleReflection(mdiForm);
		mods = m.getModules();
		
		mr = new ModuleReflection(mdiForm);
		
		dynaLoadThread = new Thread(this);
		dynaLoadThread.setPriority(Thread.MIN_PRIORITY);
		dynaLoadThread.start();
		
		// Add the toolbar to the MDI form.
		
		toolbar = new JToolBar();
		mdiForm.setToolBar(toolbar);
		mdiForm.getToolBar().setFloatable(true);
		addButtons(mdiForm.getToolBar());
       	mdiForm.getContentPane().add(mdiForm.getToolBar(), BorderLayout.NORTH);
	}
	
	private void addButtons(JToolBar toolBar) 
	{
		dragLabelMouseListener = new DragLabelMouseListener(mdiForm);
		
		// Add a JPanel to contain the draggable module reps.
		
		dragPaneAlg = new JPanel(new GridLayout(4, 0, 2, 2));
		dragPaneAlg.setMaximumSize(new Dimension(800 , 150));
		dragPaneAlg.setBorder(BorderFactory.createTitledBorder("Modules:"));
		mdiForm.getToolBar().add(dragPaneAlg);
		
		// Add another JPanel to contain the draggable visual reps
		
		dragPaneVis = new JPanel(new GridLayout(3, 0, 2, 2));
		dragPaneVis.setMaximumSize(new Dimension(800 , 150));
		dragPaneVis.setBorder(BorderFactory.createTitledBorder("Visualisations:"));
		mdiForm.getToolBar().add(dragPaneVis);
		
        	// Add the DragLabels that represent each of the loaded module types
		
		DragLabel label;
		DefaultVisualModule mod;
		
		for (int i = 0; i < mods.size(); i++)
		{
			mod = (DefaultVisualModule)mods.get(i);
			label = new DragLabel(mod.getLabelCaption(), JLabel.CENTER);
			label.setToolTipText(mod.getToolTipText());
			label.setName(mod.getName());
			label.addMouseListener(dragLabelMouseListener);
			label.addMouseMotionListener(dragLabelMouseListener);
			
			if (mod.getMode() == DefaultVisualModule.ALGORITHM_MODE)
				dragPaneAlg.add(label);
			else if (mod.getMode() == DefaultVisualModule.VISUALISATION_MODE)
				dragPaneVis.add(label);
		}
    	}
	
	public void run()
	{	
		HashMap allClasses;
		
		while (true)
		{
			if (mdiForm.getModuleVP() != null)
			{
				System.gc();
				
				mr.loadModules();
				allClasses = mr.getClasses();
				
				DrawingCanvas drawPane = mdiForm.getModuleVP().getDrawingCanvas();
				
				// Go through the modules and see if there are new ones
				
				Set set = allClasses.keySet();
				Iterator iter = set.iterator();
				DefaultVisualModule newMod;
				DefaultVisualModule exsitingMod;
				String classKey;
				
				while (iter.hasNext())
				{
					classKey = (String)iter.next();
					try
					{
						Class c = ((Class)allClasses.get(classKey));
						Class[] intArgsClass = new Class[] {mdiForm.getClass(), drawPane.getClass()};
						Constructor cons = c.getConstructor(intArgsClass);
						Object[] intArgs = new Object[] {mdiForm, drawPane};
						newMod = (DefaultVisualModule)cons.newInstance(intArgs);
						
						boolean bNewModule = true;
						
						for (int i = 0; i < mods.size(); i++)
						{
							exsitingMod = (DefaultVisualModule)mods.get(i);
							if (newMod.getName().equals(exsitingMod.getName()))
							{
								bNewModule = false;
								break;
							}
						}
						
						if (bNewModule)
						{	
							// New module. Add it to the set of existing ones
							
							mods.add(newMod);
							m.getModules().add(newMod);
							m.getClasses().put(classKey, c);
							
							// Add a new label to the toolbar
							
							DragLabel label;
							label = new DragLabel(newMod.getLabelCaption(), JLabel.CENTER);
							label.setToolTipText(newMod.getToolTipText());
							label.setName(newMod.getName());
							label.addMouseListener(dragLabelMouseListener);
							label.addMouseMotionListener(dragLabelMouseListener);
							
							if (newMod.getMode() == DefaultVisualModule.ALGORITHM_MODE)
							{	
								dragPaneAlg.add(label);
								dragPaneAlg.validate();
							}
							else if (newMod.getMode() == DefaultVisualModule.VISUALISATION_MODE)
							{
								dragPaneVis.add(label);
								dragPaneVis.validate();
							}
						}
					}
					catch (Exception e){}
				}
				
				try
				{
					dynaLoadThread.sleep(SLEEP_PERIOD);
				}
				catch (InterruptedException e){}
			}
		}
	}
	
	public void setEnabled(boolean enabled)
	{
		// Enable/disable the toolbar and all of the buttons on it.
		
		toolbar.setEnabled(false);
		
		int count;
		for (count = 0; count < numButtons; count++)
			toolbar.getComponentAtIndex(count).setEnabled(enabled);
		
	}
	
	/**
	* After dragging a module onto the drawing canvas, this method
	* is called to remove the highlight from the DragLabel on the
	* toolbar
	*/
	
	public void removeLabelHighlights()
	{
		for (int i = 0; i < dragPaneAlg.getComponentCount(); i++)
		{
			if (dragPaneAlg.getComponent(i) instanceof DragLabel)
				((DragLabel)dragPaneAlg.getComponent(i)).highlight(false);
		}
		
		for (int i = 0; i < dragPaneVis.getComponentCount(); i++)
		{
			if (dragPaneVis.getComponent(i) instanceof DragLabel)
				((DragLabel)dragPaneVis.getComponent(i)).highlight(false);
		}
	}
	
	/**
	* Accessor method to retrieve the reflection class
	*/
	
	public ModuleReflection getReflectionClass()
	{
		return m;
	}
}

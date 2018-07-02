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
 * DefaultVisualModule
 *
 * Class represents a visual module that can be dragged about on the screen.
 * A module represents a visualisation such as a scatter plot or an algorithm
 * such as a Spring Model.
 *
 *  @author Greg Ross
 */
package parent_gui;

import parent_gui.dataVolumeThresholding.HybridGenerator;

import java.util.ArrayList;

public class DefaultVisualModule extends VisualModule
{
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent drawing surface
	
	private static DrawingCanvas drawPane;
	
	// Determine whether the visual module represents either an algorithm
	// or a visualisation
	
	public static final int VISUALISATION_MODE = 0;	// Module appears in both alg and vis views
	public static final int ALGORITHM_MODE = 1;	// Module appears only in alg view
	private int mode = VISUALISATION_MODE;
	
	// Store the selection handler
	
	protected SelectionHandler selection;
	
	// Store references to ports on this module that support hybrid algorithm
	// generation, i.e., they wouldn't normally be connected but this instance
	// is a place holder for a hybrid algorithm in between the modules concerned
	
	// By default, ouput ports that convey data structures of type
	// ScriptModel.DATA_ITEM_COLLECTION, and input ports of type
	// ScriptModel.VECTOR are trigger ports. This is because when they are connected
	// to their respective counterparts, this is a signal to HIVE that it should
	// generate an algorithm to transform the data (impedance matching?)
	
	// This is set in ModulePort.setPortDataStructure()
	
	private ArrayList triggerPorts;
	
	// When importing a hybrid algorithm, HIVE must determine which modules
	// will be replaced by the trigger modules that the user has configured.
	// These are the anchor modules in the generated algorithm
	
	// e.g. a DataSource in the generated algorithm is a source anchor and
	// a scatterplot is a sink anchor
	
	// Note that we are assuming that the generated algorithm is constrained
	// to contain one anchor source and one anchor sink, no more, no less.
	
	private int anchorType = HybridGenerator.ANCHOR_NOT;
	
	// Tool-tip-text for the label that represents this module instance
	// on the toolbar
	
	private String toolTip;
	
	// Caption for the label on the toolbar
	
	private String labelCaption;
	
	public DefaultVisualModule(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		triggerPorts = new ArrayList();
	}
	
	/**
	* Accessor methods for the module's tool-tip-text and caption
	* on the toolbar label
	*/
	
	public String getToolTipText()
	{
		return toolTip;
	}
	
	public void setToolTipText(String toolTip)
	{
		this.toolTip = toolTip;
	}
	
	public String getLabelCaption()
	{
		return labelCaption;
	}
	
	public void setLabelCaption(String labelCaption)
	{
		this.labelCaption = labelCaption;
	}
	
	/**
	* Accessor methods for determining the type of anchor
	* (if the instance is part of an imported (generated)
	* hybrid algorithm).
	*/
	
	public int getAnchorType()
	{
		return anchorType;
	}
	
	public void setAnchorType(int anchorType)
	{
		this.anchorType = anchorType;
	}
	
	/**
	* Accessor method for getting the selction of a visualisation module
	*/
	
	public SelectionHandler getSelectionHandler()
	{
		return selection;
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
	
	/**
	* Accessor methods for determining whether the module is for
	* visualisation or algorithmic purposes
	*/
	
	public void setMode(int mode)
	{
		this.mode = mode;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	/**
	* Overriding this method allows modules to tighten the
	* constraints upon inter-port (module) connections
	*/
	
	public boolean allowPortConnection(ModulePort port)
	{
		return true;
	}
	
	/**
	* Accessor method for getting the array list of trigger ports
	*/
	
	public ArrayList getTriggerPorts()
	{
		return triggerPorts;
	}
	
	/**
	* This method allows modules to loosen the
	* constraints upon inter-port (module) connection so that
	* hybrid algorithm generation is facilitated. For example a data source
	* and a scatterplot would both cause this to return true
	*/
	
	public boolean allowForHybridLinking(ModulePort port)
	{
		if (triggerPorts.size() == 0)
		{
			return false;
		}
		else
		{
			if (triggerPorts.contains(port))
				return true;
			else
				return false;
		}
	}
}

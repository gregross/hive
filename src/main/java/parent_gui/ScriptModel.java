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
 * ScriptModel
 *
 * Class to impose constraints upon creating links between visual modules based
 * upon the type of ports and the data they are declared to convey.
 *
 *  @author Greg Ross
 */
package parent_gui;

import data.*;

import java.util.*;

public class ScriptModel
{	
	// Determine port types
	
	public static final int INPUT_PORT = 0;
	public static final int OUTPUT_PORT = 1;
	public static final int SELECTION_PORT = 2;
	public static final int TRIGGER_PORT_OUT = 3;
	public static final int TRIGGER_PORT_IN = 4;
	public static final int MULTI_PORT_IN = 5;
	
	// Determine data structure
	
	public static final int VECTOR = 0;
	public static final int DATA_ITEM_COLLECTION = 1;
	
	// Determine data types
	
	public final static int STRING  = 0;
	public final static int DATE    = 1;
	public final static int INTEGER = 2;
	public final static int DOUBLE  = 3;
	
	private static HashMap portDataTypes;
	
	// If a port is highlighted because a link is being dragged over it then
	// determine whether a valid link can be made
	
	public static final int VALID_LINK_HIGHLIGHT = 0;
	public static final int INVALID_LINK_HIGHLIGHT = 1;
	
	private static DrawingCanvas drawPane = null;
	
	public ScriptModel()
	{
		
	}
	
	public static boolean portsCompatible(ModulePort fromPort, ModulePort toPort)
	{
		// Given two ports that the user is trying to connect via a link,
		// determine whether this action is valid
		
		if (fromPort == null)
		{	
			return false;
		}
		
		drawPane = fromPort.getVisualModule().getParentForm().getDrawPane();
		
		if (!linkAlreadyExists(fromPort, toPort) 		// Is the link unique?
			&& dataIOModeCompatible(fromPort, toPort) 	// Are the Port I/O mode compatible (Polarity, fan-in...)?
			&& (dataStructureCompatible(fromPort, toPort)
			|| toPort.getPortMode() == 
			ScriptModel.MULTI_PORT_IN)			// Are data structures are compatible?
			&& dataTypesCompatible(fromPort, toPort) 	// Are data types are compatible?
			&& moduleAccept(fromPort, toPort) 		// Do the modules allow the connection?
			|| triggerHybrid(fromPort, toPort))		// Will the connection trigger hybrid algorithmic generation?
			
			return true;
		else
			return false;
	}
	
	private static boolean dataTypesCompatible(ModulePort fromPort, ModulePort toPort)
	{
		// Only link ports where the to-port can handle the data types
		// supplied by the from-port
		
		Set set = fromPort.getPortDataTypes().keySet();
		Iterator iter = set.iterator();
		Integer dataType = null;
		
		while (iter.hasNext())
		{
			dataType = (Integer)iter.next();
			if (!toPort.getPortDataTypes().containsKey(dataType))
				return false;
		}
		
		return true;
	}
	
	private static boolean dataStructureCompatible(ModulePort fromPort, ModulePort toPort)
	{
		// Only link ports that can handle the same data structure
		
		if (fromPort.getPortDataStructure() == toPort.getPortDataStructure())
			return true;
		else
			return false;
	}
	
	private static boolean linkAlreadyExists(ModulePort fromPort, ModulePort toPort)
	{
		// Don't allow the user to create a link if it already exists
		
		String linkKey1 = fromPort.getVisualModule().getKey() + "_" + 
			toPort.getVisualModule().getKey() + "_" + fromPort.getKey() + "_" + toPort.getKey();
		
		if((fromPort.getPortMode() == ScriptModel.SELECTION_PORT) && (toPort.getPortMode() == ScriptModel.SELECTION_PORT))
		{
			String linkKey2 = toPort.getVisualModule().getKey() + "_" + 
			fromPort.getVisualModule().getKey() + "_" + toPort.getKey() + "_" + fromPort.getKey();
			
			if ((drawPane.getLinks().containsKey(linkKey1)) || (drawPane.getLinks().containsKey(linkKey2)))
				return true;
			else
				return false;
		}
		else if (drawPane.getLinks().containsKey(linkKey1))
			return true;
		else
			return false;
	}
	
	private static boolean dataIOModeCompatible(ModulePort fromPort, ModulePort toPort)
	{
		// An input port cannot be connected to another input port
		// The same goes for output ports
		// However, ScriptModel.SELECTION_PORTs can only be connected to one another
		
		if((fromPort.getPortMode() == ScriptModel.SELECTION_PORT) && (toPort.getPortMode() == ScriptModel.SELECTION_PORT))
		{
			return true;
		}
		else if((fromPort.getPortMode() == ScriptModel.OUTPUT_PORT) && (toPort.getPortMode() == ScriptModel.MULTI_PORT_IN))
		    return true;
		else if (((fromPort.getPortMode() == ScriptModel.TRIGGER_PORT_IN) && 
		(toPort.getPortMode() == ScriptModel.TRIGGER_PORT_OUT)) ||
		((fromPort.getPortMode() == ScriptModel.TRIGGER_PORT_OUT) && 
		(toPort.getPortMode() == ScriptModel.TRIGGER_PORT_IN)))
		{
			// Trigger ports can only be connected to other trigger ports
			
			return true;
		}
		else if ((fromPort.getPortMode() == toPort.getPortMode()) || !portUnused(fromPort, toPort)
		|| (fromPort.getPortMode() == ScriptModel.SELECTION_PORT) || 
		(toPort.getPortMode() == ScriptModel.SELECTION_PORT) || isTriggerPort(fromPort) ||
		isTriggerPort(toPort))
			return false;
		else
			return true;
	}
	
	/**
	* Query the parent-modules to see whether they have imposed any extra constraints
	* on the potential link
	*/
	
	private static boolean moduleAccept(ModulePort fromPort, ModulePort toPort)
	{
		DefaultVisualModule fromMod = (DefaultVisualModule)fromPort.getVisualModule();
		DefaultVisualModule toMod = (DefaultVisualModule)toPort.getVisualModule();
		
		if (fromMod.allowPortConnection(toPort) && toMod.allowPortConnection(fromPort))
			return true;
		else
			return false;
	}
	
	private static boolean isTriggerPort(ModulePort port)
	{
		if ((port.getPortMode() == ScriptModel.TRIGGER_PORT_IN)	||
		(port.getPortMode() == ScriptModel.TRIGGER_PORT_OUT))
			return true;
		else
			return false;
	}
	
	private static boolean portUnused(ModulePort fromPort, ModulePort toPort)
	{
		// A link cannot be made if the input port has already been connected
		// to another output port
		
		Set set = drawPane.getLinks().keySet();
		Iterator iter = set.iterator();
		Link link = null;
		
		while (iter.hasNext())
		{
			link = (Link)drawPane.getLinks().get((String)iter.next());
			if (link.getToPort().getKey().equals(toPort.getKey()) &&
				link.getToPort().getVisualModule().getKey().equals(toPort.getVisualModule().getKey()))
				return false;
		}
		return true;
	}
	
	/**
	* If the two components to be connected will trigger the creation of a hybrid
	* algorithm, e.g. a scatterplot connected to a data source, then allow the
	* link
	*/
	
	public static boolean triggerHybrid(ModulePort fromPort, ModulePort toPort)
	{
		DefaultVisualModule fromMod = (DefaultVisualModule)fromPort.getVisualModule();
		DefaultVisualModule toMod = (DefaultVisualModule)toPort.getVisualModule();
		
		if  (fromMod.allowForHybridLinking(fromPort)
			&& toMod.allowForHybridLinking(toPort)
			&& dataIOModeCompatible(fromPort, toPort)) // The I/O rules still apply
			
			return true;
		else
			return false;
	}
	
	private static void initDataTypes()
	{
		portDataTypes.put(new Integer(STRING), new Integer(STRING));
		portDataTypes.put(new Integer(DATE), new Integer(DATE));
		portDataTypes.put(new Integer(INTEGER), new Integer(INTEGER));
		portDataTypes.put(new Integer(DOUBLE), new Integer(DOUBLE));
	}
	
	public static HashMap getDataTypes()
	{
		portDataTypes = new HashMap();
		initDataTypes();
		return	portDataTypes;
	}
}

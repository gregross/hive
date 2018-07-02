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
 * ModulePort
 *
 * Class defines an input or output port for a visual module
 *
 *  @author Greg Ross
 */
 
package parent_gui;

import data.*;

import java.awt.geom.*;
import java.awt.Point;
import java.util.*;

public class ModulePort extends Observable implements Observer, java.io.Serializable
{	
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Store a reference to the visual module of which this port is a part
	
	private VisualModule visMod;
	
	// Store a key to determine whether the port is input/output and which
	// number it is relative to the visual module
	
	private String key = null;
	
	// The mode determines whether this is an input or output port
	// "i" for input, "o" for output
	
	private String mode;
	
	// The port number represents the index of the array list (VisualModule.getInPorts()) that stores the 
	// painted ellipse representing this port
	
	private int portNumber;
	
	// Determine whether the port is highlighted because a link connected to it
	// is selected
	
	private boolean bLinkHighlight = false;
	
	private int portMode;
	
	// Make the data structure Vector by default
	
	private int portDataStructure = ScriptModel.VECTOR;
	
	// Set the default data types that a port can cater for
	
	private HashMap portDataTypes;
	
	// Is the port highlit because we're dragging a link over it;
	
	private int dragHighlight = -1;
	
	// The label that appears next to the port on the visual module
	
	private String portLabel = null;
	
	// The connected port(s) which this port observes
	
	private ArrayList observedPorts;
	
	// A collection of High-D CSV data
	
	DataItemCollection csvData = null;
	
	// An ArrayList collection, e.g. Low-D positions
	
	ArrayList arrayData;
	
	// Array to pass data along links between module ports
	
	private ArrayList transferData;
	
	// Collection to pass visual selection data between selection ports
	
	private Collection selectionData;
	
	// Determine whether the port is highlighted because it is compatible
	// with the currently selected port
	
	private boolean bCompatHighlight = false;
	
	// Determine whether this port is one that when connected to another
	// can trigger HIVE to create a hybrid algorithm
	
	private boolean hybridTrigger = false;
	
	public 	ModulePort(VisualModule visMod, int portMode, int num)
	{
		this.visMod = visMod;
		portNumber = num;
		this.portMode = portMode;
		
		// Determine whether the port is input or output
		
		if ((portMode == ScriptModel.INPUT_PORT) || 
			(portMode == ScriptModel.TRIGGER_PORT_IN)
			 || (portMode == ScriptModel.MULTI_PORT_IN))
			mode = "i";
		else
			mode = "o";
		
		// Determine the key which uniquely identifies this port within a visual
		// module
			
		key = mode + (new Integer(num)).toString();	
		
		// Make this port intially accept all of the available data types
		
		setDefaultTypes();
		
		// Initialise the ArrayList of ports connected to this one.
		// This port is an observer and/or observable of these
		
		observedPorts = new ArrayList();
	}
	
	public boolean getCompatHighlight()
	{
		return bCompatHighlight;
	}
	
	public void setCompatHighlight(boolean bHighlight)
	{
		bCompatHighlight = bHighlight;
	}
	
	private void setDefaultTypes()
	{
		// The default types that the port can handle are all
		// of the types offered by the ScriptModel
		
		portDataTypes = (HashMap)ScriptModel.getDataTypes().clone();
	}
	
	public VisualModule getVisualModule()
	{
		return visMod;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public int xPos()
	{
		// Return the x coordinate of the origin of the bounding box that
		// contains the painted port.
		
		Double x = null;
		double dx;
		double width;
		if (mode.equals("i"))
		{
			if (visMod.getInPorts().size() > 0)
			{
				dx = ((Ellipse2D.Double)visMod.getInPorts().get(portNumber)).getX();
				width = (((Ellipse2D.Double)visMod.getInPorts().get(portNumber)).getWidth() / 2d);
			}
			else
				return 0;
		}
		else
		{
			if (visMod.getOutPorts().size() > 0)
			{
				dx = ((Ellipse2D.Double)visMod.getOutPorts().get(portNumber)).getX();
				width = (((Ellipse2D.Double)visMod.getOutPorts().get(portNumber)).getWidth() / 2d);
			}
			else
				return 0;
		}
		x = new Double(dx + width);
		
		// Now translate the local coordinate to the global (containing) JLayeredPane Coordinate
		
		Point origin = visMod.getLocation();
		int originX = (new Double(origin.getX())).intValue();
		int iX = originX + x.intValue();
		
		return iX;
	}
	
	public int yPos()
	{
		// Return the x oordinate of the origin of the bounding box that
		// contains the painted port
		
		Double y = null;
		double dy;
		double height;
		if (mode.equals("i"))
		{
			if (visMod.getInPorts().size() > 0)
			{
				dy = ((Ellipse2D.Double)visMod.getInPorts().get(portNumber)).getY();
				height = (((Ellipse2D.Double)visMod.getInPorts().get(portNumber)).getHeight() / 2d);
			}
			else
				return 0;
		}
		else
		{
			if (visMod.getOutPorts().size() > 0)
			{
				dy = ((Ellipse2D.Double)visMod.getOutPorts().get(portNumber)).getY();
				height = (((Ellipse2D.Double)visMod.getOutPorts().get(portNumber)).getHeight() / 2d);
			}
			else
				return 0;
		}
		y = new Double(dy + height);
		
		// Now translate the local coordinate to the global (containing) JLayeredPane Coordinate
		
		Point origin = visMod.getLocation();
		int originY = (new Double(origin.getY())).intValue();
		int iY = originY + y.intValue();
		
		// If the module has been resized and the port is now conceptually off of
		// the top or bottom of the rendered module, then terminate the link at the top
		// or bottom respectively
		
		if (iY <= originY)
			iY = originY + 10;
		else if (iY >= (originY + visMod.getHeight()))
			iY = (originY + visMod.getHeight()) - 10;
		return iY;
	}
	
	public int getPortNumber()
	{
		return portNumber;
	}
	
	/**
	* Accessor methods for determineing whether this port
	* is highlighted because a link connected to it is selected
	*/
	
	public boolean getLinkHighlight()
	{
		return bLinkHighlight;
	}
	
	public void setLinkHighlight(boolean bHighlight)
	{
		bLinkHighlight = bHighlight;
	}
	
	public void setDragHighlight(int dragHighlight)
	{
		this.dragHighlight = dragHighlight;
	}
	
	public int getDragHighlight()
	{
		return dragHighlight;
	}
	
	/**
	* Accessor methods for determining the nature of a port
	*/
	
	public int getPortMode()
	{
		return portMode;
	}
	
	public int getPortDataStructure()
	{
		return portDataStructure;
	}
	
	public void setPortDataStructure(int dataStructure)
	{
		portDataStructure = dataStructure;
		
		// If the port is an output port and the data structure is
		// DATA_ITEM_COLLECTION, or the port is input and VECTOR then
		// flag these ports as allowing connections to the other
		// repective type. This is for facilitating hybrid algorithm
		// generation
		
		if (((dataStructure == ScriptModel.DATA_ITEM_COLLECTION) 
			&& (portMode == ScriptModel.OUTPUT_PORT))
			|| ((dataStructure == ScriptModel.VECTOR)
			&& (portMode == ScriptModel.INPUT_PORT)))
		{
			((DefaultVisualModule)visMod).getTriggerPorts().add(this);
			hybridTrigger = true;
		}
		else
			hybridTrigger = false;
	}
	
	/**
	* Accessor method for determining whether this port is one
	* that maybe connected to another and trigger HIVE in
	* generating a hybrid algorithm
	*/
	
	public boolean isHybridTrigger()
	{
		return hybridTrigger;
	}
	
	public HashMap getPortDataTypes()
	{
		return portDataTypes;
	}
	
	public void setPortDataTypes(HashMap dataTypes)
	{
		portDataTypes = (HashMap)dataTypes.clone();
	}
	
	/**
	* The private member variable 'key' above only uniquely
	* identifies a port within the ports of the parent VisualModule.
	* This sub provides each port with a unique key amongst all
	* of the ports across all visualModules
	*/
	
	public String getAbsoluteKey()
	{
		return (visMod.getKey() + key);
	}
	
	/**
	* Accessor method for the label next to the port on a visual module
	*/
	
	public void setPortLabel(String sText)
	{
		portLabel = sText;
	}
	
	public String getPortLabel()
	{
		return portLabel;
	}
	
	/**
	* Implementation method for the Observer MVC interface
	*/
	
	public void update(Observable o, Object arg)
	{
		// Notfy the parent VisualModule that something has changed
		
		visMod.update((ModulePort)o, this, (ArrayList)arg);
	}
	
	/**
	* When a link is created, this method is used to register
	* the 'out' port with this port as an Observable
	*
	* @param obsPort The port to be observed
	*/
	
	public void addObservablePort(ModulePort obsPort)
	{
		observedPorts.add(obsPort);
		obsPort.addObserver(this);
		
		// If a link between two ports is made whilst the 'out' port
		// holds data, send that data to the 'in' port
		
		if ((obsPort.getPortMode() == ScriptModel.OUTPUT_PORT) || (obsPort.getPortMode() == ScriptModel.SELECTION_PORT))
		{
			// The other port feeds this port
			
			if (obsPort.getData() != null)
			{
				// The other (out) port holds data
				// Set the data that this port holds equal to that data
				
				transferData = null;
				
				if (obsPort.getData().get(0) instanceof DataItemCollection)
					csvData = (DataItemCollection)obsPort.getData().get(0);
				
				if (obsPort.getData().size() > 1)
				{
					if (obsPort.getData().get(1) instanceof ArrayList)
						arrayData = (ArrayList)obsPort.getData().get(1);
					else if (obsPort.getData().get(1) instanceof Collection)
						selectionData = (Collection)obsPort.getData().get(1);
				}
				
				transferData = new ArrayList(obsPort.getData());
				
				// Notify this port's parent module that data has arrived
				
				visMod.update(obsPort, this, transferData);
			}
		}
	
	}
	
	/**
	* This method is used to send data from the parent visual module to
	* another visual module via the 'out' port connection
	*/
	
	public void sendData(ArrayList data)
	{
		csvData = null;
		arrayData = null;
		transferData = null;
		
		if (data != null)
		{
			if (data.get(0) instanceof DataItemCollection)
				csvData = (DataItemCollection)data.get(0);
				
			if (data.size() > 1)
				if (data.get(1) instanceof ArrayList)
					arrayData = (ArrayList)data.get(1);
				else if (data.get(1) instanceof Collection)
					selectionData = (Collection)data.get(1);
		}
		
		transferData = data;
		setChanged();
		notifyObservers(data);
	}
	
	/**
	* Retrieve all of the data associated with this port
	*/
	
	public ArrayList getData()
	{
		return transferData;
	}
	
	/**
	* Return the DataItemCollection held by this port (if it holds one)
	*/
	
	public DataItemCollection getDataItemCollection()
	{
		return csvData;
	}
	
	/**
	* When alink is deleted, this method is called to unregister the
	* the previously connected port
	*
	* @param delPort The connected port to be unregistered as an Observer/Observable
	*/
	
	public void removeObserver(ModulePort delPort)
	{
		for (int i = 0; i < observedPorts.size(); i++)
		{
			if (((ModulePort)observedPorts.get(i)).getAbsoluteKey().equals(delPort.getAbsoluteKey()))
			{
				delPort.deleteObserver(this);
				
				// Notify the 'in' port that the link has been removed
				
				if (delPort.getPortMode() == ScriptModel.INPUT_PORT)
					delPort.update(this, null);
				
				// Remove from the list of Observables
				
				observedPorts.remove(i);
				break;
			}
		}
	}
	
	public ArrayList getObservedPorts()
	{
		return observedPorts;
	}
	
	/**
	* Method to restore Observers
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException,
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		
		if (observedPorts != null)
		{
			for (int i = 0; i < observedPorts.size(); i++)
				((ModulePort)observedPorts.get(i)).addObserver(this);
		}
	}
}

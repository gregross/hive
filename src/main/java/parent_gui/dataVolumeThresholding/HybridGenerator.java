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
 * HybridGenerator
 *
 * Given a data set, this class generates a hybrid algorithm for
 * multidimensional scaling, tailored to those data
 *
 *  @author Greg Ross
 */
package parent_gui.dataVolumeThresholding;

import parent_gui.*;
import data.*;
import java.io.*;
import java.util.*;

public class HybridGenerator implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The deserialised hybrid algorithm stuff
	
	static SerialClass serialClass;
	
	// The ports of two linked visual modules that have triggered
	// the hybrid algorithm generation
	
	static ModulePort fromPort;
	static ModulePort toPort;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// Constants to determine the anchor modules and their types
	// (source or sink) in the generated algorithm
	
	public static final int ANCHOR_NOT = 0;
	public static final int ANCHOR_SOURCE = 1;
	public static final int ANCHOR_SINK = 2;
	
	// Anchor modules
	
	private static DefaultVisualModule anchorSourceModule;
	private static DefaultVisualModule anchorSinkModule;
	
	// A new set of modules that are imported as part of the
	// generated hybrid algorithm
	
	HashMap newModules;
	
	// The links that have been imported with the above modules
	
	HashMap newLinks;
	
	private static FeedbackForm feedbackForm;
	
	public HybridGenerator(){}
	
	/**
	* Generate the hybrid MDS algorithm
	*/
	
	public boolean generateAlgorithm(DataItemCollection dataItems, Mdi mdiForm, ModulePort fromPort, 
	ModulePort toPort, FeedbackForm feedbackForm)
	{
		this.feedbackForm = feedbackForm;
		
		HybridGenerator.mdiForm = mdiForm;
		
		HybridGenerator.fromPort = fromPort;
		HybridGenerator.toPort = toPort;
		
		int dimensionalityClass = dataItems.getDimensionalityClass();
		int cardinalityClass = dataItems.getCardinalityClass();
		
		// From the class of data, in terms of cardinality and dimensionality,
		// determine the type of algorithm to generate
		
		try
		{
			if ((cardinalityClass == DataVolumeThreshold.HIGH_CARDINALITY)
				&& (dimensionalityClass == DataVolumeThreshold.MOD_DIMENSIONALITY))
			{
				// M, H: sample-spring-interp-spring
				
				loadAlgorithm("patterns/pca.atb");
			}
			else if ((cardinalityClass == DataVolumeThreshold.MOD_CARDINALITY)
				&& (dimensionalityClass == DataVolumeThreshold.MOD_DIMENSIONALITY))
			{
				// M, M: kmeans-spring-interp-spring
				
				loadAlgorithm("patterns/sample_spring_interp.atb");
			}
			else if ((cardinalityClass == DataVolumeThreshold.LOW_CARDINALITY)
				&& (dimensionalityClass == DataVolumeThreshold.MOD_DIMENSIONALITY))
			{
				// M, L: spring
				
				loadAlgorithm("patterns/kmeans_spring_interp.atb");
			}
			else
			{
				// Default: random mapping
				
				loadAlgorithm("patterns/random_mapping.atb");
			}
			
			integrateAlgorithm();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	private void loadAlgorithm(String filePath) throws Exception
	{
		FileInputStream fis;
		ObjectInputStream inStream;
		
		// Create a stream for reading
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(filePath).getFile());
		fis = new FileInputStream(file);
		
		// Create an object that can read the stream
		
		inStream = new ObjectInputStream(fis);
		
		// Get the deserialised objects
		
		serialClass = (SerialClass)inStream.readObject();
	}
	
	/**
	* After a hybrid algorithm has been deserialised, this
	* method is used to integrate it with the user's existing
	* graph
	*/
	
	private void integrateAlgorithm()
	{
		newModules = serialClass.getModules();
		HashMap existingModules = mdiForm.getModules();
		
		newLinks = serialClass.getModuleVP_Links();
		HashMap existingLinks = mdiForm.getModvp_Links();
		
		reassignKeys();
		setParentForms(existingModules);
		
		feedbackForm.setText1("Creating components.");
		feedbackForm.setText2("Please wait...");
		
		addToExistingModules(existingModules);
		
		paintImportedPorts();
		
		unregisterAnchorPorts();
		
		feedbackForm.setText1("Creating new links.");
		feedbackForm.setText2("Please wait...");
		
		importNewLinks();
	}
	
	/**
	* When deserialising modules, call each modules paint method
	* so that transient painted ports are recreated
	*/
	
	private void paintImportedPorts()
	{
		Set set = newModules.keySet();
		Iterator iter = set.iterator();
		
		VisualModule mod;
		
		while (iter.hasNext())
		{
			mod = ((VisualModule)newModules.get((String)iter.next()));
			mod.paintComponent(mod.getGraphics());
		}
	}
	
	/**
	* When a hybrid algorithm is generated, we must apply new unique keys
	* to the imported modules. These must also be reflected in the imported link key
	*/
	
	private void reassignKeys()
	{
		Set set = newModules.keySet();
		Iterator iter = set.iterator();
		
		DefaultVisualModule newModule;
		String newKey = null, oldKey = null;
		
		boolean timeKeySet = false;
		long currentKey = 0;
		Long lKey;
		
		// HashMap to store modules after their keys have changed
		
		HashMap changedMods = new HashMap(newModules.size());
		
		while (iter.hasNext())
		{
			oldKey = (String)iter.next();
			newModule = (DefaultVisualModule)newModules.get(oldKey);
			
			// Create a new key. Only use the current time for the first key
			// just in case it's a very fast computer and two modules are
			// added at (almost) the same time
			
			if (!timeKeySet)
			{
				lKey = new Long(System.currentTimeMillis());
				newKey = lKey.toString();
				currentKey = lKey.longValue();
				timeKeySet = true;
			}
			else
			{
				currentKey++;
				lKey = new Long(currentKey);
				newKey = lKey.toString();
			}
			
			// Change the module's key to be the new one and
			// add to the array list
			
			newModule.setKey(newKey);
			changedMods.put(oldKey, newModule);
		}
		
		// Remove the components from newModules and add them
		// again with the new key
		
		set = changedMods.keySet();
		iter = set.iterator();
		
		while (iter.hasNext())
		{
			oldKey = (String)iter.next();
			newModule = (DefaultVisualModule)newModules.get(oldKey);
			newModules.remove(oldKey);
			
			newModules.put(newModule.getKey(), newModule);
			
			// Reflect the key change in all connecting links
			
			reassignLinkKeys(newModule, oldKey, newModule.getKey());
		}
	}
	
	/**
	* When a new module is assigned a new unique key, all imported
	* links to that module must also have their keys updated
	*/
	
	private void reassignLinkKeys(DefaultVisualModule newModule, String oldKey, String newKey)
	{
		Set set = newLinks.keySet();
		Iterator iter = set.iterator();
		
		// An imported link
		
		Link newLink;
		
		String newLinkKey = null, oldLinkKey = null;
		
		boolean bContinue;
		
		// HashMap to store links after their keys have changed
		
		HashMap changedLinks = new HashMap();
		
		while (iter.hasNext())
		{
			bContinue = false;
			oldLinkKey = (String)iter.next();
			newLink = ((Link)newLinks.get(oldLinkKey));
			
			// Find links that concern the newModule
			
			if (newLink.getFromModule().getKey().equals(newKey))
			{
				newLinkKey = newKey + "_" + newLink.getToModule().getKey() +
				"_" + newLink.getFromPort() + "_" + newLink.getToPort();
				
				bContinue = true;
			}
			else if (newLink.getToModule().getKey().equals(newKey))
			{
				newLinkKey = newLink.getFromModule().getKey() + "_" + newKey +
				"_" + newLink.getFromPort() + "_" + newLink.getToPort();
				
				bContinue = true;
			}
			
			if (bContinue)
			{
				newLink.setKey(newLinkKey);
				changedLinks.put(oldLinkKey, newLink);
			}
		}
		
		// Remove the old link from newLinks and add it again
		// with the new key
		
		set = changedLinks.keySet();
		iter = set.iterator();
		
		while (iter.hasNext())
		{
			oldLinkKey = (String)iter.next();
			newLink = (Link)newLinks.get(oldLinkKey);
			newLinks.remove(oldLinkKey);
			newLinks.put(newLink.getKey(), newLink);
		}
	}
	
	/**
	* Iterate through the new set of modules and set their
	* parent forms to the current moduleVP
	*/
	
	private void setParentForms(HashMap existingModules)
	{
		// First get one of the existing modules to retrieve its parent form
		
		Set set = existingModules.keySet();
		Iterator iter = set.iterator();
		VisualModule currentMod = (VisualModule)(existingModules.get((String)iter.next()));
		
		set = newModules.keySet();
		iter = set.iterator();
		
		while (iter.hasNext())
		{
			((VisualModule)newModules.get((String)iter.next())).setParentForm(currentMod.getParentForm());
		}
	}
	
	/**
	* Now add all of the new modules to the existing set
	* with the exception of those that are anchors, i.e.
	* The ones that will be replaced by the two the user used
	* to trigger the algorithmic generation
	*/
	
	private void addToExistingModules(HashMap existingModules)
	{
		Set set = newModules.keySet();
		Iterator iter = set.iterator();
		
		DefaultVisualModule newModule;
		VisualModule anchorFrom = fromPort.getVisualModule();
		VisualModule anchorTo = toPort.getVisualModule();
		
		boolean anchorSourceOmitted = false;
		boolean anchorSinkOmitted = false;
		boolean bContinue;
		String newKey;
		Integer iKey;
		
		while (iter.hasNext())
		{
			bContinue = true;
			newKey = (String)iter.next();
			
			newModule = ((DefaultVisualModule)(newModules.get(newKey)));
			
			if ((newModule.getAnchorType() == ANCHOR_SOURCE)
				&& (!anchorSourceOmitted))
			{
				anchorSourceModule = newModule;
				anchorSourceOmitted = true;
				bContinue = false;
			}
			
			if ((newModule.getAnchorType() == ANCHOR_SINK)
				&& (!anchorSinkOmitted))
			{
				anchorSinkModule = newModule;
				anchorSinkOmitted = true;
				bContinue = false;
			}
			
			if (bContinue)
			{
				existingModules.put(newKey, newModule);
				mdiForm.getModuleVP().getDrawingCanvas().add(newModule);
			}
		}
	}
	
	/**
	* After removing the anchor modules, we must unregister their ports
	* with the remaining module ports
	*/
	
	private void unregisterAnchorPorts()
	{
		// Iterate through the set of newly imported modules
		
		Set set = newModules.keySet();
		Iterator iter = set.iterator();
		
		String sKey;
		
		DefaultVisualModule newModule;
		
		// The ports that are on the newly imported (non-anchor) modules
		
		HashMap ports;
		
		ModulePort port;
		
		// The ports connected to the above ports
		
		ArrayList observedPorts;
		ModulePort observedPort;
		
		while (iter.hasNext())
		{
			sKey = (String)iter.next();
			
			if ((!sKey.equals(anchorSourceModule.getKey())) && (!sKey.equals(anchorSinkModule.getKey())))
			{
				newModule = (DefaultVisualModule)newModules.get(sKey);
				
				// Check each port's registered listener ports to see if
				// they were on one of the anchor modules. If so, then 
				// unregister them
				
				ports = newModule.getPorts();
				Set portSet = ports.keySet();
				Iterator portIter = portSet.iterator();
				
				while (portIter.hasNext())
				{
					port = (ModulePort)ports.get((String)portIter.next());
					observedPorts = port.getObservedPorts();
					
					for (int j = 0; j < observedPorts.size(); j++)
					{
						observedPort = ((ModulePort)observedPorts.get(j));
						
						if (observedPort.getVisualModule().getKey().equals(
							anchorSourceModule.getKey()) ||
							observedPort.getVisualModule().getKey().equals(
							anchorSinkModule.getKey())
						)
						{
							port.removeObserver(observedPort);
						}
					}
				}
			}
		}
	}
	
	/**
	* Append all new links apart from those to the anchor modules
	* to the existing links collection.
	*/
	
	private void importNewLinks()
	{
		HashMap newLinks = serialClass.getModuleVP_Links();
		HashMap existingLinks = mdiForm.getModvp_Links();
		
		// Copy across links that are not connected to anchor modules
		// in the generated hybrid algorithm
		
		Set set = newLinks.keySet();
		Iterator iter = set.iterator();
		
		// An imported link
		
		Link newLink;
		
		// Keys of imported modules associated by the new link
		
		String newLink_To_Module_Key;
		String newLink_From_Module_Key;
		
		// Keys of the anchor modules in the generated algorithm
		
		String AnchorSourceKey = anchorSourceModule.getKey();
		String AnchorSinkKey = anchorSinkModule.getKey();
		
		boolean bContinue;
		
		// Store references to links that are to be supplanted
		
		ArrayList supplantedSourceLinks = new ArrayList();
		ArrayList supplantedSinkLinks = new ArrayList();
		
		while (iter.hasNext())
		{
			bContinue = true;
			
			newLink = ((Link)newLinks.get((String)iter.next()));
			
			newLink_From_Module_Key = newLink.getFromModule().getKey();
			newLink_To_Module_Key = newLink.getToModule().getKey();
			
			if (AnchorSourceKey.equals(newLink_From_Module_Key)
				|| (AnchorSourceKey.equals(newLink_To_Module_Key)))
			{
				bContinue = false;
				supplantedSourceLinks.add(newLink);
			}
			if (AnchorSinkKey.equals(newLink_From_Module_Key)
				|| (AnchorSinkKey.equals(newLink_To_Module_Key)))
			{
				bContinue = false;
				supplantedSinkLinks.add(newLink);
			}
			
			if (bContinue)
				existingLinks.put(newLink.getKey(), newLink);
		}
		
		// Create the links that will join the user's trigger modules
		// to the generated hybrid algorithm
		
		hidePlaceHolderLink();
		
		createNewLinks(supplantedSourceLinks, supplantedSinkLinks);
	}
	
	/**
	* Create new links from the user's trigger modules to the hybrid
	* algorithm
	*/
	
	private void createNewLinks(ArrayList supplantedSourceLinks, ArrayList supplantedSinkLinks)
	{
		Link supplantedLink;
		
		// Do the source links.
		
		for (int i = 0; i < supplantedSourceLinks.size(); i++)
		{
			supplantedLink = (Link)supplantedSourceLinks.get(i);
			
			mdiForm.getModuleVP().getDrawingCanvas().addLink(fromPort.getVisualModule(), supplantedLink.getToModule(),
				fromPort, supplantedLink.getToPort());
		}
		
		// Do the sink links.
		
		for (int i = 0; i < supplantedSinkLinks.size(); i++)
		{
			supplantedLink = (Link)supplantedSinkLinks.get(i);
			
			mdiForm.getModuleVP().getDrawingCanvas().addLink(supplantedLink.getFromModule(), toPort.getVisualModule(),
				supplantedLink.getFromPort(), toPort);
		}
		
		// Make the links visible
		
		mdiForm.getModuleVP().getDrawingCanvas().repaint();
	}
	
	private void hidePlaceHolderLink()
	{
		// Remove the placeholder link
		
		String PlaceHolderLinkKey;
		PlaceHolderLinkKey = fromPort.getVisualModule().getKey() + "_" + toPort.getVisualModule().getKey() + "_" +
			fromPort.getKey() + "_" + toPort.getKey();
		
		if (mdiForm.getModvp_Links().containsKey(PlaceHolderLinkKey))
			mdiForm.getModvp_Links().remove(PlaceHolderLinkKey);
		
		// Remove the inter-port references for the two trigger modules
		// First unregister the toPort from the fromPort
		
		ArrayList observedPorts;
		observedPorts = fromPort.getObservedPorts();
		ModulePort observedPort;
		
		for (int j = 0; j < observedPorts.size(); j++)
		{
			observedPort = ((ModulePort)observedPorts.get(j));
			
			if (observedPort == toPort)
			{
				fromPort.removeObserver(observedPort);
			}
		}
		
		// Now unregister the fromPort from the toPort
		
		observedPorts = toPort.getObservedPorts();
		
		for (int i = 0; i < observedPorts.size(); i++)
		{
			observedPort = ((ModulePort)observedPorts.get(i));
			
			if (observedPort == fromPort)
			{
				toPort.removeObserver(observedPort);
			}
		}
	}
}

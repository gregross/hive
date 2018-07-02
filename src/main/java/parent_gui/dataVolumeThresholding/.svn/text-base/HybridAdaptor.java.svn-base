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
 * HybridAdaptor
 *
 * This class is extended by visual modules such as the scatterplot
 * to generate a hybrid algorithm. Upon receiving a DataItemCollection,
 * the module creates (if appropriate) an algorithm to process them
 * and feed the output to it
 *
 *  @author Greg Ross
 */
package parent_gui.dataVolumeThresholding;

import parent_gui.*;
import data.*;

import java.util.ArrayList;
import java.lang.Thread;

public class HybridAdaptor extends DefaultVisualModule implements Runnable, java.io.Serializable
{	
	private static Mdi mdiForm;
	
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Determine whether a hybrid algorithm has been generated and is place
	
	private boolean hybridGenerated = false;
	
	// The data items that have been passed to the inheriting
	// module in the update method
	
	private DataItemCollection dataItems = null;
	
	// Reference to the HybridGenerator class is one is required
	
	protected HybridGenerator hybridGenerator;
	
	// Thread in which the algorithm generation process will run
	
	private static Thread thread;
	
	// The ports that trigger the hybrid generation
	
	private ModulePort fromPort;
	private ModulePort toPort;
	
	// The feedback form displayed to the user while HIVE generates
	// a hybrid MDS algorithm
	
	private static FeedbackForm feedbackForm;
	
	// The following is set to true while the hybrid algorithm is
	// being created. This is to stop the first clause of hybridInPlace()
	// from returning true while the algorithm is not yet complete
	
	private boolean bProcessing = false;
	
	public HybridAdaptor(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		this.mdiForm = mdiForm;
	}
	
	/**
	* This method is called from the update method of the
	* inheriting subclass to determine whether a hybrid algorithm
	* has been created (by user) or generated (by HIVE) to give the
	* correct input
	*/
	
	public boolean hybridInPlace(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		// If the module is getting valid data, i.e. from the output
		// of some algorithm then return true
		
		if (!fromPort.isHybridTrigger())
			return !bProcessing;
		else if (arg.get(0) instanceof DataItemCollection)
		{	
			// If the user has not attached the module to a valid output
			// of an algorithm then check to see whether HIVE has generated one
			// If HIVE has not yet generated an appropriate algorithm then initiate
			// that here. This scenario could arise when a user connects a scatterplot
			// directly to a data source
			
			if ((dataItems == (DataItemCollection)arg.get(0)) && (!fromPort.isHybridTrigger()))
			{
				return hybridGenerated;
			}
			else
			{
				// Data has changed, thus initiate hybrid algorithm generation process here
				
				dataItems = (DataItemCollection)arg.get(0);
				this.fromPort = fromPort;
				this.toPort = toPort;
				
				hybridGenerator = new HybridGenerator();
				
				// start thread for creating the algorithm
				
				bProcessing = true; // Stop this method from returning true in the first 'if' clause (above)
				
				// Get an instance of the feedback for before starting the new thread of
				// execution. But do not show it yet
				
				feedbackForm = FeedbackForm.getInstance("HIVE", mdiForm);
				
				thread = new Thread(this);
				thread.start();
				
				// Now show the feedback form
				
				feedbackForm.showForm();
				
				bProcessing = false; // Allow this method to return true from the first 'if' clause (above)
				
				return false;
			}
		}
		else
		{
			return hybridGenerated;
		}
	}
	
	/**
	* The hybrid algorithm generation process should be run in its own
	* thread so that it's easier to allow the user to cancel the operation
	* and to provide feedback
	*/
	
	public void run()
	{
		hybridGenerated = hybridGenerator.generateAlgorithm(dataItems, mdiForm, fromPort, toPort, feedbackForm);
		
		// Close the feedback form
		
		feedbackForm.hide();
		feedbackForm.dispose();
		
		thread = null;
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

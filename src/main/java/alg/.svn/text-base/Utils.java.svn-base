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
 * Utils
 * 
 * Provides some static methods which are used commonly throughout the 
 * package
 *
 * @author Andrew Didsbury, Greg Ross
 */

package alg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Utils
{
    /**
     * Creates a random List of integers, all different, with maximum 
     * value max and with size elements, with no elements from exclude, and 
     * only elements from include.  include U exclude should be the empty set
     *
     * @param include Values that should be in the random sample
     * @param exclude Values that should not be in the random sample
     * @param size    The number of elements
     * @param max     The maximum value for a random number
     * @return        The arraylist of random values
     */
     
    public static ArrayList createRandomSample(HashSet include, 
					       HashSet exclude, 
					       int max, int size)
    {
	// if either of the arrayLists where null (not needed) instantiate
	// them to empty lists for the loop
	
	if (include == null) 
	{
	    include = new HashSet();
	}
	if (exclude == null) 
	{
	    exclude = new HashSet();
	}
	
	HashSet randList = new HashSet();
	Random rnd = new Random(System.currentTimeMillis());
	
	for ( int i = 0 ; i < size ; i++ ) 
	{
	    Integer rand = new Integer((int)(rnd.nextDouble() * (double)max));
	    
	    while(randList.contains(rand) ||  
		   (exclude.size() > 0 && exclude.contains(rand)) || 
		   (include.size() > 0 && !include.contains(rand))) 
	    {
		rand = new Integer((int)(rnd.nextDouble() * (double)max));
	    }
	    
	    randList.add(rand);
	}
	
	ArrayList returnList = new ArrayList(randList);
	returnList.trimToSize();
	return returnList; 
    }

}

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
 * DataVolumeThreshold
 *
 * This singleton class is used to provide the rest of HIVE
 * with LMH data volume threshold values for classifiying data
 * and automatically generating hybrid algorithmic networks
 *
 *  @author Greg Ross
 */
package parent_gui.dataVolumeThresholding;

import parent_gui.*;

import java.util.Properties;

public class DataVolumeThreshold
{
	// Path of the properties file
	
	public static final String PROP_FILE = "properties/DataStates";
	
	// The LMH values for cardinality and dimensionality are stored
	// in a properties file
	
	private static Properties properties;
	
	// Member variables for the LMH values
	
	private static int lowCard = 0;
	private static int highCard = 0;
	private static int lowDim = 0;
	private static int highDim = 0;
	
	// Constants for classifying DataItemCollections
	
	public static final int LOW_CARDINALITY = 0;
	public static final int MOD_CARDINALITY = 1;
	public static final int HIGH_CARDINALITY = 2;
	
	public static final int LOW_DIMENSIONALITY = 3;
	public static final int MOD_DIMENSIONALITY = 4;
	public static final int HIGH_DIMENSIONALITY = 5;
	
	public DataVolumeThreshold()
	{
		// Load the threshold properties from file
		
		loadProperties();
	}
	
	/**
	* Initialises the properties for this object to be the properties held 
	* for this object
	*
	*/
	public static void loadProperties()
	{
		properties = new Properties();
		
		try 
		{
            PropertiesHandler propHandler = new PropertiesHandler(PROP_FILE);
			properties.putAll(propHandler.getProperties());
		}
		catch (NoPropertiesException npe)
		{
			System.err.println("couldn't load the properties for Data States");
		}
		
		initThresholdValues();
	}
	
	private static void initThresholdValues()
	{
		lowDim        	= Integer.parseInt(properties.getProperty("dim_Low"));
		highDim         = Integer.parseInt(properties.getProperty("dim_High"));
		lowCard    	= Integer.parseInt(properties.getProperty("card_Low"));
		highCard   	= Integer.parseInt(properties.getProperty("card_High"));
	}
	
	// Accessor methods for cardinality threshold values
	
	public static int getLowCardinality()
	{
		return lowCard;
	}
	
	public static void setLowCardinality(int lowC)
	{
		lowCard = lowC;
	}
	
	public static int getHighCardinality()
	{
		return highCard;
	}
	
	public static void setHighCardinality(int highC)
	{
		highCard = highC;
	}
	
	// Accessor methods for dimensionality threshold values
	
	public static int getLowDimensionality()
	{
		return lowDim;
	}
	
	public static void setLowDimensionality(int lowD)
	{
		lowDim = lowD;
	}
	
	public static int getHighDimensionality()
	{
		return highDim;
	}
	
	public static void setHighDimensionality(int highD)
	{
		highDim = highD;
	}
	
	/**
	* Given a value for cardinality, this method returns the
	* appropriate class
	*/
	
	public static int getCardinalityClass(int cardIn)
	{
		if (cardIn < lowCard)
			return LOW_CARDINALITY;
		else if ((cardIn >= lowCard) && (cardIn <= highCard))
			return MOD_CARDINALITY;
		else
			return HIGH_CARDINALITY;
	}
	
	/**
	* Given a value for dimensionality, this method returns the
	* appropriate class
	*/
	
	public static int getDimensionalityClass(int dimIn)
	{
		if (dimIn < lowDim)
			return LOW_DIMENSIONALITY;
		else if ((dimIn >= lowDim) && (dimIn <= highDim))
			return MOD_DIMENSIONALITY;
		else
			return HIGH_DIMENSIONALITY;
	}
}

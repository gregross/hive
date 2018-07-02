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
* Algorithmic test bed
*
* class to contain the collection of data items which this
* package is visualising.
* 
* @author Andrew Didsbury, Greg Ross
*/

package data;

import math.*;
import parent_gui.dataVolumeThresholding.DataVolumeThreshold;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.io.Serializable;

public class DataItemCollection implements Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private ArrayList types;
	private ArrayList fields;
	private ArrayList dataItems;
	private ArrayList columns;
	private ArrayList maximums;
	private ArrayList minimums;
	private int       size = 0;
	private boolean   isNormalized;
	private boolean   normalising=false;
	
	private boolean useSamples = true;
	
	// layout bounds, used for normalizing desired dists
	
	private double layoutBounds;
	private double mean; 
	private double sig;
	
	private ArrayList numericDim; //booleans
	private int numNumericDims;
	private ArrayList numericDimNumbers; // number of dimension. dDN[2] is num of 2nd numeric dim
    
	// a 2d array of doubles for the desired distances between objects
	
	private double[][] desiredDist;
	
	// used for standard deviation normalization
	
	private double[]  sumOfVals;
	private double[]  sumOfSquares;
	private double[]  average;
	private double[]  sigma;
	private double    sumHDVals;
        private double    sumHDSquares;
        private double    maxDist;
        private double    minDist;
	private double    unrelatedDist;
	
	// Used to scale ordinal values which don't match in sim metric
	
	private final double ORD_FACTOR    = 0.75;
	
	// The number of standard deviations to use when normalizing data
	
	protected final double STANDARD_DEVS = 2.0;
	
	// Constants used to determine the variable types
	
	public final static int STRING  = 0;
	public final static int DATE    = 1;
	public final static int INTEGER = 2;
	public final static int DOUBLE  = 3;
	
	public final static String DEFAULT_UNDEFINED   = "NaN";
	
	// Variables for classifying the data set according to
	// cardinality and dimensionality
	
	private int cardinality;
	private int dimensionality;
	
	// Determine whether the data set holds a text corpus. If this
	// is the case then hold the DataItems as doubles not 'Doubles'
	
	boolean bTextCorpus = false;
	private String indexPath = "";
	
	// If the data represent a set of genetic sequences, then the follwing
	// is set to true
	
	private boolean bSequenceData = false;
	
	// Store the path of the data set. If the data are from a text collection then this
	// is the same as indexPath above
	
	private String dataPath = "";
	
	// Determine whether this DataItemCollection represents a transposed data set
	
	private boolean transposed = false;
	
	// If the data has a set of row labels (or if it has been transposed)
	// store them in the following array
	
	private ArrayList rowLabels = null;
	
	// Is the data binary?
	
	private boolean bBinary = false;
	
	// The following variable is true if the data set consists only of integers
	// and/or doubles and has been normalised to the interval [0, 1]
	
	private boolean bNormalised = false;
	
	// If we do indeed normalise as above then all types are converted to double;
	// All minimums are set to 0, and maximums to 1
	// we thus need to keep a copy of the old arrays for de-normalising
	
	private ArrayList oldTypes = null;
	private ArrayList oldMaximums = null;
	private ArrayList oldMinimums = null;
	private ArrayList oldColumns;
	private double[] sumNormVals;
	private double[] sumNormSquares;
	private ArrayList oldDataItems = null;
	
	// If the data are binary, then store the frequency of each variable
	
	private int[] binaryFreq;
	private int maxFreq = 0;
	private int minFreq = Integer.MAX_VALUE;
	
	//...also, store the number of binary variables for each items
	
	private int[] binaryNumVars;
	private int maxNumVars = 0;
	private int minNumVars = Integer.MAX_VALUE;
	
	// If the data is from a lower triangular matrix then the following
	// field is set to true.
	
	private boolean bLowerTriangular = false;
	
	// Array to store the pairwise values for the lower triangular matrix.
	// Values are store in lower-packed storage mode.
	
	private double[] triangle_lower_Array;
	
	/**
	* Constructor
	*/
	public DataItemCollection()
	{
		dataItems = new ArrayList();
		types     = new ArrayList();
		fields    = new ArrayList();
		maximums  = new ArrayList();
		minimums  = new ArrayList();
		columns   = new ArrayList();
		numericDim = new ArrayList();
		numericDimNumbers = new ArrayList();
	}
	
	/**
	* adds a DataItem to this collection of dataItems
	* @param data The data item to be added
	*/
	public void addItem(DataItem data)
	{
		this.bTextCorpus = false;
		dataItems.add(data);
		size++;
		
		if (!bLowerTriangular)
			if (data.getTextValues() == null)
			{
				Object[] vals = data.getValues();
				add(vals);
			}
			else
			{
				this.bTextCorpus = true;
				add(data.getTextValues());
			}
	}
	
	public void addItem(DataItem data, boolean bTextCorpus)
	{
		this.bTextCorpus = true;
		dataItems.add(data);
		size++;
		add(data.getTextValues());
	}
	
	/**
	* adds a DataItem to this collection of dataItems
	* at the specified index
	* @param data The data item to be added
	*/
	public void addItem(int index, DataItem data)
	{
		this.bTextCorpus = false;
		dataItems.add(index, data);
		size++;
		Object[] vals = data.getValues();
		add(vals);
	}
	
	/**
	* If the data set is from a text corpus then the following method should return true
	*/
	
	public boolean isTextCorpus()
	{
		return bTextCorpus;
	}
	
	/**
	* Determine whether the data represent genetic sequences
	*/
	
	public boolean isSequenceData()
	{
		return bSequenceData;
	}
	
	public void setSequenceData(boolean bSequenceData)
	{
		this.bSequenceData = bSequenceData;
	}
	
	/**
	* If the data set is from a text corpus the following two methods access its location
	*/
	
	public void setIndexPath(String indexPath)
	{
		this.indexPath = indexPath;
		dataPath = indexPath;
	}
	
	public String getIndexPath()
	{
		return indexPath;
	}
	
	/**
	* Access methods for the path of the data file. If the data are text then this is the
	* same as indexPath
	*/
	
	public String getDataPath()
	{
		return dataPath;
	}
	
	public void setDataPath(String dataPath)
	{
		this.dataPath = dataPath;
	}
	
	/**
	* The following is called if the data are from a text corpus
	*/
	
	private void add(double[] vals)
	{
		for ( int i = 0 ; i < types.size() ; i++ )
		{
			// also need to do some work in here to find max and mins
			
			if (vals.length > 0)
			{
				if (vals[i] > ((Double)maximums.get(i)).doubleValue())
					maximums.set(i, new Double(vals[i]));
				
				if (vals[i] < ((Double)minimums.get(i)).doubleValue())
					minimums.set(i, new Double(vals[i]));
			}
		}
	}
	
	/**
	* The following is called if the data are CSV
	*/
	
	private void add(Object[] vals)
	{
		for ( int i = 0 ; i < types.size() ; i++ )
		{
			//add the data to columns
			
			((ArrayList)columns.get(i)).add(vals[i]);
			
			// also need to do some work in here to find max and mins
			
			if ( vals[i] != null )
			{
				switch(((Integer)types.get(i)).intValue())
				{
					case STRING: // STRING
						break;
					case DATE: // DATE
						if (((Date)vals[i]).after((Date)maximums.get(i)))
							maximums.set(i, vals[i]);
						if (((Date)vals[i]).before((Date)minimums.get(i)))
							minimums.set(i, vals[i]);
						break;
					case DOUBLE: // DOUBLE
						if (((Double)vals[i]).compareTo((Double)maximums.get(i)) > 0 )
							maximums.set(i, vals[i]);
						if ( ((Double)vals[i]).compareTo((Double)minimums.get(i)) < 0 )
							minimums.set(i, vals[i]);
						break;
					case INTEGER: // INTEGER
						if (((Integer)vals[i]).compareTo((Integer)maximums.get(i)) > 0)
							maximums.set(i, vals[i]);
						if (((Integer)vals[i]).compareTo((Integer)minimums.get(i)) < 0)
							minimums.set(i, vals[i]);
						break;
					default:
				}
			}
		}
	}
	
	/**
	* Sets the types that this collection represents to be types
	* 
	* @param types The collection of types to be used
	*/
	public void setTypes(ArrayList types)
	{
		this.types = types;
		
		numNumericDims=0;
		//init maxs and mins
		
		for ( int i = 0 ; i < types.size() ; i++ )
		{
			//init the columns too
			
			columns.add(new ArrayList());
			
			switch(((Integer)types.get(i)).intValue())
			{
				case STRING: // STRING
					maximums.add(null);
					minimums.add(null);
					numericDim.add(new Boolean(false));
					break;
				case DATE: // DATE
					maximums.add(new Date(0));
					minimums.add(new Date(Long.MAX_VALUE));
					numericDim.add(new Boolean(false));
					break;
				case DOUBLE: // DOUBLE
					maximums.add(new Double(Double.NEGATIVE_INFINITY));
					minimums.add(new Double(Double.MAX_VALUE));
					numericDim.add(new Boolean(true));
					numericDimNumbers.add(new Integer(i));
					numNumericDims++;
					break;
				case INTEGER: // INTEGER
					maximums.add(new Integer(Integer.MIN_VALUE));
					minimums.add(new Integer(Integer.MAX_VALUE));
					numericDim.add(new Boolean(true));
					numericDimNumbers.add(new Integer(i));
					numNumericDims++;
					break;
				default:
			}
		}
	}
	
	
	public int getNumNumericDims(){
		return numNumericDims;
	}
	
	public int getNumericDimNumber(int i){
		//i = numeric dim number, returns actual dim number 
		return ((Integer)numericDimNumbers.get(i)).intValue();
	    }

	    public ArrayList getNumericDimNumbers(){
		return numericDimNumbers;
	    }
	
	    public ArrayList getNumericDims(){
		return numericDim;
	    }
	    
	
	    public void setNumericDims(ArrayList a){
		numericDim =a;
	    }
	
	    public void setNumericDimNumbers(ArrayList a){
		numericDimNumbers = a;
	    }
	    
	    
	/**
	* Returns the arrayList of types that this class contains
	*
	* @return The arrayList of types that this class contains
	*/
	public ArrayList getTypes()
	{
		return types;
	}
	
	/**
	* Sets the fields that this collection represents to be fields
	*
	* @param fields The collection of fields to be used
	*/
	public void setFields(ArrayList fields)
	{
		this.fields = fields;
	}
	
	/**
	* Returns the names of the fields that this collection contains
	*
	* @return The arrayList of fields
	*/
	public ArrayList getFields()
	{
		return fields;
	}
	
	/**
	* Sets the values used for data normalization, the arrays contain values 
	* for each column in the data set, which contains numeric data.  the sum 
	* of all values and sum of squares of values respectively.
	*
	* @param sumOfVals The sum of all values in each column
	* @param sumOfSquares The sum of squares of all values in each column
	*/
	public void setNormalizeData(double[] sumOfVals, double[] sumOfSquares)
	{
		this.sumOfVals    = sumOfVals;
		this.sumOfSquares = sumOfSquares;
		
		// calculate the sigma & average values that will be needed in 
		// optimisation
		
		for (int i = 0 ; i < sumOfVals.length ; i++ )
		{
			average[i] = sumOfVals[i]/getSize();
			sigma[i] = Math.sqrt((sumOfSquares[i] - ((double)getSize() * average[i] * average[i]))/(double)getSize());
		}
	}
	
	/**
	* Initialises the arrays used for data normalization, the arrays contain values 
	* for each column in the data set, which contains numeric data.  the sum 
	* of all values and sum of squares of values respectively.
	*
	* @param sumOfVals The sum of all values in each column
	* @param sumOfSquares The sum of squares of all values in each column
	*/
	
	public void intiNormalArrays(double[] sumOfVals, double[] sumOfSquares)
	{
		average = new double[sumOfVals.length];
		sigma   = new double[sumOfVals.length];
	}
	
	/**
	* Returns the desired distance between these two objects.  Normalisation version
	*
	* @param item1 The first object in the distance relation
	* @param item2 The second object
	* @return The desired distance
	*/
	public double getDesiredDist(int item1, int item2)
	{
		double ans;
		
		if (!bLowerTriangular)
		{
			if (bTextCorpus || (((DataItem)dataItems.get(0)).getTextValues() != null)) // DataItem consists only of doubles
				ans = getDoubleValueDistance(item1, item2);
			else if (bSequenceData)
			{
				// The data are genetic sequences, calculate the hamming distance
				
				ans = getHammingDistance(item1, item2);
			}
			else // Data Item consists of objects not primitives
			{
				if (bBinary)
					ans = getJaccardDistance(item1, item2);
				else
					ans = getDistance(item1, item2);
			}
		}
		else
			ans = getLowerTriangleDistance(item1, item2);
		
		return ans;
	}
	
	/**
	* Same as the above except we specify which attributes to included in the distance calc
	*/
	
	public double getDesiredDist(int item1, int item2, ArrayList springVar)
	{
		double ans;
		
		if (!bLowerTriangular)
		{
			if (springVar == null)
			{
				if (bTextCorpus || (((DataItem)dataItems.get(0)).getTextValues() != null)) // DataItem consists only of doubles
					ans = getDoubleValueDistance(item1, item2);
				else if (bSequenceData)
				{
					// The data are genetic sequences, calculate the hamming distance
					
					ans = getHammingDistance(item1, item2);
				}
				else // Data Item consists of objects not primitives
				{
					if (bBinary)
						ans = getJaccardDistance(item1, item2);
					else
						ans = getDistance(item1, item2);
				}
				return ans;
			}
			else
			{
				if (bTextCorpus || (((DataItem)dataItems.get(0)).getTextValues() != null)) // DataItem consists only of doubles
					ans = getDoubleValueDistance(item1, item2);
				else // Data Item consists of objects not primitives
				{
					if (bBinary)
						ans = getJaccardDistance(item1, item2, springVar);
					else
						ans = getDistance(item1, item2, springVar);
				}
					
				return ans;
			}
		}
		else
			ans = getLowerTriangleDistance(item1, item2);
		
		return ans;
	}
	
	/**
	* Return the Hamming distance between the two given sequences
	*/
	
	private double getHammingDistance(int item1, int item2)
	{
		String sItem1 = ((DataItem)dataItems.get(item1)).getValues()[0].toString();
		String sItem2 = ((DataItem)dataItems.get(item2)).getValues()[0].toString();
		StringBuffer sb1 = new StringBuffer(sItem1);
		StringBuffer sb2 = new StringBuffer(sItem2);
		
		int score = 0;
		char ch1, ch2;
		
		// Only compare if the sequences are of the same length
		
		if (sb1.length() == sb2.length())
		{
			for (int i = 0; i < sb1.length(); i++)
			{
				ch1 = sb1.charAt(i);
				ch2 = sb2.charAt(i);
				
				if (ch1 != ch2)
					score++;
			}
		}
		
		return (double)score / (double)sb1.length();
	}
	
	/**
	* Return 1 - Jaccard similarity coefficient of two items
	*/
	
	private double getJaccardDistance(int item1, int item2)
	{
		Object[] item1Vals = ((DataItem)dataItems.get(item1)).getValues();
		Object[] item2Vals = ((DataItem)dataItems.get(item2)).getValues();
		double val1 = 0, val2 = 0;
		int A = 0, B = 0, C = 0;
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			if (item2Vals[i] instanceof Double)
			{
				val1 = ((Double)item2Vals[i]).doubleValue();
				val2 = ((Double)item1Vals[i]).doubleValue();
			}
			else
			{
				val1 = ((Integer)item2Vals[i]).intValue();
				val2 = ((Integer)item1Vals[i]).intValue();
			}
			
			if ((val1 > 0) && (val2 > 0))
				A++;
			
			if ((val1 > 0) && (val2 == 0))
				B++;
			
			if ((val2 > 0) && (val1 == 0))
				C++;
		}
		
		double denominator = ((double)(A + B + C));
		
		if ((denominator == 0) || (A == 0))
			return 5;
		else
			return 1 - ((double)A / denominator);
	}
	
	/**
	* Same as the above but using only the variables identified by the ArrayList
	*/
	
	private double getJaccardDistance(int item1, int item2, ArrayList springVar)
	{
		Object[] item1Vals = ((DataItem)dataItems.get(item1)).getValues();
		Object[] item2Vals = ((DataItem)dataItems.get(item2)).getValues();
		double val1 = 0, val2 = 0;
		int A = 0, B = 0, C = 0;
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			if (springVar.contains(new Integer(i)))
			{
				if (item2Vals[i] instanceof Double)
				{
					val1 = ((Double)item2Vals[i]).doubleValue();
					val2 = ((Double)item1Vals[i]).doubleValue();
				}
				else
				{
					val1 = ((Integer)item2Vals[i]).intValue();
					val2 = ((Integer)item1Vals[i]).intValue();
				}
				
				if ((val1 > 0) && (val2 > 0))
					A++;
				
				if ((val1 > 0) && (val2 == 0))
					B++;
				
				if ((val2 > 0) && (val1 == 0))
					C++;
			}
		}
		
		double denominator = ((double)(A + B + C));
		
		if ((denominator == 0.0) || (A == 0))
			return 1;
		else
			return (1 - ((double)A / denominator));
	}
	
	/**
	* Return the Euclidean distance between two items
	*/
	
	private double getDistance(int item1, int item2)
	{
		double val1, val2 = 0.0;
		double sumDiff = 0.0;
		double ordDiff = 1.0;
		int cols = 0;
		
		val1 = 0.0; val2 = 0.0;
		
		Object[] item1Vals = ((DataItem)dataItems.get(item1)).getValues();
		Object[] item2Vals = ((DataItem)dataItems.get(item2)).getValues();
		
		//iterate thro' values while calcing distance between vars
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			if (item2Vals[i] instanceof Date && item1Vals[i] instanceof Date)
			{
				val1 = (double)((Date)item2Vals[i]).getTime();
				val2 = (double)((Date)item1Vals[i]).getTime();
				
				if ((sigma[i] != 0) && (!Double.isNaN(sigma[i])))
				{
					val1 = (val1 - average[i]) / (STANDARD_DEVS * sigma[i]);
					val2 = (val2 - average[i]) / (STANDARD_DEVS * sigma[i]);
				}
				sumDiff += ( (val1 - val2) * (val1 - val2) );
				cols++;
			}
			else if (item2Vals[i] instanceof String && item1Vals[i] instanceof String)
			{
				if (((String)item2Vals[i]).compareTo((String)item1Vals[i]) == 0)
					ordDiff *= ORD_FACTOR;
				
				cols++;
			}
			else if (item2Vals[i] instanceof Integer && item1Vals[i] instanceof Integer)
			{
				val1 = (double)((Integer)item2Vals[i]).intValue();
				val2 = (double)((Integer)item1Vals[i]).intValue();
				
				if ((sigma[i] != 0) && (!Double.isNaN(sigma[i])))
				{
					val1 = (val1 - average[i]) / (STANDARD_DEVS * sigma[i]);
					val2 = (val2 - average[i]) / (STANDARD_DEVS * sigma[i]);
				}
				sumDiff += ( (val1 - val2) * (val1 - val2) );
				cols++;
			}
			else if (item2Vals[i] instanceof Double && item1Vals[i] instanceof Double)
			{
				val1 = ((Double)item2Vals[i]).doubleValue();
				val2 = ((Double)item1Vals[i]).doubleValue();
				
				if ((sigma[i] != 0) && (!Double.isNaN(sigma[i])))
				{
					val1 = (val1 - average[i]) / (STANDARD_DEVS * sigma[i]);
					val2 = (val2 - average[i]) / (STANDARD_DEVS * sigma[i]);
				}
				
				// sum of square of diffs
				
				sumDiff += ( (val1 - val2) * (val1 - val2) );
				cols++;
			}
		}
		
		sumDiff = Math.sqrt(sumDiff);
		sumDiff *= ordDiff;
		
		//scale by the number of valid column item2Vals that existed
		
		sumDiff *= (double)item2Vals.length / (double)cols;
		return sumDiff;
		//return Math.exp(1.4 * sumDiff); // Monotonic Gaussian transformation to test NMDS function recovery
	}
	
	/**
	* Same as the above but using only the variables that are specified by the ArrayList
	*/
	
	private double getDistance(int item1, int item2, ArrayList springVar)
	{
		double val1, val2 = 0.0;
		double sumDiff = 0.0;
		double ordDiff = 1.0;
		int cols = 0;
		
		val1 = 0.0; val2 = 0.0;
		
		Object[] item1Vals = ((DataItem)dataItems.get(item1)).getValues();
		Object[] item2Vals = ((DataItem)dataItems.get(item2)).getValues();
		
		//iterate thro' values while calcing distance between vars
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			if (springVar.contains(new Integer(i)))
			{
				if (item2Vals[i] instanceof Date && item1Vals[i] instanceof Date)
				{
					val1 = (double)((Date)item2Vals[i]).getTime();
					val2 = (double)((Date)item1Vals[i]).getTime();
					
					if ((sigma[i] != 0) && (!Double.isNaN(sigma[i])))
					{
						val1 = (val1 - average[i]) / (STANDARD_DEVS * sigma[i]);
						val2 = (val2 - average[i]) / (STANDARD_DEVS * sigma[i]);
					}
					sumDiff += ( (val1 - val2) * (val1 - val2) );
					cols++;
				}
				else if (item2Vals[i] instanceof String && item1Vals[i] instanceof String)
				{
					if (((String)item2Vals[i]).compareTo((String)item1Vals[i]) == 0)
						ordDiff *= ORD_FACTOR;
					
					cols++;
				}
				else if (item2Vals[i] instanceof Integer && item1Vals[i] instanceof Integer)
				{
					val1 = (double)((Integer)item2Vals[i]).intValue();
					val2 = (double)((Integer)item1Vals[i]).intValue();
					
					if ((sigma[i] != 0) && (!Double.isNaN(sigma[i])))
					{
						val1 = (val1 - average[i]) / (STANDARD_DEVS * sigma[i]);
						val2 = (val2 - average[i]) / (STANDARD_DEVS * sigma[i]);
					}
					sumDiff += ( (val1 - val2) * (val1 - val2) );
					cols++;
				}
				else if (item2Vals[i] instanceof Double && item1Vals[i] instanceof Double)
				{
					val1 = ((Double)item2Vals[i]).doubleValue();
					val2 = ((Double)item1Vals[i]).doubleValue();
					
					if ((sigma[i] != 0) && (!Double.isNaN(sigma[i])))
					{
						val1 = (val1 - average[i]) / (STANDARD_DEVS * sigma[i]);
						val2 = (val2 - average[i]) / (STANDARD_DEVS * sigma[i]);
					}
					
					// sum of square of diffs
					
					sumDiff += ( (val1 - val2) * (val1 - val2) );
					cols++;
				}
			}
		}
		
		sumDiff = Math.sqrt(sumDiff);
		sumDiff *= ordDiff;
		
		//scale by the number of valid column item2Vals that existed
		
		sumDiff *= (double)item2Vals.length / (double)cols;
		return sumDiff;
	}
	
	private double getDoubleValueDistance(int item1, int item2)
	{
		// ********** Euclidean distance ***************
		
		/*double val1, val2 = 0.0;
		double sumDiff = 0.0;
		double ordDiff = 1.0;
		
		val1 = 0.0; val2 = 0.0;
		
		double[] item1Vals = ((DataItem)dataItems.get(item1)).getTextValues();
		double[] item2Vals = ((DataItem)dataItems.get(item2)).getTextValues();
		
		//iterate thro' values while calcing distance between vars
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			val1 = item2Vals[i];
			val2 = item1Vals[i];
			
			// sum of square of diffs
			
			sumDiff += ( (val1 - val2) * (val1 - val2) );
		}
		
		if (sumDiff > 0)
			sumDiff = Math.sqrt(sumDiff);
		
		return sumDiff;*/
		
		// ********** Cosine similarity ***************
		
		double[] item1Vals = ((DataItem)dataItems.get(item1)).getTextValues();
		double[] item2Vals = ((DataItem)dataItems.get(item2)).getTextValues();
		double innerProduct = 0;
		double len1 = 0, len2 = 0;
		double val1 = 0, val2 = 0;
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			val1 = item2Vals[i];
			val2 = item1Vals[i];
			
			innerProduct += (val1 * val2);
			len1 += (val1 * val1);
			len2 += (val2 * val2);
		}
		
		len1 = Math.sqrt(len1);
		len2 = Math.sqrt(len2);
		
		if (((len1 == 0) || (len2 == 0)) && !((len1 == 0) && (len2 == 0)))
		{
			return 5;
		}
		else if ((len1 == 0) && (len2 == 0))
			return 0;
		else
		{
			double s = (innerProduct / (len1 * len2));
			
			return (1 - s);
		}
		
		// ********** Jaccard Similarity Coefficient ***************
		
		/*double[] item1Vals = ((DataItem)dataItems.get(item1)).getTextValues();
		double[] item2Vals = ((DataItem)dataItems.get(item2)).getTextValues();
		double val1 = 0, val2 = 0;
		int A = 0, B = 0, C = 0;
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			val1 = item2Vals[i];
			val2 = item1Vals[i];
			
			if ((val1 > 0) && (val2 > 0))
				A++;
			
			if ((val1 > 0) && (val2 == 0))
				B++;
			
			if ((val2 > 0) && (val1 == 0))
				C++;
		}
		
		if ((A == 0) || ((A + B + C) == 0))
			return 5;
		else
			return (1 - ((double)A / ((double)(A + B + C))));*/
		
		// ********** Simple Matching Coefficient ***************
		
		/*double[] item1Vals = ((DataItem)dataItems.get(item1)).getTextValues();
		double[] item2Vals = ((DataItem)dataItems.get(item2)).getTextValues();
		double val1 = 0, val2 = 0;
		int A = 0, B = 0, C = 0, D = 0;
		
		for ( int i = 0 ; i < item2Vals.length ; i++ )
		{
			val1 = item2Vals[i];
			val2 = item1Vals[i];
			
			if ((val1 > 0) && (val2 > 0))
				A += (val1 + val2);
			else if ((val1 > 0) && (val2 == 0))
				B += val1;
			else if ((val2 > 0) && (val1 == 0))
				C += val2;
			else if ((val1 == 0) && (val2 == 0))
				D++;
		}
		
		return (1 - ((double)(A + D) / ((double)(A + B + C + D))));*/
	}
	
	/**
	* Accessor method for the main collection held within this class
	*
	* @return The collection that this class encapsulates
	*/
	public ArrayList getDataItems()
	{
		return dataItems;
	}
	
	public void setDataItems(ArrayList d){
		dataItems=d;
	}
	
	/**
	* Returns the data item stored at location with index int index
	*
	* @param index The index of the dataItem that is required
	* @return the data item that was stored at this location
	*/
	public DataItem getDataItem(int index)
	{
		return (DataItem)dataItems.get(index);
	}
	
	/**
	* Returns the data item with the given ID
	*/
	
	public DataItem getDataItemByID(int ID)
	{
		DataItem item = null;
		
		for (int i = 0; i < getSize(); i++)
		{
			item = (DataItem)dataItems.get(i);
			if (item.getID() == ID)
				return item;
		}
		
		return null;
	}
	
	/**
	* Returns the data from a specified "column".  This allows the data  
	* to be accessed from a different direction, instead of just in rows
	* by getting data items
	*
	* @param colNum The required column number
	* @return The ArrayList of data from that column
	*/
	public ArrayList getColumn(int colNum)
	{
		if (bTextCorpus)
		{
			ArrayList cTemp = new ArrayList();
			
			for (int i = 0; i < getSize(); i++)
			{
				cTemp.add(new Double(((DataItem)dataItems.get(i)).getTextValues()[colNum]));
			}
			return cTemp;
		}
		else
			return (ArrayList)columns.get(colNum);
	}
	
	public void setColumns(ArrayList c){
		columns = c;
	}
	
	
	/**
	* Returns the arraylist of the maximum values for each column in the
	* data set.  Columns with String values just contain empty objects
	*
	* @return The arraylist of maximums
	*/
	public ArrayList getMaximums()
	{
		return maximums;
	}
	
	public void setMaxima(ArrayList m){
		maximums = m;
	   }
	
	    public void setMinima(ArrayList m){
		minimums = m;
	    }
	
	/** 
	* Returns the maximum value for a particular column, this will either
	* be of type Integer, Double or Date. It may also be an empty object if
	* the type was String.
	*
	* @param col The column number of the maximum required
	* @return The maximum value for the specified column
	*/
	public Object getMaximum(int col)
	{
		return maximums.get(col);
	}
	
	/**
	* Returns the arraylist of the minimum values for each column in the
	* data set.  Columns with String values just contain empty objects
	*
	* @return The arraylist of minimums
	*/
	public ArrayList getMinimums()
	{
		return minimums;
	}
	
	public ArrayList getMaxima(){ return maximums;}
	public ArrayList getMinima(){ return minimums;}
	
	/** 
	* Returns the minimum value for a particular column, this will either
	* be of type Integer, Double or Date. It may also be an empty object if
	* the type was String.
	*
	* @param col The column number of the minimum required
	* @return The minimum value for the specified column
	*/
	public Object getMinimum(int col)
	{
		return minimums.get(col);
	}
	
	/** 
	* Returns the size of this data item collection
	*
	* @return The size of the data collection 
	*/
	public int getSize()
	{
		return dataItems.size();
	}
	
	/** 
	* Returns the number of fields in each of the records under analysis.
	*
	* @return The number of fields.
	*/
	public int getNumFields()
	{
		return fields.size();
	}
	
	/**
	* Returns the distance after which two objects are considered to be 
	* unrelated.
	*
	* @return The unrelated distance
	*/
	public double getUnrelatedDist()
	{
		return unrelatedDist;
	}
	
	public double[] sigma()
	{
		return sigma;
	}
	
	public void setSigma(double[] sigma)
	{
		this.sigma = sigma;
	}
	
	public double[] average()
	{
		return average;
	}
	
	public void setAverage(double[] average)
	{
		this.average = average;
	}
	
	public double stdDevs()
	{
		return STANDARD_DEVS;
	}
	
	public ArrayList getIDs()
	{
		// Return an array list containing all of the IDs of the DataItems
		// comprising the DataItemCollection
		
		ArrayList IDs = new ArrayList(getSize());
		
		for (int i=0; i<getSize(); i++)	
		IDs.add(new Integer(getDataItem(i).getID()));
		
		return IDs;
	}
	
	/**
	* Accessor method for determining data cardinality classification
	*/
	
	public int getCardinalityClass()
	{
		cardinality = DataVolumeThreshold.getCardinalityClass(getSize());
		return cardinality;
	}
	
	/**
	* Accessor method for determining data dimensionality classification
	*/
	
	public int getDimensionalityClass()
	{
		dimensionality = DataVolumeThreshold.getDimensionalityClass(columns.size());
		return dimensionality;
	}
	
	/**
	* Accessor methods for getting the normalisation arrays
	*/
	
	public double[] getSumOfVals()
	{
		return sumOfVals;
	}
	
	public double[] getSumOfSquares()
	{
		return sumOfSquares;
	}
	
	public double[] getAverage()
	{
		return average;
	}
	
	public double[] getSigma()
	{
		return sigma;
	}
	
	public void setSumOfVals(double[] sumOfVals)
	{
		this.sumOfVals = sumOfVals;
	}
	
	public void setSumOfSquares(double[] sumOfSquares)
	{
		this.sumOfSquares = sumOfSquares;
	}
	
	/**
	* Determine whether this data collection consists of all integers
	*/
	
	public boolean allIntegers()
	{
		for (int i = 0; i < types.size(); i++)
		{
			if (((Integer)types.get(i)).intValue() != DataItemCollection.INTEGER)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	* Determine whether this data collection consists of all double values
	*/
	
	public boolean allDoubles()
	{
		for (int i = 0; i < types.size(); i++)
		{
			if (((Integer)types.get(i)).intValue() != DataItemCollection.DOUBLE)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	* Determine whether this data collection consists of all double/integer values
	*/
	
	public boolean allDoublesOrIntegers()
	{
		for (int i = 0; i < types.size(); i++)
		{
			if ((((Integer)types.get(i)).intValue() != DataItemCollection.DOUBLE) && (((Integer)types.get(i)).intValue() != DataItemCollection.INTEGER))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	* When the DataItemcollection is created by CVSLoader, the following method
	* is called to determine whether the data are genetic sequences
	*/
	
	public void determineSequenceData()
	{
		// If there is only one column and the first value for that column
		// consists of a string then we can assume (for now) that the
		// data are sequences
		
		bSequenceData = false;
		
		if (types.size() == 1)
		{
			if (((Integer)types.get(0)).intValue() == DataItemCollection.STRING)
				bSequenceData = true;
		}
	}
	
	/**
	* Determine whether the data are binary. If it only contains two distinct values (1 or 0),
	* not including missing value markers, then it is binary
	*/
	
	public void determineBinary()
	{
		if (!bTextCorpus)
		{
			ArrayList aTemp = new ArrayList(); 
			for (int i = 0; i < dataItems.size(); i++)
			{
				Object[] item1Vals = ((DataItem)dataItems.get(i)).getValues();
				
				for (int j = 0; j < fields.size(); j++)
				{
					// The values shoule either be integer or double
					
					if ((item1Vals[j] instanceof Integer) || (item1Vals[j] instanceof Double))
					{
						// The values should be either 0 or 1
						
						if (item1Vals[j] instanceof Integer)
						{
							if ((((Integer)item1Vals[j]).intValue() < 0) || (((Integer)item1Vals[j]).intValue() > 1))
							{
								bBinary = false;
								return;
							}
						}
						else if (item1Vals[j] instanceof Double)
						{
							if ((((Double)item1Vals[j]).doubleValue() < 0) || (((Double)item1Vals[j]).doubleValue() > 1))
							{
								bBinary = false;
								return;
							}
						}
						
						if (item1Vals[j] != null)
						{
							if (!aTemp.contains(item1Vals[j]))
								aTemp.add(item1Vals[j]);
							
							if (aTemp.size() > 2)
							{
								bBinary = false;
								return;
							}
						}
					}
					else
					{
						bBinary = false;
						return;
					}
				}
			}
		}
		else
		{
			bBinary = false;
			return;
		}
		
		bBinary = true;
	}
	
	/**
	* Accessor methods for determining whether the data are binary
	*/
	
	public boolean getBinary()
	{
		return bBinary;
	}
	
	public void setBinary(boolean b)
	{
		bBinary = b;
	}
	
	
	/**
     * Takes in array of booleans, of size Dimensionality.  Returns a 
     * reprocessed dataItem collection to include only those dims for 
     * which the array has a True value
     */
    public DataItemCollection newCollectionActiveDims(ArrayList active){

	ArrayList dataItems = new ArrayList();
        ArrayList types     = new ArrayList();
	ArrayList fields    = new ArrayList();
        ArrayList maxima  = new ArrayList();
        ArrayList minima  = new ArrayList();
        ArrayList columns   = new ArrayList();
	ArrayList numericDim = new ArrayList();
	ArrayList numericDimNumbers = new ArrayList();	
	double[] sigma;
	double[] average;
	String indexPath;
	int actives=0;
	int skipped =0;
	for (int i=0; i<active.size(); i++){
	    if (((Boolean)active.get(i)).booleanValue()){
		actives++;
		types.add(this.types.get(i));
		fields.add(this.fields.get(i));
		maxima.add(this.maximums.get(i));
		minima.add(this.minimums.get(i));
		columns.add(this.columns.get(i));
		numericDim.add(this.numericDim.get(i));
		numericDimNumbers.add(new Integer(((Integer)this.numericDimNumbers.get(i)).intValue()-skipped));
	    }else{
		skipped++;
	    }
	}
	average = new double[actives];
	sigma  = new double[actives];
	actives=0;
	for (int i=0; i<active.size(); i++){
	    if (((Boolean)active.get(i)).booleanValue()){
		average[actives] = this.average[i];
		sigma[actives] = this.sigma[i];
		actives++;
	    }
	}
	DataItemCollection newCollection = new DataItemCollection();
        newCollection.setTypes(types);
        newCollection.setFields(fields);
	newCollection.setSigma(sigma);
	newCollection.setAverage(average);
        newCollection.setIndexPath(getIndexPath());
	newCollection.setMaxima(maxima);
	newCollection.setMinima(minima);
	newCollection.setColumns(columns);
	newCollection.setNumericDims(numericDim);
	newCollection.setNumericDimNumbers(numericDimNumbers);

	for (int i = 0; i < this.dataItems.size(); i++)  {
	    newCollection.addItem(getDataItem(i));
        }

	return newCollection;
    }
    
	
	/**
	* Factory method for creating a new DataItemCollection based on the given
	* array of DataItem IDs. The IDs are the ordinal DataItem indices
	*/
	
	public DataItemCollection createNewCollection(int[] IDs)
	{
		DataItemCollection newCollection = new DataItemCollection();
		newCollection.setTypes(new ArrayList(types));
		newCollection.setFields(getFields());
		newCollection.setSigma(sigma());
		newCollection.setAverage(average());
		newCollection.setIndexPath(getIndexPath());
		newCollection.setDataPath(dataPath);
		newCollection.setTransposed(transposed);
		newCollection.setBinary(bBinary);
		newCollection.setSumOfVals(sumOfVals);
		newCollection.setSumOfSquares(sumOfSquares);
		newCollection.setLowerTriangular(bLowerTriangular);
		int i, j, count = 0;
		
		for (i = 0; i < IDs.length; i++)
		{
			if (IDs[i] >= 0)
				if (bLowerTriangular)
					newCollection.addItem(new DataItem(getDataItem(IDs[i]).getID()));
				else
					newCollection.addItem(new DataItem(getDataItem(IDs[i])));
		}
		
		if (bLowerTriangular)
		{
			// Create a new triangular matrix.
			
			int len = (((IDs.length * IDs.length) - IDs.length) / 2) + IDs.length; // Include the diagonal.
			double[] newMatrix = new double[len];
			int newCount = 0;
			
			for (i = 0; i < IDs.length; i++)
			{
				newCount++;
				for (j = 0; j < IDs.length - (IDs.length - newCount); j++)
				{
					newMatrix[count] = getLowerTriangleDistance(IDs[i], IDs[j]);
					count++;
				}
			}
			
			newCollection.setLowerTriangleData(newMatrix);
		}
		
		if (rowLabels != null)
		{
			ArrayList rLabels = new ArrayList();
			
			for (i = 0; i < IDs.length; i++)
				if (IDs[i] < rowLabels.size())
					rLabels.add(rowLabels.get(IDs[i]));
			
			newCollection.setRowLabels(rLabels);
		}
		else
			newCollection.setRowLabels(rowLabels);
		
		newCollection.setVariableFrequencies();
		
		newCollection.setSequenceData(bSequenceData);
		
		return newCollection;
	}
	
	/**
    * Factory method for creating a new DataItemCollection based on the given
    * array of DataItem IDs. The IDs are the ordinal DataItem indices
    */
    
    public DataItemCollection createNewCollection(int[] IDs, ArrayList active){
	ArrayList dataItems = new ArrayList();
        ArrayList types     = new ArrayList();
	ArrayList fields    = new ArrayList();
      	ArrayList numericDim = new ArrayList();
	ArrayList numericDimNumbers = new ArrayList();	
	double[] sigma;
	double[] average;
	String indexPath;
	int actives=0;
	int skipped =0;
	ArrayList whichInclude = new ArrayList();
	for (int i=0; i<active.size(); i++){
	    if (((Boolean)active.get(i)).booleanValue()){
		actives++;
		types.add(this.types.get(i));
		fields.add(this.fields.get(i));
		numericDim.add(this.numericDim.get(i));
		numericDimNumbers.add(new Integer(((Integer)this.numericDimNumbers.get(i-skipped)).intValue()));
		whichInclude.add(new Integer(i));
	    }else{
		skipped++;
	    }
	}

	// should recalc sigma and avg values
// 	double[] sumVals = new double[actives];
// 	double[] sumSqs = new double[actives];

	average = new double[actives];
	sigma  = new double[actives];
	actives=0;
	for (int i=0; i<active.size(); i++){
	    if (((Boolean)active.get(i)).booleanValue()){
		average[actives] = this.average[i];
		sigma[actives] = this.sigma[i];
		actives++;
	    }
	}
	DataItemCollection newCollection = new DataItemCollection();
        newCollection.setTypes(types);
        newCollection.setFields(fields);
	newCollection.setSigma(sigma);
	newCollection.setAverage(average);
        newCollection.setIndexPath(getIndexPath());
	newCollection.setNumericDims(numericDim);
	newCollection.setNumericDimNumbers(numericDimNumbers);

	DataItem thisItem; Object[] vals;
        for (int i = 0; i < IDs.length; i++) {
            if (IDs[i] > 0){
		thisItem = getDataItem(IDs[i]);
		vals = new Object[fields.size()];
		for (int j=0; j<fields.size(); j++)
		    vals[j]=thisItem.getValue(((Integer)whichInclude.get(j)).intValue());
		newCollection.addItem(new DataItem(vals, thisItem.getID()));
	    }
	}

	return newCollection;

    }
	
	
	
	/**
	* Accessor methods for determining whether the data set has been transposed
	*/
	
	public void setTransposed(boolean transposed)
	{
		this.transposed = transposed;
	}
	
	public boolean getTransposed()
	{
		return transposed;
	}
	
	/**
	* Accessor methods for the row labels for the data set
	*/
	
	public void setRowLabels(ArrayList rowLabels)
	{
		this.rowLabels = rowLabels;
	}
	
	public ArrayList getRowLabels()
	{
		return rowLabels;
	}
	
	/**
	* Methods for determining whether the data in this set are normalised to the [0, 1] interval
	* This normalisation can only be applied if the data consist of all doubles or integers
	*/
	
	public void setNormalised(boolean bNorm)
	{
		if (allDoublesOrIntegers() && !bTextCorpus)
		{
			if (bNorm)
				normalise_01();
			else
				deNormalise_01();
			
			bNormalised = bNorm;
		}
	}
	
	/**
	* Method called to set the normalised status of the collection without actually
	* normalising - this is called where we're creating a new data collections from
	* an exiting one
	*/
	
	public void setNormalisedWithout(boolean bNorm)
	{
		bNormalised = bNorm;
	}
	
	public boolean getNormalised()
	{
		return bNormalised;
	}
	
	/**
	* Normalise the data to the interval [0, 1]
	*/
	
	private void normalise_01()
	{
		int i;
		double max;
		double min;
		
		// Set the stats arrays
		
		sumNormVals = new double[types.size()];
		sumNormSquares = new double[types.size()];
		average = new double[sumNormVals.length];
		sigma   = new double[sumNormSquares.length];
		
		// Take a copy of the original data set for de-normalising
		
		oldDataItems = new ArrayList(dataItems.size());
		for (i = 0; i < oldDataItems.size(); i++)
		{
			oldDataItems.add(new DataItem((DataItem)dataItems.get(i)));
			dataItems.add(new DataItem((DataItem)oldDataItems.get(i)));
		}
		
		oldColumns = new ArrayList(columns.size());
		ArrayList values, newValues;
		
		for (i = 0; i < columns.size(); i++)
		{
			values = (ArrayList)columns.get(i);
			newValues = new ArrayList(values.size());
			for (int j = 0; j < dataItems.size(); j++)
			{
				newValues.add(values.get(j));
			}
			oldColumns.add(newValues);
		}
		
		for (i = 0; i < types.size(); i++)
		{
			// If it's not a text collection, then the values can be
			// doubles and/or integers
			
			int type = ((Integer)types.get(i)).intValue();
			
			if (type == DOUBLE)
			{
				max = ((Double)maximums.get(i)).doubleValue();
				min = ((Double)minimums.get(i)).doubleValue();
			}
			else
			{
				max = (double)((Integer)maximums.get(i)).intValue();
				min = (double)((Integer)minimums.get(i)).intValue();
			}
			normaliseCol(i, min, max);
		}
		
		// calculate the sigma & average values that will be needed in 
		// optimisation
		
		for (i = 0 ; i < sumNormVals.length ; i++ )
		{
			average[i] = sumNormVals[i]/getSize();
			sigma[i] = Math.sqrt((sumNormSquares[i] - ((double)getSize() * average[i] * average[i]))/(double)getSize());
		}
		
		setAllTypesToDouble();
		setAllMaxAndMinNorm();
	}
	
	/**
	* If we're normalising to [0, 1] and this collection does not represent a text corpus
	* then the folooing method is called to normalise a column
	*/
	
	private void normaliseCol(int col, double min, double max)
	{
		int i;
		double absVal;
		double val;
		ArrayList values = (ArrayList)columns.get(col);
		
		// All the values should be in the interval [0, 1] thus if the minimum is
		// below 0, then the absolute value should be added onto every value in the column.
		// Or, if the min value is greater than 1 then subtract the difference from each value.
		// This will make the minimum actually 0
		
		absVal = 0 - min;
		
		for (i = 0; i < dataItems.size(); i++)
		{
			val = 0;
			
			if (values.get(i) instanceof Double)
				val = ((Double)values.get(i)).doubleValue();
			else if (values.get(i) != null)
				val = (double)((Integer)values.get(i)).intValue();
			
			if (val == min)
				val = 0;
			else
				val = (val + absVal) / (max + absVal);
			
			sumNormVals[col] += val;
			sumNormVals[col] += (val * val);
			
			values.set(i, new Double(val));
			((DataItem)dataItems.get(i)).getValues()[col] = new Double(val);
		}
	}
	
	/**
	* If we're normalising the data to [0, 1] then all types should be converted to double
	*/
	
	private void setAllTypesToDouble()
	{
		oldTypes = new ArrayList(types.size());
		
		for (int i = 0; i < types.size(); i++)
		{
			oldTypes.add(types.get(i));
			types.set(i, new Integer(DOUBLE));
		}
	}
	
	/**
	* If normalising to [0, 1], the minimums should all be zero and the maximums all 1
	*/
	
	private void setAllMaxAndMinNorm()
	{
		oldMaximums = new ArrayList(maximums);
		oldMinimums = new ArrayList(minimums);
		
		for (int i = 0; i < types.size(); i++)
		{
			maximums.set(i, new Double(1));
			minimums.set(i, new Double(0));
		}
	}
	
	/**
	* If the data have been normalised to the [0, 1] interval, use the
	* minimums and maximums arrays to denormalise it
	*/
	
	private void deNormalise_01()
	{
		// Reset old arrays
		
		maximums = oldMaximums;
		minimums = oldMinimums;
		columns = oldColumns;
		int i;
		
		types = new ArrayList(oldTypes);
		
		//Restore values
		
		for (i = 0; i < oldDataItems.size(); i++)
		{
			dataItems.add(oldDataItems.get(i));
		}
		
		ArrayList values;
		for (int j = 0; j < types.size(); j++)
		{
			values = (ArrayList)oldColumns.get(j);
			for (i = 0; i < dataItems.size(); i++)
			{
				((DataItem)dataItems.get(i)).getValues()[j] = values.get(i);
			}
		}
		
		// Restore the stats arrays
		
		intiNormalArrays(sumOfVals, sumOfSquares);
		setNormalizeData(sumOfVals, sumOfSquares);
	}
	
	/**
	* If the data are binary then determine and store the frequencies of each variable
	*/
	
	public void setVariableFrequencies()
	{
		int val, i, j;
		
		if ((bBinary) && (!transposed))
		{
			binaryFreq = new int[types.size()];
			binaryNumVars = new int[dataItems.size()];
			DataItem dataItem;
			
			for (i = 0; i < dataItems.size(); i++)
			{
				dataItem = (DataItem)dataItems.get(i);
				for (j = 0; j < types.size(); j++)
				{
					if (dataItem.getValues()[j] instanceof Double)
						val = (int)((Double)dataItem.getValues()[j]).doubleValue();
					else
						val = ((Integer)dataItem.getValues()[j]).intValue();
					
					if (val > 0)
					{
						binaryFreq[j]++;
						binaryNumVars[i]++;
					}
				}
			}
		}
		else if (bBinary)
		{
			// Data are transposed, reverse the above operations
			
			binaryFreq = new int[dataItems.size()];
			binaryNumVars = new int[types.size()];
			DataItem dataItem;
			
			for (i = 0; i < types.size(); i++)
			{
				for (j = 0; j < dataItems.size(); j++)
				{
					dataItem = (DataItem)dataItems.get(j);
					
					if (dataItem.getValues()[i] instanceof Double)
						val = (int)((Double)dataItem.getValues()[i]).doubleValue();
					else
						val = ((Integer)dataItem.getValues()[i]).intValue();
					
					if (val > 0)
					{
						binaryFreq[j]++;
						binaryNumVars[i]++;
					}
				}
			}
		}
			
		if (bBinary)
		{
			for (i = 0; i < binaryFreq.length; i++)
			{
				if (binaryFreq[i] > maxFreq)
					maxFreq = binaryFreq[i];
				
				if (binaryFreq[i] < minFreq)
					minFreq = binaryFreq[i];
			}
			
			for (i = 0; i < binaryNumVars.length; i++)
			{
				if (binaryNumVars[i] > maxNumVars)
					maxNumVars = binaryNumVars[i];
				
				if (binaryNumVars[i] < minNumVars)
					minNumVars = binaryNumVars[i];
			}
		}
	}
	
	/**
	* Accessor methods for binary frequency values
	*/
	
	public int[] getBinaryFreq()
	{
		return binaryFreq;
	}
	
	public void setBinaryFreq(int [] binaryFreq)
	{
		this.binaryFreq = binaryFreq;
	}
	
	public int getMaxFreq()
	{
		return maxFreq;
	}
	
	public void setMaxFreq(int maxFreq)
	{
		this.maxFreq = maxFreq;
	}
	
	public int getMinFreq()
	{
		return minFreq;
	}
	
	public void setMinFreq(int minFreq)
	{
		this.minFreq = minFreq;
	}
	
	public int[] getBinaryNumVars()
	{
		return binaryNumVars;
	}
	
	public void setBinaryNumVars(int[] binaryNumVars)
	{
		this.binaryNumVars = binaryNumVars;
	}
	
	public int getMaxNumVars()
	{
		return maxNumVars;
	}
	
	public void setMaxNumVars(int maxNumVars)
	{
		this.maxNumVars = maxNumVars;
	}
	
	public int getMinNumVars()
	{
		return minNumVars;
	}
	
	public void setMinNumVars(int minNumVars)
	{
		this.minNumVars = minNumVars;
	}
	
	/**
	* Accessor methods for determining whether the data are from a lower
	* triangular matrix.
	*/
	
	public boolean getLowerTriangular()
	{
		return bLowerTriangular;
	}
	
	public void setLowerTriangular(boolean bLowerTriangular)
	{
		this.bLowerTriangular = bLowerTriangular;
	}
	
	public void setLowerTriangleData(double[] data)
	{
		triangle_lower_Array = data;
	}
	
	/**
	* Given the indices of two items, return the corresonding distance from the
	* lower triangular matrix.
	*/
	
	private double getLowerTriangleDistance(int item1, int item2)
	{
		// Treating item1 and item2 as row and column indices, get the index
		// of the matrices corresponding 1-d array.
		
		int index = getLowerTriangularIndex(item1, item2);
		return triangle_lower_Array[index];
	}
	
	/**
	* Given the indices of two items from a lower triangular matrix, return the
	* 1-d array index.
	*/
	
	private int getLowerTriangularIndex(int item1, int item2)
	{
		int i, j;
		
		if (item1 > item2)
		{
			i = item2;
			j = item1;
		}
		else
		{
			i = item1;
			j = item2;
		}
		
		return ((i + 1) + (((j + 1) * j) / 2)) - 1;
	}
}

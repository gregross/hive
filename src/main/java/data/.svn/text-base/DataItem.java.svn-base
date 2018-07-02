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
 * DataItem
 * abstract class to represent a single data item in the visualiser
 * 
 * @author Andrew Didsbury, Greg Ross
 */

package data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataItem implements Serializable
{
    static final long serialVersionUID = 50L;
    
    private Object[] values = null;
    private double[] textValues = null;
    
    // ID of the data item
    
    private int ID;
    
    // Path name of a file if this data item represents a tf-idf weighted document vector
    
    private String sPath = null;
    
    /**
    * Create a new data item as a deep copy of an existing DataItem.
    */
    
    public DataItem(DataItem item)
    {
	    ID  = item.getID();
	    sPath = item.getPath();
	    int i;
	    
	    if (item.getTextValues() != null)
	    {
		    double[] newValues = new double[item.getTextValues().length];
		    for (i = 0; i < newValues.length; i++)
		    {
			    newValues[i] = item.getTextValues()[i];
		    }
		    textValues = newValues;
	    }
	    else
	    {
		    Object[] newVals = new Object[item.getValues().length];
		    for (i = 0; i < newVals.length; i++)
		    {
			    if (item.getValues()[i] instanceof Integer)
				    newVals[i] = new Integer(((Integer)item.getValues()[i]).intValue());
			    else if (item.getValues()[i] instanceof Double)
				    newVals[i] = new Double(((Double)item.getValues()[i]).doubleValue());
			    else
				    newVals[i] = item.getValues()[i];
		    }
		    values = newVals;
	    }
    }
    
    /**
     * constructor: takes an array of values for CSV data
     *
     * @param values The array of object values
     */
    public DataItem(Object[] values, int ID)
    {
	this.values = values;
	this.ID = ID;
    }
    
    /**
    * Create a new data item with just and ID.
    */
    
     public DataItem(int ID)
    {
	this.ID = ID;
    }
    
    /**
     * constructor: takes an array of values for text data
     *
     * @param values The array of object values
     */
    public DataItem(double[] textValues, int ID)
    {
	this.textValues = textValues;
	this.ID = ID;
    }
    
    
    public void appendDims(Object[] v){
	int currentLength = values.length;
	Object[] newVals = new Object[currentLength+v.length];

	for (int i=0; i<newVals.length; i++)
	    if (i<currentLength)
		newVals[i]=values[i];
	    else
		newVals[i]=v[i-currentLength];
	values = newVals;
    }
    
    
    public Object getValue(int i){
	return values[i];
    }

    public ArrayList getValueRange(int first, int last){
	ArrayList range = new ArrayList();
	for (int i=first; i<last; i++)
	    range.add(values[i]);
	return range;
    }
    
    
    /**
     * Returns the collection of CSV values that this DataItem contains
     *
     * @return The collection of values in this DataItem
     */
    public Object[] getValues()
    {
	if (values == null)
	{
		Object[] o = new Object[textValues.length];
		for (int i = 0; i < textValues.length; i++)
		{
			o[i] = new Double(textValues[i]);
		}
		
		return o;
	}
	else
	return values;
    }
    
    /**
     * Returns the collection of text tf-idf values that this DataItem contains
     *
     * @return The collection of values in this DataItem
     */
    public double[] getTextValues()
    {
	return textValues;
    }
    
    public void setTextValues(double[] textValues)
    {
        this.textValues = textValues;
    }
    
    /**
    * Return the unique ID of this dataItem
    */
    
    public int getID()
    {
	return ID;
    }
    
    /**
    * Store the path to the file that was indexed to create the tf-idf document vector
    */
    
    public void setPath(String sPath)
    {
        this.sPath = sPath;
    }
    
    /**
    * Get the path of the file that this item represents (tf-idf)
    */
    
    public String getPath()
    {
	return sPath;
    }
}

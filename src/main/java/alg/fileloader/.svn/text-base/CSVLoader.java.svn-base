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
 * CSVLoader
 * abstract class to load in a Collection of DataItems from a csv file
 * File is assumed to have field names on the first line followed
 * by types on the second line.
 *  
 *  @author Andrew Didsbury, Greg Ross
 */ 

package alg.fileloader;

import alg.*;
import data.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class CSVLoader implements DataLoader
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    public static Map            CSV_TYPES;
    protected DataItemCollection dataItemColl;
    protected int                numDataItems;
    protected String             fileName;
    protected ArrayList          fields;
    protected ArrayList URLs;
    protected ArrayList          types;
    protected BufferedReader     csvFile;
    protected double[]           sumOfVals;
    protected double[]           sumOfSquares;

    protected SimpleDateFormat dateF;
    protected String           delim;
    protected String           undef;

    public final static String DEFAULT_DOUBLE      = "Double";
    public final static String DEFAULT_INTEGER     = "INT";
    public final static String DEFAULT_STRING      = "STRING";
    public final static String DEFAULT_DATE        = "DATE";
    public final static String DEFAULT_DELIMITER   = ",";
    public final static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";  // e.g. 18/07/2003
    
    /**
     * Constructor for CSVLoader, takes a csv filename as param
     * @param fileName The csv file to be loaded
     * @param dataSource The calling visual module
     */
    public CSVLoader(String fileName) throws 
                                             FileNotFoundException, 
                                             IOException 
    {
        this.fileName = fileName;
	
        //setup some default values for reading data
	
        CSV_TYPES = new HashMap();
        CSV_TYPES.put(DEFAULT_STRING, new Integer(DataItemCollection.STRING));
        CSV_TYPES.put(DEFAULT_DATE, new Integer(DataItemCollection.DATE));
        CSV_TYPES.put(DEFAULT_INTEGER,new Integer(DataItemCollection.INTEGER));
        CSV_TYPES.put(DEFAULT_DOUBLE,new Integer(DataItemCollection.DOUBLE));
        
        dateF = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        delim = DEFAULT_DELIMITER;
        undef = DataItemCollection.DEFAULT_UNDEFINED;
        
        int start = 0;
        int end   = fileName.length();
        if ( fileName.lastIndexOf('.') != -1 )
            end = fileName.lastIndexOf('.');
        
        String propFile = fileName.substring(start, end);
	
        FileInputStream fStream = new FileInputStream(fileName);
        
        fStream.close();
        
        csvFile = new BufferedReader(new FileReader(fileName));
        
        fields = new ArrayList();
        types = new ArrayList();
        URLs = new ArrayList();
	
        dataItemColl = new DataItemCollection();
	dataItemColl.setDataPath(fileName);
    }
    
    /**
     * Reads the field names from the first line of the csv file
     * Assumes the input pointer is ready at the start of the file
     */
    public void readFields() throws IOException
    {
        int count=0;
	
        //Read in headings
	
        String line = csvFile.readLine();
        StringTokenizer tok = new StringTokenizer(line, delim); 
        
        while( tok.hasMoreTokens() ) {
            count++;
            fields.add( tok.nextToken() );
        }
        fields.trimToSize();
    }
    
    /**
     * Reads the corresponding types from the second line of csv file
     * Assumes the input pointer is at the start of the second line of the 
     * csv file.
     */
    public void readTypes() throws IOException, ParseException
    {
        // read types line
	
        String line = csvFile.readLine();
        StringTokenizer tok = new StringTokenizer(line, delim); 
        
        while( tok.hasMoreTokens() ) 
	{
            Integer type =  (Integer)CSV_TYPES.get(tok.nextToken());
            
            //if this type was not found, throw a parseException
	    
            if (type == null)
                throw new ParseException("", 0);
                
            types.add( type );
        }
        types.trimToSize();
    }
    
    /**
     * Reads from the data source and parses its contents to create a 
     * DataItemCollection
     */
    public void readData() throws IOException, ParseException
    {
        //if the file is not ready to be read, throw an IOException
	
        if ( !csvFile.ready() )
		throw new IOException();
	
        readFields();
        readTypes();
	
        dataItemColl.setFields(getFields());
        dataItemColl.setTypes(getTypes());
        
        // the three values used for normalizing the data
	
        sumOfVals    = new double[fields.size()];
        sumOfSquares = new double[fields.size()];
        
	// Initialise the arrays for normalisation data
	// This must be done just now so that each DataItem instance
	// can reference these via its reference to the parent DataItemCollection
	
	dataItemColl.intiNormalArrays(sumOfVals, sumOfSquares);
	
        String line = csvFile.readLine();
        int count  =0;
        while( line != null ) 
	{
            // parse the data item, calcs normalization data at same time
	    
            DataItem item = parseDataItem(line, dataItemColl, count);
            if (item != null)
		    dataItemColl.addItem(item);
            
            // read in the next line of the csv file
	    
            line = csvFile.readLine();
            count++;
        }
        
        // finished with the file, close it
	
        csvFile.close();
        
        //trim the dataItemColl to size, prolly overkill but just making sure
	
        dataItemColl.getDataItems().trimToSize();
	
        //normalize the data
	
        dataItemColl.setNormalizeData(sumOfVals, sumOfSquares);
	
	// Determine whether the data are binary
	
	dataItemColl.determineBinary();
	
	// Determine whether the data are genetic sequences
	
	dataItemColl.determineSequenceData();
	
	// If the data are binary, determine the frequency of each variables
	
	dataItemColl.setVariableFrequencies();
    }
    
    /**
     * Method to parse a data item, turns the String of fields in the data
     * item into a dataItem object.  Also does work on normalization data - 
     * incremements the sumOfVals and the sumOfSquares arrays
     *
     * @param line The line containing the data for this item
     * @return The DataItem object that is created.
     */
    public DataItem parseDataItem(String line, DataItemCollection parentCollection, int ID) throws ParseException
    {
        ArrayList values = new ArrayList();
        StringTokenizer tok = new StringTokenizer(line, delim, true);      
        String lastTok;        
        int colNum = 0;
        
        while( tok.hasMoreTokens() ) 
	{            
            String val = tok.nextToken();
            lastTok = val;
            
            // if this value is a delimiter, record a null
	    
            if (val.equals(delim)) 
	    {
                values.add(null);
            }
           
            // if this value is not a delimiter, continue
	    
            else 
	    {                
                // take the real delimiter
		
                if ( tok.hasMoreTokens() ) 
			lastTok = tok.nextToken();
                
                if ( val.equals( undef ) ) 
		{
			values.add(null);
                }
                else 
		{                
			int colType = ((Integer)types.get(colNum)).intValue();                   
			int type = ((Integer)types.get(colNum)).intValue();

			switch (type)
			{                        
				case DataItemCollection.STRING: // String
					values.add(val);
					break;                        
				case DataItemCollection.DATE: // Date
					Date d = dateF.parse(val);
					values.add(d);
					sumOfVals[colNum]    += (double)d.getTime();
					sumOfSquares[colNum] += (double)d.getTime() * 
						(double)d.getTime(); 
						break;                       
				case DataItemCollection.INTEGER: // Integer
					int intVal = Integer.parseInt(val);
					values.add( Integer.valueOf(val) );
					sumOfVals[colNum]    += (double)intVal;
					sumOfSquares[colNum] += (double)(intVal * intVal);     
					break;                        
				case DataItemCollection.DOUBLE: // Double
					double dblVal = Double.parseDouble(val);
					values.add( Double.valueOf(val) );
					sumOfVals[colNum]    += dblVal;
					sumOfSquares[colNum] += dblVal * dblVal; 
					break;                       
				default:               
					System.exit(0);
			}                                        
                }
            }
	    
            //deal with special case of null value at end of line
	    
            if (!tok.hasMoreTokens() && lastTok.equals(delim))
                values.add(null);
            
            colNum++;
        }                
        
	if (types.size() == colNum)
	{	
		DataItem dataItem = new DataItem(values.toArray(), ID);
		return dataItem;
	}
	else
		return null;
    }
    
    /**
     * Returns a DataItemCollection object containing the data in the csv
     * file
     * @return The DataItemCollection containing all the data.
     */
    public DataItemCollection getDataItemCollection()
    {
        return dataItemColl;
    }
        
    /**
     * gets the field names in the csv file
     * @return The field names in the csv
     */
    public ArrayList getFields()
    {
        return fields;
    }
   
    /**
     * Gets the types that correspond to the field names,
     * @return The types of each field name in the csv
     */
    public ArrayList getTypes()
    {
        return types;
    }
}


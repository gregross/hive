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
 * TriLoader
 * abstract class to load in a Collection of DataItems from a 
 * lower triangular matrix file
 *  
 *  @author Greg Ross
 */ 

package alg.dataSource_triangle;

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
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public class TriLoader
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    protected DataItemCollection dataItemColl;
    protected int                numDataItems;
    protected String             fileName;
    protected BufferedReader     triFile;
    protected String           	 delim = " ";
    protected String           	 undef;
    
    /**
     * Constructor for TriLoader, takes a filename as param.
     * @param fileName The file to be loaded
     * @param dataSource The calling visual module
     */
    public TriLoader(String fileName) throws FileNotFoundException, IOException
    {
        this.fileName = fileName;
        undef = DataItemCollection.DEFAULT_UNDEFINED;
        
        int start = 0;
        int end   = fileName.length();
        if ( fileName.lastIndexOf('.') != -1 )
            end = fileName.lastIndexOf('.');
	
        FileInputStream fStream = new FileInputStream(fileName);
        fStream.close();
	
        dataItemColl = new DataItemCollection();
	dataItemColl.setDataPath(fileName);
    }
    
    /**
     * Reads from the data source and parses its contents to create a 
     * DataItemCollection
     */
    public void readData() throws IOException, ParseException
    {
        // If the file is not ready to be read, throw an IOException
	
	triFile = new BufferedReader(new FileReader(fileName));
	 
        if (!triFile.ready())
		throw new IOException();
	
        String line = triFile.readLine();
	StringTokenizer tok;
	String val;
	ArrayList values = new ArrayList();
	int numRows = 0, numCols = 0, lastColCount;
	DataItem dataItem = null;
	dataItemColl.setLowerTriangular(true);
	ArrayList labels = new ArrayList();
	
        while(line != null)
	{
		tok = new StringTokenizer(line, delim, false);
		lastColCount = numCols;
		numCols = 0;
		
		// Take the first line as the labels.
		
		while(tok.hasMoreTokens())
		{
			val = tok.nextToken();
			
			if (numRows == 0)
			{
				labels.add(val);
			}
			else
			{
				if (val.equals(undef))
				{
					values.add(null);
				}
				else
				{
					values.add(Double.valueOf(val));
				}
				numCols++;
			}
		}
		
		// If the matrix is lower triangular then the number of columns
		// should increase by one in each row.
		
		if (numRows > 0)
		{
			if ((numCols - lastColCount) != 1)
				throw new IOException();
			
			// Read in the next line of the file
			
			dataItem = new DataItem(numRows - 1);
			dataItemColl.addItem(dataItem);
		}
		numRows++;
		line = triFile.readLine();
        }
        
        // Finished with the file, close it
	
        triFile.close();
	double[] triangleValues = new double[values.size()];
	
	for (int i = 0; i < values.size(); i++)
		triangleValues[i] = ((Double)values.get(i)).doubleValue();
	
	dataItemColl.setLowerTriangleData(triangleValues);
	dataItemColl.setRowLabels(labels);
        
        // Trim the dataItemColl to size, prolly overkill but just making sure
	
        dataItemColl.getDataItems().trimToSize();
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
}

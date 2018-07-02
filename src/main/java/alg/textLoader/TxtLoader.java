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
 * TxtLoader
 * abstract class to load in a Collection of DataItems from a text corpus
 *  
 *  @author Greg Ross
 */ 

package alg.textLoader;

import alg.*;
import data.*;
import excel.ExcelExport;
import parent_gui.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Properties;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.*;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DefaultSimilarity;

public class TxtLoader
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    protected DataItemCollection dataItemColl;
    protected int                numDataItems;
    protected String             fileName;
    protected ArrayList          fields;
    protected ArrayList          types;
    protected double[]           sumOfVals;
    protected double[]           sumOfSquares;
    
    private int numDocs = 0;
    private int numTerms = 0; // The total number of dimension terms
    private int numCrossTerms = 0; // The number of terms used to indicate context
    
    HashMap filePaths;
    
    // The number of documents that conatain both terms 1 and 2 (termPairs[term1][term2])
    
    double[][] termPairs;
    
    // The number of documents that contain a given term
    
    HashMap docFreqs;
    
    /**
     * Constructor for TxtLoader, takes a csv filename as param
     * @param fileName The csv file to be loaded
     * @param dataSource The calling visual module
     */
    public TxtLoader(String fileName)
    {
        this.fileName = fileName;
        fields = new ArrayList();
        types = new ArrayList();
        dataItemColl = new DataItemCollection();
	dataItemColl.setIndexPath(fileName);
    }
    
    /**
     * Reads from the data source and parses its contents to create a 
     * DataItemCollection
     */
	public void readData() throws java.io.FileNotFoundException, java.io.IOException
	{
		// Get the term-document matrix[docs][terms]
		
		double matrix[][] = termDocumentMatrix(fileName);
		int i = 0;
		int j = 0;
		
		// Use random mapping to initially reduce the dimensionality
		
		/*int d = 550; // The target dimensionality
		double[][] temp = new double[numDocs][d];
		temp = randomMap(matrix, d);
		numTerms = d;
		matrix = temp;
		
		for (i = 0; i < d; i++)
		{
			fields.add("x");
			types.add(new Integer(DataItemCollection.DOUBLE));
		}*/
		
		dataItemColl.setFields(getFields());
		dataItemColl.setTypes(getTypes());
		
		// The values used for normalizing the data
		
		sumOfVals    = new double[fields.size()];
		sumOfSquares = new double[fields.size()];
		
		// Initialise the arrays for normalisation data
		// This must be done just now so that each DataItem instance
		// can reference these via its reference to the parent DataItemCollection
		
		dataItemColl.intiNormalArrays(sumOfVals, sumOfSquares);
		
		// For each document, create a DataItem and add it to the DataItemCollection
		
		for (i = 0; i < numDocs; i++)
		{
			double[] values = new double[numTerms];
			double dblVal = 0d;
			int sum = 0;
			double length = 0d;
			
			for (j = 0; j < numTerms; j++)
			{
				dblVal = matrix[i][j];
				values[j] = dblVal;
				
				if (dblVal > 0.0)
					sum += 1;
				
				sumOfVals[j] += dblVal;
				sumOfSquares[j] += (dblVal * dblVal);
				length += (dblVal * dblVal);
			}
			
			// Don't try to normalise documents that have no content-bearing words
			
			if (sum > 0)
			{
				// Normalise the vector of values to a unit vector (magnitude = 1)
				
				length = Math.sqrt(length);
				for (j = 0; j < numTerms; j++)
					values[j] = (values[j] / length);
			}
			
			DataItem item = new DataItem(values, i);
			item.setPath((String)filePaths.get(new Integer(i)));
			dataItemColl.addItem(item, true);
		}
		
		//normalize the data
		
		dataItemColl.setNormalizeData(sumOfVals, sumOfSquares);
		
		//sampleDistances(dataItemColl);
	}
	
	private double[][] randomMap(double[][] termDocMatrix, int targetD)
	{
		if (numTerms > targetD)
		{
			double[][] randomMatrix = new double[numTerms][targetD];
			int i, j;
			
			for (i = 0; i < numTerms; i++)
				for (j = 0; j < targetD; j++)
					randomMatrix[i][j] = Math.random();
			
			double product = 0.0;
			double[][] result = new double[numDocs][targetD];
			
			for (i = 0; i < numDocs; i++)
			{
				for (j = 0; j < targetD; j++)
				{
					product = 0.0;
					
					for (int k = 0; k < numTerms; k++)
						product += (termDocMatrix[i][k] * randomMatrix[k][j]);
					
					result[i][j] = product;
				}
			}
			
			return result;
		}
		else
			return termDocMatrix;
	}
	
	/**
	* Test method to take large samples of the interobject distances
	* and write them to a file in order to plot the distribution.
	*/
	
	private void sampleDistances(DataItemCollection d)
	{
		int sampleSize = 60000;
		int item1, item2 = 0;
		RandomAccessFile WriteCSV;
		
		try
		{
			WriteCSV = new RandomAccessFile("distances.csv", "rw");
			
			ArrayList distances = new ArrayList(sampleSize);
			
			for (int i = 0; i < sampleSize; i++)
			{
				item1 = (int)((Math.random() * d.getSize()));
				item2 = (int)((Math.random() * d.getSize()));
				
				WriteCSV.writeBytes((new Double(d.getDesiredDist(item1, item2))).toString() + "\n");
			}
			WriteCSV.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
    
    /**
    * Create and return a term-document matrix
    */
    
    private double[][] termDocumentMatrix(String sPath) throws java.io.FileNotFoundException, java.io.IOException
    {
	    	double[][] patterns;
	    	
	    	// Get the index
	    	
		FSDirectory dir = FSDirectory.getDirectory(sPath, false);
		IndexReader reader;
		reader = IndexReader.open(dir);
		
		// Determine the number of documents
		
		numDocs = reader.numDocs();
		
		// Store the paths of the files that the index is derived from
		
		filePaths = new HashMap(numDocs);
		int i;
		String filePath = "";
		ArrayList rowLabels = new ArrayList(numDocs);
		int lastOBlique = 0;
		
		for (i = 0; i < numDocs; i++)
		{
			filePath = reader.document(i).get("filename");
			filePaths.put(new Integer(i), filePath);
			
			// Store the file names so that they can appear in a layout.
			
			lastOBlique = filePath.lastIndexOf(File.separator);
			if (lastOBlique > 0)
				rowLabels.add(filePath.substring(lastOBlique+1, filePath.length()));
		}
		
		dataItemColl.setRowLabels(rowLabels);
		
		// Get enumeration of all the terms in the index
		
		TermEnum terms;
		TermDocs docs;
		terms = reader.terms();
		
		// Determine the most frequent terms
		
		HashMap b = new HashMap();
		ArrayList crossTerms = new ArrayList();
		int absFreq = 0;
		int mostFrequent = 0;
		while (terms.next())
		{
			docs = reader.termDocs(terms.term());
			
			absFreq = 0;
			while (docs.next())
			{
				absFreq += docs.freq();
			}
			
			if (absFreq > mostFrequent)
				mostFrequent = absFreq;
		}
		
		// Determine the statistically significant terms
		
		terms = reader.terms();
		while (terms.next())
		{
			docs = reader.termDocs(terms.term());
			absFreq = 0;
			while (docs.next())
			{
				absFreq += docs.freq();
			}
			
			if ((absFreq > (0.05 * mostFrequent)) && (absFreq < (0.6 * mostFrequent)))
			{
				b.put(terms.term().text(), new Integer(1));
				numTerms++;
			}
			else if ((absFreq >= (0.6 * mostFrequent)))
			{
				b.put(terms.term().text(), new Integer(1));
				crossTerms.add(terms.term());
				numTerms++;
				numCrossTerms++;
			}
			else
			{
				b.put(terms.term().text(), null);
			}
		}
		
		// Build the term matrix
		
		terms = reader.terms();
		int termCount = 0;
		patterns = new double[numDocs][numTerms];
		Term[] termList = new Term[numTerms];
		
		DefaultSimilarity ds = new DefaultSimilarity();
		double tf = 0d;
		double idf = 0d;
		
		while (terms.next())
		{
			if (b.get(terms.term().text()) != null)
			{
				docs = reader.termDocs(terms.term());
				idf = ds.idf(terms.docFreq(), numDocs);
				
				fields.add(terms.term().text());
				types.add(new Integer(DataItemCollection.DOUBLE));
				
				while (docs.next())
				{
					tf = docs.freq();
					patterns[docs.doc()][termCount] = tf;
				}
				
				termList[termCount] = terms.term();
				termCount++;
			}
		}
		
		b = null;
		
		/*for (i = 0; i < crossTerms.size(); i++)
		{
			fields.add(((Term)crossTerms.get(i)).text());
			types.add(new Integer(DataItemCollection.DOUBLE));
			
			docs = reader.termDocs((Term)crossTerms.get(i));
			termList[termCount] = (Term)crossTerms.get(i);
			
			idf = ds.idf(terms.docFreq(), numDocs);
			
			while (docs.next())
			{
				tf = docs.freq();
				patterns[docs.doc()][termCount] = tf;
			}
			termCount++;
		}*/
		
		docFreqs = new HashMap(numTerms);
		getDocFreqs(reader, termList);
		termPairs = new double[numTerms][numTerms];
		getPairwiseDocsEntries(patterns);
		
		//buildAssociationMatrix(termList, reader, patterns);
		
		return conditionVectors(reader, patterns, termList); // Analyse with conditional probabilities
		//return patterns; // Analyse with no conditional probabilities
   	}
	
	private double[][] conditionVectors(IndexReader reader, double[][] patterns, Term[] terms)
	{
		double[][] newPatterns = new double[numDocs][numTerms];
		double cond;
		
		for (int n = 0; n < numDocs; n++)
		{
			for (int i = 0; i < numTerms; i++)
			{
				for (int j = 0; j < numTerms; j++)
				{
					if ((i != j) && (patterns[n][j] > 0))
					{
						cond = Math.pow(condProbability(i, j, reader, patterns, terms), 2d);
						if (patterns[n][i] > 0)
						{
							newPatterns[n][i] += (cond * patterns[n][i]);
						}
						else
							newPatterns[n][i] += (cond);
					}
				}
			}
		}
		
		return newPatterns;
	}
	
	/**
	* Given two terms return the conditional probability of term 1 occurring given term 2
	*/
	
	private double condProbability(int term1, int term2, IndexReader reader, double[][] patterns, Term[] terms)
	{
		double p_t2 = 0;
		double p_t2_and_t1 = 0;
		double p_t1_given_t2 = 0;
		
		p_t2 = ((Integer)docFreqs.get(terms[term2])).intValue()  / ((double)numDocs);
		p_t2_and_t1 = termPairs[term1][term2] / ((double)numDocs);
		p_t1_given_t2 = p_t2_and_t1 / p_t2;
		
		return p_t1_given_t2;
	}
	
	private void buildAssociationMatrix(Term[] terms, IndexReader reader, double[][] patterns)
	{
		int i, j;
		double[][] prob = new double[numTerms - numCrossTerms][numTerms];
		
		ArrayList coTerms = new ArrayList();
		ArrayList coProp = new ArrayList();
		
		for (i = 0; i < (numTerms - numCrossTerms); i++)
		{
			for (j = (i + 1); j < numTerms; j++)
			{
				prob[i][j] = condProbability(i, j, reader, patterns, terms);
				
				// Store reference to terms with high conditional probability
				
				if (prob[i][j] > 0.5)
				{
					coTerms.add(new Integer(i));
					coTerms.add(new Integer(j));
					coProp.add(new Double(prob[i][j]));
				}
			}
		}
		
		// Apply a higher weight to terms with higher conditional probabilities
		
		int ind = 0;
		int ind2 = 0;
		double dProb = 0d;
		if (coTerms.size() > 0)
			for (i = 0; i < numDocs; i++)
			{
				for (j = 0; j < coTerms.size(); j += 2)
				{
					ind = ((Integer)coTerms.get(j)).intValue();
					ind2 = ((Integer)coTerms.get(j + 1)).intValue();
					
					dProb = ((Double)coProp.get(j / 2)).doubleValue();
					
					if ((patterns[i][ind] > 0) && (patterns[i][ind] < (dProb * 100)))
						patterns[i][ind] = dProb * 100;
					
					if ((patterns[i][ind2] > 0) && (patterns[i][ind2] < (dProb * 100)))
						patterns[i][ind2] = dProb * 100;
				}
			}
		
		/*
		* The following part is an implementation of James Wise's "Ecological approach..."
		*
		// Sum each column vector of prob
		
		double[] sum = new double[numTerms];
		double accum = 0;
		for (j = 0; j < numTerms; j++)
		{
			for (i = 0; i < (numTerms - numCrossTerms); i++)
				sum[j] += prob[i][j];
			
			accum += sum[j];
		}
		
		// Normalise so that the sum of vector components = 1
		
		for (j = 0; j < numTerms; j++)
			sum[j] /= accum;
		
		// Replace each term's non-zero element in the patterns matrix with the corresponding sum vector value
		
		for (i = 0; i < numDocs; i++)
		{
			for (j = 0; j < numTerms; j++)
			{
				if (patterns[i][j] > 0)
					patterns[i][j] = sum[j];
			}
		}
		**********End of Jim Wise's approach)********/
	}
	
	/**
	* Return the number of documents that contain both terms A and B
	*/
	
	private int docFreq(int term1, int term2, double[][] patterns)
	{
		int sharedCount = 0;
		
		for (int i = 0; i < numDocs; i++)
		{
			if ((patterns[i][term1] > 0) && (patterns[i][term2] > 0))
				sharedCount++;
		}
		
		return sharedCount;
	}
	
	/**
	* Calculate and return a square array where the rows and cols are for term indices
	* and the value in each cell represents the number of docs that contain terms
	* indicated by the row and column
	*/
	
	private void getPairwiseDocsEntries(double[][] patterns)
	{
		for (int i = 0; i < numTerms; i++)
		{
			for (int j = i + 1; j < numTerms; j++)
			{
				termPairs[i][j] = docFreq(i, j, patterns);
			}
		}
	}
	
	/**
	* Load a HasMap with the document frequencies for individual terms
	*/
	
	private void getDocFreqs(IndexReader reader, Term[] terms)
	{
		try
		{
			for (int i = 0; i < numTerms; i++)
			{
				docFreqs.put(terms[i], new Integer(reader.docFreq(terms[i])));
			}
		}
		catch (java.io.IOException e){}
	}
	
	/**
	* Export the term-doc frequency matrix (with all terms) to Excel.
	*/
	
	public void exportTDM(String fName)
	{
		RandomAccessFile WriteCSV;
		
		try
		{
			// Delete the old file, if there was one
			
			File xlfile = new File("excelTextData.csv");
			xlfile.delete();
			
			// Create a new output file
			
			WriteCSV = new RandomAccessFile("excelTextData.csv", "rws");
			
			// Start writing at the beggining of the file.
			
			WriteCSV.seek(0);
			
			FSDirectory dir = FSDirectory.getDirectory(fName, false);
			IndexReader reader;
			reader = IndexReader.open(dir);
			TermEnum terms;
			TermDocs docs;
			int lastOBlique = 0;
			String filePath = "";
			String valueString = ","; // First cell (top-left) is empty.
			numDocs = reader.numDocs();
			String sName = "";
			int i, j;
			
			// Write a top row as the doc names.
			
			for (i = 0; i < numDocs; i++)
			{
				sName = "";
				filePath = reader.document(i).get("filename");
				lastOBlique = filePath.lastIndexOf("\\");
				if (lastOBlique > 0)
					sName = filePath.substring(lastOBlique+1, filePath.length());
				
				valueString += sName + ",";
			}
			
			valueString = valueString.substring(0, valueString.length() - 1);
			WriteCSV.writeBytes(valueString + "\r\n");
			
			// Write the left hand column as the terms, followed by their frequencies across the rows.
			
			int docNum = 0;
			terms = reader.terms();
			while (terms.next())
			{
				valueString = "";
				valueString = terms.term().text();
				docs = reader.termDocs(terms.term());
				docNum = 0;
				
				while (docs.next())
				{
					if (docNum == 0)
						valueString += ",";
					
					for (j = 0; j < (docs.doc() - docNum); j++)
					{
						valueString += ",";
					}
					
					valueString += docs.freq() + ",";
					docNum = docs.doc() + 1;
				}
				
				valueString = valueString.substring(0, valueString.length() - 1);
				WriteCSV.writeBytes(valueString + "\r\n");
			}
			
			// Close the file and release resoures
			
			WriteCSV.close();
			
			// Now open Excel.
			
			String excelPath = getExcelPath();
			
			if (excelPath != null)
			{
				if (!excelPath.equals(""))
					Runtime.getRuntime().exec(excelPath + " excelTextData.csv");
				else
					System.out.println("Could not locate MS Excel executable.");
			}
			else
				System.out.println("Could not load MS Excel path property.");
		}
		catch (Exception e){System.out.println("Could not open MS Excel. Check the file path under the settings menu.");}
	}
	
	/**
	* Get the path of the MS Excel executable.
	*/
	
	private String getExcelPath()
	{
		// Load the properties file that contains the path
		
		Properties properties = new Properties();
		
		try 
		{
                        PropertiesHandler propHandler = new PropertiesHandler(ExcelExport.PROP_FILE);
			properties.putAll(propHandler.getProperties());
			
			return properties.getProperty("excelPath");
		}
		catch (NoPropertiesException npe)
		{
			return null;
		}
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

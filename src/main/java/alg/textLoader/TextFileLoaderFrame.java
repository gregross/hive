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
 * TextFileLoaderFrame
 * 
 * Frame to allow the user to load data frames, allows them to open text files
 * to create new layouts
 *
 *
 *  @author Greg Ross
 */
 
package alg.textLoader;

import alg.*;
import data.*;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import java.text.ParseException;
import javax.swing.JOptionPane;

public class TextFileLoaderFrame extends FileHandlerFrame
{    
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    private static TextFileLoaderFrame instance;
    public static TxtLoader loader;
    
    // Store an instance of the data source so that it can be updated with the data.
    
    private TextSource dataSource;
    
    /**
     * Constructor for TextFileLoaderFrame
     * @param TextSource The calling visual module
     */
    private TextFileLoaderFrame(TextSource dataSource, boolean bOpenForIndexing)
    {
	super();
	this.bOpenForIndexing = bOpenForIndexing;
	
	setTitle("New data");	
	chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	chooser.setCurrentDirectory(new File("data in/text/"));
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	chooser.setFileFilter(new ExampleFileFilter());
		
	this.dataSource = dataSource;
    }
    
    /**
     * Static public accessor for this singleton frame
     *
     */
    public static TextFileLoaderFrame getInstance(TextSource dataSource, boolean bOpenForIndexing)
    {
	if (instance != null)
	{
		instance.dispose();
		instance = null;
	}
	
	instance = new TextFileLoaderFrame(dataSource, bOpenForIndexing);
	
	instance.dataSource = dataSource;
	instance.setVisible(true);
	instance.fileText.setText("");
	return instance;
    }
     
    /**
     * Does the work of loading things.
     */
    protected void handleFile()
    {
	String fileName = fileText.getText();
	
	try 
	{
	    System.gc();
	    
	    // See if a file has been loaded
	    
	    if (fileName.length() == 0)
	    {
		return;
	    }	
	    
	    File f = new File(fileName);
	    if (!f.exists() || !f.canRead())
	    {
		    return;
	    }
	    
	    loader = new TxtLoader(fileName);
	    loader.readData();
	    
	    // Display the file name on the visual module
	    
	    dataSource.setFileName(fileName);
	    
	    this.setVisible(false);
	    
	    // Send the text data to the calling visual module (TextSource)
	    
	    dataSource.setTextData(loader.getDataItemCollection());
	    dataSource = null;
	    
	    instance.dataSource = null;
	    System.gc();
	    
	    instance = null;
	    this.dispose();
	}
	catch (Exception e) 
	{
	   JOptionPane.showMessageDialog(null, "There was an error loading the data (" +
	    fileName + ")", "Alert", JOptionPane.ERROR_MESSAGE);
	}
    } 
    
    public static TxtLoader getTxtLoader()
    {
	    return loader;
    }
}

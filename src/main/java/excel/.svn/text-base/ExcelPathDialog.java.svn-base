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
 * ExcelPathDialog
 *
 * This dialog allows the user to define the path to the Excel exectable
 *
 *
 *  @author Greg Ross
 */
package excel;

import parent_gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Properties;
import java.io.*;

public class ExcelPathDialog extends JDialog implements ActionListener, java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	private JButton       	browse;
	private JButton       	ok;
	private JButton       	cancel;
	private JTextField  	fileText;
	private transient 	JFileChooser      chooser;
	
	protected boolean bClosing  = false;
	
	public ExcelPathDialog()
	{
		//setup the file chooser window
		
		chooser = new JFileChooser(".");
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBackground(Color.lightGray);
		
		setTitle("Set location of MS Excel program");
		setSize(450, 130); 
		setLocation(50, 50);
		
		fileText = new JTextField(25);
		
		browse = new JButton("Browse");
		ok     = new JButton("OK");
		cancel = new JButton("Cancel");
		
		browse.setActionCommand("browse");
		ok.setActionCommand("ok");
		cancel.setActionCommand("cancel");
		
		browse.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
		
		JPanel filePanel = new JPanel();
		
		filePanel.setBorder( new TitledBorder(
					LineBorder.createGrayLineBorder(),
					"Path Name:"));
		filePanel.add(fileText);
		filePanel.add(browse);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(LineBorder.createGrayLineBorder());
		
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		
		JPanel main = new JPanel(new BorderLayout());
		main.setPreferredSize(new Dimension(450, 130));
		
		main.add(filePanel, BorderLayout.CENTER);
		main.add(buttonPanel, BorderLayout.SOUTH);
		
		getContentPane().add(main);
		
		fileText.setText(setCurrentPath());
		
		// Because we haven't set a parent for the dialog, we need to make sure
	      // that it doesn't get hidden
	      
	      addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				bClosing = true;
			}
			
			public void windowDeactivated(WindowEvent e)
			{
				if (!bClosing)
				{
					show();
				}
			}
		});
		
		setModal(true);
		setResizable(false);
		this.pack();
		this.show();
	}
	
	/**
	* Required by an ActionListener, is called whenever a button is pressed
	* 
	* @param e The action event that caused this to be called
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		// cancel, close the window
		
		if (e.getActionCommand().equals("cancel"))
		{
			bClosing = true;
			hide();
			dispose();
		}
		
		//browse, launch a file chooser and get the file from it
		
		if (e.getActionCommand().equals("browse"))
		{
			int returnVal;
			
			returnVal = chooser.showOpenDialog(this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = chooser.getSelectedFile();
				fileText.setText(file.getAbsolutePath());
			}
		}
		
		if (e.getActionCommand().equals("ok"))
		{
			File file = new File(fileText.getText());
			
			// no file specified
			
			if ( fileText.getText().length() == 0 )
			{
				// System.out.println("no file");
			}
			else
				handleFile();
		}
	}
	
	/**
	* Does the work of loading things.
	*/
	
	protected void handleFile()
	{
		String fileName = fileText.getText();
		
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
		
		// Save the path to the properties file
		
		Properties properties = new Properties();
		
		try
		{
			properties.setProperty("excelPath", fileName);
			
			// Create an output stream to the properties file
			
			OutputStream output = new FileOutputStream(ExcelExport.PROP_FILE + PropertiesHandler.DEFAULT_EXT);
			
			// Save the properties in the file
			
			if (output != null)
				properties.store(output, "");
			else
				throw new NoPropertiesException();
			
			output.close();
		}
		catch (Exception npe)
		{
			System.err.println("couldn't load the properties for Data States");
		}
		bClosing = true;
		hide();
		dispose();
	}
	
	/**
	* When the form first opens, set the current path to that included in the properties file
	*/
	
	private String setCurrentPath()
	{
		// Load the properties file that contains the path
		
		Properties properties = new Properties();
		
		try 
		{
                        PropertiesHandler propHandler = new PropertiesHandler(ExcelExport.PROP_FILE);
			properties.putAll(propHandler.getProperties());
			
			return(properties.getProperty("excelPath"));
		}
		catch (NoPropertiesException npe)
		{
			return "";
		}
	}
}

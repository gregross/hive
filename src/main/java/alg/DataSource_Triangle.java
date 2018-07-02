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
 * The Original Code is HIVE.
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
 * DataSource_Triangle
 *
 * Class represents an instance of a visual module for importing data into the system.
 * The data must be in the format of a lower triangular matrix.
 *
 *  @author Greg Ross
 */
 package alg;

import data.*;
import alg.dataSource_triangle.*;
import parent_gui.*;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import java.awt.event.*;

public class DataSource_Triangle extends DefaultVisualModule
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 140;
	private int width = 116;
	
	// Store the data
	
	private DataItemCollection triData;
	
	// Label for displaying the name of the data file
	
	private JLabel lblFileName;
	
	JButton loadData;
	
	private JPanel labelPane;
	
	public DataSource_Triangle(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Triangle Data");
		setToolTipText("Lower triangle data source");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		
		// Set the background colour and dimensions
		
		setBackground(Color.orange);
		setDimension(width, height);
		
		// Create the interface controls
		
		addControls();
	}
	
	private void addControls()
	{
		// Add a JPanel to the CENTER region of the module
		// Add a button for loading data to this panel
		
		JPanel centrePane = new JPanel();
		loadData = new JButton("Load data");
		
		loadData.addActionListener(new ActionListener()
		{
            		public void actionPerformed(ActionEvent e)
			{
				DataFileLoaderFrame.getInstance(DataSource_Triangle.this);
            		}
        	});
		
		centrePane.add(loadData);
		centrePane.setOpaque(false);
		add(centrePane, "Center");
		
		// Add a label indicating the name of the data file
		
		labelPane = new JPanel();
		labelPane.setLayout(new BoxLayout(labelPane, BoxLayout.Y_AXIS));
		labelPane.setOpaque(false);
		
		lblFileName = new JLabel("File: ");
		labelPane.add(lblFileName);
		
		centrePane.add(labelPane);
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	public void setFileName(String fileName)
	{
		// Display the name of the data file
		// This is called from DataFileLoaderFrame.handleFile()
		
		int lastOBlique = 0;
		lastOBlique = fileName.lastIndexOf("\\");
		
		if (lastOBlique > 0)
		{
			String file = fileName.substring(lastOBlique+1, fileName.length());
			lblFileName.setText("File: " + file);
		}
	}
	
	public void setTriData(DataItemCollection triData)
	{
		// Get the data
		
		this.triData = triData;
		
		// Send this data to the output port
		
		ArrayList transferData = new ArrayList();
		transferData.add(triData);
		
		getOutPort(0).sendData(transferData);
	}
	
	public DataItemCollection getTriData()
	{
		return	triData;
	}
	
	/**
	* Create the ports and append them to the module
	*/
	
	private void setPorts()
	{
		ArrayList ports = new ArrayList(1);
		ModulePort port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Data out");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		addPorts(ports);
	}
	
	/**
	* Method to restore action listener for the JButton
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, 
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		
		loadData.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				DataFileLoaderFrame.getInstance(DataSource_Triangle.this);
            		}
        	});
	}
}

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
 * TextSource
 *
 * Class represents an instance of a visual module for loading in text as a
 * tf-idf weighted term-document matrix
 *
 *  @author Greg Ross
 */
package alg; 
 

import parent_gui.dataVolumeThresholding.HybridGenerator;
import alg.textLoader.*;
import alg.lucene.*;
import data.*;
import parent_gui.*;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.Box.Filler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import java.awt.event.*;

public class TextSource extends DefaultVisualModule
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 170;
	private int width = 125;
	
	// Store text data
	
	private DataItemCollection txtData;
	
	// Label for displaying the name of the data file
	
	private JLabel lblFileName;
	private String fileName = "";
	private JLabel lblN;
	private JLabel lblD;
	
	// A button to load data given that an index already exists
	
	JButton loadData;
	
	// A button to allow the user to create an index and then load the data
	
	JButton createIndex;
	
	// A button to export a term-document matrix showing term frequencies.
	
	JButton cmdExportExcel;
		
	public TextSource(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Text data");
		setToolTipText("Corpus");
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
		
		// *******************************************************************
		// This next line sets the data source as a source anchor so that when it
		// is imported as part of a hybrid algorithm generated by HIVE, it can be
		// identified and swapped by the user's source trigger module
		//
		// This is only a temporary measure here because essentially the anchors
		// will only be identified and set when the hybrid algorithm is configured
		// and added to the 'cookbook' of algorithms that can be generated.
		
		setAnchorType(HybridGenerator.ANCHOR_SOURCE);
		
		// *******************************************************************
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
				TextFileLoaderFrame.getInstance(TextSource.this, false);
            		}
        	});
		
		// Add the button for letting the user create an index
		
		createIndex = new JButton("Index/data");
		createIndex.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				TextFileLoaderFrame.getInstance(TextSource.this, true);
            		}
        	});
		
		// Add a button to export a TDM to Excel.
		
		cmdExportExcel = new JButton("Export data");
		cmdExportExcel.setEnabled(false);
		cmdExportExcel.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				TextFileLoaderFrame.getTxtLoader().exportTDM(fileName);
            		}
        	});
		
		centrePane.setOpaque(false);
		add(centrePane, "Center");
		
		// Add a label indicating the name of the data file
		
		JPanel labelPane = new JPanel();
		labelPane.setLayout(new BoxLayout(labelPane, BoxLayout.Y_AXIS));
		labelPane.setOpaque(false);
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		labelPane.add(loadData);
		labelPane.add(filler);
		labelPane.add(createIndex);
		filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		labelPane.add(filler);
		labelPane.add(cmdExportExcel);
		
		lblFileName = new JLabel("File: ");
		labelPane.add(lblFileName);
		
		centrePane.add(labelPane);
		
		// Display N and D
		
		lblN = new JLabel("N = ", JLabel.CENTER);
		lblD = new JLabel("D = ", JLabel.CENTER);
		
		labelPane.add(lblN);
		labelPane.add(lblD);
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	public void setFileName(String fileName)
	{
		// Display the name of the data file
		// This is called from TextFileLoaderFrame.handleFile()
		
		this.fileName = fileName;
		int lastOBlique = 0;
		lastOBlique = fileName.lastIndexOf("\\");
		
		if (lastOBlique > 0)
		{
			String file = fileName.substring(lastOBlique+1, fileName.length());
			lblFileName.setText("File: " + file);
		}
	}
	
	public void setTextData(DataItemCollection txtData)
	{
		// Get the text data
		
		this.txtData = txtData;
		
		// Display the statistics
		
		lblN.setText("N = " + (new Integer(txtData.getSize()).toString()));
		lblD.setText("D = " + (new Integer(txtData.getFields().size()).toString()));
		
		// Send this data to the output port
		
		ArrayList transferData = new ArrayList();
		transferData.add(txtData);
		
		getOutPort(0).sendData(transferData);
		
		cmdExportExcel.setEnabled(true);
	}
	
	public DataItemCollection getTxtData()
	{
		return	txtData;
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
				TextFileLoaderFrame.getInstance(TextSource.this, false);
            		}
        	});
		
		createIndex.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				TextFileLoaderFrame.getInstance(TextSource.this, true);
            		}
        	});
		
		cmdExportExcel.addActionListener(new ActionListener() 
		{
            		public void actionPerformed(ActionEvent e) 
			{
				TextFileLoaderFrame.getTxtLoader().exportTDM(fileName);
            		}
        	});
	}
}

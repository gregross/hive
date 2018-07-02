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
 * VarSelectTable
 *
 * This dialog allows the user to determine which variables the spring model will run on
 *
 *
 *  @author Greg Ross
 */
package alg.springModel96;

import data.*;
import alg.fileloader.CSVLoader;
import parent_gui.*;
import alg.SpringModel96;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Exception;
import java.io.*;
import java.awt.Font;
import java.util.Properties;

public class VarSelectTable extends JDialog implements ActionListener, java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	static VarSelectTable instance;
	
	// The JPanel that will be the parent container for attribute and chart controls
	
	JPanel jpControl;
	
	// Panel that will hold the attribute table and button panel
	
	private JPanel jpAttributePanel;
	
	// The DataItemCollection representing all data in the scatterplot
	
	private DataItemCollection dataItems = null;
	
	// Buttons
	
	private JButton cmdSelectAll;
	private JButton cmdSelectNone;
	
	// JList for data attributes
	
	private JTable jlAttributes;
	
	// Button for exporting data
	
	private JButton bClose;
	
	protected boolean bClosing = false;
	
	private SpringModel96 springModel;
	
	// The currently selected variables upon which to run the spring model
	
	private ArrayList springVar;
	
	public VarSelectTable(SpringModel96 springModel, DataItemCollection dataItems, ArrayList springVar)
	{
	      this.springVar = springVar;
	      this.springModel = springModel;
	      this.dataItems = dataItems;
	      
	      setSize(200, 600);
	      
	      //  Centre the window in the screen
	      
	      double left, top;
	      left = (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2.0d) - (getWidth() / 2.0d);
	      top = (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2.0d) - (getHeight() / 2.0d);
	      setLocation(new Long(Math.round(left)).intValue(), new Long(Math.round(top)).intValue());
	      
	      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	      setBackground(Color.lightGray);
	      setVisible(false);
	      
	      // Add a cancel button
	      
	      JButton bCancel = new JButton("Cancel");
	      bCancel.addActionListener(new ActionListener() 
	      {
		      public void actionPerformed(ActionEvent e) 
		      {
			      bClosing = true;
			      hide();
			      dispose();
		      }
	      });
	      
	      // Add a close button
	      
	      bClose = new JButton("Close");
	      bClose.addActionListener(this);
	      
	      JPanel buttonBox = new JPanel();
	      buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
	      buttonBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	      buttonBox.add(Box.createVerticalGlue());
	      buttonBox.add(Box.createVerticalStrut(10));
	      buttonBox.add(bCancel);
	      buttonBox.add(Box.createVerticalStrut(10));
	      buttonBox.add(bClose);
	      buttonBox.add(Box.createVerticalStrut(10));
	      
	      // Make both buttons the same size
	      
	      Dimension bSize = new Dimension(50, 25);
	      
	      bClose.setMinimumSize(bCancel.getMinimumSize());
	      bClose.setPreferredSize(bCancel.getPreferredSize());
	      bClose.setMaximumSize(bCancel.getMaximumSize());
	      
	      // Add the panel that will hold the controls for
	      // selecting the attributes to be exported and selecting
	      // chart types
	      
	      jpControl = new JPanel();
	      jpControl.setLayout(new BoxLayout(jpControl, BoxLayout.X_AXIS));
	      getContentPane().add(jpControl, BorderLayout.CENTER);
	      
	      // Add the radio buttons and table that determine what to export
	      
	      addButtons();
	      addAttributeTable();
	      
	      // Put the Cancel and close buttons under the attribute table
	      
	      jpAttributePanel.add(buttonBox);
	      
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
	      
	      pack();
	      setTitle("Select variables for spring model");
	      setModal(true);
	      setResizable(false);
	      setVisible(true);
	      
	      // Position the form in the middle of the screen
	      
	      setLocationRelativeTo(null);
	}
	
    	public static VarSelectTable getInstance(SpringModel96 springModel, DataItemCollection dataItems, ArrayList springVar)
	{
		instance = new VarSelectTable(springModel, dataItems, springVar);
		return instance;
	}
	
	public static boolean formIsOpen()
	{
		if (instance == null)
			return false;
		else
			return true;
	}
	
	/**
	* On the left of the view, a set of radio buttons allow the user to export
	* all attributes of the selected items, or just a subset. Alternatively, users
	* can also opt to have Excel create a chart
	*/
	
	private void addButtons()
	{
		// Create a new panel that will hold the buttons
		
		JPanel jpRadio = new JPanel();
		jpRadio.setLayout(new BoxLayout(jpRadio, BoxLayout.Y_AXIS));
		jpControl.add(jpRadio);
		
		// Add buttons for selecting all or no attributes
		
		cmdSelectAll = new JButton("Select all");
		cmdSelectNone = new JButton("Deselect all");
		
		if (springVar != null)
			if (springVar.size() < dataItems.getSize())
				cmdSelectAll.setEnabled(true);
			else
				cmdSelectAll.setEnabled(false);
		else
			cmdSelectAll.setEnabled(false);
		
		cmdSelectAll.addActionListener(this);
		cmdSelectNone.addActionListener(this);
		
		cmdSelectAll.setMinimumSize(cmdSelectNone.getMinimumSize());
		cmdSelectAll.setPreferredSize(cmdSelectNone.getPreferredSize());
	      	cmdSelectAll.setMaximumSize(cmdSelectNone.getMaximumSize());
		
		// Add the buttons to the new panel
		
		jpRadio.add(cmdSelectAll);
		jpRadio.add(Box.createVerticalStrut(10));
		jpRadio.add(cmdSelectNone);
	}
	
	/**
	* Add a table that lists all attributes in the data set. Place a check box next
	* to each attribute to allow the user to indicate his/her selection
	*/
	
	private void addAttributeTable()
	{
		jlAttributes = new JTable(new VarTableModel(tableDataSet(), this));
		
		jlAttributes.setShowVerticalLines(false);
		jlAttributes.setSelectionBackground(Color.white);
		jlAttributes.setSelectionForeground(Color.black);
		jlAttributes.setColumnSelectionAllowed(false);
		jlAttributes.setRowSelectionAllowed(false);
		jlAttributes.setAutoscrolls(true);
		
		JScrollPane listScroller = new JScrollPane(jlAttributes);
		
		jpAttributePanel = new JPanel();
		jpAttributePanel.setLayout(new BoxLayout(jpAttributePanel, BoxLayout.Y_AXIS));
		jpAttributePanel.add(listScroller);
		
		jpControl.add(jpAttributePanel);
		
		listScroller.setMinimumSize(new Dimension(150, 400));
		listScroller.setPreferredSize(new Dimension(150, 400));
	      	listScroller.setMaximumSize(new Dimension(150, 400));
	}
	
	/**
	* Return the Object array of data use to instantiate the attribute table
	*/
	
	private Object[][] tableDataSet()
	{
		ArrayList fields = dataItems.getFields();
		Object[][] data = new Object[fields.size()][2];
		
		for (int i = 0; i < fields.size(); i++)
		{
			if (springVar != null)
			{
				if (springVar.contains(new Integer(i)))
				{
					data[i][0] = fields.get(i).toString();
					data[i][1] = new Boolean(true);
				}
				else
				{
					data[i][0] = fields.get(i).toString();
					data[i][1] = new Boolean(false);
				}
			}
			else
			{
				data[i][0] = fields.get(i).toString();
				data[i][1] = new Boolean(true);
			}
		}
		
		return data;
	}
	
	/**
	* Implement the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == cmdSelectAll)
			selectAllAttributes();
		else if (e.getSource() == cmdSelectNone)
			deselectAllAttributes();
		else if (e.getSource() == bClose)
		{
			springModel.setSpringVars(selectedAttributes());
			
			bClosing = true;
			hide();
			dispose();
		}
	}
	
	/**
	* Select all checkboxes in the table
	*/
	
	private void selectAllAttributes()
	{
		for (int i = 0; i < dataItems.getFields().size(); i++)
		{
			if (jlAttributes.getValueAt(i, 1).toString() != "true")
				jlAttributes.setValueAt(new Boolean(true), i, 1);
		}
		bClose.setEnabled(true);
		cmdSelectNone.setEnabled(true);
		cmdSelectAll.setEnabled(false);
	}
	
	/**
	* Deselect all checkboxes in the table
	*/
	
	private void deselectAllAttributes()
	{
		for (int i = 0; i < dataItems.getFields().size(); i++)
		{
			if (jlAttributes.getValueAt(i, 1).toString() == "true")
				jlAttributes.setValueAt(new Boolean(false), i, 1);
		}
		bClose.setEnabled(false);
		cmdSelectNone.setEnabled(false);
		cmdSelectAll.setEnabled(true);
	}
	
	/**
	* Return the ordinal indices of attributes that are to be used by the spring model
	*/
	
	private ArrayList selectedAttributes()
	{
		ArrayList attributes = new ArrayList();
		
		for (int i = 0; i < jlAttributes.getRowCount(); i++)
		{
			if (jlAttributes.getValueAt(i, 1).toString() == "true")
				attributes.add(new Integer(i));
		}
		
		return attributes;
	}
	
	/**
	* If there are no attributes selected, then the export button should be disabled
	*/
	
	public void enableDisableExportButton()
	{
		ArrayList attributes = selectedAttributes();
		
		if (attributes.size() > 0)
		{	
			bClose.setEnabled(true);
			
			if (attributes.size() == jlAttributes.getRowCount())
				cmdSelectAll.setEnabled(false);
			else
				cmdSelectAll.setEnabled(true);
			
			cmdSelectNone.setEnabled(true);
		}
		else
		{
			bClose.setEnabled(false);
			cmdSelectAll.setEnabled(true);
			cmdSelectNone.setEnabled(false);
		}
	}
}

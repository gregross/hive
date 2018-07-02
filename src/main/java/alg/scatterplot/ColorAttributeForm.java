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
 * ColorAttributeForm
 *
 * This dialog allows the user to colour over a particular attribute
 *
 *
 *  @author Greg Ross
 */
package alg.scatterplot;

import data.*;
import alg.fileloader.CSVLoader;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Exception;
import java.awt.Font;

public class ColorAttributeForm extends JDialog implements ActionListener, java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	static ColorAttributeForm instance;
	
	// The JPanel that will be the parent container for attribute and chart controls
	
	JPanel jpControl;
	
	// Panel that will hold the attribute table and button panel
	
	private JPanel jpAttributePanel;
	
	// The DataItemCollection representing all data in the scatterplot
	
	private DataItemCollection dataItems = null;
	
	// JList for data attributes
	
	private JTable jlAttributes;
	
	private JButton bClose;
	
	// The table model
	
	private ColourTableModel tableModel;
	
	private ScatterPanel parent;
	
	// The index of the attribute that is currently coloured
	
	private int colorField;
	
	protected boolean bClosing;
	
	public ColorAttributeForm(String title, DataItemCollection dataItems, ScatterPanel parent, int colorField)
	{
	      this.parent = parent;
	      this.colorField = colorField;
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
			      // Reset the original colour attribute
			      
			      colorOver(intialAttribute());
			      bClosing = true;
			      hide();
			      dispose();
		      }
	      });
	      
	      // Add a close button
	      
	      bClose = new JButton("Close");
	      bClose.addActionListener(new ActionListener() 
	      {
		      public void actionPerformed(ActionEvent e) 
		      {
			      if (colorOver())
			      {
				      bClosing = true;
				      hide();
				      dispose();
			      }
			      else
			      {
				      // Prompt the use that some attributes must be selected
				      
				      promptToSelectAttributes();
			      }
		      }
	      });
	      
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
	      // selecting the attributes to be coloured over 
	      
	      jpControl = new JPanel();
	      jpControl.setLayout(new BoxLayout(jpControl, BoxLayout.X_AXIS));
	      getContentPane().add(jpControl, BorderLayout.CENTER);
	      
	      // Add the table that determines what to colour over
	      
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
	      setTitle(title);
	      setModal(true);
	      setResizable(false);
	      setVisible(true);
	      
	      // Position the form in the middle of the screen
	      
	      setLocationRelativeTo(null);
	}
	
	/**
	* If the user tries to apply color when no attributes are selected,
	* provide a prompt to select one or more attributes
	*/
	
	private void promptToSelectAttributes()
	{
		Object[] options = {"OK"};
		int answer = JOptionPane.showOptionDialog(
			SwingUtilities.getWindowAncestor(this),
			"You must select at least one attribute from the table.",
			"Colour according to attribute",
			JOptionPane.OK_OPTION,
			JOptionPane.ERROR_MESSAGE,
			null,
			options,
			options[0]);
	}
	
    	public static ColorAttributeForm getInstance(String title, DataItemCollection dataItems, ScatterPanel parent, int colorField)
	{
		instance = new ColorAttributeForm(title, dataItems, parent, colorField);
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
	* Add a table that lists all attributes in the data set. Place a check box next
	* to each attribute to allow the user to indicate his/her selection
	*/
	
	private void addAttributeTable()
	{
		tableModel = new ColourTableModel(tableDataSet(), this);
		jlAttributes = new JTable(tableModel);
		
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
			data[i][0] = fields.get(i).toString();
			
			// Select the currently coloured-over attribute
			
			if (i == colorField)
				data[i][1] = new Boolean(true);
			else
				data[i][1] = new Boolean(false);
		}
		
		return data;
	}
	
	/**
	* Implement the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		
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
	}
	
	/**
	* Deselect all checkboxes in the table
	*/
	
	public void deselectAllAttributes()
	{
		tableModel.setCodeSelecting(true);
		for (int i = 0; i < dataItems.getFields().size(); i++)
		{
			if (jlAttributes.getValueAt(i, 1).toString() == "true")
				jlAttributes.setValueAt(new Boolean(false), i, 1);
		}
		bClose.setEnabled(false);
		tableModel.setCodeSelecting(false);
	}
	
	/**
	* Return the selected attribute
	*/
	
	private String selectedAttribute()
	{
		String attribute = null;
		
		for (int i = 0; i < jlAttributes.getRowCount(); i++)
		{
			if (jlAttributes.getValueAt(i, 1).toString() == "true")
			{
				return (String)dataItems.getFields().get(i);
			}
		}
		
		return attribute;
	}
	
	/**
	* Get the attrivute name of the initally selected attribute (when the formopens)
	*/
	
	private String intialAttribute()
	{
		return (String)dataItems.getFields().get(colorField);
	}
	
	/**
	* Apply the colour to the scatterplot
	*/
	
	private boolean colorOver()
	{
		// Get the list of attribute indices (ordinal number within dataItem.getFields)
		
		String attribute = selectedAttribute();
		
		if (attribute != null)
		{
			parent.setColorField(attribute);
			return true;
		}
		else
			return false;
	}
	
	/**
	* When the user selects an attribute to colour over, this method is called to
	* give an instant preview in the scatterplot
	*/
	
	public void colorOver(String attribute)
	{
		if (attribute != null)
		{
			parent.setColorField(attribute);
		}
	}
	
	/**
	* If there are no attributes selected, then the color button should be disabled
	*/
	
	public void enableDisableExportButton()
	{
		String attribute = selectedAttribute();
		
		if (attribute != null)
		{	
			bClose.setEnabled(true);
			colorOver(attribute);
		}
		else
		{
			bClose.setEnabled(false);
		}
	}
}

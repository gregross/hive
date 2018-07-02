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
 * Sample
 *
 * Class represents an instance of a visual module for sampling data from a DataSource
 *
 *  @author Greg Ross
 */
package alg; 
 
import parent_gui.*;
import data.*;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.Box.Filler;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.event.*;
import javax.swing.text.*;

public class Sample extends DefaultVisualModule implements ActionListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	// The dimensions of the module
	
	private int height = 134;
	private int width = 200;
	
	// The input data
	
	DataItemCollection dataItems;
	
	// The sample
	
	ArrayList sample;
	DataItemCollection sampleData;
	
	// The remainder
	
	ArrayList remainder;
	DataItemCollection remainderData;
	
	// Controls
	
	JRadioButton rbN_Items, rbSqrN_Items, rb4RootN_Items, rbX_Items;
	JTextField txtSampleSize;
	JButton cmdApply;
	JPanel radioPane;
	
	// Store the sizes of the various samples
	
	private int nSize, sqrNSize, root4Size, xSize;
	
	public Sample(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Sample");
		setToolTipText("Data subset sampling");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
		setControlsEnabled(false);
	}
	
	private void calculateSampleSizes()
	{
		if (dataItems != null)
		{
			nSize = dataItems.getSize();
			sqrNSize = (new Double(Math.sqrt((new Double(dataItems.getSize())).doubleValue()))).intValue();
			root4Size = (new Double(Math.sqrt(sqrNSize))).intValue();
			
			// Set the labels of the radio buttons
			
			rbN_Items.setText("n (" + nSize + ")");
			rbSqrN_Items.setText("n^1/2 (" + sqrNSize + ")");
			rb4RootN_Items.setText("n^1/4 (" + root4Size + ")");
		}
		else
		{
			nSize = 0;
			sqrNSize = 0;
			root4Size = 0;
			rbN_Items.setText("n");
			rbSqrN_Items.setText("n^1/2");
			rb4RootN_Items.setText("n^1/4");
		}
	}
	
	private void addControls()
	{
		// New JPanel to conatain the radio button for determine the sample size
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		radioPane = new JPanel(gridbag);
		radioPane.setBorder(BorderFactory.createTitledBorder("Sample size:"));
		radioPane.setOpaque(false);
		
		// Add the radio buttons
		
		// First option to use all data items as sample
		
		rbN_Items = new JRadioButton("n");
		rbN_Items.setActionCommand("rbN_Items");
		rbN_Items.addActionListener(this);
		rbN_Items.setOpaque(false);
		rbN_Items.setSelected(true);
		
		// Use square root of data items as sample
		
		rbSqrN_Items = new JRadioButton("n^1/2");
		rbSqrN_Items.setActionCommand("rbSqrN_Items");
		rbSqrN_Items.addActionListener(this);
		rbSqrN_Items.setOpaque(false);
		
		// Use n^1/4 data items as sample
		
		rb4RootN_Items = new JRadioButton("n^1/4");
		rb4RootN_Items.setActionCommand("rb4RootN_Items");
		rb4RootN_Items.addActionListener(this);
		rb4RootN_Items.setOpaque(false);
		
		// Allow the user to specifiy the sample size
		
		rbX_Items = new JRadioButton("X <= n:");
		rbX_Items.setActionCommand("rbX_Items");
		rbX_Items.addActionListener(this);
		rbX_Items.setOpaque(false);
		
		// create button group and add the radio buttons
		
		ButtonGroup group = new ButtonGroup();
		group.add(rbN_Items);
		group.add(rbSqrN_Items);
		group.add(rb4RootN_Items);
		group.add(rbX_Items);
		
		// Add to the JPanel
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		gridbag.setConstraints(rbN_Items, c);
		radioPane.add(rbN_Items);
		
		c.gridwidth = GridBagConstraints.REMAINDER; // End row
		gridbag.setConstraints(rbSqrN_Items, c);
		radioPane.add(rbSqrN_Items);
		
		c.gridwidth = GridBagConstraints.RELATIVE; // Start row
		gridbag.setConstraints(rb4RootN_Items, c);
		radioPane.add(rb4RootN_Items);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(rbX_Items, c);
		radioPane.add(rbX_Items);
		
		// Add a JTextField instance for allowing the user to specify the sample size
		
		txtSampleSize = new JTextField();
		
		// Ensure that the user can only enter a number <= to the total size
		// of the data set
		
		txtSampleSize.setDocument(new ValidTextFieldDoc(0, txtSampleSize, null));
		txtSampleSize.setPreferredSize(new Dimension(60, 18));
		txtSampleSize.setMaximumSize(new Dimension(60, 18));
		txtSampleSize.setMinimumSize(new Dimension(60, 18));
		txtSampleSize.setActionCommand("txtSampleSize");
		txtSampleSize.addActionListener(this);
		
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(txtSampleSize, c);
		radioPane.add(txtSampleSize);
		
		// Add a button to apply changes made in the text entry box
		
		cmdApply = new JButton("Apply");
		cmdApply.setActionCommand("cmdApply");
		cmdApply.addActionListener(this);
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(cmdApply, c);
		radioPane.add(cmdApply);
		
		// Add the JPanel to the module
		
		add(radioPane, "Center");
		
		// Fill out the East and West and south regions of the module's BorderLayout
		// so the JPanel has a border
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		
		setInterfaceVisibility();
	}
	
	private void setControlsEnabled(boolean bEnabled)
	{
		rbN_Items.setEnabled(bEnabled);
		rbSqrN_Items.setEnabled(bEnabled);
		rb4RootN_Items.setEnabled(bEnabled);
		rbX_Items.setEnabled(bEnabled);
		setTextEnabled();
	}
	
	private void setTextEnabled()
	{
		if ((rbX_Items.isEnabled()) && (rbX_Items.isSelected()))
		{
			txtSampleSize.setEnabled(true);
			txtSampleSize.requestFocus();
			txtSampleSize.setBackground(Color.white);
			
			// Only enable the text box if it contains valid text
			
			if (txtSampleSize.getText().length() > 0)
				cmdApply.setEnabled(true);
			else
				cmdApply.setEnabled(false);
		}
		else
		{
			txtSampleSize.setEnabled(false);
			txtSampleSize.setBackground(Color.lightGray);
			cmdApply.setEnabled(false);
		}
	}
	
	/** 
	* Internal class to deal with valid text-entry for
	* determining the sample size
	*/
	
	private class ValidTextFieldDoc extends PlainDocument
	{
		private int maxValue;
		private JTextField txtBox;
		private JButton apply;

		public ValidTextFieldDoc(int maxValue, JTextField txtBox, JButton apply)
		{
			// The maximum allowable value that the text box can hold
			
			this.maxValue = maxValue;
			
			// Reference to the text box per se
			
			this.txtBox = txtBox;
			
			// Reference to the apply button that is enabled only when
			// the text is valid
			
			this.apply = apply;
		}
	
		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
			{
				// Allow only numerals to be entered
				
				char[] source = str.toCharArray();
				char[] result = new char[source.length];
				int j = 0;

				for (int i = 0; i < result.length; i++) 
				{
					if (Character.isDigit(source[i]))
						result[j++] = source[i];
				}
				
				// don't allow the first number to be 0
				
				if ((offs == 0) && str.equals("0"))
					return;
				
				// Exit here if a non-numeric entry was attempted.
				// Even though the text was blocked, a parse exception would
				// still arise
				
				if ((txtBox.getText() + new String(result, 0, j)).equals(""))
				{
					apply.setEnabled(false);
					return;
				}
					
				// Don't allow the number to be larger than maxValue
					
				if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) <= maxValue)
				{
					super.insertString(offs, new String(result, 0, j), a);
					apply.setEnabled(true);
				}
			}
			
			protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng)
			{
				super.removeUpdate(chng);
				
				if ((txtBox.getText().length() < 2) || (chng.getLength() == txtBox.getText().length()))
					apply.setEnabled(false);
			}
	}
	
	/**
	* Implement the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		// Determine whether to set the
		// text entry controls
		
		setTextEnabled();
		
		if (e.getSource() == cmdApply)
		{
			xSize = Integer.parseInt(txtSampleSize.getText());
			rbX_Items.setText("X <= n: (" + xSize + ")");
			createDataSets(xSize);
		}
		else if (e.getSource() == rbN_Items)
		{
			createDataSets(nSize);
		}
		else if (e.getSource() == rbSqrN_Items)
		{
			createDataSets(sqrNSize);
		}
		else if (e.getSource() == rb4RootN_Items)
		{
			createDataSets(root4Size);
		}
	}
	
	/**
	* Given the requested sample size, create the sample set and the
	* remainder set
	*/
	
	private void createDataSets(int sampleSize)
	{	
		sample = Utils.createRandomSample(null, null, dataItems.getSize(), sampleSize);
		
		int remainderSize = dataItems.getSize() - sample.size();
		
		remainder = new ArrayList(remainderSize);
		
		if (remainderSize > 0)
		{
			for (int i = 0; i < dataItems.getSize(); i++)
			{
				if (!sample.contains(new Integer(i)))
					remainder.add(new Integer(i));
			}
		}
		
		// Copy the sample data into the sample data set
		
		int[] itemIndices = new int[sample.size()];
		
		for (int j = 0; j < sample.size(); j++)
			itemIndices[j] = ((Integer)sample.get(j)).intValue();
		
		sampleData = dataItems.createNewCollection(itemIndices);
		
		// Copy the remainder data into the remainder data set
		
		itemIndices = new int[remainder.size()];
		
		for (int k = 0; k < remainder.size(); k++)
			itemIndices[k] = ((Integer)remainder.get(k)).intValue();
		
		remainderData = dataItems.createNewCollection(itemIndices);
		
		// Send the data to the output ports and beyond
		
		sendData();
	}
	
	/**
	*  This is called when a connected module wants to notify this
	*  module of a change
	*/
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				// Data on the input port
				
				dataItems = (DataItemCollection)(arg.get(0));
				if (dataItems.getSize() > 0)
					setControlsEnabled(true);
					
				calculateSampleSizes();
				txtSampleSize.setDocument(new ValidTextFieldDoc(dataItems.getSize(), txtSampleSize, cmdApply));
				
				// Sample of square root N should be the default
				
				rbSqrN_Items.setSelected(true);
				createDataSets(sqrNSize);
				setTextEnabled();
			}
		}
		else
		{
			// Input module was deleted
			
			dataItems = null;
			setControlsEnabled(false);
			calculateSampleSizes();
			sampleData = null;
			remainderData = null;
			dataItems = null;
			
			getOutPort(0).sendData(null);
			
			getOutPort(1).sendData(null);
		}
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 1;
		int numOutPorts = 2;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' port
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Sample");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 1);
		port.setPortLabel("Remainder");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		addPorts(ports);
	}
	
	/**
	* When the user selects a sample size, this method is used to
	* send the sample and remainder to the output ports
	*/
	
	public void sendData()
	{	
		// Get references to the selction handling objects
		
		// Send the sample
		
		ArrayList transferData = new ArrayList();
		transferData.add(sampleData);
		transferData.add(sample);
		
		getOutPort(0).sendData(transferData);
		
		// Send the remainder
		
		if (remainderData.getSize() > 0)
		{	
			transferData = new ArrayList();
			transferData.add(remainderData);
			transferData.add(remainder);
			
			getOutPort(1).sendData(transferData);
		}
		else
			getOutPort(1).sendData(null);
	}
	
	/**
	* Need to override paintComponent because serialisation, for
	* some reason, resets the opacity of the radio buttons. Must
	* be another Java bug
	*/
	
	public void paintComponent(java.awt.Graphics g)
	{
		super.paintComponent(g);
		
		if (radioPane != null)
		{
			radioPane.setOpaque(false);
			rbN_Items.setOpaque(false);
			rbSqrN_Items.setOpaque(false);
			rb4RootN_Items.setOpaque(false);
			rbX_Items.setOpaque(false);
		}
	}
}

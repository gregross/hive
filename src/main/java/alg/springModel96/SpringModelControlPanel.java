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
 * SpringModelControlPanel
 *
 * This class is a JPanel that provides methods for controlling the 
 * behaviour of the layout engine, this includes being able to dynamically
 * change certain parameters that affect the way a layout works, such as the 
 * freeness of damping factor
 *
 *@author Andrew Didsbury, Greg Ross
 */

package alg.springModel96;

import alg.SpringModel96;

import javax.swing.event.*;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.Runnable;
import java.lang.Thread;
import java.lang.InterruptedException;

public class SpringModelControlPanel extends JPanel implements ChangeListener, 
								Runnable, 
								ActionListener,
								ItemListener,
								DocumentListener,
								java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// Sliders for controlling spring model parameters
	
	protected JSlider freeness;
	protected JSlider spring;
	protected JSlider damping;
	
	// Controls for convergence triggering
	
	private JCheckBox chkEnableConTrigger;
	private JRadioButton rbIterationTrigger;
	private JRadioButton rbVelocityTrigger;
	private ButtonGroup radioGroup;
	private JTextField txtTriggerVal;
	private JPanel triggerPanel;
	
    	protected int SLIDER_MAX = 20;
	
	// The parent SpringModel module
	
    	private SpringModel96 springModel;
	
    	// The thread that controls the sizing and animation of
	// the spring model module
	
    	private Thread thread;
	
    	// Are we enlarging the module or shrinking it
	
    	private static final int ENLARGE = 0;
	private static final int SHRINK = 1;
	private int resizeMode;
	
    	// The original size of the module before resizing
	
    	private Dimension originalSize;
	
    	// The desired enlarged dimensions
	
    	private int DESIRED_WIDTH = 267;
	private int DESIRED_HEIGHT = 370;
	
	// Format object to format and parse numbers in the text field
	// for determining the velocity scalar threshold for convergence
	// triggering
	
	private NumberFormat doubleFormat;
	
    	public SpringModelControlPanel(SpringModel96 springModel)
	{
		// Enlarge the module
		
		setVisible(false);
		resizeMode = ENLARGE;
		this.springModel = springModel;
		thread = new Thread(this);
		thread.start();
		
	    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new EmptyBorder(5,5,5,5));
		
		//setup labels
		
		Hashtable labelTable = new Hashtable();
		JLabel nought = new JLabel("0.0");
		JLabel quarter = new JLabel("0.25");
		JLabel half = new JLabel("0.5");
		JLabel threeQuarters = new JLabel("0.75");
		JLabel one = new JLabel("1.0");
		
		labelTable.put(new Integer(0), nought );
		labelTable.put(new Integer(SLIDER_MAX / 4), quarter);
		labelTable.put(new Integer(SLIDER_MAX / 2), half);
		labelTable.put(new Integer((3 * SLIDER_MAX) / 4), threeQuarters);
		labelTable.put(new Integer(SLIDER_MAX), one);
		
		
		// Setup the FREENESS slider
		
		freeness = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_MAX, 0);
		freeness.setMajorTickSpacing(2);
		freeness.setMinorTickSpacing(1);
		freeness.setPaintTicks(true);
		freeness.addChangeListener(this);
		freeness.setLabelTable(labelTable);
		freeness.setPaintLabels(true);
		freeness.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),
					     "Freeness",
					     TitledBorder.DEFAULT_JUSTIFICATION, 
					     TitledBorder.DEFAULT_POSITION));
		freeness.setEnabled(false);
		
		// Setup the SPRING FORCE slider
		
		spring = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_MAX, 0);
		spring.setMajorTickSpacing( 2 );
		spring.setMinorTickSpacing( 1 );
		spring.setPaintTicks(true);
		spring.setLabelTable(labelTable);
		spring.setPaintLabels(true);
		spring.setBorder( new TitledBorder(new EmptyBorder(1,1,1,1), 
					   "Spring Force",
					   TitledBorder.DEFAULT_JUSTIFICATION, 
					   TitledBorder.DEFAULT_POSITION));
		spring.addChangeListener(this);
		spring.setEnabled(false);
		
		
		// Setup the DAMPING FORCE slider
		
		damping = new JSlider(JSlider.HORIZONTAL, 0, SLIDER_MAX, 0);
		damping.setMajorTickSpacing( 2 );
		damping.setMinorTickSpacing( 1 );
		damping.setPaintTicks(true);
		damping.setLabelTable(labelTable);
		damping.setPaintLabels(true);
		damping.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),  
					    "Damping Force",
					    TitledBorder.DEFAULT_JUSTIFICATION, 
					    TitledBorder.DEFAULT_POSITION));
		damping.addChangeListener(this);
		damping.setEnabled(false);
		
		add(freeness);
		add(spring);
		add(damping);
		
		addConvergenceTriggerControls();
	}
	
	/**
	* Controls for determining if and how the spring model will
	* signify the fact that it has reached some predefined level
	* of convergence.
	*/
	
	private void addConvergenceTriggerControls()
	{	
		// Radio button for interation-based triggering
		
		rbIterationTrigger = new JRadioButton("Iteration");
		rbIterationTrigger.setActionCommand("rbIterationTrigger");
		rbIterationTrigger.addActionListener(this);
		rbIterationTrigger.setOpaque(false);
		
		// Radio button for velocity-based triggering
		
		rbVelocityTrigger = new JRadioButton("Velocity");
		rbVelocityTrigger.setActionCommand("rbVelocityTrigger");
		rbVelocityTrigger.addActionListener(this);
		rbVelocityTrigger.setOpaque(false);
		
		// create button group and add the radio buttons
		
		radioGroup = new ButtonGroup();
		radioGroup.add(rbIterationTrigger);
		radioGroup.add(rbVelocityTrigger);
		
		// Text field for entering the scaler threshold
		
		txtTriggerVal = new JTextField();
		
		// Instantiate the formatting object for decimal (double)
		// numbers
		
		doubleFormat = DecimalFormat.getNumberInstance();
		((DecimalFormat)doubleFormat).applyPattern("#.#");
		doubleFormat.setMinimumFractionDigits(1);
		
		// Create the PlainDocument subclass. The null parameter means
		// that the validation here will be based upon integer values only,
		// i.e. for entering the number of iterations before spring model
		// convergence
		
		txtTriggerVal.setDocument(new ValidateTriggerField(null));
		
		// Document listener for text value changes
		
		txtTriggerVal.getDocument().addDocumentListener(this);
		
		// Checkbox to enable/disable convergence triggering
		
		chkEnableConTrigger = new JCheckBox("Enable");
		chkEnableConTrigger.addItemListener(this);
		chkEnableConTrigger.setOpaque(false);
		
		// The panel that will hold all of the above controls
		
		triggerPanel = new JPanel();
		triggerPanel.setLayout(new BoxLayout(triggerPanel, BoxLayout.X_AXIS));
		triggerPanel.setBorder(new TitledBorder("Convergence trigger"));
					     
		// Add the controls to this panel
		
		triggerPanel.add(chkEnableConTrigger);
		triggerPanel.add(rbIterationTrigger);
		triggerPanel.add(rbVelocityTrigger);
		triggerPanel.add(txtTriggerVal);
		
		// Add the panel to the super panel
		
		add(triggerPanel);
		
		// If a convergence trigger was previously set for the spring model
		// get its type and enable the controls as appropriate
		
		boolean bConTrigger;
		
		if (springModel.isConvergenceTrigger())
			bConTrigger = true;
		else
			bConTrigger = false;
		
		if (springModel.getConvergenceTrigger() == SpringModel96.ITERATIVE_CONVERGENCE)
		{
			rbIterationTrigger.setSelected(true);
			txtTriggerVal.setDocument(new ValidateTriggerField(null));
			txtTriggerVal.setText((new Integer(springModel.getIterationTrigValue())).toString());
			txtTriggerVal.getDocument().addDocumentListener(this);
		}
		else if (springModel.getConvergenceTrigger() == SpringModel96.VELOCITY_CONVERGENCE)
		{	
			rbVelocityTrigger.setSelected(true);
			txtTriggerVal.setDocument(new ValidateTriggerField(doubleFormat));
			txtTriggerVal.setText((new Double(springModel.getVelocityTrigValue())).toString());
			txtTriggerVal.getDocument().addDocumentListener(this);
		}
		
		chkEnableConTrigger.setSelected(bConTrigger);
		enableDisableTriggerControls(springModel.isTriggerEnabled());
	}
	
	/**
	* Allow the spring model to dirsctly apply a value to the text trigger
	* text field
	
	* The only time this is used is in the SpringModel.update(...) method to set
	* the default iteration trigger threshold to equal the number of data items
	*/
	
	public void setTriggerText()
	{
		if (rbIterationTrigger.isSelected())
			txtTriggerVal.setText((new Integer(springModel.getIterationTrigValue())).toString());
	}
	
	public void actionPerformed(ActionEvent e)
	{
		// When the user selects one of the radio buttons,
		// transfer the focus to the text box
		
		if ((e.getSource() == rbIterationTrigger) ||
			(e.getSource() == rbVelocityTrigger))
		{
		
			txtTriggerVal.requestFocus();
		
			// Determine the type of convergence trigger selected
				
			if (rbIterationTrigger.isSelected())
				springModel.setConvergenceTrigger(SpringModel96.ITERATIVE_CONVERGENCE);
			else
				springModel.setConvergenceTrigger(SpringModel96.VELOCITY_CONVERGENCE);
				
			// Set the text field's document
			
			if (e.getSource() == rbIterationTrigger)
			{
				txtTriggerVal.setDocument(new ValidateTriggerField(null));
				txtTriggerVal.setText((new Integer(springModel.getIterationTrigValue())).toString());
				txtTriggerVal.getDocument().addDocumentListener(this);
			}
			else
			{
				txtTriggerVal.setDocument(new ValidateTriggerField(doubleFormat));
				txtTriggerVal.setText((new Double(springModel.getVelocityTrigValue())).toString());
				txtTriggerVal.getDocument().addDocumentListener(this);
			}
		}
	}
	
	/**
	* Enable disable the controls for determining the convergence trigger
	* threshold
	*/
	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() == chkEnableConTrigger)
		{
			boolean bEnabled = false;
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				bEnabled = true;
				txtTriggerVal.setBackground(Color.white);
				txtTriggerVal.setEnabled(bEnabled);
				txtTriggerVal.requestFocus();
				
				// Determine the type of convergence trigger selected
				
				springModel.setConvergenceTriggerEnabled(true);
				
				if (rbIterationTrigger.isSelected())
					springModel.setConvergenceTrigger(SpringModel96.ITERATIVE_CONVERGENCE);
				else
					springModel.setConvergenceTrigger(SpringModel96.VELOCITY_CONVERGENCE);
			}
			else
			{
				springModel.setConvergenceTriggerEnabled(false);
				txtTriggerVal.setBackground(Color.lightGray);
			}
				
			rbIterationTrigger.setEnabled(bEnabled);
			rbVelocityTrigger.setEnabled(bEnabled);
			txtTriggerVal.setEnabled(bEnabled);
		}
	}
	
	/**
	* Implementation of the DocumentListener interface
	*/
	
	public void insertUpdate(DocumentEvent e)
	{
		updateSpringModelConTrigger();
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		updateSpringModelConTrigger();
	}
	
	public void changedUpdate(DocumentEvent e){}
	
	private void updateSpringModelConTrigger()
	{
		if (txtTriggerVal.getText().length() > 0)
		{
			if (rbIterationTrigger.isSelected())
				springModel.setIterationTrigValue(Integer.parseInt(txtTriggerVal.getText()));
			else
				springModel.setVelocityTrigValue(Double.parseDouble(txtTriggerVal.getText()));
		}
	}
	
	public void enableDisableControls(boolean bEnable)
	{
		freeness.setEnabled(bEnable);
		spring.setEnabled(bEnable);
		damping.setEnabled(bEnable);
		enableDisableTriggerControls(bEnable);
	}
	
	public void enableDisableTriggerControls(boolean bEnable)
	{
		chkEnableConTrigger.setEnabled(bEnable);
		springModel.setConvergenceTriggerEnabled(chkEnableConTrigger.isSelected());
		
		if (chkEnableConTrigger.isSelected())
		{
			rbIterationTrigger.setEnabled(bEnable);
			rbVelocityTrigger.setEnabled(bEnable);
			txtTriggerVal.setEnabled(bEnable);
			
			if (bEnable)
				txtTriggerVal.setBackground(Color.white);
			else
				txtTriggerVal.setBackground(Color.lightGray);
		}
		else
			txtTriggerVal.setBackground(Color.lightGray);
	}
	
    	public void setFreeness(double freeVal)
	{
		freeness.setValue((int)(freeVal * (double)SLIDER_MAX));
		freeness.setEnabled(true);
	}
	
	
    	public void setSpring(double springVal)
	{
		spring.setValue( (int)(springVal * (double)SLIDER_MAX));
		spring.setEnabled(true);
	}
	
	
    	public void setDamping(double dampVal)
	{
		damping.setValue( (int)(dampVal * (double)SLIDER_MAX));
		damping.setEnabled(true);
	}
	
    	/**
	* Called whenever the state of the slider is changed, required by 
	* change listener.
	*/
	
     	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider)e.getSource();
		
		if (!source.getValueIsAdjusting()) 
		{
			double val = (double)source.getValue() / (double)SLIDER_MAX;
			
	    		if (source == freeness) 
			{
				springModel.setFreeness(val);
			}
			else if (source == spring)
			{ 
				springModel.setSpringForce(val);
			}
			else if (source == damping)
			{
				springModel.setDampingFactor(val);
			}
		}
	}
	
    	private void resizeModuleForShow()
	{
		// If the module isn't already big enough, make it
		// larger so that the controls will be displayed correctly
		
	    	originalSize = springModel.getSize(null);
		double currentWidth  = originalSize.getWidth();
		double currentHeight  = originalSize.getHeight();
		
		final int INC_VALUE = 25;
		
	    	if (((new Double(currentWidth)).intValue() < DESIRED_WIDTH) || 
			((new Double(currentHeight)).intValue() < DESIRED_HEIGHT))
		{	
			// The module needs enlarging. Animate this
			
			int deltaWidth = DESIRED_WIDTH - (new Double(currentWidth)).intValue();
			if (deltaWidth < 0) 
				deltaWidth = 1;
			
			int deltaHeight = DESIRED_HEIGHT - (new Double(currentHeight)).intValue();
			if (deltaHeight < 0) 
				deltaHeight = 1;
				
			double dWidth = 1d;
			double dHeight = 1d;
			
			// Determine the rate of increase in each direction
			
			if ((deltaWidth > 0) && (deltaHeight > 0))
			{
				if (deltaWidth > deltaHeight)
				{
					dHeight = (new Double(deltaHeight)).doubleValue() / 
						(new Double(deltaWidth)).doubleValue();
				}
				else if (deltaWidth < deltaHeight)
				{
					dWidth = (new Double(deltaWidth)).doubleValue() / 
						(new Double(deltaHeight)).doubleValue();
				}
			}
			
			// Increase the size
			
			springModel.bringToFront();
			
			while (((new Double(currentWidth)).intValue() < DESIRED_WIDTH) || 
				((new Double(currentHeight)).intValue() < DESIRED_HEIGHT))
			{	
				if (((new Double(currentWidth)).intValue() < DESIRED_WIDTH))
				{
					currentWidth += (new Double(INC_VALUE * dWidth)).doubleValue();
					
					if (((new Double(currentWidth)).intValue() > DESIRED_WIDTH))
						currentWidth = DESIRED_WIDTH;
				}
				
				if (((new Double(currentHeight)).intValue() < DESIRED_HEIGHT))
				{
					currentHeight += (new Double(INC_VALUE * dHeight)).doubleValue();
					
					if (((new Double(currentHeight)).intValue() > DESIRED_HEIGHT))
						currentHeight = DESIRED_HEIGHT;
				}
				
				springModel.setDimension((new Double(currentWidth)).intValue(),
					(new Double(currentHeight)).intValue());
					
				// Delay to set the speed of animation
				
				try {thread.sleep(1);}
				catch(InterruptedException e){}
				
				springModel.validate();
			}
			
			// Ensure that the display is valid
			
			springModel.validate();
			springModel.getParentForm().getDrawPane().repaint();
			setVisible(true);	
		}
		else
			setVisible(true); // the module is already big enough
			
		springModel.setSelected(true);
		springModel.repaint();
	}
	
    	private void resizeModuleForHide()
	{
		// Restore the size of the module to
		// what it was before it was increased
		
		double currentWidth  = springModel.getSize(null).getWidth();
		double currentHeight  = springModel.getSize(null).getHeight();
		DESIRED_WIDTH = (new Double(originalSize.getWidth())).intValue();
		DESIRED_HEIGHT = (new Double(originalSize.getHeight())).intValue();
		
		final int DEC_VALUE = 50;
		
	    	if (((new Double(currentWidth)).intValue() > DESIRED_WIDTH) || 
			((new Double(currentHeight)).intValue() > DESIRED_HEIGHT))
		{	
			// The module needs reducing. Animate this
			
			int deltaWidth = (new Double(currentWidth)).intValue() - DESIRED_WIDTH;
			if (deltaWidth < 0) 
				deltaWidth = 1;
			
			int deltaHeight = (new Double(currentHeight)).intValue() - DESIRED_HEIGHT;
			if (deltaHeight < 0) 
				deltaHeight = 1;
				
			double dWidth = 1d;
			double dHeight = 1d;
			
			// Determine the rate of reduction in each direction
			
			if ((deltaWidth > 0) && (deltaHeight > 0))
			{
				if (deltaWidth > deltaHeight)
				{
					dHeight = (new Double(deltaHeight)).doubleValue() / 
						(new Double(deltaWidth)).doubleValue();
				}
				else if (deltaWidth < deltaHeight)
				{
					dWidth = (new Double(deltaWidth)).doubleValue() / 
						(new Double(deltaHeight)).doubleValue();
				}
			}
			
			// Decrease the size
			
			springModel.bringToFront();
			
			while (((new Double(currentWidth)).intValue() > DESIRED_WIDTH) || 
				((new Double(currentHeight)).intValue() > DESIRED_HEIGHT))
			{	
				if (((new Double(currentWidth)).intValue() > DESIRED_WIDTH))
				{
					currentWidth -= (new Double(DEC_VALUE * dWidth)).doubleValue();
				
					if (((new Double(currentWidth)).intValue() < DESIRED_WIDTH))
						currentWidth = DESIRED_WIDTH;
				}
				
				if (((new Double(currentHeight)).intValue() > DESIRED_HEIGHT))
				{
					currentHeight -= (new Double(DEC_VALUE * dHeight)).doubleValue();
				
					if (((new Double(currentHeight)).intValue() < DESIRED_HEIGHT))
						currentHeight = DESIRED_HEIGHT;
				}
				
				springModel.setDimension((new Double(currentWidth)).intValue(),
					(new Double(currentHeight)).intValue());
				
				// Delay to set the speed of animation
				
				try {thread.sleep(1);}
				catch(InterruptedException e){}
				
				springModel.validate();
			}
			
			// Ensure that the display is valid
			
			springModel.getParentForm().getDrawPane().repaint();
			springModel.validate();
		}
		springModel.setSelected(true);
		springModel.repaint();
	}
	
	public void restore()
	{
		thread = new Thread(this);
		resizeMode = SHRINK;
		thread.start();
	}
	
   	 public void run()
	 {
		if (resizeMode == ENLARGE)
			 resizeModuleForShow();
		else if (resizeMode == SHRINK)
			resizeModuleForHide();
	 }
	 
	 /**
	* Method to restore listeners for the Swing components
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, 
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		
		freeness.addChangeListener(this);
		spring.addChangeListener(this);
		damping.addChangeListener(this);
		
		txtTriggerVal.getDocument().addDocumentListener(this);
	}
}

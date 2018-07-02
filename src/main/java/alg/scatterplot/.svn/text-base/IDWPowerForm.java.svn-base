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
 * IDWPowerForm
 *
 * This dialog allows the user to change the power value used
 * for Inverse Distance Weighting (IDW) interpolation.
 *
 *  @author Greg Ross
 */
package alg.scatterplot;

import data.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Hashtable;

public class IDWPowerForm extends JDialog implements ChangeListener, java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	static IDWPowerForm instance;
	
	// The JPanel that will be the parent container for controls
	
	JPanel jpControl;
	JPanel jpSlider;
	
	private JButton bClose;
	JSlider sldPower;
	
	private ScatterPanel parent;
	
	// The current background colour
	
	private int initialPower;
	
	protected boolean bClosing = false;
	
	public IDWPowerForm(String title, ScatterPanel parent, int initialPower)
	{
	      this.parent = parent;
	      this.initialPower = initialPower;
	      
	      setSize(200, 100);
	      
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
			      
			      resetParentColour();
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
			      bClosing = true;
			      hide();
			      dispose();
		      }
	      });
	      bClose.setEnabled(true);
	      
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
	      
	      // Add the panel that will hold the controls for
	      // selecting the attributes to be coloured over 
	      
	      jpControl = new JPanel();
	      jpControl.setLayout(new BoxLayout(jpControl, BoxLayout.X_AXIS));
	      getContentPane().add(jpControl, BorderLayout.CENTER);
	      
	      addSlider();
	      jpControl.add(jpSlider);
	      
	      // Add the cancel and close buttons
	      
	      jpControl.add(buttonBox);
	      
	      pack();
	      setTitle(title);
	      setModal(true);
	      setResizable(false);
	      setVisible(true);
	      
	      // Position the form in the middle of the screen
	      
	      setLocationRelativeTo(null);
	}
	
	/**
	* If the user cancels the operation, reset the background colour to what it was
	*/
	
	private void resetParentColour()
	{
		parent.setIDWPower(initialPower);
	}
	
	/**
	* Add the slider that is used to set the brightness
	*/
	
	private void addSlider()
	{
		// Create the panel that the slider will sit on
		
		jpSlider = new JPanel();
		
		// Make the labels for the slider - just two, one for dark and
		// on for light
		
		Hashtable labelTable = new Hashtable();
		JLabel lblLow = new JLabel("1");
		JLabel lblMid = new JLabel("25");
		JLabel lblHigh = new JLabel("50");
		labelTable.put(new Integer(1), lblLow );
		labelTable.put(new Integer(25), lblMid );
		labelTable.put(new Integer(50), lblHigh);
		
		// Create the slider
		
		sldPower = new JSlider(JSlider.HORIZONTAL, 1, 50, initialPower);
		sldPower.setMajorTickSpacing(7);
		sldPower.setMinorTickSpacing(7);
		sldPower.setPaintTicks(true);
		sldPower.addChangeListener(this);
		sldPower.setLabelTable(labelTable);
		sldPower.setPaintLabels(true);
		
		jpSlider.add(sldPower);
	}
	
	public static IDWPowerForm getInstance(String title, ScatterPanel parent, int initialPower)
	{
		instance = new IDWPowerForm(title, parent, initialPower);
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
	* Implementation of the ChangeListener interface
	*/
	
	public void stateChanged(ChangeEvent e)
	{
		int val = sldPower.getValue();
		parent.setIDWPower(val);
	}
}

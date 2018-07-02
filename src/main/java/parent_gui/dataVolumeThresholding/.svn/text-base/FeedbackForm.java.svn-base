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
 * FeedbackForm
 *
 * This class represents a modal dialogue that tells
 * the user that HIVE is in the process of generating a hybrid
 * MDS algorithm
 *
 *
 *  @author Greg Ross
 */
package parent_gui.dataVolumeThresholding;

import parent_gui.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.Box.Filler;
import javax.swing.event.*;
import javax.swing.border.BevelBorder;

public class FeedbackForm extends JDialog
{
	static FeedbackForm instance = null;
	private static Mdi mdiForm;
	
	private String title;
	
	// This is set to be the content pane of the dialog
	// This is so that I can add a bevelled border to the
	// dialog
	
	private JPanel mainPane;
	
	// Labels
	
	private JLabel lblPrompt1 =  new JLabel();
	private JLabel lblPrompt2 =  new JLabel();
	
	public FeedbackForm(String title, Mdi mdi)
	{
	      
	      mdiForm = mdi;
	      this.title = title;
	      
	      setDefaultLookAndFeelDecorated(true);
	      setUndecorated(true);
	      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE );
	}
	
	public void showForm()
	{
		setSize(300,300);
		
		// Add a bevelled border to the dialog
		
		mainPane = new JPanel();
		mainPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setContentPane(mainPane);
		
		//  Centre the window in the screen
		
		double left, top;
		left = (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2.0d) - (getWidth() / 2.0d);
		top = (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2.0d) - (getHeight() / 2.0d);
		setLocation(new Long(Math.round(left)).intValue(), new Long(Math.round(top)).intValue());
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBackground(Color.lightGray);
		setVisible(false);
		
		addLabels();
		
		pack();
		
		setTitle(title);
		setModal(true);
		setResizable(false);
		setVisible(true);
	}
	
	private void addLabels()
	{
		// Add a label telling the user that HIVE is processing input
		// and that he/she can cancel
		
		// First, a panel to hold the label
		
		JPanel lblPanel = new JPanel();
		lblPanel.setLayout(new BoxLayout(lblPanel, BoxLayout.Y_AXIS));
		
		// Now, 1 label for each line of text
		
		lblPrompt1 = new JLabel("HIVE is preparing a layout");
		lblPrompt1.setMinimumSize(new Dimension(200, 18)); 
		lblPrompt1.setPreferredSize(new Dimension(200, 18)); 
		
		// Label 2
		
		lblPrompt2 = new JLabel("algorithm. Please wait...");
		lblPrompt2.setMinimumSize(new Dimension(200, 18)); 
		lblPrompt2.setPreferredSize(new Dimension(200, 18)); 
		
		// Add labels to label panel
		
		lblPanel.add(lblPrompt1);
		lblPanel.add(lblPrompt2);
		
		// Fillout the north, west and east regions of the dialog
		
		Filler filler = new Filler(new Dimension(20, 10), new Dimension(20, 10), new Dimension(2, 10));
		getContentPane().add(filler, BorderLayout.NORTH);
		filler = new Filler(new Dimension(20, 20), new Dimension(20, 20), new Dimension(2, 20));
		getContentPane().add(filler, BorderLayout.WEST);
		filler = new Filler(new Dimension(20, 20), new Dimension(20, 20), new Dimension(2, 20));
		getContentPane().add(filler, BorderLayout.EAST);
		
		// Add label panel to dialog
		
		getContentPane().add(lblPanel, BorderLayout.CENTER);
	}
	
    	public static FeedbackForm getInstance(String title, Mdi mdi)
	{
		instance = new FeedbackForm(title, mdi);
		
		return instance;
	}
	
	/**
	* Allow the message conveyed to the user to be changed
	*/
	
	public void setText1(String sText)
	{
		lblPrompt1.setText(sText);
	}
	
	public void setText2(String sText)
	{
		lblPrompt2.setText(sText);
	}
	
	public static boolean formIsOpen()
	{
		if (instance == null)
			return false;
		else
			return true;
	}
}

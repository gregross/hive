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
 * SSA_ResultsView
 *
 * This class represents a window to display to the user the results of an SSA run,
 * i.e. Kruskal's stress etcS
 *
 *  @author Greg Ross
 */
package alg.ssa;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.lang.Exception;
import java.io.*;
import java.awt.Font;

public class SSA_ResultsView extends JDialog implements java.io.Serializable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	static SSA_ResultsView instance;
	
	// The JPanel that will be the parent container
	
	JPanel jpControl;
	
	JTextArea txtDetails;
	
	protected boolean bClosing = false;
	
	public SSA_ResultsView()
	{
	      setSize(300, 300);
	      
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
	      
	      JPanel buttonBox = new JPanel();
	      buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
	      buttonBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	      buttonBox.add(Box.createVerticalGlue());
	      buttonBox.add(Box.createVerticalStrut(10));
	      buttonBox.add(bCancel);
	      buttonBox.add(Box.createVerticalStrut(10));
	      buttonBox.add(Box.createVerticalStrut(10));
	      
	      // Add the panel that will hold the text area
	      
	      jpControl = new JPanel();
	      jpControl.setLayout(new BoxLayout(jpControl, BoxLayout.X_AXIS));
	      
	      addTextArea();
	      jpControl.add(buttonBox);
	      
	      loadText();
	      
	      getContentPane().add(jpControl, BorderLayout.CENTER);
	      
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
	      setTitle("Results from Smallest Space Analysis");
	      setModal(true);
	      setResizable(false);
	      setVisible(true);
	      
	      // Position the form in the middle of the screen
	      
	      setLocationRelativeTo(null);
	}
	
	/**
	* Load the text from COEFFs.TMP
	*/
	
	private void loadText()
	{
		try
		{
			String userdir = System.getProperty("user.dir");
			BufferedReader outputFile = new BufferedReader(new FileReader(userdir + "/COEFFS.TMP"));
			String line = outputFile.readLine();
			
			String sResult = "";
			
			while (!line.trim().equals(""))
			{
				sResult += line + "\n";
				line = outputFile.readLine();
			}
			outputFile.close();
			txtDetails.setText(sResult);
		}
		catch (Exception e)
		{
			System.out.println("Error reading SSA results.");
		}
	}
	
	private void addTextArea()
	{
		txtDetails = new JTextArea();
		txtDetails.setColumns(50);
		txtDetails.setRows(10);
		txtDetails.setFont(new Font("sansserif", Font.BOLD, 14));
		txtDetails.setEditable( false );
		jpControl.add(txtDetails);
	}
	
    	public static SSA_ResultsView getInstance()
	{
		instance = new SSA_ResultsView();
		return instance;
	}
	
	public static boolean formIsOpen()
	{
		if (instance == null)
			return false;
		else
			return true;
	}
}

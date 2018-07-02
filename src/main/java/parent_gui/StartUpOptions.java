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
 * StartUpOptions 
 *
 * Class represents an MDI child form.  This form is initially displayed when the
 * system is opened without any project file or data source pre-specified.  That
 * is, the user has not begun a session by double-clicking a file type
 * for use with this system.
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.awt.Cursor;

public class StartUpOptions extends JDialog implements  MouseListener
{
    static StartUpOptions instance;
    private static Mdi mdiForm;
    private FileHistory fileHistory;
    
    // List of recently used files
    
    private JList list;
    
    protected boolean bClosing = false;

    public StartUpOptions(String title, Mdi mdi, FileHistory fileHistory) {
	      
       this.fileHistory = fileHistory;
       
       mdiForm = mdi;
       
	setSize(300,300);

        //  Centre the window in the screen
	
	double left, top;
	left = (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2.0d) - (getWidth() / 2.0d);
	top = (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2.0d) - (getHeight() / 2.0d);
        setLocation(new Long(Math.round(left)).intValue(), new Long(Math.round(top)).intValue());
	
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setBackground(Color.lightGray);
	setVisible(false);
	
	// Add a list of recently used files
	
	list = fileHistory.createItemList();
	
	// Add a MouseListener
	
	list.addMouseListener(this);
	
	Container c = getContentPane();
	JScrollPane scroller = new JScrollPane(list);
	scroller.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10),
        BorderFactory.createLoweredBevelBorder()));
	c.add(scroller, BorderLayout.CENTER);
	
	// Add a cancel button
	
	JButton bCancel = new JButton("Cancel");
	bCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bClosing = true;
	    hide();
            dispose();
         }
	});
	
	// Add a file-open button
	
	JButton bOpen = new JButton("Open");
	bOpen.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            
	    // Get the selected file
	    
	    open();
	    bClosing = true;
	    hide();
            dispose();
         }
	});
	
	JPanel buttonBox = new JPanel();
	buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
	buttonBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	buttonBox.add(Box.createVerticalStrut(10));
	buttonBox.add(bOpen);
	buttonBox.add(Box.createVerticalStrut(10));
	buttonBox.add(bCancel);
	buttonBox.add(Box.createVerticalGlue());
	c.add(buttonBox, BorderLayout.EAST);
	pack();
	
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
	
	setTitle("Options");
	setModal(true);
	setVisible(true);
    }
    
    public void mouseClicked(MouseEvent e)
    {
	    if (e.getClickCount() == 2) 
	    {
		int index = list.locationToIndex(e.getPoint());
		
		if (index > -1)
		{
			
			if (index == 0)
			{
				// More files
				
				bClosing = true;
				hide();
				mdiForm.showOpen();
				dispose();
			}
			else if (index == 1)
			{
				// New analysis
				
				mdiForm.openModuleVP("Modules");
				bClosing = true;
				hide();
				dispose();
			}
			else
			{
				// Listed file
				
				index = index - 2;
				
				setCursor(new Cursor(Cursor.WAIT_CURSOR));
				
				ArrayList paths = fileHistory.getPathnameHistory();
				String path = (String)paths.get(index);
				mdiForm.open(path);
				bClosing = true;
				hide();
				dispose();
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	    }
    }
    
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

    
    private void open()
    {
	    setCursor(new Cursor(Cursor.WAIT_CURSOR));
	    
	    int[] indicesToOpen = list.getSelectedIndices();
            if (indicesToOpen.length > 0) 
	    {
		    int index = indicesToOpen[0];
		    
		    if (index == 0)
			{
				// More files
				
				bClosing = true;
				hide();
				mdiForm.showOpen();
				dispose();
			}
			else if (index == 1)
			{
				// New analysis
				
				mdiForm.openModuleVP("Modules");
				bClosing = true;
				hide();
				dispose();
			}
			else
			{
				// Open existing file
				
				index = index - 2;
				
				ArrayList paths = fileHistory.getPathnameHistory();
				String path = (String)paths.get(index);
				mdiForm.open(path);
			}
	    }
	    
	    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    public static StartUpOptions getInstance(String title, Mdi mdi, FileHistory fileHistory)
    {
	instance = new StartUpOptions(title, mdi, fileHistory);
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

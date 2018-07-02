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
 * Mdi
 *
 * Main gui class, holds all of the other panels in the interface.  Holds 
 * references to all of these panels and provides accessor methods to access them
 *
 *  @author Greg Ross
 */
package parent_gui;

import alg.fileloader.ExampleFileFilter;
import parent_gui.dataVolumeThresholding.*;
import excel.ExcelPathDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.Thread;

public class Mdi extends JFrame implements ActionListener, FileHistory.IFileHistory, 
							Runnable
{
	// Stuff for serialisation
	
	static Mdi instance;
	static FileInputStream fis;
	static ObjectInputStream inStream;
	static FileOutputStream fos;
	static ObjectOutputStream outStream;
	static SerialClass serialClass;
	
	// The path and name of the current file
	
	private String currentFile = " ";
	
	// For the recently used file list
	
	private FileHistory fileHistory;
	
	JDesktopPane desktop;
	JToolBar toolBar;
	
	// Menus
	
	JMenuBar	menuBar;
	
	// File Manu
	
	JMenu 		fileMenu;
	JMenuItem	fileMenu_Open;
	JMenuItem	fileMenu_Save;
	JMenuItem	fileMenu_SaveAs;
	JMenuItem	fileMenu_Exit;
	
	// Settings menu
	
	JMenu 		settingMenu;
	JMenuItem	settingMenu_dataCategories;
	JMenuItem	settingMenu_ExcelPath;
	
	// View menu
	
	JMenu		viewMenu;
	JCheckBoxMenuItem viewMenu_DrawingPane;
	JMenuItem viewMenu_TileVisHoriz;
	JMenuItem viewMenu_TileVisVerti;
	JCheckBoxMenuItem viewMenu_LinkMode;
	
	transient private JFileChooser saveChooser;
	
	// Class for general methods and functions.
	
	GuiUtil guiUtils;
	
	// Class that handles the MDI tool bar methods.
	
	static MDIToolBar mdiToolBar;
	
	// Determine the last module type that was dragged from the toolbar.
	
	String DragModuleName;
	
	// MDI child form declarations.
	
	static ModuleVP moduleVP;
	StartUpOptions startUpOptions;
	
	// Form ro allow the user to determine the LMH categories for data
	// volume in terms of cardinality and dimensionality
	
	DataStatesForm dataStatesForm;
	
	// Store all instances of visual modules.
	
	static HashMap modules = new HashMap(20);
	
	// For each type of module store its default name so that we can 
	// determine how many modules of a specific type are open and thus
	// enumerate them in their default titles.
	
	static HashMap modNames = new HashMap(20);
	
	// Determine which visual module has one of its ports highlighted
	
	private String moduleWithPortHighlight = null;
	
	// Determine the ports that are currently highlighted because the link
	// connecting them is highlighted
	
	private ModulePort modvp_link_Highlight_FromPort = null;
	private ModulePort modvp_link_Highlight_ToPort = null;
	
	// Store all of the links between visual modules that are contained
	// in an instance of the JLayeredPanes
	
	private HashMap modvp_Links = null;
	
	// Store a reference to the selected link for each view
	
	private Link selected_ModVP_Link;
	
	// Class for utilising data volume thresholding for
	// data classification and automatic algorithm generation
	
	public static final DataVolumeThreshold dataVolumeThreshold = new DataVolumeThreshold();
	
	// Thread in which opening and closing files will be carried out
	
	private static Thread thread;
	
	// Let the thread know whether we're loading or saving
	
	private boolean bSave;
	
	// Let the thread know the path of the file that we're
	// loading or saving
	
	private String sTheFile;
	
	// The feedback form displayed to the user while HIVE load
	// or saves a file
	
	private static FeedbackForm feedbackForm;
	
	public Mdi(String filePath)
	{
		// Create and disply the MDI form.
		
		super("HIVE");
		
		// Turn the JFrame into a desktop.
			
		desktop = new JDesktopPane();
		getContentPane().add(desktop);
		
		// Register the desktop with the GUI utility class.
		
		guiUtils = new GuiUtil(this);
		
		// When child forms are moved (dragged), only show the outline.
		
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		setUpMenus();
		
		// Create the toolbar.
		
		mdiToolBar = new MDIToolBar(this);
				
		// Maximise the MDI form.
		
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setVisible(true);
		
		// Create new links collections
		
		modvp_Links = new HashMap();
		
		instance = this;
		
		// Store references to the object that shall be serialised
		
		serialClass = new SerialClass(modules, modvp_Links, modNames);
		
		fileHistory = new FileHistory(this); // init FileHistory
		fileHistory.initFileMenuHistory();
		
		ImageIcon icon = new ImageIcon("images/HIVE2.gif");
		setIconImage(icon.getImage());
		
		// If the program has been opened by double-clicking on a file
		// then don't show the start-up options.
		
		if (filePath == null)
			openStartUpOptions("Options");
		
		// Exit the system upon closing the MDI.
		
 		addWindowListener(new WindowAdapter()
 			{
				public void windowClosing(WindowEvent e)
				{
 					exit();	
 				}
 			});
	}
	
	/**
	* Close the frame, save the file history entries and exit the demo.
	*/
	
	private void exit() 
	{
		setVisible(false);
		dispose();
		fileHistory.saveHistoryEntries(); // save entries for next session
		System.exit(0);
	}
	
	private void setUpMenus()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		fileMenu = new JMenu("File");
		
		// Set the file menu's font
		
		Font f = new Font("Arial", Font.PLAIN,  15);
		fileMenu.setFont(f);
		
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("Open and save files");
		
		menuBar.add(fileMenu);
		
		// Open
		
		fileMenu_Open = new JMenuItem("Open", KeyEvent.VK_O);
		fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		fileMenu_Open.getAccessibleContext().setAccessibleDescription("Open a program");
		fileMenu_Open.addActionListener(this);
		fileMenu.add(fileMenu_Open);
		
		// Separator
		
		fileMenu.addSeparator();
		
		// Save
		
		fileMenu_Save = new JMenuItem("Save", KeyEvent.VK_S);
		fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		fileMenu_Save.getAccessibleContext().setAccessibleDescription("Save program");
		fileMenu_Save.addActionListener(this);
		fileMenu.add(fileMenu_Save);
		
		// Save as
		
		fileMenu_SaveAs = new JMenuItem("Save as", KeyEvent.VK_A);
		fileMenu_SaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		fileMenu_SaveAs.getAccessibleContext().setAccessibleDescription("Save program");
		fileMenu_SaveAs.addActionListener(this);
		fileMenu.add(fileMenu_SaveAs);
		
		// Separator
		
		fileMenu.addSeparator();
		
		// Exit
		
		fileMenu_Exit = new JMenuItem("Exit", KeyEvent.VK_X);
		fileMenu_Exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		fileMenu_Exit.getAccessibleContext().setAccessibleDescription("Exit application");
		fileMenu_Exit.addActionListener(this);
		fileMenu.add(fileMenu_Exit);
		
		// Settings menu
		
		createSettingsMenu();
		
		// View menu
		
		createViewMenu();
	}
	
	private void createSettingsMenu()
	{
		settingMenu = new JMenu("Settings");
		
		// Set the menu's font
		
		Font f = new Font("Arial", Font.PLAIN,  15);
		settingMenu.setFont(f);
		
		settingMenu.setMnemonic(KeyEvent.VK_E);
		settingMenu.getAccessibleContext().setAccessibleDescription("Set data state categories");
		
		menuBar.add(settingMenu);
		
		// Menu for setting the LMH data categories
		
		settingMenu_dataCategories = new JMenuItem("Data categories", KeyEvent.VK_D);
		settingMenu_dataCategories.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		settingMenu_dataCategories.getAccessibleContext().setAccessibleDescription("Set data categories");
		settingMenu_dataCategories.addActionListener(this);
		settingMenu.add(settingMenu_dataCategories);
		
		// Menu for allowing the user to specify the path to the MS Excel executable
		
		settingMenu_ExcelPath = new JMenuItem("MS Excel location");
		settingMenu_ExcelPath.getAccessibleContext().setAccessibleDescription("Set path of MS Excel executable");
		settingMenu_ExcelPath.addActionListener(this);
		settingMenu.add(settingMenu_ExcelPath);
	}
	
	private void createViewMenu()
	{
		viewMenu = new JMenu("View");
		
		// Set the menu's font
		
		Font f = new Font("Arial", Font.PLAIN,  15);
		viewMenu.setFont(f);
		
		viewMenu.setMnemonic(KeyEvent.VK_W);
		viewMenu.getAccessibleContext().setAccessibleDescription("View menu");
		
		menuBar.add(viewMenu);
		
		// Add the menu item for displaying and closing the drawing region
		
		viewMenu_DrawingPane = new JCheckBoxMenuItem("Drawing region");
		viewMenu_DrawingPane.setMnemonic(KeyEvent.VK_M);
		viewMenu_DrawingPane.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
		viewMenu_DrawingPane.getAccessibleContext().setAccessibleDescription("Open view to create visualisations");
		viewMenu_DrawingPane.addActionListener(this);
		viewMenu.add(viewMenu_DrawingPane);
		
		viewMenu.addSeparator();
		
		// Add the menu item for tiling visualisations horizontally
		
		viewMenu_TileVisHoriz = new JMenuItem("Tile visualisations horizontally");
		viewMenu_TileVisHoriz.setMnemonic(KeyEvent.VK_H);
		viewMenu_TileVisHoriz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
		viewMenu_TileVisHoriz.getAccessibleContext().setAccessibleDescription("Tile visualisations horizontally");
		viewMenu_TileVisHoriz.addActionListener(this);
		viewMenu_TileVisHoriz.setEnabled(false);
		viewMenu.add(viewMenu_TileVisHoriz);
		
		// Add the menu item for tiling visualisations vertically
		
		viewMenu_TileVisVerti = new JMenuItem("Tile visualisations vertically");
		viewMenu_TileVisVerti.setMnemonic(KeyEvent.VK_T);
		viewMenu_TileVisVerti.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		viewMenu_TileVisVerti.getAccessibleContext().setAccessibleDescription("Tile visualisations vertically");
		viewMenu_TileVisVerti.addActionListener(this);
		viewMenu_TileVisVerti.setEnabled(false);
		viewMenu.add(viewMenu_TileVisVerti);
		
		// Menu item for toggling link mode
		
		viewMenu.addSeparator();
		viewMenu_LinkMode = new JCheckBoxMenuItem("Link mode");
		viewMenu_LinkMode.setMnemonic(KeyEvent.VK_L);
		viewMenu_LinkMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		viewMenu_LinkMode.getAccessibleContext().setAccessibleDescription("Link mode");
		viewMenu_LinkMode.addActionListener(this);
		viewMenu_LinkMode.setEnabled(false);
		viewMenu.add(viewMenu_LinkMode);
	}
	
	// Accessor methods for gaining access to the MDI menus from moduleVP
	// and the DrawingForm instance
	
	public JCheckBoxMenuItem getOpenCanvasMenu()
	{
		return viewMenu_DrawingPane;
	}
	
	public JMenuItem getViewMenu_TileVisHoriz()
	{
		return viewMenu_TileVisHoriz;
	}
	
	public JMenuItem getViewMenu_TileVisVerti()
	{
		return viewMenu_TileVisVerti;
	}
	
	public JCheckBoxMenuItem getViewMenu_LinkMode()
	{
		return viewMenu_LinkMode;
	}
	
	public void actionPerformed(ActionEvent e)
	{	
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int result = 100;
		
		if ((JMenuItem)e.getSource() == fileMenu_Open)
		{
			// Open file
			
			// First prompt the user to save changes
			
			boolean bContinue = true;
			
			if (modules.size() > 0 )
			{
				Component parent = null;
				
				if (moduleVP != null)
				{
					moduleVP = ModuleVP.getInstance("Modules", this);
					parent = moduleVP;
				}
				
				if (parent != null)
				{
					result = JOptionPane.showInternalConfirmDialog(parent, 
						"Save changes", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
						
					if ((result == JOptionPane.NO_OPTION) || (result == JOptionPane.YES_OPTION))
						bContinue = true;
					else
						bContinue = false;
				}
			}
			
			// Show the 'Open' dialog
			
			if (bContinue)
			{
				if (result == JOptionPane.YES_OPTION)
				{
					if (currentFile.equals(" "))
						showSave();
					else
						startSave(currentFile);
				}
				
				showOpen();
			}
		}
		else if ((JMenuItem)e.getSource() == fileMenu_Save)
		{
			// Save file
			
			if (currentFile.equals(" "))
				showSave();
			else
				startSave(currentFile);
		}
		else if ((JMenuItem)e.getSource() == fileMenu_SaveAs)
		{
			showSave();
		}
		else if ((JMenuItem)e.getSource() == fileMenu_Exit)
		{
			exit();
		}
		else if ((JMenuItem)e.getSource() == settingMenu_dataCategories)
		{
			// Open the dialog that allows the user to set the data volume
			// categories
			
			dataStatesForm = DataStatesForm.getInstance("Data volume categories", this);
		}
		else if ((JMenuItem)e.getSource() == settingMenu_ExcelPath)
		{
			// Open the dialog that allows the user to set the path for the MS Excel executable
			
			ExcelPathDialog excel = new ExcelPathDialog();
		}
		else if ((JMenuItem)e.getSource() == viewMenu_DrawingPane)
		{
			if (!viewMenu_DrawingPane.getState())
			{
				moduleVP.dispose();
				
				// Disable the tiling menu options
				
				viewMenu_TileVisHoriz.setEnabled(false);
				viewMenu_TileVisVerti.setEnabled(false);
				viewMenu_LinkMode.setEnabled(false);
			}
			else
				openModuleVP("Modules");
		}
		else if ((JMenuItem)e.getSource() == viewMenu_TileVisHoriz)
		{
			if (moduleVP != null)
				tileHorizontally();
		}
		else if ((JMenuItem)e.getSource() == viewMenu_TileVisVerti)
		{
			if (moduleVP != null)
				tileVertically();
		}
		else if ((JMenuItem)e.getSource() == viewMenu_LinkMode)
		{
			if (moduleVP != null)
			{
				if (viewMenu_LinkMode.getState())
					moduleVP.setLinkMode(true);
				else
					moduleVP.setLinkMode(false);
			}
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	* Tile all visualisation components horizontally within the drawing region
	*/
	
	private void tileHorizontally()
	{
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		DefaultVisualModule mod;
		ArrayList visualMods = new ArrayList();
		
		while (iter.hasNext())
		{
			mod = ((DefaultVisualModule)modules.get((String)iter.next()));
			if (mod.getMode() == DefaultVisualModule.VISUALISATION_MODE)
			{
				visualMods.add(mod);
			}
		}
		
		int width = (new Double(moduleVP.getDrawingCanvas().getSize().getWidth())).intValue()
			- (moduleVP.getInsets().left + moduleVP.getInsets().right - 4);
		int drawPaneHeight = (new Double(moduleVP.getDrawingCanvas().getSize().getHeight())).intValue()
			- (moduleVP.getInsets().top + moduleVP.getInsets().bottom + 11);
		int height;
		
		if (visualMods.size() > 0)
		{
			height = drawPaneHeight / visualMods.size();
			int originY;
			VisualModule vis;
			
			for (int i = 0; i < visualMods.size(); i++)
			{
				vis = (VisualModule)visualMods.get(i);
				originY = i * height;
				vis.setBounds(0, originY, width, height);
				vis.setDimension(width, height);
				vis.validate();
				vis.bringToFront();
			}
		}
	}
	
	/**
	* Tile all visualisation components vertically within the drawing region
	*/
	
	private void tileVertically()
	{
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		DefaultVisualModule mod;
		ArrayList visualMods = new ArrayList();
		
		while (iter.hasNext())
		{
			mod = ((DefaultVisualModule)modules.get((String)iter.next()));
			if (mod.getMode() == DefaultVisualModule.VISUALISATION_MODE)
			{
				visualMods.add(mod);
			}
		}
		
		int drawPaneWidth = (new Double(moduleVP.getDrawingCanvas().getSize().getWidth())).intValue()
			- (moduleVP.getInsets().left + moduleVP.getInsets().right - 4);
		int height = (new Double(moduleVP.getDrawingCanvas().getSize().getHeight())).intValue()
			- (moduleVP.getInsets().top + moduleVP.getInsets().bottom + 11);
			
		int width;
		
		if (visualMods.size() > 0)
		{
			width = drawPaneWidth / visualMods.size();
			int originX;
			VisualModule vis;
			
			for (int i = 0; i < visualMods.size(); i++)
			{
				vis = (VisualModule)visualMods.get(i);
				originX = i * width;
				vis.setBounds(originX, 0, width, height);
				vis.setDimension(width, height);
				vis.validate();
				vis.bringToFront();
			}
		}
	}
	
	public void showOpen()
	{
		int result;
		
		saveChooser = new JFileChooser();
		ExampleFileFilter filter = new ExampleFileFilter("atb", "HIVE files");
		saveChooser.setFileFilter(filter);
		result = saveChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			// Make sure that an .atb file has been chosen
			
			String fileName = saveChooser.getSelectedFile().getAbsolutePath();
			int extIndex = fileName.indexOf(".atb");
			
			if (extIndex != -1)
			{
				if (extIndex == (fileName.length() - 4))
				{
					// Go ahead and deserialise
					
					startLoad(fileName);
					
					// hook into FileHistory class
					
					fileHistory.insertPathname(fileName); 
				}
			}
		}
	}
	
	/**
	* Show the save dialog and process as appropriate
	*/
	
	private void showSave()
	{
		saveChooser = new JFileChooser();
		ExampleFileFilter filter = new ExampleFileFilter("atb", "HIVE files");
		saveChooser.setFileFilter(filter);
		
		int result;
		
		result = saveChooser.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			if (saveChooser.getSelectedFile().getAbsolutePath().indexOf(".atb") == -1)
				startSave(saveChooser.getSelectedFile().getAbsolutePath() + ".atb");
			else
				startSave(saveChooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	public void open(String filePath)
	{
		try
		{
			// Create a stream for reading
			
			fis = new FileInputStream(filePath);
			
			// Create an object that can read the stream
			
			inStream = new ObjectInputStream(fis);
			
			// Close the current views
			
			boolean bCanvasWasClosed = false;
			
			// Get the deserialised objects
			
			serialClass = (SerialClass)inStream.readObject();
			
			// Remove all exiting modules
			
			removeAllModules();
			
			// Set each of the new module's parents to be the current moduleVP
			
			setModuleParents();
			
			modules = serialClass.getModules();
			paintImportedPorts();
			modvp_Links = serialClass.getModuleVP_Links();
			modNames = serialClass.getModNames();
			
			// Refresh the display
			
			moduleVP.dispose();
			openModuleVP("Modules");
			
			// Release resources
			
			fis.close();
			inStream.close();
			
			// Make filePath represent the current path
			
			currentFile = filePath;
			
			// Show the path in the title bar
			
			setTitle("HIVE - " + filePath);
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found");
		}
		catch (InvalidClassException e)
		{
			e.printStackTrace();
		}
		catch (NotSerializableException e)
		{
			System.out.println("NotSerializableException occurred");
			System.out.println(e.getLocalizedMessage());
		}
		catch (IOException e)
		{
			System.out.println("IOException occurred");
		}
		catch (java.lang.ClassNotFoundException e)
		{
			System.out.println("ClassNotFoundException occurred");
		}
	}
	
	/**
	* Before desierialising, remove all current modules from the drawing canvas
	*/
	
	private void removeAllModules()
	{
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		
		VisualModule mod;
		
		while (iter.hasNext())
		{
			mod = ((VisualModule)modules.get((String)iter.next()));
			
			// Remove the focus module reference
			
			if (mod == moduleVP.getDrawingCanvas().getParentForm().getModWithFocus())
			{
				moduleVP.getDrawingCanvas().getParentForm().setDropCoords(0, 0);
			}
			
			moduleVP.remove(mod);
		}
	}
	
	/**
	* Before importing deserialised modules, set each modules parent form
	* to be the burrent moduleVP
	*/
	
	private void setModuleParents()
	{
		if (moduleVP == null)
			openModuleVP("Modules");
		
		Set set = serialClass.getModules().keySet();
		Iterator iter = set.iterator();
		
		VisualModule mod;
		
		while (iter.hasNext())
		{
			mod = ((VisualModule)serialClass.getModules().get((String)iter.next()));
			mod.setParentForm(moduleVP.getDrawingCanvas().getParentForm());
		}
	}
	
	/**
	* When deserialising modules, call each modules paint method
	* so that transient painted ports are recreated
	*/
	
	private void paintImportedPorts()
	{
		Set set = serialClass.getModules().keySet();
		Iterator iter = set.iterator();
		
		VisualModule mod;
		
		while (iter.hasNext())
		{
			mod = ((VisualModule)serialClass.getModules().get((String)iter.next()));
			mod.paintComponent(mod.getGraphics());
		}
	}
	
	/**
	* Serialise 'modules' so that the data flow visualisations can 
	* be saved (serialised) and re-opened (deserialised) later
	*/
	
	private void save(String filePath)
	{
		preSerialise();
		
		try
		{
			//  Create a stream for writing
			
			fos = new FileOutputStream(filePath);
			
			//  Next, create an object that can write to that file
			
			outStream = new ObjectOutputStream(fos);
			
			//  Save each object
			
			outStream.writeObject(serialClass);
			
			//  Finally, we call the flush() method for our object, which 
			// forces the data to get written to the stream:
			
			outStream.flush();
			
			fos.close();
			outStream.close();
			
			// Make filePath represent the current path
			
			currentFile = filePath;
			
			// Show the path in the title bar
			
			setTitle("HIVE - " + filePath);
			
			// If any modules need to to stuff after the serialisation process, then
			// do it here
			
			afterSerialise();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found");
		}
		catch (InvalidClassException e)
		{
			System.out.println("InvalidClassException occurred");
		}
		catch (NotSerializableException e)
		{
			System.out.println("NotSerializableException occurred");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("IOException occurred");
		}
	}
	
	/**
	* Some visual modules may need to carry out things
	* before being serialised, which cannot be done
	* in the writeObject() method
	*/
	
	private void preSerialise()
	{
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		
		while (iter.hasNext())
		{
			((DefaultVisualModule)modules.get((String)iter.next())).beforeSerialise();
		}
	}
	
	private void afterSerialise()
	{
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		
		while (iter.hasNext())
		{
			((DefaultVisualModule)modules.get((String)iter.next())).afterSerialise();
		}
	}
	
	public Link getSelected_ModVP_Link()
	{
		return selected_ModVP_Link;	
	}
	
	public void setSelected_ModVP_Link(Link selected_ModVP_Link)
	{
		if (this.selected_ModVP_Link != null)
			this.selected_ModVP_Link.setSelected(false);
		this.selected_ModVP_Link = selected_ModVP_Link;	
	}
	
	public void clearSelectedModule(String key)
	{
		// Unselect the module identified by key so that its drag
		// handles are no longer visible.
		
		if (modules.containsKey(key) == true)
		{	
			DefaultVisualModule selectedMod = (DefaultVisualModule)modules.get(key);
			
			selectedMod.setSelected(false);
			selectedMod.setInterfaceVisibility();
			selectedMod.repaint();
		}
	}
	
	public void clearAllModules()
	{
		// Unselect all rendered modules so that no drag handles are
		// visible.
		
		Set set = modules.keySet();
		Iterator iter = set.iterator();
		
		while (iter.hasNext())
		{
			clearSelectedModule((String)iter.next());
		}
	}
	
	public void removeModule(String key)
	{	
		if (modules.containsKey(key) == true)
		{	
			// Remove the module from the view.
			
			DefaultVisualModule mod = (DefaultVisualModule)modules.get(key);
			
			JScrollPane scrollPane;
			scrollPane = (JScrollPane)moduleVP.getContentPane();
			
			JViewport viewPort = scrollPane.getViewport();
			DrawingCanvas layeredPane = (DrawingCanvas)viewPort.getComponent(0);
				
			// Remove the module from the view
				
			layeredPane.remove(mod);
			
			// Remove the focus module reference
			
			if (mod == moduleVP.getDrawingCanvas().getParentForm().getModWithFocus())
			{
				moduleVP.getDrawingCanvas().getParentForm().setDropCoords(0, 0);
			}
				
			// If the deleted module was linked to any other modules
			// then delete these links as well
			
			deleteLinks(key);
					
			layeredPane.repaint();
			
			// Remove the HashMap mapping.
			
			modules.remove(key);
			
			// If this was the module that has its port highlighted, set 
			// moduleWithPortHighlight to null
			
			if (moduleWithPortHighlight != null)
				if (moduleWithPortHighlight.equals(key))
					moduleWithPortHighlight = null;
		}
	}
	
	private void removeLinkPortHighlight(ModulePort fromPort, ModulePort toPort)
	{
		// When a module has been deleted, if module ports
		// are highlighted as a result of a link that was connected
		// to the deleted module, then remove these highlights
		
		// First derive a unique identifier for each of the selected ports
		// Need to take the module key into account because the port key is only
		// unique within that module
		
		String modvpFromKey = "";
		String modvpToKey = "";
		
		if (modvp_link_Highlight_FromPort != null)
			modvpFromKey = modvp_link_Highlight_FromPort.getAbsoluteKey();
		if (modvp_link_Highlight_ToPort != null)
			modvpToKey = modvp_link_Highlight_ToPort.getAbsoluteKey();
		
		// Now derive a unique identifier for each port connected to a deleted link
		// Again, take the module key into account
		
		String DelFromKey = "x";
		String DelToKey = "x";
		
		if (fromPort != null)
			DelFromKey = fromPort.getAbsoluteKey();
		if (toPort != null)
			DelToKey = toPort.getAbsoluteKey();
		
		// If the both port identifiers for the deleted link match the pair of
		// identifiers for selected ports, then remove the port highlighting
		
		if((modvpFromKey.equals(DelFromKey)) && (modvpToKey.equals(DelToKey)))
		{
			if (modvp_link_Highlight_FromPort != null)
			{
				modvp_link_Highlight_FromPort.setLinkHighlight(false);
				modvp_link_Highlight_FromPort = null;
			}
			if (modvp_link_Highlight_ToPort != null)
			{
				modvp_link_Highlight_ToPort.setLinkHighlight(false);
				modvp_link_Highlight_ToPort = null;
			}
		}
	}
	
	private void deleteLinks(String modKey)
	{
		// Given the key of a module that's to be deleted, find
		// any links associated with that module and delete them
		
		VisualModule delMod = (VisualModule)modules.get(modKey);
		VisualModule toMod, fromMod;
		DrawingCanvas drawPane = delMod.getParentForm().getDrawPane();
		HashMap links = drawPane.getLinks();
		Link link;
		String linkKey = null;
		Set set = links.keySet();
		Iterator iter = set.iterator();
		ArrayList delList = new ArrayList();
		while (iter.hasNext())
		{
			linkKey = (String)iter.next();
			link = ((Link)links.get(linkKey));
			if (link != null)
			{
				fromMod = link.getFromModule();
				toMod = link.getToModule();
				
				if (fromMod != null)
				{
					if (fromMod.getKey().equals(modKey))
						delList.add(linkKey);
				}
				if (toMod != null)
				{
					if (toMod.getKey().equals(modKey))
						delList.add(linkKey);
				}
			}
		}
		delList.trimToSize();
		ModulePort fromPort, toPort;
		for (int i = 0; i < delList.size(); i++)
		{
			// First remove any link-port highlights associated with the link
			
			fromPort = ((Link)links.get((String)delList.get(i))).getFromPort();
			toPort = ((Link)links.get((String)delList.get(i))).getToPort();
			removeLinkPortHighlight(fromPort, toPort);
			
			// Unregister the Observer modules associated with the link
			
			((Link)links.get((String)delList.get(i))).removeObservers();
			
			// Now remove the stored reference to the link
			
			links.remove((String)delList.get(i));
		}
	}
	
	public void addModule(String key, DefaultVisualModule module)
	{
		// Add an algorithmic module to the modules collection.
		
		modules.put(key, module);
	}
	
	public static HashMap getModules()
	{
		// Accessor method for retrieving the set of references to all
		// rendered modules.
		
		return modules;	
	}
	
	public DefaultVisualModule getModuleWithPortHighlight()
	{
		// Return the reference to the module that has a highlighted port.
		
		if (moduleWithPortHighlight != null)
		{
			if (modules.containsKey(moduleWithPortHighlight) == true)
			{
				return (DefaultVisualModule)modules.get(moduleWithPortHighlight);
			}
			else
				return null;
		}
		else
			return null;
	}
	
	public void setModuleWithPortHighlight(String smoduleWithPortHighlight)
	{
		// Store the key of the module that currently has one of its ports highlighted
		
		moduleWithPortHighlight = smoduleWithPortHighlight;
	}
	
	public void setPortsWithLinkHighlight(ModulePort fPort, ModulePort tPort, DrawingForm drawForm)
	{
		if (modvp_link_Highlight_FromPort != null)
			modvp_link_Highlight_FromPort.setLinkHighlight(false);
		if (modvp_link_Highlight_ToPort != null)
			modvp_link_Highlight_ToPort.setLinkHighlight(false);
			
		modvp_link_Highlight_FromPort = fPort;
		modvp_link_Highlight_ToPort = tPort;
	}
	
	public static void IncrementModuleInstances(String sModName)
	{
		// When a user adds another module, increment the number
		// of instances of this module, e.g. Spring Model = 3.
		
		int count = 0;
		if (modNames.containsKey(sModName))
		{
			count = ((Integer)modNames.get(sModName)).intValue();
			modNames.remove(sModName);
			count += 1;
			modNames.put(sModName, new Integer(count));
		}
		else
		{
			modNames.put(sModName, new Integer(1));
		}
	}
	
	public static int getNumInstances(String sModName)
	{
		// When a user drags another module onto one of the drawing areas
		// this function is called to determine how many other instances 
		// of the module exists. This number is used to show the enumeration 
		// in the modules title.
		
		int count = 0;
		
		if (modNames.containsKey(sModName))
			count = ((Integer)modNames.get(sModName)).intValue();
		
		return count;
	}
	
	public void setEnabled(boolean enabled, String nameEnabled)
	{
		// Override the JComponent setEnabled method to simulate the 
		// behaviour of modalality in any forms where this is requierd.
		
		
		mdiToolBar.setEnabled(enabled);
		EnableDisableForms(enabled, nameEnabled);
	}
	
	private void EnableDisableForms(boolean enabled, String nameEnabled)
	{
		// Enable or disable all MDI children except the one identified
		// by the title nameEnabled.
		
		int count;
		if (enabled == false)
		{
			for (count = 0; count < desktop.getAllFrames().length; count++)
			{
				if (desktop.getAllFrames()[count].getTitle().equals(nameEnabled) != true)
					desktop.getAllFrames()[count].setEnabled(false);
			}
		}
		else
		{
			for (count = 0; count < desktop.getAllFrames().length; count++)
			{
				desktop.getAllFrames()[count].setEnabled(false);
			}
		}
	}
	
 	public void openModuleVP(String title) 
 	{
		// Open the form for data flow visual programming of algorithms and
		// visual components.
		
		moduleVP = ModuleVP.getInstance(title, this);
		try
		{
			moduleVP.setMaximum(false);
		}
		catch (PropertyVetoException e){}
		moduleVP.setAppearance();
		moduleVP.moveToFront();
		
		// Set the link mode
		
		if (viewMenu_LinkMode.getState())
			moduleVP.setLinkMode(true);
		else
			moduleVP.setLinkMode(false);
        }
	
	public void openStartUpOptions(String title) 
 	{
		// If the tool has been opened without pre-specifying a file
		// then open the start-up options dialogue.
		
		startUpOptions = StartUpOptions.getInstance(title, this, fileHistory);
        }
	
	public JDesktopPane getDesktop()
	{
		return desktop;	
	}
	
	public JToolBar getToolBar()
	{
		return toolBar;	
	}
	
	public void setToolBar(JToolBar tBar)
	{
		toolBar = tBar;	
	}
	
	public static MDIToolBar getToolBarClass()
	{
		return mdiToolBar;
	}
	
	public static ModuleVP getModuleVP()
	{
		return moduleVP;
	}
	
	public static void setModuleVP(ModuleVP modVP)
	{
		moduleVP = modVP;	
	}
	
	public String getDraggedModuleName()
	{
		// Get the name of the label that was dragged from the toolbar.
		// This label represents a module in visual programming.
		
		return DragModuleName;	
	}
	
	public void setDraggedModuleName(String modName)
	{
		// Set the name of the label that was dragged from the toolbar.
		// This label represents a module in visual programming.
		
		DragModuleName = modName;
	}
	
	/*
	*   Links between visual modules are stored in modvp_Links.
	*   Each instance of DrawingCanvas also has a links collection which is merely
	*   a reference to one of these collections
	*/
	
	public HashMap getModvp_Links()
	{
		return modvp_Links;	
	}
	
	/**
	* When objects are reconstructed after deserialisation,
	* this is called to enable them to re-reference the
	* MDI form
	*/
	
	public static Mdi getInstance()
	{
		return instance;
	}
	
	// --- Implementation of FileHistory.IFileHistory interface ----------------
	
	/**
	* Get the application name to identify the configuration file in the
	* the USER_HOME directory. This name should be unique in this directory.
	* 
	* @return the application name
	*/
	
	public String getApplicationNames() 
	{
		return getTitle();
	}
	
	/**
	* Get a handle to the frame's file menu.
	* 
	* @return the frame's file menu
	*/
	
	public JMenu getFileMenus() 
	{
		return menuBar.getMenu(0);
	}
	
	/**
	* Return the size of the main application frame.
	* It is used to center the file history maintenance window.
	* 
	* @return the frame's size
	*/
	
	public Dimension getSizes() 
	{
		return super.getSize();
	}
	
	/**
	* Return the main application frame.
	* It is used to center the file history maintenance window.
	* 
	* @return the main GUI frame
	*/
	public JFrame getFram() 
	{
		return this;
	}
	
	/**
	* Simulate a load file activity.
	* 
	* @param path   the pathname of the loaded file
	*/
	public void loadFiles(String path) 
	{
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		startLoad(path);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		String filename = path.substring(path.lastIndexOf(System.getProperty("file.separator"))+1,
                              path.length());
			      
		if (filename.length() > 30) {
			filename = filename.substring(0, 30) + "...";
		}
	}
	
	// -------------------------------------------------------------------------
	
	/**
	* Before we serialise, load the feedback form and create a new thread
	*/
	
	private void startSave(String sFile)
	{
		bSave = true;
		sTheFile = sFile;
		
		// Get an instance of the feedback for before starting the new thread of
		// execution. But do not show it yet
		
		feedbackForm = FeedbackForm.getInstance("HIVE", this);
		
		thread = new Thread(this);
		thread.start();
		
		// Now show the feedback form
		
		feedbackForm.showForm();
	}
	
	/**
	* Before we deserialise, load the feedback form and create a new thread
	*/
	
	private void startLoad(String sFile)
	{
		bSave = false;
		sTheFile = sFile;
		
		// Get an instance of the feedback for before starting the new thread of
		// execution. But do not show it yet
		
		feedbackForm = FeedbackForm.getInstance("HIVE", this);
		
		thread = new Thread(this);
		thread.start();
		
		// Now show the feedback form
		
		feedbackForm.showForm();
	}
	
	public void run()
	{
		// If moduleVP is open, close it to stop bugs
		
		boolean bFormOpen = false;
		
		// Extract the file name from the path
		
		String name = null;
		if (sTheFile.indexOf("\\") > -1)
			name = sTheFile.substring(sTheFile.lastIndexOf("\\") + 1, sTheFile.length());
		
		if (moduleVP != null)
		{
			bFormOpen = moduleVP.formIsOpen();
			moduleVP.dispose();
		}
		
		if (bSave)
		{
			feedbackForm.setText1("Saving " + name);
			feedbackForm.setText2("Please wait...");
			save(sTheFile);
		}
		else
		{
			feedbackForm.setText1("Loading " + name);
			feedbackForm.setText2("Please wait...");
			open(sTheFile);
		}
		
		// Close the feedback form
		
		feedbackForm.hide();
		feedbackForm.dispose();
		
		// Re-open moduleVP if it was laready open before the
		// load/save opration
		
		if (bFormOpen)
			openModuleVP("Modules");
		
		thread = null;
	}
}

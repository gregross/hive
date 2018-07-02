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
 * VisualModule
 *
 * Class represents a visual module that can be dragged about on the screen.
 * A module represents a visualisation such as a scatter plot or an algorithm
 * such as a Spring Model.
 *
 *  @author Greg Ross
 */
package parent_gui;
 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.*;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.NullPointerException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Box.Filler;
 
public class VisualModule extends JPanel implements KeyListener, FocusListener
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	int moduleRenderWidth;
    	int moduleRenderHeight;	
	int lastRenderHeight = 0;
	int lastRenderWidth = 0;
	
	static final int SMALLEST_MODULE_HEIGHT = 30;
	static final int SMALLEST_MODULE_WIDTH = 30;
	
	int mouseDiffX, mouseDiffY = 0;
	
	// Determine whether this module is selected.
	
	private boolean bSelected = true;
	
	// Determine whether the user is resizing a module.
	
	boolean bResizing = false;
	
	// Co-ordinates and dimensions for grab handles.
	
	int grabHandleWidth = 6;
	int grabHandleHeight = 6;
	
	static final int NW_HANDLE = 0;
	static final int N_HANDLE = 1;
	static final int NE_HANDLE = 2;
	static final int E_HANDLE = 3;
	static final int SE_HANDLE = 4;
	static final int S_HANDLE = 5;
	static final int SW_HANDLE = 6;
	static final int W_HANDLE = 7;
	
	int resizeDirection = -1;
	int oldCursorX = 0;
	int oldCursorY = 0;
	
	// The parent MDI form
	
	private static Mdi mdiForm;
	
	// The parent drawing surface
	
	private static DrawingCanvas drawPane;
	
	// Instance of the JInternalFrame descendant that holds the drawPane and consequently this component
	
	private static DrawingForm parent;
	
	// The class that acts as the mouse listener for this object
	
	private VisualModuleMouseListener visModListener;
	
	// The ID of this module, as referenced from collections.
	
	private String key = null;
	
	// Is Shift key pressed?
	
	private boolean bShiftKeyPressed = false;
	
	// Is control key pressed?
	
	private boolean bControlKeyPressed = false;
	
	// Label for showing the title of the module, e.g. Filter
	
	private JLabel jTitle;
	
	// Mouse listener for the title label
	
	private MouseInputAdapter mouselistener;
	
	// The painted 'in' ports
	
	transient private ArrayList inPorts;
	
	// The painted 'out' ports
	
	transient private ArrayList outPorts;
	
	// Determine when the mouse is over a port and which port
	// none = 0
	// input = 1
	// output = 2
	
	private int mouseOverInPort = -1;
	private int mouseOverOutPort = -1;
	private String portWithHighlight = null;
	
	// The number of input and output ports
	
	private int numInPorts;
	private int numOutPorts;
	
	// All port instances
	
	private HashMap ports = null;
	
	// Is a port highlighted because a link is being dragged over it?
	
	private int dragOverHighlight = -1;
	
	// Are the swing controls hidden?
	
	private boolean bControlsHidden = false;
	
	transient private Ellipse2D.Double el;
	
	// Determine whether the rename dialog is laready open
	
	private boolean renameOpen = false;
	
	public VisualModule(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super();
		numInPorts = 0;
		numOutPorts = 0;
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		parent = drawPane.getParentForm();
		init();
	}
	
	public void setDimension(int width, int height)
	{
		moduleRenderHeight = height;
		moduleRenderWidth = width;	
		setBounds((new Double(getLocation().getX())).intValue(), 
			(new Double(getLocation().getY())).intValue(), width, height);
	}
	
	private void init()
	{
		visModListener = new VisualModuleMouseListener(this, mdiForm);
		addMouseListener(visModListener);
		addMouseMotionListener(visModListener);
		addKeyListener(this);
		addFocusListener(this);
		
		// Set its appearance according to the type of module
		// i.e. visual or non-visual.
		
		showTitle();
		setOpaque(true);
			
		// Add a border.
		
		setBorder(BorderFactory.createLineBorder(Color.gray));
	}
	
	public VisualModuleMouseListener getMotionListener()
	{
		return visModListener;	
	}
	
	private void showTitle()
	{
		// Add a label showing the title of the module, e.g. Filter 1
		
		String modTitle = mdiForm.getDraggedModuleName() + " " 
			+ mdiForm.getNumInstances(mdiForm.getDraggedModuleName());
		
		JPanel lblPanel = new JPanel();
		lblPanel.setLayout(new BorderLayout());
		Filler filler = new Filler(new Dimension(4, 4), new Dimension(4, 4), new Dimension(4, 4));
		lblPanel.add(filler, BorderLayout.NORTH);
		lblPanel.setOpaque(false);
		
		jTitle =  new JLabel(modTitle, JLabel.CENTER);
		jTitle.setName("modLabel");
		
		addLabelListener();
		
		setLayout(new BorderLayout(10, 10));
		lblPanel.add(jTitle, BorderLayout.CENTER);
		add(lblPanel, BorderLayout.NORTH);
	}
	
	private void addLabelListener()
	{
		// Add a mouse listener to the label so that when the user double-clicks
		// it, a text box appears so that he/she can rename it
		// Also, call the VisualModule's mouse listener events so that the
		// module can be repositioned by dragging the label
		
		MouseInputAdapter mouselistener = (new MouseInputAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if ((e.getClickCount() == 2) && (!renameOpen))
				{
					// Rename the module
					
					renameOpen = true;
					showRenameDialog();
				}
				else if (e.getClickCount() == 1)
				{
					// Select the module visually
					
					bringToFront();
					setFocus();
					
					// Store the click location so that when dragging
					// the mouse remains in a position relative to the module
					
					visModListener.mousePressed(e);
				}
			}
			
			public void mouseDragged(MouseEvent e)
			{
				// Reposition the module as the user
				// drags the label
				
				visModListener.mouseDragged(e);
			}
			
			public void mouseMoved(MouseEvent e)
			{
				// Delegate mouse motion events to the
				// visual module's mouse listener
				
				Double x = new Double(e.getX());
				Double y = new Double(e.getY() + 4);
				visModListener.checkMovement(x, y);
			}
		});
		
		jTitle.addMouseListener(mouselistener);
		jTitle.addMouseMotionListener(mouselistener);
	}
	
	/**
	* Open a modal dialog to allow the user
	* to enter a new name for the visual module
	*/
	
	private void showRenameDialog()
	{
		// Allow the user to enter a name
		
		String caption = (String)JOptionPane.
		showInputDialog(this,
		"Please enter a name for this component.",
		"HIVE",
		JOptionPane.PLAIN_MESSAGE,
		new ImageIcon(),
		null,
		jTitle.getText());
		
		if (caption != null)
		{
			if (caption.length() > 0)
			{
				jTitle.setText(caption);
			}
		}
		
		renameOpen = false;
	}
	
	public void setFocus()
	{
		requestFocus();	
	}
	
	public void setSelected(boolean bSelected)
	{
		// If the module is selected then draw the drag handles.
		//Else, remove the drag handles.
		
		this.bSelected = bSelected;	
	}
	
	public boolean getSelected()
	{
		// Determine whether the module has been selected and 
		// the drag handles are visible.
		
		return 	bSelected;
	}
	
	public String getKey()
	{
		return key;	
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public ArrayList getInPorts()
	{
		return inPorts;	
	}
	
	public ArrayList getOutPorts()
	{
		return outPorts;	
	}
	
	public void paintComponent(Graphics g)
	{
		if (g != null)
		{
			super.paintComponent(g);
			if ((bSelected == true) && (parent.getLinkMode() == false))
			{
				drawHandles(g);
			}
		}
		renderPorts(g);
	}
	
	public void setInterfaceVisibility()
	{
		// When in linking mode, all swing controls apart from the
		// default, should be made invisible. This method is called from
		// Mdi.clearAllModules()
		
		if (parent.getLinkMode() == true)
		{
			if (!bControlsHidden)
			{
				bControlsHidden = true;
				hideControls();
			}
		}
		else
		{
			if (bControlsHidden)
			{
				bControlsHidden = false;
				showControls();
			}
		}
	}
	
	private void hideControls()
	{
		// when in link mode, all swing controls should be hidden
		// Start the index at one beecause this is the default label
		// of the module
		
		for (int i = 1; i < getComponentCount(); i++)
			getComponent(i).setVisible(false);
	}
	
	private void showControls()
	{
		// when in link mode, all swing controls should be hidden
		// Start the index at one beecause this is the default label
		// of the module
		
		for (int i = 1; i < getComponentCount(); i++)
			getComponent(i).setVisible(true);
	}
	
	private void drawHandles(Graphics g)
	{
		// If the module has been clicked (selected), then draw the
		// drag handles
		
		// Draw a line border 4 pixels from the edge.
		
		g.setColor(Color.black);
		g.drawRect(4, 4, getWidth() - 8, getHeight() - 8);
			
		// Draw the grab handles
		// Top-left
		g.fillRect(2, 2, grabHandleWidth, grabHandleHeight);
		// Top-middle
		g.fillRect((getWidth() / 2) - (grabHandleWidth / 2), 2, grabHandleWidth, grabHandleHeight);
		// Top-right
		g.fillRect(getWidth() - (grabHandleWidth + 2), 2, grabHandleWidth, grabHandleHeight);
		// Right-middle
		g.fillRect(getWidth() - (grabHandleWidth + 2), (getHeight() / 2) - 2, grabHandleWidth, grabHandleHeight);
		// Bottom-right
		g.fillRect(getWidth() - (grabHandleWidth + 2), getHeight() - (grabHandleWidth + 2),
			grabHandleWidth, grabHandleHeight);
		// Bottom-middle
		g.fillRect((getWidth() / 2) - (grabHandleWidth / 2), 
			getHeight() - (grabHandleWidth + 2), grabHandleWidth, grabHandleHeight);
		// Bottom-left
		g.fillRect(2, getHeight() - (grabHandleWidth + 2), grabHandleWidth, grabHandleHeight);
		// Left-middle
		g.fillRect(2, (getHeight() / 2) - (grabHandleWidth / 2), grabHandleWidth, grabHandleHeight);
	}
	
	private void renderPorts(Graphics g)
	{
		// This modules I/O port(s)
			
		if (numOutPorts > 0)
			drawOutputPorts(g);
			
		if (numInPorts > 0)
			drawInputPorts(g);	
	}
	
	public int MouseOverInPort()
	{
		// Determine which port (if any) the mouse is over
		// -1 = none.
		
		return mouseOverInPort;	
	}
	
	public int MouseOverOutPort()
	{
		// Determine which port (if any) the mouse is over
		// -1 = none.
		
		return mouseOverOutPort;	
	}
	
	public void MouseOverInPort(int iMouseOverInPort)
	{
		// Determine which port (if any) the mouse is over
		// -1 = none.
		
		boolean bChanged = true;
		if (iMouseOverInPort == mouseOverInPort)
			bChanged = false;
			
		mouseOverInPort = iMouseOverInPort;
		if ((iMouseOverInPort == -1) && (mouseOverOutPort == -1))
		{
			mdiForm.setModuleWithPortHighlight(null);
			portWithHighlight = null;	
		}
		else
			mdiForm.setModuleWithPortHighlight(key);
		
		if (bChanged)
			repaint();
	}
	
	public void MouseOverOutPort(int iMouseOverOutPort)
	{
		// Determine which port (if any) the mouse is over
		// -1 = none.
		
		boolean bChanged = true;
		if (iMouseOverOutPort == mouseOverOutPort)
			bChanged = false;
			
		mouseOverOutPort = iMouseOverOutPort;
		if ((iMouseOverOutPort == -1) && (mouseOverInPort == -1))
		{
			mdiForm.setModuleWithPortHighlight(null);
			portWithHighlight = null;
		}
		else
			mdiForm.setModuleWithPortHighlight(key);
			
		if (bChanged)
			repaint();
	}
	
	public String getPortWithHighlight()
	{
		// Return the key of the port that currently has the highlight.
		
		return portWithHighlight;
	}
	
	private ArrayList calculatePortOrigin(int hc, int numPorts)
	{
		// Given that ports are drawn symmetrically from the middle
		// upwards, determine the Y-origin for a port given the total 
		// number of (input or output)ports
		
		ArrayList yCoords = new ArrayList(numPorts - 1);
		int y, temp;
			
		for (int i = numPorts; i > 1; i = i - 2)
		{
			temp = (int)(((i / 2f) * hc) + ((i / 2f) * 2));
			y = (getHeight() / 2) - temp;
			yCoords.add(new Integer(y));
			y = (getHeight() / 2) + temp - hc;
			yCoords.add(new Integer(y));
		}
		
		yCoords = sortPorts(yCoords, numPorts, hc);
		
		return 	yCoords;
	}
	
	private ArrayList sortPorts(ArrayList ports, int numPorts, int hc)
	{
		// Given that calculatePortOrigin produces an unordered set of Y
		// values, this function is used to sort the array in ascending
		// order. The complexity of this routine is O(N/2)
		
		int numSwapIters = 0;
		boolean bOdd = false;
		
		// Determine the number of swap iteration required, from whether the
		// number of in or out ports is odd or even
		
		if (GuiUtil.oddNumber(numPorts))
		{
			bOdd = true;
			if (numPorts > 2)
				numSwapIters = ports.size() / 2;
			else
				return ports;
		}
		else
		{	
			if (numPorts >= 4)
				numSwapIters = (ports.size() / 2) - 1;
			else
				return ports;
		}
		
		// Apply the sort
		
		for (int i = 1; i <= numSwapIters; i++)
		{
			ports.add(ports.size() - (i - 1), ports.get(i));
			ports.remove(i);
			ports.trimToSize();
		}
		
		// If the number of ports is odd, then the middle port is not
		// included in the array. Add this now
		
		if (bOdd)
		{
			ports.add(numPorts / 2, new Integer((getHeight() / 2) - (hc / 2)));	
		}
		
		return ports;
	}
	
	public void addPorts(ArrayList portArray)
	{
		if (ports == null)
			ports = new HashMap();
			
		if (portArray != null)
		{
			int inCount = 0;
			int outCount = 0;
			ModulePort port = null;
			for (int count = 0; count < portArray.size(); count++)
			{
				port = (ModulePort)portArray.get(count);
				
				if (port != null)
				{
					// Determine which ports are input and output
					// and how many of each
					
					String sKey = null;
					if ((port.getPortMode() == ScriptModel.INPUT_PORT) ||
					(port.getPortMode() == ScriptModel.TRIGGER_PORT_IN) ||
					(port.getPortMode() == ScriptModel.MULTI_PORT_IN))
					{
						sKey = "i" + (new Integer(inCount)).toString();
						inCount++;
					}
					else
					{
						sKey = "o" + (new Integer(outCount)).toString();
						outCount++;
					}
					
					// Copy each port across to the local HashMap collection
					
					if (!ports.containsKey(sKey))
					{
						ports.put(sKey, port);
					}
				}
			}
			numInPorts = inCount;
			numOutPorts = outCount;
		}
		
		if (ports.size() == 0)
			ports = null;
	}
	
	private void drawInputPorts(Graphics g)
	{	
		// Draw ports from the centre LHS outwards
		
		int width = 12;
		int height = 12;
		
		if (inPorts != null)
			inPorts.clear();
		inPorts = new ArrayList(numInPorts);
		
		ArrayList yCoords = null; 
		
		if (numInPorts > 1)
			yCoords = calculatePortOrigin(height, numInPorts);
		
		for (int i = 0; i < numInPorts; i++)
		{
			Graphics2D g2 = (Graphics2D)g;
			
			int x = 0;
			int y = 0;
			
			if ((numInPorts == 1) || (i >= yCoords.size()))
				y = (getHeight() / 2) - (height / 2);
			else
			{
				y = ((Integer)yCoords.get(i)).intValue();
			}
			
			ModulePort port = null;
			Shape originalClip = null;
			boolean blinkInvalid = false;
			Object oldRenderValue = null;
			
			if (g2 != null)
			{
				// Set the clipping region
				
				originalClip = g2.getClip();
				g2.clip(new Rectangle2D.Double(x, y, width, height));
				
				// Set anti-aliasing
				
				oldRenderValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				// Set the highlight according to whether the mouse pointer is currently hovering
				// above the port (about to drag)
				
				port = null;
				g2.setColor(Color.gray);
				if (ports != null)
				{
					String dKey = "i" + (new Integer(i)).toString();
					if (ports.containsKey(dKey))
					{
						port = ((ModulePort)ports.get(dKey));
					}
				}
				
				if ((mouseOverInPort == i))
				{
					g2.setColor(Color.blue);
					portWithHighlight = "i" + (new Integer(i)).toString();
				}
				else
				{
					if (port != null)
					{
						dragOverHighlight = port.getDragHighlight();
						if (port.getLinkHighlight())
							g2.setColor(Color.blue);
						
						if (port.getCompatHighlight())
							g2.setColor(Color.magenta);
					}
				}
				
				// If the user is dragging a link over another module's port
				// give feedback on whether the link would be valid
				
				blinkInvalid = false;
				
				if (port != null)
				{
					if (dragOverHighlight == ScriptModel.VALID_LINK_HIGHLIGHT)
						g2.setColor(Color.green);
					else if (dragOverHighlight == ScriptModel.INVALID_LINK_HIGHLIGHT)
					{
						g2.setColor(Color.red);
						blinkInvalid = true;
					}
				}
			}
			
			// Add the shapes, representing ports to the ArrayList so that we can check
			// in the mouse listener whether the mouse is over a port
				
			el = new Ellipse2D.Double(x, y, width, height);
			
			inPorts.add(el);
			
			// Don't draw the port if it is off the top/bottom of the module
			// and if we are in link mode
			
			if ((y >= 0) && (y < (getHeight() - height)) && (parent.getLinkMode() == true) && (g2 != null))
			{
				g2.fill((Ellipse2D.Double)inPorts.get(i));
				
				// If the link is invalid then cross out the port
				
				if (blinkInvalid)
				{
					g2.clip((Ellipse2D.Double)inPorts.get(i));
					g2.setColor(Color.black);
					Line2D.Double line;
					line = new Line2D.Double(x, y, x + width, y + height);
					g2.draw(line);
					line = new Line2D.Double(x + width, y, x, y + height);
					g2.draw(line);
				}
				
				// Render the port label
				
				if (port != null)
				{
					g2.setClip(originalClip);
					renderPortLabel(port, x, y, width, height, g2);
				}
			}
			
			// Restore
			
			if (g2 != null)
			{
				g2.setClip(originalClip);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldRenderValue);
			}
		}
	}
	
	private void drawOutputPorts(Graphics g)
	{
		// Draw ports from the centre RHS outwards
		
		int width = 12;
		int height = 12;
		
		if (outPorts != null)
			outPorts.clear();
		outPorts = new ArrayList(numOutPorts);
		
		ArrayList yCoords = null; 
		
		if (numOutPorts > 1)
			yCoords = calculatePortOrigin(height, numOutPorts);
		
		for (int i = 0; i < numOutPorts; i++)
		{
			Graphics2D g2 = (Graphics2D)g;
			
			int x = getWidth() - width;
			int y = 0;
			
			if ((numOutPorts == 1) || (i >= yCoords.size()))
				y = (getHeight() / 2) - (height / 2);
			else
			{
				y = ((Integer)yCoords.get(i)).intValue();
			}
			
			// Set the clipping region
			
			ModulePort port = null;
			Shape originalClip = null;
			boolean blinkInvalid = false;
			Object oldRenderValue = null;
			
			if (g2 != null)
			{
				originalClip = g2.getClip();
				g2.clip(new Rectangle2D.Double(x, y, width, height));
				
				// Set anti-aliasing
				
				oldRenderValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				// Set the highlight according to whether the mouse pointer is currently hovering
				// above the port (about to drag)
				
				port = null;
				g2.setColor(Color.gray);
				if (ports != null)
				{
					String dKey = "o" + (new Integer(i)).toString();
					if (ports.containsKey(dKey))
					{
						port = ((ModulePort)ports.get(dKey));
					}
				}
				
				if (mouseOverOutPort == i)
				{
					g2.setColor(Color.blue);
					portWithHighlight = "o" + (new Integer(i)).toString();
				}
				else
				{
					if (port != null)
					{
						dragOverHighlight = port.getDragHighlight();
						if (port.getLinkHighlight())
							g2.setColor(Color.blue);
						
						if (port.getCompatHighlight())
							g2.setColor(Color.magenta);
					}
				}
				
				// If the user is dragging a link over another module's port
				// give feedback on whether the link would be valid
				
				blinkInvalid = false;
				
				if (port != null)
				{
					if (dragOverHighlight == ScriptModel.VALID_LINK_HIGHLIGHT)
						g2.setColor(Color.green);
					else if (dragOverHighlight == ScriptModel.INVALID_LINK_HIGHLIGHT)
					{
						g2.setColor(Color.red);
						blinkInvalid = true;
					}
				}
				
				// Add the shapes, representing ports to the ArrayList so that we can check
				// in the mouse listener whether the mouse is over a port
				
			}
			
			el = new Ellipse2D.Double(x, y, width, height);
			outPorts.add(el);
			
			// Don't draw the port if it is off the top/bottom of the module
			// and if we are in link mode
			
			if ((y >= 0) && (y < (getHeight() - height)) && (parent.getLinkMode() == true) &&
				(g2 != null))
			{
				g2.fill((Ellipse2D.Double)outPorts.get(i));
				
				// If the link is invalid then cross out the port.
				
				if (blinkInvalid)
				{
					g2.clip((Ellipse2D.Double)outPorts.get(i));
					g2.setColor(Color.black);
					Line2D.Double line;
					line = new Line2D.Double(x, y, x + width, y + height);
					g2.draw(line);
					line = new Line2D.Double(x + width, y, x, y + height);
					g2.draw(line);
				}
				
				// Render the port label.
				
				if (port != null)
				{
					g2.setClip(originalClip);
					renderPortLabel(port, x, y, width, height, g2);
				}
			}
			
			// Restore
			
			if (g2 != null)
			{
				g2.setClip(originalClip);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldRenderValue);
			}
		}
	}
	
	private void renderPortLabel(ModulePort port, int x, int y, int width, int height, Graphics2D g2)
	{
		// Draw the port's label next to it
		
		Font f = new Font("Arial", Font.PLAIN,  12);
		g2.setFont(f);
		g2.setColor(Color.black);
		
		// Get metrics of the font for setting the clipping region
		
		FontMetrics fm = g2.getFontMetrics();
		Shape originalClip = g2.getClip();
		Rectangle2D.Float bounds = (Rectangle2D.Float)fm.getStringBounds(port.getPortLabel(), g2);
		int textHeight = (new Float(bounds.getHeight())).intValue();
		int textWidth = (new Float(bounds.getWidth())).intValue();
		
		int textY = (y + ((height - fm.getAscent()) / 2) + fm.getAscent() - 2);
		
		if ((port.getPortMode() == ScriptModel.INPUT_PORT) ||
		(port.getPortMode() == ScriptModel.TRIGGER_PORT_IN) ||
		(port.getPortMode() == ScriptModel.MULTI_PORT_IN))
		{
			// The text should be to the right of the port
			
			Rectangle2D.Float clipArea = new Rectangle2D.Float(x + width, y, textWidth, textHeight);
			g2.clip(clipArea);
			g2.drawString(port.getPortLabel(), (x + width + 1), textY);
		}
		else
		{
			// The text should be to the left of the port
			
			Rectangle2D.Float clipArea = new Rectangle2D.Float((x - textWidth), y, textWidth, textHeight);
			g2.clip(clipArea);
			g2.drawString(port.getPortLabel(), (x - textWidth), textY);
		}
		g2.setClip(originalClip);
	}
	
	public HashMap getPorts()
	{
		return ports;
	}
	
	public ModulePort getOutPort(int index)
	{
		// Given the index of an output port, return that port
		
		if (ports.get("o" + index) != null)
			return (ModulePort)ports.get("o" + index);
		else
			return null;
	}
	
	public ModulePort getInPort(int index)
	{
		// Given the index of an output port, return that port
		
		if (ports.get("i" + index) != null)
			return (ModulePort)ports.get("i" + index);
		else
			return null;
	}
	
	public void bringToFront()
	{
		// Bring the selected module to the front
		
		JLayeredPane layeredPane = (JLayeredPane)getParent();
		layeredPane.moveToFront(this);
	}
	
	public int getLastRenderHeight()
	{
		return lastRenderHeight;	
	}
	
	public void setLastRenderHeight(int height)
	{
		lastRenderHeight = height;	
	}
	
	public int getLastRenderWidth()
	{
		return lastRenderWidth;	
	}
	
	public void setLastRenderWidth(int Width)
	{
		lastRenderWidth = Width;	
	}
	
	public int getMouseDiffX()
	{
		return mouseDiffX;
	}
	
	public void setMouseDiffX(int xInt)
	{
		mouseDiffX = xInt;	
	}
	
	public int getMouseDiffY()
	{
		return mouseDiffY;
	}
	
	public void setMouseDiffY(int yInt)
	{
		mouseDiffY = yInt;	
	}
	
	public int getOldCursorX()
	{
		return oldCursorX;	
	}
	
	public void setOldCursorX(int oldX)
	{
		oldCursorX = oldX;	
	}
	
	public int getOldCursorY()
	{
		return oldCursorY;	
	}
	
	public void setOldCursorY(int oldY)
	{
		oldCursorY = oldY;	
	}
	
	public boolean getResizing()
	{
		return bResizing;
	}
	
	public void setResizing(boolean bResizing)
	{
		this.bResizing = bResizing;	
	}
	
	public int getResizeDirection()
	{
		return resizeDirection;	
	}
	
	public void setResizeDirection(int resizeDirection)
	{
		this.resizeDirection = resizeDirection;
	}
	
	public void resizeModule(int x, int y)
	{
		// Get the original origin.
		
		Double oX = new Double(getLocation().getX());
		Double oY = new Double(getLocation().getY());
		
		int oldOriginX  = oX.intValue();
		int oldOriginY = oY.intValue();
		
		// Store the co-ordinates for the bottom-right corner of the
		// module so that we can determine it will be too small.
		
		int botomRightX = oldOriginX + moduleRenderWidth;
		int botomRightY = oldOriginY + moduleRenderHeight;
		
		// Store where the cursor was first placed.
		
		int clickPosX = oldCursorX;
		int clickPosY = oldCursorY;
		
		oldCursorX = oldCursorX - x;
		oldCursorY = oldCursorY - y;
		
		// Store the new origin.
		
		int newOriginX = oldOriginX - oldCursorX;
		int newOriginY = oldOriginY - oldCursorY;
		
		int temp = 0;
		
		switch(resizeDirection)
		{	
			case NW_HANDLE:
				
				// Make sure that the x position can't be taken 
				// outwith the drawing area.
		
				if (newOriginX <= 0)
					newOriginX =0;
				
				// Make sure that the new y value is not outside
				// the screen.
				
				if (newOriginY <= 0)
					newOriginY =0;
				
				// Make sure that the new origin is not lower or left of the
				// old bottom-right corner.
				
				if (newOriginX > (botomRightX - SMALLEST_MODULE_WIDTH))
					newOriginX = botomRightX - SMALLEST_MODULE_WIDTH;
				
				if (newOriginY > (botomRightY - SMALLEST_MODULE_HEIGHT))
					newOriginY = botomRightY - SMALLEST_MODULE_HEIGHT;
					
				// Calculate the new module dimensions.
					
				if (newOriginY < oldOriginY)
				{	temp = (oldOriginY - newOriginY) + moduleRenderHeight;
					moduleRenderHeight = temp;
				}
				else if (newOriginY > oldOriginY)
				{
					temp = moduleRenderHeight - (newOriginY - oldOriginY);
					moduleRenderHeight = temp;
				}
				
				if (newOriginX < oldOriginX)
				{
					temp = moduleRenderWidth + (oldOriginX - newOriginX);
					moduleRenderWidth = temp;
				}
				else if (newOriginX > oldOriginX)
				{
					temp = moduleRenderWidth - (newOriginX - oldOriginX);
					moduleRenderWidth = temp;
				}
				
				// Resize.
				
				setBounds(newOriginX, newOriginY, moduleRenderWidth, moduleRenderHeight);
				
				break;
			case N_HANDLE:
				
				
				// Make sure that the new y value is not outside
				// the screen.
				
				if (newOriginY <= 0)
					newOriginY =0;
				else if (newOriginY >= getParent().getHeight())
					newOriginY = getParent().getHeight();
					
				// Make sure that the new origin is not lower than the
				// old bottom-right corner.
				
				if (newOriginY > (botomRightY - SMALLEST_MODULE_HEIGHT))
					newOriginY = botomRightY - SMALLEST_MODULE_HEIGHT;
					
				// Calculate the new module dimensions.
					
				if (newOriginY < oldOriginY)
				{	temp = (oldOriginY - newOriginY) + moduleRenderHeight;
					moduleRenderHeight = temp;
				}
				else if (newOriginY > oldOriginY)
				{
					temp = moduleRenderHeight - (newOriginY - oldOriginY);
					moduleRenderHeight = temp;
				}
				
				
				// Resize.
				
				setBounds(oldOriginX, newOriginY, moduleRenderWidth, moduleRenderHeight);
				
				break;
			case NE_HANDLE:
				
				
				// Stop the right edge of the module from going
				// off the screen.
				
				if (botomRightX > getParent().getWidth())
				{
					newOriginX = newOriginX - (botomRightX - getParent().getWidth());
				}
				
				// Make sure that the new y value is not outside
				// the screen.
				
				if (newOriginY <= 0)
					newOriginY =0;
				else if (newOriginY >= getParent().getHeight())
					newOriginY = getParent().getHeight();
					
				// Make sure that the new origin is not lower than the
				// old bottom-right corner.
				
				if (newOriginY > (botomRightY - SMALLEST_MODULE_HEIGHT))
					newOriginY = botomRightY - SMALLEST_MODULE_HEIGHT;
				
				// Make sure that module does not get too small.
				
				if (((newOriginX + lastRenderWidth) - oldOriginX) < SMALLEST_MODULE_WIDTH)
				{
					newOriginX = newOriginX + (oldOriginX - newOriginX);
				}
				
				// Calculate the new module dimensions.
				
				if (newOriginX < oldOriginX)
				{
					temp = (newOriginX + lastRenderWidth) - oldOriginX;
  					moduleRenderWidth = temp;
				}
				else if (newOriginX > oldOriginX)
				{
					temp = (newOriginX - oldOriginX) + lastRenderWidth;
  					moduleRenderWidth = temp;
				}
				
				if (newOriginY < oldOriginY)
				{	temp = (oldOriginY - newOriginY) + moduleRenderHeight;
					moduleRenderHeight = temp;
				}
				else if (newOriginY > oldOriginY)
				{
					temp = moduleRenderHeight - (newOriginY - oldOriginY);
					moduleRenderHeight = temp;
				}
				
				// Resize.
				
				setBounds(oldOriginX, newOriginY, moduleRenderWidth, moduleRenderHeight);
				break;
			case E_HANDLE:
				
				// Stop the right edge of the module from going
				// off the screen.
				
				if (botomRightX > getParent().getWidth())
				{
					newOriginX = newOriginX - (botomRightX - getParent().getWidth());
				}
				
				// Make sure that module does not get too small.
				
				if (((newOriginX + lastRenderWidth) - oldOriginX) < SMALLEST_MODULE_WIDTH)
				{
					newOriginX = newOriginX + (oldOriginX - newOriginX);
				}
				
				// Calculate the new module dimensions.
				
				if (newOriginX < oldOriginX)
				{
					temp = (newOriginX + lastRenderWidth) - oldOriginX;
  					moduleRenderWidth = temp;
				}
				else if (newOriginX > oldOriginX)
				{
					temp = (newOriginX - oldOriginX) + lastRenderWidth;
  					moduleRenderWidth = temp;
				}
				
				// Resize.
				
				setBounds(oldOriginX, oldOriginY, moduleRenderWidth, moduleRenderHeight);
				break;
			case SE_HANDLE:
				
				// Stop the right edge of the module from going
				// off the screen.
				
				if (botomRightX > getParent().getWidth())
				{
					newOriginX = newOriginX - (botomRightX - getParent().getWidth());
				}
				
				// Make sure that module does not get too small.
				
				if (((newOriginX + lastRenderWidth) - oldOriginX) < SMALLEST_MODULE_WIDTH)
				{
					newOriginX = newOriginX + (oldOriginX - newOriginX);
				}
					
				if (((newOriginY + lastRenderHeight) - oldOriginY) < SMALLEST_MODULE_HEIGHT)
				{
					newOriginY = newOriginY + (oldOriginY - newOriginY);
				}
				
				// Calculate the new module dimensions.
				
  				if (newOriginY < oldOriginY)
  				{	
 					temp = (newOriginY + lastRenderHeight) - oldOriginY;
  					moduleRenderHeight = temp;
  				}
  				else if (newOriginY > oldOriginY)
  				{
  					temp = (newOriginY - oldOriginY) + lastRenderHeight;
  					moduleRenderHeight = temp;
  				}
				
				if (newOriginX < oldOriginX)
				{
					temp = (newOriginX + lastRenderWidth) - oldOriginX;
  					moduleRenderWidth = temp;
				}
				else if (newOriginX > oldOriginX)
				{
					temp = (newOriginX - oldOriginX) + lastRenderWidth;
  					moduleRenderWidth = temp;
				}
				
				// Resize.
				
				setBounds(oldOriginX, oldOriginY, moduleRenderWidth, moduleRenderHeight);
				
				break;
			case S_HANDLE:
				
				// Make sure that module does not get too small.
				
				if (((newOriginY + lastRenderHeight) - oldOriginY) < SMALLEST_MODULE_HEIGHT)
				{
					newOriginY = newOriginY + (oldOriginY - newOriginY);
				}
				
				// Calculate the new module dimensions.
				
  				if (newOriginY < oldOriginY)
  				{	
 					temp = (newOriginY + lastRenderHeight) - oldOriginY;
  					moduleRenderHeight = temp;
  				}
  				else if (newOriginY > oldOriginY)
  				{
  					temp = (newOriginY - oldOriginY) + lastRenderHeight;
  					moduleRenderHeight = temp;
  				}
				
				// Resize.
				
				setBounds(oldOriginX, oldOriginY, moduleRenderWidth, moduleRenderHeight);
				
				break;
			case SW_HANDLE:
				
				// Make sure that the x position can't be taken 
				// outwith the drawing area.
				
				if (newOriginX <= 0)
					newOriginX =0;
				else if (newOriginX >= getParent().getWidth())
					newOriginX = getParent().getWidth();
				
				// Make sure that module does not get too small.
				
				if (newOriginX > (botomRightX - SMALLEST_MODULE_WIDTH))
					newOriginX = botomRightX - SMALLEST_MODULE_WIDTH;
					
				if (((newOriginY + lastRenderHeight) - oldOriginY) < SMALLEST_MODULE_HEIGHT)
				{
					newOriginY = newOriginY + (oldOriginY - newOriginY);
				}
				
				// Calculate the new module dimensions.
				
  				if (newOriginY < oldOriginY)
  				{	
 					temp = (newOriginY + lastRenderHeight) - oldOriginY;
  					moduleRenderHeight = temp;
  				}
  				else if (newOriginY > oldOriginY)
  				{
  					temp = (newOriginY - oldOriginY) + lastRenderHeight;
  					moduleRenderHeight = temp;
  				}
				
				if (newOriginX < oldOriginX)
				{
					temp = moduleRenderWidth + (oldOriginX - newOriginX);
					moduleRenderWidth = temp;
				}
				else if (newOriginX > oldOriginX)
				{
					temp = moduleRenderWidth - (newOriginX - oldOriginX);
					moduleRenderWidth = temp;
				}
				
				// Resize.
				
				setBounds(newOriginX, oldOriginY, moduleRenderWidth, moduleRenderHeight);
				
				break;
			case W_HANDLE:
				
				// Make sure that the x position can't be taken 
				// outwith the drawing area.
				
				if (newOriginX <= 0)
					newOriginX =0;
				else if (newOriginX >= getParent().getWidth())
					newOriginX = getParent().getWidth();
				
				// Make sure that the new origin is not lower or left of the
				// old bottom-right corner.
				
				if (newOriginX > (botomRightX - SMALLEST_MODULE_WIDTH))
					newOriginX = botomRightX - SMALLEST_MODULE_WIDTH;
				
				if (newOriginX < oldOriginX)
				{
					temp = moduleRenderWidth + (oldOriginX - newOriginX);
					moduleRenderWidth = temp;
				}
				else if (newOriginX > oldOriginX)
				{
					temp = moduleRenderWidth - (newOriginX - oldOriginX);
					moduleRenderWidth = temp;
				}
				
				// Resize.
				
				setBounds(newOriginX, oldOriginY, moduleRenderWidth, moduleRenderHeight);
				break;
			default:
				
		}
		
		// Make sure that the cursor stays in a position
		// relative to where it was first placed.
				
		oldCursorX = clickPosX;
		oldCursorY = clickPosY;
	}
	
	public boolean isFocusable()
	{
		// This overrides the original isFocusable method to
		// allow the panel to have keyboard focus
		
		return true;	
	}
	
	// Method declarations for the KeyListener interface.
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	public void keyPressed(KeyEvent e)
	{
		// Can delete, move etc.
		
		// Get position of orign in case the user is moving the module.
		// If the control key is pressed while the user presses an arrow
		// key, then move the module one pixel at a time, otherwise move
		// it ten at a time.
		
		Point origin = getLocation();
		Double dX = new Double(origin.getX());
		Double dY = new Double(origin.getY());
		int x = dX.intValue();
		int y = dY.intValue();
		int keyCode = e.getKeyCode();
				
		if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE)
		{
			// Delete the module and..
			// Transfer the focus to the next module in the tab
			// order
			
			if (bSelected)
			{
				mdiForm.removeModule(key);
				
				// Move the focus to the next visual module
				
				for (int i = 0; i < drawPane.getComponentCount(); i++)
				{
					if (drawPane.getComponent(i) instanceof VisualModule)
					{
						((VisualModule)drawPane.getComponent(i)).setFocus();
						break;
					}
				}
			}
		}
		else if (keyCode == KeyEvent.VK_CONTROL)
			
			// Determine whether the control key has been pressed.
			
			bControlKeyPressed = true;
		else if (keyCode == KeyEvent.VK_SHIFT)
			
			// Determine whether the shift key has been pressed.
			
			bShiftKeyPressed = true;
		else if (keyCode == KeyEvent.VK_LEFT)
		{	
			if (bControlKeyPressed == true)
			{	
				// Move left 1
				
				x -= 1;
			}
			else if ((bShiftKeyPressed == true) && (parent.getLinkMode() == false))
			{
				// Resize to LHS
				
				x = (new Double(getLocation().getX())).intValue();
				y = (new Double(getLocation().getY())).intValue();
				
				if ((moduleRenderWidth - 1) > SMALLEST_MODULE_WIDTH)
				{
					moduleRenderWidth -= 1;
					setBounds(x, y, moduleRenderWidth, moduleRenderHeight);
					getParentForm().getDrawPane().paintLinks();
				}
			}
			else
			{
				// Move left.10
				
				x -= 10;
			}
		}
		else if (keyCode == KeyEvent.VK_DOWN)
		{	
			if (bControlKeyPressed == true)
			{
				// Move down 1
				
				y += 1;
			}
			else if ((bShiftKeyPressed == true) && (parent.getLinkMode() == false))
			{
				// Resize downwards
				
				x = (new Double(getLocation().getX())).intValue();
				y = (new Double(getLocation().getY())).intValue();
				
				moduleRenderHeight += 1;
				
				setBounds(x, y, moduleRenderWidth, moduleRenderHeight);
				getParentForm().getDrawPane().paintLinks();
			}
			else
			{
				// Move down 10
				
				y += 10;
			}
		}
		else if (keyCode == KeyEvent.VK_RIGHT)
		{	
			if (bControlKeyPressed == true)
			{
				// Move right 1
				
				x += 1;
			}
			else if ((bShiftKeyPressed == true) && (parent.getLinkMode() == false))
			{
				// Resize to RHS
				
				x = (new Double(getLocation().getX())).intValue();
				y = (new Double(getLocation().getY())).intValue();
				
				moduleRenderWidth += 1;
				
				setBounds(x, y, moduleRenderWidth, moduleRenderHeight);
				getParentForm().getDrawPane().paintLinks();
			}
			else
			{
				// Move right 10
				
				x += 10;
			}
		}
		else if (keyCode == KeyEvent.VK_UP)
		{	
			if (bControlKeyPressed == true)
			{
				// Move up 1
				
				y -= 1;
			}
			else if ((bShiftKeyPressed == true) && (parent.getLinkMode() == false))
			{
				// Resize upwards
				
				x = (new Double(getLocation().getX())).intValue();
				y = (new Double(getLocation().getY())).intValue();
				
				if ((moduleRenderHeight - 1) > SMALLEST_MODULE_HEIGHT)
				{
					moduleRenderHeight -= 1;
					setBounds(x, y, moduleRenderWidth, moduleRenderHeight);
					getParentForm().getDrawPane().paintLinks();
				}
			}
			else
			{
				// Move up 10
				
				y -= 10;
			}
		}
		
		// Make sure that the user can't move the module outwith the
		// bounds of the view port.
		
		try
		{
			if (x < 0)
				x = 0;
			else if (x > (getParent().getWidth()- getWidth()))
				x = (getParent().getWidth()- getWidth());
				
			if (y < 0)
				y = 0;
			else if (y > (getParent().getHeight() - getHeight()))
				y = (getParent().getHeight()- getHeight());
			
		
			// Change the position.
		
			setBounds(x, y, moduleRenderWidth, moduleRenderHeight);
			getParentForm().getDrawPane().paintLinks();
		}
		catch(NullPointerException e2)
		{
			// The module has been removed.
		}
		
		// Consume the event to stop the scroll bars from moving.
		
		e.consume();
		
		// Move the child components to the correct positions.
		
		revalidate();
	}
	
	public void keyReleased(KeyEvent e)
	{
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_CONTROL)
			bControlKeyPressed = false;
		else if(keyCode == KeyEvent.VK_SHIFT)
			bShiftKeyPressed = false;
	}
	
	public DrawingForm getParentForm()
	{
		return parent;
	}
	
	public void setParentForm(DrawingForm parent)
	{
		// This is called from DrawingForm.reloadGraph() to redraw a VP graph after
		// one of the JInternalFrames was closed and then re-opened
		
		this.parent = parent;
		drawPane = parent.getDrawPane();
	}
	
	// Method declarations for the FocusListener interface.
	
	public void focusGained(FocusEvent e)
	{
		// If a link is selected do not allow the module to have the focus via the tab key
		
		boolean bLinkSelected = false;
		
		bLinkSelected = (parent.getDrawPane().getSelectedLink() == null) ? false : true;
		
		if (!bLinkSelected)
		{	
			mdiForm.clearAllModules();	
			bSelected = true;
			repaint();
			
			// Move the child components to the correct positions.
			
			revalidate();
			
			parent.setModWithFocus((DefaultVisualModule)this);
		}
	}
	
	public void focusLost(FocusEvent e)
	{
			
	}
	
	/**
	*  Update method implemented in specific visual module instances
	*  This is called when a connected module wants to notify this
	*  module of a change
	*
	* @param fromPort The port making the notification
	* @param toPort The port receiving the notification
	*/
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg){}
	
	/**
	* Method to restore transient/static object references after
	* deserialisation
	*/
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException,
								java.lang.ClassNotFoundException
								
	{
		in.defaultReadObject();
		mdiForm = parent_gui.Mdi.getInstance();
		addLabelListener();
	}
	
	/**
	* This can be overriden by visual module instances and is used
	* to execute code that cannot be executed within the 
	* writeObject() method before serialisation. E.g. the
	* SizeSequence class instance of JTable is not accessible and
	* is not serialisable and therefore the row heights of a
	* table that is to be serialised must be made uniform.
	*/
	
	public void beforeSerialise(){}
	public void afterSerialise(){}
}

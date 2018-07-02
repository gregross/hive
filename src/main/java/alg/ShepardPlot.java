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
 * A Shepard Plot for plotting actual interobject distance with
 * the low-D distance. Utilises JFreeChart
 *
 *  @author Greg Ross, Alistair Morrison
 */
 
 package alg;
 
 import data.*;
 import parent_gui.*;
 import math.*;
 import alg.shepardplot.*;
 
 import org.jfree.chart.labels.*;
 import org.jfree.chart.urls.*;
 import org.jfree.chart.axis.NumberAxis;
 import org.jfree.chart.plot.PlotOrientation;
 import org.jfree.chart.labels.StandardXYToolTipGenerator;
 import org.jfree.chart.urls.StandardXYURLGenerator;
 import org.jfree.chart.JFreeChart;
 import org.jfree.data.*;
 import org.jfree.chart.ChartFactory;
 import org.jfree.chart.renderer.StandardXYItemRenderer;
 import org.jfree.chart.ChartPanel;
 
 import java.awt.BorderLayout;
 import java.awt.Rectangle;
 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Graphics;
 import java.awt.event.*;
 import javax.swing.JTextField;
 import javax.swing.text.*;
 import javax.swing.JRadioButton;
 import javax.swing.ButtonGroup;
 import javax.swing.Box.Filler;
 import java.util.*;
 
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.BoxLayout;
 import javax.swing.BorderFactory;
 import javax.swing.ImageIcon;
 import javax.swing.JButton;
 import java.awt.image.BufferedImage;
 import java.lang.Runnable;
 
public class ShepardPlot extends DefaultVisualModule implements ActionListener,
								Runnable
{
	// Versioning for serialisation
	
	static final long serialVersionUID = 50L;
	
	// The high-D data set
	
	private DataItemCollection dataItems = null;
	private DataItemCollection sampleData = null;
	
	// The low-D data set
	
	private ArrayList lowD = null;
	private ArrayList sampleLowD = null;
	
	// The parent drawing surface
	
	private DrawingCanvas drawPane;
	
	private static Mdi mdiForm;
	
	private int height = 158;
	private int width = 500;
	
	private JPanel chartPane;
	
	// The panel that will hold the chart
	
	private transient ChartPanel chtPanel;
	
	// Label to show the percentage of the items plotted so far
	
	private JLabel lblPercent;
	
	private XYSeries series = null;
	private XYDataset xyDataset;
	private JFreeChart chart;
	
	// Button to allow the user to start plotting
	
	private JButton calculateAndRender;
	
	// Text field to let the user determine the size of the sample to take
	
	private JTextField txtSample;
	
	// Radio buttons for letting the user determine the sample size
	
	private JRadioButton rbSqrtSample, rbXSample, rbN;
	
	// A panel to containt the sample size controls
	
	private JPanel bPanel;
	
	// The number that is the square root of the data set size
	
	int sqrtVal = 0;
	
	// The thread on which the plotting takes place
	
	private Thread thread = null;
	
	// If the chart is plotting and the data changes, stop plotting without
	// causing an error
	
	private boolean bRunning = false;
	private boolean dataChangedAndRunning = false;
	
	// A button to allow the user to cancel a job
	
	private JButton cmdCancel;
	private boolean bCancelled = false;
	
	private transient ShepardPlotMouseListener spListener;
	
	// Stuff for selection
	
	protected Set theSelection;
	
	// Associate each point in the chart with two data items
	
	private ArrayList dataItems1;
	private ArrayList dataItems2;
	
	public ShepardPlot(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		
		setName("Shepard diagram");
		setToolTipText("Shepard diagram");
		setLabelCaption(getName());
		
		this.mdiForm = mdiForm;
		this.drawPane = drawPane;
		setPorts();
		setMode(DefaultVisualModule.VISUALISATION_MODE);
		setDimension(width, height);
		setBackground(Color.lightGray);
		renderChart();
		addChartBackgroundControls();
		addSampleControls();
		setInterfaceVisibility();
		
		// Set of selected indices
		
		theSelection = new HashSet();
	}
	
	/**
	*  This is called when a connected module wants to notify this
	*  module of a change
	*/
	
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0")) // LowD
			{
				if (arg.size() > 1)
				{
					// We presume that when the first dimension is the
					// high-D collection and the second is the low-D, the
					// same indices of the items in both sets match up to
					// refer to the same item (e.g. output of spring model)
					
					dataChangedAndRunning = bRunning;
					
					if (arg.get(0) instanceof DataItemCollection)
						dataItems = (DataItemCollection)arg.get(0);
					
					if (arg.get(1) instanceof ArrayList)
						lowD = (ArrayList)arg.get(1);
					
					try
					{
						if ((dataItems != null) && (lowD != null))
						{
							if (series != null)
								series.clear();
							
							// Clear the series collection
							
							for (int i = 0; i < xyDataset.getSeriesCount(); i++)
								((XYSeriesCollection)xyDataset).getSeries(i).clear();
							
							String sTemp = "";
							sTemp = txtSample.getText();
							
							txtSample.setDocument(new ValidTextFieldDoc(dataItems.getSize(), txtSample));
							txtSample.setText(sTemp);
							setEnableControls(true);
							cmdCancel.setEnabled(false);
							bCancelled = false;
							
							sqrtVal = (new Double(Math.sqrt((new Double(dataItems.getSize())).doubleValue()))).intValue();
							rbSqrtSample.setText("N^1/2 (" + sqrtVal + ")");
							
							lblPercent.setText("0%");
						}
					}
					catch (Exception e){}
				}
			}
			else if (toPort.getKey().equals("i1")) // HighD
			{
				System.out.println("Got high-D");
				dataChangedAndRunning = bRunning;
				
				if (arg.get(0) instanceof DataItemCollection)
					dataItems = (DataItemCollection)arg.get(0);
				
				try
				{
					if ((dataItems != null) && (lowD != null))
					{
						if (series != null)
							series.clear();
						
						// Clear the series collection
						
						for (int i = 0; i < xyDataset.getSeriesCount(); i++)
							((XYSeriesCollection)xyDataset).getSeries(i).clear();
						
						String sTemp = "";
						sTemp = txtSample.getText();
						
						txtSample.setDocument(new ValidTextFieldDoc(dataItems.getSize(), txtSample));
						txtSample.setText(sTemp);
						setEnableControls(true);
						cmdCancel.setEnabled(false);
						bCancelled = false;
						
						sqrtVal = (new Double(Math.sqrt((new Double(dataItems.getSize())).doubleValue()))).intValue();
						rbSqrtSample.setText("N^1/2 (" + sqrtVal + ")");
						
						lblPercent.setText("0%");
					}
				}
				catch (Exception e){}
			}
		}
		else
		{
			// Input module or link was deleted or reset
			
			dataItems = null;
			lowD = null;
			
			// Clear the series collection
			
			for (int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				((XYSeriesCollection)xyDataset).getSeries(i).clear();
				((XYSeriesCollection)xyDataset).removeSeries(i);
			}
			xyDataset = new XYSeriesCollection(series);
			chart.getXYPlot().setDataset(xyDataset);
			
			setEnableControls(false);
			cmdCancel.setEnabled(false);
			lblPercent.setText("0%");
			bCancelled = false;
			dataItems1 = null;
			dataItems2 = null;
		}
	}
	
	private void setEnableControls(boolean b)
	{
		calculateAndRender.setEnabled(b);
		rbSqrtSample.setEnabled(b);
		rbXSample.setEnabled(b);
		rbN.setEnabled(b);
		
		if (rbXSample.isSelected())
			setEnableTextSample(b);
		else
			setEnableTextSample(false);
		
	}
	
	private void setEnableTextSample(boolean b)
	{
		txtSample.setEnabled(b);
		
		if (b)
			txtSample.setBackground(Color.white);
		else
			txtSample.setBackground(Color.lightGray);
	}
	
	// Create the ports and append them to the module
	
	private void setPorts()
	{
		int numInPorts = 2;
		int numOutPorts = 1;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		
		// 1) Low-D and high-D
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.VECTOR);
		ports.add(port);
		
		// 2) High-D
		
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		
		// Add 'out' selection port
		
		port = new ModulePort(this, ScriptModel.SELECTION_PORT, 0);
		port.setPortLabel("Selection");
		ports.add(port);
		
		addPorts(ports);
	}
	
	private void addChartBackgroundControls()
	{
		// Add a JPanel to the centre of the module and then
		// add the chart to it
		
		chartPane = new JPanel();
		chartPane.setLayout(new BorderLayout());
		add(chartPane, "Center");
		chartPane.add(chtPanel, "Center");
		
		// Fill out the borders of the plot
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		add(filler, "South");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "West");
		filler = new Filler(new Dimension(1, 1), new Dimension(1, 1), new Dimension(2, 2));
		add(filler, "East");
	}
	
	private void addSampleControls()
	{
		// Add a button above the chart
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		calculateAndRender = new JButton("Draw chart");
		calculateAndRender.addActionListener(this);
		buttonPane.add(calculateAndRender);
		
		// Add a button to allow the user to cancel
		
		cmdCancel = new JButton("Cancel");
		cmdCancel.addActionListener(this);
		cmdCancel.setEnabled(false);
		buttonPane.add(cmdCancel);
		
		// Add a label to show the percentage complete
		
		Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
		buttonPane.add(filler);
		lblPercent = new JLabel("0%");
		buttonPane.add(lblPercent);
		
		// Add controls to set the sample size
		
		ButtonGroup group = new ButtonGroup();
		rbN = new JRadioButton("N");
		rbN.setOpaque(false);
		rbN.addActionListener(this);
		rbN.setSelected(true);
		rbSqrtSample = new JRadioButton("N^1/2");
		rbSqrtSample.addActionListener(this);
		rbSqrtSample.setOpaque(false);
		rbXSample = new JRadioButton("X <= n:");;
		rbXSample.addActionListener(this);
		rbXSample.setOpaque(false);
		group.add(rbN);
		group.add(rbSqrtSample);
		group.add(rbXSample);
		
		// Add a bordered panel to hold the sample selection controls
		
		bPanel = new JPanel();
		bPanel.setBorder(BorderFactory.createTitledBorder("Sample size:"));
		bPanel.add(rbN);
		bPanel.add(rbSqrtSample);
		bPanel.add(rbXSample);
		
		txtSample = new JTextField();
		
		// Ensure that the user can only enter a number <= to the total size
		// of the data set
		
		txtSample.setDocument(new ValidTextFieldDoc(0, txtSample));
		txtSample.setPreferredSize(new Dimension(60, 18));
		txtSample.setMaximumSize(new Dimension(60, 18));
		txtSample.setMinimumSize(new Dimension(60, 18));
		txtSample.setActionCommand("txtSampleSize");
		txtSample.addActionListener(this);
		
		bPanel.add(txtSample);
		
		buttonPane.add(bPanel);
		chartPane.add(buttonPane, "South");
		
		setEnableControls(false);
	}
	
	private void clearHighlight()
	{
		if (((XYSeriesCollection)xyDataset).getSeriesCount() > 1)
			((XYSeriesCollection)xyDataset).removeSeries(1);
	}
	
	public void handleSelection(ArrayList selection)
	{
		if ((dataItems1 != null) && (dataItems2 != null))
		{
			ArrayList selectItems = new ArrayList();
			
			int i;
			
			int index;
			
			for (i = 0; i < selection.size(); i++)
			{
				index = ((Integer)selection.get(i)).intValue();
				
				selectItems.add((Integer)dataItems1.get(index));
				selectItems.add((Integer)dataItems2.get(index));
			}
			
			if (dataItems1.size() > 0)
			{
				theSelection = new HashSet(selectItems);
				sendSelection();
			}
		}
		else
		{
			theSelection = null;
			sendSelection();
		}
	}
	
	/**
	* When the selection is changed, this is called to send the
	* selection to the selction port
	*/
	
	public void sendSelection()
	{
		// Get references to the selction handling objects
		
		ArrayList transferData = new ArrayList(2);
		transferData.add(dataItems);
		transferData.add(theSelection);
		getOutPort(0).sendData(transferData);
	}
	
	private void renderChart()
	{
		// Draw the actual chart in memory
		
		series = new CustomXYSeries("Distances");
		
		xyDataset = new XYSeriesCollection(series);
		
		chart = createCustomScatterPlot
			("",  				// Title
			"Low-D distance",           	// X-Axis label
			"High-D distance",           	// Y-Axis label
			xyDataset,          		// Dataset
			PlotOrientation.VERTICAL, 	// Orientation
			false,                		// Show legend
			false,				// Tool-tip-text
			false				// URLs
			);
			
		chart.getXYPlot().setBackgroundPaint(new Color(50,50,50));
		
		// Create a new renderer so that we can control shape and colour etc
		
		StandardXYItemRenderer r = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
		r.setSeriesPaint(0, Color.cyan);
		Rectangle rect = new Rectangle(0, 0, 2, 2);
		r.setSeriesShape(0, rect);
		r.setSeriesShapesFilled(0, new Boolean("true"));
		chart.getXYPlot().setRenderer(r);
		
		// Add a ChartPanel to hold the chart and detect user interaction
		
		chtPanel = new ChartPanel(chart);
		spListener = new ShepardPlotMouseListener(this);
		chtPanel.addMouseListener(spListener);
		chtPanel.addMouseMotionListener(spListener);
	}
	
	private JFreeChart createCustomScatterPlot(String title,
                                               String xAxisLabel,
                                               String yAxisLabel,
                                               XYDataset data,
                                               PlotOrientation orientation,
                                               boolean legend,
                                               boolean tooltips,
                                               boolean urls) 
        {
		NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		yAxis.setAutoRangeIncludesZero(false);
		
		CustomPlot plot = new CustomPlot(data, xAxis, yAxis, null);
		
		XYToolTipGenerator toolTipGenerator = null;
		if (tooltips)
		{
			toolTipGenerator = new StandardXYToolTipGenerator();
		}
		
		XYURLGenerator urlGenerator = null;
		if (urls) 
		{
			urlGenerator = new StandardXYURLGenerator();
		}
		StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES,
                                                                     toolTipGenerator,
                                                                     urlGenerator);
		renderer.setShapesFilled(Boolean.TRUE);
		plot.setRenderer(renderer);
		plot.setOrientation(orientation);
		
		JFreeChart cht = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
		
		return cht;
	}
	
	/**
	* Implementation of the ActionListener interface
	*/
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == calculateAndRender)
		{
			clearHighlight();
			
			if (rbN.isSelected())
			{
				createDataSets(lowD.size());
			}
			else if (rbSqrtSample.isSelected())
			{
				createDataSets(sqrtVal);
			}
			else if (rbXSample.isSelected())
			{
				int xSize;
				
				if (txtSample.getText().equals(""))
					xSize = 0;
				else
					xSize = Integer.parseInt(txtSample.getText());
				
				createDataSets(xSize);
			}
			
			thread = new Thread(this);
			thread.start();
		}
		else if (e.getSource() == cmdCancel)
		{
			bCancelled = true;
		}
		else if (e.getSource() == rbN)
		{
			setEnableTextSample(false);
		}
		else if (e.getSource() == rbSqrtSample)
		{
			setEnableTextSample(false);
		}
		else if (e.getSource() == rbXSample)
		{
			setEnableTextSample(true);
		}
	}
	
	public void run()
	{
		bRunning = true;
		double lowDist;
		double highDist;
		
		series.clear();
		setEnableControls(false);
		cmdCancel.setEnabled(true);
		
		lblPercent.setText("0%");
		int count = 0;
		int total = (((sampleLowD.size() * sampleLowD.size()) - sampleLowD.size()) / 2);
		
		XYSeries s = new CustomXYSeries("Distances");
		XYDataset xyDataS = new XYSeriesCollection(s);
		
		if (lowD != null)
		{
			dataItems1 = new ArrayList();
			dataItems2 = new ArrayList();
			
			for (int i = 0; i < (sampleLowD.size() - 1); i++)
			{
				for (int j = (i + 1); j < sampleLowD.size(); j++)
				{
					if (dataChangedAndRunning || bCancelled)
					{
						dataChangedAndRunning = false;
						bRunning = false;
						setEnableControls(true);
						
						cmdCancel.setEnabled(false);
						bCancelled = false;
						return;
					}
					
					lowDist = getLowDist((Coordinate)sampleLowD.get(i), (Coordinate)sampleLowD.get(j));
					highDist = sampleData.getDesiredDist(i, j);
					
					s.add(lowDist, highDist);
					
					// Store the keys of the data items for each plotted point
					
					dataItems1.add(new Integer(((DataItem)sampleData.getDataItem(i)).getID()));
					dataItems2.add(new Integer(((DataItem)sampleData.getDataItem(j)).getID()));
					
					// Update the percentage complete label
					
					count++;
					
					lblPercent.setText((int)((count / (double)total) * 100) + "%");
				}
			}
		}
		
		chart.getXYPlot().setDataset(xyDataS);
		xyDataset = xyDataS;
		
		bRunning = false;
		setEnableControls(true);
		cmdCancel.setEnabled(false);
	}
	
	private double getLowDist(Coordinate c1, Coordinate c2)
	{
		return Math.sqrt(Math.pow((c1.getX() - c2.getX()), 2) + Math.pow((c1.getY() - c2.getY()), 2));
	}
	
	/**
	* Create a data set given the number of items the user specifies
	*/
	
	private void createDataSets(int sampleSize)
	{	
		if (sampleSize < lowD.size())
		{
			ArrayList sample = Utils.createRandomSample(null, null, dataItems.getSize(), sampleSize);
			
			// Copy the sample data into the sample data set
			
			sampleLowD = new ArrayList();
			int index;
			
			int[] itemIndices = new int[sample.size()];
			
			for (int j = 0; j < sample.size(); j++)
			{
				index = ((Integer)sample.get(j)).intValue();
				itemIndices[j] = index;
				sampleLowD.add(lowD.get(index));
			}
			
			sampleData = dataItems.createNewCollection(itemIndices);
		}
		else
		{
			sampleData = dataItems;
			sampleLowD = lowD;
		}
	}
	
	/**
	* Accessor method for retrieving the chart
	*/
	
	public JFreeChart getChart()
	{
		return chart;
	}
	
	public ChartPanel getChartPanel()
	{
		return chtPanel;
	}
	
	public XYSeriesCollection getSeriesCollection()
	{
		return (XYSeriesCollection)xyDataset;
	}
	
	/** 
	* Internal class to deal with valid text-entry for
	* determining the sample size
	*/
	
	private class ValidTextFieldDoc extends PlainDocument  implements java.io.Serializable
	{
		// Versioning for serialisation
		
		static final long serialVersionUID = 50L;
		
		private int maxValue;
		private JTextField txtBox;
		
		public ValidTextFieldDoc(int maxValue, JTextField txtBox)
		{
			// The maximum allowable value that the text box can hold
			
			this.maxValue = maxValue;
			
			// Reference to the text box per se
			
			this.txtBox = txtBox;
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
					return;
					
				// Don't allow the number to be larger than maxValue
					
				if (Integer.parseInt(txtBox.getText() + new String(result, 0, j)) <= maxValue)
					super.insertString(offs, new String(result, 0, j), a);
			}
			
			protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng)
			{
				super.removeUpdate(chng);
			}
	}
	
	/**
	* Stuf to do before serialisation.
	*/
	
	public void beforeSerialise()
	{
		chartPane.remove(chtPanel);
		
		// Don't serialise until the main threaad has stopped
		
		try
		{
			if (thread != null)
				while (thread.isAlive()){}
		}
		catch (Exception e){}
		
		thread = null;
	}
	
	/**
	* Stuff to do after serialisation.
	*/
	
	public void afterSerialise()
	{
		chartPane.add(chtPanel, "Center");
	}
	
	/**
	* Stuff to de when deserialisation.
	*/
	
	private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		// Restore the series and redraw the chart
		
		XYDataset dTemp = xyDataset;
		renderChart();
		xyDataset = dTemp;
		chart.getXYPlot().setDataset(xyDataset);
		
		// Set the renering for the highlight series
		
		org.jfree.chart.plot.XYPlot p = chart.getXYPlot();
		StandardXYItemRenderer r = (StandardXYItemRenderer)p.getRenderer();
		r.setSeriesPaint(1, Color.yellow);
		Rectangle rect = new Rectangle(1, 0, 2, 2);
		r.setSeriesShape(1, rect);
		r.setSeriesShapesFilled(1, new Boolean("true"));
		
		// Make the chart visisble again
		
		chartPane.add(chtPanel, "Center");
	}
}

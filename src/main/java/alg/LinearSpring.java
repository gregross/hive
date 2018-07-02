package alg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import math.Coordinate;
import math.Vect;
import alg.springModel96.NeighbourComparator;
import alg.springModel96.SpringModelControlPanel;
import data.DataItem;
import data.DataItemCollection;
import parent_gui.DefaultVisualModule;
import parent_gui.DrawingCanvas;
import parent_gui.Mdi;
import parent_gui.ModulePort;
import parent_gui.ScriptModel;

public class LinearSpring extends DefaultVisualModule implements Runnable
{
	// Versioning for serialisation
	static final long				serialVersionUID		= 50L;

	// The dimensions of the module
	private int						height					= 100;
	private int						width					= 234;

	// Locally held values (testing if it is faster)
	private ArrayList				position;
	private ArrayList				velocity;
	private ArrayList				force;
	private DataItemCollection		dataItems;

	// Set these to default values
	private double					layoutBounds			= 1.0;
	private double					rangeLo					= -0.5;
	private double					dampingFactor			= 0.3;
	private double					springForce				= 0.7;
	private double					gravityForce			= 0.7;
	private double					gravityDampingForce		= 0.2;
	private double					timeForce				= 0.7;
	private double					timeDampingForce		= 0.2;
	private double					freeness				= 0.85;
	private double					deltaTime				= 0.3;

	// Compensates the size of the data size, the accumulated forces for 1000
	// items will be much larger than for 100 items
	private double					dataSizeFactor;
	private long					startTime;

	// Each element is also an arrayList of indices
	private ArrayList				neighbours, samples, distances, thisSampleDists, hts;
	public int						neighbourSize			= 6;
	public int						sampleSize				= 3;

	// The array of data to be transferred to other linked modules
	private ArrayList				transferData;
	
	// The thread that controls the running of the spring model
	private volatile Thread			thread;
	
	// Determine whether the high-D input has been removed
	private boolean					bHigh_D_InputRemoved	= false;
	private final JButton			start					= new JButton("Start");
	private SpringModelControlPanel	controlPanel;
	
	// Label for displaying the current iteration number
	private JLabel					lblIterNumber;
	
	// Number of spring model iterations performed so far
	private int						numIterations;
	
	// If the data set is not transposed then the following arraying contains
	// the ordinal
	// indices of the attributes on which we will run the spring model
	private ArrayList				springVar;

	public LinearSpring(Mdi mdiForm, DrawingCanvas drawPane)
	{
		super(mdiForm, drawPane);
		setName("Linear Spring Model");
		setToolTipText("Linear time Spring Model");
		setLabelCaption(getName());
		setPorts();
		setMode(DefaultVisualModule.ALGORITHM_MODE);
		setBackground(Color.orange);
		setDimension(width, height);
		addControls();
	}
	
	private void addControls()
	{
		// Add a new JPanel to the centre of the module
		JPanel jPane = new JPanel();
		jPane.setOpaque(false);
		start.setEnabled(false);
		addButtonActionListeners();

		// Add a label showing the current iteration number
		lblIterNumber = new JLabel("Iterations: 0");
		jPane.add(start);
		jPane.add(lblIterNumber);
		add(jPane, "Center");
		
		// Make the controls visible depending upon the context
		// of the VisualModule
		setInterfaceVisibility();
	}

	/**
	 * When the instance of VarSelectTable exists, set the variables upon which
	 * the spring model will run
	 */
	public void setSpringVars(ArrayList springVar)
	{
		if (springVar.size() < dataItems.getFields().size())
			this.springVar = springVar;
		else
			this.springVar = null;
	}

	private void addButtonActionListeners()
	{
		// Add a button to start/stop the spring model to this
		// new JPanel
		start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setFocus();
				if (start.getText().equals("Start"))
				{
					// Send a trigger stating that the model has started
					ArrayList transferD = new ArrayList();
					transferD.add("start");
					getOutPort(2).sendData(transferD);
					// Start the model
					startSpringModel();
				}
				else
					stopSpringModel();
			}
		});
	}

	/**
	 * When the data set is null, make sure that the user can't start the spring
	 * model
	 */
	private void startControls()
	{
		start.setText("Start");
		
		if (dataItems == null)
			start.setEnabled(false);
		else
		{
			start.setEnabled(true);
		}
	}

	private void initSpringModel()
	{
		// This method is called after the module has received data on its
		// data-in port
		// Set the number of dimensions that are being used
		springVar = null;
		Coordinate.setActiveDimensions(4);
		numIterations = 0;
		lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
		init();
		randomizePositions();
		dataSizeFactor = 1.0 / (double) (dataItems.getSize() - 1);
		
		// init the neighbours array list to be a random sample
		neighbours = new ArrayList();
		samples = new ArrayList();
		distances = new ArrayList();
		hts = new ArrayList();
		
		// init every element of neighbours and samples with a random list
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			distances.add(new ArrayList(neighbourSize));
			HashSet exclude = new HashSet();
			exclude.add(new Integer(i));
			
			// Init each neighbours set to a random list
			ArrayList neighbs = Utils.createRandomSample(null, exclude, dataItems
					.getSize(), neighbourSize);
			
			// Initialise distances - save computation later
			// also set up hashtable
			Hashtable ht = new Hashtable(neighbourSize);
			for (int y = 0; y < neighbourSize; y++)
			{
				Double di = new Double(dataItems.getDesiredDist(i, ((Integer) neighbs
						.get(y)).intValue(), springVar));
			
				((ArrayList) distances.get(i)).add(di);
				ht.put((Integer) neighbs.get(y), new Integer(y));
			}
			hts.add(ht);
			
			// sort the arraylist into ascending order
			NeighbourComparator comp = new NeighbourComparator(dataItems, i,
					(ArrayList) distances.get(i), ht);
			Collections.sort(neighbs, comp);
			Collections.sort((ArrayList) distances.get(i));
			Collections.reverse(((ArrayList) distances.get(i)));
			neighbours.add(neighbs);
			exclude = new HashSet(neighbs);
			exclude.add(new Integer(i));
			
			// Insert an ArrayList of samples into each samples element
			samples.add(Utils.createRandomSample(null, exclude, dataItems.getSize(),
					sampleSize));
		}
		thisSampleDists = new ArrayList();
		
		for (int p = 0; p < sampleSize; p++)
			thisSampleDists.add(new Double(0.0));
		
		dataSizeFactor = 1.0 / (double) (neighbourSize + sampleSize);
	}

	private void stopSpringModel()
	{
		start.setText("Start");
		thread = null;
		
		// Only allow the user to modify the convergence triggering values while
		// the spring model is not iterating
		if (controlPanel != null)
			controlPanel.enableDisableTriggerControls(true);
	}

	private void startSpringModel()
	{
		start.setText("Stop");
		thread = new Thread(this);
		thread.start();
		
		// Only allow the user to modify the convergence triggering values while
		// the spring model is not iterating
		if (controlPanel != null)
			controlPanel.enableDisableTriggerControls(false);
	}

	// Create the ports and append them to the module
	private void setPorts()
	{
		int numInPorts = 2;
		int numOutPorts = 3;
		ArrayList ports = new ArrayList(numInPorts + numOutPorts);
		ModulePort port;
		
		// Add 'in' ports
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 0);
		port.setPortLabel("Data in");
		port.setPortDataStructure(ScriptModel.DATA_ITEM_COLLECTION);
		ports.add(port);
		port = new ModulePort(this, ScriptModel.INPUT_PORT, 1);
		port.setPortLabel("Initial positions");
		ports.add(port);
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_IN, 2);
		port.setPortLabel("Start");
		ports.add(port);
		
		// Add 'out' port for low-D representation vectors
		port = new ModulePort(this, ScriptModel.OUTPUT_PORT, 0);
		port.setPortLabel("Output");
		ports.add(port);
		
		// Add 'out' port for convergence trigger
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 1);
		port.setPortLabel("Convergence trigger");
		ports.add(port);
		
		// Add 'out' port for start trigger
		port = new ModulePort(this, ScriptModel.TRIGGER_PORT_OUT, 2);
		port.setPortLabel("Started");
		ports.add(port);
		addPorts(ports);
	}

	/**
	 * This is called when a connected module wants to notify this module of a
	 * change
	 */
	public void update(ModulePort fromPort, ModulePort toPort, ArrayList arg)
	{
		if (arg != null)
		{
			if (toPort.getKey().equals("i0"))
			{
				// High D data has been sent to the first in-port
				// Only accept it if the set is large enough
				if (((DataItemCollection) arg.get(0)).getSize() > (neighbourSize + sampleSize))
				{
					// First, if the spring model is already
					// running, stop it
					bHigh_D_InputRemoved = true;
					stopSpringModel();
					getOutPort(0).sendData(null);
					bHigh_D_InputRemoved = false;
					dataItems = (DataItemCollection) arg.get(0);
		
					if (dataItems != null)
						initSpringModel();
					startControls();
					
					// If the control panel is showing then enable the controls
					if (controlPanel != null)
					{
						controlPanel.setTriggerText();
						controlPanel.enableDisableControls(true);
					}
				}
				else
				{
					// Data set is too small
					bHigh_D_InputRemoved = true;
					nullifyReferences();
					stopSpringModel();
					getOutPort(0).sendData(null);
					
					if (controlPanel != null)
						controlPanel.enableDisableControls(false);
					
					startControls();
				}
			}
			else if (toPort.getKey().equals("i1"))
			{
				// Low D data has been sent to intialise the
				// starting positions of items
				if ((ArrayList) arg.get(1) != null)
				{
					if (dataItems != null)
					{
						setInitialPositions((ArrayList) arg.get(1),
								(DataItemCollection) arg.get(0));
						getOutPort(0).sendData(transferData);
					}
				}
			}
			else if (toPort.getKey().equals("i2"))
			{
				// A trigger has been sent to start the spring model
				if (arg.get(0) instanceof String)
				{
					if ((!((String) arg.get(0)).equals("start"))
							&& (!((String) arg.get(0)).equals("first")))
					{
						if (dataItems != null)
						{
							// Send a trigger stating that the model has started
							ArrayList transferD = new ArrayList();
							transferD.add("start");
							getOutPort(2).sendData(transferD);
							startSpringModel();
						}
					}
				}
			}
		}
		else
		{
			if (toPort.getKey().equals("i0"))
			{
				// Input module was deleted
				bHigh_D_InputRemoved = true;
				nullifyReferences();
				stopSpringModel();
				getOutPort(0).sendData(null);
			}
			startControls();
		}
	}

	/**
	 * When the module recieves position data, make sure that the correct
	 * positions for this module's dataItems are set
	 */
	private void setInitialPositions(ArrayList pos, DataItemCollection dataIn)
	{
		Coordinate thisCoord;
		Coordinate newCoord;
		int dataItemID = 0;
		int dataInID = 0;
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			// Find each dataItems corresponding position in the input data
			dataItemID = dataItems.getDataItem(i).getID();
			for (int j = 0; j < dataIn.getSize(); j++)
			{
				dataInID = dataIn.getDataItem(j).getID();
				if (dataItemID == dataInID)
				{
					thisCoord = (Coordinate) position.get(i);
					newCoord = (Coordinate) pos.get(j);
					// Set the input positions as the current positions for
					// the output
					thisCoord.set(newCoord.getX(), newCoord.getY(), newCoord.getW(),
							newCoord.getZ());
					break;
				}
			}
		}
	}

	/**
	 * Initalises the data structures needed for the spring model
	 * 
	 */
	private void init()
	{
		position = new ArrayList();
		position.clear();
		velocity = new ArrayList();
		velocity.clear();
		force = new ArrayList();
		force.clear();
		// Alias all of the position, vel & force vals from the dataItems
		// so that they can be accessed locally - makes it a wee bitty faster
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			position.add(new Coordinate());
			velocity.add(new Vect());
			force.add(new Vect());
		}
	}

	/**
	 * Randomizes the starting locations of the data set. To be called once at
	 * startup preferably
	 */
	private void randomizePositions()
	{
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			Coordinate p = (Coordinate) position.get(i);
			p.set(rand.nextDouble() * layoutBounds + rangeLo, rand.nextDouble()
					* layoutBounds + rangeLo, rand.nextDouble() * layoutBounds + rangeLo,
					rand.nextDouble() * layoutBounds + rangeLo);
		}
		// Send these positions to the low-D out-port
		transferData = new ArrayList();
		transferData.add(dataItems);
		transferData.add(position);
		getOutPort(0).sendData(transferData);
	}

	/**
	 * Implementation of the runnable interface
	 */
	public void run()
	{
		Thread thisThread = Thread.currentThread();
		while (thread == thisThread)
		{
			try
			{
				doIteration();
				Thread.yield();
			}
			catch (java.lang.Exception e2)
			{
				// An exception may have arisen because the DataSource data has
				// been changed whilst in the last iteration of the current
				// thread
			}
			
			// Only pass the modified positions to the output
			// Do this after every 10 iterations
			if ((numIterations % 10) == 0)
				getOutPort(0).sendData(transferData);
			
			Thread.yield();
		}
		if (!bHigh_D_InputRemoved)
			getOutPort(0).sendData(transferData);
		else
			nullifyReferences();
	}

	/**
	 * When the high D input to the spring model has been removed nullify all of
	 * the references previously stored
	 */
	private void nullifyReferences()
	{
		dataItems = null;
		position = null;
		velocity = null;
		force = null;
		neighbours = null;
		samples = null;
		thisSampleDists = null;
		distances = null;
	}

	/**
	 * Method to perform one iteration of the layout algorithm for this layout
	 * model
	 */
	private void doIteration() throws NullPointerException
	{
		if (startTime == 0)
			startTime = System.currentTimeMillis();
		
		// Iterate over whole data set
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			// Calculate the forces that will be exerted on this object
			calcForces(i);
		}
		
		for (int i = 0; i < dataItems.getSize(); i++)
		{
			if (Coordinate.getActiveDimensions() == 4)
				calcTimeForce(i);
			if (Coordinate.getActiveDimensions() >= 3)
				calcGravityForce(i);
			
			// Integrate the changes that have just been calculated to calc
			// this objects new velocity and force
			integrateChanges(i);
		}
		
		numIterations++;
		lblIterNumber.setText("Iterations: " + (new Integer(numIterations)).toString());
	}

	/**
	 * Calculates the forces that will be exerted on dataItem with index index
	 * Calcs forces only by looking at neighbours and samples lists. Overrides
	 * the method in SpringModel
	 * 
	 * @param index
	 *            The index of the dataItem that forces are to be calculated on
	 */
	private void calcForces(int index)
	{
		// First randomize the sample
		randomizeSample(index);
		ArrayList neighbs = (ArrayList) neighbours.get(index);

		// Iterate thro' neighbour set, calcing force based on sim &
		// euclidean dist
		for (int i = 0; i < neighbourSize; i++)
		{
			addForces(index, ((Integer) neighbs.get(i)).intValue(),
					((Double) ((ArrayList) distances.get(index)).get(i)).doubleValue());
		}
		
		ArrayList sample = (ArrayList) samples.get(index);
		
		// Iterate thro' sample , calcing force based on sim & eucldiean dist
		// The following 'if' statement is used to stop force calculations on
		// the sample set
		// after a certain amount of time. The effect of this is to concentrate
		// only on local
		// areas of the layout, discounting larger distances
		// if ((numIterations < 150) || (numIterations < (Math.sqrt((new
		// Double(dataItems.getSize())).doubleValue()))))
		if (!dataItems.getBinary())
			for (int i = 0; i < sampleSize; i++)
			{
				addForces(index, ((Integer) sample.get(i)).intValue(),
						((Double) thisSampleDists.get(i)).doubleValue());
			}
		
		// Check the sample to see if any of them would make good neighbours
		findNewNeighbours(index);
	}

	/**
	 * Calculates the force that will be acting between obj1 and obj2 This is
	 * based on the difference between their actual distance and their high
	 * dimensional distance.
	 * 
	 * WITH CACHING OF DISTANCES
	 * 
	 * @param obj1
	 * @param obj2
	 * @param desiredDist
	 */
	private void addForces(int obj1, int obj2, double desiredDist)
	{
		Coordinate p1 = (Coordinate) position.get(obj1);
		Coordinate p2 = (Coordinate) position.get(obj2);
		Vect v = new Vect(p1, p2);
		double realDist = v.getLength();
		if (desiredDist == Double.MAX_VALUE) // if couldn't do comparison
			desiredDist = realDist;
		
		// spring force to attain ideal seperation
		double spring = springForce * (realDist - desiredDist);
		
		// get the velocity vector between these two points
		// this is used to calc a damping factor, to stop everything
		// getting too fast
		Vect relativeVel = new Vect((Vect) velocity.get(obj1), (Vect) velocity.get(obj2));
		Vect unitVect = v.normalizeVector();
		
		// rate of change of separation
		double separationSpeed = relativeVel.dotProduct(unitVect);
		
		// force due to damping of separation
		double damping = dampingFactor * separationSpeed;
		
		// add on the force component to each dimension
		unitVect.scale(spring + damping);
		
		// add this vector onto the force of obj1
		((Vect) force.get(obj1)).add(unitVect);
		
		// and subtract from the force of obj2
		((Vect) force.get(obj2)).sub(unitVect);
	}

	/**
	 * For the object at index point, check thro' its samples list to check if
	 * any of those objects would make better neighbours than the ones currently
	 * in the neighbours list.
	 * 
	 * @param index
	 *            The index of the element whose samples list should be examined
	 *            for better neighbours
	 */
	private void findNewNeighbours(int index)
	{
		ArrayList sample = (ArrayList) samples.get(index);
		ArrayList neighbs = (ArrayList) neighbours.get(index);
		Hashtable ht = new Hashtable();
		
		for (int i = 0; i < sampleSize; i++)
		{
			// Get the sample Object index
			int sampObj = ((Integer) sample.get(i)).intValue();
		
			// Check to see if this value would be suitable as a new neighbour
			if (((Double) thisSampleDists.get(i)).doubleValue() < ((Double) ((ArrayList) distances
					.get(index)).get(0)).doubleValue())
			{
				neighbs.set(0, new Integer(sampObj));
				((ArrayList) distances.get(index)).set(0, (Double) thisSampleDists.get(i));
			
				for (int p = 0; p < neighbourSize; p++)
				{
					ht.put((Integer) neighbs.get(p), new Integer(p));
				}
				
				// sort the arraylist into ascending order
				NeighbourComparator comp = new NeighbourComparator(dataItems, index,
						(ArrayList) distances.get(index), ht);
				
				// then sort the neighbour set and distance set
				Collections.sort(neighbs, comp);
				Collections.sort(((ArrayList) distances.get(index)));
				Collections.reverse(((ArrayList) distances.get(index)));
			}
		}
	}

	/**
	 * Creates a new arrayList of random numbers to be used by the samples
	 * ArrayList. This list will contain a sampleSize random numbers,
	 * corresponding to dataItem indices, such that none of the values are the
	 * same as ones already in the sample or already in the neighbours list and
	 * are between 0 and dataItems.getSize(). THe resulting list will be stored
	 * in samples[index].
	 * 
	 * @param index
	 *            The index of the samples arrayList to store the result
	 */
	private void randomizeSample(int index)
	{
		// The neighbours list, which is not wanted in this sample
		HashSet exclude = new HashSet((ArrayList) neighbours.get(index));
		exclude.add(new Integer(index));
		ArrayList newSample = Utils.createRandomSample(null, exclude, dataItems.getSize(),
				sampleSize);
		
		for (int y = 0; y < sampleSize; y++)
		{
			thisSampleDists.set(y, new Double(dataItems.getDesiredDist(index,
					((Integer) newSample.get(y)).intValue(), springVar)));
		}
		
		samples.set(index, newSample);
	}

	/**
	 * Method to simulate gravity acting on the system, does this by dividing
	 * the z component of the force
	 * 
	 * @param index
	 *            The index of the object to calc Gravity force for
	 */
	private void calcGravityForce(int index)
	{
		double height = ((Coordinate) position.get(index)).getZ();
		Vect f = (Vect) force.get(index);
		Vect v = (Vect) velocity.get(index);
		f.setZ(-(height * gravityForce) - (v.getZ() * gravityDampingForce));
	}

	/**
	 * Method to apply a similar effect on the fourth dimension, which I have
	 * called time, to flatten everything out to 2D
	 * 
	 * @param index
	 *            The index of the object to calc Time force for
	 */
	private void calcTimeForce(int index)
	{
		double time = ((Coordinate) position.get(index)).getW();
		Vect f = (Vect) force.get(index);
		Vect v = (Vect) velocity.get(index);
		f.setW(-(time * timeForce) - (v.getW() * timeDampingForce));
	}

	/**
	 * Integrates the changes that have already been calculated. Uses the force
	 * and velocity calculations, to move the position based on the current
	 * velocity and then to alter the current velocity based on the forces
	 * acting on this object.
	 * 
	 * @param index
	 *            The index of the object to integrate changes for
	 */
	private void integrateChanges(int index)
	{
		// Adjust the force calculation to be the average force, this
		// involves scaling by the number of calcs done
		Vect f = (Vect) force.get(index);
		f.scale(dataSizeFactor);
		// Scale velocity by force and freeness
		Vect vel = (Vect) velocity.get(index);
		Vect scaleForce = new Vect(f);
		scaleForce.scale(deltaTime);
		vel.add(scaleForce);
		vel.scale(freeness);
		// Add velocity onto position
		Vect scaleVel = new Vect(vel);
		scaleVel.scale(deltaTime);
		((Coordinate) position.get(index)).add(scaleVel);
	}

	/**
	 * Returns the coordinate position of the object corresponding to the index
	 * index
	 * 
	 * @param index
	 *            The index of the object
	 * @return The coordinate of the object
	 */
	public Coordinate getPosition(int index)
	{
		return (Coordinate) position.get(index);
	}

	/**
	 * Returns the data item at index index.
	 * 
	 * @param index
	 *            The index of the data item wanted
	 * @return The data item that was at this index
	 */
	public DataItem getDataItem(int index)
	{
		return dataItems.getDataItem(index);
	}

	/**
	 * Returns the dataItemCollection object that this layoutmodel is
	 * representing.
	 * 
	 * @return The DataItemCollection that this model is laying out
	 */
	public DataItemCollection getDataItemCollection()
	{
		return dataItems;
	}

	/**
	 * Accessor method for the dataItems object
	 * 
	 * @param dataItems
	 *            THe dataItemCollection to be used with this layout model
	 */
	public void setDataItemCollection(DataItemCollection dataItems)
	{
		this.dataItems = dataItems;
	}

	/**
	 * Returns the number of iterations that have been carried out by this
	 * layout model
	 * 
	 * @return The number of iterations that this layout model has done
	 */
	public int getNumIterations()
	{
		return numIterations;
	}

	/**
	 * Calculates the approximate error in this layout, does this by calcing the
	 * value for a subset of the data set to get an approximation of the error
	 * without slowing down the layout too much.
	 * 
	 * @return The approximation of the avg error
	 */
	public double getApproxError()
	{
		ArrayList sample = Utils.createRandomSample(null, null, dataItems.getSize(), Math
				.min(50, dataItems.getSize()));
		double error = 0.0;
		int numComps = 0;
		double lowDist = 0.0;
		double highDist = 0.0;
		
		for (int i = 1; i < sample.size(); i++)
		{
			int obj1 = ((Integer) sample.get(i)).intValue();
			for (int j = 0; j < i; j++)
			{
				int obj2 = ((Integer) sample.get(j)).intValue();
				Vect v = new Vect((Coordinate) position.get(obj1), (Coordinate) position
						.get(obj2));
				lowDist = v.getLength();
				highDist = dataItems.getDesiredDist(obj1, obj2, springVar);
				error += (lowDist - highDist);
				numComps++;
			}
		}
		
		return error / (double) numComps;
	}

	/**
	 * Returns the average error in the data set
	 * 
	 * @return the average error
	 */
	public double getAvgError()
	{
		double error = 0.0;
		int numComps = 0;
		
		for (int i = 1; i < dataItems.getSize(); i++)
		{
			for (int j = 0; j < i; j++)
			{
				Vect v = new Vect((Coordinate) position.get(i), (Coordinate) position
						.get(j));
				double lowDist = v.getLength();
				double highDist = dataItems.getDesiredDist(i, j, springVar);
				error += (lowDist - highDist);
				numComps++;
			}
		}
		
		return error / (double) numComps;
	}

	/**
	 * Returns an approximation of the average error in the data set
	 * 
	 * @return An approx of the avg velocity
	 */
	public double getApproxVelocity()
	{
		ArrayList sample = Utils.createRandomSample(null, null, dataItems.getSize(), Math
				.min(50, dataItems.getSize()));
		double totalVel = 0.0;
		
		for (int i = 0; i < sample.size(); i++)
		{
			int index = ((Integer) sample.get(i)).intValue();
			totalVel += ((Vect) velocity.get(index)).getLength();
		}
		
		return totalVel / (double) sample.size();
	}

	/**
	 * Returns the average velocity in the data set
	 * 
	 * @return the average velocity
	 */
	public double getAvgVelocity()
	{
		double totalVel = 0.0;
		
		for (int i = 0; i < dataItems.getSize(); i++)
			totalVel += ((Vect) velocity.get(i)).getLength();
		
		return totalVel / (double) dataItems.getSize();
	}

	/**
	 * Returns the average stress in the data set
	 * 
	 * @return the average stress
	 */
	public double getAvgStress()
	{
		double lowDist = 0.0;
		double highDist = 0.0;
		double totalLowDist = 0.0;
		int numComps = 0;
		double stress = 0.0;
		
		for (int i = 1; i < dataItems.getSize(); i++)
		{
			for (int j = 0; j < i; j++)
			{
				Vect v = new Vect((Coordinate) position.get(i), (Coordinate) position
						.get(j));
				lowDist = v.getLength();
				highDist = dataItems.getDesiredDist(i, j, springVar);
				stress += (lowDist - highDist) * (lowDist - highDist);
				totalLowDist += (lowDist * lowDist);
				numComps++;
			}
		}
		
		stress = stress / totalLowDist;
		return stress;
	}

	/**
	 * @return the approximate stress in the layout
	 */
	public double getApproxStress()
	{
		ArrayList sample = Utils.createRandomSample(null, null, dataItems.getSize(), Math
				.min(50, dataItems.getSize()));
		double stress = 0.0;
		int numComps = 0;
		double lowDist = 0.0;
		double highDist = 0.0;
		double totalLowDist = 0.0;
		
		for (int i = 1; i < sample.size(); i++)
		{
			int obj1 = ((Integer) sample.get(i)).intValue();
			for (int j = 0; j < i; j++)
			{
				int obj2 = ((Integer) sample.get(j)).intValue();
				Vect v = new Vect((Coordinate) position.get(obj1), (Coordinate) position
						.get(obj2));
				lowDist = v.getLength();
				highDist = dataItems.getDesiredDist(obj1, obj2, springVar);
				stress += (lowDist - highDist) * (lowDist - highDist);
				totalLowDist += (lowDist * lowDist);
				numComps++;
			}
		}
		
		stress = stress / totalLowDist;
		return stress / (double) numComps;
	}

	/**
	 * Method to restore action listener for the JButtons
	 */
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException,
			java.lang.ClassNotFoundException
	{
		in.defaultReadObject();
		addButtonActionListeners();
	}

	public void beforeSerialise()
	{
		// Don't try to serialise threads
		if (start.getText().equals("Stop"))
			stopSpringModel();
	}
}

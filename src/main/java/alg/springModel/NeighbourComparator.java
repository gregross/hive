/**
 * Algorithmic testbed
 * 
 * NeighbourComparator
 * 
 * Implementation of the comparator model, used for sorting a neighbour
 * set based on the high dimensional distance of the elements
 *
 * @author Andrew Didsbury, Greg Ross
 */

package alg.springModel;

import data.*;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.ArrayList;

public class NeighbourComparator implements Comparator, java.io.Serializable
{
    // Versioning for serialisation
    
    static final long serialVersionUID = 50L;
    
    private DataItemCollection dataItems;
    private int index;
    private ArrayList distances;
    private boolean noDists;
    private Hashtable ht;
    
    /**
     * constructor: takes the dataItemCollection that contain the 
     * desired distances, and the index that this is the neighbour list of
     * 
     * @param dataItems the data item collection
     * @param index The index of the owner of this neighbour set
     * @param dists The desired distances for index's neighbour set
     */
     
    public NeighbourComparator(DataItemCollection dataItems, int index, ArrayList dists, Hashtable ht)
    {
        this.dataItems = dataItems;
        this.index     = index;
        distances = dists;
        this.ht = ht;
        noDists=false;
    }
    
     /**
     * constructor: takes the dataItemCollection that contain the 
     * desired distances, and the index that this is the neighbour list of
     * 
     * @param dataItems the data item collection
     * @param index The index of the owner of this neighbour set
     */
     
    public NeighbourComparator(DataItemCollection dataItems, int index)
    {
        this.dataItems = dataItems;
        this.index     = index;
        noDists = true;
    }
   
    /**
     * Compares its two arguments for order.
     *
     * @param o1 The first object to be compared
     * @param o2 The other object to be compared
     * @return The result of the comparison
     */
     
    public int compare(Object o1, Object o2) 
    {
        int obj1 = ((Integer)o1).intValue();
        int obj2 = ((Integer)o2).intValue();
        double d1, d2;


        if (noDists) 
	{
            d1 = dataItems.getDesiredDist(index, obj1);
            d2 = dataItems.getDesiredDist(index, obj2);
            if (  d1 == d2) 
                return 0;
            else if ( d1 < d2) 
	    {
                return 1;
            }
            else 
	    {
                return -1;
            }
        } 
	else 
	{ 
            d1 =((Double)distances.get(((Integer)ht.get(new Integer(obj1))).intValue())).doubleValue();
            d2 =((Double)distances.get(((Integer)ht.get(new Integer(obj2))).intValue())).doubleValue();
            if ( d1 == d2) 
                return 0;
            else if ( d1 < d2) 
	    {
                return 1;
            }
            else 
	    {
                return -1;
            }
        }
    }

    /**
     * Define whether or not this class is equivalent to some other object, 
     * never used in this implementation
     */
     
    public boolean equals(Object obj) 
    {
        return false;
    }
}

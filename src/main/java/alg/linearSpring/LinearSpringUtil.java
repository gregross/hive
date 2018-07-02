package alg.linearSpring;

import java.util.ArrayList;
import data.*;

public class LinearSpringUtil
{
	private static ArrayList anchors = new ArrayList();
	
	public static ArrayList getAnchors(DataItemCollection dataItems)
	{
		int dims = dataItems.getFields().size();
		double[] minVals = new double[dims];
		double[] maxVals = new double[dims];
		int[] minIndices = new int[dims];
		int[] maxIndices = new int[dims];
		
		for (int i = 0; i < dims; i++)
		{
			minVals[i] = Double.MAX_VALUE;
			maxVals[i] = Double.MIN_VALUE;
		}
		
		ArrayList data = dataItems.getDataItems();
		DataItem item;
		double val;
		
		for(int i = 0; i < dims; i++)
		{
			for (int j = 0; j < data.size(); j++)
			{
				item = (DataItem)data.get(j); 
				val = ((Double)item.getValue(i)).doubleValue();
				
				if (val < minVals[i])
				{
					minVals[i] = val;
					minIndices[i] = j;
				}
				
				if (val > maxVals[i])
				{
					maxVals[i] = val;
					maxIndices[i] = j;
				}
			}
		}
		
		Integer index;
		
		for (int i = 0; i < minIndices.length; i++)
		{
			index = new Integer(minIndices[i]);
			
			if (!anchors.contains(index))
				anchors.add(index);
		}
		
		for (int i = 0; i < maxIndices.length; i++)
		{
			index = new Integer(maxIndices[i]);
			
			if (!anchors.contains(index))
				anchors.add(index);
		}
		
		return anchors;
	}
}

/**
 * Algorithmic test bed
 * 
 * ArrowYs
 */

package alg.timeSeries;

import java.util.ArrayList;
import java.io.Serializable;

public class ArrowXs implements Serializable
{
    // Versioning for serialisation
	
    static final long serialVersionUID = 50L;
	
    private int left;
    private int right;
    private int width;
    
    /**
     * constructor: Takes no params and sets the initial values to be 0
     */
    public ArrowXs()
    {
	this(0, 0, 0);
	
    }

    /**
     * constructor: takes two int values to represent the coords 
     * and a value for the height
     */
    public ArrowXs(int x, int y, int w)
    {
	left = x;
	right = y;
	width = w;
    }
    
   
    public int getLeft()  {
	return left;
    }
    
    public int getRight()   {
	return right;
    }
    
    
    public void setLeft(int x){
	left = x;
    }
    
    public void setRight(int y)
    {
	right = y;
    }

    public void setPosn(int arrow, int val){
	if (arrow==0)
	    left = val;
	if (arrow==1)
	    right = val;
    }

    public int hitArrow(int y){//int accuracy
	// returns 0 if hit left arrow, 1 if right  else -1
	if (y<=left && y>=(left-width))//(Math.abs(y-top-height) <= accuracy)
	    return 0;
	if (y>=right && y<=(right+width)){//(Math.abs(y-bottom) <= accuracy)
	    return 1;
	}
	return -1;
    }

}

/**
 * Algorithmic test bed
 * 
 * ArrowYs
 */

package alg.parCoords;

import java.util.ArrayList;
import java.io.Serializable;

public class ArrowYs implements Serializable
{
    // Versioning for serialisation
	
    static final long serialVersionUID = 50L;
	
    private int   top;
    private int bottom;
    private int height;
    
    /**
     * constructor: Takes no params and sets the initial values to be 0
     */
    public ArrowYs()
    {
	this(0, 0,0);
	
    }

    /**
     * constructor: takes two int values to represent the coords 
     * and a value for the height
     */
    public ArrowYs(int x, int y, int h)
    {
	top = x;
	bottom = y;
	height = h;
    }
    
   
    public int getTop()  {
	return top;
    }
    
    public int getBottom()   {
	return bottom;
    }
    
    
    public void setTop(int x){
	top = x;
    }
    
    public void setBottom(int y)
    {
	bottom = y;
    }

    public void setPosn(int arrow, int val){
	if (arrow==0)
	    top = val;
	if (arrow==1)
	    bottom = val;
    }

    public int hitArrow(int y){//int accuracy
	// returns 0 if hit top arrow, 1 if bottom  else -1
	if (y>=top && y<=(top+height))//(Math.abs(y-top-height) <= accuracy)
	    return 0;
	if (y<=bottom && y>=(bottom-height)){//(Math.abs(y-bottom) <= accuracy)
	    return 1;
	}
	return -1;
    }

}

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package alg.scatterplot;

import data.*;
import parent_gui.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Display excentric labels around items in a labeledComponent.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class DefaultExcentricLabels
    extends MouseAdapter
    implements Comparator, MouseMotionListener {
    
    Timer insideTimer;
    ArrayList hits;
    Rectangle2D.Double cursorBounds;
    int centerX;
    int centerY;
    int focusSize = 50;
    Point2D.Double[] itemPosition;
    Point2D.Double[] linkPosition;
    Point2D.Double[] labelPosition;
    Point2D.Double[] left;
    int leftCount;
    Point2D.Double[] right;
    int rightCount;
    boolean xStable = false;
    boolean yStable = false;
    JComponent component;
    ScatterPanel labeledComponent;
    boolean visible;
    int gap = 5;
    int maxLabels;
    int labelCount;
    int threshold = 20;
    boolean opaque = true;
    Color backgroundColor = Color.WHITE;
    Stroke wideStroke = new BasicStroke(1);
    private Font labelFont = new Font("sansserif", Font.PLAIN, 12);
    private SelectionHandler selected = null;
    DataItemCollection dataItems = null;

    /**
     * Constructor for DefaultExcentricLabels.
     */
    public DefaultExcentricLabels(DataItemCollection dataItems, SelectionHandler selected)
    {
	this.selected = selected;
        this.dataItems = dataItems;
	cursorBounds =
            new Rectangle2D.Double(0, 0, focusSize, focusSize);
	    
        insideTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
            }
        });
        insideTimer.setRepeats(false);
        setMaxLabels(20);
    }

    public void setVisualization(ScatterPanel labeledComponent) {
        this.labeledComponent = labeledComponent;
        JComponent c =
            (labeledComponent != null)
                ? labeledComponent
                : null;

        if (component == c)
            return;
        if (component != null) {
            component.removeMouseListener(this);
            component.removeMouseMotionListener(this);
        }
        component = c;
        if (component != null) {
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
        }
    }

    public String getItem(int index)
    {
	int i = ((Integer)hits.get(index)).intValue();
	
	if (i < dataItems.getRowLabels().size())
	{
		if (dataItems.getBinary())
			return(String)dataItems.getRowLabels().get(i) + " (" + dataItems.getBinaryFreq()[i] + ")";
		else
			return(String)dataItems.getRowLabels().get(i);
	}
	else
		return "No label";
    }
    
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
	if (labeledComponent == null || !visible || labeledComponent.getDraggingBox())
            return;
    	
	int i, j, index;
	graphics.setFont(labelFont);
        FontMetrics fm = graphics.getFontMetrics();
        computeExcentricLabels(graphics, bounds);
	
        Line2D.Double line = new Line2D.Double();
        for (i = 0; i < labelCount; i++)
	{
		// Change the font for labels representing selected items.
		
		if (hits != null)
		{
			index = ((Integer)hits.get(i)).intValue();
			if (selected.getState(index))
				labelFont = new Font("sansserif", Font.BOLD, 12);
			else
				labelFont = new Font("sansserif", Font.PLAIN, 10);
		}
		
		graphics.setFont(labelFont);
		fm = graphics.getFontMetrics();
		
            String lab = getItem(i);
	    
            if (lab == null) {
                lab = "item" + getItem(i);
            }
	    
            Point2D.Double pos = labelPosition[i];
            if (opaque) {
                graphics.setColor(Color.yellow);
                Rectangle2D sb = fm.getStringBounds(lab, graphics);
                graphics.fillRect(
                    (int) (pos.x + sb.getX() - 2),
                    (int) (pos.y + sb.getY() - 2),
                    (int) sb.getWidth() + 2,
                    (int) sb.getHeight() + 2);
                graphics.setColor(Color.green);
                graphics.drawRect(
                    (int) (pos.x + sb.getX() - 2),
                    (int) (pos.y + sb.getY() - 2),
                    (int) sb.getWidth() + 2,
                    (int) sb.getHeight() + 2);
            }
            graphics.setColor(Color.black);
            graphics.drawString(lab, (int) (pos.x), (int) (pos.y));
            line.setLine(itemPosition[i], linkPosition[i]);
            graphics.setColor(backgroundColor);
            Stroke save = graphics.getStroke();
            graphics.setStroke(wideStroke);
            graphics.draw(line);
            graphics.setStroke(save);
        }
        graphics.setColor(Color.RED);
        graphics.draw(cursorBounds);
    }

    protected void computeExcentricLabels(
        Graphics2D graphics,
        Rectangle2D bounds) {
        if (labeledComponent == null)
            return;

        cursorBounds.x = centerX - focusSize / 2;
        cursorBounds.y = centerY - focusSize / 2;

        if (hits != null)
            hits.clear();
        hits =labeledComponent.getRContents(cursorBounds.getBounds());
	
        labelCount = Math.min(maxLabels, hits.size());
        if (labelCount != 0) {
            computeItemPositions(graphics, bounds);
            projectLeftRight(graphics, bounds);
        }
    }

    protected void computeItemPositions(
        Graphics2D graphics,
        Rectangle2D bounds) {
	
	int index;
	
        for (int i = 0; i < labelCount; i++) {
	    index = ((Integer)hits.get(i)).intValue();
	    Shape s = labeledComponent.getPointShape(index);
            
            if (s == null) {
                itemPosition[i].setLocation(0, 0);
            }
            else {
		getShapeCenterIn(s, cursorBounds, itemPosition[i]);
            }
        }
    }

    private Point2D getShapeCenterIn(Shape s, Rectangle2D focus, Point2D ptOut) {
        Rectangle2D.Double inter = new Rectangle2D.Double();
        Rectangle2D rect = s.getBounds2D();
        Rectangle2D.intersect(focus, rect, inter);
        ptOut.setLocation(
            inter.getCenterX(),
            inter.getCenterY());
        return ptOut;
    }
    
    protected double comparableValueLeft(Point2D.Double pos) {
        if (yStable)
            return pos.y;
        else
            return Math.atan2(pos.y - centerY, centerX - pos.x);
    }

    protected double comparableValueRight(Point2D.Double pos) {
        if (yStable)
            return pos.getY();
        else
            return Math.atan2(pos.y - centerY, pos.x - centerX);
    }

    protected void projectLeftRight(
        Graphics2D graphics,
        Rectangle2D bounds) {
        int radius = focusSize / 2;
        int i;

        leftCount = 0;
        rightCount = 0;
        double maxHeight = 0;
        FontMetrics fm = graphics.getFontMetrics();

	int index;
        for (i = 0; i < labelCount; i++) {
		
		if (hits != null)
		{
			index = ((Integer)hits.get(i)).intValue();
			if (selected.getState(index))
				labelFont = new Font("sansserif", Font.BOLD, 12);
			else
				labelFont = new Font("sansserif", Font.PLAIN, 10);
		}
		graphics.setFont(labelFont);
		fm = graphics.getFontMetrics();
		
            Point2D.Double itemPos = itemPosition[i];
            String lab = getItem(i);
	    
            if (lab == null)
                lab = "item" + getItem(i);
            Rectangle2D sb = fm.getStringBounds(lab, graphics);
            Point2D.Double linkPos = linkPosition[i];
            Point2D.Double labelPos = labelPosition[i];

            maxHeight = Math.max(sb.getHeight(), maxHeight);
            if (itemPosition[i].getX() < centerX) {
                linkPos.y = comparableValueLeft(itemPos);
                if (xStable)
                    linkPos.x = itemPos.x - radius - gap;
                else
                    linkPos.x = centerX - radius - gap;
                labelPos.x = linkPos.x - sb.getWidth();
                left[leftCount++] = linkPos;
            }
            else {
                linkPos.y = comparableValueRight(itemPos);
                if (xStable)
                    linkPos.x = itemPos.x + radius + gap;
                else
                    linkPos.x = centerX + radius + gap;
                labelPos.x = linkPos.x;
                right[rightCount++] = linkPos;
            }
        }

        Arrays.sort(left, 0, leftCount, this);
        Arrays.sort(right, 0, rightCount, this);
	
	maxHeight += 3;
	
        double yMidLeft = leftCount * maxHeight / 2;
        double yMidRight = rightCount * maxHeight / 2;
        int ascent = fm.getAscent();

        for (i = 0; i < leftCount; i++) {
            Point2D.Double pos = left[i];
            pos.y = i * maxHeight + centerY - yMidLeft + ascent;
        }
        for (i = 0; i < rightCount; i++) {
            Point2D.Double pos = right[i];
            pos.y = i * maxHeight + centerY - yMidRight + ascent;
        }
        for (i = 0; i < linkPosition.length; i++) {
            labelPosition[i].y = linkPosition[i].y;
        }
    }

    /**
     * Returns the visible.
     * @return boolean
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visible.
     * @param visible The visible to set
     */
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            
	    if (!labeledComponent.getDraggingBox())
		    labeledComponent.repaint();
        }
    }

    /**
     * For sorting points vertically.
     *
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare(Object o1, Object o2) {
        double d =
            ((Point2D.Double) o1).getY() - ((Point2D.Double) o2).getY();
        if (d < 0)
            return -1;
        else if (d == 0)
            return 0;
        else
            return 1;
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        if (!labeledComponent.getDraggingBox())
		insideTimer.restart();
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        insideTimer.stop();
        setVisible(false);
    }

    /**
     * @see java.awt.event.MouseAdapter#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        setVisible(false);
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
    }

    int dist2(int dx, int dy) {
        return dx * dx + dy * dy;
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        if (isVisible()) 
	{
	
	    if (labeledComponent.getDraggingBox())
		setVisible(false);
	    else
	    {
		    if (dist2(centerX - e.getX(), centerY - e.getY())
			> threshold * threshold) {
			setVisible(false);
			insideTimer.restart();
		    }
	    }
	    labeledComponent.repaint();
        }
	else
	{
		if (!labeledComponent.getDraggingBox())
			insideTimer.restart();
	}
	
        centerX = e.getX();
        centerY = e.getY();
    }

    /**
     * Returns the gap.
     * @return int
     */
    public int getGap() {
        return gap;
    }

    /**
     * Sets the gap.
     * @param gap The gap to set
     */
    public void setGap(int gap) {
        this.gap = gap;
    }

    /**
     * Returns the maxLabels.
     * @return int
     */
    public int getMaxLabels() {
        return maxLabels;
    }

    void allocatePoints(Point2D.Double[] array) {
        for (int i = 0; i < array.length; i++)
            array[i] = new Point2D.Double();
    }

    /**
     * Sets the maxLabels.
     * @param maxLabels The maxLabels to set
     */
    public void setMaxLabels(int maxLabels) {
        this.maxLabels = maxLabels;
        itemPosition = new Point2D.Double[maxLabels];
        allocatePoints(itemPosition);
        linkPosition = new Point2D.Double[maxLabels];
        allocatePoints(linkPosition);
        labelPosition = new Point2D.Double[maxLabels];
        allocatePoints(labelPosition);
        left = new Point2D.Double[maxLabels];
        right = new Point2D.Double[maxLabels];
    }

    /**
     * Returns the threshold.
     *
     * When the mouse moves a distance larger than this
     * threshold since the last event, excentric labels
     * are disabled.
     *
     * @return int
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Sets the threshold.
     *
     * When the mouse moves a distance larger than the
     * specified threshold since the last event, excentric
     * labels are disabled.
     *
     * @param threshold The threshold to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    /**
     * Returns the focusSize.
     * @return int
     */
    public int getFocusSize() {
        return focusSize;
    }

    /**
     * Sets the focusSize.
     * @param focusSize The focusSize to set
     */
    public void setFocusSize(int focusSize) {
        this.focusSize = focusSize;
        cursorBounds =
            new Rectangle2D.Double(0, 0, focusSize, focusSize);
    }

    /**
     * Returns the backgroundColor.
     * @return Color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the opaque.
     * @return boolean
     */
    public boolean isOpaque() {
        return opaque;
    }

    /**
     * Sets the backgroundColor.
     * @param backgroundColor The backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Sets the opaque.
     * @param opaque The opaque to set
     */
    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

}

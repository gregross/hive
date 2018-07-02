/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------------------
 * TimeSeriesToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: TimeSeriesToolTipGenerator.java,v 1.6 2004/01/03 05:38:58 mungady Exp $
 *
 * Changes (since 30-May-2002):
 * ----------------------------
 * 30-May-2002 : Added series name to tool tip (DG);
 * 29-Aug-2002 : Modified so that series name is not shown if null (RA);
 * 23-Mar-2003 : Implemented Serializable (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 17-Nov-2003 : Implemented PublicCloneable (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A standard tool tip generator for time series plots.
 *
 * @author David Gilbert
 */
public class TimeSeriesToolTipGenerator implements XYToolTipGenerator, 
                                                   Cloneable, PublicCloneable,
                                                   Serializable {

    /** A formatter for the time. */
    private DateFormat dateFormat;

    /** A formatter for the value. */
    private NumberFormat numberFormat;

    /**
     * Default constructor.
     */
    public TimeSeriesToolTipGenerator() {

        this(DateFormat.getInstance(), NumberFormat.getNumberInstance());

    }

    /**
     * Creates a tool tip generator with the specified date and number format strings.
     *
     * @param dateFormat  the date format.
     * @param valueFormat  the value format.
     */
    public TimeSeriesToolTipGenerator(String dateFormat, String valueFormat) {
        this(new SimpleDateFormat(dateFormat), new DecimalFormat(valueFormat));
    }

    /**
     * Constructs a new tooltip generator using the specified number formats.
     *
     * @param dateFormat  the date formatter.
     * @param numberFormat  the number formatter.
     */
    public TimeSeriesToolTipGenerator(DateFormat dateFormat, NumberFormat numberFormat) {
        this.dateFormat = dateFormat;
        this.numberFormat = numberFormat;
    }

    /**
     * Returns the date formatter.
     *
     * @return the date formatter.
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Returns the number formatter.
     *
     * @return the number formatter.
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series number (zero-based index).
     * @param item  the item number (zero-based index).
     *
     * @return the tool tip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String result = "";
        String seriesName = data.getSeriesName(series);
        if (seriesName != null) {
            result += seriesName + ": ";
        }
        long x = data.getXValue(series, item).longValue();
        result = result + "date = " + this.dateFormat.format(new Date(x));

        Number y = data.getYValue(series, item);
        if (y != null) {
            result = result + ", value = " + this.numberFormat.format(y);
        }
        else {
            result = result + ", value = null";
        }

        return result;
    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    public Object clone() throws CloneNotSupportedException {
        
        TimeSeriesToolTipGenerator clone = (TimeSeriesToolTipGenerator) super.clone();

        if (this.dateFormat != null) {
            clone.dateFormat = (DateFormat) this.dateFormat.clone();
        }
        
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat) this.numberFormat.clone();
        }
        
        return clone;
        
    }
    
    /**
     * Tests if this object is equal to another.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (o instanceof TimeSeriesToolTipGenerator) {
            TimeSeriesToolTipGenerator generator = (TimeSeriesToolTipGenerator) o;
            return (this.dateFormat.equals(generator.getDateFormat())
                    && this.numberFormat.equals(generator.getNumberFormat()));
        }
        return false;

    }

}

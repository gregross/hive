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
 * -----------------
 * Performance2.java
 * -----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited).
 * Contributor(s):   -;
 *
 * $Id: Performance2.java,v 1.6 2004/01/03 05:11:01 mungady Exp $
 *
 * Changes (since 11-Oct-2002)
 * ---------------------------
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package org.jfree.chart.demo;

import java.util.Date;

/**
 * A basic performance test for a couple of common operations.
 *
 * @author David Gilbert
 */
public class Performance2 {

    /** A double primitive. */
    private double primitive = 42.0;

    /** A number object. */
    private Number object = new Double(42.0);

    /**
     * Default constructor.
     */
    public Performance2() {
    }

    /**
     * Just use double value - should be fast.
     *
     * @return the double value.
     */
    public double getPrimitive() {
        return primitive;
    }

    /**
     * Creates a Number object every time the primitive is accessed - should be really slow.
     *
     * @return creates and returns a Number object.
     */
    public Number getPrimitiveAsObject() {
        return new Double(primitive);
    }

    /**
     * Returns the object - caller has to use doubleValue() method.
     *
     * @return an existing Number object.
     */
    public Number getObject() {
        return object;
    }

    /**
     * Returns a double value generated from the Object - should be similar to previous method,
     * but is not!
     *
     * @return the doubleValue() for the Number.
     */
    public double getObjectAsPrimitive() {
        return object.doubleValue();
    }

    /**
     * Cycles through accessing the primitive.
     *
     * @param count  the number of times to access.
     */
    public void getPrimitiveLoop(int count) {

        double d = 0.0;
        for (int i = 0; i < count; i++) {
            d = getPrimitive();
        }
        System.out.println(d);

    }

    /**
     * Cycles through accessing the primitive as an object.
     *
     * @param count  the number of times to access.
     */
    public void getPrimitiveAsObjectLoop(int count) {

        double d = 0.0;
        for (int i = 0; i < count; i++) {
            d = getPrimitiveAsObject().doubleValue();
        }
        System.out.println(d);

    }

    /**
     * Cycles through accessing the object as a primitive.
     *
     * @param count  the number of times to access.
     */
    public void getObjectAsPrimitiveLoop(int count) {

        double d = 0.0;
        for (int i = 0; i < count; i++) {
            d = getObjectAsPrimitive();
        }
        System.out.println(d);

    }

    /**
     * Cycles through accessing the object.
     *
     * @param count  the number of times to access.
     */
    public void getObjectLoop(int count) {

        double d = 0.0;
        for (int i = 0; i < count; i++) {
            d = getObject().doubleValue();
        }
        System.out.println(d);

    }

    /**
     * Outputs the current status to the console.
     *
     * @param label  the label.
     * @param start  the start time.
     * @param end  the end time.
     */
    public void status(String label, Date start, Date end) {
        long elapsed = end.getTime() - start.getTime();
        System.out.println(label + start.getTime() + "-->" + end.getTime() + " = " + elapsed);
    }

    /**
     * The starting point for the performance test.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        Performance2 performance = new Performance2();
        int count = 10000000;

        for (int repeat = 0; repeat < 3; repeat++) {  // repeat a few times just to make
                                                      // sure times are consistent
            Date s1 = new Date();
            performance.getPrimitiveLoop(count);
            Date e1 = new Date();
            performance.status("getPrimitive() : ", s1, e1);

            Date s2 = new Date();
            performance.getPrimitiveAsObjectLoop(count);
            Date e2 = new Date();
            performance.status("getPrimitiveAsObject() : ", s2, e2);

            Date s3 = new Date();
            performance.getObjectLoop(count);
            Date e3 = new Date();
            performance.status("getObject() : ", s3, e3);

            Date s4 = new Date();
            performance.getObjectAsPrimitiveLoop(count);
            Date e4 = new Date();
            performance.status("getObjectAsPrimitive() : ", s4, e4);

            System.out.println("-------------------");
        }
    }

}
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
 * --------------------------------
 * CategoryTextAnnotationTests.java
 * --------------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryTextAnnotationTests.java,v 1.3 2004/01/03 04:11:59 mungady Exp $
 *
 * Changes
 * -------
 * 19-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.annotations.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;

/**
 * Tests for the {@link CategoryTextAnnotation} class.
 *
 * @author David Gilbert
 */
public class CategoryTextAnnotationTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(CategoryTextAnnotationTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public CategoryTextAnnotationTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        CategoryTextAnnotation a1 = new CategoryTextAnnotation("Test", "Category", 1.0);
        CategoryTextAnnotation a2 = new CategoryTextAnnotation("Test", "Category", 1.0);
        assertTrue(a1.equals(a2));
        
        // category 
        a1.setCategory("Category 2");
        assertFalse(a1.equals(a2));
        a2.setCategory("Category 2");
        assertTrue(a1.equals(a2));

        // categoryAnchor
        a1.setCategoryAnchor(CategoryAnchor.START);
        assertFalse(a1.equals(a2));
        a2.setCategoryAnchor(CategoryAnchor.START);
        assertTrue(a1.equals(a2));

        // value 
        a1.setValue(0.15);
        assertFalse(a1.equals(a2));
        a2.setValue(0.15);
        assertTrue(a1.equals(a2));
      
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        CategoryTextAnnotation a1 = new CategoryTextAnnotation("Test", "Category", 1.0);
        CategoryTextAnnotation a2 = null;
        try {
            a2 = (CategoryTextAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("CategoryAnnotationTests.testCloning: failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        CategoryTextAnnotation a1 = new CategoryTextAnnotation("Test", "Category", 1.0);
        CategoryTextAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (CategoryTextAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}

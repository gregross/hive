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
 * ----------------
 * KeyedObject.java
 * ----------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedObject.java,v 1.4 2004/01/05 17:11:42 mungady Exp $
 *
 * Changes:
 * --------
 * 05-Feb-2003 : Version 1 (DG);
 *
 */

package org.jfree.data;

/**
 * A (key, object) pair.
 *
 * @author David Gilbert
 */
public class KeyedObject {

    /** The key. */
    private Comparable key;

    /** The object. */
    private Object object;

    /**
     * Creates a new (key, object) pair.
     *
     * @param key  the key.
     * @param object  the object.
     */
    public KeyedObject(Comparable key, Object object) {
        this.key = key;
        this.object = object;
    }

    /**
     * Returns the key.
     *
     * @return the key.
     */
    public Comparable getKey() {
        return this.key;
    }

    /**
     * Returns the object.
     *
     * @return the object.
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * Sets the object.
     *
     * @param object  the object.
     */
    public void setObject(Object object) {
        this.object = object;
    }

}

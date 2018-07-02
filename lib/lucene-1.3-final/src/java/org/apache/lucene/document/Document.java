package org.apache.lucene.document;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.index.IndexReader;       // for javadoc
import org.apache.lucene.search.Hits;             // for javadoc

/** Documents are the unit of indexing and search.
 *
 * A Document is a set of fields.  Each field has a name and a textual value.
 * A field may be stored with the document, in which case it is returned with
 * search hits on the document.  Thus each document should typically contain
 * stored fields which uniquely identify it.
 * */

public final class Document implements java.io.Serializable {
  DocumentFieldList fieldList = null;
  private float boost = 1.0f;

  /** Constructs a new document with no fields. */
  public Document() {}


  /** Sets a boost factor for hits on any field of this document.  This value
   * will be multiplied into the score of all hits on this document.
   *
   * <p>Values are multiplied into the value of {@link Field#getBoost()} of
   * each field in this document.  Thus, this method in effect sets a default
   * boost for the fields of this document.
   *
   * @see Field#setBoost(float)
   */
  public void setBoost(float boost) {
    this.boost = boost;
  }

  /** Returns the boost factor for hits on any field of this document.
   *
   * <p>The default value is 1.0.
   *
   * <p>Note: This value is not stored directly with the document in the index.
   * Documents returned from {@link IndexReader#document(int)} and
   * {@link Hits#doc(int)} may thus not have the same value present as when
   * this document was indexed.
   *
   * @see #setBoost(float)
   */
  public float getBoost() {
    return boost;
  }

  /** Adds a field to a document.  Several fields may be added with
   * the same name.  In this case, if the fields are indexed, their text is
   * treated as though appended for the purposes of search. */
  public final void add(Field field) {
    fieldList = new DocumentFieldList(field, fieldList);
  }

  /** Returns a field with the given name if any exist in this document, or
    null.  If multiple fields exists with this name, this method returns the
    last field value added. */
  public final Field getField(String name) {
    for (DocumentFieldList list = fieldList; list != null; list = list.next)
      if (list.field.name().equals(name))
	return list.field;
    return null;
  }

  /** Returns the string value of the field with the given name if any exist in
    this document, or null.  If multiple fields exist with this name, this
    method returns the last value added. */
  public final String get(String name) {
    Field field = getField(name);
    if (field != null)
      return field.stringValue();
    else
      return null;
  }

  /** Returns an Enumeration of all the fields in a document. */
  public final Enumeration fields() {
    return new DocumentFieldEnumeration(this);
  }

  /**
   * Returns an array of {@link Field}s with the given name.
   * This method can return <code>null</code>.
   *
   * @param name the name of the field
   * @return a <code>Field[]</code> array
   */
   public final Field[] getFields(String name) {
     List tempFieldList = new ArrayList();
     for (DocumentFieldList list = fieldList; list != null; list = list.next) {
       if (list.field.name().equals(name)) {
         tempFieldList.add(list.field);
       }
     }
     int fieldCount = tempFieldList.size();
     if (fieldCount == 0) {
       return null;
     }
     else {
       return (Field[])tempFieldList.toArray(new Field[] {});
     }
   }

  /**
   * Returns an array of values of the field specified as the method parameter.
   * This method can return <code>null</code>.
   * UnStored fields' values cannot be returned by this method.
   *
   * @param name the name of the field
   * @return a <code>String[]</code> of field values
   */
  public final String[] getValues(String name) {
    Field[] namedFields = getFields(name);
    if (namedFields == null)
      return null;
    String[] values = new String[namedFields.length];
    for (int i = 0; i < namedFields.length; i++) {
      values[i] = namedFields[i].stringValue();
    }
    return values;
  }

  /** Prints the fields of a document for human consumption. */
  public final String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Document<");
    for (DocumentFieldList list = fieldList; list != null; list = list.next) {
      buffer.append(list.field.toString());
      if (list.next != null)
	buffer.append(" ");
    }
    buffer.append(">");
    return buffer.toString();
  }
}

final class DocumentFieldList implements java.io.Serializable {
  DocumentFieldList(Field f, DocumentFieldList n) {
    field = f;
    next = n;
  }
  Field field;
  DocumentFieldList next;
}

final class DocumentFieldEnumeration implements Enumeration {
  DocumentFieldList fields;
  DocumentFieldEnumeration(Document d) {
    fields = d.fieldList;
  }

  public final boolean hasMoreElements() {
    return fields == null ? false : true;
  }

  public final Object nextElement() {
    Field result = fields.field;
    fields = fields.next;
    return result;
  }
}

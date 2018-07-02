package org.apache.lucene.index;

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

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.InputStream;
import org.apache.lucene.store.OutputStream;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BitVector;

/**
 * FIXME: Describe class <code>SegmentReader</code> here.
 *
 * @version $Id: SegmentReader.java,v 1.17 2003/12/15 23:04:42 cutting Exp $
 */
final class SegmentReader extends IndexReader {
  private boolean closeDirectory = false;
  private String segment;

  FieldInfos fieldInfos;
  private FieldsReader fieldsReader;

  TermInfosReader tis;

  BitVector deletedDocs = null;
  private boolean deletedDocsDirty = false;
  private boolean normsDirty = false;

  InputStream freqStream;
  InputStream proxStream;

  // Compound File Reader when based on a compound file segment
  CompoundFileReader cfsReader;

  private class Norm {
    public Norm(InputStream in) { this.in = in; }

    private InputStream in;
    private byte[] bytes;
    private boolean dirty;

    private void reWrite(String name) throws IOException {
      // NOTE: norms are re-written in regular directory, not cfs
      OutputStream out = directory().createFile(segment + ".tmp");
      try {
        out.writeBytes(bytes, maxDoc());
      } finally {
        out.close();
      }
      String fileName = segment + ".f" + fieldInfos.fieldNumber(name);
      directory().renameFile(segment + ".tmp",  fileName);
      this.dirty = false;
    }
  }
  private Hashtable norms = new Hashtable();

  SegmentReader(SegmentInfos sis, SegmentInfo si, boolean closeDir)
    throws IOException {
    this(si);
    closeDirectory = closeDir;
    segmentInfos = sis;
  }

  SegmentReader(SegmentInfo si)
    throws IOException {
    super(si.dir);
    segment = si.name;

    // Use compound file directory for some files, if it exists
    Directory cfsDir = directory();
    if (directory().fileExists(segment + ".cfs")) {
      cfsReader = new CompoundFileReader(directory(), segment + ".cfs");
      cfsDir = cfsReader;
    }

    // No compound file exists - use the multi-file format
    fieldInfos = new FieldInfos(cfsDir, segment + ".fnm");
    fieldsReader = new FieldsReader(cfsDir, segment, fieldInfos);

    tis = new TermInfosReader(cfsDir, segment, fieldInfos);

    // NOTE: the bitvector is stored using the regular directory, not cfs
    if (hasDeletions(si))
      deletedDocs = new BitVector(directory(), segment + ".del");

    // make sure that all index files have been read or are kept open
    // so that if an index update removes them we'll still have them
    freqStream = cfsDir.openFile(segment + ".frq");
    proxStream = cfsDir.openFile(segment + ".prx");
    openNorms(cfsDir);
  }

  protected final synchronized void doClose() throws IOException {
    if (deletedDocsDirty || normsDirty) {
      synchronized (directory()) {		  // in- & inter-process sync
        new Lock.With(directory().makeLock(IndexWriter.COMMIT_LOCK_NAME),
          IndexWriter.COMMIT_LOCK_TIMEOUT) {
          public Object doBody() throws IOException {

            if (deletedDocsDirty) {               // re-write deleted 
              deletedDocs.write(directory(), segment + ".tmp");
              directory().renameFile(segment + ".tmp", segment + ".del");
            }

            if (normsDirty) {               // re-write norms 
              Enumeration keys  = norms.keys();
              Enumeration values  = norms.elements();
              while (values.hasMoreElements()) {
                String field = (String)keys.nextElement();
                Norm norm = (Norm)values.nextElement();
                if (norm.dirty) {
                  norm.reWrite(field);
                }
              }
            }

            if(segmentInfos != null)
              segmentInfos.write(directory());
            else
              directory().touchFile("segments");
            return null;
          }
        }.run();
      }
      deletedDocsDirty = false;
      normsDirty = false;
    }

    fieldsReader.close();
    tis.close();

    if (freqStream != null)
      freqStream.close();
    if (proxStream != null)
      proxStream.close();

    closeNorms();

    if (cfsReader != null)
      cfsReader.close();

    if (closeDirectory)
      directory().close();
  }

  static final boolean hasDeletions(SegmentInfo si) throws IOException {
    return si.dir.fileExists(si.name + ".del");
  }

  public boolean hasDeletions() {
    return deletedDocs != null;
  }

  static final boolean usesCompoundFile(SegmentInfo si) throws IOException {
    return si.dir.fileExists(si.name + ".cfs");
  }

  protected final synchronized void doDelete(int docNum) throws IOException {
    if (deletedDocs == null)
      deletedDocs = new BitVector(maxDoc());
    deletedDocsDirty = true;
    deletedDocs.set(docNum);
  }

  public synchronized void undeleteAll() throws IOException {
    synchronized (directory()) {		  // in- & inter-process sync
      new Lock.With(directory().makeLock(IndexWriter.COMMIT_LOCK_NAME),
                    IndexWriter.COMMIT_LOCK_TIMEOUT) {
        public Object doBody() throws IOException {
          if (directory().fileExists(segment + ".del")) {
            directory().deleteFile(segment + ".del");
          }
          return null;
        }
      };
      deletedDocs = null;
      deletedDocsDirty = false;
    }
  }


  final Vector files() throws IOException {
    Vector files = new Vector(16);
    final String ext[] = new String[] {
      "cfs", "fnm", "fdx", "fdt", "tii", "tis", "frq", "prx", "del"
    };

    for (int i=0; i<ext.length; i++) {
      String name = segment + "." + ext[i];
      if (directory().fileExists(name))
        files.addElement(name);
    }

    for (int i = 0; i < fieldInfos.size(); i++) {
      FieldInfo fi = fieldInfos.fieldInfo(i);
      if (fi.isIndexed)
        files.addElement(segment + ".f" + i);
    }
    return files;
  }

  public final TermEnum terms() throws IOException {
    return tis.terms();
  }

  public final TermEnum terms(Term t) throws IOException {
    return tis.terms(t);
  }

  public final synchronized Document document(int n) throws IOException {
    if (isDeleted(n))
      throw new IllegalArgumentException
        ("attempt to access a deleted document");
    return fieldsReader.doc(n);
  }

  public final synchronized boolean isDeleted(int n) {
    return (deletedDocs != null && deletedDocs.get(n));
  }

  public final TermDocs termDocs() throws IOException {
    return new SegmentTermDocs(this);
  }

  public final TermPositions termPositions() throws IOException {
    return new SegmentTermPositions(this);
  }

  public final int docFreq(Term t) throws IOException {
    TermInfo ti = tis.get(t);
    if (ti != null)
      return ti.docFreq;
    else
      return 0;
  }

  public final int numDocs() {
    int n = maxDoc();
    if (deletedDocs != null)
      n -= deletedDocs.count();
    return n;
  }

  public final int maxDoc() {
    return fieldsReader.size();
  }

  /**
   * @see IndexReader#getFieldNames()
   */
  public Collection getFieldNames() throws IOException {
    // maintain a unique set of field names
    Set fieldSet = new HashSet();
    for (int i = 0; i < fieldInfos.size(); i++) {
      FieldInfo fi = fieldInfos.fieldInfo(i);
      fieldSet.add(fi.name);
    }
    return fieldSet;
  }

  /**
   * @see IndexReader#getFieldNames(boolean)
   */
  public Collection getFieldNames(boolean indexed) throws IOException {
    // maintain a unique set of field names
    Set fieldSet = new HashSet();
    for (int i = 0; i < fieldInfos.size(); i++) {
      FieldInfo fi = fieldInfos.fieldInfo(i);
      if (fi.isIndexed == indexed)
        fieldSet.add(fi.name);
      }
      return fieldSet;
    }

  public synchronized byte[] norms(String field) throws IOException {
    Norm norm = (Norm)norms.get(field);
    if (norm == null)                             // not an indexed field
      return null;
    if (norm.bytes == null) {                     // value not yet read
      byte[] bytes = new byte[maxDoc()];
      norms(field, bytes, 0);
      norm.bytes = bytes;                         // cache it
    }
    return norm.bytes;
  }

  public synchronized void setNorm(int doc, String field, byte value)
    throws IOException {
    Norm norm = (Norm)norms.get(field);
    if (norm == null)                             // not an indexed field
      return;
    norm.dirty = true;                            // mark it dirty
    normsDirty = true;

    norms(field)[doc] = value;                    // set the value
  }

  /** Read norms into a pre-allocated array. */
  synchronized void norms(String field, byte[] bytes, int offset)
    throws IOException {

    Norm norm = (Norm)norms.get(field);
    if (norm == null)
      return;					  // use zeros in array

    if (norm.bytes != null) {                     // can copy from cache
      System.arraycopy(norm.bytes, 0, bytes, offset, maxDoc());
      return;
    }

    InputStream normStream = (InputStream)norm.in.clone();
    try {                                         // read from disk
      normStream.seek(0);
      normStream.readBytes(bytes, offset, maxDoc());
    } finally {
      normStream.close();
    }
  }

  private final void openNorms(Directory cfsDir) throws IOException {
    for (int i = 0; i < fieldInfos.size(); i++) {
      FieldInfo fi = fieldInfos.fieldInfo(i);
      if (fi.isIndexed) {
        String fileName = segment + ".f" + fi.number;
        // look first for re-written file, then in compound format
        Directory d = directory().fileExists(fileName) ? directory() : cfsDir;
        norms.put(fi.name, new Norm(d.openFile(fileName)));
      }
    }
  }

  private final void closeNorms() throws IOException {
    synchronized (norms) {
      Enumeration enumerator  = norms.elements();
      while (enumerator.hasMoreElements()) {
        Norm norm = (Norm)enumerator.nextElement();
        norm.in.close();
      }
    }
  }
}

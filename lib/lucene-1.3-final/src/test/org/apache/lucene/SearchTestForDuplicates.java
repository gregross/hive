package org.apache.lucene;

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

import org.apache.lucene.store.*;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;

class SearchTestForDuplicates {

  static final String PRIORITY_FIELD ="priority";
  static final String ID_FIELD ="id";
  static final String HIGH_PRIORITY ="high";
  static final String MED_PRIORITY ="medium";
  static final String LOW_PRIORITY ="low";

  public static void main(String[] args) {
    try {
      Directory directory = new RAMDirectory();
      Analyzer analyzer = new SimpleAnalyzer();
      IndexWriter writer = new IndexWriter(directory, analyzer, true);

      final int MAX_DOCS = 225;

      for (int j = 0; j < MAX_DOCS; j++) {
        Document d = new Document();
        d.add(Field.Text(PRIORITY_FIELD, HIGH_PRIORITY));
        d.add(Field.Text(ID_FIELD, Integer.toString(j)));
        writer.addDocument(d);
      }
      writer.close();

      // try a search without OR
      Searcher searcher = new IndexSearcher(directory);
      Hits hits = null;

      QueryParser parser = new QueryParser(PRIORITY_FIELD, analyzer);

      Query query = parser.parse(HIGH_PRIORITY);
      System.out.println("Query: " + query.toString(PRIORITY_FIELD));

      hits = searcher.search(query);
      printHits(hits);

      searcher.close();

      // try a new search with OR
      searcher = new IndexSearcher(directory);
      hits = null;

      parser = new QueryParser(PRIORITY_FIELD, analyzer);

      query = parser.parse(HIGH_PRIORITY + " OR " + MED_PRIORITY);
      System.out.println("Query: " + query.toString(PRIORITY_FIELD));

      hits = searcher.search(query);
      printHits(hits);

      searcher.close();

    } catch (Exception e) {
      System.out.println(" caught a " + e.getClass() +
                         "\n with message: " + e.getMessage());
    }
  }

  private static void printHits( Hits hits ) throws IOException {
    System.out.println(hits.length() + " total results\n");
    for (int i = 0 ; i < hits.length(); i++) {
      if ( i < 10 || (i > 94 && i < 105) ) {
        Document d = hits.doc(i);
        System.out.println(i + " " + d.get(ID_FIELD));
      }
    }
  }

}

package org.apache.lucene.search;

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
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultipleTermPositions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.search.Query;

/**
 * PhrasePrefixQuery is a generalized version of PhraseQuery, with an added
 * method {@link #add(Term[])}.
 * To use this class, to search for the phrase "Microsoft app*" first use
 * add(Term) on the term "Microsoft", then find all terms that has "app" as
 * prefix using IndexReader.terms(Term), and use PhrasePrefixQuery.add(Term[]
 * terms) to add them to the query.
 *
 * @author Anders Nielsen
 * @version 1.0
 */
public class PhrasePrefixQuery extends Query {
  private String field;
  private ArrayList termArrays = new ArrayList();

  private int slop = 0;

  /* Sets the phrase slop for this query.
   * @see PhraseQuery#setSlop(int)
   */
  public void setSlop(int s) { slop = s; }

  /* Sets the phrase slop for this query.
   * @see PhraseQuery#getSlop()
   */
  public int getSlop() { return slop; }

  /* Add a single term at the next position in the phrase.
   * @see PhraseQuery#add(Term)
   */
  public void add(Term term) { add(new Term[]{term}); }

  /* Add multiple terms at the next position in the phrase.  Any of the terms
   * may match.
   *
   * @see PhraseQuery#add(Term)
   */
  public void add(Term[] terms) {
    if (termArrays.size() == 0)
      field = terms[0].field();
    
    for (int i=0; i<terms.length; i++) {
      if (terms[i].field() != field) {
        throw new IllegalArgumentException
          ("All phrase terms must be in the same field (" + field + "): "
           + terms[i]);
      }
    }

    termArrays.add(terms);
  }

  private class PhrasePrefixWeight implements Weight {
    private Searcher searcher;
    private float value;
    private float idf;
    private float queryNorm;
    private float queryWeight;

    public PhrasePrefixWeight(Searcher searcher) {
      this.searcher = searcher;
    }

    public Query getQuery() { return PhrasePrefixQuery.this; }
    public float getValue() { return value; }

    public float sumOfSquaredWeights() throws IOException {
      Iterator i = termArrays.iterator();
      while (i.hasNext()) {
        Term[] terms = (Term[])i.next();
        for (int j=0; j<terms.length; j++)
          idf += searcher.getSimilarity().idf(terms[j], searcher);
      }

      queryWeight = idf * getBoost();             // compute query weight
      return queryWeight * queryWeight;           // square it
    }

    public void normalize(float queryNorm) {
      this.queryNorm = queryNorm;
      queryWeight *= queryNorm;                   // normalize query weight
      value = queryWeight * idf;                  // idf for document 
    }

    public Scorer scorer(IndexReader reader) throws IOException {
      if (termArrays.size() == 0)                  // optimize zero-term case
        return null;
    
      TermPositions[] tps = new TermPositions[termArrays.size()];
      for (int i=0; i<tps.length; i++) {
        Term[] terms = (Term[])termArrays.get(i);
      
        TermPositions p;
        if (terms.length > 1)
          p = new MultipleTermPositions(reader, terms);
        else
          p = reader.termPositions(terms[0]);
      
        if (p == null)
          return null;
      
        tps[i] = p;
      }
    
      if (slop == 0)
        return new ExactPhraseScorer(this, tps, searcher.getSimilarity(),
                                     reader.norms(field));
      else
        return new SloppyPhraseScorer(this, tps, searcher.getSimilarity(),
                                      slop, reader.norms(field));
    }
    
    public Explanation explain(IndexReader reader, int doc)
      throws IOException {
      Explanation result = new Explanation();
      result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");

      Explanation idfExpl = new Explanation(idf, "idf("+getQuery()+")");
      
      // explain query weight
      Explanation queryExpl = new Explanation();
      queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");

      Explanation boostExpl = new Explanation(getBoost(), "boost");
      if (getBoost() != 1.0f)
        queryExpl.addDetail(boostExpl);

      queryExpl.addDetail(idfExpl);
      
      Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
      queryExpl.addDetail(queryNormExpl);
      
      queryExpl.setValue(boostExpl.getValue() *
                         idfExpl.getValue() *
                         queryNormExpl.getValue());

      result.addDetail(queryExpl);
     
      // explain field weight
      Explanation fieldExpl = new Explanation();
      fieldExpl.setDescription("fieldWeight("+getQuery()+" in "+doc+
                               "), product of:");

      Explanation tfExpl = scorer(reader).explain(doc);
      fieldExpl.addDetail(tfExpl);
      fieldExpl.addDetail(idfExpl);

      Explanation fieldNormExpl = new Explanation();
      byte[] fieldNorms = reader.norms(field);
      float fieldNorm =
        fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
      fieldNormExpl.setValue(fieldNorm);
      fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
      fieldExpl.addDetail(fieldNormExpl);

      fieldExpl.setValue(tfExpl.getValue() *
                         idfExpl.getValue() *
                         fieldNormExpl.getValue());
      
      result.addDetail(fieldExpl);

      // combine them
      result.setValue(queryExpl.getValue() * fieldExpl.getValue());

      if (queryExpl.getValue() == 1.0f)
        return fieldExpl;

      return result;
    }
  }

  protected Weight createWeight(Searcher searcher) {
    if (termArrays.size() == 1) {                 // optimize one-term case
      Term[] terms = (Term[])termArrays.get(0);
      BooleanQuery boq = new BooleanQuery();
      for (int i=0; i<terms.length; i++) {
        boq.add(new TermQuery(terms[i]), false, false);
      }
      boq.setBoost(getBoost());
      return boq.createWeight(searcher);
    }
    return new PhrasePrefixWeight(searcher);
  }

  /** Prints a user-readable version of this query. */
  public final String toString(String f) {
    StringBuffer buffer = new StringBuffer();
    if (!field.equals(f)) {
      buffer.append(field);
      buffer.append(":");
    }

    buffer.append("\"");
    Iterator i = termArrays.iterator();
    while (i.hasNext()) {
      Term[] terms = (Term[])i.next();
      buffer.append(terms[0].text() + (terms.length > 0 ? "*" : ""));
    }
    buffer.append("\"");

    if (slop != 0) {
      buffer.append("~");
      buffer.append(slop);
    }

    if (getBoost() != 1.0f) {
      buffer.append("^");
      buffer.append(Float.toString(getBoost()));
    }

    return buffer.toString();
  }
}

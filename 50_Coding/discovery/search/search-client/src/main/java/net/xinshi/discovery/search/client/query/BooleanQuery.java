package net.xinshi.discovery.search.client.query;


import net.xinshi.discovery.search.client.util.ToStringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BooleanQuery extends Query implements Iterable<BooleanClause> {

  private static int maxClauseCount = 1024;

  /** Thrown when an attempt is made to add more than {@link
   * #getMaxClauseCount()} clauses. This typically happens if
   * a PrefixQuery, FuzzyQuery, WildcardQuery, or TermRangeQuery
   * is expanded to many terms during search.
   */
  public static class TooManyClauses extends RuntimeException {
    public TooManyClauses() {
      super("maxClauseCount is set to " + maxClauseCount);
    }
  }

  /** Return the maximum number of clauses permitted, 1024 by default.
   * Attempts to add more than the permitted number of clauses cause {@link
   * TooManyClauses} to be thrown.
   * @see #setMaxClauseCount(int)
   */
  public static int getMaxClauseCount() { return maxClauseCount; }

  /**
   * Set the maximum number of clauses permitted per BooleanQuery.
   * Default value is 1024.
   */
  public static void setMaxClauseCount(int maxClauseCount) {
    if (maxClauseCount < 1)
      throw new IllegalArgumentException("maxClauseCount must be >= 1");
    BooleanQuery.maxClauseCount = maxClauseCount;
  }

  private ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
  private final boolean disableCoord;

  /** Constructs an empty boolean query. */
  public BooleanQuery() {
    disableCoord = false;
  }


  /**
   * Specifies a minimum number of the optional BooleanClauses
   * which must be satisfied.
   *
   * <p>
   * By default no optional clauses are necessary for a match
   * (unless there are no required clauses).  If this method is used,
   * then the specified number of clauses is required.
   * </p>
   * <p>
   * Use of this method is totally independent of specifying that
   * any specific clauses are required (or prohibited).  This number will
   * only be compared against the number of matching optional clauses.
   * </p>
   *
   * @param min the number of optional clauses that must match
   */
  public void setMinimumNumberShouldMatch(int min) {
    this.minNrShouldMatch = min;
  }
  protected int minNrShouldMatch = 0;

  /**
   * Gets the minimum number of the optional BooleanClauses
   * which must be satisfied.
   */
  public int getMinimumNumberShouldMatch() {
    return minNrShouldMatch;
  }

  /** Adds a clause to a boolean query.
   *
   * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number
   * @see #getMaxClauseCount()
   */
  public void add(Query query, BooleanClause.Occur occur) {
    add(new BooleanClause(query, occur));
  }

  /** Adds a clause to a boolean query.
   * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number
   * @see #getMaxClauseCount()
   */
  public void add(BooleanClause clause) {
    if (clauses.size() >= maxClauseCount)
      throw new TooManyClauses();

    clauses.add(clause);
  }

  /** Returns the set of clauses in this query. */
  public BooleanClause[] getClauses() {
    return clauses.toArray(new BooleanClause[clauses.size()]);
  }

  /** Returns the list of clauses in this query. */
  public List<BooleanClause> clauses() { return clauses; }

  /** Returns an iterator on the clauses in this query. It implements the {@link Iterable} interface to
   * make it possible to do:
   * <pre>for (BooleanClause clause : booleanQuery) {}</pre>
   */
  public final Iterator<BooleanClause> iterator() { return clauses().iterator(); }


    public Query getQuery() { return BooleanQuery.this; }


  @Override @SuppressWarnings("unchecked")
  public BooleanQuery clone() {
    BooleanQuery clone = (BooleanQuery)super.clone();
    clone.clauses = (ArrayList<BooleanClause>) this.clauses.clone();
    return clone;
  }

  /** Prints a user-readable version of this query. */
  @Override
  public String toString(String field) {
    StringBuilder buffer = new StringBuilder();
    boolean needParens=(getBoost() != 1.0) || (getMinimumNumberShouldMatch()>0) ;
    if (needParens) {
      buffer.append("(");
    }

    for (int i = 0 ; i < clauses.size(); i++) {
      BooleanClause c = clauses.get(i);
      if (c.isProhibited())
        buffer.append("-");
      else if (c.isRequired())
        buffer.append("+");

      Query subQuery = c.getQuery();
      if (subQuery != null) {
        if (subQuery instanceof BooleanQuery) {	  // wrap sub-bools in parens
          buffer.append("(");
          buffer.append(subQuery.toString(field));
          buffer.append(")");
        } else {
          buffer.append(subQuery.toString(field));
        }
      } else {
        buffer.append("null");
      }

      if (i != clauses.size()-1)
        buffer.append(" ");
    }

    if (needParens) {
      buffer.append(")");
    }

    if (getMinimumNumberShouldMatch()>0) {
      buffer.append('~');
      buffer.append(getMinimumNumberShouldMatch());
    }

    if (getBoost() != 1.0f)
    {
      buffer.append(ToStringUtils.boost(getBoost()));
    }

    return buffer.toString();
  }

  /** Returns true iff <code>o</code> is equal to this. */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BooleanQuery))
      return false;
    BooleanQuery other = (BooleanQuery)o;
    return (this.getBoost() == other.getBoost())
        && this.clauses.equals(other.clauses)
        && this.getMinimumNumberShouldMatch() == other.getMinimumNumberShouldMatch()
        && this.disableCoord == other.disableCoord;
  }

  /** Returns a hash code value for this object.*/
  @Override
  public int hashCode() {
    return Float.floatToIntBits(getBoost()) ^ clauses.hashCode()
      + getMinimumNumberShouldMatch() + (disableCoord ? 17:0);
  }
  
}

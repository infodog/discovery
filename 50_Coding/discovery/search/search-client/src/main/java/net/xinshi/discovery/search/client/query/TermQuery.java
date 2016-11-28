package net.xinshi.discovery.search.client.query;


import net.xinshi.discovery.search.client.util.ToStringUtils;

public class TermQuery extends Query {
    private final Term term;

    @Override
    public String toString() {
        return this.toString("");
    }


    public Query getQuery() {
        return TermQuery.this;
    }


    /**
     * Constructs a query for the term <code>t</code>.
     */
    public TermQuery(Term t) {
        term = t;
    }


    /**
     * Returns the term of this query.
     */
    public Term getTerm() {
        return term;
    }


    /**
     * Prints a user-readable version of this query.
     */
    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!term.field().equals(field)) {
            buffer.append(term.field());
            buffer.append(":");
        }
        buffer.append("\"");
        buffer.append(term.text());
        buffer.append("\"");
        buffer.append(ToStringUtils.boost(getBoost()));
        return buffer.toString();
    }

    /**
     * Returns true iff <code>o</code> is equal to this.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TermQuery))
            return false;
        TermQuery other = (TermQuery) o;
        return (this.getBoost() == other.getBoost())
                && this.term.equals(other.term);
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(getBoost()) ^ term.hashCode();
    }

}

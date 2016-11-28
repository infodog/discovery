package net.xinshi.discovery.search.client.query;


import net.xinshi.discovery.search.client.util.ToStringUtils;

/**
 * A Query that matches documents within an range of terms.
**/


public class TermRangeQuery extends Query {
    private String field;
    private String lowerTerm;
    private String upperTerm;
    private boolean includeLower;
    private boolean includeUpper;

    public TermRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper) {
        this.field = field;
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }


    /**
     * Returns the lower value of this range query
     */
    public String getLowerTerm() {
        return lowerTerm;
    }

    /**
     * Returns the upper value of this range query
     */
    public String getUpperTerm() {
        return upperTerm;
    }

    /**
     * Returns <code>true</code> if the lower endpoint is inclusive
     */
    public boolean includesLower() {
        return includeLower;
    }

    /**
     * Returns <code>true</code> if the upper endpoint is inclusive
     */
    public boolean includesUpper() {
        return includeUpper;
    }

    /**
     * Returns the field name for this query
     */
    public final String getField() {
        return field;
    }


    /**
     * Prints a user-readable version of this query.
     */
    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!getField().equals(field)) {
            buffer.append(getField());
            buffer.append(":");
        }

        buffer.append(includeLower ? '[' : '{');
        // TODO: all these toStrings for queries should just output the bytes, it might not be UTF-8!

        buffer.append(lowerTerm == null || "*".equals(lowerTerm) ? "" : "\"");
        buffer.append(lowerTerm != null ? ("*".equals(lowerTerm) ? "\\*" :  lowerTerm) : "*");
        buffer.append(lowerTerm == null || "*".equals(lowerTerm) ? "" : "\"");
        buffer.append(" TO ");
        buffer.append(upperTerm == null || "*".equals(upperTerm) ? "" : "\"");
        buffer.append(upperTerm != null ? ("*".equals(upperTerm) ? "\\*" : upperTerm) : "*");
        buffer.append(upperTerm == null || "*".equals(upperTerm) ? "" : "\"");
        buffer.append(includeUpper ? ']' : '}');
        buffer.append(ToStringUtils.boost(getBoost()));
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (includeLower ? 1231 : 1237);
        result = prime * result + (includeUpper ? 1231 : 1237);
        result = prime * result + ((lowerTerm == null) ? 0 : lowerTerm.hashCode());
        result = prime * result + ((upperTerm == null) ? 0 : upperTerm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermRangeQuery other = (TermRangeQuery) obj;
        if (includeLower != other.includeLower)
            return false;
        if (includeUpper != other.includeUpper)
            return false;
        if (lowerTerm == null) {
            if (other.lowerTerm != null)
                return false;
        } else if (!lowerTerm.equals(other.lowerTerm))
            return false;
        if (upperTerm == null) {
            if (other.upperTerm != null)
                return false;
        } else if (!upperTerm.equals(other.upperTerm))
            return false;
        return true;
    }

}

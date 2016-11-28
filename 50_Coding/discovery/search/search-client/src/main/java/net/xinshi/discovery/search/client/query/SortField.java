package net.xinshi.discovery.search.client.query;

public class SortField {

    /**
     * Specifies the type of the terms to be sorted, or special types such as CUSTOM
     */
    public static enum Type {

        /**
         * Sort by document score (relevance).  Sort values are Float and higher
         * values are at the front.
         */
        SCORE,

        /**
         * Sort by document number (index order).  Sort values are Integer and lower
         * values are at the front.
         */
        DOC,

        /**
         * Sort using term values as Strings.  Sort values are String and lower
         * values are at the front.
         */
        STRING,

        /**
         * Sort using term values as encoded Integers.  Sort values are Integer and
         * lower values are at the front.
         */
        INT,

        /**
         * Sort using term values as encoded Floats.  Sort values are Float and
         * lower values are at the front.
         */
        FLOAT,

        /**
         * Sort using term values as encoded Longs.  Sort values are Long and
         * lower values are at the front.
         */
        LONG,

        /**
         * Sort using term values as encoded Doubles.  Sort values are Double and
         * lower values are at the front.
         */
        DOUBLE,

        /**
         * Sort using term values as encoded Shorts.  Sort values are Short and
         * lower values are at the front.
         */
        SHORT,

        /**
         * Sort using a custom Comparator.  Sort values are any Comparable and
         * sorting is done according to natural order.
         */
        CUSTOM,

        /**
         * Sort using term values as encoded Bytes.  Sort values are Byte and
         * lower values are at the front.
         */
        BYTE,

        /**
         * Sort using term values as Strings, but comparing by
         * value (using String.compareTo) for all comparisons.
         * This is typically slower than {@link #STRING}, which
         * uses ordinals to do the sorting.
         */
        STRING_VAL,

        /**
         * Sort use byte[] index values.
         */
        BYTES,

        /**
         * Force rewriting of SortField using
         * before it can be used for sorting
         */
        REWRITEABLE
    }

    /**
     * Represents sorting by document score (relevance).
     */
    public static final SortField FIELD_SCORE = new SortField(null, Type.SCORE);

    /**
     * Represents sorting by document number (index order).
     */
    public static final SortField FIELD_DOC = new SortField(null, Type.DOC);

    private String field;
    private Type type;  // defaults to determining type dynamically
    boolean reverse = false;  // defaults to natural order

    // Used for 'sortMissingFirst/Last'
    public Object missingValue = null;

    public SortField() {

    }

    /**
     * Creates a sort by terms in the given field with the type of term
     * values explicitly given.
     *
     * @param field Name of field to sort by.  Can be <code>null</code> if
     *              <code>type</code> is SCORE or DOC.
     * @param type  Type of values in the terms.
     */
    public SortField(String field, Type type) {
        initFieldType(field, type);
    }

    /**
     * Creates a sort, possibly in reverse, by terms in the given field with the
     * type of term values explicitly given.
     *
     * @param field   Name of field to sort by.  Can be <code>null</code> if
     *                <code>type</code> is SCORE or DOC.
     * @param type    Type of values in the terms.
     * @param reverse True if natural order should be reversed.
     */
    public SortField(String field, Type type, boolean reverse) {
        initFieldType(field, type);
        this.reverse = reverse;
    }


    public SortField setMissingValue(Object missingValue) {
        if (type != Type.BYTE && type != Type.SHORT && type != Type.INT && type != Type.FLOAT && type != Type.LONG && type != Type.DOUBLE) {
            throw new IllegalArgumentException("Missing value only works for numeric types");
        }
        this.missingValue = missingValue;
        return this;
    }


    // Sets field & type, and ensures field is not NULL unless
    // type is SCORE or DOC
    private void initFieldType(String field, Type type) {
        this.type = type;
        if (field == null) {
            if (type != Type.SCORE && type != Type.DOC) {
                throw new IllegalArgumentException("field can only be null when type is SCORE or DOC");
            }
        } else {
            this.field = field;
        }
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isUseIndexValues() {
        return useIndexValues;
    }

    /**
     * Returns the name of the field.  Could return <code>null</code>
     * if the sort is by SCORE or DOC.
     *
     * @return Name of field, possibly <code>null</code>.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the type of contents in the field.
     *
     * @return One of the constants SCORE, DOC, STRING, INT or FLOAT.
     */
    public Type getType() {
        return type;
    }


    /**
     * Returns whether the sort should be reversed.
     *
     * @return True if natural order should be reversed.
     */
    public boolean getReverse() {
        return reverse;
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        String dv = useIndexValues ? " [dv]" : "";
        switch (type) {
            case SCORE:
                buffer.append("<score>");
                break;

            case DOC:
                buffer.append("<doc>");
                break;

            case STRING:
                buffer.append("<string" + dv + ": \"").append(field).append("\">");
                break;

            case STRING_VAL:
                buffer.append("<string_val" + dv + ": \"").append(field).append("\">");
                break;

            case BYTE:
                buffer.append("<byte: \"").append(field).append("\">");
                break;

            case SHORT:
                buffer.append("<short: \"").append(field).append("\">");
                break;

            case INT:
                buffer.append("<int" + dv + ": \"").append(field).append("\">");
                break;

            case LONG:
                buffer.append("<long: \"").append(field).append("\">");
                break;

            case FLOAT:
                buffer.append("<float" + dv + ": \"").append(field).append("\">");
                break;

            case DOUBLE:
                buffer.append("<double" + dv + ": \"").append(field).append("\">");
                break;

            case REWRITEABLE:
                buffer.append("<rewriteable: \"").append(field).append("\">");
                break;

            default:
                buffer.append("<???: \"").append(field).append("\">");
                break;
        }

        if (reverse) buffer.append('!');

        return buffer.toString();
    }


    public int hashCode() {
        int hash = type.hashCode() ^ 0x346565dd + Boolean.valueOf(reverse).hashCode() ^ 0xaf5998bb;
        if (field != null) hash += field.hashCode() ^ 0xff5685dd;
        return hash;
    }

    private boolean useIndexValues;

    public void setUseIndexValues(boolean b) {
        useIndexValues = b;
    }

    public boolean getUseIndexValues() {
        return useIndexValues;
    }
}

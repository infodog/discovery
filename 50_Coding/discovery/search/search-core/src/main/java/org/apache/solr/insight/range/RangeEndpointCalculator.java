package org.apache.solr.insight.range;

import org.apache.solr.common.SolrException;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.util.DateMathParser;

import java.text.NumberFormat;
import java.util.Date;

/**
 * Perhaps someday instead of having a giant "instanceof" case
 * statement to pick an impl, we can add a "RangeFacetable" marker
 * interface to FieldTypes and they can return instances of these
 * directly from some method -- but until then, keep this locked down
 * and private.
 */
public abstract class RangeEndpointCalculator<T extends Comparable<T>> {
    protected final SchemaField field;

    public RangeEndpointCalculator(final SchemaField field) {
        this.field = field;
    }

    /**
     * Formats a Range endpoint for use as a range label name in the response.
     * Default Impl just uses toString()
     */
    public String formatValue(final T val) {
        return val.toString();
    }

    /**
     * Parses a String param into an Range endpoint value throwing
     * a useful exception if not possible
     */
    public final T getValue(final String rawval) {
        try {
            return parseVal(rawval);
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                    "Can't parse value " + rawval + " for field: "
                    , e);
        }
    }

    /**
     * Parses a String param into an Range endpoint.
     * Can throw a low level format exception as needed.
     */
    protected abstract T parseVal(final String rawval)
            throws java.text.ParseException;

    /**
     * Parses a String param into a value that represents the gap and
     * can be included in the response, throwing
     * a useful exception if not possible.
     * <p/>
     * Note: uses Object as the return type instead of T for things like
     * Date where gap is just a DateMathParser string
     */
    public final Object getGap(final String gap) {
        try {
            return parseGap(gap);
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                    "Can't parse gap " + gap + " for field: "
                    , e);
        }
    }

    /**
     * Parses a String param into a value that represents the gap and
     * can be included in the response.
     * Can throw a low level format exception as needed.
     * <p/>
     * Default Impl calls parseVal
     */
    protected Object parseGap(final String rawval)
            throws java.text.ParseException {
        return parseVal(rawval);
    }

    /**
     * Adds the String gap param to a low Range endpoint value to determine
     * the corrisponding high Range endpoint value, throwing
     * a useful exception if not possible.
     */
    public final T addGap(T value, String gap) {
        try {
            return parseAndAddGap(value, gap);
        } catch (Exception e) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                    "Can't add gap " + gap + " to value " + value +
                            " for field: ", e);
        }
    }

    /**
     * Adds the String gap param to a low Range endpoint value to determine
     * the corrisponding high Range endpoint value.
     * Can throw a low level format exception as needed.
     */
    protected abstract T parseAndAddGap(T value, String gap)
            throws java.text.ParseException;

}


class DateRangeEndpointCalculator
        extends RangeEndpointCalculator<Date> {
    private final Date now;
    private DateField datetype;

    public DateRangeEndpointCalculator(final SchemaField f,
                                       final Date now) {
        super(f);
        this.now = now;
        this.datetype = new DateField();
    }

    @Override
    public String formatValue(Date val) {
        return this.datetype.toExternal(val);
    }

    @Override
    protected Date parseVal(String rawval) {
        return this.datetype.parseMath(now, rawval);
    }

    @Override
    protected Object parseGap(final String rawval) {
        return rawval;
    }

    @Override
    public Date parseAndAddGap(Date value, String gap) throws java.text.ParseException {
        final DateMathParser dmp = new DateMathParser();
        dmp.setNow(value);
        return dmp.parseMath(gap);
    }
}


class LongRangeEndpointCalculator
        extends RangeEndpointCalculator<Long> {

    public LongRangeEndpointCalculator(final SchemaField f) {
        super(f);
    }

    @Override
    protected Long parseVal(String rawval) {
        return Long.valueOf(rawval);
    }

    @Override
    public Long parseAndAddGap(Long value, String gap) {
        return new Long(value.longValue() + Long.valueOf(gap).longValue());
    }
}


class FloatRangeEndpointCalculator
        extends RangeEndpointCalculator<Float> {

    public FloatRangeEndpointCalculator(final SchemaField f) {
        super(f);
    }

    @Override
    protected Float parseVal(String rawval) {
        return Float.valueOf(rawval);
    }

    @Override
    public Float parseAndAddGap(Float value, String gap) {
        return new Float(value.floatValue() + Float.valueOf(gap).floatValue());
    }
}

class DoubleRangeEndpointCalculator
        extends RangeEndpointCalculator<Double> {

    public DoubleRangeEndpointCalculator(final SchemaField f) {
        super(f);
    }

    @Override
    protected Double parseVal(String rawval) {
        return Double.valueOf(rawval);
    }

    @Override
    public Double parseAndAddGap(Double value, String gap) {
        return new Double(value.doubleValue() + Double.valueOf(gap).doubleValue());
    }
}

class IntegerRangeEndpointCalculator
        extends RangeEndpointCalculator<Integer> {

    public IntegerRangeEndpointCalculator(final SchemaField f) {
        super(f);
    }

    @Override
    protected Integer parseVal(String rawval) {
        return Integer.valueOf(rawval);
    }

    @Override
    public Integer parseAndAddGap(Integer value, String gap) {
        return new Integer(value.intValue() + Integer.valueOf(gap).intValue());
    }
}
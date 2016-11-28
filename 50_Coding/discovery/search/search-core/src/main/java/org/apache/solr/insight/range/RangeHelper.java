package org.apache.solr.insight.range;

import org.apache.solr.common.SolrException;
import org.apache.solr.schema.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 8/21/13
 * Time: 3:50 PM
 */
public class RangeHelper {
    private static RangeHelper instance;

    private RangeHelper() {
    }

    public synchronized static RangeHelper getInstance() {
        if (instance == null) {
            instance = new RangeHelper();
        }
        return instance;
    }

    public List<RangeItem> getDateRange(String begin, String last, String gap) {

        RangeEndpointCalculator<?> calc = new DateRangeEndpointCalculator(null, null);


        return this.calcRange(calc, begin, last, gap,false);
    }

    public List<RangeItem> getRange(SchemaField sf,String begin, String last, String gap) {
        final FieldType ft = sf.getType();
        RangeEndpointCalculator<?> calc = null;
        boolean labelRange = true;

        if (ft instanceof TrieField) {
            final TrieField trie = (TrieField)ft;

            switch (trie.getType()) {
                case FLOAT:
                    calc = new FloatRangeEndpointCalculator(null);
                    break;
                case DOUBLE:
                    calc = new DoubleRangeEndpointCalculator(null);
                    break;
                case INTEGER:
                    calc = new IntegerRangeEndpointCalculator(null);
                    break;
                case LONG:
                    calc = new LongRangeEndpointCalculator(null);
                    break;
                default:
                    throw new SolrException
                            (SolrException.ErrorCode.BAD_REQUEST,
                                    "Unable to range facet on tried field of unexpected type:" + ft.toString());
            }
        } else if (ft instanceof DateField) {
            calc = new DateRangeEndpointCalculator(null, null);
            labelRange = false;
        } else if (ft instanceof SortableIntField) {
            calc = new IntegerRangeEndpointCalculator(null);
        } else if (ft instanceof SortableLongField) {
            calc = new LongRangeEndpointCalculator(null);
        } else if (ft instanceof SortableFloatField) {
            calc = new FloatRangeEndpointCalculator(null);
        } else if (ft instanceof SortableDoubleField) {
            calc = new DoubleRangeEndpointCalculator(null);
        } else {
            throw new SolrException
                    (SolrException.ErrorCode.BAD_REQUEST,
                            "Unable to range facet on field:" + null);
        }
        return this.calcRange(calc,begin,last,gap,labelRange);
    }

    private <T extends Comparable<T>> List<RangeItem> calcRange(RangeEndpointCalculator<T> calc, String begin, String last, String gap, boolean labelRange) {
        List<RangeItem> items = new ArrayList<RangeItem>();

        final T start = calc.getValue(begin);
        // not final, hardend may change this
        T end = calc.getValue(last);

        if (end.compareTo(start) < 0) {
            throw new SolrException
                    (SolrException.ErrorCode.BAD_REQUEST,
                            "range facet 'end' comes before 'start': " + end + " < " + start);
        }
        T low = start;
        while (low.compareTo(end) < 0) {
            T high = calc.addGap(low, gap);
            if (high.compareTo(low) < 0) {
                throw new SolrException
                        (SolrException.ErrorCode.BAD_REQUEST,
                                "range facet infinite loop (is gap negative? did the math overflow?)");
            }
            if (high.compareTo(low) == 0) {
                throw new SolrException
                        (SolrException.ErrorCode.BAD_REQUEST,
                                "range facet infinite loop: gap is either zero, or too small relative start/end and caused underflow: " + low + " + " + gap + " = " + high);
            }

            String lowS = calc.formatValue(low);
            String highS = calc.formatValue(high);

            RangeItem item = new RangeItem();

            item.setStart(lowS);
            item.setEnd(highS);

            if (labelRange) {
                item.setLabel(lowS + "-" + highS);
            } else {
                item.setLabel(lowS.replace("00Z","00.00+01:00"));
            }
            items.add(item);
            low = high;
        }

        return items;
    }
}

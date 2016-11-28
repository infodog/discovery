package org.apache.solr.handler.component;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.schema.*;
import org.apache.solr.util.MapUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating instance of {@link StatsValues}
 */
@Deprecated
public class DiscoveryStatsValuesFactory {

    /**
     * Creates an instance of StatsValues which supports values from a field of the given FieldType
     *
     * @param sf SchemaField for the field whose statistics will be created by the resulting StatsValues
     * @return Instance of StatsValues that will create statistics from values from a field of the given type
     */
    public static StatsValues createStatsValues(SchemaField sf, String func, int offset, int limit) {
        FieldType fieldType = sf.getType();
        if (DoubleField.class.isInstance(fieldType) ||
                IntField.class.isInstance(fieldType) ||
                LongField.class.isInstance(fieldType) ||
                ShortField.class.isInstance(fieldType) ||
                FloatField.class.isInstance(fieldType) ||
                ByteField.class.isInstance(fieldType) ||
                TrieField.class.isInstance(fieldType) ||
                SortableDoubleField.class.isInstance(fieldType) ||
                SortableIntField.class.isInstance(fieldType) ||
                SortableLongField.class.isInstance(fieldType) ||
                SortableFloatField.class.isInstance(fieldType)) {

            if (func != null && "count".equals(func)) {
                return new CountStatsValues(sf, func, offset, limit);
            } else if (func != null && "dcount".equals(func.trim())) {
                return new DistinctCountStatsValues(sf, func, offset, limit);
            } else if (func != null && "sum".equals(func)) {
                return new SumStatsValues(sf, func, offset, limit);
            } else if (func != null && "avg".equals(func)) {
                return new AverageStatsValues(sf, func, offset, limit);
            } else {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Field type " + fieldType + " is not currently supported -- function: " + func);
            }
        } else if (StrField.class.isInstance(fieldType)) {
            if (func != null && "count".equals(func)) {
                return new CountStatsValues(sf, func, offset, limit);
            } else if (func != null && "dcount".equals(func.trim())) {
                return new DistinctCountStatsValues(sf, func, offset, limit);
            } else {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Field type " + fieldType + " is not currently supported -- function: " + func);
            }
        } else {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Field type " + fieldType + " is not currently supported");
        }
    }
}


/**
 * Abstract implementation of {@link StatsValues} that provides the default behavior
 * for most StatsValues implementations.
 * <p/>
 * There are very few requirements placed on what statistics concrete implementations should collect, with the only required
 * statistics being the minimum and maximum values.
 */
@Deprecated
abstract class DiscoveryAbstractStatsValues<T> implements StatsValues {
    private static final String FACETS = "facets";
    final protected SchemaField sf;
    final protected FieldType ft;
    private String func = "count";
    private int offset = 0;
    private int limit = 10;

    private ValueSource valueSource;
    protected FunctionValues values;

    // facetField   facetValue
    protected Map<String, Map<String, StatsValues>> facets = new HashMap<String, Map<String, StatsValues>>();

    protected DiscoveryAbstractStatsValues(SchemaField sf) {
        this.sf = sf;
        this.ft = sf.getType();
    }

    protected DiscoveryAbstractStatsValues(SchemaField sf, String func, int offset, int limit) {
        this.sf = sf;
        this.ft = sf.getType();
        this.func = func;
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * {@inheritDoc}
     */
    public void accumulate(NamedList stv) {
        updateTypeSpecificStats(stv);

        NamedList f = (NamedList) stv.get(FACETS);
        if (f == null) {
            return;
        }

        for (int i = 0; i < f.size(); i++) {
            String field = f.getName(i);
            NamedList vals = (NamedList) f.getVal(i);
            Map<String, StatsValues> addTo = facets.get(field);
            if (addTo == null) {
                addTo = new HashMap<String, StatsValues>();
                facets.put(field, addTo);
            }
            for (int j = 0; j < vals.size(); j++) {
                String val = vals.getName(j);
                StatsValues vvals = addTo.get(val);
                if (vvals == null) {
                    vvals = DiscoveryStatsValuesFactory.createStatsValues(sf, this.func, this.offset, this.limit);
                    addTo.put(val, vvals);
                }
                vvals.accumulate((NamedList) vals.getVal(j));
            }
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    public void accumulate(BytesRef value) {
//        updateTypeSpecificStats(ft.toObject(sf, value));
//    }

    /**
     * {@inheritDoc}
     */
    public void accumulate(BytesRef value, int count) {
        T typedValue = (T) ft.toObject(sf, value);
        accumulate(typedValue, count);
    }

    public void accumulate(T value, int count) {
        updateTypeSpecificStats(value, count);
    }


    /**
     * {@inheritDoc}
     */
    public void addFacet(String facetName, Map<String, StatsValues> facetValues) {
        facets.put(facetName, facetValues);
    }

    public void setNextReader(AtomicReaderContext ctx) throws IOException {
        if (valueSource == null) {
            valueSource = ft.getValueSource(sf, null);
        }
        values = valueSource.getValues(Collections.emptyMap(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    public NamedList<?> getStatsValues() {
        NamedList<Object> res = new SimpleOrderedMap<Object>();

        addTypeSpecificStats(res);

        // add the facet stats
        NamedList<NamedList<?>> nl = new SimpleOrderedMap<NamedList<?>>();

        for (Map.Entry<String, Map<String, StatsValues>> entry : facets.entrySet()) {
            NamedList<NamedList<?>> nl2 = new SimpleOrderedMap<NamedList<?>>();

            //Sort
            Map<String, StatsValues> facet = MapUtil.sortByValue(entry.getValue());

            int off = offset;
            int lim = limit;
            for (Map.Entry<String, StatsValues> e2 : facet.entrySet()) {
                if (--off >= 0) continue;
                if (--lim < 0) break;

                if (e2.getKey() != null && !"".equals(e2.getKey())) {
                    nl2.add(e2.getKey(), e2.getValue().getStatsValues());
                } else {
//                    TODO check me out
//                    nl2.add("empty:" + e2.getKey(), e2.getValue().getStatsValues());
                }
            }

            nl.add(entry.getKey(), nl2);
        }
        res.add(FACETS, nl);
        return res;
    }

//    protected abstract void updateTypeSpecificStats(Object value);

    /**
     * Updates the type specific statistics based on the given value
     *
     * @param value Value the statistics should be updated against
     * @param count Number of times the value is being accumulated
     */
    protected abstract void updateTypeSpecificStats(T value, int count);

    /**
     * Updates the type specific statistics based on the values in the given list
     *
     * @param stv List containing values the current statistics should be updated against
     */
    protected abstract void updateTypeSpecificStats(NamedList stv);

    /**
     * Add any type specific statistics to the given NamedList
     *
     * @param res NamedList to add the type specific statistics too
     */
    protected abstract void addTypeSpecificStats(NamedList<Object> res);
}

@Deprecated
class CountStatsValues extends DiscoveryAbstractStatsValues<String> {
    protected long count;

    public CountStatsValues(SchemaField sf, String func, int offset, int limit) {
        super(sf, func, offset, limit);
    }

    @Override
    protected void updateTypeSpecificStats(String value, int count) {
        this.count += count;
    }

//    @Override
//    protected void updateTypeSpecificStats(T value, int count) {
//        this.count+= count;
//
//    }


//    @Override
//    protected void updateTypeSpecificStats(Object value) {
//        this.count++;
//    }
//
//
//    @Override
//    protected void updateTypeSpecificStats(Object value, int count) {
//        this.count += count;
//    }

    /**
     * {@inheritDoc}
     */
    protected void updateTypeSpecificStats(NamedList stv) {
        count += (Long) stv.get("count");
    }

    @Override
    protected void addTypeSpecificStats(NamedList<Object> res) {
        res.add("count", this.count);
    }

    @Override
    public void accumulate(int docID) {
        if (values.exists(docID)) {
            accumulate(values.strVal(docID), 1);
        } else {
            missing();
        }
    }

    @Override
    public void missing() {

    }

    @Override
    public void addMissing(int count) {

    }

    @Override
    public int compareTo(Object o) {
        CountStatsValues other = (CountStatsValues) o;
        Long t = other.count - this.count;
        return t.intValue();
    }
}


//TODO  distributed solr?
@Deprecated
class DistinctCountStatsValues extends DiscoveryAbstractStatsValues<String> {
    //protected long dcount = 0;
    protected Map<String, Boolean> dmap = new HashMap<String, Boolean>();


    public DistinctCountStatsValues(SchemaField sf, String func, int offset, int limit) {
        super(sf, func, offset, limit);
    }

    @Override
    protected void updateTypeSpecificStats(String value, int count) {
        dmap.put(value, true);
    }

    /**
     * {@inheritDoc}
     */
    protected void updateTypeSpecificStats(NamedList stv) {
        //dcount += (Long) stv.get("dcount");
    }

    @Override
    protected void addTypeSpecificStats(NamedList<Object> res) {
        res.add("dcount", this.dmap.size());
    }

    @Override
    public void accumulate(int docID) {
        if (values.exists(docID)) {
            accumulate(values.strVal(docID), 1);
        } else {
            missing();
        }
    }

    @Override
    public void missing() {

    }

    @Override
    public void addMissing(int count) {

    }

    @Override
    public int compareTo(Object o) {
        DistinctCountStatsValues other = (DistinctCountStatsValues) o;
        Integer t = other.dmap.size() - this.dmap.size();
        return t;
    }
}

@Deprecated
class SumStatsValues extends DiscoveryAbstractStatsValues<Number> {
    protected double sum;

    public SumStatsValues(SchemaField sf, String func, int offset, int limit) {
        super(sf, func, offset, limit);
    }

    @Override
    protected void updateTypeSpecificStats(Number value, int count) {
        this.sum += value.doubleValue();
    }

//    @Override
//    protected void updateTypeSpecificStats(Object obj) {
//        Number v = (Number) obj;
//        double value = v.doubleValue();
//        sum += value;
//
//    }
//
//    @Override
//    protected void updateTypeSpecificStats(Object obj, int count) {
//        Number v = (Number) obj;
//        double value = v.doubleValue();
//        sum += value;
//    }

    /**
     * {@inheritDoc}
     */
    protected void updateTypeSpecificStats(NamedList stv) {
        sum += ((Number) stv.get("sum")).doubleValue();
    }

    @Override
    protected void addTypeSpecificStats(NamedList<Object> res) {
        res.add("sum", this.sum);
    }

    @Override
    public void accumulate(int docID) {
        if (values.exists(docID)) {
            accumulate((Number) values.objectVal(docID), 1);
        } else {
            missing();
        }
    }

    @Override
    public void missing() {

    }

    @Override
    public void addMissing(int count) {

    }

    @Override
    public int compareTo(Object o) {
        SumStatsValues other = (SumStatsValues) o;
        Double t = other.sum - this.sum;
        return t.intValue();
    }
}

@Deprecated
class AverageStatsValues extends DiscoveryAbstractStatsValues<Number> {
    protected double sum;
    protected long count;

    public AverageStatsValues(SchemaField sf, String func, int offset, int limit) {
        super(sf, func, offset, limit);
    }

    @Override
    protected void updateTypeSpecificStats(Number value, int count) {
        sum += value.doubleValue();
        this.count += count;
    }

//    @Override
//    protected void updateTypeSpecificStats(Object obj) {
//        Number v = (Number) obj;
//        double value = v.doubleValue();
//        sum += value;
//        this.count++;
//
//    }

//    @Override
//    protected void updateTypeSpecificStats(Object obj, int count) {
//        Number v = (Number) obj;
//        double value = v.doubleValue();
//        sum += value;
//        this.count += count;
//    }

    /**
     * {@inheritDoc}
     */
    protected void updateTypeSpecificStats(NamedList stv) {
        sum += ((Number) stv.get("sum")).doubleValue();
        count += ((Number) stv.get("count")).longValue();
    }

    @Override
    protected void addTypeSpecificStats(NamedList<Object> res) {
        res.add("avg", this.sum / this.count);
    }

    @Override
    public void accumulate(int docID) {
        if (values.exists(docID)) {
            accumulate((Number) values.objectVal(docID), 1);
        } else {
            missing();
        }
    }

    @Override
    public void missing() {

    }

    @Override
    public void addMissing(int count) {

    }

    @Override
    public int compareTo(Object o) {
        SumStatsValues other = (SumStatsValues) o;
        Double t = other.sum - this.sum;
        return t.intValue();
    }
}
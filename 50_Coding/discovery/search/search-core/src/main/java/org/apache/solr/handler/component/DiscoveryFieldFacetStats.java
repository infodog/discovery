package org.apache.solr.handler.component;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.SolrIndexSearcher;

import java.io.IOException;
import java.util.*;


/**
 * 9/10/2009 - Moved out of StatsComponent to allow open access to UnInvertedField
 * FieldFacetStats is a utility to accumulate statistics on a set of values in one field,
 * for facet values present in another field.
 * <p/>
 *
 * @see StatsComponent
 */
@Deprecated
public class DiscoveryFieldFacetStats {
    public final String name;
    //final FieldCache.DocTermsIndex si;
    final SchemaField facet_sf;
    final SchemaField field_sf;

//    final int startTermIndex;
//    final int endTermIndex;
//    final int nTerms;
//
//    final int numStatsTerms;

    public Map<String, StatsValues> facetStatsValues;

    final List<HashMap<String, Integer>> facetStatsTerms;

    private final BytesRef tempBR = new BytesRef();

    final AtomicReader topLevelReader;
    AtomicReaderContext leave;
    final ValueSource valueSource;
    AtomicReaderContext context;
    FunctionValues values;

    SortedDocValues topLevelSortedValues = null;



    private String func = "count";

    private int offset = 0;
    private int limit = 10;



    public DiscoveryFieldFacetStats(SolrIndexSearcher searcher, String name, SchemaField field_sf, SchemaField facet_sf, String func, int offset, int limit) {
        this.name = name;
//        this.si = si;
        this.field_sf = field_sf;
        this.facet_sf = facet_sf;
//        this.numStatsTerms = numStatsTerms;
//
//        startTermIndex = 1;
//        endTermIndex = si.numOrd();
//        nTerms = endTermIndex - startTermIndex;

        topLevelReader = searcher.getAtomicReader();
        valueSource = facet_sf.getType().getValueSource(facet_sf, null);


        facetStatsValues = new HashMap<String, StatsValues>();

        // for mv stats field, we'll want to keep track of terms
        facetStatsTerms = new ArrayList<HashMap<String, Integer>>();
//        if (numStatsTerms == 0) return;
//        int i = 0;
//        for (; i < numStatsTerms; i++) {
//            facetStatsTerms.add(new HashMap<String, Integer>());
//        }

        this.func = func;

        this.offset = offset;
        this.limit = limit;
    }

//    BytesRef getTermText(int docID, BytesRef ret) {
//        final int ord = si.getOrd(docID);
//        if (ord == 0) {
//            return null;
//        } else {
//            return si.lookup(ord, ret);
//        }
//    }

//    public boolean facet(int docID, BytesRef v) {
//        int term = si.getOrd(docID);
//        int arrIdx = term - startTermIndex;
//        if (arrIdx >= 0 && arrIdx < nTerms) {
//            final BytesRef br = si.lookup(term, tempBR);
//            String key = (br == null) ? null : facet_sf.getType().indexedToReadable(br.utf8ToString());
//            StatsValues stats = facetStatsValues.get(key);
//            if (stats == null) {
//                stats = DiscoveryStatsValuesFactory.createStatsValues(field_sf, this.func, this.offset, this.limit);
//                facetStatsValues.put(key, stats);
//            }
//
//            if (v != null && v.length > 0) {
//                stats.accumulate(v);
//            } else {
//                stats.missing();
//                return false;
//            }
//            return true;
//        }
//        return false;
//    }

    private StatsValues getStatsValues(String key) throws IOException {
        StatsValues stats = facetStatsValues.get(key);
        if (stats == null) {
            stats = DiscoveryStatsValuesFactory.createStatsValues(field_sf, this.func, this.offset, this.limit);
            facetStatsValues.put(key, stats);
            stats.setNextReader(context);
        }
        return stats;
    }

    // docID is relative to the context
    public void facet(int docID) throws IOException {
        final String key = values.exists(docID)
                ? values.strVal(docID)
                : null;
        final StatsValues stats = getStatsValues(key);
        stats.accumulate(docID);
    }


    public void range(Number min, Number max) {
        Map<String, StatsValues> result = new HashMap<String, StatsValues>();

        for (Map.Entry<String, StatsValues> entry : facetStatsValues.entrySet()) {
            Number count = (Number)entry.getValue().getStatsValues().get("count");
            if (count.longValue() >= min.longValue() && count.longValue() <= max.longValue()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        this.facetStatsValues = result;
    }

    public int getTotal() {
        return this.facetStatsValues.size();
    }


    // Function to keep track of facet counts for term number.
    // Currently only used by UnInvertedField stats
//    public boolean facetTermNum(int docID, int statsTermNum) {
//
//        int term = si.getOrd(docID);
//        int arrIdx = term - startTermIndex;
//        if (arrIdx >= 0 && arrIdx < nTerms) {
//            final BytesRef br = si.lookup(term, tempBR);
//            String key = br == null ? null : br.utf8ToString();
//            HashMap<String, Integer> statsTermCounts = facetStatsTerms.get(statsTermNum);
//            Integer statsTermCount = statsTermCounts.get(key);
//            if (statsTermCount == null) {
//                statsTermCounts.put(key, 1);
//            } else {
//                statsTermCounts.put(key, statsTermCount + 1);
//            }
//            return true;
//        }
//        return false;
//    }


//    //function to accumulate counts for statsTermNum to specified value
//    public boolean accumulateTermNum(int statsTermNum, BytesRef value) {
//        if (value == null) return false;
//        for (Map.Entry<String, Integer> stringIntegerEntry : facetStatsTerms.get(statsTermNum).entrySet()) {
//            Map.Entry pairs = (Map.Entry) stringIntegerEntry;
//            String key = (String) pairs.getKey();
//            StatsValues facetStats = facetStatsValues.get(key);
//            if (facetStats == null) {
//                facetStats = DiscoveryStatsValuesFactory.createStatsValues(field_sf, this.func, this.offset, this.limit);
//                facetStatsValues.put(key, facetStats);
//            }
//            Integer count = (Integer) pairs.getValue();
//            if (count != null) {
//                facetStats.accumulate(value, count);
//            }
//        }
//        return true;
//    }

    // Function to keep track of facet counts for term number.
    // Currently only used by UnInvertedField stats
    public boolean facetTermNum(int docID, int statsTermNum) throws IOException {
        if (topLevelSortedValues == null) {
            topLevelSortedValues = FieldCache.DEFAULT.getTermsIndex(topLevelReader, name);
        }

        int term = topLevelSortedValues.getOrd(docID);
        int arrIdx = term;
        if (arrIdx >= 0 && arrIdx < topLevelSortedValues.getValueCount()) {
            final BytesRef br;
            if (term == -1) {
                br = null;
            } else {
                br = tempBR;
                topLevelSortedValues.lookupOrd(term, tempBR);
            }
            String key = br == null ? null : br.utf8ToString();
            while (facetStatsTerms.size() <= statsTermNum) {
                facetStatsTerms.add(new HashMap<String, Integer>());
            }
            final Map<String, Integer> statsTermCounts = facetStatsTerms.get(statsTermNum);
            Integer statsTermCount = statsTermCounts.get(key);
            if (statsTermCount == null) {
                statsTermCounts.put(key, 1);
            } else {
                statsTermCounts.put(key, statsTermCount + 1);
            }
            return true;
        }
        return false;
    }


    //function to accumulate counts for statsTermNum to specified value
    public boolean accumulateTermNum(int statsTermNum, BytesRef value) throws IOException {
        if (value == null) return false;
        while (facetStatsTerms.size() <= statsTermNum) {
            facetStatsTerms.add(new HashMap<String, Integer>());
        }
        for (Map.Entry<String, Integer> stringIntegerEntry : facetStatsTerms.get(statsTermNum).entrySet()) {
            Map.Entry pairs = (Map.Entry) stringIntegerEntry;
            String key = (String) pairs.getKey();
            StatsValues facetStats = facetStatsValues.get(key);
            if (facetStats == null) {
                facetStats = DiscoveryStatsValuesFactory.createStatsValues(field_sf, this.func, this.offset, this.limit);
                facetStatsValues.put(key, facetStats);
            }
            Integer count = (Integer) pairs.getValue();
            if (count != null) {
                facetStats.accumulate(value, count);
            }
        }
        return true;
    }

    public void setNextReader(AtomicReaderContext ctx) throws IOException {
        this.context = ctx;
        values = valueSource.getValues(Collections.emptyMap(), ctx);
        for (StatsValues stats : facetStatsValues.values()) {
            stats.setNextReader(ctx);
        }
    }


}



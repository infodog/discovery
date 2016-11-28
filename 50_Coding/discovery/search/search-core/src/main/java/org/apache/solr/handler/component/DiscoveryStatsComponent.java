package org.apache.solr.handler.component;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.RequiredSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.params.StatsParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.params.DiscoveryStatsParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.UnInvertedField;
import org.apache.solr.schema.*;
import org.apache.solr.search.*;
import org.apache.solr.util.DateMathParser;

import java.io.IOException;
import java.util.*;


@Deprecated
public class DiscoveryStatsComponent extends SearchComponent {

    public static final String COMPONENT_NAME = "discoveryStats";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        if (rb.req.getParams().getBool(DiscoveryStatsParams.STATS, false)) {
            rb.setNeedDocSet(true);
            rb.doStats = true;
        }
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException {
        if (rb.doStats) {
            SolrParams params = rb.req.getParams();
            DiscoverySimpleStats s = new DiscoverySimpleStats(rb.req,
                    rb.getResults().docSet,
                    params);

            // TODO ???? add this directly to the response, or to the builder?
            try {
                rb.rsp.add("stats", s.getStatsCounts());
            } catch (SyntaxError syntaxError) {
                syntaxError.printStackTrace();
            }

            String[] fs = rb.req.getParams().getParams(DiscoveryStatsParams.STATS_ORDER_FIELDS);
            if (fs != null && fs.length > 0) {
                List<String> fields = new ArrayList<String>();
                for (String f : fs) {
                    fields.add(f);
                }
                rb.rsp.add("fields", fields);
            }

            rb.rsp.add("total", s.getTotal());
        }
    }

    @Override
    public int distributedProcess(ResponseBuilder rb) throws IOException {
        return ResponseBuilder.STAGE_DONE;
    }

    @Override
    public void modifyRequest(ResponseBuilder rb, SearchComponent who, ShardRequest sreq) {
        if (!rb.doStats) return;

        if ((sreq.purpose & ShardRequest.PURPOSE_GET_TOP_IDS) != 0) {
            sreq.purpose |= ShardRequest.PURPOSE_GET_STATS;

            StatsInfo si = rb._statsInfo;
            if (si == null) {
                rb._statsInfo = si = new StatsInfo();
                si.parse(rb.req.getParams(), rb);
                // should already be true...
                // sreq.params.set(StatsParams.STATS, "true");
            }
        } else {
            // turn off stats on other requests
            sreq.params.set(StatsParams.STATS, "false");
            // we could optionally remove stats params
        }
    }

    @Override
    public void handleResponses(ResponseBuilder rb, ShardRequest sreq) {
        if (!rb.doStats || (sreq.purpose & ShardRequest.PURPOSE_GET_STATS) == 0) return;

        StatsInfo si = rb._statsInfo;

        for (ShardResponse srsp : sreq.responses) {
            NamedList stats = (NamedList) srsp.getSolrResponse().getResponse().get("stats");

            NamedList stats_fields = (NamedList) stats.get("stats_fields");
            if (stats_fields != null) {
                for (int i = 0; i < stats_fields.size(); i++) {
                    String field = stats_fields.getName(i);
                    StatsValues stv = si.statsFields.get(field);
                    NamedList shardStv = (NamedList) stats_fields.get(field);
                    stv.accumulate(shardStv);
                }
            }
        }
    }

    @Override
    public void finishStage(ResponseBuilder rb) {
        if (!rb.doStats || rb.stage != ResponseBuilder.STAGE_GET_FIELDS) return;
        // wait until STAGE_GET_FIELDS
        // so that "result" is already stored in the response (for aesthetics)

        StatsInfo si = rb._statsInfo;

        NamedList<NamedList<Object>> stats = new SimpleOrderedMap<NamedList<Object>>();
        NamedList<Object> stats_fields = new SimpleOrderedMap<Object>();
        stats.add("stats_fields", stats_fields);
        for (String field : si.statsFields.keySet()) {
            NamedList stv = si.statsFields.get(field).getStatsValues();
            if ((Long) stv.get("count") != 0) {
                stats_fields.add(field, stv);
            } else {
                stats_fields.add(field, null);
            }
        }

        rb.rsp.add("stats", stats);

        rb._statsInfo = null;
    }


    /////////////////////////////////////////////
    ///  SolrInfoMBean
    ////////////////////////////////////////////

    @Override
    public String getDescription() {
        return "Calculate Statistics";
    }

    @Override
    public String getSource() {
        return "";
    }

}

@Deprecated
class StatsInfo {
    Map<String, StatsValues> statsFields;

    void parse(SolrParams params, ResponseBuilder rb) {
        statsFields = new HashMap<String, StatsValues>();

        String[] statsFs = params.getParams(DiscoveryStatsParams.STATS_FIELD);
        String func = params.get(DiscoveryStatsParams.STATS_FUNCTION);
        int offset = params.getInt(DiscoveryStatsParams.STATS_OFFSET, 0);
        int limit = params.getInt(DiscoveryStatsParams.STATS_LIMIT, 10);
        if (statsFs != null) {
            for (String field : statsFs) {
                SchemaField sf = rb.req.getSchema().getField(field);
                statsFields.put(field, DiscoveryStatsValuesFactory.createStatsValues(sf, func, offset, limit));
            }
        }
    }
}

@Deprecated
class DiscoverySimpleStats {

    /**
     * The main set of documents
     */
    protected DocSet docs;
    /**
     * Configuration params behavior should be driven by
     */
    protected SolrParams params;

    protected SolrParams required;

    /**
     * Searcher to use for all calculations
     */
    protected SolrIndexSearcher searcher;
    protected SolrQueryRequest req;

    // per-facet values
    SolrParams localParams; // localParams on this particular facet command
    String facetValue;      // the field to or query to facet on (minus local params)
    DocSet base;            // the base docset for this particular facet
    String key;             // what name should the results be stored under
    int threads;

    public int getTotal() {
        return total;
    }

    private int total = 0;

    public DiscoverySimpleStats(SolrQueryRequest req,
                                DocSet docs,
                                SolrParams params) {
        this.req = req;
        this.searcher = req.getSearcher();
        this.docs = docs;
        this.params = params;
        this.required = new RequiredSolrParams(params);
    }

    public NamedList<Object> getStatsCounts() throws IOException, SyntaxError {
        NamedList<Object> res = new SimpleOrderedMap<Object>();
        res.add("stats_fields", getStatsFields());
        return res;
    }

    public NamedList<Object> getStatsFields() throws IOException, SyntaxError {
        NamedList<Object> res = new SimpleOrderedMap<Object>();
        String[] statsFs = params.getParams(DiscoveryStatsParams.STATS_FIELD);
        String[] funcs = params.getParams(DiscoveryStatsParams.STATS_FUNCTION);
        int offset = params.getInt(DiscoveryStatsParams.STATS_OFFSET, 0);
        int limit = params.getInt(DiscoveryStatsParams.STATS_LIMIT, 10);
        int i = 0;
        if (null != statsFs) {
            for (String f : statsFs) {
                String[] facets = params.getFieldParams(f, DiscoveryStatsParams.STATS_FACET);
                if (facets == null) {
                    facets = new String[0]; // make sure it is something...
                }
                SchemaField sf = searcher.getSchema().getField(f);
                FieldType ft = sf.getType();
                NamedList stv;

                // Currently, only UnInvertedField can deal with multi-part trie fields
                String prefix = TrieField.getMainValuePrefix(ft);

                if (sf.multiValued() || ft.multiValuedFieldCache() || prefix != null) {
                    //use UnInvertedField for multivalued fields
                    UnInvertedField uif = UnInvertedField.getUnInvertedField(f, searcher);
                    stv = uif.getStats(searcher, docs, facets).getStatsValues();
                } else {
                    stv = getFieldCacheStats(f, facets, funcs[i], offset, limit);
                }

                res.add(f, stv);

                //Range
                String rangeFs = params.get(DiscoveryStatsParams.STATS_FACET_RANGE);
                if (rangeFs != null) {
                    try {
                        //res.remove(f);
                        NamedList range = getFacetRangeCounts(rangeFs, sf, funcs[i], offset, limit);
                        //NamedList facetR = new SimpleOrderedMap();
                        //facetR.add("facets", range);
                        //res.add(f, facetR);
                        stv.remove("facets");
                        stv.add("facets", range);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                i++;
            }
        }



        return res;
    }

    // why does this use a top-level field cache?
    public NamedList<?> getFieldCacheStats(String fieldName, String[] facet, String func, int offset, int limit) throws IOException {
        SchemaField sf = searcher.getSchema().getField(fieldName);

//        FieldCache.DocTermsIndex si;
//        try {
//            si = FieldCache.DEFAULT.getTermsIndex(searcher.getAtomicReader(), fieldName);
//        } catch (IOException e) {
//            throw new RuntimeException("failed to open field cache for: " + fieldName, e);
//        }
        StatsValues allstats = DiscoveryStatsValuesFactory.createStatsValues(sf, func, offset, limit);

//        final int nTerms = si.numOrd();
//        if (nTerms <= 0 || docs.size() <= 0) return allstats.getStatsValues();

        // don't worry about faceting if no documents match...
        List<DiscoveryFieldFacetStats> facetStats = new ArrayList<DiscoveryFieldFacetStats>();

//        FieldCache.DocTermsIndex facetTermsIndex;

        for (String facetField : facet) {
            SchemaField fsf = searcher.getSchema().getField(facetField);

            if (fsf.multiValued()) {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
                        "Stats can only facet on single-valued fields, not: " + facetField);
            }

//            try {
//                facetTermsIndex = FieldCache.DEFAULT.getTermsIndex(searcher.getAtomicReader(), facetField);
//            } catch (IOException e) {
//                throw new RuntimeException("failed to open field cache for: "
//                        + facetField, e);
//            }
            facetStats.add(new DiscoveryFieldFacetStats(searcher,facetField, sf, fsf, func, offset, limit));
        }

        final Iterator<AtomicReaderContext> ctxIt = searcher.getIndexReader().leaves().iterator();
        AtomicReaderContext ctx = null;
        for (DocIterator docsIt = docs.iterator(); docsIt.hasNext(); ) {
            final int doc = docsIt.nextDoc();
            if (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc()) {
                // advance
                do {
                    ctx = ctxIt.next();
                } while (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc());
                assert doc >= ctx.docBase;

                // propagate the context among accumulators.
                allstats.setNextReader(ctx);
                for (DiscoveryFieldFacetStats f : facetStats) {
                    f.setNextReader(ctx);
                }
            }

            // accumulate
            allstats.accumulate(doc - ctx.docBase);
            for (DiscoveryFieldFacetStats f : facetStats) {
                f.facet(doc - ctx.docBase);
            }
        }

        Number min = null;
        Number max = null;
        try {
            min = Long.valueOf(params.get(DiscoveryStatsParams.STATS_MIN));
            max = Long.valueOf(params.get(DiscoveryStatsParams.STATS_MAX));
        } catch (Exception e) {
//            e.printStackTrace();
        }

        for (DiscoveryFieldFacetStats f : facetStats) {
            if (min != null && max !=null) {
                f.range(min, max);
            }
            allstats.addFacet(f.name, f.facetStatsValues);
            total = f.getTotal();
        }
        return allstats.getStatsValues();
    }

    void parseParams(String type, String param) throws IOException, SyntaxError {
        localParams = QueryParsing.getLocalParams(param, req.getParams());
        base = docs;
        facetValue = param;
        key = param;
        threads = -1;

        if (localParams == null) return;

        // reset set the default key now that localParams have been removed
        key = facetValue;

        String threadStr = localParams.get(CommonParams.THREADS);
        if (threadStr != null) {
            threads = Integer.parseInt(threadStr);
        }

        // figure out if we need a new base DocSet
        String excludeStr = localParams.get(CommonParams.EXCLUDE);
        if (excludeStr == null) return;
    }


    private NamedList getFacetRangeCounts(String facetRange, SchemaField statF, String func, int offset, int limit)
            throws IOException, SyntaxError {
        NamedList resOuter = new SimpleOrderedMap();
        final IndexSchema schema = searcher.getSchema();

        parseParams(DiscoveryStatsParams.STATS_FACET_RANGE, facetRange);
        String f = facetValue;

        final SchemaField sf = schema.getField(f);
        final FieldType ft = sf.getType();

        RangeEndpointCalculator<?> calc = null;

        if (ft instanceof TrieField) {

        } else if (ft instanceof DateField) {
            calc = new DateRangeEndpointCalculator(sf, null);
        } else {
            throw new SolrException
                    (SolrException.ErrorCode.BAD_REQUEST,
                            "Unable to range facet on field:" + sf);
        }

        resOuter.add(key, getFacetRangeValue(sf, statF, calc, func, offset, limit));

        return resOuter;
    }

    private <T extends Comparable<T>> NamedList getFacetRangeValue
            (final SchemaField sf, final SchemaField statF,
             final RangeEndpointCalculator<T> calc, String func, int offset, int limit) throws IOException {

        final String f = sf.getName();

        final NamedList counts = new NamedList();


        final T start = calc.getValue(required.getFieldParam(f, DiscoveryStatsParams.STATS_FACET_RANGE_START));
        // not final, hardend may change this
        T end = calc.getValue(required.getFieldParam(f, DiscoveryStatsParams.STATS_FACET_RANGE_END));
        if (end.compareTo(start) < 0) {
            throw new SolrException
                    (SolrException.ErrorCode.BAD_REQUEST,
                            "range facet 'end' comes before 'start': " + end + " < " + start);
        }

        final String gap = required.getFieldParam(f, DiscoveryStatsParams.STATS_FACET_RANGE_GAP);
        // explicitly return the gap.  compute this early so we are more
        // likely to catch parse errors before attempting math
        //res.add("gap", calc.getGap(gap));

        final int minCount = 1;

        T low = start;

        while (low.compareTo(end) < 0) {
            T high = calc.addGap(low, gap);
//            if (end.compareTo(high) < 0) {
//                end = high;
//            }
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

            final boolean includeLower = true;
            final boolean includeUpper = true;

            final String lowS = calc.formatValue(low);
            final String highS = calc.formatValue(high);

            NamedList<?> stv;
            stv = rangValue(sf, statF, lowS,  highS, includeLower, includeUpper, func, offset, limit);

//            final int count = rangeCount(sf, lowS, highS,
//                    includeLower, includeUpper);
////            if (count >= minCount) {
//                counts.add(lowS, count);
//            }

            //counts.add(lowS, count);
            //format
            String label = lowS.replace("T00:00:00Z","");
            label = label.replace(":00:00Z", "");
            counts.add(label, stv);
            low = high;
        }

        //res.add("start", start);
        //res.add("end", end);
        total = counts.size();
        return counts;
    }

    private NamedList<?> rangValue(SchemaField rangeF, SchemaField statF, String low, String high, boolean iLow, boolean iHigh, String func, int offset, int limit) throws IOException {

        StatsValues allstats = DiscoveryStatsValuesFactory.createStatsValues(statF, func, offset, limit);
        Query rangeQ = rangeF.getType().getRangeQuery(null, rangeF, low, high, iLow, iHigh);

        final Iterator<AtomicReaderContext> ctxIt = searcher.getIndexReader().leaves().iterator();
        AtomicReaderContext ctx = null;

        DocSet docs = searcher.getDocSet(rangeQ, base);

        for (DocIterator docsIt = docs.iterator(); docsIt.hasNext(); ) {
            final int doc = docsIt.nextDoc();
            if (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc()) {
                // advance
                do {
                    ctx = ctxIt.next();
                } while (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc());
                assert doc >= ctx.docBase;

                // propagate the context among accumulators.
                allstats.setNextReader(ctx);
            }

            // accumulate
            allstats.accumulate(doc - ctx.docBase);
        }



        return allstats.getStatsValues();
    }

    /**
     * Macro for getting the numDocs of range over docs
     *
     * @see SolrIndexSearcher#numDocs
     */
//    protected int rangeCount(SchemaField sf, String low, String high,
//                             boolean iLow, boolean iHigh) throws IOException {
//        Query rangeQ = sf.getType().getRangeQuery(null, sf, low, high, iLow, iHigh);
//        return searcher.numDocs(rangeQ, base);
//    }


    /**
     * Perhaps someday instead of having a giant "instanceof" case
     * statement to pick an impl, we can add a "RangeFacetable" marker
     * interface to FieldTypes and they can return instances of these
     * directly from some method -- but until then, keep this locked down
     * and private.
     */
    private static abstract class RangeEndpointCalculator<T extends Comparable<T>> {
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
                        "Can't parse value " + rawval + " for field: " +
                                field.getName(), e);
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
                        "Can't parse gap " + gap + " for field: " +
                                field.getName(), e);
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
                                " for field: " + field.getName(), e);
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

    private static class DateRangeEndpointCalculator
            extends RangeEndpointCalculator<Date> {
        private final Date now;

        public DateRangeEndpointCalculator(final SchemaField f,
                                           final Date now) {
            super(f);
            this.now = now;
            if (!(field.getType() instanceof DateField)) {
                throw new IllegalArgumentException
                        ("SchemaField must use filed type extending DateField");
            }
        }

        @Override
        public String formatValue(Date val) {
            return ((DateField) field.getType()).toExternal(val);
        }

        @Override
        protected Date parseVal(String rawval) {
            return ((DateField) field.getType()).parseMath(now, rawval);
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

}



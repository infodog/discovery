package org.apache.solr.handler.component;

//import au.com.bytecode.opencsv.CSVReader;
//import au.com.bytecode.opencsv.CSVWriter;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.insight.fieldFunc.FieldFunctionFactory;
import org.apache.solr.insight.fieldFunc.IFieldFunction;
import org.apache.solr.insight.filter.InterResult;
import org.apache.solr.insight.range.RangeEndpointCalculator;
import org.apache.solr.insight.range.RangeHelper;
import org.apache.solr.insight.range.RangeItem;
import org.apache.solr.params.InsightParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.schema.*;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocSet;
import org.apache.solr.search.SolrIndexSearcher;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 6/28/13
 * Time: 11:19 AM
 */
public class FieldValueCollector {
    public static Pattern RANGE_PATTERN = Pattern.compile("(.+)\\(start=(.+) end=(.+) gap=(.+)\\)");
    private SolrIndexSearcher searcher;
    private DocSet docs;
    private String field;
    private IFieldFunction fieldFunction;
    private String[] valueFields;
    private IFieldFunction[] valueFieldFunctions;
    private boolean isRange = false;
    private String range_start;
    private String range_end;
    private String range_gap;

    private Object[] parseField(String fieldSpec){
        Object[] result = new Object[2];
        String[] parts = fieldSpec.split("@@");
        result[0] = parts[0];
        if(parts.length>1){
            result[1] = FieldFunctionFactory.getFunction(parts[1]);
        }
        return result;
    }
    public FieldValueCollector(SolrQueryRequest req, DocSet docs, SolrParams params) {
        this.searcher = req.getSearcher();
        this.docs = docs;
        String fields = params.get(InsightParams.INSIGHT_FIELD);

        String[] fcs = fields.split(",");
        String[] des = new String[fcs.length - 1];
        this.valueFieldFunctions = new IFieldFunction[fcs.length - 1];
        for(int i=1; i<fcs.length; i++){
            String fieldSpec = fcs[i];
            Object[] fieldParts = parseField(fieldSpec);
            des[i-1] = (String) fieldParts[0];
            if(fieldParts.length>1){
                this.valueFieldFunctions[i-1] = (IFieldFunction) fieldParts[1];
            }

        }
        //System.arraycopy(fcs, 1, des, 0, fcs.length - 1);
        this.valueFields = des;

        Object[] fieldParts = parseField(fcs[0]);
        this.field = (String) fieldParts[0];
        if(fieldParts.length>1) {
            this.fieldFunction = (IFieldFunction) fieldParts[1];
        }

        Matcher range = RANGE_PATTERN.matcher(this.field);
        if (range.find()) {
            this.isRange = true;
            this.range_start = range.group(2);
            this.range_end = range.group(3);
            this.range_gap = range.group(4);
            this.field = range.group(1).trim();

        } else {
            this.isRange = false;
        }
    }

    public InterResult getResults() throws Exception {
        if (isRange) {
            //Matcher range = RANGE_PATTERN.matcher(this.field);
            return this.getRangeInterResult(this.range_start, this.range_end, this.range_gap);
        } else {
            return getInterResult();
        }
    }

    private InterResult getRangeInterResult(String begin, String last, String gap) throws Exception {
        final SchemaField sf = this.searcher.getSchema().getField(this.field);

        return this.getRangeValues(sf, begin, last, gap);
    }

    private InterResult getRangeValues(SchemaField sf, String begin, String last, String gap) throws Exception {

//        CharArrayWriter caWriter = new CharArrayWriter();
        long total = 0;
        ArrayList<String[]> lines = new ArrayList();

//        CSVWriter csvWriter = new CSVWriter(caWriter);

        String[] head = new String[valueFields.length + 1];
        head[0] = field;
        int j = 1;
        for (String valueField : valueFields) {
            head[j++] = valueField;
        }
        lines.add(head);
//        csvWriter.writeNext(head);


        //List<RangeItem> items = RangeHelper.getInstance().getDateRange(begin, last, gap);
        List<RangeItem> items = RangeHelper.getInstance().getRange(sf, begin, last, gap);

        final boolean includeLower = true;
        final boolean includeUpper = true;
        for (RangeItem item : items) {
            final String lowS = item.getStart();
            final String highS = item.getEnd();
            Query rangeQ = sf.getType().getRangeQuery(null, sf, lowS, highS, includeLower, includeUpper);
            final Iterator<AtomicReaderContext> ctxIt = searcher.getIndexReader().leaves().iterator();
            AtomicReaderContext ctx = null;

            DocSet docs = searcher.getDocSet(rangeQ, this.docs);

            boolean absent = true;
            HashMap<String,FilterValue> filterValuesMap = new HashMap();
            for (DocIterator docsIt = docs.iterator(); docsIt.hasNext(); ) {
                final int doc = docsIt.nextDoc();
                if (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc()) {
                    // advance
                    do {
                        ctx = ctxIt.next();
                    } while (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc());
                    assert doc >= ctx.docBase;
                    for (String valueField : valueFields){
                        filterValuesMap.put(valueField,new FilterValue(ctx,this.searcher,valueField));
                    }
                }

                String key = item.getLabel();
                if (key != null && !"".equals(key)) {
                    String[] line = new String[valueFields.length + 1];
                    line[0] = key;
                    int i = 1;
                    for (String valueField : valueFields) {
                        FilterValue filterValue = filterValuesMap.get(valueField);
                        String value = filterValue.getValue(doc - ctx.docBase);
                        line[i++] = value;
                    }
//                    csvWriter.writeNext(line);
                    lines.add(line);
                    total++;
                    absent = false;
                }
            }

            if (absent) {
                String[] line = new String[valueFields.length + 1];
                line[0] = item.getLabel();
                int i = 1;
                for (String valueField : valueFields) {
                    String value = "insight_absent";
                    line[i++] = value;
                }
                lines.add(line);
            }

        }

//        CharArrayReader caReader = new CharArrayReader(caWriter.toCharArray());
//        CSVReader csvReader = new CSVReader(caReader);
        InterResult interOut = new InterResult();
//        interOut.setReader(csvReader);
        interOut.setLines(lines);
        interOut.setTotal(total);

        return interOut;
    }

    private InterResult getInterResult() throws Exception {
        return getResults(0,-1);
    }

    public InterResult getResults(int offset, int limit) throws Exception {
        SchemaField sf = searcher.getSchema().getField(this.field);
        FieldType ft = sf.getType();
        //CharArrayWriter caWriter = new CharArrayWriter();
        ArrayList lines = new ArrayList();
        long total;
        if (sf.multiValued() || ft.multiValuedFieldCache()) {
            throw new Exception("not implemented yet!");
        } else {
            //CSVWriter csvWriter = new CSVWriter(caWriter);

            String[] head = new String[valueFields.length + 1];
            head[0] = field;
            int j = 1;
            for (String valueField : valueFields) {
                head[j++] = valueField;
            }
//            csvWriter.writeNext(head);
            lines.add(head);

            int off = offset;
            int lim = limit;

            final Iterator<AtomicReaderContext> ctxIt = searcher.getIndexReader().leaves().iterator();
            AtomicReaderContext ctx = null;
            total = docs.size();
            HashMap<String,FilterValue> filterValuesMap = new HashMap();
            for (DocIterator docsIt = docs.iterator(); docsIt.hasNext(); ) {
                final int doc = docsIt.nextDoc();
                if(--off >= 0) continue;

                if(limit>=0 && --lim  < 0) break;

                if (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc()) {
                    // advance
                    do {
                        ctx = ctxIt.next();
                    } while (ctx == null || doc >= ctx.docBase + ctx.reader().maxDoc());
                    assert doc >= ctx.docBase;
                    filterValuesMap.put(this.field,new FilterValue(ctx,this.searcher, this.field));
                    for (String valueField : valueFields){
                        filterValuesMap.put(valueField,new FilterValue(ctx,this.searcher,valueField));
                    }
                }
                String[] line = new String[valueFields.length + 1];
                FilterValue filterValue = filterValuesMap.get(this.field);
                String key = filterValue.getValue(doc - ctx.docBase);
                if(this.fieldFunction!=null){
                    key = this.fieldFunction.getValue(key);
                }
                line[0] = key;
                if(key==null){
                    line[0] =  "NaN";
                }
                int i = 1;
                for (String valueField : valueFields) {
                    filterValue = filterValuesMap.get(valueField);
                    String value = filterValue.getValue(doc - ctx.docBase);
                    if(this.valueFieldFunctions[i-1]!=null){
                        value = this.valueFieldFunctions[i-1].getValue(value);
                    }
                    if(value!=null) {
                        line[i++] = value;
                    }
                    else{
                        line[i++] = "NaN";
                    }
                }
                //csvWriter.writeNext(line);
                lines.add(line);
            }
        }

//        CharArrayReader caReader = new CharArrayReader(caWriter.toCharArray());
//        CSVReader csvReader = new CSVReader(caReader);
        InterResult interOut = new InterResult();
//        interOut.setReader(csvReader);
        interOut.setLines(lines);
        interOut.setTotal(total);

        return interOut;
    }
}

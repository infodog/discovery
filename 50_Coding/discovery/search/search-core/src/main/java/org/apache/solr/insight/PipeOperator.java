package org.apache.solr.insight;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.insight.filter.InsightFilter;
import org.apache.solr.insight.filter.InterResult;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/1/13
 * Time: 11:15 AM
 */
public class PipeOperator {
    private List<InsightFilter> filters;
    private InterResult interResult;
    private int offset = 0;
    private int limit = 10;

    public PipeOperator(List<InsightFilter> filters, InterResult interResult, int offset, int limit) {
        this.filters = filters;
        this.interResult = interResult;
        this.offset = offset;
        this.limit = limit;
    }

    public NamedList getOutput() throws Exception {

        for (InsightFilter filter : filters) {
            InterResult result = filter.process(this.interResult);
            this.interResult = result;
        }

        //CSVReader reader = interResult.getReader();
        List<String[]> lines =interResult.getLines();
        NamedList result = new SimpleOrderedMap();

        //for export
        //CharArrayWriter caWriter = new CharArrayWriter();
        //CSVWriter csvWriter = new CSVWriter(caWriter);


        String[] fields = lines.get(0);
        if (fields != null) {
            result.add("fields", fields);
            //csvWriter.writeNext(fields);
        }

        int off = offset;
        int lim = limit;
        String[] line;
        List<NamedList> rows = new ArrayList<NamedList>();
        for(int j=1; j<lines.size();j++) {
            if (--off >= 0) continue;
            if (--lim < 0) break;
            line = lines.get(j);
            NamedList row = new SimpleOrderedMap();
            int i = 0;
            for (String cell : line) {
                row.add(fields[i++], cell);
            }
            rows.add(row);
            //csvWriter.writeNext(line);
        }
        result.add("rows", rows);
        result.add("total", interResult.getTotal());

//        csvWriter.writeAll(reader.readAll());
//        CharArrayReader caReader = new CharArrayReader(caWriter.toCharArray());
//        CSVReader output = new CSVReader(caReader);
//
//
//        result.add("export", output);

        return result;
    }
}

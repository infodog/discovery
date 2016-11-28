package org.apache.solr.insight.filter;

import au.com.bytecode.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/3/13
 * Time: 2:50 PM
 */
public class InterResult {
    private CSVReader reader;
    private long total;

    public List<String[]> getLines() {
        return lines;
    }

    public void setLines(List<String[]> lines) {
        this.lines = lines;
    }

    List<String[]> lines;



    public CSVReader getReader() {
        return reader;
    }

    public void setReader(CSVReader reader) {
        this.reader = reader;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}

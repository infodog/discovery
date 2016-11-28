package net.xinshi.discovery.search.mgt.bean;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import net.xinshi.discovery.search.mgt.response.InsightResponseWriter;
import net.xinshi.discovery.search.mgt.response.InsightXMLResponseWriter;
import net.xinshi.discovery.search.mgt.response.JSONColsResponseWriter;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.insight.filter.InterResult;
import org.apache.solr.response.ResultContext;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.ReturnFields;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/5/13
 * Time: 6:57 PM
 */
public class ResultSet {
    public static String OUTPUT_MODE_JSON_COLS = "json_cols";
    public static String OUTPUT_MODE_JSON = "json";
    public static String OUTPUT_MODE_XML = "xml";
    public static String OUTPUT_MODE_CSV = "csv";
    public static InsightResponseWriter xmlWriter = new InsightXMLResponseWriter();
    public static InsightResponseWriter jsonColumnsWriter = new JSONColsResponseWriter();


    private long total;
    private NamedList values;
    private ResultContext resultContext;
    private IndexSchema schema;
    private SolrParams params;
    private ReturnFields returnFields;
    private CSVReader csvReader;

    public CSVReader getCsvReader() {
        return csvReader;
    }

    public void setCsvReader(CSVReader csvReader) {
        this.csvReader = csvReader;
    }

    public ReturnFields getReturnFields() {
        return returnFields;
    }

    public void setReturnFields(ReturnFields returnFields) {
        this.returnFields = returnFields;
    }

    public SolrParams getParams() {
        return params;
    }

    public void setParams(SolrParams params) {
        this.params = params;
    }

    public IndexSchema getSchema() {
        return schema;
    }

    public void setSchema(IndexSchema schema) {
        this.schema = schema;
    }

    public ResultContext getResultContext() {
        return resultContext;
    }

    public void setResultContext(ResultContext resultContext) {
        this.resultContext = resultContext;
    }

    public NamedList getValues() {
        return values;
    }

    public void setValues(NamedList values) {
        this.values = values;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }



    public String output(String mode, int offset, int limit) {
        if (OUTPUT_MODE_JSON_COLS.equals(mode)) {
            return this.outputJsonColumns(offset, limit);
        } else if (OUTPUT_MODE_JSON.equals(mode)) {
            return this.outputJSON(offset, limit);
        } else if (OUTPUT_MODE_XML.equals(mode)) {
            return this.outputXML(offset, limit);
        }
        return "";
    }

    private String outputXML(int offset, int limit) {
        StringWriter sw = new StringWriter(32000);
        try {
            xmlWriter.write(sw,this.getSchema(),this.getParams(),this.getValues(), this.getReturnFields(), offset, limit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    private String outputJSON(int offset, int limit) {
        StringWriter sw = new StringWriter(32000);
        return sw.toString();
    }

    private String outputJsonColumns(int offset, int limit) {
        StringWriter sw = new StringWriter(32000);

        try {
            jsonColumnsWriter.write(sw,this.getSchema(),this.getParams(),this.getValues(), this.getReturnFields(), offset, limit);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sw.toString();
    }

    public String export() {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);
        try {
            csvWriter.writeAll(this.csvReader.readAll());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}

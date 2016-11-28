package net.xinshi.discovery.search.mgt.response;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.ReturnFields;

import java.io.IOException;
import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/11/13
 * Time: 10:52 AM
 */
public class InsightXMLResponseWriter implements InsightResponseWriter {
    @Override
    public void write(Writer writer, IndexSchema schema, SolrParams params, NamedList values, ReturnFields returnFields, int offset, int limit) throws IOException {
        InsightXMLWriter w = new InsightXMLWriter(writer, schema, params, values, returnFields, offset, limit);
        try {
            w.writeResponse();
        } finally {
            w.close();
        }
    }
}

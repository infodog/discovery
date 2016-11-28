package org.apache.solr.handler.component;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.SolrIndexSearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 6/28/13
 * Time: 3:28 PM
 */
public class FilterValue {
    ValueSource valueSource;
    FunctionValues values;
    SolrIndexSearcher searcher;
    String f;
    AtomicReaderContext ctx;
    boolean isStoredAndNoIndexed;

    public FilterValue(AtomicReaderContext ctx, SolrIndexSearcher searcher, String f) throws Exception{
        SchemaField sf = searcher.getSchema().getField(f);
        FieldType ft = sf.getType();

        if (sf.stored() && !sf.indexed()) {
            isStoredAndNoIndexed = true;
        }
        else{
            isStoredAndNoIndexed = false;
        }
        this.searcher= searcher;
        this.f = f;
        this.ctx = ctx;
        valueSource = ft.getValueSource(sf, null);
        values = valueSource.getValues(Collections.emptyMap(), ctx);
    }

    public  String getValue(int docID) {
        if(isStoredAndNoIndexed){
            try {
                Document doc = searcher.getIndexReader().document(docID);
                SolrDocument solrDoc = toSolrDocument(searcher.getSchema(),doc);
                Field value = (Field)solrDoc.getFieldValue(f);
                if (value != null) {
                    return value.stringValue();
                } else {
                    return "";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            if (values.exists(docID)) {
                return values.strVal(docID);
            }
            return null;
        }

    }

    private static final SolrDocument toSolrDocument(IndexSchema schema, Document doc) {
        SolrDocument out = new SolrDocument();
        for( IndexableField f : doc) {
            // Make sure multivalued fields are represented as lists
            Object existing = out.get(f.name());
            if (existing == null) {
                SchemaField sf = schema.getFieldOrNull(f.name());
                if (sf != null && sf.multiValued()) {
                    List<Object> vals = new ArrayList<Object>();
                    vals.add( f );
                    out.setField( f.name(), vals );
                }
                else{
                    out.setField( f.name(), f );
                }
            }
            else {
                out.addField( f.name(), f );
            }
        }
        return out;
    }
}

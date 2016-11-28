package org.apache.solr.schema;

import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.SortField;
import org.apache.solr.response.TextResponseWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: benzhao
 * Date: 11/16/12
 * Time: 5:44 PM
 */
public class FacetColumnField extends PrimitiveFieldType {
    @Override
    public boolean isPolyField() {
        return true;   // really only true if the field is indexed
    }

    @Override
    public List<IndexableField> createFields(SchemaField field, Object value, float boost) {
        String path = (String) value;
        String[] p = path.split("/");
        List<IndexableField> fields = new ArrayList<IndexableField>(p.length);
        org.apache.lucene.document.FieldType customType = new org.apache.lucene.document.FieldType();
        customType.setIndexed(field.indexed());
        customType.setTokenized(field.isTokenized());
        customType.setStored(field.stored());
        customType.setOmitNorms(field.omitNorms());
        customType.setStoreTermVectors(field.storeTermVector());
        customType.setStoreTermVectorOffsets(field.storeTermOffsets());
        customType.setStoreTermVectorPositions(field.storeTermPositions());

        int i = 0;
        for (String s : p) {
            if (s == null || "".equals(s)) {
                s = "empty";
            }
            fields.add(this.createField(field.getName() + i, s, customType, 1f));
            i++;
        }

        return fields;
    }


    @Override
    public void write(TextResponseWriter writer, String name, IndexableField f) throws IOException {
        writer.writeStr(name, f.stringValue(), true);
    }

    @Override
    public SortField getSortField(SchemaField field, boolean top) {
        return getStringSort(field, top);
    }
}

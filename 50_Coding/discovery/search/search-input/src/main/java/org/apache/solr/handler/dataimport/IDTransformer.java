
package org.apache.solr.handler.dataimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class
        IDTransformer extends Transformer {
    private static final Logger LOG = LoggerFactory
            .getLogger(IDTransformer.class);

    @Override
    @SuppressWarnings("unchecked")
    public Object transformRow(Map<String, Object> aRow, Context context) {
        aRow.put("id", UUID.randomUUID());

        return aRow;
    }

}

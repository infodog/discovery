
package org.apache.solr.handler.dataimport.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.handler.dataimport.SolrWriter;

public class ConfigNameConstants {
    public static final String SCRIPT = "script";

    public static final String NAME = "name";

    public static final String PROCESSOR = "processor";

    /**
     * @deprecated use IMPORTER_NS_SHORT instead
     */
    @Deprecated
    public static final String IMPORTER_NS = "dataimporter";

    public static final String IMPORTER_NS_SHORT = "dih";

    public static final String ROOT_ENTITY = "rootEntity";

    public static final String FUNCTION = "function";

    public static final String CLASS = "class";

    public static final String DATA_SRC = "dataSource";

    public static final Set<String> RESERVED_WORDS;

    static {
        Set<String> rw = new HashSet<String>();
        rw.add(IMPORTER_NS);
        rw.add(IMPORTER_NS_SHORT);
        rw.add("request");
        rw.add("delta");
        rw.add("functions");
        rw.add("session");
        rw.add(SolrWriter.LAST_INDEX_KEY);
        RESERVED_WORDS = Collections.unmodifiableSet(rw);
    }
}

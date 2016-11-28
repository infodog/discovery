package org.apache.solr.handler.dataimport;

import java.util.Map;

/**
 * <p>
 * Use this API to implement a custom transformer for any given entity
 * </p>
 * <p/>
 * <p>
 * Implementations of this abstract class must provide a public no-args constructor.
 * </p>
 * <p/>
 * <p>
 * Refer to <a
 * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * </p>
 * <p/>
 * <b>This API is experimental and may change in the future.</b>
 *
 * @since solr 1.3
 */
public abstract class Transformer {
    /**
     * The input is a row of data and the output has to be a new row.
     *
     * @param context The current context
     * @param row     A row of data
     * @return The changed data. It must be a {@link java.util.Map}&lt;{@link String}, {@link Object}&gt; if it returns
     *         only one row or if there are multiple rows to be returned it must
     *         be a {@link java.util.List}&lt;{@link java.util.Map}&lt;{@link String}, {@link Object}&gt;&gt;
     */
    public abstract Object transformRow(Map<String, Object> row, Context context);
}

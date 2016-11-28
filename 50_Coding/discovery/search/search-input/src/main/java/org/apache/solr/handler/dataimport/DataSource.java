package org.apache.solr.handler.dataimport;

import java.util.Properties;

/**
 * <p>
 * Provides data from a source with a given query.
 * </p>
 * <p/>
 * <p>
 * Implementation of this abstract class must provide a default no-arg constructor
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
public abstract class DataSource<T> {

    /**
     * Initializes the DataSource with the <code>Context</code> and
     * initialization properties.
     * <p/>
     * This is invoked by the <code>DataImporter</code> after creating an
     * instance of this class.
     *
     * @param context
     * @param initProps
     */
    public abstract void init(Context context, Properties initProps);

    /**
     * Get records for the given query.The return type depends on the
     * implementation .
     *
     * @param query The query string. It can be a SQL for JdbcDataSource or a URL
     *              for HttpDataSource or a file location for FileDataSource or a custom
     *              format for your own custom DataSource.
     * @return Depends on the implementation. For instance JdbcDataSource returns
     *         an Iterator&lt;Map &lt;String,Object&gt;&gt;
     */
    public abstract T getData(String query);

    /**
     * Cleans up resources of this DataSource after use.
     */
    public abstract void close();
}

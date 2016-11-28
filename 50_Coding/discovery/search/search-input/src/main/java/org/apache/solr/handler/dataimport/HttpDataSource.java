
package org.apache.solr.handler.dataimport;

/**
 * <p>
 * A data source implementation which can be used to read character files using
 * HTTP.
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
 * @deprecated use {@link org.apache.solr.handler.dataimport.URLDataSource} instead
 */
@Deprecated
public class HttpDataSource extends URLDataSource {

}

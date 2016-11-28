package org.apache.solr.handler.dataimport;

/**
 * Event listener for DataImportHandler
 * <p/>
 * <b>This API is experimental and subject to change</b>
 *
 * @since solr 1.4
 */
public interface EventListener {

    /**
     * Event callback
     *
     * @param ctx the Context in which this event was called
     */
    public void onEvent(Context ctx);

}

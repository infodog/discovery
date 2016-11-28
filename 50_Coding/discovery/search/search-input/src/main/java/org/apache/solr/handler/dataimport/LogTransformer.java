package org.apache.solr.handler.dataimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A {@link Transformer} implementation which logs messages in a given template format.
 * <p/>
 * Refer to <a href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * <p/>
 * <b>This API is experimental and may change in the future.</b>
 *
 * @since solr 1.4
 */
public class LogTransformer extends Transformer {
    Logger LOG = LoggerFactory.getLogger(LogTransformer.class);

    @Override
    public Object transformRow(Map<String, Object> row, Context ctx) {
        String expr = ctx.getEntityAttribute(LOG_TEMPLATE);
        String level = ctx.replaceTokens(ctx.getEntityAttribute(LOG_LEVEL));

        if (expr == null || level == null) return row;

        if ("info".equals(level)) {
            if (LOG.isInfoEnabled())
                LOG.info(ctx.replaceTokens(expr));
        } else if ("trace".equals(level)) {
            if (LOG.isTraceEnabled())
                LOG.trace(ctx.replaceTokens(expr));
        } else if ("warn".equals(level)) {
            if (LOG.isWarnEnabled())
                LOG.warn(ctx.replaceTokens(expr));
        } else if ("error".equals(level)) {
            if (LOG.isErrorEnabled())
                LOG.error(ctx.replaceTokens(expr));
        } else if ("debug".equals(level)) {
            if (LOG.isDebugEnabled())
                LOG.debug(ctx.replaceTokens(expr));
        }

        return row;
    }

    public static final String LOG_TEMPLATE = "logTemplate";
    public static final String LOG_LEVEL = "logLevel";
}

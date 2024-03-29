package org.apache.solr.handler.dataimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.util.Properties;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;


/**
 * This can be useful for users who have a DB field containing BLOBs which may be Rich documents
 * <p/>
 * The datasouce may be configured as follows
 * <p/>
 * &lt;dataSource name="f1" type="FieldStreamDataSource" /&gt;
 * <p/>
 * The entity which uses this datasource must keep and attribute dataField
 * <p/>
 * The fieldname must be resolvable from {@link VariableResolver}
 * <p/>
 * This may be used with any {@link org.apache.solr.handler.dataimport.EntityProcessor} which uses a {@link org.apache.solr.handler.dataimport.DataSource}&lt;{@link java.io.InputStream}&gt; eg: TikaEntityProcessor
 * <p/>
 *
 * @since 3.1
 */
public class FieldStreamDataSource extends DataSource<InputStream> {
    private static final Logger LOG = LoggerFactory.getLogger(FieldReaderDataSource.class);
    protected VariableResolver vr;
    protected String dataField;
    private EntityProcessorWrapper wrapper;

    @Override
    public void init(Context context, Properties initProps) {
        dataField = context.getEntityAttribute("dataField");
        wrapper = (EntityProcessorWrapper) context.getEntityProcessor();
    /*no op*/
    }

    @Override
    public InputStream getData(String query) {
        Object o = wrapper.getVariableResolver().resolve(dataField);
        if (o == null) {
            throw new DataImportHandlerException(SEVERE, "No field available for name : " + dataField);
        }
        if (o instanceof Blob) {
            Blob blob = (Blob) o;
            try {
                //Most of the JDBC drivers have getBinaryStream defined as public
                // so let us just check it
                Method m = blob.getClass().getDeclaredMethod("getBinaryStream");
                if (Modifier.isPublic(m.getModifiers())) {
                    return (InputStream) m.invoke(blob);
                } else {
                    // force invoke
                    m.setAccessible(true);
                    return (InputStream) m.invoke(blob);
                }
            } catch (Exception e) {
                LOG.info("Unable to get data from BLOB");
                return null;

            }
        } else if (o instanceof byte[]) {
            byte[] bytes = (byte[]) o;
            return new ByteArrayInputStream(bytes);
        } else {
            throw new RuntimeException("unsupported type : " + o.getClass());
        }

    }

    @Override
    public void close() {
    }
}

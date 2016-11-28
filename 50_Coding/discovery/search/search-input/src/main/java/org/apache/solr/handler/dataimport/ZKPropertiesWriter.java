package org.apache.solr.handler.dataimport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.apache.solr.common.cloud.SolrZkClient;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKPropertiesWriter implements DIHPropertiesWriter {

    private static final Logger log = LoggerFactory
            .getLogger(ZKPropertiesWriter.class);

    private String path;
    private SolrZkClient zkClient;

    @Override
    public void init(DataImporter dataImporter) {
        String collection = dataImporter.getCore().getCoreDescriptor()
                .getCloudDescriptor().getCollectionName();
        String persistFilename;
        if (dataImporter.getHandlerName() != null) {
            persistFilename = dataImporter.getHandlerName() + ".properties";
        } else {
            persistFilename = SimplePropertiesWriter.IMPORTER_PROPERTIES;
        }
        path = "/configs/" + collection + "/" + persistFilename;
        zkClient = dataImporter.getCore().getCoreDescriptor().getCoreContainer()
                .getZkController().getZkClient();
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void persist(Properties props) {
        Properties existing = readIndexerProperties();
        existing.putAll(props);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            existing.store(output, "");
            byte[] bytes = output.toByteArray();
            if (!zkClient.exists(path, false)) {
                try {
                    zkClient.makePath(path, false);
                } catch (NodeExistsException e) {
                }
            }
            zkClient.setData(path, bytes, false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(
                    "Could not persist properties to " + path + " :" + e.getClass(), e);
        } catch (Exception e) {
            log.warn(
                    "Could not persist properties to " + path + " :" + e.getClass(), e);
        }
    }

    @Override
    public Properties readIndexerProperties() {
        Properties props = new Properties();
        try {
            byte[] data = zkClient.getData(path, null, null, false);
            if (data != null) {
                ByteArrayInputStream input = new ByteArrayInputStream(data);
                props.load(input);
            }
        } catch (Throwable e) {
            log.warn(
                    "Could not read DIH properties from " + path + " :" + e.getClass(), e);
        }
        return props;
    }
}

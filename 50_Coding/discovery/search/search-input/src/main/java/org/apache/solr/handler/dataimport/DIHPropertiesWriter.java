package org.apache.solr.handler.dataimport;


import java.util.Properties;

public interface DIHPropertiesWriter {

    public void init(DataImporter dataImporter);

    public boolean isWritable();

    public void persist(Properties props);

    public Properties readIndexerProperties();

}

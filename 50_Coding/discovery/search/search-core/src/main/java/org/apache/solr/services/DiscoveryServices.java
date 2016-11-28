package org.apache.solr.services;

import org.apache.solr.core.CoreContainer;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/15/13
 * Time: 3:46 PM
 */
public class DiscoveryServices {

    public static CoreContainer coreContainer;
    public static void init(){
        CoreContainer cores = new CoreContainer();
        cores.load();
        coreContainer =  cores;
    }

}

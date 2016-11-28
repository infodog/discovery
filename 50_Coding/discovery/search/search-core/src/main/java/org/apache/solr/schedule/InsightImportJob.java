package org.apache.solr.schedule;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.services.DiscoveryServices;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/10/13
 * Time: 10:42 AM
 */
public class InsightImportJob implements Job {
    protected CoreContainer coreContainer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //SolrCore insight = DiscoveryServies.coreContainer.getCore("insight_admin");
        if (DiscoveryServices.coreContainer!=null) {
            String arg = System.getProperty("insight.data.url");
            System.out.println("in InsightImportJob,the insight.data.url=" + arg);
            String dataUrl;
            if (arg == null || "".equals(arg)) {
                dataUrl = "http://127.0.0.1:8080/discovery-search-web/insight_admin/dataimport?command=full-import&clean=false";
            } else {
                dataUrl = arg + "/discovery-search-web/insight_admin/dataimport?command=full-import&clean=false";
            }

            try {
                URL url = new URL(dataUrl);
                url.openConnection();
                System.out.println(url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

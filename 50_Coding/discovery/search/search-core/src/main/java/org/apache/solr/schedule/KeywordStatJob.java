package org.apache.solr.schedule;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.params.TrackParams;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.CommitUpdateCommand;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * User: benzhao
 * Date: 11/17/12
 * Time: 3:29 PM
 */
public class KeywordStatJob implements Job {
    protected CoreContainer coreContainer;
    private final static SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyyMMdd");


    public void setCoreContainer(CoreContainer coreContainer) {
        this.coreContainer = coreContainer;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("begin analyzing keywords");

        for (SolrCore solrCore : this.coreContainer.getCores()) {

            try {
                Map params = new HashMap();
                params.put(TrackParams.TRACK_STAT, "true");

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                System.out.println(calendar.getTime());
                System.out.println(solrCore.getName());
                params.put(TrackParams.TRACK_STAT_DATE, dayDateFormat.format(calendar.getTime()));

                SolrQueryRequest req = new LocalSolrQueryRequest(solrCore, "", null, 0, 1, params);
                SolrQueryResponse rsp = new SolrQueryResponse();

                solrCore.getRequestHandler("/select").handleRequest(req, rsp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

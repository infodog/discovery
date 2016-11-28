package org.apache.solr.schedule;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.update.CommitUpdateCommand;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

/**
 * User: benzhao
 * Date: 11/17/12
 * Time: 3:29 PM
 */
public class OptimizeIndexJob implements Job {
    protected CoreContainer coreContainer;

    public void setCoreContainer(CoreContainer coreContainer) {
        this.coreContainer = coreContainer;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("begin optimizing indexes");
        CommitUpdateCommand cuc = new CommitUpdateCommand(null,true);

        for (SolrCore solrCore : this.coreContainer.getCores()) {

            try {
                solrCore.getUpdateHandler().commit(cuc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

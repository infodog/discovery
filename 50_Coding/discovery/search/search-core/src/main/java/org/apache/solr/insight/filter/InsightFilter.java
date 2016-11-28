package org.apache.solr.insight.filter;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/1/13
 * Time: 11:23 AM
 */
public abstract class InsightFilter {
    private String command;

    protected InsightFilter(String command) {
        this.command = command;
    }

    public abstract InterResult process(InterResult interResult) throws Exception;

}

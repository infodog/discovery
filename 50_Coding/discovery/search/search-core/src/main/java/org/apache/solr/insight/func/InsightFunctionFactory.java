package org.apache.solr.insight.func;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/2/13
 * Time: 10:07 AM
 */
public class InsightFunctionFactory {
    public static InsightFunction create(String function, String field) throws Exception {
        InsightFunction f = null;
        if ("count".equals(function)) {
            f = new CountInsightFunction(field);
        } else if ("dc".equals(function)) {
            f = new DistinctInsightFunction(field);
        } else if ("dcount".equals(function)) {
            f = new DistinctInsightFunction(field);
        } else if ("sum".equals(function)) {
            f = new SumInsightFunction(field);
        } else if ("avg".equals(function)) {
            f = new AverageInsightFunction(field);
        } else if ("max".equals(function)) {
            f = new MaxInsightFunction(field);
        } else if ("min".equals(function)) {
            f = new MinInsightFunction(field);
        }else {
            throw new Exception("not supported function");
        }
        return f;
    }
}

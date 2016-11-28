package net.xinshi.discovery.search.mgt.services;

import net.xinshi.discovery.search.mgt.bean.SearchJob;
import org.apache.solr.params.InsightParams;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/27/13
 * Time: 2:45 PM
 */
public class DQLParser {
    // command(arg) as alias group by arg as alias
    //{1}({2}) as {3} group by {4} as {5}
    public static Pattern STATS_COMMAND_PATTERN = Pattern.compile("(.+)\\((.+)\\) as (.+) group by (.+) as (.+)");

    // function(arg)
    //{1}({2})
    public static Pattern STATS_FUNCTION_PATTERN = Pattern.compile("(.+)\\((.+)\\)");

    // function(arg) as alias
    //{1}({2}) as {3}
    public static Pattern STATS_FUNCTION_ALIAS_PATTERN = Pattern.compile("(.+)\\((.+)\\) as (.+)");


    //timefield(start=arg end=arg gap=arg)
    //{1}(start={2} end={3} gap={4})
    public static Pattern STATS_RANGE_FACET_PATTERN = Pattern.compile("(.+)\\(start=(.+) end=(.+) gap=(.+)\\)");

    //Value Range count(id, range=[min..max])
    public static Pattern STATS_VALUE_RANGE_PATTERN = Pattern.compile("(.+),range=\\[(.+)\\.\\.(.+)\\]");

    public static Pattern STATS_MULTI_FUNCTIONS_PATTERN = Pattern.compile("stat (.+) group by (.+) as (.+)");

    //drill down
    public static Pattern DRILLDOWN_PATTERN = Pattern.compile("(.+) group by (.+)_hierarchy([0-9]+) as (.+)");

    public Map parse(SearchJob job, String owner) {
        String query = job.getSearch();
        Map params = new HashMap();
        params.put("wt", "xml");

//        List<String> fields = new ArrayList<String>();
        String q = null;
        String fieldCollector = null;
        String pipe = null;
        String[] chart_fields = null;
        String[] cfs = query.split("\\|\\^");
        if (cfs.length >= 2) {
            query = cfs[0].trim();
            String postPipe = cfs[1].trim();
            if (postPipe.startsWith("chart field")) {
                chart_fields = postPipe.substring(11).split(",");
                for (int i = 0; i < chart_fields.length; i++) {
                    chart_fields[i] = chart_fields[i].trim();
                }
            }
        }

        if (query.indexOf("|") < 0) {
            query = query + "| field content_store";
        }

        String[] lines = query.split("\\|");

        if (lines.length >= 2) {
            q = lines[0].trim();
            fieldCollector = lines[1].trim().substring(5).trim();

            int i = query.indexOf("|", query.indexOf("|") + 1);
            if (i > 0) {
                pipe = query.substring(i + 1).trim();
            }
        } else {
            q = query;
        }

        if (q.startsWith("search")) {
            q = q.substring(6);
        }

        String preQ = q;
        q = "{!lucene q.op=AND df=content_text}" + q;
        params.put("q", q);

        String fq = "";

        //System.out.println("Owner : " + owner);
        if (!"admin".equals(owner)) {

            String user = owner;
            String mid = null;
            String[] line = owner.split("::");
            if (line != null && line.length >= 2) {
                user = line[0].trim();//saasId
                mid = line[1].trim();//merchantId
            }

            fq = "insight_merchant:" + user;

            if (mid != null) {
                fq = fq + " merchantId:" + mid;
            }

            System.out.println("user = " + user + " mid= " + mid);


        }

        boolean timerange = job.getEarliest_time() != null && !"".equals(job.getEarliest_time())
                && !"0".equals(job.getEarliest_time())
                && job.getLatest_time() != null && !"".equals(job.getLatest_time());

        if (timerange) {
            fq = fq + " create-time:[" + job.getEarliest_time() + " TO " + job.getLatest_time() + "]";
        }

        if (fq != null && !"".equals(fq)) {
            params.put("fq", fq);
        }

        //drilldown
        try {
            if (pipe != null && !"".equals(pipe.trim())) {
                Matcher dp = DRILLDOWN_PATTERN.matcher(pipe);
                if (dp.matches()) {
                    String field = dp.group(2);
                    int h = Integer.parseInt(dp.group(3));
                    job.setCurrent_hierarchy(preQ + " " + field + "_hierarchy" + h);
                    job.setNext_hierarchy(field + "_hierarchy" + (h + 1));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (fieldCollector != null) {
            params.put(InsightParams.INSIGHT, true);
            params.put(InsightParams.INSIGHT_FIELD, fieldCollector.trim());

            if (pipe != null) {
                params.put(InsightParams.INSIGHT_PIPE, pipe);
            }

            if (chart_fields != null && chart_fields.length > 0) {
                params.put(InsightParams.INSIGHT_CHART_FIELDS, chart_fields);
            }
        }

        return params;
    }
}

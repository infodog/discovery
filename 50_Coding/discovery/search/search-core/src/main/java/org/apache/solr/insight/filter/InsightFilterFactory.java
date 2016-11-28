package org.apache.solr.insight.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/4/13
 * Time: 3:10 PM
 */
public class InsightFilterFactory {

    public static List<InsightFilter> parseFilters(String pipe) {
        List<InsightFilter> filters = new ArrayList<InsightFilter>();
        if (pipe != null) {
            String[] ps = pipe.split("\\|");
            for (String p : ps) {
                p = p.trim();
                if (p.startsWith("stat")) {
                    InsightFilter filter = new StatInsightFilter(p);
                    filters.add(filter);
                } else if (p.startsWith("sort")) {
                    InsightFilter filter = new SortInsightFilter(p);
                    filters.add(filter);
                } else if (p.startsWith("range")) {
                    InsightFilter filter = new RangeInsightFilter(p);
                    filters.add(filter);
                } else if (p.startsWith("percentage")) {
                    InsightFilter filter = new PercentageInsightFilter(p);
                    filters.add(filter);
                }  else if (p.startsWith("divide")) {
                    InsightFilter filter = new DivideInsightFilter(p);
                    filters.add(filter);
                }
                else if (p.startsWith("where")) {
                    InsightFilter filter = new WhereFilter(p);
                    filters.add(filter);
                }
            }
        }

        return filters;
    }
}

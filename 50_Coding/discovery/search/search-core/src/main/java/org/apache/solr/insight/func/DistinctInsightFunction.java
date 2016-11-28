package org.apache.solr.insight.func;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/2/13
 * Time: 10:12 AM
 */
public class DistinctInsightFunction extends InsightFunction {
    private Map<String, Boolean> distinct = new HashMap<String, Boolean>();

    protected DistinctInsightFunction(String field) {
        super(field);
    }

    @Override
    public void accumlate(String value) {
        if (value != null && !"insight_absent".equals(value)) {
            distinct.put(value, true);
        }
    }

    @Override
    public String calc() {
        return String.valueOf(this.distinct.size());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DistinctInsightFunction df = new DistinctInsightFunction(this.getField());
        return df;
    }
}

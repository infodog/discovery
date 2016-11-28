package org.apache.solr.insight.func;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/2/13
 * Time: 10:11 AM
 */
public class CountInsightFunction extends InsightFunction {
    private long count = 0;
    protected CountInsightFunction(String field) {
        super(field);
    }

    @Override
    public void accumlate(String value) {
        if (value != null && !"insight_absent".equals(value)) {
            count ++;
        }
    }

    @Override
    public String calc() {
        return String.valueOf(count);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CountInsightFunction cf = new CountInsightFunction(this.getField());
        return cf;
    }
}

package org.apache.solr.insight.func;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/2/13
 * Time: 10:12 AM
 */
public class AverageInsightFunction extends InsightFunction {
    private BigDecimal sum = new BigDecimal(0);
    private long count = 0;

    protected AverageInsightFunction(String field) {
        super(field);
    }

    @Override
    public void accumlate(String value) {
        if (value != null && !"insight_absent".equals(value)) {
            BigDecimal t = new BigDecimal(value);
            this.sum = this.sum.add(t);
            this.count++;
        }
    }

    @Override
    public String calc() {
        BigDecimal avg = this.sum.divide(new BigDecimal(this.count), 2, RoundingMode.HALF_UP);
        return String.valueOf(avg.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AverageInsightFunction af = new AverageInsightFunction(this.getField());
        return af;
    }
}

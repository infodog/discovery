package org.apache.solr.insight.func;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/2/13
 * Time: 10:11 AM
 */
public class SumInsightFunction extends InsightFunction {
    private BigDecimal sum = new BigDecimal(0);
    protected SumInsightFunction(String field) {
        super(field);
    }

    @Override
    public void accumlate(String value) {
        if (value != null && !"insight_absent".equals(value)) {
            try {
                BigDecimal t = new BigDecimal(value);
                this.sum = this.sum.add(t);
            } catch (Exception e) {
                System.out.println(value);
                e.printStackTrace();
            }
        }
    }

    @Override
    public String calc() {
        return String.valueOf(this.sum.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SumInsightFunction sf = new SumInsightFunction(this.getField());
        return sf;
    }
}

package org.apache.solr.insight.func;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/1/13
 * Time: 5:52 PM
 */
public abstract class InsightFunction {
    private String field;

    protected InsightFunction(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public abstract void accumlate(String value);
    public abstract String calc();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

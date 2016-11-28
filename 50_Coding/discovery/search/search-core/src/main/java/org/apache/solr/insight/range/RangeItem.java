package org.apache.solr.insight.range;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 8/21/13
 * Time: 3:52 PM
 */
public class RangeItem {
    private String start;
    private String end;
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}


package org.apache.solr.insight.fieldFunc.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.insight.fieldFunc.IFieldFunction;

/**
 * Created by Administrator on 2014-08-14.
 */
public class DomainFieldFunction implements IFieldFunction {
    @Override
    public String getValue(String v) {
        if(v==null){
            return null;
        }
        int beginPos = v.indexOf("://") + 3;
        if (beginPos < 3) {
            return v;
        }
        int endPos = v.indexOf("/", beginPos);
        if (beginPos > 0 && endPos > 0) {
            return v.substring(beginPos, endPos);
        } else {
            return v;
        }
    }
}

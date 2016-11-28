package org.apache.solr.insight.fieldFunc;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.insight.fieldFunc.impl.DomainFieldFunction;

/**
 * Created by Administrator on 2014-08-15.
 */
public class FieldFunctionFactory {
    public static IFieldFunction getFunction(String name){
        if(StringUtils.equals("domain",name)){
            return new DomainFieldFunction();
        }
        return null;
    }
}

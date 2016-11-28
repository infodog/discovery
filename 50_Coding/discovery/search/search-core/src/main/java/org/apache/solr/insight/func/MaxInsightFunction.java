package org.apache.solr.insight.func;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2014-07-23.
 */
public class MaxInsightFunction extends InsightFunction {
    static final int  ValueType_Long = 0;
    static final int  ValueType_string = 1;
    int valueType = ValueType_string;
    String stringCurMax = "";
    long longCurMax = -1000000000;
    protected MaxInsightFunction(String field) {
        super(field);
        if(field.startsWith("metric_")){
            valueType = ValueType_Long;
        }

    }

    @Override
    public void accumlate(String value) {
        if(valueType == ValueType_Long){
            try {
                long lValue = Long.parseLong(value);
                if(lValue>longCurMax){
                    longCurMax = lValue;
                }
            }
            catch(Exception e){

            }

        }
        else{
            if(value!=null){
                if(stringCurMax.equals("")){
                    stringCurMax = value;
                }
                else{
                    if(value.compareTo(stringCurMax)>0){
                        stringCurMax = value;
                    }
                }
            }
        }
    }

    @Override
    public String calc() {
        if(valueType == ValueType_Long) {
            return "" + longCurMax;
        }
        else{
            return stringCurMax;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MaxInsightFunction sf = new MaxInsightFunction(this.getField());
        return sf;
    }
}

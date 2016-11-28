package org.apache.solr.insight.func;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2014-07-23.
 */
public class MinInsightFunction extends InsightFunction {
    static final int  ValueType_Long = 0;
    static final int  ValueType_string = 1;
    int valueType = ValueType_string;
    String stringCurMin = "";
    long longCurMin = -1000000000;
    protected MinInsightFunction(String field) {
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
                if(lValue<longCurMin){
                    longCurMin = lValue;
                }
            }
            catch(Exception e){

            }
        }
        else{
            if(value!=null){
                if(stringCurMin.equals("")){
                    stringCurMin = value;
                }
                else{
                    if(value.compareTo(stringCurMin)<0){
                        stringCurMin = value;
                    }
                }
            }
        }
    }

    @Override
    public String calc() {
        if(valueType == ValueType_Long) {
            return "" + longCurMin;
        }
        else{
            return stringCurMin;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MinInsightFunction sf = new MinInsightFunction(this.getField());
        return sf;
    }
}

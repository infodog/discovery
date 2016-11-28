package org.apache.solr.insight.filter;

import org.apache.commons.lang.StringUtils;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Administrator on 2014-09-08.
 */
public class WhereFilter extends InsightFilter {
    Object compiled = null;
    @Override
    public InterResult process(InterResult interResult) throws Exception {
        List<String[]> lines = interResult.getLines();
        Iterator<String[]> itLines = lines.iterator();

        String[] header =  itLines.next();
        String[] varNames = new String[header.length];
        if (header == null) {
            throw new Exception("Table is empty!");
        }

        for(int i=0; i<header.length;i++){
            String name = header[i];
            name = StringUtils.replace(name,".","_");
            name = StringUtils.replace(name,"-","_");
            varNames[i] = name;
        }
        List<String[]> output = new ArrayList();
        output.add(header);
        while(itLines.hasNext()){
            Map vars = new HashMap();
            String[] line = itLines.next();
            for(int i=0; i<header.length; i++){
                vars.put(varNames[i],line[i]);
            }
            Boolean pass = (Boolean) MVEL.executeExpression(compiled, vars);
            if(pass){
                output.add(line);
            }

        }
        InterResult interOutput = new InterResult();
        interOutput.setLines(output);
        interOutput.setTotal(output.size()-1);//去掉header
        return interOutput;
    }

    public WhereFilter(String command) {
        super(command);
        if(StringUtils.startsWith(command,"where")){
            String expr = command.substring("where".length());
            expr = StringUtils.trim(expr);
            compiled = MVEL.compileExpression(expr);
        }
    }
}

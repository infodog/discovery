package org.apache.solr.insight.filter;

import org.apache.solr.insight.func.InsightFunction;
import org.apache.solr.insight.func.InsightFunctionFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/1/13
 * Time: 11:29 AM
 */
public class StatInsightFilter extends InsightFilter {

    public static String STAT_WITHOUT_GROUPBY = "insight_stat";
    public static Pattern STATS_MULTI_FUNCTIONS_PATTERN = Pattern.compile("stat (.+) group by (.+) as (.+)");
    // function(arg) as alias
    //{1}({2}) as {3}
    public static Pattern STATS_FUNCTION_ALIAS_PATTERN = Pattern.compile("(.+)\\((.+)\\) as (.+)");

    private Map<String, Integer> index = new HashMap<String, Integer>();
    private List<String> alias = new ArrayList<String>();
    private ArrayList<InsightFunction> funcs = new ArrayList<InsightFunction>();
    private String groupby;
    private Map<String, Collection<InsightFunction>> results = new LinkedHashMap<String, Collection<InsightFunction>>();


    public StatInsightFilter(String command) {
        super(command);

        if (!command.contains("group by")) {
            command += " group by " + STAT_WITHOUT_GROUPBY + " as " + STAT_WITHOUT_GROUPBY;;
        }

        Matcher multi = STATS_MULTI_FUNCTIONS_PATTERN.matcher(command);
        if (multi.find()) {
            this.groupby = multi.group(2);
            this.alias.add(multi.group(3));
            String[] multifunc = multi.group(1).split(",");

            for (String s : multifunc) {
                Matcher func = STATS_FUNCTION_ALIAS_PATTERN.matcher(s);
                if (func.find()) {
                    try {
                        InsightFunction insightFunction = InsightFunctionFactory.create(func.group(1).trim(), func.group(2).trim());
                        if (insightFunction != null) {
                            this.funcs.add(insightFunction);
                            alias.add(func.group(3));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public InterResult process(InterResult interResult) throws Exception {
        try {
            List<String[]> lines = interResult.getLines();
            Iterator<String[]> itLines = lines.iterator();
            //header
            String[] line = itLines.next();

            if (line == null) {
                throw new Exception("Table is empty!");
            }

            for (int i = 0; i < line.length; i++) {
                index.put(line[i], i);
            }
            Integer groupByIdx = index.get(this.groupby);
            int iGroupByIdx = 0;
            if(groupByIdx==null && !STAT_WITHOUT_GROUPBY.equals(this.groupby)){
                throw new Exception("group by:" + this.groupby + " not found.");
            }
            else{
                if(!STAT_WITHOUT_GROUPBY.equals(this.groupby)) {
                    iGroupByIdx = groupByIdx.intValue();
                }
            }

            //body
            while (itLines.hasNext()) {
                line = itLines.next();
                ArrayList<InsightFunction> list;
                if (STAT_WITHOUT_GROUPBY.equals(this.groupby)) {
                    list = (ArrayList)results.get(STAT_WITHOUT_GROUPBY);
                } else {
                    list = (ArrayList)results.get(line[iGroupByIdx]);
                }

                if (list == null) {
                    list = new ArrayList<InsightFunction>(funcs.size());
                    for (InsightFunction func : funcs) {
                        try {
                            list.add((InsightFunction)func.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (STAT_WITHOUT_GROUPBY.equals(this.groupby)) {
                        results.put(STAT_WITHOUT_GROUPBY, list);
                    } else {
                        results.put(line[index.get(this.groupby)], list);
                    }
                }

                for (InsightFunction insightFunction : list) {
                    String key = insightFunction.getField();
                    Integer ki = this.index.get(key);
                    String value = line[ki];
                    insightFunction.accumlate(value);
                }
            }

            List<String[]> rows = new ArrayList();

            String[] nextline = {};
            rows.add(alias.toArray(nextline));
//            csvWriter.writeNext(alias.toArray(nextline));

            //body
            long total = 0;
            for (Map.Entry<String, Collection<InsightFunction>> entry : results.entrySet()) {
                List<String> row = new ArrayList<String>();
                row.add(entry.getKey());
                for (InsightFunction ifunc : entry.getValue()) {
                    row.add(ifunc.calc());
                }
                rows.add(row.toArray(nextline));
                total++;
            }


            InterResult interOutput = new InterResult();
            interOutput.setLines(rows);
            interOutput.setTotal(total);

            return interOutput;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

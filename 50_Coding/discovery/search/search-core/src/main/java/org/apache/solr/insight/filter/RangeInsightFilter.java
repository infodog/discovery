package org.apache.solr.insight.filter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/5/13
 * Time: 10:49 AM
 */
public class RangeInsightFilter extends InsightFilter {
    public static Pattern SORT_PATTERN = Pattern.compile("range (.+)\\[(.+)\\.\\.(.+)\\]");
    private String field;
    private String low;
    private String high;

    public RangeInsightFilter(String command) {
        super(command);
        Matcher m = SORT_PATTERN.matcher(command);
        if (m.find()) {
            this.field = m.group(1);
            this.low = m.group(2);
            this.high = m.group(3);
        }
    }

    @Override
    public InterResult process(InterResult interResult) throws Exception {
//        CSVReader reader = interResult.getReader();
        List<String[]> lines =interResult.getLines();
        //header
        Iterator<String[]> itLines = lines.iterator();
        String[] header = itLines.next();

        if (header == null) {
            throw new Exception("Table is empty!");
        }
        Map<String, Integer> index = new HashMap<String, Integer>();
        for (int i = 0; i < header.length; i++) {
            index.put(header[i], i);
        }

        Double lowv = Double.valueOf(this.low);
        Double highv = Double.valueOf(this.high);
        int i = index.get(this.field);

        List<String[]> rows = new ArrayList<String[]>();
        //body
        long total = 0;
        String[] line;
        while (itLines.hasNext()) {
            line = itLines.next();
            boolean accept = false;
            Double v = Double.valueOf(line[i]);
            accept = ((v >= lowv) && (v <= highv));
            if (accept) {
                rows.add(line);
                total++;
            }
        }


        InterResult out = new InterResult();
        out.setTotal(total);
        out.setLines(rows);
        return out;
    }
}

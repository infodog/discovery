package org.apache.solr.insight.filter;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/4/13
 * Time: 4:26 PM
 */
public class SortInsightFilter extends InsightFilter {
    public static Pattern SORT_PATTERN = Pattern.compile("sort (.+)\\((.+)\\) (.+)");
    private String field;
    private String type;
    private boolean desc = true;

    public SortInsightFilter(String command) {
        super(command);
        Matcher m = SORT_PATTERN.matcher(command);
        if (m.find()) {
            this.type = m.group(1);
            this.field = m.group(2);
            String order = m.group(3);
            if (order != null && "asc".equals(order.trim())) {
                this.desc = false;
            }
        }
    }

    @Override
    public InterResult process(InterResult interResult) throws Exception {
//        CSVReader reader = interResult.getReader();
        //header
        List<String[]> lines = interResult.getLines();
        Iterator<String[]> itLines = lines.iterator();
        String[] header = itLines.next();

        if (header == null) {
            throw new Exception("Table is empty!");
        }
        Map<String, Integer> index = new HashMap<String, Integer>();
        for (int i = 0; i < header.length; i++) {
            index.put(header[i], i);
        }
        List<String[]> rows = new ArrayList<String[]>();
        //body
        String[] line;
        while (itLines.hasNext()) {
            line = itLines.next();
            rows.add(line);
        }
        Collections.sort(rows, new InsightComparator(index.get(this.field), this.type));

        if (!this.desc) {
            Collections.reverse(rows);
        }

        rows.add(0,header);

        InterResult out = new InterResult();
        out.setTotal(interResult.getTotal());
        out.setLines(rows);
        return out;
    }


    class InsightComparator implements Comparator {
        private int index;
        private String type;

        InsightComparator(int index, String type) {
            this.index = index;
            this.type = type;
        }

        @Override
        public int compare(Object o, Object o2) {
            String[] line = (String[]) o;
            String[] line2 = (String[]) o2;

            if ("double".equals(this.type)) {
                Double d = Double.valueOf(line[this.index]);
                Double d2 = Double.valueOf(line2[this.index]);
                return d2.compareTo(d);
            } else if ("integer".equals(this.type)) {
                Integer v = Integer.valueOf(line[this.index]);
                Integer v2 = Integer.valueOf(line2[this.index]);
                return v2.compareTo(v);
            } else {
                return line2[this.index].compareTo(line[this.index]);
            }
        }
    }
}

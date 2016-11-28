package org.apache.solr.insight.filter;


import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/8/13
 * Time: 11:09 AM
 */
public class PercentageInsightFilter extends InsightFilter {
    public static Pattern PERCENTAGE_PATTERN = Pattern.compile("percentage add (.+),(.+) as (.+)");

    private String numerator;
    private String denominator;
    private String alias;

    protected PercentageInsightFilter(String command) {
        super(command);
        Matcher m = PERCENTAGE_PATTERN.matcher(command);
        if (m.find()) {
            this.numerator = m.group(1).trim();
            this.denominator = m.group(2).trim();
            this.alias = m.group(3).trim();
        }
    }

    @Override
    public InterResult process(InterResult interResult) throws Exception {
//        CSVReader reader = interResult.getReader();
        //header
        List<String[]> lines = interResult.getLines();
        Iterator<String[]> linesIt = lines.iterator();
        String[] header = linesIt.next();

        int columns = header.length + 1;
        String[] newHeader = new String[columns];
        System.arraycopy(header,0,newHeader,0,header.length);
        newHeader[header.length] = alias;

        if (header == null) {
            throw new Exception("Table is empty!");
        }

        if (this.numerator == null || this.denominator == null) {
            throw new Exception("The numerator or denominator is null");
        }

        Map<String, Integer> index = new HashMap<String, Integer>();
        for (int i = 0; i < header.length; i++) {
            index.put(header[i], i);
        }

        int nindex = index.get(this.numerator);
        int dindex = index.get(this.denominator);

        DecimalFormat df = new DecimalFormat("0.00");
        List<String[]> rows = new ArrayList<String[]>();
        //body
        String[] line;
        while (linesIt.hasNext()) {
            line = linesIt.next();
            String[] newLine = new String[columns];
            Double num = Double.valueOf(line[nindex]);
            Double den = Double.valueOf(line[dindex]);
            System.arraycopy(line,0,newLine,0,line.length);
            if (den.longValue() != 0) {
                Double per = num / den;
                newLine[line.length] = String.valueOf(df.format(per * 100)) + "%";
                rows.add(newLine);
            } else {
                newLine[line.length] = "undefined";
                rows.add(newLine);
            }
        }



        InterResult out = new InterResult();
        out.setTotal(interResult.getTotal());
//        out.setReader(output);
        out.setLines(rows);
        return out;

    }
}

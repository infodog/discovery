package org.apache.solr.insight.filter;


import org.junit.Test;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/8/13
 * Time: 11:10 AM
 */
public class PercentageInsightFilterTest {
    @Test
    public void testProcess() throws Exception {
        List<String[]> input = new ArrayList();

        input.add(new String[]{"user", "session", "uuid", "price", "profit"});
        input.add(new String[]{"benzhao", "01", "007", "99.9", "20"});
        input.add(new String[]{"ben", "02", "007", "100", "30"});
        input.add(new String[]{"hekate", "03", "008", "300", "70"});
        input.add(new String[]{"bh", "03", "008", "0.0", "0.0"});



        String command = "percentage add profit,price as Percentage";

        InterResult interResult = new InterResult();
        interResult.setLines(input);
        interResult.setTotal(3);
        InsightFilter sif = InsightFilterFactory.parseFilters(command).get(0);
        InterResult out = sif.process(interResult);
        String[] line;

        List<String[]> result = out.getLines();
        Iterator<String[]> resultIt = result.iterator();

        System.out.println(out.getTotal());

        assertEquals(3, out.getTotal());

        line=resultIt.next();
        assertEquals("user", line[0]);
        assertEquals("session", line[1]);
        assertEquals("uuid", line[2]);
        assertEquals("price", line[3]);
        assertEquals("profit", line[4]);
        assertEquals("Percentage", line[5]);

        line=resultIt.next();
        assertEquals("benzhao", line[0]);
        assertEquals("01", line[1]);
        assertEquals("007", line[2]);
        assertEquals("99.9", line[3]);
        assertEquals("20", line[4]);
        assertEquals("20.02%", line[5]);

        line=resultIt.next();
        assertEquals("ben", line[0]);
        assertEquals("02", line[1]);
        assertEquals("007", line[2]);
        assertEquals("100", line[3]);
        assertEquals("30", line[4]);
        assertEquals("30.00%", line[5]);

        line=resultIt.next();
        assertEquals("hekate", line[0]);
        assertEquals("03", line[1]);
        assertEquals("008", line[2]);
        assertEquals("300", line[3]);
        assertEquals("70", line[4]);
        assertEquals("23.33%", line[5]);

        line=resultIt.next();
        assertEquals("bh", line[0]);
        assertEquals("03", line[1]);
        assertEquals("008", line[2]);
        assertEquals("0.0", line[3]);
        assertEquals("0.0", line[4]);
        assertEquals("undefined", line[5]);


        while (resultIt.hasNext()) {
            line = resultIt.next();
            System.out.println(line[0] + "  " + line[1] + "  " + line[2] + "  " + line[3] + "  " + line[4] + "  " + line[5]);
        }

    }
}

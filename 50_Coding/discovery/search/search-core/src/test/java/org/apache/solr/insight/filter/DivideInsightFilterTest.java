package org.apache.solr.insight.filter;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 10/15/13
 * Time: 3:29 PM
 */
public class DivideInsightFilterTest {
    @Test
    public void testProcess() throws Exception {


        List<String[]> input = new ArrayList();
        input.add(new String[]{"user", "session", "uuid", "price","profit"});
        input.add(new String[]{"benzhao", "01", "007", "500", "5"});
        String command = "divide price,profit as per";

        InterResult interResult = new InterResult();
        interResult.setLines(input);
        interResult.setTotal(3);
        InsightFilter sif = InsightFilterFactory.parseFilters(command).get(0);
        InterResult out = sif.process(interResult);
        String[] line;

        //CSVReader result = out.getReader();
        List<String[]> lines = out.getLines();

        System.out.println(out.getTotal());

        assertEquals(3, out.getTotal());
        Iterator<String[]> lineIt = lines.iterator();
        line=lineIt.next();
        assertEquals("user", line[0]);
        assertEquals("session", line[1]);
        assertEquals("uuid", line[2]);
        assertEquals("price", line[3]);
        assertEquals("profit", line[4]);
        assertEquals("per", line[5]);

        line=lineIt.next();
        assertEquals("benzhao", line[0]);
        assertEquals("01", line[1]);
        assertEquals("007", line[2]);
        assertEquals("500", line[3]);
        assertEquals("5", line[4]);
        assertEquals("100.00", line[5]);


        while (lineIt.hasNext()) {
            line=lineIt.next();
            System.out.println(line[0] + "  " + line[1] + "  " + line[2] + "  " + line[3] + "  " + line[4] + "  " + line[5]);
        }

    }
}

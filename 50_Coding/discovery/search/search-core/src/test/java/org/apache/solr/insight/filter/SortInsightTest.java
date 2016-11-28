package org.apache.solr.insight.filter;


import org.junit.Test;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/4/13
 * Time: 4:29 PM
 */
public class SortInsightTest {
    @Test
    public void testProcess() throws Exception {
        List<String[]> input = new ArrayList();

        input.add(new String[]{"user", "session", "uuid", "price"});
        input.add(new String[]{"benzhao", "01", "007", "99.9"});
        input.add(new String[]{"ben", "02", "007", "100"});
        input.add(new String[]{"hekate", "03", "008", "300"});


        String command = "sort double(price) desc";

        InterResult interResult = new InterResult();
        interResult.setLines(input);
        interResult.setTotal(3);
        InsightFilter sif = new SortInsightFilter(command);
        InterResult out = sif.process(interResult);
        String[] line;

        List<String[]> result = out.getLines();
        Iterator<String[]> itResult = result.iterator();

        System.out.println(out.getTotal());

        assertEquals(3, out.getTotal());

        line=itResult.next();
        assertEquals("user", line[0]);
        assertEquals("session", line[1]);
        assertEquals("uuid", line[2]);
        assertEquals("price", line[3]);

        line=itResult.next();
        assertEquals("hekate", line[0]);
        assertEquals("03", line[1]);
        assertEquals("008", line[2]);
        assertEquals("300", line[3]);


        line=itResult.next();
        assertEquals("ben", line[0]);
        assertEquals("02", line[1]);
        assertEquals("007", line[2]);
        assertEquals("100", line[3]);

        line=itResult.next();
        assertEquals("benzhao", line[0]);
        assertEquals("01", line[1]);
        assertEquals("007", line[2]);
        assertEquals("99.9", line[3]);


        while (itResult.hasNext()) {
            line=itResult.next();
            System.out.println(line[0] + "  " + line[1] + "  " + line[2] + "  " + line[3]);
        }
    }

}

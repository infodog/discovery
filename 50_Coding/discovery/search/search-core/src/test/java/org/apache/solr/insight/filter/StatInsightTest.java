package org.apache.solr.insight.filter;


import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 7/1/13
 * Time: 11:32 AM
 */

public class StatInsightTest {

    @Test
    public void testProcess() throws Exception {
        List<String[]> input = new ArrayList();
        input.add(new String[]{"user", "session", "uuid", "price"});
        input.add(new String[]{"ben", "01", "007", "99.97999"});
        input.add(new String[]{"ben", "02", "007", "100"});
        input.add(new String[]{"hekate", "03", "008", "300"});


        //String DQL = "search * | field user, session, uuid | stat count(session) as PV, dc(uuid) as UV group by user as USER";
        Map<String, String> params = new HashMap<String, String>();

//        params.put("count", "session");
//        params.put("dcount", "uuid");

        String command = "stat count(session) as PV, dc(uuid) as UV, sum(price) as Total Price, avg(price) as Average Price group by user as USER";

        InterResult interResult = new InterResult();
        interResult.setLines(input);
        interResult.setTotal(3);
        StatInsightFilter sif = new StatInsightFilter(command);
        InterResult out = sif.process(interResult);
        String[] line;

        List<String[]> result = out.getLines();
        Iterator<String[]> itResult = result.iterator();

        assertEquals(2, out.getTotal());

        line=itResult.next();

        assertEquals("USER", line[0]);
        assertEquals("PV", line[1]);
        assertEquals("UV", line[2]);
        assertEquals("Total Price", line[3]);
        assertEquals("Average Price", line[4]);

        line=itResult.next();
        assertEquals("ben", line[0]);
        assertEquals("2", line[1]);
        assertEquals("1", line[2]);
        assertEquals("199.98", line[3]);
        assertEquals("99.99", line[4]);


        line=itResult.next();
        assertEquals("hekate", line[0]);
        assertEquals("1", line[1]);
        assertEquals("1", line[2]);
        assertEquals("300.00", line[3]);
        assertEquals("300.00", line[4]);


        line =itResult.next();
        assertNull(line);

        while (itResult.hasNext()) {
            line = itResult.next();
            System.out.println(line[0] + "  " + line[1] + "  " + line[2] + "  " + line[3] + "  " + line[4]);
        }
    }
}

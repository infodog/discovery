package org.apache.solr.insight.range;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 8/21/13
 * Time: 3:55 PM
 */
public class RangeHelperTest {
    @Test
    public void testGetDateRange() throws Exception {
        RangeHelper helper = RangeHelper.getInstance();
        List<RangeItem> items = helper.getDateRange("2013-03-23T00:00:00Z", "2013-03-25T00:00:00Z", "+1DAY");
        assertEquals(2, items.size());
        assertEquals("2013-03-23T00:00:00Z", items.get(0).getStart());
        assertEquals("2013-03-24T00:00:00Z", items.get(0).getEnd());

        assertEquals("2013-03-24T00:00:00Z", items.get(1).getStart());
        assertEquals("2013-03-25T00:00:00Z", items.get(1).getEnd());


        items = helper.getDateRange("2013-03-23T00:00:00Z", "2013-03-27T00:00:00Z", "+2DAY");
        for (RangeItem item : items) {
            System.out.println(item.getStart() + " ... " + item.getEnd());

        }

        assertEquals(2, items.size());
        assertEquals("2013-03-23T00:00:00Z", items.get(0).getStart());
        assertEquals("2013-03-25T00:00:00Z", items.get(0).getEnd());

        assertEquals("2013-03-25T00:00:00Z", items.get(1).getStart());
        assertEquals("2013-03-27T00:00:00Z", items.get(1).getEnd());

    }
}

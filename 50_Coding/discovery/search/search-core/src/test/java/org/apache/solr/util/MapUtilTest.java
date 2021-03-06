package org.apache.solr.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 3/7/13
 * Time: 5:39 PM
 */
public class MapUtilTest {
    @Test
    public void testSortByValue() {
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<String, Integer>(1000);
        for (int i = 0; i < 1000; ++i) {
            testMap.put("SomeString" + random.nextInt(), random.nextInt());
        }

        testMap = MapUtil.sortByValue(testMap);
        Assert.assertEquals(1000, testMap.size());

        Integer previous = null;
        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            Assert.assertNotNull(entry.getValue());
            if (previous != null) {
                Assert.assertTrue(entry.getValue() >= previous);
            }
            previous = entry.getValue();
        }
    }
}

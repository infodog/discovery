package net.xinshi.discovery.search.mgt.services;

import net.xinshi.discovery.search.mgt.bean.SearchJob;
import org.junit.Test;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 1/27/13
 * Time: 3:09 PM
 */
public class DQLParserTest {
    @Test
    public void testParse() throws Exception {
        DQLParser dqlParser = new DQLParser();

        String query = "search type:u";
        SearchJob job = new SearchJob();
        job.setSearch(query);

        Map<String, String> result = dqlParser.parse(job, "admin");
        System.out.println(result.toString());
        assertEquals("{!lucene q.op=AND df=content_text} type:u", result.get("q"));


//        query = "search type:u | stat income";
//        result = dqlParser.parse(query);
//        System.out.println(result.toString());
//        assertEquals("{!lucene q.op=AND df=content_text} type:u", result.get("q"));
//        assertEquals("true", result.get("stats"));
//        assertEquals("income", result.get("stats.field"));
//
//
//        query = "search type:o | stat amount group by member";
//        result = dqlParser.parse(query);
//        System.out.println(result.toString());
//        assertEquals("{!lucene q.op=AND df=content_text} type:o", result.get("q"));
//        assertEquals("true", result.get("stats"));
//        assertEquals("amount", result.get("stats.field"));
//        assertEquals("member", result.get("stats.facet"));
//
//
//        query = "search type:o | facet merchant";
//        result = dqlParser.parse(query);
//        System.out.println(result.toString());
//        assertEquals("{!lucene q.op=AND df=content_text} type:o", result.get("q"));
//        assertEquals("merchant", result.get("facet.field"));

        query = "search type:o | stat amount group by member | chart count";


        //        query = "search type:o | group by member";
    }

    @Test
    public void testReg() throws Exception {
        //Pattern p = Pattern.compile("(.+)\\((.+)\\) as (.+) group by (.+) as (.+)");

        Matcher m = DQLParser.STATS_COMMAND_PATTERN.matcher("sum(metric_purchase-price) as 销售额 group by productName as 商品名称");
        System.out.println(m.groupCount());

        assertEquals(true, m.find());
        assertEquals("sum", m.group(1));
        assertEquals("metric_purchase-price", m.group(2));
        assertEquals("销售额", m.group(3));
        assertEquals("productName", m.group(4));
        assertEquals("商品名称", m.group(5));


        Matcher mtime = DQLParser.STATS_COMMAND_PATTERN.matcher("sum(metric_purchase-price) as 销售额 group by create-time(start=-7day end=now gap=1day) as 时间");

        assertEquals(true, mtime.find());
        assertEquals("sum", mtime.group(1));
        assertEquals("metric_purchase-price", mtime.group(2));
        assertEquals("销售额", mtime.group(3));
        assertEquals("create-time(start=-7day end=now gap=1day)", mtime.group(4));
        assertEquals("时间", mtime.group(5));

        Matcher range = DQLParser.STATS_RANGE_FACET_PATTERN.matcher("create-time(start=-7day end=now gap=1day)");
        assertEquals(true, range.find());
        assertEquals("create-time", range.group(1));
        assertEquals("-7day", range.group(2));
        assertEquals("now", range.group(3));
        assertEquals("1day", range.group(4));

        Matcher valueRange = DQLParser.STATS_VALUE_RANGE_PATTERN.matcher("id,range=[10..500]");
        assertEquals(true, valueRange.find());
        assertEquals("id", valueRange.group(1));
        assertEquals("10", valueRange.group(2));
        assertEquals("500", valueRange.group(3));


        Matcher function = DQLParser.STATS_FUNCTION_PATTERN.matcher("count(id)");
        assertEquals(true, function.find());
        assertEquals("count", function.group(1));
        assertEquals("id", function.group(2));





        String query = "stat count(id) as PV, dcount(uid) as UV, sum(price) as Price group by visit_type as 用户类型";
        Matcher multi = DQLParser.STATS_MULTI_FUNCTIONS_PATTERN.matcher(query);
        assertEquals(true, multi.find());
        assertEquals("count(id) as PV, dcount(uid) as UV, sum(price) as Price", multi.group(1));
        assertEquals("visit_type", multi.group(2));
        assertEquals("用户类型", multi.group(3));

        while (multi.find()) {
            System.out.println("Match \"" + multi.group() + "\" at positions " +
                    multi.start() + "-" + (multi.end() - 1));

            for (int i = 1; i <= multi.groupCount(); i++) {
                System.out.println(multi.group(i));
            }
        }
    }

}

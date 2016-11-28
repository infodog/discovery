package net.xinshi.discovery.search.client;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * User: benzhao
 * Date: 10/18/12
 * Time: 3:07 PM
 */
public class BaseTest {

    @Test
    public void TestMulti() throws JSONException {
        JSONObject docs = new JSONObject();

        docs.put("delete", "1");
        docs.put("delete", "2");

        docs.put("add", "1");
        docs.put("add", "2");

        System.out.println(docs.toString());
    }
}

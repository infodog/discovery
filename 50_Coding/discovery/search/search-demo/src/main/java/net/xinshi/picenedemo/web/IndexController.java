package net.xinshi.picenedemo.web;

import net.xinshi.discovery.search.client.services.SearchServices;
import net.xinshi.discovery.search.client.services.impl.JavaSearchClient;
import net.xinshi.picenedemo.product.Product;
import net.xinshi.picenedemo.product.ProductDocumentBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private SearchServices ss;


    @RequestMapping("/insights")
    @ResponseBody
    public String insights() {
        String objString = "{\"region\":\"地区管理/中国/安徽省/合肥市/庐阳区\",\"metric_order-id\":\"2011091418061240680\",\"userPurchaseType\":\"老会员\", \"merchant\":\"默认商家\",\"order-type\":\"普通订单\",\"create-month\":\"2011-09\",\"metric_user-id\":\"51687\",\"create-time\":\"2013-03-24T18:06:12Z\",\"promotion\":\"促销方式\",\"metric_notCollectPrice\":\"156\",\"order-source\":\"前台订单\",\"order-state\":\"已确认\",\"confirm-time\":\"2013-03-24T18:06:12Z\",\"delivery-mode\":\"全场免运费\",\"id\":\"2011091418061240687\",\"tokenized_user-name\":\"lijunli\",\"metric_total-price\":\"8888\",\"order-paystate\":\"已支付\"," +
                "\"item\":[{\"product-skuAttrs\":\"\",\"metric_total-grossProfit\":0,\"metric_purchase-price\":0,\"tokenized_product-name\":\"赠品爱仕达无烟炒锅\",\"metric_purchase-amount\":1,\"category\":\"商品主分类/家用电器/厨卫家电/电磁炉\",\"product-attrs\":\"商品产地:中国大陆^标题:（赠品）爱仕达无烟炒锅^尺寸:624×364×644（外包装）^生产厂家:爱仕达^毛重（kG）:4\",\"metric_product-totalPrice\":0,\"finish-time\":\"2013-03-24T18:06:12Z\",\"brand\":\"爱仕达\",\"product-vid\":\"\",\"productName\":\"赠品爱仕达无烟炒锅\",\"product-id\":\"p_52178\",\"package-states\":\"已确认,已确认未完成\"}]," +
                "\"logisticsName\":\"\",\"metric_logisticsFee\":\"0\",\"create-day\":\"2011-09-14\",\"merchantId\":\"m_100\",\"pay-mode\":\"货到付款,\",\"metric_collectPrice\":\"18523\"}";

//        String objString = "{\"lastModified-time\":\"2011-12-23T09:40:32Z\",\"isEnable\":\"已激活\",\"birthday-time\":\"1970-01-01T00:00:00Z\",\"create-time\":\"2013-03-27T11:42:35Z\",\"loginId\":\"peng\",\"education\":\"\",\"userCardBindStatus\":\"否\"," +
//                "\"id\":\"50008\",\"metric_orderCount\":\"0\",\"userPurchaseType\":\"新会员\",\"postalCode\":\"\",\"nickName\":\"\",\"lastModifiedUserId\":\"peng\",\"description\":\"\",\"income\":\"\",\"gender\":\"保密\",\"industry\":\"\",\"mobilPhone\":\"\",\"createUserId\":\"root\"," +
//                "\"logo\":\"http://127.0.0.1:8080/upload/2011/09/09/50100.png\",\"recommendedUserId\":\"\",\"marriage\":\"已婚\",\"checkedemailStatus\":\"是\",\"officePhone\":\"\",\"checkedphoneStatus\":\"是\",\"address\":\"\",\"email\":\"peng@q.com\"," +
//                "\"memberGroups\":{\"c_102_group\":{\"create-time\":\"2013-03-27T11:42:35Z\",\"groupName\":\"五星会员\",\"allGroupName\":\"普通会员;三星会员;五星会员;\"}},\"isAdmin\":\"是\",\"realName\":\"peng\"}";
//        String type = "user";
        String type = "order";
        JSONObject obj = null;
        try {
            obj = new JSONObject(objString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray orders = new JSONArray();
        orders.put(obj);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 100
        cm.setDefaultMaxPerRoute(100);
        HttpClient httpClient = new DefaultHttpClient(cm, params);


        URIBuilder builder = new URIBuilder();
        String url = "10.10.10.242:8080";
        builder.setScheme("http").setHost(url).setPath("/collector/import/m100/" + type + "/add")
                .setParameter("objects", orders.toString());


        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(builder.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        StringEntity data = new StringEntity(params, ContentType.create("application/json", "UTF-8"));

//        httpPost.setEntity(data);

        try {
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    String content = EntityUtils.toString(entity, "UTF-8");
                    System.out.println(content);

                } finally {
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "OK";
    }


    @RequestMapping("/index")
    @ResponseBody
    public String index(@RequestParam String id, @RequestParam String title) {
        List<Product> products = new ArrayList<Product>();
        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        products.add(product);
        System.out.println(id + " : " + title);
        ProductDocumentBuilder pdb = new ProductDocumentBuilder(products);

        try {
            this.ss.index(pdb);
        } catch (Exception e) {
            e.printStackTrace();
            return "error" + e.getMessage();
        }

        return "OK";
    }

    @RequestMapping("/indexWithProjectName")
    @ResponseBody
    public String index(@RequestParam String projectName, @RequestParam String id, @RequestParam String title) {
        List<Product> products = new ArrayList<Product>();
        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        products.add(product);
System.out.println(projectName + " : " + id + " : " + title);
        ProductDocumentBuilder pdb = new ProductDocumentBuilder(products);

        try {
            JavaSearchClient
                    javaSearchClient = (JavaSearchClient) this.ss;
            javaSearchClient.setProjectName(projectName);

            this.ss.index(pdb);
        } catch (Exception e) {
            e.printStackTrace();
            return "error" + e.getMessage();
        }

        return "OK";
    }


    @RequestMapping("/reco")
    @ResponseBody
    public String reco() {
        List<Product> products = new ArrayList<Product>();
        Product product = new Product();
        product.setId("doc1");
        product.setTitle("doc1");
        product.setUsers(new ArrayList<String>());
        product.getUsers().add("user1");
        product.getUsers().add("user4");
        product.getUsers().add("user5");

        products.add(product);


        Product product2 = new Product();
        product2.setId("doc2");
        product2.setTitle("doc2");
        product2.setUsers(new ArrayList<String>());
        product2.getUsers().add("user2");
        product2.getUsers().add("user3");
        products.add(product2);


        Product product3 = new Product();
        product3.setId("doc3");
        product3.setTitle("doc3");
        product3.setUsers(new ArrayList<String>());
        product3.getUsers().add("user4");
        products.add(product3);


        Product product4 = new Product();
        product4.setId("doc4");
        product4.setTitle("doc4");
        product4.setUsers(new ArrayList<String>());
        product4.getUsers().add("user4");
        product4.getUsers().add("user5");
        products.add(product4);


        Product product5 = new Product();
        product5.setId("doc5");
        product5.setTitle("doc5");
        product5.setUsers(new ArrayList<String>());
        product5.getUsers().add("user4");
        product5.getUsers().add("user1");
        products.add(product5);


        ProductDocumentBuilder pdb = new ProductDocumentBuilder(products);

        try {
            this.ss.index(pdb);
        } catch (Exception e) {
            e.printStackTrace();
            return "error" + e.getMessage();
        }

        return "OK";
    }


}

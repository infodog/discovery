package net.xinshi.discovery.search.client.services.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.xinshi.discovery.search.client.query.*;
import net.xinshi.discovery.search.client.reco.MatchItem;
import net.xinshi.discovery.search.client.reco.SimilarItem;
import net.xinshi.discovery.search.client.reco.SuggestItem;
import net.xinshi.discovery.search.client.services.*;
import net.xinshi.discovery.search.client.util.MidThreadLocal;
import net.xinshi.discovery.search.client.util.NamePair;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/26/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class JavaSearchClient implements SearchServices {
    public static String INDEX_KEY = "discovery.all.in.one.key";
    private String url;
    private String projectName;
    static HttpClient httpClient;
    private List<String> urls;
    private int idx;

    @Deprecated
    private String defaultIP;

    private boolean debug = false;
    private Cache<String, List<String>> catCaches;
    private Cache<String, List<String>> suggestsCaches;
    private Map<String, Boolean> independentIndexes;
    private Set<String> failedUrls = Collections.synchronizedSet(new HashSet<String>());

    public Map<String, Boolean> getIndependentIndexes() {
        return independentIndexes;
    }

    public void setIndependentIndexes(Map<String, Boolean> independentIndexes) {
        this.independentIndexes = independentIndexes;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getDefaultIP() {
        return defaultIP;
    }

    public void setDefaultIP(String defaultIP) {
        this.defaultIP = defaultIP;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public JavaSearchClient() {
        super();
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(200);
        // Increase default max connection per route to 100
        cm.setDefaultMaxPerRoute(100);

        httpClient = new DefaultHttpClient(cm, params);

        catCaches = CacheBuilder.newBuilder()
                .maximumSize(100000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

        suggestsCaches = CacheBuilder.newBuilder()
                .maximumSize(100000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

        this.independentIndexes = new HashMap<String, Boolean>();
        startCheckServerThread();

    }

    synchronized void reAlive(List<String> aliveAgain){
        this.urls.removeAll(failedUrls);
        this.urls.addAll(aliveAgain);
        failedUrls.removeAll(aliveAgain);
    }
    private void checkFailedUrls(){
        IndexItem item = getSearchName("product");
        List<String> aliveAgain = new ArrayList();
        List<String> reallyDie = new ArrayList();
        if(failedUrls.size()==0){
            return;
        }
        for(String failedUrl : failedUrls){
            System.out.println("checked failedUrl=" + failedUrl);
            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost(failedUrl).setPath("/" + item.getIndexName().toLowerCase() + "/select")
                    .setParameter("q", "abc");
            try {
//                System.out.println("Http going to get.....");
                HttpGet httpGet = new HttpGet(builder.build());
                HttpResponse response = httpClient.execute(httpGet);
//                System.out.println("Http has sent....." + builder.build().toString());
                HttpEntity entity = response.getEntity();


                if (entity != null) {
                    String content = EntityUtils.toString(entity, "UTF-8");

                    try {
//                        System.out.println(content);
                    } finally {
                        EntityUtils.consume(entity);
                    }
                }
//                System.out.println("checked failedUrl=" + failedUrl + " is ok again.");
                aliveAgain.add(failedUrl);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        reAlive(aliveAgain);
    }


    private void startCheckServerThread(){
        new Thread(new Runnable(){
            public void run(){
                while(true) {
                    try {
                        checkFailedUrls();
//                        System.out.println("checking failed urls");
                        Thread.sleep(2000);
                    } catch (Throwable t) {
                    }
                }
            }
        }).start();
    }


    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
        String urls[] = url.split(",");
        this.urls = new Vector();
        for(String u : urls){
            this.urls.add(u);
        }
    }

    private SearchResults searchByUrl(String url,SearchArgs args) throws Exception{
        long beginSearch = System.currentTimeMillis();
        String sType = args.getSearchType().toString();
        IndexItem indexItem = getSearchName(sType);
        long beginQuery = System.currentTimeMillis();
        Query query = args.getQueryBuilder().buildQuery(args);
        long endQuery = System.currentTimeMillis();
        if (debug) {
            System.out.println("Query time: " + (endQuery - beginSearch));
        }
        String searchArgs;
        if (indexItem.isInAll()) {
            BooleanQuery bq = new BooleanQuery();
            //不同类型的对象放到同一个索引，用INDEX_KEY区分
            TermQuery tq = new TermQuery(new Term(INDEX_KEY, indexItem.getName()));
            bq.add(tq, BooleanClause.Occur.MUST);
            bq.add(query, BooleanClause.Occur.MUST);
            searchArgs = bq.toString();
        } else {
            searchArgs = query.toString();
        }
        //Collaborative filtering推荐用
        if (args.getFilteringArgs() != null) {
            BooleanQuery bq = new BooleanQuery();
            bq.add(this.collaborativeFiltering(args), BooleanClause.Occur.MUST);
            searchArgs = searchArgs + " " + bq.toString();
        }

        long beginFilter = System.currentTimeMillis();
        String filterArgs = "";
        try {
            filterArgs = args.getQueryBuilder().buildFilter(args).toString();
        } catch (Exception e) {
            System.out.println("Discovery Search Filter error!");
            //e.printStackTrace();
        }
        long endFilter = System.currentTimeMillis();
        if (debug) {
            System.out.println("Filter Time : " + (endFilter - beginSearch));
        }

        if (args.getFilterQuery() != null) {
            filterArgs = filterArgs + " " + args.getFilterQuery().toString();
        }

        //ids
        if (args.getIds() != null && args.getIds().size() > 0) {
            BooleanQuery bq = new BooleanQuery();
            bq.add(this.idsQuery(args.getIds()), BooleanClause.Occur.MUST);
            filterArgs = filterArgs + bq;
        }

        if (args.getNotIds() != null && args.getNotIds().size() > 0) {
            BooleanQuery bq = new BooleanQuery();
            bq.add(this.idsQuery(args.getNotIds()), BooleanClause.Occur.MUST_NOT);
            filterArgs = filterArgs + bq;
        }

        if (args.getInternalType() != null && args.getInternalType().length() > 0) {
            filterArgs = filterArgs + " +stats_self_saas_type:" + args.getInternalType();
        }

        URIBuilder builder = new URIBuilder();


        builder.setScheme("http").setHost(url).setPath("/" + indexItem.getIndexName().toLowerCase() + "/select")
                .setParameter("q", searchArgs)
                .setParameter("wt", args.getWt());


        if (this.debug) {
            System.out.println("Query: " + searchArgs);
        }

        if (filterArgs != null && !"".equals(filterArgs.trim())) {
            builder.setParameter("fq", filterArgs);


            if (this.debug) {
                System.out.println("Filter Query: " + filterArgs);
            }
        }

        builder.setParameter("start", String.valueOf(args.getStartFrom()));
        builder.setParameter("rows", String.valueOf(args.getFetchCount()));

        if (args.getHightlight_field() != null) {
            PiceneTextQuery ptq = new PiceneTextQuery(args.getHightlight_field(), args.getHightlight_keyword());
            builder.setParameter("hl", "true");
            builder.setParameter("hl.q", ptq.toString());
            builder.setParameter("hl.fl", args.getHightlight_field());
            builder.setParameter("hl.simple.pre", "<span class='highlightStyle'>");
            builder.setParameter("hl.simple.post", "</span>");
        }

        if (args.getFacetFields() != null && args.getFacetFields().size() > 0) {
            builder.setParameter("facet", "true");
            builder.setParameter("facet.mincount", "1");
            for (String field : args.getFacetFields()) {
                builder.addParameter("facet.field", field);
            }
        }

        //Track
        if (args.getTrack_keyword() != null && !"".equals(args.getTrack_keyword())) {
            builder.setParameter("track", "true");
            builder.setParameter("track.keyword", args.getTrack_keyword());
            if (args.getTrack_category() != null && !"".equals(args.getTrack_category())) {
                builder.setParameter("track.category", args.getTrack_category());
            }
        }

        //sort
        if (args.getSortFields() != null) {
            SortField[] fields = args.getSortFields();
            StringBuilder sort = new StringBuilder();
            for (SortField field : fields) {
                if (field.getReverse()) {
                    sort.append(field.getField());
                    sort.append(" desc,");
                } else {
                    sort.append(field.getField());
                    sort.append(" asc,");
                }
            }
            builder.setParameter("sort", sort.toString().substring(0, sort.toString().length() - 1));
        }

        parseDQL(args, builder);
        URI uri = builder.build();
        long endUri = System.currentTimeMillis();

        if (isDebug()) {
            System.out.println(uri.toString());
            System.out.println("uri time : " + (endUri - beginSearch));
        }

        HttpGet httpGet = new HttpGet(uri);


        long beginClient = System.currentTimeMillis();
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        long endClient = System.currentTimeMillis();

        if (debug) {
            System.out.println("Client : " + (endClient - beginClient));
        }

        if (entity != null) {
            String content = EntityUtils.toString(entity, "UTF-8");

            try {
                if (isDebug()) {
                    System.out.println(content);
                    long endSearch = System.currentTimeMillis();
                    System.out.println("search time: " + (endSearch - beginSearch));
                }
                if (content != null && content.trim().length() > 0) {
                    return SearchResults.getFromJson(content);
                }
            } finally {
                EntityUtils.consume(entity);
            }
        }

        return null;
    }

    synchronized String getValidUrl(){
        if(this.idx>=this.urls.size()){
            this.idx = 0;
        }
        if(this.urls.size()==0){
            return (String) this.failedUrls.toArray()[0];
        }
        String validUrl = (String) this.urls.get(idx);
        this.idx = this.idx++;
        return validUrl;
    }

    public SearchResults search(SearchArgs args) throws Exception {
        String validUrl = getValidUrl();
        try{
            return searchByUrl(validUrl,args);
        }
        catch(Exception e){
            this.failedUrls.add(validUrl);
            return null;
        }
    }

    private void parseDQL(SearchArgs args, URIBuilder builder) {
        String dql = args.getInsight_dql();
        if (dql != null && !"".equals(dql)) {
            builder.setParameter("start", "0");
            builder.setParameter("rows", "1");

            String fields = null;
            String pipe = null;
            if (dql.startsWith("field")) {
                fields = dql.substring(5, dql.indexOf("|")).trim();
                pipe = dql.substring(dql.indexOf("|") + 1).trim();
            }
            builder.setParameter("discovery.insight", "true");
            builder.setParameter("discovery.insight.field", fields);
            builder.setParameter("discovery.insight.pipe", pipe);
            builder.setParameter("discovery.insight.offset", String.valueOf(args.getInsigit_dql_offset()));
            builder.setParameter("discovery.insight.limit", String.valueOf(args.getInsight_dql_limit()));
            return;
        }

        //stats deprecated
        if (args.getSumFields() != null && args.getSumFields().size() > 0) {

            builder.setParameter("start", "0");
            builder.setParameter("rows", "1");


            builder.setParameter("discovery.insight", "true");
            StringBuilder fields = new StringBuilder();
            StringBuilder stat = new StringBuilder();

            for (SumArg sumArg : args.getSumFields()) {

                if (fields.length() == 0) {
                    fields.append(sumArg.getFieldName());
                } else {
                    fields.append(",");
                    fields.append(sumArg.getFieldName());
                }

                if (stat.length() == 0) {
                    stat.append(" | stat ");
                    stat.append(sumArg.getType());
                    stat.append("(");
                    stat.append(sumArg.getFieldName());
                    stat.append(")");
                    stat.append(" as ");
                    stat.append(sumArg.getFieldName());
                } else {
                    stat.append(",");
                    stat.append(sumArg.getType());
                    stat.append("(");
                    stat.append(sumArg.getFieldName());
                    stat.append(")");
                    stat.append(" as ");
                    stat.append(sumArg.getFieldName());
                }

            }

            StringBuilder pipe = new StringBuilder();
            pipe.append(stat.toString());

            builder.setParameter("discovery.insight.field", fields.toString());
            builder.setParameter("discovery.insight.pipe", pipe.toString());
        }


        if (args.getSumFacetFields() != null && args.getSumFacetFields().size() > 0) {
            builder.setParameter("start", "0");
            builder.setParameter("rows", "1");

            builder.setParameter("discovery.insight", "true");
            FacetSumArg facetSumArg = args.getSumFacetFields().get(0);
            String offset = "0";
            String limit = "50";
            String facet = facetSumArg.getFacetField();
            StringBuilder fields = new StringBuilder();
            fields.append(facet);

            StringBuilder stat = new StringBuilder();
            List<SumArg> sumFields = facetSumArg.getSumFields();

            for (SumArg sumArg : sumFields) {

                fields.append(",");
                fields.append(sumArg.getFieldName());

                if (stat.length() == 0) {
                    stat.append(" | stat ");
                    stat.append(sumArg.getType());
                    stat.append("(");
                    stat.append(sumArg.getFieldName());
                    stat.append(")");
                    stat.append(" as ");
                    stat.append(sumArg.getFieldName());
                } else {
                    stat.append(",");
                    stat.append(sumArg.getType());
                    stat.append("(");
                    stat.append(sumArg.getFieldName());
                    stat.append(")");
                    stat.append(" as ");
                    stat.append(sumArg.getFieldName());
                }

            }


            offset = String.valueOf(facetSumArg.getOffset());
            limit = String.valueOf(facetSumArg.getLimit());

            StringBuilder pipe = new StringBuilder();
            pipe.append(stat.toString());
            pipe.append(" group by ");
            pipe.append(facet);
            pipe.append(" as ");
            pipe.append(facet);


            builder.setParameter("discovery.insight.field", fields.toString());
            builder.setParameter("discovery.insight.pipe", pipe.toString());
            builder.setParameter("discovery.insight.offset", offset);
            builder.setParameter("discovery.insight.limit", limit);
        }
    }

    private Query idsQuery(List<String> ids) {
        BooleanQuery idsQuery = new BooleanQuery();
        for (String id : ids) {
            Query query = new TermQuery(new Term("id", id));
            idsQuery.add(query, BooleanClause.Occur.SHOULD);
        }
        return idsQuery;
    }

    //推荐用
    private Query collaborativeFiltering(SearchArgs args) {
        List<NamePair> filtering = args.getFilteringArgs();
        BooleanQuery filteringItems = new BooleanQuery();
        try {
            for (NamePair namePair : args.getFilteringArgs()) {
                Query query = new TermQuery(new Term(args.getFilterFiled(), namePair.getName()));
                query.setBoost(Float.valueOf(namePair.getValue()));
                filteringItems.add(query, BooleanClause.Occur.SHOULD);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filteringItems;
    }

    public String getIndexName(String searchType){
        IndexItem item = getSearchName(searchType);
        return item.getIndexName();
    }

    public IndexItem getIndexItem(String searchType){
        return getSearchName(searchType);
    }

    private IndexItem getSearchName(String searchType) {
        String mid = null;//其实是saasId
        boolean isSaas = false;
        try {
            mid = MidThreadLocal.get();
            if (mid != null) {
                isSaas = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder name = new StringBuilder();

        name.append(searchType);

        if (this.getProjectName() != null && !"".equals(this.getProjectName().trim())) {
            name.append("_");
            name.append(this.getProjectName());
        }

        if (mid != null && !"".equals(mid.trim())) {
            name.append("_");
            name.append(mid);
        }

//        try {
//            if (defaultIP != null && !"".equals(defaultIP.trim())) {
//                name.append("_");
//                String tip = defaultIP.replace(".", "");
//                name.append(tip);
//            } else {
//                InetAddress addr = InetAddress.getLocalHost();
//                String ip = addr.getHostAddress();
//
//                if (ip != null && !"".equals(ip)) {
//                    name.append("_");
//                    ip = ip.replace(".", "");
//                    name.append(ip);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        IndexItem item = new IndexItem();
        item.setSaas(isSaas);

        Boolean flag = this.independentIndexes.get(searchType);
        if (isSaas && (flag == null || flag == false)) {
            item.setInAll(true);
            item.setIndexName("all_in_one");
            item.setName(name.toString());
        } else {
            item.setIndexName(name.toString());
        }
        return item;
    }

    private void indexToUrl(String url,DocumentBuilder builder) throws Exception{

//        String sType = builder.getSearchType().toString();
        String sType = builder.getSearchTypeString();
        IndexItem indexItem = this.getSearchName(sType);
        try {
            URIBuilder urlBuilder = new URIBuilder();
            urlBuilder.setScheme("http").setHost(url).setPath("/" + indexItem.getIndexName().toLowerCase() + "/update/json");
            URI uri = urlBuilder.build();

            JSONArray item = new JSONArray();
            List<String> realIds = new ArrayList<String>();
            for (JSONObject o : builder.getDoc()) {
                if (o != null) {
                    String id = o.optString("id");
                    if (id != null) {
                        realIds.add(id);
                    }
                    if (indexItem.isInAll()) {
                        o.put(INDEX_KEY, indexItem.getName());
                        o.put("all.in.one.id", indexItem.getName() + "_" + id);
                    } else {
                        o.put("all.in.one.id", id);
                    }
                    item.put(o);
                }
            }
            //delete
            StringBuilder delete = new StringBuilder();
            delete.append("{");
            boolean first = true;
            if (realIds.size() != builder.getKeys().size()) {
                for (String id : builder.getKeys()) {
                    if (!realIds.contains(id)) {
                        if (first) {
                            delete.append("\"delete\": {\"query\":");
                            first = false;
                        } else {
                            delete.append(", \"delete\": {\"query\":");
                        }
                        delete.append("\"");
                        if (indexItem.isInAll()) {
                            delete.append("all.in.one.id:" + indexItem.getName() + "_" + id);
                        } else {
                            delete.append("all.in.one.id:" + id);
                        }
                        delete.append("\"");
                        delete.append("}");
                    }
                }
                delete.append("}");
                postToBuild(uri, delete.toString());
            }
            //build
            String params = item.toString();
            postToBuild(uri, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    synchronized private Object[] copyUrls(){
        return  this.urls.toArray();
    }

    public void index(DocumentBuilder builder) throws Exception {
        boolean isGood = true;
        Object[] serverUrls = this.copyUrls();
        for(Object url : serverUrls){
            try {
                indexToUrl(url.toString(), builder);
            }
            catch (Exception e){
                this.failedUrls.add(url.toString());
                isGood = false;
                e.printStackTrace();
            }
        }
        if(!isGood) {
            throw new Exception("some Urls die." + StringUtils.join(this.failedUrls,","));
        }
    }


    private void postToBuild(URI uri, String params) throws Exception {
        HttpPost httpPost = new HttpPost(uri);

        StringEntity data = new StringEntity(params, ContentType.create("application/json", "UTF-8"));

        httpPost.setEntity(data);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                String content = EntityUtils.toString(entity, "UTF-8");
                System.out.println(content);

                JSONObject res = new JSONObject(content);

                if (res != null && res.optJSONObject("responseHeader").optInt("status") == 0) {

                } else {
                    throw new Exception("index error");
                }
            } finally {
                EntityUtils.consume(entity);
            }
        }
    }

    public List<SuggestItem> autoComplete(SearchArgs args) throws Exception {
        String sType = args.getSearchType().toString();

        IndexItem indexItem = getSearchName(sType);

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(url).setPath("/" + indexItem.getIndexName().toLowerCase() + "/suggest")
                .setParameter("wt", "json");

        builder.setParameter("spellcheck.q", args.getComplete());
        builder.setParameter("spellcheck.count", String.valueOf(args.getComplete_num()));
        //builder.setParameter("spellcheck.dictionary", "suggest");

        URI uri = builder.build();
        HttpGet httpGet = new HttpGet(uri);


        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String content = EntityUtils.toString(entity, "UTF-8");

            try {
                if (content != null && content.trim().length() > 0) {
                    JSONObject result = new JSONObject(content);
                    JSONObject spell = result.getJSONObject("spellcheck");
                    JSONArray suggestions = spell.optJSONArray("suggestions");
                    if (suggestions != null && suggestions.length() >= 3) {
                        JSONObject temp = suggestions.optJSONObject(1);
                        if (temp != null) {
                            JSONArray suggestion = temp.optJSONArray("suggestion");
                            if (suggestion != null && suggestion.length() > 0) {
                                List<SuggestItem> items = new ArrayList<SuggestItem>();
                                for (int i = 0; i < suggestion.length(); i++) {
                                    SuggestItem item = new SuggestItem();
                                    item.setName(suggestion.optString(i));
                                    items.add(item);
                                }
                                return items;
                            }
                        }
                    }
                }
            } finally {
                EntityUtils.consume(entity);
            }
        }

        return new ArrayList<SuggestItem>();
    }

    public List<String> autoSuggest(SearchArgs args) {
        if (args.getAuto_suggest() == null || "".equals(args.getAuto_suggest().trim())) {
            return new ArrayList<String>();
        }

        String keyword = args.getAuto_suggest();
        String key = this.getSearchName(args.getSearchType().toString()) + ":" + keyword;

        List<String> suggests = this.suggestsCaches.getIfPresent(key);
        if (suggests != null) {
            return suggests;
        }
        suggests = new ArrayList<String>();

        args.setKeyword_stat_not_itself(Boolean.TRUE);
        args.setKeyword_stat_with_results(Boolean.TRUE);
        args.setKeyword_stat_search(keyword);
        args.setKeyword_stat_limit(args.getAuto_suggest_num());
        SearchKeywordStat stats = this.keywordStat(args);
        if (stats != null && stats.getKeywords() != null) {
            for (NamePair namePair : stats.getKeywords()) {
                suggests.add(namePair.getName());
            }
        }
        this.suggestsCaches.put(key, suggests);
        return suggests;
    }

    public List<SimilarItem> spellCheck(SearchArgs args) throws Exception {
        List<SimilarItem> items = new ArrayList<SimilarItem>();
        if (args.getSpellcheck() == null || "".equals(args.getSpellcheck())) {
            return items;
        }
        String sType = args.getSearchType().toString();

        IndexItem indexItem = getSearchName(sType);

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(url).setPath("/" + indexItem.getIndexName().toLowerCase() + "/spell")
                .setParameter("wt", "json");

        builder.setParameter("spellcheck", "true");
        builder.setParameter("spellcheck.q", args.getSpellcheck());
        builder.setParameter("spellcheck.count", String.valueOf(args.getSpell_num()));
//        builder.setParameter("spellcheck.dictionary", "default");

        URI uri = builder.build();
        HttpGet httpGet = new HttpGet(uri);


        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String content = EntityUtils.toString(entity, "UTF-8");

            try {
                if (content != null && content.trim().length() > 0) {
                    JSONObject result = new JSONObject(content);
                    if (result == null) {
                        return new ArrayList<SimilarItem>();
                    }
                    JSONObject spell = result.optJSONObject("spellcheck");
                    if (spell == null) {
                        return new ArrayList<SimilarItem>();
                    }
                    JSONArray suggestions = spell.optJSONArray("suggestions");
                    if (suggestions != null && suggestions.length() >= 2) {
                        JSONObject temp = suggestions.optJSONObject(1);
                        if (temp != null) {
                            JSONArray suggestion = temp.optJSONArray("suggestion");
                            if (suggestion != null && suggestion.length() > 0) {

                                for (int i = 0; i < suggestion.length(); i++) {
                                    JSONObject sug = suggestion.getJSONObject(i);

                                    SimilarItem item = new SimilarItem();
                                    item.setName(sug.optString("word"));
                                    item.setSearches(sug.optInt("freq"));

                                    items.add(item);
                                }
                                return items;
                            }
                        }
                    }
                }
            } finally {
                EntityUtils.consume(entity);
            }
        }

        return new ArrayList<SimilarItem>();
    }

    public List<MatchItem> partialMatch(SearchArgs args) {
        return new ArrayList<MatchItem>();
    }

    public List<String> collaborateFilter(SearchArgs args, List<String> ids, String field) {
        try {
            SearchArgs idsArgs = args.getClass().newInstance();
            idsArgs.setIds(ids);
            idsArgs.getFacetFields().add(field);

            SearchResults results = this.search(idsArgs);

            List<NamePair> item = results.getFacets().get(field);

            if (item.size() == 0) {
                return new ArrayList<String>();
            }

            args.setFilterFiled(field);
            args.setFilteringArgs(item);
            args.setNotIds(ids);

            results = this.search(args);

            return results.getLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public SearchKeywordStat keywordStat(SearchArgs args) {
        SearchKeywordStat sks = new SearchKeywordStat();

        BooleanQuery bq = new BooleanQuery();

        if (args.getKeyword_stat_begin_date() != null || args.getKeyword_stat_end_date() != null) {
            TermRangeQuery range = new TermRangeQuery("keyworddate", args.getKeyword_stat_begin_date(), args.getKeyword_stat_end_date(), true, true);
            bq.add(range, BooleanClause.Occur.MUST);
        }

        if (args.getKeyword_stat_search() != null && !"".equals(args.getKeyword_stat_search())) {
            PiceneTextQuery text = new PiceneTextQuery("keyword_text", args.getKeyword_stat_search());
            bq.add(text, BooleanClause.Occur.MUST);
        }

        if (args.getKeyword_stat_is_no_results() != null && args.getKeyword_stat_is_no_results()) {
            TermQuery term = new TermQuery(new Term("foundNum", "0"));
            bq.add(term, BooleanClause.Occur.MUST);
        }

        if (args.getKeyword_stat_with_results() != null && args.getKeyword_stat_with_results()) {
            TermQuery term = new TermQuery(new Term("foundNum", "0"));
            bq.add(term, BooleanClause.Occur.MUST_NOT);
        }

        if (args.getKeyword_stat_not_itself() != null && args.getKeyword_stat_not_itself()) {
            TermQuery term = new TermQuery(new Term("keyword", args.getKeyword_stat_search()));
            bq.add(term, BooleanClause.Occur.MUST_NOT);
        }

        if (bq.clauses().size() > 0) {
            args.setFilterQuery(bq);
        }


        args.setWt("json");
        args.setInternalType("keyword");
        FacetSumArg facetSumArg = new FacetSumArg();
        facetSumArg.setFacetField("keyword");
        facetSumArg.setLimit(args.getKeyword_stat_limit());
        facetSumArg.setOffset(args.getKeyword_stat_offset());
        facetSumArg.getSumFields().add(new SumArg("count_i", SumArg.SUM));
        args.getSumFacetFields().add(facetSumArg);

        try {
            SearchResults sr = this.search(args);

            List<NamePair> keywords = new ArrayList<NamePair>();

            Map<String, Collection<FacetSumRow>> sumFacets = sr.getSumFacets();
            Collection<FacetSumRow> fsrs = sumFacets.get("keyword");
            if (fsrs != null) {
                for (FacetSumRow fsr : fsrs) {
                    Collection<NamePair> nps = fsr.getPairs();
                    for (NamePair np : nps) {
                        keywords.add(np);
                    }
                }
            }

            sks.setKeywords(keywords);
            sks.setTotal(sr.getResponse().optInt("total"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sks;
    }

    public List<String> searchKeywordCategory(SearchArgs args) {
        String keyword = args.getKeyword_category();

        if (keyword == null && "".equals(keyword)) {
            return null;
        }
        String key = this.getSearchName(args.getSearchType().toString()) + ":" + keyword;
        List<String> category = this.catCaches.getIfPresent(key);

        if (category != null) {
            return category;
        }
        args.setWt("json");
        args.setInternalType("keyword");
        FacetSumArg facetSumArg = new FacetSumArg();
        facetSumArg.setFacetField("category");
        facetSumArg.getSumFields().add(new SumArg("count_i", SumArg.SUM));
        facetSumArg.setLimit(args.getKeyword_category_num());
        args.getSumFacetFields().add(facetSumArg);
        TermQuery term = new TermQuery(new Term("keyword", keyword));
        BooleanQuery bq = new BooleanQuery();
        bq.add(term, BooleanClause.Occur.MUST);
        args.setFilterQuery(bq);

        try {
            SearchResults sr = this.search(args);

            category = new ArrayList<String>();

            Map<String, Collection<FacetSumRow>> sumFacets = sr.getSumFacets();
            Collection<FacetSumRow> fsrs = sumFacets.get("category");
            if (fsrs != null) {
                for (FacetSumRow fsr : fsrs) {
                    Collection<NamePair> nps = fsr.getPairs();
                    for (NamePair np : nps) {
                        category.add(np.getName());
                    }
                }
            }

            this.catCaches.put(key, category);
            System.out.println("loading from category discovery : " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return category;
    }


    public void close() {
        httpClient.getConnectionManager().shutdown();
    }
}

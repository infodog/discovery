package net.xinshi.discovery.search.client.services;

import net.xinshi.discovery.search.client.util.NamePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class SearchResults {
    private int total; //total number
    private List<String> lists = new ArrayList<String>(); //current page result

    //obtainValue
    private List<String> valueLists = new ArrayList<String>();

    private JSONArray docs = new JSONArray();

    private JSONObject highlighting = new JSONObject();

    private JSONObject response = new JSONObject();

    private JSONObject insight = new JSONObject();

    //facet search
    private Map<String, List<NamePair>> facets = new HashMap<String, List<NamePair>>(); //facet search

    //summary
    @Deprecated
    private Collection<NamePair> sum;
    @Deprecated
    private Map<String, Collection<FacetSumRow>> sumFacets = new HashMap<String, Collection<FacetSumRow>>();


    public JSONObject getInsight() {
        return insight;
    }

    public void setInsight(JSONObject insight) {
        this.insight = insight;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public JSONObject getHighlighting() {
        return highlighting;
    }

    public void setHighlighting(JSONObject highlighting) {
        this.highlighting = highlighting;
    }

    public JSONArray getDocs() {
        return docs;
    }

    public void setDocs(JSONArray docs) {
        this.docs = docs;
    }

    public List<String> getValueLists() {
        return valueLists;
    }

    public void setValueLists(List<String> valueLists) {
        this.valueLists = valueLists;
    }


    public Map<String, List<NamePair>> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, List<NamePair>> facets) {
        this.facets = facets;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<String> getLists() {
        return lists;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
    }

    public static SearchResults getFromJson(String json) throws Exception {
        SearchResults sr = new SearchResults();
        JSONObject temp = new JSONObject(json);
        sr.setResponse(temp);


        JSONObject insight = temp.optJSONObject("insight");

        if (insight != null) {
            sr.setInsight(insight);

            try {
                JSONArray rows = insight.getJSONArray("rows");
                if (rows != null && rows.length() > 0) {
                    Map<String, Collection<FacetSumRow>> sumFacets = jsonToFacetSums(insight);
                    sr.setSumFacets(sumFacets);

                    if ("insight_stat".equals(rows.optJSONObject(0).optString("insight_stat"))) {
                        List<NamePair> pairs = new ArrayList<NamePair>();
                        JSONArray fields = insight.optJSONArray("fields");
                        for (int i = 1; i < fields.length(); i++) {
                            String key = fields.optString(i);
                            pairs.add(new NamePair(key, rows.optJSONObject(0).optString(key)));
                        }

                        sr.setSum(pairs);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sr;
        }

        JSONObject header = temp.getJSONObject("responseHeader");
        JSONObject result = temp.optJSONObject("response");

        if (result == null) {
            return sr;
        }

        int total = result.getInt("numFound");
        sr.setTotal(total);

        JSONArray lists = result.getJSONArray("docs");
        List<String> ids = new ArrayList<String>();
        if (lists != null) {
            for (int i = 0; i < lists.length(); i++) {
                JSONObject id = lists.getJSONObject(i);
                ids.add(id.getString("id"));
            }
        }
        sr.setLists(ids);

        sr.setDocs(lists);

        sr.setHighlighting(temp.optJSONObject("highlighting"));


        JSONObject fs = temp.optJSONObject("facet_counts");
        if (fs != null) {
            JSONObject facet_fields = fs.optJSONObject("facet_fields");
            if (facet_fields != null) {
                Map<String, List<NamePair>> facets = jsonToFacets(facet_fields);
                sr.setFacets(facets);
            }
        }

        return sr;
    }

    private static Map<String, Collection<FacetSumRow>> jsonToFacetSums(JSONObject insight) {
        JSONArray rows = null;
        JSONArray fields = null;
        int total = insight.optInt("total");
        try {
            fields = insight.getJSONArray("fields");
            rows = insight.getJSONArray("rows");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, Collection<FacetSumRow>> facets = new HashMap<String, Collection<FacetSumRow>>();
        if (rows != null) {
            Collection<FacetSumRow> facet = new ArrayList<FacetSumRow>();
            for (int i = 0; i < rows.length(); i++) {
                try {
                    JSONObject row = rows.getJSONObject(i);
                    FacetSumRow facetSumRow = new FacetSumRow();
                    facetSumRow.setName(row.optString(fields.optString(0)));


                    Collection<NamePair> pairs = new ArrayList<NamePair>();
                    for (int j = 1; j < fields.length(); j++) {
                        String key = fields.optString(j);
                        pairs.add(new NamePair(key, row.optString(key)));
                    }

                    facetSumRow.setPairs(pairs);
                    facet.add(facetSumRow);
                    facetSumRow.setTotal(total);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            facets.put(fields.optString(0), facet);
        }

        return facets;
    }

    private static Map<String, List<NamePair>> jsonToFacets(JSONObject fs) throws Exception {
        Map<String, List<NamePair>> facets = new HashMap<String, List<NamePair>>();

        Iterator<String> iterator = (Iterator<String>) fs.keys();
        while (iterator.hasNext()) {
            String field = iterator.next();
            JSONArray ps = fs.getJSONArray(field);
            List<NamePair> pairs = new ArrayList<NamePair>();
            for (int j = 0; j < ps.length(); j = j + 2) {
                NamePair namePair = new NamePair(ps.getString(j), ps.getString(j + 1));
                pairs.add(namePair);
            }
            facets.put(field, pairs);
        }
        return facets;
    }

    public Collection<NamePair> getSum() {
        return sum;
    }

    public void setSum(Collection<NamePair> sum) {
        this.sum = sum;
    }

    public Map<String, Collection<FacetSumRow>> getSumFacets() {
        return sumFacets;
    }

    public void setSumFacets(Map<String, Collection<FacetSumRow>> sumFacets) {
        this.sumFacets = sumFacets;
    }
}
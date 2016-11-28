package net.xinshi.discovery.search.client.reco.impl;

import net.xinshi.discovery.search.client.reco.MatchItem;
import net.xinshi.discovery.search.client.reco.RecommenderClient;
import net.xinshi.discovery.search.client.reco.SimilarItem;
import net.xinshi.discovery.search.client.reco.SuggestItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/28/12
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class JavaRecoClient implements RecommenderClient {
    private String url;
    private String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public List<SuggestItem> autoComplete(String prefix) throws Exception {
//        return new ArrayList<SuggestItem>();
//    }
//
//    public List<String> autoSuggest(String keyword) {
//        return new ArrayList<String>();
//    }
//
//    public List<SimilarItem> spellCheck(String word, int numSuggest) {
//        return new ArrayList<SimilarItem>();
//    }
//
//    public List<MatchItem> partialMatch(String keyword) {
//        return new ArrayList<MatchItem>();
//    }
}

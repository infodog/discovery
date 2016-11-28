package net.xinshi.discovery.search.client.services;

import net.xinshi.discovery.search.client.util.NamePair;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 3/8/13
 * Time: 5:03 PM
 */
public class SearchKeywordStat {
    private int total;
    private List<NamePair> keywords;

    public List<NamePair> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<NamePair> keywords) {
        this.keywords = keywords;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

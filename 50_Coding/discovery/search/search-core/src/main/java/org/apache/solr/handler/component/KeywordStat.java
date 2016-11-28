package org.apache.solr.handler.component;

import com.google.common.base.Joiner;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 3/1/13
 * Time: 4:37 PM
 */
public class KeywordStat {
    private String keyword;
    private String category;
    private int foundNum;
    private int count;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFoundNum() {
        return foundNum;
    }

    public void setFoundNum(int foundNum) {
        this.foundNum = foundNum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSolrKey() {
        return Joiner.on("::").join(this.keyword, this.category, this.date);
    }
}

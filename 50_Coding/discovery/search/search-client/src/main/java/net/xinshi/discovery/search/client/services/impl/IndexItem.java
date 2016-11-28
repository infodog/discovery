package net.xinshi.discovery.search.client.services.impl;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/22/13
 * Time: 2:36 PM
 */
public class IndexItem {
    private boolean isSaas;
    private boolean isInAll;

    private String indexName;
    private String name;


    public boolean isInAll() {
        return isInAll;
    }

    public void setInAll(boolean inAll) {
        isInAll = inAll;
    }

    public boolean isSaas() {
        return isSaas;
    }

    public void setSaas(boolean saas) {
        isSaas = saas;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

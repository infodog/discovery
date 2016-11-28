package net.xinshi.picenedemo.user;

import net.xinshi.discovery.search.client.services.QueryBuilder;
import net.xinshi.discovery.search.client.services.SearchArgs;
import net.xinshi.discovery.search.client.services.SearchType;

import java.util.List;


public class UserSearchArgs extends SearchArgs {
    private String id;

    public List<String> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<String> purchases) {
        this.purchases = purchases;
    }

    private List<String> purchases;

	@Override
	public SearchType getSearchType() {
		return SearchTypes.USER;
	}
    
	@Override
	public QueryBuilder getQueryBuilder() {
		return UserQueryBuilder.getInstance();
	}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

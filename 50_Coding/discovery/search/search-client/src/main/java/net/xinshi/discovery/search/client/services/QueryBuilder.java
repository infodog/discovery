package net.xinshi.discovery.search.client.services;


import net.xinshi.discovery.search.client.query.Query;

public interface QueryBuilder {
	public Query buildQuery(SearchArgs args) throws Exception;
	public Query buildFilter(SearchArgs args) throws Exception;
}

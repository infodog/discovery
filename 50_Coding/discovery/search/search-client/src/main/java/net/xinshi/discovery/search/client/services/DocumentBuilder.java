package net.xinshi.discovery.search.client.services;

import org.json.JSONObject;

import java.util.Collection;

public interface DocumentBuilder {
	/**
	 * Search Type  
	 * @return
	 */	
	public SearchType getSearchType();
	public String getSearchTypeString();
	public Collection<JSONObject> getDoc() throws Exception;
	public Collection<String> getKeys() throws Exception;
 }

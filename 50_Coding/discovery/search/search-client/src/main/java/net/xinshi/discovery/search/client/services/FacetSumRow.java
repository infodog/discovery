package net.xinshi.discovery.search.client.services;

import net.xinshi.discovery.search.client.util.NamePair;

import java.util.Collection;


public class FacetSumRow implements Comparable<FacetSumRow>{
	private String name;
	private Collection<NamePair> pairs;
	
	//total number of the all records
	private int total;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Collection<NamePair> getPairs() {
		return pairs;
	}
	public void setPairs(Collection<NamePair> pairs) {
		this.pairs = pairs;
	}
	public int compareTo(FacetSumRow o) {		
		return this.name.compareTo(o.getName());
	}

}

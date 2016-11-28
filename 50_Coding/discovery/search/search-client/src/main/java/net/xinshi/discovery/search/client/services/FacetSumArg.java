package net.xinshi.discovery.search.client.services;

import java.util.ArrayList;
import java.util.List;

public class FacetSumArg {	
	private String facetField;
	private List<SumArg> sumFields = new ArrayList<SumArg>();
	private String sort;
	private int offset = 0;
	private int limit = 10;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public List<SumArg> getSumFields() {
		return sumFields;
	}
	public void setSumFields(List<SumArg> sumFields) {
		this.sumFields = sumFields;
	}
	public String getFacetField() {
		return facetField;
	}
	public void setFacetField(String facetField) {
		this.facetField = facetField;
	}
}

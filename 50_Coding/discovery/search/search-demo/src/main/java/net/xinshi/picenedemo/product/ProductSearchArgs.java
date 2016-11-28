package net.xinshi.picenedemo.product;

import net.xinshi.discovery.search.client.services.QueryBuilder;
import net.xinshi.discovery.search.client.services.SearchArgs;
import net.xinshi.discovery.search.client.services.SearchType;

import java.util.List;


public class ProductSearchArgs extends SearchArgs {
    private String id;
    private String name;
    private String merchantId;
    private String columnId;
    private Long beginPrice;
    private Long endPrice;




    public Long getBeginPrice() {
		return beginPrice;
	}

	public void setBeginPrice(Long beginPrice) {
		this.beginPrice = beginPrice;
	}

	public Long getEndPrice() {
		return endPrice;
	}

	public void setEndPrice(Long endPrice) {
		this.endPrice = endPrice;
	}

	@Override
	public SearchType getSearchType() {
		return SearchTypes.PRODUCT;
	}
    
	@Override
	public QueryBuilder getQueryBuilder() {
		return ProductQueryBuilder.getInstance();
	}
    
    public void setMerchantId(String merchantId){
        this.merchantId = merchantId;
    }

    public String getMerchantId(){
       return merchantId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }
}

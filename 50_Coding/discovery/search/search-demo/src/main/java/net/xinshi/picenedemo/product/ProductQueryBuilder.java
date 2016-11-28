package net.xinshi.picenedemo.product;


import net.xinshi.discovery.search.client.query.*;
import net.xinshi.discovery.search.client.services.QueryBuilder;
import net.xinshi.discovery.search.client.services.SearchArgs;

import java.util.Date;


public class ProductQueryBuilder implements QueryBuilder {
    private static ProductQueryBuilder builder;

    private ProductQueryBuilder(){
    }
    
    public synchronized static ProductQueryBuilder getInstance(){
    	if(builder != null) {
    		return builder;
    	} else {
    		builder = new ProductQueryBuilder();
    		return builder;
    	}
    }
    
    public Query buildQuery(SearchArgs productSearchArgs) throws Exception {
        ProductSearchArgs varProductSearchArgs = (ProductSearchArgs)productSearchArgs;
        BooleanQuery resultQuery = new BooleanQuery();
        //BooleanQuery.setMaxClauseCount(16384);

        this.searchByTitle(varProductSearchArgs,resultQuery);
        this.searchByPrice(varProductSearchArgs,resultQuery);

        if (resultQuery.clauses().size() <= 0) {
            resultQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
        }

        return resultQuery;
    }

	private void searchByPrice(ProductSearchArgs args,
			BooleanQuery resultQuery) {
		if(args.getBeginPrice() == null && args.getEndPrice() == null) {
			return;
		}
		String begin = args.getBeginPrice()!=null ? String.valueOf(args.getBeginPrice()) : null;
		String end = args.getEndPrice()!=null ? String.valueOf(args.getEndPrice()) : null;


        //test
        //begin = new Date().toString();

		Query query = new TermRangeQuery(ProductSearchFields.Keyword.PRICE,begin,end,true,true);
		//Query query = NumericRangeQuery.newLongRange(ProductSearchFields.Keyword.PRICE,args.getBeginPrice(),args.getEndPrice(),true,true);
		resultQuery.add(query,BooleanClause.Occur.MUST);
	}

	public Query buildFilter(SearchArgs args) throws Exception {
		ProductSearchArgs varProductSearchArgs = (ProductSearchArgs)args;
		BooleanQuery resultQuery = new BooleanQuery();
		this.filterById(varProductSearchArgs,resultQuery);
		this.filterByMerchantId(varProductSearchArgs,resultQuery);
		this.filterByColumnId(varProductSearchArgs,resultQuery);
		/*Filter filter = null;
		if(resultQuery.clauses().size()>0) {
			filter = new QueryWrapperFilter(resultQuery);
		}*/
		return resultQuery;
	}


//	public Sort buildSort(SearchArgs args) throws Exception {
//		Sort sort = null;
//		if(args.getSortFields() != null) {
//			sort = new Sort(args.getSortFields());
//		}
//		return sort;
//	}

    private void filterById(ProductSearchArgs productSearchArgs, BooleanQuery booleanQuery) throws Exception {
        if (productSearchArgs.getId() != null && !"".equals(productSearchArgs.getId())) {
            Query query = new TermQuery(new Term(ProductSearchFields.ID.ID, productSearchArgs.getId()));
            booleanQuery.add(query, BooleanClause.Occur.MUST);

//            //test
//            Query prefixQuery = new PrefixQuery(new Term(ProductSearchFields.ID.ID, productSearchArgs.getId()));
//            booleanQuery.add(prefixQuery, BooleanClause.Occur.MUST);
        }
    }

    private void filterByMerchantId(ProductSearchArgs productSearchArgs, BooleanQuery booleanQuery) throws Exception {
        if (productSearchArgs.getMerchantId() != null && !"".equals(productSearchArgs.getMerchantId())) {
            Query query = new TermQuery(new Term(ProductSearchFields.Keyword.MERCHANTID, productSearchArgs.getMerchantId()));
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        }
    }

    private void searchByTitle(ProductSearchArgs productSearchArgs, BooleanQuery booleanQuery) throws Exception {
        if (productSearchArgs.getName() != null && !"".equals(productSearchArgs.getName())) {
            //String queryString = ProductSearchFields.Keyword.NAME + ":" + productSearchArgs.getName();
            //QueryParser paser = new QueryParser(Version.LUCENE_30,"", analyzer);
            //Query query = paser.parse(queryString);
            //Query query = parser.parse(ProductSearchFields.Keyword.NAME, productSearchArgs.getName());
        	PiceneTextQuery query = new PiceneTextQuery(ProductSearchFields.Text.NAME, productSearchArgs.getName());
            query.setBoost(5);

//            TermQuery query = new TermQuery(new Term(ProductSearchFields.Keyword.NAME, productSearchArgs.getName()));

            //PhraseQuery query = new PhraseQuery();



        	booleanQuery.add(query, BooleanClause.Occur.MUST);
        }
    }

    private void filterByColumnId(ProductSearchArgs productSearchArgs, BooleanQuery booleanQuery) throws Exception {
        if (productSearchArgs.getColumnId() != null && !"".equals(productSearchArgs.getColumnId())) {
            Query query = new TermQuery(new Term(ProductSearchFields.MultiValued.FACET_COLUMN + 1, productSearchArgs.getColumnId()));
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        }
    }
}

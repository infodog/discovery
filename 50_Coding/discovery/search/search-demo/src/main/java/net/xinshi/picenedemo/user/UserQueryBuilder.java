package net.xinshi.picenedemo.user;


import net.xinshi.discovery.search.client.query.*;
import net.xinshi.discovery.search.client.services.QueryBuilder;
import net.xinshi.discovery.search.client.services.SearchArgs;


public class UserQueryBuilder implements QueryBuilder {
    private static UserQueryBuilder builder;

    private UserQueryBuilder(){
    }
    
    public synchronized static UserQueryBuilder getInstance(){
    	if(builder != null) {
    		return builder;
    	} else {
    		builder = new UserQueryBuilder();
    		return builder;
    	}
    }
    
    public Query buildQuery(SearchArgs userSearchArgs) throws Exception {
        UserSearchArgs varUserSearchArgs = (UserSearchArgs)userSearchArgs;
        BooleanQuery resultQuery = new BooleanQuery();
        //BooleanQuery.setMaxClauseCount(16384);

        if (resultQuery.clauses().size() <= 0) {
            resultQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
        }

        return resultQuery;
    }


	public Query buildFilter(SearchArgs args) throws Exception {
		UserSearchArgs varUserSearchArgs = (UserSearchArgs)args;
		BooleanQuery resultQuery = new BooleanQuery();
		this.filterById(varUserSearchArgs, resultQuery);

		return resultQuery;
	}


    private void filterById(UserSearchArgs userSearchArgs, BooleanQuery booleanQuery) throws Exception {
        if (userSearchArgs.getId() != null && !"".equals(userSearchArgs.getId())) {
            Query query = new TermQuery(new Term(UserSearchFields.ID.ID, userSearchArgs.getId()));
            booleanQuery.add(query, BooleanClause.Occur.MUST);
        }
    }


    private void filterByPurchases(UserSearchArgs userSearchArgs, BooleanQuery booleanQuery) throws Exception {
        if (userSearchArgs.getPurchases() != null && userSearchArgs.getPurchases().size()>0) {

            BooleanQuery pbq = new BooleanQuery();
            for (String purchase : userSearchArgs.getPurchases()) {
                Query query = new TermQuery(new Term(UserSearchFields.Keyword.PURCHASES, purchase));
                pbq.add(query, BooleanClause.Occur.SHOULD);
            }


            booleanQuery.add(pbq, BooleanClause.Occur.MUST);

        }
    }


}

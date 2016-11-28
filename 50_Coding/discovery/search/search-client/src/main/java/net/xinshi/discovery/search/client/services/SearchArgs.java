package net.xinshi.discovery.search.client.services;

import net.xinshi.discovery.search.client.query.Query;
import net.xinshi.discovery.search.client.query.SortField;
import net.xinshi.discovery.search.client.util.NamePair;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchArgs {
	public static final String MULTI_VALUE_FLAG = "multi_";

	/**
	 * Search Type
	 * 
	 * @return
	 */
	public abstract SearchType getSearchType();

	public abstract QueryBuilder getQueryBuilder();

    private String internalType = null;

	private int initialRecord; // start
	private int fetchCount = 20; // default 20 //number

    private String wt = "json";

	// facet search
	private List<String> facetFields = new ArrayList<String>();
	private List<Query> facetQuery = new ArrayList<Query>();

	// sort
	private SortField[] sortFields;

	// summary   use insight_dql instead
    @Deprecated
	private List<SumArg> sumFields = new ArrayList<SumArg>();
    //use insight_dql instead
	@Deprecated
    private List<FacetSumArg> sumFacetFields = new ArrayList<FacetSumArg>();

    //DQL
    private String insight_dql;
    private int insigit_dql_offset = 0;
    private int insight_dql_limit = 10;

    // retrivalValue
	private String valueField;
	
	//highlight
	private String hightlight_keyword;

    private String hightlight_field;


    //spell check
    private String spellcheck;
    private int spell_num = 3;

    //auto complete
    private String complete;
    private int complete_num = 10;

    //auto suggest
    private String auto_suggest;
    private int auto_suggest_num = 10;

    //partial match
    private String partial_match;

    //Collaborative filtering
    private List<NamePair> filteringArgs;
    private String filterFiled;
    private List<String> ids;
    private List<String> notIds;


    private Query filterQuery;


    //Keyword Stat
    private String keyword_stat_begin_date;
    private String keyword_stat_end_date;
    private Boolean keyword_stat_is_no_results;
    private Boolean keyword_stat_with_results;
    private Boolean keyword_stat_not_itself;
    private String keyword_stat_search;
    private int keyword_stat_offset = 0;
    private int keyword_stat_limit = 10;

    //keyword Category
    private String keyword_category;
    private int keyword_category_num = 2;


    public String getPartial_match() {
        return partial_match;
    }

    public void setPartial_match(String partial_match) {
        this.partial_match = partial_match;
    }

    public String getAuto_suggest() {
        return auto_suggest;
    }

    public void setAuto_suggest(String auto_suggest) {
        this.auto_suggest = auto_suggest;
    }

    public int getAuto_suggest_num() {
        return auto_suggest_num;
    }

    public void setAuto_suggest_num(int auto_suggest_num) {
        this.auto_suggest_num = auto_suggest_num;
    }


    public int getInsigit_dql_offset() {
        return insigit_dql_offset;
    }

    public void setInsigit_dql_offset(int insigit_dql_offset) {
        this.insigit_dql_offset = insigit_dql_offset;
    }

    public int getInsight_dql_limit() {
        return insight_dql_limit;
    }

    public void setInsight_dql_limit(int insight_dql_limit) {
        this.insight_dql_limit = insight_dql_limit;
    }

    public String getInsight_dql() {
        return insight_dql;
    }

    public void setInsight_dql(String insight_dql) {
        this.insight_dql = insight_dql;
    }


    public int getKeyword_category_num() {
        return keyword_category_num;
    }

    public void setKeyword_category_num(int keyword_category_num) {
        this.keyword_category_num = keyword_category_num;
    }

    public String getKeyword_category() {
        return keyword_category;
    }

    public void setKeyword_category(String keyword_category) {
        this.keyword_category = keyword_category;
    }

    public Boolean getKeyword_stat_not_itself() {
        return keyword_stat_not_itself;
    }

    public void setKeyword_stat_not_itself(Boolean keyword_stat_not_itself) {
        this.keyword_stat_not_itself = keyword_stat_not_itself;
    }

    public Boolean getKeyword_stat_with_results() {
        return keyword_stat_with_results;
    }

    public void setKeyword_stat_with_results(Boolean keyword_stat_with_results) {
        this.keyword_stat_with_results = keyword_stat_with_results;
    }

    public int getKeyword_stat_limit() {
        return keyword_stat_limit;
    }

    public void setKeyword_stat_limit(int keyword_stat_limit) {
        this.keyword_stat_limit = keyword_stat_limit;
    }

    public int getKeyword_stat_offset() {
        return keyword_stat_offset;
    }

    public void setKeyword_stat_offset(int keyword_stat_offset) {
        this.keyword_stat_offset = keyword_stat_offset;
    }

    public String getKeyword_stat_begin_date() {
        return keyword_stat_begin_date;
    }

    public void setKeyword_stat_begin_date(String keyword_stat_begin_date) {
        this.keyword_stat_begin_date = keyword_stat_begin_date;
    }

    public String getKeyword_stat_end_date() {
        return keyword_stat_end_date;
    }

    public void setKeyword_stat_end_date(String keyword_stat_end_date) {
        this.keyword_stat_end_date = keyword_stat_end_date;
    }

    public Boolean getKeyword_stat_is_no_results() {
        return keyword_stat_is_no_results;
    }

    public void setKeyword_stat_is_no_results(Boolean keyword_stat_is_no_results) {
        this.keyword_stat_is_no_results = keyword_stat_is_no_results;
    }

    public String getKeyword_stat_search() {
        return keyword_stat_search;
    }

    public void setKeyword_stat_search(String keyword_stat_search) {
        this.keyword_stat_search = keyword_stat_search;
    }

    public Query getFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(Query filterQuery) {
        this.filterQuery = filterQuery;
    }

    public String getInternalType() {
        return internalType;
    }

    public void setInternalType(String internalType) {
        this.internalType = internalType;
    }

    public String getWt() {
        return wt;
    }

    public void setWt(String wt) {
        this.wt = wt;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getNotIds() {
        return notIds;
    }

    public void setNotIds(List<String> notIds) {
        this.notIds = notIds;
    }

    public String getFilterFiled() {
        return filterFiled;
    }

    public void setFilterFiled(String filterFiled) {
        this.filterFiled = filterFiled;
    }

    public List<NamePair> getFilteringArgs() {
        return filteringArgs;
    }

    public void setFilteringArgs(List<NamePair> filteringArgs) {
        this.filteringArgs = filteringArgs;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public int getComplete_num() {
        return complete_num;
    }

    public void setComplete_num(int complete_num) {
        this.complete_num = complete_num;
    }

    public String getSpellcheck() {
        return spellcheck;
    }

    public void setSpellcheck(String spellcheck) {
        this.spellcheck = spellcheck;
    }

    public int getSpell_num() {
        return spell_num;
    }

    public void setSpell_num(int spell_num) {
        this.spell_num = spell_num;
    }

    public String getHightlight_field() {
        return hightlight_field;
    }

    public void setHightlight_field(String hightlight_field) {
        this.hightlight_field = hightlight_field;
    }

    public String getHightlight_keyword() {
		return hightlight_keyword;
	}

	public void setHightlight_keyword(String hightlight_keyword) {
		this.hightlight_keyword = hightlight_keyword;
	}

	/**
	 * for auto completion, auto suggestion, spell correction, partial match
	 * etc.
	 */
	private String track_keyword;

    private String track_category;

    public String getTrack_category() {
        return track_category;
    }

    public void setTrack_category(String track_category) {
        this.track_category = track_category;
    }

    public String getTrack_keyword() {
		return track_keyword;
	}

	public void setTrack_keyword(String track_keyword) {
		this.track_keyword = track_keyword;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public List<SumArg> getSumFields() {
		return sumFields;
	}

	public void setSumFields(List<SumArg> sumFields) {
		this.sumFields = sumFields;
	}

	public List<FacetSumArg> getSumFacetFields() {
		return sumFacetFields;
	}

	public void setSumFacetFields(List<FacetSumArg> sumFacetFields) {
		this.sumFacetFields = sumFacetFields;
	}

	public List<Query> getFacetQuery() {
		return facetQuery;
	}

	public List<String> getFacetFields() {
		return facetFields;
	}

	public int getFetchCount() {
		return fetchCount;
	}

	public void setFetchCount(int fetchCount) {
		this.fetchCount = fetchCount;
	}

	public int getInitialRecord() {
		return initialRecord;
	}

	public void setInitialRecord(int initialRecord) {
		this.initialRecord = initialRecord;
	}

	public void setFromPath(int page) {
		this.initialRecord = page;
	}

	public int getStartFrom() {
		return this.initialRecord;
	}

	public SortField[] getSortFields() {
		return sortFields;
	}

	public void setSortFields(SortField[] sortFields) {
		this.sortFields = sortFields;
	}
}

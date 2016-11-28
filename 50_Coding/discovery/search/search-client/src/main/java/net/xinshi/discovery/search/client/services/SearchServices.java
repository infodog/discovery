package net.xinshi.discovery.search.client.services;

import net.xinshi.discovery.search.client.reco.MatchItem;
import net.xinshi.discovery.search.client.reco.SimilarItem;
import net.xinshi.discovery.search.client.reco.SuggestItem;

import java.util.List;

public interface SearchServices {
	public SearchResults search(SearchArgs args) throws Exception;
	public void index(DocumentBuilder builder) throws Exception;

    //String prefix
    public List<SuggestItem> autoComplete(SearchArgs args) throws Exception;

    //String keyword
    public List<String> autoSuggest(SearchArgs args);

    //String word, int numSuggest    --- spellcheck, spellcheckNum
    public List<SimilarItem> spellCheck(SearchArgs args) throws Exception;

    //String keyword
    public List<MatchItem> partialMatch(SearchArgs args);

    //Collaborate Filter Recommendation
    public List<String> collaborateFilter(SearchArgs args,List<String> ids, String field);

    public SearchKeywordStat keywordStat(SearchArgs args);

    public List<String> searchKeywordCategory(SearchArgs args);
}

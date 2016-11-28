package org.apache.lucene.analysis.chinese.hhmm;

import org.apache.lucene.analysis.chinese.dictionary.Dictionary;
import org.apache.lucene.analysis.chinese.dictionary.SearchDictionary;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordDictionaryTest {

	@Test
	public void testGetPrefixMatchCharArray() {
		Dictionary wordDict = SearchDictionary.getInstance();
		boolean flag = wordDict.putIntoDictionary("4g", 10000);
		System.out.println(flag);
		wordDict.putIntoDictionary("电商", 10000);
		
		char[] charArray = {'4','g'};
		int index = wordDict.getPrefixMatch(charArray);		
		System.out.println(index);
		
		System.out.println(wordDict.getFrequency(charArray));
		
		assertEquals(wordDict.getFrequency(charArray),10000);		
		
		
		char[] dianshang = {'电','商'};
		index = wordDict.getPrefixMatch(dianshang);		
		System.out.println(index);
		
		System.out.println(wordDict.getFrequency(dianshang));
		
		
		char[] diannao = {'电','脑'};
		index = wordDict.getPrefixMatch(diannao);
		System.out.println(index);
	}
	
	
	@Test
	public void testBatchPutIntoDictionary() {
	}
}

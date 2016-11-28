package org.apache.solr.search;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PiceneQueryParserTest {
	private Map<String, Set<String>> synonym = new HashMap<String, Set<String>>();
	
	@Test
	public void testParse() throws Exception {
		PiceneQueryParser parser = new PiceneQueryParser();
		Query q = parser.parse("苹果手机");
		System.out.println(q);
        assertEquals("(+(content:\"苹 果\") +(content:\"手 机\")) content:\"苹 果 手 机\"",q.toString());
		BooleanQuery bq = (BooleanQuery)q;
		System.out.println(bq.getClauses().length);
        assertEquals(2,bq.getClauses().length);

		q = parser.parse("电话电脑");
		System.out.println(q);


        q = parser.parse("ben@me.com");
        System.out.println(q);
//        assertEquals("(+(content:\"b e n\") +(content:\"m e\") +(content:\"c o m\")) content:\"b e n m e c o m\"",q.toString());

        q = parser.parse("ben@me.com");
        System.out.println(q);
//        assertEquals("(+(content:\"b e n\") +(content:\"m e\") +(content:\"c o m\")) content:\"b e n m e c o m\"",q.toString());

//		
//		SearchDictionary.getInstance().putIntoDictionary("4g", 10000);
//		q = parser.parse("4g");
//		System.out.println(q);
		
//		this.add("电视", "电视机");
//		MemSynonymEngine engine = (MemSynonymEngine)MemSynonymEngine.getInstance();
//		engine.replaceBy(synonym);
//		synonym = new HashMap<String, Set<String>>();
//
//		Query q = parser.parse("电视机");
//		System.out.println(q);
	}
	
	
//	public boolean add(String word, String syn) {
//		Set<String> list = this.synonym.get(word);
//		if (list != null) {
//			list.add(syn);
//		} else {
//			list = new HashSet<String>();
//			list.add(syn);
//			this.synonym.put(word, list);
//		}
//
//		Set<String> synlist = this.synonym.get(syn);
//		if (synlist != null) {
//			synlist.add(word);
//		} else {
//			synlist = new HashSet<String>();
//			synlist.add(word);
//			this.synonym.put(syn, synlist);
//		}
//		return true;
//	}
	
}

package org.apache.lucene.analysis.frequency;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.StringReader;


public class WordFrequencyAnalyzerTest extends BaseTokenStreamTestCase {

	public void testWordFrequency() throws Exception {
		WordFrequencyAnalyzer oa = new WordFrequencyAnalyzer();



		String sentence = "中文mac::2";
		String result[] = { "中文mac", "中文mac"};

        TokenStream ts = oa.tokenStream("dummy", new StringReader(sentence));
        CharTermAttribute ct = ts.addAttribute(CharTermAttribute.class);
        while (ts.incrementToken()) {
            System.out.println(ct.toString());
        }

//		assertAnalyzesTo(oa, sentence, result);
        assertTokenStreamContents(oa.tokenStream("content", new StringReader(sentence)), result, null, null, null, null, null, null);

	}

}

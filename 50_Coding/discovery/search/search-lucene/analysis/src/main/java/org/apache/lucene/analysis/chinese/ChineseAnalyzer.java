package org.apache.lucene.analysis.chinese;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.chinese.dictionary.Dictionary;
import org.apache.lucene.analysis.chinese.synonym.MemSynonymEngine;
import org.apache.lucene.analysis.chinese.synonym.SynonymFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Set;


public final class ChineseAnalyzer extends Analyzer {
	private Dictionary dict;

    public ChineseAnalyzer() {
		super();
	}

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer tokenizer = null;

        if(this.dict == null) {
            tokenizer = new ChineseTokenizer(reader);
        } else {
            tokenizer = new ChineseTokenizer(reader,dict);
        }

        return new TokenStreamComponents(tokenizer);
    }

    public ChineseAnalyzer(Dictionary dict) {
		super();
		this.dict = dict;
	}
}

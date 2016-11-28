package org.apache.solr.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.chinese.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;

import java.io.StringReader;
import java.util.Stack;

public class PiceneQueryParser {
	private Analyzer analyzer;
	private Analyzer phraseAanlyzer;
	private String defaultField;

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public PiceneQueryParser(String defautFiend, Analyzer analyzer,
			Analyzer phraseAanlyzer) {
		super();
		this.analyzer = analyzer;
		this.phraseAanlyzer = phraseAanlyzer;
		this.defaultField = defautFiend;
	}

	public PiceneQueryParser() {
		super();
        this.defaultField = "content";
        this.analyzer = new StandardAnalyzer(Version.LUCENE_44);

		this.phraseAanlyzer = new ChineseAnalyzer();
	}

	public Query parse(String query) throws Exception {
		return this.parse(this.defaultField, query);
	}

	public Query parse(String field, String query) throws Exception {
		TokenStream phraseStream = this.phraseAanlyzer.tokenStream(
				field, new StringReader(query));
		CharTermAttribute term = phraseStream
				.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posInc = phraseStream
				.addAttribute(PositionIncrementAttribute.class);

		Stack<String> synonymStack = new Stack<String>();

		BooleanQuery qry = new BooleanQuery();
		BooleanQuery pq = new BooleanQuery();
        phraseStream.reset();
		while (phraseStream.incrementToken()) {
			if (synonymStack.size() > 0 && posInc.getPositionIncrement() != 0) {
				BooleanQuery synonyms = new BooleanQuery();
				while (synonymStack.size() > 0) {
					String t = synonymStack.pop();
					if (t.length() > 1 && !t.equals(query)) {
						Query q = parseQuery(field, t);
                        synonyms.add(q, BooleanClause.Occur.SHOULD);
					} else {
						Query q = new TermQuery(new Term(field, t));
						synonyms.add(q, BooleanClause.Occur.SHOULD);
					}
				}
				pq.add(synonyms, BooleanClause.Occur.MUST);
			}
            synonymStack.push(term.toString());
		}

		if (synonymStack.size() > 0) {
			BooleanQuery synonyms = new BooleanQuery();
			while (synonymStack.size() > 0) {
				String t = synonymStack.pop();
				if (t.length() > 1 && !t.equals(query)) {
					Query q = parseQuery(field, t);
					synonyms.add(q, BooleanClause.Occur.SHOULD);
				} else {
					Query q = new TermQuery(new Term(field, t));
					synonyms.add(q, BooleanClause.Occur.SHOULD);
				}
			}
			pq.add(synonyms, BooleanClause.Occur.MUST);
		}

		qry.add(pq, BooleanClause.Occur.SHOULD);

		Query tq = this.parseQuery(field, query);
		qry.add(tq, BooleanClause.Occur.SHOULD);

		return qry;
	}

	private Query parseQuery(String field, String phrase) throws Exception {
		TokenStream stream = this.analyzer.tokenStream(field,
				new StringReader(phrase));


		CharTermAttribute term = stream
				.addAttribute(CharTermAttribute.class);
		PhraseQuery pq = new PhraseQuery();
        try {
            stream.reset();
            while (stream.incrementToken()) {
                pq.add(new Term(field, term.toString()));
            }
            stream.end();
        } finally {
            stream.close();
        }
        return pq;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Analyzer getPhraseAanlyzer() {
		return phraseAanlyzer;
	}

	public void setPhraseAanlyzer(Analyzer phraseAanlyzer) {
		this.phraseAanlyzer = phraseAanlyzer;
	}

	public String getDefaultField() {
		return defaultField;
	}

	public void setDefaultField(String defaultField) {
		this.defaultField = defaultField;
	}
}

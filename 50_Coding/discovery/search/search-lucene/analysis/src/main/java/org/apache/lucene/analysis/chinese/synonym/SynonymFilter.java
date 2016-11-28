package org.apache.lucene.analysis.chinese.synonym;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.Stack;

public final class SynonymFilter extends TokenFilter {
	public static final String TOKEN_TYPE_SYNONYM = "SYNONYM";
	private Stack<String> synonymStack;
	private SynonymEngine engine;
	private State current;
	private final CharTermAttribute termAtt;
	private final PositionIncrementAttribute posIncrAtt;

	public SynonymFilter(TokenStream in, SynonymEngine engine) {
		super(in);
		synonymStack = new Stack<String>();
		this.engine = engine;
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (synonymStack.size() > 0) {
			String syn = synonymStack.pop();
			restoreState(current);
			//termAtt.setTermBuffer(syn);
            termAtt.append(syn);
			posIncrAtt.setPositionIncrement(0);
			return true;
		}
		
		if (!input.incrementToken())
			return false;
		
		if(this.addAliasesToStack()){
			current = captureState();
		}	
		
		return true;
	}

	private boolean addAliasesToStack() throws IOException {
		String[] synonyms = engine.getSynonyms(termAtt.toString());
		if (synonyms == null) {
			return false;
		}
		for (String synonym : synonyms) {
			synonymStack.push(synonym);
		}
		return true;
	}

}

package org.apache.lucene.analysis.chinese.synonym;

import java.io.IOException;

public interface SynonymEngine {
	public String[] getSynonyms(String s) throws IOException;
}

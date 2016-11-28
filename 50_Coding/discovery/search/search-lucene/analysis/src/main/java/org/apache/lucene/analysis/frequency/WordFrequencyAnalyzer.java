package org.apache.lucene.analysis.frequency;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

import java.io.Reader;

public final class WordFrequencyAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return new TokenStreamComponents(new WordFrequencyTokenizer(Version.LUCENE_40,reader));
    }
}

package org.apache.lucene.analysis.frequency;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/29/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordFrequencyTokenizerFactory extends TokenizerFactory {


    public WordFrequencyTokenizerFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory attributeFactory, Reader reader) {
        return new WordFrequencyTokenizer(Version.LUCENE_40,reader);
    }
}

package org.apache.lucene.analysis.chinese;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

import java.io.Reader;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/29/12
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChineseTokenizerFactory extends TokenizerFactory {


    public ChineseTokenizerFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory attributeFactory, Reader reader) {
        return new ChineseTokenizer(reader);
    }
}

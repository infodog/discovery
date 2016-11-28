package org.apache.lucene.analysis.chinese;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 9/29/12
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestChineseTokenizerFactory extends BaseTokenStreamTestCase {

    public void testChineseTokenizer() throws Exception {
        Reader reader = new StringReader("广州信景技术有限公司");
        Map<String, String> args = Collections.emptyMap();
        ChineseTokenizerFactory factory = new ChineseTokenizerFactory(args);

        Tokenizer stream = factory.create(reader);
//        while(stream.incrementToken()) {
//            CharTermAttribute t = stream.addAttribute(CharTermAttribute.class);
//            System.out.println(t.toString());
//        }

        assertTokenStreamContents(stream,
                new String[] {"广州", "信", "景", "技术", "有限公司" });
    }

}

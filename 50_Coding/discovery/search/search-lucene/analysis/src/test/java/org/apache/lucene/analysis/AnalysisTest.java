package org.apache.lucene.analysis;

import org.apache.lucene.analysis.pattern.PatternTokenizerFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 11/14/13
 * Time: 4:11 PM
 */
public class AnalysisTest {
    @Test
    public void testPattern() throws Exception {

        Map<String, String> args = new HashMap<String, String>();
        args.put(PatternTokenizerFactory.GROUP, "0");
        args.put(PatternTokenizerFactory.PATTERN, "(.)");
        Reader reader = new StringReader("我是中文aa;bb;cc 111234456");

        PatternTokenizerFactory tokFactory = new PatternTokenizerFactory(args);

        TokenStream ts = tokFactory.create(reader);


        OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);

        try {
            ts.reset(); // Resets this stream to the beginning. (Required)
            while (ts.incrementToken()) {
                // Use AttributeSource.reflectAsString(boolean)
                // for token stream debugging.
                System.out.println("token: " + ts.reflectAsString(false));

//                System.out.println("token start offset: " + offsetAtt.startOffset());
//                System.out.println("  token end offset: " + offsetAtt.endOffset());
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
        } finally {
            ts.close(); // Release resources associated with this stream.
        }
    }
}

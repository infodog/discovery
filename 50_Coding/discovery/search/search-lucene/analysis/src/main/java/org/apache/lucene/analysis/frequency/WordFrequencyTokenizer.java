package org.apache.lucene.analysis.frequency;

import com.google.common.base.Splitter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public final class WordFrequencyTokenizer extends Tokenizer {

    private String word = null;
    private int freq = 0;

    private CharTermAttribute termAtt;

    public WordFrequencyTokenizer(Version matchVersion, Reader input) {
        super(input);
        termAtt = addAttribute(CharTermAttribute.class);
    }

//    public WordFrequencyTokenizer(Version matchVersion, AttributeSource source, Reader input) {
//        super(source, input);
//        termAtt = addAttribute(CharTermAttribute.class);
//    }

    public WordFrequencyTokenizer(Version matchVersion, AttributeFactory factory, Reader input) {
        super(factory, input);
        termAtt = addAttribute(CharTermAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {

        if (word == null) {
            StringBuilder buffer = new StringBuilder();
            int ci = input.read();
            char ch = (char) ci;
            while (true) {
                if (ci == -1) {
                    break;
                }
                buffer.append(ch);
                ci = input.read();
                ch = (char) ci;
            }

            String item = buffer.toString().toLowerCase();
            Iterable<String> words = Splitter.on("::").split(item);
            Iterator<String> list = words.iterator();
            if (list.hasNext()) {
                word = list.next();
            }

            if (list.hasNext()) {
                try {
                    freq = Integer.parseInt(list.next());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    freq = 1;
                }
            } else {
                freq = 1;
            }

        }


        if (freq == 0 || word == null) {
            return false;
        } else {
            this.clearAttributes();
            freq--;
            termAtt.setLength(word.length());
            termAtt.setEmpty().append(word);
            return true;
        }

    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.word = null;
        this.freq = 0;
    }
}

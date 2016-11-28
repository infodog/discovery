package org.apache.lucene.analysis.chinese;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.chinese.dictionary.Dictionary;
import org.apache.lucene.analysis.chinese.dictionary.WordType;
import org.apache.lucene.analysis.chinese.hhmm.HHMMSegmenter;
import org.apache.lucene.analysis.chinese.hhmm.SegToken;
import org.apache.lucene.analysis.chinese.hhmm.SegTokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class ChineseTokenizer extends Tokenizer {

    public ChineseTokenizer(Reader input) {
        super(input);
        this.init();
    }

    public ChineseTokenizer(Reader input, Dictionary dict) {
        super(input);
        this.hhmmSegmenter = new HHMMSegmenter(dict);
        this.init();
    }

    private HHMMSegmenter hhmmSegmenter = new HHMMSegmenter();
    private SegTokenFilter tokenFilter = new SegTokenFilter();
    private Iterator<SegToken> tokenIter;
    private CharTermAttribute termAtt;
    private OffsetAttribute offsetAtt;
    private TypeAttribute typeAtt;
    private int finalOffset = 0;

    private void init() {
        termAtt = addAttribute(CharTermAttribute.class);
        offsetAtt = addAttribute(OffsetAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (tokenIter == null) {
            StringBuilder buffer = new StringBuilder();
            int ci = input.read();
            char ch = (char) ci;
            //TODO Are there any performance problems?
            while (true) {
                if (ci == -1) {
                    break;
                }
                buffer.append(ch);
                ci = input.read();
                ch = (char) ci;
            }

            String sentence = buffer.toString().toLowerCase();

            finalOffset = sentence.length();

            List<SegToken> segTokenList = hhmmSegmenter.process(sentence);

            List<SegToken> result = Collections.emptyList();

            //TODO:?
            if (segTokenList.size() > 2) {
                result = segTokenList.subList(1, segTokenList.size() - 1);
            }

            int max = 0;
            for (SegToken st : result) {
                convertSegToken(st, sentence, 0);
                //finalOffset = st.endOffset;
                if(max < st.endOffset){
                    max = st.endOffset;
                }
            }

            if(finalOffset < max) {
                finalOffset = max;
            }

            this.tokenIter = result.iterator();
        }


        if (this.tokenIter == null || !tokenIter.hasNext()) {
            return false;
        }

        this.clearAttributes();


        SegToken nextWord = tokenIter.next();
        //termAtt.setTermBuffer(nextWord.charArray, 0, nextWord.charArray.length);
        termAtt.copyBuffer(nextWord.charArray, 0, nextWord.charArray.length);
        offsetAtt.setOffset(nextWord.startOffset, nextWord.endOffset);
        typeAtt.setType(String.valueOf(nextWord.wordType));
        return true;
    }


    public SegToken convertSegToken(SegToken st, String sentence,
                                    int sentenceStartOffset) {

        switch (st.wordType) {
            case WordType.STRING:
            case WordType.NUMBER:
            case WordType.FULLWIDTH_NUMBER:
            case WordType.FULLWIDTH_STRING:
                st.charArray = sentence.substring(st.startOffset, st.endOffset)
                        .toCharArray();
                break;
            default:
                break;
        }

        st = tokenFilter.filter(st);
        st.startOffset += sentenceStartOffset;
        st.endOffset += sentenceStartOffset;
        return st;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenIter = null;
    }

    /**
     * TODO check it out
     * @author benzhao
     * @date 1/14/13
     * @time 2:57 PM
     *
     */


    //    @Override
//    public void setReader(Reader input) throws IOException {
//        super.setReader(input);
//
//        reset();
//    }

    @Override
    public void end() {
        // set final offset
        offsetAtt.setOffset(finalOffset, finalOffset);
    }
}

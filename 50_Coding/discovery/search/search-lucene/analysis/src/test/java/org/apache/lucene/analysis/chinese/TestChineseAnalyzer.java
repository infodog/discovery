package org.apache.lucene.analysis.chinese;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.chinese.dictionary.SearchDictionary;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestChineseAnalyzer extends BaseTokenStreamTestCase {

	@Before
	public void setup() {
		SearchDictionary.getInstance().putIntoDictionary("3g", 1000);
		SearchDictionary.getInstance().putIntoDictionary("16g", 1000);
		SearchDictionary.getInstance().putIntoDictionary("c5", 1000);
		SearchDictionary.getInstance().putIntoDictionary("n8", 1000);
		SearchDictionary.getInstance().putIntoDictionary("电商", 1000);
		SearchDictionary.getInstance().putIntoDictionary("e66", 1000);
	}

	@Test
	public void testLetters() throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "i3g";
		String result[] = {"i","3g"};
		assertAnalyzesTo(ca, sentence, result);
	}

	@Test
	public void testOneLetter() throws Exception {
		Analyzer ca = new ChineseAnalyzer();

        String digit = "5";
        String result1[] = {"5"};
        assertAnalyzesTo(ca, digit, result1);


        String sentence = "i";
		String result[] = {"i"};
		assertAnalyzesTo(ca, sentence, result);


		String hanzi = "含";
		String resulthanzi[] = {"含"};
		assertAnalyzesTo(ca, hanzi, resulthanzi);
	}

	@Test
	public void testTwoLetters() throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "e4";
		String result[] = {"e","4"};
		assertAnalyzesTo(ca, sentence, result);
	}

	@Test
	public void testTitleWithModel() throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "诺基亚（NOKIA）C5-03 3G手机（黑色音乐版）WCDMA/GSM 非定制机";
		String result[] = {"诺基亚","nokia","c5","03","3g","手机","黑色","音乐","版","wcdma","gsm","非","定制","机"};
		assertAnalyzesTo(ca, sentence, result);
	}


//	@Test
//	public void testTitle() throws Exception {
//		Analyzer ca = new ChineseAnalyzer();
//		String sentence = "苹果（APPLE）iphone4 3G手机16G（白色）WCDMA/GSM 非定制机 n8电商  e66";
//		String result[] = {"苹果","apple","iphone","4","3g","手机","16g","白色","wcdma","gsm","非","定制","机","n8","电商","e66"};
//		assertAnalyzesTo(ca, sentence, result);
//	}

	@Test
	public void testChineseStopWordsDefault() throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "我购买了道具和服装。";
		String result[] = { "我", "购买", "了", "道具", "和", "服装" };
		assertAnalyzesTo(ca, sentence, result);
	}


    @Test
    public void testChinese() throws IOException {
        Analyzer ca = new ChineseAnalyzer();
        String sentence = "美的（airmate）壁扇FW4022A（蓝白色）";
        String result[] = {"美", "的", "airmate", "壁", "扇", "fw","4022","a", "蓝","白色"};
        assertAnalyzesTo(ca, sentence, result);
    }

	@Test
	public void testChineseStopWordsDefaultTwoPhrases() throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "我购买了道具和服装。 我购买了道具和服装。";
		String result[] = { "我", "购买", "了", "道具", "和", "服装", "我", "购买", "了",
				"道具", "和", "服装" };
		assertAnalyzesTo(ca, sentence, result);
	}

	@Test
	public void testChineseStopWordsDefaultTwoPhrasesIdeoSpace()
			throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "我购买了道具和服装　我购买了道具和服装。";
		String result[] = { "我", "购买", "了", "道具", "和", "服装", "我", "购买", "了",
				"道具", "和", "服装" };
		assertAnalyzesTo(ca, sentence, result);
	}

	@Test
	public void testChineseStopWords2() throws Exception {
		Analyzer ca = new ChineseAnalyzer();
		String sentence = "Title:San"; // : is a stopword
		String result[] = { "title", "san" };
		int startOffsets[] = { 0, 6 };
		int endOffsets[] = { 5, 9 };
		//int posIncr[] = { 1, 2 };
		assertAnalyzesTo(ca, sentence, result, startOffsets, endOffsets);
	}

	@Test
	public void testMixedLatinChinese() throws Exception {
		assertAnalyzesTo(new ChineseAnalyzer(), "我购买 Tests 了道具和服装",
				new String[] { "我", "购买", "tests", "了", "道具", "和", "服装" });

		assertAnalyzesTo(new ChineseAnalyzer(), "我购买了一台sony电视机",
				new String[] { "我", "购买", "了","一","台", "sony","电视机"});
	}

	@Test
	public void testNumerics() throws Exception {
		assertAnalyzesTo(
				new ChineseAnalyzer(),
				"我购买 Tests 了道具和服装1234",
				new String[] { "我", "购买", "tests", "了", "道具", "和", "服装", "1234" });
	}

	@Test
	public void testFullWidth() throws Exception {
		assertAnalyzesTo(
				new ChineseAnalyzer(),
				"我购买 Ｔｅｓｔｓ 了道具和服装１２３４",
				new String[] { "我", "购买", "tests", "了", "道具", "和", "服装", "1234" });
	}

	@Test
	public void testNonChinese() throws Exception {
		assertAnalyzesTo(new ChineseAnalyzer(), "我购买 روبرتTests 了道具和服装",
				new String[] { "我", "购买", "ر", "و", "ب", "ر", "ت", "tests", "了",
						"道具", "和", "服装" });
	}

//	@Test
//	public void testOOV() throws Exception {
//		assertAnalyzesTo(new ChineseAnalyzer(), "优素福·拉扎·吉拉尼", new String[] {
//				"优", "素", "福", "拉", "扎", "吉", "拉", "尼" });
//
//		assertAnalyzesTo(new ChineseAnalyzer(), "优素福拉扎吉拉尼", new String[] { "优",
//				"素", "福", "拉", "扎", "吉", "拉", "尼" });
//	}

	@Test
	public void testOffsets() throws Exception {
		assertAnalyzesTo(new ChineseAnalyzer(), "我购买了道具和服装 ", new String[] {
				"我", "购买", "了", "道具", "和", "服装" },
				new int[] { 0, 1, 3, 4, 6, 7 }, new int[] { 1, 3, 4, 6, 7, 9 });
	}

    public void testEmail() throws Exception {
        ChineseAnalyzer chinese = new ChineseAnalyzer();
        String sentence = "ben@me.com";
        int[] startOffsets = {0,4,7};
        int[] endOffsets = {3,6,10};
        String r[] = { "ben", "me","com"};

        assertAnalyzesTo(chinese,sentence,r,startOffsets,endOffsets);
        assertAnalyzesTo(chinese,sentence,r,startOffsets,endOffsets);
        assertAnalyzesTo(chinese,sentence,r,startOffsets,endOffsets);
        assertAnalyzesTo(chinese,sentence,r,startOffsets,endOffsets);


    }
}

package org.apache.lucene.search.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {
    public static String getPinYin(String zhongwen)
            throws BadHanyuPinyinOutputFormatCombination {

        String zhongWenPinYin = "";
        char[] chars = zhongwen.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],
                    getDefaultOutputFormat());
            // 当转换不是中文字符时,返回null
            if (pinYin != null) {
                zhongWenPinYin += capitalize(pinYin[0]);
            } else {
                zhongWenPinYin += chars[i];
            }
        }
        return zhongWenPinYin.toLowerCase().replace(" ", "");
    }

    public static String abbreviate(String chinese) throws BadHanyuPinyinOutputFormatCombination {
        String abbrev = "";
        char[] chars = chinese.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],
                    getDefaultOutputFormat());
            if (pinYin != null) {
                try {
                    abbrev += pinYin[0].toCharArray()[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return abbrev.toLowerCase().replace(" ", "");
    }

    //支持多音字 如 空调   返回 kongkongdiaotiao
    public static String getPinYinEx(String zhongwen) throws BadHanyuPinyinOutputFormatCombination {
        String zhongWenPinYin = "";
        char[] chars = zhongwen.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],
                    getDefaultOutputFormat());
            // 当转换不是中文字符时,返回null
            Map<String, Boolean> identical = new HashMap<String, Boolean>();
            if (pinYin != null) {
                for (String py : pinYin) {
                    if (py != null) {
                        Boolean exits = identical.get(py);
                        if (exits == null) {
                            zhongWenPinYin += py;
                            identical.put(py, true);
                        }
                    }
                }
            } else {
                zhongWenPinYin += chars[i];
            }
        }
        return zhongWenPinYin;
    }

    /**
     * Default Format 默认输出格式
     *
     * @return
     */
    public static HanyuPinyinOutputFormat getDefaultOutputFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 没有音调数字
        format.setVCharType(HanyuPinyinVCharType.WITH_V);// u显示
        return format;
    }

    /**
     * Capitalize 首字母大写
     *
     * @param s
     * @return
     */
    public static String capitalize(String s) {
        char ch[];
        ch = s.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        String newString = new String(ch);
        return newString;
    }
}
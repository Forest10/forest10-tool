package forest10.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author Forest10
 * 2017/12/7
 */
public class ChineseToEnglish {


	/**
	 * 将汉字转换为全拼
	 *
	 * @param src
	 * @return
	 * @throws BadHanyuPinyinOutputFormatCombination
	 */
	public static String getPingYin(String src) throws BadHanyuPinyinOutputFormatCombination {

		char[] t1;
		t1 = src.toCharArray();
		String[] t2;
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();

		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		for (int i = 0; i < t0; i++) {
			// 判断是否为汉字字符
			if (java.lang.Character.toString(t1[i]).matches(
					"[\\u4E00-\\u9FA5]+")) {
				t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
				t4 += t2[0];
			} else {
				t4 += java.lang.Character.toString(t1[i]);
			}
		}
		return t4;
	}

	/**
	 * 返回中文的首字母
	 *
	 * @param str
	 * @return
	 */
	public static String getPinYinHeadChar(String str) {

		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert;
	}


}

package forest10.encrypt;


import java.security.MessageDigest;

/**
 * MD5操作类
 *
 * @author Forest10
 * 2017/12/7
 */
public class Md5Util {
	private final static String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "a", "b", "c", "d", "e", "f"};

	/**
	 * 转换字节数组为16进制字串
	 *
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuilder resultSb = new StringBuilder();
		for (byte aB : b) {
			resultSb.append(byteToHexString(aB));
		}
		return resultSb.toString();
	}

	/**
	 * 转换byte到16进制
	 *
	 * @param b 要转换的byte
	 * @return 16进制格式
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return HEX_DIGITS[d1] + HEX_DIGITS[d2];
	}

	/**
	 * MD5编码
	 *
	 * @param origin 原始字符串
	 * @return 经过MD5加密之后的结果
	 */
	public static String md5Encode(String origin) {
		String resultString = null;
		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("md5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}

}

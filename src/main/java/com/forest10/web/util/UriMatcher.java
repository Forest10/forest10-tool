package com.forest10.web.util;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Forest10
 * @date 2018/9/19 下午2:23
 */
public class UriMatcher {


	public static boolean match(String[] needMatchUrl, String uri) {

		for (String url : needMatchUrl) {
			url = StringUtils.trimToEmpty(url);
			if (url.endsWith("*")) {
				String sub = url.substring(0, url.length() - 1);
				if (uri.startsWith(sub)) {
					return true;
				}
			} else if (uri.equals(url)) {
				return true;
			}
		}
		return false;
	}
}


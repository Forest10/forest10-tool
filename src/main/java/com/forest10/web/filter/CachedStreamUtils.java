package com.forest10.web.filter;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:19
 */
@Slf4j
public class CachedStreamUtils {

	public static void checkCacheSizeParam(int initCacheSize, int maxCacheSize) {
		if (initCacheSize <= 0) {
			throw new IllegalArgumentException("init cache size is invalid!");
		}
		if (maxCacheSize <= 0) {
			throw new IllegalArgumentException("max cache size is valid!");
		}
		if (initCacheSize > maxCacheSize) {
			throw new IllegalArgumentException("init cache is large than max cache size!!");
		}
	}


	public static void safeWrite(OutputStream out, int val) {
		try {
			out.write(val);
		} catch (IOException e) {
			log.debug("", e);
		}
	}
}
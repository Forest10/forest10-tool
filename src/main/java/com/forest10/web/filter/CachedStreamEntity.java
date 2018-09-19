package com.forest10.web.filter;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:17
 */
public abstract interface CachedStreamEntity {
	public abstract CachedStream getCachedStream();

	public abstract void flushStream();
}

package com.forest10.web.filter;

import java.io.Closeable;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:18
 */

public abstract interface CachedStream extends Closeable {
	public abstract byte[] getCached();
}

package com.forest10.web.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:18
 */
public class CachedInputStream extends ServletInputStream implements CachedStream {
	private ByteArrayOutputStream cachedOutputStream;
	private HttpServletRequest request;
	private int maxCacheSize;

	public CachedInputStream(HttpServletRequest request, int initCacheSize, int maxCacheSize) {
		CachedStreamUtils.checkCacheSizeParam(initCacheSize, maxCacheSize);
		this.request = request;
		this.cachedOutputStream = new ByteArrayOutputStream(initCacheSize);
		this.maxCacheSize = maxCacheSize;
	}

	@Override
	public int read()
			throws IOException {
		int b = this.request.getInputStream().read();
		if ((b != -1) && (this.cachedOutputStream.size() < this.maxCacheSize)) {
			CachedStreamUtils.safeWrite(this.cachedOutputStream, b);
		}
		return b;
	}

	@Override
	public byte[] getCached() {
		return this.cachedOutputStream.toByteArray();
	}

	@Override
	public void close()
			throws IOException {
		super.close();
		this.cachedOutputStream.close();
	}

	@Override
	public boolean isFinished() {
		try {
			return this.request.getInputStream().isFinished();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isReady() {
		try {
			return this.request.getInputStream().isReady();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		try {
			this.request.getInputStream().setReadListener(readListener);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
package forest10.web.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:20
 */
public class CachedOutputStream extends ServletOutputStream implements CachedStream {
	private ByteArrayOutputStream cachedOutputStream;
	private ServletOutputStream srcOutputStream;
	private int maxCacheSize;

	public CachedOutputStream(ServletOutputStream srcOutputStream, int initCacheSize, int maxCacheSize) {
		CachedStreamUtils.checkCacheSizeParam(initCacheSize, maxCacheSize);
		this.srcOutputStream = srcOutputStream;
		this.cachedOutputStream = new ByteArrayOutputStream(initCacheSize);
		this.maxCacheSize = maxCacheSize;
	}

	@Override
	public byte[] getCached() {
		return this.cachedOutputStream.toByteArray();
	}

	@Override
	public void write(int b)
			throws IOException {
		this.srcOutputStream.write(b);
		if (this.cachedOutputStream.size() < this.maxCacheSize) {
			CachedStreamUtils.safeWrite(this.cachedOutputStream, b);
		}
	}

	@Override
	public void close()
			throws IOException {
		super.close();
		this.cachedOutputStream.close();
	}

	@Override
	public boolean isReady() {
		return this.srcOutputStream.isReady();
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		this.srcOutputStream.setWriteListener(writeListener);
	}
}
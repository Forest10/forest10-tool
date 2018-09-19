package forest10.web.filter;


import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:17
 */
public class CachedHttpServletRequestWrapper extends HttpServletRequestWrapper implements CachedStreamEntity {
	private CachedInputStream cachedInputStream;

	public CachedHttpServletRequestWrapper(HttpServletRequest httpServletRequest, int initCacheSize, int maxCacheSize)
			throws IOException {
		super(httpServletRequest);
		this.cachedInputStream = new CachedInputStream(httpServletRequest, initCacheSize, maxCacheSize);
	}

	@Override
	public ServletInputStream getInputStream()
			throws IOException {
		return this.cachedInputStream;
	}

	@Override
	public CachedStream getCachedStream() {
		return this.cachedInputStream;
	}

	@Override
	public void flushStream() {
	}
}
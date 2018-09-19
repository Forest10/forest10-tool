package com.forest10.web.filter.realize;

import com.forest10.web.filter.CachedHttpServletRequestWrapper;
import com.forest10.web.filter.CachedHttpServletResponseWrapper;
import com.forest10.web.util.UriMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Forest10
 * 你可以指定哪些URL不进行处理{@see excludeUri},
 * OncePerRequestFilter保证此filter执行唯一性
 */
@Slf4j
public class LogFilter extends OncePerRequestFilter {

	private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();
	private static final int MAX_CACHE_LEN = 2 * 1024 * 1024;
	private static final int INIT_CACHE_LEN = 512 * 1024;
	private static final int LIMIT_BODY_LENGTH = 2000;
	private static final String LOG_FMT = "method[%s %s] user[%s %s] header[%s] req[%s] resp[%s] time[%sms]";
	private String[] excludeUris;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//是否是文件上传,防止 request双重 cache, 造成内存使用过大
		if (UriMatcher.match(excludeUris, request.getRequestURI()) || ServletFileUpload.isMultipartContent(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		//使用 stopWatch 用于记录时间
		StopWatch clock = new StopWatch();
		clock.start();
		//获取 cache request和 response
		CachedHttpServletRequestWrapper httpServletRequestWrapper = new CachedHttpServletRequestWrapper(request,
				INIT_CACHE_LEN, MAX_CACHE_LEN);
		CachedHttpServletResponseWrapper httpServletResponseWrapper = new CachedHttpServletResponseWrapper(response,
				INIT_CACHE_LEN, MAX_CACHE_LEN);
		try {
			filterChain.doFilter(httpServletRequestWrapper, httpServletResponseWrapper);
		} finally {
			clock.stop();
			saveLogData(httpServletRequestWrapper, httpServletResponseWrapper, clock.getTotalTimeMillis());
		}
	}

	private void saveLogData(CachedHttpServletRequestWrapper requestWrapper,
	                         CachedHttpServletResponseWrapper responseWrapper, long time) throws UnsupportedEncodingException {
		try {
			// 如果使用了Writer就需要刷新流
			requestWrapper.flushStream();
			responseWrapper.flushStream();
			byte[] requestData = requestWrapper.getCachedStream().getCached();
			byte[] responseData = responseWrapper.getCachedStream().getCached();
			//判断是 requestParam 或 requestBody
			String requestBody = requestData == null ? StringUtils.EMPTY : new String(requestData);
			String responseBody = responseData == null ? StringUtils.EMPTY : new String(responseData);
			// 处理请求参数map 注意这里返回的map不能更改，所以需要复制一份
			Map<String, String[]> params = Maps.newHashMap(requestWrapper.getParameterMap());
			String paramString = params.entrySet().stream()
					.map(entry -> entry.getKey() + "=" + COMMA_JOINER.join(entry.getValue()))
					.collect(Collectors.joining("&"));
			//兼容 encode url
			if (StringUtils.equals(requestWrapper.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
				paramString = URLDecoder.decode(paramString, "UTF-8");
			}
			// 构造请求header
			Enumeration<String> headerNames = requestWrapper.getHeaderNames();
			Map<String, String> headerMap = Maps.newHashMap();
			while (headerNames.hasMoreElements()) {
				String key = headerNames.nextElement();
				String value = requestWrapper.getHeader(key);
				headerMap.put(key, value);
			}
			String method = requestWrapper.getMethod();
			String url = requestWrapper.getRequestURL().toString();
			String userId = StringUtils.EMPTY;
			String userName = StringUtils.EMPTY;
			String requestHeader = headerMap.isEmpty() ? StringUtils.EMPTY : headerMap.toString();
			requestBody = MapUtils.isEmpty(params) ? requestBody : paramString;
			responseBody = responseBody.length() < LIMIT_BODY_LENGTH ?
					responseBody.replaceAll("\n|\r", "") : StringUtils.EMPTY;
			//打印 log
			log.info(String.format(LOG_FMT, method, url, userId, userName, requestHeader, requestBody, responseBody, time));
		} finally {
			IOUtils.closeQuietly(requestWrapper.getCachedStream());
			IOUtils.closeQuietly(responseWrapper.getCachedStream());
		}
	}

	public void setExcludeUri(String[] excludeUris) {
		this.excludeUris = excludeUris;
	}
}
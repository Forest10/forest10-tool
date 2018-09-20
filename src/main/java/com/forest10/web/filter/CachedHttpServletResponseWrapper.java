package com.forest10.web.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:21
 */
public class CachedHttpServletResponseWrapper extends HttpServletResponseWrapper implements CachedStreamEntity {

    private CachedOutputStream cachedOutputStream;
    private PrintWriter printWriter;

    public CachedHttpServletResponseWrapper(HttpServletResponse cachedOutputStream, int initCacheSize, int maxCacheSize)
        throws IOException {
        super(cachedOutputStream);
        this.cachedOutputStream =
            new CachedOutputStream(cachedOutputStream.getOutputStream(), initCacheSize, maxCacheSize);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return this.cachedOutputStream;
    }

    @Override
    public CachedStream getCachedStream() {
        return this.cachedOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (this.printWriter == null) {
            this.printWriter = new PrintWriter(new OutputStreamWriter(this.cachedOutputStream, getCharacterEncoding()));
        }
        return this.printWriter;
    }

    @Override
    public void flushStream() {
        if (this.printWriter != null) {
            this.printWriter.flush();
        }
    }
}
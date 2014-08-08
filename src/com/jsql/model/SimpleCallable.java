package com.jsql.model;
import java.util.concurrent.Callable;

import com.jsql.view.GUIMediator;

/**
 * Callable for parallelized HTTP tasks
 * url: SQL query
 * content: source code of the web page
 * tag: store user information (ex. current index)
 */
public class SimpleCallable implements Callable<SimpleCallable> {
    public String url, content, tag;
    SimpleCallable(String url) {
        this.url = url;
    }

    SimpleCallable(String url, String tag) {
        this(url);
        this.tag = tag;
    }

    @Override
    public SimpleCallable call() throws Exception {
        content = GUIMediator.model().inject(url);
        return this;
    }
}
package com.jsql.model;
import java.util.concurrent.Callable;

import com.jsql.view.GUIMediator;

/**
 * Callable for parallelized HTTP tasks
 * url: SQL query
 * content: source code of the web page
 * tag: store user information (ex. current index)
 */
public class SourceCodeCallable implements Callable<SourceCodeCallable> {
    /**
     * URL to load.
     */
    public String url;

    /**
     * Source code for current page.
     */
    public String content;

    /**
     * Character used for current page.
     */
    public String insertionCharacter;
    
    /**
     * Create a callable to get initial query or insertion character.
     * @param url
     */
    public SourceCodeCallable(String url) {
        this.url = url;
    }

    /**
     * Create callable for current insertion character test.
     * @param url
     * @param insertionCharacter
     */
    public SourceCodeCallable(String url, String insertionCharacter) {
        this(url);
        this.insertionCharacter = insertionCharacter;
    }

    @Override
    public SourceCodeCallable call() throws Exception {
        this.content = GUIMediator.model().inject(this.url);
        return this;
    }
}
package com.jsql.model.suspendable;

import java.util.concurrent.Callable;

import com.jsql.model.MediatorModel;

/**
 * Callable for parallelized HTTP tasks
 * url: SQL query
 * content: source code of the web page
 * tag: store user information (ex. current index)
 */
public class CallableHTMLPage implements Callable<CallableHTMLPage> {
    /**
     * URL to load.
     */
    private String url;

    /**
     * Source code for current page.
     */
    private String content = "";

    /**
     * Character used for current page.
     */
    private String insertionCharacter;
    
    /**
     * Create a callable to get initial query or insertion character.
     * @param url
     */
    public CallableHTMLPage(String url) {
        this.url = url;
    }

    /**
     * Create callable for current insertion character test.
     * @param url
     * @param insertionCharacter
     */
    public CallableHTMLPage(String url, String insertionCharacter) {
        this(url);
        this.insertionCharacter = insertionCharacter;
    }

    @Override
    public CallableHTMLPage call() throws Exception {
        this.content = MediatorModel.model().injectWithoutIndex(this.url);
        return this;
    }

    public String getUrl() {
        return url;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getInsertionCharacter() {
        return insertionCharacter;
    }
}
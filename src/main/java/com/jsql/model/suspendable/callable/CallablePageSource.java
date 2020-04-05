package com.jsql.model.suspendable.callable;

import java.util.concurrent.Callable;

import com.jsql.model.InjectionModel;

/**
 * Callable for concurrent HTTP tasks
 * url: SQL query
 * content: source code of the web page
 * tag: store user information (ex. current index)
 */
public class CallablePageSource implements Callable<CallablePageSource> {
    
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
    private String characterInsertion;
    
    private InjectionModel injectionModel;
    
    /**
     * Create a callable to get initial query or insertion character.
     * @param url
     * @param injectionModel
     */
    public CallablePageSource(String url, InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
        this.url = url;
    }

    /**
     * Create callable for current insertion character test.
     * @param url
     * @param characterInsertion
     * @param injectionModel2
     */
    public CallablePageSource(String url, String characterInsertion, InjectionModel injectionModel) {
        
        this(url, injectionModel);
        
        this.characterInsertion = characterInsertion;
    }
    
    @Override
    public CallablePageSource call() throws Exception {
        
        this.content = this.injectionModel.injectWithoutIndex(this.url);
        
        return this;
    }

    // Getters and setters

    public String getUrl() {
        return this.url;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getCharacterInsertion() {
        return this.characterInsertion;
    }
}
package com.jsql.model.injection;
import java.util.concurrent.Callable;

/**
 * Callable for parallelized HTTP tasks
 * url: SQL query
 * content: source code of the web page
 * tag: store user information (ex. current index)
 */
public class CallableSourceCode implements Callable<CallableSourceCode> {
    /**
     * URL to load.
     */
    private String url;

    /**
     * Source code for current page.
     */
    private String content;

    /**
     * Character used for current page.
     */
    private String insertionCharacter;
    
    /**
     * Create a callable to get initial query or insertion character.
     * @param url
     */
    public CallableSourceCode(String url) {
        this.url = url;
    }

    /**
     * Create callable for current insertion character test.
     * @param url
     * @param insertionCharacter
     */
    public CallableSourceCode(String url, String insertionCharacter) {
        this(url);
        this.insertionCharacter = insertionCharacter;
    }

    @Override
    public CallableSourceCode call() throws Exception {
        this.content = MediatorModel.model().inject(this.url);
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
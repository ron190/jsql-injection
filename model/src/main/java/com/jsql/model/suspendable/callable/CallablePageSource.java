package com.jsql.model.suspendable.callable;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

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
    private String query;
    private String metadataInjectionProcess;

    /**
     * Source code for current page.
     */
    private String content = StringUtils.EMPTY;

    /**
     * Character used for current page.
     */
    private String characterInsertion;
    
    private InjectionModel injectionModel;
    
    /**
     * Create a callable to get initial query or insertion character.
     * @param query
     * @param injectionModel
     */
    public CallablePageSource(String query, InjectionModel injectionModel, String metadataInjectionProcess) {
        
        this.query = query;
        this.injectionModel = injectionModel;
        this.metadataInjectionProcess = metadataInjectionProcess;
    }

    /**
     * Create callable for current insertion character test.
     * @param query
     * @param characterInsertion
     */
    public CallablePageSource(
        String query,
        String characterInsertion,
        InjectionModel injectionModel,
        String metadataInjectionProcess
    ) {
        
        this(query, injectionModel, metadataInjectionProcess);
        
        this.characterInsertion = characterInsertion;
    }
    
    @Override
    public CallablePageSource call() throws Exception {
        
        this.content = this.injectionModel.injectWithoutIndex(this.query, this.metadataInjectionProcess);
        
        return this;
    }

    
    // Getters and setters

    public String getQuery() {
        return this.query;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getCharacterInsertion() {
        return this.characterInsertion;
    }
}
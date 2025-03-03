package com.jsql.model.suspendable.callable;

import com.jsql.model.InjectionModel;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;

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
    private final String query;
    private final int nbIndex;
    private final String metadataInjectionProcess;

    /**
     * Source code for current page.
     */
    private String content = StringUtils.EMPTY;

    /**
     * Character used for current page.
     */
    private String characterInsertion;
    
    private final InjectionModel injectionModel;
    
    /**
     * Create a callable to get initial query or insertion character.
     */
    public CallablePageSource(String query, InjectionModel injectionModel, String metadataInjectionProcess, int nbIndex) {
        this.query = query;
        this.nbIndex = nbIndex;
        this.injectionModel = injectionModel;
        this.metadataInjectionProcess = metadataInjectionProcess;
    }

    /**
     * Create callable for current insertion character test.
     */
    public CallablePageSource(
        String query,
        String characterInsertion,
        InjectionModel injectionModel,
        String metadataInjectionProcess
    ) {
        this(query, injectionModel, metadataInjectionProcess, 0);
        this.characterInsertion = characterInsertion;
    }
    
    @Override
    public CallablePageSource call() {
        this.content = this.injectionModel.injectWithoutIndex(this.query, this.metadataInjectionProcess);
        return this;
    }

    
    // Getters

    public String getQuery() {
        return this.query;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getCharacterInsertion() {
        return this.characterInsertion;
    }

    public int getNbIndex() {
        return this.nbIndex;
    }
}
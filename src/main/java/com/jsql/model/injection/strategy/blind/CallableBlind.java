package com.jsql.model.injection.strategy.blind;

import java.util.LinkedList;
import java.util.List;

import com.jsql.model.MediatorModel;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

/**
 * Define a call HTTP to the server, require the associated url, character
 * position and bit. Opcodes represents the differences between
 * the TRUE page, and the resulting page.
 */
public class CallableBlind extends AbstractCallableBoolean<CallableBlind> {
    
    /**
     * List of differences found between the TRUE page, and the present page.
     */
    private LinkedList<Diff> opcodes = new LinkedList<>();
    
    private static final DiffMatchPatch DIFFMATCHPATCH = new DiffMatchPatch();

    /**
     * Constructor for preparation and blind confirmation.
     * @param inj
     */
    public CallableBlind(String inj) {
        this.blindUrl = MediatorModel.model().getVendor().instance().sqlTestBlind(inj);
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     */
    public CallableBlind(String inj, int indexCharacter, int bit) {
        this.blindUrl = MediatorModel.model().getVendor().instance().sqlBitTestBlind(inj, indexCharacter, bit);
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }
    
    /**
     * Constructor for length test.
     * @param inj
     * @param indexCharacter
     * @param isTestingLength
     */
    public CallableBlind(String inj, int indexCharacter, boolean isTestingLength) {
        this.blindUrl = MediatorModel.model().getVendor().instance().sqlLengthTestBlind(inj, indexCharacter);
        this.isTestingLength = isTestingLength;
    }

    /**
     * Check if a result page means the SQL query is true,
     * confirm that nothing in the resulting page is also defined
     * in the pages from every FALSE SQL queries.
     * @return true if the current SQL query is true
     */
    @Override
    public boolean isTrue() {
        for (Diff falseDiff: InjectionBlind.getConstantFalseMark()) {
            // Fix #4386: NullPointerException on contains()
            // opcodes is initialized to an empty new LinkedList<>()
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
        String ctnt = InjectionBlind.callUrl(this.blindUrl);
        this.opcodes = DIFFMATCHPATCH.diffMain(InjectionBlind.getBlankTrueMark(), ctnt, true);
        DIFFMATCHPATCH.diffCleanupEfficiency(this.opcodes);
        return this;
    }
    
    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
    
}

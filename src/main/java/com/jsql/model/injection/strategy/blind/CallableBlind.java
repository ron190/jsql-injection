package com.jsql.model.injection.strategy.blind;

import java.util.LinkedList;
import java.util.List;

import com.jsql.model.InjectionModel;
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
     * @param injectionBlind
     */
    InjectionBlind injectionBlind;
    public CallableBlind(String inj, InjectionModel injectionModel, InjectionBlind injectionBlind) {
        this.injectionModel = injectionModel;
        this.injectionBlind = injectionBlind;
        this.blindUrl = this.injectionModel.getVendor().instance().sqlTestBlind(inj);
    }
    
    /**
     * Constructor for bit test.
     * @param inj
     * @param indexCharacter
     * @param bit
     * @param injectionModel
     */
    InjectionModel injectionModel;
    public CallableBlind(String inj, int indexCharacter, int bit, InjectionModel injectionModel, InjectionBlind injectionBlind) {
        this.injectionBlind = injectionBlind;
        this.injectionModel = injectionModel;
        this.blindUrl = this.injectionModel.getVendor().instance().sqlBitTestBlind(inj, indexCharacter, bit);
        this.currentIndex = indexCharacter;
        this.currentBit = bit;
    }
    
    /**
     * Constructor for length test.
     * @param inj
     * @param indexCharacter
     * @param isTestingLength
     * @param injectionModel
     * @param injectionBlind2
     */
    public CallableBlind(String inj, int indexCharacter, boolean isTestingLength, InjectionModel injectionModel, InjectionBlind injectionBlind) {
        this.injectionBlind = injectionBlind;
        this.injectionModel = injectionModel;
        this.blindUrl = this.injectionModel.getVendor().instance().sqlLengthTestBlind(inj, indexCharacter);
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
        for (Diff falseDiff: this.injectionBlind.getConstantFalseMark()) {
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
        String ctnt = this.injectionBlind.callUrl(this.blindUrl);
        this.opcodes = DIFFMATCHPATCH.diffMain(this.injectionBlind.getBlankTrueMark(), ctnt, true);
        DIFFMATCHPATCH.diffCleanupEfficiency(this.opcodes);
        return this;
    }
    
    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
    
}

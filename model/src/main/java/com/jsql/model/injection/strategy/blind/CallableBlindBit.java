package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import name.fraser.neil.plaintext.diff_match_patch;
import static name.fraser.neil.plaintext.diff_match_patch.Diff;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Define a call HTTP to the server, require the associated url, character
 * position and bit. Diffs represent the differences between
 * the reference page, and the current page.
 */
public class CallableBlindBit extends AbstractCallableBit<CallableBlindBit> {
    
    private LinkedList<Diff> diffsWithReference = new LinkedList<>();  // List of differences found between the reference page, and the current page
    
    private static final diff_match_patch DIFF_MATCH_PATCH = new diff_match_patch();

    private final InjectionBlindBit injectionBlindBit;
    
    private final InjectionModel injectionModel;
    private final String metadataInjectionProcess;
    
    /**
     * Constructor for preparation and blind confirmation.
     */
    public CallableBlindBit(String sqlQuery, InjectionModel injectionModel, InjectionBlindBit injectionBlindBit, BlindOperator blindMode, String metadataInjectionProcess) {
        this.injectionModel = injectionModel;
        this.injectionBlindBit = injectionBlindBit;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlind(sqlQuery, blindMode);
    }
    
    /**
     * Constructor for bits test.
     */
    public CallableBlindBit(
        String sqlQuery,
        int indexChar,
        int bit,
        InjectionModel injectionModel,
        InjectionBlindBit injectionBlindBit,
        BlindOperator blindMode,
        String metadataInjectionProcess
    ) {
        this(sqlQuery, injectionModel, injectionBlindBit, blindMode, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlBitTestBlind(sqlQuery, indexChar, bit, blindMode);
        this.currentIndex = indexChar;
        this.currentBit = bit;
    }

    /**
     * Check if a result page means the SQL query is true,
     * confirm that nothing in the resulting page is also defined
     * in the pages from every FALSE SQL queries.
     * @return true if the current SQL query is true
     */
    @Override
    public boolean isTrue() {
        // Fix #95426: ConcurrentModificationException on iterator.next()
        List<Diff> falseDiffs = new CopyOnWriteArrayList<>(this.injectionBlindBit.getFalseDiffs());
        for (Diff falseDiff: falseDiffs) {
            // Fix #4386: NullPointerException on contains()
            // diffsWithReference is initialized to an empty new LinkedList<>()
            if (this.diffsWithReference.contains(falseDiff)) {
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
    public CallableBlindBit call() {
        String result = this.injectionBlindBit.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.diffsWithReference = CallableBlindBit.DIFF_MATCH_PATCH.diff_main(this.injectionBlindBit.getSourceReferencePage(), result, true);
        CallableBlindBit.DIFF_MATCH_PATCH.diff_cleanupEfficiency(this.diffsWithReference);
        return this;
    }
    
    public List<Diff> getDiffsWithReference() {
        return this.diffsWithReference;
    }
}

package com.jsql.model.injection.strategy.blind.callable;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.strategy.blind.InjectionBlindBit;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

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
    private static final DiffMatchPatch DIFF_MATCH_PATCH = new DiffMatchPatch();
    private final InjectionBlindBit injectionBlind;

    private final InjectionModel injectionModel;
    private final String metadataInjectionProcess;

    /**
     * Constructor for preparation and blind confirmation.
     */
    public CallableBlindBit(String sqlQuery, InjectionModel injectionModel, InjectionBlindBit injectionBlind, BlindOperator blindOperator, String metadataInjectionProcess) {
        this.injectionModel = injectionModel;
        this.injectionBlind = injectionBlind;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlindWithOperator(sqlQuery, blindOperator);
    }

    /**
     * Constructor for bits test.
     */
    public CallableBlindBit(
        String sqlQuery,
        int indexChar,
        int bit,
        InjectionModel injectionModel,
        InjectionBlindBit injectionBlind,
        BlindOperator blindOperator,
        String metadataInjectionProcess
    ) {
        this(sqlQuery, injectionModel, injectionBlind, blindOperator, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlBlindBit(sqlQuery, indexChar, bit, blindOperator);
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
        List<Diff> falseDiffs = new CopyOnWriteArrayList<>(this.injectionBlind.getFalseDiffs());
        for (Diff falseDiff: falseDiffs) {  // ignored when false OR false => falsy empty
            try {  // Fix #96195: NullPointerException on contains()
                // Fix #4386: NullPointerException on contains(), diffsWithReference initialized to new LinkedList<>()
                if (this.diffsWithReference.contains(falseDiff)) {
                    return false;
                }
            } catch (NullPointerException e) {
                return false;
            }
        }
        List<Diff> trueDiffs = new CopyOnWriteArrayList<>(this.injectionBlind.getTrueDiffs());
        for (Diff trueDiff: trueDiffs) {
            try {  // Fix #96134: NullPointerException on contains()
                if (!this.diffsWithReference.contains(trueDiff)) {  // required, set to false when empty falseDiffs
                    return false;
                }
            } catch (NullPointerException e) {
                return false;
            }
        }
        return true;  // not in falseDiffs and in trueDiffs
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Build the list of differences found between TRUE and the current page.
     * @return Functional Blind Callable
     */
    @Override
    public CallableBlindBit call() {
        String result = this.injectionBlind.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.diffsWithReference = CallableBlindBit.DIFF_MATCH_PATCH.diffMain(this.injectionBlind.getSourceReferencePage(), result, true);
        CallableBlindBit.DIFF_MATCH_PATCH.diffCleanupEfficiency(this.diffsWithReference);
        return this;
    }

    public List<Diff> getDiffsWithReference() {
        return this.diffsWithReference;
    }
}

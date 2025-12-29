package com.jsql.model.injection.strategy.blind.callable;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.strategy.blind.InjectionBlindBin;
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
public class CallableBlindBin extends AbstractCallableBit<CallableBlindBin> {

    private final int low;
    private final int mid;
    private final int high;

    private LinkedList<Diff> diffsWithReference = new LinkedList<>();  // List of differences found between the reference page, and the current page
    private static final DiffMatchPatch DIFF_MATCH_PATCH = new DiffMatchPatch();
    private final InjectionBlindBin injectionBlind;

    private final String metadataInjectionProcess;

    /**
     * Constructor for preparation and blind confirmation.
     */
    public CallableBlindBin(
        String sqlQuery,
        InjectionModel injectionModel,
        InjectionBlindBin injectionBlind,
        BlindOperator blindOperator,
        int low, int mid, int high,
        String metadataInjectionProcess
    ) {
        this.isBinary = true;
        this.low = low;
        this.mid = mid;
        this.high = high;
        this.injectionBlind = injectionBlind;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlindWithOperator(sqlQuery, blindOperator);
    }

    /**
     * Constructor for bits test.
     */
    public CallableBlindBin(
        String sqlQuery,
        int indexChar,
        InjectionModel injectionModel,
        InjectionBlindBin injectionBlind,
        BlindOperator blindOperator,
        int low, int mid, int high,
        String metadataInjectionProcess
    ) {
        this(sqlQuery, injectionModel, injectionBlind, blindOperator, low, mid, high, metadataInjectionProcess);
        this.booleanUrl = injectionModel.getMediatorVendor().getVendor().instance().sqlBlindBin(sqlQuery, indexChar, mid, blindOperator);
        this.currentIndex = indexChar;
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
            // Fix #4386: NullPointerException on contains(), diffsWithReference initialized to new LinkedList<>()
            if (this.diffsWithReference.contains(falseDiff)) {
                return false;
            }
        }
        List<Diff> trueDiffs = new CopyOnWriteArrayList<>(this.injectionBlind.getTrueDiffs());
        for (Diff trueDiff: trueDiffs) {
            if (!this.diffsWithReference.contains(trueDiff)) {  // required, set to false when empty falseDiffs
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
    public CallableBlindBin call() {
        String result = this.injectionBlind.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.diffsWithReference = CallableBlindBin.DIFF_MATCH_PATCH.diffMain(this.injectionBlind.getSourceReferencePage(), result, true);
        CallableBlindBin.DIFF_MATCH_PATCH.diffCleanupEfficiency(this.diffsWithReference);
        return this;
    }

    public List<Diff> getDiffsWithReference() {
        return this.diffsWithReference;
    }

    public int getLow() {
        return this.low;
    }

    public int getMid() {
        return this.mid;
    }

    public int getHigh() {
        return this.high;
    }
}

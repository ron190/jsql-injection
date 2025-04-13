package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import name.fraser.neil.plaintext.diff_match_patch;
import static name.fraser.neil.plaintext.diff_match_patch.Diff;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CallableMultibit extends AbstractCallableBinary<CallableMultibit> {

    private LinkedList<Diff> diffsWithReference = new LinkedList<>();

    private static final diff_match_patch DIFF_MATCH_PATCH = new diff_match_patch();

    private final InjectionMultibit injectionMultibit;

    private final String metadataInjectionProcess;

    public CallableMultibit(String sqlQuery, InjectionMultibit injectionMultibit, String metadataInjectionProcess) {
        this.injectionMultibit = injectionMultibit;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = sqlQuery;
        this.isMultibit = true;
    }

    public CallableMultibit(
        String sqlQuery,
        int indexCharacter,
        int block,
        InjectionModel injectionModel,
        InjectionMultibit injectionMultibit,
        String metadataInjectionProcess
    ) {
        this(
            injectionModel.getMediatorVendor().getVendor().instance().sqlMultibit(
                sqlQuery,
                indexCharacter,
                3 * block - 2
            ),
            injectionMultibit,
            metadataInjectionProcess
        );
        this.block = block;
        this.currentIndex = indexCharacter;
    }

    @Override
    public CallableMultibit call() {
        String result = this.injectionMultibit.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.diffsWithReference = CallableMultibit.DIFF_MATCH_PATCH.diff_main(this.injectionMultibit.getSourceReference(), result, true);
        CallableMultibit.DIFF_MATCH_PATCH.diff_cleanupEfficiency(this.diffsWithReference);

        this.diffsWithReference.removeAll(this.injectionMultibit.getDiffsCommonWithAllIds());

        for (int i = 0; i < this.injectionMultibit.getDiffsById().size() ; i++) {
            if (new HashSet<>(this.injectionMultibit.getDiffsById().get(i)).containsAll(this.diffsWithReference)) {
                this.idPage = i;
            }
        }
        return this;
    }

    @Override
    public boolean isTrue() {
        return false;  // ignored
    }

    public List<Diff> getDiffsWithReference() {
        return this.diffsWithReference;
    }
    public int getIdPage() {
        return this.idPage;
    }
}

package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CallableMultibit extends AbstractCallableBoolean<CallableMultibit> {

    private LinkedList<Diff> diffsWithReference = new LinkedList<>();

    private static final DiffMatchPatch DIFFMATCHPATCH = new DiffMatchPatch();

    private final InjectionMultibit injectionMultibit;

    private final String metadataInjectionProcess;

    public CallableMultibit(String sqlQuery, InjectionMultibit injectionMultibit, String metadataInjectionProcess) {

        this.injectionMultibit = injectionMultibit;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = sqlQuery;
        this.isMultibit = true;
    }

    public CallableMultibit(String sqlQuery, int indexCharacter, int block, InjectionModel injectionModel, InjectionMultibit injectionMultibit, String metadataInjectionProcess) {

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

        this.diffsWithReference = DIFFMATCHPATCH.diffMain(this.injectionMultibit.getSourceReference(), result, true);

        DIFFMATCHPATCH.diffCleanupEfficiency(this.diffsWithReference);

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
        // ignored
        return false;
    }

    public List<Diff> getDiffsWithReference() {
        return this.diffsWithReference;
    }
    public int getIdPage() {
        return this.idPage;
    }
}

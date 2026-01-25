package com.jsql.model.injection.strategy.blind.callable;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.InjectionMultibit;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

import java.util.LinkedList;
import java.util.List;

public class CallableMultibit extends AbstractCallableBit<CallableMultibit> {

    private LinkedList<Diff> diffsWithReference = new LinkedList<>();

    private static final DiffMatchPatch DIFF_MATCH_PATCH = new DiffMatchPatch();

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
        int indexChar,
        int block,
        InjectionModel injectionModel,
        InjectionMultibit injectionMultibit,
        String metadataInjectionProcess
    ) {
        this(
            injectionModel.getMediatorEngine().getEngine().instance().sqlMultibit(
                sqlQuery,
                indexChar,
                3 * block - 2
            ),
            injectionMultibit,
            metadataInjectionProcess
        );
        this.block = block;
        this.currentIndex = indexChar;
    }

    @Override
    public CallableMultibit call() {
        String result = this.injectionMultibit.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.diffsWithReference = CallableMultibit.DIFF_MATCH_PATCH.diffMain(this.injectionMultibit.getSourceReference(), result, true);
        CallableMultibit.DIFF_MATCH_PATCH.diffCleanupEfficiency(this.diffsWithReference);

        this.diffsWithReference.removeAll(this.injectionMultibit.getDiffsCommonWithAllIds());

        for (int i = 0 ; i < this.injectionMultibit.getDiffsById().size() ; i++) {
            if (this.injectionMultibit.getDiffsById().get(i).containsAll(this.diffsWithReference)) {  // quick-fix HashSet wrapping not working
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
}

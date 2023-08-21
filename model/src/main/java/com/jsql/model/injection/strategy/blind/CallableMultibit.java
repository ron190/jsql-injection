package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CallableMultibit extends AbstractCallableBoolean<CallableMultibit> {

    private LinkedList<Diff> opcodes = new LinkedList<>();

    private static final DiffMatchPatch DIFFMATCHPATCH = new DiffMatchPatch();

    private final InjectionMultibit injectionMultibit;

    private final InjectionModel injectionModel;
    private final String metadataInjectionProcess;
    protected boolean isMultibit = true;

    public CallableMultibit(String sqlQuery, InjectionModel injectionModel, InjectionMultibit injectionMultibit, String metadataInjectionProcess) {

        this.injectionModel = injectionModel;
        this.injectionMultibit = injectionMultibit;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = sqlQuery;
    }

    public CallableMultibit(String sqlQuery, int indexCharacter, int block, InjectionModel injectionModel, InjectionMultibit injectionMultibit, String metadataInjectionProcess) {

        this.injectionModel = injectionModel;
        this.injectionMultibit = injectionMultibit;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = this.injectionModel.getMediatorVendor().getVendor().instance().sqlMultibit(
            sqlQuery,
            indexCharacter,
            3 * block - 2
        );
        this.block = block;
        this.currentIndex = indexCharacter;
    }

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

    @Override
    public boolean isTrue() {
        // ignored
        return false;
    }

    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
    public int getIdPage() {
        return this.idPage;
    }
    public boolean isMultibit() {
        return isMultibit;
    }
}

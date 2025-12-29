package com.jsql.model.injection.strategy.blind.callable;

import com.jsql.model.injection.strategy.blind.InjectionVendor;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CallableVendor extends AbstractCallableBit<CallableVendor> {

    private LinkedList<Diff> opcodes = new LinkedList<>();  // List of differences found between the reference page, and the present page

    private static final DiffMatchPatch DIFF_MATCH_PATCH = new DiffMatchPatch();

    private final InjectionVendor injectionCharInsertion;

    private final String metadataInjectionProcess;

    public CallableVendor(String inj, InjectionVendor injectionCharInsertion, String metadataInjectionProcess) {
        this.injectionCharInsertion = injectionCharInsertion;
        this.metadataInjectionProcess = metadataInjectionProcess;
        this.booleanUrl = inj;
    }

    @Override
    public boolean isTrue() {
        List<Diff> copyTrueMarks = new CopyOnWriteArrayList<>(this.injectionCharInsertion.getConstantTrueMark());
        for (Diff trueDiff: copyTrueMarks) {
            if (!this.opcodes.contains(trueDiff)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CallableVendor call() {
        String source = this.injectionCharInsertion.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.opcodes = CallableVendor.DIFF_MATCH_PATCH.diffMain(
            this.injectionCharInsertion.getBlankFalseMark(),
            source,
            false
        );
        CallableVendor.DIFF_MATCH_PATCH.diffCleanupEfficiency(this.opcodes);
        return this;
    }

    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
}

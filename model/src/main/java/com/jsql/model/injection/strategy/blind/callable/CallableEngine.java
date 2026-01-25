package com.jsql.model.injection.strategy.blind.callable;

import com.jsql.model.injection.strategy.blind.InjectionEngine;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.model.injection.strategy.blind.patch.DiffMatchPatch;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CallableEngine extends AbstractCallableBit<CallableEngine> {

    private LinkedList<Diff> opcodes = new LinkedList<>();  // List of differences found between the reference page, and the present page

    private static final DiffMatchPatch DIFF_MATCH_PATCH = new DiffMatchPatch();

    private final InjectionEngine injectionCharInsertion;

    private final String metadataInjectionProcess;

    public CallableEngine(String inj, InjectionEngine injectionCharInsertion, String metadataInjectionProcess) {
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
    public CallableEngine call() {
        String source = this.injectionCharInsertion.callUrl(this.booleanUrl, this.metadataInjectionProcess, this);
        this.opcodes = CallableEngine.DIFF_MATCH_PATCH.diffMain(
            this.injectionCharInsertion.getBlankFalseMark(),
            source,
            false
        );
        CallableEngine.DIFF_MATCH_PATCH.diffCleanupEfficiency(this.opcodes);
        return this;
    }

    public List<Diff> getOpcodes() {
        return this.opcodes;
    }
}

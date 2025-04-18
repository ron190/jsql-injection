package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.injection.strategy.blind.callable.AbstractCallableBit;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractInjectionMonobit<T extends AbstractCallableBit<T>> extends AbstractInjectionBit<T> {

    protected AbstractInjectionMonobit(InjectionModel injectionModel, BlindOperator blindOperator) {
        super(injectionModel, blindOperator);
    }
    
    abstract T getCallableBitTest(String sqlQuery, int indexChar, int bit);

    public void initNextChar(
            String sqlQuery,
            List<char[]> bytes,
            AtomicInteger indexChar,
            CompletionService<T> taskCompletionService,
            AtomicInteger countTasksSubmitted,
            AtomicInteger countBadAsciiCode,
            T currentCallable
    ) {
        indexChar.incrementAndGet();
        
        // New undefined bits of the next character
        // Chars all have the last bit set to 0 in Ascii table
        bytes.add(AbstractInjectionBit.getBitsUnset());
        
        // Test the 7 bits for the next character, save its position and current bit for later
        // Ignore last bit 128 and only check for first seven bits
        for (int bit: new int[]{ 1, 2, 4, 8, 16, 32, 64 }) {
            taskCompletionService.submit(
                this.getCallableBitTest(
                    sqlQuery,
                    indexChar.get(),
                    bit
                )
            );
            countTasksSubmitted.addAndGet(1);
        }
    }

    public char[] initMaskAsciiChar(List<char[]> bytes, T currentCallable) {
        char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);  // bits for current url
        int positionInMask = (int) (
            8 - (Math.log(2) + Math.log(currentCallable.getCurrentBit())) / Math.log(2)  // some math (2^x => x)
        );
        asciiCodeMask[positionInMask] = currentCallable.isTrue() ? '1' : '0';  // set current bit
        return asciiCodeMask;
    }
}

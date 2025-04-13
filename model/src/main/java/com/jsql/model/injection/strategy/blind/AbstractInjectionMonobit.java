package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractInjectionMonobit<T extends AbstractCallableBinary<T>> extends AbstractInjectionBinary<T> {

    protected AbstractInjectionMonobit(InjectionModel injectionModel, BinaryMode binaryMode) {
        super(injectionModel, binaryMode);
    }
    
    abstract T getCallableBitTest(String sqlQuery, int indexCharacter, int bit);

    public void initNextChars(
        String sqlQuery,
        List<char[]> bytes,
        AtomicInteger indexCharacter,
        CompletionService<T> taskCompletionService,
        AtomicInteger countTasksSubmitted,
        T currentCallable
    ) {
        indexCharacter.incrementAndGet();
        
        // New undefined bits of the next character
        // Chars all have the last bit set to 0 in Ascii table
        bytes.add(new char[]{ '0', 'x', 'x', 'x', 'x', 'x', 'x', 'x' });
        
        // Test the 7 bits for the next character, save its position and current bit for later
        // Ignore last bit 128 and only check for first seven bits
        for (int bit: new int[]{ 1, 2, 4, 8, 16, 32, 64 }) {
            taskCompletionService.submit(
                this.getCallableBitTest(
                    sqlQuery,
                    indexCharacter.get(),
                    bit
                )
            );
            countTasksSubmitted.addAndGet(1);
        }
    }

    public char[] initBinaryMask(List<char[]> bytes, T currentCallable) {
        char[] asciiCodeMask = bytes.get(currentCallable.getCurrentIndex() - 1);  // bits for current url
        int positionInMask = (int) (
            8 - (Math.log(2) + Math.log(currentCallable.getCurrentBit())) / Math.log(2)  // some math (2^x => x)
        );
        asciiCodeMask[positionInMask] = currentCallable.isTrue() ? '1' : '0';  // set current bit
        return asciiCodeMask;
    }
}

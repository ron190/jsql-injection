package com.jsql.model.suspendable.callable;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryCallable implements ThreadFactory {
    
    private final String nameThread;
    
    public ThreadFactoryCallable(String nameThread) {
        
        this.nameThread = nameThread;
    }
    
    @Override
    public Thread newThread(Runnable runnable) {
        
        return new Thread(runnable, this.nameThread);
    }
}

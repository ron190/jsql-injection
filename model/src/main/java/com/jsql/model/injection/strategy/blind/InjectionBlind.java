package com.jsql.model.injection.strategy.blind;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.strategy.blind.patch.Diff;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A blind attack class using concurrent threads.
 */
public class InjectionBlind extends AbstractInjectionMonobit<CallableBlind> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    // Source code of the TRUE web page (usually ?id=1)
    private String sourceReferencePage;

    /**
     * List of string differences found in all the FALSE queries, compared
     * to the reference page. Each FALSE pages should contain
     * at least one same string, which shouldn't be present in all
     * the TRUE queries.
     */
    private List<Diff> falseDiffs = new ArrayList<>();

    /**
     * Create blind attack initialization.
     * If every false diffs are not in true diffs and every true diffs are in
     * true diffs, then Blind attack is confirmed.
     * @param blindMode
     */
    public InjectionBlind(InjectionModel injectionModel, BooleanMode blindMode) {
        
        super(injectionModel, blindMode);
        
        // No blind
        if (this.falseTests.isEmpty() || this.injectionModel.isStoppedByUser()) {
            
            return;
        }
        
        // Call the SQL request which must be TRUE (usually ?id=1)
        this.sourceReferencePage = this.callUrl(StringUtils.EMPTY, "blind#ref");

        // Concurrent calls to the FALSE statements,
        // it will use inject() from the model
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindTagFalse");
        
        Collection<CallableBlind> callablesFalseTest = new ArrayList<>();
        
        for (String falseTest: this.falseTests) {
            
            callablesFalseTest.add(new CallableBlind(falseTest, injectionModel, this, blindMode, "blind#falsy"));
        }
        
        // Delete junk from the results of FALSE statements,
        // keep only diffs found in each and every FALSE pages.
        // Allow the user to stop the loop
        try {
            List<Future<CallableBlind>> futuresFalseTest = taskExecutor.invokeAll(callablesFalseTest);
            
            taskExecutor.shutdown();
            
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }

            for (Future<CallableBlind> futureFalseTest: futuresFalseTest) {

                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }

                if (this.falseDiffs.isEmpty()) {
                    // Init diffs
                    this.falseDiffs = futureFalseTest.get().getDiffsWithReference();
                } else {
                    // Clean unmatching diffs
                    this.falseDiffs.retainAll(futureFalseTest.get().getDiffsWithReference());
                }
            }
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }

        if (this.injectionModel.isStoppedByUser()) {
            return;
        }
        
        this.cleanTrueDiffs(injectionModel, blindMode);
    }

    private void cleanTrueDiffs(InjectionModel injectionModel, BooleanMode blindMode) {
        
        // Concurrent calls to the TRUE statements,
        // it will use inject() from the model.
        ExecutorService taskExecutor = this.injectionModel.getMediatorUtils().getThreadUtil().getExecutor("CallableGetBlindTagTrue");

        Collection<CallableBlind> callablesTrueTest = new ArrayList<>();
        
        for (String trueTest: this.trueTests) {
            
            callablesTrueTest.add(new CallableBlind(trueTest, injectionModel, this, blindMode, "blind#truthy"));
        }
        
        // Remove TRUE diffs in the FALSE diffs, because
        // a significant FALSE statement shouldn't contain any TRUE diff.
        // Allow the user to stop the loop.
        try {
            List<Future<CallableBlind>> futuresTrueTest = taskExecutor.invokeAll(callablesTrueTest);
            
            taskExecutor.shutdown();
            if (!taskExecutor.awaitTermination(15, TimeUnit.SECONDS)) {
                
                taskExecutor.shutdownNow();
            }
        
            for (Future<CallableBlind> futureTrueTest: futuresTrueTest) {
                
                if (this.injectionModel.isStoppedByUser()) {
                    return;
                }
                this.falseDiffs.removeAll(futureTrueTest.get().getDiffsWithReference());
            }
            
        } catch (ExecutionException e) {
            
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            
        } catch (InterruptedException e) {
            
            LOGGER.log(LogLevelUtil.IGNORE, e, e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public CallableBlind getCallableBitTest(String sqlQuery, int indexCharacter, int bit) {

        return new CallableBlind(sqlQuery, indexCharacter, bit, this.injectionModel, this, this.booleanMode, "bit#" + indexCharacter + "~" + bit);
    }

    @Override
    public boolean isInjectable() throws StoppedByUserSlidingException {
        
        if (this.injectionModel.isStoppedByUser()) {
            throw new StoppedByUserSlidingException();
        }
        
        var blindTest = new CallableBlind(
            this.injectionModel.getMediatorVendor().getVendor().instance().sqlTestBooleanInitialization(),
            this.injectionModel,
            this,
            this.booleanMode,
            "blind#confirm"
        );
        
        try {
            blindTest.call();
        } catch (Exception e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
        }

        return blindTest.isTrue() && !this.falseDiffs.isEmpty();
    }

    @Override
    public String getInfoMessage() {
        
        return "- Strategy Blind: query True when Diffs are matching " + this.falseDiffs + "\n\n";
    }
    
    
    // Getter and setter

    public String getSourceReferencePage() {
        return this.sourceReferencePage;
    }
    
    public List<Diff> getFalseDiffs() {
        return this.falseDiffs;
    }
}

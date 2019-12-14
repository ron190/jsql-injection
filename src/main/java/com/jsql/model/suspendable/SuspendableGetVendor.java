package com.jsql.model.suspendable;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.jsql.i18n.I18n;
import com.jsql.model.InjectionModel;
import com.jsql.model.InjectionModel.Vendor;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.callable.CallablePageSource;
import com.jsql.model.suspendable.callable.ThreadFactoryCallable;

/**
 * Runnable class, define insertionCharacter that will be used by all futures requests,
 * i.e -1 in "[..].php?id=-1 union select[..]", sometimes it's -1, 0', 0, etc,
 * this class/function tries to find the working one by searching a special error message
 * in the source page.
 */
public class SuspendableGetVendor extends AbstractSuspendable<Vendor> {
	
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    public SuspendableGetVendor(InjectionModel injectionModel) {
        super(injectionModel);
    }

    /**
     * 
     */
    @Override
    public Vendor run(Object... args) throws StoppedByUserSlidingException {
        Vendor vendor = null;
        
        if (this.injectionModel.getVendorByUser() != injectionModel.AUTO) {
            vendor = this.injectionModel.getVendorByUser();
            LOGGER.info(I18n.valueByKey("LOG_DATABASE_TYPE_FORCED_BY_USER") +" ["+ vendor +"]");
        } else {
        
            // Parallelize the search and let the user stops the process if needed.
            // SQL: force a wrong ORDER BY clause with an inexistent column, order by 1337,
            // and check if a correct error message is sent back by the server:
            //         Unknown column '1337' in 'order clause'
            // or   supplied argument is not a valid MySQL result resource
            ExecutorService taskExecutor = Executors.newCachedThreadPool(new ThreadFactoryCallable("CallableGetVendor"));
            CompletionService<CallablePageSource> taskCompletionService = new ExecutorCompletionService<>(taskExecutor);
            for (String insertionCharacter : new String[] {"'\"#-)'\""}) {
                taskCompletionService.submit(
                    new CallablePageSource(
                        insertionCharacter,
                        insertionCharacter,
                        injectionModel
                    )
                );
            }
    
            int total = 1;
            while (0 < total) {
    
                if (this.isSuspended()) {
                    throw new StoppedByUserSlidingException();
                }
                
                try {
                    CallablePageSource currentCallable = taskCompletionService.take().get();
                    total--;
                    String pageSource = currentCallable.getContent();
                    
                    for (Vendor vendorTest: injectionModel.vendors.stream().toArray(Vendor[]::new)) {
                      if (
                          pageSource.matches(
                              "(?si).*("
                              + vendorTest.instance().fingerprintErrorsAsRegex()
                              + ").*"
                          )
                      ) {
                          vendor = vendorTest;
                          LOGGER.debug("Found database ["+ vendor +"]");
                          break;
                      }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Interruption while determining the type of database", e);
                }
                
            }
            
            // End the job
            try {
                taskExecutor.shutdown();
                taskExecutor.awaitTermination(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
            
            if (vendor == null) {
                vendor = injectionModel.MYSQL;
                LOGGER.warn(I18n.valueByKey("LOG_DATABASE_TYPE_NOT_FOUND") +" ["+ vendor +"]");
            } else {
                LOGGER.info(I18n.valueByKey("LOG_USING_DATABASE_TYPE") +" ["+ vendor +"]");
                
                Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
                msgHeader.put(
                    Header.URL,
                    injectionModel.connectionUtil.getUrlBase()
                    + injectionModel.parameterUtil.getQueryStringFromEntries()
                );
                msgHeader.put(Header.VENDOR, vendor);
                
                Request requestDatabaseIdentified = new Request();
                requestDatabaseIdentified.setMessage(Interaction.DATABASE_IDENTIFIED);
                requestDatabaseIdentified.setParameters(msgHeader);
                this.injectionModel.sendToViews(requestDatabaseIdentified);
            }
        }
        
        Request requestSetVendor = new Request();
        requestSetVendor.setMessage(Interaction.SET_VENDOR);
        requestSetVendor.setParameters(vendor);
        this.injectionModel.sendToViews(requestSetVendor);
        
        return vendor;
    }
    
}
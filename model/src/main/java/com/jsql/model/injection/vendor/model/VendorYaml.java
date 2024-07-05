package com.jsql.model.injection.vendor.model;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;
import com.jsql.model.injection.vendor.model.yaml.Method;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.StringUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.jsql.model.accessible.DataAccess.*;

public class VendorYaml implements AbstractVendor {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * SQL characters marking the end of the result of an injection.
     * Process stops when this schema is encountered:
     * <pre>SQLix01x03x03x07
     */
    public static final String LEAD_HEX = "0x53714c69";
    public static final String TRAIL_SQL = "%01%03%03%07";
    public static final String TRAIL_HEX = "0x01030307";

    /**
     * SQL character used between each table cells.
     * Expected schema of multiple table cells :
     * <pre>
     * %04[table cell]%05[number of occurrences]%04%06%04[table cell]%05[number of occurrences]%04
     */
    public static final String SEPARATOR_CELL_SQL = "%06";
    public static final String SEPARATOR_CELL_HEX = "0x06";

    public static final String ENCLOSE_VALUE_HEX = "0x04";

    /**
     * SQL character used between the table cell and the number of occurrence of the cell text.
     * Expected schema of a table cell data is
     * <pre>%04[table cell]%05[number of occurrences]%04
     */
    public static final String SEPARATOR_QTE_SQL = "%05";
    public static final String SEPARATOR_QTE_HEX = "0x05";

    /**
     * SQL character enclosing a table cell returned by injection.
     * It allows to detect the correct end of a table cell data during parsing.
     * Expected schema of a table cell data is
     * <pre>%04[table cell]%05[number of occurrences]%04
     */
    public static final String ENCLOSE_VALUE_SQL = "%04";

    public static final String CALIBRATOR_SQL = "%23";
    public static final String CALIBRATOR_HEX = "0x23";
    
    public static final String FORMAT_INDEX = "1337%s7331";

    private static final String BOOLEAN_MODE = "${boolean.mode}";

    public static final String LIMIT = "${limit}";
    private static final String LIMIT_VALUE = "${limit.value}";

    private static final String RESULT_RANGE = "${result_range}";

    private static final String INDICE_UNIQUE = "${indice_unique}";

    private static final String CALIBRATOR = "${calibrator}";

    private static final String INDICES = "${indices}";
    public static final String INDICE = "${indice}";
    public static final String WINDOW_CHAR = "${window.char}";
    public static final String BLOCK_MULTIBIT = "${multibit.block}";

    public static final String WINDOW = "${window}";

    public static final String CAPACITY = "${capacity}";
    public static final String DEFAULT_CAPACITY = "65565";

    private static final String SLEEP_TIME = "${sleep_time}";

    private static final String BIT = "${bit}";

    public static final String INJECTION = "${injection}";

    private static final String TEST = "${test}";

    private static final String FILEPATH = "${filepath}";
    private static final String FILEPATH_HEX = "${filepath.hex}";

    private static final String BODY_HEX = "${body.hex}";

    private static final String FIELDS = "${fields}";
    private static final String FIELD = "${field.value}";

    private static final String TABLE = "${table}";
    private static final String DATABASE = "${database}";

    private static final String TABLE_HEX = "${table.hex}";
    private static final String DATABASE_HEX = "${database.hex}";
    
    private final ModelYaml modelYaml;
    
    private final InjectionModel injectionModel;
    
    public VendorYaml(String fileYaml, InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
        
        var yaml = new Yaml();
        this.modelYaml = yaml.loadAs(
            VendorYaml.class.getClassLoader().getResourceAsStream("vendor/"+ fileYaml),
            ModelYaml.class
        );
    }

    @Override
    public String sqlDatabases() {
        
        String sqlQuery = this.modelYaml.getResource().getSchema().getDatabase();
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getDatabase())) {
                sqlQuery = this.modelYaml.getResource().getDios().getDatabase();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but database query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getDatabase())) {
                sqlQuery = this.modelYaml.getResource().getZip().getDatabase();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but database query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        }
        
        return sqlQuery;
    }
    
    @Override
    public String sqlTables(Database database) {
        
        String sqlQuery = this.modelYaml.getResource().getSchema().getTable();
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getTable())) {
                sqlQuery = this.modelYaml.getResource().getDios().getTable();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but table query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getTable())) {
                sqlQuery = this.modelYaml.getResource().getZip().getTable();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but table query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        }
        
        String databaseUtf8 = Hex.encodeHexString(database.toString().getBytes(StandardCharsets.UTF_8));
        
        return sqlQuery
            .replace(DATABASE_HEX, databaseUtf8)
            .replace(DATABASE, database.toString());
    }

    @Override
    public String sqlColumns(Table table) {
        
        String sqlQuery = this.modelYaml.getResource().getSchema().getColumn();
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getColumn())) {
                sqlQuery = this.modelYaml.getResource().getDios().getColumn();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but column query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getColumn())) {
                sqlQuery = this.modelYaml.getResource().getZip().getColumn();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but column query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        }
        
        String databaseUtf8 = Hex.encodeHexString(table.getParent().toString().getBytes(StandardCharsets.UTF_8));
        String tableUtf8 = Hex.encodeHexString(table.toString().getBytes(StandardCharsets.UTF_8));
        
        return sqlQuery
            .replace(DATABASE_HEX, databaseUtf8)
            .replace(TABLE_HEX, tableUtf8)
            .replace(DATABASE, table.getParent().toString())
            .replace(TABLE, table.toString());
    }

    @Override
    public String sqlRows(String[] namesColumns, Database database, Table table) {
        
        String sqlField = this.modelYaml.getResource().getSchema().getRow().getFields().getField();
        String sqlConcatFields = this.modelYaml.getResource().getSchema().getRow().getFields().getConcat();
        String sqlQuery = this.modelYaml.getResource().getSchema().getRow().getQuery();
        
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getDatabase())) {

                sqlField = this.modelYaml.getResource().getDios().getRow().getFields().getField();
                sqlConcatFields = this.modelYaml.getResource().getDios().getRow().getFields().getConcat();
                sqlQuery = this.modelYaml.getResource().getDios().getRow().getQuery();
            
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but row query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getDatabase())) {

                sqlField = this.modelYaml.getResource().getZip().getRow().getFields().getField();
                sqlConcatFields = this.modelYaml.getResource().getZip().getRow().getFields().getConcat();
                sqlQuery = this.modelYaml.getResource().getZip().getRow().getQuery();
            
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but row query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorVendor().getVendor()
                );
            }
        }
        
        var matcherSqlField = Pattern.compile("(?s)(.*)"+ Pattern.quote(FIELD) +"(.*)").matcher(sqlField);
        String leadSqlField = StringUtils.EMPTY;
        String trailSqlField = StringUtils.EMPTY;
        
        if (matcherSqlField.find()) {
            
            leadSqlField = matcherSqlField.group(1);
            trailSqlField = matcherSqlField.group(2);
        }
        
        var namesColumnUtf8 = new String[namesColumns.length];
        
        for (var i = 0 ; i < namesColumns.length ; i++) {
            
            namesColumnUtf8[i] = StringUtil.detectUtf8(namesColumns[i]);
            namesColumnUtf8[i] = URLEncoder.encode(namesColumnUtf8[i], StandardCharsets.UTF_8);
        }
        
        var nameDatabaseUtf8 = StringUtil.detectUtf8(database.toString());
        nameDatabaseUtf8 = URLEncoder.encode(nameDatabaseUtf8, StandardCharsets.UTF_8);
        
        var nameTableUtf8 = StringUtil.detectUtf8(table.toString());
        nameTableUtf8 = URLEncoder.encode(nameTableUtf8, StandardCharsets.UTF_8);
        
        return sqlQuery.replace(
                FIELDS,
                leadSqlField
                + String.join(
                    trailSqlField + sqlConcatFields + leadSqlField,
                    namesColumnUtf8
                )
                + trailSqlField
            )
            .replace(DATABASE, nameDatabaseUtf8)
            .replace(TABLE, nameTableUtf8);
    }

    @Override
    public String sqlFileRead(String filePath) {
        return this.modelYaml.getResource().getFile().getRead()
            .replace(FILEPATH_HEX, Hex.encodeHexString(filePath.getBytes(StandardCharsets.UTF_8)))  // MySQL
            .replace(FILEPATH, filePath);  // PostgreSQL
    }

    @Override
    public String sqlTextIntoFile(String body, String path) {

        String visibleIndex = String.format(
            VendorYaml.FORMAT_INDEX,
            this.injectionModel.getMediatorStrategy().getSpecificNormal().getVisibleIndex()
        );

        return this.injectionModel.getIndexesInUrl()
            .replaceAll(
                visibleIndex,
                this.modelYaml.getResource().getFile().getWrite()
                .getBody()
                .replace(
                    BODY_HEX,
                    Hex.encodeHexString(body.getBytes(StandardCharsets.UTF_8))
                )
            )
            + StringUtils.SPACE
            + this.modelYaml.getResource().getFile().getWrite()
            .getPath()
            .replace(FILEPATH, path);
    }

    @Override
    public String sqlTestBlind(String check, BooleanMode blindMode) {

        String replacement = getMode(blindMode);

        return this.modelYaml.getStrategy().getBoolean()
            .getBlind()
            .replace(BOOLEAN_MODE, replacement)
            .replace(TEST, check)
            .trim();  // trim spaces in '${boolean.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlBitTestBlind(String inj, int indexCharacter, int bit, BooleanMode blindMode) {

        String replacement = getMode(blindMode);

        return this.modelYaml.getStrategy().getBoolean()
            .getBlind()
            .replace(BOOLEAN_MODE, replacement)
            .replace(
                TEST,
                this.modelYaml.getStrategy().getBoolean().getTest().getBit()
                .replace(INJECTION, inj)
                .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
                .replace(BIT, Integer.toString(bit))
            )
            .trim();  // trim spaces in '${boolean.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlTimeTest(String check, BooleanMode blindMode) {

        String replacement = getMode(blindMode);
        int countSleepTimeStrategy = this.injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingSleepTimeStrategy()
            ? this.injectionModel.getMediatorUtils().getPreferencesUtil().countSleepTimeStrategy()
            : 5;

        return this.modelYaml.getStrategy().getBoolean()
            .getTime()
            .replace(BOOLEAN_MODE, replacement)
            .replace(TEST, check)
            .replace(SLEEP_TIME, Long.toString(countSleepTimeStrategy))
            .trim();  // trim spaces in '${boolean.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit, BooleanMode blindMode) {

        String replacement = getMode(blindMode);
        int countSleepTimeStrategy = this.injectionModel.getMediatorUtils().getPreferencesUtil().isLimitingSleepTimeStrategy()
            ? this.injectionModel.getMediatorUtils().getPreferencesUtil().countSleepTimeStrategy()
            : 5;

        return this.modelYaml.getStrategy().getBoolean()
            .getTime()
            .replace(BOOLEAN_MODE, replacement)
            .replace(
                TEST,
                this.modelYaml.getStrategy().getBoolean().getTest()
                .getBit()
                .replace(INJECTION, inj)
                .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
                .replace(BIT, Integer.toString(bit))
            )
            .replace(SLEEP_TIME, Long.toString(countSleepTimeStrategy))
            .trim();  // trim spaces in '${boolean.mode} ${test}' when no mode, not covered by cleanSql()
    }

    private String getMode(BooleanMode blindMode) {

        String replacement;
        switch (blindMode) {
            case AND: replacement = this.modelYaml.getStrategy().getBoolean().getModeAnd(); break;
            case OR: replacement = this.modelYaml.getStrategy().getBoolean().getModeOr(); break;
            case STACKED: replacement = this.modelYaml.getStrategy().getBoolean().getModeStacked(); break;
            case NO_MODE:
            default: replacement = StringUtils.EMPTY; break;
        }
        return replacement;
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition, boolean isReport) {
        return VendorYaml.replaceTags(
            getSlidingWindow(isReport)
            .replace(INJECTION, sqlQuery)
            .replace(WINDOW_CHAR, startPosition)
            .replace(CAPACITY, DEFAULT_CAPACITY)
        );
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition, boolean isReport) {
        return VendorYaml.replaceTags(
            getSlidingWindow(isReport)
            .replace(INJECTION, sqlQuery)
            .replace(WINDOW_CHAR, startPosition)
            .replace(CAPACITY, DEFAULT_CAPACITY)
        );
    }

    @Override
    public String sqlMultibit(String inj, int indexCharacter, int block){
        return this.modelYaml.getStrategy().getBoolean().getMultibit()
            .replace(INJECTION, inj)
            .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
            .replace(BLOCK_MULTIBIT, Integer.toString(block));
    }

    @Override
    public String sqlErrorCalibrator(Method errorMethod) {
        return VendorYaml.replaceTags(
            errorMethod.getQuery()
            .replace(VendorYaml.WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
            .replace(VendorYaml.INJECTION, this.modelYaml.getStrategy().getConfiguration().getCalibrator())
            .replace(VendorYaml.WINDOW_CHAR, "1")
            .replace(VendorYaml.CAPACITY, Integer.toString(errorMethod.getCapacity()))
        );
    }

    @Override
    public String sqlErrorIndice(Method errorMethod) {

        var indexZeroToFind = "0";

        return VendorYaml.replaceTags(
            errorMethod.getQuery()
            .replace(VendorYaml.WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
            .replace(VendorYaml.INJECTION, this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, indexZeroToFind))
            .replace(VendorYaml.WINDOW_CHAR, "1")
            .replace(VendorYaml.CAPACITY, Integer.toString(errorMethod.getCapacity()))
        );
    }

    @Override
    public String sqlError(String sqlQuery, String startPosition, int indexMethodError, boolean isReport) {
        return VendorYaml.replaceTags(
            this.modelYaml.getStrategy().getError().getMethod().get(indexMethodError).getQuery()
            .replace(WINDOW, getSlidingWindow(isReport))
            .replace(INJECTION, sqlQuery)
            .replace(WINDOW_CHAR, startPosition)
            .replace(
                CAPACITY,
                Integer.toString(
                    this.modelYaml.getStrategy().getError()
                    .getMethod()
                    .get(indexMethodError)
                    .getCapacity()
                )
            )
        );
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition, boolean isReport) {
        return VendorYaml.replaceTags(
            getSlidingWindow(isReport)
            .replace(INJECTION, sqlQuery)
            .replace(WINDOW_CHAR, startPosition)
            .replace(CAPACITY, this.injectionModel.getMediatorStrategy().getNormal().getPerformanceLength())
        );
    }

    @Override
    public String sqlStacked(String sqlQuery, String startPosition, boolean isReport) {
        return this.modelYaml.getStrategy().getStacked().replace(
            WINDOW,
            VendorYaml.replaceTags(
                getSlidingWindow(isReport)
                .replace(INJECTION, sqlQuery)
                .replace(WINDOW_CHAR, startPosition)
                .replace(CAPACITY, DEFAULT_CAPACITY)
            )
        );
    }

    @Override
    public String sqlCapacity(String[] indexes) {

        String regexIndexes = String.join("|", indexes);
        String regexVisibleIndexesToFind = String.format(VendorYaml.FORMAT_INDEX, "(%s)");

        return this.injectionModel.getIndexesInUrl().replaceAll(
            String.format(regexVisibleIndexesToFind, regexIndexes),
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getNormal().getCapacity()
                .replace(CALIBRATOR, this.modelYaml.getStrategy().getConfiguration().getCalibrator())
                .replace(INDICE, "$1")
            )
        );
    }

    @Override
    public String sqlIndices(Integer nbFields) {
        
        String replaceTag = StringUtils.EMPTY;
        List<String> fields = new ArrayList<>();
        
        var indice = 1;
        
        for ( ; indice <= nbFields ; indice++) {
            
            String field = this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, Integer.toString(indice));
            fields.add(field);
            replaceTag = field;
        }
        
        indice--;
        
        return this.modelYaml.getStrategy().getNormal()
            .getIndices()
            .replace(
                INDICES,
                String.join(",", fields.toArray(new String[0]))
            )
            .replace(INDICE_UNIQUE, replaceTag)
            .replace(
                RESULT_RANGE,
                String.join(",", Collections.nCopies(indice, "r"))
            );
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        
        var limitBoundary = 0;
        
        try {
            limitBoundary = Integer.parseInt(this.modelYaml.getStrategy().getConfiguration().getLimitBoundary());
        } catch (NumberFormatException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect Limit start index, force to 0");
        }
        
        return this.modelYaml.getStrategy().getConfiguration()
            .getLimit()
            .replace(LIMIT_VALUE, Integer.toString(limitSQLResult + limitBoundary));
    }
    
    @Override
    public String fingerprintErrorsAsRegex() {
        return StringUtils.join(
            this.modelYaml.getStrategy().getConfiguration().getFingerprint()
            .getErrorMessage()
            .stream()
            .map(m -> ".*"+ m +".*")
            .toArray(),
            "|"
        );
    }
    
    public static String replaceTags(String sqlRequest) {
        return sqlRequest
            .replace("${enclose_value_sql}", ENCLOSE_VALUE_SQL)
            .replace("${enclose_value_hex}", ENCLOSE_VALUE_HEX)
            .replace("${separator_qte_sql}", SEPARATOR_QTE_SQL)
            .replace("${separator_qte_hex}", SEPARATOR_QTE_HEX)
            .replace("${separator_cell_sql}", SEPARATOR_CELL_SQL)
            .replace("${separator_cell_hex}", SEPARATOR_CELL_HEX)
            .replace("${calibrator_sql}", CALIBRATOR_SQL)
            .replace("${calibrator_hex}", CALIBRATOR_HEX)
            .replace("${trail_sql}", TRAIL_SQL)
            .replace("${trail_hex}", TRAIL_HEX)
            .replace("${lead}", LEAD)
            .replace("${lead_hex}", LEAD_HEX);
    }

    /**
     * Get payload with sliding window except for vulnerability report
     */
    private String getSlidingWindow(boolean isReport) {
        return isReport
            ? "(" + INJECTION + ")"
            : this.modelYaml.getStrategy().getConfiguration().getSlidingWindow();
    }
    
    
    // Getter and setter

    @Override
    public String sqlBooleanBlind() {
        return this.modelYaml.getStrategy().getBoolean().getBlind();
    }

    @Override
    public String sqlBooleanTime() {
        return this.modelYaml.getStrategy().getBoolean().getTime();
    }

    @Override
    public String sqlInfos() {
        return this.modelYaml.getResource().getInfo();
    }

    @Override
    public String sqlPrivilegeTest() {
        return this.modelYaml.getResource().getFile().getPrivilege();
    }

    @Override
    public List<String> getListFalseTest() {
        return this.modelYaml.getStrategy().getBoolean().getTest().getFalsy();
    }

    @Override
    public List<String> getListTrueTest() {
        return this.modelYaml.getStrategy().getBoolean().getTest().getTruthy();
    }

    @Override
    public String sqlTestBooleanInitialization() {
        return this.modelYaml.getStrategy().getBoolean().getTest().getInitialization();
    }

    @Override
    public String sqlOrderBy() {
        return this.modelYaml.getStrategy().getNormal().getOrderBy();
    }
    
    @Override
    public String endingComment() {
        if (this.injectionModel.getMediatorUtils().getPreferencesUtil().isUrlRandomSuffixDisabled()) {
            return this.modelYaml.getStrategy().getConfiguration().getEndingComment();
        } else {
            return this.modelYaml.getStrategy().getConfiguration().getEndingComment()
                // Allows Boolean match fingerprinting on host errors
                + RandomStringUtils.randomAlphanumeric(4);
        }
    }

    @Override
    public ModelYaml getModelYaml() {
        return this.modelYaml;
    }
}
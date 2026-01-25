package com.jsql.model.injection.engine.model;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.blind.AbstractInjectionBit.BlindOperator;
import com.jsql.model.injection.engine.model.yaml.Method;
import com.jsql.model.injection.engine.model.yaml.ModelYaml;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static com.jsql.model.accessible.DataAccess.*;

public class EngineYaml implements AbstractEngine {
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    /**
     * SQL characters marking the end of the result of an injection.
     * Process stops when this schema is encountered:
     * <pre>SqLix01x03x03x07
     */
    public static final String LEAD_HEX = "0x53714c69";
    public static final String LEAD_PIPE = "Sq'||'Li";
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

    public static final String CALIBRATOR_SQL = "a";
    public static final String CALIBRATOR_HEX = "0x61";
    
    public static final String FORMAT_INDEX = "1337%s7331";

    private static final String BINARY_MODE = "${binary.mode}";
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
    private static final String MID_CHR = "${mid}";
    private static final String MID_INT = "${mid.int}";
    public static final String INJECTION = "${injection}";
    public static final String TEST = "${test}";
    public static final String FILEPATH_HEX = "${filepath.hex}";
    private static final String FIELDS = "${fields}";
    private static final String FIELD = "${field.value}";
    private static final String TABLE = "${table}";
    private static final String DATABASE = "${database}";
    private static final String TABLE_HEX = "${table.hex}";
    private static final String DATABASE_HEX = "${database.hex}";
    private static final String DNS_DOMAIN = "${dns.domain}";
    private static final String DNS_RANDOM = "${dns.random}";

    private final ModelYaml modelYaml;
    private final InjectionModel injectionModel;
    
    public EngineYaml(String fileYaml, InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        var yaml = new Yaml();
        this.modelYaml = yaml.loadAs(
            EngineYaml.class.getClassLoader().getResourceAsStream("engine/" + fileYaml),
            ModelYaml.class
        );
    }

    @Override
    public String sqlDatabases() {
        String sqlQuery = this.modelYaml.getResource().getSchema().getDatabase();
        
        if (this.injectionModel.getMediatorUtils().preferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getDatabase())) {
                sqlQuery = this.modelYaml.getResource().getDios().getDatabase();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but database query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().preferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getDatabase())) {
                sqlQuery = this.modelYaml.getResource().getZip().getDatabase();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but database query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        }
        return sqlQuery;
    }
    
    @Override
    public String sqlTables(Database database) {
        String sqlQuery = this.modelYaml.getResource().getSchema().getTable();
        
        if (this.injectionModel.getMediatorUtils().preferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getTable())) {
                sqlQuery = this.modelYaml.getResource().getDios().getTable();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but table query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().preferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getTable())) {
                sqlQuery = this.modelYaml.getResource().getZip().getTable();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but table query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        }
        
        String databaseUtf8 = Hex.encodeHexString(database.toString().getBytes(StandardCharsets.UTF_8));
        return sqlQuery
            .replace(EngineYaml.DATABASE_HEX, databaseUtf8)
            .replace(EngineYaml.DATABASE, database.toString());
    }

    @Override
    public String sqlColumns(Table table) {
        String sqlQuery = this.modelYaml.getResource().getSchema().getColumn();
        
        if (this.injectionModel.getMediatorUtils().preferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getColumn())) {
                sqlQuery = this.modelYaml.getResource().getDios().getColumn();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but column query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().preferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getColumn())) {
                sqlQuery = this.modelYaml.getResource().getZip().getColumn();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but column query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        }
        
        String databaseUtf8 = Hex.encodeHexString(table.getParent().toString().getBytes(StandardCharsets.UTF_8));
        String tableUtf8 = Hex.encodeHexString(table.toString().getBytes(StandardCharsets.UTF_8));
        
        return sqlQuery
            .replace(EngineYaml.DATABASE_HEX, databaseUtf8)
            .replace(EngineYaml.TABLE_HEX, tableUtf8)
            .replace(EngineYaml.DATABASE, table.getParent().toString())
            .replace(EngineYaml.TABLE, table.toString());
    }

    @Override
    public String sqlRows(String[] namesColumns, Database database, Table table) {
        String sqlField = this.modelYaml.getResource().getSchema().getRow().getFields().getField();
        String sqlConcatFields = this.modelYaml.getResource().getSchema().getRow().getFields().getConcat();
        String sqlQuery = this.modelYaml.getResource().getSchema().getRow().getQuery();
        
        if (this.injectionModel.getMediatorUtils().preferencesUtil().isDiosStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getDios().getDatabase())) {
                sqlField = this.modelYaml.getResource().getDios().getRow().getFields().getField();
                sqlConcatFields = this.modelYaml.getResource().getDios().getRow().getFields().getConcat();
                sqlQuery = this.modelYaml.getResource().getDios().getRow().getQuery();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Dios] activated but row query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        } else if (this.injectionModel.getMediatorUtils().preferencesUtil().isZipStrategy()) {
            if (StringUtils.isNotBlank(this.modelYaml.getResource().getZip().getDatabase())) {
                sqlField = this.modelYaml.getResource().getZip().getRow().getFields().getField();
                sqlConcatFields = this.modelYaml.getResource().getZip().getRow().getFields().getConcat();
                sqlQuery = this.modelYaml.getResource().getZip().getRow().getQuery();
            } else {
                LOGGER.log(
                    LogLevelUtil.CONSOLE_INFORM,
                    "Strategy [Zip] activated but row query is undefined for [{}], fallback to default",
                    () -> this.injectionModel.getMediatorEngine().getEngine()
                );
            }
        }
        
        var matcherSqlField = Pattern.compile("(?s)(.*)"+ Pattern.quote(EngineYaml.FIELD) +"(.*)").matcher(sqlField);
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
                EngineYaml.FIELDS,
                leadSqlField
                + String.join(
                    trailSqlField + sqlConcatFields + leadSqlField,
                    namesColumnUtf8
                )
                + trailSqlField
            )
            .replace(EngineYaml.DATABASE, nameDatabaseUtf8)
            .replace(EngineYaml.TABLE, nameTableUtf8);
    }

    @Override
    public String sqlTestBlindWithOperator(String check, BlindOperator blindOperator) {
        String replacement = this.getMode(blindOperator);
        return this.modelYaml.getStrategy().getBinary().getBlind()
            .replace(EngineYaml.BINARY_MODE, replacement)
            .replace(EngineYaml.TEST, check)
            .trim();  // trim spaces in '${binary.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlBlindBit(String inj, int indexChar, int bit, BlindOperator blindOperator) {
        String replacement = this.getMode(blindOperator);
        return this.modelYaml.getStrategy().getBinary().getBlind()
            .replace(EngineYaml.BINARY_MODE, replacement)
            .replace(
                EngineYaml.TEST,
                this.modelYaml.getStrategy().getBinary().getTest().getBit()
                .replace(EngineYaml.INJECTION, inj)
                .replace(EngineYaml.WINDOW_CHAR, Integer.toString(indexChar))
                .replace(EngineYaml.BIT, Integer.toString(bit))
            )
            .trim();  // trim spaces in '${binary.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlBlindBin(String inj, int indexChar, int mid, BlindOperator blindOperator) {
        String replacement = this.getMode(blindOperator);
        return this.modelYaml.getStrategy().getBinary().getBlind()
            .replace(EngineYaml.BINARY_MODE, replacement)
            .replace(
                EngineYaml.TEST,
                this.modelYaml.getStrategy().getBinary().getTest().getBin()
                .replace(EngineYaml.INJECTION, inj)
                .replace(EngineYaml.WINDOW_CHAR, Integer.toString(indexChar))
                .replace(
                    EngineYaml.MID_CHR,
                    StringUtil.toUrl(Character.toString((char) mid).replace("'", "''"))  // escape quote
                )
                .replace(EngineYaml.MID_INT, String.valueOf(mid))
            )
            .trim();  // trim spaces in '${binary.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlTestTimeWithOperator(String check, BlindOperator blindOperator) {
        String replacement = this.getMode(blindOperator);
        int countSleepTimeStrategy = this.injectionModel.getMediatorUtils().preferencesUtil().isLimitingSleepTimeStrategy()
            ? this.injectionModel.getMediatorUtils().preferencesUtil().countSleepTimeStrategy()
            : 5;
        return this.modelYaml.getStrategy().getBinary().getTime()
            .replace(EngineYaml.BINARY_MODE, replacement)
            .replace(EngineYaml.TEST, check)
            .replace(EngineYaml.SLEEP_TIME, Long.toString(countSleepTimeStrategy))
            .trim();  // trim spaces in '${binary.mode} ${test}' when no mode, not covered by cleanSql()
    }

    @Override
    public String sqlTimeBit(String inj, int indexChar, int bit, BlindOperator blindOperator) {
        String replacement = this.getMode(blindOperator);
        int countSleepTimeStrategy = this.injectionModel.getMediatorUtils().preferencesUtil().isLimitingSleepTimeStrategy()
            ? this.injectionModel.getMediatorUtils().preferencesUtil().countSleepTimeStrategy()
            : 5;
        return this.modelYaml.getStrategy().getBinary().getTime()
            .replace(EngineYaml.BINARY_MODE, replacement)
            .replace(
                EngineYaml.TEST,
                this.modelYaml.getStrategy().getBinary().getTest()
                .getBit()
                .replace(EngineYaml.INJECTION, inj)
                .replace(EngineYaml.WINDOW_CHAR, Integer.toString(indexChar))
                .replace(EngineYaml.BIT, Integer.toString(bit))
            )
            .replace(EngineYaml.SLEEP_TIME, Long.toString(countSleepTimeStrategy))
            .trim();  // trim spaces in '${binary.mode} ${test}' when no mode, not covered by cleanSql()
    }

    private String getMode(BlindOperator blindOperator) {
        return switch (blindOperator) {
            case AND -> this.modelYaml.getStrategy().getBinary().getModeAnd();
            case OR -> this.modelYaml.getStrategy().getBinary().getModeOr();
            case STACK -> this.modelYaml.getStrategy().getBinary().getModeStack();
            default -> StringUtils.EMPTY;
        };
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition, boolean isReport) {
        return EngineYaml.replaceTags(
            this.getSlidingWindow(isReport)
            .replace(EngineYaml.INJECTION, sqlQuery)
            .replace(EngineYaml.WINDOW_CHAR, startPosition)
            .replace(EngineYaml.CAPACITY, EngineYaml.DEFAULT_CAPACITY)
        );
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition, boolean isReport) {
        return EngineYaml.replaceTags(
            this.getSlidingWindow(isReport)
            .replace(EngineYaml.INJECTION, sqlQuery)
            .replace(EngineYaml.WINDOW_CHAR, startPosition)
            .replace(EngineYaml.CAPACITY, EngineYaml.DEFAULT_CAPACITY)
        );
    }

    @Override
    public String sqlMultibit(String inj, int indexChar, int block){
        return this.modelYaml.getStrategy().getBinary().getMultibit()
            .replace(EngineYaml.INJECTION, inj)
            .replace(EngineYaml.WINDOW_CHAR, Integer.toString(indexChar))
            .replace(EngineYaml.BLOCK_MULTIBIT, Integer.toString(block));
    }

    @Override
    public String sqlErrorCalibrator(Method errorMethod) {
        return EngineYaml.replaceTags(
            errorMethod.getQuery()
            .replace(EngineYaml.WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
            .replace(EngineYaml.INJECTION, this.modelYaml.getStrategy().getConfiguration().getCalibrator())
            .replace(EngineYaml.WINDOW_CHAR, "1")
            .replace(EngineYaml.CAPACITY, Integer.toString(errorMethod.getCapacity()))
        );
    }

    @Override
    public String sqlErrorIndice(Method errorMethod) {
        var indexZeroToFind = "0";
        return EngineYaml.replaceTags(
            errorMethod.getQuery()
            .replace(EngineYaml.WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
            .replace(EngineYaml.INJECTION, this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(EngineYaml.INDICE, indexZeroToFind))
            .replace(EngineYaml.WINDOW_CHAR, "1")
            .replace(EngineYaml.CAPACITY, Integer.toString(errorMethod.getCapacity()))
        );
    }

    @Override
    public String sqlError(String sqlQuery, String startPosition, int indexMethodError, boolean isReport) {
        return EngineYaml.replaceTags(
            this.modelYaml.getStrategy().getError().getMethod().get(indexMethodError).getQuery()
            .replace(EngineYaml.WINDOW, this.getSlidingWindow(isReport))
            .replace(EngineYaml.INJECTION, sqlQuery)
            .replace(EngineYaml.WINDOW_CHAR, startPosition)
            .replace(
                EngineYaml.CAPACITY,
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
    public String sqlUnion(String sqlQuery, String startPosition, boolean isReport) {
        return EngineYaml.replaceTags(
            this.getSlidingWindow(isReport)
            .replace(EngineYaml.INJECTION, sqlQuery)
            .replace(EngineYaml.WINDOW_CHAR, startPosition)
            .replace(EngineYaml.CAPACITY, this.injectionModel.getMediatorStrategy().getUnion().getPerformanceLength())
        );
    }

    @Override
    public String sqlDns(String sqlQuery, String startPosition, BlindOperator blindOperator, boolean isReport) {
        String replacement = this.getMode(blindOperator);
        String result = EngineYaml.replaceTags(
            this.modelYaml.getStrategy().getDns()
            .replace(EngineYaml.WINDOW, this.getSlidingWindow(isReport))
            .replace(EngineYaml.BINARY_MODE, replacement)
            .replace(EngineYaml.INJECTION, sqlQuery)
            .replace(EngineYaml.DNS_DOMAIN, this.injectionModel.getMediatorUtils().preferencesUtil().getDnsDomain())
            .replace(EngineYaml.WINDOW_CHAR, startPosition)
            .replace(EngineYaml.CAPACITY, EngineYaml.DEFAULT_CAPACITY)
        );
        return Pattern.compile(Pattern.quote(EngineYaml.DNS_RANDOM))
            .matcher(result)
            .replaceAll(m -> String.format("%03d", ThreadLocalRandom.current().nextInt(999)));
    }

    @Override
    public String sqlStack(String sqlQuery, String startPosition, boolean isReport) {
        return this.modelYaml.getStrategy().getStack().replace(
            EngineYaml.WINDOW,
            EngineYaml.replaceTags(
                this.getSlidingWindow(isReport)
                .replace(EngineYaml.INJECTION, sqlQuery)
                .replace(EngineYaml.WINDOW_CHAR, startPosition)
                .replace(EngineYaml.CAPACITY, EngineYaml.DEFAULT_CAPACITY)
            )
        );
    }

    @Override
    public String sqlCapacity(String[] indexes) {
        String regexIndexes = String.join("|", indexes);
        String regexVisibleIndexesToFind = String.format(EngineYaml.FORMAT_INDEX, "(%s)");
        return this.injectionModel.getMediatorStrategy().getSpecificUnion().getIndexesInUrl().replaceAll(
            String.format(regexVisibleIndexesToFind, regexIndexes),
            EngineYaml.replaceTags(
                this.modelYaml.getStrategy().getUnion().getCapacity()
                .replace(EngineYaml.CALIBRATOR, this.modelYaml.getStrategy().getConfiguration().getCalibrator())
                .replace(EngineYaml.INDICE, "$1")
            )
        );
    }

    @Override
    public String sqlIndices(Integer nbFields) {
        String replaceTag = StringUtils.EMPTY;
        List<String> fields = new ArrayList<>();
        var indice = 1;
        for ( ; indice <= nbFields ; indice++) {
            String field = this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(EngineYaml.INDICE, Integer.toString(indice));
            fields.add(field);
            replaceTag = field;
        }
        indice--;
        return this.modelYaml.getStrategy().getUnion()
            .getIndices()
            .replace(
                EngineYaml.INDICES,
                String.join(",", fields.toArray(new String[0]))
            )
            .replace(EngineYaml.INDICE_UNIQUE, replaceTag)
            .replace(
                EngineYaml.RESULT_RANGE,
                String.join(",", Collections.nCopies(indice, "r"))
            );
    }

    @Override
    public String sqlLimit(Integer limitSqlResult) {
        var limitBoundary = 0;
        try {
            limitBoundary = Integer.parseInt(this.modelYaml.getStrategy().getConfiguration().getLimitBoundary());
        } catch (NumberFormatException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Incorrect Limit start index, force to 0");
        }
        return this.modelYaml.getStrategy().getConfiguration()
            .getLimit()
            .replace(EngineYaml.LIMIT_VALUE, Integer.toString(limitSqlResult + limitBoundary));
    }
    
    @Override
    public String fingerprintErrorsAsRegex() {
        return "(?si)"+ StringUtils.join(
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
            .replace("${enclose_value_sql}", EngineYaml.ENCLOSE_VALUE_SQL)
            .replace("${enclose_value_hex}", EngineYaml.ENCLOSE_VALUE_HEX)
            .replace("${separator_qte_sql}", EngineYaml.SEPARATOR_QTE_SQL)
            .replace("${separator_qte_hex}", EngineYaml.SEPARATOR_QTE_HEX)
            .replace("${separator_cell_sql}", EngineYaml.SEPARATOR_CELL_SQL)
            .replace("${separator_cell_hex}", EngineYaml.SEPARATOR_CELL_HEX)
            .replace("${calibrator_sql}", EngineYaml.CALIBRATOR_SQL)
            .replace("${calibrator_raw}", EngineYaml.CALIBRATOR_SQL.repeat(100))
            .replace("${calibrator_hex}", EngineYaml.CALIBRATOR_HEX)
            .replace("${trail_sql}", EngineYaml.TRAIL_SQL)
            .replace("${trail_hex}", EngineYaml.TRAIL_HEX)
            .replace("${lead}", LEAD)
            .replace("${lead_hex}", EngineYaml.LEAD_HEX)
            .replace("${lead_pipe}", EngineYaml.LEAD_PIPE);
    }

    /**
     * Get payload with sliding window except for vulnerability report
     */
    private String getSlidingWindow(boolean isReport) {
        return isReport
            ? "(" + EngineYaml.INJECTION + ")"
            : this.modelYaml.getStrategy().getConfiguration().getSlidingWindow();
    }
    
    
    // Getter and setter

    @Override
    public String sqlInfos() {
        return this.modelYaml.getResource().getInfo();
    }

    @Override
    public List<String> getFalsyBit() {
        return this.modelYaml.getStrategy().getBinary().getTest().getFalsyBit();
    }

    @Override
    public List<String> getTruthyBit() {
        return this.modelYaml.getStrategy().getBinary().getTest().getTruthyBit();
    }

    @Override
    public List<String> getFalsyBin() {
        return this.modelYaml.getStrategy().getBinary().getTest().getFalsyBin();
    }

    @Override
    public List<String> getTruthyBin() {
        return this.modelYaml.getStrategy().getBinary().getTest().getTruthyBin();
    }

    @Override
    public String sqlBlindConfirm() {
        return this.modelYaml.getStrategy().getBinary().getTest().getInit();
    }

    @Override
    public String sqlOrderBy() {
        return this.modelYaml.getStrategy().getUnion().getOrderBy();
    }
    
    @Override
    public String endingComment() {
        if (this.injectionModel.getMediatorUtils().preferencesUtil().isUrlRandomSuffixDisabled()) {
            return this.modelYaml.getStrategy().getConfiguration().getEndingComment();
        } else {
            return this.modelYaml.getStrategy().getConfiguration().getEndingComment()
                + RandomStringUtils.secure().nextAlphanumeric(4);  // Allows binary match fingerprinting on host errors
        }
    }

    @Override
    public ModelYaml getModelYaml() {
        return this.modelYaml;
    }
}
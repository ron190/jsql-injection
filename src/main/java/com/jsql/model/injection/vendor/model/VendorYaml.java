package com.jsql.model.injection.vendor.model;

import static com.jsql.model.accessible.DataAccess.CALIBRATOR_HEX;
import static com.jsql.model.accessible.DataAccess.CALIBRATOR_SQL;
import static com.jsql.model.accessible.DataAccess.ENCLOSE_VALUE_HEX;
import static com.jsql.model.accessible.DataAccess.ENCLOSE_VALUE_SQL;
import static com.jsql.model.accessible.DataAccess.LEAD;
import static com.jsql.model.accessible.DataAccess.LEAD_HEX;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_CELL_HEX;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_CELL_SQL;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_QTE_HEX;
import static com.jsql.model.accessible.DataAccess.SEPARATOR_QTE_SQL;
import static com.jsql.model.accessible.DataAccess.TRAIL_HEX;
import static com.jsql.model.accessible.DataAccess.TRAIL_SQL;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.blind.InjectionTime;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.util.StringUtil;

public class VendorYaml implements AbstractVendor {
    
    private ModelYaml modelYaml;
    
    private static final String LIMIT = "${LIMIT}";

    private static final String RESULT_RANGE = "${RESULT_RANGE}";

    private static final String INDICE_UNIQUE = "${INDICE_UNIQUE}";

    private static final String INDICES = "${INDICES}";

    private static final String CALIBRATOR = "${CALIBRATOR}";

    private static final String INDICE = "${INDICE}";

    private static final String WINDOW = "${WINDOW}";

    private static final String CAPACITY = "${CAPACITY}";

    private static final String SLEEP_TIME = "${SLEEP_TIME}";

    private static final String BIT = "${BIT}";

    private static final String INDEX = "${INDEX}";

    private static final String INJECTION = "${INJECTION}";

    private static final String TEST = "${TEST}";

    private static final String FILEPATH_HEX = "${FILEPATH.HEX}";

    private static final String FILEPATH = "${FILEPATH}";

    private static final String CONTENT_HEX = "${CONTENT.HEX}";

    private static final String FIELDS = "${FIELDS}";

    private static final String FIELD = "${FIELD}";

    private static final String TABLE = "${TABLE}";

    private static final String TABLE_HEX = "${TABLE.HEX}";

    private static final String DATABASE = "${DATABASE}";

    private static final String DATABASE_HEX = "${DATABASE.HEX}";
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private InjectionModel injectionModel;
    
    public VendorYaml(String fileYaml, InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        
        Yaml yaml = new Yaml();
        this.modelYaml = yaml.loadAs(VendorYaml.class.getClassLoader().getResourceAsStream("vendor/"+ fileYaml), ModelYaml.class);
    }

    @Override
    public String sqlInfos() {
        return this.modelYaml.getResource().getInfo();
    }

    @Override
    public String sqlDatabases() {
        return this.modelYaml.getResource().getSchema().getDatabase();
    }

    @Override
    public String sqlTables(Database database) {
        return
            this.modelYaml.getResource().getSchema().getTable()
                .replace(DATABASE_HEX, Hex.encodeHexString(database.toString().getBytes()))
                .replace(DATABASE, database.toString());
    }

    @Override
    public String sqlColumns(Table table) {
        return
            this.modelYaml.getResource().getSchema().getColumn()
                .replace(DATABASE_HEX, Hex.encodeHexString(table.getParent().toString().getBytes()))
                .replace(TABLE_HEX, Hex.encodeHexString(table.toString().getBytes()))
                .replace(DATABASE, table.getParent().toString())
                .replace(TABLE, table.toString());
    }

    @Override
    public String sqlRows(String[] namesColumns, Database database, Table table) {
        String sqlField = this.modelYaml.getResource().getSchema().getRow().getFields().getField();
        Matcher matcherSqlField = Pattern.compile("(?s)(.*)"+ Pattern.quote(FIELD) +"(.*)").matcher(sqlField);
        String leadSqlField = "";
        String trailSqlField = "";
        
        if (matcherSqlField.find()) {
            leadSqlField = matcherSqlField.group(1);
            trailSqlField = matcherSqlField.group(2);
        }
        
        String sqlConcatFields = this.modelYaml.getResource().getSchema().getRow().getFields().getConcat();
        
        String[] namesColumnUtf8 = new String[namesColumns.length];
        for (int i = 0 ; i < namesColumns.length ; i++) {
            namesColumnUtf8[i] = StringUtil.detectUtf8(namesColumns[i]);
            try {
                namesColumnUtf8[i] = URLEncoder.encode(namesColumnUtf8[i], StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        
        String nameDatabaseUtf8 = StringUtil.detectUtf8(database.toString());
        try {
            nameDatabaseUtf8 = URLEncoder.encode(nameDatabaseUtf8, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        String nameTableUtf8 = StringUtil.detectUtf8(table.toString());
        try {
            nameTableUtf8 = URLEncoder.encode(nameTableUtf8, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        
        return
            this.modelYaml.getResource().getSchema().getRow().getQuery()
                .replace(
                    FIELDS,
                    leadSqlField
                    + String.join(trailSqlField + sqlConcatFields + leadSqlField, namesColumnUtf8)
                    + trailSqlField
                )
                .replace(DATABASE, nameDatabaseUtf8)
                .replace(TABLE, nameTableUtf8);
    }

    @Override
    public String sqlPrivilegeTest() {
        return
            this.modelYaml.getResource().getFile().getPrivilege();
    }

    @Override
    public String sqlFileRead(String filePath) {
        return
            this.modelYaml.getResource().getFile().getRead()
                .replace(FILEPATH_HEX, Hex.encodeHexString(filePath.getBytes()));
    }

    @Override
    public String sqlTextIntoFile(String content, String filePath) {
        return
            this.injectionModel.getIndexesInUrl()
                .replaceAll(
                    "1337" + this.injectionModel.getMediatorStrategy().getNormal().getVisibleIndex() + "7331",
                    this.modelYaml.getResource().getFile().getCreate().getContent()
                        .replace(
                            CONTENT_HEX,
                            Hex.encodeHexString(content.getBytes())
                        )
                )
                .replaceAll("--++", "") +" "+ this.modelYaml.getResource().getFile().getCreate().getQuery()
                .replace(FILEPATH, filePath)
        ;
    }

    @Override
    public String[] getListFalseTest() {
        return
            this.modelYaml.getStrategy().getBoolean() != null
            ? this.modelYaml.getStrategy().getBoolean().getTest().getFalses().toArray(new String[0])
            : new String[0];
    }

    @Override
    public String[] getListTrueTest() {
        return
            this.modelYaml.getStrategy().getBoolean() != null
            ? this.modelYaml.getStrategy().getBoolean().getTest().getTrues().toArray(new String[0])
            : new String[0];
    }

    @Override
    public String sqlTestBlindFirst() {
        return
            this.modelYaml.getStrategy().getBoolean() != null
            ? this.modelYaml.getStrategy().getBoolean().getTest().getInitialization()
            : null;
    }

    @Override
    public String sqlTestBlind(String check) {
        return
            " "+
            this.modelYaml.getStrategy().getBoolean().getBlind()
                .replace(TEST, check);
    }

    @Override
    public String sqlBitTestBlind(String inj, int indexCharacter, int bit) {
        return
            " "+
            this.modelYaml.getStrategy().getBoolean().getBlind()
                .replace(TEST,
                    this.modelYaml.getStrategy().getBoolean().getTest().getBit()
                        .replace(INJECTION, inj)
                        .replace(INDEX, Integer.toString(indexCharacter))
                        .replace(BIT, Integer.toString(bit))
                );
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter) {
        return
            " "+
            this.modelYaml.getStrategy().getBoolean().getBlind()
                .replace(TEST,
                    this.modelYaml.getStrategy().getBoolean().getTest().getLength()
                        .replace(INJECTION, inj)
                        .replace(INDEX, Integer.toString(indexCharacter))
                );
    }

    @Override
    public String sqlTimeTest(String check) {
        String sqlTime = "";
        
        if (this.modelYaml.getStrategy().getBoolean().getTime() != null) {
            sqlTime =
                " "+
                this.modelYaml.getStrategy().getBoolean().getTime()
                    .replace(TEST, check)
                    .replace(SLEEP_TIME, Long.toString(InjectionTime.SLEEP_TIME));
        }
        
        return sqlTime;
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit) {
        return
            " "+
            this.modelYaml.getStrategy().getBoolean().getTime()
                .replace(
                    TEST,
                    this.modelYaml.getStrategy().getBoolean().getTest().getBit()
                        .replace(INJECTION, inj)
                        .replace(INDEX, Integer.toString(indexCharacter))
                        .replace(BIT, Integer.toString(bit))
                )
                .replace(
                    SLEEP_TIME,
                    Long.toString(InjectionTime.SLEEP_TIME)
                );
    }

    @Override
    public String sqlLengthTestTime(String inj, int indexCharacter) {
        return
            " "+
            this.modelYaml.getStrategy().getBoolean().getTime()
                .replace(
                    TEST,
                    this.modelYaml.getStrategy().getBoolean().getTest().getLength()
                        .replace(INJECTION, inj)
                        .replace(INDEX, Integer.toString(indexCharacter))
                )
                .replace(
                    SLEEP_TIME,
                    Long.toString(InjectionTime.SLEEP_TIME)
                );
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition) {
        return
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getConfiguration().getSlidingWindow()
                    .replace(INJECTION, sqlQuery)
                    .replace(INDEX, ""+ startPosition)
                    .replace(CAPACITY, "65565")
            );
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition) {
        return
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getConfiguration().getSlidingWindow()
                    .replace(INJECTION, sqlQuery)
                    .replace(INDEX, ""+ startPosition)
                    .replace(CAPACITY, "65565")
            );
    }

    @Override
    public String sqlTestError() {
        return
            " "+
            this.modelYaml.getStrategy().getError().getMethod().get(this.injectionModel.getMediatorStrategy().getError().getIndexMethodError()).getQuery()
                .replace(WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
                .replace(INJECTION, this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, "0"))
                .replace(INDEX, "1");
    }

    @Override
    public String sqlError(String sqlQuery, String startPosition) {
        return
            " "+
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getError().getMethod().get(this.injectionModel.getMediatorStrategy().getError().getIndexMethodError()).getQuery()
                    .replace(WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
                    .replace(INJECTION, sqlQuery)
                    .replace(INDEX, ""+startPosition)
                    .replace(CAPACITY, Integer.toString(this.modelYaml.getStrategy().getError().getMethod().get(this.injectionModel.getMediatorStrategy().getError().getIndexMethodError()).getCapacity()))
            );
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
        return
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getConfiguration().getSlidingWindow()
                    .replace(INJECTION, sqlQuery)
                    .replace(INDEX, ""+startPosition)
                    .replace(CAPACITY, ""+this.injectionModel.getMediatorStrategy().getNormal().getPerformanceLength())
            );
    }

    @Override
    public String sqlCapacity(String[] indexes) {
        return
                this.injectionModel.getIndexesInUrl().replaceAll(
                "1337("+ String.join("|", indexes) +")7331",
                VendorYaml.replaceTags(
                    this.modelYaml.getStrategy().getNormal().getCapacity()
                        .replace(CALIBRATOR, this.modelYaml.getStrategy().getConfiguration().getCalibrator())
                        .replace(INDICE, "$1")
                )
            );
    }

    @Override
    public String sqlIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<>();
        
        int indice = 1;
        for (  ; indice <= nbFields ; indice++) {
            fields.add(this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, Integer.toString(indice)));
            replaceTag = this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, Integer.toString(indice));
        }
        indice--;
        
        return
            " "+
            this.modelYaml.getStrategy().getNormal().getIndices()
                .replace(INDICES, String.join(",", fields.toArray(new String[fields.size()])))
                .replace(INDICE_UNIQUE, replaceTag)
                .replace(RESULT_RANGE, String.join(",", Collections.nCopies(indice, "r")));
    }

    @Override
    public String sqlOrderBy() {
        return this.modelYaml.getStrategy().getNormal().getOrderBy();
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        int limitBoundary = this.modelYaml.getStrategy().getConfiguration().getLimitBoundary();
        return
            this.modelYaml.getStrategy().getConfiguration().getLimit()
                .replace(LIMIT, Integer.toString(limitSQLResult + limitBoundary));
    }
    
    @Override
    public String endingComment() {
        return this.modelYaml.getStrategy().getConfiguration().getEndingComment();
    }
    
    @Override
    public String fingerprintErrorsAsRegex() {
        return StringUtils.join(
            this.modelYaml.getStrategy().getConfiguration().getFingerprint().getErrorMessage()
                .stream()
                .map(Pattern::quote)
                .toArray(),
            "|"
        );
    }
    
    public static String replaceTags(String sqlRequest) {
        return
            sqlRequest
                .replace("${ENCLOSE_VALUE_SQL}", ENCLOSE_VALUE_SQL)
                .replace("${ENCLOSE_VALUE_HEX}", ENCLOSE_VALUE_HEX)
                .replace("${SEPARATOR_QTE_SQL}", SEPARATOR_QTE_SQL)
                .replace("${SEPARATOR_QTE_HEX}", SEPARATOR_QTE_HEX)
                .replace("${SEPARATOR_CELL_SQL}", SEPARATOR_CELL_SQL)
                .replace("${SEPARATOR_CELL_HEX}", SEPARATOR_CELL_HEX)
                .replace("${CALIBRATOR_SQL}", CALIBRATOR_SQL)
                .replace("${CALIBRATOR_HEX}", CALIBRATOR_HEX)
                .replace("${TRAIL_SQL}", TRAIL_SQL)
                .replace("${TRAIL_HEX}", TRAIL_HEX)
                .replace("${LEAD}", LEAD)
                .replace("${LEAD_HEX}", LEAD_HEX);
    }

    @Override
    public ModelYaml getModelYaml() {
        return this.modelYaml;
    }
    
}
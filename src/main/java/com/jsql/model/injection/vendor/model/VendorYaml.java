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
import com.jsql.model.injection.strategy.blind.AbstractInjectionBoolean.BooleanMode;
import com.jsql.model.injection.strategy.blind.InjectionTime;
import com.jsql.model.injection.vendor.model.yaml.ModelYaml;
import com.jsql.util.StringUtil;

public class VendorYaml implements AbstractVendor {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private static final String BOOLEAN_MODE = "${boolean.mode}";
    
    public static final String LIMIT = "${limit}";
    private static final String LIMIT_VALUE = "${limit.value}";

    private static final String RESULT_RANGE = "${result_range}";

    private static final String INDICE_UNIQUE = "${indice_unique}";

    private static final String CALIBRATOR = "${calibrator}";

    private static final String INDICES = "${indices}";
    private static final String INDICE = "${indice}";
    private static final String WINDOW_CHAR = "${window.char}";

    private static final String WINDOW = "${window}";

    private static final String CAPACITY = "${capacity}";

    private static final String SLEEP_TIME = "${sleep_time}";

    private static final String BIT = "${bit}";

    private static final String INJECTION = "${injection}";

    private static final String TEST = "${test}";

    private static final String FILEPATH = "${filepath}";
    private static final String FILEPATH_HEX = "${filepath.hex}";

    private static final String CONTENT_HEX = "${content.hex}";

    private static final String FIELDS = "${fields}";
    private static final String FIELD = "${field.value}";

    private static final String TABLE = "${table}";
    private static final String DATABASE = "${database}";

    private static final String TABLE_HEX = "${table.hex}";
    private static final String DATABASE_HEX = "${database.hex}";
    
    private ModelYaml modelYaml;
    
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
            this.modelYaml.getResource().getSchema()
            .getTable()
            .replace(DATABASE_HEX, Hex.encodeHexString(database.toString().getBytes()))
            .replace(DATABASE, database.toString());
    }

    @Override
    public String sqlColumns(Table table) {
        
        return
            this.modelYaml.getResource().getSchema()
            .getColumn()
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
            this.modelYaml.getResource().getSchema().getRow()
            .getQuery()
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
        return this.modelYaml.getResource().getFile().getPrivilege();
    }

    @Override
    public String sqlFileRead(String filePath) {
        
        return
            this.modelYaml.getResource().getFile()
            .getRead()
            .replace(FILEPATH_HEX, Hex.encodeHexString(filePath.getBytes()));
    }

    @Override
    public String sqlTextIntoFile(String content, String filePath) {
        
        return
            this.injectionModel
            .getIndexesInUrl()
            .replaceAll(
                "1337" + this.injectionModel.getMediatorStrategy().getNormal().getVisibleIndex() + "7331",
                this.modelYaml.getResource().getFile().getCreate()
                .getContent()
                .replace(
                    CONTENT_HEX,
                    Hex.encodeHexString(content.getBytes())
                )
            )
            .replaceAll("--++", "")
            + StringUtils.SPACE
            + this.modelYaml.getResource().getFile().getCreate()
            .getQuery()
            .replace(FILEPATH, filePath);
    }

    @Override
    public List<String> getListFalseTest() {
        return this.modelYaml.getStrategy().getBoolean().getTest().getFalses();
    }

    @Override
    public List<String> getListTrueTest() {
        return this.modelYaml.getStrategy().getBoolean().getTest().getTrues();
    }

    @Override
    public String sqlTestBooleanInitialization() {
        return this.modelYaml.getStrategy().getBoolean().getTest().getInitialization();
    }

    @Override
    public String sqlTestBlind(String check, BooleanMode blindMode) {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getBoolean()
            .getBlind()
            .replace(BOOLEAN_MODE,
                blindMode == BooleanMode.AND
                ? this.modelYaml.getStrategy().getBoolean().getModeAnd()
                : this.modelYaml.getStrategy().getBoolean().getModeOr()
            )
            .replace(TEST, check);
    }

    @Override
    public String sqlBitTestBlind(String inj, int indexCharacter, int bit, BooleanMode blindMode) {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getBoolean()
            .getBlind()
            .replace(BOOLEAN_MODE,
                blindMode == BooleanMode.AND
                ? this.modelYaml.getStrategy().getBoolean().getModeAnd()
                : this.modelYaml.getStrategy().getBoolean().getModeOr()
            )
            .replace(TEST,
                this.modelYaml.getStrategy().getBoolean().getTest().getBit()
                .replace(INJECTION, inj)
                .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
                .replace(BIT, Integer.toString(bit))
            );
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter, BooleanMode blindMode) {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getBoolean()
            .getBlind()
            .replace(BOOLEAN_MODE,
                blindMode == BooleanMode.AND
                ? this.modelYaml.getStrategy().getBoolean().getModeAnd()
                : this.modelYaml.getStrategy().getBoolean().getModeOr()
            )
            .replace(TEST,
                this.modelYaml.getStrategy().getBoolean().getTest()
                .getLength()
                .replace(INJECTION, inj)
                .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
            );
    }

    @Override
    public String sqlTimeTest(String check, BooleanMode blindMode) {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getBoolean()
            .getTime()
            .replace(BOOLEAN_MODE,
                blindMode == BooleanMode.AND
                ? this.modelYaml.getStrategy().getBoolean().getModeAnd()
                : this.modelYaml.getStrategy().getBoolean().getModeOr()
            )
            .replace(TEST, check)
            .replace(SLEEP_TIME, Long.toString(InjectionTime.SLEEP_TIME));
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit, BooleanMode blindMode) {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getBoolean()
            .getTime()
            .replace(BOOLEAN_MODE,
                blindMode == BooleanMode.AND
                ? this.modelYaml.getStrategy().getBoolean().getModeAnd()
                : this.modelYaml.getStrategy().getBoolean().getModeOr()
            )
            .replace(
                TEST,
                this.modelYaml.getStrategy().getBoolean().getTest()
                .getBit()
                .replace(INJECTION, inj)
                .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
                .replace(BIT, Integer.toString(bit))
            )
            .replace(
                SLEEP_TIME,
                Long.toString(InjectionTime.SLEEP_TIME)
            );
    }

    @Override
    public String sqlLengthTestTime(String inj, int indexCharacter, BooleanMode blindMode) {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getBoolean()
            .getTime()
            .replace(BOOLEAN_MODE,
                blindMode == BooleanMode.AND
                ? this.modelYaml.getStrategy().getBoolean().getModeAnd()
                : this.modelYaml.getStrategy().getBoolean().getModeOr()
            )
            .replace(
                TEST,
                this.modelYaml.getStrategy().getBoolean().getTest()
                .getLength()
                .replace(INJECTION, inj)
                .replace(WINDOW_CHAR, Integer.toString(indexCharacter))
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
                this.modelYaml.getStrategy().getConfiguration()
                .getSlidingWindow()
                .replace(INJECTION, sqlQuery)
                .replace(WINDOW_CHAR, ""+ startPosition)
                .replace(CAPACITY, "65565")
            );
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition) {
        
        return
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getConfiguration()
                .getSlidingWindow()
                .replace(INJECTION, sqlQuery)
                .replace(WINDOW_CHAR, ""+ startPosition)
                .replace(CAPACITY, "65565")
            );
    }

    @Override
    public String sqlTestError() {
        
        return
            StringUtils.SPACE
            + this.modelYaml.getStrategy().getError().getMethod().get(this.injectionModel.getMediatorStrategy().getError().getIndexMethodError())
            .getQuery()
            .replace(WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
            .replace(INJECTION, this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, "0"))
            .replace(WINDOW_CHAR, "1");
    }

    @Override
    public String sqlError(String sqlQuery, String startPosition) {
        
        return
            StringUtils.SPACE
            + VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getError().getMethod().get(this.injectionModel.getMediatorStrategy().getError().getIndexMethodError())
                .getQuery()
                .replace(WINDOW, this.modelYaml.getStrategy().getConfiguration().getSlidingWindow())
                .replace(INJECTION, sqlQuery)
                .replace(WINDOW_CHAR, ""+startPosition)
                .replace(CAPACITY, Integer.toString(this.modelYaml.getStrategy().getError().getMethod().get(this.injectionModel.getMediatorStrategy().getError().getIndexMethodError()).getCapacity()))
            );
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
        
        return
            VendorYaml.replaceTags(
                this.modelYaml.getStrategy().getConfiguration()
                .getSlidingWindow()
                .replace(INJECTION, sqlQuery)
                .replace(WINDOW_CHAR, ""+startPosition)
                .replace(CAPACITY, ""+this.injectionModel.getMediatorStrategy().getNormal().getPerformanceLength())
            );
    }

    @Override
    public String sqlCapacity(String[] indexes) {
        
        return
            this.injectionModel
            .getIndexesInUrl()
            .replaceAll(
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
        
        String replaceTag = StringUtils.EMPTY;
        List<String> fields = new ArrayList<>();
        
        int indice = 1;
        
        for ( ; indice <= nbFields ; indice++) {
            
            String field = this.modelYaml.getStrategy().getConfiguration().getFailsafe().replace(INDICE, Integer.toString(indice));
            
            fields.add(field);
            
            replaceTag = field;
        }
        
        indice--;
        
        return
            StringUtils.SPACE + this.modelYaml.getStrategy().getNormal()
            .getIndices()
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
            this.modelYaml.getStrategy().getConfiguration()
            .getLimit()
            .replace(LIMIT_VALUE, Integer.toString(limitSQLResult + limitBoundary));
    }
    
    @Override
    public String endingComment() {
        return this.modelYaml.getStrategy().getConfiguration().getEndingComment();
    }
    
    @Override
    public String fingerprintErrorsAsRegex() {
        
        return StringUtils
            .join(
                this.modelYaml.getStrategy().getConfiguration().getFingerprint()
                .getErrorMessage()
                .stream()
                .map(Pattern::quote)
                .toArray(),
                "|"
            );
    }
    
    public static String replaceTags(String sqlRequest) {
        
        return
            sqlRequest
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

    @Override
    public ModelYaml getModelYaml() {
        return this.modelYaml;
    }
}
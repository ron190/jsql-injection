package com.jsql.model.injection.vendor;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import com.jsql.model.MediatorModel;
import com.jsql.model.bean.database.Database;
import com.jsql.model.bean.database.Table;
import com.jsql.model.injection.strategy.NormalStrategy;
import com.jsql.model.injection.strategy.Strategy;
import com.jsql.model.injection.strategy.blind.ConcreteTimeInjection;
import com.jsql.util.StringUtil;

public class VendorXml extends AbstractVendorDefault {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public VendorXml(String fileXml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.xmlModel = (Model) jaxbUnmarshaller.unmarshal(Model.class.getResource(fileXml));
        } catch (JAXBException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public String sqlInfos() {
        return this.xmlModel.getResource().getInfo();
    }

    @Override
    public String sqlDatabases() {
        return this.xmlModel.getResource().getSchema().getDatabase();
    }

    @Override
    public String sqlTables(Database database) {
        return 
            this.xmlModel.getResource().getSchema().getTable()
            .replace("${DATABASE.HEX}", Hex.encodeHexString(database.toString().getBytes()))
            .replace("${DATABASE}", database.toString());
    }

    @Override
    public String sqlColumns(Table table) {
        return 
            this.xmlModel.getResource().getSchema().getColumn()
            .replace("${DATABASE.HEX}", Hex.encodeHexString(table.getParent().toString().getBytes()))
            .replace("${TABLE.HEX}", Hex.encodeHexString(table.toString().getBytes()))
            .replace("${DATABASE}", table.getParent().toString())
            .replace("${TABLE}", table.toString());
    }

    @Override
    public String sqlRows(String[] namesColumns, Database database, Table table) {
        String sqlField = this.xmlModel.getResource().getSchema().getRow().getFields().getField();
        Matcher matcherSqlField = Pattern.compile("(.*)"+ Pattern.quote("${FIELD}") +"(.*)").matcher(sqlField);
        String leadSqlField = "";
        String trailSqlField = "";
        
        if (matcherSqlField.find()) {
            leadSqlField = matcherSqlField.group(1);
            trailSqlField = matcherSqlField.group(2);
        }
        
        String sqlConcatFields = this.xmlModel.getResource().getSchema().getRow().getFields().getConcat();
        
        String[] namesColumnUtf8 = new String[namesColumns.length];
        for (int i = 0 ; i < namesColumns.length ; i++) {
            namesColumnUtf8[i] = StringUtil.detectUtf8(namesColumns[i]);
            try {
                namesColumnUtf8[i] = URLEncoder.encode(namesColumnUtf8[i], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e, e);
            }
        }
        
        String nameDatabaseUtf8 = StringUtil.detectUtf8(database.toString());
        try {
            nameDatabaseUtf8 = URLEncoder.encode(nameDatabaseUtf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e, e);
        }
        
        String nameTableUtf8 = StringUtil.detectUtf8(table.toString());
        try {
            nameTableUtf8 = URLEncoder.encode(nameTableUtf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e, e);
        }
        
        return 
            this.xmlModel.getResource().getSchema().getRow().getQuery()
            .replace(
                "${FIELDS}", 
                leadSqlField 
                + StringUtil.join(namesColumnUtf8, trailSqlField + sqlConcatFields + leadSqlField) 
                + trailSqlField
            )
            .replace("${DATABASE}", nameDatabaseUtf8)
            .replace("${TABLE}", nameTableUtf8);
    }

    @Override
    public String sqlPrivilegeTest() {
        return 
            this.xmlModel.getResource().getFile().getPrivilege();
    }

    @Override
    public String sqlFileRead(String filePath) {
        return 
            this.xmlModel.getResource().getFile().getRead()
            .replace("${FILEPATH.HEX}", Hex.encodeHexString(filePath.getBytes()));
    }

    @Override
    public String sqlTextIntoFile(String content, String filePath) {
        return 
            MediatorModel.model().getIndexesInUrl()
            .replaceAll(
                "1337" + ((NormalStrategy) Strategy.NORMAL.instance()).getVisibleIndex() + "7331",
                this.xmlModel.getResource().getFile().getCreate().getContent()
                .replace("${CONTENT.HEX}", Hex.encodeHexString(content.getBytes()))
            )
            .replaceAll("--++", "")
            + this.xmlModel.getResource().getFile().getCreate().getQuery()
            .replace("${FILEPATH}", filePath);
    }

    @Override
    public String[] getListFalseTest() {
        return 
            this.xmlModel.getStrategy().getBoolean() != null 
            ? this.xmlModel.getStrategy().getBoolean().getTest().getFalse().toArray(new String[0])
            : new String[0];
    }

    @Override
    public String[] getListTrueTest() {
        return 
            this.xmlModel.getStrategy().getBoolean() != null 
            ? this.xmlModel.getStrategy().getBoolean().getTest().getTrue().toArray(new String[0])
            : new String[0];
    }

    @Override
    public String sqlTestBlindFirst() {
        return 
            this.xmlModel.getStrategy().getBoolean() != null 
            ? this.xmlModel.getStrategy().getBoolean().getTest().getInitialization()
            : null;
    }

    @Override
    public String sqlTestBlind(String check) {
        return 
            " "+ 
            this.xmlModel.getStrategy().getBoolean().getBlind()
            .replace("${TEST}", check);
    }

    @Override
    public String sqlBitTestBlind(String inj, int indexCharacter, int bit) {
        return 
            " "+ 
            this.xmlModel.getStrategy().getBoolean().getBlind()
            .replace("${TEST}", this.xmlModel.getStrategy().getBoolean().getTest().getBit()
            .replace("${INJECTION}", inj)
            .replace("${INDEX}", ""+ indexCharacter)
            .replace("${BIT}", ""+ bit));
    }

    @Override
    public String sqlLengthTestBlind(String inj, int indexCharacter) {
        return 
            " "+ 
            this.xmlModel.getStrategy().getBoolean().getBlind()
            .replace("${TEST}", this.xmlModel.getStrategy().getBoolean().getTest().getLength()
            .replace("${INJECTION}", inj)
            .replace("${INDEX}", ""+ indexCharacter));
    }

    @Override
    public String sqlTimeTest(String check) {
        String sqlTime = super.sqlTimeTest(check);
        
        if (this.xmlModel.getStrategy().getBoolean().getTime() != null) {
            sqlTime = 
                " "+ 
                this.xmlModel.getStrategy().getBoolean().getTime()
                .replace("${TEST}", check)
                .replace("${SLEEP_TIME}", ""+ ConcreteTimeInjection.SLEEP_TIME);
        }
        
        return sqlTime;
    }

    @Override
    public String sqlBitTestTime(String inj, int indexCharacter, int bit) {
        return 
            " "+ 
            this.xmlModel.getStrategy().getBoolean().getTime()
            .replace("${TEST}", this.xmlModel.getStrategy().getBoolean().getTest().getBit()
            .replace("${INJECTION}", inj)
            .replace("${INDEX}", ""+ indexCharacter)
            .replace("${BIT}", ""+ bit))
            .replace("${SLEEP_TIME}", ""+ ConcreteTimeInjection.SLEEP_TIME);
    }

    @Override
    public String sqlLengthTestTime(String inj, int indexCharacter) {
        return 
            " "+ 
            this.xmlModel.getStrategy().getBoolean().getTime()
            .replace("${TEST}", this.xmlModel.getStrategy().getBoolean().getTest().getLength()
            .replace("${INJECTION}", inj)
            .replace("${INDEX}", ""+ indexCharacter))
            .replace("${SLEEP_TIME}", ""+ ConcreteTimeInjection.SLEEP_TIME);
    }

    @Override
    public String sqlBlind(String sqlQuery, String startPosition) {
        return 
            replaceTags(
                this.xmlModel.getStrategy().getConfiguration().getSlidingWindow()
                .replace("${INJECTION}", sqlQuery)
                .replace("${INDEX}", ""+ startPosition)
                .replace("${CAPACITY}", "65565")
            );
    }

    @Override
    public String sqlTime(String sqlQuery, String startPosition) {
        return 
            replaceTags(
                this.xmlModel.getStrategy().getConfiguration().getSlidingWindow()
                .replace("${INJECTION}", sqlQuery)
                .replace("${INDEX}", ""+ startPosition)
                .replace("${CAPACITY}", "65565")
            );
    }

    @Override
    public String sqlTestErrorBased() {
        return 
            " "+ 
            this.xmlModel.getStrategy().getError().getMethod().get(Strategy.ERRORBASED.instance().getErrorIndex()).getQuery()
            .replace("${WINDOW}", this.xmlModel.getStrategy().getConfiguration().getSlidingWindow())
            .replace("${INJECTION}", this.xmlModel.getStrategy().getConfiguration().getFailsafe().replace("${INDICE}", "0"))
            .replace("${INDEX}", "1");
    }

    @Override
    public String sqlErrorBased(String sqlQuery, String startPosition) {
        return 
            " "+ 
            replaceTags(
                this.xmlModel.getStrategy().getError().getMethod().get(Strategy.ERRORBASED.instance().getErrorIndex()).getQuery()
                .replace("${WINDOW}", this.xmlModel.getStrategy().getConfiguration().getSlidingWindow())
                .replace("${INJECTION}", sqlQuery)
                .replace("${INDEX}", ""+startPosition)
                .replace("${CAPACITY}", ""+this.xmlModel.getStrategy().getError().getMethod().get(Strategy.ERRORBASED.instance().getErrorIndex()).getCapacity())
            );
    }

    @Override
    public String sqlNormal(String sqlQuery, String startPosition) {
        return 
            replaceTags(
                this.xmlModel.getStrategy().getConfiguration().getSlidingWindow()
                .replace("${INJECTION}", sqlQuery)
                .replace("${INDEX}", ""+startPosition)
                .replace("${CAPACITY}", ""+Strategy.NORMAL.instance().getPerformanceLength())
            );
    }

    @Override
    public String sqlCapacity(String[] indexes) {
        return 
            MediatorModel.model().getIndexesInUrl().replaceAll(
                "1337("+ StringUtil.join(indexes, "|") +")7331",
                replaceTags(
                    this.xmlModel.getStrategy().getNormal().getCapacity()
                        .replace("${INDICE}", "$1")
                        .replace("${CALIBRATOR}", this.xmlModel.getStrategy().getConfiguration().getCalibrator())
                )
            );
    }

    @Override
    public String sqlIndices(Integer nbFields) {
        String replaceTag = "";
        List<String> fields = new ArrayList<>(); 
        
        int indice = 0;
        for (  ; indice <= nbFields ; indice++) {
            fields.add(this.xmlModel.getStrategy().getConfiguration().getFailsafe().replace("${INDICE}", ""+ indice));
            replaceTag = this.xmlModel.getStrategy().getConfiguration().getFailsafe().replace("${INDICE}", ""+ indice);
        }
        indice--;
        
        return 
            " "+ 
            this.xmlModel.getStrategy().getNormal().getIndices()
            .replace("${INDICES}", StringUtil.join(fields.toArray(new String[fields.size()]), ","))
            .replace("${INDICES_STARS}", replaceTag)
            .replace("${STARS}", String.join(",", Collections.nCopies(indice, "r")));
    }

    @Override
    public String sqlOrderBy() {
        return this.xmlModel.getStrategy().getNormal().getOrderBy();
    }

    @Override
    public String sqlLimit(Integer limitSQLResult) {
        int limitBoundary = this.xmlModel.getStrategy().getConfiguration().getLimitBoundary();
        return 
            this.xmlModel.getStrategy().getConfiguration().getLimit()
            .replace("${LIMIT}", ""+ (limitSQLResult + limitBoundary));
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
            .replace("${TRAIL_SQL}", TRAIL_SQL)
            .replace("${TRAIL_HEX}", TRAIL_HEX)
            .replace("${LEAD}", LEAD)
            .replace("${LEAD_HEX}", LEAD_HEX);
    }
}
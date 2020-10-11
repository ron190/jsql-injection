package com.jsql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.jsql.util.tampering.TamperingType;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class TamperingUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private boolean isBase64 = false;
    private boolean isVersionComment = false;
    private boolean isFunctionComment = false;
    private boolean isEqualToLike = false;
    private boolean isRandomCase = false;
    private boolean isHexToChar = false;
    private boolean isStringToChar = false;
    private boolean isQuoteToUtf8 = false;
    private boolean isEval = false;
    private boolean isSpaceToMultilineComment = false;
    private boolean isSpaceToDashComment = false;
    private boolean isSpaceToSharpComment = false;
    
    private String customTamper = null;
    
    private static final NashornScriptEngineFactory NASHORN_FACTORY = new NashornScriptEngineFactory();

    // TODO builder
    public void set(
        boolean isBase64,
        boolean isVersionComment,
        boolean isFunctionComment,
        boolean isEqualToLike,
        boolean isRandomCase,
        boolean isHexToChar,
        boolean isQuoteToUtf8,
        boolean isEval,
        boolean isSpaceToMultilineComment,
        boolean isSpaceToDashComment,
        boolean isSpaceToSharpComment,
        boolean isStringToChar
    ) {
        
        this.isBase64 = isBase64;
        this.isVersionComment = isVersionComment;
        this.isFunctionComment = isFunctionComment;
        this.isEqualToLike = isEqualToLike;
        this.isRandomCase = isRandomCase;
        this.isHexToChar = isHexToChar;
        this.isQuoteToUtf8 = isQuoteToUtf8;
        this.isEval = isEval;
        this.isSpaceToMultilineComment = isSpaceToMultilineComment;
        this.isSpaceToDashComment = isSpaceToDashComment;
        this.isSpaceToSharpComment = isSpaceToSharpComment;
        this.isStringToChar = isStringToChar;
    }
    
    private static String eval(String sqlQuery, String jsTampering) {
        
        Object resultSqlTampered = null;
        
        try {
            if (StringUtils.isEmpty(jsTampering)) {
                
                throw new ScriptException("Tampering context is empty");
            }
            
            ScriptEngine nashornEngine = NASHORN_FACTORY.getScriptEngine();
            nashornEngine.eval(jsTampering);
            
            Invocable nashornInvocable = (Invocable) nashornEngine;
            resultSqlTampered = nashornInvocable.invokeFunction("tampering", sqlQuery);
            
        } catch (ScriptException e) {
            
            LOGGER.warn("Tampering context contains errors: " + e.getMessage(), e);
            resultSqlTampered = sqlQuery;
            
        } catch (NoSuchMethodException e) {
            
            LOGGER.warn("Tampering context is not properly defined: " + e.getMessage(), e);
            LOGGER.warn("Minimal tampering context is: var tampering = function(sql) {return sql}");
            resultSqlTampered = sqlQuery;
        }
        
        return resultSqlTampered.toString();
    }
    
    public String tamper(String sqlQueryDefault) {
        
        String lead = null;
        String sqlQuery = null;
        String trail = null;
        
        // Transform only SQL query without HTTP parameters and syntax changed, like p=1'+[sql]
        Matcher matcherSql = Pattern.compile("(?s)(.*<tampering>)(.*)(</tampering>.*)").matcher(sqlQueryDefault);
        
        if (matcherSql.find()) {
            
           lead = matcherSql.group(1);
           sqlQuery = matcherSql.group(2);
           trail = matcherSql.group(3);
        }
        
        if (this.isEval) {
            
            sqlQuery = eval(sqlQuery, this.customTamper);
        }

        if (this.isRandomCase) {
            
            sqlQuery = eval(sqlQuery, TamperingType.RANDOM_CASE.instance().getJavascript());
        }
        
        if (this.isEqualToLike) {
            
            sqlQuery = eval(sqlQuery, TamperingType.EQUAL_TO_LIKE.instance().getJavascript());
        }
        
        sqlQuery = lead + sqlQuery + trail;
        
        sqlQuery = sqlQuery.replaceAll("(?i)<tampering>", StringUtils.EMPTY);
        sqlQuery = sqlQuery.replaceAll("(?i)</tampering>", StringUtils.EMPTY);
        
        // Empty when checking character insertion
        if (StringUtils.isEmpty(sqlQuery)) {
            
            return StringUtils.EMPTY;
        }
        
        // Transform all query, SQL and HTTP

        if (this.isHexToChar) {
            
            sqlQuery = eval(sqlQuery, TamperingType.HEX_TO_CHAR.instance().getJavascript());
        }
        
        if (this.isStringToChar) {
            
            sqlQuery = eval(sqlQuery, TamperingType.STRING_TO_CHAR.instance().getJavascript());
        }

        if (this.isFunctionComment) {
            
            sqlQuery = eval(sqlQuery, TamperingType.COMMENT_TO_METHOD_SIGNATURE.instance().getJavascript());
        }

        if (this.isVersionComment) {
            
            sqlQuery = eval(sqlQuery, TamperingType.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getJavascript());
        }
        
        // Dependency to: EQUAL_TO_LIKE
        if (this.isSpaceToDashComment) {
            
            sqlQuery = eval(sqlQuery, TamperingType.SPACE_TO_DASH_COMMENT.instance().getJavascript());
            
        } else if (this.isSpaceToMultilineComment) {
            
            sqlQuery = eval(sqlQuery, TamperingType.SPACE_TO_MULTILINE_COMMENT.instance().getJavascript());
            
        } else if (this.isSpaceToSharpComment) {
            
            sqlQuery = eval(sqlQuery, TamperingType.SPACE_TO_SHARP_COMMENT.instance().getJavascript());
        }
        
        if (this.isBase64) {
            
            sqlQuery = eval(sqlQuery, TamperingType.BASE64.instance().getJavascript());
        }
        
        // Include character insertion at the beginning of query
        if (this.isQuoteToUtf8) {
            
            sqlQuery = eval(sqlQuery, TamperingType.QUOTE_TO_UTF8.instance().getJavascript());
        }
        
        return sqlQuery;
    }
    
    // Getter and setter

    public String getCustomTamper() {
        return this.customTamper;
    }

    public void setCustomTamper(String customTamper) {
        this.customTamper = customTamper;
    }
}

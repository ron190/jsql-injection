package com.jsql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class TamperingUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    public static boolean isBase64 = false;
    public static boolean isVersionComment = false;
    public static boolean isFunctionComment = false;
    public static boolean isEqualToLike = false;
    public static boolean isRandomCase = false;
    public static boolean isHexToChar = false;
    public static boolean isQuoteToUtf8 = false;
    public static boolean isEval = false;
    public static boolean isSpaceToMultilineComment = false;
    public static boolean isSpaceToDashComment = false;
    public static boolean isSpaceToSharpComment = false;
    
    public static String eval = null;

    private TamperingUtil() {
        // TODO Auto-generated constructor stub
    }

    public static void set(
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
        boolean isSpaceToSharpComment
    ) {
        TamperingUtil.isBase64 = isBase64;                 
        TamperingUtil.isVersionComment = isVersionComment;         
        TamperingUtil.isFunctionComment = isFunctionComment;        
        TamperingUtil.isEqualToLike = isEqualToLike;            
        TamperingUtil.isRandomCase = isRandomCase;             
        TamperingUtil.isHexToChar = isHexToChar;             
        TamperingUtil.isQuoteToUtf8 = isQuoteToUtf8;             
        TamperingUtil.isEval = isEval;                   
        TamperingUtil.isSpaceToMultilineComment = isSpaceToMultilineComment;
        TamperingUtil.isSpaceToDashComment = isSpaceToDashComment;     
        TamperingUtil.isSpaceToSharpComment = isSpaceToSharpComment;    
    }
    
    static ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName("nashorn");
    
    private static String eval(String out, String js) {
        Object result = null;
        
        try {
//            nashornEngine.eval(new FileReader("src/main/resources/random-case.js"));
//            nashornEngine.eval("load('src/main/resources/random-case.js'); var tampering = function(sql) {return "+ js +"}");
            nashornEngine.eval(js);
            
            Invocable invocable = (Invocable) nashornEngine;
            result = invocable.invokeFunction("tampering", out);
        } catch (ScriptException | NoSuchMethodException e) {
            LOGGER.warn(e);
            result = out;
        }
        
        return result.toString();
    }
    
    public static String tamper(String in) {
        String sqlQueryDefault = in;
        
        String lead = null;
        String sqlQuery = null;
        String trail = null;
        
        Matcher m = Pattern.compile("(.*SlQqLs)(.*)(lSqQsL.*)").matcher(sqlQueryDefault);
        if (m.find()) {
           lead = m.group(1);
           sqlQuery = m.group(2);
           trail = m.group(3);
        }
        
        if (TamperingUtil.isRandomCase) {
            sqlQuery = eval(sqlQuery, Tampering.RANDOM_CASE.instance().getXmlModel().getJavascript());
        }

        if (TamperingUtil.isHexToChar) {
            sqlQuery = eval(sqlQuery, Tampering.HEX_TO_CHAR.instance().getXmlModel().getJavascript());
        }

        if (TamperingUtil.isFunctionComment) {
            sqlQuery = eval(sqlQuery, Tampering.COMMENT_TO_METHOD_SIGNATURE.instance().getXmlModel().getJavascript());
        }

        if (TamperingUtil.isVersionComment) {
            sqlQuery = eval(sqlQuery, Tampering.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getXmlModel().getJavascript());
        }
        
        if (TamperingUtil.isEqualToLike) {
            sqlQuery = eval(sqlQuery, Tampering.EQUAL_TO_LIKE.instance().getXmlModel().getJavascript());
        }
        
        // Dependency to: EQUAL_TO_LIKE
        if (TamperingUtil.isSpaceToDashComment) {
            sqlQuery = eval(sqlQuery, Tampering.SPACE_TO_DASH_COMMENT.instance().getXmlModel().getJavascript());
            
        } else if (TamperingUtil.isSpaceToMultilineComment) {
            sqlQuery = eval(sqlQuery, Tampering.SPACE_TO_MULTILINE_COMMENT.instance().getXmlModel().getJavascript());
            
        } else if (TamperingUtil.isSpaceToSharpComment) {
            sqlQuery = eval(sqlQuery, Tampering.SPACE_TO_SHARP_COMMENT.instance().getXmlModel().getJavascript());
        }
        
        if (TamperingUtil.isEval) {
            sqlQuery = eval(sqlQuery, TamperingUtil.eval);
        }
        
        sqlQuery = lead + sqlQuery + trail;
        
        // Include character insertion at the beginning of query
        if (TamperingUtil.isQuoteToUtf8) {
            sqlQuery = eval(sqlQuery, Tampering.QUOTE_TO_UTF8.instance().getXmlModel().getJavascript());
        }
        
        if (TamperingUtil.isBase64) {
            sqlQuery = eval(sqlQuery, Tampering.BASE64.instance().getXmlModel().getJavascript());
        }
        
        // Problème si le tag contient des caractères spéciaux
        sqlQuery = sqlQuery.replaceAll("(?i)SlQqLs", "");
        sqlQuery = sqlQuery.replaceAll("(?i)lSqQsL", "");
        
        return sqlQuery;
    }

}

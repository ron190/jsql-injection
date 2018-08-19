package com.jsql.util;

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
    public static boolean isEval = false;
    public static boolean isSpaceToMultilineComment = false;
    public static boolean isSpaceToDashComment = false;
    public static boolean isSpaceToSharpComment = false;

    private TamperingUtil() {
        // TODO Auto-generated constructor stub
    }

    public static void set(
        boolean isBase64, 
        boolean isVersionComment, 
        boolean isFunctionComment, 
        boolean isEqualToLike, 
        boolean isRandomCase,
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
            LOGGER.error(e);
            result = out;
        }
        
        return result.toString();
    }
    
    public static String tamper(String in) {
        String out = in;
        
        if (TamperingUtil.isBase64) {
            out = eval(out, Tampering.BASE64.instance().getXmlModel().getJavascript());
        }
        
        if (TamperingUtil.isRandomCase) {
            out = eval(out, Tampering.RANDOM_CASE.instance().getXmlModel().getJavascript());
        }
        
        out = out.replaceAll("(?i)SlQqLs", "");
        out = out.replaceAll("(?i)lSqQsL", "");

        if (TamperingUtil.isFunctionComment) {
            out = eval(out, Tampering.COMMENT_TO_METHOD_SIGNATURE.instance().getXmlModel().getJavascript());
        }

        if (TamperingUtil.isVersionComment) {
            out = eval(out, Tampering.VERSIONED_COMMENT_TO_METHOD_SIGNATURE.instance().getXmlModel().getJavascript());
        }
        
        if (TamperingUtil.isEqualToLike) {
            out = eval(out, Tampering.EQUAL_TO_LIKE.instance().getXmlModel().getJavascript());
        }
        
        if (TamperingUtil.isSpaceToDashComment) {
            out = eval(out, Tampering.SPACE_TO_DASH_COMMENT.instance().getXmlModel().getJavascript());
            
        } else if (TamperingUtil.isSpaceToMultilineComment) {
            out = eval(out, Tampering.SPACE_TO_MULTILINE_COMMENT.instance().getXmlModel().getJavascript());
            
        } else if (TamperingUtil.isSpaceToSharpComment) {
            out = eval(out, Tampering.SPACE_TO_SHARP_COMMENT.instance().getXmlModel().getJavascript());
        }
        
        return out;
    }

}

package com.jsql.util.tampering;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.jsql.util.LogLevel;

public enum TamperingType {
    
    BASE64("base64.yml"),
    COMMENT_TO_METHOD_SIGNATURE("comment-to-method-signature.yml"),
    EQUAL_TO_LIKE("equal-to-like.yml"),
    RANDOM_CASE("random-case.yml"),
    SPACE_TO_DASH_COMMENT("space-to-dash-comment.yml"),
    SPACE_TO_MULTILINE_COMMENT("space-to-multiline-comment.yml"),
    SPACE_TO_SHARP_COMMENT("space-to-sharp-comment.yml"),
    VERSIONED_COMMENT_TO_METHOD_SIGNATURE("version-comment-to-method-signature.yml"),
    HEX_TO_CHAR("hex-to-char.yml"),
    STRING_TO_CHAR("string-to-char.yml"),
    QUOTE_TO_UTF8("quote-to-utf8.yml");
    
    /**
     * Log4j logger sent to view.
     */
    private final Logger logger = LogManager.getRootLogger();
    
    private ModelYamlTampering instanceModelYaml;
    
    private TamperingType(String fileYaml) {
        
        var yaml = new Yaml();
        
        try (var inputStream = TamperingType.class.getClassLoader().getResourceAsStream("tamper/"+ fileYaml)) {
        
            this.instanceModelYaml = yaml.loadAs(inputStream, ModelYamlTampering.class);
            
        } catch (IOException e) {

            this.logger.log(LogLevel.CONSOLE_JAVA, e, e);
        }
    }
    
    public ModelYamlTampering instance() {
        return this.instanceModelYaml;
    }
}
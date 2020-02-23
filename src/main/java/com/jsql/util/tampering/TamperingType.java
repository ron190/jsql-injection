package com.jsql.util.tampering;

import org.yaml.snakeyaml.Yaml;

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
    QUOTE_TO_UTF8("quote-to-utf8.yml");
    
    private final ModelYamlTampering instanceModelYaml;
    
    private TamperingType(String fileYaml) {
        
        Yaml yaml = new Yaml();
        this.instanceModelYaml = yaml.loadAs(TamperingType.class.getClassLoader().getResourceAsStream("tamper/"+ fileYaml), ModelYamlTampering.class);
    }
    
    public ModelYamlTampering instance() {
        return this.instanceModelYaml;
    }
}
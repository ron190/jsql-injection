package com.jsql.util.tampering;

public enum Tampering {
    
    BASE64(new TamperingYaml("base64.yml")),
    COMMENT_TO_METHOD_SIGNATURE(new TamperingYaml("comment-to-method-signature.yml")),
    EQUAL_TO_LIKE(new TamperingYaml("equal-to-like.yml")),
    RANDOM_CASE(new TamperingYaml("random-case.yml")),
    SPACE_TO_DASH_COMMENT(new TamperingYaml("space-to-dash-comment.yml")),
    SPACE_TO_MULTILINE_COMMENT(new TamperingYaml("space-to-multiline-comment.yml")),
    SPACE_TO_SHARP_COMMENT(new TamperingYaml("space-to-sharp-comment.yml")),
    VERSIONED_COMMENT_TO_METHOD_SIGNATURE(new TamperingYaml("version-comment-to-method-signature.yml")),
    HEX_TO_CHAR(new TamperingYaml("hex-to-char.yml")),
    QUOTE_TO_UTF8(new TamperingYaml("quote-to-utf8.yml"));
    
    private final TamperingYaml instanceTamperingYaml;
    
    private Tampering(TamperingYaml instanceTamperingYaml) {
        this.instanceTamperingYaml = instanceTamperingYaml;
    }
    
    public TamperingYaml instance() {
        return this.instanceTamperingYaml;
    }
    
}

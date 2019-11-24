package com.jsql.util;

public enum Tampering {
    
    BASE64(new TamperingXml("base64.xml")),
    COMMENT_TO_METHOD_SIGNATURE(new TamperingXml("comment-to-method-signature.xml")),
    EQUAL_TO_LIKE(new TamperingXml("equal-to-like.xml")),
    RANDOM_CASE(new TamperingXml("random-case.xml")),
    SPACE_TO_DASH_COMMENT(new TamperingXml("space-to-dash-comment.xml")),
    SPACE_TO_MULTILINE_COMMENT(new TamperingXml("space-to-multiline-comment.xml")),
    SPACE_TO_SHARP_COMMENT(new TamperingXml("space-to-sharp-comment.xml")),
    VERSIONED_COMMENT_TO_METHOD_SIGNATURE(new TamperingXml("version-comment-to-method-signature.xml")),
    HEX_TO_CHAR(new TamperingXml("hex-to-char.xml")),
    QUOTE_TO_UTF8(new TamperingXml("quote-to-utf8.xml"));
    
    private final TamperingXml instanceVendor;
    
    private Tampering(TamperingXml instanceVendor) {
        this.instanceVendor = instanceVendor;
    }
    
    public TamperingXml instance() {
        return this.instanceVendor;
    }
    
}

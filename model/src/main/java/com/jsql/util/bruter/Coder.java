package com.jsql.util.bruter;

public enum Coder {
    MD2("Md2"),
    MD4("Md4"),
    MD5("Md5"),
    SHA1("Sha-1"),
    SHA256("Sha-256"),
    SHA384("Sha-384"),
    SHA512("Sha-512"),
    MYSQL("Mysql"),
    ADLER32("Adler32"),
    CRC16("Crc16"),
    CRC32("Crc32"),
    CRC64("Crc64"),
    HEX("Hex"),
    HEX_ZIP("Hex(zipped)"),
    BASE64_ZIP("Base64(zipped)"),
    BASE64("Base64"),
    BASE32("Base32"),
    BASE58("Base58"),
    BASE16("Base16"),
    HTML("Html"),
    HTML_DECIMAL("Html (decimal)"),
    URL("Url"),
    UNICODE("Unicode");

    public final String label;

    Coder(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}

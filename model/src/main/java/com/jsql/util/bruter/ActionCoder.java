package com.jsql.util.bruter;

import com.jsql.util.StringUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ActionCoder {
    MD2(Coder.MD2.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toHash(this.name, value);
        }
    },
    MD4(Coder.MD4.label, true) {
        @Override
        public String run(String value) {
            return HashUtil.toMd4(value);
        }
    },
    MD5(Coder.MD5.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toHash(this.name, value);
        }
    },
    SHA_1(Coder.SHA1.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toHash(this.name, value);
        }
    },
    SHA_256(Coder.SHA256.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toHash(this.name, value);
        }
    },
    SHA_384(Coder.SHA384.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toHash(this.name, value);
        }
    },
    SHA_512(Coder.SHA512.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toHash(this.name, value);
        }
    },
    MYSQL(Coder.MYSQL.label, true) {
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            return HashUtil.toMySql(value);
        }
    },
    ADLER32(Coder.ADLER32.label, true, false) {
        @Override
        public String run(String value) {
            return HashUtil.toAdler32(value);
        }
    },
    CRC16(Coder.CRC16.label, true, false) {
        @Override
        public String run(String value) {
            return HashUtil.toCrc16(value);
        }
    },
    CRC32(Coder.CRC32.label, true, false) {
        @Override
        public String run(String value) {
            return HashUtil.toCrc32(value);
        }
    },
    CRC64(Coder.CRC64.label, true, false) {
        @Override
        public String run(String value) {
            return HashUtil.toCrc64(value);
        }
    },
    ENCODE_TO_HEX(ActionCoder.ENCODE_TO + Coder.HEX) {
        @Override
        public String run(String value) {
            return StringUtil.toHex(value);
        }
    },
    ENCODE_TO_HEX_ZIP(ActionCoder.ENCODE_TO + Coder.HEX_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.toHexZip(value);
        }
    },
    ENCODE_TO_BASE64_ZIP(ActionCoder.ENCODE_TO + Coder.BASE64_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.toBase64Zip(value);
        }
    },
    ENCODE_TO_BASE64(ActionCoder.ENCODE_TO + Coder.BASE64) {
        @Override
        public String run(String value) {
            return StringUtil.base64Encode(value);
        }
    },
    ENCODE_TO_BASE32(ActionCoder.ENCODE_TO + Coder.BASE32) {
        @Override
        public String run(String value) {
            return StringUtil.base32Encode(value);
        }
    },
    ENCODE_TO_BASE58(ActionCoder.ENCODE_TO + Coder.BASE58) {
        @Override
        public String run(String value) {
            return StringUtil.base58Encode(value);
        }
    },
    ENCODE_TO_BASE16(ActionCoder.ENCODE_TO + Coder.BASE16) {
        @Override
        public String run(String value) {
            return StringUtil.base16Encode(value);
        }
    },
    ENCODE_TO_HTML(ActionCoder.ENCODE_TO + Coder.HTML) {
        @Override
        public String run(String value) {
            return StringUtil.toHtml(value);
        }
    },
    ENCODE_TO_HTML_DECIMAL(ActionCoder.ENCODE_TO + Coder.HTML_DECIMAL) {
        @Override
        public String run(String value) {
            return StringUtil.toHtmlDecimal(value);
        }
    },
    ENCODE_TO_URL(ActionCoder.ENCODE_TO + Coder.URL) {
        @Override
        public String run(String value) {
            return StringUtil.toUrl(value);
        }
    },
    ENCODE_TO_UNICODE(ActionCoder.ENCODE_TO + Coder.UNICODE) {
        @Override
        public String run(String value) {
            return StringEscapeUtils.escapeJava(value);
        }
    },
    DECODE_FROM_HEX(ActionCoder.DECODE_FROM + Coder.HEX) {
        @Override
        public String run(String value) {
            return StringUtil.fromHex(value);
        }
    },
    DECODE_FROM_HEX_ZIP(ActionCoder.DECODE_FROM + Coder.HEX_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.fromHexZip(value);
        }
    },
    DECODE_FROM_BASE64_ZIP(ActionCoder.DECODE_FROM + Coder.BASE64_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.fromBase64Zip(value);
        }
    },
    DECODE_FROM_BASE64(ActionCoder.DECODE_FROM + Coder.BASE64) {
        @Override
        public String run(String value) {
            return StringUtil.base64Decode(value);
        }
    },
    DECODE_FROM_BASE32(ActionCoder.DECODE_FROM + Coder.BASE32) {
        @Override
        public String run(String value) {
            return StringUtil.base32Decode(value);
        }
    },
    DECODE_FROM_BASE58(ActionCoder.DECODE_FROM + Coder.BASE58) {
        @Override
        public String run(String value) {
            return StringUtil.base58Decode(value);
        }
    },
    DECODE_FROM_BASE16(ActionCoder.DECODE_FROM + Coder.BASE16) {
        @Override
        public String run(String value) {
            return StringUtil.base16Decode(value);
        }
    },
    DECODE_FROM_HTML(ActionCoder.DECODE_FROM + Coder.HTML) {
        @Override
        public String run(String value) {
            return StringUtil.fromHtml(value);
        }
    },
    DECODE_FROM_UNICODE(ActionCoder.DECODE_FROM + Coder.UNICODE) {
        @Override
        public String run(String value) {
            return StringEscapeUtils.unescapeJava(value);
        }
    },
    DECODE_FROM_URL(ActionCoder.DECODE_FROM + Coder.URL) {
        @Override
        public String run(String value) {
            return StringUtil.fromUrl(value);
        }
    };

    public static final String ENCODE_TO = "Encode to ";
    public static final String DECODE_FROM = "Decode from ";
    
    protected final String name;
    private final boolean isHash;
    private final boolean isEmptyPossible;

    ActionCoder(String name) {
        this(name, false, false);
    }
    ActionCoder(String name, boolean isHash) {
        this(name, isHash, true);
    }
    ActionCoder(String name, boolean isHash, boolean isEmptyPossible) {
        this.name = name;
        this.isHash = isHash;
        this.isEmptyPossible = isEmptyPossible;
    }

    public static List<String> getHashes() {
        return Arrays.stream(ActionCoder.values())
            .filter(action -> action.isHash)
            .map(actionCoder -> actionCoder.name)
            .toList();
    }

    public static List<String> getHashesEmpty() {
        return Arrays.stream(ActionCoder.values())
            .filter(action -> action.isHash && action.isEmptyPossible)
            .map(actionCoder -> actionCoder.name)
            .toList();
    }

    public static Optional<ActionCoder> forName(String name) {
        return Arrays.stream(ActionCoder.values())
            .filter(action -> name.equals(action.name))
            .findFirst();
    }

    public abstract String run(String value) throws IOException, NoSuchAlgorithmException;

    public String getName() {
        return this.name;
    }
}
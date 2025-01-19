package com.jsql.util.bruter;

import com.jsql.util.StringUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    ENCODE_TO_HEX("Encode to "+ Coder.HEX) {
        @Override
        public String run(String value) {
            return StringUtil.toHex(value);
        }
    },
    ENCODE_TO_HEX_ZIP("Encode to "+ Coder.HEX_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.toHexZip(value);
        }
    },
    ENCODE_TO_BASE64_ZIP("Encode to "+ Coder.BASE64_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.toBase64Zip(value);
        }
    },
    ENCODE_TO_BASE64("Encode to "+ Coder.BASE64) {
        @Override
        public String run(String value) {
            return StringUtil.base64Encode(value);
        }
    },
    ENCODE_TO_BASE32("Encode to "+ Coder.BASE32) {
        @Override
        public String run(String value) {
            return StringUtil.base32Encode(value);
        }
    },
    ENCODE_TO_BASE58("Encode to "+ Coder.BASE58) {
        @Override
        public String run(String value) {
            return StringUtil.base58Encode(value);
        }
    },
    ENCODE_TO_BASE16("Encode to "+ Coder.BASE16) {
        @Override
        public String run(String value) {
            return StringUtil.base16Encode(value);
        }
    },
    ENCODE_TO_HTML("Encode to "+ Coder.HTML) {
        @Override
        public String run(String value) {
            return StringUtil.toHtml(value);
        }
    },
    ENCODE_TO_HTML_DECIMAL("Encode to "+ Coder.HTML_DECIMAL) {
        @Override
        public String run(String value) {
            return StringUtil.toHtmlDecimal(value);
        }
    },
    ENCODE_TO_URL("Encode to "+ Coder.URL) {
        @Override
        public String run(String value) {
            return StringUtil.toUrl(value);
        }
    },
    ENCODE_TO_UNICODE("Encode to "+ Coder.UNICODE) {
        @Override
        public String run(String value) {
            return StringEscapeUtils.escapeJava(value);
        }
    },
    DECODE_FROM_HEX("Decode from "+ Coder.HEX) {
        @Override
        public String run(String value) {
            return StringUtil.fromHex(value);
        }
    },
    DECODE_FROM_HEX_ZIP("Decode from "+ Coder.HEX_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.fromHexZip(value);
        }
    },
    DECODE_FROM_BASE64_ZIP("Decode from "+ Coder.BASE64_ZIP) {
        @Override
        public String run(String value) throws IOException {
            return StringUtil.fromBase64Zip(value);
        }
    },
    DECODE_FROM_BASE64("Decode from "+ Coder.BASE64) {
        @Override
        public String run(String value) {
            return StringUtil.base64Decode(value);
        }
    },
    DECODE_FROM_BASE32("Decode from "+ Coder.BASE32) {
        @Override
        public String run(String value) {
            return StringUtil.base32Decode(value);
        }
    },
    DECODE_FROM_BASE58("Decode from "+ Coder.BASE58) {
        @Override
        public String run(String value) {
            return StringUtil.base58Decode(value);
        }
    },
    DECODE_FROM_BASE16("Decode from "+ Coder.BASE16) {
        @Override
        public String run(String value) {
            return StringUtil.base16Decode(value);
        }
    },
    DECODE_FROM_HTML("Decode from "+ Coder.HTML) {
        @Override
        public String run(String value) {
            return StringUtil.fromHtml(value);
        }
    },
    DECODE_FROM_UNICODE("Decode from "+ Coder.UNICODE) {
        @Override
        public String run(String value) {
            return StringEscapeUtils.unescapeJava(value);
        }
    },
    DECODE_FROM_URL("Decode from "+ Coder.URL) {
        @Override
        public String run(String value) {
            return StringUtil.fromUrl(value);
        }
    };

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
            .collect(Collectors.toList());
    }

    public static List<String> getHashesEmpty() {
        return Arrays.stream(ActionCoder.values())
            .filter(action -> action.isHash && action.isEmptyPossible)
            .map(actionCoder -> actionCoder.name)
            .collect(Collectors.toList());
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
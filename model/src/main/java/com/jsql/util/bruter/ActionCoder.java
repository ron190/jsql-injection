package com.jsql.util.bruter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.jsql.util.StringUtil;

public enum ActionCoder {

    MD2("Md2") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toHash(this.name, value);
        }
    },
    
    MD4("Md4") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toMd4(value);
        }
    },
    
    MD5("Md5") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toHash(this.name, value);
        }
    },
    
    SHA_1("Sha-1") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toHash(this.name, value);
        }
    },
    
    SHA_256("Sha-256") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toHash(this.name, value);
        }
    },
    
    SHA_384("Sha-384") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toHash(this.name, value);
        }
    },
    
    SHA_512("Sha-512") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toHash(this.name, value);
        }
    },
    
    MYSQL("Mysql") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toMySql(value);
        }
    },
    
    ADLER32("Adler32") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toAdler32(value);
        }
    },
    
    CRC16("Crc16") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toCrc16(value);
        }
    },
    
    CRC32("Crc32") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toCrc32(value);
        }
    },
    
    CRC64("Crc64") {
        
        @Override
        public String run(String value) throws NoSuchAlgorithmException {
            
            return HashUtil.toCrc64(value);
        }
    },
    
    ENCODE_TO_HEX("Encode to Hex") {
        
        @Override
        public String run(String value) throws UnsupportedEncodingException {
            
            return StringUtil.toHex(value);
        }
    },
    
    ENCODE_TO_HEX_ZIPPED("Encode to Hex(zipped)") {
        
        @Override
        public String run(String value) throws IOException {
            
            return StringUtil.toHexZip(value);
        }
    },
    
    ENCODE_TO_BASE64_ZIPPED("Encode to Base64(zipped)") {
        
        @Override
        public String run(String value) throws IOException {
            
            return StringUtil.toBase64Zip(value);
        }
    },
    
    ENCODE_TO_BASE64("Encode to Base64") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base64Encode(value);
        }
    },
    
    ENCODE_TO_BASE32("Encode to Base32") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base32Encode(value);
        }
    },
    
    ENCODE_TO_BASE58("Encode to Base58") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base58Encode(value);
        }
    },
    
    ENCODE_TO_BASE16("Encode to Base16") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base16Encode(value);
        }
    },
    
    ENCODE_TO_HTML("Encode to Html") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.toHtml(value);
        }
    },
    
    ENCODE_TO_HTML_DECIMAL("Encode to Html (decimal)") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.decimalHtmlEncode(value, true);
        }
    },
    
    ENCODE_TO_URL("Encode to Url") {
        
        @Override
        public String run(String value) throws UnsupportedEncodingException {
            
            return StringUtil.toUrl(value);
        }
    },
    
    ENCODE_TO_UNICODE("Encode to Unicode") {
        
        @Override
        public String run(String value) {
            
            return StringEscapeUtils.escapeJava(value);
        }
    },

    DECODE_FROM_HEX("Decode from Hex") {
        
        @Override
        public String run(String value) throws UnsupportedEncodingException, DecoderException {
            
            return StringUtil.fromHex(value);
        }
    },
    
    DECODE_FROM_HEX_ZIPPED("Decode from Hex(zipped)") {
        
        @Override
        public String run(String value) throws IOException, DecoderException {
            
            return StringUtil.fromHexZip(value);
        }
    },
    
    DECODE_FROM_BASE64_ZIPPED("Decode from Base64(zipped)") {
        
        @Override
        public String run(String value) throws IOException {
            
            return StringUtil.fromBase64Zip(value);
        }
    },
    
    DECODE_FROM_BASE64("Decode from Base64") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base64Decode(value);
        }
    },
    
    DECODE_FROM_BASE32("Decode from Base32") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base32Decode(value);
        }
    },
    
    DECODE_FROM_BASE58("Decode from Base58") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base58Decode(value);
        }
    },
    
    DECODE_FROM_BASE16("Decode from Base16") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.base16Decode(value);
        }
    },
    
    DECODE_FROM_HTML("Decode from Html") {
        
        @Override
        public String run(String value) {
            
            return StringUtil.fromHtml(value);
        }
    },
    
    DECODE_FROM_UNICODE("Decode from Unicode") {
        
        @Override
        public String run(String value) {
            
            return StringEscapeUtils.unescapeJava(value);
        }
    },
    
    DECODE_FROM_URL("Decode from Url") {
        
        @Override
        public String run(String value) throws UnsupportedEncodingException {
            
            return
                StringUtils
                .replaceEach(
                    StringUtil.fromUrl(value),
                    new String[] { "&", "\"", "<", ">" },
                    new String[] { "&amp;", "&quot;", "&lt;", "&gt;" }
                );
        }
    };

    protected String name;

    private ActionCoder(String name) {
        this.name = name;
    }

    public static Optional<ActionCoder> forName(String name) {
        
        return
            Arrays
            .asList(ActionCoder.values())
            .stream()
            .filter(action -> name.equals(action.name))
            .findFirst();
    }

    public abstract String run(String value) throws DecoderException, IOException, NoSuchAlgorithmException;
}
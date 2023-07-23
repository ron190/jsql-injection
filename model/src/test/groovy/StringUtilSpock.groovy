import com.jsql.util.StringUtil
import com.jsql.util.bruter.HashUtil
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class StringUtilSpock extends Specification {
    
    def 'Check encoding/decoding methods from StringUtil and HashUtil'() {
        
        expect:
            StringUtil.decimalHtmlEncode('יאח') == '&#233;&#224;&#231;'
            StringUtil.hexstr('313233616263') == '123abc'
            !StringUtil.isUtf8('eca')
            !StringUtil.isUtf8(null)
            StringUtil.isUtf8('יחא')
            StringUtil.base64Encode('יחא') == 'w6nDp8Og'
            StringUtil.base64Decode('w6nDp8Og') == 'יחא'
            StringUtil.base16Encode('יחא') == 'C3A9C3A7C3A0'
            StringUtil.base16Decode('C3A9C3A7C3A0') == 'יחא'
            StringUtil.base32Encode('יחא') == 'YOU4HJ6DUA======'
            StringUtil.base32Decode('YOU4HJ6DUA======') == 'יחא'
            StringUtil.base58Encode('יחא') == '2gSCm18Kq'
            StringUtil.base58Decode('2gSCm18Kq') == 'יחא'
            StringUtil.clean('/**//*!*/  a  a  /*a*/-  b  b  -  c  c  d') == '/**//*!*/a a-b b-c c d'

            StringUtil.compress(null) == null
            StringUtil.decompress(null) == null
            
            HashUtil.toMd4('eac') == '128A23E4553B3EE368109E5CEE8CF2C1'
            HashUtil.toAdler32('eac') == '39256362'
            HashUtil.toCrc16('eac') == 'a679'
            HashUtil.toCrc32('eac') == '419478237'
            HashUtil.toCrc64('eac') == '6380454362392559616'
            HashUtil.toMySql('eac') != null  // unstable
            HashUtil.toHash('md5', 'eac') == '31E0E4C9C2AEE79C4BFC58C460F4DDBF'

            StringUtil.toHex('eac') == '656163'
            StringUtil.fromHex('656163') == 'eac'
            [
                '1fc28b08000000000000004b4d4c0600c39dc2ba001903000000',     // Java 11
                '1fc28b08000000000000c3bf4b4d4c0600c39dc2ba001903000000'    // Java >=17
            ].contains(StringUtil.toHexZip('eac'))
            StringUtil.fromHexZip('1fc28b08000000000000004b4d4c0600c39dc2ba001903000000') == 'eac'
            [
                'H8KLCAAAAAAAAABLTUwGAMOdwroAGQMAAAA=', // Java 11
                'H8KLCAAAAAAAAMO/S01MBgDDncK6ABkDAAAA'  // Java >=17
            ].contains(StringUtil.toBase64Zip('eac'))
            StringUtil.fromBase64Zip('H8KLCAAAAAAAAABLTUwGAMOdwroAGQMAAAA=') == 'eac'
            StringUtil.toUrl('eac') == 'eac'
            StringUtil.fromUrl('eac') == 'eac'
            StringUtil.fromHtml('&eacute;&agrave;&ccedil;') == 'יאח'
            StringUtil.decimalHtmlEncode('<>&יאח', false) == '<>&&#233;&#224;&#231;'

            // Additional & for html parsing in textpane
            StringUtil.toHtml('יאח') == '&amp;eacute;&amp;agrave;&amp;ccedil;'
            StringUtil.decimalHtmlEncode('<>&יאח', true) == '&amp;lt;&amp;gt;&amp;&amp;#233;&amp;#224;&amp;#231;'
            
            StringUtil.detectUtf8(null) == StringUtils.EMPTY
            StringUtil.detectUtf8("יאחט") == new String("יאחט".bytes, StandardCharsets.UTF_8)
            StringUtil.detectUtf8("eace") == "eace"
    }
}
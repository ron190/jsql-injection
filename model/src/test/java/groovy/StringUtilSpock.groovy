package groovy

import java.nio.charset.StandardCharsets

import org.apache.commons.lang3.StringUtils

import com.jsql.util.StringUtil
import com.jsql.util.bruter.HashUtil

import spock.lang.Specification

class StringUtilSpock extends Specification {
    
    def 'Check decimalHtmlEncode'() {
        
        expect:
            StringUtil.decimalHtmlEncode('יאח') == '&#233;&#224;&#231;'
            StringUtil.hexstr('313233616263') == '123abc'
            StringUtil.isUtf8('eca') == false
            StringUtil.isUtf8(null) == false
            StringUtil.isUtf8('יחא') == true
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
            HashUtil.toHash('md5', 'eac') == '31E0E4C9C2AEE79C4BFC58C460F4DDBF'
            
            // Unstable
            // StringUtil.toMySql('eac') == '11B486AA8E872E374B5174BF1BE1592AEA28D7CE'
            
            StringUtil.toHex('eac') == '656163'
            StringUtil.fromHex('656163') == 'eac'
            StringUtil.toHexZip('eac') == '1fc28b08000000000000004b4d4c0600c39dc2ba001903000000'
            StringUtil.fromHexZip('1fc28b08000000000000004b4d4c0600c39dc2ba001903000000') == 'eac'
            StringUtil.toBase64Zip('eac') == 'H8KLCAAAAAAAAABLTUwGAMOdwroAGQMAAAA='
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
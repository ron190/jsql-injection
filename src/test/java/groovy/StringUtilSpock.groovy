package groovy

import org.apache.commons.text.StringEscapeUtils

import com.jsql.util.StringUtil

import spock.lang.Specification

class StringUtilSpock extends Specification {
    
    def stringUtil

    def 'Check decimalHtmlEncode'() {
        
        expect:
            stringUtil.decimalHtmlEncode('יאח') == '&#233;&#224;&#231;'
            stringUtil.hexstr('313233616263') == '123abc'
            stringUtil.isUtf8('eca') == false
            stringUtil.isUtf8(null) == false
            stringUtil.isUtf8('יחא') == true
            stringUtil.base64Encode('יחא') == 'w6nDp8Og'
            stringUtil.base64Decode('w6nDp8Og') == 'יחא'

            stringUtil.compress(null) == null
            stringUtil.decompress(null) == null
            
            StringUtil.toMd4('eac') == '128A23E4553B3EE368109E5CEE8CF2C1'
            StringUtil.toAdler32('eac') == '39256362'
            StringUtil.toCrc16('eac') == 'a679'
            StringUtil.toCrc32('eac') == '419478237'
            StringUtil.toCrc64('eac') == '6380454362392559616'
            StringUtil.toHash('md5', 'eac') == '31E0E4C9C2AEE79C4BFC58C460F4DDBF'
            
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
    }
    
    def setup() {
        stringUtil = new StringUtil()
    }
}
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
            
            StringUtil.toMd4('יאח') == 'F2C46E2ABB20203FDD7D73B9A1BDBCFA'
            StringUtil.toAdler32('יאח') == '90505905'
            StringUtil.toCrc16('יאח') == '7ed8'
            StringUtil.toCrc32('יאח') == '962770442'
            StringUtil.toCrc64('יאח') == '-8749774789217878016'
            StringUtil.toHash('md5', 'יאח') == 'AA676AC3C6F41D942ABF5F1CB0F6BAFC'
            StringUtil.toMySql('יאח') == '48481696940015FA4B6C5947F110340339290749'
            StringUtil.toHex('יאח') == 'c3a9c3a0c3a7'
            StringUtil.fromHex('c3a9c3a0c3a7') == 'יאח'
            StringUtil.toHexZip('יאח') == '1fc28b08000000000000007bc3b9c3a039000ac2b6623903000000'
            StringUtil.fromHexZip('1fc28b08000000000000007bc3b9c3a039000ac2b6623903000000') == 'יאח'
            StringUtil.toBase64Zip('יאח') == 'H8KLCAAAAAAAAAB7w7nDoDkACsK2YjkDAAAA'
            StringUtil.fromBase64Zip('H8KLCAAAAAAAAAB7w7nDoDkACsK2YjkDAAAA') == 'יאח'
            StringUtil.toUrl('יאח') == '%C3%A9%C3%A0%C3%A7'
            StringUtil.fromUrl('%C3%A9%C3%A0%C3%A7') == 'יאח'
            StringUtil.fromHtml('&eacute;&agrave;&ccedil;') == 'יאח'
            StringUtil.decimalHtmlEncode('<>&יאח', false) == '<>&&#233;&#224;&#231;'

            StringUtil.toHtml('יאח') == '&amp;eacute;&amp;agrave;&amp;ccedil;'
            StringUtil.decimalHtmlEncode('<>&יאח', true) == '&amp;lt;&amp;gt;&amp;&amp;#233;&amp;#224;&amp;#231;'
    }
    
    def setup() {
        stringUtil = new StringUtil()
    }
}
package groovy

import com.jsql.util.StringUtil

import spock.lang.Specification

class StringUtilSpock extends Specification {
    
    def stringUtil

    def 'Check decimalHtmlEncode'() {
        expect:
            stringUtil.decimalHtmlEncode('יאח') == '&#233;&#224;&#231;'
            stringUtil.hexstr('313233616263') == '123abc'
            stringUtil.isUtf8('eca') == false
            stringUtil.isUtf8('יחא') == true
            stringUtil.base64Encode('יחא') == 'w6nDp8Og'
            stringUtil.base64Decode('w6nDp8Og') == 'יחא'
    }
    
    def setup() {
        stringUtil = new StringUtil()
    }
    
}
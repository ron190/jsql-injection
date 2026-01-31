import com.jsql.util.StringUtil
import com.jsql.util.bruter.ActionCoder
import com.jsql.util.bruter.HashUtil
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class StringUtilSpock extends Specification {
    
    def 'Check encoding/decoding methods from StringUtil and HashUtil'() {
        
        expect:
            StringUtil.hexstr('313233616263') == '123abc'
            !StringUtil.containsNonStandardScripts('eca')
            !StringUtil.containsNonStandardScripts(null)
            !StringUtil.containsNonStandardScripts('יחא')
            StringUtil.base64Encode('יחא') == 'w6nDp8Og'
            StringUtil.base64Decode('w6nDp8Og') == 'יחא'
            StringUtil.base16Encode('יחא') == 'C3A9C3A7C3A0'
            StringUtil.base16Decode('C3A9C3A7C3A0') == 'יחא'
            StringUtil.base32Encode('יחא') == 'YOU4HJ6DUA======'
            StringUtil.base32Decode('YOU4HJ6DUA======') == 'יחא'
            StringUtil.base58Encode('יחא') == '2gSCm18Kq'
            StringUtil.base58Decode('2gSCm18Kq') == 'יחא'
            StringUtil.cleanSql('''
                /**//*!*/  a  a  /*
                a
                */-  b  
                b  -  c
                c  d
            ''') == '/**//*!*/a a-b b-c c d'  // var integer operation in a-b
            StringUtil.cleanSql('ls -l') == 'ls-l'  // not SQL, bypassed with encoding

            HashUtil.toMd4('eac') == '128A23E4553B3EE368109E5CEE8CF2C1'
            HashUtil.toAdler32('eac') == '39256362'
            HashUtil.toCrc16('eac') == 'a679'
            HashUtil.toCrc32('eac') == '419478237'
            HashUtil.toCrc64('eac') == '6380454362392559616'
            HashUtil.toMySql('eac') != null  // unstable
            HashUtil.toHash('md5', 'eac') == '31E0E4C9C2AEE79C4BFC58C460F4DDBF'

            StringUtil.toUrl('eac') == 'eac'
            StringUtil.fromUrl('eac') == 'eac'
            StringUtil.toHex('eac') == '656163'
            StringUtil.fromHex('656163') == 'eac'

            StringUtil.toHexZip('eac') == '789c4b4d4c06000257012a'
            StringUtil.fromHexZip('789c4b4d4c06000257012a') == 'eac'
            StringUtil.toBase64Zip('eac') == 'eJxLTUwGAAJXASo='
            StringUtil.fromBase64Zip('eJxLTUwGAAJXASo=') == 'eac'

            var specialChars = '<>>#78970-ט_)אח-_ט!,:;?§./.?*ש^$µ%£'
            var specialCharsEncode = StandardCharsets.UTF_8.encode(specialChars).toString()
            var hexZip = '789ccb4a2c4bd4cbcbccd7f3484d2c70aa2c49752a4d4b4b2d8a2ec82fb63550c8c9ccb5353156484e2cb035378f050067db0f2d'
            var b64Zip = 'eJzLSixL1MvLzNfzSE0scKosSXUqTUtLLYouyC+2NVDIycy1NTFWSE4ssDU3jwUAZ9sPLQ=='
            var url = '%3C%3E%3E%2378970-%C3%A8_%29%C3%A0%C3%A7-_%C3%A8%21%2C%3A%3B%3F%C2%A7.%2F.%3F*%C3%B9%5E%24%C2%B5%25%C2%A3'
            StringUtil.toHexZip(specialCharsEncode) == hexZip
            StringUtil.fromHexZip(hexZip) == specialCharsEncode
            StringUtil.toBase64Zip(specialCharsEncode) == b64Zip
            StringUtil.fromBase64Zip(b64Zip) == specialCharsEncode
            StringUtil.toUrl(specialChars) == url
            StringUtil.fromUrl(url) == specialChars

            StringUtil.fromHtml('&eacute;&agrave;&ccedil;') == 'יאח'
            StringUtil.toHtml('יאח') == '&eacute;&agrave;&ccedil;'
            StringUtil.toHtmlDecimal('<>&יאח') == '<>&&#233;&#224;&#231;'

            StringUtil.detectUtf8(null) == StringUtils.EMPTY
            StringUtil.detectUtf8("יאחט") == new String("יאחט".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
            StringUtil.detectUtf8("eace") == "eace"

            ActionCoder.getHashesEmpty().forEach {
                assert ActionCoder.forName(it).orElseThrow().run('') ==~ /[A-Z0-9]{25,}/
            }
    }
}
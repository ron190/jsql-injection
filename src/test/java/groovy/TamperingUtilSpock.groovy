package groovy

import com.jsql.util.tampering.TamperingUtil

import spock.lang.Specification

class TamperingUtilSpock extends Specification {
    
    def tamperingUtil
    def result

    def 'Check Base64 tampering'() {
        when: tamperingUtil.set(true, false, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ '123abc' +'lSqQsL')
        then: result == 'MTIzYWJj'
    }

    def 'Check VersionComment tampering'() {
        when: tamperingUtil.set(false, true, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'aconcat()' +'lSqQsL')
        then: result == 'a/*!concat*/()'
    }

    def 'Check FunctionComment tampering'() {
        when: tamperingUtil.set(false, false, true, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'aconcat()' +'lSqQsL')
        then: result == 'aconcat/**/()'
    }

    def 'Check EqualToLike tampering'() {
        when: tamperingUtil.set(false, false, false, true, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'a=b' +'lSqQsL')
        then: result == 'a+like+b'
    }
    
    def 'Check RandomCase tampering'() {
        when: tamperingUtil.set(false, false, false, false, true, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'abcdefghijklmnopqrstuvwxyz' +'lSqQsL')
        then:
            ![
                'abcdefghijklmnopqrstuvwxyz',
                'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
            ].contains(result)
            result.toLowerCase() == 'abcdefghijklmnopqrstuvwxyz'
            result.toUpperCase() == 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    }

    def 'Check HexToChar tampering'() {
        when: tamperingUtil.set(false, false, false, false, false, true, false, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ '0x4142' +'lSqQsL')
        then: result == 'concat(char(65),char(66))'
    }

    def 'Check QuoteToUtf8 tampering'() {
        when: tamperingUtil.set(false, false, false, false, false, false, true, false, false, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ "'" +'lSqQsL')
        then: result == '%ef%bc%87'
    }
    
    def 'Check SpaceToMultilineComment tampering'() {
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, true, false, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'a+b' +'lSqQsL')
        then: result == 'a/**/b'
    }
    
    def 'Check SpaceToDashComment tampering'() {
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, true, false)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'a+b' +'lSqQsL')
        then: result == 'a--%0Ab'
    }
    
    def 'Check SpaceToSharpComment tampering'() {
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, false, true)
        and: result = tamperingUtil.tamper('SlQqLs'+ 'a+b' +'lSqQsL')
        then: result == 'a%23%0Ab'
    }
    
    def setup() {
        tamperingUtil = new TamperingUtil()
        result = null
    }
    
}
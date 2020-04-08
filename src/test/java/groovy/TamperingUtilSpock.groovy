package groovy

import com.jsql.util.tampering.TamperingUtil

import spock.lang.Specification

class TamperingUtilSpock extends Specification {
    
    def tamperingUtil
    def result

    def 'Check Base64 tampering'() {
        
        when: tamperingUtil.set(true, false, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ '123abc' +'</tampering>')
        then: result == 'MTIzYWJj'
    }

    def 'Check VersionComment tampering'() {
        
        when: tamperingUtil.set(false, true, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'aconcat()' +'</tampering>')
        then: result == 'a/*!concat*/()'
    }

    def 'Check FunctionComment tampering'() {
        
        when: tamperingUtil.set(false, false, true, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'aconcat()' +'</tampering>')
        then: result == 'aconcat/**/()'
    }

    def 'Check EqualToLike tampering'() {
        
        when: tamperingUtil.set(false, false, false, true, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a=b' +'</tampering>')
        then: result == 'a+like+b'
    }
    
    def 'Check RandomCase tampering'() {
        
        when: tamperingUtil.set(false, false, false, false, true, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'abcdefghijklmnopqrstuvwxyz' +'</tampering>')
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
        and: result = tamperingUtil.tamper('<tampering>'+ '0x4142' +'</tampering>')
        then: result == 'concat(char(65),char(66))'
    }

    def 'Check QuoteToUtf8 tampering'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, true, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ "'" +'</tampering>')
        then: result == '%ef%bc%87'
    }
    
    def 'Check SpaceToMultilineComment tampering'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, true, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a+b' +'</tampering>')
        then: result == 'a/**/b'
    }
    
    def 'Check SpaceToDashComment tampering'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, true, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a+b' +'</tampering>')
        then: result == 'a--%0Ab'
    }
    
    def 'Check SpaceToSharpComment tampering'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, false, true)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a+b' +'</tampering>')
        then: result == 'a%23%0Ab'
    }
    
    def 'Check no tampering'() {
        
        when: result = tamperingUtil.tamper('abc')
        then: result == ''
    }
    
    def 'Check eval'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, true, false, false, false)
        and: tamperingUtil.customTamper = '''
            var tampering = function(sql) {
                return sql.replace(/a/, 'b')
            }            
        '''
        and: result = tamperingUtil.tamper('<tampering>'+ 'a' +'</tampering>')
        then: result == 'b'
    }
    
    def setup() {
        
        tamperingUtil = new TamperingUtil()
        result = null
    }
}
package groovy

import com.jsql.util.TamperingUtil

import spock.lang.Specification

class TamperingUtilSpock extends Specification {
    
    def tamperingUtil
    def result

    def 'Check Base64'() {
        
        when: tamperingUtil.set(true, false, false, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ '123abc' +'</tampering>')
        then: result == 'MTIzYWJj'
    }

    def 'Check VersionComment'() {
        
        when: tamperingUtil.set(false, true, false, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'aconcat(b c,d,`e`)f,%2b,0x06' +'</tampering>')
        then: result == '/*!aconcat*/(b c,d,`e`)f,%2b,0x06'
    }

    def 'Check FunctionComment'() {
        
        when: tamperingUtil.set(false, false, true, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'aconcat(b)' +'</tampering>')
        then: result == 'aconcat/**/(b)'
    }

    def 'Check EqualToLike'() {
        
        when: tamperingUtil.set(false, false, false, true, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a=b' +'</tampering>')
        then: result == 'a+like+b'
    }

    def 'Check RandomCase'() {
        
        when: tamperingUtil.set(false, false, false, false, true, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'abcdefghijklmnopqrstuvwxyz' +'</tampering>')
        then:
            ![
                'abcdefghijklmnopqrstuvwxyz',
                'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
            ].contains(result)
            result.toLowerCase() == 'abcdefghijklmnopqrstuvwxyz'
            result.toUpperCase() == 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    }

    def 'Check StringToChar ab'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, false, false, true)
        and: result = tamperingUtil.tamper('<tampering>'+ "'ab'" +'</tampering>')
        then: result == 'concat(char(97),char(98))'
    }

    def 'Check HexToChar AB'() {
        
        when: tamperingUtil.set(false, false, false, false, false, true, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ '0x4142' +'</tampering>')
        then: result == 'concat(char(65),char(66))'
    }

    def 'Check QuoteToUtf8'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, true, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ "'" +'</tampering>')
        then: result == '%ef%bc%87'
    }
    
    // Multiple
    
    def 'Check VersionComment+FunctionComment'() {
        
        when: tamperingUtil.set(false, true, true, false, false, false, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'aconcat(b)' +'</tampering>')
        then: result == 'aconcat/**/(b)'
    }
    
    def 'Check VersionComment+FunctionComment+HexToChar'() {
        
        when: tamperingUtil.set(false, true, true, false, false, true, false, false, false, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'aconcat(0x4142)' +'</tampering>')
        then: result == 'aconcat/**/(concat/**/(char/**/(65),char/**/(66)))'
    }
    
    // Comments
    
    def 'Check SpaceToMultilineComment'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, true, false, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a+b' +'</tampering>')
        then: result == 'a/**/b'
    }
    
    def 'Check SpaceToDashComment'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, true, false, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a+b' +'</tampering>')
        then: result == 'a--%0Ab'
    }
    
    def 'Check SpaceToSharpComment'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, false, false, false, true, false)
        and: result = tamperingUtil.tamper('<tampering>'+ 'a+b' +'</tampering>')
        then: result == 'a%23%0Ab'
    }
    
    // Special
    
    def 'Check no tampering'() {
        
        when: result = tamperingUtil.tamper('<tampering>'+ 'abc' +'</tampering>')
        then: result == 'abc'
    }
    
    def 'Check eval'() {
        
        when: tamperingUtil.set(false, false, false, false, false, false, false, true, false, false, false, false)
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
package groovy

import org.apache.log4j.Logger

import com.jsql.model.InjectionModel
import com.jsql.model.bean.database.Column
import com.jsql.model.bean.database.Database
import com.jsql.model.bean.database.Table
import com.jsql.model.exception.InjectionFailureException
import com.jsql.util.bruter.HashBruter

import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class BruterSpock extends Specification {

    def bruter
    def conditions
    
    def 'Check simple MD5 bruteforce'() {
        
        when:
            bruter.hash = '900150983CD24FB0D6963F7D28E17F72'
            bruter.type = 'md5'
            bruter.minLength = 1
            bruter.maxLength = 3
            bruter.addLowerCaseLetters()
        and:
            
        and:
            bruter.tryBruteForce()
        
        then:
            conditions.eventually {
                assert bruter.isDone()
                assert bruter.isFound()
            }
            bruter.password == 'abc'
            bruter.generatedHash == '900150983CD24FB0D6963F7D28E17F72'
            bruter.found == bruter.done == true
    }
    
    def setup() {
        
        bruter = new HashBruter()
        conditions = new PollingConditions(timeout: 5)
    }
}
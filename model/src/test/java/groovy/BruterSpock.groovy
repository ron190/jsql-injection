package groovy

import com.jsql.util.bruter.HashBruter

import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class BruterSpock extends Specification {

    def bruter
    def conditions
    
    def 'Check simple MD5 bruteforce with upperCase and specialCharacters'() {
        
        when:
            bruter.hash = '1BB6A03994B24BF5A99D9445176A8F76'
            bruter.addUpperCaseLetters()
            bruter.addSpecialCharacters()
            
        and:
            bruter.tryBruteForce()
        
        then:
            conditions.eventually {
                assert bruter.done
                assert bruter.found
            }
            bruter.password == 'A~'
            bruter.generatedHash == '1BB6A03994B24BF5A99D9445176A8F76'
            bruter.found == bruter.done == true
    }
    
    def 'Check simple MD5 bruteforce with lowerCase and digits'() {
        
        when:
            bruter.hash = '8A8BB7CD343AA2AD99B7D762030857A2'
            bruter.addLowerCaseLetters()
            bruter.addDigits()
            
        and:
            bruter.tryBruteForce()
        
        then:
            conditions.eventually {
                assert bruter.done 
                assert bruter.found
            }
            bruter.password == 'a1'
            bruter.generatedHash == '8A8BB7CD343AA2AD99B7D762030857A2'
            bruter.found == bruter.done == true
    }
    
    def 'Check simple MD5 bruteforce not found'() {
        
        when:
            bruter.hash = '0CC175B9C0F1B6A831C399E269772661'
            bruter.addLowerCaseLetters()
            bruter.excludeChars('a')
            bruter.maxLength = 1
            
        and:
            bruter.starttime = System.nanoTime();
            bruter.tryBruteForce()
            bruter.endtime = System.nanoTime();
        
        then:
            conditions.eventually {
                assert bruter.done
            }
            bruter.password == 'z'
            bruter.generatedHash == 'FBADE9E36A3F36D3D676C1B808451DD7'
            bruter.found == false
            bruter.done == true
            bruter.numberOfPossibilities == 25
            bruter.remainder == 0
            bruter.endtime - bruter.starttime > 0
    }
    
    def 'Check bruteforce hash per second and elapsed time format'() {
        
        def conditionsHashNotFound = new PollingConditions(timeout: 5)
        
        when:
            bruter.hash = '0CC175B9C0F1B6A831C399E269772661'
            bruter.addLowerCaseLetters()
            bruter.excludeChars('a')
            bruter.maxLength = 10
            
        and:
        Thread.start {
            bruter.starttime = System.nanoTime();
            bruter.tryBruteForce()
            sleep(1500);
        }
        sleep(1500);
        bruter.endtime = System.nanoTime();
        
        then:
            conditionsHashNotFound.eventually {}
            bruter.done == false
            bruter.calculateTimeElapsed() =~ /Time elapsed: \ddays \dh \dmin \ds/
            bruter.getPerSecond() > 0
    }
    
    def 'Check elapsed time format'() {
        
        when:
            bruter.starttime = 0000000000000000d;
            bruter.endtime =   0003600000000000d;
        then:
            bruter.calculateTimeElapsed() =~ /Time elapsed: 0days 1h 0min 0s/
        
        when:
            bruter.starttime = 0000000000000000d;
            bruter.endtime =   0000060000000000d;
        then:
            bruter.calculateTimeElapsed() =~ /Time elapsed: 0days 0h 1min 0s/
        
        when:
            bruter.starttime = 0000000000000000d;
            bruter.endtime =   0093784000000000d;
        then:
            bruter.calculateTimeElapsed() =~ /Time elapsed: 1days 2h 3min 4s/
        
        when:
            bruter.starttime = 0000000000000000d;
            bruter.endtime =   0090061000000000d;
        then:
            bruter.calculateTimeElapsed() =~ /Time elapsed: 1days 1h 1min 1s/
    }
    
    def setup() {
        
        bruter = new HashBruter()
        bruter.type = 'md5'
        bruter.minLength = 1
        bruter.maxLength = 2
        
        conditions = new PollingConditions(timeout: 5)
    }
}
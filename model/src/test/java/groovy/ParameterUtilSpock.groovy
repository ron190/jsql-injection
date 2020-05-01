package groovy

import com.jsql.model.InjectionModel
import com.jsql.model.exception.InjectionFailureException

import spock.lang.Specification

class ParameterUtilSpock extends Specification {
    
    def injectionModel
    def parameterUtil

    def 'Check STAR is used by correct method injection when check all param is disabled'() {
        
        when: parameterUtil.initializeQueryString('http://127.0.0.1?a=a*')
        and: parameterUtil.initializeHeader('')
        and: parameterUtil.initializeRequest('')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.header
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when: parameterUtil.initializeQueryString('http://127.0.0.1')
        and: parameterUtil.initializeHeader('a: a*')
        and: parameterUtil.initializeRequest('')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.request
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when: parameterUtil.initializeQueryString('http://127.0.0.1')
        and: parameterUtil.initializeHeader('')
        and: parameterUtil.initializeRequest('a=a*')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.query
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }

    def 'Check that empty query string, request and header is not allowed'() {
        
        when: parameterUtil.initializeQueryString('http://127.0.0.1')
        
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.query
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.request
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.header
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }

    def 'Check STAR is used one time only'() {
        
        when: parameterUtil.initializeQueryString('http://127.0.0.1?a=a*&b=b')
        and: parameterUtil.initializeRequest('a=a*&b=b')
        and: parameterUtil.initializeHeader('''
            a: a*
            b: b
        ''')
        
        and: parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }
    
    def setup() {
        
        injectionModel = new InjectionModel()
        parameterUtil = injectionModel.mediatorUtils.parameterUtil
    }
}
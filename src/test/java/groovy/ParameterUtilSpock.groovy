package groovy

import com.jsql.model.InjectionModel
import com.jsql.model.exception.InjectionFailureException

import spock.lang.Specification

class ParameterUtilSpock extends Specification {

    def 'Check STAR is used by correct method injection when check all param is disabled'() {
        
        when: parameterUtil.initQueryString('http://127.0.0.1?a=a*')
        and: parameterUtil.initHeader('')
        and: parameterUtil.initRequest('')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethodInjection.header
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when: parameterUtil.initQueryString('http://127.0.0.1')
        and: parameterUtil.initHeader('a: a*')
        and: parameterUtil.initRequest('')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethodInjection.request
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when: parameterUtil.initQueryString('http://127.0.0.1')
        and: parameterUtil.initHeader('')
        and: parameterUtil.initRequest('a=a*')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethodInjection.query
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }

    def 'Check that empty querystring, request and header is not allowed'() {
        
        when: parameterUtil.initQueryString('http://127.0.0.1')
        
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethodInjection.query
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethodInjection.request
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethodInjection.header
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }

    def 'Check STAR is used one time only'() {
        
        when: parameterUtil.initQueryString('http://127.0.0.1?a=a*&b=b')
        and: parameterUtil.initRequest('a=a*&b=b')
        and: parameterUtil.initHeader('''
            a: a*
            b: b
        ''')
        
        and: parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }
    
    def injectionModel
    def parameterUtil
    def setup() {
        injectionModel = new InjectionModel()
        parameterUtil = injectionModel.mediatorUtils.parameterUtil
    }
    
}
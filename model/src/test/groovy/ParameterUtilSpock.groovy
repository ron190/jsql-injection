import com.jsql.model.InjectionModel
import com.jsql.model.exception.InjectionFailureException
import com.jsql.util.ParameterUtil
import spock.lang.Specification

class ParameterUtilSpock extends Specification {

    InjectionModel injectionModel
    ParameterUtil parameterUtil

    def 'Check STAR is used by correct method injection when check all param is disabled'() {
        when: parameterUtil.initQueryString('http://127.0.0.1?a=a*')
        and: parameterUtil.initHeader('')
        and: parameterUtil.initRequest('')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.header
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when: parameterUtil.initQueryString('http://127.0.0.1')
        and: parameterUtil.initHeader('a: a*')
        and: parameterUtil.initRequest('')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.request
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
        
        when: parameterUtil.initQueryString('http://127.0.0.1')
        and: parameterUtil.initHeader('')
        and: parameterUtil.initRequest('a=a*')
        and:
            injectionModel.mediatorUtils.connectionUtil.methodInjection = injectionModel.mediatorMethod.query
            parameterUtil.checkParametersFormat()
        then: thrown InjectionFailureException
    }

    def 'Check that empty query string, request and header is not allowed'() {
        when: parameterUtil.initQueryString('http://127.0.0.1')
        
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
        when: parameterUtil.initQueryString('http://127.0.0.1?a=a*&b=b')
        and: parameterUtil.initRequest('a=a*&b=b')
        and: parameterUtil.initHeader('''
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
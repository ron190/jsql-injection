package groovy

import com.jsql.util.I18nUtil

import spock.lang.Specification

class I18nUtilSpock extends Specification {

    def 'Check locale is asian'() {

        expect: I18nUtil.isAsian(Locale.FRANCE) == false
        and: I18nUtil.isAsian(Locale.KOREAN) == true
        and: I18nUtil.isAsian(Locale.JAPANESE) == true
        and: I18nUtil.isAsian(Locale.CHINESE) == true
    }
}
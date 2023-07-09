import com.jsql.util.I18nUtil
import spock.lang.Specification

class I18nUtilSpock extends Specification {

    def 'Check locale is asian'() {

        expect:
            !I18nUtil.isAsian(Locale.FRANCE)
            I18nUtil.isAsian(Locale.KOREAN)
            I18nUtil.isAsian(Locale.JAPANESE)
            I18nUtil.isAsian(Locale.CHINESE)
    }
}
package groovy

import java.util.AbstractMap.SimpleEntry
import java.util.stream.Collectors

import org.json.JSONArray
import org.json.JSONObject

import com.jsql.util.I18nUtil
import com.jsql.util.JsonUtil

import spock.lang.Specification

class I18nUtilSpock extends Specification {

    def 'Check locale is asian'() {

        expect: I18nUtil.isAsian(Locale.FRANCE) == false
        and: I18nUtil.isAsian(Locale.KOREAN) == true
        and: I18nUtil.isAsian(Locale.JAPANESE) == true
        and: I18nUtil.isAsian(Locale.CHINESE) == true
    }
}
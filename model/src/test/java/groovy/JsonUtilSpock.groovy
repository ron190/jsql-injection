package groovy

import java.util.AbstractMap.SimpleEntry
import java.util.stream.Collectors

import org.json.JSONArray
import org.json.JSONObject

import com.jsql.util.JsonUtil

import spock.lang.Specification

class JsonUtilSpock extends Specification {

    def 'Add STAR when searching for key'() {
        
        when: List<SimpleEntry<String, String>> entries = JsonUtil.createEntries(oJsonObject, "root", parentXPath)
        
        then:
            ((JSONObject) oJsonObject)
            .getJSONArray('d')
            .getJSONArray(2)
            .getJSONObject(0)
            .get('d') == 'd*'
        
        where:
            parentXPath = new SimpleEntry<String, String>("root.d[2][0].d", "d")
            oJsonObject = JsonUtil.getJson('''
                {
                    a: 'a',
                    b: {b: 'b'},
                    c: [{c: 'c'}],
                    d: [null, null, [{d: 'd'}]],
                    e: {e: [null, null, [{e: 'e'}]]},
                }
            ''')
    }

    def 'Replace STAR when not searching for key'() {
        
        when: JsonUtil.createEntries(oJsonObject, "root", null)
        and: List<SimpleEntry<String, String>> entries = JsonUtil.createEntries(oJsonObject, "root", null)
        
        then:
            entries.stream().map({e -> e.toString()}).collect(Collectors.toList()) ==
            [
                'root.a=a',
                'root.b.b=b',
                'root.c[2].c=c',
                'root.d[2][0].d=d',
                'root.e.e[4][0].e=e',
            ]
        
        where:
            oJsonObject = JsonUtil.getJson('''
                {
                    a: 'a*',
                    b: {b: 'b*'},
                    c: [0, false, {c: 'c*'}],
                    d: [null, null, [{d: 'd*'}]],
                    e: {e: [null, 1, false, null, [{e: 'e*'}]]},
                }
            ''')
    }
        
    def 'Map json string to xpath'() {
        
        when: List<SimpleEntry<String, String>> entriesJsonObject = JsonUtil.createEntries(oJsonObject, "root", null)
        and: List<SimpleEntry<String, String>> entriesJsonArray = JsonUtil.createEntries(oJsonArray, "root", null)
        then:
            entriesJsonObject.stream().map({e -> e.toString()}).collect(Collectors.toList()) ==
            [
                'root.a=a',
                'root.b.b=b',
                'root.c[0].c=c',
                'root.d[2][0].d=d',
                'root.e.e[2][0].e=e',
                'root.f[2][4].f[2]=f',
            ]
            entriesJsonArray.stream().map({e -> e.toString()}).collect(Collectors.toList()) ==
            [
                'root[3].a=a',
                'root[3].b.b=b',
                'root[3].c[0].c=c',
                'root[3].d[2][0].d=d',
                'root[3].e.e[2][0].e=e',
                'root[3].f[2][4].f[2]=f',
            ]
        
        where:
            oJsonObject = JsonUtil.getJson('''
                {
                    a: 'a',
                    b: {b: 'b'},
                    c: [{c: 'c'}],
                    d: [null, null, [{d: 'd'}]],
                    e: {e: [null, null, [{e: 'e'}]]},
                    f: [1, false, [1, true, {f: true}, {f: 1}, {f: [true, 1, 'f']}]]
                }
            ''')
            
            oJsonArray = JsonUtil.getJson("""
                [
                    1,
                    true,
                    null,
                    ${oJsonObject},
                    null
                ]
            """)
    }
    
    def 'Convert json string to Java JSON'() {
        
        expect: JsonUtil.getJson("{}") instanceof JSONObject
        and: JsonUtil.getJson("[]") instanceof JSONArray
        and: JsonUtil.getJson("0") instanceof Object
    }
    
    def 'Test json container'() {
        
        expect: JsonUtil.isJson("{}") == true
        and: JsonUtil.isJson("[]") == true
        and: JsonUtil.isJson("0") == false
    }
}
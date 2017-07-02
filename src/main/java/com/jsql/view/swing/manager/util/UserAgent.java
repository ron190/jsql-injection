package com.jsql.view.swing.manager.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public enum UserAgent {
    
    CHROME("Chrome", UserAgentType.BROWSER, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"),
    CHROMIUM("Chromium", UserAgentType.BROWSER, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/34.0.1847.116 Chrome/34.0.1847.116 Safari/537.36"),
    FIREFOX("Firefox", UserAgentType.BROWSER, "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0"),
    MAXTHON("Maxthon", UserAgentType.BROWSER, "Mozilla/5.0 (X11; Linux x86_64; Ubuntu 14.04.2 LTS) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.0 Maxthon/1.0.5.3 Safari/537.36"),
    SAFARI("Safari", UserAgentType.BROWSER, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A"),
    OPERA("Opera", UserAgentType.BROWSER, "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16"),
    SEAMONKEY("SeaMonkey", UserAgentType.BROWSER, "Mozilla/5.0 (Windows NT 5.2; RW; rv:7.0a1) Gecko/20091211 SeaMonkey/9.23a1pre"),
    
//    GMAIL("Gmail", UserAgentType.FEED_READER, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
//
//    GMAIL("Gmail", UserAgentType.LIBRARY, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
    
    ANDROID("Android", UserAgentType.MOBILE_BROWSER, "Mozilla/5.0 (Linux; U; Android 4.2; en-us; Nexus 10 Build/JVP15I) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30"),
    BLACKBERRY("BlackBerry", UserAgentType.MOBILE_BROWSER, "Mozilla/5.0 (BlackBerry; U; BlackBerry 9800; nl) AppleWebKit/534.8+ (KHTML, like Gecko) Version/6.0.0.668 Mobile Safari/534.8+"),
    CHROMEMOBILE("Chrome Mobile", UserAgentType.MOBILE_BROWSER, "Mozilla/5.0 (Linux; Android 4.1; Galaxy Nexus Build/JRN84D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"),
    DOLPHIN("Dolphin", UserAgentType.MOBILE_BROWSER, "Mozilla/5.0 (SAMSUNG; SAMSUNG-GT-S8500/S8500XXJF4; U; Bada/1.0; fr-fr) AppleWebKit/533.1 (KHTML, like Gecko) Dolfin/2.0 Mobile WVGA SMM-MMS/1.2.0 OPN-B"),
    DUCKDUCKGO("DuckDuckGo Mobile", UserAgentType.MOBILE_BROWSER, "DDG-Android-3.0.12"),
    FIREFOXMOBILE("Firefox Mobile", UserAgentType.MOBILE_BROWSER, "Mozilla/5.0 (Android 4.2.2; Tablet; rv:47.0) Gecko/47.0 Firefox/47.0"),
    EDGEMOBILE("Edge Mobile", UserAgentType.MOBILE_BROWSER, "Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; HTC; 0P6B180) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Mobile Safari/537.36 Edge/13.10586"),
    
//    GMAIL("Gmail", UserAgentType.MULTIMEDIA_PLAYER, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
    
    WGET("Wget", UserAgentType.OFFLINE_BROWSER, "Wget/1.16 (linux-gnu)"),
    
//    GMAIL("Gmail", UserAgentType.OTHER, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
//
//    GMAIL("Gmail", UserAgentType.VALIDATOR, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
//
//    GMAIL("Gmail", UserAgentType.WAP_BROWSER, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
    
    GMAIL("Gmail", UserAgentType.EMAIL_CLIENT, "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7 (via ggpht.com GoogleImageProxy)"),
    OUTLOOK("Outlook", UserAgentType.EMAIL_CLIENT, "Microsoft Office/16.0 (Microsoft Outlook Mail 16.0.6416; Pro)"),
    THUNDERBIRD("Thunderbird", UserAgentType.EMAIL_CLIENT, "Mozilla/5.0 (Windows NT 6.1; rv:45.0) Gecko/20100101 Thunderbird/45.0")
    ;
    
    private String label;
    private UserAgentType type;
    private String nameUserAgent;
    
    private static final Map<UserAgentType, List<UserAgent>> list = new EnumMap<>(UserAgentType.class);
    
    static {
        for (UserAgent u: UserAgent.values()) {
            if (list.get(u.type) == null) {
                list.put(u.type, new ArrayList<>());
            }
            list.get(u.type).add(u);
        }
    }
    
    private UserAgent(String label, UserAgentType type, String userAgent) {
        this.label = label;
        this.type = type;
        this.nameUserAgent = userAgent;
    }

    public String getLabel() {
        return this.label;
    }

    public UserAgentType getType() {
        return this.type;
    }

    public String getNameUserAgent() {
        return this.nameUserAgent;
    }

    public static Map<UserAgentType, List<UserAgent>> getList() {
        return list;
    }
    
}

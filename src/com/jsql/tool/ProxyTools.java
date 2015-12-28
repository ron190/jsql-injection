package com.jsql.tool;

import java.util.prefs.Preferences;

import com.jsql.model.injection.InjectionModel;
import com.jsql.model.injection.MediatorModel;

public class ProxyTools {
    /**
     * Utility class.
     */
    private ProxyTools() {
        //not called
    }
    
    public static void set(boolean useProxy, String proxyAddress, String proxyPort) {
        // Define proxy settings
        MediatorModel.model().useProxy = useProxy;
        MediatorModel.model().proxyAddress = proxyAddress;
        MediatorModel.model().proxyPort = proxyPort;

        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());
        prefs.putBoolean("useProxy", MediatorModel.model().useProxy);
        prefs.put("proxyAddress", MediatorModel.model().proxyAddress);
        prefs.put("proxyPort", MediatorModel.model().proxyPort);

        if (MediatorModel.model().useProxy) {
            System.setProperty("http.proxyHost", MediatorModel.model().proxyAddress);
            System.setProperty("http.proxyPort", MediatorModel.model().proxyPort);
        } else {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        }
    }
    
    public static void init() {
        // Use Preferences API to persist proxy configuration
        Preferences prefs = Preferences.userRoot().node(InjectionModel.class.getName());

        // Default proxy disabled
        MediatorModel.model().useProxy = prefs.getBoolean("useProxy", false);
        MediatorModel.model().checkUpdateAtStartup = prefs.getBoolean("updateAtStartup", true);
        MediatorModel.model().reportBugs = prefs.getBoolean("reportBugs", true);
        MediatorModel.model().enableEvasion = prefs.getBoolean("enableEvasion", false);
        MediatorModel.model().followRedirection = prefs.getBoolean("followRedirection", false);

        // Default TOR config
        MediatorModel.model().proxyAddress = prefs.get("proxyAddress", "127.0.0.1");
        MediatorModel.model().proxyPort = prefs.get("proxyPort", "8118");
        
        MediatorModel.model().prefPathFile = prefs.get("pathFile", System.getProperty("user.dir"));

        if (MediatorModel.model().useProxy) {
            System.setProperty("http.proxyHost", MediatorModel.model().proxyAddress);
            System.setProperty("http.proxyPort", MediatorModel.model().proxyPort);
        }
    }
}

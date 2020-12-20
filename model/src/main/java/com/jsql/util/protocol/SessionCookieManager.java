package com.jsql.util.protocol;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SessionCookieManager extends CookieHandler {
    
    private CookiePolicy policyCallback;
    
    private static final ThreadLocal<CookieStore> COOKIE_JARS = ThreadLocal.withInitial(() -> new CookieManager().getCookieStore());
    
    private static final SessionCookieManager INSTANCE = new SessionCookieManager();

    public SessionCookieManager() {
        this(null);
    }

    public static SessionCookieManager getInstance() {
        
        return INSTANCE;
    }

    public void clear() {
        
        this.getCookieStore().removeAll();
    }
    
    public SessionCookieManager(CookiePolicy cookiePolicy) {
        
        // use default cookie policy if not specify one
        this.policyCallback =
            cookiePolicy == null
            ? CookiePolicy.ACCEPT_ALL //note that I changed it to ACCEPT_ALL
            : cookiePolicy;

        // if not specify CookieStore to use, use default one
    }

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        
        // pre-condition check
        if (uri == null || requestHeaders == null) {
            
            throw new IllegalArgumentException("Argument is null");
        }

        Map<String, List<String>> cookieMap = new HashMap<>();
        
        // if there's no default CookieStore, no way for us to get any cookie
        if (this.getCookieStore() == null) {
            
            return Collections.unmodifiableMap(cookieMap);
        }

        List<HttpCookie> cookies = new ArrayList<>();
        
        for (HttpCookie cookie : this.getCookieStore().get(uri)) {
            
            // apply path-matches rule (RFC 2965 sec. 3.3.4)
            if (this.pathMatches(uri.getPath(), cookie.getPath())) {
                
                cookies.add(cookie);
            }
        }

        // apply sort rule (RFC 2965 sec. 3.3.4)
        List<String> cookieHeader = this.sortByPath(cookies);

        cookieMap.put("Cookie", cookieHeader);
        return Collections.unmodifiableMap(cookieMap);
    }


    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        
        // pre-condition check
        if (uri == null || responseHeaders == null) {
            
            throw new IllegalArgumentException("Argument is null");
        }

        // if there's no default CookieStore, no need to remember any cookie
        if (this.getCookieStore() == null) {
            
            return;
        }

        for (Entry<String, List<String>> entrySetHeader : responseHeaders.entrySet()) {
            
            // RFC 2965 3.2.2, key must be 'Set-Cookie2'
            // we also accept 'Set-Cookie' here for backward compatibility
            if (
                !"Set-Cookie2".equalsIgnoreCase(entrySetHeader.getKey())
                && !"Set-Cookie".equalsIgnoreCase(entrySetHeader.getKey())
            ) {
                continue;
            }

            this.putAcceptedCookie(uri, entrySetHeader);
        }
    }

    private void putAcceptedCookie(URI uri, Entry<String, List<String>> entrySetHeader) {
        
        for (String headerValue : entrySetHeader.getValue()) {
            
            try {
                List<HttpCookie> cookies = HttpCookie.parse(headerValue);
                
                for (HttpCookie cookie : cookies) {
                    
                    if (this.shouldAcceptInternal(uri, cookie)) {
                        
                        this.getCookieStore().add(uri, cookie);
                    }
                }
            } catch (IllegalArgumentException e) {
                // invalid set-cookie header string
                // no-op
            }
        }
    }

    /* ---------------- Private operations -------------- */

    // to determine whether or not accept this cookie
    private boolean shouldAcceptInternal(URI uri, HttpCookie cookie) {
        
        try {
            return this.policyCallback.shouldAccept(uri, cookie);
            
        } catch (Exception ignored) { // pretect against malicious callback
            
            return false;
        }
    }

    /**
     * path-matches algorithm, as defined by RFC 2965
     */
    private boolean pathMatches(String pathUri, String pathToMatchWith) {
        
        if (pathUri == pathToMatchWith) {
            
            return true;
        }
        
        if (pathUri == null || pathToMatchWith == null) {
            
            return false;
        }
        
        return pathUri.startsWith(pathToMatchWith);
    }

    /**
     * sort cookies with respect to their path: those with more specific Path attributes
     * precede those with less specific, as defined in RFC 2965 sec. 3.3.4
     */
    private List<String> sortByPath(List<HttpCookie> cookies) {
        
        Collections.sort(cookies, new CookiePathComparator());

        List<String> cookieHeader = new ArrayList<>();
        
        for (HttpCookie cookie : cookies) {
            
            // Netscape cookie spec and RFC 2965 have different format of Cookie
            // header; RFC 2965 requires a leading $Version="1" string while Netscape
            // does not.
            // The workaround here is to add a $Version="1" string in advance
            if (cookies.indexOf(cookie) == 0 && cookie.getVersion() > 0) {
                
                cookieHeader.add("$Version=\"1\"");
            }

            cookieHeader.add(cookie.toString());
        }
        
        return cookieHeader;
    }

    private class CookiePathComparator implements Comparator<HttpCookie> {
        
        @Override
        public int compare(HttpCookie c1, HttpCookie c2) {
            
            if (c1 == c2) {
                
                return 0;
            }
            
            if (c1 == null) {
                
                return -1;
            }
            
            if (c2 == null) {
                
                return 1;
            }

            // path rule only applies to the cookies with same name
            if (!c1.getName().equals(c2.getName())) {
                
                return 0;
            }

            // those with more specific Path attributes precede those with less specific
            if (c1.getPath().startsWith(c2.getPath())) {
                
                return -1;
                
            } else if (c2.getPath().startsWith(c1.getPath())) {
                
                return 1;
                
            } else {
                
                return 0;
            }
        }
    }
    
    public void setCookiePolicy(CookiePolicy cookiePolicy) {
        
        if (cookiePolicy != null) {
            
            this.policyCallback = cookiePolicy;
        }
    }

    public CookieStore getCookieStore() {
        
        return COOKIE_JARS.get();
    }
}
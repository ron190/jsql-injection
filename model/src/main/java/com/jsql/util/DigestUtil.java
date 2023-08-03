package com.jsql.util;

import com.jsql.model.InjectionModel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest.Builder;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class DigestUtil {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private String tokenDigest = null;

    private final InjectionModel injectionModel;

    public DigestUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public void parseWwwAuthenticate(Map<String, String> mapResponse) {

        if (
            mapResponse.containsKey(HeaderUtil.WWW_AUTHENTICATE_RESPONSE)
            && mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE).trim().startsWith("Digest")
        ) {

            String[] digestParts = StringUtils.split(
                mapResponse.get(HeaderUtil.WWW_AUTHENTICATE_RESPONSE).replaceAll("(?i)^\\s*Digest", ""),
                ","
            );

            Map<String, String> cookieValues = Arrays.stream(digestParts)
                .map(cookie -> {
                    String[] cookieEntry = StringUtils.split(cookie, "=");
                    return new SimpleEntry<>(
                        cookieEntry[0].trim(),
                        cookieEntry[1].trim()
                    );
                })
                .collect(
                    Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)
                );

            String realm = cookieValues.get("realm").replace("\"", "");
            String qop = cookieValues.get("qop").replace("\"", "");
            String nonce = cookieValues.get("nonce").replace("\"", "");

            try {
                String username = this.injectionModel.getMediatorUtils().getAuthenticationUtil().getUsernameAuthentication();
                String password = this.injectionModel.getMediatorUtils().getAuthenticationUtil().getPasswordAuthentication();
                String nc = "00000001";
                String cnonce = "2ecb0e39da79fcb5aa6ffb1bd45cb3bb";

                URL url = new URL(this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser());
                String path = url.getFile();

                String ha1 = DigestUtils.md5Hex(
                    String.format("%s:%s:%s", username, realm, password)
                );
                String ha2 = DigestUtils.md5Hex(
                    String.format("%s:%s", this.injectionModel.getMediatorUtils().getConnectionUtil().getTypeRequest(), path)
                );
                String response = DigestUtils.md5Hex(
                    String.format("%s:%s:%s:%s:%s:%s", ha1, nonce, nc, cnonce, qop, ha2)
                );

                this.tokenDigest = String.format(
                    "Digest username=\"%s\",realm=\"%s\",nonce=\"%s\",uri=\"%s\",cnonce=\"%s\",nc=%s,response=\"%s\",qop=\"%s\"",
                    username, realm, nonce, path, cnonce, nc, response, qop
                );

            } catch (MalformedURLException e) {

                LOGGER.error("Incorrect URL", e);
            }
        }
    }

    public void addHeaderToken(Builder httpRequest) {
        
        if (this.tokenDigest == null) {

             return;
        }

        httpRequest.setHeader("Authorization", this.tokenDigest);
    }

    public boolean isDigest() {
        return this.tokenDigest != null;
    }

    public void setTokenDigest(String tokenDigest) {
        this.tokenDigest = tokenDigest;
    }

    public String getTokenDigest() {
        return tokenDigest;
    }
}

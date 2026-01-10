package com.jsql.model.bean.util;

import java.util.Map;

/**
 * An HTTP object containing request and response data.
 *
 * @param url      GET request.
 * @param post     POST request.
 * @param header   Header request.
 * @param response Header sent back by server.
 */
public record HttpHeader(
    String url,
    String post,
    Map<String, String> header,
    Map<String, String> response,
    String source
) {
    @Override
    public String toString() {
        return this.url;
    }
}

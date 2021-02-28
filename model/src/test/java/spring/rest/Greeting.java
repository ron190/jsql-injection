package spring.rest;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class Greeting {

    private final long id = 0;
    private final String content;

    public Greeting(long id, String content) {
//        this.id = id;
        this.content = content;
    }

    public long getId() {
        return this.id;
    }

    @JsonRawValue
    public String getContent() {
//        return " <meta name=\"_csrf\" content=\"${_csrf.token}\"/>\r\n" +
//                "    <meta name=\"_csrf_header\" content=\"${_csrf.headerName}\"/>" + this.content;
        return this.content;
    }
}
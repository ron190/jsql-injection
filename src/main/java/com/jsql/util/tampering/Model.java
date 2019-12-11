package com.jsql.util.tampering;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "tooltip", "description", "group", "javascript" })
public class Model {

    @JsonProperty("tooltip")
    private String tooltip;
    @JsonProperty("description")
    private String description;
    @JsonProperty("group")
    private String group;
    @JsonProperty("javascript")
    private String javascript;

    @JsonProperty("tooltip")
    public String getTooltip() {
        return tooltip;
    }

    @JsonProperty("tooltip")
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("group")
    public String getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("javascript")
    public String getJavascript() {
        return javascript;
    }

    @JsonProperty("javascript")
    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

}

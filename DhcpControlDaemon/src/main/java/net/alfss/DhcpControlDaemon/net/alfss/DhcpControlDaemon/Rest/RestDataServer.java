package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataServer {
    private Long      id;
    private String    name;
    private String    options;
    private boolean   enableDdns;
    private boolean   isAuthoritative;

    public RestDataServer() {

    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("options")
    public String getOptions() {
        return options;
    }

    @JsonProperty("options")
    public void setOptions(String options) {
        this.options = options;
    }

    @JsonProperty("enable_ddns")
    public boolean getEnableDdns() {
        return enableDdns;
    }

    @JsonProperty("enable_ddns")
    public void setEnableDdns(boolean enableDdns) {
        this.enableDdns = enableDdns;
    }

    @JsonProperty("is_authoritative")
    public boolean getAuthoritative() {
        return isAuthoritative;
    }

    @JsonProperty("is_authoritative")
    public void setAuthoritative(boolean authoritative) {
        isAuthoritative = authoritative;
    }
}

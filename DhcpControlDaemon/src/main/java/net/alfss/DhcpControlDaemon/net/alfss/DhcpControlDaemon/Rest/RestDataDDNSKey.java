package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 29.02.12
 * Time: 17:58
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataDDNSKey {
    private Long      id;
    private Long      serverId;
    private String    name;
    private String    algorithm;
    private String    secret;

    public RestDataDDNSKey() {
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("server_id")
    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    @JsonProperty("server_id")
    public Long getServerId() {
        return serverId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("algorithm")
    public String getAlgorithm() {
        return algorithm;
    }

    @JsonProperty("algorithm")
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @JsonProperty("secret")
    public String getSecret() {
        return secret;
    }

    @JsonProperty("secret")
    public void setSecret(String secret) {
        this.secret = secret;
    }
}


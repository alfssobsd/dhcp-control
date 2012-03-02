package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataDDNSZone {
    private Long      id;
    private Long      subnetId;
    private Long      ddnsKeyId;
    private String    name;
    private String    primary;
    private boolean   isReverse;

    public RestDataDDNSZone() {

    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("subnet_id")
    public Long getSubnetId() {
        return subnetId;
    }

    @JsonProperty("subnet_id")
    public void setSubnetId(Long subnetId) {
        this.subnetId = subnetId;
    }

    @JsonProperty("ddns_key_id")
    public Long getDdnsKeyId() {
        return ddnsKeyId;
    }

    @JsonProperty("ddns_key_id")
    public void setDdnsKeyId(Long ddnsKeyId) {
        this.ddnsKeyId = ddnsKeyId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("primary")
    public String getPrimary() {
        return primary;
    }

    @JsonProperty("primary")
    public void setPrimary(String primary) {
        this.primary = primary;
    }

    @JsonProperty("is_reverse")
    public boolean getReverse() {
        return isReverse;
    }

    @JsonProperty("is_reverse")
    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }
}
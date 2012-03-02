package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataRangeIP {
    private Long      id;
    private Long      subnetId;
    private String    ipStart;
    private String    ipEnd;

    public RestDataRangeIP() {

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

    @JsonProperty("ip_start")
    public String getIpStart() {
        return ipStart;
    }

    @JsonProperty("ip_start")
    public void setIpStart(String ipStart) {
        this.ipStart = ipStart;
    }

    @JsonProperty("ip_end")
    public String getIpEnd() {
        return ipEnd;
    }

    @JsonProperty("ip_end")
    public void setIpEnd(String ipEnd) {
        this.ipEnd = ipEnd;
    }
}
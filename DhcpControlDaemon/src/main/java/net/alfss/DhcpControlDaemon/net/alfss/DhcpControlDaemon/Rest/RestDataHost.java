package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataHost {
    private Long      id;
    private Long      serverId;
    private Long      subnetId;
    private Long      groupId;
    private String    name;
    private String    ip;
    private String    mac;

    public RestDataHost() {

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
    public Long getServerId() {
        return serverId;
    }

    @JsonProperty("server_id")
    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    @JsonProperty("subnet_id")
    public Long getSubnetId() {
        return subnetId;
    }

    @JsonProperty("subnet_id")
    public void setSubnetId(Long subnetId) {
        this.subnetId = subnetId;
    }

    @JsonProperty("group_id")
    public Long getGroupId() {
        return groupId;
    }

    @JsonProperty("group_id")
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    @JsonProperty("ip")
    public void setIp(String ip) {
        this.ip = ip;
    }

    @JsonProperty("mac")
    public String getMac() {
        return mac;
    }

    @JsonProperty("mac")
    public void setMac(String mac) {
        this.mac = mac;
    }
}

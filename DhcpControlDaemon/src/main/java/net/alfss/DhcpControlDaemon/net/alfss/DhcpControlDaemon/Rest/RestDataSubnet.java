package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataSubnet {
    private Long      id;
    private Long      serverId;
    private String    net;
    private String    netmask;
    private String    broadcast;
    private String    network;
    private String    options;
    private boolean   enableDdns;
    private boolean   isIpv6;

    public RestDataSubnet() {

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

    @JsonProperty("net")
    public String getNet() {
        return net;
    }

    @JsonProperty("net")
    public void setNet(String net) {
        this.net = net;
    }

    @JsonProperty("netmask")
    public String getNetmask() {
        return netmask;
    }

    @JsonProperty("netmask")
    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    @JsonProperty("broadcast")
    public String getBroadcast() {
        return broadcast;
    }

    @JsonProperty("broadcast")
    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    @JsonProperty("network")
    public String getNetwork() {
        return network;
    }

    @JsonProperty("network")
    public void setNetwork(String network) {
        this.network = network;
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

    @JsonProperty("is_ipv6")
    public boolean getIpv6() {
        return isIpv6;
    }

    @JsonProperty("is_ipv6")
    public void setIpv6(boolean ipv6) {
        isIpv6 = ipv6;
    }
}

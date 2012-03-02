package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestDataGroup {
    private Long       id;
    private Long       subnetId;
    private String    name;
    private String    options;

    public RestDataGroup() {

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
}
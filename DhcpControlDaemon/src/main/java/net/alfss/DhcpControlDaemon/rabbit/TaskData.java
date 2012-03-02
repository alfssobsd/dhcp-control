package net.alfss.DhcpControlDaemon.rabbit;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 02.03.12
 * Time: 1:45
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskData {
    private Long   serverId;
    private Long   subnetId;
    private Long   groupId;
    private Long   hostId;
    private String action;

    public TaskData() {

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

    @JsonProperty("host_id")
    public Long getHostId() {
        return hostId;
    }

    @JsonProperty("host_id")
    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(String action) {
        this.action = action;
    }
}

package net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 29.02.12
 * Time: 23:40
 */
public class RestApi {
    private RestTemplate restTemplate = null;
    private String hostUrl;
    private String apiToken;

    public RestApi(String hostUrl, String apiToken) {
        MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(messageConverter);
        restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);

        this.hostUrl  = hostUrl;
        this.apiToken = apiToken;
    }

    //Server
    public List<RestDataServer> getServerList() {
        String url = "{host}/servers.json?api_token={apiToken}";
        RestDataServer [] servers = restTemplate.getForObject(url, RestDataServer[].class, hostUrl, apiToken);

        return Arrays.asList(servers);
    }

    public RestDataServer getServer(Long serverId) {
        String url = "{host}/servers/{serverId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataServer.class, hostUrl, serverId, apiToken);
    }

    //DDNS key
    public List<RestDataDDNSKey> getDDNSKeyList(Long serverId) {
        String url = "{host}/servers/{serverId}/ddns_keys.json?api_token={apiToken}";

        RestDataDDNSKey [] keys = restTemplate.getForObject(url, RestDataDDNSKey[].class, hostUrl, serverId, apiToken);
        return Arrays.asList(keys);
    }

    public RestDataDDNSKey getDDNSKey(Long serverId, Long keyId) {
        String url = "{host}/servers/{serverId}/ddns_keys/{keyId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataDDNSKey.class, hostUrl, serverId, keyId, apiToken);
    }

    //Subnet
    public List<RestDataSubnet> getSubnetList(Long serverId) {
        String url = "{host}/servers/{serverId}/subnets.json?api_token={apiToken}";

        RestDataSubnet [] subnets = restTemplate.getForObject(url, RestDataSubnet[].class, hostUrl, serverId, apiToken);
        return Arrays.asList(subnets);
    }

    public RestDataSubnet getSubnet(Long serverId, Long subnetId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataSubnet.class, hostUrl, serverId, subnetId, apiToken);
    }

    //DDNS Zone
    public List<RestDataDDNSZone> getDDNSZoneList(Long serverId, Long subnetId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}/ddns_zones.json?api_token={apiToken}";

        RestDataDDNSZone [] zones = restTemplate.getForObject(url, RestDataDDNSZone[].class, hostUrl, serverId, subnetId, apiToken);
        return Arrays.asList(zones);
    }

    public RestDataDDNSZone getDDNSZone(Long serverId, Long subnetId, Long ddnsZoneId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}/ddns_zones/{ddnsZoneId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataDDNSZone.class, hostUrl, serverId, subnetId, ddnsZoneId, apiToken);
    }

    //Range Ip
    public List<RestDataRangeIP> getRangeIPList(Long serverId, Long subnetId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}/range_ips.json?api_token={apiToken}";

        RestDataRangeIP [] rangeIPs = restTemplate.getForObject(url, RestDataRangeIP[].class, hostUrl, serverId, subnetId, apiToken);
        return Arrays.asList(rangeIPs);
    }

    public RestDataRangeIP getRangeIP(Long serverId, Long subnetId, Long rangeId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}/range_ips/{rangeId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataRangeIP.class, hostUrl, serverId, subnetId, rangeId, apiToken);
    }

    //Group
    public List<RestDataGroup> getGroupList(Long serverId, Long subnetId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}/groups.json?api_token={apiToken}";

        RestDataGroup [] groups = restTemplate.getForObject(url, RestDataGroup[].class, hostUrl, serverId, subnetId, apiToken);
        return Arrays.asList(groups);
    }

    public RestDataGroup getGroup(Long serverId, Long subnetId, Long groupId) {
        String url = "{host}/servers/{serverId}/subnets/{subnetId}/groups/{groupId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataGroup.class, hostUrl, serverId, subnetId, groupId, apiToken);
    }

    //Host
    public List<RestDataHost> getHostList(Long serverId, Long subnetId, Long groupId) {
        String url ="{host}/servers/{serverId}/subnets/{subnetId}/groups/{groupId}/hosts.json?api_token={apiToken}";
        RestDataHost [] hosts = restTemplate.getForObject(url, RestDataHost[].class, hostUrl, serverId, subnetId, groupId, apiToken);
        return Arrays.asList(hosts);
    }

    public RestDataHost getHost(Long serverId, Long subnetId, Long groupId, Long hostId) {
        String url ="{host}/servers/{serverId}/subnets/{subnetId}/groups/{groupId}/hosts/{hostId}.json?api_token={apiToken}";
        return restTemplate.getForObject(url, RestDataHost.class, hostUrl, serverId, subnetId, groupId, hostId, apiToken);
    }
}

/*
 * Copyright (c) 2012 Sergey V. Kravchuk <alfss.obsd@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package net.alfss.DhcpControlDaemon;import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import net.sf.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 01.01.12
 * Time: 3:43
 */

public class RestApi extends RestAssured {

    String      api_token;
    RestAssured client;

    public RestApi(String t_url, int t_port, String t_api_token) {
        api_token = t_api_token;

        client         = new RestAssured();
        client.baseURI = t_url;
        client.port    = t_port;
    }

    //Server
    public List<RestDataServer> getServers() throws RestApiError {
        JsonPath             json;
        List<RestDataServer> servers;
        List<JSONObject>     resultList;
        servers    = new ArrayList<RestDataServer>();
        resultList = new ArrayList<JSONObject>();

        try {
            json = client.get(MessageFormat.format("/servers.json?api_token={0}",
                                                    api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataServer t_server = new RestDataServer(temp);
                servers.add(t_server);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get servers:" + e.toString());
        }

        return servers;
    }

    public RestDataServer getServer(int server_id) throws RestApiError  {
        JsonPath       json;
        RestDataServer server;

        try {
            json = client.get(MessageFormat.format("/servers/{0}.json?api_token={1}",
                                                      server_id,
                                                      api_token)).jsonPath();
            server = new RestDataServer((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error get server id=" + server_id + ":" + e.toString());
        }

        return server;
    }

    //DDNS key
    public List<RestDataDDNSKey> getDDNSKeys(int server_id) throws RestApiError {
        JsonPath              json;
        List<RestDataDDNSKey> keys;
        List<JSONObject>      resultList;
        keys       = new ArrayList<RestDataDDNSKey>();
        resultList = new ArrayList<JSONObject>();

        try {
            json = client.get(MessageFormat.format("/servers/{0}/ddns_keys.json?api_token={1}",
                                                    server_id,
                                                    api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataDDNSKey t_key = new RestDataDDNSKey(temp);
                keys.add(t_key);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get ddns keys:" + e.toString());
        }

        return keys;
    }

    public RestDataDDNSKey getDDNSKey(int server_id, int key_id) throws RestApiError {
        JsonPath        json;
        RestDataDDNSKey key;
        
        try {
            json = client.get(MessageFormat.format("/servers/{0}/ddns_keys/{1}.json?api_token={2}",
                                                   server_id,
                                                   key_id,
                                                   api_token)).jsonPath();
            key  = new RestDataDDNSKey((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error get ddns key:" + e.toString());
        }

        return key;
    }


    //Subnet
    public List<RestDataSubnet> getSubnets(int server_id) throws RestApiError {
        JsonPath              json;
        List<RestDataSubnet>  subnets;
        List<JSONObject>      resultList;
        subnets    = new ArrayList<RestDataSubnet>();
        resultList = new ArrayList<JSONObject>();

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets.json?api_token={1}",
                                                    server_id,
                                                    api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataSubnet t_net = new RestDataSubnet(temp);
                subnets.add(t_net);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get subnets:" + e.toString());
        }

        return subnets;
    }

    public RestDataSubnet getSubnet(int server_id, int subnet_id) throws RestApiError {
        JsonPath       json;
        RestDataSubnet subnet;

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}.json?api_token={2}",
                                                      server_id,
                                                      subnet_id,
                                                      api_token)).jsonPath();
            subnet = new RestDataSubnet((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error get subnet:"+ e.toString());
        }

        return subnet;
    }

    //DDNS Zone
    public List<RestDataDDNSZone> getDDNSZones(int server_id, int subnet_id) throws RestApiError {
        JsonPath               json;
        List<RestDataDDNSZone> zones;
        List<JSONObject>       resultList;
        zones      = new ArrayList<RestDataDDNSZone>();
        resultList = new ArrayList<JSONObject>();

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/ddns_zones.json?api_token={2}",
                                                    server_id,
                                                    subnet_id,
                                                    api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataDDNSZone t_zone = new RestDataDDNSZone(temp);
                zones.add(t_zone);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get ddns zones:" + e.toString());
        }

        return zones;
    }

    public RestDataDDNSZone getDDNSZone(int server_id, int subnet_id, int ddns_zone_id) throws RestApiError {
        JsonPath         json;
        RestDataDDNSZone zone;

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/ddns_zones/{2}.json?api_token={3}",
                                                    server_id,
                                                    subnet_id,
                                                    ddns_zone_id,
                                                    api_token)).jsonPath();
            zone = new RestDataDDNSZone((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error ddns zone");
        }

        return zone;
    }

    //Range IP
    public List<RestDataRangeIP> getRangesIP(int server_id, int subnet_id) throws RestApiError {
        JsonPath               json;
        List<RestDataRangeIP>  ranges;
        List<JSONObject>       resultList;
        ranges      = new ArrayList<RestDataRangeIP>();
        resultList  = new ArrayList<JSONObject>();

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/range_ips.json?api_token={2}",
                                                    server_id,
                                                    subnet_id,
                                                    api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataRangeIP t_range = new RestDataRangeIP(temp);
                ranges.add(t_range);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get ranges ip:" + e.toString());
        }

        return ranges;
    }

    public RestDataRangeIP getRangeIP(int server_id, int subnet_id, int range_id) throws RestApiError {
        JsonPath        json;
        RestDataRangeIP range;

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/range_ips/{2}.json?api_token={3}",
                                                     server_id,
                                                     subnet_id,
                                                     range_id,
                                                     api_token)).jsonPath();
            range = new RestDataRangeIP((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error range ip:" + e.toString());
        }

        return range;
    }

    //Group
    public List<RestDataGroup> getGroups(int server_id, int subnet_id) throws RestApiError {
        JsonPath               json;
        List<RestDataGroup>    groups;
        List<JSONObject>       resultList;
        groups      = new ArrayList<RestDataGroup>();
        resultList  = new ArrayList<JSONObject>();

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/groups.json?api_token={2}",
                                                    server_id,
                                                    subnet_id,
                                                    api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataGroup t_group = new RestDataGroup(temp);
                groups.add(t_group);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get groups");
        }

        return groups;
    }

    public RestDataGroup getGroup(int server_id, int subnet_id, int group_id) throws RestApiError {
        JsonPath      json;
        RestDataGroup group;

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/groups/{2}.json?api_token={3}",
                                                     server_id,
                                                     subnet_id,
                                                     group_id,
                                                     api_token)).jsonPath();
            group = new RestDataGroup((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error group");
        }

        return group;
    }

    //Host
    public List<RestDataHost> getHosts(int server_id, int subnet_id, int group_id) throws RestApiError {
        JsonPath               json;
        List<RestDataHost>     hosts;
        List<JSONObject>       resultList;
        hosts       = new ArrayList<RestDataHost>();
        resultList  = new ArrayList<JSONObject>();

        try {
            json  = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/groups/{2}/hosts.json?api_token={3}",
                                                     server_id,
                                                     subnet_id,
                                                     group_id,
                                                     api_token)).jsonPath();
            resultList = json.get();
            for (JSONObject temp : resultList){
                RestDataHost t_host = new RestDataHost(temp);
                hosts.add(t_host);
            }
        } catch (Exception e) {
            throw new RestApiError("Error get hosts");
        }

        return hosts;
    }

    public RestDataHost getHost(int server_id, int subnet_id, int group_id, int host_id) throws RestApiError {
        JsonPath     json;
        RestDataHost host;

        try {
            json = client.get(MessageFormat.format("/servers/{0}/subnets/{1}/groups/{2}/hosts/{3}.json?api_token={4}",
                                                    server_id,
                                                    subnet_id,
                                                    group_id,
                                                    host_id,
                                                    api_token)).jsonPath();
            host = new RestDataHost((JSONObject) json.get());
        } catch (Exception e){
            throw new RestApiError("Error host");
        }

        return host;
    }

}

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

package net.alfss.DhcpControlDaemon;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 01.01.12
 * Time: 3:43
 */

public class RestApi {

    String         apiToken;
    String         baseUrl;
    ClientResource client;

    public RestApi(String t_url, String t_api_token) {
        apiToken = t_api_token;
        baseUrl  = t_url;
        client = new ClientResource(baseUrl);
    }

    //Server
    public List<RestDataServer> getServers() throws RestApiError {
        List<RestDataServer>  servers;
        servers               = new ArrayList<RestDataServer>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers.json?api_token={0}", apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONArray resultList          = jr.getJsonArray();
            for (int i = 0; i < resultList.length(); i++) {
                servers.add( new RestDataServer(resultList.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RestApiError("Error get servers:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get servers:" + e.toString());
        }

        return servers;
    }

    public RestDataServer getServer(int server_id) throws RestApiError  {
        RestDataServer server;

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}.json?api_token={1}", server_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            server = new RestDataServer(json);
        } catch (IOException e) {
            throw new RestApiError("Error get server id=" + server_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get server id=" + server_id + ":" + e.toString());
        }

        return server;
    }

    //DDNS key
    public List<RestDataDDNSKey> getDDNSKeys(int server_id) throws RestApiError {
        List<RestDataDDNSKey> keys;
        keys                  = new ArrayList<RestDataDDNSKey>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/ddns_keys.json?api_token={1}", server_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONArray resultList          = jr.getJsonArray();
            for (int i = 0; i < resultList.length(); i++) {
                keys.add(new RestDataDDNSKey(resultList.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RestApiError("Error get ddns keys:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get ddns keys:" + e.toString());
        }

        return keys;
    }

    public RestDataDDNSKey getDDNSKey(int server_id, int key_id) throws RestApiError {
        RestDataDDNSKey key;

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/ddns_keys/{1}.json?api_token={2}", server_id, key_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            key = new RestDataDDNSKey(json);
        } catch (IOException e) {
            throw new RestApiError("Error get key_id id=" + key_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get key_id id=" + key_id + ":" + e.toString());
        }

        return key;
    }


    //Subnet
    public List<RestDataSubnet> getSubnets(int server_id) throws RestApiError {
        List<RestDataSubnet>  subnets;
        subnets               = new ArrayList<RestDataSubnet>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets.json?api_token={1}", server_id, apiToken)).get(MediaType.APPLICATION_JSON);
            if (client.getStatus().isSuccess()) {
                JsonRepresentation jr         = new JsonRepresentation(representation);
                JSONArray resultList          = jr.getJsonArray();
                for (int i = 0; i < resultList.length(); i++) {
                    subnets.add(new RestDataSubnet(resultList.getJSONObject(i)));
                }
            }
        } catch (ResourceException e) {
            throw new RestApiError("Error get subnets:" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get subnets:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get subnets:" + e.toString());
        }

        return subnets;
    }

    public RestDataSubnet getSubnet(int server_id, int subnet_id) throws RestApiError {
        RestDataSubnet subnet;

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}.json?api_token={2}", server_id, subnet_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            subnet = new RestDataSubnet(json);
        } catch (ResourceException e) {
            throw new RestApiError("Error get subnet id=" + subnet_id + ":" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get subnet id=" + subnet_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get subnet id=" + subnet_id + ":" + e.toString());
        }

        return subnet;
    }

    //DDNS Zone
    public List<RestDataDDNSZone> getDDNSZones(int server_id, int subnet_id) throws RestApiError {
        List<RestDataDDNSZone> zones;
        zones                  = new ArrayList<RestDataDDNSZone>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/ddns_zones.json?api_token={2}", server_id, subnet_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONArray resultList          = jr.getJsonArray();
            for (int i = 0; i < resultList.length(); i++) {
                zones.add(new RestDataDDNSZone(resultList.getJSONObject(i)));
            }
        } catch (ResourceException e) {
            throw new RestApiError("Error get ddns zones:" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get ddns zones:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get ddns zones:" + e.toString());
        }

        return zones;
    }

    public RestDataDDNSZone getDDNSZone(int server_id, int subnet_id, int ddns_zone_id) throws RestApiError {
        RestDataDDNSZone zone;

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/ddns_zones/{2}.json?api_token={3}", server_id, subnet_id, ddns_zone_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            zone = new RestDataDDNSZone(json);

        } catch (ResourceException e) {
            throw new RestApiError("Error get ddns zone id=" + ddns_zone_id + ":" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get ddns zone id=" + ddns_zone_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get ddns zone id=" + ddns_zone_id + ":" + e.toString());
        }

        return zone;
    }

    //Range IP
    public List<RestDataRangeIP> getRangesIP(int server_id, int subnet_id) throws RestApiError {
        List<RestDataRangeIP>  ranges;
        ranges      = new ArrayList<RestDataRangeIP>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/range_ips.json?api_token={2}", server_id, subnet_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONArray resultList          = jr.getJsonArray();
            for (int i = 0; i < resultList.length(); i++) {
                ranges.add(new RestDataRangeIP(resultList.getJSONObject(i)));
            }

        } catch (ResourceException e) {
            throw new RestApiError("Error get ranges ip:" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get ranges ip:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get ranges ip:" + e.toString());
        }

        return ranges;
    }

    public RestDataRangeIP getRangeIP(int server_id, int subnet_id, int range_id) throws RestApiError {
        RestDataRangeIP range;

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/range_ips/{2}.json?api_token={3}", server_id, subnet_id, range_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            range = new RestDataRangeIP(json);
        } catch (ResourceException e) {
            throw new RestApiError("Error get range ip id=" + range_id + ":" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get range ip id=" + range_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get range ip id=" + range_id+ ":" + e.toString());
        }

        return range;
    }

    //Group
    public List<RestDataGroup> getGroups(int server_id, int subnet_id) throws RestApiError {
        List<RestDataGroup>   groups;
        groups                = new ArrayList<RestDataGroup>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/groups.json?api_token={2}", server_id, subnet_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONArray resultList          = jr.getJsonArray();
            for (int i = 0; i < resultList.length(); i++) {
                groups.add(new RestDataGroup(resultList.getJSONObject(i)));
            }
        } catch (ResourceException e) {
            throw new RestApiError("Error get groups:" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get groups:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get groups:" + e.toString());
        }

        return groups;
    }

    public RestDataGroup getGroup(int server_id, int subnet_id, int group_id) throws RestApiError {
        RestDataGroup group;
        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/groups/{2}.json?api_token={3}", server_id, subnet_id, group_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            group = new RestDataGroup(json);
        } catch (ResourceException e) {
            throw new RestApiError("Error get group id=" + group_id + ":" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get group id=" + group_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get group id=" + group_id + ":" + e.toString());
        }

        return group;
    }

    //Host
    public List<RestDataHost> getHosts(int server_id, int subnet_id, int group_id) throws RestApiError {
        List<RestDataHost>    hosts;
        hosts                 = new ArrayList<RestDataHost>();

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/groups/{2}/hosts.json?api_token={3}", server_id, subnet_id, group_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONArray resultList          = jr.getJsonArray();
            for (int i = 0; i < resultList.length(); i++) {
                hosts.add(new RestDataHost(resultList.getJSONObject(i)));
            }
        } catch (ResourceException e) {
            throw new RestApiError("Error get hosts:" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get hosts:" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get hosts:" + e.toString());
        }

        return hosts;
    }

    public RestDataHost getHost(int server_id, int subnet_id, int group_id, int host_id) throws RestApiError {
        RestDataHost    host;

        try {
            Representation representation = client.getChild(MessageFormat.format("/servers/{0}/subnets/{1}/groups/{2}/hosts/{3}.json?api_token={4}", server_id, subnet_id, group_id, host_id, apiToken)).get(MediaType.APPLICATION_JSON);
            JsonRepresentation jr         = new JsonRepresentation(representation);
            JSONObject         json       = jr.getJsonObject();

            host = new RestDataHost(json);
        } catch (ResourceException e) {
            throw new RestApiError("Error get host id=" + host_id + ":" + e.toString());
        } catch (IOException e) {
            throw new RestApiError("Error get host id=" + host_id + ":" + e.toString());
        } catch (JSONException e) {
            throw new RestApiError("Error get host id=" + host_id + ":" + e.toString());
        }

        return host;
    }

}

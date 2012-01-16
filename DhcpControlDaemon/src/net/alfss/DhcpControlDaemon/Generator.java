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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 01.01.12
 * Time: 3:43
 */

public class Generator {

    private String config_path;
    private RestApi rest;
    private RestDataServer server = null;
    private int server_id;
    final Logger logger = (Logger) LoggerFactory.getLogger("net/alfss/DhcpControlDaemon");

    public Generator(String config_path, String url, String api_token, int server_id) {
        this.config_path = config_path;
        this.rest        = new RestApi(url, api_token);
        this.server_id   = server_id;
        try {
            this.server = rest.getServer(server_id);
        } catch (RestApiError e) {
            logger.error(MessageFormat.format("Error get server: {0}", e.toString()));
        }

        this.createDir(config_path + "/tmp");
    }

    public void genServer() {
        logger.warn(MessageFormat.format("Generate server id:{0}", server_id));
        try {
            this.server = rest.getServer(server_id);

            logger.debug(MessageFormat.format("server: name:{0}, ddns:{1}, authoritative:{2}",
                    server.name, server.enable_ddns, server.is_authoritative));
            logger.debug(server.options);



            File     temp_config  = new File(config_path + "/tmp/dhcpd.conf");
            FileWriter fstream    = new FileWriter(temp_config);
            BufferedWriter config = new BufferedWriter(fstream);

            if (server.is_authoritative) {
                config.write("authoritative;\n");
            } else {
                config.write("not authoritative;\n");
            }

            config.write("\n");
            config.write(server.options + "\n");
            config.write("\n");

            if (server.enable_ddns) {
                config.write("ddns-update-style interim;\n");
                for(RestDataDDNSKey t_key: rest.getDDNSKeys(server_id)){
                    config.write(MessageFormat.format("key {0} '{\n", t_key.name));
                    config.write(MessageFormat.format("    algorithm {0};\n", t_key.algorithm));
                    config.write(MessageFormat.format("    secret    \"{0}\";\n", t_key.secret));
                    config.write("};\n\n");
                }
            }

            for(RestDataSubnet t_subnet: rest.getSubnets(server_id)){
                config.write(MessageFormat.format("include \"{0}/include/subnet{1}.conf\";\n", config_path, t_subnet.id));
            }

            config.close();

            FileUtils.copyFile(temp_config, new File(config_path + "/dhcpd.conf"));
            temp_config.delete();


        } catch (IOException e) {
                logger.error(MessageFormat.format("genServer: {0}", e.toString()));
        } catch (RestApiError e) {
                logger.error(MessageFormat.format("genServer: {0}", e.toString()));
        }

        this.createDir(config_path + "/include");
    }


    public void removeSubnet(int subnet_id) {
        logger.warn(MessageFormat.format("Remove {0}/include/subnet{1}.conf", config_path, subnet_id));
        logger.warn(MessageFormat.format("Remove {0}/include/subnet{1}", config_path, subnet_id));
        File  dir = new File(MessageFormat.format("{0}/include/subnet{1}", config_path, subnet_id));
        File  file = new File(MessageFormat.format("{0}/include/subnet{1}.conf", config_path, subnet_id));
        try {
            FileUtils.forceDelete(dir);
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void genSubnet(RestDataSubnet subnet, boolean required_get) {
        logger.warn(MessageFormat.format("Generate subnet id:{0}", subnet.id));
        try {
            if (required_get) {
                    if(server == null){
                        this.server = rest.getServer(this.server_id);
                    }
                    subnet = rest.getSubnet(server.id, subnet.id);
            }
            logger.debug(MessageFormat.format("subnet: net:{0}, ddns:{1}, ipv6:{2}",
                    subnet.net, subnet.enable_ddns, subnet.is_ipv6));
            logger.debug(subnet.options);


            File       temp_config       = new File(MessageFormat.format("{0}/tmp/subnet{1}.conf", config_path, subnet.id));
            FileWriter fstream           = new FileWriter(temp_config);
            BufferedWriter config        = new BufferedWriter(fstream);
            List<RestDataRangeIP> ranges = rest.getRangesIP(subnet.server_id, subnet.id);
            List<RestDataDDNSZone> zones = rest.getDDNSZones(subnet.server_id, subnet.id);
            List<RestDataGroup>   groups = rest.getGroups(subnet.server_id, subnet.id);

            config.write(MessageFormat.format("subnet {0} netmask {1} '{\n", subnet.network, subnet.netmask));
            
            for(RestDataRangeIP range: ranges){
                config.write(MessageFormat.format("  range {0} {1};\n", range.ip_start, range.ip_end));
            }

            config.write(MessageFormat.format("  option subnet-mask       {0};\n", subnet.netmask));
            config.write(MessageFormat.format("  option broadcast-address {0};\n", subnet.broadcast));
            config.write("\n");
            config.write(MessageFormat.format("{0}\n", subnet.options));
            config.write("\n");

            if (server.enable_ddns) {
                config.write("  ddns-updates on;\n");
                for(RestDataDDNSZone zone: zones) {
                    RestDataDDNSKey key = rest.getDDNSKey(subnet.server_id, zone.ddns_key_id);
                    if(key != null) {
                        if(zone.is_reverse) {
                            config.write("  ddns-rev-domainname \"in-addr.arpa\";\n");
                            config.write(MessageFormat.format("  zone {0}.in-addr.arpa. ", zone.name));
                            config.write(MessageFormat.format("'{ primary {0}; key {1}; '}\n", zone.primary, key.name));
                        } else {
                            config.write(MessageFormat.format("  ddns-domainname \"{0}\";\n", zone.name));
                            config.write(MessageFormat.format("  zone {0}. ", zone.name));
                            config.write(MessageFormat.format("'{ primary {0}; key {1}; '}\n", zone.primary, key.name));
                        }
                    }
                }
            }
            config.write("}\n\n");

            for(RestDataGroup group: groups){
                config.write(MessageFormat.format("include \"{0}/include/subnet{1}/group{2}.conf\";\n", config_path, subnet.id, group.id));
            }
            config.write("\n");
            config.close();

            this.createDir(MessageFormat.format("{0}/include/subnet{1}", config_path, subnet.id));

            FileUtils.copyFile(temp_config, new File(config_path + "/include/subnet" + subnet.id + ".conf"));
            temp_config.delete();

        } catch (IOException e) {
            logger.error(MessageFormat.format("genSubnet: {0}", e.toString()));
        } catch (RestApiError e){
            logger.error(MessageFormat.format("genSubnet: {0}", e.toString()));
        }

    }

    public List<RestDataSubnet> genSubnets() {
        List<RestDataSubnet> subnets;
        subnets = new ArrayList<RestDataSubnet>();

        try {
            subnets = rest.getSubnets(server.id);
            for(RestDataSubnet t_subnet: subnets){
                this.genSubnet(t_subnet, false);
            }
        } catch (RestApiError e) {
            logger.error(MessageFormat.format("genSubnets: {0}", e.toString()));
        }
        
        return subnets;
    }

    public void removeGroup(int subnet_id, int group_id) {
        logger.warn(MessageFormat.format("Remove {0}/include/subnet{1}/group{2}.conf", config_path, subnet_id, group_id));
        File  dir = new File(MessageFormat.format("{0}/include/subnet{1}/group{2}.conf", config_path, subnet_id, group_id));
        try {
            FileUtils.forceDelete(dir);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void genGroup(RestDataGroup group, RestDataSubnet subnet, boolean required_get) {
        logger.warn(MessageFormat.format("Generate group id:{0}", group.id));
        try {
            if (required_get) {
                if(server == null) {
                    this.server = rest.getServer(this.server_id);
                }
                group  = rest.getGroup(server.id, group.subnet_id, group.id);
                subnet = rest.getSubnet(server.id, group.subnet_id);
            }

            logger.debug(MessageFormat.format("group: {0}", group.name));
            logger.debug(group.options);

            File temp_config         = new File(MessageFormat.format("{0}/tmp/group{1}.conf", config_path, group.id));
            FileWriter fstream       = new FileWriter(temp_config);
            BufferedWriter config    = new BufferedWriter(fstream);
            List<RestDataHost> hosts = rest.getHosts(server.id, group.subnet_id, group.id);

            if (hosts.size() > 0) {
                config.write("group {\n");
                config.write(MessageFormat.format("{0}\n\n", group.options));

                for(RestDataHost host: hosts) {
                    config.write(MessageFormat.format("  host {0} '{\n", host.name));
                    if(server.enable_ddns && subnet.enable_ddns){
                        config.write(MessageFormat.format("    ddns-hostname     \"{0}\";\n", host.name));
                    } else {
                        config.write(MessageFormat.format("    option host-name  \"{0}\";\n", host.name));
                    }
                    config.write(MessageFormat.format("    fixed-address     {0};\n", host.ip));
                    config.write(MessageFormat.format("    hardware ethernet {0};\n", host.mac));
                    config.write("  }\n\n");
                }

                config.write("}\n\n");
            }
            config.close();

            FileUtils.copyFile(temp_config, new File(config_path + "/include/subnet" + subnet.id + "/group" + group.id + ".conf"));
            temp_config.delete();


        } catch (IOException e) {
            logger.error(MessageFormat.format("genGroup: {0}", e.toString()));
        } catch (RestApiError e){
            logger.error(MessageFormat.format("genGroup: {0}", e.toString()));
        }
    }

    public List<RestDataGroup> genGroups(RestDataSubnet subnet, boolean required_get) {
        List<RestDataGroup> groups;
        groups = new ArrayList<RestDataGroup>();

        try {
            if (required_get) {
                subnet = rest.getSubnet(server.id, subnet.id);
            }

            groups = rest.getGroups(server.id, subnet.id);

            if(subnet != null) {
                for(RestDataGroup t_group: groups){
                    this.genGroup(t_group, subnet, false);
                }
            }
        } catch (RestApiError e) {
            logger.error(MessageFormat.format("genGroup: {0}", e.toString()));
        }

        return groups;
    }


    public void genAll(){
        this.genServer();
        List<RestDataSubnet> subnets = this.genSubnets();
        for(RestDataSubnet subnet: subnets){
            this.genGroups(subnet, false);
        }
    }

    private void createDir(String dir) {
        File t_dir = new File(dir);
        if(!t_dir.exists() && !t_dir.mkdirs()){
            logger.error(MessageFormat.format("Error create dir: {0}", t_dir.toString()));
        }
    }

}

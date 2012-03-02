package net.alfss.DhcpControlDaemon.dhcp;

import net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class Generator {

    private String configPath;
    private RestApi rest;
    private RestDataServer server = null;
    private Long serverId;
    final Logger logger = (Logger) LoggerFactory.getLogger("net/alfss/DhcpControlDaemon");

    public Generator(String configPath, String hostUrl, String apiToken, Long serverId) {
        this.configPath = configPath;
        this.rest        = new RestApi(hostUrl, apiToken);
        this.serverId    = serverId;

        try {
            this.server      = rest.getServer(serverId);
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error Connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }

        this.createDir(configPath + "/tmp");
    }

    public void genServer() {
        logger.warn(MessageFormat.format("Generate server id:{0}", serverId));
        try {
            this.server = rest.getServer(serverId);

            logger.debug(MessageFormat.format("server: name:{0}, ddns:{1}, authoritative:{2}", server.getName(), server.getEnableDdns(), server.getAuthoritative()));
            logger.debug(server.getOptions());

            File tempConfig       = new File(configPath + "/tmp/dhcpd.conf");
            FileWriter fstream    = new FileWriter(tempConfig);
            BufferedWriter config = new BufferedWriter(fstream);

            if (server.getAuthoritative()) {
                config.write("authoritative;\n");
            } else {
                config.write("not authoritative;\n");
            }

            config.write("\n");
            config.write(server.getOptions() + "\n");
            config.write("\n");

            if (server.getEnableDdns()) {
                config.write("ddns-update-style interim;\n");
                for(RestDataDDNSKey tmpKey: rest.getDDNSKeyList(serverId)){
                    config.write(MessageFormat.format("key {0} '{\n", tmpKey.getName()));
                    config.write(MessageFormat.format("    algorithm {0};\n", tmpKey.getAlgorithm()));
                    config.write(MessageFormat.format("    secret    \"{0}\";\n", tmpKey.getSecret()));
                    config.write("};\n\n");
                }
            }

            for(RestDataSubnet tmpSubnet: rest.getSubnetList(serverId)){
                config.write(MessageFormat.format("include \"{0}/include/subnet{1}.conf\";\n", configPath, tmpSubnet.getId()));
            }

            config.close();

            FileUtils.copyFile(tempConfig, new File(configPath + "/dhcpd.conf"));
            tempConfig.delete();


        } catch (IOException e) {
            logger.error(MessageFormat.format("genServer: {0}", e.toString()));
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error Connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }

        this.createDir(configPath + "/include");
    }


    public void removeSubnet(Long subnetId) {
        logger.warn(MessageFormat.format("Remove {0}/include/subnet{1}.conf", configPath, subnetId));
        logger.warn(MessageFormat.format("Remove {0}/include/subnet{1}", configPath, subnetId));
        File  dir = new File(MessageFormat.format("{0}/include/subnet{1}", configPath, subnetId));
        File  file = new File(MessageFormat.format("{0}/include/subnet{1}.conf", configPath, subnetId));
        try {
            FileUtils.forceDelete(dir);
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void genSubnet(RestDataSubnet subnet, boolean requiredGet) {
        logger.warn(MessageFormat.format("Generate subnet id:{0}", subnet.getId()));
        try {
            if (requiredGet) {
                if(server == null){
                    this.server = rest.getServer(this.serverId);
                }
                subnet = rest.getSubnet(server.getId(), subnet.getId());
            }
            logger.debug(MessageFormat.format("subnet: net:{0}, ddns:{1}, ipv6:{2}", subnet.getNet(), subnet.getEnableDdns(), subnet.getIpv6()));
            logger.debug(subnet.getOptions());


            File       tempConfig        = new File(MessageFormat.format("{0}/tmp/subnet{1}.conf", configPath, subnet.getId()));
            FileWriter fstream           = new FileWriter(tempConfig);
            BufferedWriter config        = new BufferedWriter(fstream);
            List<RestDataRangeIP> rangeList = rest.getRangeIPList(subnet.getServerId(), subnet.getId());
            List<RestDataDDNSZone> zoneList = rest.getDDNSZoneList(subnet.getServerId(), subnet.getId());
            List<RestDataGroup>   groupList = rest.getGroupList(subnet.getServerId(), subnet.getId());

            config.write(MessageFormat.format("subnet {0} netmask {1} '{\n", subnet.getNetwork(), subnet.getNetmask()));

            for(RestDataRangeIP range: rangeList){
                config.write(MessageFormat.format("  range {0} {1};\n", range.getIpStart(), range.getIpEnd()));
            }

            config.write(MessageFormat.format("  option subnet-mask       {0};\n", subnet.getNetmask()));
            config.write(MessageFormat.format("  option broadcast-address {0};\n", subnet.getBroadcast()));
            config.write("\n");
            config.write(MessageFormat.format("{0}\n", subnet.getOptions()));
            config.write("\n");

            if (server.getEnableDdns()) {
                config.write("  ddns-updates on;\n");
                for(RestDataDDNSZone zone: zoneList) {
                    RestDataDDNSKey key = rest.getDDNSKey(subnet.getServerId(), zone.getDdnsKeyId());
                    if(key != null) {
                        if(zone.getReverse()) {
                            config.write("  ddns-rev-domainname \"in-addr.arpa\";\n");
                            config.write(MessageFormat.format("  zone {0}.in-addr.arpa. ", zone.getName()));
                            config.write(MessageFormat.format("'{ primary {0}; key {1}; '}\n", zone.getPrimary(), key.getName()));
                        } else {
                            config.write(MessageFormat.format("  ddns-domainname \"{0}\";\n", zone.getName()));
                            config.write(MessageFormat.format("  zone {0}. ", zone.getName()));
                            config.write(MessageFormat.format("'{ primary {0}; key {1}; '}\n", zone.getPrimary(), key.getName()));
                        }
                    }
                }
            }
            config.write("}\n\n");

            for(RestDataGroup group: groupList){
                config.write(MessageFormat.format("include \"{0}/include/subnet{1}/group{2}.conf\";\n", configPath, subnet.getId(), group.getId()));
            }
            config.write("\n");
            config.close();

            this.createDir(MessageFormat.format("{0}/include/subnet{1}", configPath, subnet.getId()));

            FileUtils.copyFile(tempConfig, new File(configPath + "/include/subnet" + subnet.getId() + ".conf"));
            tempConfig.delete();

        } catch (IOException e) {
            logger.error(MessageFormat.format("genSubnet: {0}", e.toString()));
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }

    }

    public List<RestDataSubnet> genSubnets() {
        List<RestDataSubnet> subnetList = new ArrayList<RestDataSubnet>();
        try {
            if(server == null){
                this.server = rest.getServer(this.serverId);
            }
            subnetList = rest.getSubnetList(server.getId());
            for(RestDataSubnet subnet: subnetList){
                this.genSubnet(subnet, false);
            }
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }

        return subnetList;
    }

    public void removeGroup(Long subnetId, Long groupId) {
        logger.warn(MessageFormat.format("Remove {0}/include/subnet{1}/group{2}.conf", configPath, subnetId, groupId));
        File  file = new File(MessageFormat.format("{0}/include/subnet{1}/group{2}.conf", configPath, subnetId, groupId));
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void genGroup(RestDataGroup group, RestDataSubnet subnet, boolean requiredGet) {
        logger.warn(MessageFormat.format("Generate group id:{0}", group.getId()));
        try {
            if(server == null) {
                this.server = rest.getServer(this.serverId);
            }
            if (requiredGet) {
                group  = rest.getGroup(server.getId(), group.getSubnetId(), group.getId());
                subnet = rest.getSubnet(server.getId(), group.getSubnetId());
            }

            logger.debug(MessageFormat.format("group: {0}", group.getName()));
            logger.debug(group.getOptions());

            File tempConfig         = new File(MessageFormat.format("{0}/tmp/group{1}.conf", configPath, group.getId()));
            FileWriter fstream       = new FileWriter(tempConfig);
            BufferedWriter config    = new BufferedWriter(fstream);
            List<RestDataHost> hostList = rest.getHostList(server.getId(), group.getSubnetId(), group.getId());

            if (hostList.size() > 0) {
                config.write("group {\n");
                config.write(MessageFormat.format("{0}\n\n", group.getOptions()));

                for(RestDataHost host: hostList) {
                    config.write(MessageFormat.format("  host {0} '{\n", host.getName()));
                    if(server.getEnableDdns() && subnet.getEnableDdns()){
                        config.write(MessageFormat.format("    ddns-hostname     \"{0}\";\n", host.getName()));
                    } else {
                        config.write(MessageFormat.format("    option host-name  \"{0}\";\n", host.getName()));
                    }
                    config.write(MessageFormat.format("    fixed-address     {0};\n", host.getIp()));
                    config.write(MessageFormat.format("    hardware ethernet {0};\n", host.getMac()));
                    config.write("  }\n\n");
                }

                config.write("}\n\n");
            }
            config.close();

            FileUtils.copyFile(tempConfig, new File(configPath + "/include/subnet" + subnet.getId() + "/group" + group.getId() + ".conf"));
            tempConfig.delete();


        } catch (IOException e) {
            logger.error(MessageFormat.format("genGroup: {0}", e.toString()));
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }
    }

    public List<RestDataGroup> genGroups(RestDataSubnet subnet, boolean requiredGet) {
        List<RestDataGroup> groupList = new ArrayList<RestDataGroup>();
        try {
            if (requiredGet) {
                subnet = rest.getSubnet(server.getId(), subnet.getId());
            }

            groupList = rest.getGroupList(server.getId(), subnet.getId());

            if(subnet != null) {
                for(RestDataGroup group: groupList){
                    this.genGroup(group, subnet, false);
                }
            }
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }

        return groupList;
    }


    public void genAll(){
        try {
            this.genServer();
            List<RestDataSubnet> subnetList = this.genSubnets();
            for(RestDataSubnet subnet: subnetList){
                this.genGroups(subnet, false);
            }
        } catch (HttpClientErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        } catch (ResourceAccessException e) {
            //error connect
            logger.error("Generator(error connect): " + e.getMessage());
        } catch (HttpServerErrorException e) {
            //error HTTP
            logger.error("Generator(error HTTP): " + e.getMessage());
        }
    }

    private void createDir(String path) {
        File dir = new File(path);
        if(!dir.exists() && !dir.mkdirs()){
            logger.error(MessageFormat.format("Error create dir: {0}", dir.toString()));
        }
    }

}

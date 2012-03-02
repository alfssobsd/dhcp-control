package net.alfss.DhcpControlDaemon;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 29.02.12
 * Time: 4:05
 */

import org.apache.commons.configuration.XMLConfiguration;

public class Config {
    public String rabbit_host;
    public int    rabbit_port;
    public String rabbit_vhost;
    public String rabbit_user;
    public String rabbit_password;
    public String rest_api_url;
    public int    rest_api_port;
    public String rest_api_token;
    public long   reat_api_server_id;
    public String dhcp_dir;
    public String dhcp_restart_cmd;


    public Config(XMLConfiguration xml_config) {
        this.dhcp_dir           = xml_config.getString("dhcp_dir", "/etc/dhcpd");
        this.dhcp_restart_cmd   = xml_config.getString("dhcp_restart_cmd", "/etc/init.d/dhcpd restart");
        this.rabbit_host        = xml_config.getString("rabbit_host", "localhost");
        this.rabbit_port        = xml_config.getInt("rabbit_port", 5672);
        this.rabbit_vhost       = xml_config.getString("rabbit_vhost", "/dhcp_control");
        this.rabbit_user        = xml_config.getString("rabbit_user", "guest");
        this.rabbit_password    = xml_config.getString("rabbit_password", "guest");
        this.rest_api_url       = xml_config.getString("rest_api_url", "http://localhost");
        this.rest_api_port      = xml_config.getInt("rest_api_port", 3001);
        this.rest_api_token     = xml_config.getString("rest_api_token", "string_token");
        this.reat_api_server_id = xml_config.getLong("reat_api_server_id", 0);
    }
}

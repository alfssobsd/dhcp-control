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

import org.apache.commons.configuration.XMLConfiguration;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 09.01.12
 * Time: 7:02
 */
public class Config {
    public String rabbit_host;
    public int    rabbit_port;
    public String rabbit_vhost;
    public String rabbit_user;
    public String rabbit_password;
    public String rest_api_url;
    public int    rest_api_port;
    public String rest_api_token;
    public int    reat_api_server_id;
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
        this.reat_api_server_id = xml_config.getInt("reat_api_server_id", 0);
    }
}

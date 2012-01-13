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

package net.alfss.DhcpControlDaemon;import net.sf.json.JSONObject;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 03.01.12
 * Time: 3:23
 */
public class RestDataSubnet {
    public int       id;
    public int       server_id;
    public String    net;
    public String    netmask;
    public String    broadcast;
    public String    network;
    public String    options;
    public Boolean   enable_ddns;
    public Boolean   is_ipv6;

    public RestDataSubnet() {

    }

    public RestDataSubnet(JSONObject item) {
        this.id          = item.getInt("id");
        this.server_id   = item.getInt("server_id");
        this.net         = item.getString("net");
        this.netmask     = item.getString("netmask");
        this.broadcast   = item.getString("broadcast");
        this.network     = item.getString("network");
        this.options     = item.getString("options");
        this.enable_ddns = item.getBoolean("enable_ddns");
        this.is_ipv6     = item.getBoolean("is_ipv6");
    }
}

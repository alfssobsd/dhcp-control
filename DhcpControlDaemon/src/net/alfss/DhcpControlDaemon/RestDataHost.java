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
 * Date: 04.01.12
 * Time: 1:25
 */

public class RestDataHost {
    public int       id;
    public int       server_id;
    public int       subnet_id;
    public int       group_id;
    public String    name;
    public String    ip;
    public String    mac;

    public RestDataHost() {

    }

    public RestDataHost(JSONObject item) {
        this.id        = item.getInt("id");
        this.server_id = item.getInt("server_id");
        this.subnet_id = item.getInt("subnet_id");
        this.group_id  = item.getInt("group_id");
        this.name      = item.getString("name");
        this.ip        = item.getString("ip");
        this.mac       = item.getString("mac");
    }
}

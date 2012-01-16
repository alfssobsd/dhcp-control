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
import org.json.JSONException;
import org.json.JSONObject;

public class RestDataServer {
    public int       id;
    public String    name;
    public String    options;
    public Boolean   enable_ddns;
    public Boolean   is_authoritative;

    public RestDataServer() {

    }

    public RestDataServer(JSONObject item) throws JSONException {
        this.id               = item.getInt("id");
        this.name             = item.getString("name");
        this.is_authoritative = item.getBoolean("is_authoritative");
        this.enable_ddns      = item.getBoolean("enable_ddns");
        this.options          = item.getString("options");
    }
}

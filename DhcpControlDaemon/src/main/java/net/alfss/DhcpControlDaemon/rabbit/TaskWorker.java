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

package net.alfss.DhcpControlDaemon.rabbit;

import net.alfss.DhcpControlDaemon.Config;
import net.alfss.DhcpControlDaemon.dhcp.Generator;
import net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest.RestDataGroup;
import net.alfss.DhcpControlDaemon.net.alfss.DhcpControlDaemon.Rest.RestDataSubnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 05.01.12
 * Time: 17:06
 */
public class TaskWorker extends  Thread {

    private final Object monitor = new Object();
    private boolean flag = true;
    private Queue<TaskData> tasks = new ConcurrentLinkedQueue<TaskData>();
    private Config config;
    final Logger logger = (Logger) LoggerFactory.getLogger("net/alfss/DhcpControlDaemon");
    private Generator generator;

    TaskWorker(Config t_config) {
        this.config = t_config;

        generator = new Generator(config.dhcp_dir,
                config.rest_api_url,
                config.rest_api_token,
                config.reat_api_server_id);
    }

    @Override
    public void run(){
        logger.warn("Start TaskWorker");
        int count = 0;
        while (flag){
            synchronized (monitor){
                if(this.chekTask() || count >= 5){
                    count = 0;
                    try {
                        logger.warn("Restart dhcp");
                        Process child = Runtime.getRuntime().exec(config.dhcp_restart_cmd);
                        if (this.chekTask()) {
                            monitor.wait();
                        }
                    } catch (IOException e){
                        logger.error("TaskWorker:" + e.toString());
                        try {
                            sleep(3000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        logger.error("TaskWorker:" + e.toString());
                    }
                }
            }

            count++;
            logger.debug("Unlock TaskWorker " + tasks.size());

            TaskData task = tasks.poll();
            if (task != null) {
                this.runTask(task);
            }
        }
    }

    public boolean chekTask(){
        synchronized (tasks){
            return tasks.isEmpty();
        }
    }

    public void addTask(TaskData t_task) {
        synchronized (tasks){
            tasks.add(t_task);
        }
    }

    public void unlockMonitor() {
        synchronized (monitor) {
            monitor.notify();
        }
    }

    public void shutdown () {
        flag = false;
        synchronized (monitor) {
            monitor.notify();
        }
    }

    private void runTask(TaskData task){

        if (task.getAction().equals("server:update") || task.getAction().equals("server:create")) {
            generator.genServer();
        } else if (task.getAction().equals("subnet:create")) {

            RestDataSubnet subnet = new RestDataSubnet();
            subnet.setServerId(task.getServerId());
            subnet.setId(task.getSubnetId());
            generator.genSubnet(subnet, true);
            generator.genServer();
        } else if (task.getAction().equals("subnet:update")) {

            RestDataSubnet subnet = new RestDataSubnet();
            subnet.setServerId(task.getServerId());
            subnet.setId(task.getSubnetId());
            generator.genSubnet(subnet, true);
        } else if (task.getAction().equals("subnet:remove")) {

            generator.removeSubnet(task.getSubnetId());
            generator.genServer();
        } else if (task.getAction().equals("group:create")) {

            RestDataSubnet subnet = new RestDataSubnet();
            RestDataGroup  group  = new RestDataGroup();
            group.setSubnetId(task.getSubnetId());
            group.setId(task.getGroupId());
            generator.genGroup(group, subnet, true);
            generator.genSubnet(subnet, false);
        } else if (task.getAction().equals("group:update")) {

            RestDataSubnet subnet = new RestDataSubnet();
            RestDataGroup group  = new RestDataGroup();
            group.setSubnetId(task.getSubnetId());
            group.setId(task.getGroupId());
            generator.genGroup(group, subnet, true);
        } else if (task.getAction().equals("group:remove")) {

            RestDataSubnet subnet = new RestDataSubnet();
            subnet.setServerId(task.getServerId());
            subnet.setId(task.getSubnetId());
            generator.removeGroup(task.getSubnetId(), task.getGroupId());
            generator.genSubnet(subnet, true);
        } else {
            logger.error("Unknow task action:", task.getAction());
        }
    }
}

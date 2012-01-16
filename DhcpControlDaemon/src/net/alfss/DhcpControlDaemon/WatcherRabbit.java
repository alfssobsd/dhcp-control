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

import com.rabbitmq.client.*;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 05.01.12
 * Time: 3:08
 */
public class WatcherRabbit extends Thread {
    private TaskWorker worker;
    private Config config;
    private QueueingConsumer  consumer;
    final private int  sleepTime = 10;
    final Logger logger = (Logger) LoggerFactory.getLogger("net/alfss/DhcpControlDaemon");


    public WatcherRabbit (Config t_config) {
        worker = new TaskWorker(t_config);
        worker.start();
        this.config = t_config;
    }

    @Override
    public void run() {
        logger.warn("Start WatcherRabbit");
        this.subscribeRabbit();
        logger.warn("Stop WatcherRabbit");
    }

    public void reconnectRabbit() {
        logger.error(MessageFormat.format("Reconnect to RabbitMQ server after {0} sec", sleepTime));
        try {
            sleep(1000 * sleepTime);
            this.connectRabbit();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public boolean connectRabbit() {
        ConnectionFactory factory =  new ConnectionFactory();
        factory.setHost(config.rabbit_host);
        factory.setPort(config.rabbit_port);
        factory.setVirtualHost(config.rabbit_vhost);
        factory.setUsername(config.rabbit_user);
        factory.setPassword(config.rabbit_password);
        factory.setConnectionTimeout(10000);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            logger.warn("Connected to RabbitMQ server");

            String exchangeName = "task" + config.reat_api_server_id;
            String queueName = exchangeName;

            channel.queueDeclare(queueName, false, false, false, null);

            channel.exchangeDeclare(exchangeName, "direct");
            channel.queueBind(queueName, exchangeName, "");
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueName, true, consumer);

            logger.warn(MessageFormat.format("Subscribe RabbitMQ queue: task{0} vhost:{1}", config.reat_api_server_id, config.rabbit_vhost));

            return true;
        } catch (IOException e) {
            logger.error("Error connect to RabbitMQ server");
            logger.error(e.toString());
            for(StackTraceElement traceElement:e.getStackTrace()){
                logger.debug(traceElement.toString());
            }
        }

        return false;
    }

    public void subscribeRabbit(){
        boolean flag = true;
        while(!this.connectRabbit()){
            this.reconnectRabbit();
        }
        while (flag) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());
                TaskData data = new TaskData(message);

                logger.warn("Read Task");
                logger.debug(message);

                worker.addTask(data);
                worker.unlockMonitor();
            } catch (JSONException e) {
                logger.error("Error task:", e.toString());
            } catch (InterruptedException e) {
                logger.error(e.toString());
                for(StackTraceElement traceElement:e.getStackTrace()){
                    logger.debug(traceElement.toString());
                }
                this.reconnectRabbit();
            } catch (ShutdownSignalException e) {
                logger.error(e.toString());
                this.reconnectRabbit();
            }
        }

    }
}

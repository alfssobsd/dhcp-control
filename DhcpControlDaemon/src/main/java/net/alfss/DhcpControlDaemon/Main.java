package net.alfss.DhcpControlDaemon;

import net.alfss.DhcpControlDaemon.dhcp.Generator;
import net.alfss.DhcpControlDaemon.rabbit.WatcherRabbit;
import org.apache.commons.cli.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import static java.lang.System.exit;

/**
 * User: Sergey V. Kravchuk <alfss.obsd@gmail.com>
 * Date: 29.02.12
 * Time: 4:01
 */
public class Main implements Daemon {
    final Logger logger = (Logger) LoggerFactory.getLogger("net/alfss/DhcpControlDaemon");
    private String [] arguments;
    private boolean generateAll = false;
    private String version = "1.0";

    public void startDaemon(String [] args){
        String config_file = "/etc/dhcp_control_daemon.xml";

        Options opt  = new Options();
        opt.addOption("h", false, "print help");
        opt.addOption("v", false, "print version");
        opt.addOption("c", true, "config file");
        opt.addOption("g", false, "generate all dhcp config");
        BasicParser parser= new BasicParser();
        try {
            CommandLine cl = parser.parse(opt, args);


            if( cl.hasOption('h') ) {
                HelpFormatter f = new HelpFormatter();
                f.printHelp("OptionsTip", opt);
                exit(0);
            }

            if( cl.hasOption('v') ) {
                System.out.println("DhcpControlDaemon: " + version);
                exit(0);
            }

            if (cl.hasOption('c')){
                config_file = cl.getOptionValue("c");
            }
            if (cl.hasOption('g')){
                generateAll = true;
            }

        } catch (ParseException e) {
            System.out.println("Argument Error: " + e.getMessage());
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OptionsTip", opt);
            exit(0);
        }

        logger.error(MessageFormat.format("############# Start DhcpControlDaemon {0}#############", version));
        try {
            XMLConfiguration xml_config = new XMLConfiguration(config_file);
            Config config = new Config(xml_config);
            if(!generateAll){
                WatcherRabbit watcherRabbit = new WatcherRabbit(config);
                watcherRabbit.start();
            } else {
                Generator generator;
                generator = new Generator(config.dhcp_dir,
                        config.rest_api_url,
                        config.rest_api_token,
                        config.reat_api_server_id);
                generator.genAll();
            }
        } catch (ConfigurationException e) {
            logger.error(e.toString());
            exit(0);
        }
    }


    public static void main (String [] args){
        Main main = new Main();
        main.startDaemon(args);
    }

    public void init(DaemonContext daemonContext) throws DaemonInitException, Exception {
        arguments = daemonContext.getArguments();
    }

    public void start() throws Exception {
        this.startDaemon(arguments);

    }

    public void stop() throws Exception {
        logger.error(MessageFormat.format("############# Stop DhcpControlDaemon {0}#############", version));
    }

    public void destroy() {

    }
}

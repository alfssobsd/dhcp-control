#!/bin/sh -e

### BEGIN INIT INFO
# Provides:          dhcp-control-daemon
# Required-Start:    $remote_fs
# Required-Stop:     $remote_fs
# Should-Start:      $network $syslog
# Should-Stop:       $network $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start and stop dhcp-control-daemon
# Description:       dhcp-control-daemon
#        which translates ip addresses to and from internet names
### END INIT INFO

PATH=/sbin:/bin:/usr/sbin:/usr/bin

LOGBACK=/etc/dhcp-control-daemon/logback.xml
CONF=/etc/dhcp-control-daemon/dhcp_control_daemon.xml
JSVC=/usr/bin/jsvc
JVM_OPT="-Xms64m -Xmx128m  -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC"
PIDFILE=/var/run/dhcp-control-daemon.pid
DAEMONJAR=/opt/DhcpControlDaemon/DhcpControlDaemon.jar
COMMON_DAEMON=/usr/share/java/commons-daemon.jar
MAINCLASS=net.alfss.DhcpControlDaemon.Main
JAVA=/usr/bin/java

test -f /etc/default/dhcp-control-daemon && . /etc/default/dhcp-control-daemon


. /lib/lsb/init-functions

case "$1" in
    start)
        $JSVC -jvm server -pidfile $PIDFILE -Dlogback.configurationFile=$LOGBACK $JVM_OPT \
            -cp $DAEMONJAR $MAINCLASS -c $CONF
    ;;

    stop)
        $JSVC -stop -pidfile $PIDFILE $MAINCLASS
    ;;

    restart)
	$0 stop
	$0 start
    ;;

    regen)
        $JAVA -Dlogback.configurationFile=$LOGBACK $JVM_OPT -jar $DAEMONJAR -c $CONF -g
    ;;

    *)
	log_action_msg "Usage: /etc/init.d/dhcp-control-daemon {start|stop|restart|regen}"
	exit 1
    ;;
esac

exit 0
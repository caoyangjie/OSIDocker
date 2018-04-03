#!/bin/sh

## java env
#export JAVA_HOME=/unionbank/jdk18
#export JRE_HOME=$JAVA_HOME/jre

## application path
APP_DIR=/unionbank
APP_NAME=union-bank-web

JAR_NAME=$APP_NAME\.jar

cd $APP_DIR

case "$1" in

    start)
		## check app process weather exists
		$0 stop
		echo "=== satrt $APP_NAME"
		nohup java -Duser.timezone=GMT+08 -Xms1024m -Xmx4096m -jar $APP_DIR/$JAR_NAME >/dev/null 2>&1 &
        ;;

    stop)
		## check app process weather exists
		process=`ps aux | grep -w "$APP_NAME" |grep -w "java" | grep -v grep`
		if [ "$process" == "" ]; then
			echo "=== $APP_NAME process not exists"
		else
			echo "=== $APP_NAME process exists"
			echo "=== $APP_NAME process is : $process"
			## get PID by process name
			P_ID=`ps -ef | grep -w "$APP_NAME" |grep -w "java"| grep -v "grep" | awk '{print $2}'`
			echo "=== $APP_NAME process PID is:$P_ID"
			echo "=== begin kill $APP_NAME process"
			kill $P_ID
			
			sleep 3

			P_ID=`ps -ef | grep -w "$APP_NAME" |grep -w "java"| grep -v "grep" | awk '{print $2}'`
			if [ "$P_ID" == "" ]; then
				echo "=== $APP_NAME process stop success"
			else
				echo "=== $APP_NAME process kill failed, PID is:$P_ID"
				echo "=== begin kill -9 $APP_NAME process, PID is:$P_ID"
			sleep 5
				kill -9 $P_ID
			fi
		fi
        ;;

    restart)
        $0 stop
        sleep 2
        $0 start
        echo "=== restart $APP_NAME"
        ;;

    *)
        ## start
        $0 start
        ;;
esac
exit 0


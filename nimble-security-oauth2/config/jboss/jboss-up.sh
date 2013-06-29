#!/bin/sh

if [ -z "$JBOSS_CONF" ]; then
  JBOSS_CONF="/etc/jboss-as/jboss-as.conf"
fi

echo "Load properties: $JBOSS_CONF"
[ -r "$JBOSS_CONF" ] && . "${JBOSS_CONF}"

#check to see if jboss should be running on this machine
if [ -z "$JBOSS_DOWN" ]; then
  JBOSS_DOWN="/tmp/jboss.staydown"
fi

#should JBoss be running
if [ -f $JBOSS_DOWN ]; then
    STAY_DOWN=1
else
    STAY_DOWN=0
fi


isJBossRunning() {
    echo "pid file: $JBOSS_PIDFILE"
    #if jboss was started using the service command, which it should've been, then we can look for the require pid on the pidfile
    if [ "x$JBOSS_PIDFILE" != "x" ] && [ -f $JBOSS_PIDFILE ]; then
        echo "pidfile found, lets read it"
        read kpid < $JBOSS_PIDFILE
        found=`ps -p $kpid | grep -v PID`
        if [ "x$found" != "x" ]; then
            echo "Found running pid from init.d file. Exiting"
            return 1
        fi
    else
        echo "No pid file recognized.  Will continue to look for a running jboss process but this is a problem in itself"
    fi

    #look for the correct running process
    found=`ps auxwww | grep org.jboss.as.standalone | grep -v grep | grep -o "[0-9]\+" | grep -o "[0-9]\+" -m1`
    if [ "x$found" != "x" ]; then
        echo "Found running jboss process. Exiting"
        return 1
    fi

    return 0

}


stopJBoss() {
    FIRST=5
    FINAL=10
    count=1
    #stop it via service first
    service jboss stop
    sleep 3
    #now check if it is still running
    pid=`ps auxwww | grep org.jboss.as.standalone | grep -v grep | grep -o "[0-9]\+" | grep -o "[0-9]\+" -m1`
    if [ "$pid" != "" ]; then
        echo "Now need to kill $pid"
        kill $pid
        while [ "$pid" != "" ]
        do
            echo  "."
            sleep 1
            let count=count+1
            if [ "$count" -gt "$FIRST" ];
            then
                #up the anty a bit, lets try to kill this thing
                kill -4 $pid
                if [ "$count" -gt "$FINAL" ]
                then
                    echo ""
                    echo "force kill of $pid"
                    kill -9 $pid
                    return 1
                fi
            fi
            pid=`ps auxwww | grep org.jboss.as.standalone | grep -v grep | grep -o "[0-9]\+" | grep -o "[0-9]\+" -m1`
        done
        echo "JBoss is stopped.  Had to kill it."
    else
        echo "JBoss is stopped.  No need to kill."
    fi

    return 1

}

getPid() {
    echo "find pid for $1"
    ps auxwww | grep $1 | grep -v grep
    PID=`ps auxwww | grep $SEARCH | grep -v grep | grep -o "[0-9]\+" | grep -o "[0-9]\+" -m1`
    #echo "returning pid $PID"
    return $PID

}


#see if JBoss is running
isJBossRunning
JBOSS_UP=$?

#echo "Is JBoss already running? $JBOSS_UP"

if [ $JBOSS_UP -eq 1 ]; then
    #JBoss is running, should it be?
    #echo "JBoss is up, should it be: $STAY_DOWN"
    if [ $STAY_DOWN -eq 1 ]; then
        echo "JBoss is running and it shouldn't be.  Shut it down."
        stopJBoss
    fi
else
    #it's not running, should we start it
    if [ $STAY_DOWN -ne 1 ]; then
        #if we got here we then we did not find the process.  Time to (re)start
        echo "(Re)starting JBoss"
        service jboss restart
    fi
fi


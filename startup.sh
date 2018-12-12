#!/bin/sh
set -x

tc qdisc add dev eth0 root handle 1: htb default 1
tc class add dev eth0 parent 1: classid 1:1 htb rate 10000mbps
tc class add dev eth0 parent 1:1 classid 1:200 htb rate 20000Kbit ceil 20480Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:800 htb rate 80000Kbit ceil 81920Kbit prio 1

jar=`ls /root/*.jar`
export LOGGING_PATH=/root/logs
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar ${jar} $1

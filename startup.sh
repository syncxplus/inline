#!/bin/sh
set -x

tc qdisc add dev eth0 root handle 1: htb default 1
tc class add dev eth0 parent 1: classid 1:1 htb rate 1000mbps
tc class add dev eth0 parent 1:1 classid 1:10 htb rate 1000Kbit ceil 1024Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:20 htb rate 2000Kbit ceil 2048Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:30 htb rate 3000Kbit ceil 3072Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:50 htb rate 5000Kbit ceil 5120Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:80 htb rate 8000Kbit ceil 8192Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:100 htb rate 10000Kbit ceil 10240Kbit prio 1
tc class add dev eth0 parent 1:1 classid 1:200 htb rate 20000Kbit ceil 20480Kbit prio 1

gradle clean
gradle bootJar
cd build/libs
jar=`ls *.jar`
export LOGGING_PATH=/usr/bin/app/logs
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar ${jar} $1

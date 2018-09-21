#!/bin/sh
set -x
gradle clean
gradle bootJar
cd build/libs
jar=`ls *.jar`
echo java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar ${jar} $1

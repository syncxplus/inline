#!/bin/sh
set -x
gradle clean
gradle bootJar
cd build/libs
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar ./inline-1.0.jar $1

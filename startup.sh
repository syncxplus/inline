#!/bin/sh
set -x
gradle clean
gradle bootJar
cd build/libs
java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar ./inline-0.1.jar $1

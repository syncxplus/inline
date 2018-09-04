#!/bin/sh
set -x
gradle clean
gradle bootJar
cd build/libs
java -Xms128m -Xmx256M -jar ./inline-0.1.jar $1

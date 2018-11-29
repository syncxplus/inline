FROM openjdk:8u181-jdk-alpine3.8

LABEL maintainer="jibo@outlook.com"

RUN apk add --no-cache apr curl git iproute2 openssh tomcat-native

WORKDIR /root
ADD build/libs/* /root/
COPY startup.sh /root/
ENTRYPOINT ["/bin/sh", "startup.sh"]

FROM openjdk:8u181-jdk-alpine3.8

LABEL maintainer="jibo@outlook.com"

RUN apk add --no-cache apr curl git iproute2 openssl tomcat-native

WORKDIR /root

RUN version=$(curl -ks --connect-timeout 10 -m 10 https://api.github.com/repos/syncxplus/inline/releases/latest | grep tag_name | awk '{print $2}' | sed 's/[",]//g') \
  && curl -OL https://github.com/syncxplus/inline/releases/download/${version}/inline-${version}.jar

COPY startup.sh .

ENTRYPOINT ["/bin/sh", "startup.sh"]

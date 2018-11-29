FROM openjdk:8u181-jdk-alpine3.8

LABEL maintainer="jibo@outlook.com"

RUN apk add --no-cache curl git iproute2 openssh

ARG GRADLE_VERSION=4.10.1
ARG GRADLE_SHA=e53ce3a01cf016b5d294eef20977ad4e3c13e761ac1e475f1ffad4c6141a92bd

RUN cd /usr/lib \
 && curl https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle-bin.zip \
 && echo "$GRADLE_SHA  gradle-bin.zip" | sha256sum -c - \
 && unzip "gradle-bin.zip" \
 && ln -s "/usr/lib/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle \
 && rm "gradle-bin.zip"
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

VOLUME ["/root/.gradle/caches"]
WORKDIR /usr/bin/app
COPY . ./
ENTRYPOINT ["/bin/sh", "startup.sh"]

FROM openjdk:8u171-jdk-stretch

LABEL maintainer="jibo@outlook.com"

ENV GRADLE_VERSION 4.9
ENV GRADLE_SHA e66e69dce8173dd2004b39ba93586a184628bc6c28461bc771d6835f7f9b0d28
RUN cd /usr/lib \
 && curl -fl https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle-bin.zip \
 && echo "$GRADLE_SHA gradle-bin.zip" | sha256sum -c - \
 && unzip "gradle-bin.zip" \
 && ln -s "/usr/lib/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle \
 && rm "gradle-bin.zip"
ENV GRADLE_HOME /usr/lib/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

RUN apt-get update \
 && apt-get install -y --allow-unauthenticated --no-install-recommends locales \
 && apt-get clean && rm -r /var/lib/apt/lists/* \
 && sed -i 's/# zh_CN.UTF-8 UTF-8/zh_CN.UTF-8 UTF-8/1' /etc/locale.gen \
 && locale-gen \
 && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
 && echo "Asia/Shanghai" > /etc/timezone
ENV LANG zh_CN.UTF-8

VOLUME ["/root/.gradle/caches"]
WORKDIR /usr/bin/app
COPY . ./
ENTRYPOINT ["/bin/bash", "startup.sh"]

#!/bin/sh

function checkCommand {
    command -v ${1} >/dev/null 2>&1 || {
        return 1
    }
    return 0
}

function killProcess {
    pid=$(ps -ef |grep "${1}" |grep -v grep |awk "{print \$2}")
    if [ ! -z ${pid} ]; then
        all=(${pid//\s+/ })
        for id in ${all[@]}
        do
            if [ "${id}" -gt 0 ] 2>/dev/null ;then
                echo "kill ${id}"
                if [ `uname -s` != Darwin ]; then
                    pstree -p $id| awk -F"[()]" "{system(\"kill \"\$2)}"
                else
                    pstree -g3 -l2 -U $id| awk "{system(\"kill \"\$2)}"
                fi
            else
                echo "ignore ${id}"
            fi
        done
    fi
}

export LOGGING_PATH=/root/logs
export VERSION=1.6

set -ex
if checkCommand unzip; then
  echo unzip already installed
else
  yum install -y unzip
fi
if [ ! -e "inline-${VERSION}.jar" ]; then
  curl -OL https://github.com/syncxplus/inline/releases/download/${VERSION}/inline-${VERSION}.zip
  unzip -o inline-${VERSION}.zip
  rm -rf inline-${VERSION}.zip
fi
set +ex

[ ! -e "/usr/lib64/libtcnative-1.so" ] && {
  yum install -y apr
  tcnative=tomcat-native-1.2.17-1.el7.x86_64.rpm
  curl -OL http://dl.fedoraproject.org/pub/epel/7/x86_64/Packages/t/${tcnative}
  rpm -ivh ${tcnative}
  rm -rf ${tcnative}
}

if checkCommand pstree; then
    echo pstree already installed
else
    if [ `uname -s` != Darwin ]; then
        yum install -y psmisc
    else
        brew install pstree
    fi
fi

killProcess "inline.*jar"
[ -e nohup.out ] && rm -rf nohup.out
nohup java -jar inline-${VERSION}.jar ${1:-} &

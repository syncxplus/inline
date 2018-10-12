#!/bin/sh

export VERSION=1.2-beta
export SB_VERSION=1.2
export LOGGING_PATH=/root/logs

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

if checkCommand pstree; then
    echo pstree already installed
else
    if [ `uname -s` != Darwin ]; then
        yum install -y psmisc
    else
        brew install pstree
    fi
fi

if checkCommand unzip; then
    echo unzip already installed
else
    yum install -y unzip
fi

killProcess "inline.*jar"
[ -e "inline-${VERSION}.jar" ] && rm -rf inline-${VERSION}.jar
[ -e nohup.out ] && rm -rf nohup.out

set -euo pipefail

curl -OL https://github.com/syncxplus/inline/releases/download/${VERSION}/inline-${VERSION}.zip
unzip -o inline-${VERSION}.zip && rm -rf inline-${VERSION}.zip
curl https://raw.githubusercontent.com/syncxplus/shadowbox/shadowbox/src/server_manager/install_scripts/install_server.sh | bash
nohup java -jar inline-${VERSION}.jar ${1:-} &

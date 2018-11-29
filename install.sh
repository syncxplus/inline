#!/bin/sh

function checkCommand {
    command -v ${1} >/dev/null 2>&1 || {
        return 1
    }
    return 0
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

killProcess "inline.*jar"

[ -e nohup.out ] && rm -rf nohup.out

VERSION=1.8

docker pull syncxplus/inline:${VERSION}
container=$(docker ps -a|grep inline|awk '{print $1}')
[[ ! -z "${container}" ]] && {
    docker rm -f -v ${container}
}
docker run --restart always --name inline -d --net host -p 8080:8080 --privileged -v /root/logs:/root/logs syncxplus/inline:${VERSION}

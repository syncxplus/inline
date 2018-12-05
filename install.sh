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
  if [[ `uname -s` != Darwin ]]; then
    yum install -y psmisc
  else
    brew install pstree
  fi
fi

function killProcess {
  pid=$(ps -ef |grep "${1}" |grep -v grep |awk "{print \$2}")
  if [[ ! -z ${pid} ]]; then
    all=(${pid//\s+/ })
      for id in ${all[@]}
      do
        if [[ "${id}" -gt 0 ]] 2>/dev/null ;then
          echo "kill ${id}"
          if [[ `uname -s` != Darwin ]]; then
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

readonly name=syncxplus/inline

readonly tag=$(curl -ks --connect-timeout 10 -m 10 https://registry.hub.docker.com/v1/repositories/${name}/tags |sed -e 's/[][]//g' -e 's/"//g' -e 's/ //g' | tr '}' '\n' | awk -F: '{print $3}'|grep -v '[A-Za-z]' | sort | awk 'END{print}')

if [[ "$?" != 0 ]]; then
  version=latest
else
  version=${tag}
fi

readonly image=${name}:${version}

echo Using ${image}

docker pull ${image}

readonly container=$(docker ps -a | grep inline | awk '{print $1}')

[[ ! -z "${container}" ]] && docker rm -f -v ${container}

docker run --restart always --name inline --net host --privileged -p 8080:8080 -v /root/logs:/root/logs -d ${image}

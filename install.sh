#!/bin/sh

set -euo pipefail

function checkCommand {
  command -v ${1} >/dev/null 2>&1 || {
    return 1
  }
  return 0
}

if checkCommand docker-compose; then
  echo docker-compose already installed
else
  curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
  python get-pip.py
  rm -rf get-pip.py
  pip install docker-compose --ignore-installed
fi

curl -H 'Cache-Control:no-cache' -OL https://raw.githubusercontent.com/syncxplus/inline/master/docker-compose.yml

[[ ! -z "$(docker ps -a|grep inline$)" ]] && docker rm -vf inline
[[ ! -z "$(docker ps -a|grep shadowbox$)" ]] && docker rm -vf shadowbox

docker-compose up -d

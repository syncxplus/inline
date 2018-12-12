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
  curl -L https://github.com/docker/compose/releases/download/1.23.2/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
  chmod +x /usr/local/bin/docker-compose
fi

curl -H 'Cache-Control:no-cache' -OL https://raw.githubusercontent.com/syncxplus/inline/master/docker-compose.yml

[[ ! -z "$(docker ps -a|grep inline$)" ]] && docker rm -vf inline
[[ ! -z "$(docker ps -a|grep shadowbox$)" ]] && docker rm -vf shadowbox

docker-compose up -d

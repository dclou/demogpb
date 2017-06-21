#!/bin/bash
[ "${PWD##*/}" = "demogpb" ] && cd ..
docker stack rm dclou
docker wait $(docker ps | awk '/dclou/{print $1}')
(cd platform && ./run.sh)
(cd demogpb && ./run.sh)

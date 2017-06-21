#!/bin/bash

if command -v docker-machine &>/dev/null; then
  #docker-machine create -d virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 7096 dclou || true
  #eval $(docker-machine env dclou)
  if docker-machine env dclou &>/dev/null; then
    eval $(docker-machine env dclou)
    IP="$(docker-machine ip dclou)"
    echo "Runnung in Virtaul Machine dclou with ip:$IP"
  else
    echo "Runnung on the local station"
  fi
fi

docker stack deploy -c docker-compose.yml dclou

# List all images
#docker stack ps dclou
#Overall service info
#docker service ls

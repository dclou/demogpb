#!/bin/bash
docker stack rm dclou
docker images 'dcloudemo/*' --format '{{.ID}}' | xargs docker rmi -f

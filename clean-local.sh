#!/bin/bash
docker stack rm dclou
docker images '*' --format '{{.ID}}' | xargs docker rmi -f

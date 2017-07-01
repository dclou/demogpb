#!/bin/sh

HOST="$1"

mvn -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -s .travis.settings.xml -B package -DdeployDocker -DskipTests

ssh git clone -oStrictHostKeyChecking=no -p50022 user@$HOST 'ssh user@10.10.50.11 rm -rf demogpb'
ssh git clone -oStrictHostKeyChecking=no -p50022 user@$HOST 'ssh user@10.10.50.11 git clone https://github.com/dclou/demogpb'
ssh git clone -oStrictHostKeyChecking=no -p50022 user@$HOST 'ssh user@10.10.50.11 "cd demogpb && git checkout release-1.0.0 && ./run.sh"'

#!/bin/bash
cd Paper/work/test-server

port=25565
jar="../../Paper-Server/target/paper-1.14.4.jar"
baseargs="-server -Xms${PAPER_MIN_TEST_MEMORY:-512M} -Xmx${PAPER_TEST_MEMORY:-2G} -Dfile.encoding=UTF-8 -XX:MaxGCPauseMillis=150 -XX:+UseG1GC "
baseargs="$baseargs -DIReallyKnowWhatIAmDoingISwear=1 -XX:TargetSurvivorRatio=90 "
baseargs="$baseargs -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=40 -XX:G1MaxNewSizePercent=80 "
baseargs="$baseargs -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=20 "
baseargs="$baseargs -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5100"
cmd="java ${PAPER_TEST_BASE_JVM_ARGS:-$baseargs} -jar $jar -p $port"
/usr/bin/env bash -c "$cmd"


#!/bin/bash
cd Waterfall/work
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5101 -jar ../Waterfall-Proxy/bootstrap/target/Waterfall.jar
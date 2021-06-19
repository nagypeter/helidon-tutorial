#!/bin/sh

#usage
#curl -LSs https://raw.githubusercontent.com/nagypeter/helidon-tutorial/master/scripts/setup.zipkin.sh | bash

cd ~

kill -9 $(ps -ef | grep '[j]ava -jar zipkin.jar' | awk '{print $2}')

rm -rf zipkin

mkdir zipkin

cd zipkin

curl -sSL https://zipkin.io/quickstart.sh | bash -s

java -jar zipkin.jar &

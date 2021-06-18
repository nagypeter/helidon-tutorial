#!/bin/sh

#usage
#curl -LSs https://raw.githubusercontent.com/nagypeter/helidon-tutorial/master/scripts/setup.prometheus.sh | bash

cd ~

kill -9 $(ps -ef | grep '[p]rometheus --config.file=prometheus.yml' | awk '{print $2}')

rm -rf prometheus

mkdir prometheus

cd prometheus

if [ "$(uname -s)" == "Darwin" ]; then
  curl -LJO https://github.com/prometheus/prometheus/releases/download/v2.28.0-rc.0/prometheus-2.28.0-rc.0.darwin-amd64.tar.gz
elif [ "$(uname -s)" == "Linux" ]; then
  curl -LJO https://github.com/prometheus/prometheus/releases/download/v2.28.0-rc.0/prometheus-2.28.0-rc.0.linux-amd64.tar.gz
fi

tar xvfz prometheus-*
prometheus_dir=$(echo */)
cd $prometheus_dir

cat > prometheus.yml <<EOF
global:
  scrape_interval:     15s
  external_labels:
    monitor: 'codelab-monitor'
scrape_configs:
  - job_name: 'prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['localhost:8080', 'localhost:8081']
EOF

./prometheus --config.file=prometheus.yml &

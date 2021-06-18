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
cd prometheus*

cat << EOF > prometheus.yml
global:
  scrape_interval:     15s # By default, scrape targets every 15 seconds.

  # Attach these labels to any time series or alerts when communicating with
  # external systems (federation, remote storage, Alertmanager).
  external_labels:
    monitor: 'codelab-monitor'

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: 'prometheus'

    # Override the global default and scrape targets from this job every 5 seconds.
    scrape_interval: 5s

    static_configs:
      - targets: ['localhost:8080', 'localhost:8081']

EOF

./prometheus --config.file=prometheus.yml &

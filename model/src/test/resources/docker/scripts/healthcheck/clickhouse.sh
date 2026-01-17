#!/bin/sh

set -e

retry=0

until docker exec -i jsql-clickhouse clickhouse-client -u dba --password dba -q "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Clickhouse is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Clickhouse is up - executing command"
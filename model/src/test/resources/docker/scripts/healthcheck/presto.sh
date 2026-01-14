#!/bin/sh

set -e

retry=0

until docker exec -i jsql-presto presto-cli --server 127.0.0.1:8084 --execute "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Presto is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Presto is up - executing command"
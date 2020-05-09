#!/bin/sh

set -e

retry=0

until docker exec -i jsql-mysql mysql -uroot -pmy-secret-pw -e "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Mysql is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Mysql is up - executing command"

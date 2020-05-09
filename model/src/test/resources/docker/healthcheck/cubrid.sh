#!/bin/sh

set -e

retry=0

until docker exec -i jsql-cubrid csql demodb -c "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Cubrid is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Cubrid is up - executing command"
exec $cmd

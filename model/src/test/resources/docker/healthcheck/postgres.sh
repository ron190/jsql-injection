#!/bin/sh

set -e

retry=0

until docker exec -i jsql-postgres psql -h "127.0.0.1" -U "postgres" -c "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Postgres is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Postgres is up - executing command"
 
#!/bin/sh

set -e

retry=0

until docker exec -i jsql-sqlserver /opt/mssql-tools/bin/sqlcmd \
-S "tcp:127.0.0.1,1434" \
-U SA \
-P "yourStrong(!)Password" \
-Q "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Sqlserver is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Sqlserver is up - executing command"

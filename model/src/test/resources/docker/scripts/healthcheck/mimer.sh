#!/bin/sh

set -e

retry=0

docker logs jsql-mimer

until cat <<EOF | docker exec -i jsql-mimer bsql -u SYSADM -p SYSADM
  select 'jsqlValue' as jsqlColumn from (values(0));
EOF
do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Mimer is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Mimer is up - executing command"
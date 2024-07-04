#!/bin/sh

set -e

retry=0

cat <<EOF | docker exec -i jsql-monetdb /bin/bash
    echo $'user=monetdb\npassword=monetdb' > .monetdb
EOF
until docker exec -i jsql-monetdb mclient -d db -s "select 'jsqlValue' as jsqlColumn"; do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Monetdb is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Monetdb is up - executing command"
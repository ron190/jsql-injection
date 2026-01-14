#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i jsql-virtuoso isql-vt 127.0.0.1 dba dba
select 'jsqlValue' as jsqlColumn;
EOF
do
  retry=$((retry+1))
  if [ $retry -gt 60 ] ; then
    exit 1
  fi

  >&2 echo "Virtuoso is unavailable - sleeping #${retry}"
  sleep 1
done

>&2 echo "Virtuoso is up - executing command"
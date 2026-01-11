#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i jsql-exasol exaplus -u sys -p exasol -c 127.0.0.1:8563/nocertcheck
select 'jsqlValue' as jsqlColumn;
EOF
do
  retry=$((retry+1))
  if [ $retry -gt 60 ] ; then
    exit 1
  fi

  >&2 echo "Exasol is unavailable - sleeping #${retry}"
  sleep 1
done

>&2 echo "Exasol is up - executing command"
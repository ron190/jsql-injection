#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i jsql-vertica /opt/vertica/bin/vsql
select 'jsqlValue' as jsqlColumn
EOF
do
  retry=$((retry+1))
  if [ $retry -gt 60 ] ; then
    exit 1
  fi

  >&2 echo "Vertica is unavailable - sleeping #${retry}"
  sleep 1
done

>&2 echo "Vertica is up - executing command"
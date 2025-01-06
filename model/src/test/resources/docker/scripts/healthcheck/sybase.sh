#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i jsql-sybase /sybase/isql
select 'jsqlValue' as jsqlColumn
go
EOF
do
  retry=$((retry+1))
  if [ $retry -gt 60 ] ; then
    exit 1
  fi

  >&2 echo "Sybase is unavailable - sleeping #${retry}"
  sleep 1
done

>&2 echo "Sybase is up - executing command"
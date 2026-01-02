#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i --workdir /database/config/db2inst1/sqllib/bin --user db2inst1 jsql-db2 /bin/bash
    . /database/config/db2inst1/sqllib/db2profile
    db2 list active databases
    db2 connect to testdb IN SHARE MODE user db2inst1 using test
    db2 select 1 as jsqlColumn from sysibm.sysversions
EOF
do
  retry=$((retry+1))
  if [ $retry -gt 60 ] ; then
    exit 1
  fi
  
  >&2 echo "Db2 is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Db2 is up - executing command"

# should be in Dockerfile or docker-compose when db is up
cat <<EOF | docker exec -i --workdir /database/config/db2inst1/sqllib/bin --user db2inst1 jsql-db2 /bin/bash
    . /database/config/db2inst1/sqllib/db2profile
    db2 update dbm cfg using SVCENAME 50011
    db2 force applications all
    db2stop
    db2start
EOF
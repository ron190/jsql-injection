#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i --user db2inst1 jsql-db2 /bin/bash     
    
    export DB2INSTANCE=db2inst1                                                                                                                            
    cd database/config/db2inst1/sqllib/bin  
    ./db2 connect to testdb                                  
    ./db2 select 1 as jsqlColumn from sysibm.sysversions                    
EOF
do
  retry=$((retry+1))
  if [ $retry -gt 15 ] ; then
    exit 1
  fi
  
  >&2 echo "Db2 is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Db2 is up - executing command"
exec $cmd
#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i jsql-oracle sqlplus -S system/Password1_One@XE
WHENEVER OSERROR EXIT FAILURE;
WHENEVER SQLERROR EXIT SQL.SQLCODE;
select 'jsqlValue' as jsqlColumn from dual;
EOF
do
  retry=$((retry+1))
  if [ $retry -gt $((60*10)) ] ; then
    exit 1
  fi

  >&2 echo "Oracle is unavailable - sleeping #${retry}"
  sleep 10
done

# bounce listener
lsnrctl status
chmod 777 /opt/oracle/product/18c/dbhomeXE/network/log/
cat <<EOF | lsnrctl
stop
start
EOF

>&2 echo "Oracle is up - executing command"
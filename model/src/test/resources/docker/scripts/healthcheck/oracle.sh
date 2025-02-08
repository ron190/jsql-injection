#!/bin/sh
set -e

retry=0
until cat <<EOF | docker exec -i jsql-oracle sqlplus 'system/Password1_One@XE'
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

cat <<EOF | docker exec -i jsql-oracle /bin/bash
echo '1/4 Status lsnrctl...' && lsnrctl status
echo '2/4 Chmod log...' && chmod 777 /opt/oracle/product/18c/dbhomeXE/network/log/
echo '3/4 Bouncing...' && cat <<EOF2 | lsnrctl
stop
start
EOF2
echo '4/4 Chmod oracle...' && chmod 6751 \$ORACLE_HOME/bin/oracle
EOF

retry=0
until cat <<EOF | docker exec -i jsql-oracle sqlplus 'system/Password1_One@XE'
WHENEVER OSERROR EXIT FAILURE;
WHENEVER SQLERROR EXIT SQL.SQLCODE;
select 'jsqlValue' as jsqlColumn from dual;
EOF
do
  retry=$((retry+1))
  if [ $retry -gt $((10)) ] ; then
    exit 1
  fi

  >&2 echo "Oracle is unavailable - sleeping #${retry}"
  sleep 10
done

>&2 echo "Oracle is up - executing command"
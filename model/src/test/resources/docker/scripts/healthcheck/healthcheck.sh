#!/bin/bash

function main {
  case $1 in
    Main)
      waiter Cubrid
      waiter Firebird
      waiter Informix
      waiter Lamp-Mysql
      waiter Lamp-Postgres
      waiter Mysql-5-5-53
      waiter Neo4j
      waiter Postgres
      waiter Mysql  # last as slow
    ;;

    Db2)
      waiter "$1"

      # should be in Dockerfile or docker-compose when db is up
      cat <<EOF | docker exec -i --workdir /database/config/db2inst1/sqllib/bin --user db2inst1 jsql-db2 /bin/bash
        . /database/config/db2inst1/sqllib/db2profile
        db2 update dbm cfg using SVCENAME 50011
        db2 force applications all
        sleep 6
        db2stop || true
        db2start || true
EOF
    ;;

    Hana)
      sleep 120  # hana setup
      waiter "$1"
      echo Starting post start phase, creating tenant database, sleeping 300s...
      sleep 300  # end of startup after getting result
    ;;

    Monetdb)
      cat <<EOF | docker exec -i jsql-monetdb /bin/bash
        echo $'user=monetdb\npassword=monetdb' > .monetdb
EOF
      waiter "$1"
    ;;

    Oracle)
      retry=0
      waiter "$1"

      >&2 echo "Up - bouncing..."
      cat <<EOF | docker exec -i jsql-oracle /bin/bash
        echo '### 1/4 Status lsnrctl...'
        lsnrctl status
        echo '### 2/4 Chmod log...'
        chmod 777 /opt/oracle/product/18c/dbhomeXE/network/log/
        echo '### 3/4 Bouncing...'
        cat <<EOF2 | lsnrctl
        stop
        start
EOF2
        echo '### 4/4 Chmod oracle...'
        chmod 6751 \$ORACLE_HOME/bin/oracle
EOF

      waiter "$1"
    ;;

    *)
      if test -n "$1"; then
        waiter "$1"
      else
        echo 'Missing vendor'
      fi
    ;;
  esac
}

function Clickhouse {  # shellcheck disable=SC2317
  docker exec -i jsql-clickhouse clickhouse-client -u dba --password dba -q "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Cubrid {  # shellcheck disable=SC2317
  docker exec -i jsql-cubrid csql demodb -c "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Db2 {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i --workdir /database/config/db2inst1/sqllib/bin --user db2inst1 jsql-db2 /bin/bash
    . /database/config/db2inst1/sqllib/db2profile
    db2 list active databases
    db2 connect to testdb IN SHARE MODE user db2inst1 using test
    db2 select 1 as jsqlColumn from sysibm.sysversions
EOF
}  # correct status 1 on error

function Exasol {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-exasol exaplus -u sys -p exasol -c 127.0.0.1:8563/nocertcheck
    select 'jsqlValue' as jsqlColumn;
EOF
}  # no status 1 on error

function Firebird {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-firebird /usr/local/firebird/bin/isql
    CONNECT /firebird/data/EMPLOYEE.FDB;
    select 'jsqlValue' as jsqlColumn from rdb\$database;
EOF
}  # correct status 1 on error

# shellcheck disable=SC2317
function Hana {
  result=$(  # todo should use hdbsql cli instead (not working)
    echo '
      Class.forName("com.sap.db.jdbc.Driver")
      import java.sql.*;

      StringBuilder result = new StringBuilder();
      try (
          Connection con = DriverManager.getConnection("jdbc:sap://127.0.0.1:39017?encrypt=false&validateCertificate=false", "system", "1anaHEXH");
          PreparedStatement pstmt = con.prepareStatement("select 1337330+1 from dummy");
          ResultSet rs = pstmt.executeQuery()
      ) {
          while (rs.next()) result.append(rs.getString(1));
      }
      System.out.println(result);
    ' | jshell --class-path "model/src/test/resources/docker/scripts/healthcheck/jdbc/ngdbc-2.27.6.jar" --feedback silent
  )
  echo "$result" | grep -q "1337331"
}  # correct status 1 on error

function Informix {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-informix /bin/bash
    export INFORMIXDIR=/opt/ibm/informix/
    export INFORMIXSERVER=informix
    /opt/ibm/informix/bin/dbaccess <<EOF2
    DATABASE sysutils;
    select 'jsqlValue' as jsqlColumn;
EOF2
EOF
}  # correct status 1 on error

function Lamp-Mysql {  # shellcheck disable=SC2317
  docker exec -i jsql-lamp mysql -uroot -ppassword -e "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Lamp-Postgres {  # shellcheck disable=SC2317
  docker exec -i jsql-lamp psql 'postgresql://postgres:my-secret-pw@localhost' -c "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Mimer {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-mimer bsql -u SYSADM -p SYSADM
    select 'jsqlValue' as jsqlColumn from (values(0));
EOF
}  # no status 1 on error

function Monetdb {  # shellcheck disable=SC2317
  docker exec -i jsql-monetdb mclient -d db -s "select 'jsqlValue' as jsqlColumn"
}  # no status 1 on error

function Mysql {  # shellcheck disable=SC2317
  docker exec -i jsql-mysql mysql -uroot -pmy-secret-pw -e "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Mysql-5-5-53 {  # shellcheck disable=SC2317
  docker exec -i jsql-mysql-5-5-53 mysql -uroot -pmy-secret-pw -e "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Neo4j {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-neo4j bin/cypher-shell -u neo4j -p test -d neo4j
    return 'Neo4j query done';
EOF
}  # correct status 1 on error

function Oracle {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-oracle sqlplus 'system/Password1_One@XE'
  WHENEVER OSERROR EXIT FAILURE;
  WHENEVER SQLERROR EXIT SQL.SQLCODE;
  select 'jsqlValue' as jsqlColumn from dual;
EOF
}  # no status 1 on error

function Postgres {  # shellcheck disable=SC2317
  docker exec -i jsql-postgres psql -h "localhost" -p 5432 -U postgres -c "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Presto {  # shellcheck disable=SC2317
  docker exec -i jsql-presto presto-cli --server 127.0.0.1:8084 --execute "select 'jsqlValue' as jsqlColumn"
}  # correct status 1 on error

function Sqlserver {  # shellcheck disable=SC2317
  docker exec -i jsql-sqlserver /opt/mssql-tools/bin/sqlcmd -S "tcp:jsql-sqlserver,1433" -U SA -P 'yourStrong(!)Password' -Q "select 'jsqlValue' as jsqlColumn"
}  # no status 1 on error

function Sybase {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-sybase /sybase/isql
  select 'jsqlValue' as jsqlColumn
  go
EOF
}  # no status 1 on error

function Vertica {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-vertica /opt/vertica/bin/vsql -U dbadmin -w password
  select 'jsqlValue' as jsqlColumn
EOF
}  # no status 1 on error

function Virtuoso {  # shellcheck disable=SC2317
  cat <<EOF | docker exec -i jsql-virtuoso isql-vt 127.0.0.1 dba dba
  select 'jsqlValue' as jsqlColumn;
EOF
}  # no status 1 on error

function waiter {
  retry=0
  if ! fn_exists "$1"; then
    echo "Missing $1 check, exiting"
    exit 1
  fi

  echo "Checking $1..."
  until eval "$1"; do
    retry=$((retry+1))
    if [ $retry -gt $((60 * 10)) ]; then
      exit 1
    fi
    >&2 echo "Unavailable - sleeping #${retry}"
    sleep 10
  done
  >&2 echo "Up"
}

function fn_exists {
  declare -F "$@" > /dev/null;
}

main "$@"; exit
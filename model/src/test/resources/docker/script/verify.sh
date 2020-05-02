# Buff MySQL
buffMysql="
   mysql -uroot -pmy-secret-pw -e ' \
   SET GLOBAL max_connections = 100000; \
   SET GLOBAL thread_cache_size = 16384; \
   SET GLOBAL table_open_cache = 524288; \
   ';
"
docker exec -i jsql-mysql /bin/bash -c ${buffMysql}

# Check MySQL status
checkMysql="
   mysql -uroot -pmy-secret-pw -e ' \
   SHOW GLOBAL variables WHERE Variable_name RLIKE \"thread_cache_size|^max_connections|table_open_cache\"; \
   SHOW GLOBAL status WHERE Variable_name RLIKE \"Threads_cached|Max_used_connections|Threads_created|Connections|Opened_tables|Threads_connected|Threads_running|^Queries\" \
   ';
"
docker exec -i jsql-mysql /bin/bash -c ${checkMysql}

# Check Postgres status
checkPostgre="
    export PGPASSWORD=my-secret-pw;
    psql -U postgres -h 127.0.0.1 -d \"\" -e -a -c ' \
        show max_connections; \
    '
"
docker exec -i jsql-postgres /bin/bash -c ${checkPostgre}
   
docker exec -i jsql-sqlserver /opt/mssql-tools/bin/sqlcmd \
-S "tcp:127.0.0.1,1434" \
-U SA \
-P "yourStrong(!)Password" \
-Q "select @@version"
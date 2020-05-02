# Buff MySQL
docker exec -i jsql-mysql /bin/bash \
-c << EOF
   mysql -uroot -pmy-secret-pw -e '
   SET GLOBAL max_connections = 100000;
   SET GLOBAL thread_cache_size = 16384;
   SET GLOBAL table_open_cache = 524288;
   ';
EOF

# Check MySQL status
docker exec -i jsql-mysql /bin/bash \
-c << EOF
   mysql -uroot -pmy-secret-pw -e '
   SHOW GLOBAL variables WHERE Variable_name RLIKE \"thread_cache_size|^max_connections|table_open_cache\";
   SHOW GLOBAL status WHERE Variable_name RLIKE \"Threads_cached|Max_used_connections|Threads_created|Connections|Opened_tables|Threads_connected|Threads_running|^Queries\"
   ';
EOF

# Check Postgres status
docker exec -i jsql-postgres /bin/bash \
-c << EOF
   export PGPASSWORD=my-secret-pw;
   psql -U postgres -h 127.0.0.1 -d \"\" -e -a -c '
   show max_connections;
   ';
EOF
   
docker exec -i jsql-sqlserver /opt/mssql-tools/bin/sqlcmd \
-S "tcp:127.0.0.1,1434" \
-U SA \
-P "yourStrong(!)Password" \
-Q "select @@version"
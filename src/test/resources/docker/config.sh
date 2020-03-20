REM Buff MySQL
docker exec -it jsql-mysql /bin/bash\
-c "\
   mysql -uroot -pmy-secret-pw -e '\
   SET GLOBAL max_connections = 100000;\
   SET GLOBAL thread_cache_size = 16384;\
   SET GLOBAL table_open_cache = 524288;\
   ';\
   "

REM Check MySQL status
docker exec -it jsql-mysql /bin/bash\
-c "\
   mysql -uroot -pmy-secret-pw -e '\
   SHOW GLOBAL variables WHERE Variable_name RLIKE \"thread_cache_size|^max_connections|table_open_cache\";\
   SHOW GLOBAL status WHERE Variable_name RLIKE \"Threads_cached|Max_used_connections|Threads_created|Connections|Opened_tables|Threads_connected|Threads_running|^Queries\"\
   ';\
   "

REM Check Postgres status
docker exec -it jsql-postgres /bin/bash\
-c "\
   export PGPASSWORD=my-secret-pw;\
   psql -U postgres -h 127.0.0.1 -d \"\" -e -a -c '\
   show max_connections;\
   ';\
   "
     
REM Check SQL Server connection
sleep 10s\
sudo docker exec -it jsql-sqlserver /opt/mssql-tools/bin/sqlcmd -S "tcp:127.0.0.1,1434" -U SA -P "yourStrong(!)Password" -Q "select @@version"
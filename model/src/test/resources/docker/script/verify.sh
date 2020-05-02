# Check MySQL status
cat <<EOF | sudo docker exec -i jsql-mysql /bin/bash
   mysql -uroot -pmy-secret-pw -e ' 
       SHOW GLOBAL variables WHERE Variable_name RLIKE "thread_cache_size|^max_connections|table_open_cache"; 
       SHOW GLOBAL status WHERE Variable_name RLIKE "Threads_cached|Max_used_connections|Threads_created|Connections|Opened_tables|Threads_connected|Threads_running|^Queries" 
   ';
EOF

# Check Postgres status
cat <<EOF | sudo docker exec -i jsql-postgres /bin/bash
    export PGPASSWORD=my-secret-pw;
    psql -U postgres -h 127.0.0.1 -d "" -e -a -c ' 
        show max_connections; 
    '
EOF
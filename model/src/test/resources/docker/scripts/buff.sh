# Buff MySQL
cat <<EOF | sudo docker exec -i jsql-mysql /bin/bash
  mysql -uroot -pmy-secret-pw -e '
    SET GLOBAL max_connections = 100000;
    SET GLOBAL thread_cache_size = 16384;
    SET GLOBAL table_open_cache = 524288;
  '
EOF

cat <<EOF | sudo docker exec -i jsql-mysql-5-5-53 /bin/bash
  mysql -uroot -pmy-secret-pw -e '
    SET GLOBAL max_connections = 100000;
    SET GLOBAL thread_cache_size = 16384;
    SET GLOBAL table_open_cache = 524288;
  '
EOF
# docker start $(docker ps -a -q)

docker run --name jsql-mysql \
--publish 3306:3306 \
-e MYSQL_ROOT_PASSWORD=my-secret-pw \
-d mysql &
pids[$!]=$!

docker run --name jsql-mysql-5.5.40 \
--publish 3307:3306 \
-e MYSQL_ROOT_PASSWORD=my-secret-pw \
-d mysql:5.5.40 &
pids[$!]=$!

docker run --name jsql-postgres \
--publish 5432:5432 \
-e POSTGRES_PASSWORD=my-secret-pw \
-d postgres \
-c 'shared_buffers=256MB' \
-c 'max_connections=1000' &
pids[$!]=$!

docker run --name jsql-sqlserver \
--publish 1434:1434 \
--publish 1433:1433 \
-e "ACCEPT_EULA=Y" \
-e "SA_PASSWORD=yourStrong(!)Password" \
-u 0:0 \
-d sqlserver &
pids[$!]=$!

docker run --name jsql-neo4j \
--publish 7687:7687 \
-e NEO4J_AUTH=neo4j/test \
-d neo4j &
pids[$!]=$! 

docker run --name jsql-cubrid \
--publish 33000:33000 \
-d cubrid &
pids[$!]=$!

docker run --name jsql-db2 \
-itd \
--privileged=true \
-p 50000:50000 \
-e LICENSE=accept \
-e DB2INST1_PASSWORD=test \
-e DBNAME=testdb \
ibmcom/db2 \
-d db2 &
pids[$!]=$!
    
for pid in ${pids[*]}; do wait $pid; done
jobs && docker images && docker ps && pwd
sleep 30s
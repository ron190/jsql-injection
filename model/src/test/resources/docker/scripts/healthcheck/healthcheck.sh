#!/bin/bash

pwd
echo 1/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/cubrid.sh
echo 2/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/mysql-5-5-40.sh
echo 3/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/mysql.sh
echo 4/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/neo4j.sh
echo 5/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/postgresql.sh
echo 6/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/sqlserver.sh
echo 7/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/monetdb.sh
echo 8/8 ======================================
./model/src/test/resources/docker/scripts/healthcheck/db2.sh
echo End ======================================


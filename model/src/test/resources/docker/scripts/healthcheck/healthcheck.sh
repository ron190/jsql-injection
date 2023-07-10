#!/bin/bash

pwd
echo 1/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/cubrid.sh
echo 2/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/db2.sh
echo 3/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/mysql-5-5-40.sh
echo 4/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/mysql.sh
echo 5/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/neo4j.sh
echo 6/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/postgres.sh
echo 7/7 ======================================
./model/src/test/resources/docker/scripts/healthcheck/sqlserver.sh
echo End ======================================


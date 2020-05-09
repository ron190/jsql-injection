#!/bin/bash

pwd
echo ======================================
./model/src/test/resources/docker/healthcheck/cubrid.sh
echo ======================================
./model/src/test/resources/docker/healthcheck/db2.sh
echo ======================================
./model/src/test/resources/docker/healthcheck/mysql-5.5.40.sh
echo ======================================
./model/src/test/resources/docker/healthcheck/mysql.sh
echo ======================================
./model/src/test/resources/docker/healthcheck/neo4j.sh
echo ======================================
./model/src/test/resources/docker/healthcheck/postgres.sh
echo ======================================
./model/src/test/resources/docker/healthcheck/sqlserver.sh
echo ======================================


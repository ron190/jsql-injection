#!/bin/bash

steps=0
function __echoStep {
  steps=$((steps+1)) && echo "## Step $steps/8"
}

__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/cubrid.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/mysql-5-5-40.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/mysql.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/neo4j.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/postgresql.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/sqlserver.sh
#__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/monetdb.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/mimer.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/db2.sh
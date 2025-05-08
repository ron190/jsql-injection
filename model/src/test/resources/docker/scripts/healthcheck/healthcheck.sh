#!/bin/bash

steps=0
function __echoStep {
  steps=$((steps+1)) && echo "## Step $steps/6"
}

__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/cubrid.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/mysql-5-5-53.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/mysql.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/neo4j.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/postgres.sh
__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/mimer.sh
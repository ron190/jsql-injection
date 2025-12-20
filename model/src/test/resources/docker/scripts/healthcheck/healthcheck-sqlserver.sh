#!/bin/bash

steps=0
function __echoStep {
  steps=$((steps+1)) && echo "## Step $steps/3"
}

__echoStep && ./model/src/test/resources/docker/scripts/healthcheck/sqlserver.sh

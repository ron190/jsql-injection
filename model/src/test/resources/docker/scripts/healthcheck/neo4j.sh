#!/bin/sh

set -e

retry=0

until cat <<EOF | docker exec -i jsql-neo4j bin/cypher-shell -u neo4j -p test -d neo4j

    return 'Neo4j query done';
EOF
do

  retry=$((retry+1))
  if [ $retry -gt 30 ] ; then
    exit 1
  fi
  
  >&2 echo "Neo4j is unavailable - sleeping #${retry}"
  sleep 1
done
  
>&2 echo "Neo4j is up - executing command"

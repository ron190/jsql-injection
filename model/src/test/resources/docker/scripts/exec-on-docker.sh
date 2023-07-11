docker exec -i jsql-mysql ./model/src/test/resources/vnc/execute-on-vnc.sh bash -c "$1"

DOCKER_RUN="$?"
echo docker run exit code: $DOCKER_RUN
if [ "${DOCKER_RUN}" != "0" ]; then exit 1; fi
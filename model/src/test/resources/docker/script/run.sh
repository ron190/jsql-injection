docker run                                                    \
    -v "$HOME/.m2":/root/.m2                                  \
    -v "$HOME/.sonar/cache":/root/.sonar/cache                \
    -e "SONARQUBE_SCANNER_PARAMS=${SONARQUBE_SCANNER_PARAMS}" \
    -e "SONAR_TOKEN=${SONAR_TOKEN}"                           \
    -e "MAVEN_NASHORN=${MAVEN_NASHORN}"                       \
    --network docker_jsql-network                             \
    jsql:latest                                               \
    ./model/src/test/resources/vnc/execute-on-vnc.sh          \
    mvn clean verify sonar:sonar
    
export DOCKER_RUN=$?
echo "docker run exit code: ${DOCKER_RUN}"
if [ ${DOCKER_RUN} -ne 0 ]; then exit 1; fi
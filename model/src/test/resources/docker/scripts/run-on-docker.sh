docker run                                          \
  -t                                                \
  -v "$HOME/.m2":/root/.m2                          \
  -v "$HOME/.sonar/cache":/root/.sonar/cache        \
  --network docker_jsql-network                     \
  --name docker_jsql-container                      \
  -e GITHUB_SHA                                     \
  -e SONAR_TOKEN                                    \
  -e CODACY_PROJECT_TOKEN                           \
  -e CODECOV_TOKEN                                  \
  -e MAVEN_NASHORN                                  \
  jsql:latest                                       \
  ./model/src/test/resources/vnc/execute-on-vnc.sh  \
  bash -c "$1"

DOCKER_RUN="$?"
echo docker run exit code: $DOCKER_RUN
if [ "${DOCKER_RUN}" != "0" ]; then exit 1; fi
. ./.github/workflows/scripts/github-functions.sh

# Upgrade versions
oldRevision=$(__getProperty 'jsql.version')
newRevision=$((${oldRevision//0./} + 1))

mvn versions:set-property -Dproperty=revision -DnewVersion=v0.$newRevision
sed -r -i -e "s/(jsql\.version = 0\.)(${oldRevision//0./})/\1$newRevision/g" "$CONFIG_FILE"
sed -r -i -e "s/(\"version\": \"0\.)(${oldRevision//0./}\")/\1$newRevision\"/g" "$WEB_SERVICE_FILE"

__gitPush

# Add release
responseCreateRelease=$(__createRelease)
echo "$responseCreateRelease"

releaseId=$(echo "$responseCreateRelease" | jq '.id')
echo "release-id=$releaseId" >> "$GITHUB_OUTPUT"

# Build and upload
mvn clean install -DskipTests

mv view/target/view-v0.$newRevision-jar-with-dependencies.jar view/target/jsql-injection-v0.$newRevision.jar

responseUploadAsset=$(__createAsset)
echo "$responseUploadAsset"
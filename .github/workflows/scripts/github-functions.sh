CONFIG_FILE="./model/src/main/resources/config.properties"
WEB_SERVICE_FILE="./web/services/jsql-injection.json"

function __getProperty {
  propToTrim=$(grep "${1}" "$CONFIG_FILE" | cut -d'=' -f2)
  # no quote for trim
  echo $(echo "$propToTrim")
}

function __createRelease {
  response="$(
    curl -L \
    -X POST \
    -H "Accept: application/vnd.github+json" \
    -H "Authorization: Bearer $GITHUB_TOKEN"\
    -H "X-GitHub-Api-Version: 2022-11-28" \
    https://api.github.com/repos/ron190/jsql-injection/releases \
    -d '{
      "tag_name": "v0.'"$newRevision"'",
      "name": "jSQL Injection v0.'"$newRevision"'",
      "draft": true,
      "prerelease": true,
      "discussion_category_name": "Releases"
    }'
  )"
  echo "$response"
}

function __createAsset {
  response="$(
    curl -L \
    -X POST \
    -H "Accept: application/vnd.github+json" \
    -H "Authorization: Bearer $GITHUB_TOKEN" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    -H "Content-Type: application/octet-stream" \
    "https://uploads.github.com/repos/ron190/jsql-injection/releases/$releaseId/assets?name=jsql-injection-v0.$newRevision.jar" \
    --data-binary "@view/target/jsql-injection-v0.$newRevision.jar"
  )"
  echo "$response"
}

function __gitPush {
  git pull  # Integrate the remote changes
  git config --global user.name "Github Actions"
  git config --global user.email no-response@github.com
  echo "diff:" && git diff --name-only
  git add pom.xml $CONFIG_FILE $WEB_SERVICE_FILE
  echo "diff --staged:" && git diff --staged --name-only
  git commit -m "[Pipeline] Release v0.$newRevision"
  git push
}

function __approveAsset {
  echo "approveAsset: $(
    curl -L \
      -X PATCH \
      -H "Accept: application/vnd.github+json" \
      -H "Authorization: Bearer $GITHUB_TOKEN" \
      -H "X-GitHub-Api-Version: 2022-11-28" \
      "https://api.github.com/repos/ron190/jsql-injection/releases/$RELEASE_ID" \
      -d '{
        "draft": false
      }'
  )"
}
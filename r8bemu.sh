#!/bin/bash
set -e
APP_HOME="$(dirname "$(realpath $0)")"
export JAR_FILE="$APP_HOME/target/r8bemu.jar"

if [ ! -f "$JAR_FILE" ]; then
  javac -d "$APP_HOME/classes" $(find $APP_HOME/src -name '*.java')
  mkdir -p "$(dirname "$JAR_FILE")"
  jar --create \
    --file "$JAR_FILE" \
    --main-class com.joprovost.r8bemu.R8BEmu \
    -C "$APP_HOME/classes" com \
    -C "$APP_HOME/resources" images \
    -C "$APP_HOME/resources" template
fi

exec $APP_HOME/resources/scripts/launch.sh "$@"

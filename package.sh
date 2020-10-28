#!/bin/bash
set -e
APP_HOME="$(dirname "$(realpath $0)")"
TARGET="$APP_HOME/target"
DIST="$APP_HOME/dist"
PACKAGE="$TARGET/package"
JAR_FILE="$TARGET/r8bemu.jar"

# Compile
rm -Rf "$TARGET"
javac -d "$TARGET/classes" \
  --release 11 \
  $(find $APP_HOME/src -name '*.java')
jar --create \
  --file "$JAR_FILE" \
  --main-class com.joprovost.r8bemu.R8BEmu \
  -C "$TARGET/classes" com \
  -C "$APP_HOME/resources" images \
  -C "$APP_HOME/resources" template

# Package
VERSION="$(git describe --tags)"
mkdir -p "$PACKAGE"
cp "$JAR_FILE" "$PACKAGE"
mkdir -p "$DIST"

SHELL_SCRIPT="$DIST/R8BEmu-$VERSION.sh"
cat "$APP_HOME/resources/scripts/launch.sh" "$JAR_FILE" > "$SHELL_SCRIPT"
chmod +x "$SHELL_SCRIPT"

BATCH_SCRIPT="$DIST/R8BEmu-$VERSION.bat"
cat "$APP_HOME/resources/scripts/launch.bat" "$JAR_FILE" > "$BATCH_SCRIPT"
chmod +x "$BATCH_SCRIPT"

case $(uname) in
  Darwin)
    jpackage \
      --name R8BEmu \
      --main-jar r8bemu.jar \
      --main-class com.joprovost.r8bemu.R8BEmu \
      --app-version "$VERSION" \
      --input "$PACKAGE" \
      --dest "$DIST" \
      --icon "$APP_HOME/resources/images/logo.icns"
    ;;
  Linux)
    jpackage \
      --name R8BEmu \
      --main-jar r8bemu.jar \
      --main-class com.joprovost.r8bemu.R8BEmu \
      --app-version "$VERSION" \
      --input "$PACKAGE" \
      --dest "$DIST" \
      --icon "$APP_HOME/resources/images/logo_128x128.png" \
      --linux-menu-group "Emulator" \
      --linux-shortcut
    ;;
esac

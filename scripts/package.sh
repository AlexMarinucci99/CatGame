#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

APP_NAME="CatGame"
MAIN_CLASS="com.master.bro.catdinogame.GameLauncher"
APP_VERSION="${APP_VERSION:-1.0.0}"
if [[ ! "$APP_VERSION" =~ ^[1-9][0-9]*(\.[0-9]+){0,2}$ ]]; then
  echo "APP_VERSION must have one to three numeric components and start above zero (for example 1.0.0): $APP_VERSION" >&2
  exit 2
fi

DIST="$ROOT_DIR/target/distributions"
INPUT="$ROOT_DIR/target/package-input"
IMAGE="$DIST/app-image/$APP_NAME"
rm -rf "$DIST" "$INPUT"
mkdir -p "$DIST" "$INPUT" "$DIST/app-image"

mvn -B -ntp org.apache.maven.plugins:maven-dependency-plugin:3.8.1:copy-dependencies \
  -DincludeScope=runtime -DoutputDirectory="$INPUT"
cp "$ROOT_DIR/target/CatDinoGame-"*.jar "$INPUT/"

MAIN_JAR="$(basename "$(find "$INPUT" -maxdepth 1 -name 'CatDinoGame-*.jar' -print -quit)")"
test -n "$MAIN_JAR"

jpackage \
  --type app-image \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --vendor "AlexMarinucci99" \
  --description "Gioco arcade 2D con MasterCat" \
  --input "$INPUT" \
  --main-jar "$MAIN_JAR" \
  --main-class "$MAIN_CLASS" \
  --dest "$DIST/app-image"

if [ "$(uname -s)" = Darwin ]; then
  IMAGE="$IMAGE.app"
fi

case "$(uname -s)" in
  Linux)
    jpackage \
      --type deb --name "$APP_NAME" --app-version "$APP_VERSION" \
      --vendor "AlexMarinucci99" --description "Gioco arcade 2D con MasterCat" \
      --app-image "$IMAGE" --dest "$DIST" \
      --linux-package-name catgame --linux-menu-group Game \
      --linux-shortcut --linux-package-deps "libgtk-3-0,libasound2,libgl1"
    DEB="$(find "$DIST" -maxdepth 1 -name '*.deb' -print -quit)"
    test -n "$DEB"
    mv "$DEB" "$DIST/CatGame-linux-$(uname -m)-$APP_VERSION.deb"
    (cd "$DIST/app-image" && zip -qr "$DIST/CatGame-linux-$(uname -m)-$APP_VERSION.zip" "$APP_NAME")
    ;;
  Darwin)
    jpackage \
      --type dmg --name "$APP_NAME" --app-version "$APP_VERSION" \
      --vendor "AlexMarinucci99" --description "Gioco arcade 2D con MasterCat" \
      --app-image "$IMAGE" --dest "$DIST" \
      --mac-package-identifier "com.alexmarinucci99.catgame" \
      --mac-package-name "$APP_NAME"
    DMG="$(find "$DIST" -maxdepth 1 -name '*.dmg' -print -quit)"
    test -n "$DMG"
    mv "$DMG" "$DIST/CatGame-macos-$(uname -m)-$APP_VERSION.dmg"
    ditto -c -k --sequesterRsrc --keepParent "$IMAGE" "$DIST/CatGame-macos-$(uname -m)-$APP_VERSION.zip"
    ;;
  *) echo "Unsupported packaging host: $(uname -s)" >&2; exit 2 ;;
esac

JAR_ENTRIES="$ROOT_DIR/target/package-jar-entries.txt"
jar tf "$INPUT/$MAIN_JAR" > "$JAR_ENTRIES"
while IFS= read -r resource; do
  resource="${resource#"$ROOT_DIR/src/main/resources/"}"
  grep -Fx "$resource" "$JAR_ENTRIES" >/dev/null || {
    echo "Missing packaged game resource: $resource" >&2
    exit 1
  }
done < <(find "$ROOT_DIR/src/main/resources" -type f)

if [ "$(uname -s)" = Darwin ]; then
  test -x "$IMAGE/Contents/MacOS/$APP_NAME"
  test -d "$IMAGE/Contents/runtime"
else
  test -x "$IMAGE/bin/$APP_NAME"
  test -d "$IMAGE/lib/runtime"
fi
find "$DIST" -maxdepth 1 -type f \( -name '*.deb' -o -name '*.dmg' \) -print -quit | grep -q .
find "$DIST" -maxdepth 1 -type f -name '*.zip' -print -quit | grep -q .
echo "Native packages created in $DIST"
find "$DIST" -maxdepth 1 -type f -printf '%f %s bytes\n' 2>/dev/null || find "$DIST" -maxdepth 1 -type f -print

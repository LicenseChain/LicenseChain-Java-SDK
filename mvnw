#!/usr/bin/env sh

set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
MAVEN_VERSION="3.9.9"
MAVEN_DIST="apache-maven-$MAVEN_VERSION"
MAVEN_HOME="$WRAPPER_DIR/$MAVEN_DIST"
MVN_BIN="$MAVEN_HOME/bin/mvn"
ARCHIVE="$WRAPPER_DIR/$MAVEN_DIST-bin.tar.gz"
URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/$MAVEN_DIST-bin.tar.gz"

if [ ! -x "$MVN_BIN" ]; then
  mkdir -p "$WRAPPER_DIR"
  if [ ! -f "$ARCHIVE" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "$URL" -o "$ARCHIVE"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "$URL" -O "$ARCHIVE"
    else
      echo "mvnw: curl or wget is required to download Maven." >&2
      exit 1
    fi
  fi
  tar -xzf "$ARCHIVE" -C "$WRAPPER_DIR"
fi

exec "$MVN_BIN" "$@"

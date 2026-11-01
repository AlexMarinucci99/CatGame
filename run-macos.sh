#!/usr/bin/env sh
set -eu
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
if ! command -v java >/dev/null 2>&1; then
  echo 'Java 21 is required. Install a Java 21 JDK and add java to PATH.' >&2
  exit 1
fi
if ! command -v javac >/dev/null 2>&1; then
  echo 'A Java 21 JDK is required (javac was not found).' >&2
  exit 1
fi
JDK_VERSION=$(javac -version 2>&1)
JDK_MAJOR=${JDK_VERSION#javac }
JDK_MAJOR=${JDK_MAJOR%%.*}
if ! [ "$JDK_MAJOR" -ge 21 ] 2>/dev/null; then
  echo 'Java 21 or newer is required. Check java, javac and JAVA_HOME.' >&2
  exit 1
fi
if ! command -v mvn >/dev/null 2>&1; then
  echo 'Apache Maven is required. Install Maven and add mvn to PATH.' >&2
  exit 1
fi
cd "$SCRIPT_DIR"
exec mvn -B -ntp -DskipTests package javafx:run

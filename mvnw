#!/bin/sh
# Maven Wrapper startup script for Unix
set -e
MVNW_LAUNCHER=mvnw
WRAPPER_JAR="$HOME/.m2/wrapper/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
if [ -f "$WRAPPER_JAR" ]; then
    exec java -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
else
    mkdir -p "$HOME/.m2/wrapper"
    curl -L -o "$WRAPPER_JAR" "$WRAPPER_URL"
    exec java -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
fi

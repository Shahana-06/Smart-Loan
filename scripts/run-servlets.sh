#!/bin/sh
set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
if [ -z "${TOMCAT_HOME:-}" ]; then
    printf '%s\n' "Set TOMCAT_HOME to an Apache Tomcat 10.1 installation." >&2
    exit 1
fi

if [ -x "$TOMCAT_HOME/bin/catalina.sh" ]; then
    CATALINA="$TOMCAT_HOME/bin/catalina.sh"
    WEBAPPS="$TOMCAT_HOME/webapps"
elif [ -x "$TOMCAT_HOME/libexec/bin/catalina.sh" ]; then
    CATALINA="$TOMCAT_HOME/libexec/bin/catalina.sh"
    WEBAPPS="$TOMCAT_HOME/libexec/webapps"
else
    printf 'Tomcat launcher not found under %s. Check TOMCAT_HOME.\n' "$TOMCAT_HOME" >&2
    exit 1
fi

if [ ! -d "$WEBAPPS" ]; then
    printf 'Tomcat webapps directory not found: %s\n' "$WEBAPPS" >&2
    exit 1
fi

cd "$PROJECT_DIR"
./mvnw clean package
export SMARTLOAN_DB="${SMARTLOAN_DB:-$PROJECT_DIR/smartloan.db}"
cp "$PROJECT_DIR/target/smart-loan.war" "$WEBAPPS/smart-loan.war"
exec "$CATALINA" run

#!/bin/sh
set -eu

PROJECT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$PROJECT_DIR"
export SMARTLOAN_DB="${SMARTLOAN_DB:-$PROJECT_DIR/smartloan.db}"
exec ./mvnw javafx:run

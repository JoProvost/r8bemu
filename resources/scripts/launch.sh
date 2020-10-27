#!/bin/bash
trap 'stty cooked echo 2>/dev/null || true; echo ""' EXIT
set -e
JAR_FILE="${JAR_FILE:-$0}"
if [[ "$*" =~ '--terminal' ]]; then clear; stty raw -echo; fi
java -jar "$JAR_FILE" "$@"
exit 0

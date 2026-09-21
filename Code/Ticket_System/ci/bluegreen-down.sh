#!/usr/bin/env bash
# Raeumt die Blue/Green-Umgebungen und den Proxy wieder auf.
set -euo pipefail

cd "$(dirname "$0")/.."
# shellcheck source=ci/bluegreen/lib.sh
. ci/bluegreen/lib.sh

for color in blue green; do
  docker compose -f "$COMPOSE_FILE" -p "$(project_of "$color")" down -v > /dev/null 2>&1 || true
  echo "--> Umgebung $color entfernt"
done
docker rm -f "$PROXY_NAME" > /dev/null 2>&1 || true
rm -f "$STATE_FILE" "$GENERATED_CONF"
echo "--> Proxy entfernt, Zustand zurueckgesetzt"

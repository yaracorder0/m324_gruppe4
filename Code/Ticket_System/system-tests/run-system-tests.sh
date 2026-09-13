#!/usr/bin/env bash
# Startet beide Microservices als JARs und fuehrt die Systemtests mit dem
# IntelliJ HTTP Client (Docker-Image) aus. Wird von den CI-Workflows verwendet.
#
# Voraussetzungen:
#   - PostgreSQL laeuft (docker compose up -d) und ist frisch initialisiert
#   - JARs liegen unter $JAR_DIR (Default: die target-Ordner der Services)
#
# Aufruf aus Code/Ticket_System:  bash system-tests/run-system-tests.sh
set -euo pipefail

cd "$(dirname "$0")/.."

EMPLOYEE_JAR=${EMPLOYEE_JAR:-$(find employee-service/target -maxdepth 1 -name 'employee-service-*.jar' | head -n 1)}
TICKET_JAR=${TICKET_JAR:-$(find ticket-service/target -maxdepth 1 -name 'ticket-service-*.jar' | head -n 1)}
IJHTTP_IMAGE=jetbrains/intellij-http-client:2026.2.2
LOG_DIR=system-tests/logs
mkdir -p "$LOG_DIR"

wait_for() {
  local name=$1 url=$2
  for _ in $(seq 1 60); do
    if curl -sf -o /dev/null "$url"; then
      echo "$name ist bereit ($url)"
      return 0
    fi
    sleep 2
  done
  echo "$name wurde nicht rechtzeitig bereit ($url)"
  cat "$LOG_DIR/$name.log"
  return 1
}

echo "Starte employee-service: $EMPLOYEE_JAR"
java -jar "$EMPLOYEE_JAR" > "$LOG_DIR/employee-service.log" 2>&1 &
echo "Starte ticket-service: $TICKET_JAR"
java -jar "$TICKET_JAR" > "$LOG_DIR/ticket-service.log" 2>&1 &

# Services beim Beenden des Skripts wieder stoppen
trap 'kill $(jobs -p) 2>/dev/null || true' EXIT

wait_for employee-service http://localhost:8081/api/employees
wait_for ticket-service http://localhost:8082/api/tickets

echo "Fuehre Systemtests aus"
docker run --rm --network host \
  -v "$PWD/system-tests:/workdir" \
  "$IJHTTP_IMAGE" \
  --env-file=http-client.env.json --env=ci \
  --report=reports --no-progress \
  system-tests.http

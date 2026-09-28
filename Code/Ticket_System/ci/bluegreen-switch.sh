#!/usr/bin/env bash
# ==============================================================================
# Traffic-Switching fuer Blue/Green: schaltet den Proxy auf die angegebene Farbe.
# Das ist die Rollback-Strategie "Traffic-Switching" aus der Theorie (T4):
# Die alte Umgebung laeuft noch, deshalb dauert der Rollback nur Sekunden.
#
# Aufruf aus Code/Ticket_System:
#   bash ci/bluegreen-switch.sh blue
# ==============================================================================

set -euo pipefail

cd "$(dirname "$0")/.."
# shellcheck source=ci/bluegreen/lib.sh
. ci/bluegreen/lib.sh

COLOR="${1:?Farbe angeben: blue oder green}"
if [ "$COLOR" != "blue" ] && [ "$COLOR" != "green" ]; then
  echo "Ungueltige Farbe: $COLOR (erlaubt: blue, green)"
  exit 1
fi

PROJECT="$(project_of "$COLOR")"
if [ -z "$(docker compose -f "$COMPOSE_FILE" -p "$PROJECT" ps -q 2>/dev/null)" ]; then
  echo "[-] Die Umgebung $COLOR laeuft nicht, Umschalten nicht moeglich."
  exit 1
fi

START=$(date +%s)
echo "--> Schalte Traffic von $(active_color) auf $COLOR"
switch_traffic_to "$COLOR"

if ! bash ci/smoke-test.sh "http://localhost:${PROXY_EMPLOYEE_PORT}" "http://localhost:${PROXY_TICKET_PORT}" > /dev/null; then
  echo "[-] Smoke Test nach dem Umschalten fehlgeschlagen!"
  exit 1
fi
END=$(date +%s)

echo "[+] Traffic laeuft jetzt auf $COLOR (Dauer inkl. Validierung: $((END - START))s)"

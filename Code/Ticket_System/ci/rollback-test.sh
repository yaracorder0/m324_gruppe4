#!/usr/bin/env bash
# ==============================================================================
# Praktischer Rollback-Test fuer P4 Continuous Deployment
#
# Prueft die Rollback-Strategie an der laufenden Umgebung:
#   1. Ausgangslage: stabile Version laeuft und ist gesund
#   2. Fehlerhaftes Release ausrollen (ungueltige DB-Config via Override-Datei)
#   3. Smoke Test erkennt den Fehler (muss fehlschlagen)
#   4. Automatischer Rollback: stabile Version erneut ausrollen
#   5. Smoke Test bestaetigt die Wiederherstellung, MTTR wird gemessen
#
# Das Skript ist nur dann erfolgreich (Exit 0), wenn der Fehler erkannt UND
# der Rollback erfolgreich war.
#
# Aufruf aus Code/Ticket_System:
#   IMAGE_TAG=<stabiler-tag> bash ci/rollback-test.sh
# ==============================================================================

set -euo pipefail

cd "$(dirname "$0")/.."

COMPOSE_FILE="docker-compose.release.yml"
BROKEN_OVERRIDE="ci/docker-compose.broken.yml"
STABLE_TAG="${IMAGE_TAG:-latest}"
EMPLOYEE_URL="${EMPLOYEE_URL:-http://localhost:8081}"
TICKET_URL="${TICKET_URL:-http://localhost:8082}"

echo "========================================================"
echo " [ROLLBACK-TEST] Praktische Pruefung der Rollback-Strategie"
echo " Stabile Version: $STABLE_TAG"
echo "========================================================"

# ---------------------------------------------------------------------------
echo ""
echo "[Schritt 1/5] Ausgangslage pruefen: laeuft die stabile Version?"
if ! bash ci/smoke-test.sh "$EMPLOYEE_URL" "$TICKET_URL" > /dev/null; then
  echo "[-] ABBRUCH: Die stabile Version ist vor dem Test nicht gesund."
  exit 1
fi
echo "[+] Stabile Version $STABLE_TAG laeuft und ist funktionsfaehig."

# ---------------------------------------------------------------------------
echo ""
echo "[Schritt 2/5] Fehlerhaftes Release ausrollen (ungueltige Datenbank-Konfiguration)"
docker compose -f "$COMPOSE_FILE" -f "$BROKEN_OVERRIDE" up -d --force-recreate employee-service
echo "[+] Fehlerhafte Version ist ausgerollt."

# ---------------------------------------------------------------------------
echo ""
echo "[Schritt 3/5] Post-Deployment Smoke Test (muss fehlschlagen)"
FAILURE_DETECTED_AT=$(date +%s)
if MAX_RETRIES=5 bash ci/smoke-test.sh "$EMPLOYEE_URL" "$TICKET_URL"; then
  echo "[-] FEHLER: Der Smoke Test war erfolgreich, obwohl das Release defekt ist."
  echo "    Der Rollback-Mechanismus wuerde einen echten Fehler nicht erkennen."
  echo "--> Umgebung wird trotzdem auf die stabile Version zurueckgesetzt."
  docker compose -f "$COMPOSE_FILE" up -d --force-recreate employee-service
  exit 1
fi
echo "[!] ALARM: Smoke Test fehlgeschlagen, fehlerhaftes Release wurde erkannt."
echo "--> Health-Status des fehlerhaften Service:"
curl -s "$EMPLOYEE_URL/actuator/health" | head -c 200 || echo "(Service nicht erreichbar)"
echo ""

# ---------------------------------------------------------------------------
echo ""
echo "[Schritt 4/5] AUTOMATISCHER ROLLBACK auf die stabile Version $STABLE_TAG"
ROLLBACK_START=$(date +%s)
docker compose -f "$COMPOSE_FILE" up -d --force-recreate employee-service
echo "[+] Rollback-Deployment gestartet."

# ---------------------------------------------------------------------------
echo ""
echo "[Schritt 5/5] Validierung nach dem Rollback"
if ! bash ci/smoke-test.sh "$EMPLOYEE_URL" "$TICKET_URL"; then
  echo "[-] FEHLER: Das System ist nach dem Rollback nicht funktionsfaehig."
  exit 1
fi
ROLLBACK_END=$(date +%s)

echo ""
echo "========================================================"
echo " ROLLBACK-TEST BESTANDEN"
echo " - Fehlerhaftes Release wurde erkannt"
echo " - Rollback auf $STABLE_TAG war erfolgreich"
echo " - Zeit bis zur Wiederherstellung (MTTR): $((ROLLBACK_END - ROLLBACK_START))s"
echo " - Zeit ab Fehlererkennung gesamt:        $((ROLLBACK_END - FAILURE_DETECTED_AT))s"
echo "========================================================"

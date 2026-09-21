#!/usr/bin/env bash
# ==============================================================================
# Blue/Green Deployment (zweite Rollout-Strategie, Vergleich zu "Recreate")
#
# Ablauf:
#   1. Aktive Farbe ermitteln (Blue oder Green)
#   2. Neue Version in die INAKTIVE Umgebung ausrollen (Nutzer merken davon nichts)
#   3. Smoke Test direkt gegen die inaktive Umgebung
#   4. Erst bei Erfolg: Traffic per nginx auf die neue Umgebung umschalten
#   5. Smoke Test ueber den Proxy bestaetigt den Wechsel
#   6. Alte Umgebung bleibt laufen -> Rollback in Sekunden (bluegreen-switch.sh)
#
# Aufruf aus Code/Ticket_System:
#   IMAGE_REGISTRY=... bash ci/bluegreen-deploy.sh <image-tag>
# ==============================================================================

set -euo pipefail

cd "$(dirname "$0")/.."
# shellcheck source=ci/bluegreen/lib.sh
. ci/bluegreen/lib.sh

NEW_TAG="${1:?Image-Tag angeben, z.B. bash ci/bluegreen-deploy.sh 1.0.0}"
export IMAGE_REGISTRY="${IMAGE_REGISTRY:-ghcr.io/yaracorder0/m324_gruppe4}"

ACTIVE="$(active_color)"
TARGET="$(inactive_color)"
TARGET_EMPLOYEE_PORT="$(employee_port_of "$TARGET")"
TARGET_TICKET_PORT="$(ticket_port_of "$TARGET")"

echo "========================================================"
echo " [BLUE/GREEN] Rollout von Version $NEW_TAG"
echo " Aktive Umgebung:  $ACTIVE"
echo " Ziel-Umgebung:    $TARGET (inaktiv)"
echo "========================================================"

echo ""
echo "[Schritt 1/4] Neue Version in die inaktive Umgebung ($TARGET) ausrollen"
IMAGE_TAG="$NEW_TAG" \
EMPLOYEE_PORT="$TARGET_EMPLOYEE_PORT" \
TICKET_PORT="$TARGET_TICKET_PORT" \
  docker compose -f "$COMPOSE_FILE" -p "$(project_of "$TARGET")" up -d

echo ""
echo "[Schritt 2/4] Smoke Test gegen die inaktive Umgebung (kein Nutzer-Traffic)"
if ! bash ci/smoke-test.sh "http://localhost:${TARGET_EMPLOYEE_PORT}" "http://localhost:${TARGET_TICKET_PORT}"; then
  echo ""
  echo "[-] Smoke Test fehlgeschlagen: es wird NICHT umgeschaltet."
  echo "    Die aktive Umgebung ($ACTIVE) bedient weiterhin allen Traffic (Zero Downtime)."
  exit 1
fi

echo ""
echo "[Schritt 3/4] Traffic auf $TARGET umschalten"
switch_traffic_to "$TARGET"
echo "[+] Proxy leitet jetzt auf $TARGET."

echo ""
echo "[Schritt 4/4] Smoke Test ueber den Proxy"
if ! bash ci/smoke-test.sh "http://localhost:${PROXY_EMPLOYEE_PORT}" "http://localhost:${PROXY_TICKET_PORT}"; then
  echo "[-] Fehler nach dem Umschalten, schalte zurueck auf $ACTIVE"
  [ "$ACTIVE" != "none" ] && switch_traffic_to "$ACTIVE"
  exit 1
fi

echo ""
echo "========================================================"
echo " BLUE/GREEN ROLLOUT ERFOLGREICH"
echo " Aktiv: $TARGET (Version $NEW_TAG)"
if [ "$ACTIVE" != "none" ]; then
  echo " Vorherige Umgebung $ACTIVE laeuft weiter -> sofortiger Rollback moeglich:"
  echo "   bash ci/bluegreen-switch.sh $ACTIVE"
fi
echo " Zugriff ueber Proxy: http://localhost:${PROXY_EMPLOYEE_PORT} / http://localhost:${PROXY_TICKET_PORT}"
echo "========================================================"

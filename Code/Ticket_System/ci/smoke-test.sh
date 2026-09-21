#!/usr/bin/env bash
# ==============================================================================
# Smoke Test Skript fuer P4 Continuous Deployment
# Validiert Health Checks und Kernfunktionen der Microservices nach dem Rollout.
#
# Aufruf:
#   bash ci/smoke-test.sh [EMPLOYEE_URL] [TICKET_URL]
#   Beispiel: bash ci/smoke-test.sh http://localhost:8081 http://localhost:8082
# ==============================================================================

set -euo pipefail

EMPLOYEE_URL="${1:-${EMPLOYEE_URL:-http://localhost:8081}}"
TICKET_URL="${2:-${TICKET_URL:-http://localhost:8082}}"
MAX_RETRIES=30
RETRY_INTERVAL=2

echo "========================================================"
echo " Starting CD Smoke Tests"
echo " Target employee-service: $EMPLOYEE_URL"
echo " Target ticket-service:   $TICKET_URL"
echo "========================================================"

wait_for_service() {
  local service_name="$1"
  local health_url="$2"
  echo -n "Checking health for $service_name ($health_url)... "

  for i in $(seq 1 "$MAX_RETRIES"); do
    if response=$(curl -s -f -m 5 "$health_url" 2>/dev/null); then
      if echo "$response" | grep -q '"status":"UP"'; then
        echo " OK (Attempt $i)"
        return 0
      fi
    fi
    sleep "$RETRY_INTERVAL"
  done

  echo " FAILED after $MAX_RETRIES attempts!"
  return 1
}

# 1. Health Checks
echo ""
echo "[Step 1/4] Health Check Validierung (Spring Boot Actuator)"
wait_for_service "employee-service" "$EMPLOYEE_URL/actuator/health"
wait_for_service "ticket-service" "$TICKET_URL/actuator/health"

# 2. Employee API Test (Create & Read)
echo ""
echo "[Step 2/4] Employee API Smoke Test"
EMPLOYEE_PAYLOAD='{"firstName":"Smoke","lastName":"Tester","joinedDate":"2026-01-01"}'

CREATE_EMP_RES=$(curl -s -w "\n%{http_code}" -X POST "$EMPLOYEE_URL/api/employees" \
  -H "Content-Type: application/json" \
  -d "$EMPLOYEE_PAYLOAD")

HTTP_STATUS=$(echo "$CREATE_EMP_RES" | tail -n1)
EMP_BODY=$(echo "$CREATE_EMP_RES" | sed '$d')

if [ "$HTTP_STATUS" -ne 200 ] && [ "$HTTP_STATUS" -ne 201 ]; then
  echo "[-] FAILED: POST /api/employees returned HTTP $HTTP_STATUS"
  echo "$EMP_BODY"
  exit 1
fi

EMPLOYEE_ID=$(echo "$EMP_BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
echo "[+] Created test employee with ID: $EMPLOYEE_ID (HTTP $HTTP_STATUS)"

GET_EMP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$EMPLOYEE_URL/api/employees/$EMPLOYEE_ID")
if [ "$GET_EMP_STATUS" -ne 200 ]; then
  echo "[-] FAILED: GET /api/employees/$EMPLOYEE_ID returned HTTP $GET_EMP_STATUS"
  exit 1
fi
echo "[+] Verified employee retrieval (HTTP $GET_EMP_STATUS)"

# 3. Ticket API Test & Inter-Service Communication
echo ""
echo "[Step 3/4] Ticket API & Inter-Service Communication Smoke Test"
TICKET_PAYLOAD="{\"title\":\"CD Rollout Validation\",\"description\":\"Testing automated deployment pipeline\",\"assignedEmployeeId\":$EMPLOYEE_ID}"

CREATE_TICKET_RES=$(curl -s -w "\n%{http_code}" -X POST "$TICKET_URL/api/tickets" \
  -H "Content-Type: application/json" \
  -d "$TICKET_PAYLOAD")

TICKET_STATUS=$(echo "$CREATE_TICKET_RES" | tail -n1)
TICKET_BODY=$(echo "$CREATE_TICKET_RES" | sed '$d')

if [ "$TICKET_STATUS" -ne 200 ] && [ "$TICKET_STATUS" -ne 201 ]; then
  echo "[-] FAILED: POST /api/tickets returned HTTP $TICKET_STATUS"
  echo "$TICKET_BODY"
  exit 1
fi

TICKET_ID=$(echo "$TICKET_BODY" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)
echo "[+] Created test ticket with ID: $TICKET_ID assigned to Employee $EMPLOYEE_ID (HTTP $TICKET_STATUS)"

GET_TICKET_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$TICKET_URL/api/tickets/$TICKET_ID")
if [ "$GET_TICKET_STATUS" -ne 200 ]; then
  echo "[-] FAILED: GET /api/tickets/$TICKET_ID returned HTTP $GET_TICKET_STATUS"
  exit 1
fi
echo "[+] Verified ticket retrieval (HTTP $GET_TICKET_STATUS)"

# 4. Summary
echo ""
echo "========================================================"
echo " SMOKE TESTS PASSED SUCCESSFULLY!"
echo " All services healthy and functional."
echo "========================================================"
exit 0

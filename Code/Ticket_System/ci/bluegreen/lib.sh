# Gemeinsame Funktionen fuer das Blue/Green Deployment.
# Wird von bluegreen-deploy.sh und bluegreen-switch.sh eingebunden.

COMPOSE_FILE="docker-compose.release.yml"
PROXY_NAME="ticket-system-proxy"
STATE_FILE="ci/bluegreen/.active-color"
TEMPLATE="ci/bluegreen/nginx.conf.template"
GENERATED_CONF="ci/bluegreen/nginx.active.conf"

# Oeffentliche Ports des Proxys (der Nutzer spricht immer diese an)
PROXY_EMPLOYEE_PORT="${PROXY_EMPLOYEE_PORT:-18081}"
PROXY_TICKET_PORT="${PROXY_TICKET_PORT:-18082}"

# Interne Ports pro Farbe, damit beide Umgebungen parallel laufen koennen
blue_employee_port=8081
blue_ticket_port=8082
green_employee_port=9081
green_ticket_port=9082

project_of() { echo "ticket-system-$1"; }

employee_port_of() {
  if [ "$1" = "blue" ]; then echo "$blue_employee_port"; else echo "$green_employee_port"; fi
}

ticket_port_of() {
  if [ "$1" = "blue" ]; then echo "$blue_ticket_port"; else echo "$green_ticket_port"; fi
}

active_color() {
  if [ -f "$STATE_FILE" ]; then cat "$STATE_FILE"; else echo "none"; fi
}

inactive_color() {
  if [ "$(active_color)" = "blue" ]; then echo "green"; else echo "blue"; fi
}

# Proxy starten, falls er noch nicht laeuft
ensure_proxy() {
  if [ -n "$(docker ps -q -f "name=^${PROXY_NAME}$")" ]; then
    return 0
  fi
  docker rm -f "$PROXY_NAME" > /dev/null 2>&1 || true
  echo "--> Starte Reverse Proxy (nginx) auf Port ${PROXY_EMPLOYEE_PORT}/${PROXY_TICKET_PORT}"
  docker run -d --name "$PROXY_NAME" \
    -p "${PROXY_EMPLOYEE_PORT}:80" \
    -p "${PROXY_TICKET_PORT}:81" \
    nginx:1.27-alpine > /dev/null
}

# Traffic auf die angegebene Farbe umschalten (nginx-Konfiguration + Reload)
switch_traffic_to() {
  local color="$1"
  local network
  network="$(project_of "$color")_default"

  ensure_proxy
  # Proxy muss im Netzwerk der Ziel-Umgebung sein, um die Container zu erreichen
  docker network connect "$network" "$PROXY_NAME" > /dev/null 2>&1 || true

  sed "s/ACTIVE_COLOR/${color}/g" "$TEMPLATE" > "$GENERATED_CONF"
  docker cp "$GENERATED_CONF" "${PROXY_NAME}:/etc/nginx/nginx.conf" > /dev/null
  docker exec "$PROXY_NAME" nginx -s reload > /dev/null 2>&1 ||
    docker restart "$PROXY_NAME" > /dev/null

  echo "$color" > "$STATE_FILE"
}

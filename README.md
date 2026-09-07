# M324 - Ticket System

## Projektbeschreibung

Im Rahmen des Moduls **Application Engineering (M324)** entwickeln wir eine framework-basierte Webapplikation (Ticketing-System), die den gesamten Software Development Lifecycle von der Anforderungsaufnahme bis hin zum Deployment in eine Abnahmeumgebung durchläuft.

Das Projekt verfolgt folgende Schwerpunkte:

* **Anforderungsmanagement & Teamarbeit:** Anforderungen werden in User Stories und Issues dokumentiert sowie mit Akzeptanzkriterien versehen. Jeder Entwicklungsschritt ist nachvollziehbar, indem Commits mit Issues verknüpft und Pull Requests mit Peer Reviews genutzt werden.
* **Lokale Entwicklungsumgebung & Automatisierung:** Die Entwicklungsumgebung wird mit Build-Tools, Testframeworks und Containerisierung konfiguriert. Abhängigkeiten werden über Maven verwaltet.
* **Sourcecode- & Artefaktverwaltung:** Der Code wird nach einem klaren Git-Workflow (Feature-Branches) organisiert und versioniert.
* **Continuous Integration (CI):** Durch eine Build-Pipeline wird der Code bei jedem Pull Request automatisch gebaut, getestet und auf Qualität geprüft, um Fehler frühzeitig zu minimieren.
* **Continuous Deployment (CD):** Die Anwendung wird über automatisierten Prozess (Infrastructure as Code, Containerisierung mit Docker) für die Zielumgebung aufbereitet.

**Nutzen des Projekts:** Das Projekt zeigt praxisnah, wie durch automatisierte CI/CD-Prozesse ein schnelleres Feedback erreicht, die Codequalität gesteigert, Risiken minimiert und eine saubere Nachvollziehbarkeit gewährleistet werden.

---

## Technologien & Tools

### Tech-Stack
* **Sprache & Framework:** Java 21 / Spring Boot 4.1.0
* **Build-Tool & Dependency Management:** Maven
* **Datenbank:** PostgreSQL (in Docker containerisiert)
* **Testing:** JUnit 5 & Mockito

### Tools & Infrastruktur
* **Projektmanagement:** GitHub Issue Board (Kanban)
* **CI/CD Pipeline:** GitHub Actions
* **Containerisierung:** Docker & Docker Compose
* **API-Testing:** IntelliJ HTTP Client (`requests.http`) & Postman

---

## Lokale Ausführung & Testing

### 1. Lokale Infrastruktur starten (Datenbank)
Die beiden Microservices verwenden getrennte PostgreSQL-Datenbanken (`employee_db` und `ticket_db`), welche über Docker Compose bereitgestellt werden:
```bash
cd Code/Ticket_System
docker compose up -d
```
> [!NOTE]
> Die Datenbank läuft auf Port **5433** und initialisiert automatisch die Tabellen sowie Testdaten über das Skript `init-scripts/01-init.sql`.

### 2. Tests ausführen (Unit- vs. Integrationstests)
Die Testsuite ist klar in isolierte Unit-Tests und datenbankgestützte Integrationstests unterteilt:

* **Unit-Tests (100% Offline / ohne laufende Datenbank):**
  - Enthalten in den Testklassen `EmployeeControllerTest`, `EmployeeServiceTest`, `TicketControllerTest` und `TicketServiceTest`.
  - Nutzen **Mockito** und **MockMvc** im Standalone-Setup. Sie laufen blitzschnell und sind völlig unabhängig von externen Systemen, Docker oder dem Netzwerk.
  - Können jederzeit ohne Vorbedingungen ausgeführt werden:
    ```bash
    mvn test -Dtest=*ControllerTest,*ServiceTest
    ```

* **Integrationstests (Benötigen laufende PostgreSQL-Instanz):**
  - Enthalten in `EmployeeIntegrationTest` und `TicketIntegrationTest`.
  - Fahren den realen Spring-Boot-Kontext hoch und testen die Persistierung in der echten PostgreSQL-Datenbank.
  - **Wichtig:** Diese Tests setzen voraus, dass die Docker-Datenbank auf Port 5433 läuft (`docker compose up -d`).
  - Ausführung aller Tests (inklusive Integrationstests):
    ```bash
    mvn test
    ```

### 3. Microservices lokal starten
* **Employee Service:** `ch.tbz.employeeservice.EmployeeServiceApplication` auf Port `8081`
* **Ticket Service:** `ch.tbz.ticketservice.TicketServiceApplication` auf Port `8082`

### 4. Manuelle Testdurchführung
In der Datei [requests.http](Code/Ticket_System/requests.http) sind 13 vorkonfigurierte HTTP-Aufrufe (Happy- und Sad-Paths) enthalten, die direkt über den IntelliJ HTTP Client per Klick ausgeführt werden können.

---

### Verlinkungen

### [Zeitlogging](https://docs.google.com/spreadsheets/d/17e9LMVPYqv1bTdXw913_RSP1YrC4l1ToeMtOxQgsptQ/edit?usp=sharing) 

### [Kanban Board](https://github.com/users/yaracorder0/projects/1)

### [Theorieblöcke](Theorie)

### [Dokumentation](Dokumentation)

---

### Lernjournal
- [Anik](https://github.com/Annniiikkk/M324_Lernjournal)
- [Katarina](https://docs.google.com/document/d/1el_p7bzdW--ECTEN-wacaIZpOr6AKe6Lbfxn2Tso7wE/edit?usp=sharing)
- [Yara](https://github.com/yaracorder0/m324_lernjournal)

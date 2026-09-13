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

### 2. Tests ausführen (Unit-, Integrations- und Systemtests)
Die Testsuite ist in drei Stufen unterteilt (Details siehe [P3 Dokumentation](Dokumentation/P3_CI_Variantenvergleich.md#teststufen)). Alle Befehle werden in `Code/Ticket_System` ausgeführt:

* **Unit-Tests (ohne laufende Datenbank, Maven Surefire):**
  - Enthalten in den Testklassen `EmployeeControllerTest`, `EmployeeServiceTest`, `TicketControllerTest` und `TicketServiceTest`.
  - Nutzen **Mockito** und **MockMvc** im Standalone-Setup und sind unabhängig von externen Systemen, Docker oder dem Netzwerk.
    ```bash
    mvn test
    ```

* **Integrationstests (benötigen laufende PostgreSQL-Instanz, Maven Failsafe):**
  - Enthalten in `EmployeeIntegrationTest`, `TicketIntegrationTest` und den `*ApplicationTests`.
  - Fahren den realen Spring-Boot-Kontext hoch und testen die Persistierung in der echten PostgreSQL-Datenbank.
  - **Wichtig:** Die Docker-Datenbank muss auf Port 5433 laufen (`docker compose up -d`).
    ```bash
    mvn verify                  # Unit- und Integrationstests
    mvn verify -DskipUTs=true   # nur Integrationstests
    ```

* **Systemtests (beide Services laufen als JARs):**
  - Enthalten in [system-tests/system-tests.http](Code/Ticket_System/system-tests/system-tests.http), testen das Zusammenspiel beider Services.
  - Voraussetzung: frisch initialisierte Datenbank und gebaute JARs (`mvn package`), benötigt Linux/Git Bash und Docker.
    ```bash
    bash system-tests/run-system-tests.sh
    ```

### 3. Microservices lokal starten
* **Employee Service:** `ch.tbz.employeeservice.EmployeeServiceApplication` auf Port `8081`
* **Ticket Service:** `ch.tbz.ticketservice.TicketServiceApplication` auf Port `8082`

### 4. Manuelle Testdurchführung
In der Datei [requests.http](Code/Ticket_System/requests.http) sind 13 vorkonfigurierte HTTP-Aufrufe (Happy- und Sad-Paths) enthalten, die direkt über den IntelliJ HTTP Client per Klick ausgeführt werden können.

### 5. CI-Pipeline und Docker-Images
* Die CI-Pipeline ([CI.yml](.github/workflows/CI.yml)) baut und testet bei jedem Push auf `feature/**` und `main` sowie bei Pull Requests auf `main`.
* Sind alle Tests erfolgreich, werden auf `main` und bei Release-Tags (`v*`) Docker-Images in die GitHub Container Registry publiziert (Details siehe [P3b Dokumentation](Dokumentation/P3b_Artefakt_Repository.md)).
* Gesamtsystem aus den publizierten Images starten:
  ```bash
  cd Code/Ticket_System
  IMAGE_TAG=latest docker compose -f docker-compose.release.yml up -d
  ```

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

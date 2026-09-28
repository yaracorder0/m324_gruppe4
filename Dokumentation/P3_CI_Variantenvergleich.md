# Dokumentation: CI-Pipeline und Variantenvergleich (V2 vs. V4)

**Projektname:** Ticket_System  
**Modul + Gruppe:** M324_Gruppe_4  
**Auftrag:** P3 – Continuous Integration

---

## Ziel des Dokuments
In diesem Dokument beschreiben wir den Aufbau unserer CI-Pipeline mit GitHub Actions für die zwei Microservices `employee-service` und `ticket-service`.
Es umfasst die Grundlagen von GitHub Actions, die Teststufen, die Kopplung an unsere Branching-Strategie, den Vergleich der zwei umgesetzten Varianten sowie die Begründung, weshalb wir uns für **Variante 2** als finale CI-Pipeline entschieden haben.

---

## Ausgangslage
Unser Ticket_System besteht aus zwei unabhängigen Spring-Boot-Microservices:

| Service | Port | Datenbank |
|---|---|---|
| `employee-service` | 8081 | `employee_db` |
| `ticket-service` | 8082 | `ticket_db` |

Beide Services liegen im **selben Git-Repository** unter `Code/Ticket_System` und teilen sich ein gemeinsames Parent-`pom.xml` (Maven Multi-Module).
Der `ticket-service` ruft zur Laufzeit per REST den `employee-service` auf, um zu prüfen, ob ein Mitarbeiter existiert.
Dadurch stellt sich die Frage, ob beide Services in **einer gemeinsamen Pipeline** oder in **getrennten Pipelines** gebaut und getestet werden sollen.

---

## Grundlagen GitHub Actions
Für die CI verwenden wir GitHub Actions, da unser Repository auf GitHub liegt (siehe [P1](P1_Projektsetup_und_Infrastruktur.md)).

| Begriff | Bedeutung | Bei uns |
|---|---|---|
| **Workflow** | YAML-Datei unter `.github/workflows/`, entspricht einer Pipeline | `CI.yml` (V2) bzw. drei Workflows (V4) |
| **Event / Trigger** (`on:`) | Ereignis, das den Workflow startet | `push`, `pull_request`, `workflow_dispatch` |
| **Job** | Gruppe von Schritten auf einem eigenen, frischen Runner. Jobs laufen standardmässig parallel | ein Job pro Service, ein Job für die Systemtests |
| **Step** | Einzelner Befehl (`run:`) oder eine wiederverwendbare Action (`uses:`) | `mvn ...`, `actions/checkout`, `actions/upload-artifact` |
| **`needs`** | Abhängigkeit zwischen Jobs, der Job startet erst, wenn die anderen erfolgreich waren | Systemtests warten auf beide Service-Jobs |
| **Runner** | Virtuelle Maschine, auf der ein Job läuft | `ubuntu-latest` (inkl. Docker, Java, Python) |
| **Artefakt** | Datei, die ein Job hochlädt, damit andere Jobs oder Personen sie herunterladen können | JARs und Testreports |
| **Cache** | Wiederverwendung von Dateien zwischen Läufen | Maven-Repository (`~/.m2`) über `actions/setup-java` |

**Was gehört wohin?**
- **Eigener Workflow:** Prozesse mit eigenem Auslöser oder eigener Verantwortung (z. B. eine Pipeline pro Service in V4).
- **Eigener Job:** Aufgaben, die parallel laufen können oder eine eigene, saubere Umgebung brauchen (ein Service, die Systemtests).
- **Step:** Aufeinander aufbauende Schritte innerhalb derselben Umgebung (Build → DB starten → Integrationstests).

---

## Betrachtete Varianten
Wir haben uns überlegt, wie die Aufgaben auf Workflows, Jobs und Steps verteilt werden können:

| Variante | Aufbau | Status |
|---|---|---|
| V1 | Ein Workflow, ein Job, der alles nacheinander ausführt | Nur theoretisch betrachtet: keine parallelen und getrennten Resultate pro Service |
| **V2** | Ein Workflow, ein Job pro Service (parallel) + Systemtest-Job | **Umgesetzt** (Branch `feature/P3_CI_V2`), finale Variante |
| V3 | Wie V2, aber als Matrix-Job (`matrix: service: [...]`) | Nur theoretisch betrachtet: weniger YAML, aber Systemtests passen nicht in die Matrix und einzelne Services lassen sich schwerer anpassen |
| **V4** | Ein Workflow pro Service mit Path-Filter + eigener Systemtest-Workflow | **Umgesetzt** (Branch `feature/P3_CI_V4`) |

Umgesetzt und verglichen haben wir die zwei Varianten, die sich grundsätzlich unterscheiden: **alles in einem Workflow (V2)** gegenüber **getrennten Workflows pro Service (V4)**.

---

## Teststufen
Die Tests sind in drei Stufen aufgeteilt. Jede Stufe hat eine eigene Aufgabe, eigene Voraussetzungen und ein eigenes Maven-Plugin bzw. Tool.

| Stufe | Was wird getestet? | Testklassen / Dateien | Tool | Voraussetzung | Befehl |
|---|---|---|---|---|---|
| **Unit-Tests** | Einzelne Klassen (Controller, Service) isoliert mit Mocks | `*ControllerTest`, `*ServiceTest` | JUnit 5, Mockito, Surefire | keine | `mvn -pl <service> -am package` |
| **Integrationstests** | Spring-Kontext, REST-Endpunkte und echte Datenbank innerhalb **eines** Service. Beim `ticket-service` wird der `employee-service` gemockt | `*IntegrationTest`, `*ApplicationTests` | JUnit 5, Spring Boot Test, Failsafe | PostgreSQL | `mvn -pl <service> -am verify -DskipUTs=true` |
| **Systemtests** | Das Gesamtsystem: beide Services laufen als echte JARs und kommunizieren miteinander, nichts ist gemockt | `system-tests/system-tests.http` (10 Tests) | IntelliJ HTTP Client CLI (Docker-Image) | PostgreSQL + beide JARs | `bash system-tests/run-system-tests.sh` |

### Umsetzung der Trennung in Maven
Im Parent-`pom.xml` ist konfiguriert:
- **Surefire** (Phase `test`) schliesst `*IntegrationTest` und `*ApplicationTests` aus und führt nur Unit-Tests aus.
- **Failsafe** (Phasen `integration-test` und `verify`) führt genau diese Klassen aus.
- Mit der Property `-DskipUTs=true` werden die Unit-Tests übersprungen, damit sie in der zweiten Stufe nicht nochmals laufen.

Die Klasse `*ApplicationTests` gehört zu den Integrationstests, da sie den ganzen Spring-Kontext startet und dafür eine Datenbank benötigt.

### Neu umgesetzte Tests: Systemtests
Unit- und Integrationstests wurden bereits in P2 geschrieben. In P3 haben wir zusätzlich **Systemtests** umgesetzt, da bisher kein Test das Zusammenspiel der beiden Services geprüft hat.
Die Integrationstests des `ticket-service` mocken den `employee-service`, ein Fehler in der REST-Kommunikation wäre dort also nicht aufgefallen.

Die Systemtests basieren auf unserer bestehenden `requests.http` aus P2, wurden aber um automatische Prüfungen (`client.test`) ergänzt und werden in der CI extern über das Docker-Image `jetbrains/intellij-http-client` ausgeführt:

| Test | Prüfung |
|---|---|
| ST-01 – ST-05 | `employee-service`: Liste, Anlegen, Abruf per ID (Persistenz), ungültiger Skill-Level (400), unbekannte ID (404) |
| **ST-06** | **Service-übergreifend:** Ticket für den in ST-02 neu angelegten Mitarbeiter erstellen (201). Funktioniert nur, wenn der `ticket-service` den `employee-service` wirklich erreicht |
| ST-07 | Service-übergreifend: Ticket für unbekannten Mitarbeiter wird abgelehnt (400) |
| ST-08 – ST-10 | `ticket-service`: ungültiges Datum, fehlende Zuweisung, neues Ticket in der Liste |

Das Skript `run-system-tests.sh` startet beide JARs, wartet bis beide Services antworten, führt die Tests aus und legt einen JUnit-Report (`system-tests/reports/`) sowie die Service-Logs (`system-tests/logs/`) ab.
Schlägt ein Test fehl, beendet sich der HTTP Client mit Exit-Code 1 und der Job wird rot.

### Integration der Tests in den Build
- Die **Unit-Tests** laufen im selben Schritt wie der Build (`package`). Schlägt ein Unit-Test fehl, wird kein JAR erstellt und die weiteren Schritte werden übersprungen (Fail Fast, ohne unnötig eine Datenbank zu starten).
- Die **Integrationstests** laufen erst, wenn Build und Unit-Tests erfolgreich waren.
- Die **Systemtests** testen genau die JARs, die vorher alle Unit- und Integrationstests bestanden haben (in V2 werden sie aus den Service-Jobs übernommen).
- Test-Reports werden mit `if: always()` auch bei Fehlern hochgeladen, damit ein fehlgeschlagener Lauf analysiert werden kann.

---

## Branching-Strategie und CI-Auslösung
Unsere Branching-Strategie aus [P1](P1_Projektsetup_und_Infrastruktur.md): keine direkten Pushes auf `main`, jede Änderung wird in einem `feature/<name>`-Branch entwickelt und per Pull Request mit Review gemergt.

### Welche Events lösen die Pipeline aus?

| Event | Branch | Zweck |
|---|---|---|
| `push` | `feature/**` | Schnelles Feedback bei jedem Commit während der Entwicklung |
| `pull_request` | Ziel `main` | Prüfung vor dem Merge, Resultat ist im Pull Request sichtbar |
| `push` | `main` | Prüfung nach dem Merge, `main` bleibt nachweislich lauffähig |
| `workflow_dispatch` | beliebig | Manueller Start, z. B. durch die Lehrperson oder für den Laufzeitvergleich ohne Cache |

Branches, die nicht mit `feature/` beginnen (z. B. alte Theorie-Branches), lösen bei einem Push keine Pipeline aus. Sie werden erst beim Pull Request auf `main` geprüft.

### Regeln für `main` (Branch-Ruleset)
Die Regeln sind als **Branch-Ruleset** (*Settings → Rules → Rulesets*) mit dem Ziel "Default Branch" hinterlegt und auf **Active** gesetzt. Der Bypass-Bereich ist leer, die Regeln gelten also für alle Teammitglieder:

| Regel | Einstellung | Wirkung |
|---|---|---|
| Require a pull request before merging | 1 Approval | Direkte Pushes auf `main` sind nicht möglich, jede Änderung wird reviewt |
| Require status checks to pass | `Build & Test employee-service`, `Build & Test ticket-service`, `Systemtests (beide Services)` | Nur Code, der alle drei Teststufen bestanden hat, gelangt auf `main` |
| Block force pushes | aktiv | Die Historie von `main` kann nicht überschrieben werden |
| Restrict deletions | aktiv | `main` kann nicht gelöscht werden |

---

## Variante 2: Ein Workflow mit einem Job pro Service

### Aufbau
Es gibt **eine** Workflow-Datei (`.github/workflows/CI.yml`) mit drei Jobs:

```
CI.yml  (bei jedem Push / PR, ohne Path-Filter)
├── Job: employee-service  →  Build & Unit-Tests → DB → Integrationstests → Upload JAR
├── Job: ticket-service    →  Build & Unit-Tests → DB → Integrationstests → Upload JAR
└── Job: system-tests      (needs: beide Service-Jobs)
                           →  Download beider JARs → DB → Systemtests → Upload Reports
```

### Eigenschaften
- Bei jeder Änderung werden **immer beide Services** gebaut und getestet.
- Die Service-Jobs laufen parallel, die Resultate sind pro Service getrennt sichtbar.
- Die Systemtests starten nur, wenn beide Service-Jobs erfolgreich waren, und verwenden **deren JARs weiter**. Es wird also nichts doppelt gebaut.

---

## Variante 4: Ein Workflow pro Service mit Path-Filter

### Aufbau
Es gibt **drei** Workflow-Dateien:

```
employee-service.yml  (nur bei Änderungen in employee-service/** oder gemeinsamen Dateien)
└── Job: build-and-test  →  Build & Unit-Tests → DB → Integrationstests → Upload JAR

ticket-service.yml    (nur bei Änderungen in ticket-service/** oder gemeinsamen Dateien)
└── Job: build-and-test  →  Build & Unit-Tests → DB → Integrationstests → Upload JAR

system-tests.yml      (bei jeder Änderung in Code/Ticket_System/**)
└── Job: system-tests    →  Build beider Services (ohne Tests) → DB → Systemtests → Upload Reports
```

Gemeinsame Dateien im Path-Filter: `pom.xml`, `docker-compose.yml`, `init-scripts/**`, `ci/**` und die Workflow-Datei selbst.

### Eigenschaften
- Die Service-Workflows bauen nur den Service, der sich tatsächlich geändert hat.
- Jeder Service hat eine vollständig eigenständige Pipeline.
- Die Systemtests passen in keinen der Service-Workflows, da sie beide Services brauchen. Sie benötigen einen **dritten Workflow**.
- Artefakte aus anderen Workflows sind nicht direkt verfügbar. Der Systemtest-Workflow muss **beide Services nochmals bauen** und kann nicht auf die Service-Workflows warten.

---

## Vergleich

| Kriterium | Variante 2 | Variante 4 |
|---|---|---|
| Anzahl Workflow-Dateien | 1 | 3 (zwei davon fast identisch) |
| Wann wird gebaut? | Immer beide Services | Nur der geänderte Service (+ Systemtests immer) |
| Parallele Ausführung | Ja (zwei Service-Jobs) | Ja (drei Workflows) |
| Getrennte Resultate pro Service | Ja | Ja |
| Systemtests | Eigener Job mit `needs`, verwendet die getesteten JARs | Eigener Workflow, baut beide Services nochmals |
| Reihenfolge Unit/Integration → System | Garantiert durch `needs` | Nicht garantiert, Systemtests laufen parallel zu den Service-Tests |
| Kompatibel mit Branch Protection (Required Checks) | Ja, ohne Zusatzaufwand | Problematisch (siehe Argument 2) |
| Risiko, gemeinsame Dateien zu übersehen | Keines | Vorhanden, Path-Filter müssen gepflegt werden |
| Übersicht für die Lehrperson | Ein Workflow, ein Lauf | Drei Workflows, getrennte Läufe |
| Wartungsaufwand | Gering | Höher (Duplikate, Filter) |
| Eingesparte Laufzeit | – | Gering, da die Systemtests ohnehin beide Services bauen |
| Erweiterbarkeit für P3b | Publish-Job mit `needs: system-tests` ergänzen | Publish-Step pro Workflow ergänzen, ohne Garantie, dass die Systemtests grün waren |

### Gemessene Laufzeiten
Die folgenden Werte werden aus den Logs unserer eigenen Pipeline-Läufe im Actions-Tab übernommen.

| Szenario                           | Variante 2 | Variante 4 |
|------------------------------------|------------|------------|
| Änderung nur im `employee-service` | 54s        | 1m 2s      |
| Änderung nur im `ticket-service`   | 50s        | 1m 10s     |
| Systemtests                       | 1m 11s     | 1m 21s     |


Links zu den Pipeline-Läufen:
https://github.com/yaracorder0/m324_gruppe4/actions

---

## Argumentation: Weshalb Variante 2 für uns besser ist

- **Projektgrösse:** Der Hauptvorteil von V4 ist, dass unveränderte Services nicht neu gebaut werden. Bei nur zwei kleinen Services ist die Ersparnis gering, zumal der Systemtest-Workflow in V4 ohnehin beide Services baut. In V2 laufen die Service-Jobs parallel.
- **Branching-Strategie:** Unsere Required Status Checks funktionieren in V2 ohne Zusatzaufwand, weil immer alle drei Jobs laufen. In V4 startet der Workflow des unveränderten Service wegen des Path-Filters nicht, der Check bleibt "Pending" und blockiert den Merge.
- **Gemeinsame Abhängigkeiten:** Beide Services teilen Parent-`pom.xml`, `docker-compose.yml`, Init-Skript und CI-Hilfsskript. In V4 müssen all diese Pfade in beiden Path-Filtern gepflegt werden. Ein vergessener Pfad bedeutet, dass eine Änderung ungetestet auf `main` gelangt. V2 baut immer beides.
- **Systemtests:** Sie brauchen beide Services. In V2 wartet ein zusätzlicher Job mit `needs` und testet genau die JARs, die alle vorherigen Tests bestanden haben. In V4 braucht es einen dritten Workflow, der beide Services erneut baut und nicht garantieren kann, dass die Service-Tests grün waren.
- **Wartungsaufwand:** V4 besteht aus drei Workflow-Dateien, zwei davon bis auf den Service-Namen identisch. Jede Anpassung muss mehrfach gemacht werden, und die Dateien laufen mit der Zeit auseinander. In V2 liegt die gesamte CI-Logik in einer Datei.
- **Nachvollziehbarkeit:** Die Lehrperson startet in V2 einen einzigen Workflow (`workflow_dispatch`) und sieht in einem Lauf die Resultate aller Teststufen. In V4 müssten drei Workflows einzeln gestartet und ausgewertet werden.

---

## Gegenargumente und deren Bewertung
Variante 4 hat auch klare Vorteile, die wir bewusst abgewogen haben:

| Vorteil von Variante 4 | Unsere Bewertung |
|---|---|
| Services sind vollständig unabhängig, wie es bei Microservices üblich ist | Richtig, allerdings liegen unsere Services im selben Repository und teilen sich ein Parent-POM. Eine echte Unabhängigkeit ist dadurch ohnehin nicht gegeben. |
| Unveränderte Services werden nicht gebaut, was Runner-Minuten spart | Bei unserer Projektgrösse ist die Ersparnis gering, und der Systemtest-Workflow baut ohnehin beide Services. Die Sicherheit, dass immer alles getestet wird, ist uns wichtiger. |
| Jeder Service kann in P3b / CD unabhängig versioniert und ausgeliefert werden | Das ist auch in Variante 2 möglich, da jeder Service einen eigenen Job und ein eigenes Artefakt hat. |

---

## Vorbereitung für P3b
Die Pipeline erzeugt pro Service ein klares Build-Ergebnis:
- Das ausführbare Spring-Boot-JAR (`<service>-0.0.1-SNAPSHOT.jar`) wird als Artefakt `<service>-jar-<commit-sha>` hochgeladen.
- Über den Commit-SHA im Namen ist jedes JAR eindeutig einem Commit und damit einem Pipeline-Lauf zugeordnet.
- Fehlt das JAR, schlägt der Upload fehl (`if-no-files-found: error`).

In P3b wird in Variante 2 ein zusätzlicher Publish-Job mit `needs: system-tests` ergänzt, der die JARs mit einer richtigen Versionsnummer in ein Artefakt-Repository publiziert. So werden nur Artefakte publiziert, die alle drei Teststufen bestanden haben.

---

## Entscheidung
Wir setzen **Variante 2** als finale CI-Pipeline ein.

Variante 2 bietet uns parallele und pro Service getrennte Resultate, eine garantierte Reihenfolge der Teststufen bis zu den Systemtests, funktioniert ohne Workarounds mit unserer Branch Protection, testet gemeinsame Abhängigkeiten immer mit und ist für ein Zweierteam einfach zu warten.
Die Vorteile von Variante 4 kommen erst bei grösseren Projekten, mehr Services oder getrennten Repositories zum Tragen.

Sollte das Projekt in Zukunft wachsen (z. B. weitere Services oder deutlich längere Build-Zeiten), wäre ein Wechsel auf Variante 4 sinnvoll.

### Gruppen Schlussfolgerung nach Auswertung der Pipeline-Läufe:

Wir haben uns für V2 entschieden, da die Laufzeit im Vergleich zu V4 deutlich kürzer ist. Ein weiteres Kriterium war der Implementierungs- und Anpassungsaufwand der Pipelines, der bei V2 wesentlich geringer ausfällt. Für den Rahmen unseres Schulprojekts bietet V2 somit die optimal passende Lösung.

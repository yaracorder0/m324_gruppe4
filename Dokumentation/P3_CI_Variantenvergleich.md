# Dokumentation: CI-Variantenvergleich (V2 vs. V4)

**Projektname:** Ticket_System  
**Modul + Gruppe:** M324_Gruppe_4  
**Auftrag:** P3 – Continuous Integration

---

## Ziel des Dokuments
In diesem Dokument vergleichen wir zwei Varianten, wie die CI-Pipeline für unsere zwei Microservices (`employee-service` und `ticket-service`) mit GitHub Actions strukturiert werden kann.
Wir zeigen die Vor- und Nachteile beider Varianten auf und begründen, weshalb wir uns für **Variante 2** als finale CI-Pipeline entschieden haben.

---

## Ausgangslage
Unser Ticket_System besteht aus zwei unabhängigen Spring-Boot-Microservices:

| Service | Port | Datenbank |
|---|---|---|
| `employee-service` | 8081 | `employee_db` |
| `ticket-service` | 8082 | `ticket_db` |

Beide Services liegen im **selben Git-Repository** unter `Code/Ticket_System` und teilen sich ein gemeinsames Parent-`pom.xml` (Maven Multi-Module).
Dadurch stellt sich die Frage, ob beide Services in **einer gemeinsamen Pipeline** oder in **getrennten Pipelines** gebaut und getestet werden sollen.

Für beide Varianten gelten dieselben Grundbausteine:
- Java 21 (Temurin) mit Maven-Caching über `actions/setup-java`
- PostgreSQL wird über `docker compose up -d --wait` inkl. Init-Skript gestartet
- Build und Tests pro Service über `mvn -B -pl <service> -am verify`
- Die gebauten JARs werden pro Service als Artefakt hochgeladen (Vorbereitung für P3b)

---

## Variante 2: Ein Workflow mit einem Job pro Service

### Aufbau
Es gibt **eine** Workflow-Datei (`ci.yml`). Darin laufen zwei Jobs **parallel**: einer für den `employee-service` und einer für den `ticket-service`.
Der Workflow wird bei jedem Push auf `main` oder `feature/**`, bei jedem Pull Request auf `main` sowie manuell (`workflow_dispatch`) ausgelöst.

```
ci.yml
├── Job: employee-service  →  Build & Test  →  Upload JAR
└── Job: ticket-service    →  Build & Test  →  Upload JAR
```

### Eigenschaften
- Bei jeder Änderung werden **immer beide Services** gebaut und getestet.
- Die Jobs laufen parallel, die Resultate sind pro Service getrennt sichtbar.
- Schlägt ein Service fehl, läuft der andere trotzdem weiter und zeigt sein eigenes Resultat.

---

## Variante 4: Ein Workflow pro Service mit Path-Filter

### Aufbau
Es gibt **zwei** Workflow-Dateien (`employee-service.yml` und `ticket-service.yml`).
Jeder Workflow wird über einen `paths`-Filter nur dann ausgelöst, wenn sich Dateien im jeweiligen Service-Ordner (oder im Parent-`pom.xml`) ändern.

```
employee-service.yml  (nur bei Änderungen in employee-service/** oder pom.xml)
└── Job: build-and-test  →  Upload JAR

ticket-service.yml    (nur bei Änderungen in ticket-service/** oder pom.xml)
└── Job: build-and-test  →  Upload JAR
```

### Eigenschaften
- Es wird nur der Service gebaut, der sich tatsächlich geändert hat.
- Jeder Service hat eine vollständig eigenständige Pipeline.
- Dies entspricht dem Vorgehen in grösseren Microservice-Architekturen, in denen Services unabhängig voneinander entwickelt und ausgeliefert werden.

---

## Vergleich

| Kriterium | Variante 2 | Variante 4 |
|---|---|---|
| Anzahl Workflow-Dateien | 1 | 2 (fast identisch) |
| Wann wird gebaut? | Immer beide Services | Nur der geänderte Service |
| Parallele Ausführung | Ja (zwei Jobs) | Ja (zwei Workflows) |
| Getrennte Resultate pro Service | Ja | Ja |
| Kompatibel mit Branch Protection (Required Checks) | Ja, ohne Zusatzaufwand | Problematisch (siehe Argument 2) |
| Risiko, Änderungen am Parent-POM zu übersehen | Keines | Vorhanden, Path-Filter muss gepflegt werden |
| Übersicht für die Lehrperson | Ein Workflow, ein Lauf | Zwei Workflows, getrennte Läufe |
| Wartungsaufwand | Gering | Höher (Duplikate, Filter) |
| Eingesparte Laufzeit | – | Gering, da unsere Builds nur wenige Minuten dauern |
| Erweiterbarkeit für P3b | Publish-Job pro Service ergänzen | Publish-Step pro Workflow ergänzen |

### Gemessene Laufzeiten
> Die folgenden Werte werden aus den Logs unserer eigenen Pipeline-Läufe im Actions-Tab übernommen.

| Szenario | Variante 2 | Variante 4 |
|---|---|---|
| Änderung nur im `employee-service` | _TODO_ | _TODO_ |
| Änderung nur im `ticket-service` | _TODO_ | _TODO_ |
| Änderung am Parent-`pom.xml` | _TODO_ | _TODO_ |
| Lauf ohne Maven-Cache | _TODO_ | _TODO_ |
| Lauf mit Maven-Cache | _TODO_ | _TODO_ |

Links zu den Pipeline-Läufen: _TODO_

---

## Argumentation: Weshalb Variante 2 für uns besser ist

### Argument 1: Die Grösse unseres Projekts rechtfertigt keine getrennten Pipelines
Der grösste Vorteil von Variante 4 ist, dass unveränderte Services nicht neu gebaut werden. Dieser Vorteil lohnt sich vor allem bei grossen Systemen mit vielen Services und langen Build-Zeiten.
Unser Projekt besteht aus nur zwei kleinen Services, deren Build und Tests nur wenige Minuten dauern. Die Zeitersparnis durch Path-Filter ist daher gering.
Gleichzeitig laufen die beiden Jobs in Variante 2 parallel, sodass die Gesamtdauer ungefähr der Dauer des langsameren Services entspricht.

### Argument 2: Variante 2 funktioniert sauber mit unserer Branching-Strategie
Gemäss unserer Branching-Strategie (siehe [P1](P1_Projektsetup_und_Infrastruktur.md)) sind keine direkten Pushes auf `main` erlaubt und jede Änderung wird über einen Pull Request integriert.
Damit ein Pull Request nur bei erfolgreicher Pipeline gemergt werden kann, definieren wir die CI-Jobs als **Required Status Checks** in der Branch Protection.

Bei Variante 4 entsteht hier ein Problem: Ändert ein Pull Request nur einen Service, wird der Workflow des anderen Services wegen des Path-Filters gar nicht gestartet.
Ist dieser Check als "required" markiert, bleibt er im Status **"Pending"** hängen und der Pull Request kann nicht gemergt werden.
Das liesse sich nur mit zusätzlichen Workarounds lösen (z. B. Dummy-Workflows oder ein zusätzlicher Sammel-Job), was die Pipeline komplizierter macht.

Bei Variante 2 laufen immer beide Jobs, womit beide ohne Zusatzaufwand als Required Checks verwendet werden können.

### Argument 3: Gemeinsame Abhängigkeiten werden immer mitgetestet
Beide Services teilen sich das Parent-`pom.xml` (Spring-Boot-Version, Java-Version, Module).
Bei Variante 4 muss dieses File in beiden Path-Filtern eingetragen sein. Kommen später weitere gemeinsame Dateien hinzu (z. B. `docker-compose.yml`, `init-scripts/` oder die Workflow-Datei selbst), müssen die Filter jedes Mal manuell angepasst werden.
Wird ein Pfad vergessen, wird eine Änderung nicht getestet und ein Fehler gelangt unbemerkt auf `main`.

Variante 2 baut immer beide Services und ist daher gegen solche Fehler geschützt. Das ist genau das Ziel von Continuous Integration: Fehler so früh wie möglich erkennen.

### Argument 4: Weniger Wartungsaufwand für ein Zweierteam
Die beiden Workflow-Dateien von Variante 4 sind bis auf den Service-Namen identisch.
Jede Anpassung (z. B. neue Java-Version, zusätzlicher Test-Schritt, Publish-Schritt in P3b) muss in beiden Dateien gemacht werden. Dabei besteht die Gefahr, dass die Dateien mit der Zeit auseinanderlaufen.
Bei Variante 2 befindet sich die gesamte CI-Logik in einer Datei, was für ein kleines Team übersichtlicher und einfacher zu reviewen ist.

### Argument 5: Bessere Nachvollziehbarkeit für die Lehrperson
Die Lehrperson muss vollen Zugriff auf unsere Pipelines haben und diese ausführen können.
Bei Variante 2 gibt es einen einzigen Workflow, der über `workflow_dispatch` manuell gestartet werden kann und in einem Lauf die Resultate beider Services zeigt.
Bei Variante 4 müssten zwei Workflows gesucht, einzeln gestartet und separat ausgewertet werden.

---

## Gegenargumente und deren Bewertung
Variante 4 hat auch klare Vorteile, die wir bewusst abgewogen haben:

| Vorteil von Variante 4 | Unsere Bewertung |
|---|---|
| Services sind vollständig unabhängig, wie es bei Microservices üblich ist | Richtig, allerdings liegen unsere Services im selben Repository und teilen sich ein Parent-POM. Eine echte Unabhängigkeit ist dadurch ohnehin nicht gegeben. |
| Unveränderte Services werden nicht gebaut, was Runner-Minuten spart | Bei unserer Projektgrösse ist die Ersparnis gering. Die Sicherheit, dass immer alles getestet wird, ist uns wichtiger. |
| Jeder Service kann in P3b / CD unabhängig versioniert und ausgeliefert werden | Das ist auch in Variante 2 möglich, da jeder Service einen eigenen Job und ein eigenes Artefakt hat. |

---

## Entscheidung
Wir setzen **Variante 2** als finale CI-Pipeline ein.

Variante 2 bietet uns parallele und pro Service getrennte Resultate, funktioniert ohne Workarounds mit unserer Branch Protection, testet gemeinsame Abhängigkeiten immer mit und ist für ein Zweierteam einfach zu warten.
Die Vorteile von Variante 4 kommen erst bei grösseren Projekten, mehr Services oder getrennten Repositories zum Tragen.

Sollte das Projekt in Zukunft wachsen (z. B. weitere Services oder deutlich längere Build-Zeiten), wäre ein Wechsel auf Variante 4 sinnvoll.

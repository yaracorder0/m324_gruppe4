# KI-Nachweis

**Projektname:** Ticket_System  
**Modul + Gruppe:** M324_Gruppe_4

---

### Auftrag
Initialisierung der Projektumgebung,Festlegung der Technologien sowie Festhaltung der Entscheidungen in einer Dokumentation für P1.

### KI-Tool
Keines (Für die Technologie- und Infrastrukturentscheidung wurde keine KI verwendet).

### Zweck der Nutzung
Entfällt, da für die Entscheidungen an Tag 01 keine KI eingesetzt wurde.

### Eigener Startplan ohne KI
Unser Team hat die Technologie- und Infrastrukturentscheidung vollständig eigenständig diskutiert und getroffen:
* **Projektart:** Pure Backend Web-API (ohne Frontend) für ein Ticket-System.
* **Technologie-Stack:** **Java 21 mit Spring Boot 4.1.0** und **Maven**, da alle Gruppenmitglieder bereits fundierte Vorkenntnisse mitbringen.
* **Datenbank:** **PostgreSQL** in einem lokalen Docker-Container, um allen eine identische und leicht zurücksetzbare Entwicklungsumgebung zu bieten.
* **Testing & Tools:** **JUnit 5 / Mockito** für Unit Tests, **GitHub Issue Board** als Kanban Board und **GitHub Actions** für die CI/CD-Pipeline.

---

### KI-Nutzung

#### Promptauszug oder Kurzfassung
Keine Prompts verwendet.

#### Wichtigste KI-Antwort
Entfällt.

#### Übernommene Vorschläge
Keine. Alle Entscheidungen basieren auf unserem eigenen Vorwissen und den Team-Diskussionen.

#### Angepasste oder verworfene Vorschläge
Entfällt.

---

### Validierung

#### Prüfung durch Tests, Logs, Pipeline, Review, Screenshot oder Quelle
* Überprüfung der Docker-Compose-Datei sowie der application.properties
* Sichtprüfung der Abhängigkeiten in der Maven `pom.xml` zur Bestätigung, dass JUnit 5 vorhanden ist.

#### Resultat der Prüfung
Das Setup funktioniert lokal einwandfrei und deckt alle Anforderungen für Tag 01 vollständig ab.

#### Korrekturen nach der Prüfung
Keine Korrekturen notwendig.

---

### Eigenleistung

#### Was habe ich selbst entschieden?
Sämtliche Architekturentscheidungen (Sprache, Framework, Datenbank, Containerisierung, Branching-Strategie und CI/CD-Tooling) wurden zu 100 % eigenständig im Team erarbeitet und entschieden.

#### Was kann ich in einem Code-Walkthrough, Pipeline-Walkthrough oder Fachgespräch erklären?
* Warum wir uns für Java/Spring Boot und PostgreSQL entschieden haben und welche Vorteile uns das Teamwissen bringt.
* Wie das Zusammenspiel zwischen Spring Boot, der Datenbank in Docker und den JUnit-Tests funktioniert.
* Wie unsere Branching-Strategie funktioniert und warum wir direkte Pushes auf `main` verhindern.

---

### Reflexion

#### Wo hat KI geholfen?
Bei der Technologieentscheidung an Tag 01 wurde keine KI genutzt.

#### Wo war KI falsch, unvollständig oder zu allgemein?
Entfällt.

#### Was habe ich durch den KI-Einsatz fachlich gelernt?
Entfällt.
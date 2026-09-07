# Dokumentation: Technologie- und Infrastrukturentscheidung

**Projektname:** Ticket_System  
**Modul + Gruppe:** M324_Gruppe_4

---

## Ziel des Dokuments
Dieses Dokument beschreibt die vollständige Initialisierung der Projektumgebung für das Modul M324.  
Es umfasst die Technologieentscheidung, die Infrastrukturwahl, die Git‑Struktur, die Nutzung von KI sowie alle Abgabevorgaben für Tag 01.

---

## Technologien
Zu Beginn des Projekts haben wir im Team gemeinsam diskutiert, welche Technologien und Frameworks wir zur Umsetzung des Projekts verwenden wollen.
Wir haben uns dazu entschieden, **Java 21** mit **Spring Boot 4.1.0** und **Maven** statt Gradle einzusetzen, da wir alle schon ausgiebig mit diesen Technologien gearbeitet haben und uns damit am besten auskennen.

Da wir im Verlaufe des Projekts Unit Tests schreiben werden, war für uns die direkte Unterstützung durch ein Testing-Framework entscheidend.
Mit Spring Boot nutzen wir **JUnit 5** und **Mockito**, welche out-of-the-box integriert sind und eine professionelle Testabdeckung unserer API-Endpoints ermöglichen.

### Datenbank & Betreibbarkeit
Für die Datenbank haben wir uns für PostgreSQL entschieden, da wir mit Postgres die meiste Erfahrung haben – mehr als mit MongoDB.

Ein weiteres Thema, das wir im Team diskutiert haben, ist die Art und Weise, wie wir die Datenbank betreiben wollen. Dabei gab es zwei Möglichkeiten:

1. Eine extern gehostete Instanz (z. B. in der Cloud)
2. Eine lokal gehostete Instanz (z. B. mit Docker)

Wir haben uns dazu entschieden, die lokale Variante in diesem Projekt umzusetzen, da sie sich für den Rahmen dieses Projekts am besten eignet.
Dadurch können alle Teammitglieder mit einer identischen Datenbank arbeiten, ohne dass manuelle Installationen nötig sind.
Zusätzlich ist es einfacher, die Datenbank jederzeit neu aufzusetzen.
Ausserdem wollen wir ein Skript erstellen, welches die Testdaten lädt, sodass alle Teammitglieder mit der gleichen Datenbasis arbeiten können.

---

## Tools
* **GitHub Issue Board:**  
  Für die Planung und das Tracking der Aufgaben verwenden wir das in GitHub integrierte Issue Board. Dort werden alle Issues (Stories) mit den dazugehörigen Sub-Tasks verwaltet, was eine effiziente Übersicht ermöglicht.

* **CI/CD Prozesse:**  
  Für die Automatisierung von Build- und Test-Prozessen setzen wir auf **GitHub Actions**. Da unser Repository auf GitHub liegt, lässt sich die CI/CD-Pipeline dort direkt integrieren, um bei jedem Merge Request automatisiert unsere JUnit-Tests auszuführen.

* **Manuelles API-Testing (requests.http):**  
  Für das schnelle, manuelle Testen und Verifizieren der REST-Endpunkte setzen wir auf eine direkt im Repository abgelegte **`requests.http`**-Datei (IntelliJ HTTP Client). Dadurch können alle Teammitglieder vordefinierte Anfragen für alle User Stories (sowohl Happy Paths als auch Sad Paths) direkt per Klick in der IDE ausführen, ohne externe Tools installieren oder konfigurieren zu müssen.

---

## Branching-Strategie

### Grundsätze
- Wir haben uns entschieden, keine direkten Pushes auf `main` zu erlauben.
- Änderungen am Code oder an der Dokumentation werden über Merge Requests integriert, damit keine Konflikte auf `main` entstehen.
- Dieser Grundsatz wird nach dem Abschliessen von P1 umgesetzt.

### Feature-Branches
- Für neue Features wird ein Branch nach folgendem Schema erstellt: `feature/<feature-name>`
- Die Entwicklung des Features oder der Dokumentation erfolgt in diesem Branch.
- Nach Fertigstellung wird ein Merge Request gegen `main` erstellt und nach einem Review sowie dem Approval eines Teammitglieds gemerged.
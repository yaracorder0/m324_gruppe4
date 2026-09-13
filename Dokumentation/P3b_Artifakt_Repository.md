# Dokumentation: Artefakt-Repository

**Projektname:** Ticket_System  
**Modul + Gruppe:** M324_Gruppe_4  
**Auftrag:** P3b – Artefakt-Repository

---

## Ziel des Dokuments
In diesem Dokument beschreiben wir, wie wir unsere CI-Pipeline aus [P3](P3_CI_Variantenvergleich.md) erweitert haben: Die Pipeline baut und testet nicht mehr nur, sondern publiziert zusätzlich pro Microservice ein versioniertes Docker-Image in die **GitHub Container Registry (GHCR)**.
Es umfasst die Wahl des Artefakt-Typs und Repositories, die Namenskonvention und Versionierung, die Pipeline-Integration, Rechte und Secrets, den Nachweis des publizierten Artefakts sowie die Verwendung in P4.

---

## Grundlagen

### Sourcecode, Build-Artefakt und Deployment-Artefakt

| Begriff | Bedeutung | Bei uns |
|---|---|---|
| **Sourcecode** | Der Code im Git-Repository. Muss erst gebaut werden, bevor er ausgeführt werden kann | Java-Klassen, `pom.xml`, `Dockerfile` |
| **Build-Artefakt** | Ergebnis des Build-Prozesses, ausführbar, aber abhängig von der Umgebung (z. B. installiertes Java) | `employee-service-0.0.1-SNAPSHOT.jar`, `ticket-service-0.0.1-SNAPSHOT.jar` |
| **Deployment-Artefakt** | Paket, das ohne weitere Schritte in einer Zielumgebung gestartet werden kann und alles Nötige mitbringt | Docker-Image mit Java-Runtime und JAR |

In unserer Pipeline entsteht zuerst das Build-Artefakt (JAR). Dieses durchläuft alle Teststufen und wird danach unverändert in das Deployment-Artefakt (Docker-Image) verpackt.

### Weshalb Artefakte versioniert und zentral abgelegt werden
- **Build once, deploy many:** Ein Artefakt wird genau einmal gebaut und getestet. Alle Umgebungen (Test, Abnahme, Produktion) verwenden danach dasselbe Artefakt, statt den Code jedes Mal neu zu bauen.
- **Nachvollziehbarkeit:** Über die Version ist klar, welcher Commit in welchem Artefakt steckt und welcher Pipeline-Lauf es erzeugt hat.
- **Reproduzierbarkeit und Rollback:** Ältere Versionen bleiben in der Registry verfügbar. Bei einem Fehler kann auf eine frühere Version zurückgewechselt werden.
- **Zentraler Zugriff:** Teammitglieder, die Lehrperson und später die Deployment-Pipeline beziehen das Artefakt aus einer Quelle, statt Dateien manuell weiterzugeben.

---

## 1. Wahl des Artefakt-Typs und Repositories

### Artefakt-Typ

| Option | Bewertung |
|---|---|
| **Docker-Image** ✅ | Deployment-Artefakt: enthält Java-Runtime und JAR und kann in P4 direkt gestartet werden. Docker verwenden wir bereits für die Datenbank und die Systemtests. |
| Maven-Artefakt (JAR) | Nur Build-Artefakt: für ein Deployment wird zusätzlich eine Java-Installation oder ein Image benötigt. Sinnvoll für Bibliotheken, die von anderen Projekten eingebunden werden, was bei uns nicht der Fall ist. |

### Repository / Registry

| Option | Vorteile | Nachteile |
|---|---|---|
| **GitHub Container Registry (GHCR)** ✅ | Im selben GitHub-Konto wie Code, Issues und Pipeline. Authentifizierung über das automatische `GITHUB_TOKEN`, kein zusätzliches Secret. Images werden direkt mit dem Repository verknüpft. | Standardmässig privat, Sichtbarkeit muss angepasst werden |
| Docker Hub | Bekannteste Registry, öffentliche Images einfach auffindbar | Eigenes Konto nötig, Access Token muss als Secret gespeichert werden, Rate Limits bei Pulls |
| GitHub Packages (Maven) | Ebenfalls in GitHub integriert | Nur für JARs, liefert kein Deployment-Artefakt |
| GitLab Container Registry | Gleichwertige Funktionen | Unser Code und die Pipeline liegen auf GitHub, ein zweites System wäre unnötig |

**Entscheidung:** Wir publizieren **Docker-Images in die GitHub Container Registry**.
Damit bleibt alles an einem Ort, es wird kein zusätzliches Secret benötigt und das Image kann in P4 direkt deployt werden.

---

## 2. Namenskonvention und Versionierung

### Image-Namen
```
ghcr.io/<owner>/<repository>/<service>
```

| Service | Image |
|---|---|
| `employee-service` | `ghcr.io/yaracorder0/m324_gruppe4/employee-service` |
| `ticket-service` | `ghcr.io/yaracorder0/m324_gruppe4/ticket-service` |

- Pro Microservice ein eigenes Image, damit die Services unabhängig voneinander deployt werden können.
- Der Service-Name entspricht dem Maven-Modul und dem Ordnernamen.
- Alles kleingeschrieben, da Container-Registries keine Grossbuchstaben erlauben. Die Pipeline wandelt den Repository-Namen deshalb automatisch in Kleinbuchstaben um.

### Tags

| Tag | Beispiel | Wann? | Zweck |
|---|---|---|---|
| `sha-<commit>` | `sha-e9a3a42` | Jeder Push auf `main` und jeder Release-Tag | Eindeutig einem Commit zugeordnet, ändert sich nie |
| `latest` | `latest` | Jeder Push auf `main` | Zeigt immer auf den neusten getesteten Stand von `main` |
| `<major>.<minor>.<patch>` | `1.0.0` | Git-Tag `v1.0.0` | Offizielles Release nach Semantic Versioning, für Deployments |
| `<major>.<minor>` | `1.0` | Git-Tag `v1.0.0` | Zeigt auf das neuste Patch-Release dieser Version |

**Semantic Versioning:** `MAJOR` bei inkompatiblen API-Änderungen, `MINOR` bei neuen Funktionen, `PATCH` bei Bugfixes.

**Regeln:**
- Feature-Branches und Pull Requests erzeugen **kein** publiziertes Image. Das Image wird dort nur gebaut, damit ein fehlerhaftes `Dockerfile` früh auffällt.
- `latest` wird nur für lokale Tests verwendet. Deployments verwenden immer einen festen Tag (`1.0.0` oder `sha-...`), damit sie reproduzierbar sind.
- SemVer-Tags werden nur über Git-Tags (`v*`) erzeugt, die auf einem Commit auf `main` gesetzt werden.
- Zusätzlich enthält jedes Image OCI-Labels (`org.opencontainers.image.source`, `.revision`, `.version`, `.created`), über die Repository, Commit und Version direkt im Image nachvollziehbar sind.

---

## 3. Einrichtung der Registry
Die GitHub Container Registry muss nicht separat installiert werden. Sie ist für jedes GitHub-Konto verfügbar.

1. **Workflow-Berechtigungen prüfen:** Settings → Actions → General → Workflow permissions bleibt auf **"Read repository contents and packages permissions"**. Schreibrechte werden nur im Publish-Job vergeben (siehe Abschnitt 5).
2. **Erstes Image publizieren:** Beim ersten erfolgreichen Push auf `main` legt die Pipeline die Packages `employee-service` und `ticket-service` automatisch an.
3. **Mit Repository verknüpfen:** Über das Label `org.opencontainers.image.source` erscheinen die Packages auf der Startseite des Repositories unter "Packages".
4. **Sichtbarkeit anpassen:** Neue Packages sind privat. Unter Package settings → Change visibility → **Public**, damit die Lehrperson die Images ohne Login beziehen kann.

---

## 4. Pipeline-Integration
Die bestehende P3-Pipeline ([CI.yml](../.github/workflows/CI.yml), Variante 2) bleibt vollständig erhalten und wird um den Job `publish` erweitert.

```
CI.yml
├── Job: employee-service  →  Build & Unit-Tests → Integrationstests → Upload JAR
├── Job: ticket-service    →  Build & Unit-Tests → Integrationstests → Upload JAR
├── Job: system-tests      (needs: beide Service-Jobs)  →  Systemtests mit beiden JARs
└── Job: publish           (needs: system-tests, Matrix: employee-service, ticket-service)
                           →  Download JAR → Tags bestimmen → Docker Build → Push → Ausgabe
```

### Ablauf des Publish-Jobs

| Schritt | Umsetzung | Zweck |
|---|---|---|
| Voraussetzung | `needs: [ system-tests ]` | Publiziert wird nur, wenn Build, Unit-, Integrations- und Systemtests erfolgreich waren |
| Matrix | `service: [ employee-service, ticket-service ]` | Ein Image pro Service, beide parallel, ohne doppelten YAML-Code |
| JAR übernehmen | `actions/download-artifact` | Genau das JAR, das alle Tests bestanden hat, wird verpackt. Es wird nichts neu kompiliert |
| Tags und Labels | `docker/metadata-action` | Erzeugt die Tags gemäss Abschnitt 2 und die OCI-Labels |
| Login | `docker/login-action` mit `GITHUB_TOKEN` | Nur wenn tatsächlich publiziert wird |
| Build & Push | `docker/build-push-action` | Baut das Image aus `Code/Ticket_System/<service>/Dockerfile`, pusht nur auf `main` und bei `v*`-Tags |
| Caching | `cache-from/cache-to: type=gha` | Docker-Layer (z. B. Java-Basis-Image) werden zwischen Läufen wiederverwendet |
| Ausgabe | Job-Summary und Log | Zeigt Image, Tags, Digest, Commit, Pipeline-Lauf und den `docker pull`-Befehl |

### Wann wird publiziert?

| Event | Build & Tests | Image gebaut | Image publiziert | Tags |
|---|---|---|---|---|
| Push auf `feature/**` | ✅ | ✅ | ❌ | – |
| Pull Request auf `main` | ✅ | ✅ | ❌ | – |
| Push auf `main` (Merge) | ✅ | ✅ | ✅ | `sha-<commit>`, `latest` |
| Push Git-Tag `v1.0.0` | ✅ | ✅ | ✅ | `sha-<commit>`, `1.0.0`, `1.0` |
| Manueller Start (`workflow_dispatch`) | ✅ | ✅ | ❌ | – |

### Dockerfile
Pro Service liegt ein `Dockerfile` im Service-Ordner:
- Basis-Image `eclipse-temurin:21-jre-alpine-3.22`: nur die Java-Runtime (kein JDK), klein und mit fester Version.
- Das JAR wird aus `target/` kopiert und nicht im Image gebaut. Dadurch ist garantiert, dass das getestete JAR ausgeliefert wird.
- Die Anwendung läuft als eigener Benutzer `spring` und nicht als `root`.
- Eine `.dockerignore` sorgt dafür, dass nur das JAR in den Build-Kontext gelangt (keine Quelldateien oder lokalen Konfigurationen).

### Konfiguration zur Laufzeit
Die Images enthalten keine umgebungsspezifischen Werte. Datenbank und Service-URL werden beim Start über Umgebungsvariablen gesetzt (Spring Boot überschreibt damit die Werte aus `application.properties`):

| Variable | Service | Beispiel |
|---|---|---|
| `SPRING_DATASOURCE_URL` | beide | `jdbc:postgresql://postgres:5432/employee_db` |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | beide | Zugangsdaten der Zielumgebung |
| `EMPLOYEE_SERVICE_URL` | `ticket-service` | `http://employee-service:8081/api/employees` |

---

## 5. Zugriff, Rechte und Secrets

### Authentifizierung der Pipeline
- Die Pipeline verwendet das **`GITHUB_TOKEN`**. Dieses Token wird von GitHub für jeden Pipeline-Lauf automatisch erzeugt und läuft nach dem Lauf ab.
- Es wird **kein Personal Access Token (PAT)** und kein eigenes Secret benötigt. Ein PAT wäre an eine Person gebunden, lange gültig und müsste manuell rotiert werden.
- Im Workflow wird das Token nur über `${{ secrets.GITHUB_TOKEN }}` referenziert. GitHub maskiert den Wert automatisch in den Logs.

### Least Privilege
| Ebene | Einstellung |
|---|---|
| Repository-Standard | Workflow permissions: nur Leserechte |
| Jobs `employee-service`, `ticket-service`, `system-tests` | Standard (nur lesen), kein Zugriff auf die Registry |
| Job `publish` | `contents: read`, `packages: write`: nur dieser Job darf Images publizieren |
| Pull Requests aus Forks | Erhalten von GitHub nur ein Token mit Leserechten, können also nichts publizieren |

### Wer darf was?

| Rolle | Push (publizieren) | Pull (beziehen) |
|---|---|---|
| CI-Pipeline (`GITHUB_TOKEN`) | ✅ nur Job `publish` auf `main` / `v*` | ✅ |
| Teammitglieder | ❌ kein manuelles Publizieren, nur über die Pipeline | ✅ |
| Lehrperson | ❌ | ✅ (Package öffentlich) |
| Öffentlichkeit | ❌ | ✅ (Package öffentlich) |

Da Images nur über die Pipeline publiziert werden, durchläuft jedes Image zwingend alle Tests. Zusätzlich gelangt Code nur über einen Pull Request mit Review auf `main` (Branch Protection, siehe [P3](P3_CI_Variantenvergleich.md#branching-strategie-und-ci-auslösung)).

### Umgang mit Secrets
- Im Repository und in den Images sind **keine Tokens oder Passwörter** für die Registry abgelegt.
- Die Datenbank-Zugangsdaten in `docker-compose.yml`, `docker-compose.release.yml` und `application.properties` sind **reine Entwicklungs- und Testwerte** für lokale Container ohne echte Daten. Für eine produktive Umgebung in P4 werden sie über Umgebungsvariablen bzw. GitHub Secrets gesetzt und nicht ins Image eingebaut.
- Weil die Images öffentlich sind, dürfen sie keine Secrets enthalten. Die `.dockerignore` verhindert, dass versehentlich lokale Dateien ins Image gelangen.
- In KI-Tools wurden keine Tokens, Passwörter oder privaten Zugangsdaten eingegeben.

---

## 6. Nachweis des publizierten Artefakts

### Pipeline-Ausgabe
Der Publish-Job schreibt pro Service eine Tabelle in die Job-Summary des Pipeline-Laufs (Image, Tags, Digest, Commit, Link zum Lauf, `docker pull`-Befehl).

### Packages in der Registry
Die Packages sind auf der Startseite des Repositories rechts unter "Packages" verlinkt.
- Link employee-service: https://github.com/yaracorder0/m324_gruppe4/pkgs/container/m324_gruppe4%2Femployee-service
- Link ticket-service: https://github.com/yaracorder0/m324_gruppe4/pkgs/container/m324_gruppe4%2Fticket-service

### Release
- Link: https://github.com/yaracorder0/m324_gruppe4/actions

### Artefakt beziehen
```bash
docker pull ghcr.io/yaracorder0/m324_gruppe4/employee-service:1.0.0
docker pull ghcr.io/yaracorder0/m324_gruppe4/ticket-service:1.0.0
```

---

## 7. Verwendung des Artefakts in P4 (Deployment / Release)

### Testumgebung mit den publizierten Images
Mit [docker-compose.release.yml](../Code/Ticket_System/docker-compose.release.yml) wird das Gesamtsystem ausschliesslich aus den publizierten Images gestartet, ohne den Code zu bauen:

```bash
cd Code/Ticket_System
IMAGE_TAG=1.0.0 docker compose -f docker-compose.release.yml up -d
```

- Startet PostgreSQL (inkl. Init-Skript), `employee-service` und `ticket-service`.
- Die Services starten erst, wenn die Datenbank bereit ist (Healthcheck).
- Mit `IMAGE_TAG` wird festgelegt, welche Version gestartet wird.

Diese Umgebung wurde lokal mit selbst gebauten Images getestet: Alle 10 Systemtests aus P3 waren gegen die Container erfolgreich (inkl. ST-06, der die Kommunikation zwischen den beiden Containern prüft).

### Ablauf in P4
1. Ein Release wird durch einen Git-Tag (z. B. `v1.1.0`) auf `main` ausgelöst.
2. Die CI-Pipeline testet und publiziert die Images mit dem Tag `1.1.0`.
3. Die CD-Pipeline bzw. das Deployment verwendet genau diesen Tag. Es wird nicht neu gebaut.
4. **Rollback:** Bei einem Fehler wird das Deployment mit dem vorherigen Tag (z. B. `IMAGE_TAG=1.0.0`) neu gestartet.
5. Konfiguration und Zugangsdaten der Zielumgebung werden beim Deployment über Umgebungsvariablen bzw. Secrets gesetzt.

Die Pipeline ist darauf vorbereitet, in P4 einen Deploy-Job mit `needs: publish` zu ergänzen, der die soeben publizierten Images in die Abnahmeumgebung ausrollt.

---

## KI-Nachweis
Der Artefakt-Review mit KI ist im [KI-Nachweis](KI_Nachweis.md#p3b-artefakt-repository) dokumentiert.
 
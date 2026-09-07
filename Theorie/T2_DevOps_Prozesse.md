# DevOps Prozesse

## Inhalt

- [SDLC - Software Development Life Cycle](#sdlc---software-development-life-cycle)
- [DevOps LifeCycle](#devops-lifecycle)
- [Vergleich SDLC vs. DevOps Lifecycle](#vergleich-sdlc-vs.-devops-lifecycle)
- [MVP - Minimum Viable Product](#mvp---minimum-viable-product)

## SDLC - Software Development Life Cycle

Der **SDLC (Software Development Life Cycle)** beschreibt den gesamten Lebenszyklus einer Software.
Er unterteilt die Softwareentwicklung in verschiedene, voneinander abhängige Phasen.

Je nach SDLC-Modell können die Phasen nacheinander, parallel oder wiederholt durchgeführt werden.

### Die 7 Phasen:

#### 1. Planung – Ziele, Umfang, Zeit, Kosten und Ressourcen festlegen
#### 2. Analyse – Anforderungen der Benutzer/Kunden erfassen und analysieren
#### 3. Entwurf (Design) – Architektur, Datenbanken, Benutzeroberfläche etc. planen
#### 4. Codierung / Entwicklung – Software wird programmiert
#### 5. Testen – Fehler finden und prüfen, ob die Anforderungen erfüllt sind
#### 6. Bereitstellung (Deployment) – Software wird veröffentlicht bzw. in die Produktionsumgebung gebracht
#### 7. Wartung – Fehler beheben, Updates durchführen und Software weiterentwickeln

### Anwendung und Steuerung des SDLC

- Der SDLC dient als strukturierter Rahmen für die Planung, Entwicklung, Bereitstellung und Wartung von Software.
- Die einzelnen Phasen werden je nach SDLC-Modell nacheinander oder iterativ durchlaufen.
- Fortschritt und Ergebnisse werden während des Projekts überprüft und bei Bedarf angepasst.

### Vorteile von SDLC

- bessere software
- verbesserte produktivität
- minimiertes risiko
- verbesserte projekttransparenz
- bessere zusammenarbeit
- effizienteres ressourcenmanagement
- verbesserte kundenzufriedenheit

## DevOps Lifecycle

DevOps verbindet **Softwareentwicklung (Development)** und **IT-Betrieb (Operations)**.

Ziel ist es, Entwicklung, Tests und Bereitstellung durch **Zusammenarbeit und Automatisierung** schneller und zuverlässiger zu machen.

Kernphasen:

1. **Planung**
   - Anforderungen sammeln und Aufgaben planen

2. **Code**
   - Software entwickeln und Code verwalten

3. **Build / Erstellung**
   - Code wird zu einer ausführbaren Anwendung zusammengebaut

4. **Test**
   - Software automatisiert und manuell testen

5. **Release**
   - Eine fertige Version für die Veröffentlichung vorbereiten

6. **Bereitstellung / Deployment**
   - Software in die Produktionsumgebung bringen

7. **Überwachung / Monitoring**
   - Software im Betrieb überwachen
   - Fehler und Feedback erkennen

Die Phasen bilden einen **kontinuierlichen Kreislauf**:
Erkenntnisse aus dem Monitoring fliessen wieder in die Planung ein.

Traditionell sind Entwicklung und Betrieb stärker voneinander getrennt.
DevOps versucht diese Trennung aufzuheben.

Ziele von DevOps:
- schnellere und häufigere Releases
- stärkere Zusammenarbeit zwischen Dev und Ops
- mehr Automatisierung
- Fehler früher erkennen
- schnellere Reaktion auf Probleme und Feedback
- zuverlässigere Software

## Vergleich SDLC vs. DevOps Lifecycle

| SDLC | DevOps |
|---|---|
| Beschreibt den gesamten Lebenszyklus einer Software | Verbindet Entwicklung (Dev) und Betrieb (Ops) |
| Fokus auf strukturierter Entwicklung von Planung bis Wartung | Fokus auf Zusammenarbeit, Automatisierung und kontinuierliche Verbesserung |
| Je nach Modell können Phasen nacheinander oder iterativ ablaufen | Phasen bilden einen kontinuierlichen Kreislauf |
| Entwicklung und Bereitstellung können stärker getrennt sein | Entwicklung, Bereitstellung und Betrieb arbeiten eng zusammen |
| Automatisierung ist nicht zwingend zentral | Starker Fokus auf Automatisierung, z.B. CI/CD |


### SDLC
- sorgt für einen strukturierten und planbaren Entwicklungsprozess
- klare Phasen helfen bei Planung und Qualitätskontrolle
- Bereitstellung kann je nach Modell seltener und in grösseren Releases erfolgen
- Beschreibt WAS im Lebenszyklus einer Software passiert

### DevOps
- schnellere und häufigere Releases
- automatisierte Tests und Deployments
- Fehler und Probleme werden schneller erkannt
- Feedback aus dem Betrieb fliesst direkt zurück in die Entwicklung
- engere Zusammenarbeit zwischen Entwicklung und Betrieb
- Beschreibt WIE Entwicklung und Betrieb zusammenarbeiten

## MVP - Minimum Viable Product

Ein **MVP (Minimum Viable Product)** ist die einfachste funktionsfähige Version
eines Produkts, die bereits einen Nutzen für Benutzer bietet.

Es enthält nur die wichtigsten Funktionen, die notwendig sind, um das Produkt
zu verwenden und zu testen.

### Merkmale
- nur die wichtigsten Kernfunktionen
- funktionsfähig und für Benutzer nutzbar
- schnell entwickelbar und bereitstellbar
- ermöglicht frühes Feedback
- wird anhand des Feedbacks weiterentwickelt

### Bedeutung im DevOps Lifecycle

Ein MVP ermöglicht es, eine erste Version der Software **schnell bereitzustellen**
und echtes Feedback von Benutzern zu erhalten. Das Feedback fliesst wieder in den DevOps Lifecycle ein:

**Planen → Entwickeln → Testen → Bereitstellen → Feedback/Überwachen → Verbessern → erneut bereitstellen**

Dadurch kann das Produkt **schrittweise und iterativ verbessert** werden.

### Vorteile
- schnelles Benutzerfeedback
- Fehler und Probleme früh erkennen
- unnötige Entwicklung vermeiden
- Anforderungen besser an die Benutzer anpassen
- schnelle und kontinuierliche Verbesserung

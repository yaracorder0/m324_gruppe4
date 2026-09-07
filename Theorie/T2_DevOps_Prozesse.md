# DevOps Prozesse

## Inhalt

- [SDLC - Software Development Life Cycle](#sdlc---software-development-life-cycle)
- [DevOps LifeCycle](#devops-lifecycle)
- [Vergleich SDLC vs. DevOps Lifecycle](#vergleich-sdlc-vs.-devops-lifecycle)
- [MVP - Minimum Viable Product](#mvp---minimum-viable-product)

## SDLC - Software Development Life Cycle

Der **SDLC (Software Development Life Cycle)** beschreibt den gesamten Lebenszyklus einer Software.
Er unterteilt die Softwareentwicklung in verschiedene, wiederholbare, voneinander abhängige Phasen.

Je nach SDLC-Modell können die Phasen nacheinander, parallel oder wiederholt durchgeführt werden.

### Die 7 Phasen:

#### 1. Planung
- Ziele und Umfang des Projekts festlegen
- Zeit, Kosten und benötigte Ressourcen planen
- Risiken einschätzen

#### 2. Analyse
- Anforderungen der Benutzer/Kunden erfassen
- Anforderungen analysieren und dokumentieren
- Festlegen, was die Software können muss

#### 3. Entwurf / Design
- Technische Lösung planen
- Architektur, Datenbanken und Benutzeroberfläche entwerfen
- Grundlage für die Entwicklung erstellen

#### 4. Codierung / Entwicklung
- Software wird programmiert
- Anforderungen und Designs werden technisch umgesetzt

#### 5. Testen
- Software auf Fehler überprüfen
- Prüfen, ob die Anforderungen erfüllt werden
- Qualität und Funktionalität sicherstellen

#### 6. Bereitstellung / Deployment
- Fertige Software wird veröffentlicht
- Software wird in die Produktionsumgebung gebracht
- Benutzer können das System verwenden

#### 7. Wartung
- Fehler nach der Veröffentlichung beheben
- Updates und Verbesserungen durchführen
- Software an neue Anforderungen anpassen

#### Anwendung und Steuerung des SDLC

Der SDLC wird von Entwicklungsteams verwendet, um Software strukturiert zu planen,
zu entwickeln, bereitzustellen und zu warten.

Während des Projekts werden unter anderem folgende Faktoren überwacht:

- Anforderungen
- Fortschritt
- Zeit
- Kosten
- Qualität
- Risiken
- Ressourcen

Projektleitung, Entwickler, Tester und weitere Beteiligte arbeiten dabei zusammen. Je nach Vorgehensmodell werden die SDLC-Phasen unterschiedlich durchgeführt:

**Wasserfallmodell:**
- Die Phasen werden nacheinander durchgeführt.
- Eine Phase wird normalerweise abgeschlossen, bevor die nächste beginnt.
- Änderungen im späteren Verlauf sind schwieriger umzusetzen.
- Eignet sich besonders, wenn die Anforderungen von Anfang an klar sind.


![img.png](img.png)

**Agiles Modell:**
- Die Entwicklung erfolgt in kurzen, wiederholten Zyklen (Iterationen).
- Software wird schrittweise entwickelt und verbessert.
- Feedback und Änderungen können laufend berücksichtigt werden.
- Eignet sich besonders, wenn sich Anforderungen während des Projekts verändern können.

![img_1.png](img_1.png)

### Vorteile von SDLC

- bessere Softwarequalität
- höhere Produktivität
- geringeres Projektrisiko
- bessere Projekttransparenz
- bessere Zusammenarbeit
- effizienteres Ressourcenmanagement
- bessere Planbarkeit von Zeit und Kosten
- höhere Kundenzufriedenheit

## DevOps Lifecycle

DevOps verbindet **Softwareentwicklung (Development)** und **IT-Betrieb (Operations)**.

Ziel ist es, Entwicklung, Tests und Bereitstellung durch **Zusammenarbeit und Automatisierung** schneller und zuverlässiger zu machen.

Grundprinzipien:
- Zusammenarbeit zwischen Dev und Ops
- Automatisierung
- Continuous Integration (CI)
- Continuous Delivery/Deployment (CD)

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
zu verwenden und zu testen. (Minimum)  
Es ist trotzdem bereits funktionsfähig und bietet den Benutzern einen echten Nutzen. (Viable)  
Weitere Funktionen werden erst später anhand von Feedback ergänzt.

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

![img_2.png](img_2.png)


## Transfer

In meinem früheren Projekt bei Swisscom konnte ich vor allem den **DevOps Lifecycle** in der Praxis wiedererkennen. Ich war dort hauptsächlich im Testing tätig und arbeitete unter anderem mit automatisierten Tests.

Die **Testphase** spielte dabei eine wichtige Rolle. Durch automatisierte Tests konnten Funktionen der Software regelmässig überprüft und Fehler frühzeitig erkannt werden. Das zeigt auch einen wichtigen Unterschied zu einem rein traditionellen Entwicklungsansatz, da bei DevOps **Automatisierung und kontinuierliches Testen** eine grössere Rolle spielen.

Eine mögliche Verbesserung wäre, noch mehr Tests zu automatisieren und stärker in den Entwicklungsprozess zu integrieren. Dadurch könnten Fehler noch früher erkannt und die Software schneller und zuverlässiger für ein Release vorbereitet werden.

## KI Nachweis

Prompt: "Kannst du meine Sätze besser formulieren und die Grammatik korrigieren?"  
KI-Hinweis: Die KI hat meine selbst verfassten Sätze sprachlich überarbeitet und grammatikalische Fehler korrigiert.  
Korrigierte Stelle: Abschnitt "Transfer"  
Eigene Schlussfolgerung: Der überarbeitete Abschnitt ist gut formuliert und verständlich.


## Quellen

https://www.ibm.com/de-de/think/topics/sdlc  
https://aws.amazon.com/de/what-is/sdlc/  
https://www.ibm.com/de-de/think/topics/devops-lifecycle  
https://asana.com/de/resources/minimum-viable-product  
https://t2informatik.de/wissen-kompakt/minimum-viable-product/
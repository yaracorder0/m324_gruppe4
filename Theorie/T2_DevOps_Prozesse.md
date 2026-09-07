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



**Agiles Modell:**
- Die Entwicklung erfolgt in kurzen, wiederholten Zyklen (Iterationen).
- Software wird schrittweise entwickelt und verbessert.
- Feedback und Änderungen können laufend berücksichtigt werden.
- Eignet sich besonders, wenn sich Anforderungen während des Projekts verändern können.


### Vorteile von SDLC

- **Bessere Softwarequalität:** Durch festgelegte Entwicklungs- und Testphasen wird die Software regelmässig überprüft.
- **Geringeres Projektrisiko:** Probleme und falsche Anforderungen können früh erkannt werden, bevor sie später hohe Kosten verursachen.
- **Bessere Projekttransparenz:** Durch die klaren Phasen ist ersichtlich, in welchem Stand sich das Projekt befindet.
- **Bessere Zusammenarbeit:** Aufgaben und Verantwortlichkeiten können den verschiedenen Phasen klar zugeordnet werden.
- **Effizienteres Ressourcenmanagement:** Zeit, Kosten und benötigte Ressourcen können frühzeitig geplant werden.
- **Höhere Kundenzufriedenheit:** Anforderungen werden bereits in der Analyse berücksichtigt und die fertige Software wird auf diese geprüft.


Die Ergebnisse einer Phase bilden die Grundlage für die nächste Phase.
Beispielsweise werden die Anforderungen aus der Analyse an die Entwicklung weitergegeben.
Nach der Entwicklung wird die Software getestet und anschliessend für die Bereitstellung vorbereitet.

Dadurch begleitet der SDLC die Software von der ersten Planung bis zum Betrieb und zur Wartung.


![img_4.png](img_4.png)

## DevOps Lifecycle

DevOps verbindet **Softwareentwicklung (Development)** und **IT-Betrieb (Operations)**.

Ziel ist es, Entwicklung, Tests und Bereitstellung durch **Zusammenarbeit und Automatisierung** schneller und zuverlässiger zu machen.

Grundprinzipien:
- Zusammenarbeit zwischen Dev und Ops
- Automatisierung von wiederholbaren Abläufen
- Continuous Integration und Continuous Delivery/Deployment (CI/CD)
- automatisierte Tests
- Infrastructure as Code (IaC)
- kontinuierliches Monitoring und Feedback

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


7. **Operate / Betrieb**
   - Software und Infrastruktur im laufenden Betrieb verwalten
   - Stabilität und Verfügbarkeit der Anwendung sicherstellen


8**Überwachung / Monitoring**
   - Software im Betrieb überwachen
   - Fehler und Feedback erkennen

Die Phasen bilden einen **kontinuierlichen Kreislauf**:
Erkenntnisse aus dem Monitoring fliessen wieder in die Planung ein.

Traditionell sind Entwicklung und Betrieb stärker voneinander getrennt.
DevOps versucht diese Trennung aufzuheben.
DevOps ist nicht nur eine Sammlung von Tools, sondern verändert auch die Zusammenarbeit.
Entwicklung und Betrieb arbeiten gemeinsam an der Software und übernehmen gemeinsam Verantwortung.
Dadurch werden getrennte Teams bzw. Silos reduziert.

![img_3.png](img_3.png)

Ziele von DevOps:
- schnellere und häufigere Releases
- stärkere Zusammenarbeit zwischen Dev und Ops
- mehr Automatisierung
- Fehler früher erkennen
- schnellere Reaktion auf Probleme und Feedback
- zuverlässigere Software



## Vergleich SDLC vs. DevOps Lifecycle

SDLC und DevOps schliessen sich nicht gegenseitig aus.
DevOps kann als moderne bzw. agile Umsetzung des Software-Lebenszyklus betrachtet werden.
Auch bei DevOps wird geplant, entwickelt, getestet und bereitgestellt, jedoch in kürzeren,
kontinuierlichen und stärker automatisierten Zyklen.

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


## Anwendung in unserem Projekt

Die Konzepte SDLC, DevOps und MVP lassen sich auch auf unser Ticket-System übertragen.
Viele der einzelnen Phasen und Prinzipien sind in unserem Entwicklungsprozess direkt erkennbar.

### 1. SDLC im Ticket-System

Auch bei unserem Ticket-System durchlaufen wir verschiedene Phasen des SDLC.

- **Planung und Analyse:** Anforderungen und Funktionen des Ticket-Systems werden festgelegt und als Aufgaben geplant.
- **Entwicklung:** Backend und Frontend werden entwickelt und die PostgreSQL-Datenbank angebunden.
- **Test:** Die implementierten Funktionen werden getestet.
- **Bereitstellung:** Die Anwendung wird mit Docker Compose in Containern bereitgestellt.
- **Wartung:** Fehler können behoben und neue Funktionen schrittweise ergänzt werden.

Unser Vorgehen entspricht dabei eher einem **iterativen Ansatz**, da das System nicht einmal vollständig entwickelt und danach veröffentlicht wird, sondern schrittweise erweitert und verbessert wird.


### 2. DevOps im Ticket-System

Auch verschiedene DevOps-Praktiken werden in unserem Projekt eingesetzt.

Backend und PostgreSQL-Datenbank werden mit **Docker Compose** in Containern ausgeführt.
Dadurch kann die Anwendung reproduzierbar bereitgestellt werden.

Für das **Monitoring** wird der Spring-Boot-Actuator verwendet.
Über den `/actuator/health`-Endpunkt kann überprüft werden, ob die Anwendung und die Verbindung zur Datenbank funktionieren.

Dadurch sind neben der Entwicklung auch **Deployment, Betrieb und Monitoring** Teil unseres Entwicklungsprozesses.


### 3. SDLC und DevOps im Projekt

Der SDLC beschreibt die verschiedenen Schritte, die wir bei der Entwicklung unseres Ticket-Systems durchlaufen.

DevOps ergänzt diesen Prozess durch Praktiken, welche Entwicklung und Betrieb stärker miteinander verbinden.
Docker Compose unterstützt beispielsweise die Bereitstellung und Spring Boot Actuator die Überwachung der laufenden Anwendung.

Dadurch endet die Arbeit nicht nach der Entwicklung und dem Testen, sondern umfasst auch die Bereitstellung und den anschliessenden Betrieb.


### 4. MVP unseres Ticket-Systems

Das MVP unseres Ticket-Systems besteht aus den wichtigsten Funktionen, die notwendig sind,
damit das System bereits sinnvoll genutzt werden kann.

Anstatt von Anfang an alle geplanten Funktionen umzusetzen, wird zuerst eine funktionsfähige
Grundversion entwickelt. Zusätzliche Funktionen können anschliessend schrittweise ergänzt und
anhand von Tests und Feedback verbessert werden.

Dadurch können wir früh überprüfen, ob die Grundfunktionen des Ticket-Systems funktionieren,
bevor weitere Features entwickelt werden.


## Quellen

https://www.ibm.com/de-de/think/topics/sdlc  
https://aws.amazon.com/de/what-is/sdlc/  
https://www.ibm.com/de-de/think/topics/devops-lifecycle  
https://asana.com/de/resources/minimum-viable-product  
https://t2informatik.de/wissen-kompakt/minimum-viable-product/
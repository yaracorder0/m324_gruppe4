# DevOps Kultur - CALMS & The Three Ways

## Inhalt 
- [Neue Begriffe](#Neue-Begriffe)
- [CALMS](#CALMS)
- [The Three Ways](#The-Three-Ways)
- [Vergleich](#Vergleich)
- [Bewertung der Transformation](#Bewertung-der-Transformation)

## Neue Begriffe
*Entwicklungszyklen*
- Phase, die ein Feature von der Anforderung über Development und Testing bis zur Produktion durchläuft. 
*Silos*
- Organisatorische Abgrenzung zwischen Entwicklung und Betrieb
*Lead Time*
- Gesamtzeit vom Erstellen eines Tickets bis zum deployment
*Processing Time
- Netto arbeitszeit an einem Ticket, ohne Wartezeiten auf Reviews.
*MTTR (Mean Time to Repair)*
- Durchschnittliche Zeit von einem Fehler bis zur Behebung

## CALMS Prinzipien
***CALMS*** - Culture Automation Lean Measurement Sharing
<br>
![img.png](images/img.png)

### C - Culture (Kultur)
- Zusammenarbeit zwischen Entwicklern (Dev) und Betreiber (Ops), anstatt Silos
- Gemeinsame Verantwortung übernehmen, für Qualität, Betrieb und schnellen Releases
- Keine Schuldzuweisung (No Blame Culture), sondern Fehler werden analysiert und behoben
- Management, Entwicklung und Betrieb verfolgen dieselben Ziele
- Kultur wird durch Workshops und Teambuilding verändert

### A - Automation (Automatisierung)
- Wiederholende Aufgaben werden automatisiert: Tests, Builds, Deployments und Monitoring
- CI/CD Pipeline werden eingeführt
- Automatisierung beschleunigt Prozesse, reduziert Fehler

### L - Lean (Schlankheit)
- Unnötige Prozesse und Arbeiten vermeiden 
- Fokus auf kontinuierliche Verbesserung und Kundennutzen
- Experimente ermöglichen, schnelleres Feedback zu sammeln, um daraus zu lernen

### M - Measurement (Messung)
- Entscheidungen basieren auf Messungen und Feedback
- Typische Metriken sind MTTR (Mean Time to Repair), Lead und Processing Time
- Unterscheidung zwischen Prozess-, Betriebs- und Business-Metriken
- Messung zeigt Schwachstellen und macht auch Erfolge sichtbarer

### S - Sharing (Wissenfluss)
- Wissen, Erfahrungen und Fehler werden offen geteilt
- Fördert Lernen innerhalb und zwischen Team 
- Schafft Vertrauen und offene Fehlerkultur
- Ziel ist, die Organisation soll aus Fehler lernen und Doppelarbeit vermeiden

### Fazit
Das CALMS-Modell bietet eine klare Struktur, um die Kernelementen von DevOps zu verstehen.
Sie dient als ein Orientierungsrahmen, ersetzt aber keine Erfolgsmessung

---

## The Three Ways
![img.png](images/img2.png)

### 1. System Thinking 
- Fokus auf das gesamte System (anstatt Silos)
- Reibungsloses Zusammenarbeiten/Flow zwischen Entwicklern, Betreiber und letztendlich dem Kunden
- Arbeiten werden sichtbar gemacht, Übergaben werden minimiert
- Entwicklungszyklen werden gemessen und optimiert (anstatt 6-monatige Releases, zu wochentlichen)
- Einsetzung von Kanban-Board, um die Arbeit zu visualisieren

### 2. Amplify Feedback Loop
- Feedback sollte schnell, kontinuierlich und automatisiert erfolgen (MTTR)
- Probleme werden sofort erkannt und behoben, um Qualität zu sichern
- Ermöglicht schnelle Reaktion auf Kundenwunsch und minimiert Ausfallzeiten (Wöchentliche Releases)

### 3. Continual Learning and Experimentation
- Wissen wird nicht gehortet, sondern stetig geteilt und im Projekt verankert
- Fehler werden schuldlos analysiert (No Blame-Culture), um Verbesserungen zu teilen??
- Etablierung von Feedback-Loops und Wissensteilung, durch Workshops, Teambuilding und Erfahungsberichten

## Vergleich

| Gemeinsame Idee                   | CALMS-Bezug                                                                                                                            | Bezug zu The Three Ways                                                                                                    | Beispiel aus  Fallbeispiel                                                                                                                  | Eigene Einschätzung                                                                                                                          |
| :-------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------------------------------------------- |
| **Kulturwandel & Vertrauen**      | **Culture:** Zusammenarbeit zwischen Dev & Ops, gemeinsame Verantwortung (No Blame Culture)                                            | **3. Way:** Continual Learning & Experimentation (Lernen aus Fehlern & Experimente ermöglichen)                            | Kulturveränderung durch Workshops und Teambuilding, sowohl schuldfreie Fehleranalyse                                                        | Kultur bildet die Basis von guter Arbeit, mehr Vertrauen und angstfreie Arbeit ermöglicht schnellere Analysen und Releases                   |
| **Fluss & Effizienz**             | **Automation & Lean:** Wiederkehrende Aufgaben automatisieren (Tests, Builds, CI/CD) <br>unnötige Prozesse und Verschwendung vermeiden | **1. Way:** Systems Thinking/Flow (Gesamtsystem im Überblick, Übergaben minimieren, Arbeit mit Kanban-Board visualisieren) | Umstellung von 6 monatigen auf wöchentliche Releases, Einsatz von CI/CD Pipelines und Kanban-Boards                                         | Automatisierung erleichert arbeit und reduziert manuelle Fehler.<br>Dadurch ensteht einen klaren Fluss und mehr Verständnis                  |
| **Schnelle Rückmeldung & Daten**  | **Measurement:** Entscheidungen auf Basis von Daten, Prozess- und Betriebs-Metriken treffen                                            | **2. Way:** Amplify Feedback Loops (schnelles, kontinuierliches und automatisierten Feedback)                              | Evaluierung von Metriken wie MTTR (Mean Time to Repair), Lead Time und Processing Time, welche zur Optimierung der Entwicklungszyklen hilft | Gemessene Prozesse können verbessert werden. Schnelles Feedback ermöglicht kürzere Ausfallzeiten, und schnellere Reaktion auf Kundenanfragen |
| **Wissenstransfer & Transparenz** | **Sharing:** Wissen, Erfahrungen und Fehler offen im und zwischen den Teams teilen                                                     | **3. Way:** Continual Learning & Experimentation (Wissen im Projekt verankern und nicht horten)                            | Etablierung von Wissensteilung und Austausch durch Erfahrungsberichte, Teambuilding und Workshops                                           | Gemeinsame Dokumentation und Wissensaustausch verhindert Doppelarbeit. Es verbessert sowohl auch Qualität der Arbeit im ganzen Projekt       |

## Bewertung der Transformation

1. Automation (CALMS & Flow)
- Löst das Problem von langen, manuellen und fehlerhaften Prozessen. Weg von Code bis Produktion wird schneller und zuverlässiger, welches man bei den wöchentlichen Releases erkennen kann.
2. Culture (CALMS & Continuous Learning and Feedback Loops)
- Vernichtete das Wissens hoarding und Blame Culture. Zusammenarbeit zwischen Entwicklern und Betreiber, wird grundsätzlich stabiler

## Transfer 

In meinem Betrieb wird der Punkt *Sharing* aus CALMS oft angesprochen, aber im Alltag fast nicht richtig angenwendet. 
Dokumentationen werden geschrieben, jedoch die Kultur des Austauschs und der Zusammenarbeit bleibt weiterhin ein Verbesserungspunkt.

Das Hauptproblem in unserem Projekt ist, dass selten die Zeit und das Budget für längere Austausche oder Workshops da sind. Im eigenen interesse des Betriebs sollte das jedoch an mehr Bedeutung gewinnen.


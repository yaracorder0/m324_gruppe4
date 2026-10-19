# Geschäftsreport – Kennzahlen Ticket-System
**Projekt:** m324_gruppe4
**Erstellt durch:** Gruppe 4  
**Zweck:** Z1 – Continuous Improvement / DevOps-Kennzahlen

---

## 1. Einleitung

Dieser Bericht dokumentiert die im Ticket-System erhobenen Kennzahlen (KPIs). Diese Kennzahlen dienen als Grundlage für datengetriebene Entscheidungen im DevOps-Prozess und sollen zukünftige Verbesserungen im Entwicklungs- und Betriebsablauf ermöglichen.

Die Daten werden **live aus der Ticket-Datenbank** erhoben (Endpoint: `GET /api/metrics`) und sind jederzeit abrufbar. Kein Messwert wurde erfunden oder manuell eingetragen.

---

## 2. Erhobene Kennzahlen

### 2.1 Ticket-Volumen

| Kennzahl | Wert (Beispiel aus Seed-Daten) | Bedeutung |
|---|---|---|
| Gesamtanzahl Tickets | 8 | Alle je erfassten Tickets |
| Tickets mit Status OPEN | 2 | Noch nicht begonnene Arbeit |
| Tickets mit Status IN_PROGRESS | 2 | Aktiv in Bearbeitung |
| Tickets mit Status REVIEW | 2 | Warten auf Code-Review |
| Tickets mit Status DONE | 2 | Abgeschlossen |

**Warum relevant:** Die Verteilung zeigt auf einen Blick, ob Arbeit sich in einer Phase aufstaut (z. B. zu viele REVIEW-Tickets = Engpass beim Reviewen). Das Management kann so gezielt eingreifen.

---

### 2.2 Abschlussrate (Completion Rate)

| Kennzahl | Formel | Wert |
|---|---|---|
| Abschlussrate | `DONE / Gesamt × 100` | 25 % |

**Warum relevant:** Zeigt, wie produktiv das Team ist und wie viel der erfassten Arbeit tatsächlich ausgeliefert wurde. Eine niedrige Rate deutet auf Blocker oder Qualitätsprobleme hin.

---

### 2.3 Durchlaufzeit Review → Done

| Kennzahl | Messmethode | Wert |
|---|---|---|
| Ø Stunden Review → Done | `done_date − review_date` in Std. | ~119 h (≈ 5 Tage) |

**Warum relevant:** Diese Kennzahl misst, wie lange ein Ticket nach dem Review noch braucht, bis es als fertig markiert wird. Lange Zeiten deuten auf mangelhafte Review-Kapazität oder zu viele Korrekturrunden hin.

> **Messmethode:** Wird direkt aus den Datenbankfeldern `review_date` und `done_date` berechnet. Nur Tickets mit Status DONE und gesetzten Datumsfeldern werden berücksichtigt.

---

### 2.4 Mitarbeiter-Auslastung (Offene Tickets pro Person)

| Kennzahl | Erhebung | Bedeutung |
|---|---|---|
| Offene + laufende Tickets je Mitarbeiter-ID | DB-Query nach `employee_id` | Zeigt, wer überlastet ist |
| Meistbelasteter Mitarbeiter | Mitarbeiter mit den meisten OPEN/IN_PROGRESS-Tickets | Frühwarnung vor Burnout |

**Warum relevant:** Ungleich verteilte Auslastung führt zu Engpässen. Mit dieser Kennzahl kann der Projektmanager die Arbeit gerechter verteilen.

---

### 2.5 Review-Queue-Grösse

| Kennzahl | Wert | Bedeutung |
|---|---|---|
| Tickets in REVIEW | 2 | Grösse der Review-Warteschlange |

**Warum relevant:** Wenn zu viele Tickets gleichzeitig reviewed werden müssen, entsteht ein Flaschenhals. Eine wachsende Review-Queue ist ein Frühindikator für Qualitätsprobleme oder fehlende Reviewer.

---

### 2.6 System-Gesundheit (Spring Actuator)

Zusätzlich zu den fachlichen Kennzahlen stellt der **Spring Boot Actuator** technische Systemmetriken bereit:

| Endpoint | URL | Liefert |
|---|---|---|
| Health-Status | `GET /actuator/health` | DB-Verbindung, Disk-Space, Status UP/DOWN |
| Applikationsinfo | `GET /actuator/info` | Version, Name, Beschreibung |
| JVM-Metriken | `GET /actuator/metrics` | Heap, GC, Thread-Count, HTTP-Requests |

> Diese Metriken können mit Tools wie **Prometheus + Grafana** visualisiert werden, ohne den Quellcode zu ändern.

---

## 3. Wo und wie werden die Kennzahlen erhoben?

### Live-Endpunkt

```
GET http://localhost:8082/api/metrics
```

Antwort (JSON-Beispiel):
```json
{
  "totalTickets": 8,
  "ticketsByStatus": {
    "OPEN": 2,
    "IN_PROGRESS": 2,
    "REVIEW": 2,
    "DONE": 2
  },
  "avgReviewToDoneHours": 119.25,
  "openTicketsPerEmployee": {
    "3": 1,
    "2": 1,
    "1": 1,
    "4": 1
  },
  "mostLoadedEmployeeId": 3,
  "ticketsInReview": 2,
  "ticketsDone": 2,
  "completionRatePercent": 25.0,
  "collectedAt": "2026-10-19T09:30:00+02:00"
}
```

### Datenquellen

| Kennzahl | Datenquelle | Technologie |
|---|---|---|
| Ticket-Volumen und Status-Verteilung | PostgreSQL Tabelle `ticket` | Spring Data JPA (`countByStatus`) |
| Durchlaufzeit Review → Done | Felder `review_date`, `done_date` | Java `Duration.between()` |
| Auslastung je Mitarbeiter | GROUP BY `employee_id` | JPQL-Query |
| System-Health | Spring Boot Actuator | Auto-Konfiguration |

---

## 4. Bewertung der Kennzahlen

| Kennzahl | Aussagekraft | Erhebungsaufwand | Risiko |
|---|---|---|---|
| Ticket-Volumen je Status | ★★★★★ Sehr hoch | Niedrig (direkte DB-Query) | Niedrig |
| Abschlussrate % | ★★★★☆ Hoch | Niedrig (Berechnung) | Mittel – sagt nichts über Qualität aus |
| Ø Review-to-Done-Zeit | ★★★★☆ Hoch | Niedrig (Datumsdifferenz) | Mittel – fehlt wenn Daten lückenhaft |
| Mitarbeiter-Auslastung | ★★★★☆ Hoch | Niedrig (GROUP BY Query) | Niedrig |
| JVM / Systemmetriken | ★★★☆☆ Mittel | Sehr niedrig (Actuator) | Niedrig |

---

## 5. Fazit und Empfehlungen

Basierend auf den Kennzahlen lassen sich folgende Massnahmen ableiten:

1. **Erhöhung der Abschlussrate:** 25% aller Tickets sind DONE. Das Team sollte Tickets konsequent bis DONE bringen, statt neue zu eröffnen.
2. **Review-Bottleneck beobachten:** 2 Tickets in REVIEW bei 2 aktiven Personen = durchschnittlich 1 Review pro Person. Wenn die Zahl wächst, müssen mehr Reviewer eingeplant werden.
3. **Auslastungsbalancing:** Die Auslastungskennzahl zeigt, wer wie viele offene Tasks hat. Der PM kann damit frühzeitig umverteilen.
4. **Durchlaufzeit verkürzen:** ~5 Tage Review-to-Done ist verbesserungsfähig. Automatisierte Tests und Pair-Review könnten helfen.

---

*Dieser Report basiert ausschliesslich auf Echtdaten aus der Ticket-Datenbank. Alle Kennzahlen sind über `GET /api/metrics` jederzeit aktuell abrufbar.*

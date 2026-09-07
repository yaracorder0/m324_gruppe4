-- Erstelle beide getrennten Datenbanken
CREATE DATABASE employee_db;
CREATE DATABASE ticket_db;

-- ========================================================
-- 1. Tabellen und Daten für Employee Service (employee_db)
-- ========================================================
\connect employee_db;

CREATE TABLE employee (
                          id BIGSERIAL PRIMARY KEY,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          joined_date DATE NOT NULL,
                          skill_level INT CHECK (skill_level BETWEEN 1 AND 5) NOT NULL
);

INSERT INTO employee (first_name, last_name, joined_date, skill_level)
VALUES
    ('Max', 'Muster', '2021-05-15', 4),
    ('Erika', 'Mustermann', '2022-01-10', 2),
    ('Hans', 'Meier', '2019-11-01', 5),
    ('Anna', 'Schmidt', '2023-03-20', 1),
    ('Laura', 'Fischer', '2020-08-12', 3),
    ('David', 'Weber', '2024-02-01', 2);

-- ========================================================
-- 2. Tabellen und Daten für Ticket Service (ticket_db)
-- ========================================================
\connect ticket_db;

CREATE TABLE ticket (
                        id BIGSERIAL PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        description TEXT,
                        status VARCHAR(20) NOT NULL,
                        review_date TIMESTAMP WITH TIME ZONE,
                        done_date TIMESTAMP WITH TIME ZONE,
                        employee_id BIGINT NOT NULL
);

INSERT INTO ticket (title, description, status, review_date, done_date, employee_id)
VALUES
    -- Open Tickets
    ('Login Bug fixen', 'Fehler beim OAuth-Login beheben', 'OPEN', NULL, NULL, 1),
    ('Dark Mode hinzufügen', 'UI-Support für Dark Mode umsetzen', 'OPEN', NULL, NULL, 4),

    -- In Progress Tickets
    ('Performance Optimierung', 'Datenbankabfragen für Mitarbeiterliste beschleunigen', 'IN_PROGRESS', NULL, NULL, 3),
    ('API Dokumentation', 'Swagger/OpenAPI Spezifikation erstellen', 'IN_PROGRESS', NULL, NULL, 2),

    -- Review Tickets
    ('Passwort Reset Feature', 'E-Mail-Reset-Flow wurde implementiert', 'REVIEW', '2026-09-01T10:00:00Z', NULL, 1),
    ('Docker Compose Setup', 'Postgres Container Setup optimieren', 'REVIEW', '2026-09-04T14:30:00Z', NULL, 5),

    -- Done Tickets
    ('Initiales Projektsetup', 'Grundgerüst für Mikroservices aufgesetzt', 'DONE', '2026-08-20T09:00:00Z', '2026-08-25T16:00:00Z', 3),
    ('Unit-Tests schreiben', 'Tests für Employee-Controller erstellt', 'DONE', '2026-08-28T11:00:00Z', '2026-08-30T15:45:00Z', 2);
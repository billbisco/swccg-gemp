-- The first recurring leagues (Bill, 2026-10-20), as league_schedule rows.  Run once by hand on prod AFTER
-- update_league_schedule.sql; the rows are DISABLED (enabled = b'0') so nothing is created until an admin reviews
-- each one in League Admin > League schedules (Edit / Preview) and turns it on.  Re-running the script would add
-- duplicate rows: it is a seed, not a migration.
--
-- A seed script rather than an admin "Create defaults" button: the seven definitions are data an admin reviews once,
-- a script is read before it is run and leaves no endpoint that could re-create them by accident, and the League
-- Admin page already has everything needed to change or add one afterwards.
--
-- Codes come from swccgFormats.json (constructed formats), SealedLeagueType (sealed products) and swccgDrafts.json
-- (drafts).  The template is the New-league form's fields by name; each event's params.series lists the serie(s)
-- as {format, duration (days), matches}.  League names come from name_pattern: "{series} - {month} {yyyy}" gives
-- "Open - November 2026" (every name must stay within 45 characters).
--
-- GUESSED (Bill to confirm): the cadence (monthly, all starting 2026-11-01, created 7 days ahead); the shape of a
-- constructed league (4 series of 7 days, 10 ranked matches each, 500 silver, all cards, no deck lock-in - the
-- League Admin form's defaults); Reflections Sealed as 7 days per serie, 10 matches (its product fixes the serie
-- count); the Cube as a solo-draft league of "Cube v7 + Objectives Draft" (c7obj_draft), the one non-legacy cube in
-- swccgDrafts.json, one 28-day serie; "Classic No V (Modified)" as format classic_no_virtual_m and "Premiere - A New
-- Hope" as the constructed format premiere_anh (premiere_anh_sealed is the sealed product).

INSERT INTO `league_schedule`
  (`name`, `league_type`, `template`, `events`, `name_pattern`, `next_event_date`, `next_event_index`, `interval_months`, `lead_days`, `enabled`, `created_by`)
VALUES
  ('Premiere - Death Star II', 'CONSTRUCTED',
   '{"cost":500,"collectionType":"default","lockedDeckType":"","allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Premiere - Death Star II","params":{"series":[{"format":"premiere_ds2","duration":7,"matches":10},{"format":"premiere_ds2","duration":7,"matches":10},{"format":"premiere_ds2","duration":7,"matches":10},{"format":"premiere_ds2","duration":7,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed'),

  ('Classic No V (Modified)', 'CONSTRUCTED',
   '{"cost":500,"collectionType":"default","lockedDeckType":"","allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Classic No V (Modified)","params":{"series":[{"format":"classic_no_virtual_m","duration":7,"matches":10},{"format":"classic_no_virtual_m","duration":7,"matches":10},{"format":"classic_no_virtual_m","duration":7,"matches":10},{"format":"classic_no_virtual_m","duration":7,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed'),

  ('Ewok', 'CONSTRUCTED',
   '{"cost":500,"collectionType":"default","lockedDeckType":"","allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Ewok","params":{"series":[{"format":"ewok","duration":7,"matches":10},{"format":"ewok","duration":7,"matches":10},{"format":"ewok","duration":7,"matches":10},{"format":"ewok","duration":7,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed'),

  ('Reflections Sealed', 'SEALED',
   '{"cost":500,"allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Reflections Sealed","params":{"series":[{"format":"reflections_sealed","duration":7,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed'),

  ('Premiere - A New Hope', 'CONSTRUCTED',
   '{"cost":500,"collectionType":"default","lockedDeckType":"","allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Premiere - A New Hope","params":{"series":[{"format":"premiere_anh","duration":7,"matches":10},{"format":"premiere_anh","duration":7,"matches":10},{"format":"premiere_anh","duration":7,"matches":10},{"format":"premiere_anh","duration":7,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed'),

  ('Open', 'CONSTRUCTED',
   '{"cost":500,"collectionType":"default","lockedDeckType":"","allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Open","params":{"series":[{"format":"open","duration":7,"matches":10},{"format":"open","duration":7,"matches":10},{"format":"open","duration":7,"matches":10},{"format":"open","duration":7,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed'),

  ('Cube Draft', 'SOLODRAFT',
   '{"cost":500,"allowTimeExtensions":false,"allowSpectators":true,"showPlayerNames":false,"invitationOnly":false,"registrationInfo":"","decisionTimeoutSeconds":300,"timePerPlayerMinutes":50}',
   '[{"name":"Cube v7 + Objectives","params":{"series":[{"format":"c7obj_draft","duration":28,"matches":10}]}}]',
   '{series} - {month} {yyyy}', 20261101, 0, 1.000, 7, b'0', 'seed');

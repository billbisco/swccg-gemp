-- Indexes for the Events tab's completed-event browser (GET /eventHistory) and calendar (GET /calendar).
-- Run once by hand on an existing database; a database created from database_creation_script.sql still needs it.
-- Only indexes are added; no column changes.
--
-- The browser buckets one month at a time: tournaments by tournament.start (there is no finish column, only
-- stage = 'FINISHED'), leagues by league.end; the calendar reads leagues by start / end and tournaments by start.
-- None of those columns was indexed (league: PRIMARY only; tournament: PRIMARY only).
--
-- tournament_player / tournament_match are read by every details drawer and by the browser's player counts;
-- neither had an index on tournament_id, so both were full scans.  (league_participation already has one on
-- league_type.)

ALTER TABLE `tournament`        ADD INDEX `tournament_stage_start` (`stage`, `start`);
ALTER TABLE `tournament`        ADD INDEX `tournament_start` (`start`);
ALTER TABLE `league`            ADD INDEX `league_end` (`end`);
ALTER TABLE `league`            ADD INDEX `league_start` (`start`);
ALTER TABLE `tournament_player` ADD INDEX `tournament_player_tid` (`tournament_id`);
ALTER TABLE `tournament_match`  ADD INDEX `tournament_match_tid` (`tournament_id`);

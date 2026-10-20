-- League schedules: recurring league definitions the server turns into league rows on its own
-- ("Scheduled (not yet created)" on the Events calendar, "League schedules" in League Admin).
-- Run once by hand on an existing database BEFORE deploying the build that uses it; a database created from
-- database_creation_script.sql already has both changes.
--
-- Modelled on LOTR GEMP's db/2026-09-16_league-schedule.sql and 2026-09-17_league-schedule-link.sql, with the dates
-- in this server's form (yyyyMMdd integers, as league.start / league.end) and the enabled flag OFF by default.
--
-- template / events are JSON text rather than this server's league.parameters string.  That string is positional
-- and differs per league type (sealed: format,start,duration,matches,code,name; constructed: start,collection,
-- count,format,duration,matches,...), and it embeds the per-league values (start date, code, name) a schedule has
-- to fill in each time.  So a schedule stores the New-league form's fields by name (the same fields
-- /admin/league/<type>/create takes) and the server renders the parameters string through the same code the
-- League Admin form uses when it creates each league.  The rendered string still has to fit league.parameters
-- (255 chars); the server checks that when a schedule is saved or previewed.

CREATE TABLE IF NOT EXISTS `league_schedule` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,              -- series name, e.g. "Open", "Reflections Sealed"
  `league_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,       -- CONSTRUCTED / SEALED / SOLODRAFT (the League Admin form's three kinds)
  `template` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,                 -- JSON: the New-league form fields every league of the series shares
  `events` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,                   -- JSON array: [{"name": "...", "params": {"series": [{"format","duration","matches"}, ...]}}, ...]
  `name_pattern` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT '{series} - {event}' ,
  `next_event_date` INT(11) NOT NULL ,                                                -- yyyyMMdd: start day of the next league to create
  `next_event_index` INT(11) NOT NULL DEFAULT 0 ,                                     -- index into events of the next league to create
  `interval_months` DECIMAL(6,3) NOT NULL DEFAULT 1.000 ,                             -- whole part: calendar months; fraction: weeks of a 4-week month (0.25 = weekly)
  `lead_days` INT(11) NOT NULL DEFAULT 7 ,                                            -- create the league this many days before its start day
  `enabled` BIT(1) NOT NULL DEFAULT b'0' ,                                            -- off until an admin turns it on
  `last_created_league_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL ,  -- league.type (code) of the last league created
  `last_run` DATETIME NULL ,
  `last_error` VARCHAR(1000) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL ,
  `created_by` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL ,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

-- Which schedule created a league (NULL for a league an admin made by hand).  With league.start it is the
-- scheduler's idempotency key: a schedule never creates a second league for the same start day.
ALTER TABLE `league` ADD COLUMN `schedule_id` INT(11) NULL DEFAULT NULL;
ALTER TABLE `league` ADD INDEX `league_schedule_start` (`schedule_id`, `start`);

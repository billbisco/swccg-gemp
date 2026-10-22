-- Update script: guest/play metrics (raw events + daily totals)
-- Safe to run multiple times (uses IF NOT EXISTS)
-- Raw rows are deleted after 90 days. Daily totals have no ids and are kept.

-- -----------------------------------------------------
-- Table `gemp-swccg`.`metrics_event`
-- One row per counted event. Never stores IP, username, email, user agent,
-- location, chat or decklists. guest_id is empty when the browser sends
-- Do Not Track / Global Privacy Control, and when the player is logged in.
-- event_time is UTC epoch milliseconds truncated to the minute.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `metrics_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `event_time` BIGINT NOT NULL ,
  `event` VARCHAR(32) NOT NULL ,
  `guest_id` VARCHAR(64) NULL ,
  `game_id` VARCHAR(64) NULL ,
  `logged_in` TINYINT(1) NOT NULL DEFAULT 0 ,
  `details` TEXT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `idx_metrics_event_time` (`event_time`) ,
  INDEX `idx_metrics_event_type` (`event`, `event_time`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `gemp-swccg`.`metrics_daily`
-- Totals for one UTC day. No guest or game ids. Kept indefinitely.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `metrics_daily` (
  `day` DATE NOT NULL ,
  `metric` VARCHAR(64) NOT NULL ,
  `count` INT(11) NOT NULL DEFAULT 0 ,
  PRIMARY KEY (`day`, `metric`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_bin;

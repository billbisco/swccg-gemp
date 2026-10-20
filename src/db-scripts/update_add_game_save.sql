-- Update script: Add game_save table (save/resume)
-- Safe to run multiple times (uses IF NOT EXISTS)
-- Holds one row per saved game: header JSON, gzipped move log, optional Kryo snapshot.
-- Rows are deleted 15 days after updated_at (epoch milliseconds).

CREATE TABLE IF NOT EXISTS `game_save` (
  `game_id` VARCHAR(64) NOT NULL ,
  `owner` VARCHAR(64) NOT NULL ,
  `header` MEDIUMTEXT NOT NULL ,
  `move_log` MEDIUMBLOB NOT NULL ,
  `snapshot` MEDIUMBLOB NULL ,
  `snapshot_log_size` INT(11) NOT NULL DEFAULT 0 ,
  `snapshot_fingerprint` VARCHAR(64) NULL ,
  `updated_at` BIGINT NOT NULL ,
  PRIMARY KEY (`game_id`) ,
  INDEX `idx_game_save_owner` (`owner`) ,
  INDEX `idx_game_save_updated` (`updated_at`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_bin;

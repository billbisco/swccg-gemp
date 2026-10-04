-- Cache of format codes a deck currently passes, plus the collection it was saved from.
-- formats_revision is a hash of swccgFormats.json so stale rows recompute on next list/save.
ALTER TABLE `deck` ADD COLUMN IF NOT EXISTS `valid_formats` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `deck` ADD COLUMN IF NOT EXISTS `formats_revision` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `deck` ADD COLUMN IF NOT EXISTS `source_collection` VARCHAR(80) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;

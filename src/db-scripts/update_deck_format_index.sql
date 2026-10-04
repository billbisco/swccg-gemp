-- Cache of format codes a deck currently passes, plus the collection it was saved from.
-- valid_formats stores format_index triples |code:stamp:P| or |code:stamp:F|.
-- formats_revision is unused (legacy global hash); new writes leave it NULL.
ALTER TABLE `deck` ADD COLUMN IF NOT EXISTS `valid_formats` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `deck` ADD COLUMN IF NOT EXISTS `formats_revision` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `deck` ADD COLUMN IF NOT EXISTS `source_collection` VARCHAR(80) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;

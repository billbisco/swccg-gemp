-- Full player names (swccg-guild/work#249, Bill 2026-11-11 t122u): names are 1-32 characters and may contain spaces.
-- Widens the columns that hold a player name and were 10 characters. Keeps charset, collation and NULL-ness.
-- Safe to run before the new build (only widens) and safe to run twice.
ALTER TABLE `player` MODIFY `name` VARCHAR(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL;
ALTER TABLE `tournament_player` MODIFY `player` VARCHAR(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL;
ALTER TABLE `player_password_reset` MODIFY `created_by` VARCHAR(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL;

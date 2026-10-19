-- Migration: salted password hashes + one-time admin reset codes
-- Safe to run on a live DB; it only widens a column and adds a table.
-- Run BEFORE deploying the code that writes PBKDF2 hashes (they are ~90 chars).

-- 1. Room for "pbkdf2-sha256$<iterations>$<salt>$<hash>". Existing 64-char SHA-256 hex values are untouched
--    and keep working; each one is replaced the next time that player logs in.
ALTER TABLE `player` MODIFY `password` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL;

-- 2. One-time reset codes issued by admins (hash only, 24h expiry, single use).
CREATE TABLE IF NOT EXISTS `player_password_reset` (
  `player_id` INT(11) NOT NULL,
  `code_hash` CHAR(64) CHARACTER SET 'ascii' COLLATE 'ascii_bin' NOT NULL,
  `expires_at` BIGINT NOT NULL,
  `created_by` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL,
  `created_at` BIGINT NOT NULL,
  PRIMARY KEY (`player_id`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin;

-- 3. Accounts left in the OLD reset state (blank password) were claimable by anyone through /register.
--    After this deploy they simply cannot log in until an admin issues a new one-time code.
--    List them (read-only) so admins can re-issue codes to the real owners:
-- SELECT id, name FROM player WHERE password = '';

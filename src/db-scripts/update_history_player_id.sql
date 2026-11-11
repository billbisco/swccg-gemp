-- swccg-guild/work#264: the player id beside the name shown at the table in game_history.
-- A player's games follow the account after a rename; each row keeps the name that was at the table.
-- Rows of bots and guests (no player row) keep a null id. Idempotent. DbGameHistoryDAO runs the same
-- statements once, on its first use, when the columns are missing.
ALTER TABLE game_history ADD COLUMN IF NOT EXISTS winner_id INT(11) NULL, ADD COLUMN IF NOT EXISTS loser_id INT(11) NULL;
ALTER TABLE game_history ADD INDEX IF NOT EXISTS game_history_winner_id (winner_id), ADD INDEX IF NOT EXISTS game_history_loser_id (loser_id);
UPDATE game_history h JOIN player p ON p.name = h.winner SET h.winner_id = p.id WHERE h.winner_id IS NULL;
UPDATE game_history h JOIN player p ON p.name = h.loser SET h.loser_id = p.id WHERE h.loser_id IS NULL;

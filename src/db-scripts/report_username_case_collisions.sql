-- Read-only report: player names that differ only by letter case.
-- After the case-insensitive-username change:
--   * new signups can no longer create such pairs;
--   * a login that matches one of these names exactly still works (exact case wins);
--   * a login typed in some third casing matches neither and fails, so those players must type their exact name.
-- Nothing is merged or renamed automatically. Decide per pair (rename, deactivate, or leave).
SELECT LOWER(name) AS folded_name,
       COUNT(*) AS accounts,
       GROUP_CONCAT(CONCAT(name, ' (id ', id, ', created ', IFNULL(account_creation_date, '?'), ')') ORDER BY id SEPARATOR '; ') AS members
FROM player
GROUP BY LOWER(name)
HAVING COUNT(*) > 1
ORDER BY accounts DESC, folded_name;

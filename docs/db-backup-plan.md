# Database and config backups (GEMP + wiki)

Status: **installed on the production VPS on 2026-10-19** (`swccg-db-backup.timer`).

## What runs

`bin/backup/swccg-nightly-backup.sh`, installed as `/usr/local/sbin/swccg-nightly-backup.sh`, config in
`/etc/swccg-backup/backup.conf` (example: `bin/backup/backup.conf.example`).

| Prefix | Contents | Notes |
|---|---|---|
| `gemp/` | `mariadb-dump --all-databases` of `gemp_swccg_db_1` | accounts, hashes, collections, decks, leagues, history |
| `wiki/` | `wiki-db-*`: dump of `swccg_wiki_db`; `wiki-config-*`: LocalSettings.php, extra/flaggedrevs settings, SwccgChrome.php, compose files, .env | **no images/**: page text and File: uploads are already exported nightly to GitHub (`swccg-backup.timer`) |
| `config/` | /etc/nginx, certbot renewal configs, swccg systemd units, /etc/swccg-backup, GEMP compose files, .env, gemp-swccg.properties | |
| `forum/` | hook stub, disabled (`FORUM_ENABLED=0`; define `forum_backup()` in the conf) | |

Every file is gzip + **age**-encrypted to the public recipient in `/etc/swccg-backup/age-recipients.txt`.
**The private key is not on the VPS.** Restores are decrypted off-box. A `.sha256` sits next to each file;
a DB dump that does not end with `-- Dump completed` is discarded (status 1).

Regenerable MediaWiki caches are dumped schema-only (`WIKI_EXCLUDE_DATA`): `l10n_cache`, `objectcache`,
`searchindex`, `querycache*`, `job`, `module_deps`. That cut the wiki dump from 557 MB to 97 MB.
After restoring the wiki run `php maintenance/run.php rebuildLocalisationCache --force` and
`php maintenance/run.php rebuildtextindex`.

Local retention: 14 days under `/var/backups/swccg/<prefix>/`.

Off-site: Backblaze B2 bucket `swccg-backups`, same prefixes, via `bin/backup/swccg-b2-upload.py`
(native B2 API: authorize, get_upload_url, upload_file; checks the returned size and SHA-1). The key in
`/etc/swccg-ops/b2-backup-writer.env` (root 600, never in git) can write and list buckets only, so a
compromised VPS cannot read or delete backups. The bucket's default retention is COMPLIANCE 30 days.
Old objects are removed only by a bucket lifecycle rule, not by this script.

Never commit DB dumps, keys or env files to GitHub.

## Schedule

`swccg-db-backup.timer`: daily 08:30 UTC (04:30 America/Caracas) + up to 10 min random delay.
The GitHub wiki export (`swccg-backup.timer`) runs at 07:15 UTC.

## Restore (tested 2026-10-19)

On a machine holding the age private key:

```sh
age -d -i swccg-backup-age-PRIVATE-KEY.txt gemp-<stamp>.sql.gz.age | gzip -dc > gemp.sql
docker run -d --rm --name restore-test --network none -e MARIADB_ROOT_PASSWORD=x mariadb:10.11
docker exec -i restore-test mariadb -uroot -px < gemp.sql
```

Compare row counts for `player`, `deck`, `collection`, `league`, `game_history` (GEMP) and `page`, `revision`,
`text`, `content`, `slots`, `actor`, `user`, `flaggedrevs`, `logging` (wiki) with production, then
`docker rm -f restore-test`. Repeat quarterly.

## Legacy password hashes in old backups

Since the follow-up to the hashing change, the server wraps every remaining unsalted SHA-256 hash in
PBKDF2 at start-up. Dumps taken **before** that (including the 2026-10-19 pre-deploy and pre-upgrade
backups) still contain unsalted hashes for players who had not logged in yet. Treat them as sensitive
and delete them when no longer needed.

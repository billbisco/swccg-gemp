# Database backup plan (GEMP + wiki)

Status: **plan only.** Nothing here has been installed or run on the server. Setting it up needs Bill's approval.

## Problem

The only backup job today (`swccg-backup.timer` → `ops/nightly_backup.py`) exports wiki page text and files to GitHub.
Neither database is backed up:

- **GEMP** (`gemp_swccg_db_1`, raw datadir under `/opt/gemp-swccg/database`): accounts, password hashes, collections, decks, league and game history.
- **Wiki** (`swccg_wiki_db`): user accounts, full revision history, FlaggedRevs state.

Losing the disk loses all of it.

## What to run

`bin/backup/gemp-db-backup.sh`, nightly:

- `mariadb-dump`/`mysqldump --single-transaction --all-databases` inside each DB container (consistent snapshot, no lock-out). The root password is read from the container's own environment, never passed on the host command line.
- gzip, a `.sha256` next to each file, and a check that the dump ends with `-- Dump completed` (a truncated dump is not rotated in).
- Files are `umask 077` (they contain password hashes and the wiki user table).
- Local retention: 14 days (`KEEP_DAYS`).
- Off-box copy: `rsync` to `OFFBOX_TARGET` when set.

Expected size: tens of MB compressed; a few seconds to a minute per database.

### Schedule (systemd, proposed)

```ini
# /etc/systemd/system/swccg-db-backup.service
[Service]
Type=oneshot
Environment=BACKUP_DIR=/var/backups/swccg-db
Environment=OFFBOX_TARGET=<choose, see below>
ExecStart=/opt/gemp-swccg/bin/backup/gemp-db-backup.sh

# /etc/systemd/system/swccg-db-backup.timer
[Timer]
OnCalendar=*-*-* 04:30:00 UTC
RandomizedDelaySec=15m
Persistent=true
[Install]
WantedBy=timers.target
```

Check the container names with `docker ps` first; the defaults are `gemp_swccg_db_1` and `swccg_wiki_db`.

## Off-box destination (Bill to choose)

| Option | Cost | Notes |
|---|---|---|
| Hetzner Storage Box (BX11, 1 TB) | about €4/month | Same provider, rsync/ssh built in, separate from the VPS. Simplest. |
| Backblaze B2 / any S3 bucket | cents/month at this size | Use `rclone` instead of rsync; enable object lock / versioning so a compromised VPS cannot delete history. |
| Hetzner server snapshots/backups | 20% of server price | Whole-disk, 7 slots. Good extra layer, not a substitute (same account, coarse restore). Check whether already enabled in the Hetzner console. |

Whatever is chosen, the VPS should only be able to **write** new files there, not delete old ones (append-only key or bucket object lock), so a break-in cannot wipe the backups too.

## Restore test (do once after setup, then quarterly)

1. Copy the latest `gemp-*.sql.gz` to a scratch machine (not production).
2. `docker run -d --name restore-test -e MARIADB_ROOT_PASSWORD=x mariadb:11`
3. `gzip -dc gemp-*.sql.gz | docker exec -i restore-test mariadb -uroot -px`
4. Sanity checks: `SELECT COUNT(*) FROM \`gemp-swccg\`.player;` matches production roughly; a known player's decks are present.
5. Repeat for the wiki dump and, on a staging wiki container, load `Special:Version` and a page history.
6. Write the date and result in the ops log. Delete the scratch container.

## Before the password-hashing deploy

Take one manual run of the script right before applying `db-scripts/update_password_hashing.sql`, and keep that file outside the 14-day rotation.

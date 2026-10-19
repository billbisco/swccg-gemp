#!/usr/bin/env bash
# Nightly logical backup of the GEMP and wiki databases.
# NOT installed or run by this PR. See docs/db-backup-plan.md for setup, off-box copy and restore test.
#
# Usage: gemp-db-backup.sh            (uses defaults below)
#        BACKUP_DIR=/srv/backups GEMP_DB_CONTAINER=gemp_swccg_db_1 gemp-db-backup.sh
#
# Credentials: read inside each container from its own MYSQL_ROOT_PASSWORD / MARIADB_ROOT_PASSWORD
# environment, so no password ever appears on this host's command line or in this script.
set -euo pipefail

BACKUP_DIR="${BACKUP_DIR:-/var/backups/swccg-db}"
KEEP_DAYS="${KEEP_DAYS:-14}"
GEMP_DB_CONTAINER="${GEMP_DB_CONTAINER:-gemp_swccg_db_1}"
WIKI_DB_CONTAINER="${WIKI_DB_CONTAINER:-swccg_wiki_db}"
OFFBOX_TARGET="${OFFBOX_TARGET:-}"   # e.g. "backup@storagebox.example:swccg-db/" (rsync over ssh); empty = skip

stamp="$(date -u +%Y%m%dT%H%M%SZ)"
umask 077
mkdir -p "$BACKUP_DIR"

dump() {
  local container="$1" label="$2"
  local out="$BACKUP_DIR/${label}-${stamp}.sql.gz"
  # --single-transaction: consistent InnoDB snapshot without locking players out.
  docker exec "$container" sh -c '
    PW="${MARIADB_ROOT_PASSWORD:-${MYSQL_ROOT_PASSWORD:-}}"
    DUMP=$(command -v mariadb-dump || command -v mysqldump)
    MYSQL_PWD="$PW" exec "$DUMP" -uroot --single-transaction --quick --routines --events --triggers \
      --hex-blob --default-character-set=utf8mb4 --all-databases
  ' | gzip -9 > "$out.partial"
  # A dump that ends without the completion marker is truncated; keep it out of rotation.
  if gzip -dc "$out.partial" | tail -n 1 | grep -q -- "-- Dump completed"; then
    mv "$out.partial" "$out"
    sha256sum "$out" > "$out.sha256"
    echo "ok $out $(du -h "$out" | cut -f1)"
  else
    echo "FAILED (incomplete dump) $label" >&2
    return 1
  fi
}

status=0
dump "$GEMP_DB_CONTAINER" gemp || status=1
dump "$WIKI_DB_CONTAINER" wiki || status=1

find "$BACKUP_DIR" -name '*.sql.gz*' -mtime +"$KEEP_DAYS" -delete

if [ -n "$OFFBOX_TARGET" ]; then
  rsync -a --partial "$BACKUP_DIR"/ "$OFFBOX_TARGET" || status=1
fi

exit "$status"

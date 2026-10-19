#!/usr/bin/env bash
# swccg nightly backup: GEMP DB, wiki DB + config, server config, forum hook.
# Every artifact is age-encrypted to the PUBLIC recipient(s) in $AGE_RECIPIENTS_FILE.
# The private key is NOT on this host; restores are decrypted off-box.
#
# Layout (mirrored to B2 bucket swccg-backups under the same prefixes):
#   $BACKUP_ROOT/gemp/gemp-<stamp>.sql.gz.age        (+ .sha256 of the encrypted file)
#   $BACKUP_ROOT/wiki/wiki-db-<stamp>.sql.gz.age
#   $BACKUP_ROOT/wiki/wiki-config-<stamp>.tar.gz.age  (LocalSettings.php + settings/compose; NO images/)
#   $BACKUP_ROOT/config/config-<stamp>.tar.gz.age     (nginx, compose files, env files, systemd units)
#   $BACKUP_ROOT/forum/                               (hook stub, disabled)
# Wiki page text and File: uploads are already exported nightly to GitHub (swccg-backup.timer);
# this job never pushes anything to GitHub.
set -uo pipefail

CONF=/etc/swccg-backup/backup.conf
[ -r "$CONF" ] && . "$CONF"

BACKUP_ROOT="${BACKUP_ROOT:-/var/backups/swccg}"
KEEP_DAYS="${KEEP_DAYS:-14}"
AGE_RECIPIENTS_FILE="${AGE_RECIPIENTS_FILE:-/etc/swccg-backup/age-recipients.txt}"
GEMP_DB_CONTAINER="${GEMP_DB_CONTAINER:-gemp_swccg_db_1}"
WIKI_DB_CONTAINER="${WIKI_DB_CONTAINER:-swccg_wiki_db}"
WIKI_DIR="${WIKI_DIR:-/opt/swccg-wiki}"
# Regenerable MediaWiki caches: schema only. After a restore run
#   php maintenance/run.php rebuildLocalisationCache --force ; php maintenance/run.php rebuildtextindex
WIKI_EXCLUDE_DATA="${WIKI_EXCLUDE_DATA-wiki.l10n_cache wiki.objectcache wiki.searchindex wiki.querycache wiki.querycachetwo wiki.querycache_info wiki.job wiki.module_deps}"
FORUM_ENABLED="${FORUM_ENABLED:-0}"
B2_UPLOAD_ENABLED="${B2_UPLOAD_ENABLED:-0}"
B2_ENV_FILE="${B2_ENV_FILE:-/etc/swccg-ops/b2-backup-writer.env}"

stamp="$(date -u +%Y%m%dT%H%M%SZ)"
umask 077
status=0
log() { echo "$(date -u +%FT%TZ) $*"; }

[ -s "$AGE_RECIPIENTS_FILE" ] || { log "FATAL no age recipients at $AGE_RECIPIENTS_FILE"; exit 2; }
command -v age >/dev/null || { log "FATAL age not installed"; exit 2; }
mkdir -p "$BACKUP_ROOT"/{gemp,wiki,config,forum}

enc() { age -R "$AGE_RECIPIENTS_FILE" -o "$1.partial"; }
finish() { # $1 final path; checks age header, writes sha256
  local f="$1"
  if [ -s "$f.partial" ] && head -c 22 "$f.partial" | grep -q "age-encryption.org/v1"; then
    mv "$f.partial" "$f"; ( cd "$(dirname "$f")" && sha256sum "$(basename "$f")" > "$(basename "$f").sha256" )
    log "ok $f $(du -h "$f" | cut -f1)"
  else
    rm -f "$f.partial"; log "FAILED $f"; status=1
  fi
}

dump_db() { # container label dest-dir [exclude-data "db.t1 db.t2"]
  local container="$1" label="$2" dir="$3" excl="${4:-}" out="$3/$2-${stamp}.sql.gz.age" marker
  marker="$(mktemp)"
  # Root password is read inside the container from its own env; never on this host's argv.
  # Everything is dumped with data except $EXCL (regenerable caches), whose schema is appended after.
  docker exec -e EXCL="$excl" "$container" sh -c '
    PW="${MARIADB_ROOT_PASSWORD:-${MYSQL_ROOT_PASSWORD:-}}"; export MYSQL_PWD="$PW"
    DUMP=$(command -v mariadb-dump || command -v mysqldump)
    OPTS="-uroot --single-transaction --quick --routines --events --triggers --hex-blob --default-character-set=utf8mb4"
    IGN=""; for t in $EXCL; do IGN="$IGN --ignore-table=$t"; done
    "$DUMP" $OPTS $IGN --all-databases || exit 1
    for t in $EXCL; do
      printf "\nUSE \`%s\`;\n" "${t%%.*}"
      "$DUMP" $OPTS --no-data --skip-triggers "${t%%.*}" "${t#*.}" || exit 1
    done
  ' | tee >(tail -n 1 > "$marker") | gzip -9 | enc "$out"
  local rc=("${PIPESTATUS[@]}")
  sleep 1
  if [ "${rc[0]}" = 0 ] && grep -q -- "-- Dump completed" "$marker"; then finish "$out"
  else rm -f "$out.partial"; log "FAILED (incomplete dump rc=${rc[*]}) $label"; status=1; fi
  rm -f "$marker"
}

tar_enc() { # out then paths (missing paths skipped)
  local out="$1"; shift; local have=()
  for p in "$@"; do [ -e "$p" ] && have+=("$p"); done
  tar -czf - --ignore-failed-read "${have[@]}" 2>/dev/null | enc "$out"; finish "$out"
}

log "start $stamp"
dump_db "$GEMP_DB_CONTAINER" gemp "$BACKUP_ROOT/gemp"
dump_db "$WIKI_DB_CONTAINER" wiki-db "$BACKUP_ROOT/wiki" "$WIKI_EXCLUDE_DATA"
tar_enc "$BACKUP_ROOT/wiki/wiki-config-${stamp}.tar.gz.age" \
  "$WIKI_DIR/LocalSettings.php" "$WIKI_DIR/extra-settings.php" "$WIKI_DIR/flaggedrevs-settings.php" \
  "$WIKI_DIR/SwccgChrome.php" "$WIKI_DIR/docker-compose.yml" "$WIKI_DIR/docker-compose.localsettings.yml" "$WIKI_DIR/.env"
tar_enc "$BACKUP_ROOT/config/config-${stamp}.tar.gz.age" \
  /etc/nginx /etc/letsencrypt/renewal /etc/systemd/system/swccg-*.service /etc/systemd/system/swccg-*.timer \
  /etc/swccg-backup /opt/gemp-swccg/src/docker-compose.yml /opt/gemp-swccg/src/docker-compose.build.yml \
  /opt/gemp-swccg/src/docker-compose-prod.yml /opt/gemp-swccg/src/.env \
  /opt/gemp-swccg/src/gemp-swccg-common/src/main/resources/gemp-swccg.properties

if [ "$FORUM_ENABLED" = 1 ]; then
  # Hook: define forum_backup() in $CONF to write $BACKUP_ROOT/forum/forum-<stamp>.*.age
  if declare -F forum_backup >/dev/null; then forum_backup "$stamp" || status=1
  else log "FORUM_ENABLED=1 but no forum_backup() defined in $CONF"; status=1; fi
fi

find "$BACKUP_ROOT" -type f \( -name '*.age' -o -name '*.age.sha256' -o -name '*.partial' \) -mtime +"$KEEP_DAYS" -delete

if [ "$B2_UPLOAD_ENABLED" = 1 ]; then
  # Native B2 API (writeFiles-only key, no list/HEAD needed). Bucket default retention: COMPLIANCE 30d.
  if [ "$status" = 0 ]; then
    /usr/local/sbin/swccg-b2-upload.py "$B2_ENV_FILE" "$BACKUP_ROOT" "$stamp" | tee -a "$BACKUP_ROOT/b2-uploads.jsonl" || { log "B2 upload FAILED"; status=1; }
  else
    log "skipping B2 upload because a local step failed"
  fi
fi
log "end status=$status"
exit "$status"

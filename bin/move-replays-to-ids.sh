#!/bin/bash
# swccg-guild/work#264: move replay folders from replays/<name>/ to replays/<account id>/.
# Usage: move-replays-to-ids.sh REPLAYS_DIR UNDO_FILE < name<TAB>id lines (from the player table)
#   e.g. mariadb -N -e 'select name, id from player' | move-replays-to-ids.sh /opt/gemp-swccg/replays undo.tsv
# Moves file by file and never overwrites; a name with no folder is skipped. Every move is written to
# UNDO_FILE as "id/file<TAB>name/file", so it can be reversed:
#   while IFS=$'\t' read -r to from; do mkdir -p "$DIR/${from%/*}"; mv -n "$DIR/$to" "$DIR/$from"; done < UNDO_FILE
# Play reads both places (id first, then name), so this can run before or after the new build starts.
set -euo pipefail
DIR=${1:?replays dir}; UNDO=${2:?undo file}
[ -d "$DIR" ] || { echo "no such dir: $DIR" >&2; exit 1; }
moved=0; skipped=0
while IFS=$'\t' read -r name id; do
  [[ "$id" =~ ^[0-9]+$ ]] || { echo "bad id for a row; skipped" >&2; continue; }
  [ -n "$name" ] && [ "$name" != "$id" ] && [ -d "$DIR/$name" ] || continue
  case "$name" in */*|.*) echo "odd name; skipped" >&2; continue;; esac
  mkdir -p "$DIR/$id"
  for f in "$DIR/$name"/*; do
    [ -e "$f" ] || continue
    b=$(basename "$f")
    if [ -e "$DIR/$id/$b" ]; then skipped=$((skipped+1)); continue; fi
    mv -n "$f" "$DIR/$id/$b"
    printf '%s\t%s\n' "$id/$b" "$name/$b" >> "$UNDO"
    moved=$((moved+1))
  done
  rmdir "$DIR/$name" 2>/dev/null || true
done
echo "moved $moved file(s), skipped $skipped existing"

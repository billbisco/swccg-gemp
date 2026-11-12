#!/usr/bin/env python3
"""Per-set baseline of card test coverage, as TSV on stdout.

    coverage.py                 one row per set, then a TOTAL row
    coverage.py --cards [set]   one row per card (all sets, or just the one given)

Set rows:   set  classes  in_db  with_test_file  pct_with_test  test_methods  ignored  untested_ids  orphan_tests
  classes          card classes Card<set>_<NNN>.java under gemp-swccg-cards (light + dark; _BACK sides are
                   counted as their own classes)
  in_db            of those, how many have an entry in card_blueprint_database.json
  with_test_file   of those, how many have at least one Card_<set>_<NNN>[_Label]_Tests.java (padded or not,
                   either side folder) under gemp-swccg-server tests
  test_methods     @Test / @ParameterizedTest methods in those test files
  ignored          of those, how many carry @Ignore / @Disabled
  untested_ids     count of classes with no test file (the ids themselves are in --cards)
  orphan_tests     Card_*_Tests files in the set's test folder whose id matches no card class

Card rows:  id  side  title  test_files  test_methods  ignored   (test_files space-separated)

Tests that only name a card from a shared/batch file are not counted here; packet.py lists the latter for one card.
"""
import re
import sys

import campaign as c


def set_dirs():
    out = []
    for d in c.CARDS_JAVA.iterdir():
        m = re.fullmatch(r"set(\d+)", d.name)
        if m and d.is_dir():
            out.append(m.group(1))
    return sorted(out, key=int)


def card_rows(set_no):
    db = c.blueprints()
    rows = []
    for side in ("light", "dark"):
        d = c.CARDS_JAVA / ("set" + set_no) / side
        if not d.is_dir():
            continue
        for f in d.iterdir():
            m = c.CLASS_RE.match(f.name)
            if not m:
                continue
            card_id = "%d_%d%s" % (int(m.group(1)), int(m.group(2)), m.group(3))
            tfs = c.test_files(card_id)
            methods = [t for f in tfs for t in c.test_methods(f)]
            bp = db.get(card_id)
            title = bp["title"] if bp else ""
            if not title:
                t = re.search(r"\*\s*Title:\s*(.+)", f.read_text(encoding="utf-8", errors="replace"))
                title = t.group(1).strip() if t else ""
            rows.append({"id": card_id, "side": side, "title": title, "in_db": bp is not None,
                         "test": tfs, "methods": len(methods), "ignored": sum(1 for _, i in methods if i),
                         "num": int(m.group(2))})
    return sorted(rows, key=lambda r: (r["num"], r["id"]))


def main(argv):
    if argv and argv[0] in ("-h", "--help"):
        print(__doc__.strip())
        return 0
    if argv and argv[0] == "--cards":
        sets = [argv[1]] if len(argv) > 1 else set_dirs()
        print("id\tside\ttitle\ttest_files\ttest_methods\tignored")
        for s in sets:
            for r in card_rows(s):
                print("\t".join([r["id"], r["side"], r["title"], " ".join(c.rel(f) for f in r["test"]),
                                 str(r["methods"]), str(r["ignored"])]))
        return 0
    if argv:
        print(__doc__.strip())
        return 2
    print("set\tclasses\tin_db\twith_test_file\tpct_with_test\ttest_methods\tignored\tuntested_ids\torphan_tests")
    tot = [0, 0, 0, 0, 0]
    orphans_by_set = {}
    for cid in c.test_index():
        if c.class_file(cid) is None:
            s_no = c.split_id(cid)[0]
            orphans_by_set[s_no] = orphans_by_set.get(s_no, 0) + len(c.test_files(cid))
    tot_orphans = 0
    for s in set_dirs():
        rows = card_rows(s)
        if not rows:
            continue
        n = len(rows)
        vals = [n, sum(r["in_db"] for r in rows), sum(1 for r in rows if r["test"]),
                sum(r["methods"] for r in rows), sum(r["ignored"] for r in rows)]
        tot = [a + b for a, b in zip(tot, vals)]
        orphans = orphans_by_set.get(s, 0)
        tot_orphans += orphans
        print("%s\t%d\t%d\t%d\t%.1f\t%d\t%d\t%d\t%d" % (s, vals[0], vals[1], vals[2], 100.0 * vals[2] / n, vals[3],
                                                        vals[4], n - vals[2], orphans))
    print("TOTAL\t%d\t%d\t%d\t%.1f\t%d\t%d\t%d\t%d" % (tot[0], tot[1], tot[2], 100.0 * tot[2] / max(tot[0], 1),
                                                      tot[3], tot[4], tot[0] - tot[2], tot_orphans))
    return 0


if __name__ == "__main__":
    import signal
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)
    sys.exit(main(sys.argv[1:]))

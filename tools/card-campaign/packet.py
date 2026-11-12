#!/usr/bin/env python3
"""Prints everything one card's test work starts from, so nobody has to go and find it.

    packet.py <id>            the full packet: card data, existing tests, the Java card class
    packet.py <id> --blind    the analyst's packet: what the card says (text and stats) and nothing
                              of how it is implemented or tested
    packet.py <id> --with-test  the full packet plus the current test file's source

<id> is a blueprint id as in card_blueprint_database.json (1_1, 1_001 and 1_1 are the same card).
Several ids may be given; each packet is separated by a rule.

Card data comes from card_blueprint_database.json.  A card the database does not hold (legacy sets such as
1100+, 2200+, text states like 2_45o) falls back to what the card class itself declares: its header comment,
title, setLore and setGameText strings.  That fallback is printed in blind mode too, since it is the card's
text, not its implementation.

The full packet also lists the test methods of the card's test files (Card_<set>_<NNN>_Tests, plus unpadded
or labelled ones such as Card_1_39_Tests or Card_2_045_TextStates_Tests) and every other
test file that names the card's blueprint id in a string literal (shared/batch tests).
"""
import re
import signal
import sys
from pathlib import Path

import campaign as c

ORDER = ["title", "side", "cardCategory", "cardTypes", "cardSubtype", "uniqueness", "expansionSet", "rarity",
         "deployCost", "destiny", "power", "ability", "politics", "forfeit", "armor", "maneuver", "defenseValue",
         "ferocity", "landspeed", "hyperspeed", "pilotCapacity", "passengerCapacity", "vehicleCapacity",
         "astromechCapacity", "icons", "keywords", "personas", "species", "modelTypes"]
TEXT = ["gameText", "locationDarkSideGameText", "locationLightSideGameText", "lore"]
SKIP = {"cardId", "slug", "slugWithSetName"}


def fmt(value):
    if isinstance(value, list):
        parts = []
        for v in value:
            if isinstance(v, dict) and "icon" in v:
                parts.append(v["icon"] + ("" if v.get("count", 1) == 1 else " x%d" % v["count"]))
            else:
                parts.append(str(v))
        return ", ".join(parts)
    return str(value)


def java_strings(call, src):
    """The string literals of every call(...) in src, concatenated where the argument is "a" + "b"."""
    out = []
    for m in re.finditer(r"\b%s\s*\(((?:\s*\+?\s*\"(?:[^\"\\]|\\.)*\")+)\s*\)" % call, src):
        out.append("".join(bytes(s, "utf-8").decode("unicode_escape", errors="replace")
                           for s in re.findall(r"\"((?:[^\"\\]|\\.)*)\"", m.group(1))))
    return out


def from_class(src):
    """What a card class declares about itself, for cards missing from the database."""
    info = {}
    header = re.search(r"/\*\*(.*?)\*/", src, re.S)
    if header:
        for line in header.group(1).splitlines():
            m = re.match(r"\s*\*\s*(Set|Type|Subtype|Title):\s*(.+)", line)
            if m:
                info[m.group(1).lower()] = m.group(2).strip()
    sup = re.search(r"super\s*\(\s*Side\.(LIGHT|DARK)\s*,([^;]*)\);", src)
    if sup:
        info["side"] = sup.group(1)
        info["super(...)"] = re.sub(r"\s+", " ", sup.group(2)).strip()
    for call, key in (("setGameText", "gameText"), ("setLore", "lore"),
                      ("setLocationDarkSideGameText", "locationDarkSideGameText"),
                      ("setLocationLightSideGameText", "locationLightSideGameText")):
        strings = java_strings(call, src)
        if strings:
            info[key] = " / ".join(strings)
    return info


def other_tests_naming(card_id, own):
    own = set(own)
    pad = c.padded(card_id)
    pat = re.compile(r"\"(?:%s|%s)\"" % (re.escape(card_id), re.escape(pad)))
    hits = []
    for f in c.TEST_ROOT.rglob("*.java"):
        if f in own:
            continue
        try:
            text = f.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        n = len(pat.findall(text))
        if n:
            hits.append((f, n))
    return sorted(hits)


def packet(card_id, blind, with_test):
    card_id = c.norm_id(card_id)
    cls = c.class_file(card_id)
    src = cls.read_text(encoding="utf-8", errors="replace") if cls else None
    bp = c.blueprints().get(card_id)
    print("# %s  %s" % (card_id, (bp or {}).get("title") or (from_class(src).get("title") if src else "?")))

    print("\n## Card data" + ("" if bp else " (not in card_blueprint_database.json; from the card class)"))
    if bp:
        for k in ORDER + sorted(k for k in bp if k not in ORDER and k not in TEXT and k not in SKIP):
            if k in bp and k not in TEXT and k not in SKIP:
                print("%-18s %s" % (k + ":", fmt(bp[k])))
        for k in TEXT:
            if bp.get(k):
                print("\n%s:\n  %s" % (k, bp[k]))
    elif src:
        info = from_class(src)
        for k in ("title", "side", "set", "type", "subtype", "super(...)"):
            if k in info:
                print("%-18s %s" % (k + ":", info[k]))
        for k in TEXT:
            if info.get(k):
                print("\n%s:\n  %s" % (k, info[k]))
    else:
        print("No card class and no database entry for %s." % card_id)
        return 1

    if blind:
        return 0

    tfs = c.test_files(card_id)
    tf = tfs[0] if tfs else None
    print("\n## Tests")
    for f in tfs:
        methods = c.test_methods(f)
        print("%s  (%d test%s)" % (c.rel(f), len(methods), "" if len(methods) == 1 else "s"))
        for name, ignored in methods:
            print("  - " + name + ("  [ignored]" if ignored else ""))
    if not tfs:
        print("No test file (expected %s.java)." % c.test_class_simple_name(card_id))
    others = other_tests_naming(card_id, tfs)
    if others:
        print("Other test files naming \"%s\":" % card_id)
        for f, n in others:
            print("  %s  (%d)" % (c.rel(f), n))

    print("\n## Card class")
    if cls:
        print(c.rel(cls))
        print("```java\n" + src.rstrip() + "\n```")
    else:
        print("No card class (expected %s.java)." % c.class_name(card_id))

    for f in tfs if with_test else []:
        print("\n## Test file source: " + c.rel(f))
        print("```java\n" + f.read_text(encoding="utf-8", errors="replace").rstrip() + "\n```")
    return 0


def main(argv):
    flags = {a for a in argv if a.startswith("--")}
    ids = [a for a in argv if not a.startswith("--")]
    unknown = flags - {"--blind", "--with-test"}
    if not ids or unknown or ("--blind" in flags and "--with-test" in flags):
        print(__doc__.strip())
        return 2
    rc = 0
    for i, card_id in enumerate(ids):
        if i:
            print("\n" + "-" * 100 + "\n")
        try:
            rc |= packet(card_id, "--blind" in flags, "--with-test" in flags)
        except ValueError as e:
            print("error: %s" % e, file=sys.stderr)
            rc |= 2
    return rc


if __name__ == "__main__":
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)
    sys.exit(main(sys.argv[1:]))

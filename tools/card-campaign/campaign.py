"""Shared paths and lookups for the SWCCG card test campaign tools (stdlib only).

Card ids are written as in the blueprint database: <set>_<number>[suffix], e.g. 1_1, 1_001, 200_45, 2_45o.
The number may be padded or not; the class is Card<set>_<NNN><suffix> and the test Card_<set>_<NNN><suffix>_Tests.
"""
import json
import os
import re
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
SRC = REPO / "src"
CARDS_JAVA = SRC / "gemp-swccg-cards" / "src" / "main" / "java" / "com" / "gempukku" / "swccgo" / "cards"
BLUEPRINT_DB = SRC / "gemp-swccg-cards" / "src" / "main" / "resources" / "card_blueprint_database.json"
SERVER = SRC / "gemp-swccg-server"
TEST_ROOT = SERVER / "src" / "test" / "java"
CARD_TESTS = TEST_ROOT / "com" / "gempukku" / "swccgo" / "cards"

ID_RE = re.compile(r"^(\d+)_(\d+)([A-Za-z_]*)$")
CLASS_RE = re.compile(r"^Card(\d+)_(\d+)([A-Za-z_]*)\.java$")
TEST_RE = re.compile(r"^Card_(\d+)_(\d+)([A-Za-z_]*)_Tests\.java$")


def norm_id(card_id):
    """'1_001' -> '1_1', '2_045o' -> '2_45o'.  Raises ValueError on anything else."""
    m = ID_RE.match(card_id.strip())
    if not m:
        raise ValueError("not a card id: %r (expected <set>_<number>, e.g. 1_1)" % card_id)
    return "%d_%d%s" % (int(m.group(1)), int(m.group(2)), m.group(3))


def split_id(card_id):
    m = ID_RE.match(norm_id(card_id))
    return m.group(1), int(m.group(2)), m.group(3)


def padded(card_id):
    s, n, suf = split_id(card_id)
    return "%s_%03d%s" % (s, n, suf)


def class_name(card_id):
    return "Card" + padded(card_id)


def test_class_simple_name(card_id):
    return "Card_" + padded(card_id) + "_Tests"


def _find(root, set_no, filename):
    for side in ("light", "dark"):
        p = root / ("set" + set_no) / side / filename
        if p.is_file():
            return p
    return None


def class_file(card_id):
    s, _, _ = split_id(card_id)
    return _find(CARDS_JAVA, s, class_name(card_id) + ".java")


TEST_NAME_RE = re.compile(r"^Card_(\d+)_(\d+)([a-z]*(?:_BACK)?)((?:_[A-Za-z0-9]+)*?)_Tests\.java$")
_test_index = None


def test_index():
    """normalised card id -> [test files], for every Card_<set>_<n>..._Tests.java under the card tests.
    Accepts padded and unpadded numbers (Card_1_039_Tests, Card_1_39_Tests), labelled extra files
    (Card_2_045_TextStates_Tests), and either side folder.  The file named exactly Card_<set>_<NNN>_Tests
    comes first."""
    global _test_index
    if _test_index is None:
        _test_index = {}
        for f in sorted(CARD_TESTS.rglob("Card_*_Tests.java")):
            m = TEST_NAME_RE.match(f.name)
            if not m:
                continue
            cid = "%d_%d%s" % (int(m.group(1)), int(m.group(2)), m.group(3))
            _test_index.setdefault(cid, []).append(f)
        for cid, files in _test_index.items():
            files.sort(key=lambda f: (f.name != test_class_simple_name(cid) + ".java", f.name))
    return _test_index


def test_files(card_id):
    return list(test_index().get(norm_id(card_id), []))


def test_file(card_id):
    """The card's main test file (the first of test_files), or None."""
    files = test_files(card_id)
    return files[0] if files else None


def test_fqcn(path):
    """The class name a test file declares (its package + file name), so a file kept outside the tree works too."""
    path = Path(path)
    m = re.search(r"^\s*package\s+([\w.]+)\s*;", path.read_text(encoding="utf-8", errors="replace"), re.M)
    return (m.group(1) + "." if m else "") + path.stem


_db = None


def blueprints():
    """cardId (normalised) -> blueprint dict."""
    global _db
    if _db is None:
        with open(BLUEPRINT_DB, encoding="utf-8") as f:
            _db = {norm_id(c["cardId"]): c for c in json.load(f) if ID_RE.match(c.get("cardId", ""))}
    return _db


IGNORE_RE = re.compile(r"@(?:Ignore|Disabled)\b")


def test_methods(path):
    """[(name, ignored)] for the @Test methods of a test file, in file order."""
    text = Path(path).read_text(encoding="utf-8", errors="replace")
    out = []
    for m in re.finditer(r"((?:\s*@\w+(?:\([^)]*\))?)+)\s*public\s+void\s+(\w+)\s*\(", text):
        annotations = m.group(1)
        if re.search(r"@(?:Test|ParameterizedTest)\b", annotations):
            out.append((m.group(2), bool(IGNORE_RE.search(annotations))))
    return out


def card_ids_in_set(set_no):
    """All ids that have a card class in set<set_no> (both sides)."""
    ids = []
    for side in ("light", "dark"):
        d = CARDS_JAVA / ("set" + str(set_no)) / side
        if d.is_dir():
            for f in d.iterdir():
                m = CLASS_RE.match(f.name)
                if m:
                    ids.append("%d_%d%s" % (int(m.group(1)), int(m.group(2)), m.group(3)))
    return ids


def resolve_ids(words):
    """Card ids and selectors (set:1) -> normalised ids."""
    out = []
    for w in words:
        if w.startswith("set:"):
            out.extend(sorted(card_ids_in_set(w[4:]), key=lambda i: (split_id(i)[1], i)))
        else:
            out.append(norm_id(w))
    return out


def rel(p):
    try:
        return os.path.relpath(str(p), str(REPO))
    except ValueError:
        return str(p)

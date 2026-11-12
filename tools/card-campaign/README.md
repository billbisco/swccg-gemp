# Card test campaign tools

Tools for the SWCCG GEMP card test campaign (swccg-guild/work #159), modelled on ketura's LOTR campaign scripts
(PlayersCouncil/gemp-lotr, branch `feature/card-test-overhaul`, `.claude/skills/card-test-campaign/scripts/`).
Python 3, standard library only. Run them from anywhere; they find the repo from their own location.

| Tool | What it does |
|------|--------------|
| `packet.py` | Everything one card's test work starts from |
| `cardtest.py` | Runs chosen cards' test classes, prints pass/fail per test |
| `coverage.py` | Per-set baseline: card classes vs cards with tests vs test methods (TSV) |
| `campaign.py` | Shared paths and id lookups (imported by the others) |
| `runner/CardTestRunner.java` | JUnit 4 runner used by `cardtest.py run`'s fast path |

Card ids are blueprint ids as in `card_blueprint_database.json`: `1_1`, `1_188`, `200_45`. Padded and unpadded
numbers are the same card (`1_001` = `1_1`). The class is `Card<set>_<NNN>` under
`src/gemp-swccg-cards/.../cards/set<set>/{light,dark}/`, the test `Card_<set>_<NNN>_Tests` under
`src/gemp-swccg-server/src/test/java/.../cards/set<set>/{light,dark}/`.

## packet.py

```
python3 tools/card-campaign/packet.py 1_188              # full packet
python3 tools/card-campaign/packet.py 1_188 --blind      # text and stats only
python3 tools/card-campaign/packet.py 1_188 --with-test  # full packet plus the test file source
```

- Card data: title, side, category/type/subtype, uniqueness, set, rarity, every stat, icons, keywords, game text
  (location texts for both sides), lore, from the blueprint database. Cards not in the database (legacy sets
  1100+/2200+, Set 15, 601, text states like `2_45o`) fall back to the class's header comment, title, `setGameText`
  and `setLore`.
- Tests (not with `--blind`): the methods of the card's test files (padded, unpadded or labelled names such as
  `Card_1_39_Tests`, `Card_2_045_TextStates_Tests`), with `[ignored]` marks, and every other test file that
  names the card's id in a string literal (shared or batch tests that use it as a helper).
- Card class (not with `--blind`): the full Java source.

`--blind` is the analyst's packet: write the test plan from what the card says, then compare with the
implementation.

## cardtest.py

```
python3 tools/card-campaign/cardtest.py setup                    # once; again after card-class/engine changes
python3 tools/card-campaign/cardtest.py run 1_1 1_188 1_39       # seconds
python3 tools/card-campaign/cardtest.py run 1_188#MouseDroidVehiclePresentReachWorks
python3 tools/card-campaign/cardtest.py run set:1                # every set 1 card with a test file
python3 tools/card-campaign/cardtest.py run 1_188 --maven        # through Maven/Surefire, no setup needed
```

Output is one line per test, `PASS` / `FAIL` / `IGNORED`, failure messages with the stack trimmed to the test's
own frames, then `TOTAL run N, failed N, ignored N`. Exit 0 = all passed, 1 = a failure, 2 = nothing ran or the
test did not compile.

**Fast path** (default): `setup` runs `mvn -q -B -DskipTests -pl gemp-swccg-server -am install` from `src/` and
records the dependency classpath in `src/gemp-swccg-server/target/cardtest.classpath`. `run` then compiles just
the named test files with `javac` and runs them with JUnit 4 under Surefire's JVM flags (`--add-opens` list,
`-Dgemp.shuffleSeed=1`), from `src/gemp-swccg-server`. Each sibling module's `target/classes` comes before its
installed jar, so after editing a card class, `mvn -q -o -DskipTests -pl gemp-swccg-cards compile` (from `src/`)
is enough. Test file edits are picked up at once. `--out <name>` gives each agent its own compile folder so
parallel runs don't collide. `--verbose` shows the game's log output.

**Maven path** (`--maven`): runs
`mvn -q -B -pl gemp-swccg-server -am -Dtest=<classes> -Dsurefire.failIfNoSpecifiedTests=false -Dcheckstyle.skip test`
and reads per-test results from `target/surefire-reports`. Slower (it builds every module each time) but needs no
setup and matches CI. Use it for JUnit 5 test classes, which the fast path skips.

## coverage.py

```
python3 tools/card-campaign/coverage.py              # one row per set + TOTAL
python3 tools/card-campaign/coverage.py --cards 1    # one row per card of set 1 (omit the set for all)
```

Set columns: `set classes in_db with_test_file pct_with_test test_methods ignored untested_ids orphan_tests`.
`orphan_tests` counts `Card_*_Tests` files whose id has no card class. Card columns:
`id side title test_files test_methods ignored`. Tests that only use a card as a helper in another card's file
are not counted (packet.py lists them for one card). Paste the TSV into the tracker as the baseline.

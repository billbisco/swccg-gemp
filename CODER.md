# Coder brief: finish a batch of Virtual Legacy cards (billbisco/swccg-gemp)

You implement one batch of Players Committee **Virtual Legacy** cards for GEMP (the Star Wars CCG server; Java 21,
Maven, sources under `src/`) on one new branch. The orchestrator opens the PR, reviews it, and releases it in a big
batch. You do not open PRs.

## Inputs

- Brief: `git fetch origin ops/legacy-cleanup && git show FETCH_HEAD:CODER.md`.
- Your packet: `git show FETCH_HEAD:packets/<BATCH>.json`. It lists the cards. For each card it gives:
  - `implementation_id`, the blueprint id: `1240_60` means set 1240, card 60, file `Card1240_060.java`;
  - side and type;
  - `pc`, the Players Committee card JSON. This is the source of truth: `front.gametext`, title (`•` means unique),
    stats, `icons`, `extraText` (keywords), `lore`, `imageUrl`;
  - `drafted_on`: earlier unverified ChatGPT branches that already hold a draft of this card.
- Original Virtual predecessors: many Legacy cards are reprints or tweaks of Original Virtual cards, sets 2xx and
  11xx. Find them by title, for example
  `grep -rl 'Title.Frustration' src/gemp-swccg-cards/src/main/java`. Reuse the correct behaviour and test patterns,
  and keep every printed difference.
- Patterns: other `set12xx` Legacy cards already on master are the house style, for example
  `cards/set1261/dark/Card1261_018.java` and its test `Card_1261_018_Tests.java`.

## Setup

```
git config user.name billbisco && git config user.email billbisco@users.noreply.github.com
git fetch origin master && git checkout -B legacy/<branch-from-packet> origin/master
```

- **No Claude attribution.** No Co-Authored-By or Claude-Session trailers, no "Generated with" text.
- **Pushing.** `git push origin legacy/<branch>`. Never force-push.
- **Don't use the GitHub API.** No PRs, comments or merges.

## Work

1. **Drafts.** For each card with a `drafted_on` branch, start from its draft:
   `git show origin/<draft-branch>:<path>`. Copy only that card's blueprint and test files. Then review the draft
   against the PC JSON as strictly as new code, and fix what is wrong or missing.
2. **Blueprints.** Implement every printed clause with the correct scope and timing:
   - your vs opponent's; here, present, at;
   - once per turn or per game; which phase;
   - use vs lose Force; may vs must; costs vs results;
   - "if not able to otherwise", immunity numbers.

   Also match title, uniqueness, stats, icons, keywords and lore.
3. **Tests.** Write one VirtualTableScenario gameplay test per clause, plus an inverse or boundary that shows it
   doesn't over-apply, plus a stats/identity test.
4. **Enums.** New `GameTextActionId` (or other enum) constants are appended at the end of the list only.
5. **Engine.** If a clause needs engine support, add a small, general engine piece with its own test, and make sure
   existing tests still pass. If it would be a big redesign, leave that clause out, finish the rest, and report it.
6. **Mapping-only printings** (an "(AI)" alternate image, or a printing whose implementation is another blueprint):
   add the line to `src/gemp-swccg-cards/src/main/resources/blueprintMapping.txt` (`printing,implementation`).
   Make sure the image URL for the printing exists in
   `src/gemp-swccg-async/src/main/web/js/gemp-016/cards/CardImages.js`, and add a test or mapping check.

## Verify

From `src/`:
- `mvn -q -B install -DskipTests`.
- `mvn -B -pl gemp-swccg-server test -Dtest='Card_<S>_<NNN>_Tests+...'` for all your cards.
- Before the final push, run the full suite, `mvn -B test`. It must be green.

Commit in small steps; one commit per card is fine.

## Report

- The branch and final SHA.
- One line per card: done or partial, and the missing clause if partial.
- Engine changes made, test counts, full-suite result, and questions that need a ruling.

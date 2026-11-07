# Fix brief: Virtual Legacy card PRs (billbisco/swccg-gemp)

These PRs implement Players Committee **Virtual Legacy** cards for GEMP (the Star Wars CCG server; Java 21, Maven,
sources under `src/`). An independent review found concrete problems, or a missing engine capability, in each PR
in your packet. Your job is to fix them on the existing PR branches so they can go out in the next batch release.

## Your packet

`git fetch origin ops/legacy-cleanup && git show FETCH_HEAD:packets/<GROUP>.json`. For each PR it gives:
- `branch` and `base`;
- the reviewer's verdict, `fix_instructions` / `blocked_on` / `notes`, and per-card issues;
- the Players Committee card JSON (`pc_front`). The card JSON is the source of truth for printed text, stats and
  icons, unless the review notes say the official card image differs. In that case follow the image, as the review
  says.

## Setup (per PR)

```
cd <repo>   # a clone of https://github.com/billbisco/swccg-gemp (there may already be one at /home/claude/swccg-gemp)
git config user.name billbisco && git config user.email billbisco@users.noreply.github.com
git fetch origin <branch>:refs/remotes/origin/<branch>
git checkout -B <branch> origin/<branch>
```

- **No Claude attribution, anywhere.** No Co-Authored-By or Claude-Session trailers, no "Generated with" lines.
- **Commit and push.** Commit with a short message naming the card (for example `1261_11 Kir Kanos: add persona`),
  then `git push origin <branch>`.
- **Don't rewrite the branch.** No rebase and no force-push, except where the packet explicitly says to rebase
  (PR 527).
- **Don't use the GitHub API.** No PR bodies or comments, no ready-for-review, no merging. The orchestrator does
  all of that.

## How to fix

- **Fix every listed issue.** Implement each missing clause as printed. Reuse existing engine pieces and the
  Original Virtual (OVS) predecessor where the review names one. Look at similar cards on master for patterns.
- **Engine changes** (packets marked E, or a fix that needs one):
  - Keep the change minimal and general: a modifier, condition, trigger or query any card could use, never a
    card-ID special case inside shared code.
  - Existing cards must behave exactly as before.
  - Add engine-level tests where sensible, plus the card's gameplay tests.
- **New enum constants** (`GameTextActionId`, `ModifyGameTextType`, `Persona`, `ModifierFlag`, ...): append at
  the end of the constant list only. Never reorder or rename existing ones; saved games depend on the ordinals.
- **Tests:** each printed clause needs a gameplay test that exercises it, including the inverse or boundary that
  shows it doesn't over-apply. Use the existing VirtualTableScenario test framework, as the card's current tests do.
- **Rulings:** if a fix needs a rules judgement the card text does not settle, implement the most literal reading
  of the printed text, and list the question in your report. Don't block on it.
- **If you can't fix something properly** (for example, it needs a large engine redesign), leave that clause out,
  keep everything else working, and explain exactly what is missing and why.

## Verify (each PR)

From `src/`:
- `mvn -q -B install -DskipTests` (about 1–2 minutes).
- Card-only changes: `mvn -B -pl gemp-swccg-server test -Dtest='Card_<S>_<NNN>_Tests'` for every card touched,
  plus any other test class that references the cards or engine pieces you changed.
- Engine changes: the full suite, `mvn -B test`. Every existing test must still pass.

## Report

For each PR, give:
- the branch and the pushed commit SHA;
- what you changed, one or two lines per issue;
- the tests you ran, with pass/fail counts;
- anything not done or needing a ruling.

Be concise.

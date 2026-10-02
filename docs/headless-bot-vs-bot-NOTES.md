# Headless bot-vs-bot spike

Branch: `feature/headless-bot-vs-bot`

## Goal

Run **two in-process `SwccgAiController`s** (no Hall HTTP, no UI client) until a game ends,
logging winner, turns, decision counts, and failures. Batch path runs N games and writes CSV
for bot measurement. Optional **JSONL decision traces** capture compact per-decision rows for
later policy training / eval.

## How to run — single game (spike)

From repo `src/` (Maven reactor root):

```bash
cd /workspace/swccg-gemp/src
mvn -pl gemp-swccg-server -am -DfailIfNoTests=false -Dtest=HeadlessBotVsBotSpikeTest test
```

Useful properties:

| Property | Default | Meaning |
|----------|---------|---------|
| `headless.verbose` | `true` (spike) / `false` (batch) | Progress every N decisions |
| `headless.maxDecisions` | `25000` | Abort if exceeded |
| `headless.maxMillis` | `180000` | Wall-clock abort (3 min) |
| `headless.alsoRando` | `false` | Also run Beginner vs RandoCalAi (spike or batch) |
| `headless.traces` | `false` | Append per-decision JSONL traces |
| `headless.traces.path` | `target/headless-decision-traces.jsonl` | Trace output path |

Example Beginner vs Rando (single game):

```bash
mvn -pl gemp-swccg-server -am \
  -Dtest=HeadlessBotVsBotSpikeTest \
  -Dheadless.alsoRando=true \
  test
```

## How to run — batch self-play (CSV)

Default: **5 games**, Beginner vs Beginner, CSV under the server module `target/`:

```bash
cd /workspace/swccg-gemp/src
mvn -pl gemp-swccg-server -am \
  -Dtest=HeadlessBotVsBotBatchTest \
  -Dheadless.games=5 \
  -Dheadless.dark=BEGINNER \
  -Dheadless.light=BEGINNER \
  test
```

CSV path (from Maven cwd = `gemp-swccg-server/`):

`target/headless-bot-vs-bot-batch.csv`

Batch properties:

| Property | Default | Meaning |
|----------|---------|---------|
| `headless.games` | `5` | Number of games (CI-friendly; raise for measurement) |
| `headless.dark` | `BEGINNER` | Dark AI: `BEGINNER`, `ADVANCED`, `RANDO` |
| `headless.light` | `BEGINNER` | Light AI: same enum |
| `headless.csv` | `target/headless-bot-vs-bot-batch.csv` | Output CSV path |
| `headless.verbose` | `false` | Per-decision progress (noisy for batch) |
| `headless.maxDecisions` | `25000` | Per-game abort |
| `headless.maxMillis` | `180000` | Per-game wall-clock abort |
| `headless.alsoAdvanced` | `false` | Extra optional batch: Beginner vs Advanced (uses `headless.games`, default CSV `target/headless-bot-vs-bot-batch-advanced.csv`) |
| `headless.alsoRando` | `false` | Extra optional batch: Beginner vs Rando |
| `headless.traces` | `false` | Write per-decision JSONL traces |
| `headless.traces.path` | `target/headless-decision-traces.jsonl` | Trace output path |

Larger measurement run example:

```bash
mvn -pl gemp-swccg-server -am \
  -Dtest=HeadlessBotVsBotBatchTest#batchSelfPlay_writesCsv \
  -Dheadless.games=50 \
  -Dheadless.dark=BEGINNER \
  -Dheadless.light=ADVANCED \
  -Dheadless.csv=target/beginner-vs-advanced-50.csv \
  -Dheadless.maxMillis=300000 \
  test
```

CSV columns:

```
gameIndex,darkAi,lightAi,winner,darkDecisions,lightDecisions,darkTurns,lightTurns,elapsedMs,error
```

- `winner` — player id (`~OzzelBot` / `~AckbarBot`) or empty if unfinished
- `error` — empty on success; otherwise stopper / first failure note / thrown exception

The decision loop is **not duplicated**: batch calls `HeadlessBotVsBotRunner.playOneGame` (shared libraries reused across games when `reuseLibraries=true`).

## How to run — decision traces (JSONL)

Enable with `-Dheadless.traces=true`. One JSON object per line (JSONL), shared across the batch
when using `HeadlessBotVsBotBatch`. Default path: `target/headless-decision-traces.jsonl`.

```bash
cd /workspace/swccg-gemp/src
mvn -pl gemp-swccg-server -am   -Dtest=HeadlessBotVsBotBatchTest#batchSelfPlay_writesCsv   -Dheadless.games=1   -Dheadless.dark=BEGINNER   -Dheadless.light=BEGINNER   -Dheadless.traces=true   -Dheadless.traces.path=target/headless-decision-traces.jsonl   test
```

Trace properties:

| Property | Default | Meaning |
|----------|---------|---------|
| `headless.traces` | `false` | Enable JSONL decision traces |
| `headless.traces.path` | `target/headless-decision-traces.jsonl` | Output path (under module cwd / `target/`) |

Each line includes (compact — titles/ids, not full game state):

- `gameId`, `gameIndex`, `decisionIndex`, `ts`
- `playerId`, `side` (`DARK`/`LIGHT`), `aiSkill`
- `turn`, `phase`
- `decisionType`, `decisionId`, optional truncated `decisionText`
- `options` — capped list of `{actionId,cardId,blueprintId,text}` plus small scalars (`min`/`max`/…)
- `optionCount`, `chosen`, `accepted` (false on invalid AI answers)
- `darkLF`, `lightLF`, `darkHand`, `lightHand`

**Gaps / not yet traced:** full board layout, reserve deck tops, force piles beyond LF totals,
AI internal scores, seed/shuffle state. Start with decisionType + options + choice + LF/hand.

CSV batch path is unchanged when traces are off (default).

## Files

| Path | Role |
|------|------|
| `gemp-swccg-server/.../ai/HeadlessBotVsBotRunner.java` | Shared driver: decks, register AIs, decision loop |
| `gemp-swccg-server/.../ai/HeadlessDecisionTraceWriter.java` | Compact JSONL per-decision traces |
| `gemp-swccg-server/.../ai/HeadlessBotVsBotSpikeTest.java` | Single-game JUnit spike |
| `gemp-swccg-server/.../ai/HeadlessBotVsBotBatch.java` | Batch runner + CSV writer (+ optional traces) |
| `gemp-swccg-server/.../ai/HeadlessBotVsBotBatchTest.java` | Batch JUnit entry (Surefire-configurable N / matchup) |
| `docs/headless-bot-vs-bot-NOTES.md` | This file |

## Key code paths

1. **Deck loading** — `DeckSerialization.buildDeckFromContents` with embedded
   Librarian strings `Open 40 card - Beginner Dark/Light` (from `db-scripts/sample_decks.sql`).
2. **Game construction** — same core as `VirtualTableScenario.InitializeGameWithDecks`:
   `DefaultUserFeedback` + `DefaultSwccgGame` + `SwccgoFormatLibrary.getFormat("open")`.
3. **AI registration** — `AiRegistry.register(gameId, playerId, ai)` for **both** seats
   (`~OzzelBot` Dark / `~AckbarBot` Light). Hall only registers one via
   `HallServer.createGame` → `AiRegistry.register` after `AwaitingTable.setAiPlayer`.
4. **Decision loop** — mirrors `SwccgGameMediator.maybeLetAiPlay`:
   - `ai.setGame(game)`
   - `answer = ai.decide(playerId, decision, gameState)`
   - `userFeedback.participantDecided` → `decision.decisionMade(answer)`
   - `game.carryOutPendingActionsUntilDecisionNeeded()`
5. **Why not call mediator only?** — `MAX_AI_CHAIN = 50` in `SwccgGameMediator` resets only
   when a **non-AI** player is pending. With two AIs the Hall path stops after 50 chained
   answers. This spike drives an external loop with no chain cap (only maxDecisions / maxMillis).

## What was learned

- `AiRegistry` already supports multiple AIs per `gameId` (`Map<playerId, SwccgAiController>`).
  Hall seating is the one-AI limitation, not the registry.
- `VirtualTableScenario` is the closest headless harness but is card-test oriented (filler decks,
  manual `StartGame` / `PlayerDecided`). Bot-vs-bot needs full decks + autonomous decide loop.
- Open 40 beginner decks are enough to start a real Open-format game without DB/Librarian.
- Peter’s MCP is unrelated (LLM client only); not used.
- For production Hall bot-vs-bot later: either raise/reset `MAX_AI_CHAIN` for pure-AI tables,
  or extract the spike loop into a shared `AiGameDriver` used by Hall and batch runners.
- Batch reuse of `SwccgCardBlueprintLibrary` / `SwccgoFormatLibrary` across games cuts startup
  cost vs constructing libraries per game.

## Next steps (self-play / training)

1. Move runner/batch to `src/main` (or a thin `gemp-swccg-selfplay` module) for a real CLI jar.
2. Fix or parameterize `MAX_AI_CHAIN` so Hall `playVsAi` can seat two bots the same way.
3. ~~Capture decision traces~~ — JSONL via `HeadlessDecisionTraceWriter` (`headless.traces=true`).
   Next: richer features / AI scores / board snapshots if needed for imitation / RL.
4. Optional: validate decks with format checker before start; add more librarian archetypes.
5. Stability: track repeated invalid answers / stuck phases; auto-forfeit after N stalls.
6. Seed control / deterministic shuffle if the engine exposes it.

## First successful run (2026-10-02 America/Caracas)

```
BeginnerAi vs BeginnerAi, Open 40 beginner decks, format=open
finished=true winner=~OzzelBot (Dark)
decisions=1063 invalid=0
dsTurn=7 lsTurn=6 phase=CONTROL
dsLF=8 lsLF=0
elapsedMs≈14171
byPlayer={~OzzelBot=529, ~AckbarBot=534}
failures=0
```

Command used:

```bash
cd /workspace/swccg-gemp/src
mvn -pl gemp-swccg-server -am -DfailIfNoTests=false -Dtest=HeadlessBotVsBotSpikeTest test
```

## Success criteria checklist

- [x] Code registers two AIs on one game and drives toward completion
- [x] `mvn test` targeted spike green (Beginner vs Beginner finished)
- [x] Notes: how to run, learnings, next steps
- [x] Batch self-play: N games + CSV (Beginner vs Beginner; Advanced/Rando via props)
- [x] Per-decision JSONL traces (compact options + choice + LF/hand; system-property gated)

## Decision-trace verification (2026-10-02 America/Caracas)

```
Beginner vs Beginner, N=1, headless.traces=true
finished=true winner=~OzzelBot
acceptedDecisions=846 traceLines=846 (1:1)
avg ~527 bytes/line, max ~2557
CSV path unchanged when traces off
```

Command:

```bash
cd /workspace/swccg-gemp/src
mvn -pl gemp-swccg-server -am   -Dtest=HeadlessBotVsBotBatchTest#batchSelfPlay_writesCsv   -Dheadless.games=1 -Dheadless.traces=true   -Dheadless.traces.path=target/headless-decision-traces.jsonl   test
```

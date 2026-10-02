# Headless bot-vs-bot spike

Branch: `feature/headless-bot-vs-bot`

## Goal

Run **two in-process `SwccgAiController`s** (no Hall HTTP, no UI client) until a game ends,
logging winner, turns, decision counts, and failures.

## How to run

From repo `src/` (Maven reactor root):

```bash
cd /workspace/swccg-gemp/src
mvn -pl gemp-swccg-server -am -Dtest=HeadlessBotVsBotSpikeTest test
```

Useful properties:

| Property | Default | Meaning |
|----------|---------|---------|
| `headless.verbose` | `true` | Progress every N decisions |
| `headless.maxDecisions` | `25000` | Abort if exceeded |
| `headless.maxMillis` | `180000` | Wall-clock abort (3 min) |
| `headless.alsoRando` | `false` | Also run Beginner vs RandoCalAi |

Example Beginner vs Rando:

```bash
mvn -pl gemp-swccg-server -am \
  -Dtest=HeadlessBotVsBotSpikeTest \
  -Dheadless.alsoRando=true \
  test
```

## Files

| Path | Role |
|------|------|
| `gemp-swccg-server/.../ai/HeadlessBotVsBotRunner.java` | Driver: decks, register AIs, decision loop |
| `gemp-swccg-server/.../ai/HeadlessBotVsBotSpikeTest.java` | JUnit entry |
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

## Next steps (self-play / training)

1. Extract `HeadlessBotVsBotRunner` to `src/main` (or a thin `gemp-swccg-selfplay` module) and
   add a batch CLI: N games, seed control, CSV of winners / turns / decisions / LF.
2. Fix or parameterize `MAX_AI_CHAIN` so Hall `playVsAi` can seat two bots the same way.
3. Capture decision traces (decision type, scored options, chosen answer) for imitation /
   RL datasets — do **not** block on MCP/LLM for self-play.
4. Optional: validate decks with format checker before start; add more librarian archetypes.
5. Stability: track repeated invalid answers / stuck phases; auto-forfeit after N stalls.

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
mvn -pl gemp-swccg-server -am -Dtest=HeadlessBotVsBotSpikeTest test
```

## Success criteria checklist

- [x] Code registers two AIs on one game and drives toward completion
- [x] `mvn test` targeted spike green (Beginner vs Beginner finished)
- [x] Notes: how to run, learnings, next steps

# Pre-errata Legacy versions

Older (pre-errata) texts of Players Committee Virtual Block ("Legacy") cards, playable in **Anything Goes only**.
The **Legacy** format always uses the latest text of every card.

## Id scheme

| | Current card | Pre-errata version |
|---|---|---|
| Set | published sheet set 1200–1292 (e.g. 1261 = Block 6.1) | sheet set **+ 1000** (2200–2292, e.g. 2261) |
| Number | sheet number `n` | `n` for the first older version, `n + 1000` for the next, `n + 2000` … |
| Example | `1261_33` Dark Waters & Krayt Dragon Bones | `2261_33` (Sep 2009 slip text) |
| Class | `cards/set1261/dark/Card1261_033.java` | `cards/set2261/dark/Card2261_033.java` |

- `LegacyVersion.currentPrintingIdOf("2261_33")` → `"1261_33"`; `LegacyVersion.versionIdOf("1261_33", 0)` → `"2261_33"`.
- Always mirror the **published sheet id** (12xx), even when the current card's class lives in set 601
  (blueprintMapping alias), e.g. a version of `1240_8` is `2240_8`.
- **k = 0 is the first older version added** for a card; a later addition takes the next free k (+1000).
  The date order is in the metadata (`printedOn`), not the id.
- The card pool looks at k = 0 .. 5 (`LegacyVersion.MAX_VERSIONS_PER_CARD`; raised from 5 to 6 in batch B41 for Dark Waters, which has six older versions).

- A version may differ from its current card only in a printed stat or icon (Bill: "a change is a change is a change"),
  e.g. Dewback (V) `2210_80` printed MANEUVER 4 where the errata'd card has ARMOR 4. A rename alone is a version too:
  Ice-Heart `2271_5` has the current Ysanne Isard text and differs only in its display title.
- **No change, no errata (Bill, 2026-11-08):** a text that differs only in icon notation (the 2012–13 PC website card
  list spells out or drops icons: "[Pilot] 2", ▼/▲, Immune to Alter) is not a version. The 54 ids that existed only
  because of such texts are aliased in `blueprintMapping.txt` (`RemovedLegacyVersionIdsIconLossTests`).
- **Every publication is a version (Bill, 2026-11-11):** a text that a later document printed again word for word is still a
  version of its own, in date order. E.g. Katana: original `2240_60` (2009 release sheet), PC-E1 `2240_1060` (2012 card
  list), PC-E2 = the 2013 Master List, which repeats the original words, = the current card `1240_60`. So a version may
  have the current card's exact text when another version of the card differs (`LegacyVersionsTests.republishedLater`).
- **Retired version ids:** if a version id that was already released stops being a version (its text became the
  current card's), keep it as an alias of the current card in `blueprintMapping.txt` (after `# END legacy`) so saved
  decks still load, e.g. `2220_161,1220_161`. The version pool skips aliased ids.

## AI printings (Bill, 2026-11-08)

An alternate-image (AI) printing that had its own published text gets its own version cards ("Why wouldn't they?
They are a different image"). An AI printing is the same card as its base printing (title, uniqueness, persona,
deck counts, bans).

- **Version id:** the AI printing's id through the same scheme, keeping the suffix: `1250_45ai` → `2250_45ai`
  (k = 0), `2250_1045ai` (k = 1); class `cards/set2250/light/Card2250_045ai.java` (the class loader pads the number
  and keeps `ai`). An AI printing with a plain numeric id uses the plain scheme: `1240_3` (Juno Eclipse AI) → `2240_3`.
- `currentPrintingId` is the AI printing (`1250_45ai`); `LegacyVersion.currentPrintingIdOf("2250_45ai")` → `1250_45ai`.
- **Class:** extend the base printing's version (`Card2250_045ai extends Card2250_045`) and call `setLegacyVersion`
  with the AI printing's id and source; the text, behaviour and dates are the base version's (errata apply to the card,
  not to one printing). The card pool lists AI versions next to the others (it tries `n` and `nai` for every sheet number).
- **Image:** the AI image that shows that text. If none shows it, reuse the current AI image and list the card as
  needing a CardMaker image.
- **Own current class (only when needed):** an AI printing whose latest text differs from its base card's gets a class
  `cards/set12xx/<side>/Card12xx_NNNai.java`; remove its `12xx_NNai,…` alias from `blueprintMapping.txt`. Deck counts and
  bans still count it as the base printing (`LegacyIds.withoutAlternateImageSuffix` in
  `DefaultSwccgFormat.sameCardBlueprintId`). None needed so far (2026-11-08): every AI printing's latest text is its base
  card's latest text.
- Done: `2240_3` (Juno Eclipse AI), `2240_8ai` (Galen Marek AI; retired in batch B39 — one AI version, aliased to `1240_8ai`), `2250_2ai` (Aurra Sing AI), `2250_44ai`
  (Master Qui-Gon AI), `2250_45ai` (Mace Windu AI), `2281_30ai` (Supreme Chancellor Valorum AI).

## Formats

- `swccgFormats.json`: Anything Goes lists sets 2200, 2210, … 2292 (all 18 sheets, so new versions need no format
  change). Legacy does not list them, so a version is "from set banned in this format" there.
- Deck builder: versions are in the default collection and in Anything Goes format searches; the Set filter has
  "Legacy (pre-errata versions)" (`set:legacy_versions`).

## Same card

A version keeps the current card's title (a different printed name is its Display Title, display only, see "Display Title"), uniqueness, persona, side, type and `ExpansionSet` (the sheet it was printed
on), so uniqueness, persona and "title" rules see one card. Deck counts (restricted list, bans) count a version as its
current printing (`DefaultSwccgFormat.sameCardBlueprintId`), and banning the current card bans its versions.

## Display Title

**Rule (Bill, 2026-11-11):** a card's title (`getTitle()`) is its label, "(V)" included for a Virtual card: "Bail
Organa (V)", "Admiral Ackbar (V)", even when the face prints something else ("Bail Organa", "Admiral Ackbar"). The
face's wording is the card's **Display Title**, set only on cards where it differs. "(V)" never changes the game title:
Virtual cards "are considered to have the same title as the original card (thus meaning they are identical cards for
game purposes)". Hunt Down and Hunt Down (V) are both Hunt Down; Luke Skywalker and Luke Skywalker (V) are both lost to
Monnok. So the Display Title is display only, and every rule (uniqueness, persona, title matching, deck limits) uses
`getTitle()`.

Some versions were printed under an older name (e.g. Grievous, Hunter Of Jedi was printed as Cyborg Commander, Hunter
Of Jedi). Keep the class title the current card's title and add one line after `setLegacyVersion(...)`:

```java
setDisplayTitle("Cyborg Commander, Hunter Of Jedi");
```

- Display only: the game log (card links and full names), deck lists, deck validation messages, the deck builder's
  name sort and the `title` attribute of deck/delivery XML show the Display Title (`GameUtils.getFullName`,
  `getCardLink`, `GameUtils.getDisplayTitle`). In-game card info adds "Printed as <display title>; the same card as <current
  title> for every rule".
- Rules never see it: `getTitle()`/`getTitles()` stay the current title, so uniqueness, persona, `Filters.title`,
  `titleContains`, `sameTitle`, deck limits and cards that name the current title all treat the version as the current
  card. `GameUtils.getRulesFullName` gives the current name.
- Deck builder name search finds the version by either name.
- Write it without uniqueness dots, as printed on the release sheet slip (`printed_title` in
  `ai-team-skills/shared/gemp-legacy-cards/versions/legacy-versions-v3.json`), including "(V)" when the slip has it.
  **"(V)" follows the face (Bill, 2026-11-10):** the sheet's heading over the slip says whether the title has
  "(V)" (the title bar never prints it): "Galen, Secret Apprentice (V)", "Booster's Star Destroyer (V)", but
  "Mara Jade With Lightsaber", "He Is Not Ready & Imperial Propaganda". The full name shows a Display Title exactly as
  given; no " (V)" suffix is added to it even when the current card has one (`isDisplayTitleComplete`). A current card
  whose Master List heading has no "(V)" does not call `setVirtualSuffix(true)` (e.g. Admiral Motti, Battlestation
  Coordinator). Checked by `LegacyVInTitleTests`.
  Only the versions printed before the rename get it (the Master List renames are 2013-04-27); a later version printed
  under the current name has none. On a version, call `setDisplayTitle` after `setLegacyVersion`.
- Names inside game text (Black Leader, Fallen Jedi, Cyborg Commander in other cards' text) are text, not titles.
- Checked by `LegacyVersionDisplayTitleTests`.

### Accented printed titles (Bill, 2026-11-12)

Any card may have a Display Title, not only a Legacy version. Cards whose face prints accents keep the plain title for
every rule and show the printed spelling: `setDisplayTitle("Padmé Naberrie")` right after `super(...)`. Without
`setLegacyVersion` the Display Title is only the printed spelling of the title, so the full name still gets " (V)"
when the card has it ("•Padmé Naberrie (V)"). Cards: 11_8, 11_9, 203_10, 203_36, 1111_13, 601_55 (= 1250_48,
1250_48ai) Padmé Naberrie; 1116_4, 1250_52 Senator Padmé Amidala; 12_24 Ric Olié; 14_29 Ric Olié, Bravo Leader;
12_25 Sabé; 12_26 Saché; 12_33, 201_7 Yané; 200_11, 601_223 (= 1291_40) Dormé; 207_2 Chirrut Îmwe; 209_40 Vaneé.

Name search (deck builder `name:` filter, `SortAndFilterCards.nameContains`): case-insensitive; a word typed without
accents ignores accents in card names ("padme" finds Padmé), a word typed with an accent matches only that accent
("Padmé" finds Padmé, not a plain "Padme"). Checked by `AccentedDisplayTitleTests`.

## Display

- `setLegacyVersion(new LegacyVersion(currentId, label, source, printedOn, supersededOn))` in the constructor.
- **Label (Bill, 2026-11-08):** the original printing has **no label** (pass `""`). Every later version is `<Issuer>-E<n>`: `PC-E1`, `PC-E2`, … (`D-E1` for Decipher); n counts every published change to the card, rephrases included, in date order. Rephrase-only versions get their own card too ("a change is a change is a change").
- A non-empty label is appended to the full name everywhere (game log, deck validation): `Rogue Insertion [PC-E1]`; an original shows the plain title.
  In-game card info shows `Version: <label or Original printing> (<source>; Anything Goes only)`.
- Image: the wiki slip crop of the release sheet that printed that text (the wiki page's `image2` file), added to
  `CardImages.js` after `// END legacy`. Get the URL from the wiki API:
  `https://wiki.swccg.com/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:<image2>`
  (reachable from Bill's PC shell). Check by eye that the slip shows the old text; if no published image shows it,
  make a CardMaker image of that text (ai-team-skills `shared/swccg-cardmaker/LEGACY-VERSION-IMAGES.md`), upload it to
  the wiki and use its URL (batch method, Bill 2026-11-08: every version shows its own text).
- `printedOn` / `supersededOn` are for the period-accurate formats epic (swccg-guild/work#225). When the exact errata
  date is unknown, use the Virtual Cards Master List date (2014-02-04) as an upper bound and say so in the source.

## Current card image (Bill, 2026-11-08)

The current card (12xx/601 id) must show the **latest** text. The generated `// BEGIN legacy … // END legacy` block in
`CardImages.js` takes the PC's card image, which often shows the original text. When it does, add an override line
for the current id in the "Current Legacy cards whose image … shows an older text" section right after `// END legacy`
(a later key in the object literal wins, and the generator only rewrites the block between the markers). The URL is
the published image of the latest text, or a CardMaker recreation uploaded to the wiki
(`shared/swccg-cardmaker/LEGACY-VERSION-IMAGES.md` in ai-team-skills). Example: `1210_124` Faithful Service (V),
PC-E1 → `VB1-123-Faithful_Service-PC-E1-cardmaker.png`; its original version `2210_124` keeps the release-sheet slip.

## Checklist: add a pre-errata version

1. Find the old text and its source (release sheet slip, date) — e.g. `ai-team-skills/shared/gemp-legacy-cards/SLIP-TEXT-AUDIT-2026-11-07.md`.
2. Pick the id: sheet set + 1000, same number (or + 1000 per extra older version). Check the id is free.
3. Copy the current class to `cards/set22xx/<side>/Card22xx_NNN.java` (or restore the old class from git history),
   rename the class, set the old game text/behaviour, keep title/uniqueness/persona/`ExpansionSet` the same, and call
   `setLegacyVersion(...)`. Reuse the current card's `GameTextActionId`s; add new ones only at the end of the enum.
   If the version was printed under an older name, add `setDisplayTitle("…")` (see "Display Title").
   "X is an Out Of Play Interrupt": `ChangeCardSubtypeModifier(self, <X>, CardSubtype.OUT_OF_PLAY)` alone places X out
   of play when completed (`getInterruptType` honours it); no `InterruptPlacedOutOfPlayWhenCompletedModifier` needed.
4. Add the image line to `CardImages.js` (after `// END legacy`).
5. Tests in `src/test/.../cards/set22xx/<side>/Card_22xx_NNN_Tests.java`: identity with the exact old text and version
   info, gameplay tests for each clause that differs from the current card, and a uniqueness test with the current
   card. `LegacyVersionsTests.EveryImplementedVersionIsTheSameCardAsItsCurrentPrinting` checks every version
   automatically (same title/uniqueness/persona/etc., differs in game text or printed stats/icons, label in full name); add the id to
   `PILOT_VERSIONS` there only if you want the explicit format/deck-builder checks.
6. Nothing else: formats, card pool and the set filter pick the new class up by id.

# Text states of one printing (Decipher-era errata)

The same idea for any printing, not only Legacy sheets: an older **text state** of a card is its own blueprint, playable
in **Anything Goes only**. Id design: ai-team-skills `shared/swccg-card-identity/SKILL.md`, "Text states of one
printing (errata)".

## Ids

| Id | Text | Example |
|---|---|---|
| bare `<set>_<n>` | the card's current text (unchanged) | `2_45` Blast The Door, Kid! |
| `<set>_<n>o` | as first published (print) | `2_45o` (A New Hope, 1997) |
| `<set>_<n>d` | Decipher's last word (1998 Glossary / 2002 Supplement) | `2_45d` (Glossary 2.0, Nov 1998) |
| `<set>_<n>o1` … `o9` | intermediate revisions in date order, only when a printing has more than three states | |

- The state letter is part of the blueprint id. An AI printing keeps `ai` before it (`1250_45aio`); a back side keeps
  `_BACK` after it (`7_100d_BACK`).
- **Loader:** `SwccgCardBlueprintLibrary` resolves `2_45d` to class `cards/set2/light/Card2_045d.java`
  (`TextState.paddedCardNumber`). A state whose text equals another id may instead be an alias line in
  `blueprintMapping.txt` (`2_45o,2_45`); an aliased id is not listed in the card pool. An id with a state letter and no
  class or alias is an unknown card (it no longer crashes the loader).
- `TextState.currentIdOf("2_45d")` → `"2_45"`; `TextState.textStateIdOf("2_45", "o")` → `"2_45o"`.

## Same card

A text state keeps the bare card's title, uniqueness, persona, side, type, set and rarity, so uniqueness, persona and
title rules see one card. Deck counts (restricted list) and bans count it as the bare card
(`DefaultSwccgFormat.sameCardBlueprintId`), and banning the bare card bans its text states.

## Metadata and display

`setTextState(new TextState(currentId, label, source, printedOn, supersededOn))` in the constructor.

- **Label:** `Printed` for `o`; `<Issuer>-E<n>` for an errata (`D-E1` = Decipher's first errata, as for Legacy
  versions). The full name shows it: `Blast The Door, Kid! [D-E1]`. In-game card info adds
  `Text: D-E1 (<source>; Anything Goes only)`.
- `printedOn` / `supersededOn`: dates for the period-accurate formats (swccg-guild/work#225); use an upper bound and
  say so when the exact date is unknown.
- `setDisplayTitle(...)` works for a text state printed under another name, as for Legacy versions.

## Formats and deck builder

- A format allows text states with `"textStates": true` in `swccgFormats.json`. Only Anything Goes has it; every other
  format rejects a text state ("Deck contains an older text of a card …"), even when it lists the card's set.
- Card pool: the ids in `gemp-swccg-cards/src/main/resources/textStates.txt` (`TextStateCardPool`) are in the default
  collection and in Anything Goes format searches. Deck builder set filter: "Older card texts (printed / Decipher
  errata)" (`set:text_states`).
- Not built yet: a format **text era** that rewrites bare ids to the state in force on a date (identity skill,
  swccg-guild/work#11), and a deck-load rewrite of states to bare for "latest" formats.

## Checklist: add a text state

1. Find the text and its source on the wiki (the card's article and its `(Errata)` article: `card_uid`, game text,
   errata history, image).
2. Pick the id: bare id + `o`, `d` or `o<n>`.
3. Add `cards/set<N>/<side>/Card<N>_<NNN><state>.java`: copy (or extend) the bare class, set the state's game text and
   behaviour, keep title/uniqueness/persona/set/rarity, and call `setTextState(...)`. If the state plays exactly as
   another class, extend it and change only the text and the text state (e.g. `Card2_045d extends Card2_045`).
4. List the id in `textStates.txt` (`TextStatesTests.EveryTextStateClassIsListedForTheCardPool` checks it).
5. Image: add `"<id>":"<url>"` to `CardImages.js` after `// END legacy` (the wiki file that shows that text, from
   `https://wiki.swccg.com/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:<file>`). Run
   `dupcheck.py` and `node src/test/js/cardImageUrlTests.js`.
6. Tests: a card test with the exact text, stats and text-state info, a gameplay test for each clause that plays
   differently from the bare card, and a check that the bare card is unchanged (e.g.
   `cards/set2/light/Card_2_045_TextStates_Tests.java`). `TextStatesTests` checks every listed state is the same card
   as its bare printing.

Done: `2_45o`, `2_45d` (Blast The Door, Kid!). As printed, `2_45o` excludes only the leaders and characters of
ability > 2 in the battle when it resolves; the 2002 Glossary Supplement added that ones introduced later are excluded
too, which `2_45d` and the bare `2_45` do.

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
- The card pool looks at k = 0 .. 4 (`LegacyVersion.MAX_VERSIONS_PER_CARD`).

- A version may differ from its current card only in a printed stat or icon (Bill: "a change is a change is a change"),
  e.g. Dewback (V) `2210_80` printed MANEUVER 4 where the errata'd card has ARMOR 4. A rename alone is a version too:
  Ice-Heart `2271_5` has the current Ysanne Isard text and differs only in its printed title.
- **No change, no errata (Bill, 2026-11-08):** a text that differs only in icon notation (the 2012–13 PC website card
  list spells out or drops icons: "[Pilot] 2", ▼/▲, Immune to Alter) is not a version. The 54 ids that existed only
  because of such texts are aliased in `blueprintMapping.txt` (`RemovedLegacyVersionIdsIconLossTests`).
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

A version keeps the current card's title (a different printed name is display only, see "Printed title"), uniqueness, persona, side, type and `ExpansionSet` (the sheet it was printed
on), so uniqueness, persona and "title" rules see one card. Deck counts (restricted list, bans) count a version as its
current printing (`DefaultSwccgFormat.sameCardBlueprintId`), and banning the current card bans its versions.

## Printed title

Some versions were printed under an older name (e.g. Grievous, Hunter Of Jedi was printed as Cyborg Commander, Hunter
Of Jedi). Keep the class title the current card's title and add one line after `setLegacyVersion(...)`:

```java
setPrintedTitle("Cyborg Commander, Hunter Of Jedi");
```

- Display only: the game log (card links and full names), deck lists, deck validation messages, the deck builder's
  name sort and the `title` attribute of deck/delivery XML show the printed title (`GameUtils.getFullName`,
  `getCardLink`, `getDisplayTitle`). In-game card info adds "Printed as <printed title>; the same card as <current
  title> for every rule".
- Rules never see it: `getTitle()`/`getTitles()` stay the current title, so uniqueness, persona, `Filters.title`,
  `titleContains`, `sameTitle`, deck limits and cards that name the current title all treat the version as the current
  card. `GameUtils.getRulesFullName` gives the current name.
- Deck builder name search finds the version by either name.
- Write it without uniqueness dots, as printed on the release sheet slip (`printed_title` in
  `ai-team-skills/shared/gemp-legacy-cards/versions/legacy-versions-v3.json`), including "(V)" when the slip has it.
  Only the versions printed before the rename get it (the Master List renames are 2013-04-27); a later version printed
  under the current name has none. `setPrintedTitle` throws unless `setLegacyVersion` was called first.
- Names inside game text (Black Leader, Fallen Jedi, Cyborg Commander in other cards' text) are text, not titles.
- Checked by `LegacyVersionPrintedTitleTests`.

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
   If the version was printed under an older name, add `setPrintedTitle("…")` (see "Printed title").
   "X is an Out Of Play Interrupt": `ChangeCardSubtypeModifier(self, <X>, CardSubtype.OUT_OF_PLAY)` alone places X out
   of play when completed (`getInterruptType` honours it); no `InterruptPlacedOutOfPlayWhenCompletedModifier` needed.
4. Add the image line to `CardImages.js` (after `// END legacy`).
5. Tests in `src/test/.../cards/set22xx/<side>/Card_22xx_NNN_Tests.java`: identity with the exact old text and version
   info, gameplay tests for each clause that differs from the current card, and a uniqueness test with the current
   card. `LegacyVersionsTests.EveryImplementedVersionIsTheSameCardAsItsCurrentPrinting` checks every version
   automatically (same title/uniqueness/persona/etc., differs in game text or printed stats/icons, label in full name); add the id to
   `PILOT_VERSIONS` there only if you want the explicit format/deck-builder checks.
6. Nothing else: formats, card pool and the set filter pick the new class up by id.

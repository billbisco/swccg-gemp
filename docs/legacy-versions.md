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
- The card pool looks at k = 0, 1, 2 (`LegacyVersion.MAX_VERSIONS_PER_CARD`).

- A version may differ from its current card only in a printed stat or icon (Bill: "a change is a change is a change"),
  e.g. Dewback (V) `2210_80` printed MANEUVER 4 where the errata'd card has ARMOR 4.
- **Retired version ids:** if a version id that was already released stops being a version (its text became the
  current card's), keep it as an alias of the current card in `blueprintMapping.txt` (after `# END legacy`) so saved
  decks still load, e.g. `2220_161,1220_161`. The version pool skips aliased ids.

## Formats

- `swccgFormats.json`: Anything Goes lists sets 2200, 2210, … 2292 (all 18 sheets, so new versions need no format
  change). Legacy does not list them, so a version is "from set banned in this format" there.
- Deck builder: versions are in the default collection and in Anything Goes format searches; the Set filter has
  "Legacy (pre-errata versions)" (`set:legacy_versions`).

## Same card

A version keeps the current card's title, uniqueness, persona, side, type and `ExpansionSet` (the sheet it was printed
on), so uniqueness, persona and "title" rules see one card. Deck counts (restricted list, bans) count a version as its
current printing (`DefaultSwccgFormat.sameCardBlueprintId`), and banning the current card bans its versions.

## Display

- `setLegacyVersion(new LegacyVersion(currentId, label, source, printedOn, supersededOn))` in the constructor.
- **Label (Bill, 2026-11-08):** the original printing has **no label** (pass `""`). Every later version is `<Issuer>-E<n>`: `PC-E1`, `PC-E2`, … (`D-E1` for Decipher); n counts every published change to the card, rephrases included, in date order. Rephrase-only versions get their own card too ("a change is a change is a change").
- A non-empty label is appended to the full name everywhere (game log, deck validation): `Rogue Insertion [PC-E1]`; an original shows the plain title.
  In-game card info shows `Version: <label or Original printing> (<source>; Anything Goes only)`.
- Image: the wiki slip crop of the release sheet that printed that text (the wiki page's `image2` file), added to
  `CardImages.js` after `// END legacy`. Get the URL from the wiki API:
  `https://wiki.swccg.com/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:<image2>`
  (reachable from Bill's PC shell). Check by eye that the slip shows the old text; if no slip exists, reuse the
  current image URL and rely on the label.
- `printedOn` / `supersededOn` are for the period-accurate formats epic (swccg-guild/work#225). When the exact errata
  date is unknown, use the Virtual Cards Master List date (2014-02-04) as an upper bound and say so in the source.

## Checklist: add a pre-errata version

1. Find the old text and its source (release sheet slip, date) — e.g. `ai-team-skills/shared/gemp-legacy-cards/SLIP-TEXT-AUDIT-2026-11-07.md`.
2. Pick the id: sheet set + 1000, same number (or + 1000 per extra older version). Check the id is free.
3. Copy the current class to `cards/set22xx/<side>/Card22xx_NNN.java` (or restore the old class from git history),
   rename the class, set the old game text/behaviour, keep title/uniqueness/persona/`ExpansionSet` the same, and call
   `setLegacyVersion(...)`. Reuse the current card's `GameTextActionId`s; add new ones only at the end of the enum.
4. Add the image line to `CardImages.js` (after `// END legacy`).
5. Tests in `src/test/.../cards/set22xx/<side>/Card_22xx_NNN_Tests.java`: identity with the exact old text and version
   info, gameplay tests for each clause that differs from the current card, and a uniqueness test with the current
   card. `LegacyVersionsTests.EveryImplementedVersionIsTheSameCardAsItsCurrentPrinting` checks every version
   automatically (same title/uniqueness/persona/etc., differs in game text or printed stats/icons, label in full name); add the id to
   `PILOT_VERSIONS` there only if you want the explicit format/deck-builder checks.
6. Nothing else: formats, card pool and the set filter pick the new class up by id.

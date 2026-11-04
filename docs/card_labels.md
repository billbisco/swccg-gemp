# card_labels.json: the PC's extra printed labels

`card_labels.json` lists the small labels that the Players Committee prints on its Virtual cards. They don't appear in
the PC's card data (`swccgpc/swccg-card-json`). This file is the single place they are recorded (swccg-guild/work#100;
Bill 2026-11-03, Q1). The wiki `{{Card}}` template and CardMaker copy from it. GEMP itself only stores the file and
doesn't read it, because GEMP shows the PC's card images, which already have the labels printed on them.

## Shape

```json
{
  "_meta": { "schema": 1, "source": "…", "checked": "2026-11-03" },
  "cards": {
    "226_3": {
      "title": "•Endless Legions",
      "image": "https://res.starwarsccg.org/cards/Virtual26-Dark/hires/endlesslegions.png",
      "original_title": "Trooper Charge",
      "concept_credit": "Original concept by the SWCCG Community",
      "art_credit": "Image courtesy of Jesus Conde"
    }
  }
}
```

- **Keys** are GEMP blueprint ids (`gempId` in the PC card JSON).
  - A Virtual Legacy card is keyed by its billbisco/swccg-gemp id (`601_*` or `12xx_*`), because the PC's Legacy JSON
    has no `gempId`. A Legacy card not yet coded in GEMP is keyed `pc-legacy:<PC card id>` until it is.
  - Alternate-image printings share their card's gempId in the PC data (GEMP marks them with `^`). The plain printing
    keeps the gempId; every other printing is keyed `<gempId>^<its extra parenthetical>`, for example `200_71^AI`,
    `203_26^C-Slip AI 2` and `200_95^Tatooine`.
  - A back face gets its own entry with the key `<id>_BACK`.
- **Every checked card has an entry.** A field that is missing means the label was checked and isn't printed. An
  entry with no label fields therefore means "none". `labels.csv` in the ticket evidence shows the same data as a
  table.
- **Text is exactly as printed:** spelling, punctuation, capitals and typographic apostrophes (’). It is never
  corrected silently. One exception: runs of spaces are stored as a single space, even where the PC leaves a wider gap
  (the "& TM.  TM, ®" gap in the Decipher copyright lines). If the PC image itself has a mistake, the text stays as printed and `notes` says what's wrong
  (R5).

## Fields

| Field | What it is | Example | Where it is printed (PC 745 × 1039 face) |
|---|---|---|---|
| `original_title` | The black strip along the bottom edge, left: the title of the card this Virtual card was made from (often a Decipher card). It can be the card's own title. On Virtual Legacy cards it is the slip title, printed with its "•" and "(V)". | "Trooper Charge" (Endless Legions); "Let’s Keep A Little Optimism Here" (A New Hope, VS27); "•The Dark Path (V)" (Vader's Helmet) | Bottom margin, left edge of the text x ≈ 38, cap top ≈ 1009, baseline ≈ 1030; white bold condensed (CardMaker preset `bottom-left`) |
| `original_title_italic` | `true` when the whole strip is set in italics. When only part of it is italic (the PC italicises starship names, e.g. "Boba Fett In *Slave I*"), it holds that italic part as text. | `true` on Devastator (V); "Executor" on Executor: Comm Station | as above |
| `errata_mark` | The errata stamp, bottom right. | "E1", "E2", "D-E1" | Bottom margin, right edge x ≈ 670, baseline ≈ 1030; white heavy oblique (preset `errata`) |
| `ai_mark` | The alternate-image stamp as printed, bottom right, next to the small Players Committee logo: "(AI)" or "(ALT)". An errata mark beside it is stored separately in `errata_mark`. | "(AI)" on Darth Vader, Betrayer Of The Jedi (AI) | Bottom margin, right |
| `slip_mark` | Virtual Legacy cards only: the slip number printed at the right of the bottom strip. | "92E", "D45", "3•20" | Bottom margin, right |
| `concept_credit` | The "Original concept by …" line, normally in the frame's bottom bar, also on Virtual Block cards (often with the tournament it was won at: "Original concept by James Booker, Worlds 2005"). `concept_credit_position` says where it is when it is elsewhere ("left side", "right side", or "side" on landscape Virtual Block cards). | "Original concept by James Booker" | Centred on the frame's bottom bar, baseline ≈ 994; white condensed italic with a dark edge (preset `concept`) |
| `art_credit` | The art credit, normally printed up the left side of the frame beside the art. `art_credit_side: "right"` or `art_credit_position: "bottom bar"` marks the cards that print it elsewhere. | "Image courtesy of Jesus Conde" | Left frame edge, text centre x ≈ 61, reading bottom to top; Akzidenz Condensed, about 25 px cap height |
| `copyright` | The vertical copyright line beside the art, exactly as printed: the standard "ARTWORK NEITHER AFFILIATED WITH NOR ENDORSED BY LUCASFILM LTD." (Virtual Sets 0–27), or the Decipher card's own line that many (V) cards keep (Bill 2026-11-03: every line is recorded, so cards can be recreated exactly). A Decipher line printed in two halves on a system's two sides is joined with " / ". `copyright_side: "left"` when it is printed on the left. On Virtual Block cards only the legible "Artwork not affiliated or endorsed by Lucasfilm Ltd." disclaimer is recorded: their Lucasfilm/Decipher lines can't be read reliably at 350 px. No field: none is printed (Rescue In The Clouds (V), I'm Sorry (V)). | "ARTWORK NEITHER AFFILIATED WITH NOR ENDORSED BY LUCASFILM LTD."; "© 2001 LUCASFILM LTD. & TM. TM, ® & © 2001 DECIPHER INC." (No Civility, Only Politics (V)) | Right frame edge, text centre x ≈ 683, reading bottom to top |
| `dedication` | An "In memory of …" line in the bottom bar. | "In memory of John Anderson, 1982 - 2018" (Puck, Red 12) | As `concept_credit` |
| `bar_text`, `edge_text` | Any other small printed line in the bottom bar, or on a card's edge (`edge_text_position`). | "2004 New Year's Resolution Contest" | — |
| `notes` | Free text for anything a reviewer needs: a misprint on the PC image, a label that is only partly readable, an unusual position. | "PC image prints 'Jabba’s Palace Sabacc (Dark)'" | — |

Landscape faces (1039 × 745) print the title strip along one long edge. The positions above are for the face turned so
that the strip is at the bottom.

## How it is made

`card_labels.json` is built from the PC's own images (Virtual Sets 0–27, Virtual Defensive Shields and Virtual Premium at
hires 745 × 1039; Virtual Legacy blocks at the 350 × 490 holotable size, the largest the PC publishes). Each label region is OCR'd, and each reading is then
checked against the crop of the image. The build scripts and the per-card table are kept in swccg-guild/work
`evidence/100/`.

# Wave VB7.2-a handoff (branch legacy/p-vb7-2-a)

Java in for all 10 cards: 1272_3 Irol (V), 1272_5 Imperial Stockpile, 1272_6 Trophy Of A Bounty Hunter,
1272_7 Where Are You Taking Them?, 1272_10 Blockade Flagship (V), 1272_12 Kashyyyk: Forest Maze,
1272_14 Bail Organa Father Of Rebellion, 1272_15 Rahm Kota Blind Jedi, 1272_16 Commander Wedge (V), 1272_17 Beezer (V).
PC-file edit: GameTextActionId only (11 LEGACY__ ids after LEGACY__ISB_CENTRAL_COMMANDER). No engine files.

Tests: 32/32 green for 1272_3, 10, 14, 15, 16, 17 (wave-only run). Full suite NOT run. legacy_check NOT run.

Gaps (to finish):
- No test classes yet for 1272_5, 1272_6, 1272_7, 1272_12 (code compiles, untested; legacy_check will FAIL "no test class").
- Kota "or if Galen on table, any Reserve Deck" branch untested (test removed mid-debug).
- No mutation checks done.
- Text follows printed sheet: Stockpile return clause (printed, not rev1), WAYTT "gametext"/"just-resolved",
  Wedge "[Virtual Block 4] Their Fire" (VB4 only), [T]/[S] expanded to "deploy/take ... from Reserve Deck; reshuffle";
  these differ from pack `front` and will show as legacy_check FAILs.
- Sheet shows unidentified lore-box set icons on Bail, Trophy (gold), Stockpile (gray), WAYTT (orange) not in pack; not added.
- Interpretations: Bail control-phase Force Pile take = once per control phase; "may not flip back" = while Wedge pilots at a battleground site.

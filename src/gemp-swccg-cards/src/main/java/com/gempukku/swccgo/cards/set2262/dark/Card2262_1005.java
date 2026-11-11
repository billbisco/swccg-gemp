package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Card
 * Title: Broken Concentration (V)
 * PC-E1 version of 1262_5: the PC website card list text (by 2012-07-21; Virtual Block 6 list and Virtual master list
 * to 2013-01-11). Against the original (2262_5): “Jedi Test number” is written “Jedi Test Number”, with a capital N.
 * The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236; Bill
 * 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_1005 extends Card1262_005 {
    public Card2262_1005() {
        super();
        setGameText("Deploy on table. Cancels game text of [Reflections 3] Naboo sites. Training destiny draws are -X, where X = apprentice's ability - Jedi Test Number. Once per game, may place this Effect out of play to reduce your battle damage by 4. Immune to NOOOOOOOOOOOO!. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1262_5", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

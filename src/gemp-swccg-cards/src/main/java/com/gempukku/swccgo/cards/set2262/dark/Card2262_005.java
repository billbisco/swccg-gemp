package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Card
 * Title: Broken Concentration (V)
 * Original version of 1262_5: the text of the Virtual Block 6.2 release sheet (2009-12-07), card #05; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2262_1005) changes it: “Jedi Test number” is
 * written “Jedi Test Number”, with a capital N. The Master List of 2013-04-27 prints the original words again; it is
 * listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_005 extends Card1262_005 {
    public Card2262_005() {
        super();
        setGameText("Deploy on table. Cancels game text of [Reflections 3] Naboo sites. Training destiny draws are -X, where X = apprentice's ability - Jedi Test number. Once per game, may place this Effect out of play to reduce your battle damage by 4. Immune to NOOOOOOOOOOOO!. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1262_5", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #05; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

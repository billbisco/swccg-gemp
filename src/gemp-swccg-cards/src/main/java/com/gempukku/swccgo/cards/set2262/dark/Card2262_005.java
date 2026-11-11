package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Card
 * Title: Broken Concentration (V)
 * Original version of 1262_5. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2262_5) = the Virtual Block 6.2 release sheet (2009-12-07), card #05; PC-E1
 * (2262_1005) = the PC website card list (by 2012-07-21): “Jedi Test number” is written “Jedi Test Number”, with a
 * capital N. PC-E2 (2262_2005) = the PC card image (uploaded 2013-02-25; it may have been made earlier), the original
 * words again; PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2262_005 extends Card1262_005 {
    public Card2262_005() {
        super();
        setGameText("Deploy on table. Cancels game text of [Reflections 3] Naboo sites. Training destiny draws are -X, where X = apprentice's ability - Jedi Test number. Once per game, may place this Effect out of play to reduce your battle damage by 4. Immune to NOOOOOOOOOOOO!. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1262_5", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #05; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

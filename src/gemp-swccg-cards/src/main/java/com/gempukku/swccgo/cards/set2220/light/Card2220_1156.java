package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Out Of Play
 * Title: Through The Force Things You Will See
 *
 * PC-E1 version of 1220_156: the PC website card list text (seen 2012-07-21 .. 2013-01-11; date is an upper bound),
 * "fewer cards in Used Pile than in Force Pile". The card list does not show the card type, so it keeps the release
 * sheet's Out Of Play Interrupt; no once-per-game limit. The Virtual Cards Master List (2013-04-27, PC-E2) is the current
 * text. Plays the same as the original (2220_156); only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1156 extends Card2220_156 {
    public Card2220_1156() {
        super();
        setGameText("If you are about to recirculate at the end of your turn, and you have fewer cards in Used Pile than in Force Pile, place your Used Pile under your Force Pile instead. OR Place the top card of your Used Pile on top of your Force Pile.");
        setLegacyVersion(new LegacyVersion("1220_156", "PC-E1",
                "PC website card list (S1, seen 2012-07-21 .. 2013-01-11; date is an upper bound)", LocalDate.of(2012, 7, 21), LocalDate.of(2012, 11, 19)));
    }
}

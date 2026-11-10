package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_029;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Title: Het Nkik (V)
 * Original version of 1281_29: the Virtual Block 8.1 release sheet text (2012-05-11; the PC card image and the Master List of 2013-04-27
 * print it, the Master List copy being a stale reprint). The PC website card list adds "the" ("where X = the number of
 * deserts"): PC-E1 (by 2012-07-21, 2281_1029, "cancelled") and PC-E2 (by 2012-12-12, "canceled", the current card). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_029 extends Card1281_029 {
    public Card2281_029() {
        super();
        setGameText("Deploys only on Tatooine. While with another Jawa, your Force drains and Force drain bonuses here may not be canceled. During your control phase, may draw X cards from Reserve Deck, where X = number of deserts you control with a Jawa.");
        setLegacyVersion(new LegacyVersion("1281_29", "",
                "Virtual Block 8.1 release sheet (2012-05-11); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}

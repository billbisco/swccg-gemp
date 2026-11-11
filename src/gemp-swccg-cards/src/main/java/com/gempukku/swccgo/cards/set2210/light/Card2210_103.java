package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_227;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: Leia (V)
 * Original version of 1210_103 (class 601_227): the Virtual Block 1 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21, version 2210_1103). The Master List (2013-04-27,
 * PC-E2, the current card) repeats these words, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_103 extends Card601_227 {
    public Card2210_103() {
        super();
        setGameText("While at opponent's Galactic Senate, Leia's game text may not be canceled and may lose 1 Force to place Leia and any character present out of play. During battle, may cancel game text of a leader of ability < 5 with Leia for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1210_103", "",
                "Virtual Block 1 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

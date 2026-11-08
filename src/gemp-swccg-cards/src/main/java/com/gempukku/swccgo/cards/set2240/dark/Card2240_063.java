package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_211;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Title: Stinger (V)
 * Original version of 1240_63: the Virtual Block 4 release sheet text (Dec 2009), "if Chewie or Leia on table". The PC website
 * card list (by Jul 2012, PC-E1) is the current text ("if Leia or Chewie on table"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_063 extends Card601_211 {
    public Card2240_063() {
        super();
        setGameText("May add 1 pilot. Guri deploys -3 aboard. While Guri piloting, during any control phase may draw top card of Reserve Deck (if Chewie or Leia on table, Force Pile instead) and immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1240_63", "",
                "Virtual Block 4 release sheet (2009-12-07), card #63", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

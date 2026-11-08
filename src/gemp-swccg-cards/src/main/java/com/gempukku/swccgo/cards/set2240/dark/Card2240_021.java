package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Blown Clear (V)
 * Original version of 1240_21: the Virtual Block 4 release sheet text (Dec 2009), "if a logistics agenda or admiral here".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_021 extends Card1240_021 {
    public Card2240_021() {
        super();
        setGameText("Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or admiral here, may subtract X from a starship's forfeit and immunity to attrition for remainder of turn, where X = total influence at Conference Room.");
        setLegacyVersion(new LegacyVersion("1240_21", "",
                "Virtual Block 4 release sheet (2009-12-07), card #19", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

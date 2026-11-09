package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_014;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Interrupt
 * Title: A Dark Time For The Rebellion (V)
 * Original version of 1273_14: the Virtual Block 7.3 release sheet text ("retrieve 1 Force", "just drawn destiny", "OR ▲ Control"). The PC card
 * list (PC-E1, the current card, 2012-07-21 to 2013-01-11) writes "1 force", "just drwn destiny" and "Or". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_014 extends Card1273_014 {
    public Card2273_014() {
        super();
        setGameText("If opponent went first this game, retrieve 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate up to 2 Force. OR ▲ Control. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn).");
        setLegacyVersion(new LegacyVersion("1273_14", "",
                "Virtual Block 7.3 release sheet (2011-06-03)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}

package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Pellaeon
 * Original version of 1273_2: the Virtual Block 7.3 release sheet text (Jun 2011; surviving copy re-made Jul 2011).
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: the published typo "reveled", "lose one force",
 * no comma after "if aboard Justifier", "react" without quotes), the current card. Plays the same as the current card;
 * only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_002 extends Card1273_002 {
    public Card2273_002() {
        super();
        setGameText("[Pilot] 2. May be revealed from hand to take Justifier into hand from Reserve Deck; reshuffle and deploy both simultaneously (for -1 Force each). Opponent's passengers here are forfeit = 0. Once per game, if aboard Justifier, may lose 1 Force to move away from battle as a 'react' (at hyperspeed +2).");
        setLegacyVersion(new LegacyVersion("1273_2", "",
                "Virtual Block 7.3 release sheet (2011-06-03; surviving copy re-made 2011-07-25)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}

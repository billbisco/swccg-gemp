package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Darth Vader, Betrayer Of The Jedi
 * Original version of 1261_10: the Virtual Block 6.1 release sheet text. The PC website card list (PC-E1, 2261_1010)
 * dropped "here"; the Virtual Cards Master List (PC-E2, the current card) reorders the clauses. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_010 extends Card1261_010 {
    public Card2261_010() {
        setGameText("[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, lightsabers here may not be 'swung' twice, their immunity to attrition is canceled and you may add one battle destiny. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_10", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #10", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}

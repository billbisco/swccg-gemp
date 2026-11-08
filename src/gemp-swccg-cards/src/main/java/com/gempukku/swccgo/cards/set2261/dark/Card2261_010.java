package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Darth Vader, Betrayer Of The Jedi
 * PC-E1 version of 1261_10: the Virtual Cards Master List wording (2013-04-27), clauses reordered; same play as the Sep 2009 sheet text GEMP's current card shows.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_010 extends Card1261_010 {
    public Card2261_010() {
        setGameText("[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, may add one battle destiny, lightsabers here may not be 'swung' twice, and their immunity to attrition is canceled. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_10", "PC-E1",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }
}

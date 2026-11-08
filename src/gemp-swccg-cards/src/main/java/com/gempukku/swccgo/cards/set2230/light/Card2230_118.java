package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_118;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Weapon
 * Title: Enhanced Proton Torpedoes (V)
 * Original version of 1230_118: the Virtual Block 3 release sheet text (Dec 2009). The PC website card list (by Jul 2012,
 * PC-E1) is the current text: the card list made the Heroic clause its own sentence ("Draw destiny. If on a Heroic starfighter, may use 1 Force to add 3 to total. Target hit if destiny > defense value."); GEMP keeps the +3 as a choice made when firing, as for Concussion Missiles (V) with the same wording.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_118 extends Card1230_118 {
    public Card2230_118() {
        super();
        setGameText("Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny (If on a Heroic starfighter, may first use 1 Force to add 3 to total). Target hit if total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1230_118", "",
                "Virtual Block 3 release sheet (2009-12-07), card #118", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

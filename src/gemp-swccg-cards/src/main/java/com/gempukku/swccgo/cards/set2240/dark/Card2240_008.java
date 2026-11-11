package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_081;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Galen Marek, Starkiller
 * Original version of 1240_8: the Virtual Block 4 release sheet text (Dec 2009, printed as Galen, Secret Apprentice), naming
 * Fallen Jedi (Maris Brood) and Black Leader (Juno Eclipse), renamed on the Virtual Cards Master List (Apr 2013). The title
 * stays Galen Marek, Starkiller so it is the same card. Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_008 extends Card601_081 {
    public Card2240_008() {
        super();
        setLore("'Perhaps you are not as strong as the Emperor thought.'");
        setGameText("Galen's forfeit may not be reduced. Power and defense value +2 while with Fallen Jedi. If defending a battle alone (or defending with Black Leader), may be targeted by Force Lightning. Immune to Clash Of Sabers and attrition < 4.");
        setLegacyVersion(new LegacyVersion("1240_8", "",
                "Virtual Block 4 release sheet (2009-12-07), card #4 (printed as Galen, Secret Apprentice)", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        setDisplayTitle("Galen, Secret Apprentice (V)");
    }
}

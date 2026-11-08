package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_095;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Starship
 * Title: Rogue Shadow
 * Original version of 1240_59: the Virtual Block 4 release sheet text (Dec 2009, printed as Galen's Fighter), naming Black Leader
 * (Juno Eclipse, renamed on the Virtual Cards Master List, Apr 2013). The title stays Rogue Shadow so it is the same card. Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_059 extends Card601_095 {
    public Card2240_059() {
        super();
        setGameText("May add 2 pilots and 2 passengers. If Black Leader piloting, adds one battle destiny. If it just took off, may use its hyperspeed and land. Immune to attrition < 7 if Galen on table, even while landed.");
        setLegacyVersion(new LegacyVersion("1240_59", "",
                "Virtual Block 4 release sheet (2009-12-07), card #58 (printed as Galen's Fighter)", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }
}

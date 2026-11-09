package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_151;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Character
 * Title: Gardulla The Hutt (V)
 * Original version of 1274_1 (class 601_151): the Virtual Block 7.4 release sheet (2011-12-16) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "to any site you occupy" for "to a site you occupy", "force" and "interrupt" in lower case, and no comma after "Once per game"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_001 extends Card601_151 {
    public Card2274_001() {
        super();
        setGameText("During battle, if [Block 4] No Bargain on table, may lose 2 Force to cancel a non-[Immune To Sense] Interrupt. Once per game, during your move phase, may relocate any of your characters here to a site you occupy as a regular move (or vice versa). Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1274_1", "",
                "Virtual Block 7.4 release sheet (2011-12-16)", LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21)));
    }
}

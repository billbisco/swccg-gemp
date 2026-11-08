package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_053;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Title: Accuser (V)
 * Original version of 1230_53: the Virtual Block 3 release sheet text (Dec 2009), "Deploys -3" (the card list reads "Deploy -3").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_053 extends Card1230_053 {
    public Card2230_053() {
        super();
        setGameText("Deploys -3 if Death Star II on table. May add 6 pilots, 8 passengers, 2 vehicles, and 4 TIEs. Permanent pilot aboard provides ability of 2. Power and forfeit +1 for each Rebel starship here.");
        setLegacyVersion(new LegacyVersion("1230_53", "",
                "Virtual Block 3 release sheet (2009-12-07), card #53", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

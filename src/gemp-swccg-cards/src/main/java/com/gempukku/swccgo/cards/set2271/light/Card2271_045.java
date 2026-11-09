package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.set1271.light.Card1271_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Mission
 * Title: Rebel Aces
 * Original version of 1271_45: the Virtual Block 7.1 release sheet text ("astromech capacity", slip 45). The PC card list (PC-E1, the current
 * card, 2012-07-21 to 2013-01-11) spells "capacit". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_045 extends Card1271_045 {
    public Card2271_045() {
        super();
        setGameText("Deploy on table. Your Sandwhirl is canceled. At battleground systems where you have a Rebel of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may ▼ Close Air Support. Your Rebel starfighters without astromech capacity (except Falcon or Artoo-Detoo In Red 5) gain \"May add 1 astromech.\"");
        setLegacyVersion(new LegacyVersion("1271_45", "",
                "Virtual Block 7.1 release sheet (2010-06-18), card #45", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
    }
}

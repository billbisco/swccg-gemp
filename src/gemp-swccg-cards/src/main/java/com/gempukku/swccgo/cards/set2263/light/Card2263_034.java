package com.gempukku.swccgo.cards.set2263.light;

import com.gempukku.swccgo.cards.set1263.light.Card1263_034;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Starship
 * Title: Rogue Squadron X-wing
 * Original version of 1263_34: the Virtual Block 6.3 release sheet text ("this starship may not be lost before the damage
 * segment of battle"), also on the PC card image and the Master List (2013-04-27). Replaced on 2014-07-09 (PC forum
 * "Current Rulings/Errata": "and, if in battle, this card may not be lost before the damage segment"; "not a gameplay
 * change"). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_034 extends Card1263_034 {
    public Card2263_034() {
        super();
        setGameText("May add 1 pilot and 1 astromech. While Rebel Aces on table, pilot is forfeit +2 and this starship may not be lost before the damage segment of battle. Matching starship for any Rogue Squadron pilot.");
        setLegacyVersion(new LegacyVersion("1263_34", "",
                "Virtual Block 6.3 release sheet (released 2010-03-12), card #3-34", LocalDate.of(2010, 3, 12), LocalDate.of(2014, 7, 9)));
    }
}

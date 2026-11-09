package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Jango Fett, The Assassin
 * PC-E1 version of 1281_9: the PC website card list text, still titled The Mandalorian, Father Of Fett (VBlock8Type
 * 2012-07-21; VMasterType 2012-07-21 .. 2013-01-11): "While piloting Slave I with Boba (...), it is immune to attrition
 * <9" for "While piloted by two Fetts (...), Slave I is immune to attrition < 9", and "lost pile". Same play as the
 * original (Slave I piloted by Jango and Boba is immune to attrition < 9 either way), so it keeps the original's
 * behaviour. From 2012-12-12 the Virtual Block 8 list printed the sheet wording again; the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card) printed the sheet wording under the new title Jango Fett, The Assassin.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1009 extends Card2281_009 {
    public Card2281_1009() {
        super();
        setGameText("Adds 3 to the power of anything he pilots. Adds one battle destiny. SD-17 Homing Missile may deploy on (and is a matching weapon for) him, fires for free, and when fired may go to lost pile instead. While piloting Slave I with Boba (unless your [Block 4] objective on table), it is immune to attrition <9.");
        setLegacyVersion(new LegacyVersion("1281_9", "PC-E1",
                "PC website card list (by 2012-07-21), printed as The Mandalorian, Father Of Fett",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 3, 13)));
    }
}

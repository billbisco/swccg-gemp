package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_013;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Jango Fett, The Assassin
 * Original version of 1281_9 (current class 601_13): the Virtual Block 8.1 release sheet (official text as of
 * 2012-05-11; the surviving copy was re-made 2012-07-18) printed this card as "•The Mandalorian, Father Of Fett" with
 * the lore "Trade Federation assassin and scout." and the same game text as the current card. The Virtual Cards
 * Master List (2013-04-27; PC card list by 2013-03-13) renamed it Jango Fett, The Assassin, lore "Trade Federation.
 * Scout." Bill (2026-11-09): title changes are versions. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_009 extends Card601_013 {
    public Card2281_009() {
        super();
        setLore("Trade Federation assassin and scout.");
        setGameText("Adds 3 to the power of anything he pilots. Adds one battle destiny. SD-17 Homing Missile may deploy on (and is a matching weapon for) him, fires for free, and when fired may go to Lost Pile instead. While piloted by two Fetts (unless your [Block 4] objective on table), Slave I is immune to attrition < 9.");
        setLegacyVersion(new LegacyVersion("1281_9", "",
                "Virtual Block 8.1 release sheet (2012-05-11), printed as The Mandalorian, Father Of Fett",
                LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
        setPrintedTitle("The Mandalorian, Father Of Fett");
    }
}

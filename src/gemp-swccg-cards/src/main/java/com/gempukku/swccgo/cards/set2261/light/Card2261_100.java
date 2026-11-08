package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_174;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Scomp Link Access (V)
 * Original version of 1261_100: the Virtual Block 6.1 release sheet text, (download icons, "with < 3 [Light Side Force]"; the PC card list, PC-E1, spells them out).
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_100 extends Card601_174 {
    public Card2261_100() {
        setGameText("USED: ▲ a non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with < 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1261_100", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #101", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}

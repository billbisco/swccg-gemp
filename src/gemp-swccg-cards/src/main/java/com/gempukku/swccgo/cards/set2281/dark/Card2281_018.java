package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_018;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Epic Event
 * Title: Give Yourself To The Dark Side
 * Original version of 1281_18: the Virtual Block 8.1 release sheet text (slip 00; "Son Of Vader from here as if from hand"). The PC card list
 * (PC-E1, the current card, 2012-07-21 to 2013-01-11) drops "from here" and writes "Son of Vader" / "Rebel Starships". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_018 extends Card1281_018 {
    public Card2281_018() {
        super();
        setGameText("If Bring Him Before Me on table, play instead of a Starting Interrupt. ▼ an Endor site and two always-[Immune to Alter] Effects. Deploy on table; may ▲ and stack Son Of Vader here. A Powerful Ally: While Son Of Vader on table, Jedi, Rebels, and Rebel starships lose all immunity to attrition and opponent loses 1 Force at the end of each turn. When Son Of Vader leaves table, stack him here. If Luke crossed over, Your Destiny is lost and may deploy (or persona replace) Son Of Vader from here as if from hand.");
        setLegacyVersion(new LegacyVersion("1281_18", "",
                "Virtual Block 8.1 release sheet (2012-05-11), card #00", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}

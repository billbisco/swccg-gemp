package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_001;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Alien
 * Title: Cad Bane
 * Original version of 1263_1: the Virtual Block 6.3 release sheet printed this card as "Bane, The Bounty Hunter". The
 * Players Committee renamed it Cad Bane; the game text did not change. The slip prints no lore ("bounty hunter" was
 * in the title); the Master List adds the lore "Bounty hunter.". The rename is not in the PC's "New Persona Changes"
 * list of 2013-01-29. Bill (2026-11-09): a title change is a version. Same card and persona as the current printing
 * (printed title is display only). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_001 extends Card1263_001 {
    public Card2263_001() {
        super();
        setLore("");
        setLegacyVersion(new LegacyVersion("1263_1", "",
                "Virtual Block 6.3 release sheet (2010-03-15), card #3-01, printed as Bane, The Bounty Hunter; renamed Cad Bane (PC card list by 2013-03-13, still Bane, The Bounty Hunter on 2013-01-11; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2010, 3, 15), LocalDate.of(2013, 4, 27)));
        setPrintedTitle("Bane, The Bounty Hunter");
    }
}

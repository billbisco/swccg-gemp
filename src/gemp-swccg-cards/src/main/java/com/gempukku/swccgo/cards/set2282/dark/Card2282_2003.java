package com.gempukku.swccgo.cards.set2282.dark;

import com.gempukku.swccgo.cards.set1282.dark.Card1282_003;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Title: You May Start Your Landing (V)
 *
 * PC-E2 version of 1282_3: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_2003 extends Card1282_003 {
    public Card2282_2003() {
        super();
        setGameText("If 5th Marker on table, deploy on table. During your control phase, opponent loses 1 Force for each marker site you control with an AT-AT (limit 1 unless [Virtual Block 2] Imperial Control on table). Once per game, may take Walker Garrison and/or 3rd Marker into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1282_3", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}

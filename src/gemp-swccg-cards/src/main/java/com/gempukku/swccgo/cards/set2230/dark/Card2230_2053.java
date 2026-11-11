package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_053;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Accuser
 *
 * PC-E2 version of 1230_53: the PC card image (accuser.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2053 extends Card1230_053 {
    public Card2230_2053() {
        super();
        setGameText("Deploys -3 if Death Star II on table. May add 6 pilots, 8 passengers, 2 vehicles, and 4 TIEs. Permanent pilot aboard provides ability of 2. Power and forfeit +1 for each Rebel starship here.");
        setLegacyVersion(new LegacyVersion("1230_53", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.set1271.light.Card1271_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Rebel Aces
 *
 * PC-E2 version of 1271_45: the PC card image (rebelaces.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2045 extends Card1271_045 {
    public Card2271_2045() {
        super();
        setGameText("Deploy on table. Your Sandwhirl is canceled. At battleground systems where you have a Rebel of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may ▼ Close Air Support. Your Rebel starfighters without astromech capacity (except Falcon or Artoo-Detoo In Red 5) gain \"May add 1 astromech.\"");
        setLegacyVersion(new LegacyVersion("1271_45", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

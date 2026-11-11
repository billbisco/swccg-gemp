package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_098;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Weapon Levitation & The Empire's Back
 *
 * PC-E2 version of 1261_48: the PC card image (weaponlevitation&theempiresback.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2048 extends Card601_098 {
    public Card2261_2048() {
        super();
        setGameText("USED: Cancel Disarmed or Wookiee Strangle. OR Once per battle involving Vader, reveal a device or character weapon from Used Pile and add its printed destiny number to your total power; reshuffle. LOST: Use 1 Force to ▼ (or retrieve) Vader. OR If opponent just initiated a battle against Vader defending alone at a site, his forfeit cannot be reduced.");
        setLegacyVersion(new LegacyVersion("1261_48", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

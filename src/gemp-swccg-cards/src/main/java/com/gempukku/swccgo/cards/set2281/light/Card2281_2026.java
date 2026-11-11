package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Chewbacca, Walking Carpet
 *
 * PC-E2 version of 1281_26: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2026 extends Card1281_026 {
    public Card2281_2026() {
        super();
        setGameText("[Pilot] 2. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 Force to deploy a droid from Lost Pile. During battle, may target a just-hit character. Target's forfeit is restored to normal.");
        setLegacyVersion(new LegacyVersion("1281_26", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}

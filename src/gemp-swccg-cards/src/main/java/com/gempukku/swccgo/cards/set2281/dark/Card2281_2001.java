package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_001;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Aurra Sing, Deadly Assassin
 *
 * PC-E2 version of 1281_1: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2001 extends Card1281_001 {
    public Card2281_2001() {
        super();
        setGameText("Once per game may deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle: it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (< 4 while armed with a rifle).");
        setLegacyVersion(new LegacyVersion("1281_1", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}

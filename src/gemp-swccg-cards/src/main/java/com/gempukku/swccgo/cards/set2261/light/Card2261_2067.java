package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_067;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Jabba's Prize/Jabba's Prize
 *
 * PC-E2 version of 1261_67: the PC card image (jabbasprizefront.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2067 extends Card1261_067 {
    public Card2261_2067() {
        super();
        setGameText("Jabba's Prize is a Light Side card and does not count towards your deck limit (but subtracts one from the number of cards you may place under your Starting Effect). Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisoned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1.");
        setLegacyVersion(new LegacyVersion("1261_67", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

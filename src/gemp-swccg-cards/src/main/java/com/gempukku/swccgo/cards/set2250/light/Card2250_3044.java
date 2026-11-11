package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_044;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Title: Master Qui-Gon (V)
 * PC-E3 version of 1250_44 (Batch S1, Bill 2026-11-11: a PC card image's punctuation counts): the PC card image (on the PC
 * server by 2013-02-25) prints the forum order without the comma, "Immune to You Are Beaten and attrition." (as AR 2.0
 * Appendix A also retypes it). Followed by the Master List (PC-E4, 2013-04-27, the current card). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_3044 extends Card1250_044 {
    public Card2250_3044() {
        setGameText("Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. May not be Disarmed. Immune to You Are Beaten and attrition.");
        setLegacyVersion(new LegacyVersion("1250_44", "PC-E3",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_044;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Master Qui-Gon (V)
 * PC-E1 version of 1250_44: the PC website card list text ("Immune to You Are Beaten and attrition. May not be
 * Disarmed."; AR 2.0 Appendix A, PC-E2 and the current card, puts "May not be Disarmed" first). Same play as the
 * current card. Image: the current card image.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1044 extends Card1250_044 {
    public Card2250_1044() {
        setGameText("Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. Immune to You Are Beaten and attrition. May not be Disarmed.");
        setLegacyVersion(new LegacyVersion("1250_44", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2012, 8, 27)));
    }
}

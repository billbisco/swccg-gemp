package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_075;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Rebel Scout
 *
 * PC-E2 version of 1230_75: the PC card image (lukeskywalkerrebelscout.gif), dated by its server upload
 * (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2075 extends Card1230_075 {
    public Card2230_2075() {
        super();
        setGameText("Adds 2 to power of anything he pilots. Deploys -2 to a forest. Luke's weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1230_75", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

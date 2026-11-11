package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Jeroen Webb
 *
 * PC-E2 version of 1220_92: the PC card image (jeroenwebb.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2092 extends Card1220_092 {
    public Card2220_2092() {
        super();
        setGameText("If deploying to opponent's mobile site, deploys -2 as an Undercover spy. Nevar Yalnal may not target spies here. Opponent's total influence here is -1 for each of your spies at sites.");
        setLegacyVersion(new LegacyVersion("1220_92", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Diversionary Tactics
 *
 * PC-E2 version of 1230_102: the PC card image (diversionarytactics.gif), dated by its server upload (2013-02-25,
 * a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2102 extends Card1230_102 {
    public Card2230_2102() {
        super();
        setGameText("USED: Target one piloted starship at a system. That starship is hyperspeed -1 for remainder of turn. LOST: If you have two unique (•) piloted Rebel (or [Republic]) starfighters in battle at a system, draw destiny. If destiny < 4, exclude one starship from that battle.");
        setLegacyVersion(new LegacyVersion("1230_102", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

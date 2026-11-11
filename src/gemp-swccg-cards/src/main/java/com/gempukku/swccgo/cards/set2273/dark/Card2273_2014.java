package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_014;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: A Dark Time For The Rebellion (V)
 *
 * PC-E2 version of 1273_14: the PC card image (adarktimefortherebellion.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2014 extends Card1273_014 {
    public Card2273_2014() {
        super();
        setGameText("If opponent went first this game, retrieve 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate up to 2 Force. OR ▲ Control. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn).");
        setLegacyVersion(new LegacyVersion("1273_14", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

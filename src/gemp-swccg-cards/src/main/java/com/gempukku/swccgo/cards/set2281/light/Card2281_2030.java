package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_030;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Supreme Chancellor Valorum (V)
 *
 * PC-E2 version of 1281_30: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2030 extends Card1281_030 {
    public Card2281_2030() {
        super();
        setGameText("Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to ▲ a card with 'governor,' 'political,' or 'Valorum' in lore.");
        setLegacyVersion(new LegacyVersion("1281_30", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}

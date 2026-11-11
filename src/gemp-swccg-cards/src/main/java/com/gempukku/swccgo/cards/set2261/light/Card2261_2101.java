package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_101;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Solo Han (V)
 * PC-E2 version of 1261_101: the Players Committee card image solohan.gif (made by 2013-02-25, server date; placed at
 * that upper bound, Bill 2026-11-11). It prints the release-sheet words without the comma after "vehicle present)".
 * Plays as the current card (the Master List, PC-E3). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2101 extends Card1261_101 {
    public Card2261_2101() {
        super();
        setGameText("Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present) and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_101", "PC-E2",
                "Players Committee card image (made by 2013-02-25, server date)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

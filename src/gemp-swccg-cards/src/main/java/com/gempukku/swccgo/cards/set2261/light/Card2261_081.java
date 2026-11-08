package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_081;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Incom Corporation & Koensayr Manufacturing
 * Original version of 1261_81: the Virtual Block 6.1 sheet text (first printed on the revised copy, 2010-04-20;
 * reprinted by the Master List), "Echo Base Operations is canceled." (dropped by the PC card list, PC-E1, the current
 * card). Gameplay tests: Card_1261_081_Tests.Original*EchoBaseOperations.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_081 extends Card1261_081 {
    public Card2261_081() {
        setGameText("Deploy on a Rebel Base war room. Echo Base Operations is canceled. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_81", "",
                "Virtual Block 6.1 release sheet, revised (2010-04-20), card #80", LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected boolean cancelsEchoBaseOperations() {
        return true;
    }
}

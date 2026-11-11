package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_081;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Incom Corporation & Koensayr Manufacturing
 * PC-E2 version of 1261_81: the Players Committee card image incomcorporation&koensayrmanufacturing.gif (folder Virtual6-Light, upload date 2013-02-25; Bill
 * 2026-11-11: the server date places the image). It repeats the release-sheet words exactly, after the PC website card
 * list (PC-E1) had changed them, so it is a version of its own; the Master List (2013-04-27, PC-E3) repeats the same
 * words and is the current card, so this version plays as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2081 extends Card1261_081 {
    public Card2261_2081() {
        super();
        setGameText("Deploy on a Rebel Base war room. Echo Base Operations is canceled. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_81", "PC-E2",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

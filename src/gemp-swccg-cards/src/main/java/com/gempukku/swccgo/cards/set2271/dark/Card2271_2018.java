package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_018;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Commence Primary Ignition (V)
 *
 * PC-E2 version of 1271_18: the PC card image (commenceprimaryignition.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2018 extends Card1271_018 {
    public Card2271_2018() {
        super();
        setGameText("If Death Star orbits Alderaan (and [Virtual Block 7] Central Core on table), deploy on Death Star and fire Superlaser at Alderaan; it is 'blown away.' Once during your control phase, if Death Star orbiting a planet (except a Rebel Base) without related sites, may fire Superlaser at planet (regardless of Objective restrictions) as follows: Draw destiny. Add 2 if you control that system. If destiny + Y - Z > 8, target is 'blown away' and opponent loses 4 Force from Life Force. Y = total Death Star sites where opponent has no presence. Z = total sites at Yavin 4.");
        setLegacyVersion(new LegacyVersion("1271_18", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

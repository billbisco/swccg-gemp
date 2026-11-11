package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set601.light.Card601_167;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Impressive, Most Impressive (V)
 *
 * PC-E2 version of 1220_145: the PC card image (impressivemostimpressive.gif), dated by its server upload
 * (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2145 extends Card601_167 {
    public Card2220_2145() {
        super();
        setGameText("Cancel All Too Easy, Imperial Barrier (targeting your Jedi), Stunning Leader (where your Jedi participating), or You Are Beaten (except when canceling Uncontrollable Fury). [Immune to Sense] OR Once per game, if opponent just initiated a battle, you may take the first weapons segment action.");
        setLegacyVersion(new LegacyVersion("1220_145", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Tusken Breath Mask (V)
 *
 * PC-E2 version of 1240_92: the PC card image (tuskenbreathmask.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2092 extends Card1240_092 {
    public Card2240_2092() {
        super();
        setGameText("Deploy on an exterior Tatooine battleground site. X = the face up destiny number of your Objective. Once per turn, if all your ability on table is provided by aliens, may add X to your ability during battle at a site. This Effect canceled if opponent controls this site.");
        setLegacyVersion(new LegacyVersion("1240_92", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

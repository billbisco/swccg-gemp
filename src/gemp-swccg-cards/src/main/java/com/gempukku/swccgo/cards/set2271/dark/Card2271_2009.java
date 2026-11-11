package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_076;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Combat Response (V)
 *
 * PC-E2 version of 1271_9: the PC card image (combatresponse.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2009 extends Card601_076 {
    public Card2271_2009() {
        super();
        setGameText("Deploy on table. Tallon Roll is canceled. Once per turn, may reveal an unpiloted starfighter from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. Your unique (•) TIEs (except Scythe Squadron TIEs) at systems may use 1 Force to relocate (as a regular move) to system within 3 parsecs. [Immune to Alter]");
        setLegacyVersion(new LegacyVersion("1271_9", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

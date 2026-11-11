package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.cards.set1272.light.Card1272_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Title: The Camp (V)
 *
 * PC-E2 version of 1272_26: the PC card image (thecamp.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2272_2026 extends Card1272_026 {
    public Card2272_2026() {
        super();
        setGameText("Unless your [Reflections 2] objective on table, deploy on table. If [Permanent Weapon] Maul in battle at a site, and you have no unique (•) character of ability > 3 (except Luke of ability = 4) there, lightsabers there may not be 'swung' twice. Once per game, may take one K'lor'slug or a [Setup] card into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1272_26", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

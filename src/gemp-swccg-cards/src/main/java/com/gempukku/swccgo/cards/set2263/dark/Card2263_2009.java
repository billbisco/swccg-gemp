package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Title: Guild Of Assassins
 *
 * PC-E2 version of 1263_9: the PC card image (guildofassassins.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_2009 extends Card1263_009 {
    public Card2263_2009() {
        super();
        setGameText("Deploy on table. Maul is lost. Once per character, when you deploy Aurra, Galen, Guri, or Mara to a Coruscant or Tatooine site, may take any one card from Used Pile into hand; reshuffle. At locations you occupy, your Force generation may not be prevented or canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1263_9", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}

package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Title: Guild Of Assassins
 * PC-E1 version of 1263_9: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_1009 extends Card1263_009 {
    public Card2263_1009() {
        super();
        setGameText("Deploy on table. Maul is lost. Once per character, when you deploy Aura, Galen, Guri, or Mara to a Coruscant or Tatooine site, may take any one card from Used Pile into hand; reshuffle. At locations you occupy, your Force generation may not be prevented or canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1263_9", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}

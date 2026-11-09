package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Effect
 * Title: Guild Of Assassins
 * Original version of 1263_9: the Virtual Block 6.3 release sheet text, "when you deploy Aurra, Galen, Guri, or Mara".
 * The PC card list (PC-E1, the current card, 2012-07-21 to 2013-01-11) spells it "Aura". Same play (the card checks
 * the Aurra Sing persona either way).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_009 extends Card1263_009 {
    public Card2263_009() {
        setGameText("Deploy on table. Maul is lost. Once per character, when you deploy Aurra, Galen, Guri, or Mara to a Coruscant or Tatooine site, may take any one card from Used Pile into hand; reshuffle. At locations you occupy, your Force generation may not be prevented or canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1263_9", "",
                "Virtual Block 6.3 release sheet (released 2010-03-12), card #3-09", LocalDate.of(2010, 3, 12), LocalDate.of(2012, 7, 21)));
    }
}

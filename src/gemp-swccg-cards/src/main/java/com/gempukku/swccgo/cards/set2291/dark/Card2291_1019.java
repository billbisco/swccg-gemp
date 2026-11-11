package com.gempukku.swccgo.cards.set2291.dark;

import com.gempukku.swccgo.cards.set1291.dark.Card1291_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Geonosis: Separatist Council Room
 * Original version of 1291_19: the Virtual Block 9.1 release sheet (2014-01-10): "While two senators here, they may use
 * their game text as if in a senate majority." The errata posted on the PC forum on 2014-07-09 (p890420) struck "may"
 * (PC-E1, the current card). GEMP plays both texts the same way (the senators are treated as in a senate majority).
 * The id 2291_19 is an alias of the current card (blueprintMapping), so this version is 2291_1019.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_1019 extends Card1291_019 {
    public Card2291_1019() {
        setLocationDarkSideGameText("May deploy Gunray from Reserve Deck; reshuffle. While two senators here, they may use their game text as if in a senate majority.");
        setGameText("Dark: May deploy Gunray from Reserve Deck; reshuffle. While two senators here, they may use their game text as if in a senate majority. Light: While Gunray or opponent's senator here, your spies may not deploy here.");
        setLegacyVersion(new LegacyVersion("1291_19", "",
                "Virtual Block 9.1 release sheet (2014-01-10)", LocalDate.of(2014, 1, 10), LocalDate.of(2014, 7, 9)));
    }
}

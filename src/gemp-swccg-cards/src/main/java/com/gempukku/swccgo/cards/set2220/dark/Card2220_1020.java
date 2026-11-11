package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Creature
 * Title: •One-Arm (V)
 * PC-E1 version of 1220_20 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 2 release sheet original (2220_20) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: two changes: "creatures" is added after the [Selective Creature] icon, and "on table" is added after "Wampa Cave".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1020 extends Card1220_020 {
    public Card2220_1020() {
        super();
        setGameText("Habitat: Hoth sites. Deploys only to a marker site. Wampas are [Selective Creature] creatures. Other wampas are ferocity and defense value +2. Once per game, may [download] Wampa Cave on table.");
        setLegacyVersion(new LegacyVersion("1220_20", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

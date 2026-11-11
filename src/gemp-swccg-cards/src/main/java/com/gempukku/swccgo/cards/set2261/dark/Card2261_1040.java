package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_028;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: •Tarkin's Bounty (V)
 * PC-E1 version of 1261_40 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Set 6: Reflections IV sheet original (2261_2040) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: two changes: "one" is added before the card name, and a comma is added after "Astromech Shortage".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1040 extends Card601_028 {
    public Card2261_1040() {
        super();
        setGameText("Deploy on table. Each Amidala stacked on a Political Effect is a senator. While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash). Once per turn, may ▲ one [Virtual] Astromech Shortage, or lose 2 Force to cancel a Political Effect. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_40", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

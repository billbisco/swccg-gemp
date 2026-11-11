package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_064;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Look Sir, Droids (V)
 * PC-E1 version of 1210_64 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2210_2064) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2210_3064), which prints the original words again.
 * Against the original: two changes: "two always [Immune to Alter] Effects" is reworded as "two Effects which are always [Immune to Alter]", and "Interrupt" in the last sentence is written "interrupt", with a small i.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1064 extends Card1210_064 {
    public Card2210_1064() {
        super();
        setGameText("USED: [upload] a non-[Droid with Presence], non-[Permanent Weapon] droid. OR Place a card from hand on Used Pile. STARTING: [download] two Effects which are always [Immune to Alter]. If you have only one location (with < 3 [Dark Side Force]) on table, may [download] Central Office or Mos Eisley. Place this interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1210_64", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}

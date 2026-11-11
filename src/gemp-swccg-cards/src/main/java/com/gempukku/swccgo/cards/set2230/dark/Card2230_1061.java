package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_061;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Weapon
 * Title: •Feltipern Trevagg's Stun Rifle (V)
 * PC-E1 version of 1230_61 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2061) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3061), which prints the original words again.
 * Against the original: one change: "you" is added ("and you may take").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1061 extends Card1230_061 {
    public Card2230_1061() {
        super();
        setGameText("Use 1 Force to deploy on your bounty hunter (free on Feltipern Trevagg). May target a character for free. Draw destiny. Target captured (and you may take one Interrupt from Lost Pile into hand) if destiny +2 > defense value.");
        setLegacyVersion(new LegacyVersion("1230_61", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}

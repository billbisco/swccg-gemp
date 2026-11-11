package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_065;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: •Aved Luun (V)
 * PC-E1 version of 1230_65 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2065) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3065), which prints the original words again.
 * Against the original: two changes: "one" is dropped before "Jawa Camp", and "on table" is added at the end.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1065 extends Card1230_065 {
    public Card2230_1065() {
        super();
        setGameText("Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May [download] Jawa Camp, Sergeant Doallyn, or Thedit on table.");
        setLegacyVersion(new LegacyVersion("1230_65", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}

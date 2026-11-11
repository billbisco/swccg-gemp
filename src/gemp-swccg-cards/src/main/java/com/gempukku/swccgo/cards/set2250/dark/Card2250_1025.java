package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: •Wipe Them Out, All Of Them (V)
 * PC-E1 version of 1250_25 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2250_2025) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2250_3025), which prints the original words again.
 * Against the original: one change: "weapon" after the [Permanent Weapon] icon is dropped ("[Permanent Weapon] destiny draws").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1025 extends Card1250_025 {
    public Card2250_1025() {
        super();
        setGameText("Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_25", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}

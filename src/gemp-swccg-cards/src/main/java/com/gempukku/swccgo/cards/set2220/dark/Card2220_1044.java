package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_044;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: •Pride Of The Empire (V)
 * PC-E1 version of 1220_44 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 2 release sheet original (2220_44) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: two changes: "TIE/sa" is written "TIE/SA" (both times), and "(or, once per game, retrieve)" moves after "Bombing Run or ComScan Detection". The list also leaves out the uniqueness dot "(•)" after "unique", as it drops the dot from titles; that alone is not counted.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1044 extends Card1220_044 {
    public Card2220_1044() {
        super();
        setGameText("If you just deployed a TIE/SA, deploy on a unique TIE/SA. Bombing Run is [Immune to Control]. May place this card in Used Pile to [upload] Bombing Run or ComScan Detection (or, once per game, retrieve); opponent loses 1 Force. (Immune to Control.)");
        setLegacyVersion(new LegacyVersion("1220_44", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

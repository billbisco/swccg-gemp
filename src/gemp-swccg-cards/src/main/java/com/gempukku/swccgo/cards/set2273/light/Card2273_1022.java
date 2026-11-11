package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: •Dack Ralter (V)
 * PC-E1 version of 1273_22 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 7.3 release sheet original (2273_22) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: four changes: "pilot" is written "Pilot", with a capital P; the comma after "During battle" is dropped; "is" is added ("If Dack is about to leave table"); and the comma after "table" is dropped.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1022 extends Card1273_022 {
    public Card2273_1022() {
        super();
        setGameText("[Pilot] 2. Heroic. Rogue Squadron Pilot. During battle when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack is about to leave table may place him in Used Pile instead.");
        setLegacyVersion(new LegacyVersion("1273_22", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

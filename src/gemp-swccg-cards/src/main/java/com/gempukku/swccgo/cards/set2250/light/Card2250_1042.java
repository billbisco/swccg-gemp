package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_165;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Ki-Adi-Mundi (V)
 * PC-E1 version of 1250_42: the PC website card list text (by 2012-07-21; Virtual Block 5 list and Virtual master
 * list to 2013-01-11). Against the original (2250_42): “Obi-Wan's” is written “Obi-wan's”, with a small w, twice
 * (“Obi-wan's Cape, Obi-wan's Journal”). The Master List of 2013-04-27 (PC-E2, the current card) prints the original
 * words again (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2250_1042 extends Card601_165 {
    public Card2250_1042() {
        super();
        setGameText("While Maul present, Ki-Adi-Mundi is Heroic.  Obi-wan's Cape, Obi-wan's Journal, and [Virtual Block 5] Jedi Lightsaber may deploy on Ki-Adi-Mundi.  Once per game, may exchange two cards from hand with any one card in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1250_42", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

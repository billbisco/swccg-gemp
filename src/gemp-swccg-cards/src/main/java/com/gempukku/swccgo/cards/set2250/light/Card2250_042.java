package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_165;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Ki-Adi-Mundi (V)
 * Original version of 1250_42. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2250_42) = the Virtual Block 5 release sheet (2009-12-07), card #42; PC-E1
 * (2250_1042) = the PC website card list (by 2012-07-21): “Obi-Wan's” is written “Obi-wan's”, with a small w, twice
 * (“Obi-wan's Cape, Obi-wan's Journal”). PC-E2 (2250_2042) = the PC card image (uploaded 2013-02-25; it may have been
 * made earlier), the original words again; PC-E3 = the Master List (2013-04-27), the original words again, the
 * current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_042 extends Card601_165 {
    public Card2250_042() {
        super();
        setGameText("While Maul present, Ki-Adi-Mundi is Heroic.  Obi-Wan's Cape, Obi-Wan's Journal, and [Virtual Block 5] Jedi Lightsaber may deploy on Ki-Adi-Mundi.  Once per game, may exchange two cards from hand with any one card in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1250_42", "",
                "Virtual Block 5 release sheet (2009-12-07), card #42; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_165;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Ki-Adi-Mundi (V)
 * Original version of 1250_42: the text of the Virtual Block 5 release sheet (2009-12-07), card #42; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2250_1042) changes it: “Obi-Wan's” is written
 * “Obi-wan's”, with a small w, twice (“Obi-wan's Cape, Obi-wan's Journal”). The Master List of 2013-04-27 prints the
 * original words again; it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication
 * is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_042 extends Card601_165 {
    public Card2250_042() {
        super();
        setGameText("While Maul present, Ki-Adi-Mundi is Heroic.  Obi-Wan's Cape, Obi-Wan's Journal, and [Virtual Block 5] Jedi Lightsaber may deploy on Ki-Adi-Mundi.  Once per game, may exchange two cards from hand with any one card in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1250_42", "",
                "Virtual Block 5 release sheet (2009-12-07), card #42; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

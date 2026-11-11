package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_072;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Senator Leia Organa
 * Original version of 1240_72. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at
 * its server upload date): original (2240_72) = the Virtual Block 4 release sheet (2009-12-07), card #72; PC-E1
 * (2240_1072) = the PC website card list (by 2012-07-21): “it is immune to Lateral Damage” is written “it is Immune
 * to Lateral Damage”, with a capital I. PC-E2 (2240_2072) = the PC card image (uploaded 2013-02-25; it may have been
 * made earlier), the original words again; PC-E3 = the Master List (2013-04-27), the original words again, the
 * current card. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_072 extends Card1240_072 {
    public Card2240_072() {
        super();
        setGameText("Agendas: justice, rebellion. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). While aboard Alderaan Consular Ship, it is immune to Lateral Damage and attrition. Attrition against opponent is +2 here.");
        setLegacyVersion(new LegacyVersion("1240_72", "",
                "Virtual Block 4 release sheet (2009-12-07), card #72; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

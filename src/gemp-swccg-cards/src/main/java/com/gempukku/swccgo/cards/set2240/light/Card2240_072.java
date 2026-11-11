package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_072;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Senator Leia Organa
 * Original version of 1240_72: the text of the Virtual Block 4 release sheet (2009-12-07), card #72; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2240_1072) changes it: “it is immune to Lateral
 * Damage” is written “it is Immune to Lateral Damage”, with a capital I. The Master List of 2013-04-27 prints the
 * original words again; it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication
 * is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_072 extends Card1240_072 {
    public Card2240_072() {
        super();
        setGameText("Agendas: justice, rebellion. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). While aboard Alderaan Consular Ship, it is immune to Lateral Damage and attrition. Attrition against opponent is +2 here.");
        setLegacyVersion(new LegacyVersion("1240_72", "",
                "Virtual Block 4 release sheet (2009-12-07), card #72; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

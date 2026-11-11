package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_072;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Senator Leia Organa
 * PC-E1 version of 1240_72: the PC website card list text (by 2012-07-21; Virtual Block 4 list and Virtual master
 * list to 2013-01-11). Against the original (2240_72): “it is immune to Lateral Damage” is written “it is Immune to
 * Lateral Damage”, with a capital I. The Master List of 2013-04-27 (PC-E2, the current card) prints the original
 * words again (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_1072 extends Card1240_072 {
    public Card2240_1072() {
        super();
        setGameText("Agendas: justice, rebellion. When deployed, may play a Defensive Shield from under your Starting Effect (as if from hand). While aboard Alderaan Consular Ship, it is Immune to Lateral Damage and attrition. Attrition against opponent is +2 here.");
        setLegacyVersion(new LegacyVersion("1240_72", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

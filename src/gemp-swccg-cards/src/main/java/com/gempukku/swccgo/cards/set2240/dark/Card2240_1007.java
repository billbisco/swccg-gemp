package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Janus Greejatus
 * PC-E1 version of 1240_7: the PC website card list text (by 2012-07-21; Virtual Block 4 list and Virtual master list
 * to 2013-01-11). Against the original (2240_7): “your total influence here” is written “your total Influence here”,
 * with a capital I. The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again
 * (swccg-guild/work#236; Bill 2026-11-11: every publication is a version). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_1007 extends Card1240_007 {
    public Card2240_1007() {
        super();
        setGameText("Agendas: logistics, rule. Once per turn, if at Conference Room and you just drew a non-Epic Event destiny < your total Influence here, may use 2 Force to cancel and redraw that destiny or use 4 Force to add 2 to that destiny.");
        setLegacyVersion(new LegacyVersion("1240_7", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

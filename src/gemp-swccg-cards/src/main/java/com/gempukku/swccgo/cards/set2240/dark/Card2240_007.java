package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Janus Greejatus
 * Original version of 1240_7: the text of the Virtual Block 4 release sheet (2009-12-07), card #08; the PC card image
 * prints it. The PC website card list (by 2012-07-21, PC-E1, 2240_1007) changes it: “your total influence here” is
 * written “your total Influence here”, with a capital I. The Master List of 2013-04-27 prints the original words
 * again; it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_007 extends Card1240_007 {
    public Card2240_007() {
        super();
        setGameText("Agendas: logistics, rule. Once per turn, if at Conference Room and you just drew a non-Epic Event destiny < your total influence here, may use 2 Force to cancel and redraw that destiny or use 4 Force to add 2 to that destiny.");
        setLegacyVersion(new LegacyVersion("1240_7", "",
                "Virtual Block 4 release sheet (2009-12-07), card #08; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

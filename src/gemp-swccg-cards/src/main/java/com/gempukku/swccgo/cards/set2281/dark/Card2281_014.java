package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_194;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: You'll Be Dead!
 * Original version of 1281_14: the text of the Virtual Block 8.1 release sheet (2012-05-11), card #03; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2281_1014) changes it: “a Lost Interrupt” is
 * written “a Lost interrupt”, with a small i. The Master List of 2013-04-27 prints the original words again; it is
 * listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_014 extends Card601_194 {
    public Card2281_014() {
        super();
        setGameText("Deploy on table. Non-lightsaber weapons carried by your non-Dark Jedi characters may not be stolen. During your control phase, opponent loses 1 Force for each battleground site you control with a non-[Permanent Weapon] blaster present. Imperial Artillery is a Lost Interrupt. Immune to Alter.");
        setLegacyVersion(new LegacyVersion("1281_14", "",
                "Virtual Block 8.1 release sheet (2012-05-11), card #03; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}

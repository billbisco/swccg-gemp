package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_194;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: You'll Be Dead!
 * PC-E1 version of 1281_14: the PC website card list text (by 2012-07-21; Virtual Block 8 list and Virtual master
 * list to 2013-01-11). Against the original (2281_14): “a Lost Interrupt” is written “a Lost interrupt”, with a small
 * i. The Master List of 2013-04-27 (PC-E2, the current card) prints the original words again (swccg-guild/work#236;
 * Bill 2026-11-11: every publication is a version). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1014 extends Card601_194 {
    public Card2281_1014() {
        super();
        setGameText("Deploy on table. Non-lightsaber weapons carried by your non-Dark Jedi characters may not be stolen. During your control phase, opponent loses 1 Force for each battleground site you control with a non-[Permanent Weapon] blaster present. Imperial Artillery is a Lost interrupt. Immune to Alter.");
        setLegacyVersion(new LegacyVersion("1281_14", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the Master List (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}

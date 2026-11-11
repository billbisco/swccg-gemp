package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_015;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Well Guarded (V)
 * PC-E2 version of 1281_15: the Virtual Cards Master List (2013-04-27), which repeats the release-sheet words. The PC card
 * image (folder upload date 2013-05-07, PC-E3) prints the same words and is the current card, so this version plays as
 * the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2015 extends Card1281_015 {
    public Card2281_2015() {
        super();
        setGameText("Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_15", "PC-E2",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}

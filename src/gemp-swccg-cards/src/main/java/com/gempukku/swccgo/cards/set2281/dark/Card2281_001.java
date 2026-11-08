package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_001;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Republic
 * Title: Aurra Sing, Deadly Assassin
 * Original version of 1281_1 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_001 extends Card1281_001 {
    public Card2281_001() {
        super();
        setGameText("Once per game, may deploy a device on Aurra from Reserve Deck; reshuffle. While armed with a [Reflections III] rifle: it may not be retargeted or stolen, her total weapon destiny is +1, and characters she hits may not be used to satisfy attrition. Immune to attrition < 3 (< 4 while armed with a rifle).");
        setLegacyVersion(new LegacyVersion("1281_1", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}

package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Join Us Or Die
 * Original version of 1281_11 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_011 extends Card1281_011 {
    public Card2281_011() {
        super();
        setGameText("Deploy on your Throne Room. While Emperor and Vader here, Take Your Father's Place does not deplete Life Force or cause Force loss. Once per turn, may deploy So Be It, Jedi or a docking bay from Reserve Deck; reshuffle. 'Battle' on Insignificant Rebellion is treated as 'battle or duel.' (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_11", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}

package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Forced Servitude
 * Original version of 1210_36: the Virtual Block 1 release sheet text (Dec 2009, slip 36); the PC website card list
 * (by 2012-07-21) has the current wording ("targeting your Heroic card is" became "targeting your Heroic cards is"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_036 extends Card1210_036 {
    public Card2210_036() {
        super();
        setGameText("Deploy on table. Your troopers are Heroic. Opponent's total weapon destiny targeting your Heroic card is +2. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_36", "",
                "Virtual Block 1 release sheet (2009-12-07), card #36; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}

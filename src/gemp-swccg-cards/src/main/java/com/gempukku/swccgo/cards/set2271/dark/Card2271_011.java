package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.common.LegacyVersion;
import java.time.LocalDate;
import com.gempukku.swccgo.cards.set1271.LegacySetupSearchEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Side;

/** Original I'll Take Them Myself (Virtual Block 7.1 sheet, 2010-06-18), a version of 1271_11; replaced by the PC card-list rephrase (PC-E1, by 2012-07-21). Same play. Anything Goes only (docs/legacy-versions.md). */
public class Card2271_011 extends LegacySetupSearchEffect {
    public Card2271_011() {
        super(Side.DARK, "I'll Take Them Myself", GameTextActionId.LEGACY__ILL_TAKE_THEM_MYSELF__TAKE_SETUP,
            GameTextActionId.LEGACY__ILL_TAKE_THEM_MYSELF__TAKE_IMMEDIATE_EFFECT);
        setLore("'Several fighters have broken off from the main group. Come with me.' Darth Vader targets his TIE fighter's fire-linked blaster cannons at the Rebel pilots in the trench.");
        setGameText("Deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. [Immune to Alter.]");
        setLegacyVersion(new LegacyVersion("1271_11", "",
                "Virtual Block 7.1 release sheet (2010-06-18)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
    }
}

package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.set1271.LegacySetupSearchEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Side;

/** Virtual Block 7.1: I'll Take Them Myself. */
public class Card1271_011 extends LegacySetupSearchEffect {
    public Card1271_011() {
        super(Side.DARK, "I'll Take Them Myself", GameTextActionId.LEGACY__ILL_TAKE_THEM_MYSELF__TAKE_SETUP,
            GameTextActionId.LEGACY__ILL_TAKE_THEM_MYSELF__TAKE_IMMEDIATE_EFFECT);
        setLore("'Several fighters have broken off from the main group. Come with me.' Darth Vader targets his TIE fighter's fire-linked blaster cannons at the Rebel pilots in the trench.");
        setGameText("Deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)");
    }
}

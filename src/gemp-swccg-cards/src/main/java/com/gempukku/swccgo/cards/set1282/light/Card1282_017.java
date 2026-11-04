package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyCancelActions;
import com.gempukku.swccgo.cards.LegacyIKnowThatLaugh;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Loje Nella (V)
 */
public class Card1282_017 extends AbstractAlien {
    public Card1282_017() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Loje Nella", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Accountant. Descended from a species of cliff boring worms. Reluctant assistant to Mosep. Purposefully mismanages funds to obstruct Jabba's nefarious activities.");
        setGameText("May lose 2 Force to cancel Cold Feet if targeting Bo Shuda. While four or more cards are stacked on I Know That Laugh and/or your Objective, your Force drain bonuses may not be canceled. If you just Force drained with an alien, may activate 1 Force.");
        addKeywords(Keyword.ACCOUNTANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self,
                LegacyIKnowThatLaugh.stackedOnIKnowThatLaughAndOrObjectiveCondition(self.getOwner(), 4), Filters.your(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.title(Title.Cold_Feet), Filters.Bo_Shuda)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            LegacyCancelActions.buildLoseForceToCancelCardBeingPlayedAction(action, effect, 2);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerId, Filters.sameLocationAs(self, Filters.and(Filters.your(playerId), Filters.alien)))
                && GameConditions.canActivateForce(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}

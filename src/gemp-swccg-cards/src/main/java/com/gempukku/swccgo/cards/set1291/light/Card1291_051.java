package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipSingleSidedStackedCard;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: Cloning Cylinders
 */
public class Card1291_051 extends AbstractNormalEffect {
    public Card1291_051() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Cloning_Cylinders, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("");
        setGameText("Use 4 Force to deploy on table. During your control phase, may use 2 Force to stack a clone from your Lost Pile here face down. At the start of your turn, turn all cards here face up. You may deploy any face up card stacked here (as if from hand). (Immune to Alter.)");
        addIcons(Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CLONING_CYLINDERS_V__STACK_CLONE;
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.hasLostPile(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack a clone from Lost Pile");
            action.setActionMsg("Stack a clone from Lost Pile face down on " + GameUtils.getCardLink(self));
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new ChooseCardFromPileEffect(action, playerId, Zone.LOST_PILE, playerId, Filters.clone) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            if (selectedCard != null) {
                                action.appendEffect(
                                        new StackOneCardFromLostPileEffect(action, selectedCard, self, true, false, false));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                && GameConditions.hasStackedCards(game, self)) {
            Collection<PhysicalCard> faceDown = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.face_down);
            if (!faceDown.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Turn all cards on " + GameUtils.getCardLink(self) + " face up");
                action.setActionMsg("Turn all cards on " + GameUtils.getCardLink(self) + " face up");
                for (PhysicalCard card : faceDown) {
                    action.appendEffect(new FlipSingleSidedStackedCard(action, card));
                    action.addAnimationGroup(card);
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter stackedFaceUpHere = Filters.and(Filters.stackedOn(self, Filters.sameCardId(self)), Filters.not(Filters.face_down));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsIfFromHandModifier(self, stackedFaceUpHere));
        return modifiers;
    }
}

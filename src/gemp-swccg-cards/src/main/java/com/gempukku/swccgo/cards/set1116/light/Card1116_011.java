package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Objective
 * Title: We Have A Plan / They Will Be Lost And Confused
 */
public class Card1116_011 extends AbstractObjective {
    public Card1116_011() {
        super(Side.LIGHT, 0, "We Have A Plan", ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy Coruscant system (may not be converted) and Blockade Flagship: Prison with Palpatine imprisoned there. May deploy Security Control. For remainder of game, your Force generation is +1 at Coruscant battlegrounds. Unless you have 13 or more cards in hand, clones are immune to Monnok. While this side up, at Blockade Flagship sites, imprisoned captives may not be transferred. Once per turn, may deploy a battleground site to Coruscant. Flip this card if Palpatine on Coruscant.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_system, true, false) {
            @Override
            public String getChoiceText() {
                return "Choose Coruscant to deploy";
            }
        });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeConvertedModifier(self, Filters.Coruscant_system));
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.battleground, Filters.or(Filters.Coruscant_site, Filters.Coruscant_system)), 1, playerId));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.clone, new CardsInHandFewerThanCondition(playerId, 13), Title.Monnok));
        modifiers.add(new MayNotBeTransferredModifier(self, Filters.and(Filters.captive, Filters.at(Filters.titleContains("Blockade Flagship")))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THERON_NETT_V__DEPLOY_ABOARD;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a battleground site to Coruscant");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.site, Filters.battleground), Title.Coruscant, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.titleContains("Palpatine"), Filters.Coruscant_location))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.appendEffect(new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}

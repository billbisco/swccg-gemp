package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelEffectsOfRevolutionModifier;
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Objective
 * Title: Imperial Occupation / Imperial Control
 */
public class Card1104_040_BACK extends AbstractObjective {
    public Card1104_040_BACK() {
        super(Side.DARK, 7, Title.Imperial_Control, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, Rebel Base Occupation is canceled. Walker Garrison is an Out Of Play Interrupt and Immune to Sense. Rebels deploy +1 to Hoth sites. Total battle destiny at Hoth system +1 for each Imperial there. Unless opponent controls a Hoth location, Force drain bonuses may not be canceled. Once per turn, when you deploy an AT-AT, may take any one card (except Interrupt or vehicle) from Lost Pile into hand. During deploy phase, may deploy a marker site from Reserve Deck; reshuffle.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.AT_AT, -1));
        modifiers.add(new MayNotPlayModifier(self, Filters.title(Title.Colo_Claw_Fish), playerId));
        modifiers.add(new CancelEffectsOfRevolutionModifier(self, null));
        modifiers.add(new SuspendsCardModifier(self, Filters.Revolution));
        modifiers.add(new ChangeCardSubtypeModifier(self, Filters.title(Title.Walker_Garrison), CardSubtype.OUT_OF_PLAY));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title(Title.Walker_Garrison), Title.Sense));
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.Rebel_Base_Occupation)));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Rebel, 1, Filters.Hoth_site));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.Hoth_system,
                new OnTableEvaluator(self, Filters.and(Filters.Imperial, Filters.at(Filters.Hoth_system))),
                playerId, true));
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self,
                new UnlessCondition(new ControlsCondition(opponent, Filters.Hoth_location)),
                Filters.your(playerId)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployActionId = GameTextActionId.OVS__IMPERIAL_CONTROL__DEPLOY_MARKER_SITE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, deployActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployActionId);
            action.setText("Deploy a marker site from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.marker_site, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId takeActionId = GameTextActionId.OVS__IMPERIAL_CONTROL__TAKE_FROM_LOST_PILE;
        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.AT_AT)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, takeActionId)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, takeActionId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, takeActionId);
            action.setText("Take a card into hand from Lost Pile");
            action.setActionMsg("Take any one card (except Interrupt or vehicle) from Lost Pile into hand");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.and(Filters.not(Filters.Interrupt), Filters.not(Filters.vehicle)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}

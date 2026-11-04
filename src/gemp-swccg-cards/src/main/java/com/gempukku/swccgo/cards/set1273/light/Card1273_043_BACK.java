package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToCancelModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToHalfPrintedModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Objective
 * Title: Rebel Strike Team / Garrison Destroyed (V)
 */
public class Card1273_043_BACK extends AbstractObjective {
    public Card1273_043_BACK() {
        super(Side.LIGHT, 7, "Garrison Destroyed", ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setGameText(Card1273_043.SHEET_GAMETEXT);
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationImmuneToCancelModifier(self, Filters.Endor_location,
                new OnTableCondition(self, Filters.Establish_Secret_Base), Filters.any, null));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self,
                Filters.and(Filters.Endor_site, Filters.occupiesWith(playerId, self, Filters.scout)), opponent, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        TopLevelGameTextAction takeEpicEvent = Card1273_043.takeEpicEventAction(playerId, game, self, gameTextSourceCardId);
        if (takeEpicEvent != null) {
            actions.add(takeEpicEvent);
        }

        // May raise your converted Endor to the top
        PhysicalCard endorToConvert = Filters.findFirstFromTopLocationsOnTable(game,
                Filters.and(Filters.Endor_location, Filters.canBeConvertedByRaisingLocationToTop(playerId)));
        if (endorToConvert != null) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Raise your converted Endor to the top");
            action.setActionMsg("Raise converted " + GameUtils.getCardLink(endorToConvert) + " to the top");
            // Perform result(s)
            action.appendEffect(
                    new ConvertLocationByRaisingToTopEffect(action, endorToConvert, true));
            actions.add(action);
        }

        // Once per game, may deploy from Reserve Deck (or deploy from hand) a Rebel starship for half its printed deploy cost
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GARRISON_DESTROYED_V__DEPLOY_REBEL_STARSHIP;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Rebel starship from Reserve Deck");
                action.setActionMsg("Deploy a Rebel starship from Reserve Deck for half its printed deploy cost");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                appendHalfCostDeploy(action, self, new DeployCardFromReserveDeckEffect(action, Filters.Rebel_starship, true));
                actions.add(action);
            }
            if (GameConditions.hasInHand(game, playerId, Filters.Rebel_starship)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Rebel starship from hand");
                action.setActionMsg("Deploy a Rebel starship from hand for half its printed deploy cost");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                appendHalfCostDeploy(action, self, new DeployCardFromHandEffect(action, playerId, Filters.Rebel_starship, false));
                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * Deploys with Rebel starships at half their printed deploy cost (round down) while this deployment is carried out.
     */
    private static void appendHalfCostDeploy(final TopLevelGameTextAction action, PhysicalCard self, StandardEffect deployEffect) {
        final boolean[] active = new boolean[]{false};
        Condition whileDeploying = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return active[0];
            }
        };
        action.appendEffect(
                new AddUntilEndOfTurnModifierEffect(action,
                        new ResetDeployCostToHalfPrintedModifier(self, Filters.Rebel_starship, whileDeploying), null));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        active[0] = true;
                    }
                });
        action.appendEffect(deployEffect);
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        active[0] = false;
                    }
                });
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Whenever you initiate a battle with a scout, may retrieve 2 Force
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.scout, Filters.participatingInBattle))) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 2));
            List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
            actions.add(action);
            return actions;
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        RequiredGameTextTriggerAction loseAction = Card1273_043.loseCardsAction(game, effectResult, self, gameTextSourceCardId);
        if (loseAction != null) {
            actions.add(loseAction);
        }

        // While this side up, Endor Shield is canceled
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Filter endorShield = Filters.and(Filters.Endor_Shield, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CANCELED));
            PhysicalCard toCancel = Filters.findFirstActive(game, self, endorShield);
            if (toCancel != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Cancel Endor Shield");
                action.setActionMsg("Cancel " + GameUtils.getCardLink(toCancel));
                // Perform result(s)
                action.appendEffect(
                        new CancelCardOnTableEffect(action, toCancel));
                actions.add(action);
            }
        }
        return actions;
    }
}

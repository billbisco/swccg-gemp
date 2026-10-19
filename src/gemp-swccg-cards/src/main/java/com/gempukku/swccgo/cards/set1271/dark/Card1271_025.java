package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.DestinyIncreaseLimitModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitIncreaseLimitModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerIncreaseLimitModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.UseCalculationForDeployCostModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Mission
 * Subtype: Critical
 * Title: Galactic Domination
 */
public class Card1271_025 extends AbstractMission {
    public Card1271_025() {
        super(Side.DARK, 0, "Galactic Domination", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V, true);
        setGameText("Deploy on table. Flagship and Endor Shield are canceled. You may not deploy [Independent] or [Trade Federation] starships. Once per turn, may use 1 Force to deploy an Executor site or a battleground system from Reserve Deck; reshuffle. While Flagship Operations on Executor: its power may not be reduced, your Force drains at battleground systems = 3, your TIE Assault Squadron may deploy without replacement (deploy = 3), and, wherever you have a TIE with a capital starship, draw one battle destiny if not able to otherwise. Your cards add no more than 2 to the destiny, power, and forfeit of your squadrons.");
        addIcons(Icon.SETUP, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition flagshipOpsOnExecutor = new OnTableCondition(self, Filters.and(Filters.Flagship_Operations, Filters.attachedTo(Filters.Executor)));
        Filter tieWithCapital = Filters.and(Filters.your(self), Filters.TIE,
                Filters.at(Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.capital_starship))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.starship, Filters.or(Icon.INDEPENDENT, Icon.TRADE_FEDERATION)), playerId));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.Executor, flagshipOpsOnExecutor, playerId));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.Executor, flagshipOpsOnExecutor, game.getOpponent(playerId)));
        modifiers.add(new ResetForceDrainModifier(self, Filters.battleground_system, flagshipOpsOnExecutor, 3) {
            @Override
            public boolean isForPlayer(String player) {
                return playerId.equals(player);
            }
        });
        Filter tieAssault = Filters.and(Filters.your(self), Filters.TIE_Assault_Squadron);
        modifiers.add(new MayDeployToLocationModifier(self, tieAssault, flagshipOpsOnExecutor, Filters.system));
        modifiers.add(new UseCalculationForDeployCostModifier(self, tieAssault, flagshipOpsOnExecutor, new ConstantEvaluator(3)));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, tieWithCapital, flagshipOpsOnExecutor, 1));
        Filter yourSquadrons = Filters.and(Filters.your(self), Filters.squadron);
        modifiers.add(new DestinyIncreaseLimitModifier(self, yourSquadrons, 2));
        modifiers.add(new PowerIncreaseLimitModifier(self, yourSquadrons, 2));
        modifiers.add(new ForfeitIncreaseLimitModifier(self, yourSquadrons, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter toCancel = Filters.or(Filters.Flagship, Filters.Endor_Shield);
        if (TriggerConditions.isPlayingCard(game, effect, toCancel)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Flagship)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Flagship, Title.Flagship);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Endor_Shield)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Endor_Shield, Title.Endor_Shield);
                actions.add(action);
            }
        }
        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GALACTIC_DOMINATION__DEPLOY_EXECUTOR_SITE_OR_SYSTEM;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Executor site or battleground system");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Executor_site, Filters.battleground_system), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}

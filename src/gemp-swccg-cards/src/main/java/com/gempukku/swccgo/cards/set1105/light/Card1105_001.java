package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.UtinniEffectCompletedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: Asteroids Do Not Concern Me
 */
public class Card1105_001 extends AbstractNormalEffect {
    public Card1105_001() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Asteroids_Do_Not_Concern_Me, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("The personal fears of Vader's minions do not affect his priorities.");
        setGameText("Deploy on table. Asteroid Sanctuary is canceled. When you complete Rycar's Run, place it out of play. During your control phase, opponent loses 1 Force for each Asteroid Field you control (limit 3). During your deploy phase, may deploy one Rycar's Run or asteroid sector on table from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Asteroid_Sanctuary)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Asteroid_Sanctuary)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Asteroid_Sanctuary, Title.Asteroid_Sanctuary);
            actions.add(action);
        }

        if (TriggerConditions.utinniEffectCompleted(game, effectResult, Filters.and(Filters.your(self), Filters.Rycars_Run))) {
            PhysicalCard rycarsRun = ((UtinniEffectCompletedResult) effectResult).getUtinniEffect();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(rycarsRun) + " out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(rycarsRun) + " out of play");
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, rycarsRun));
            actions.add(action);
        }

        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            int forceToLose = asteroidFieldForceLoss(game, playerId);
            if (forceToLose > 0) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + forceToLose + " Force");
                action.appendEffect(
                        new LoseForceEffect(action, opponent, forceToLose));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            int forceToLose = asteroidFieldForceLoss(game, playerId);
            if (forceToLose > 0) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + forceToLose + " Force");
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendEffect(
                        new LoseForceEffect(action, opponent, forceToLose));
                actions.add(action);
            }
        }

        GameTextActionId deployId = GameTextActionId.LEGACY__ASTEROIDS_DO_NOT_CONCERN_ME_V__DEPLOY_RYCARS_RUN_OR_ASTEROID_SECTOR;
        Filter deployFilter = Filters.or(Filters.Rycars_Run, Filters.asteroid_sector);
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, deployId);
            action.setText("Deploy Rycar's Run or asteroid sector");
            action.setActionMsg("Deploy a Rycar's Run or asteroid sector from Reserve Deck");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, deployFilter, true));
            actions.add(action);
        }

        return actions;
    }

    private int asteroidFieldForceLoss(SwccgGame game, String playerId) {
        return Math.min(3, Filters.countTopLocationsOnTable(game, Filters.and(Filters.Asteroid_Field, Filters.controls(playerId))));
    }
}

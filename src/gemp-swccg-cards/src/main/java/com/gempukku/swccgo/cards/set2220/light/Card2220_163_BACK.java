package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyUsingCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Legacy Block 2; final Light master p47
 * Type: Objective
 * Title: Local Uprising / Liberation (back)
 *
 * Back side of the original Local Uprising (2220_163). Same text as the current back (1220_163_BACK); only the front
 * was changed on the Virtual Cards Master List (2013-04-27). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_163_BACK extends AbstractObjective {
    public Card2220_163_BACK() {
        super(Side.LIGHT, 7, Title.Liberation, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, Wedge may not cancel battle destiny draws on Hoth. During your control phase: opponent loses 1 Force for each battleground marker site you control with both a T-47 and a Rebel, and you may use 2 Force to take any one card into hand from Reserve Deck; reshuffle. Whenever you must lose Force (except from your card, battle damage, or a Force drain at a battleground), reduce the loss (to a minimum of 1) by the number of Hoth locations you occupy. Flip this card if you do not control two exterior Hoth sites. Place out of play if Main Power Generators 'blown away.'");
        setLegacyVersion(new LegacyVersion("1220_163", "",
                "Virtual Block 2 release sheet (2009-12-07), card #164", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotCancelBattleDestinyUsingCardModifier(self, Filters.Hoth_site, Filters.Wedge, null));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId loseForceId = GameTextActionId.OTHER_CARD_ACTION_2;
        int numSites = Filters.countTopLocationsOnTable(game, Filters.and(
                Filters.marker_site,
                Filters.battleground_site,
                Filters.controlsWith(playerId, self, Filters.T_47),
                Filters.controlsWith(playerId, self, Filters.Rebel)));
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, loseForceId, Phase.CONTROL)
                && numSites > 0) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, loseForceId);
            action.setText("Make opponent lose " + numSites + " Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, numSites));
            actions.add(action);
        }

        GameTextActionId takeCardId = GameTextActionId.LEGACY__LIBERATION__TAKE_ANY_CARD;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, takeCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeCardId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeCardId);
            action.setText("Take any card into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.any, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        GameTextActionId reduceActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.isAboutToLoseForceMoreThan(game, effectResult, playerId, 1)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.your(self))
                && !TriggerConditions.isAboutToLoseForceFromBattleDamage(game, effectResult, playerId)
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.battleground)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, reduceActionId)) {
            int numToReduce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Hoth_location, Filters.occupies(playerId)));
            if (numToReduce > 0) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, reduceActionId);
                action.setText("Reduce Force loss by " + numToReduce);
                action.setActionMsg("Reduce Force loss by " + numToReduce + " (to a minimum of 1)");
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, numToReduce, 1));
                actions.add(action);
            }
        }

        GameTextActionId loseForceId = GameTextActionId.OTHER_CARD_ACTION_2;
        int numSites = Filters.countTopLocationsOnTable(game, Filters.and(
                Filters.marker_site,
                Filters.battleground_site,
                Filters.controlsWith(playerId, self, Filters.T_47),
                Filters.controlsWith(playerId, self, Filters.Rebel)));
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, loseForceId, Phase.CONTROL)
                && numSites > 0) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, loseForceId);
            action.setPerformingPlayer(playerId);
            action.setText("Make opponent lose " + numSites + " Force");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, numSites));
            actions.add(action);
        }

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.controls(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.exterior_Hoth_site)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.title(Title.Main_Power_Generators, true))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}

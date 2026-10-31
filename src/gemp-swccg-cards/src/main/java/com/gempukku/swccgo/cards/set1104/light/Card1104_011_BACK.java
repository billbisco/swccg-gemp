package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.modifiers.ChangeCardSubtypeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Objective
 * Title: Local Uprising / Liberation
 */
public class Card1104_011_BACK extends AbstractObjective {
    public Card1104_011_BACK() {
        super(Side.LIGHT, 7, Title.Liberation, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, whenever you must lose Force (except from your card, battle damage, or a Force drain at a battleground), reduce the loss (to a minimum of 1) by the number of Hoth locations you occupy. During your control phase, opponent loses 1 Force for each marker site (except 1st Marker) you control with both a T-47 and a Rebel. During your control phase, may use 2 Force to ▲ any one card. Flip this card if you do not control two exterior Hoth sites. Place out of play if Main Power Generators 'blown away.'");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Hoth_Sentry, Filters.and(Filters.location, Filters.not(Filters.Hoth_location), Filters.not(Filters.title(Title.Rendezvous_Point)))), playerId));
        modifiers.add(new ChangeCardSubtypeModifier(self, Filters.title(Title.T_47_Battle_Formation), CardSubtype.OUT_OF_PLAY));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title(Title.T_47_Battle_Formation), Title.Sense));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId loseForceId = GameTextActionId.OTHER_CARD_ACTION_2;
        int numSites = Filters.countTopLocationsOnTable(game, Filters.and(
                Filters.marker_site,
                Filters.not(Filters.First_Marker),
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

        GameTextActionId takeCardId = GameTextActionId.OVS__LIBERATION__TAKE_ANY_CARD;
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
                Filters.not(Filters.First_Marker),
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

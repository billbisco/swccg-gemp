package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LegacyMayIgnorePlaceCardFromHandDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: Begin Landing Your Troops & The Dark Path
 */
public class Card1291_009 extends AbstractNormalEffect {
    public Card1291_009() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Begin Landing Your Troops & The Dark Path", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("Deploy on table. Your [Separatist] droids are power +1 and may shuttle from battleground systems for free. During battle, may place this Effect out of play to cancel and redraw your just-drawn destiny. Once per turn, each player may ignore any deploy cost on [Virtual Block 4] Watch Your Step and once per game, while that Objective on table, you may take a starfighter into hand from Reserve Deck; reshuffle (or draw two cards from Reserve Deck). Jedi Tests may not modify your battle or weapon destiny draws. Lost if your [Virtual Block 4] objective on table. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    /**
     * [Virtual Block 4] Watch Your Step (either side up).
     */
    private static Filter watchYourStep() {
        return Filters.and(Filters.Objective, Icon.LEGACY_BLOCK_4,
                Filters.or(Filters.Watch_Your_Step, Filters.title(Title.This_Place_Can_Be_A_Little_Rough)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourSeparatistDroids = Filters.and(Filters.your(self), Filters.droid, Icon.SEPARATIST);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourSeparatistDroids, 1));
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, yourSeparatistDroids, Filters.battleground_system));
        modifiers.add(new LegacyMayIgnorePlaceCardFromHandDeployCostModifier(self, watchYourStep(),
                GameTextActionId.LEGACY__BEGIN_LANDING_YOUR_TROOPS__IGNORE_WATCH_YOUR_STEP_DEPLOY_COST));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // During battle, may place this Effect out of play to cancel and redraw your just-drawn destiny
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__BEGIN_LANDING_YOUR_TROOPS__CANCEL_AND_REDRAW_DESTINY);
            action.setText("Cancel and redraw destiny");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play to cancel and redraw destiny");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BEGIN_LANDING_YOUR_TROOPS__TAKE_STARFIGHTER_OR_DRAW_TWO;
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Once per game, while that Objective on table, may take a starfighter into hand from Reserve Deck (or draw two cards from Reserve Deck)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, watchYourStep())) {

            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take a starfighter into hand from Reserve Deck");
                action.setActionMsg("Take a starfighter into hand from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.starfighter, true));
                actions.add(action);
            }

            if (GameConditions.hasReserveDeck(game, playerId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Draw two cards from Reserve Deck");
                action.setActionMsg("Draw two cards from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, 2));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Lost if your [Virtual Block 4] objective on table
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Objective, Icon.LEGACY_BLOCK_4))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
